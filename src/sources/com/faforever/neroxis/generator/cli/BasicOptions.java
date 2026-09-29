package com.faforever.neroxis.generator.cli;

import com.faforever.neroxis.cli.MultipleMapSizeConverter;
import com.faforever.neroxis.util.dds.DDSHeader;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/BasicOptions.class */
public class BasicOptions {

    @CommandLine.Option(names = {"--spawn-count"}, order = 5, defaultValue = "6", description = {"Spawn count for the generated map"}, showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private int spawnCount = 6;

    @CommandLine.Option(names = {"--num-teams"}, order = 6, defaultValue = "2", description = {"Number of teams for the generated map (0 is no teams asymmetric)"}, showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private int numTeams = 2;

    @CommandLine.Option(names = {"--map-size"}, order = 4, defaultValue = "512", description = {"Generated map size, can be specified in oGrids (e.g 512) or km (e.g 10km)"}, showDefaultValue = CommandLine.Help.Visibility.ALWAYS, converter = {MultipleMapSizeConverter.class})
    private int mapSize = DDSHeader.CUBEMAP_FLAG;

    @Generated
    public void setSpawnCount(final int spawnCount) {
        this.spawnCount = spawnCount;
    }

    @Generated
    public void setNumTeams(final int numTeams) {
        this.numTeams = numTeams;
    }

    @Generated
    public void setMapSize(final int mapSize) {
        this.mapSize = mapSize;
    }

    @Generated
    public int getSpawnCount() {
        return this.spawnCount;
    }

    @Generated
    public int getNumTeams() {
        return this.numTeams;
    }

    @Generated
    public int getMapSize() {
        return this.mapSize;
    }
}
