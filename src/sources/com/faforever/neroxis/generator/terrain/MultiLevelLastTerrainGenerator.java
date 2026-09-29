package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.brushes.Brushes;
import com.faforever.neroxis.generator.FractalWaterMasks;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/MultiLevelLastTerrainGenerator.class */
public class MultiLevelLastTerrainGenerator extends BasicLastTerrainGenerator {
    protected FractalWaterMasks waterMask;
    protected BooleanMask waterArea;
    protected FloatMask waterAreaBlur;
    protected BooleanMask waterAreaMinusIsland;
    protected BooleanMask bridgeLandArea;
    protected BooleanMask secondLevelLand;
    protected BooleanMask thirdLevelLand;
    protected FloatMask landNoiseMap;
    protected int noiseSmallestDetail;
    protected float noiseOctaveMultiplier;
    protected int noiseMapBlurAmount;
    protected int maxNoiseOctaves;
    protected int noiseScaleMaxToValue;
    protected float landNoiseMapFirstLevel;
    protected float landNoiseMapSecondLevel;
    protected float landNoiseMapThirdLevel;
    protected FloatMask rampExclusion;

    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator, com.faforever.neroxis.generator.terrain.SpawnLastTerrainGenerator, com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        super.initialize(map, random, generatorParameters, symmetrySettings);
        this.landNoiseMap = new FloatMask(1, random.split(), this.land.getSymmetrySettings(), "landNoiseMap");
        this.secondLevelLand = new BooleanMask(1, random.split(), symmetrySettings, "secondLevelLand");
        this.thirdLevelLand = new BooleanMask(1, random.split(), symmetrySettings, "thirdLevelLand");
        this.rampExclusion = new FloatMask(1, random.split(), symmetrySettings, "rampExclusion");
        this.waterAreaBlur = new FloatMask(1, random.split(), symmetrySettings, "waterAreaBlur");
        this.waterArea = new BooleanMask(1, random.split(), symmetrySettings, "waterArea");
        this.bridgeLandArea = new BooleanMask(1, random.split(), symmetrySettings, "bridgeLandArea");
        this.noiseSmallestDetail = 5;
        this.noiseOctaveMultiplier = 1.0f;
        this.noiseMapBlurAmount = 8;
        this.noiseScaleMaxToValue = 40;
        this.landNoiseMapFirstLevel = 6.0f;
        this.landNoiseMapSecondLevel = 25.0f;
        this.landNoiseMapThirdLevel = 35.0f;
        this.spawnSize = 64;
        this.plateauHeight = 6.0f;
        this.plateauBrushIntensity = 16.0f;
        this.mountainDensity = random.nextFloat() / 3.0f;
        this.waterMask = FractalWaterMasks.NONE;
    }

    protected void setMaxNoiseOctaves() {
        this.maxNoiseOctaves = 7;
    }

    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void landSetup() {
        int mapSize = this.map.getSize();
        setMaxNoiseOctaves();
        int numOctaves = 0;
        while (numOctaves < this.maxNoiseOctaves && (this.noiseSmallestDetail << numOctaves) <= mapSize) {
            numOctaves++;
        }
        float amplitude = 1.0f;
        this.landNoiseMap.setSize(mapSize + 1);
        this.waterArea.setSize(mapSize + 1);
        this.bridgeLandArea.setSize(mapSize + 1);
        if (this.waterMask != FractalWaterMasks.NONE) {
            addWaterAreasToNoiseMap(mapSize);
        }
        for (int octave = 0; octave < numOctaves; octave++) {
            FloatMask octaveNoise = new FloatMask(mapSize + 1, this.random.split(), this.land.getSymmetrySettings(), "landNoiseOctave" + octave);
            octaveNoise.addPerlinNoise(this.noiseSmallestDetail << octave, 1.0f / numOctaves);
            octaveNoise.multiply(Float.valueOf(amplitude));
            this.landNoiseMap.add(octaveNoise);
            amplitude *= this.noiseOctaveMultiplier;
        }
        this.landNoiseMap.blur(this.noiseMapBlurAmount);
        this.landNoiseMap.scaleToNewMinAndMaxHeight(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, this.noiseScaleMaxToValue);
        this.land = this.landNoiseMap.copyAsBooleanMask(Float.valueOf(this.landNoiseMapFirstLevel)).erode(0.3f, 10);
        this.secondLevelLand = this.landNoiseMap.copyAsBooleanMask(Float.valueOf(this.landNoiseMapSecondLevel)).erode(0.3f, 10);
        this.thirdLevelLand = this.landNoiseMap.copyAsBooleanMask(Float.valueOf(this.landNoiseMapThirdLevel)).erode(0.3f, 10);
    }

    protected void addWaterAreasToNoiseMap(int mapSize) {
    }

    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void plateausSetup() {
        int mapSize = this.map.getSize();
        this.plateaus.setSize(mapSize + 1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void initRamps() {
        this.ramps.setSize(this.map.getSize() + 1);
        this.ramps = (BooleanMask) this.secondLevelLand.copy();
        this.ramps.outline();
        this.rampExclusion.setSize(this.ramps.getSize());
        this.rampExclusion.addPerlinNoise(StrictMath.min(128, this.rampExclusion.getSize()), 1.0f);
        BooleanMask rampExclusionMask = this.rampExclusion.copyAsBooleanMask(Float.valueOf(0.4f), Float.valueOf(0.6f));
        this.ramps.subtract(rampExclusionMask.invert());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void blurRamps() {
        BooleanMask inflatedRamps = (BooleanMask) this.ramps.copy();
        ((FloatMask) this.heightmap.blur(48, inflatedRamps).blur(32, inflatedRamps.inflate(2)).blur(4, inflatedRamps.inflate(4)).blur(4, inflatedRamps.inflate(8)).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).clampMax(Float.valueOf(255.0f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.SpawnLastTerrainGenerator
    protected void setupSpawnMaskPipeline() {
        ((BooleanMask) this.spawnMask.init(this.land)).subtract(this.secondLevelLand).subtract(this.unbuildable).deflate(8);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicLastTerrainGenerator
    protected void setupHeightmapPipeline() {
        int mapSize = this.map.getSize();
        int numBrushes = Brushes.GENERATOR_BRUSHES.size();
        String brush = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(numBrushes));
        setupMountainHeightmapPipeline();
        setupPlateauHeightmapPipeline();
        setupSmallFeatureHeightmapPipeline();
        initRamps();
        BooleanMask water = ((BooleanMask) this.land.copy()).invert();
        BooleanMask deepWater = ((BooleanMask) water.copy()).deflate(32);
        this.heightmap.setSize(mapSize + 1);
        this.heightmapLand.setSize(mapSize + 1);
        this.heightmapOcean.setSize(mapSize + 1);
        this.heightMapNoise.setSize(mapSize / 128);
        ((FloatMask) ((FloatMask) this.heightmapOcean.addDistance(this.land, -0.45f).clampMin(Float.valueOf(this.oceanFloor))).useBrushWithinAreaWithDensity(water.deflate(8).subtract(deepWater), brush, this.shallowWaterBrushSize, this.shallowWaterBrushDensity, this.shallowWaterBrushIntensity, false).useBrushWithinAreaWithDensity(deepWater, brush, this.deepWaterBrushSize, this.deepWaterBrushDensity, this.deepWaterBrushIntensity, false).clampMax(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(4, deepWater).blur(1);
        ((FloatMask) this.heightmapLand.add(this.heightmapHills).add(this.heightmapValleys).add(this.heightmapMountains).add(Float.valueOf(this.landHeight))).add(this.secondLevelLand, Float.valueOf(this.plateauHeight)).blur(1, ((BooleanMask) this.secondLevelLand.copy()).inflate(6)).add(this.thirdLevelLand, Float.valueOf(this.plateauHeight)).blur(1, ((BooleanMask) this.thirdLevelLand.copy()).inflate(6));
        this.heightmapLand.add(this.heightmapPlateaus).add(this.heightmapOcean);
        this.heightmap.add(this.heightmapLand).add(Float.valueOf(this.waterHeight));
        if (this.heightMapNoise.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            this.heightMapNoise.addWhiteNoise(this.plateauHeight / 3.0f).resample(mapSize / 64);
            this.heightMapNoise.addWhiteNoise(this.plateauHeight / 3.0f).resample(mapSize + 1);
            ((FloatMask) ((FloatMask) ((FloatMask) this.heightMapNoise.addWhiteNoise(1.0f).subtractAvg()).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).setToValue(((BooleanMask) this.land.copy()).invert().inflate(16), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(mapSize / 16);
            this.heightmap.add(this.heightMapNoise);
        }
        blurRamps();
    }
}
