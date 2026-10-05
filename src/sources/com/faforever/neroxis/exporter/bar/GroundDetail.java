package com.faforever.neroxis.exporter.bar;

import java.util.SplittableRandom;
import java.util.stream.IntStream;

/**
 * Le texture di dettaglio del suolo: per ogni materiale un'immagine ripetibile 1024x1024 che il gioco
 * sovrappone al terreno visto da vicino. Nei primi tre canali c'e' la direzione della superficie
 * (per le luci), nel quarto la variazione di luminosita'.
 *
 * <p>Ogni materiale nasce da un rilievo in scala di grigi costruito con rumore ripetibile (i bordi
 * combaciano), a cui si aggiungono sassi o fili d'erba; dal rilievo si ricavano pendenze e dettaglio.</p>
 */
final class GroundDetail {
    static final int SIZE = 1024;
    private static final int WRAP = SIZE - 1;
    private static final int PIXELS = SIZE * SIZE;
    private static final int TGA_HEADER = 18;
    private static final double FULL_TURN = 6.283185307179586d;

    /** I materiali disponibili, con la forza del rilievo e del dettaglio. */
    enum Material {
        ROCK(0.17f, 0.032f),
        GRAVEL(0.26f, 0.05f),
        GRASS(0.28f, 0.06f),
        SNOW(0.11f, 0.02f),
        DUST(0.19f, 0.036f),
        CRACKED(0.25f, 0.052f),
        SEABED(0.1f, 0.022f);

        private final float normalStrength;
        private final float detailStrength;

        Material(float normalStrength, float detailStrength) {
            this.normalStrength = normalStrength;
            this.detailStrength = detailStrength;
        }
    }

    private GroundDetail() {}

    /** La texture di dettaglio di un materiale, come file .tga a 32 bit. */
    static byte[] createTexture(Material material, int seed) {
        float[] relief = buildRelief(material, seed);
        float[] detail = removeLargeShapes(relief, 20);
        rescale(detail, material.detailStrength);

        float[] slopeX = new float[PIXELS];
        float[] slopeY = new float[PIXELS];
        IntStream.range(0, SIZE).parallel().forEach(y -> {
            for (int x = 0; x < SIZE; x++) {
                int i = y * SIZE + x;
                slopeX[i] = relief[y * SIZE + ((x + 1) & WRAP)] - relief[y * SIZE + ((x - 1) & WRAP)];
                slopeY[i] = relief[((y + 1) & WRAP) * SIZE + x] - relief[((y - 1) & WRAP) * SIZE + x];
            }
        });
        float spread = (float) StrictMath.sqrt((sumOfSquares(slopeX) + sumOfSquares(slopeY)) / 2097152.0f);
        float slopeScale = spread > 0.0f ? material.normalStrength / spread : 0.0f;

        byte[] tga = new byte[TGA_HEADER + PIXELS * 4];
        tga[2] = 2;          // immagine a colori non compressa
        tga[13] = 4;         // larghezza 1024
        tga[15] = 4;         // altezza 1024
        tga[16] = 32;        // bit per pixel
        tga[17] = 8;         // otto bit di trasparenza
        IntStream.range(0, SIZE).parallel().forEach(y -> {
            // il .tga parte dalla riga piu' in basso
            int at = TGA_HEADER + (WRAP - y) * SIZE * 4;
            for (int x = 0; x < SIZE; x++) {
                int i = y * SIZE + x;
                float nx = -Math.max(-2.5f, Math.min(2.5f, slopeX[i] * slopeScale));
                float ny = -Math.max(-2.5f, Math.min(2.5f, slopeY[i] * slopeScale));
                float length = (float) StrictMath.sqrt(nx * nx + ny * ny + 1.0f);
                tga[at++] = unit(1.0f / length * 0.5f + 0.5f);
                tga[at++] = unit(ny / length * 0.5f + 0.5f);
                tga[at++] = unit(nx / length * 0.5f + 0.5f);
                tga[at++] = unit(0.5f + Math.max(-0.22f, Math.min(0.22f, detail[i])));
            }
        });
        return tga;
    }

