package com.faforever.neroxis.generator.cli;

import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/TopLevelOptions.class */
public class TopLevelOptions {

    @CommandLine.Option(names = {"--map-name"}, order = 1, description = {"Name of map to recreate. Must be of the form neroxis_map_generator_version_seed_options, if present other parameter options will be ignored"})
    private String specifiedMapName;

    @CommandLine.ArgGroup(exclusive = false)
    private SpecifiedOptions specifiedOptions = new SpecifiedOptions();

    @Generated
    public void setSpecifiedMapName(final String specifiedMapName) {
        this.specifiedMapName = specifiedMapName;
    }

    @Generated
    public void setSpecifiedOptions(final SpecifiedOptions specifiedOptions) {
        this.specifiedOptions = specifiedOptions;
    }

    @Generated
    public String getSpecifiedMapName() {
        return this.specifiedMapName;
    }

    @Generated
    public SpecifiedOptions getSpecifiedOptions() {
        return this.specifiedOptions;
    }
}
