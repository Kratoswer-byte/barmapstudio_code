package com.faforever.neroxis.generator.texture;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.GeneratorParameters;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.generator.terrain.TerrainGenerator;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.placement.DecalPlacer;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.NormalMask;
import com.faforever.neroxis.mask.Vector4Mask;
import com.faforever.neroxis.util.DebugUtil;
import com.faforever.neroxis.util.ImageUtil;
import java.io.IOException;
import java.util.random.RandomGenerator;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/texture/TextureGenerator.class */
public abstract class TextureGenerator {
    protected SCMap map;
    protected Biome biome;
    protected RandomGenerator.SplittableGenerator random;
    protected GeneratorParameters generatorParameters;
    protected SymmetrySettings symmetrySettings;
    protected FloatMask heightmap;
    protected FloatMask slope;
    protected NormalMask normals;
    protected FloatMask shadows;
    protected FloatMask scaledWaterDepth;
    protected BooleanMask shadowsMask;
    protected Vector4Mask texturesLowMask;
    protected Vector4Mask texturesHighMask;
    protected Vector4Mask texturesLowPreviewMask;
    protected Vector4Mask texturesHighPreviewMask;
    protected FloatMask heightmapPreview;
    protected FloatMask irradiance;
    protected DecalPlacer decalPlacer;
    protected BooleanMask passableLand;
    protected BooleanMask fieldDecal;
    protected BooleanMask slopeDecal;

    protected abstract void setupTexturePipeline();

    public abstract Biome loadBiome();

    @Generated
    public SCMap getMap() {
        return this.map;
    }

    @Generated
    public Biome getBiome() {
        return this.biome;
    }

    @Generated
    public RandomGenerator.SplittableGenerator getRandom() {
        return this.random;
    }

    @Generated
    public GeneratorParameters getGeneratorParameters() {
        return this.generatorParameters;
    }

    @Generated
    public SymmetrySettings getSymmetrySettings() {
        return this.symmetrySettings;
    }

    @Generated
    public FloatMask getHeightmap() {
        return this.heightmap;
    }

    @Generated
    public FloatMask getSlope() {
        return this.slope;
    }

    @Generated
    public NormalMask getNormals() {
        return this.normals;
    }

    @Generated
    public FloatMask getShadows() {
        return this.shadows;
    }

    @Generated
    public FloatMask getScaledWaterDepth() {
        return this.scaledWaterDepth;
    }

    @Generated
    public BooleanMask getShadowsMask() {
        return this.shadowsMask;
    }

    @Generated
    public Vector4Mask getTexturesLowMask() {
        return this.texturesLowMask;
    }

    @Generated
    public Vector4Mask getTexturesHighMask() {
        return this.texturesHighMask;
    }

    @Generated
    public Vector4Mask getTexturesLowPreviewMask() {
        return this.texturesLowPreviewMask;
    }

    @Generated
    public Vector4Mask getTexturesHighPreviewMask() {
        return this.texturesHighPreviewMask;
    }

    @Generated
    public FloatMask getHeightmapPreview() {
        return this.heightmapPreview;
    }

    @Generated
    public FloatMask getIrradiance() {
        return this.irradiance;
    }

    @Generated
    public DecalPlacer getDecalPlacer() {
        return this.decalPlacer;
    }

    @Generated
    public BooleanMask getPassableLand() {
        return this.passableLand;
    }

    @Generated
    public BooleanMask getFieldDecal() {
        return this.fieldDecal;
    }

