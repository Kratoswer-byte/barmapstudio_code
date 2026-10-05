package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Spazio cloud dell'utente in cui caricare le mappe, per chi entra nelle sue partite. Parla il protocollo S3
 * (Cloudflare R2, Backblaze B2, Amazon S3 e simili) senza librerie esterne.
 *
 * <p>Le chiavi di accesso stanno solo nelle preferenze dell'utente di questo PC: non finiscono mai nella
 * cartella del programma, nelle mappe o nei file di ingresso, che contengono soltanto l'indirizzo pubblico.</p>
 */
final class CloudStorage {
    private static final Preferences PREFS = Preferences.userNodeForPackage(CloudStorage.class).node("cloud");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    /** Indirizzi ammessi nei file di ingresso: niente caratteri che uno script potrebbe interpretare. */
    static final Pattern SAFE_URL = Pattern.compile("https?://[A-Za-z0-9._~:/\\-]+");

    /** Impostazioni dello spazio cloud. */
    record Settings(String endpoint, String region, String bucket, String accessKey, String secretKey, String publicUrl) {
        boolean complete() {
            return !endpoint.isBlank() && !bucket.isBlank() && !accessKey.isBlank() && !secretKey.isBlank() && !publicUrl.isBlank();
        }

        /** Regione usata nella firma: quella indicata, altrimenti ricavata dall'indirizzo, altrimenti "auto" (R2). */
        String signingRegion() {
            if (!region.isBlank()) {
                return region.trim();
            }
            Matcher match = Pattern.compile("s3[.-]([a-z0-9-]+)\\.(backblazeb2|amazonaws|wasabisys|scw)\\.").matcher(endpoint);
            return match.find() ? match.group(1) : "auto";
        }

        /** Indirizzo pubblico di un file caricato. */
        String publicUrlOf(String key) {
            return publicUrl.trim().replaceAll("/+$", "") + "/" + key;
        }
    }

    private CloudStorage() {}

    static Settings load() {
        return new Settings(PREFS.get("endpoint", ""), PREFS.get("region", ""), PREFS.get("bucket", ""),
            PREFS.get("accessKey", ""), PREFS.get("secretKey", ""), PREFS.get("publicUrl", ""));
    }

    static void save(Settings settings) {
        PREFS.put("endpoint", settings.endpoint().trim());
        PREFS.put("region", settings.region().trim());
        PREFS.put("bucket", settings.bucket().trim());
        PREFS.put("accessKey", settings.accessKey().trim());
        PREFS.put("secretKey", settings.secretKey().trim());
        PREFS.put("publicUrl", settings.publicUrl().trim());
    }

    /** Impronta SHA-256 di un file, in esadecimale minuscolo. */
    static String sha256(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1 << 16];
            for (int read; (read = in.read(buffer)) > 0; ) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    /**
     * Carica un file nello spazio cloud.
     *
     * @return l'indirizzo pubblico da cui scaricarlo
     */
    static String upload(Settings settings, Path file, String key, String sha256) throws IOException, InterruptedException {
        HttpRequest.Builder request = signed(settings, "PUT", key, sha256)
            .timeout(Duration.ofMinutes(30))
            .header("Content-Type", "application/octet-stream")
            .PUT(HttpRequest.BodyPublishers.ofFile(file));
        expectOk(client().send(request.build(), HttpResponse.BodyHandlers.ofString()), "upload");
        return settings.publicUrlOf(key);
    }

