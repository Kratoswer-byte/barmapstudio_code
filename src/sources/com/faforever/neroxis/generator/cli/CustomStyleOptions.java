package com.faforever.neroxis.generator.cli;

import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.generator.util.serial.PropStyle;
import com.faforever.neroxis.generator.util.serial.ResourceStyle;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import lombok.Generated;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/cli/CustomStyleOptions.class */
public class CustomStyleOptions {

    @CommandLine.Option(names = {"--texture-style", "--biome"}, description = {"Texture style to use for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    private BiomeName biomeName;

    @CommandLine.Option(names = {"--terrain-style"}, order = 29, description = {"Terrain style to use for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    private TerrainStyle terrainStyle;

    @CommandLine.Option(names = {"--resource-style"}, order = 29, description = {"Resource style to use for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    private ResourceStyle resourceStyle;

    @CommandLine.Option(names = {"--prop-style"}, order = 29, description = {"Prop style to use for the generated map. Values: ${COMPLETION-CANDIDATES}"})
    private PropStyle propStyle;

    @CommandLine.Option(names = {"--reclaim-density"}, order = 29, description = {"Reclaim density for the generated map. Min: 0 Max: 1"}, converter = {BinnedDensityConverter.class})
    private Float reclaimDensity;

    @CommandLine.Option(names = {"--resource-density"}, order = 29, description = {"Resource density for the generated map. Min: 0 Max: 1"}, converter = {BinnedDensityConverter.class})
    private Float resourceDensity;

    @Generated
    public void setBiomeName(final BiomeName biomeName) {
        this.biomeName = biomeName;
    }

    @Generated
    public void setTerrainStyle(final TerrainStyle terrainStyle) {
        this.terrainStyle = terrainStyle;
    }

    @Generated
    public void setResourceStyle(final ResourceStyle resourceStyle) {
        this.resourceStyle = resourceStyle;
    }

    @Generated
    public void setPropStyle(final PropStyle propStyle) {
        this.propStyle = propStyle;
    }

    @Generated
    public void setReclaimDensity(final Float reclaimDensity) {
        this.reclaimDensity = reclaimDensity;
    }

    @Generated
    public void setResourceDensity(final Float resourceDensity) {
        this.resourceDensity = resourceDensity;
    }

    @Generated
    public BiomeName getBiomeName() {
        return this.biomeName;
    }

    @Generated
    public TerrainStyle getTerrainStyle() {
        return this.terrainStyle;
    }

    @Generated
    public ResourceStyle getResourceStyle() {
        return this.resourceStyle;
    }

    @Generated
    public PropStyle getPropStyle() {
        return this.propStyle;
    }

    @Generated
    public Float getReclaimDensity() {
        return this.reclaimDensity;
    }

    @Generated
    public Float getResourceDensity() {
        return this.resourceDensity;
    }
}
