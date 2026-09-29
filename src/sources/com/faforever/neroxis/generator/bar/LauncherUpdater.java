package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LauncherUpdater.class */
final class LauncherUpdater {
    private static final String PUBLIC_KEY = "MCowBQYDK2VwAyEAJX+vlxwIrkRozJ/I88UykWOFZkttFSlVpwQZCOlH0fY=";
    private static final String BUILD_INFO = "com/faforever/neroxis/generator/bar/build-info.properties";

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LauncherUpdater$Progress.class */
    interface Progress {
        void at(String text, double fraction);
    }

    private LauncherUpdater() {
    }

    static boolean steam() {
        if (System.getenv("SteamAppId") != null) {
            return true;
        }
        try {
            Path jar = Path.of(LauncherUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return (jar.getParent() == null || jar.getParent().getParent() == null || !Files.isRegularFile(jar.getParent().getParent().resolve("steam_appid.txt"), new LinkOption[0])) ? false : true;
        } catch (Exception e) {
            return false;
        }
    }

    static boolean appStore() {
        if (Boolean.getBoolean("neroxis.appstore")) {
            return true;
        }
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac")) {
            return false;
        }
        if (System.getenv("APP_SANDBOX_CONTAINER_ID") != null) {
            return true;
        }
        String bundle = System.getProperty("neroxis.bundle");
        return bundle != null && Files.isRegularFile(Path.of(bundle, "_MASReceipt", "receipt"), new LinkOption[0]);
    }

