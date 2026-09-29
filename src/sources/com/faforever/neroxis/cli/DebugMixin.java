package com.faforever.neroxis.cli;

import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/DebugMixin.class */
public class DebugMixin {

    @CommandLine.Option(names = {"--debug"}, description = {"Enable debugging"}, negatable = true)
    private boolean debug;

    @Generated
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Generated
    public boolean isDebug() {
        return this.debug;
    }
}
