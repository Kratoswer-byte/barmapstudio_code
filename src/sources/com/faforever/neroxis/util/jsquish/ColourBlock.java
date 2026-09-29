package com.faforever.neroxis.util.jsquish;

import java.util.Arrays;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/ColourBlock.class */
final class ColourBlock {
    private ColourBlock() {
    }

    static int gammaColour(final float colour, final float scale) {
        return Math.round(scale * colour);
    }

    static void writeColourBlock3(final Vec start, final Vec end, final int[] indices, final byte[] block, final int offset) {
        int[] remapped = new int[16];
        int a = floatTo565(start);
        int b = floatTo565(end);
        if (a <= b) {
            System.arraycopy(indices, 0, remapped, 0, 16);
        } else {
            a = b;
            b = a;
            for (int i = 0; i < 16; i++) {
                if (indices[i] == 0) {
                    remapped[i] = 1;
                } else if (indices[i] == 1) {
                    remapped[i] = 0;
                } else {
                    remapped[i] = indices[i];
                }
            }
        }
        writeColourBlock(a, b, remapped, block, offset);
    }

    private static int floatTo565(final Vec colour) {
        int r = Math.round(31.0f * colour.x());
        int g = Math.round(63.0f * colour.y());
        int b = Math.round(31.0f * colour.z());
        return (r << 11) | (g << 5) | b;
    }

    private static void writeColourBlock(final int a, final int b, final int[] indices, final byte[] block, final int offset) {
        block[offset] = (byte) (a & 255);
        block[offset + 1] = (byte) ((a >> 8) & 255);
        block[offset + 2] = (byte) (b & 255);
        block[offset + 3] = (byte) ((b >> 8) & 255);
        for (int i = 0; i < 4; i++) {
            int index = 4 * i;
            block[offset + 4 + i] = (byte) (indices[index] | (indices[index + 1] << 2) | (indices[index + 2] << 4) | (indices[index + 3] << 6));
        }
    }

    static void writeColourBlock4(final Vec start, final Vec end, final int[] indices, final byte[] block, final int offset) {
        int[] remapped = new int[16];
        int a = floatTo565(start);
        int b = floatTo565(end);
        if (a < b) {
            a = b;
            b = a;
            for (int i = 0; i < 16; i++) {
                remapped[i] = (indices[i] ^ 1) & 3;
            }
        } else if (a == b) {
            Arrays.fill(remapped, 0);
        } else {
            System.arraycopy(indices, 0, remapped, 0, 16);
        }
        writeColourBlock(a, b, remapped, block, offset);
    }

    static void decompressColour(final byte[] rgba, final byte[] block, final int offset, final boolean isDXT1) {
        int color2;
        int color3;
        int[] codes = new int[16];
        int color0 = unpack565(block, offset, codes, 0);
        int color1 = unpack565(block, offset + 2, codes, 4);
        if (color0 <= color1) {
            color2 = (color0 + color1) / 2;
            color3 = 0;
        } else {
            color2 = ((2 * color0) + color1) / 3;
            color3 = (color0 + (2 * color1)) / 3;
        }
        unpack565(color2, codes, 8);
        unpack565(color3, codes, 12);
        codes[15] = color0 <= color1 ? 0 : 255;
        int[] indices = new int[16];
        for (int i = 0; i < 4; i++) {
            int index = 4 * i;
            byte b = block[offset + 4 + i];
            indices[index] = b & 3;
            indices[index + 1] = (b >> 2) & 3;
            indices[index + 2] = (b >> 4) & 3;
            indices[index + 3] = (b >> 6) & 3;
        }
        for (int i2 = 0; i2 < 16; i2++) {
            int index2 = 4 * indices[i2];
            for (int j = 0; j < 4; j++) {
                rgba[(4 * i2) + j] = (byte) codes[index2 + j];
            }
        }
    }

    private static int unpack565(final byte[] packed, final int pOffset, final int[] colour, final int cOffset) {
        int value = (packed[pOffset] & 255) | ((packed[pOffset + 1] & 255) << 8);
        int red = (value >> 11) & 31;
        int green = (value >> 5) & 63;
        int blue = value & 31;
        colour[cOffset] = (red << 3) | (red >> 2);
        colour[cOffset + 1] = (green << 2) | (green >> 4);
        colour[cOffset + 2] = (blue << 3) | (blue >> 2);
        colour[cOffset + 3] = 255;
        return value;
    }

    private static void unpack565(final int value, final int[] colour, final int cOffset) {
        int red = (value >> 11) & 31;
        int green = (value >> 5) & 63;
        int blue = value & 31;
        colour[cOffset] = (red << 3) | (red >> 2);
        colour[cOffset + 1] = (green << 2) | (green >> 4);
        colour[cOffset + 2] = (blue << 3) | (blue >> 2);
        colour[cOffset + 3] = 255;
    }
}
