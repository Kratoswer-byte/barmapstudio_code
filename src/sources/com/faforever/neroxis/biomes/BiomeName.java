package com.faforever.neroxis.biomes;

import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/biomes/BiomeName.class */
public enum BiomeName {
    BRIMSTONE("Brimstone"),
    DESERT("Desert"),
    EARLYAUTUMN("EarlyAutumn"),
    FRITHEN("Frithen"),
    MARS("Mars"),
    MOONLIGHT("Moonlight"),
    PRAYER("Prayer"),
    STONES("Stones"),
    SUNSET("Sunset"),
    SYRTIS("Syrtis"),
    WINDINGRIVER("WindingRiver"),
    WONDER("Wonder"),
    CRYSTALLINE("Crystalline");

    private final String folderName;

    @Generated
    BiomeName(final String folderName) {
        this.folderName = folderName;
    }

    @Generated
    public String getFolderName() {
        return this.folderName;
    }
}
