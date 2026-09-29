package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.util.SpawnPlacementException;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.placement.SpawnPlacer;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.DebugUtil;
import java.util.random.RandomGenerator;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/SpawnLastTerrainGenerator.class */
public abstract class SpawnLastTerrainGenerator extends TerrainGenerator {
    protected BooleanMask spawnMask;
    private BooleanMask spawnWaterMask;
    private SpawnPlacer spawnPlacer;

    protected abstract void setupTerrainPipeline();

    @Generated
    public BooleanMask getSpawnMask() {
        return this.spawnMask;
    }

    @Generated
    public BooleanMask getSpawnWaterMask() {
        return this.spawnWaterMask;
    }

    @Generated
    public SpawnPlacer getSpawnPlacer() {
        return this.spawnPlacer;
    }

    protected int getMinTeammateSeparation() {
        return this.map.getSize() / 8;
    }

    protected int getMaxTeammateSeparation() {
        return this.map.getSize() / 4;
    }

    @Override // com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        super.initialize(map, random, generatorParameters, symmetrySettings);
        this.spawnMask = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "spawnMask");
        this.spawnWaterMask = new BooleanMask(map.getSize() + 1, random.split(), symmetrySettings, "spawnWaterMask");
        this.spawnPlacer = new SpawnPlacer(map, random.split());
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupSpawnMaskPipeline() {
        this.spawnWaterMask.init(((BooleanMask) this.unbuildable.copy()).invert().deflate(8));
        ((BooleanMask) this.spawnMask.init(this.spawnWaterMask)).multiply(this.heightmap.copyAsBooleanMask(Float.valueOf(this.map.getBiome().waterSettings().elevation())));
    }

    @Override // com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void placeSpawns() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "placeSpawns", () -> {
            if (this.spawnPlacer.placeSpawns(this.generatorParameters.spawnCount(), (BooleanMask) this.spawnMask.getFinalMask(), getMinTeammateSeparation(), getMaxTeammateSeparation(), getTeamSeparation())) {
            } else {
                throw new SpawnPlacementException("Unable to place all spawns");
            }
        });
    }

    @Override // com.faforever.neroxis.generator.terrain.TerrainGenerator
    public final void setupPipeline() {
        setupTerrainPipeline();
        this.heightmap.forceSymmetry();
        setupPassablePipeline();
        setupSpawnMaskPipeline();
    }
}
