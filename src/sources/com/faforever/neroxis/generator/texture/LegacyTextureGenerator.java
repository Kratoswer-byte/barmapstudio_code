package com.faforever.neroxis.generator.texture;

import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.IntegerMask;
import com.faforever.neroxis.mask.NormalMask;
import com.faforever.neroxis.mask.Vector4Mask;
import com.faforever.neroxis.util.DebugUtil;
import com.faforever.neroxis.util.ImageUtil;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/texture/LegacyTextureGenerator.class */
public abstract class LegacyTextureGenerator extends TextureGenerator {
    protected BooleanMask realLand;
    protected BooleanMask realPlateaus;
    protected FloatMask accentGroundTexture;
    protected FloatMask waterBeachTexture;
    protected FloatMask accentSlopesTexture;
    protected FloatMask accentPlateauTexture;
    protected FloatMask slopesTexture;
    protected FloatMask underWaterTexture;
    protected FloatMask rockTexture;
    protected FloatMask accentRockTexture;
    protected IntegerMask terrainType;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.texture.TextureGenerator
    protected void setupTexturePipeline() {
        BooleanMask flat = this.slope.copyAsBooleanMask(Float.valueOf(0.05f)).invert();
        BooleanMask slopes = this.slope.copyAsBooleanMask(Float.valueOf(0.15f));
        BooleanMask accentSlopes = this.slope.copyAsBooleanMask(Float.valueOf(0.75f)).invert().subtract(flat);
        BooleanMask rock = this.slope.copyAsBooleanMask(Float.valueOf(0.75f));
        BooleanMask accentRock = this.slope.copyAsBooleanMask(Float.valueOf(0.75f)).inflate(2);
        BooleanMask realWater = ((BooleanMask) this.realLand.copy()).invert();
        BooleanMask shadowsInWater = ((BooleanMask) this.shadowsMask.copy()).multiply((BooleanMask) ((BooleanMask) realWater.copy()).setSize(this.map.getSize()));
        this.shadows.setToValue((BooleanMask) shadowsInWater.copy(), Float.valueOf(1.0f));
        shadowsInWater.add((BooleanMask) ((BooleanMask) this.realLand.copy()).setSize(this.map.getSize()), ((BooleanMask) shadowsInWater.copy()).inflate(6));
        this.shadows.subtract((BooleanMask) ((BooleanMask) realWater.copy()).setSize(this.map.getSize()), shadowsInWater.copyAsFloatMask(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f).blur(6)).blur(1);
        int textureSize = this.generatorParameters.mapSize() + 1;
        int mapSize = this.generatorParameters.mapSize();
        ((FloatMask) ((FloatMask) ((FloatMask) this.accentGroundTexture.setSize(textureSize)).addPerlinNoise(mapSize / 8, 1.0f).addGaussianNoise(0.05f).clampMax(Float.valueOf(1.0f))).setToValue(realWater, Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(2);
        ((FloatMask) ((FloatMask) ((FloatMask) this.accentPlateauTexture.setSize(textureSize)).addPerlinNoise(mapSize / 16, 1.0f).addGaussianNoise(0.05f).clampMax(Float.valueOf(1.0f))).setToValue(((BooleanMask) this.realPlateaus.copy()).invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(8);
        ((FloatMask) this.slopesTexture.init(slopes, Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(0.75f))).blur(16).add(slopes, Float.valueOf(0.5f)).blur(16).clampMax(Float.valueOf(1.0f));
        ((FloatMask) ((FloatMask) ((FloatMask) this.accentSlopesTexture.setSize(textureSize)).addPerlinNoise(mapSize / 16, 0.5f).addGaussianNoise(0.05f).clampMax(Float.valueOf(1.0f))).setToValue(((BooleanMask) accentSlopes.copy()).invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(16);
        ((FloatMask) ((FloatMask) this.underWaterTexture.init(realWater.deflate(1), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(0.7f))).add((FloatMask) ((FloatMask) this.scaledWaterDepth.copy()).multiply(Float.valueOf(0.3f))).clampMax(Float.valueOf(1.0f))).blur(1);
        ((FloatMask) this.waterBeachTexture.init(realWater.inflate(12).subtract(this.realPlateaus), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(1.0f))).blur(12);
        ((FloatMask) this.rockTexture.init(rock, Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(1.0f))).blur(4).add(rock, Float.valueOf(1.0f)).blur(2).clampMax(Float.valueOf(1.0f));
        ((FloatMask) ((FloatMask) ((FloatMask) this.accentRockTexture.setSize(textureSize)).addPerlinNoise(mapSize / 16, 1.0f).addGaussianNoise(0.05f).clampMax(Float.valueOf(1.0f))).setToValue(((BooleanMask) accentRock.copy()).invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(2);
        this.texturesLowMask.setComponents(this.accentGroundTexture, this.accentPlateauTexture, this.slopesTexture, this.accentSlopesTexture);
        this.texturesHighMask.setComponents(this.waterBeachTexture, this.underWaterTexture, this.rockTexture, this.accentRockTexture);
        setupTerrainType(mapSize);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupTerrainType(int mapSize) {
        this.terrainType.setSize(mapSize);
        List<Integer> terrainTypes = this.map.getBiome().terrainMaterials().terrainTypes();
        ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) this.terrainType.add((Integer) terrainTypes.getFirst())).setToValue(((FloatMask) this.accentGroundTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.5f)), terrainTypes.get(1))).setToValue(((FloatMask) this.accentPlateauTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.5f)), terrainTypes.get(2))).setToValue(((FloatMask) this.slopesTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.3f)), terrainTypes.get(3))).setToValue(((FloatMask) this.accentSlopesTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.3f)), terrainTypes.get(4))).setToValue(((FloatMask) this.waterBeachTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.5f)), terrainTypes.get(5))).setToValue(((FloatMask) this.accentRockTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.35f)), terrainTypes.get(7))).setToValue(((FloatMask) this.rockTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.55f)), terrainTypes.get(6))).setToValue(((FloatMask) this.underWaterTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.7f)), terrainTypes.get(8))).setToValue(((FloatMask) this.underWaterTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.8f)), terrainTypes.get(9));
    }

    @Override // com.faforever.neroxis.generator.texture.TextureGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings, TerrainGenerator terrainGenerator) {
        super.initialize(map, random, generatorParameters, symmetrySettings, terrainGenerator);
        this.realLand = this.heightmap.copyAsBooleanMask(Float.valueOf(this.biome.waterSettings().elevation()));
        this.realPlateaus = this.heightmap.copyAsBooleanMask(Float.valueOf(this.biome.waterSettings().elevation() + 3.0f));
        this.accentGroundTexture = new FloatMask(1, random.split(), symmetrySettings, "accentGroundTexture");
        this.waterBeachTexture = new FloatMask(1, random.split(), symmetrySettings, "waterBeachTexture");
        this.accentSlopesTexture = new FloatMask(1, random.split(), symmetrySettings, "accentSlopesTexture");
        this.accentPlateauTexture = new FloatMask(1, random.split(), symmetrySettings, "accentPlateauTexture");
        this.slopesTexture = new FloatMask(1, random.split(), symmetrySettings, "slopesTexture");
        this.underWaterTexture = new FloatMask(1, random.split(), symmetrySettings, "underWaterTexture");
        this.rockTexture = new FloatMask(1, random.split(), symmetrySettings, "rockTexture");
        this.accentRockTexture = new FloatMask(1, random.split(), symmetrySettings, "accentRockTexture");
        this.terrainType = new IntegerMask(1, random.split(), symmetrySettings, "terrainType");
    }

    @Override // com.faforever.neroxis.generator.texture.TextureGenerator
    public void setTextures() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "generateTextures", () -> {
            this.map.setTextureMasksScaled(this.map.getTextureMasksLow(), (Vector4Mask) this.texturesLowMask.getFinalMask());
            this.map.setTextureMasksScaled(this.map.getTextureMasksHigh(), (Vector4Mask) this.texturesHighMask.getFinalMask());
            this.map.setTerrainType(this.map.getTerrainType(), (IntegerMask) this.terrainType.getFinalMask());
            this.map.setMapNormalTexture(ImageUtil.getMapNormalTexture((NormalMask) this.normals.getFinalMask()));
            this.map.setMapInfoTexture(ImageUtil.getMapInfoTexture((FloatMask) this.scaledWaterDepth.getFinalMask(), (FloatMask) this.shadows.getFinalMask()));
        });
    }
}
