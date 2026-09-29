package com.faforever.neroxis.cli;

import java.nio.file.Path;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/OutputFolderMixin.class */
public class OutputFolderMixin {

    @CommandLine.Option(names = {"--out-path", "--folder-path"}, order = 1, description = {"Folder to save the map to"}, defaultValue = ".")
    private Path outputPath = Path.of(".", new String[0]);

    @Generated
    public void setOutputPath(final Path outputPath) {
        this.outputPath = outputPath;
    }

    @Generated
    public Path getOutputPath() {
        return this.outputPath;
    }
}
