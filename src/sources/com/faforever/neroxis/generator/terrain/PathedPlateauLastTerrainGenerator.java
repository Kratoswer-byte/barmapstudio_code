package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.mask.MapMaskMethods;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/PathedPlateauLastTerrainGenerator.class */
public abstract class PathedPlateauLastTerrainGenerator extends BasicLastTerrainGenerator {
    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void plateausSetup() {
        int mapSize = this.map.getSize();
        int maxStepSize = mapSize / 128;
        int numPaths = ((int) (12.0f * this.plateauDensity)) / this.symmetrySettings.spawnSymmetry().getNumSymPoints();
        this.plateaus.setSize(mapSize + 1);
        MapMaskMethods.pathInCenterBounds(this.random.split(), this.plateaus, maxStepSize, numPaths, 16, 0, 1.5707964f);
        this.plateaus.inflate(mapSize / PreviewGenerator.PREVIEW_SIZE).setSize(mapSize / 4);
        this.plateaus.dilute(0.5f, 4).setSize(mapSize + 1);
        this.plateaus.blur(12);
    }
}
