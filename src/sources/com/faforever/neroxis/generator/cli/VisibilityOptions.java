package com.faforever.neroxis.generator.cli;

import com.faforever.neroxis.generator.util.serial.Visibility;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/VisibilityOptions.class */
public class VisibilityOptions {

    @CommandLine.Option(names = {"--visibility"}, order = 100, description = {"Visibility for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    private Visibility visibility;

    @Generated
    public Visibility getVisibility() {
        return this.visibility;
    }

    @CommandLine.Option(names = {"--tournament-style"}, hidden = true, description = {"Remove the preview.png and add time of original generation to map"})
    private void setTournamentStyle(boolean value) {
        this.visibility = Visibility.TOURNAMENT;
    }

    @CommandLine.Option(names = {"--blind"}, hidden = true, description = {"Remove the preview.png, add time of original generation to map, and remove in game lobby preview"})
    private void setBlind(boolean value) {
        this.visibility = Visibility.BLIND;
    }

    @CommandLine.Option(names = {"--unexplored"}, order = 4, hidden = true, description = {"Remove the preview.png, add time of original generation to map, remove in game lobby preview, and add unexplored fog of war"})
    private void setUnexplored(boolean value) {
        this.visibility = Visibility.UNEXPLORED;
    }
}