    /** Un'immagine a 32 bit come file .tga non compresso. */
    static byte[] encodeTga(int[] argb, int width, int height) {
        byte[] tga = new byte[TGA_HEADER + width * height * 4];
        tga[2] = 2;
        tga[12] = (byte) width;
        tga[13] = (byte) (width >> 8);
        tga[14] = (byte) height;
        tga[15] = (byte) (height >> 8);
        tga[16] = 32;
        tga[17] = 8;
        IntStream.range(0, height).parallel().forEach(y -> {
            int at = TGA_HEADER + (height - 1 - y) * width * 4;
            for (int x = 0; x < width; x++) {
                int pixel = argb[y * width + x];
                tga[at++] = (byte) pixel;
                tga[at++] = (byte) (pixel >> 8);
                tga[at++] = (byte) (pixel >> 16);
                tga[at++] = (byte) (pixel >>> 24);
            }
        });
        return tga;
    }

    /** Rumore a piu' scale i cui bordi combaciano: lo stesso valore a 0 e a 1. */
    static float tileableNoise(float u, float v, int period, int octaves, float gain, int seed) {
        return layered(u, v, period, octaves, gain, seed);
    }

    // ------------------------------------------------------------------ rilievo dei materiali

    private static float[] buildRelief(Material material, int seed) {
        float[] relief = new float[PIXELS];
        IntStream.range(0, SIZE).parallel().forEach(y -> {
            for (int x = 0; x < SIZE; x++) {
                float u = x / 1024.0f;
                float v = y / 1024.0f;
                relief[y * SIZE + x] = switch (material) {
                    case ROCK -> rock(u, v, seed);
                    case GRAVEL -> layered(u, v, 6, 6, 0.6f, seed + 23) * 0.45f;
                    case GRASS -> grass(u, v, seed);
                    case SNOW -> layered(u, v, 3, 5, 0.45f, seed) + layered(u, v, 96, 2, 0.5f, seed + 5) * 0.05f;
                    case DUST -> rippled(u, v, seed, 17, 0.16f);
                    case CRACKED -> cracked(u, v, seed);
                    case SEABED -> rippled(u, v, seed, 8, 0.3f);
                };
            }
        });
        switch (material) {
            case GRAVEL -> scatterPebbles(relief, seed, 2600, 3, 22, 1.0f);
            case GRASS -> scatterBlades(relief, seed);
            case DUST, CRACKED, SEABED -> scatterPebbles(relief, seed, 350, 2, 7, 0.35f);
            default -> { }
        }
        return relief;
    }

    /** Roccia: creste a piu' scale, strati inclinati e fratture tra blocchi. */
    private static float rock(float u, float v, int seed) {
        float shiftU = layered(u, v, 3, 4, 0.5f, seed + 11) - 0.5f;
        float shiftV = layered(u, v, 3, 4, 0.5f, seed + 13) - 0.5f;
        float wu = u + shiftU * 0.15f;
        float wv = v + shiftV * 0.15f;
        float ridges = 0.0f;
        float strength = 1.0f;
        float total = 0.0f;
        float carry = 1.0f;
        int period = 3;
        for (int octave = 0; octave < 8; octave++) {
            float n = smoothNoise(wu, wv, period, seed + octave * 131);
            float ridge = 1.0f - Math.abs(2.0f * n - 1.0f);
            float sharp = ridge * ridge * carry;
            carry = Math.max(0.0f, Math.min(1.0f, sharp * 2.0f));
            ridges += sharp * strength;
            total += strength;
            strength *= 0.58f;
            period *= 2;
        }
        float ridged = ridges / total;
        float strata = (float) StrictMath.sin(FULL_TURN * (double) (9.0f * wv + 2.0f * wu) + (double) (shiftU * 14.0f)) * 0.5f + 0.5f;
        float[] cells = cellular(u + shiftU * 0.3f, v + shiftV * 0.3f, 5, seed + 3);
        float fracture = ease(0.0f, 0.035f, cells[1] - cells[0]);
        float fractured = ease(0.45f, 0.6f, layered(u, v, 4, 2, 0.5f, seed + 19));
        return (ridged * 0.8f + strata * 0.12f + cells[2] * 0.08f) * (1.0f - (1.0f - fracture) * fractured * 0.3f);
    }

