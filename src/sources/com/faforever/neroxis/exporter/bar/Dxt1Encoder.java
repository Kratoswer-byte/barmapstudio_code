package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.generator.util.serial.GeneratedMapNameEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/Dxt1Encoder.class */
final class Dxt1Encoder {
    private Dxt1Encoder() {
    }

    static int compress(int[] rgb, int width, int height, byte[] out, int outOffset) {
        int[] block = new int[16];
        int offset = outOffset;
        for (int by = 0; by < height; by += 4) {
            for (int bx = 0; bx < width; bx += 4) {
                for (int y = 0; y < 4; y++) {
                    int row = ((by + y) * width) + bx;
                    System.arraycopy(rgb, row, block, y * 4, 4);
                }
                compressBlock(block, out, offset);
                offset += 8;
            }
        }
        return offset - outOffset;
    }

    static void compressBlock(int[] block, byte[] out, int offset) {
        int i;
        int minR = 255;
        int minG = 255;
        int minB = 255;
        int maxR = 0;
        int maxG = 0;
        int maxB = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            int c = block[i2];
            int r = (c >> 16) & 255;
            int g = (c >> 8) & 255;
            int b = c & 255;
            minR = Math.min(minR, r);
            minG = Math.min(minG, g);
            minB = Math.min(minB, b);
            maxR = Math.max(maxR, r);
            maxG = Math.max(maxG, g);
            maxB = Math.max(maxB, b);
        }
        int insetR = (maxR - minR) >> 4;
        int insetG = (maxG - minG) >> 4;
        int insetB = (maxB - minB) >> 4;
        int c0 = to565(maxR - insetR, maxG - insetG, maxB - insetB);
        int c1 = to565(minR + insetR, minG + insetG, minB + insetB);
        if (c0 < c1) {
            int tmp = c0;
            c0 = c1;
            c1 = tmp;
        }
        long indices = 0;
        if (c0 != c1) {
            int r0 = expand5(c0 >> 11);
            int g0 = expand6((c0 >> 5) & 63);
            int b0 = expand5(c0 & 31);
            int r1 = expand5(c1 >> 11);
            int g1 = expand6((c1 >> 5) & 63);
            int b1 = expand5(c1 & 31);
            int dr = r0 - r1;
            int dg = g0 - g1;
            int db = b0 - b1;
            int lengthSquared = (dr * dr) + (dg * dg) + (db * db);
            for (int i3 = 15; i3 >= 0; i3--) {
                int c2 = block[i3];
                int dot = ((((c2 >> 16) & 255) - r1) * dr) + ((((c2 >> 8) & 255) - g1) * dg) + (((c2 & 255) - b1) * db);
                if (dot * 6 < lengthSquared) {
                    i = 1;
                } else if (dot * 2 < lengthSquared) {
                    i = 3;
                } else if (dot * 6 < lengthSquared * 5) {
                    i = 2;
                } else {
                    i = 0;
                }
                int index = i;
                indices = (indices << 2) | ((long) index);
            }
        }
        out[offset] = (byte) c0;
        out[offset + 1] = (byte) (c0 >> 8);
        out[offset + 2] = (byte) c1;
        out[offset + 3] = (byte) (c1 >> 8);
        out[offset + 4] = (byte) indices;
        out[offset + 5] = (byte) (indices >> 8);
        out[offset + 6] = (byte) (indices >> 16);
        out[offset + 7] = (byte) (indices >> 24);
    }

    static byte[] createDxt5Dds(int[] argb, int width, int height) {
        int dataSize = (width / 4) * (height / 4) * 16;
        ByteBuffer buffer = ByteBuffer.allocate(128 + dataSize).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(new byte[]{68, 68, 83, 32});
        buffer.putInt(124);
        buffer.putInt(528391);
        buffer.putInt(height);
        buffer.putInt(width);
        buffer.putInt(dataSize);
        buffer.position(76);
        buffer.putInt(32);
        buffer.putInt(4);
        buffer.put(new byte[]{68, 88, 84, 53});
        buffer.position(108);
        buffer.putInt(4096);
        byte[] out = buffer.array();
        int[] block = new int[16];
        int offset = 128;
        for (int by = 0; by < height; by += 4) {
            for (int bx = 0; bx < width; bx += 4) {
                for (int y = 0; y < 4; y++) {
                    System.arraycopy(argb, ((by + y) * width) + bx, block, y * 4, 4);
                }
                compressAlphaBlock(block, out, offset);
                compressBlock(block, out, offset + 8);
                offset += 16;
            }
        }
        return out;
    }

    private static void compressAlphaBlock(int[] block, byte[] out, int offset) {
        int min = 255;
        int max = 0;
        for (int i = 0; i < 16; i++) {
            int alpha = block[i] >>> 24;
            min = Math.min(min, alpha);
            max = Math.max(max, alpha);
        }
        out[offset] = (byte) max;
        out[offset + 1] = (byte) min;
        long indices = 0;
        if (max > min) {
            for (int i2 = 15; i2 >= 0; i2--) {
                int step = (((max - (block[i2] >>> 24)) * 7) + ((max - min) / 2)) / (max - min);
                int index = step == 0 ? 0 : step == 7 ? 1 : step + 1;
                indices = (indices << 3) | ((long) index);
            }
        }
        for (int i3 = 0; i3 < 6; i3++) {
            out[offset + 2 + i3] = (byte) (indices >> (8 * i3));
        }
    }

    static int[] downsample(int[] rgb, int width, int height) {
        int newWidth = width / 2;
        int newHeight = height / 2;
        int[] result = new int[newWidth * newHeight];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int i = (y * 2 * width) + (x * 2);
                int a = rgb[i];
                int b = rgb[i + 1];
                int c = rgb[i + width];
                int d = rgb[i + width + 1];
                int r = ((((((a >> 16) & 255) + ((b >> 16) & 255)) + ((c >> 16) & 255)) + ((d >> 16) & 255)) + 2) >> 2;
                int g = ((((((a >> 8) & 255) + ((b >> 8) & 255)) + ((c >> 8) & 255)) + ((d >> 8) & 255)) + 2) >> 2;
                int bl = (((((a & 255) + (b & 255)) + (c & 255)) + (d & 255)) + 2) >> 2;
                result[(y * newWidth) + x] = (r << 16) | (g << 8) | bl;
            }
        }
        return result;
    }

    private static int to565(int r, int g, int b) {
        return ((((r * 31) + GeneratedMapNameEncoder.NUM_BINS) / 255) << 11) | ((((g * 63) + GeneratedMapNameEncoder.NUM_BINS) / 255) << 5) | (((b * 31) + GeneratedMapNameEncoder.NUM_BINS) / 255);
    }

    private static int expand5(int value) {
        return (value << 3) | (value >> 2);
    }

    private static int expand6(int value) {
        return (value << 2) | (value >> 4);
    }
}
