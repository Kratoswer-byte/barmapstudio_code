package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/bar/BarExportOptions.class */
public final class BarExportOptions extends Record {
    private final int squaresPerCell;
    private final float heightScale;
    private final float baseMetal;
    private final float minMetal;
    private final float maxMetal;
    private final boolean uniformMetal;
    private final int numTeams;
    private final String mapName;
    private final String mapVersion;
    private final String author;
    private final float reclaim;
    private final Path featuresRepository;
    private final int minWind;
    private final int maxWind;
    private final int tidal;
    public static final BarExportOptions DEFAULT = new BarExportOptions(2, 0.7f, 2.0f, 2.0f, 3.5f, false, 0, null, "1.0", "Neroxis Map Generator", 1.0f, null, 3, 16, 18);

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BarExportOptions.class), BarExportOptions.class, "squaresPerCell;heightScale;baseMetal;minMetal;maxMetal;uniformMetal;numTeams;mapName;mapVersion;author;reclaim;featuresRepository;minWind;maxWind;tidal", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->squaresPerCell:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->heightScale:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->baseMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->minMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->maxMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->uniformMetal:Z", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->numTeams:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->mapVersion:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->reclaim:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->featuresRepository:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->minWind:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->maxWind:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->tidal:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BarExportOptions.class), BarExportOptions.class, "squaresPerCell;heightScale;baseMetal;minMetal;maxMetal;uniformMetal;numTeams;mapName;mapVersion;author;reclaim;featuresRepository;minWind;maxWind;tidal", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->squaresPerCell:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->heightScale:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->baseMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->minMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->maxMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->uniformMetal:Z", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->numTeams:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->mapVersion:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->reclaim:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->featuresRepository:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->minWind:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->maxWind:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->tidal:I").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BarExportOptions.class, Object.class), BarExportOptions.class, "squaresPerCell;heightScale;baseMetal;minMetal;maxMetal;uniformMetal;numTeams;mapName;mapVersion;author;reclaim;featuresRepository;minWind;maxWind;tidal", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->squaresPerCell:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->heightScale:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->baseMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->minMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->maxMetal:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->uniformMetal:Z", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->numTeams:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->mapVersion:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->reclaim:F", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->featuresRepository:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->minWind:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->maxWind:I", "FIELD:Lcom/faforever/neroxis/exporter/bar/BarExportOptions;->tidal:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public int squaresPerCell() {
        return this.squaresPerCell;
    }

    public float heightScale() {
        return this.heightScale;
    }

    public float baseMetal() {
        return this.baseMetal;
    }

    public float minMetal() {
        return this.minMetal;
    }

    public float maxMetal() {
        return this.maxMetal;
    }

    public boolean uniformMetal() {
        return this.uniformMetal;
    }

    public int numTeams() {
        return this.numTeams;
    }

    public String mapName() {
        return this.mapName;
    }

    public String mapVersion() {
        return this.mapVersion;
    }

    public String author() {
        return this.author;
    }

    public float reclaim() {
        return this.reclaim;
    }

    public Path featuresRepository() {
        return this.featuresRepository;
    }

    public int minWind() {
        return this.minWind;
    }

    public int maxWind() {
        return this.maxWind;
    }

    public int tidal() {
        return this.tidal;
    }

    public BarExportOptions(int squaresPerCell, float heightScale, float baseMetal, float minMetal, float maxMetal, boolean uniformMetal, int numTeams, String mapName, String mapVersion, String author, float reclaim, Path featuresRepository) {
        this(squaresPerCell, heightScale, baseMetal, minMetal, maxMetal, uniformMetal, numTeams, mapName, mapVersion, author, reclaim, featuresRepository, 3, 16, 18);
    }

    public BarExportOptions(int squaresPerCell, float heightScale, float baseMetal, float minMetal, float maxMetal, boolean uniformMetal, int numTeams, String mapName, String mapVersion, String author, float reclaim, Path featuresRepository, int minWind, int maxWind, int tidal) {
        if (squaresPerCell < 1 || squaresPerCell > 4) {
            throw new IllegalArgumentException("squaresPerCell must be between 1 and 4");
        }
        if (reclaim < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            throw new IllegalArgumentException("reclaim must not be negative");
        }
        if (heightScale <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            throw new IllegalArgumentException("heightScale must be positive");
        }
        if (minMetal < 0.1f || maxMetal < minMetal || maxMetal > 10.0f) {
            throw new IllegalArgumentException("the metal range must be ordered and between 0.1 and 10");
        }
        if (mapName != null && !mapName.matches("[A-Za-z0-9][A-Za-z0-9 _.-]{1,40}")) {
            throw new IllegalArgumentException("the map name may only contain letters, digits, spaces and _ . -");
        }
        if (baseMetal < minMetal || baseMetal > maxMetal) {
            throw new IllegalArgumentException("the base metal income must be inside the metal range");
        }
        if (minWind < 0 || maxWind < minWind || maxWind > 50) {
            throw new IllegalArgumentException("the wind range must be ordered and between 0 and 50");
        }
        if (tidal >= 0 && tidal <= 100) {
            this.squaresPerCell = squaresPerCell;
            this.heightScale = heightScale;
            this.baseMetal = baseMetal;
            this.minMetal = minMetal;
            this.maxMetal = maxMetal;
            this.uniformMetal = uniformMetal;
            this.numTeams = numTeams;
            this.mapName = mapName;
            this.mapVersion = mapVersion;
            this.author = author;
            this.reclaim = reclaim;
            this.featuresRepository = featuresRepository;
            this.minWind = minWind;
            this.maxWind = maxWind;
            this.tidal = tidal;
            return;
        }
        throw new IllegalArgumentException("the tidal strength must be between 0 and 100");
    }
}
