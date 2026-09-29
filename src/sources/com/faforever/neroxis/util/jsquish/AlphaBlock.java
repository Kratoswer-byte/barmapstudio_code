package com.faforever.neroxis.util.jsquish;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/AlphaBlock.class */
final class AlphaBlock {
    private AlphaBlock() {
    }

    static void compressAlphaDxt3(final byte[] rgba, final int mask, final byte[] block, final int offset) {
        for (int i = 0; i < 8; i++) {
            float alpha1 = (rgba[(8 * i) + 3] & 255) * 0.05882353f;
            float alpha2 = (rgba[(8 * i) + 7] & 255) * 0.05882353f;
            int quant1 = Math.round(alpha1);
            int quant2 = Math.round(alpha2);
            int bit1 = 1 << (2 * i);
            int bit2 = 1 << ((2 * i) + 1);
            if ((mask & bit1) == 0) {
                quant1 = 0;
            }
            if ((mask & bit2) == 0) {
                quant2 = 0;
            }
            block[offset + i] = (byte) (quant1 | (quant2 << 4));
        }
    }

    static void decompressAlphaDxt3(final byte[] rgba, final byte[] block, final int offset) {
        for (int i = 0; i < 8; i++) {
            int quant = block[offset + i] & 255;
            int lo = quant & 15;
            int hi = quant & 240;
            rgba[(8 * i) + 3] = (byte) (lo | (lo << 4));
            rgba[(8 * i) + 7] = (byte) (hi | (hi >> 4));
        }
    }

    static void compressAlphaDxt5(final byte[] rgba, final int mask, final byte[] block, final int offset) {
        int min5 = 255;
        int max5 = 0;
        int min7 = 255;
        int max7 = 0;
        for (int i = 0; i < 16; i++) {
            int bit = 1 << i;
            if ((mask & bit) != 0) {
                int value = rgba[(4 * i) + 3] & 255;
                if (value < min7) {
                    min7 = value;
                }
                if (value > max7) {
                    max7 = value;
                }
                if (value != 0 && value < min5) {
                    min5 = value;
                }
                if (value != 255 && value > max5) {
                    max5 = value;
                }
            }
        }
        if (min5 > max5) {
            min5 = max5;
        }
        if (min7 > max7) {
            min7 = max7;
        }
        if (max5 - min5 < 5) {
            max5 = Math.min(min5 + 5, 255);
        }
        if (max5 - min5 < 5) {
            min5 = Math.max(0, max5 - 5);
        }
        if (max7 - min7 < 7) {
            max7 = Math.min(min7 + 7, 255);
        }
        if (max7 - min7 < 7) {
            min7 = Math.max(0, max7 - 7);
        }
        int[] codes5 = new int[8];
        codes5[0] = min5;
        codes5[1] = max5;
        for (int i2 = 1; i2 < 5; i2++) {
            codes5[1 + i2] = (((5 - i2) * min5) + (i2 * max5)) / 5;
        }
        codes5[6] = 0;
        codes5[7] = 255;
        int[] codes7 = new int[8];
        codes7[0] = min7;
        codes7[1] = max7;
        for (int i3 = 1; i3 < 7; i3++) {
            codes7[1 + i3] = (((7 - i3) * min7) + (i3 * max7)) / 7;
        }
        int[] indices5 = new int[16];
        int[] indices7 = new int[16];
        int err5 = fitCodes(rgba, mask, codes5, indices5);
        int err7 = fitCodes(rgba, mask, codes7, indices7);
        if (err5 <= err7) {
            writeAlphaBlock5(min5, max5, indices5, block, offset);
        } else {
            writeAlphaBlock7(min7, max7, indices7, block, offset);
        }
    }