    /**
     * Prova completa: carica un piccolo file, lo rilegge dall'indirizzo pubblico come farebbe un amico e lo toglie.
     *
     * @throws IOException con una spiegazione del passo che non ha funzionato
     */
    static void test(Settings settings) throws IOException, InterruptedException {
        byte[] content = ("BAR Map Studio " + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
        String key = "barmapstudio_test.txt";
        String hash = hex(sha256(content));
        HttpRequest put = signed(settings, "PUT", key, hash)
            .timeout(Duration.ofSeconds(40))
            .header("Content-Type", "text/plain")
            .PUT(HttpRequest.BodyPublishers.ofByteArray(content)).build();
        expectOk(client().send(put, HttpResponse.BodyHandlers.ofString()), "upload");
        try {
            HttpRequest get = HttpRequest.newBuilder(URI.create(settings.publicUrlOf(key) + "?t=" + System.currentTimeMillis()))
                .timeout(Duration.ofSeconds(40)).GET().build();
            HttpResponse<byte[]> read = client().send(get, HttpResponse.BodyHandlers.ofByteArray());
            if (read.statusCode() != 200) {
                throw new IOException("public download: HTTP " + read.statusCode());
            }
            if (!Arrays.equals(read.body(), content)) {
                throw new IOException("public download: different content");
            }
        } finally {
            try {
                HttpRequest delete = signed(settings, "DELETE", key, hex(sha256(new byte[0])))
                    .timeout(Duration.ofSeconds(40)).DELETE().build();
                client().send(delete, HttpResponse.BodyHandlers.discarding());
            } catch (Exception ignored) {
                // una chiave che puo' solo scrivere non riesce a togliere il file di prova: non e' un errore
            }
        }
    }

    private static HttpClient client() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    private static void expectOk(HttpResponse<String> response, String step) throws IOException {
        if (response.statusCode() / 100 != 2) {
            String body = response.body() == null ? "" : response.body();
            Matcher code = Pattern.compile("<Code>([^<]+)</Code>").matcher(body);
            Matcher message = Pattern.compile("<Message>([^<]+)</Message>").matcher(body);
            throw new IOException(step + ": HTTP " + response.statusCode()
                + (code.find() ? " " + code.group(1) : "") + (message.find() ? " - " + message.group(1) : ""));
        }
    }

    /** Richiesta firmata (AWS Signature V4) verso bucket/chiave in stile percorso. */
    private static HttpRequest.Builder signed(Settings settings, String method, String key, String payloadHash) {
        String endpoint = settings.endpoint().trim().replaceAll("/+$", "");
        if (!endpoint.contains("://")) {
            endpoint = "https://" + endpoint;
        }
        String path = "/" + encodePath(settings.bucket().trim()) + "/" + encodePath(key);
        URI uri = URI.create(endpoint + path);
        String host = uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "");
        String stamp = ZonedDateTime.now(ZoneOffset.UTC).format(STAMP);
        Map<String, String> headers = new TreeMap<>();
        headers.put("host", host);
        headers.put("x-amz-content-sha256", payloadHash);
        headers.put("x-amz-date", stamp);
        String authorization = authorization(method, uri.getRawPath(), headers, payloadHash, settings.signingRegion(),
            settings.accessKey().trim(), settings.secretKey().trim(), stamp);
        return HttpRequest.newBuilder(uri)
            .header("x-amz-content-sha256", payloadHash)
            .header("x-amz-date", stamp)
            .header("Authorization", authorization);
    }

    /**
     * Intestazione Authorization di AWS Signature V4 per il servizio s3.
     *
     * @param headers intestazioni firmate, nomi in minuscolo, in ordine alfabetico
     */
    static String authorization(String method, String canonicalPath, Map<String, String> headers, String payloadHash,
                                String region, String accessKey, String secretKey, String stamp) {
        String date = stamp.substring(0, 8);
        StringBuilder canonicalHeaders = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            canonicalHeaders.append(header.getKey()).append(':').append(header.getValue().trim()).append('\n');
        }
        String signedHeaders = String.join(";", headers.keySet());
        String canonicalRequest = method + "\n" + canonicalPath + "\n\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + payloadHash;
        String scope = date + "/" + region + "/s3/aws4_request";
        String toSign = "AWS4-HMAC-SHA256\n" + stamp + "\n" + scope + "\n" + hex(sha256(canonicalRequest.getBytes(StandardCharsets.UTF_8)));
        byte[] signingKey = hmac(hmac(hmac(hmac(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8), date), region), "s3"), "aws4_request");
        return "AWS4-HMAC-SHA256 Credential=" + accessKey + "/" + scope + ", SignedHeaders=" + signedHeaders
            + ", Signature=" + hex(hmac(signingKey, toSign));
    }

    /** Codifica di un percorso come la vuole S3: tutto tranne lettere, cifre, - . _ ~ e la barra. */
    static String encodePath(String path) {
        StringBuilder out = new StringBuilder();
        for (byte b : path.getBytes(StandardCharsets.UTF_8)) {
            char c = (char) (b & 0xFF);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '.' || c == '_' || c == '~' || c == '/') {
                out.append(c);
            } else {
                out.append('%').append(String.format("%02X", b & 0xFF));
            }
        }
        return out.toString();
    }

    private static byte[] sha256(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] hmac(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hex(byte[] data) {
        return HexFormat.of().formatHex(data);
    }
}
