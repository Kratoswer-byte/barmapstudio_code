package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

/**
 * Le mappe installate in BAR: l'elenco, l'anteprima e i punti delle risorse di ciascuna.
 *
 * <p>L'elenco viene dal registro degli archivi che il motore tiene in {@code cache/ArchiveCache*.lua}.
 * Anteprima e risorse si leggono dal file .smf della mappa (formato pubblico del motore): l'anteprima e'
 * un'immagine 1024x1024 compressa DXT1, il metallo una mappa a meta' risoluzione, i geyser stanno tra gli
 * oggetti. Le mappe .sdz sono archivi zip; le .sd7 si aprono con il 7-Zip di BAR, che e' lento: per
 * questo i risultati vengono salvati in una cartella di cache e riletti finche' l'archivio non cambia.</p>
 */
final class BarMaps {
    private static final int SMF_HEADER = 80;
    private static final int MINIMAP_SIDE = 1024;
    /** Riga del registro degli archivi: rientro, chiave e valore. */
    private static final Pattern CACHE_LINE = Pattern.compile("^(\\t+)(\\w+) = \"?(.*?)\"?,$");

    /** Una mappa installata. */
    record InstalledMap(Path archive, String name, String description, String author, String mapFile) {}

    /** Punti metallo e geyser, in coordinate da 0 a 1 rispetto ai lati della mappa. */
    record Marks(List<float[]> metal, List<float[]> geos) {
        static final Marks NONE = new Marks(List.of(), List.of());
    }

    private BarMaps() {}

    // ------------------------------------------------------------------ elenco

    /** Le mappe che il motore conosce e il cui archivio esiste ancora, in ordine di nome. */
    static List<InstalledMap> list(Path data) {
        List<InstalledMap> maps = new ArrayList<>();
        Path registry = null;
        try (Stream<Path> files = Files.list(data.resolve("cache"))) {
            registry = files.filter(file -> file.getFileName().toString().matches("ArchiveCache\\d*\\.lua"))
                .max(Comparator.comparingLong(file -> file.toFile().lastModified())).orElse(null);
        } catch (IOException | RuntimeException ignored) {
        }
        if (registry == null) {
            return maps;
        }
        // ogni archivio: una riga "name" a tre rientri, poi le sue proprieta' a quattro rientri
        String archive = null;
        Map<String, String> values = new HashMap<>();
        try (Stream<String> lines = Files.lines(registry, StandardCharsets.ISO_8859_1)) {
            for (String line : (Iterable<String>) lines::iterator) {
                Matcher matcher = CACHE_LINE.matcher(line);
                if (!matcher.find()) {
                    continue;
                }
                int depth = matcher.group(1).length();
                if (depth == 3 && matcher.group(2).equals("name")) {
                    addMap(maps, data, archive, values);
                    archive = matcher.group(3);
                    values.clear();
                } else if (depth == 4) {
                    values.put(matcher.group(2), matcher.group(3));
                }
            }
            addMap(maps, data, archive, values);
        } catch (IOException | RuntimeException ignored) {
        }
        maps.sort(Comparator.comparing(map -> map.name().toLowerCase(Locale.ROOT)));
        return maps;
    }

    private static void addMap(List<InstalledMap> maps, Path data, String archive, Map<String, String> values) {
        // modtype 3 = mappa
        if (archive == null || !"3".equals(values.get("modtype")) || values.get("name") == null || values.get("mapfile") == null) {
            return;
        }
        Path file = data.resolve("maps").resolve(archive);
        if (Files.isRegularFile(file)) {
            maps.add(new InstalledMap(file, values.get("name"), values.getOrDefault("description", ""),
                values.getOrDefault("author", ""), values.get("mapfile")));
        }
    }

    // ------------------------------------------------------------------ anteprima e risorse

    /** L'anteprima 1024x1024 della mappa, o null se non si riesce a leggerla. */
    static BufferedImage minimap(Path data, Path cacheFolder, InstalledMap map) {
        Path cached = cacheFolder.resolve(map.archive().getFileName() + ".1024.png");
        try {
            if (fresh(cached, map.archive())) {
                return ImageIO.read(cached.toFile());
            }
        } catch (IOException | RuntimeException ignored) {
        }
        try {
            if (isZip(map)) {
                // le mappe create con lo Studio portano gia' un'anteprima pronta
                try (ZipFile zip = new ZipFile(map.archive().toFile())) {
                    ZipEntry preview = zip.stream().filter(entry -> entry.getName().endsWith("_preview.png")).findFirst().orElse(null);
                    if (preview != null) {
                        try (InputStream in = zip.getInputStream(preview)) {
                            BufferedImage image = ImageIO.read(in);
                            if (image != null) {
                                return image;
                            }
                        }
                    }
                }
            }
            byte[] smf = readSmf(data, map);
            if (smf == null) {
                return null;
            }
            saveMarks(cacheFolder, map, parseMarks(smf));
            BufferedImage image = decodeMinimap(smf);
            if (image != null) {
                Files.createDirectories(cacheFolder);
                ImageIO.write(image, "png", cached.toFile());
            }
            return image;
        } catch (IOException | RuntimeException | OutOfMemoryError e) {
            return null;
        }
    }

