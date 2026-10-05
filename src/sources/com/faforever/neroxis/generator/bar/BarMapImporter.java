package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.bar.BarBiomes;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Legge una mappa BAR esistente (.sd7 / .sdz) per caricarla nell'editor come base modificabile:
 * forma del terreno (SMF), texture colore (SMT, ridotta), punti di partenza e luce/acqua
 * (mapinfo.lua), punti metallo (layout o metal map) e geyser (feature "geovent").
 */
public final class BarMapImporter {

    public static final class Imported {
        public String name;
        /** Dimensioni in celle dell'editor (1 cella = 2 quadrati SMF = 16 elmi). */
        public int cellsW, cellsH;
        /** Quote reali in elmi. */
        public float minHeight, maxHeight;
        /** Heightmap a 16 bit, alla dimensione reale della mappa in quadrati (+1). */
        public BufferedImage heightmap;
        public BufferedImage texture;
        /** Posizioni in elmi: {x, z}. */
        public final List<float[]> spawns = new ArrayList<>();
        public final List<float[]> mexes = new ArrayList<>();
        public final List<float[]> geos = new ArrayList<>();
        public BarBiomes.BarBiome biome;
    }

    private BarMapImporter() {}

    public static Imported load(File archive) throws Exception {
        Path data = Platform.defaultData();
        Path sevenZip = data != null ? Platform.sevenZip(data) : null;
        if (sevenZip == null) {
            throw new IllegalStateException("7-Zip di BAR non trovato: serve l'installazione di Beyond All Reason.");
        }
        List<String> files = list(sevenZip, archive);
        String smf = files.stream().filter(f -> f.toLowerCase().endsWith(".smf")).findFirst().orElse(null);
        if (smf == null) {
            throw new IllegalArgumentException("L'archivio non contiene una mappa (.smf).");
        }
        Path tmp = Files.createTempDirectory("barmap-import");
        try {
            List<String> wanted = new ArrayList<>();
            wanted.add(smf);
            for (String f : files) {
                String low = f.toLowerCase().replace('\\', '/');
                if (low.endsWith(".smt") || low.equals("mapinfo.lua") || low.endsWith("map_metal_layout.lua")) {
                    wanted.add(f);
                }
            }
            List<String> command = new ArrayList<>(List.of(sevenZip.toString(), "e", "-y", "-o" + tmp, archive.getAbsolutePath()));
            command.addAll(wanted);
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            process.getInputStream().readAllBytes();
            process.waitFor();

            Imported result = new Imported();
            String info = Files.isRegularFile(tmp.resolve("mapinfo.lua")) ? stripComments(Files.readString(tmp.resolve("mapinfo.lua"), StandardCharsets.ISO_8859_1)) : "";
            Matcher nameMatcher = Pattern.compile("\\bname\\s*=\\s*[\"']([^\"']+)[\"']").matcher(info);
            result.name = nameMatcher.find() ? nameMatcher.group(1) : archive.getName();

            byte[] smfBytes = Files.readAllBytes(tmp.resolve(new File(smf).getName()));
            ByteBuffer b = ByteBuffer.wrap(smfBytes).order(ByteOrder.LITTLE_ENDIAN);
            if (!new String(smfBytes, 0, 15, StandardCharsets.US_ASCII).equals("spring map file")) {
                throw new IllegalArgumentException("File .smf non valido.");
            }
            b.position(24);
            int mapX = b.getInt();
            int mapY = b.getInt();
            b.getInt(); // squareSize
            b.getInt(); // texelPerSquare
            b.getInt(); // tileSize
            float minH = b.getFloat();
            float maxH = b.getFloat();
            int heightPtr = b.getInt();
            b.getInt(); // typeMap
            int tilesPtr = b.getInt();
            b.getInt(); // minimap
            int metalPtr = b.getInt();
            int featurePtr = b.getInt();
            String smfBlock = block(info, "smf");
            minH = number(smfBlock, "minheight", minH);
            maxH = number(smfBlock, "maxheight", maxH);
            result.minHeight = minH;
            result.maxHeight = maxH;
            result.cellsW = mapX / 2;
            result.cellsH = mapY / 2;

            // --- heightmap alla dimensione reale della mappa (il generatore la colloca da solo se e' rettangolare)
            BufferedImage hm = new BufferedImage(mapX + 1, mapY + 1, BufferedImage.TYPE_USHORT_GRAY);
            WritableRaster raster = hm.getRaster();
            for (int y = 0; y <= mapY; y++) {
                for (int x = 0; x <= mapX; x++) {
                    raster.setSample(x, y, 0, b.getShort(heightPtr + (2 * ((y * (mapX + 1)) + x))) & 0xFFFF);
                }
            }
            result.heightmap = hm;

            // --- texture colore: un blocco 8x8 (mip) per ogni tile da 32 pixel
            b.position(tilesPtr);
            int numFiles = b.getInt();
            b.getInt(); // numTiles
            List<byte[]> smts = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            for (int i = 0; i < numFiles; i++) {
                int count = b.getInt();
                int start = b.position();
                int end = start;
                while (smfBytes[end] != 0) end++;
                String smtName = new String(smfBytes, start, end - start, StandardCharsets.ISO_8859_1);
                b.position(end + 1);
                Path smtPath = tmp.resolve(new File(smtName.replace('\\', '/')).getName());
                smts.add(Files.isRegularFile(smtPath) ? Files.readAllBytes(smtPath) : null);
                counts.add(count);
            }
            int cw = mapX / 4;
            int ch = mapY / 4;
            int indexPos = b.position();
            BufferedImage texture = new BufferedImage(cw * 8, ch * 8, BufferedImage.TYPE_INT_RGB);
            int[] block = new int[16];
            boolean anyTile = false;
            for (int cy = 0; cy < ch; cy++) {
                for (int cx = 0; cx < cw; cx++) {
                    int tile = b.getInt(indexPos + (4 * ((cy * cw) + cx)));
                    int file = 0;
                    while (file < counts.size() && tile >= counts.get(file)) {
                        tile -= counts.get(file);
                        file++;
                    }
                    if (file >= smts.size() || smts.get(file) == null) continue;
                    byte[] smt = smts.get(file);
                    int offset = 32 + (tile * 680) + 640;
                    if (offset + 32 > smt.length) continue;
                    anyTile = true;
                    for (int by = 0; by < 2; by++) {
                        for (int bx = 0; bx < 2; bx++) {
                            decodeDxt1(smt, offset + (8 * ((by * 2) + bx)), block);
                            for (int p = 0; p < 16; p++) {
                                texture.setRGB((cx * 8) + (bx * 4) + (p % 4), (cy * 8) + (by * 4) + (p / 4), block[p]);
                            }
                        }
                    }
                }
            }
            result.texture = anyTile ? texture : null;

            // --- punti di partenza (mapinfo teams)
            Matcher start = Pattern.compile("startPos\\s*=\\s*\\{\\s*x\\s*=\\s*(-?[0-9.]+)\\s*,\\s*z\\s*=\\s*(-?[0-9.]+)", Pattern.CASE_INSENSITIVE).matcher(info);
            while (start.find()) {
                result.spawns.add(new float[]{Float.parseFloat(start.group(1)), Float.parseFloat(start.group(2))});
            }

            // --- punti metallo: file di layout se presente, altrimenti macchie della metal map
            Path layout = tmp.resolve("map_metal_layout.lua");
            if (Files.isRegularFile(layout)) {
                Matcher spot = Pattern.compile("x\\s*=\\s*(-?[0-9.]+)\\s*,\\s*z\\s*=\\s*(-?[0-9.]+)", Pattern.CASE_INSENSITIVE)
                    .matcher(stripComments(Files.readString(layout, StandardCharsets.ISO_8859_1)));
                while (spot.find()) {
                    result.mexes.add(new float[]{Float.parseFloat(spot.group(1)), Float.parseFloat(spot.group(2))});
                }
            }
            if (result.mexes.isEmpty()) {
                findMetalSpots(smfBytes, metalPtr, mapX / 2, mapY / 2, result.mexes);
            }

            // --- geyser: feature "geovent" dell'SMF
            b.position(featurePtr);
            int numTypes = b.getInt();
            int numFeatures = b.getInt();
            List<String> types = new ArrayList<>();
            int pos = b.position();
            for (int i = 0; i < numTypes; i++) {
                int end = pos;
                while (smfBytes[end] != 0) end++;
                types.add(new String(smfBytes, pos, end - pos, StandardCharsets.ISO_8859_1).toLowerCase());
                pos = end + 1;
            }
            // record feature: tipo (int), x, y, z, rotazione, scala (float) = 24 byte
            for (int i = 0; i < numFeatures && pos + 24 <= smfBytes.length; i++, pos += 24) {
                int type = b.getInt(pos);
                if (type >= 0 && type < types.size() && types.get(type).contains("geovent")) {
                    result.geos.add(new float[]{b.getFloat(pos + 4), b.getFloat(pos + 12)});
                }
            }

            result.biome = readBiome(result.name, info);
            return result;
        } finally {
            try (var walk = Files.walk(tmp)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            } catch (Exception ignored) {}
        }
    }

