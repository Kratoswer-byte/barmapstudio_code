package com.faforever.neroxis.cli;

import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/VisualizeMixin.class */
public class VisualizeMixin {

    @CommandLine.Option(names = {"--visualize"}, description = {"Enable visualization"}, negatable = true)
    private boolean visualize;

    @Generated
    public void setVisualize(final boolean visualize) {
        this.visualize = visualize;
    }

    @Generated
    public boolean isVisualize() {
        return this.visualize;
    }
}