    /** Erba: ciuffi grandi e piccoli sopra un fondo morbido. */
    private static float grass(float u, float v, int seed) {
        float shiftU = layered(u, v, 4, 3, 0.5f, seed + 45) - 0.5f;
        float shiftV = layered(u, v, 4, 3, 0.5f, seed + 47) - 0.5f;
        float wu = u + shiftU * 0.12f;
        float wv = v + shiftV * 0.12f;
        float large = ease(0.4f, 0.62f, layered(wu, wv, 7, 4, 0.55f, seed + 49));
        float small = ease(0.42f, 0.64f, layered(wu, wv, 19, 3, 0.6f, seed + 50));
        float tufts = large * 0.55f + small * 0.45f;
        return tufts * 0.7f + layered(u, v, 5, 4, 0.55f, seed + 43) * 0.5f + layered(u, v, 96, 3, 0.6f, seed + 41) * 0.2f;
    }

    /** Polvere e fondale: increspature che compaiono a chiazze sopra un fondo granuloso. */
    private static float rippled(float u, float v, int seed, int ripples, float amount) {
        float bend = layered(u, v, 3, 4, 0.5f, seed + 51);
        float patches = ease(0.35f, 0.65f, layered(u, v, 4, 3, 0.5f, seed + 57));
        float ripple = (float) StrictMath.sin(FULL_TURN * (double) (ripples * u + 3.0f * v) + (double) (bend * 11.0f)) * 0.5f + 0.5f;
        return ripple * amount * patches + layered(u, v, 6, 7, 0.6f, seed + 53) * (1.0f - amount * 0.5f);
    }

    /** Terra spaccata: zolle separate da crepe, con i bordi leggermente rialzati. */
    private static float cracked(float u, float v, int seed) {
        float shiftU = layered(u, v, 5, 4, 0.55f, seed + 61) - 0.5f;
        float shiftV = layered(u, v, 5, 4, 0.55f, seed + 63) - 0.5f;
        float[] cells = cellular(u + shiftU * 0.12f, v + shiftV * 0.12f, 8, seed + 67);
        float edge = cells[1] - cells[0];
        float crack = ease(0.0f, 0.045f, edge);
        float curl = ease(0.0f, 0.45f, edge);
        return crack * (0.55f + cells[2] * 0.2f - curl * 0.12f) + layered(u, v, 12, 6, 0.6f, seed + 71) * 0.45f;
    }

    /** Aggiunge sassi: cupole ovali di grandezza e orientamento casuali. */
    private static void scatterPebbles(float[] relief, int seed, int count, int minRadius, int maxRadius, float amount) {
        SplittableRandom random = new SplittableRandom((long) seed * 31 + 7);
        for (int i = 0; i < count; i++) {
            float centreX = random.nextFloat() * 1024.0f;
            float centreY = random.nextFloat() * 1024.0f;
            float size = random.nextFloat();
            float radius = minRadius + (maxRadius - minRadius) * size * size * size;
            float stretch = 0.7f + random.nextFloat() * 0.6f;
            float angle = random.nextFloat() * 3.1415927f;
            float cos = (float) StrictMath.cos(angle);
            float sin = (float) StrictMath.sin(angle);
            float top = (0.35f + 0.65f * random.nextFloat()) * amount * (0.5f + 0.5f * radius / maxRadius);
            int reach = (int) Math.ceil(radius * 1.45f);
            int baseX = (int) centreX;
            int baseY = (int) centreY;
            for (int dy = -reach; dy <= reach; dy++) {
                for (int dx = -reach; dx <= reach; dx++) {
                    float px = baseX + dx + 0.5f - centreX;
                    float py = baseY + dy + 0.5f - centreY;
                    float rx = (px * cos + py * sin) / (radius * stretch);
                    float ry = (-px * sin + py * cos) / (radius / stretch);
                    float distance = rx * rx + ry * ry;
                    if (distance < 1.0f) {
                        int at = ((baseY + dy) & WRAP) * SIZE + ((baseX + dx) & WRAP);
                        float dome = (float) StrictMath.sqrt(1.0f - distance) * top + 0.15f * amount;
                        relief[at] = Math.max(relief[at], dome);
                    }
                }
            }
        }
    }

