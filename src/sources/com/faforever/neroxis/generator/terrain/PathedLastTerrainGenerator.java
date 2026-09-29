package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.MapMaskMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/PathedLastTerrainGenerator.class */
public abstract class PathedLastTerrainGenerator extends BasicLastTerrainGenerator {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
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
