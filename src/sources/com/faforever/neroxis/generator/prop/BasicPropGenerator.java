package com.faforever.neroxis.generator.prop;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.DebugUtil;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/prop/BasicPropGenerator.class */
public class BasicPropGenerator extends PropGenerator {
    protected BooleanMask treeMask;
    protected BooleanMask cliffRockMask;
    protected BooleanMask fieldStoneMask;

    @Override // com.faforever.neroxis.generator.prop.PropGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings, TerrainGenerator terrainGenerator) {
        super.initialize(map, random, generatorParameters, symmetrySettings, terrainGenerator);
        this.treeMask = new BooleanMask(1, random.split(), symmetrySettings, "treeMask");
        this.cliffRockMask = new BooleanMask(1, random.split(), symmetrySettings, "cliffRockMask");
        this.fieldStoneMask = new BooleanMask(1, random.split(), symmetrySettings, "fieldStoneMask");
    }

    @Override // com.faforever.neroxis.generator.prop.PropGenerator
    public void placeProps() {
        placePropsWithExclusion(generatePropExclusionMasks());
    }

    @Override // com.faforever.neroxis.generator.prop.PropGenerator
    public void placeUnits() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected BooleanMask generatePropExclusionMasks() {
        BooleanMask noProps = new BooleanMask(1, (RandomGenerator.SplittableGenerator) null, this.symmetrySettings, "noProps");
        noProps.init((BooleanMask) this.unbuildable.getFinalMask());
        generateExclusionZones(noProps, 30.0f, 2.0f, 8.0f);
        return noProps;
    }

    protected void generateExclusionZones(BooleanMask mask, float spawnSpacing, float mexSpacing, float hydroSpacing) {
        this.map.getSpawns().forEach(spawn -> {
            mask.fillCircle(spawn.getPosition(), spawnSpacing, true);
        });
        this.map.getMexes().forEach(mex -> {
            mask.fillCircle(mex.getPosition(), mexSpacing, true);
        });
        this.map.getHydros().forEach(hydro -> {
            mask.fillCircle(hydro.getPosition(), hydroSpacing, true);
        });
    }

    public void placePropsWithExclusion(BooleanMask noProps) {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "placeProps", () -> {
            Biome biome = this.map.getBiome();
            this.propPlacer.placeProps(((BooleanMask) this.treeMask.getFinalMask()).subtract(noProps), biome.propMaterials().treeGroups(), 3.0f, 7.0f, false);
            this.propPlacer.placeProps((BooleanMask) this.cliffRockMask.getFinalMask(), biome.propMaterials().rocks(), 0.5f, 2.5f, false);
            this.propPlacer.placeProps(((BooleanMask) this.fieldStoneMask.getFinalMask()).subtract(noProps), biome.propMaterials().boulders(), 30.0f, true);
        });
    }

    @Override // com.faforever.neroxis.generator.prop.PropGenerator
    public void setupPipeline() {
        setupPropPipeline();
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupPropPipeline() {
        int mapSize = this.map.getSize();
        this.treeMask.setSize(mapSize / 16);
        this.cliffRockMask.setSize(mapSize / 16);
        this.fieldStoneMask.setSize(mapSize / 4);
        this.cliffRockMask.randomize(((this.reclaimDensity * 0.75f) + (this.random.nextFloat() * 0.25f)) * 0.5f).setSize(mapSize + 1);
        this.cliffRockMask.multiply(this.impassable).dilute(0.5f, 6).subtract(this.impassable).multiply(this.passableLand);
        this.fieldStoneMask.randomize(((this.reclaimDensity + this.random.nextFloat()) / 2.0f) * 0.0025f).setSize(mapSize + 1);
        this.fieldStoneMask.multiply(this.passableLand).fillEdge(10, false);
        this.treeMask.randomize(((this.reclaimDensity + this.random.nextFloat()) / 2.0f) * 0.15f).setSize(mapSize / 4);
        this.treeMask.inflate(2).erode(0.5f);
        this.treeMask.setSize(mapSize + 1);
        this.treeMask.multiply(((BooleanMask) this.passableLand.copy()).deflate(8)).fillEdge(8, false);
    }
}
