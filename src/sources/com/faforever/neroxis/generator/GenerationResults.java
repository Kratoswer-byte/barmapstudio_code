package com.faforever.neroxis.generator;

import com.faforever.neroxis.biomes.BiomeName;
import com.faforever.neroxis.generator.util.serial.PropStyle;
import com.faforever.neroxis.generator.util.serial.ResourceStyle;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.Pipeline;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/GenerationResults.class */
final class GenerationResults extends Record {
    private final SCMap map;
    private final SymmetrySettings symmetrySettings;
    private final TerrainStyle terrainStyle;
    private final PropStyle propStyle;
    private final BiomeName biomeName;
    private final ResourceStyle resourceStyle;
    private final List<Pipeline.Entry> terrainPipelineEntries;
    private final List<Pipeline.Entry> placementPipelineEntries;

    GenerationResults(SCMap map, SymmetrySettings symmetrySettings, TerrainStyle terrainStyle, PropStyle propStyle, BiomeName biomeName, ResourceStyle resourceStyle, List<Pipeline.Entry> terrainPipelineEntries, List<Pipeline.Entry> placementPipelineEntries) {
        this.map = map;
        this.symmetrySettings = symmetrySettings;
        this.terrainStyle = terrainStyle;
        this.propStyle = propStyle;
        this.biomeName = biomeName;
        this.resourceStyle = resourceStyle;
        this.terrainPipelineEntries = terrainPipelineEntries;
        this.placementPipelineEntries = placementPipelineEntries;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GenerationResults.class), GenerationResults.class, "map;symmetrySettings;terrainStyle;propStyle;biomeName;resourceStyle;terrainPipelineEntries;placementPipelineEntries", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->map:Lcom/faforever/neroxis/map/SCMap;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->symmetrySettings:Lcom/faforever/neroxis/map/SymmetrySettings;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->terrainStyle:Lcom/faforever/neroxis/generator/util/serial/TerrainStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->propStyle:Lcom/faforever/neroxis/generator/util/serial/PropStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->biomeName:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->resourceStyle:Lcom/faforever/neroxis/generator/util/serial/ResourceStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->terrainPipelineEntries:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->placementPipelineEntries:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GenerationResults.class), GenerationResults.class, "map;symmetrySettings;terrainStyle;propStyle;biomeName;resourceStyle;terrainPipelineEntries;placementPipelineEntries", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->map:Lcom/faforever/neroxis/map/SCMap;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->symmetrySettings:Lcom/faforever/neroxis/map/SymmetrySettings;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->terrainStyle:Lcom/faforever/neroxis/generator/util/serial/TerrainStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->propStyle:Lcom/faforever/neroxis/generator/util/serial/PropStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->biomeName:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->resourceStyle:Lcom/faforever/neroxis/generator/util/serial/ResourceStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->terrainPipelineEntries:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->placementPipelineEntries:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GenerationResults.class, Object.class), GenerationResults.class, "map;symmetrySettings;terrainStyle;propStyle;biomeName;resourceStyle;terrainPipelineEntries;placementPipelineEntries", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->map:Lcom/faforever/neroxis/map/SCMap;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->symmetrySettings:Lcom/faforever/neroxis/map/SymmetrySettings;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->terrainStyle:Lcom/faforever/neroxis/generator/util/serial/TerrainStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->propStyle:Lcom/faforever/neroxis/generator/util/serial/PropStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->biomeName:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->resourceStyle:Lcom/faforever/neroxis/generator/util/serial/ResourceStyle;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->terrainPipelineEntries:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/GenerationResults;->placementPipelineEntries:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public SCMap map() {
        return this.map;
    }

    public SymmetrySettings symmetrySettings() {
        return this.symmetrySettings;
    }

    public TerrainStyle terrainStyle() {
        return this.terrainStyle;
    }

    public PropStyle propStyle() {
        return this.propStyle;
    }

    public BiomeName biomeName() {
        return this.biomeName;
    }

    public ResourceStyle resourceStyle() {
        return this.resourceStyle;
    }

    public List<Pipeline.Entry> terrainPipelineEntries() {
        return this.terrainPipelineEntries;
    }

    public List<Pipeline.Entry> placementPipelineEntries() {
        return this.placementPipelineEntries;
    }

    public String settingsToString() {
        return "Symmetry Settings: %s\nBiome: %s\nTerrainStyle: %s\nResourceStyle: %s\nPropStyle: %s\n".formatted(this.symmetrySettings, this.biomeName, this.terrainStyle, this.resourceStyle, this.propStyle);
    }

    public void writePipelines(OutputStream out) throws IOException {
        try {
            for (Pipeline.Entry entry : this.terrainPipelineEntries) {
                out.write(entry.getResult().toHash().getBytes(StandardCharsets.UTF_8));
                out.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            for (Pipeline.Entry entry2 : this.placementPipelineEntries) {
                out.write(entry2.getResult().toHash().getBytes(StandardCharsets.UTF_8));
                out.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            out.flush();
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }
}
