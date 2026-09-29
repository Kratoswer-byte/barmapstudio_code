package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.FractalFlattenParams;
import com.faforever.neroxis.generator.FractalParams;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.MapMaskMethods;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.Set;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/FractalNoiseLastTerrainGenerator.class */
public class FractalNoiseLastTerrainGenerator extends MultiLevelLastTerrainGenerator {
    private BooleanMask symmetryLines;
    protected FloatMask symmetryCliffs;
    protected FloatMask rampNoise;
    protected FloatMask rawMountains;
    protected FractalParams fractalParams;

    @Override // com.faforever.neroxis.generator.terrain.MultiLevelLastTerrainGenerator, com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator, com.faforever.neroxis.generator.terrain.SpawnLastTerrainGenerator, com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        super.initialize(map, random, generatorParameters, symmetrySettings);
        this.symmetryLines = new BooleanMask(1, random.split(), symmetrySettings, "symmetryLines");
        this.symmetryCliffs = new FloatMask(1, random.split(), symmetrySettings, "symmetryCliffs");
        this.rampNoise = new FloatMask(1, random.split(), symmetrySettings, "rampNoise");
        this.rawMountains = new FloatMask(1, random.split(), symmetrySettings, "rawMountains");
        this.rampNoise.setSize((int) (32.0f * (map.getSize() / 1024.0f)));
        this.rampNoise.addWhiteNoise(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f);
        this.rampNoise.setSize(map.getSize() + 1);
        this.waterHeight = map.getBiome().waterSettings().elevation();
        this.waterMask = this.fractalParams.fractalWaterMask();
        this.noiseSmallestDetail = this.fractalParams.noiseSmallestDetail().intValue();
        this.noiseOctaveMultiplier = this.fractalParams.noiseOctaveMultiplier().floatValue();
        this.noiseMapBlurAmount = this.fractalParams.noiseMapBlurAmount().intValue();
        this.noiseScaleMaxToValue = 50;
        this.mountainBrushSize = 24;
        this.mountainBrushDensity = 8.0f;
        this.mountainBrushIntensity = 3.0f;
        this.symmetryLines.setSize(map.getSize() + 1);
        this.symmetryLines.drawSymmetryLines(symmetrySettings.terrainSymmetry());
    }

    @Override // com.faforever.neroxis.generator.terrain.MultiLevelLastTerrainGenerator, com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void landSetup() {
        super.landSetup();
        this.landNoiseMap.scaleToNewMinAndMaxHeight(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f);
        this.landNoiseMap.scaleExponentially(this.fractalParams.noiseExpMultiplier().floatValue());
        this.landNoiseMap.scaleToNewMinAndMaxHeight(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, this.noiseScaleMaxToValue);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void mountainSetup() {
        this.symmetryCliffs = (FloatMask) this.landNoiseMap.copy();
        this.symmetryCliffs.supcomGradient();
        this.symmetryCliffs.setToValue(((BooleanMask) this.symmetryLines.copy()).inflate(3).invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
        ((BooleanMask) this.mountains.setSize(this.map.getSize() + 1)).set((x, y) -> {
            return true;
        });
    }

    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void setupMountainHeightmapPipeline() {
        this.rawMountains.setSize(this.map.getSize() + 1);
        this.mountains.init(this.rawMountains.copyAsBooleanMask(Float.valueOf(this.plateauHeight / 2.0f)));
    }

    @Override // com.faforever.neroxis.generator.terrain.MultiLevelLastTerrainGenerator, com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void initRamps() {
        this.ramps.setSize(this.landNoiseMap.getSize());
        for (FractalFlattenParams fractalFlattenParams : this.fractalParams.fractalFlattenParams()) {
            if (fractalFlattenParams.hasRamps().booleanValue()) {
                BooleanMask layer = this.landNoiseMap.copyAsBooleanMask(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), fractalFlattenParams.maxHeight());
                layer.outline();
                layer.subtract(this.rampNoise.copyAsBooleanMask(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(1.0f - fractalFlattenParams.rampPercentage().floatValue())));
                this.ramps.add(layer);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.MultiLevelLastTerrainGenerator, com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void blurRamps() {
        BooleanMask inflatedRamps = (BooleanMask) this.ramps.copy();
        ((FloatMask) this.heightmap.blur(48, inflatedRamps).blur(32, inflatedRamps.inflate(2)).blur(4, inflatedRamps.inflate(4)).blur(4, inflatedRamps.inflate(8)).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).clampMax(Float.valueOf(255.0f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.MultiLevelLastTerrainGenerator, com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void setupHeightmapPipeline() {
        int mapSize = this.map.getSize();
        initRamps();
        this.heightmap.setSize(mapSize + 1);
        this.heightmapLand.setSize(mapSize + 1);
        this.heightMapNoise.setSize(mapSize / 128);
        this.heightmapLand.add(this.landNoiseMap);
        for (FractalFlattenParams fractalFlattenParams : this.fractalParams.fractalFlattenParams()) {
            MapMaskMethods.flattenHeightBand(this.heightmapLand, this.landNoiseMap, fractalFlattenParams.minHeight().floatValue(), fractalFlattenParams.maxHeight().floatValue(), fractalFlattenParams.destinationMinHeight().floatValue(), fractalFlattenParams.destinationMaxHeight().floatValue(), fractalFlattenParams.slope().floatValue());
        }
        for (FractalFlattenParams fractalFlattenParams2 : this.fractalParams.fractalFlattenParams()) {
            if (fractalFlattenParams2.edgeBlur().intValue() > 0) {
                BooleanMask flattenMaskOutline = this.landNoiseMap.copyAsBooleanMask(fractalFlattenParams2.minHeight(), fractalFlattenParams2.maxHeight()).outline().inflate(fractalFlattenParams2.edgeBlur().intValue());
                this.heightmapLand.blur(fractalFlattenParams2.edgeBlur().intValue(), flattenMaskOutline);
            }
        }
        this.heightmap.add(this.heightmapLand);
        if (Set.of((Object[]) new Symmetry[]{Symmetry.POINT2, Symmetry.POINT3, Symmetry.POINT4, Symmetry.POINT5, Symmetry.POINT6, Symmetry.POINT7, Symmetry.POINT8, Symmetry.POINT9, Symmetry.POINT10, Symmetry.POINT11, Symmetry.POINT12, Symmetry.POINT13, Symmetry.POINT14, Symmetry.POINT15, Symmetry.POINT16}).contains(this.symmetrySettings.terrainSymmetry())) {
            this.heightmapMountains.setSize(this.map.getSize() + 1);
            this.heightmapMountains.useBrushWithCliffMap(this.symmetryCliffs, this.mountainBrushSize);
            this.heightmapMountains.scaleToNewMinAndMaxHeight(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, this.noiseScaleMaxToValue - 5.0f);
            this.heightmapMountains.set((x, y) -> {
                return Float.valueOf(this.heightmapMountains.get(x, y).floatValue() <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? -128.0f : this.heightmapMountains.get(x, y).floatValue());
            });
            this.heightmapLand.blur(3, ((BooleanMask) this.symmetryLines.copy()).inflate(10));
            this.heightmap.max(this.heightmapMountains);
            this.heightmap.blur(1, ((BooleanMask) this.symmetryLines.copy()).inflate(10));
        }
        setupMountainHeightmapPipeline();
        this.heightmap.add(this.rawMountains);
        this.heightmap.add(Float.valueOf(this.waterHeight - this.fractalParams.waterHeight().floatValue()));
        if (!this.symmetrySettings.spawnSymmetry().isPerfectSymmetry()) {
            BooleanMask outerCircle = new BooleanMask(mapSize + 1, this.random.split(), this.symmetrySettings, "outerCircle");
            outerCircle.fillCircle(new Vector2(mapSize / 2.0f, mapSize / 2.0f), mapSize / 2.0f, true);
            outerCircle.invert();
            this.heightmap.setToValue(outerCircle, Float.valueOf(this.waterHeight));
            this.heightmap.blur(5, outerCircle.outline().inflate(5));
        }
        blurRamps();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.MultiLevelLastTerrainGenerator, com.faforever.neroxis.generator.terrain.SpawnLastTerrainGenerator
    protected void setupSpawnMaskPipeline() {
        for (FractalFlattenParams fractalFlattenParams : this.fractalParams.fractalFlattenParams()) {
            if (fractalFlattenParams.spawnable().booleanValue()) {
                this.spawnMask.add(this.landNoiseMap.copyAsBooleanMask(fractalFlattenParams.minHeight(), fractalFlattenParams.maxHeight()).deflate(fractalFlattenParams.spawnMaskDeflate().intValue()));
            }
        }
        ((BooleanMask) this.spawnMask.subtract(this.unbuildable).fillCenter(this.map.getSize() / 3, false)).deflate(this.fractalParams.spawnMaskDeflate().intValue());
    }

    @Override // com.faforever.neroxis.generator.terrain.TerrainGenerator
    public int getTeamSeparation() {
        if (this.generatorParameters.numTeams() < 2) {
            return 0;
        }
        if (this.generatorParameters.numTeams() == 2) {
            return this.map.getSize() / this.fractalParams.teamSeparation().intValue();
        }
        return StrictMath.min(this.map.getSize() / this.generatorParameters.numTeams(), PreviewGenerator.PREVIEW_SIZE);
    }
}