    @Generated
    public BooleanMask getSlopeDecal() {
        return this.slopeDecal;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void initialize(SCMap map, RandomGenerator.SplittableGenerator random, GeneratorParameters generatorParameters, SymmetrySettings symmetrySettings, TerrainGenerator terrainGenerator) {
        this.map = map;
        this.biome = loadBiome();
        this.random = random.split();
        this.generatorParameters = generatorParameters;
        this.symmetrySettings = symmetrySettings;
        this.heightmap = new FloatMask(1, random.split(), symmetrySettings, "heightmap");
        this.slope = new FloatMask(1, random.split(), symmetrySettings, "slope");
        this.passableLand = new BooleanMask(1, random.split(), symmetrySettings, "passableLand");
        this.passableLand.init(terrainGenerator.getPassableLand());
        this.slope.init(terrainGenerator.getSlope());
        this.fieldDecal = new BooleanMask(1, random.split(), symmetrySettings, "fieldDecal");
        this.slopeDecal = new BooleanMask(1, random.split(), symmetrySettings, "slopeDecal");
        this.decalPlacer = new DecalPlacer(map, random.split());
        this.heightmap.init(terrainGenerator.getHeightmap());
        this.slope.init(terrainGenerator.getSlope());
        this.normals = ((FloatMask) this.heightmap.copy()).addGaussianNoise(0.025f).blur(1).copyAsNormalMask(1.0f);
        FloatMask heightMapSize = (FloatMask) ((FloatMask) this.heightmap.copy()).resample(map.getSize());
        this.shadowsMask = heightMapSize.copyAsShadowMask(this.biome.lightingSettings().sunDirection()).inflate(1);
        this.shadows = this.shadowsMask.copyAsFloatMask(1.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        float abyssDepth = this.biome.waterSettings().elevation() - this.biome.waterSettings().elevationAbyss();
        this.scaledWaterDepth = (FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) ((FloatMask) this.heightmap.copy()).subtract(Float.valueOf(this.biome.waterSettings().elevation()))).multiply(Float.valueOf(-1.0f))).divide(Float.valueOf(abyssDepth))).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
        this.texturesLowMask = new Vector4Mask(map.getSize() + 1, random.split(), symmetrySettings, "texturesLow");
        this.texturesHighMask = new Vector4Mask(map.getSize() + 1, random.split(), symmetrySettings, "texturesHigh");
    }

    public void setTextures() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "generateTextures", () -> {
            this.map.setTextureMasksScaled(this.map.getTextureMasksLow(), (Vector4Mask) this.texturesLowMask.getFinalMask());
            this.map.setTextureMasksScaled(this.map.getTextureMasksHigh(), (Vector4Mask) this.texturesHighMask.getFinalMask());
            this.map.setMapNormalTexture(ImageUtil.getMapNormalTexture((NormalMask) this.normals.getFinalMask()));
            this.map.setMapInfoTexture(ImageUtil.getMapInfoTexture((FloatMask) this.scaledWaterDepth.getFinalMask(), (FloatMask) this.shadows.getFinalMask()));
        });
    }

    public void placeDecals() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "placeDecals", () -> {
            this.decalPlacer.placeDecals((BooleanMask) this.fieldDecal.getFinalMask(), this.map.getBiome().decalMaterials().fieldNormals(), 32.0f, 32.0f, 24.0f, 32.0f);
            this.decalPlacer.placeDecals((BooleanMask) this.fieldDecal.getFinalMask(), this.map.getBiome().decalMaterials().fieldAlbedos(), 64.0f, 128.0f, 24.0f, 32.0f);
            this.decalPlacer.placeDecals((BooleanMask) this.slopeDecal.getFinalMask(), this.map.getBiome().decalMaterials().slopeNormals(), 16.0f, 32.0f, 16.0f, 32.0f);
            this.decalPlacer.placeDecals((BooleanMask) this.slopeDecal.getFinalMask(), this.map.getBiome().decalMaterials().slopeAlbedos(), 64.0f, 128.0f, 32.0f, 48.0f);
        });
    }

    public void setCompressedDecals() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "setCompressedDecals", () -> {
            this.map.setCompressedShadows(ImageUtil.compressShadow((FloatMask) this.shadows.getFinalMask(), this.biome.lightingSettings()));
            this.map.setCompressedNormal(ImageUtil.compressNormal((NormalMask) this.normals.getFinalMask()));
        });
    }

    public void generatePreview() {
        DebugUtil.timedRun("com.faforever.neroxis.map.generator", "generatePreview", () -> {
            try {
                PreviewGenerator.generatePreview((FloatMask) this.heightmapPreview.getFinalMask(), (FloatMask) this.irradiance.getFinalMask(), this.map, (Vector4Mask) this.texturesLowPreviewMask.getFinalMask(), (Vector4Mask) this.texturesHighPreviewMask.getFinalMask());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setupDecalPipeline() {
        this.fieldDecal.init(this.passableLand);
        this.slopeDecal.init(this.slope, Float.valueOf(0.25f));
        this.fieldDecal.subtract(((BooleanMask) this.slopeDecal.copy()).inflate(16));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setupPreviewPipeline() {
        this.texturesLowPreviewMask = (Vector4Mask) ((Vector4Mask) this.texturesLowMask.copy()).resample(PreviewGenerator.PREVIEW_SIZE);
        this.texturesHighPreviewMask = (Vector4Mask) ((Vector4Mask) this.texturesHighMask.copy()).resample(PreviewGenerator.PREVIEW_SIZE);
        this.heightmapPreview = (FloatMask) ((FloatMask) this.heightmap.copy()).resample(PreviewGenerator.PREVIEW_SIZE);
        this.irradiance = (FloatMask) ((NormalMask) ((FloatMask) this.heightmap.copy()).copyAsNormalMask(8.0f).resample(PreviewGenerator.PREVIEW_SIZE)).copyAsDotProduct(this.map.getBiome().lightingSettings().sunDirection()).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
    }

    public final void setupPipeline() {
        setupTexturePipeline();
        setupDecalPipeline();
        setupPreviewPipeline();
    }
}
