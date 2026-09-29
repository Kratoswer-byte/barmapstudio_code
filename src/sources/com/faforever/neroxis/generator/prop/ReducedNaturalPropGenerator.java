package com.faforever.neroxis.generator.prop;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.DebugUtil;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/prop/ReducedNaturalPropGenerator.class */
public abstract class ReducedNaturalPropGenerator extends BasicPropGenerator {
    @Override // com.faforever.neroxis.generator.prop.BasicPropGenerator
    public void placePropsWithExclusion(BooleanMask noProps) {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "placeProps", () -> {
            Biome biome = this.map.getBiome();
            this.propPlacer.placeProps(((BooleanMask) this.treeMask.getFinalMask()).subtract(noProps), biome.propMaterials().treeGroups(), 3.0f, 7.0f, false);
            this.propPlacer.placeProps((BooleanMask) this.cliffRockMask.getFinalMask(), biome.propMaterials().rocks(), 0.5f, 3.5f, false);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.prop.BasicPropGenerator
    protected void setupPropPipeline() {
        int mapSize = this.map.getSize();
        this.treeMask.setSize(mapSize / 16);
        this.cliffRockMask.setSize(mapSize / 16);
        this.cliffRockMask.randomize(((this.reclaimDensity * 0.75f) + (this.random.nextFloat() * 0.25f)) * 0.25f).setSize(mapSize + 1);
        this.cliffRockMask.multiply(this.impassable).dilute(0.5f, 6).subtract(this.impassable).multiply(this.passableLand);
        this.treeMask.randomize(((this.reclaimDensity + this.random.nextFloat()) / 2.0f) * 0.1f).setSize(mapSize / 4);
        this.treeMask.inflate(2).erode(0.5f);
        this.treeMask.setSize(mapSize + 1);
        this.treeMask.multiply(((BooleanMask) this.passableLand.copy()).deflate(8)).fillEdge(8, false);
    }
}