    /** I punti metallo e i geyser della mappa. */
    static Marks marks(Path data, Path cacheFolder, InstalledMap map) {
        Path cached = marksFile(cacheFolder, map);
        try {
            if (fresh(cached, map.archive())) {
                List<float[]> metal = new ArrayList<>();
                List<float[]> geos = new ArrayList<>();
                for (String line : Files.readAllLines(cached)) {
                    String[] fields = line.split(" ");
                    if (fields.length == 3) {
                        (fields[0].equals("m") ? metal : geos).add(new float[]{Float.parseFloat(fields[1]), Float.parseFloat(fields[2])});
                    }
                }
                return new Marks(metal, geos);
            }
        } catch (IOException | RuntimeException ignored) {
        }
        try {
            byte[] smf = readSmf(data, map);
            if (smf == null) {
                return Marks.NONE;
            }
            Marks marks = parseMarks(smf);
            saveMarks(cacheFolder, map, marks);
            return marks;
        } catch (IOException | RuntimeException | OutOfMemoryError e) {
            return Marks.NONE;
        }
    }

    private static boolean isZip(InstalledMap map) {
        return map.archive().getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".sdz");
    }

    private static boolean fresh(Path cached, Path archive) throws IOException {
        return Files.isRegularFile(cached) && Files.getLastModifiedTime(cached).compareTo(Files.getLastModifiedTime(archive)) >= 0;
    }

    private static Path marksFile(Path cacheFolder, InstalledMap map) {
        return cacheFolder.resolve(map.archive().getFileName() + ".marks");
    }

    private static void saveMarks(Path cacheFolder, InstalledMap map, Marks marks) {
        try {
            Files.createDirectories(cacheFolder);
            List<String> lines = new ArrayList<>();
            for (float[] spot : marks.metal()) {
                lines.add(String.format(Locale.ROOT, "m %.4f %.4f", spot[0], spot[1]));
            }
            for (float[] geo : marks.geos()) {
                lines.add(String.format(Locale.ROOT, "g %.4f %.4f", geo[0], geo[1]));
            }
            Files.write(marksFile(cacheFolder, map), lines);
        } catch (IOException | RuntimeException ignored) {
        }
    }

    /** Il file .smf della mappa, letto dall'archivio. */
    private static byte[] readSmf(Path data, InstalledMap map) throws IOException {
        if (isZip(map)) {
            try (ZipFile zip = new ZipFile(map.archive().toFile())) {
                ZipEntry smf = zip.getEntry(map.mapFile());
                if (smf == null) {
                    return null;
                }
                try (InputStream in = zip.getInputStream(smf)) {
                    return in.readAllBytes();
                }
            }
        }
        Path sevenZip = BarLocator.sevenZip(data);
        if (sevenZip == null) {
            return null;
        }
        Process process = new ProcessBuilder(sevenZip.toString(), "e", "-so", map.archive().toString(), map.mapFile())
            .redirectError(ProcessBuilder.Redirect.DISCARD).start();
        try (InputStream in = process.getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return bytes.length == 0 ? null : bytes;
        } finally {
            process.destroy();
        }
    }

    /** Decodifica l'anteprima: 256x256 blocchi DXT1 da 4x4 pixel, a partire dalla posizione scritta nell'intestazione. */
    private static BufferedImage decodeMinimap(byte[] smf) {
        if (smf.length < SMF_HEADER) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(smf).order(ByteOrder.LITTLE_ENDIAN);
        int offset = buffer.getInt(64);
        int blocks = MINIMAP_SIDE / 4;
        if (offset < SMF_HEADER || (long) offset + (long) blocks * blocks * 8 > smf.length) {
            return null;
        }
        int[] pixels = new int[MINIMAP_SIDE * MINIMAP_SIDE];
        int[] palette = new int[4];
        int position = offset;
        for (int blockY = 0; blockY < blocks; blockY++) {
            for (int blockX = 0; blockX < blocks; blockX++) {
                int first = buffer.getShort(position) & 0xFFFF;
                int second = buffer.getShort(position + 2) & 0xFFFF;
                int lookup = buffer.getInt(position + 4);
                position += 8;
                palette[0] = colour565(first);
                palette[1] = colour565(second);
                if (first > second) {
                    palette[2] = blend(palette[0], palette[1], 2, 1);
                    palette[3] = blend(palette[0], palette[1], 1, 2);
                } else {
                    palette[2] = blend(palette[0], palette[1], 1, 1);
                    palette[3] = 0;
                }
                for (int pixel = 0; pixel < 16; pixel++) {
                    int x = blockX * 4 + (pixel & 3);
                    int y = blockY * 4 + (pixel >> 2);
                    pixels[y * MINIMAP_SIDE + x] = palette[(lookup >>> (pixel * 2)) & 3];
                }
            }
        }
        BufferedImage image = new BufferedImage(MINIMAP_SIDE, MINIMAP_SIDE, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, MINIMAP_SIDE, MINIMAP_SIDE, pixels, 0, MINIMAP_SIDE);
        return image;
    }

    private static int colour565(int colour) {
        int red = ((colour >> 11) & 31) * 255 / 31;
        int green = ((colour >> 5) & 63) * 255 / 63;
        int blue = (colour & 31) * 255 / 31;
        return (red << 16) | (green << 8) | blue;
    }

    private static int blend(int a, int b, int weightA, int weightB) {
        int total = weightA + weightB;
        int red = (((a >> 16) & 255) * weightA + ((b >> 16) & 255) * weightB) / total;
        int green = (((a >> 8) & 255) * weightA + ((b >> 8) & 255) * weightB) / total;
        int blue = ((a & 255) * weightA + (b & 255) * weightB) / total;
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Cerca i punti metallo e i geyser nel file .smf.
     * Un punto metallo e' una macchia di celle non vuote nella mappa del metallo: se ne prende il centro.
     */
    private static Marks parseMarks(byte[] smf) {
        if (smf.length < SMF_HEADER) {
            return Marks.NONE;
        }
        ByteBuffer buffer = ByteBuffer.wrap(smf).order(ByteOrder.LITTLE_ENDIAN);
        int mapX = buffer.getInt(24);
        int mapY = buffer.getInt(28);
        int metalOffset = buffer.getInt(68);
        int featureOffset = buffer.getInt(72);
        int width = mapX / 2;
        int height = mapY / 2;

        List<float[]> metal = new ArrayList<>();
        if (width > 0 && height > 0 && metalOffset > 0 && (long) metalOffset + (long) width * height <= smf.length) {
            boolean[] visited = new boolean[width * height];
            int[] pending = new int[width * height];
            for (int start = 0; start < width * height; start++) {
                if (visited[start] || smf[metalOffset + start] == 0) {
                    continue;
                }
                int cells = 0;
                long sumX = 0;
                long sumY = 0;
                int top = 0;
                pending[top++] = start;
                visited[start] = true;
                while (top > 0) {
                    int cell = pending[--top];
                    int x = cell % width;
                    int y = cell / width;
                    cells++;
                    sumX += x;
                    sumY += y;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx < 0 || ny < 0 || nx >= width || ny >= height) {
                                continue;
                            }
                            int next = ny * width + nx;
                            if (!visited[next] && smf[metalOffset + next] != 0) {
                                visited[next] = true;
                                pending[top++] = next;
                            }
                        }
                    }
                }
                // una macchia enorme non e' un punto ma metallo sparso ovunque
                if (cells <= 400) {
                    metal.add(new float[]{((float) sumX / cells + 0.5f) / width, ((float) sumY / cells + 0.5f) / height});
                }
            }
            if (metal.size() > 600) {
                metal.clear();
            }
        }

        List<float[]> geos = new ArrayList<>();
        if (featureOffset > 0 && featureOffset + 8 <= smf.length) {
            int typeCount = buffer.getInt(featureOffset);
            int featureCount = buffer.getInt(featureOffset + 4);
            int position = featureOffset + 8;
            // prima i nomi dei tipi di oggetto, uno dopo l'altro chiusi da uno zero...
            List<String> types = new ArrayList<>();
            for (int i = 0; i < typeCount && position < smf.length; i++) {
                int end = position;
                while (end < smf.length && smf[end] != 0) {
                    end++;
                }
                types.add(new String(smf, position, end - position, StandardCharsets.ISO_8859_1).toLowerCase(Locale.ROOT));
                position = end + 1;
            }
            // ...poi gli oggetti: tipo, posizione x y z, rotazione, dimensione (24 byte)
            float mapWidth = mapX * 8.0f;
            float mapHeight = mapY * 8.0f;
            for (int i = 0; i < featureCount && position + 24 <= smf.length; i++, position += 24) {
                int type = buffer.getInt(position);
                if (type >= 0 && type < types.size() && types.get(type).contains("geovent")) {
                    geos.add(new float[]{buffer.getFloat(position + 4) / mapWidth, buffer.getFloat(position + 12) / mapHeight});
                }
            }
        }
        return new Marks(metal, geos);
    }
}
