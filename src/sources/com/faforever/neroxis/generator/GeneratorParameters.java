package com.faforever.neroxis.generator;

import com.faforever.neroxis.generator.util.serial.MapStyle;
import com.faforever.neroxis.generator.util.serial.Visibility;
import com.faforever.neroxis.map.SymmetrySettings;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/GeneratorParameters.class */
public final class GeneratorParameters extends Record implements SizeSpawnParameters {
    private final int spawnCount;
    private final int mapSize;
    private final int numTeams;
    private final MapStyle mapStyle;
    private final SymmetrySettings symmetrySettings;
    private final Visibility visibility;

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GeneratorParameters.class), GeneratorParameters.class, "spawnCount;mapSize;numTeams;mapStyle;symmetrySettings;visibility", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->spawnCount:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->mapSize:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->numTeams:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->mapStyle:Lcom/faforever/neroxis/generator/util/serial/MapStyle;", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->symmetrySettings:Lcom/faforever/neroxis/map/SymmetrySettings;", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->visibility:Lcom/faforever/neroxis/generator/util/serial/Visibility;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GeneratorParameters.class), GeneratorParameters.class, "spawnCount;mapSize;numTeams;mapStyle;symmetrySettings;visibility", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->spawnCount:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->mapSize:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->numTeams:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->mapStyle:Lcom/faforever/neroxis/generator/util/serial/MapStyle;", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->symmetrySettings:Lcom/faforever/neroxis/map/SymmetrySettings;", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->visibility:Lcom/faforever/neroxis/generator/util/serial/Visibility;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GeneratorParameters.class, Object.class), GeneratorParameters.class, "spawnCount;mapSize;numTeams;mapStyle;symmetrySettings;visibility", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->spawnCount:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->mapSize:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->numTeams:I", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->mapStyle:Lcom/faforever/neroxis/generator/util/serial/MapStyle;", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->symmetrySettings:Lcom/faforever/neroxis/map/SymmetrySettings;", "FIELD:Lcom/faforever/neroxis/generator/GeneratorParameters;->visibility:Lcom/faforever/neroxis/generator/util/serial/Visibility;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    @Override // com.faforever.neroxis.generator.SizeSpawnParameters
    public int spawnCount() {
        return this.spawnCount;
    }

    @Override // com.faforever.neroxis.generator.SizeSpawnParameters
    public int mapSize() {
        return this.mapSize;
    }

    @Override // com.faforever.neroxis.generator.SizeSpawnParameters
    public int numTeams() {
        return this.numTeams;
    }

    public MapStyle mapStyle() {
        return this.mapStyle;
    }

    public SymmetrySettings symmetrySettings() {
        return this.symmetrySettings;
    }

    public Visibility visibility() {
        return this.visibility;
    }

    public GeneratorParameters(int spawnCount, int mapSize, int numTeams, MapStyle mapStyle, SymmetrySettings symmetrySettings, Visibility visibility) {
        if (numTeams != 0 && spawnCount % numTeams != 0) {
            throw new IllegalArgumentException("Spawn Count `%d` not a multiple of Num Teams `%d`".formatted(Integer.valueOf(spawnCount), Integer.valueOf(numTeams)));
        }
        if (numTeams == 0 || symmetrySettings.terrainSymmetry().getNumSymPoints() % numTeams == 0) {
            this.spawnCount = spawnCount;
            this.mapSize = mapSize;
            this.numTeams = numTeams;
            this.mapStyle = mapStyle;
            this.symmetrySettings = symmetrySettings;
            this.visibility = visibility;
            return;
        }
        throw new IllegalArgumentException("Terrain symmetry `%s` not compatible with Num Teams `%d`".formatted(symmetrySettings.terrainSymmetry(), Integer.valueOf(numTeams)));
    }
}