    private static int fitCodes(final byte[] rgba, final int mask, final int[] codes, final int[] indices) {
        int err = 0;
        for (int i = 0; i < 16; i++) {
            int bit = 1 << i;
            if ((mask & bit) == 0) {
                indices[i] = 0;
            } else {
                int value = rgba[(4 * i) + 3] & 255;
                int least = Integer.MAX_VALUE;
                int index = 0;
                for (int j = 0; j < 8; j++) {
                    int dist = value - codes[j];
                    int dist2 = dist * dist;
                    if (dist2 < least) {
                        least = dist2;
                        index = j;
                    }
                }
                indices[i] = index;
                err += least;
            }
        }
        return err;
    }

    private static void writeAlphaBlock5(final int alpha0, final int alpha1, final int[] indices, final byte[] block, final int offset) {
        int[] swapped = new int[16];
        if (alpha0 > alpha1) {
            for (int i = 0; i < 16; i++) {
                int index = indices[i];
                if (index == 0) {
                    swapped[i] = 1;
                } else if (index == 1) {
                    swapped[i] = 0;
                } else if (index <= 5) {
                    swapped[i] = 7 - index;
                } else {
                    swapped[i] = index;
                }
            }
            writeAlphaBlock(alpha1, alpha0, swapped, block, offset);
            return;
        }
        writeAlphaBlock(alpha0, alpha1, indices, block, offset);
    }

    private static void writeAlphaBlock(final int alpha0, final int alpha1, final int[] indices, final byte[] block, final int offset) {
        block[offset] = (byte) alpha0;
        block[offset + 1] = (byte) alpha1;
        int src = 0;
        int dest = 2;
        for (int i = 0; i < 2; i++) {
            int value = 0;
            for (int j = 0; j < 8; j++) {
                int i2 = src;
                src++;
                int index = indices[i2];
                value |= index << (3 * j);
            }
            for (int j2 = 0; j2 < 3; j2++) {
                int i3 = dest;
                dest++;
                block[offset + i3] = (byte) ((value >> (8 * j2)) & 255);
            }
        }
    }

    private static void writeAlphaBlock7(final int alpha0, final int alpha1, final int[] indices, final byte[] block, final int offset) {
        int[] swapped = new int[16];
        if (alpha0 < alpha1) {
            for (int i = 0; i < 16; i++) {
                int index = indices[i];
                if (index == 0) {
                    swapped[i] = 1;
                } else if (index == 1) {
                    swapped[i] = 0;
                } else {
                    swapped[i] = 9 - index;
                }
            }
            writeAlphaBlock(alpha1, alpha0, swapped, block, offset);
            return;
        }
        writeAlphaBlock(alpha0, alpha1, indices, block, offset);
    }

    static void decompressAlphaDxt5(final byte[] rgba, final byte[] block, final int offset) {
        int alpha0 = block[offset] & 255;
        int alpha1 = block[offset + 1] & 255;
        int[] codes = new int[8];
        codes[0] = alpha0;
        codes[1] = alpha1;
        if (alpha0 <= alpha1) {
            for (int i = 1; i < 5; i++) {
                codes[1 + i] = (((5 - i) * alpha0) + (i * alpha1)) / 5;
            }
            codes[6] = 0;
            codes[7] = 255;
        } else {
            for (int i2 = 1; i2 < 7; i2++) {
                codes[1 + i2] = (((7 - i2) * alpha0) + (i2 * alpha1)) / 7;
            }
        }
        int[] indices = new int[16];
        int src = 2;
        int dest = 0;
        for (int i3 = 0; i3 < 2; i3++) {
            int value = 0;
            for (int j = 0; j < 3; j++) {
                int i4 = src;
                src++;
                int b = block[offset + i4] & 255;
                value |= b << (8 * j);
            }
            for (int j2 = 0; j2 < 8; j2++) {
                int index = (value >> (3 * j2)) & 7;
                int i5 = dest;
                dest++;
                indices[i5] = index;
            }
        }
        for (int i6 = 0; i6 < 16; i6++) {
            rgba[(4 * i6) + 3] = (byte) codes[indices[i6]];
        }
    }
}
