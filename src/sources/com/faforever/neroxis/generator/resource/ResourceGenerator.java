package com.faforever.neroxis.generator.resource;

import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.placement.HydroPlacer;
import com.faforever.neroxis.map.placement.MexPlacer;
import com.faforever.neroxis.mask.BooleanMask;
import java.util.random.RandomGenerator;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/resource/ResourceGenerator.class */
public abstract class ResourceGenerator {
    protected SCMap map;
    protected RandomGenerator.SplittableGenerator random;
    protected GeneratorParameters generatorParameters;
    protected SymmetrySettings symmetrySettings;
    protected MexPlacer mexPlacer;
    protected HydroPlacer hydroPlacer;
    protected BooleanMask unbuildable;
    protected BooleanMask passableLand;
    protected BooleanMask passableWater;
    protected BooleanMask resourceMask;
    protected BooleanMask waterResourceMask;
    protected BooleanMask mexDeadZone;
    protected int mexesPerPlayer;
    protected float resourceDensity = -1.0f;
    protected int hydroCountOverride = -1;

    public abstract void setupPipeline();

    public abstract void placeResources();

    protected abstract int getMexCount();

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
    public MexPlacer getMexPlacer() {
        return this.mexPlacer;
    }

    @Generated
    public HydroPlacer getHydroPlacer() {
        return this.hydroPlacer;
    }

    @Generated
    public BooleanMask getUnbuildable() {
        return this.unbuildable;
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
    public BooleanMask getResourceMask() {
        return this.resourceMask;
    }

    @Generated
    public BooleanMask getWaterResourceMask() {
        return this.waterResourceMask;
    }

    @Generated
    public BooleanMask getMexDeadZone() {
        return this.mexDeadZone;
    }

    @Generated
    public float getResourceDensity() {
        return this.resourceDensity;
    }

    @Generated
    public int getMexesPerPlayer() {
        return this.mexesPerPlayer;
    }

    @Generated
    public int getHydroCountOverride() {
        return this.hydroCountOverride;
    }

    public void setMexesPerPlayer(int mexesPerPlayer) {
        this.mexesPerPlayer = mexesPerPlayer;
    }

    public void setHydroCount(int hydroCount) {
        this.hydroCountOverride = hydroCount;
    }

    protected int hydroCount() {
        return this.hydroCountOverride >= 0 ? this.hydroCountOverride : this.generatorParameters.spawnCount();
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void placeHydros() {
        int hydroCount = hydroCount();
        this.hydroPlacer.placeHydros(hydroCount, ((BooleanMask) this.resourceMask.getFinalMask()).deflate(8));
        if (this.hydroCountOverride > 0 && this.map.getHydroCount() < hydroCount) {
            this.hydroPlacer.placeRemainingHydros(hydroCount, (BooleanMask) this.resourceMask.getFinalMask());
        }
    }

    public void setResourceDensity(float resourceDensity) {
        if (this.resourceDensity != -1.0f) {
            throw new IllegalStateException("resource density has already been set");
        }
        if (resourceDensity < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || resourceDensity > 1.0f) {
            throw new IllegalArgumentException("resource density must be between 0 and 1, was %f".formatted(Float.valueOf(resourceDensity)));
        }
        this.resourceDensity = resourceDensity;
    }

    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings, TerrainGenerator terrainGenerator) {
        this.map = map;
        this.random = random.split();
        this.generatorParameters = generatorParameters;
        this.symmetrySettings = symmetrySettings;
        this.passableWater = new BooleanMask(1, random.split(), symmetrySettings, "passableWater");
        this.unbuildable = new BooleanMask(1, random.split(), symmetrySettings, "unbuildable");
        this.passableLand = new BooleanMask(1, random.split(), symmetrySettings, "passableLand");
        this.resourceMask = new BooleanMask(1, random.split(), symmetrySettings, "resourceMask");
        this.waterResourceMask = new BooleanMask(1, random.split(), symmetrySettings, "waterResourceMask");
        this.mexDeadZone = new BooleanMask(1, random.split(), symmetrySettings, "mexDeadZone");
        this.passableWater.init(terrainGenerator.getPassableWater());
        this.unbuildable.init(terrainGenerator.getUnbuildable());
        this.passableLand.init(terrainGenerator.getPassableLand());
        this.mexDeadZone.init(terrainGenerator.getMexDeadZone());
        this.mexPlacer = new MexPlacer(map, random.split());
        if (this.mexesPerPlayer > 0) {
            this.mexPlacer.setMexesPerPlayer(this.mexesPerPlayer, this.mexesPerPlayer);
        }
        this.hydroPlacer = new HydroPlacer(map, random.split());
        if (this.resourceDensity == -1.0f) {
            setResourceDensity(random.nextFloat());
        }
    }
}
