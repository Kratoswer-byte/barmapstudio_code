package com.faforever.neroxis.generator.terrain;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/SpawnFirstTerrainGenerator.class */
public abstract class SpawnFirstTerrainGenerator extends TerrainGenerator {
    protected abstract void setupTerrainPipeline();

    @Override // com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void placeSpawns() {
    }

    @Override // com.faforever.neroxis.generator.terrain.TerrainGenerator
    public final void setupPipeline() {
        setupTerrainPipeline();
        this.heightmap.forceSymmetry();
        setupPassablePipeline();
    }
}
