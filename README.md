# Distributed-Computing-Project-1
# 💬 Java P2P Multi-Threaded Chat Application
---

## 📖 Abstract

A **Peer-to-Peer chat application** built in Java, where a central **Directory Server** handles peer discovery while **direct TCP connections** power real-time messaging. Once connected, peers communicate without involving the server — fully decentralized.

The system demonstrates key networking concepts including **socket programming**, **concurrency**, and **decentralized peer communication**.

---

## 🏗️ Architecture

### 🖥️ Directory Server — The Coordinator

The server is the central hub for peer discovery only. It plays no role in the actual chat.

- **Heartbeat Tracking** — Maintains a live list of active users via periodic `I am alive` signals sent by each peer.
- **Multithreading** — Spawns a dedicated thread per client for non-blocking concurrent handling.
- **Information Broker** — Returns the IP and port of a requested peer to bootstrap a direct connection.

### 👤 Client Peer — The Node

Each client acts simultaneously as a **server** (listening for incoming messages) and a **client** (initiating outbound connections).

- **Heartbeat Mechanism** — Sends a signal every second to remain `Active` on the directory.
- **Peer Discovery** — Periodically receives the updated active-user list from the Directory Server.
- **Direct TCP Link** — Establishes a direct socket connection to the chosen peer, bypassing the server.
- **Async Communication** — Separates sending and receiving into independent threads for a smooth, lag-free experience.
---

## 📚 Concepts Demonstrated

- TCP Socket Programming in Java
- Multithreading with `Thread` / `Runnable`
- Client-Server and P2P hybrid architecture
- Heartbeat / keep-alive patterns
- Concurrent data structures for peer tracking


