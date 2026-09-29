package com.faforever.neroxis.cli;

import java.nio.file.Path;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/RequiredMapPathMixin.class */
public class RequiredMapPathMixin {

    @CommandLine.Option(names = {"--map-path"}, required = true, description = {"Map folder containing map to modify"}, converter = {MapFolderConverter.class})
    private Path mapPath;

    @Generated
    public void setMapPath(final Path mapPath) {
        this.mapPath = mapPath;
    }

    @Generated
    public Path getMapPath() {
        return this.mapPath;
    }
}