    static Path runningJar() {
        if (steam() || appStore()) {
            return null;
        }
        try {
            Path jar = Path.of(LauncherUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(jar, new LinkOption[0]) && jar.getFileName().toString().equals("launcher.jar") && jar.getParent() != null && jar.getParent().getFileName().toString().equals("app")) {
                return jar;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    static boolean stageUpdate(String downloadBase) throws InterruptedException, IOException {
        return stageUpdate(downloadBase, (text, fraction) -> {
        });
    }

    static boolean stageUpdate(String downloadBase, Progress progress) throws InterruptedException, IOException {
        Path jar = runningJar();
        if (jar == null) {
            return false;
        }
        try {
            FileChannel lock = FileChannel.open(jar.resolveSibling("update.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            try {
                FileLock held = lock.tryLock();
                if (held == null) {
                    log("update check skipped: another Eclipse is checking already");
                    if (lock != null) {
                        lock.close();
                    }
                    return false;
                }
                try {
                    boolean zStageUpdateGuarded = stageUpdateGuarded(downloadBase, progress);
                    held.release();
                    if (lock != null) {
                        lock.close();
                    }
                    return zStageUpdateGuarded;
                } catch (Throwable th) {
                    held.release();
                    throw th;
                }
            } catch (Throwable th2) {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        } catch (OverlappingFileLockException e) {
            log("update check skipped: another check runs in this launcher");
            return false;
        }
    }

    private static boolean stageUpdateGuarded(String downloadBase, Progress progress) throws InterruptedException, IOException {
        try {
            return stageUpdateOnce(downloadBase, progress);
        } catch (IOException e) {
            if (e.getMessage() == null || !e.getMessage().startsWith("the downloaded update")) {
                log("update check failed: " + String.valueOf(e));
                throw e;
            }
            log("update check: " + e.getMessage() + " - a publish in progress? looking again");
            progress.at("The server is being updated right now, looking again in a moment...", -1.0d);
            Thread.sleep(8000L);
            try {
                return stageUpdateOnce(downloadBase, progress);
            } catch (IOException again) {
                log("update check failed: " + String.valueOf(again));
                throw again;
            }
        }
    }

    private static void log(String line) {
        Path jar = runningJar();
        if (jar == null || jar.getParent() == null || jar.getParent().getParent() == null) {
            return;
        }
        try {
            Path log = jar.getParent().getParent().resolve("update.log");
            if (Files.exists(log, new LinkOption[0]) && Files.size(log) > 200000) {
                Files.delete(log);
            }
            Files.writeString(log, String.valueOf(LocalDateTime.now().withNano(0)) + "  " + line + System.lineSeparator(), StandardCharsets.UTF_8, new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND});
        } catch (IOException e) {
        }
    }

    private static boolean stageUpdateOnce(String downloadBase, Progress progress) throws InterruptedException, IOException {
        Path jar = runningJar();
        if (jar == null) {
            return false;
        }
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
        HttpResponse<String> info = http.send(HttpRequest.newBuilder(URI.create(downloadBase + "launcher.json")).timeout(Duration.ofSeconds(15L)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (info.statusCode() != 200) {
            return false;
        }
        String sha256 = field((String) info.body(), "sha256");
        String signature = field((String) info.body(), "signature");
        if (sha256 == null || signature == null || !sha256.matches("[0-9a-f]{64}")) {
            return false;
        }
        if (!verify(sha256, signature)) {
            throw new IOException("the update information is not signed by the publisher");
        }
        Path staged = jar.resolveSibling("launcher-new.jar");
        if (sha256.equals(hash(jar))) {
            Files.deleteIfExists(staged);
            return false;
        }
        if (Files.isRegularFile(staged, new LinkOption[0]) && sha256.equals(hash(staged))) {
            return true;
        }
        Path partial = jar.resolveSibling("launcher-download.tmp");
        if (!Files.isRegularFile(partial, new LinkOption[0]) || !sha256.equals(hash(partial))) {
            HttpResponse<InputStream> download = http.send(HttpRequest.newBuilder(URI.create(downloadBase + "launcher.jar")).timeout(Duration.ofMinutes(10L)).build(), HttpResponse.BodyHandlers.ofInputStream());
            if (download.statusCode() != 200) {
                return false;
            }
            long total = download.headers().firstValueAsLong("Content-Length").orElse(-1L);
            InputStream stream = (InputStream) download.body();
            try {
                OutputStream out = Files.newOutputStream(partial, new OpenOption[0]);
                try {
                    byte[] buffer = new byte[65536];
                    long done = 0;
                    long shown = -1;
                    while (true) {
                        int read = stream.read(buffer);
                        if (read <= 0) {
                            break;
                        }
                        out.write(buffer, 0, read);
                        done += (long) read;
                        if (done - shown > 262144) {
                            shown = done;
                            Object[] objArr = new Object[2];
                            objArr[0] = Double.valueOf(done / 1048576.0d);
                            objArr[1] = total > 0 ? String.format("%.1f", Double.valueOf(total / 1048576.0d)) : "?";
                            progress.at(String.format("Downloading the new version  %.1f of %s MB", objArr), total > 0 ? Math.min(1.0d, done / total) : -1.0d);
                        }
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (stream != null) {
                        stream.close();
                    }
                    progress.at("Checking the download...", 1.0d);
                    if (!sha256.equals(hash(partial))) {
                        Files.deleteIfExists(partial);
                        throw new IOException("the downloaded update does not match its signed hash");
                    }
                    if (MacOs.isMac() && !hasClass(partial, "com/faforever/neroxis/generator/bar/MacOs.class")) {
                        Files.deleteIfExists(partial);
                        System.err.println("update skipped: the published launcher has no macOS support");
                        return false;
                    }
                    long published = builtAt(partial);
                    if (published < runningBuiltAt()) {
                        Files.deleteIfExists(partial);
                        System.err.println("update skipped: the published launcher (built " + String.valueOf(Instant.ofEpochMilli(published)) + ") is older than this one (built " + String.valueOf(Instant.ofEpochMilli(runningBuiltAt())) + ")");
                        return false;
                    }
                } catch (Throwable th) {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        }
        IOException last = null;
        for (int attempt = 0; attempt < 15; attempt++) {
            try {
                Files.move(partial, staged, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                last = e;
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new IOException("launcher-new.jar is in use, the update waits for the next start", last);
    }

    static long runningBuiltAt() {
        try {
            InputStream stream = LauncherUpdater.class.getResourceAsStream("build-info.properties");
            if (stream != null) {
                try {
                    Properties info = new Properties();
                    info.load(stream);
                    long j = Long.parseLong(info.getProperty("built", "0").trim());
                    if (stream != null) {
                        stream.close();
                    }
                    return j;
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (stream != null) {
                stream.close();
            }
            return 0L;
        } catch (IOException | RuntimeException e) {
            return 0L;
        }
    }

    static String runningCommit() {
        try {
            InputStream stream = LauncherUpdater.class.getResourceAsStream("build-info.properties");
            if (stream != null) {
                try {
                    Properties info = new Properties();
                    info.load(stream);
                    String strTrim = info.getProperty("commit", "unknown").trim();
                    if (stream != null) {
                        stream.close();
                    }
                    return strTrim;
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (stream != null) {
                stream.close();
            }
            return "unknown";
        } catch (IOException | RuntimeException e) {
            return "unknown";
        }
    }

    static long builtAt(Path jar) {
        try {
            ZipFile zip = new ZipFile(jar.toFile());
            try {
                ZipEntry entry = zip.getEntry(BUILD_INFO);
                if (entry != null) {
                    Properties info = new Properties();
                    InputStream stream = zip.getInputStream(entry);
                    try {
                        info.load(stream);
                        if (stream != null) {
                            stream.close();
                        }
                        long j = Long.parseLong(info.getProperty("built", "0").trim());
                        zip.close();
                        return j;
                    } catch (Throwable th) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                zip.close();
                return 0L;
            } catch (Throwable th3) {
                try {
                    zip.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException | RuntimeException e) {
            return 0L;
        }
    }

    private static boolean hasClass(Path jar, String entry) {
        try {
            ZipFile zip = new ZipFile(jar.toFile());
            try {
                boolean z = zip.getEntry(entry) != null;
                zip.close();
                return z;
            } catch (Throwable th) {
                try {
                    zip.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static String field(String json, String name) {
        Matcher matcher = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    static String hash(Path file) throws IOException {
        try {
            InputStream stream = Files.newInputStream(file, new OpenOption[0]);
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] buffer = new byte[65536];
                while (true) {
                    int read = stream.read(buffer);
                    if (read <= 0) {
                        break;
                    }
                    digest.update(buffer, 0, read);
                    throw new IOException(e);
                }
                String hex = HexFormat.of().formatHex(digest.digest());
                if (stream != null) {
                    stream.close();
                }
                return hex;
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    private static boolean verify(String sha256, String signatureBase64) {
        try {
            PublicKey key = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)));
            Signature signature = Signature.getInstance("Ed25519");
            signature.initVerify(key);
            signature.update(sha256.getBytes(StandardCharsets.US_ASCII));
            return signature.verify(Base64.getDecoder().decode(signatureBase64));
        } catch (Exception e) {
            return false;
        }
    }
}
