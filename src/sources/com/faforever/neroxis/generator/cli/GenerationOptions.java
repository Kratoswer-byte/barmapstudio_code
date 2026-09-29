package com.faforever.neroxis.generator.cli;

import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/GenerationOptions.class */
public class GenerationOptions {

    @CommandLine.ArgGroup
    private VisibilityOptions visibilityOptions;

    @CommandLine.ArgGroup(exclusive = false)
    private CasualOptions casualOptions = new CasualOptions();

    @Generated
    public VisibilityOptions getVisibilityOptions() {
        return this.visibilityOptions;
    }

    @Generated
    public CasualOptions getCasualOptions() {
        return this.casualOptions;
    }
}