    /** Aggiunge fili d'erba: brevi tratti curvi, piu' chiari verso la punta. */
    private static void scatterBlades(float[] relief, int seed) {
        SplittableRandom random = new SplittableRandom((long) seed * 131 + 3);
        for (int i = 0; i < 90000; i++) {
            float x = random.nextFloat() * 1024.0f;
            float y = random.nextFloat() * 1024.0f;
            float angle = (float) (random.nextGaussian() * 0.7d + 1.1d);
            float bend = (float) random.nextGaussian() * 0.06f;
            int length = 7 + random.nextInt(14);
            float top = 0.35f + random.nextFloat() * 0.65f;
            for (int step = 0; step < length; step++) {
                float value = top * (0.35f + 0.65f * step / length);
                int ix = (int) Math.floor(x);
                int iy = (int) Math.floor(y);
                float fx = x - ix;
                float fy = y - iy;
                brighten(relief, ix, iy, value * (1.0f - fx) * (1.0f - fy));
                brighten(relief, ix + 1, iy, value * fx * (1.0f - fy));
                brighten(relief, ix, iy + 1, value * (1.0f - fx) * fy);
                brighten(relief, ix + 1, iy + 1, value * fx * fy);
                angle += bend;
                x += (float) StrictMath.cos(angle);
                y += (float) StrictMath.sin(angle);
            }
        }
    }

    private static void brighten(float[] relief, int x, int y, float value) {
        int at = (y & WRAP) * SIZE + (x & WRAP);
        relief[at] = Math.max(relief[at], relief[at] * 0.5f + value);
    }

    // ------------------------------------------------------------------ rumore

    private static float layered(float u, float v, int period, int octaves, float gain, int seed) {
        float sum = 0.0f;
        float strength = 1.0f;
        float total = 0.0f;
        for (int octave = 0; octave < octaves; octave++) {
            sum += smoothNoise(u, v, period, seed + octave * 101) * strength;
            total += strength;
            strength *= gain;
            period *= 2;
        }
        return sum / total;
    }

    /** Rumore a gradienti su una griglia che si ripete ogni {@code period} celle; risultato tra 0 e 1. */
    private static float smoothNoise(float u, float v, int period, int seed) {
        float x = u * period;
        float y = v * period;
        int cellX = (int) Math.floor(x);
        int cellY = (int) Math.floor(y);
        float fx = x - cellX;
        float fy = y - cellY;
        float blendX = fx * fx * fx * (fx * (fx * 6.0f - 15.0f) + 10.0f);
        float blendY = fy * fy * fy * (fy * (fy * 6.0f - 15.0f) + 10.0f);
        int left = Math.floorMod(cellX, period);
        int right = Math.floorMod(cellX + 1, period);
        int upper = Math.floorMod(cellY, period);
        int lower = Math.floorMod(cellY + 1, period);
        float top = corner(left, upper, seed, fx, fy) * (1.0f - blendX) + corner(right, upper, seed, fx - 1.0f, fy) * blendX;
        float bottom = corner(left, lower, seed, fx, fy - 1.0f) * (1.0f - blendX) + corner(right, lower, seed, fx - 1.0f, fy - 1.0f) * blendX;
        return Math.max(0.0f, Math.min(1.0f, (top * (1.0f - blendY) + bottom * blendY) * 0.7071f + 0.5f));
    }

    private static float corner(int x, int y, int seed, float dx, float dy) {
        float angle = scramble(x, y, seed) * 6.2831855f;
        return (float) StrictMath.cos(angle) * dx + (float) StrictMath.sin(angle) * dy;
    }