    /** Luce, atmosfera e acqua della mappa, come bioma BAR (i colori del terreno vengono dalla texture importata). */
    private static BarBiomes.BarBiome readBiome(String name, String info) {
        String light = block(info, "lighting");
        String atmosphere = block(info, "atmosphere");
        String water = block(info, "water");
        float[] neutral = {0.45f, 0.42f, 0.36f};
        return new BarBiomes.BarBiome("BAR: " + name, name, "", "DESERT",
            new float[][]{neutral, null, null, new float[]{0.36f, 0.34f, 0.31f}, null, null},
            new float[][]{triple(light, "sunDir"), triple(light, "groundAmbientColor"), triple(light, "groundDiffuseColor"),
                triple(light, "groundSpecularColor"), triple(light, "unitAmbientColor"), triple(light, "unitDiffuseColor"), triple(light, "unitSpecularColor")},
            number(light, "groundShadowDensity", Float.NaN),
            new float[][]{triple(atmosphere, "fogColor"), triple(atmosphere, "skyColor"), triple(atmosphere, "sunColor"), triple(atmosphere, "cloudColor")},
            number(atmosphere, "fogStart", Float.NaN), number(atmosphere, "fogEnd", Float.NaN),
            new float[][]{triple(water, "absorb"), triple(water, "baseColor"), triple(water, "minColor"), triple(water, "surfaceColor"), triple(water, "planeColor")},
            number(water, "surfaceAlpha", Float.NaN));
    }

