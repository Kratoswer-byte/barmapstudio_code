package com.faforever.neroxis.util.jsquish;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/jsquish/CompressorColourFit.class */
abstract class CompressorColourFit {
    protected static final float GRID_X = 31.0f;
    protected static final float GRID_Y = 63.0f;
    protected static final float GRID_Z = 31.0f;
    protected static final float GRID_X_RCP = 0.032258064f;
    protected static final float GRID_Y_RCP = 0.015873017f;
    protected static final float GRID_Z_RCP = 0.032258064f;
    protected final ColourSet colours;
    protected final Squish.CompressionType type;

    abstract void compress3(byte[] block, int offset);

    abstract void compress4(byte[] block, int offset);

    protected CompressorColourFit(final ColourSet colours, final Squish.CompressionType type) {
        this.colours = colours;
        this.type = type;
    }

    protected static float clamp(final float v, final float GRID, final float GRID_RCP) {
        if (v <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
        }
        if (v >= 1.0f) {
            return 1.0f;
        }
        return ((int) ((GRID * v) + 0.5f)) * GRID_RCP;
    }

    void compress(final byte[] block, final int offset) {
        if (this.type == Squish.CompressionType.DXT1) {
            compress3(block, offset);
            if (!this.colours.isTransparent()) {
                compress4(block, offset);
                return;
            }
            return;
        }
        compress4(block, offset);
    }
}
