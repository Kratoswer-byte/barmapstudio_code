package com.faforever.neroxis.generator.prop;

import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.placement.PropPlacer;
import com.faforever.neroxis.map.placement.UnitPlacer;
import com.faforever.neroxis.mask.BooleanMask;
import java.util.random.RandomGenerator;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/prop/PropGenerator.class */
public abstract class PropGenerator {
    protected SCMap map;
    protected RandomGenerator.SplittableGenerator random;
    protected GeneratorParameters generatorParameters;
    protected SymmetrySettings symmetrySettings;
    protected UnitPlacer unitPlacer;
    protected PropPlacer propPlacer;
    protected BooleanMask impassable;
    protected BooleanMask unbuildable;
    protected BooleanMask passableLand;
    protected float reclaimDensity = -1.0f;

    public abstract void setupPipeline();

    public abstract void placeProps();

    public abstract void placeUnits();

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
    public UnitPlacer getUnitPlacer() {
        return this.unitPlacer;
    }

    @Generated
    public PropPlacer getPropPlacer() {
        return this.propPlacer;
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
    public BooleanMask getPassableLand() {
        return this.passableLand;
    }

    @Generated
    public float getReclaimDensity() {
        return this.reclaimDensity;
    }

    public void setReclaimDensity(float reclaimDensity) {
        if (this.reclaimDensity != -1.0f) {
            throw new IllegalStateException("resource density has already been set");
        }
        if (reclaimDensity < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || reclaimDensity > 1.0f) {
            throw new IllegalArgumentException("reclaim density must be between 0 and 1, was %f".formatted(Float.valueOf(reclaimDensity)));
        }
        this.reclaimDensity = reclaimDensity;
    }

    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings, TerrainGenerator terrainGenerator) {
        this.map = map;
        this.random = random.split();
        this.generatorParameters = generatorParameters;
        this.symmetrySettings = symmetrySettings;
        this.impassable = new BooleanMask(1, random.split(), symmetrySettings, "impassable");
        this.unbuildable = new BooleanMask(1, random.split(), symmetrySettings, "unbuildable");
        this.passableLand = new BooleanMask(1, random.split(), symmetrySettings, "passableLand");
        this.impassable.init(terrainGenerator.getImpassable());
        this.unbuildable.init(terrainGenerator.getUnbuildable());
        this.passableLand.init(terrainGenerator.getPassableLand());
        this.unitPlacer = new UnitPlacer(random.split());
        this.propPlacer = new PropPlacer(map, random.split());
        if (this.reclaimDensity == -1.0f) {
            setReclaimDensity(random.nextFloat());
        }
    }
}
