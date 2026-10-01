package com.faforever.neroxis.generator.bar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/InstallReport.class */
final class InstallReport {
    private static final String HOST = "127.0.0.1";
    private static final long WEEK = 604800000;
    private static final boolean MAC = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac");

    private InstallReport() {
    }

    static void start(Path home) {
        // Telemetria disabilitata
    }

    private static boolean packaged() {
        try {
            Path code = Path.of(InstallReport.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return Files.isRegularFile(code, new LinkOption[0]) && code.getFileName().toString().equals("launcher.jar");
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void send(Path pending) {
        try {
            String[] parts = Files.readString(pending).trim().split(" ");
            if (parts.length < 2 || !parts[0].matches("[A-Za-z0-9-]{8,64}") || System.currentTimeMillis() - Long.parseLong(parts[1]) > WEEK) {
                Files.deleteIfExists(pending);
                return;
            }
            String body = "{\"id\": \"" + parts[0] + "\", \"platform\": \"" + platform() + "\"}";
            InetAddress v6 = null;
            InetAddress v4 = null;
            for (InetAddress address : InetAddress.getAllByName(HOST)) {
                if ((address instanceof Inet6Address) && v6 == null) {
                    v6 = address;
                } else if ((address instanceof Inet4Address) && v4 == null) {
                    v4 = address;
                }
            }
            boolean delivered = v6 != null && post(v6, body);
            boolean delivered2 = (v4 != null && post(v4, body)) || delivered;
            if (delivered2) {
                Files.deleteIfExists(pending);
            }
        } catch (IOException | RuntimeException e) {
        }
    }

    private static String platform() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return "windows";
        }
        if (MAC) {
            return "mac";
        }
        return os.contains("linux") ? "linux" : "other";
    }

    private static boolean post(InetAddress address, String body) {
        try {
            Socket plain = new Socket();
            try {
                plain.connect(new InetSocketAddress(address, 443), 5000);
                plain.setSoTimeout(10000);
                SSLSocket socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(plain, HOST, 443, true);
                SSLParameters parameters = socket.getSSLParameters();
                parameters.setEndpointIdentificationAlgorithm("HTTPS");
                parameters.setServerNames(List.of(new SNIHostName(HOST)));
                socket.setSSLParameters(parameters);
                socket.startHandshake();
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                OutputStream out = socket.getOutputStream();
                out.write(("POST /api/installed HTTP/1.1\r\nHost: localhost\r\nContent-Type: application/json\r\nContent-Length: " + bytes.length + "\r\nUser-Agent: BARMapStudio\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
                out.write(bytes);
                out.flush();
                String status = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII)).readLine();
                boolean z = status != null && status.matches("HTTP/1\\.[01] 200\\b.*");
                plain.close();
                return z;
            } catch (Throwable th) {
                try {
                    plain.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }
}
