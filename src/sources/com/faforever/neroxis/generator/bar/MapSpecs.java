package com.faforever.neroxis.generator.bar;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Specifiche e opzioni di una mappa BAR, lette dal suo archivio: dimensioni e quote dal file .smf, vento,
 * marea, gravita', metallo, acqua e punti di partenza da mapinfo.lua.
 *
 * <p>Le mappe ufficiali (.sd7) si aprono con il 7-Zip di BAR, che e' lento: il risultato viene salvato
 * accanto alle miniature e riletto finche' l'archivio non cambia.</p>
 */
final class MapSpecs {
    private final Properties values = new Properties();

    private MapSpecs() {}

    /** Valore numerico, o NaN se la mappa non lo dichiara. */
    float number(String key) {
        try {
            return Float.parseFloat(values.getProperty(key, ""));
        } catch (NumberFormatException e) {
            return Float.NaN;
        }
    }

    boolean has(String key) {
        return !values.getProperty(key, "").isBlank();
    }

    String text(String key) {
        return values.getProperty(key, "");
    }

    boolean flag(String key) {
        return "true".equals(values.getProperty(key));
    }

    static MapSpecs read(Path data, Path cacheFolder, Path archive, String mapFile) {
        MapSpecs specs = new MapSpecs();
        Path cached = cacheFolder.resolve(archive.getFileName() + ".specs");
        try {
            if (Files.isRegularFile(cached) && Files.getLastModifiedTime(cached).compareTo(Files.getLastModifiedTime(archive)) >= 0) {
                try (Reader reader = Files.newBufferedReader(cached, StandardCharsets.UTF_8)) {
                    specs.values.load(reader);
                }
                return specs;
            }
        } catch (IOException | RuntimeException ignored) {
        }
        byte[] header = null;
        String info = null;
        try {
            if (archive.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".sdz")) {
                try (ZipFile zip = new ZipFile(archive.toFile())) {
                    ZipEntry smf = zip.getEntry(mapFile);
                    if (smf != null) {
                        try (InputStream in = zip.getInputStream(smf)) {
                            header = in.readNBytes(64);
                        }
                    }
                    ZipEntry lua = zip.getEntry("mapinfo.lua");
                    if (lua != null) {
                        try (InputStream in = zip.getInputStream(lua)) {
                            info = new String(in.readAllBytes(), StandardCharsets.ISO_8859_1);
                        }
                    }
                }
            } else {
                Path sevenZip = Platform.sevenZip(data);
                if (sevenZip != null && Files.isRegularFile(sevenZip)) {
                    byte[] lua = extract(sevenZip, archive, "mapinfo.lua", 4 << 20);
                    info = lua == null ? null : new String(lua, StandardCharsets.ISO_8859_1);
                    header = extract(sevenZip, archive, mapFile, 64);
                }
            }
        } catch (IOException | RuntimeException ignored) {
        }
        if (header != null && header.length >= 52 && new String(header, 0, 15, StandardCharsets.ISO_8859_1).equals("spring map file")) {
            ByteBuffer smf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
            specs.put("mapx", smf.getInt(24) / 64f);
            specs.put("mapy", smf.getInt(28) / 64f);
            specs.put("minheight", smf.getFloat(44));
            specs.put("maxheight", smf.getFloat(48));
        }
        if (info != null) {
            String lua = info.replaceAll("(?s)--\\[\\[.*?]]", "").replaceAll("--[^\\n]*", "");
            // mapinfo puo' correggere le quote scritte nel file .smf
            specs.copyNumber(lua, "minheight", "minheight");
            specs.copyNumber(lua, "maxheight", "maxheight");
            specs.copyNumber(lua, "gravity", "gravity");
            specs.copyNumber(lua, "tidalStrength", "tidal");
            specs.copyNumber(lua, "maxMetal", "maxmetal");
            specs.copyNumber(lua, "extractorRadius", "extractor");
            specs.copyNumber(lua, "maphardness", "hardness");
            specs.copyNumber(lua, "minWind", "minwind");
            specs.copyNumber(lua, "maxWind", "maxwind");
            specs.copyFlag(lua, "notDeformable", "notdeformable");
            specs.copyFlag(lua, "voidWater", "voidwater");
            Matcher damage = Pattern.compile("(?is)\\bwater\\s*=\\s*\\{.{0,400}?\\bdamage\\s*=\\s*(-?[\\d.]+)").matcher(lua);
            if (damage.find()) {
                specs.values.setProperty("waterdamage", damage.group(1));
            }
            specs.copyText(lua, "author", "author");
            specs.copyText(lua, "version", "version");
            int starts = 0;
            Matcher start = Pattern.compile("(?i)\\bstartPos\\s*=").matcher(lua);
            while (start.find()) {
                starts++;
            }
            if (starts > 0) {
                specs.values.setProperty("starts", String.valueOf(starts));
            }
        }
        if (!specs.values.isEmpty()) {
            try {
                Files.createDirectories(cacheFolder);
                try (Writer writer = Files.newBufferedWriter(cached, StandardCharsets.UTF_8)) {
                    specs.values.store(writer, null);
                }
            } catch (IOException ignored) {
            }
        }
        return specs;
    }

    private void put(String key, float value) {
        if (Float.isFinite(value)) {
            values.setProperty(key, String.valueOf(value));
        }
    }

    private void copyNumber(String lua, String luaKey, String key) {
        Matcher matcher = Pattern.compile("(?i)\\b" + luaKey + "\\s*=\\s*(-?[\\d.]+)").matcher(lua);
        if (matcher.find()) {
            values.setProperty(key, matcher.group(1));
        }
    }

    private void copyFlag(String lua, String luaKey, String key) {
        Matcher matcher = Pattern.compile("(?i)\\b" + luaKey + "\\s*=\\s*(true|false)").matcher(lua);
        if (matcher.find()) {
            values.setProperty(key, matcher.group(1).toLowerCase(Locale.ROOT));
        }
    }

    private void copyText(String lua, String luaKey, String key) {
        Matcher matcher = Pattern.compile("(?i)\\b" + luaKey + "\\s*=\\s*[\"']([^\"'\\n]*)[\"']").matcher(lua);
        if (matcher.find() && !matcher.group(1).isBlank()) {
            values.setProperty(key, matcher.group(1).trim());
        }
    }

    /** Legge al massimo {@code limit} byte di un file dentro un archivio .sd7, poi ferma 7-Zip. */
    private static byte[] extract(Path sevenZip, Path archive, String file, int limit) {
        Process process = null;
        try {
            process = new ProcessBuilder(sevenZip.toString(), "e", "-so", archive.toString(), file)
                .redirectError(ProcessBuilder.Redirect.DISCARD).start();
            byte[] bytes;
            try (InputStream in = process.getInputStream()) {
                bytes = in.readNBytes(limit);
            }
            return bytes.length == 0 ? null : bytes;
        } catch (IOException e) {
            return null;
        } finally {
            if (process != null) {
                process.destroyForcibly();
                try {
                    process.waitFor(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