    /** Macchie della metal map (1 byte ogni 2 quadrati) -> centro di ogni macchia, in elmi. */
    private static void findMetalSpots(byte[] smf, int metalPtr, int w, int h, List<float[]> out) {
        if (metalPtr <= 0 || metalPtr + (w * h) > smf.length) return;
        boolean[] seen = new boolean[w * h];
        int[] stack = new int[w * h];
        for (int startIndex = 0; startIndex < w * h; startIndex++) {
            if (seen[startIndex] || smf[metalPtr + startIndex] == 0) continue;
            int top = 0;
            stack[top++] = startIndex;
            seen[startIndex] = true;
            long sumX = 0, sumY = 0;
            int count = 0;
            while (top > 0) {
                int index = stack[--top];
                int x = index % w, y = index / w;
                sumX += x; sumY += y; count++;
                int[] neighbours = {index - 1, index + 1, index - w, index + w};
                for (int k = 0; k < 4; k++) {
                    int n = neighbours[k];
                    if (n < 0 || n >= w * h || seen[n] || smf[metalPtr + n] == 0) continue;
                    if (k < 2 && n / w != y) continue;
                    seen[n] = true;
                    stack[top++] = n;
                }
            }
            // una macchia grande come mezza mappa e' metallo diffuso, non un punto
            if (count >= 2 && count < 400 && out.size() < 400) {
                out.add(new float[]{(sumX / (float) count + 0.5f) * 16.0f, (sumY / (float) count + 0.5f) * 16.0f});
            }
        }
    }

