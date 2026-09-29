package com.faforever.neroxis.util.jsquish;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/ColourSet.class */
final class ColourSet {
    private final Vec[] points = new Vec[16];
    private final float[] weights = new float[16];
    private final int[] remap = new int[16];
    private int count;
    private boolean transparent;

    ColourSet() {
        for (int i = 0; i < this.points.length; i++) {
            this.points[i] = new Vec();
        }
    }

    void init(final byte[] rgba, final int mask, final Squish.CompressionType type, final boolean weightAlpha) {
        boolean isDXT1 = type == Squish.CompressionType.DXT1;
        this.count = 0;
        this.transparent = false;
        for (int i = 0; i < 16; i++) {
            int bit = 1 << i;
            if ((mask & bit) == 0) {
                this.remap[i] = -1;
            } else if (isDXT1 && (rgba[(4 * i) + 3] & 255) < 128) {
                this.remap[i] = -1;
                this.transparent = true;
            } else {
                int j = 0;
                while (true) {
                    if (j == i) {
                        float r = (rgba[4 * i] & 255) / 255.0f;
                        float g = (rgba[(4 * i) + 1] & 255) / 255.0f;
                        float b = (rgba[(4 * i) + 2] & 255) / 255.0f;
                        this.points[this.count].set(r, g, b);
                        this.weights[this.count] = weightAlpha ? ((rgba[(4 * i) + 3] & 255) + 1) / 256.0f : 1.0f;
                        int i2 = this.count;
                        this.count = i2 + 1;
                        this.remap[i] = i2;
                        break;
                    }
                    int oldBit = 1 << j;
                    boolean match = (mask & oldBit) != 0 && rgba[4 * i] == rgba[4 * j] && rgba[(4 * i) + 1] == rgba[(4 * j) + 1] && rgba[(4 * i) + 2] == rgba[(4 * j) + 2] && !isDXT1;
                    if (!match) {
                        j++;
                    } else {
                        int index = this.remap[j];
                        float[] fArr = this.weights;
                        fArr[index] = fArr[index] + (weightAlpha ? ((rgba[(4 * i) + 3] & 255) + 1) / 256.0f : 1.0f);
                        this.remap[i] = index;
                        break;
                    }
                }
            }
        }
    }

    int getCount() {
        return this.count;
    }

    Vec[] getPoints() {
        return this.points;
    }

    float[] getWeights() {
        return this.weights;
    }

    boolean isTransparent() {
        return this.transparent;
    }

    void remapIndices(final int[] source, final int[] target) {
        for (int i = 0; i < 16; i++) {
            int j = this.remap[i];
            if (j == -1) {
                target[i] = 3;
            } else {
                target[i] = source[j];
            }
        }
    }
}
