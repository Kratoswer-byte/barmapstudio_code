package com.faforever.neroxis.biomes;

import com.faforever.neroxis.util.serial.biome.DecalMaterials;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.serial.biome.PropMaterials;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.serial.biome.WaterSettings;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/biomes/Biome.class */
public final class Biome extends Record {
    private final BiomeName name;
    private final TerrainMaterials terrainMaterials;
    private final PropMaterials propMaterials;
    private final DecalMaterials decalMaterials;
    private final WaterSettings waterSettings;
    private final LightingSettings lightingSettings;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Biome.class), Biome.class, "name;terrainMaterials;propMaterials;decalMaterials;waterSettings;lightingSettings", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->name:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->terrainMaterials:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->propMaterials:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->decalMaterials:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->waterSettings:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->lightingSettings:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Biome.class), Biome.class, "name;terrainMaterials;propMaterials;decalMaterials;waterSettings;lightingSettings", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->name:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->terrainMaterials:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->propMaterials:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->decalMaterials:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->waterSettings:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->lightingSettings:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Biome.class, Object.class), Biome.class, "name;terrainMaterials;propMaterials;decalMaterials;waterSettings;lightingSettings", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->name:Lcom/faforever/neroxis/biomes/BiomeName;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->terrainMaterials:Lcom/faforever/neroxis/util/serial/biome/TerrainMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->propMaterials:Lcom/faforever/neroxis/util/serial/biome/PropMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->decalMaterials:Lcom/faforever/neroxis/util/serial/biome/DecalMaterials;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->waterSettings:Lcom/faforever/neroxis/util/serial/biome/WaterSettings;", "FIELD:Lcom/faforever/neroxis/biomes/Biome;->lightingSettings:Lcom/faforever/neroxis/util/serial/biome/LightingSettings;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public BiomeName name() {
        return this.name;
    }

    public TerrainMaterials terrainMaterials() {
        return this.terrainMaterials;
    }

    public PropMaterials propMaterials() {
        return this.propMaterials;
    }

    public DecalMaterials decalMaterials() {
        return this.decalMaterials;
    }

    public WaterSettings waterSettings() {
        return this.waterSettings;
    }

    public LightingSettings lightingSettings() {
        return this.lightingSettings;
    }

    public Biome(BiomeName name, TerrainMaterials terrainMaterials, PropMaterials propMaterials, DecalMaterials decalMaterials, WaterSettings waterSettings, LightingSettings lightingSettings) {
        Objects.requireNonNull(terrainMaterials);
        Objects.requireNonNull(propMaterials);
        Objects.requireNonNull(decalMaterials);
        Objects.requireNonNull(waterSettings);
        Objects.requireNonNull(lightingSettings);
        this.name = name;
        this.terrainMaterials = terrainMaterials;
        this.propMaterials = propMaterials;
        this.decalMaterials = decalMaterials;
        this.waterSettings = waterSettings;
        this.lightingSettings = lightingSettings;
    }
}