    private static void decodeDxt1(byte[] data, int offset, int[] out) {
        int c0 = (data[offset] & 255) | ((data[offset + 1] & 255) << 8);
        int c1 = (data[offset + 2] & 255) | ((data[offset + 3] & 255) << 8);
        int[][] palette = new int[4][];
        palette[0] = rgb565(c0);
        palette[1] = rgb565(c1);
        if (c0 > c1) {
            palette[2] = mix(palette[0], palette[1], 2, 1);
            palette[3] = mix(palette[0], palette[1], 1, 2);
        } else {
            palette[2] = mix(palette[0], palette[1], 1, 1);
            palette[3] = new int[]{0, 0, 0};
        }
        long bits = (data[offset + 4] & 255L) | ((data[offset + 5] & 255L) << 8) | ((data[offset + 6] & 255L) << 16) | ((data[offset + 7] & 255L) << 24);
        for (int i = 0; i < 16; i++) {
            int[] c = palette[(int) ((bits >> (2 * i)) & 3)];
            out[i] = (c[0] << 16) | (c[1] << 8) | c[2];
        }
    }

    private static int[] rgb565(int c) {
        return new int[]{((c >> 11) & 31) * 255 / 31, ((c >> 5) & 63) * 255 / 63, (c & 31) * 255 / 31};
    }

    private static int[] mix(int[] a, int[] b, int wa, int wb) {
        return new int[]{((a[0] * wa) + (b[0] * wb)) / (wa + wb), ((a[1] * wa) + (b[1] * wb)) / (wa + wb), ((a[2] * wa) + (b[2] * wb)) / (wa + wb)};
    }

    private static List<String> list(Path sevenZip, File archive) throws Exception {
        Process process = new ProcessBuilder(sevenZip.toString(), "l", "-slt", archive.getAbsolutePath()).redirectErrorStream(true).start();
        String out = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        process.waitFor();
        List<String> files = new ArrayList<>();
        Matcher m = Pattern.compile("(?m)^Path = (.+)$").matcher(out);
        boolean first = true;
        while (m.find()) {
            if (first) { first = false; continue; } // la prima voce e' l'archivio stesso
            files.add(m.group(1).trim());
        }
        return files;
    }

    private static String stripComments(String lua) {
        return lua.replaceAll("(?s)--\\[\\[.*?\\]\\]", "").replaceAll("--[^\\n]*", "");
    }

    /** Contenuto di "key = { ... }" (graffe bilanciate), stringa vuota se manca. */
    private static String block(String text, String key) {
        Matcher m = Pattern.compile("\\b" + key + "\\s*=\\s*\\{", Pattern.CASE_INSENSITIVE).matcher(text);
        if (!m.find()) return "";
        int depth = 1;
        int i = m.end();
        while (i < text.length() && depth > 0) {
            char c = text.charAt(i++);
            if (c == '{') depth++;
            else if (c == '}') depth--;
        }
        return text.substring(m.end(), Math.max(m.end(), i - 1));
    }

    private static float number(String text, String key, float fallback) {
        Matcher m = Pattern.compile("\\b" + key + "\\s*=\\s*(-?[0-9]+\\.?[0-9]*)", Pattern.CASE_INSENSITIVE).matcher(text);
        return m.find() ? Float.parseFloat(m.group(1)) : fallback;
    }

    private static float[] triple(String text, String key) {
        Matcher m = Pattern.compile("\\b" + key + "\\s*=\\s*\\{([^{}]*)\\}", Pattern.CASE_INSENSITIVE).matcher(text);
        if (!m.find()) return null;
        Matcher n = Pattern.compile("-?[0-9]+\\.?[0-9]*").matcher(m.group(1));
        float[] values = new float[3];
        for (int i = 0; i < 3; i++) {
            if (!n.find()) return null;
            values[i] = Float.parseFloat(n.group());
        }
        return values;
    }
}