    /** Rumore a celle: distanza dal punto piu' vicino, dal secondo e un valore casuale della cella piu' vicina. */
    private static float[] cellular(float u, float v, int cells, int seed) {
        float x = (u - (float) Math.floor(u)) * cells;
        float y = (v - (float) Math.floor(v)) * cells;
        int cellX = (int) x;
        int cellY = (int) y;
        float nearest = Float.MAX_VALUE;
        float second = Float.MAX_VALUE;
        float value = 0.0f;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int wrappedX = Math.floorMod(cellX + dx, cells);
                int wrappedY = Math.floorMod(cellY + dy, cells);
                float pointX = cellX + dx + scramble(wrappedX, wrappedY, seed);
                float pointY = cellY + dy + scramble(wrappedX, wrappedY, seed + 1);
                float distance = (float) StrictMath.sqrt((pointX - x) * (pointX - x) + (pointY - y) * (pointY - y));
                if (distance < nearest) {
                    second = nearest;
                    nearest = distance;
                    value = scramble(wrappedX, wrappedY, seed + 2);
                } else if (distance < second) {
                    second = distance;
                }
            }
        }
        return new float[]{nearest, second, value};
    }

    /** Un numero tra 0 e 1 che dipende solo dalle tre entrate. */
    private static float scramble(int x, int y, int seed) {
        int h = x * -1640531535 + y * -2048144777 + seed * -1028477379;
        h = (h ^ (h >>> 15)) * 739982445;
        h = (h ^ (h >>> 12)) * 695872825;
        return ((h ^ (h >>> 15)) & 0xFFFFFF) / 1.6777216E7f;
    }

    private static float ease(float from, float to, float value) {
        float t = Math.max(0.0f, Math.min(1.0f, (value - from) / (to - from)));
        return t * t * (3.0f - 2.0f * t);
    }

    // ------------------------------------------------------------------ filtri

    /** Toglie le forme grandi sottraendo la media di un quadrato di lato 2*radius+1 (con i bordi che combaciano). */
    private static float[] removeLargeShapes(float[] values, int radius) {
        float[] rows = new float[values.length];
        float[] result = new float[values.length];
        int window = radius * 2 + 1;
        IntStream.range(0, SIZE).parallel().forEach(y -> {
            float sum = 0.0f;
            for (int x = -radius; x <= radius; x++) {
                sum += values[y * SIZE + (x & WRAP)];
            }
            for (int x = 0; x < SIZE; x++) {
                rows[y * SIZE + x] = sum / window;
                sum += values[y * SIZE + ((x + radius + 1) & WRAP)] - values[y * SIZE + ((x - radius) & WRAP)];
            }
        });
        IntStream.range(0, SIZE).parallel().forEach(x -> {
            float sum = 0.0f;
            for (int y = -radius; y <= radius; y++) {
                sum += rows[(y & WRAP) * SIZE + x];
            }
            for (int y = 0; y < SIZE; y++) {
                result[y * SIZE + x] = values[y * SIZE + x] - sum / window;
                sum += rows[((y + radius + 1) & WRAP) * SIZE + x] - rows[((y - radius) & WRAP) * SIZE + x];
            }
        });
        return result;
    }

    /** Porta i valori a media zero e alla dispersione voluta. */
    private static void rescale(float[] values, float deviation) {
        double sum = 0.0d;
        for (float value : values) {
            sum += value;
        }
        double mean = sum / values.length;
        double variance = 0.0d;
        for (float value : values) {
            variance += (value - mean) * (value - mean);
        }
        float current = (float) StrictMath.sqrt(variance / values.length);
        float scale = current > 0.0f ? deviation / current : 0.0f;
        for (int i = 0; i < values.length; i++) {
            values[i] = (float) ((values[i] - mean) * (double) scale);
        }
    }

    private static float sumOfSquares(float[] values) {
        double sum = 0.0d;
        for (float value : values) {
            sum += value * value;
        }
        return (float) sum;
    }

    private static byte unit(float value) {
        return (byte) Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }
}
