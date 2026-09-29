package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.MapMaskMethods;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/PathedTerrainGenerator.class */
public abstract class PathedTerrainGenerator extends BasicTerrainGenerator {
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator, com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        super.initialize(map, random, generatorParameters, symmetrySettings);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void spawnTerrainSetup() {
        int mapSize = this.map.getSize();
        this.spawnPlateauMask.setSize(mapSize / 4);
        this.spawnPlateauMask.erode(0.5f, 4).dilute(0.5f, 8);
        this.spawnPlateauMask.erode(0.5f).setSize(mapSize + 1);
        this.spawnPlateauMask.blur(4);
        this.spawnLandMask.setSize(mapSize / 4);
        this.spawnLandMask.erode(0.25f, mapSize / 128).dilute(0.5f, 4);
        this.spawnLandMask.erode(0.5f).setSize(mapSize + 1);
        this.spawnLandMask.blur(4);
        this.plateaus.subtract(this.spawnLandMask).add(this.spawnPlateauMask);
        this.land.add(this.spawnLandMask).add(this.spawnPlateauMask);
        ensureSpawnTerrain();
        this.mountains.multiply(((BooleanMask) this.land.copy()).deflate(24));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void initRamps() {
        int mapSize = this.map.getSize();
        int maxStepSize = mapSize / 128;
        int numPaths = ((int) (this.rampDensity * 20.0f)) / this.symmetrySettings.terrainSymmetry().getNumSymPoints();
        int bound = mapSize / 4;
        this.ramps.setSize(mapSize + 1);
        MapMaskMethods.pathInEdgeBounds(this.random.split(), this.ramps, maxStepSize, numPaths, 2, bound, 1.5707964f);
        MapMaskMethods.pathInCenterBounds(this.random.split(), this.ramps, maxStepSize, numPaths / 2, 2, bound, 1.5707964f);
        this.ramps.subtract(((BooleanMask) this.connections.copy()).inflate(32)).inflate(maxStepSize / 2).multiply(((BooleanMask) this.plateaus.copy()).outline()).add(((BooleanMask) this.connections.copy()).inflate(maxStepSize / 2).multiply(((BooleanMask) this.plateaus.copy()).outline())).subtract(this.mountains).inflate(8);
    }
}
