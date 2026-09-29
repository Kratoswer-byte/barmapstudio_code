package com.faforever.neroxis.generator.cli;

import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/SpecifiedOptions.class */
public class SpecifiedOptions {

    @CommandLine.ArgGroup(exclusive = false)
    private BasicOptions basicOptions = new BasicOptions();

    @CommandLine.ArgGroup
    private GenerationOptions generationOptions = new GenerationOptions();

    @CommandLine.Option(names = {"--num-to-generate"}, order = 2, defaultValue = "1", description = {"Number of maps to create"})
    private int numToGenerate = 1;

    @Generated
    public void setBasicOptions(final BasicOptions basicOptions) {
        this.basicOptions = basicOptions;
    }

    @Generated
    public void setGenerationOptions(final GenerationOptions generationOptions) {
        this.generationOptions = generationOptions;
    }

    @Generated
    public void setNumToGenerate(final int numToGenerate) {
        this.numToGenerate = numToGenerate;
    }

    @Generated
    public BasicOptions getBasicOptions() {
        return this.basicOptions;
    }

    @Generated
    public GenerationOptions getGenerationOptions() {
        return this.generationOptions;
    }

    @Generated
    public int getNumToGenerate() {
        return this.numToGenerate;
    }
}
