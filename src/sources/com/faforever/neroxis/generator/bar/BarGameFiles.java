package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Legge i file del gioco installato (definizioni delle unita', modelli, texture, testi).
 *
 * <p>BAR scarica il gioco con il sistema "rapid" del motore:</p>
 * <ul>
 * <li>{@code rapid/<sito>/byar/versions.gz}: l'elenco delle versioni, una per riga: etichetta, codice, dipendenze, nome;</li>
 * <li>{@code packages/<codice>.sdp}: l'indice di una versione: per ogni file il nome e la sua impronta MD5;</li>
 * <li>{@code pool/<prime due cifre>/<resto dell'impronta>.gz}: il contenuto di ogni file, compresso.</li>
 * </ul>
 */
final class BarGameFiles {
    private static final String GAME = "byar";

    /** L'indice della versione installata: nome del file (minuscolo) -> impronta. */
    private record Index(Path file, long modified, String version, Map<String, String> entries, Map<String, String> names) {}

    private static Index cached;

    private BarGameFiles() {}

    /** Il nome della versione del gioco installata (per esempio "Beyond All Reason test-12345-abcdef"), o null. */
    static String installedVersion(Path data) {
        Index index = index(data);
        return index == null ? null : index.version();
    }

    /** Il contenuto di un file del gioco, o null se non c'e'. Il nome non distingue maiuscole e minuscole. */
    static byte[] read(Path data, String file) {
        Index index = index(data);
        if (index == null) {
            return null;
        }
        String md5 = index.entries().get(file.toLowerCase(Locale.ROOT));
        return md5 == null ? null : pooled(data, md5);
    }

    /** Tutti i file del gioco il cui nome soddisfa la condizione, nell'ordine dell'indice. */
    static Map<String, byte[]> readAll(Path data, Predicate<String> which) {
        Map<String, byte[]> files = new LinkedHashMap<>();
        Index index = index(data);
        if (index == null) {
            return files;
        }
        for (Map.Entry<String, String> entry : index.names().entrySet()) {
            if (which.test(entry.getKey())) {
                byte[] content = pooled(data, entry.getValue());
                if (content != null) {
                    files.put(entry.getKey(), content);
                }
            }
        }
        return files;
    }

    private static byte[] pooled(Path data, String md5) {
        Path file = data.resolve("pool").resolve(md5.substring(0, 2)).resolve(md5.substring(2) + ".gz");
        try (InputStream in = new GZIPInputStream(Files.newInputStream(file))) {
            return in.readAllBytes();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** L'indice della versione piu' recente tra quelle effettivamente scaricate; riletto solo se cambia. */
    private static synchronized Index index(Path data) {
        try {
            Path versions = null;
            try (Stream<Path> sites = Files.list(data.resolve("rapid"))) {
                for (Path site : sites.toList()) {
                    Path candidate = site.resolve(GAME).resolve("versions.gz");
                    if (Files.isRegularFile(candidate)) {
                        versions = candidate;
                    }
                }
            }
            if (versions == null) {
                return null;
            }
            List<String> lines;
            try (InputStream in = new GZIPInputStream(Files.newInputStream(versions))) {
                lines = new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
            }
            // le versioni sono in ordine di uscita: si parte dall'ultima e si prende la prima gia' scaricata
            for (int i = lines.size() - 1; i >= 0; i--) {
                String[] fields = lines.get(i).split(",");
                if (fields.length < 2) {
                    continue;
                }
                Path sdp = data.resolve("packages").resolve(fields[1] + ".sdp");
                if (!Files.isRegularFile(sdp)) {
                    continue;
                }
                long modified = Files.getLastModifiedTime(sdp).toMillis();
                if (cached != null && cached.file().equals(sdp) && cached.modified() == modified) {
                    return cached;
                }
                byte[] raw;
                try (InputStream in = new GZIPInputStream(Files.newInputStream(sdp))) {
                    raw = in.readAllBytes();
                }
                Map<String, String> entries = new LinkedHashMap<>();
                Map<String, String> names = new LinkedHashMap<>();
                HexFormat hex = HexFormat.of();
                int position = 0;
                // ogni voce: lunghezza del nome (1 byte), nome, impronta MD5 (16 byte), controllo (4), dimensione (4)
                while (position < raw.length) {
                    int length = raw[position] & 0xFF;
                    if (position + 1 + length + 24 > raw.length) {
                        break;
                    }
                    String name = new String(raw, position + 1, length, StandardCharsets.UTF_8);
                    String md5 = hex.formatHex(raw, position + 1 + length, position + 1 + length + 16);
                    names.put(name, md5);
                    entries.putIfAbsent(name.toLowerCase(Locale.ROOT), md5);
                    position += 1 + length + 24;
                }
                cached = new Index(sdp, modified, fields.length >= 4 ? fields[3].trim() : null, entries, names);
                return cached;
            }
        } catch (IOException | RuntimeException ignored) {
        }
        return null;
    }
}
