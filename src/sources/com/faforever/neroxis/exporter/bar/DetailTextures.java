package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/DetailTextures.class */
final class DetailTextures {
    static final int SIZE = 1024;
    private static final int MASK = 1023;

    private DetailTextures() {
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/DetailTextures$Material.class */
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

    static byte[] createTexture(Material material, int seed) {
        float[] height = createHeight(material, seed);
        float[] detail = highPass(height, 20);
        scaleToDeviation(detail, material.detailStrength);
        float[] gradientX = new float[1048576];
        float[] gradientY = new float[1048576];
        IntStream.range(0, 1024).parallel().forEach(y -> {
            for (int x = 0; x < 1024; x++) {
                int i = (y * 1024) + x;
                gradientX[i] = height[(y * 1024) + ((x + 1) & MASK)] - height[(y * 1024) + ((x - 1) & MASK)];
                gradientY[i] = height[(((y + 1) & MASK) * 1024) + x] - height[(((y - 1) & MASK) * 1024) + x];
            }
        });
        float deviation = (float) StrictMath.sqrt((squareSum(gradientX) + squareSum(gradientY)) / 2097152.0f);
        float gradientScale = deviation > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? material.normalStrength / deviation : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
        byte[] tga = new byte[4194322];
        tga[2] = 2;
        tga[12] = 0;
        tga[13] = 4;
        tga[14] = 0;
        tga[15] = 4;
        tga[16] = 32;
        tga[17] = 8;
        IntStream.range(0, 1024).parallel().forEach(y2 -> {
            int offset = 18 + ((MASK - y2) * 1024 * 4);
            for (int x = 0; x < 1024; x++) {
                int i = (y2 * 1024) + x;
                float nx = -Math.max(-2.5f, Math.min(2.5f, gradientX[i] * gradientScale));
                float ny = -Math.max(-2.5f, Math.min(2.5f, gradientY[i] * gradientScale));
                float length = (float) StrictMath.sqrt((nx * nx) + (ny * ny) + 1.0f);
                int i2 = offset;
                int offset2 = offset + 1;
                tga[i2] = toByte(((1.0f / length) * 0.5f) + 0.5f);
                int offset3 = offset2 + 1;
                tga[offset2] = toByte(((ny / length) * 0.5f) + 0.5f);
                int offset4 = offset3 + 1;
                tga[offset3] = toByte(((nx / length) * 0.5f) + 0.5f);
                offset = offset4 + 1;
                tga[offset4] = toByte(0.5f + Math.max(-0.22f, Math.min(0.22f, detail[i])));
            }
        });
        return tga;
    }

    private static float[] createHeight(Material material, int seed) {
        float[] height = new float[1048576];
        IntStream.range(0, 1024).parallel().forEach(y -> {
            float fDust;
            for (int x = 0; x < 1024; x++) {
                float u = x / 1024.0f;
                float v = y / 1024.0f;
                int i = (y * 1024) + x;
                switch (material) {
                    case ROCK:
                        fDust = rock(u, v, seed);
                        break;
                    case GRAVEL:
                        fDust = fbm(u, v, 6, 6, 0.6f, seed + 23) * 0.45f;
                        break;
                    case GRASS:
                        fDust = grassBase(u, v, seed);
                        break;
                    case SNOW:
                        fDust = fbm(u, v, 3, 5, 0.45f, seed) + (fbm(u, v, 96, 2, 0.5f, seed + 5) * 0.05f);
                        break;
                    case DUST:
                        fDust = dust(u, v, seed, 17, 0.16f);
                        break;
                    case CRACKED:
                        fDust = cracked(u, v, seed);
                        break;
                    case SEABED:
                        fDust = dust(u, v, seed, 8, 0.3f);
                        break;
                    default:
                        throw new MatchException((String) null, (Throwable) null);
                }
                height[i] = fDust;
            }
        });
        switch (material.ordinal()) {
            case 1:
                stampPebbles(height, seed, 2600, 3, 22, 1.0f);
                break;
            case 2:
                stampBlades(height, seed);
                break;
            case 4:
            case 5:
            case 6:
                stampPebbles(height, seed, 350, 2, 7, 0.35f);
                break;
        }
        return height;
    }

    private static float rock(float u, float v, int seed) {
        float warpU = fbm(u, v, 3, 4, 0.5f, seed + 11) - 0.5f;
        float warpV = fbm(u, v, 3, 4, 0.5f, seed + 13) - 0.5f;
        float wu = u + (warpU * 0.15f);
        float wv = v + (warpV * 0.15f);
        float ridged = 0.0f;
        float amplitude = 1.0f;
        float total = 0.0f;
        float weight = 1.0f;
        int period = 3;
        for (int octave = 0; octave < 8; octave++) {
            float n = noise(wu, wv, period, seed + (octave * 131));
            float ridge = 1.0f - Math.abs((2.0f * n) - 1.0f);
            float ridge2 = ridge * ridge * weight;
            weight = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, ridge2 * 2.0f));
            ridged += ridge2 * amplitude;
            total += amplitude;
            amplitude *= 0.58f;
            period *= 2;
        }
        float ridged2 = ridged / total;
        float strata = (((float) StrictMath.sin((6.283185307179586d * ((double) ((9.0f * wv) + (2.0f * wu)))) + ((double) (warpU * 14.0f)))) * 0.5f) + 0.5f;
        float[] cells = worley(u + (warpU * 0.3f), v + (warpV * 0.3f), 5, seed + 3);
        float fracture = smoothStep(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.035f, cells[1] - cells[0]);
        float fracturePresent = smoothStep(0.45f, 0.6f, fbm(u, v, 4, 2, 0.5f, seed + 19));
        return ((ridged2 * 0.8f) + (strata * 0.12f) + (cells[2] * 0.08f)) * (1.0f - (((1.0f - fracture) * fracturePresent) * 0.3f));
    }

    private static float grassBase(float u, float v, int seed) {
        float warpU = fbm(u, v, 4, 3, 0.5f, seed + 45) - 0.5f;
        float warpV = fbm(u, v, 4, 3, 0.5f, seed + 47) - 0.5f;
        float wu = u + (warpU * 0.12f);
        float wv = v + (warpV * 0.12f);
        float large = smoothStep(0.4f, 0.62f, fbm(wu, wv, 7, 4, 0.55f, seed + 49));
        float small = smoothStep(0.42f, 0.64f, fbm(wu, wv, 19, 3, 0.6f, seed + 50));
        float tufts = (large * 0.55f) + (small * 0.45f);
        return (tufts * 0.7f) + (fbm(u, v, 5, 4, 0.55f, seed + 43) * 0.5f) + (fbm(u, v, 96, 3, 0.6f, seed + 41) * 0.2f);
    }

    private static float dust(float u, float v, int seed, int ripples, float rippleAmount) {
        float warp = fbm(u, v, 3, 4, 0.5f, seed + 51);
        float fade = smoothStep(0.35f, 0.65f, fbm(u, v, 4, 3, 0.5f, seed + 57));
        float ripple = (((float) StrictMath.sin((6.283185307179586d * ((double) ((ripples * u) + (3.0f * v)))) + ((double) (warp * 11.0f)))) * 0.5f) + 0.5f;
        return (ripple * rippleAmount * fade) + (fbm(u, v, 6, 7, 0.6f, seed + 53) * (1.0f - (rippleAmount * 0.5f)));
    }

    private static float cracked(float u, float v, int seed) {
        float warpU = fbm(u, v, 5, 4, 0.55f, seed + 61) - 0.5f;
        float warpV = fbm(u, v, 5, 4, 0.55f, seed + 63) - 0.5f;
        float[] cells = worley(u + (warpU * 0.12f), v + (warpV * 0.12f), 8, seed + 67);
        float edge = cells[1] - cells[0];
        float crack = smoothStep(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.045f, edge);
        float curl = smoothStep(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.45f, edge);
        return (crack * ((0.55f + (cells[2] * 0.2f)) - (curl * 0.12f))) + (fbm(u, v, 12, 6, 0.6f, seed + 71) * 0.45f);
    }

    private static void stampPebbles(float[] height, int seed, int count, int minRadius, int maxRadius, float amount) {
        SplittableRandom random = new SplittableRandom((((long) seed) * 31) + 7);
        for (int i = 0; i < count; i++) {
            float centerX = random.nextFloat() * 1024.0f;
            float centerY = random.nextFloat() * 1024.0f;
            float size = random.nextFloat();
            float radius = minRadius + ((maxRadius - minRadius) * size * size * size);
            float stretch = 0.7f + (random.nextFloat() * 0.6f);
            float angle = random.nextFloat() * 3.1415927f;
            float cos = (float) StrictMath.cos(angle);
            float sin = (float) StrictMath.sin(angle);
            float top = (0.35f + (0.65f * random.nextFloat())) * amount * (0.5f + ((0.5f * radius) / maxRadius));
            int reach = (int) Math.ceil(radius * 1.45f);
            int baseX = (int) centerX;
            int baseY = (int) centerY;
            for (int dy = -reach; dy <= reach; dy++) {
                for (int dx = -reach; dx <= reach; dx++) {
                    float px = ((baseX + dx) + 0.5f) - centerX;
                    float py = ((baseY + dy) + 0.5f) - centerY;
                    float rx = ((px * cos) + (py * sin)) / (radius * stretch);
                    float ry = (((-px) * sin) + (py * cos)) / (radius / stretch);
                    float d = (rx * rx) + (ry * ry);
                    if (d < 1.0f) {
                        int index = (((baseY + dy) & MASK) * 1024) + ((baseX + dx) & MASK);
                        float dome = (((float) StrictMath.sqrt(1.0f - d)) * top) + (0.15f * amount);
                        height[index] = Math.max(height[index], dome);
                    }
                }
            }
        }
    }

    private static void stampBlades(float[] height, int seed) {
        SplittableRandom random = new SplittableRandom((((long) seed) * 131) + 3);
        for (int i = 0; i < 90000; i++) {
            float x = random.nextFloat() * 1024.0f;
            float y = random.nextFloat() * 1024.0f;
            float angle = (float) ((random.nextGaussian() * 0.7d) + 1.1d);
            float bend = ((float) random.nextGaussian()) * 0.06f;
            int length = 7 + random.nextInt(14);
            float top = 0.35f + (random.nextFloat() * 0.65f);
            for (int step = 0; step < length; step++) {
                float value = top * (0.35f + ((0.65f * step) / length));
                int ix = (int) Math.floor(x);
                int iy = (int) Math.floor(y);
                float fx = x - ix;
                float fy = y - iy;
                splat(height, ix, iy, value * (1.0f - fx) * (1.0f - fy));
                splat(height, ix + 1, iy, value * fx * (1.0f - fy));
                splat(height, ix, iy + 1, value * (1.0f - fx) * fy);
                splat(height, ix + 1, iy + 1, value * fx * fy);
                angle += bend;
                x += (float) StrictMath.cos(angle);
                y += (float) StrictMath.sin(angle);
            }
        }
    }

    private static void splat(float[] height, int x, int y, float value) {
        int index = ((y & MASK) * 1024) + (x & MASK);
        height[index] = Math.max(height[index], (height[index] * 0.5f) + value);
    }

    static byte[] encodeTga(int[] argb, int width, int height) {
        byte[] tga = new byte[18 + (width * height * 4)];
        tga[2] = 2;
        tga[12] = (byte) width;
        tga[13] = (byte) (width >> 8);
        tga[14] = (byte) height;
        tga[15] = (byte) (height >> 8);
        tga[16] = 32;
        tga[17] = 8;
        IntStream.range(0, height).parallel().forEach(y -> {
            int offset = 18 + (((height - 1) - y) * width * 4);
            for (int x = 0; x < width; x++) {
                int pixel = argb[(y * width) + x];
                int i = offset;
                int offset2 = offset + 1;
                tga[i] = (byte) pixel;
                int offset3 = offset2 + 1;
                tga[offset2] = (byte) (pixel >> 8);
                int offset4 = offset3 + 1;
                tga[offset3] = (byte) (pixel >> 16);
                offset = offset4 + 1;
                tga[offset4] = (byte) (pixel >>> 24);
            }
        });
        return tga;
    }

    static float tileableNoise(float u, float v, int period, int octaves, float gain, int seed) {
        return fbm(u, v, period, octaves, gain, seed);
    }

    private static float fbm(float u, float v, int period, int octaves, float gain, int seed) {
        float result = 0.0f;
        float amplitude = 1.0f;
        float total = 0.0f;
        for (int octave = 0; octave < octaves; octave++) {
            result += noise(u, v, period, seed + (octave * 101)) * amplitude;
            total += amplitude;
            amplitude *= gain;
            period *= 2;
        }
        return result / total;
    }

    private static float noise(float u, float v, int period, int seed) {
        float x = u * period;
        float y = v * period;
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        float fx = x - x0;
        float fy = y - y0;
        float tx = fx * fx * fx * ((fx * ((fx * 6.0f) - 15.0f)) + 10.0f);
        float ty = fy * fy * fy * ((fy * ((fy * 6.0f) - 15.0f)) + 10.0f);
        int xa = Math.floorMod(x0, period);
        int xb = Math.floorMod(x0 + 1, period);
        int ya = Math.floorMod(y0, period);
        int yb = Math.floorMod(y0 + 1, period);
        float top = (gradient(xa, ya, seed, fx, fy) * (1.0f - tx)) + (gradient(xb, ya, seed, fx - 1.0f, fy) * tx);
        float bottom = (gradient(xa, yb, seed, fx, fy - 1.0f) * (1.0f - tx)) + (gradient(xb, yb, seed, fx - 1.0f, fy - 1.0f) * tx);
        return Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (((top * (1.0f - ty)) + (bottom * ty)) * 0.7071f) + 0.5f));
    }

    private static float gradient(int x, int y, int seed, float dx, float dy) {
        float angle = hash(x, y, seed) * 6.2831855f;
        return (((float) StrictMath.cos(angle)) * dx) + (((float) StrictMath.sin(angle)) * dy);
    }

    private static float[] worley(float u, float v, int cells, int seed) {
        float x = (u - ((float) Math.floor(u))) * cells;
        float y = (v - ((float) Math.floor(v))) * cells;
        int cellX = (int) x;
        int cellY = (int) y;
        float nearest = Float.MAX_VALUE;
        float second = Float.MAX_VALUE;
        float value = 0.0f;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int wrappedX = Math.floorMod(cellX + dx, cells);
                int wrappedY = Math.floorMod(cellY + dy, cells);
                float pointX = cellX + dx + hash(wrappedX, wrappedY, seed);
                float pointY = cellY + dy + hash(wrappedX, wrappedY, seed + 1);
                float distance = (float) StrictMath.sqrt(((pointX - x) * (pointX - x)) + ((pointY - y) * (pointY - y)));
                if (distance < nearest) {
                    second = nearest;
                    nearest = distance;
                    value = hash(wrappedX, wrappedY, seed + 2);
                } else if (distance < second) {
                    second = distance;
                }
            }
        }
        return new float[]{nearest, second, value};
    }

    private static float hash(int x, int y, int seed) {
        int h = (x * (-1640531535)) + (y * (-2048144777)) + (seed * (-1028477379));
        int h2 = (h ^ (h >>> 15)) * 739982445;
        int h3 = (h2 ^ (h2 >>> 12)) * 695872825;
        return ((h3 ^ (h3 >>> 15)) & 16777215) / 1.6777216E7f;
    }

    private static float smoothStep(float edge0, float edge1, float value) {
        float t = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, (value - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - (2.0f * t));
    }

    private static float[] highPass(float[] values, int radius) {
        float[] horizontal = new float[values.length];
        float[] result = new float[values.length];
        int window = (radius * 2) + 1;
        IntStream.range(0, 1024).parallel().forEach(y -> {
            float sum = 0.0f;
            for (int x = -radius; x <= radius; x++) {
                sum += values[(y * 1024) + (x & MASK)];
            }
            for (int x2 = 0; x2 < 1024; x2++) {
                horizontal[(y * 1024) + x2] = sum / window;
                sum += values[(y * 1024) + (((x2 + radius) + 1) & MASK)] - values[(y * 1024) + ((x2 - radius) & MASK)];
            }
        });
        IntStream.range(0, 1024).parallel().forEach(x -> {
            float sum = 0.0f;
            for (int y2 = -radius; y2 <= radius; y2++) {
                sum += horizontal[((y2 & MASK) * 1024) + x];
            }
            for (int y3 = 0; y3 < 1024; y3++) {
                result[(y3 * 1024) + x] = values[(y3 * 1024) + x] - (sum / window);
                sum += horizontal[((((y3 + radius) + 1) & MASK) * 1024) + x] - horizontal[(((y3 - radius) & MASK) * 1024) + x];
            }
        });
        return result;
    }

    private static void scaleToDeviation(float[] values, float deviation) {
        double mean = 0.0d;
        for (float f : values) {
            mean += (double) f;
        }
        double mean2 = mean / ((double) values.length);
        double variance = 0.0d;
        for (float value : values) {
            variance += (((double) value) - mean2) * (((double) value) - mean2);
        }
        float current = (float) StrictMath.sqrt(variance / ((double) values.length));
        float scale = current > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? deviation / current : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
        for (int i = 0; i < values.length; i++) {
            values[i] = (float) ((((double) values[i]) - mean2) * ((double) scale));
        }
    }

    private static float squareSum(float[] values) {
        double sum = 0.0d;
        for (float value : values) {
            sum += (double) (value * value);
        }
        return (float) sum;
    }

    private static byte toByte(float value) {
        return (byte) Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }
}
