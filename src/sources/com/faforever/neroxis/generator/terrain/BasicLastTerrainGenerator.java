package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.brushes.Brushes;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.MapMaskMethods;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/BasicLastTerrainGenerator.class */
public class BasicLastTerrainGenerator extends SpawnLastTerrainGenerator {
    protected BooleanMask land;
    protected BooleanMask mountains;
    protected BooleanMask hills;
    protected BooleanMask valleys;
    protected BooleanMask plateaus;
    protected BooleanMask ramps;
    protected BooleanMask connections;
    protected FloatMask heightmapValleys;
    protected FloatMask heightmapHills;
    protected FloatMask heightmapPlateaus;
    protected FloatMask heightmapMountains;
    protected FloatMask heightmapLand;
    protected FloatMask heightmapOcean;
    protected FloatMask heightMapNoise;
    protected int spawnSize;
    protected float waterHeight;
    protected float plateauHeight;
    protected float oceanFloor;
    protected float valleyFloor;
    protected float landHeight;
    protected float landDensity;
    protected float plateauDensity;
    protected float mountainDensity;
    protected float rampDensity;
    protected float shallowWaterBrushIntensity;
    protected float deepWaterBrushIntensity;
    protected float plateauBrushDensity;
    protected float valleyBrushDensity;
    protected float hillBrushDensity;
    protected int shallowWaterBrushSize;
    protected float shallowWaterBrushDensity;
    protected int deepWaterBrushSize;
    protected float deepWaterBrushDensity;
    protected int mountainBrushSize;
    protected int plateauBrushSize;
    protected int smallFeatureBrushSize;
    protected float mountainBrushIntensity;
    protected float plateauBrushIntensity;
    protected float valleyBrushIntensity;
    protected float hillBrushIntensity;
    protected float mountainBrushDensity;

