package com.faforever.neroxis.generator.util.serial;

import com.faforever.neroxis.generator.SizeSpawnParameters;
import com.faforever.neroxis.map.Symmetry;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.SplittableRandom;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapNameParameters.class */
public final class MapNameParameters extends Record implements SizeSpawnParameters {
    private final long seed;
    private final int spawnCount;
    private final int mapSize;
    private final int numTeams;
    private final Mode mode;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapNameParameters$Mode.class */
    public interface Mode {
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, MapNameParameters.class), MapNameParameters.class, "seed;spawnCount;mapSize;numTeams;mode", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->seed:J", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->spawnCount:I", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->mapSize:I", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->numTeams:I", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->mode:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Mode;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, MapNameParameters.class, Object.class), MapNameParameters.class, "seed;spawnCount;mapSize;numTeams;mode", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->seed:J", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->spawnCount:I", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->mapSize:I", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->numTeams:I", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters;->mode:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Mode;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public long seed() {
        return this.seed;
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

    public Mode mode() {
        return this.mode;
    }

    public MapNameParameters(long seed, int spawnCount, int mapSize, int numTeams, Mode mode) {
        if (mapSize % 64 != 0) {
            throw new IllegalArgumentException("mapSize must be a multiple of 64");
        }
        if (numTeams != 0 && spawnCount % numTeams != 0) {
            throw new IllegalArgumentException("Spawn Count `%d` not a multiple of Num Teams `%d`".formatted(Integer.valueOf(spawnCount), Integer.valueOf(numTeams)));
        }
        if (numTeams != 0 && (mode instanceof Casual)) {
            Casual casual = (Casual) mode;
            if (casual.terrainSymmetry() != null && casual.terrainSymmetry().getNumSymPoints() % numTeams != 0) {
                throw new IllegalArgumentException("Terrain symmetry `%s` not compatible with Num Teams `%d`".formatted(casual.terrainSymmetry(), Integer.valueOf(numTeams)));
            }
        }
        this.seed = seed;
        this.spawnCount = spawnCount;
        this.mapSize = mapSize;
        this.numTeams = numTeams;
        this.mode = mode;
    }

    public boolean allowDebug() {
        return this.mode instanceof Casual;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public SplittableRandom createRandom() throws MatchException {
        Mode mode = this.mode;
        if (mode instanceof Competitive) {
            Competitive competitive = (Competitive) mode;
            try {
                long generationTime = competitive.generationTime();
                if (1 != 0) {
                    competitive.visibility();
                    return new SplittableRandom(new SplittableRandom(seed()).nextLong() ^ new SplittableRandom(generationTime).nextLong());
                }
            } catch (Throwable th) {
                throw new MatchException(th.toString(), th);
            }
        }
        return new SplittableRandom(seed());
    }

    @Override // java.lang.Record
    public String toString() {
        return "Seed: %d\nSpawns: %d\nMap Size: %d\nNum Teams: %d\n%s".formatted(Long.valueOf(this.seed), Integer.valueOf(this.spawnCount), Integer.valueOf(this.mapSize), Integer.valueOf(this.numTeams), this.mode);
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapNameParameters$Competitive.class */
    public static final class Competitive extends Record implements Mode {
        private final long generationTime;
        private final Visibility visibility;

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Competitive.class), Competitive.class, "generationTime;visibility", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Competitive;->generationTime:J", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Competitive;->visibility:Lcom/faforever/neroxis/generator/util/serial/Visibility;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Competitive.class, Object.class), Competitive.class, "generationTime;visibility", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Competitive;->generationTime:J", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Competitive;->visibility:Lcom/faforever/neroxis/generator/util/serial/Visibility;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public long generationTime() {
            return this.generationTime;
        }

        public Visibility visibility() {
            return this.visibility;
        }

        public Competitive(long generationTime, Visibility visibility) {
            if (generationTime != 0) {
                this.generationTime = generationTime;
                this.visibility = visibility;
                return;
            }
            throw new IllegalArgumentException("Generation Time must exist for competitive mode");
        }

        @Override // java.lang.Record
        public String toString() {
            Object[] objArr = new Object[2];
            objArr[0] = this.generationTime == 0 ? null : Instant.ofEpochSecond(this.generationTime);
            objArr[1] = this.visibility;
            return "Generation Time: %s\nVisibility: %s".formatted(objArr);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/util/serial/MapNameParameters$Casual.class */
    public static final class Casual extends Record implements Mode {
        private final Symmetry terrainSymmetry;
        private final MapStyle mapStyle;

        public Casual(Symmetry terrainSymmetry, MapStyle mapStyle) {
            this.terrainSymmetry = terrainSymmetry;
            this.mapStyle = mapStyle;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Casual.class), Casual.class, "terrainSymmetry;mapStyle", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Casual;->terrainSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Casual;->mapStyle:Lcom/faforever/neroxis/generator/util/serial/MapStyle;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Casual.class, Object.class), Casual.class, "terrainSymmetry;mapStyle", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Casual;->terrainSymmetry:Lcom/faforever/neroxis/map/Symmetry;", "FIELD:Lcom/faforever/neroxis/generator/util/serial/MapNameParameters$Casual;->mapStyle:Lcom/faforever/neroxis/generator/util/serial/MapStyle;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Symmetry terrainSymmetry() {
            return this.terrainSymmetry;
        }

        public MapStyle mapStyle() {
            return this.mapStyle;
        }

        @Override // java.lang.Record
        public String toString() {
            return "Terrain Symmetry: %s\nStyle: %s".formatted(this.terrainSymmetry, this.mapStyle);
        }
    }
}
