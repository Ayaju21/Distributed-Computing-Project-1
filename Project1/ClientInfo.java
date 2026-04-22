public class ClientInfo {
    public final String name;
    public final String ip;
    public final int port;
    public volatile long lastSeen;

    public ClientInfo(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.lastSeen = System.currentTimeMillis();
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }
}