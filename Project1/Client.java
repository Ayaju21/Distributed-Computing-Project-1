import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private final String name;
    private final int localPort;
    private final Socket directorySocket;
    private final PrintWriter dirOut;
    private final BufferedReader dirIn;

    public Client(String name, int localPort) throws IOException {
        this.name = name;
        this.localPort = localPort;
        this.directorySocket = new Socket("localhost", 3000);
        this.dirOut = new PrintWriter(directorySocket.getOutputStream(), true);
        this.dirIn = new BufferedReader(new InputStreamReader(directorySocket.getInputStream()));
        startHeartbeat();
        startListening();
        runCommandLoop();
    }

    private void startHeartbeat() {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                dirOut.println("ALIVE " + name + " " + localPort);
            }
        }, 0, 1000);
    }

    private void startListening() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(localPort)) {
                while (true) {
                    Socket client = serverSocket.accept();
                    new Thread(() -> handleIncoming(client)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleIncoming(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);  // ✅ الرسالة تحتوي اسم المرسل بالفعل
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runCommandLoop() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Command (list / connect <name>): ");
            String input = scanner.nextLine();
            if (input.equals("list")) {
                dirOut.println("LIST");
                System.out.println("Active Clients: " + dirIn.readLine());
            } else if (input.startsWith("connect ")) {
                String target = input.split(" ")[1];
                dirOut.println("GET " + target);
                String response = dirIn.readLine();
                if (response.equals("NOT_FOUND")) {
                    System.out.println("Client not found.");
                } else {
                    String[] parts = response.split(" ");
                    String ip = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    System.out.println("Connected to: " + target);  // ✅ يعرض اسم الطرف الآخر
                    chatWithClient(ip, port);
                }
            }
        }
    }

    private void chatWithClient(String ip, int port) {
        try (Socket socket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Chat started. Type messages:");
            while (true) {
                String msg = scanner.nextLine();
                out.println(name + ": " + msg);  // ✅ إرسال الرسالة مع اسم المرسل
            }
        } catch (IOException e) {
            System.out.println("Connection failed.");
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java Client <name> <port>");
            return;
        }
        new Client(args[0], Integer.parseInt(args[1]));
    }
}
