package com.faforever.neroxis.generator.terrain;

import com.faforever.neroxis.brushes.Brushes;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/terrain/RiversTerrainGenerator.class */
public class RiversTerrainGenerator extends BasicTerrainGenerator {
    protected BooleanMask riverMountains;
    protected BooleanMask riverMask;
    private FloatMask riverMountainExclusion;
    private FloatMask rivers;
    private FloatMask plats;
    private FloatMask rampExclusion;
    private BooleanMask plateauExclusion;

    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator, com.faforever.neroxis.generator.terrain.TerrainGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings) {
        super.initialize(map, random, generatorParameters, symmetrySettings);
        int mapSize = map.getSize();
        this.riverMountainExclusion = new FloatMask(mapSize, random.split(), this.symmetrySettings, "riverMountainExclusion");
        this.rivers = new FloatMask(mapSize, random.split(), this.land.getSymmetrySettings(), "rivers");
        this.plats = new FloatMask(mapSize, random.split(), this.plateaus.getSymmetrySettings(), "mountainplateaus");
        this.plateauExclusion = new BooleanMask(mapSize, random.split(), getSymmetrySettings(), "plateauExclusion");
        this.rampExclusion = new FloatMask(1, random.split(), this.symmetrySettings, "rampExclusion");
        this.plateauHeight = 9.0f;
        this.plateauBrushSize = 96;
        this.plateauBrushIntensity = 8.0f;
        this.plateauBrushDensity = 0.3f;
        this.plateauDensity = (random.nextFloat() * 0.03f) + 0.83f;
        this.mountainBrushSize = 24;
        this.mountainBrushDensity = 8.0f;
        this.mountainBrushIntensity = 0.8f;
        this.spawnSize = 48;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void landSetup() {
        int mapSize = this.map.getSize();
        this.land.setSize(mapSize);
        int riversScale = mapSize / 64;
        this.rivers.addPerlinNoise(StrictMath.min(96 + riversScale, mapSize), 1.0f);
        this.riverMask = this.rivers.copyAsBooleanMask(Float.valueOf(0.5f), Float.valueOf(0.65f));
        this.riverMask.invert();
        this.riverMask.blur(10);
        this.riverMask.add((BooleanMask) ((BooleanMask) this.connections.copy()).dilute(1.0f, 20).setSize(this.riverMask.getSize()));
        this.riverMask.erode(0.3f, 10);
        String[] SPAWN_MASK_BRUSHES = {"mountain4.png", "mountain7.png", "mountain8.png", "mountain9.png"};
        this.map.getSpawns().forEach(spawn -> {
            if (spawn.getTeamID() == 0) {
                Vector3 location = spawn.getPosition();
                String brush = SPAWN_MASK_BRUSHES[StrictMath.abs(this.random.nextInt()) % SPAWN_MASK_BRUSHES.length];
                this.riverMask.addBrush(new Vector2(location.x(), location.z()), brush, 1.0f, 256.0f, 150);
            }
        });
        this.land.add(this.riverMask);
        this.land.setSize(mapSize + 1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void plateausSetup() {
        int mapSize = this.map.getSize();
        this.spawnPlateauMask.clear();
        this.plateaus.setSize(mapSize);
        this.plats.addPerlinNoise(32, 1.0f);
        BooleanMask platMountains = this.plats.copyAsBooleanMask(Float.valueOf(this.plateauDensity));
        String[] SPAWN_MASK_BRUSHES = {"mountain4.png", "mountain7.png", "mountain8.png", "mountain9.png"};
        this.map.getSpawns().forEach(spawn -> {
            if (spawn.getTeamID() == 0) {
                Vector3 location = spawn.getPosition();
                String brush = SPAWN_MASK_BRUSHES[StrictMath.abs(this.random.nextInt()) % SPAWN_MASK_BRUSHES.length];
                this.plateauExclusion.addBrush(new Vector2(location.x(), location.z()), brush, 1.0f, 256.0f, 150);
            }
        });
        platMountains.subtract(this.plateauExclusion);
        platMountains.setSize(mapSize);
        this.plateaus.add(platMountains);
        this.plateaus.setSize(mapSize + 1);
        this.plateaus.subtract((BooleanMask) ((BooleanMask) this.riverMask.copy()).invert().blur(10).setSize(this.plateaus.getSize()));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void setupPlateauHeightmapPipeline() {
        String brush = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(Brushes.GENERATOR_BRUSHES.size()));
        this.heightmapPlateaus.setSize(this.map.getSize() + 1);
        this.heightmapPlateaus.useBrushWithinAreaWithDensity(this.plateaus, brush, this.plateauBrushSize, this.plateauBrushDensity, this.plateauBrushIntensity, false).clampMax(Float.valueOf(this.plateauHeight));
        BooleanMask inverseRiverMask = ((BooleanMask) ((BooleanMask) this.riverMask.copy()).invert().setSize(this.heightmapPlateaus.getSize())).blur(6);
        this.heightmapPlateaus.subtract(inverseRiverMask, this.heightmapPlateaus).subtract(inverseRiverMask, Float.valueOf(0.45f)).blur(1);
        BooleanMask paintedPlateaus = this.heightmapPlateaus.copyAsBooleanMask(Float.valueOf(this.plateauHeight - 3.0f));
        this.land.add(paintedPlateaus);
        this.plateaus.init(paintedPlateaus);
        this.plateaus.subtract(this.spawnLandMask).subtract((BooleanMask) ((BooleanMask) this.riverMask.copy()).invert().setSize(this.plateaus.getSize())).add(this.spawnPlateauMask);
        ((FloatMask) this.heightmapPlateaus.add(this.plateaus, Float.valueOf(2.0f)).clampMax(Float.valueOf(this.plateauHeight))).blur(1, this.plateaus);
        BooleanMask plateauBase = this.heightmapPlateaus.copyAsBooleanMask(Float.valueOf(1.0f));
        this.heightmapPlateaus.blur(4, ((BooleanMask) plateauBase.copy()).inflate(96).subtract(((BooleanMask) plateauBase.copy()).inflate(4)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void initRamps() {
        this.ramps.setSize(this.map.getSize() + 1);
        this.ramps = (BooleanMask) this.plateaus.copy();
        this.ramps.outline();
        ((FloatMask) this.rampExclusion.setSize(this.ramps.getSize())).addPerlinNoise(64, 1.0f);
        BooleanMask rampExclusionMask = this.rampExclusion.copyAsBooleanMask(Float.valueOf(0.4f), Float.valueOf(0.8f));
        this.ramps.subtract(rampExclusionMask);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void blurRamps() {
        BooleanMask inflatedRamps = (BooleanMask) this.ramps.copy();
        ((FloatMask) this.heightmap.blur(48, inflatedRamps).blur(32, inflatedRamps.inflate(5)).blur(4, ((BooleanMask) inflatedRamps.copy()).outline().inflate(4)).blur(8, ((BooleanMask) inflatedRamps.copy()).outline().inflate(8)).blur(4, ((BooleanMask) inflatedRamps.copy()).outline().inflate(16)).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).clampMax(Float.valueOf(255.0f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void mountainSetup() {
        int mapSize = this.map.getSize();
        this.mountains.setSize(mapSize / 2);
        this.riverMountains = ((BooleanMask) this.riverMask.copy()).erode(1.0f, 5).outline().inflate(2);
        this.riverMountainExclusion.addPerlinNoise(64, 1.0f);
        BooleanMask riverMountainExclusionMask = this.riverMountainExclusion.copyAsBooleanMask(Float.valueOf(0.3f), Float.valueOf(0.75f));
        this.riverMountains.subtract(riverMountainExclusionMask);
        this.riverMountains.setSize(this.mountains.getSize());
        this.mountains.add(this.riverMountains);
        this.mountains.setSize(mapSize + 1);
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
        this.mountains.subtract(((BooleanMask) this.spawnLandMask.copy()).inflate(this.mountainBrushSize / 4));
        this.plateaus.multiply(this.land).subtract(this.spawnLandMask).add(this.spawnPlateauMask);
        this.land.add(this.plateaus).add(this.spawnLandMask).add(this.spawnPlateauMask);
    }

    @Override // com.faforever.neroxis.generator.terrain.BasicTerrainGenerator
    protected void spawnMaskSetup() {
        this.map.getSpawns().forEach(spawn -> {
            Vector3 location = spawn.getPosition();
            this.spawnLandMask.fillCircle(location, this.spawnSize, true);
        });
    }
}
