import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DirectoryServer {
    private static final int PORT = 3000;
    private static final Map<String, ClientInfo> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Directory Server started on port " + PORT);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (ClientInfo info : clients.values()) {
                if (System.currentTimeMillis() - info.lastSeen > 5000) {
                    clients.remove(info.name);
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientInfo {
        String name;
        String ip;
        int port;
        long lastSeen;

        ClientInfo(String name, String ip, int port) {
            this.name = name;
            this.ip = ip;
            this.port = port;
            this.lastSeen = System.currentTimeMillis();
        }

        void refresh() {
            this.lastSeen = System.currentTimeMillis();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String msg;
                while ((msg = in.readLine()) != null) {
                    String[] parts = msg.split(" ");
                    switch (parts[0]) {
                        case "ALIVE": {
                            String name = parts[1];
                            int port = Integer.parseInt(parts[2]);
                            clients.put(name, new ClientInfo(name, socket.getInetAddress().getHostAddress(), port));
                            break;
                        }
                        case "LIST": {
                            out.println(String.join(",", clients.keySet()));
                            break;
                        }
                        case "GET": {
                            String target = parts[1];
                            ClientInfo ci = clients.get(target);
                            if (ci != null) {
                                out.println(ci.ip + " " + ci.port);
                            } else {
                                out.println("NOT_FOUND");
                            }
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