    @Override // com.faforever.neroxis.generator.terrain.SpawnLastTerrainGenerator, com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        super.initialize(map, random, generatorParameters, symmetrySettings);
        this.land = new BooleanMask(1, random.split(), symmetrySettings, "land");
        this.mountains = new BooleanMask(1, random.split(), symmetrySettings, "mountains");
        this.plateaus = new BooleanMask(1, random.split(), symmetrySettings, "plateaus");
        this.ramps = new BooleanMask(1, random.split(), symmetrySettings, "ramps");
        this.hills = new BooleanMask(1, random.split(), symmetrySettings, "hills");
        this.valleys = new BooleanMask(1, random.split(), symmetrySettings, "valleys");
        this.connections = new BooleanMask(1, random.split(), symmetrySettings, "connections");
        this.heightmapValleys = new FloatMask(1, random.split(), symmetrySettings, "heightmapValleys");
        this.heightmapHills = new FloatMask(1, random.split(), symmetrySettings, "heightmapHills");
        this.heightmapPlateaus = new FloatMask(1, random.split(), symmetrySettings, "heightmapPlateaus");
        this.heightmapMountains = new FloatMask(1, random.split(), symmetrySettings, "heightmapMountains");
        this.heightmapLand = new FloatMask(1, random.split(), symmetrySettings, "heightmapLand");
        this.heightmapOcean = new FloatMask(1, random.split(), symmetrySettings, "heightmapOcean");
        this.heightMapNoise = new FloatMask(1, random.split(), symmetrySettings, "heightmapNoise");
        this.spawnSize = 48;
        this.waterHeight = map.getBiome().waterSettings().elevation();
        this.plateauHeight = 6.0f;
        this.oceanFloor = -16.0f;
        this.valleyFloor = -5.0f;
        this.landHeight = 0.25f;
        this.landDensity = random.nextFloat();
        this.plateauDensity = random.nextFloat();
        this.mountainDensity = random.nextFloat();
        this.rampDensity = random.nextFloat();
        int rawMountainBrushSize = map.getSize() < 512 ? 16 : 32;
        this.mountainBrushSize = (int) ((2.0f - this.mountainDensity) * rawMountainBrushSize);
        this.mountainBrushDensity = map.getSize() < 512 ? 0.1f : 0.05f;
        this.mountainBrushIntensity = 10.0f;
        this.plateauBrushSize = 64;
        this.plateauBrushDensity = 0.16f;
        this.plateauBrushIntensity = 10.0f;
        this.smallFeatureBrushSize = 24;
        this.hillBrushIntensity = 0.5f;
        this.valleyBrushDensity = 0.72f;
        this.hillBrushDensity = 0.72f;
        this.shallowWaterBrushIntensity = 0.5f;
        this.shallowWaterBrushSize = 24;
        this.shallowWaterBrushDensity = 1.0f;
        this.deepWaterBrushIntensity = 1.0f;
        this.deepWaterBrushSize = 64;
        this.deepWaterBrushDensity = 0.065f;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    @Override // com.faforever.neroxis.generator.terrain.SpawnLastTerrainGenerator
    protected void setupTerrainPipeline() throws MatchException {
        teamConnectionsSetup();
        landSetup();
        plateausSetup();
        mountainSetup();
        symmetrySetup();
        enforceSymmetry();
        setupHeightmapPipeline();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    protected void teamConnectionsSetup() throws MatchException {
        float maxStepSize = this.map.getSize() / 128.0f;
        int numTeamConnections = (int) (((((this.rampDensity + this.plateauDensity) + this.mountainDensity) / 3.0f) * 2.0f) + 1.0f);
        this.connections.setSize(this.map.getSize() + 1);
        List<Vector2> locations = SymmetryUtil.getRandomPointsInBounds(this.random.split(), this.symmetrySettings.terrainSymmetry(), this.map.getSize(), this.random.nextInt(2, 6));
        MapMaskMethods.connectLocationsAroundCenter(locations, this.random.split(), this.connections, 0, 1, numTeamConnections, maxStepSize, 32);
        MapMaskMethods.connectLocations(locations, this.random.split(), this.connections, 1, 1, maxStepSize);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void landSetup() {
        float landDensityRange = 0.9f - 0.835f;
        float scaledLandDensity = (this.landDensity * landDensityRange) + 0.835f;
        int mapSize = this.map.getSize();
        this.land.setSize(mapSize / 16);
        this.land.randomize(scaledLandDensity).blur(2, 0.75f).erode(0.5f);
        this.land.setSize(mapSize / 4);
        this.land.dilute(0.5f, mapSize / 128);
        this.land.setSize(mapSize + 1);
        this.land.blur(8, 0.75f);
        if (mapSize <= 512) {
            this.land.add(((BooleanMask) this.connections.copy()).inflate(this.mountainBrushSize / 8).blur(12, 0.125f));
        }
    }

    protected void plateausSetup() {
        float plateauDensityRange = 0.7f - 0.6f;
        float scaledPlateauDensity = (this.plateauDensity * plateauDensityRange) + 0.6f;
        this.plateaus.setSize(this.map.getSize() / 16);
        this.plateaus.randomize(scaledPlateauDensity).blur(2, 0.75f).setSize(this.map.getSize() / 4);
        this.plateaus.dilute(0.5f, this.map.getSize() / 128);
        this.plateaus.setSize(this.map.getSize() + 1);
        this.plateaus.blur(16, 0.75f);
    }

    protected void mountainSetup() {
        this.mountains.setSize(this.map.getSize() / 4);
        if (this.random.nextBoolean()) {
            this.mountains.progressiveWalk((int) ((this.mountainDensity * 100.0f) / this.symmetrySettings.terrainSymmetry().getNumSymPoints()), this.map.getSize() / 64);
        } else {
            this.mountains.randomWalk((int) ((this.mountainDensity * 100.0f) / this.symmetrySettings.terrainSymmetry().getNumSymPoints()), this.map.getSize() / 64);
        }
        this.mountains.dilute(0.5f, 4);
        this.mountains.setSize(this.map.getSize() + 1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void symmetrySetup() {
        if (!this.symmetrySettings.spawnSymmetry().isPerfectSymmetry()) {
            float halfSize = this.map.getSize() / 2.0f;
            int forceRadius = this.symmetrySettings.spawnSymmetry().getNumSymPoints();
            ((BooleanMask) this.land.limitToCenteredCircle(halfSize).forceSymmetry()).inflate(forceRadius).deflate(forceRadius);
            ((BooleanMask) this.plateaus.limitToCenteredCircle(halfSize).forceSymmetry()).inflate(forceRadius).deflate(forceRadius);
            ((BooleanMask) this.mountains.limitToCenteredCircle(halfSize).forceSymmetry()).inflate(forceRadius).deflate(forceRadius);
        }
    }

    protected void enforceSymmetry() {
        SymmetrySettings symmetrySettings = this.heightmap.getSymmetrySettings();
        if (!symmetrySettings.terrainSymmetry().isPerfectSymmetry() && symmetrySettings.spawnSymmetry().isPerfectSymmetry()) {
            this.land.forceSymmetry();
            this.mountains.forceSymmetry();
            this.plateaus.forceSymmetry();
            this.ramps.forceSymmetry();
            this.hills.forceSymmetry();
            this.valleys.forceSymmetry();
            this.connections.forceSymmetry();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
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
        ((FloatMask) this.heightmapLand.add(this.heightmapHills).add(this.heightmapValleys).add(this.heightmapMountains).add(Float.valueOf(this.landHeight))).add(this.heightmapPlateaus).add(this.heightmapOcean);
        this.heightmap.add(this.heightmapLand).add(Float.valueOf(this.waterHeight));
        if (this.heightMapNoise.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            this.heightMapNoise.addWhiteNoise(this.plateauHeight / 3.0f).resample(mapSize / 64);
            this.heightMapNoise.addWhiteNoise(this.plateauHeight / 3.0f).resample(mapSize + 1);
            ((FloatMask) ((FloatMask) ((FloatMask) this.heightMapNoise.addWhiteNoise(1.0f).subtractAvg()).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).setToValue(((BooleanMask) this.land.copy()).invert().inflate(16), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(mapSize / 16);
            this.heightmap.add(this.heightMapNoise);
        }
        blurRamps();
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupMountainHeightmapPipeline() {
        String brush = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(Brushes.GENERATOR_BRUSHES.size()));
        this.heightmapMountains.setSize(this.map.getSize() + 1);
        this.heightmapMountains.useBrushWithinAreaWithDensity(this.mountains, brush, this.mountainBrushSize, this.mountainBrushDensity, this.mountainBrushIntensity, false);
        BooleanMask paintedMountains = this.heightmapMountains.copyAsBooleanMask(Float.valueOf(this.plateauHeight / 2.0f));
        this.mountains.init(paintedMountains);
        this.land.add(paintedMountains);
        this.heightmapMountains.blur(4, ((BooleanMask) this.mountains.copy()).inflate(64).subtract(this.mountains));
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupPlateauHeightmapPipeline() {
        String brush = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(Brushes.GENERATOR_BRUSHES.size()));
        this.heightmapPlateaus.setSize(this.map.getSize() + 1);
        this.heightmapPlateaus.useBrushWithinAreaWithDensity(this.plateaus, brush, this.plateauBrushSize, this.plateauBrushDensity, this.plateauBrushIntensity, false).clampMax(Float.valueOf(this.plateauHeight));
        BooleanMask paintedPlateaus = this.heightmapPlateaus.copyAsBooleanMask(Float.valueOf(this.plateauHeight - 3.0f));
        this.land.add(paintedPlateaus);
        this.plateaus.init(paintedPlateaus);
        ((FloatMask) this.heightmapPlateaus.add(this.plateaus, Float.valueOf(2.0f)).clampMax(Float.valueOf(this.plateauHeight))).blur(1, this.plateaus);
        BooleanMask plateauBase = this.heightmapPlateaus.copyAsBooleanMask(Float.valueOf(1.0f));
        this.heightmapPlateaus.blur(4, ((BooleanMask) plateauBase.copy()).inflate(96).subtract(((BooleanMask) plateauBase.copy()).inflate(4)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupSmallFeatureHeightmapPipeline() {
        int numSymPoints = this.symmetrySettings.spawnSymmetry().getNumSymPoints();
        String brushValley = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(Brushes.GENERATOR_BRUSHES.size()));
        String brushHill = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(Brushes.GENERATOR_BRUSHES.size()));
        this.heightmapValleys.setSize(this.map.getSize() + 1);
        this.heightmapHills.setSize(this.map.getSize() + 1);
        this.hills.setSize(this.map.getSize() / 4);
        this.valleys.setSize(this.map.getSize() / 4);
        this.hills.randomWalk(this.random.nextInt(4) + 1, this.random.nextInt(this.map.getSize() / 4) / numSymPoints).dilute(0.5f, 2).setSize(this.map.getSize() + 1);
        this.hills.multiply(((BooleanMask) this.land.copy()).deflate(8)).subtract(((BooleanMask) this.plateaus.copy()).outline().inflate(8));
        this.valleys.randomWalk(this.random.nextInt(4), this.random.nextInt(this.map.getSize() / 4) / numSymPoints).dilute(0.5f, 4).setSize(this.map.getSize() + 1);
        this.valleys.multiply(((BooleanMask) this.plateaus.copy()).deflate(8));
        this.valleyBrushIntensity = -0.35f;
        this.heightmapValleys.useBrushWithinAreaWithDensity(this.valleys, brushValley, this.smallFeatureBrushSize, this.valleyBrushDensity, this.valleyBrushIntensity, false).clampMin(Float.valueOf(this.valleyFloor));
        this.heightmapHills.useBrushWithinAreaWithDensity(this.hills.add(((BooleanMask) this.mountains.copy()).outline().inflate(4).acid(0.01f, 4)), brushHill, this.smallFeatureBrushSize, this.hillBrushDensity, this.hillBrushIntensity, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void initRamps() {
        int maxStepSize = this.map.getSize() / 128;
        int numPaths = ((int) (this.rampDensity * 20.0f)) / this.symmetrySettings.terrainSymmetry().getNumSymPoints();
        int bound = this.map.getSize() / 4;
        this.ramps.setSize(this.map.getSize() + 1);
        if (this.map.getSize() >= 512) {
            MapMaskMethods.pathInEdgeBounds(this.random.split(), this.ramps, maxStepSize, numPaths, 2, bound, 1.5707964f);
        } else {
            MapMaskMethods.pathInEdgeBounds(this.random.split(), this.ramps, maxStepSize, numPaths / 4, 2, bound, 1.5707964f);
        }
        this.ramps.subtract(((BooleanMask) this.connections.copy()).inflate(64)).inflate(maxStepSize / 2).add(((BooleanMask) this.connections.copy()).inflate(maxStepSize / 2)).multiply(((BooleanMask) this.plateaus.copy()).outline()).subtract(this.mountains).inflate(10);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void blurRamps() {
        BooleanMask noRamps = ((BooleanMask) this.plateaus.copy()).outline().subtract(this.ramps).add(this.mountains);
        BooleanMask inflatedRamps = (BooleanMask) this.ramps.copy();
        ((FloatMask) this.heightmap.blur(48, inflatedRamps).blur(32, inflatedRamps.inflate(8).subtract(noRamps.inflate(4))).blur(4, ((BooleanMask) inflatedRamps.copy()).outline().inflate(4)).blur(24, inflatedRamps.inflate(8).subtract(noRamps.inflate(4))).blur(4, ((BooleanMask) inflatedRamps.copy()).outline().inflate(4)).blur(16, inflatedRamps.inflate(16).subtract(noRamps.inflate(4))).blur(4, ((BooleanMask) inflatedRamps.copy()).outline().inflate(4)).blur(8, inflatedRamps.inflate(16).subtract(noRamps.inflate(4))).blur(4, ((BooleanMask) inflatedRamps.copy()).outline().inflate(4)).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).clampMax(Float.valueOf(255.0f));
    }
}
