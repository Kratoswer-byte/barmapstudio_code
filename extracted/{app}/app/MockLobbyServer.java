import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MockLobbyServer {
    public static void main(String[] args) {
        // 1. Thread for HTTP Map Service on port 8090 (pure java.base)
        Thread httpThread = new Thread(() -> {
            try (ServerSocket httpSocket = new ServerSocket(8090, 50, InetAddress.getByName("127.0.0.1"))) {
                System.out.println("Mock Map Service listening on 127.0.0.1:8090");
                while (true) {
                    Socket client = httpSocket.accept();
                    new Thread(() -> handleHttpClient(client), "mock-http-client").start();
                }
            } catch (Exception e) {
                System.err.println("HTTP map service stopped: " + e.getMessage());
            }
        }, "mock-http-server");
        httpThread.setDaemon(true);
        httpThread.start();

        // 2. TASServer Socket on port 8200 (pure java.base)
        try (ServerSocket serverSocket = new ServerSocket(8200, 50, InetAddress.getByName("127.0.0.1"))) {
            System.out.println("Mock Lobby Server listening on 127.0.0.1:8200");
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleLobbyClient(client), "mock-lobby-client").start();
            }
        } catch (Exception e) {
            System.err.println("Lobby server error: " + e.getMessage());
        }
    }

    private static void handleHttpClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            OutputStream out = socket.getOutputStream()
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null) return;
            
            // Read until empty line (end of headers)
            String header;
            while ((header = reader.readLine()) != null && !header.isEmpty()) {
                // ignore
            }

            byte[] body;
            String contentType = "application/json";
            if (requestLine.contains("/download/version.txt")) {
                body = "1.0.0\n".getBytes(StandardCharsets.UTF_8);
                contentType = "text/plain";
            } else {
                body = "[]".getBytes(StandardCharsets.UTF_8);
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                              "Content-Type: " + contentType + "\r\n" +
                              "Content-Length: " + body.length + "\r\n" +
                              "Connection: close\r\n\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.write(body);
            out.flush();
        } catch (Exception e) {
            // client disconnected
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    private static void handleLobbyClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            writer.write("TASSERVER 0.38 0 1\n");
            writer.flush();

            String line;
            String currentUser = "gigginometronotte";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("LOGIN")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 1) {
                        currentUser = parts[1];
                    }
                    writer.write("ACCEPTED " + currentUser + "\n");
                    writer.write("SERVERMSG Benvenuto nel server locale Eclipse offline!\n");
                    writer.write("ADDUSER " + currentUser + " IT 0\n");
                    writer.write("CLIENTSTATUS " + currentUser + " 0\n");
                    writer.write("LOGININFOEND\n");
                    writer.flush();
                } else if (line.startsWith("PING")) {
                    writer.write("PONG\n");
                    writer.flush();
                } else if (line.startsWith("JOIN")) {
                    writer.write("JOIN main\n");
                    writer.write("JOINED main " + currentUser + "\n");
                    writer.flush();
                } else if (line.startsWith("MYSTATUS")) {
                    String[] parts = line.split("\\s+");
                    String status = parts.length > 1 ? parts[1] : "0";
                    writer.write("CLIENTSTATUS " + currentUser + " " + status + "\n");
                    writer.flush();
                } else if (line.startsWith("OPENBATTLE")) {
                    writer.write("OPENEDBATTLE 1 0 0 " + currentUser + " 127.0.0.1 8452 16 1 0 0 0 0 0 0 0 0\n");
                    writer.write("JOINEDBATTLE 1 " + currentUser + "\n");
                    writer.write("JOINBATTLE 1\n");
                    writer.flush();
                } else if (line.startsWith("LEAVEBATTLE")) {
                    writer.write("LEFTBATTLE 1 " + currentUser + "\n");
                    writer.flush();
                } else if (line.startsWith("EXIT")) {
                    break;
                }
            }
        } catch (Exception e) {
            // Connection closed
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }
}
