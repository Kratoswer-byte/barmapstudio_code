package com.faforever.neroxis.generator;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.util.Range;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/ParameterConstraints.class */
public final class ParameterConstraints extends Record {
    private final Range spawnCountRange;
    private final Range mapSizeRange;
    private final Range numTeamsRange;
    public static ParameterConstraints ANY = builder().build();

    public ParameterConstraints(Range spawnCountRange, Range mapSizeRange, Range numTeamsRange) {
        this.spawnCountRange = spawnCountRange;
        this.mapSizeRange = mapSizeRange;
        this.numTeamsRange = numTeamsRange;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ParameterConstraints.class), ParameterConstraints.class, "spawnCountRange;mapSizeRange;numTeamsRange", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->spawnCountRange:Lcom/faforever/neroxis/util/Range;", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->mapSizeRange:Lcom/faforever/neroxis/util/Range;", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->numTeamsRange:Lcom/faforever/neroxis/util/Range;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ParameterConstraints.class), ParameterConstraints.class, "spawnCountRange;mapSizeRange;numTeamsRange", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->spawnCountRange:Lcom/faforever/neroxis/util/Range;", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->mapSizeRange:Lcom/faforever/neroxis/util/Range;", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->numTeamsRange:Lcom/faforever/neroxis/util/Range;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ParameterConstraints.class, Object.class), ParameterConstraints.class, "spawnCountRange;mapSizeRange;numTeamsRange", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->spawnCountRange:Lcom/faforever/neroxis/util/Range;", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->mapSizeRange:Lcom/faforever/neroxis/util/Range;", "FIELD:Lcom/faforever/neroxis/generator/ParameterConstraints;->numTeamsRange:Lcom/faforever/neroxis/util/Range;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public Range spawnCountRange() {
        return this.spawnCountRange;
    }

    public Range mapSizeRange() {
        return this.mapSizeRange;
    }

    public Range numTeamsRange() {
        return this.numTeamsRange;
    }

    public static ParameterConstraintsBuilder builder() {
        return new ParameterConstraintsBuilder();
    }

    public boolean matches(SizeSpawnParameters sizeSpawnParameters) {
        return this.numTeamsRange.contains((float) sizeSpawnParameters.numTeams()) && this.spawnCountRange.contains((float) sizeSpawnParameters.spawnCount()) && this.mapSizeRange.contains((float) sizeSpawnParameters.mapSize());
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/ParameterConstraints$ParameterConstraintsBuilder.class */
    public static class ParameterConstraintsBuilder {
        Range spawnCountRange = Range.of(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 16.0f);
        Range mapSizeRange = Range.of(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 2048.0f);
        Range numTeamsRange = Range.of(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 16.0f);

        public ParameterConstraints build() {
            return new ParameterConstraints(this.spawnCountRange, this.mapSizeRange, this.numTeamsRange);
        }

        public ParameterConstraintsBuilder spawnCount(float min, float max) {
            this.spawnCountRange = Range.of(min, max);
            return this;
        }

        public ParameterConstraintsBuilder mapSizes(float min, float max) {
            this.mapSizeRange = Range.of(min, max);
            return this;
        }

        public ParameterConstraintsBuilder numTeams(float min, float max) {
            this.numTeamsRange = Range.of(min, max);
            return this;
        }
    }
}
