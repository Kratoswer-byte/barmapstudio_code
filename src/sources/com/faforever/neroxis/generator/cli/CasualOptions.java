package com.faforever.neroxis.generator.cli;

import com.faforever.neroxis.map.Symmetry;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/CasualOptions.class */
public class CasualOptions {

    @CommandLine.ArgGroup
    private StyleOptions styleOptions;

    @CommandLine.Option(names = {"--seed"}, order = 3, description = {"Seed for the generated map"})
    private Long seed;

    @CommandLine.Option(names = {"--terrain-symmetry"}, order = 100, description = {"Base terrain symmetry for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    private Symmetry terrainSymmetry;

    @Generated
    public StyleOptions getStyleOptions() {
        return this.styleOptions;
    }

    @Generated
    public Long getSeed() {
        return this.seed;
    }

    @Generated
    public Symmetry getTerrainSymmetry() {
        return this.terrainSymmetry;
    }
}
