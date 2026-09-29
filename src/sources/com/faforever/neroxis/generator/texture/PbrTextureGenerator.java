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

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/texture/PbrTextureGenerator.class */
public abstract class PbrTextureGenerator extends TextureGenerator {
    protected BooleanMask realLand;
    protected BooleanMask realPlateaus;
    protected FloatMask groundTexture;
    protected FloatMask groundAccentTexture;
    protected FloatMask debrisTexture;
    protected FloatMask plateauTexture;
    protected FloatMask slopesTexture;
    protected FloatMask underWaterTexture;
    protected FloatMask waterBeachTexture;
    protected FloatMask cliffAccentTexture;
    protected FloatMask roughnessModifierTexture;
    protected IntegerMask terrainType;
    protected FloatMask waterSurfaceShadows;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.generator.texture.TextureGenerator
    protected void setupTexturePipeline() {
        BooleanMask debris = this.slope.copyAsBooleanMask(Float.valueOf(0.2f));
        BooleanMask cliff = this.slope.copyAsBooleanMask(Float.valueOf(0.75f));
        BooleanMask extendedCliff = ((BooleanMask) cliff.copy()).inflate(2);
        BooleanMask realWater = ((BooleanMask) this.realLand.copy()).invert();
        FloatMask waterBeach = ((BooleanMask) realWater.copy()).inflate(11).subtract(this.realPlateaus).subtract(this.slope.copyAsBooleanMask(Float.valueOf(0.05f)).invert()).add(realWater).copyAsFloatMask(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f).blur(4);
        FloatMask cliffMask = cliff.copyAsFloatMask(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f).blur(4).add(cliff, Float.valueOf(1.0f)).blur(2).add(cliff, Float.valueOf(0.5f));
        int textureSize = this.generatorParameters.mapSize() + 1;
        int mapSize = this.generatorParameters.mapSize();
        BooleanMask realWaterMapSized = (BooleanMask) ((BooleanMask) realWater.copy()).setSize(mapSize);
        BooleanMask shadowsInWater = ((BooleanMask) this.shadowsMask.copy()).multiply(realWaterMapSized);
        this.shadows.setToValue(shadowsInWater, Float.valueOf(1.0f));
        shadowsInWater.add((BooleanMask) ((BooleanMask) this.realLand.copy()).setSize(mapSize), ((BooleanMask) shadowsInWater.copy()).inflate(6));
        this.shadows.subtract(realWaterMapSized, shadowsInWater.copyAsFloatMask(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f).blur(6)).blur(1);
        this.waterSurfaceShadows = ((BooleanMask) ((FloatMask) ((FloatMask) this.heightmap.copy()).clampMin(Float.valueOf(this.biome.waterSettings().elevation()))).copyAsShadowMask(this.biome.lightingSettings().sunDirection()).inflate(1).resample(this.map.getSize() / 2)).copyAsFloatMask(1.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT).blur(1);
        ((FloatMask) this.waterBeachTexture.setSize(textureSize)).add(waterBeach).subtract(cliffMask);
        ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.cliffAccentTexture.setSize(textureSize)).add(Float.valueOf(0.2f))).addPerlinNoise(mapSize / 32, 0.6f).clampMax(Float.valueOf(1.0f))).setToValue(((BooleanMask) extendedCliff.copy()).invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(2).addGaussianNoise(0.05f);
        ((FloatMask) ((FloatMask) this.groundTexture.setSize(textureSize)).add(Float.valueOf(1.0f))).subtract(waterBeach).subtract(cliffMask).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
        ((FloatMask) ((FloatMask) ((FloatMask) this.groundAccentTexture.setSize(textureSize)).add(Float.valueOf(0.1f))).addPerlinNoise(mapSize / 16, 0.5f).addPerlinNoise(mapSize / 6, 0.2f).addGaussianNoise(0.05f).clampMax(Float.valueOf(1.0f))).multiply(this.groundTexture).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
        ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.slopesTexture.init(this.slope)).subtract(Float.valueOf(0.05f))).multiply(Float.valueOf(4.0f))).addPerlinNoise(mapSize / 40, 0.2f).addPerlinNoise(mapSize / 8, 0.1f).addGaussianNoise(0.05f).subtract(Float.valueOf(0.2f))).clampMax(Float.valueOf(1.0f))).subtract(waterBeach).subtract((FloatMask) ((FloatMask) ((FloatMask) cliffMask.copy()).subtract(Float.valueOf(0.2f))).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)));
        ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.debrisTexture.init(((BooleanMask) cliff.copy()).inflate(8), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(1.0f))).subtract(Float.valueOf(0.25f))).addPerlinNoise(mapSize / 8, 0.2f).addGaussianNoise(0.05f).setToValue(((BooleanMask) debris.copy()).invert(), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).setToValue(this.realPlateaus, Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).subtract(realWater.copyAsFloatMask(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f).blur(4)).subtract(cliff, Float.valueOf(0.9f)).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).multiply(Float.valueOf(0.7f))).blur(1);
        ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.plateauTexture.setSize(textureSize)).add(Float.valueOf(0.5f))).addPerlinNoise(mapSize / 40, 0.4f).multiply((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.heightmap.copy()).subtract(Float.valueOf(this.biome.waterSettings().elevation() + 3.0f))).multiply(Float.valueOf(0.5f))).clampMax(Float.valueOf(1.0f))).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).multiply(this.slope.copyAsBooleanMask(Float.valueOf(0.01f)).deflate(4), (FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.slope.copy()).multiply(Float.valueOf(-2.0f))).add(Float.valueOf(1.0f))).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(2).setToValue(cliff, Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT))).blur(1).multiply(Float.valueOf(0.8f));
        ((FloatMask) ((FloatMask) this.underWaterTexture.init(realWater.deflate(1), Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), Float.valueOf(0.7f))).add((FloatMask) ((FloatMask) this.scaledWaterDepth.copy()).multiply(Float.valueOf(0.3f))).clampMax(Float.valueOf(1.0f))).blur(1);
        ((FloatMask) ((FloatMask) this.roughnessModifierTexture.setSize(textureSize)).add(Float.valueOf(0.5f))).addPerlinNoise(mapSize / 70, 0.1f);
        ((FloatMask) this.slopesTexture.multiply(Float.valueOf(0.6f))).add(this.slopesTexture.copyAsBooleanMask(Float.valueOf(0.01f)), Float.valueOf(0.3f));
        this.texturesLowMask.setComponents(this.waterBeachTexture, this.cliffAccentTexture, this.groundTexture, this.groundAccentTexture);
        this.texturesHighMask.setComponents(this.slopesTexture, this.debrisTexture, this.plateauTexture, this.roughnessModifierTexture);
        setupTerrainType(mapSize);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setupTerrainType(int mapSize) {
        this.terrainType.setSize(mapSize);
        List<Integer> terrainTypes = this.map.getBiome().terrainMaterials().terrainTypes();
        ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) ((IntegerMask) this.terrainType.add((Integer) terrainTypes.getFirst())).setToValue(((FloatMask) this.waterBeachTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.55f)), terrainTypes.get(1))).setToValue(((FloatMask) this.cliffAccentTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.35f)), terrainTypes.get(2))).setToValue(((FloatMask) this.groundTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.5f)), terrainTypes.get(3))).setToValue(((FloatMask) this.groundAccentTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.5f)), terrainTypes.get(4))).setToValue(((FloatMask) this.slopesTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.3f)), terrainTypes.get(5))).setToValue(((FloatMask) this.debrisTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.3f)), terrainTypes.get(6))).setToValue(((FloatMask) this.plateauTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.5f)), terrainTypes.get(7))).setToValue(((FloatMask) this.underWaterTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.7f)), terrainTypes.get(8))).setToValue(((FloatMask) this.underWaterTexture.setSize(mapSize)).copyAsBooleanMask(Float.valueOf(0.8f)), terrainTypes.get(9));
    }

    @Override // com.faforever.neroxis.generator.texture.TextureGenerator
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings, TerrainGenerator terrainGenerator) {
        super.initialize(map, random, generatorParameters, symmetrySettings, terrainGenerator);
        this.map.setTerrainShaderPath(SCMap.PBR_SHADER_NAME);
        this.realLand = this.heightmap.copyAsBooleanMask(Float.valueOf(this.biome.waterSettings().elevation()));
        this.realPlateaus = this.heightmap.copyAsBooleanMask(Float.valueOf(this.biome.waterSettings().elevation() + 5.0f));
        this.waterBeachTexture = new FloatMask(1, random.split(), symmetrySettings, "waterBeachTexture");
        this.cliffAccentTexture = new FloatMask(1, random.split(), symmetrySettings, "cliffAccentTexture");
        this.groundTexture = new FloatMask(1, random.split(), symmetrySettings, "groundTexture");
        this.groundAccentTexture = new FloatMask(1, random.split(), symmetrySettings, "groundAccentTexture");
        this.slopesTexture = new FloatMask(1, random.split(), symmetrySettings, "slopesTexture");
        this.debrisTexture = new FloatMask(1, random.split(), symmetrySettings, "debrisTexture");
        this.plateauTexture = new FloatMask(1, random.split(), symmetrySettings, "plateauTexture");
        this.underWaterTexture = new FloatMask(1, random.split(), symmetrySettings, "underWaterTexture");
        this.roughnessModifierTexture = new FloatMask(1, random.split(), symmetrySettings, "roughnessModifierTexture");
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
            this.map.setWaterShadowMap(this.map.getWaterShadowMap(), (FloatMask) this.waterSurfaceShadows.getFinalMask());
        });
    }
}
