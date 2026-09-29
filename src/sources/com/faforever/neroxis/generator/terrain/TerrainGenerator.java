package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.util.DebugUtil;
import java.util.random.RandomGenerator;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/TerrainGenerator.class */
public abstract class TerrainGenerator {
    protected SCMap map;
    protected RandomGenerator.SplittableGenerator random;
    protected GeneratorParameters generatorParameters;
    protected SymmetrySettings symmetrySettings;
    protected FloatMask heightmap;
    protected BooleanMask impassable;
    protected BooleanMask unbuildable;
    protected BooleanMask passable;
    protected BooleanMask passableLand;
    protected BooleanMask passableWater;
    protected BooleanMask mexDeadZone;
    protected FloatMask slope;

    public abstract void setupPipeline();

    public abstract void placeSpawns();

    @Generated
    public SCMap getMap() {
        return this.map;
    }

    @Generated
    public RandomGenerator.SplittableGenerator getRandom() {
        return this.random;
    }

    @Generated
    public GeneratorParameters getGeneratorParameters() {
        return this.generatorParameters;
    }

    @Generated
    public SymmetrySettings getSymmetrySettings() {
        return this.symmetrySettings;
    }

    @Generated
    public FloatMask getHeightmap() {
        return this.heightmap;
    }

    @Generated
    public BooleanMask getImpassable() {
        return this.impassable;
    }

    @Generated
    public BooleanMask getUnbuildable() {
        return this.unbuildable;
    }

    @Generated
    public BooleanMask getPassable() {
        return this.passable;
    }

    @Generated
    public BooleanMask getPassableLand() {
        return this.passableLand;
    }

    @Generated
    public BooleanMask getPassableWater() {
        return this.passableWater;
    }

    @Generated
    public BooleanMask getMexDeadZone() {
        return this.mexDeadZone;
    }

    @Generated
    public FloatMask getSlope() {
        return this.slope;
    }

    public void setHeightmapImage() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "setHeightMap", () -> {
            return ((FloatMask) this.heightmap.getFinalMask()).writeToImage(this.map.getHeightmap(), 1.0f / this.map.getHeightMapScale());
        });
    }

    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        this.map = map;
        this.random = random.split();
        this.generatorParameters = generatorParameters;
        this.symmetrySettings = symmetrySettings;
        this.heightmap = new FloatMask(map.getSize() + 1, random.split(), symmetrySettings, "heightmap");
        this.slope = new FloatMask(map.getSize() + 1, random.split(), symmetrySettings, "slope");
        this.impassable = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "impassable");
        this.unbuildable = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "unbuildable");
        this.passable = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "passable");
        this.passableLand = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "passableLand");
        this.passableWater = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "passableWater");
        this.mexDeadZone = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "mexDeadZone");
    }

    public float getSpawnSeparation() {
        if (this.generatorParameters.numTeams() < 2) {
            return (this.generatorParameters.mapSize() / this.generatorParameters.spawnCount()) * 1.5f;
        }
        if (this.generatorParameters.numTeams() == 2) {
            return this.random.nextInt((this.map.getSize() / 4) - (this.map.getSize() / 16)) + (this.map.getSize() / 16.0f);
        }
        if (this.generatorParameters.numTeams() < 8) {
            return this.random.nextInt(((this.map.getSize() / 2) / this.generatorParameters.numTeams()) - (this.map.getSize() / 16)) + (this.map.getSize() / 16.0f);
        }
        return SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
    }

    protected int getTeamSeparation() {
        if (this.generatorParameters.numTeams() < 2) {
            return 0;
        }
        if (this.generatorParameters.numTeams() == 2) {
            return this.map.getSize() / 2;
        }
        return StrictMath.min(this.map.getSize() / this.generatorParameters.numTeams(), PreviewGenerator.PREVIEW_SIZE);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupPassablePipeline() {
        BooleanMask actualLand = this.heightmap.copyAsBooleanMask(Float.valueOf(this.map.getBiome().waterSettings().elevation()));
        this.slope.init(((FloatMask) this.heightmap.copy()).supcomGradient());
        this.impassable.init(this.slope, Float.valueOf(0.7f));
        this.unbuildable.init(this.slope, Float.valueOf(0.05f));
        this.impassable.inflate(4);
        ((BooleanMask) this.passable.init(this.impassable)).invert();
        this.passableLand.init(actualLand);
        ((BooleanMask) this.passableWater.init(actualLand)).invert();
        this.passable.fillEdge(8, false);
        this.passableLand.multiply(this.passable);
        this.passableWater.deflate(16).fillEdge(8, false);
    }
}
