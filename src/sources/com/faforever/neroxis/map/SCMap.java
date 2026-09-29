package com.faforever.neroxis.map;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.IntegerMask;
import com.faforever.neroxis.mask.Mask;
import com.faforever.neroxis.mask.Vector4Mask;
import com.faforever.neroxis.util.ImageUtil;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.serial.biome.WaterSettings;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/SCMap.class */
public class SCMap {
    public static final String PBR_SHADER_NAME = "Terrain250";
    public static final String LEGACY_SHADER_NAME = "TTerrainXP";
    public static final int SIGNATURE = 443572557;
    public static final int VERSION_MAJOR = 2;
    public static final int WAVE_NORMAL_COUNT = 4;
    public static final float[] WAVE_NORMAL_REPEATS = {9.0E-4f, 0.009f, 0.05f, 0.5f};
    public static final Vector2[] WAVE_NORMAL_MOVEMENTS = {new Vector2(0.5f, -0.95f), new Vector2(0.05f, -0.095f), new Vector2(0.01f, 0.03f), new Vector2(5.0E-4f, 9.0E-4f)};
    public static final String[] WAVE_TEXTURE_PATHS = {"/textures/engine/waves.dds", "/textures/engine/waves.dds", "/textures/engine/waves.dds", "/textures/engine/waves.dds"};
    private byte[] compressedNormal;
    private byte[] compressedShadows;
    private int size;
    private Vector4 playableArea;
    private boolean isUnexplored;
    private Biome biome;
    private BufferedImage heightmap;
    private BufferedImage normalMap;
    private BufferedImage textureMasksLow;
    private BufferedImage textureMasksHigh;
    private BufferedImage waterMap;
    private BufferedImage waterFoamMap;
    private BufferedImage waterShadowMap;
    private BufferedImage waterDepthBiasMap;
    private BufferedImage terrainType;
    private BufferedImage mapInfoTexture;
    private BufferedImage mapNormalTexture;
    private final List<Spawn> spawns = new ArrayList();
    private final List<Marker> mexes = new ArrayList();
    private final List<Marker> hydros = new ArrayList();
    private final List<Marker> blankMarkers = new ArrayList();
    private final List<DecalGroup> decalGroups = new ArrayList();
    private final List<Decal> decals = new ArrayList();
    private final List<WaveGenerator> waveGenerators = new ArrayList();
    private final List<Prop> props = new ArrayList();
    private final List<Army> armies = new ArrayList();
    private final List<AIMarker> landAIMarkers = new ArrayList();
    private final List<AIMarker> amphibiousAIMarkers = new ArrayList();
    private final List<AIMarker> navyAIMarkers = new ArrayList();
    private final List<AIMarker> airAIMarkers = new ArrayList();
    private final List<AIMarker> rallyMarkers = new ArrayList();
    private final List<AIMarker> expansionAIMarkers = new ArrayList();
    private final List<AIMarker> largeExpansionAIMarkers = new ArrayList();
    private final List<AIMarker> navalAreaAIMarkers = new ArrayList();
    private final List<AIMarker> navalRallyMarkers = new ArrayList();
    private float heightMapScale = 0.0078125f;
    private String name = "";
    private int minorVersion = 56;
    private String description = "";
    private String terrainShaderPath = LEGACY_SHADER_NAME;
    private String backgroundPath = "/textures/environment/defaultbackground.dds";
    private boolean generatePreview = true;
    private float noRushRadius = 50.0f;
    private String folderName = "";
    private String filePrefix = "";
    private String script = "";
    private String skyCubePath = "/textures/environment/defaultskycube.dds";
    private SkyBox skyBox = new SkyBox();
    private BufferedImage preview = new BufferedImage(PreviewGenerator.PREVIEW_SIZE, PreviewGenerator.PREVIEW_SIZE, 2);
    private int cartographicContourInterval = 100;
    private int cartographicDeepWaterColor = new Color(71, 140, 181).getRGB();
    private int cartographicMapContourColor = new Color(0, 0, 0).getRGB();
    private int cartographicMapShoreColor = new Color(141, 200, 225).getRGB();
    private int cartographicMapLandStartColor = new Color(119, 101, 108).getRGB();
    private int cartographicMapLandEndColor = new Color(206, 206, 176).getRGB();

    @Generated
    public void setCompressedNormal(final byte[] compressedNormal) {
        this.compressedNormal = compressedNormal;
    }

    @Generated
    public void setCompressedShadows(final byte[] compressedShadows) {
        this.compressedShadows = compressedShadows;
    }

    @Generated
    public void setHeightMapScale(final float heightMapScale) {
        this.heightMapScale = heightMapScale;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setPlayableArea(final Vector4 playableArea) {
        this.playableArea = playableArea;
    }

    @Generated
    public void setMinorVersion(final int minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Generated
    public void setDescription(final String description) {
        this.description = description;
    }

    @Generated
    public void setTerrainShaderPath(final String terrainShaderPath) {
        this.terrainShaderPath = terrainShaderPath;
    }

    @Generated
    public void setBackgroundPath(final String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }

    @Generated
    public void setGeneratePreview(final boolean generatePreview) {
        this.generatePreview = generatePreview;
    }

    @Generated
    public void setUnexplored(final boolean isUnexplored) {
        this.isUnexplored = isUnexplored;
    }

    @Generated
    public void setNoRushRadius(final float noRushRadius) {
        this.noRushRadius = noRushRadius;
    }

    @Generated
    public void setFolderName(final String folderName) {
        this.folderName = folderName;
    }

    @Generated
    public void setFilePrefix(final String filePrefix) {
        this.filePrefix = filePrefix;
    }

    @Generated
    public void setScript(final String script) {
        this.script = script;
    }

    @Generated
    public void setSkyCubePath(final String skyCubePath) {
        this.skyCubePath = skyCubePath;
    }

    @Generated
    public void setBiome(final Biome biome) {
        this.biome = biome;
    }

    @Generated
    public void setSkyBox(final SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    @Generated
    public void setNormalMap(final BufferedImage normalMap) {
        this.normalMap = normalMap;
    }

    @Generated
    public void setTextureMasksLow(final BufferedImage textureMasksLow) {
        this.textureMasksLow = textureMasksLow;
    }

    @Generated
    public void setTextureMasksHigh(final BufferedImage textureMasksHigh) {
        this.textureMasksHigh = textureMasksHigh;
    }

    @Generated
    public void setWaterMap(final BufferedImage waterMap) {
        this.waterMap = waterMap;
    }

    @Generated
    public void setMapInfoTexture(final BufferedImage mapInfoTexture) {
        this.mapInfoTexture = mapInfoTexture;
    }

    @Generated
    public void setMapNormalTexture(final BufferedImage mapNormalTexture) {
        this.mapNormalTexture = mapNormalTexture;
    }

    @Generated
    public void setCartographicContourInterval(final int cartographicContourInterval) {
        this.cartographicContourInterval = cartographicContourInterval;
    }

    @Generated
    public void setCartographicDeepWaterColor(final int cartographicDeepWaterColor) {
        this.cartographicDeepWaterColor = cartographicDeepWaterColor;
    }

    @Generated
    public void setCartographicMapContourColor(final int cartographicMapContourColor) {
        this.cartographicMapContourColor = cartographicMapContourColor;
    }

    @Generated
    public void setCartographicMapShoreColor(final int cartographicMapShoreColor) {
        this.cartographicMapShoreColor = cartographicMapShoreColor;
    }

    @Generated
    public void setCartographicMapLandStartColor(final int cartographicMapLandStartColor) {
        this.cartographicMapLandStartColor = cartographicMapLandStartColor;
    }

    @Generated
    public void setCartographicMapLandEndColor(final int cartographicMapLandEndColor) {
        this.cartographicMapLandEndColor = cartographicMapLandEndColor;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SCMap)) {
            return false;
        }
        SCMap other = (SCMap) o;
        if (!other.canEqual(this) || Float.compare(getHeightMapScale(), other.getHeightMapScale()) != 0 || getSize() != other.getSize() || getMinorVersion() != other.getMinorVersion() || isGeneratePreview() != other.isGeneratePreview() || isUnexplored() != other.isUnexplored() || Float.compare(getNoRushRadius(), other.getNoRushRadius()) != 0 || getCartographicContourInterval() != other.getCartographicContourInterval() || getCartographicDeepWaterColor() != other.getCartographicDeepWaterColor() || getCartographicMapContourColor() != other.getCartographicMapContourColor() || getCartographicMapShoreColor() != other.getCartographicMapShoreColor() || getCartographicMapLandStartColor() != other.getCartographicMapLandStartColor() || getCartographicMapLandEndColor() != other.getCartographicMapLandEndColor()) {
            return false;
        }
        Object this$spawns = getSpawns();
        Object other$spawns = other.getSpawns();
        if (this$spawns == null) {
            if (other$spawns != null) {
                return false;
            }
        } else if (!this$spawns.equals(other$spawns)) {
            return false;
        }
        Object this$mexes = getMexes();
        Object other$mexes = other.getMexes();
        if (this$mexes == null) {
            if (other$mexes != null) {
                return false;
            }
        } else if (!this$mexes.equals(other$mexes)) {
            return false;
        }
        Object this$hydros = getHydros();
        Object other$hydros = other.getHydros();
        if (this$hydros == null) {
            if (other$hydros != null) {
                return false;
            }
        } else if (!this$hydros.equals(other$hydros)) {
            return false;
        }
        Object this$blankMarkers = getBlankMarkers();
        Object other$blankMarkers = other.getBlankMarkers();
        if (this$blankMarkers == null) {
            if (other$blankMarkers != null) {
                return false;
            }
        } else if (!this$blankMarkers.equals(other$blankMarkers)) {
            return false;
        }
        Object this$decalGroups = getDecalGroups();
        Object other$decalGroups = other.getDecalGroups();
        if (this$decalGroups == null) {
            if (other$decalGroups != null) {
                return false;
            }
        } else if (!this$decalGroups.equals(other$decalGroups)) {
            return false;
        }
        Object this$decals = getDecals();
        Object other$decals = other.getDecals();
        if (this$decals == null) {
            if (other$decals != null) {
                return false;
            }
        } else if (!this$decals.equals(other$decals)) {
            return false;
        }
        Object this$waveGenerators = getWaveGenerators();
        Object other$waveGenerators = other.getWaveGenerators();
        if (this$waveGenerators == null) {
            if (other$waveGenerators != null) {
                return false;
            }
        } else if (!this$waveGenerators.equals(other$waveGenerators)) {
            return false;
        }
        Object this$props = getProps();
        Object other$props = other.getProps();
        if (this$props == null) {
            if (other$props != null) {
                return false;
            }
        } else if (!this$props.equals(other$props)) {
            return false;
        }
        Object this$armies = getArmies();
        Object other$armies = other.getArmies();
        if (this$armies == null) {
            if (other$armies != null) {
                return false;
            }
        } else if (!this$armies.equals(other$armies)) {
            return false;
        }
        Object this$landAIMarkers = getLandAIMarkers();
        Object other$landAIMarkers = other.getLandAIMarkers();
        if (this$landAIMarkers == null) {
            if (other$landAIMarkers != null) {
                return false;
            }
        } else if (!this$landAIMarkers.equals(other$landAIMarkers)) {
            return false;
        }
        Object this$amphibiousAIMarkers = getAmphibiousAIMarkers();
        Object other$amphibiousAIMarkers = other.getAmphibiousAIMarkers();
        if (this$amphibiousAIMarkers == null) {
            if (other$amphibiousAIMarkers != null) {
                return false;
            }
        } else if (!this$amphibiousAIMarkers.equals(other$amphibiousAIMarkers)) {
            return false;
        }
        Object this$navyAIMarkers = getNavyAIMarkers();
        Object other$navyAIMarkers = other.getNavyAIMarkers();
        if (this$navyAIMarkers == null) {
            if (other$navyAIMarkers != null) {
                return false;
            }
        } else if (!this$navyAIMarkers.equals(other$navyAIMarkers)) {
            return false;
        }
        Object this$airAIMarkers = getAirAIMarkers();
        Object other$airAIMarkers = other.getAirAIMarkers();
        if (this$airAIMarkers == null) {
            if (other$airAIMarkers != null) {
                return false;
            }
        } else if (!this$airAIMarkers.equals(other$airAIMarkers)) {
            return false;
        }
        Object this$rallyMarkers = getRallyMarkers();
        Object other$rallyMarkers = other.getRallyMarkers();
        if (this$rallyMarkers == null) {
            if (other$rallyMarkers != null) {
                return false;
            }
        } else if (!this$rallyMarkers.equals(other$rallyMarkers)) {
            return false;
        }
        Object this$expansionAIMarkers = getExpansionAIMarkers();
        Object other$expansionAIMarkers = other.getExpansionAIMarkers();
        if (this$expansionAIMarkers == null) {
            if (other$expansionAIMarkers != null) {
                return false;
            }
        } else if (!this$expansionAIMarkers.equals(other$expansionAIMarkers)) {
            return false;
        }
        Object this$largeExpansionAIMarkers = getLargeExpansionAIMarkers();
        Object other$largeExpansionAIMarkers = other.getLargeExpansionAIMarkers();
        if (this$largeExpansionAIMarkers == null) {
            if (other$largeExpansionAIMarkers != null) {
                return false;
            }
        } else if (!this$largeExpansionAIMarkers.equals(other$largeExpansionAIMarkers)) {
            return false;
        }
        Object this$navalAreaAIMarkers = getNavalAreaAIMarkers();
        Object other$navalAreaAIMarkers = other.getNavalAreaAIMarkers();
        if (this$navalAreaAIMarkers == null) {
            if (other$navalAreaAIMarkers != null) {
                return false;
            }
        } else if (!this$navalAreaAIMarkers.equals(other$navalAreaAIMarkers)) {
            return false;
        }
        Object this$navalRallyMarkers = getNavalRallyMarkers();
        Object other$navalRallyMarkers = other.getNavalRallyMarkers();
        if (this$navalRallyMarkers == null) {
            if (other$navalRallyMarkers != null) {
                return false;
            }
        } else if (!this$navalRallyMarkers.equals(other$navalRallyMarkers)) {
            return false;
        }
        if (!Arrays.equals(getCompressedNormal(), other.getCompressedNormal()) || !Arrays.equals(getCompressedShadows(), other.getCompressedShadows())) {
            return false;
        }
        Object this$name = getName();
        Object other$name = other.getName();
        if (this$name == null) {
            if (other$name != null) {
                return false;
            }
        } else if (!this$name.equals(other$name)) {
            return false;
        }
        Object this$playableArea = getPlayableArea();
        Object other$playableArea = other.getPlayableArea();
        if (this$playableArea == null) {
            if (other$playableArea != null) {
                return false;
            }
        } else if (!this$playableArea.equals(other$playableArea)) {
            return false;
        }
        Object this$description = getDescription();
        Object other$description = other.getDescription();
        if (this$description == null) {
            if (other$description != null) {
                return false;
            }
        } else if (!this$description.equals(other$description)) {
            return false;
        }
        Object this$terrainShaderPath = getTerrainShaderPath();
        Object other$terrainShaderPath = other.getTerrainShaderPath();
        if (this$terrainShaderPath == null) {
            if (other$terrainShaderPath != null) {
                return false;
            }
        } else if (!this$terrainShaderPath.equals(other$terrainShaderPath)) {
            return false;
        }
        Object this$backgroundPath = getBackgroundPath();
        Object other$backgroundPath = other.getBackgroundPath();
        if (this$backgroundPath == null) {
            if (other$backgroundPath != null) {
                return false;
            }
        } else if (!this$backgroundPath.equals(other$backgroundPath)) {
            return false;
        }
        Object this$folderName = getFolderName();
        Object other$folderName = other.getFolderName();
        if (this$folderName == null) {
            if (other$folderName != null) {
                return false;
            }
        } else if (!this$folderName.equals(other$folderName)) {
            return false;
        }
        Object this$filePrefix = getFilePrefix();
        Object other$filePrefix = other.getFilePrefix();
        if (this$filePrefix == null) {
            if (other$filePrefix != null) {
                return false;
            }
        } else if (!this$filePrefix.equals(other$filePrefix)) {
            return false;
        }
        Object this$script = getScript();
        Object other$script = other.getScript();
        if (this$script == null) {
            if (other$script != null) {
                return false;
            }
        } else if (!this$script.equals(other$script)) {
            return false;
        }
        Object this$skyCubePath = getSkyCubePath();
        Object other$skyCubePath = other.getSkyCubePath();
        if (this$skyCubePath == null) {
            if (other$skyCubePath != null) {
                return false;
            }
        } else if (!this$skyCubePath.equals(other$skyCubePath)) {
            return false;
        }
        Object this$biome = getBiome();
        Object other$biome = other.getBiome();
        if (this$biome == null) {
            if (other$biome != null) {
                return false;
            }
        } else if (!this$biome.equals(other$biome)) {
            return false;
        }
        Object this$skyBox = getSkyBox();
        Object other$skyBox = other.getSkyBox();
        if (this$skyBox == null) {
            if (other$skyBox != null) {
                return false;
            }
        } else if (!this$skyBox.equals(other$skyBox)) {
            return false;
        }
        Object this$preview = getPreview();
        Object other$preview = other.getPreview();
        if (this$preview == null) {
            if (other$preview != null) {
                return false;
            }
        } else if (!this$preview.equals(other$preview)) {
            return false;
        }
        Object this$heightmap = getHeightmap();
        Object other$heightmap = other.getHeightmap();
        if (this$heightmap == null) {
            if (other$heightmap != null) {
                return false;
            }
        } else if (!this$heightmap.equals(other$heightmap)) {
            return false;
        }
        Object this$normalMap = getNormalMap();
        Object other$normalMap = other.getNormalMap();
        if (this$normalMap == null) {
            if (other$normalMap != null) {
                return false;
            }
        } else if (!this$normalMap.equals(other$normalMap)) {
            return false;
        }
        Object this$textureMasksLow = getTextureMasksLow();
        Object other$textureMasksLow = other.getTextureMasksLow();
        if (this$textureMasksLow == null) {
            if (other$textureMasksLow != null) {
                return false;
            }
        } else if (!this$textureMasksLow.equals(other$textureMasksLow)) {
            return false;
        }
        Object this$textureMasksHigh = getTextureMasksHigh();
        Object other$textureMasksHigh = other.getTextureMasksHigh();
        if (this$textureMasksHigh == null) {
            if (other$textureMasksHigh != null) {
                return false;
            }
        } else if (!this$textureMasksHigh.equals(other$textureMasksHigh)) {
            return false;
        }
        Object this$waterMap = getWaterMap();
        Object other$waterMap = other.getWaterMap();
        if (this$waterMap == null) {
            if (other$waterMap != null) {
                return false;
            }
        } else if (!this$waterMap.equals(other$waterMap)) {
            return false;
        }
        Object this$waterFoamMap = getWaterFoamMap();
        Object other$waterFoamMap = other.getWaterFoamMap();
        if (this$waterFoamMap == null) {
            if (other$waterFoamMap != null) {
                return false;
            }
        } else if (!this$waterFoamMap.equals(other$waterFoamMap)) {
            return false;
        }
        Object this$waterShadowMap = getWaterShadowMap();
        Object other$waterShadowMap = other.getWaterShadowMap();
        if (this$waterShadowMap == null) {
            if (other$waterShadowMap != null) {
                return false;
            }
        } else if (!this$waterShadowMap.equals(other$waterShadowMap)) {
            return false;
        }
        Object this$waterDepthBiasMap = getWaterDepthBiasMap();
        Object other$waterDepthBiasMap = other.getWaterDepthBiasMap();
        if (this$waterDepthBiasMap == null) {
            if (other$waterDepthBiasMap != null) {
                return false;
            }
        } else if (!this$waterDepthBiasMap.equals(other$waterDepthBiasMap)) {
            return false;
        }
        Object this$terrainType = getTerrainType();
        Object other$terrainType = other.getTerrainType();
        if (this$terrainType == null) {
            if (other$terrainType != null) {
                return false;
            }
        } else if (!this$terrainType.equals(other$terrainType)) {
            return false;
        }
        Object this$mapInfoTexture = getMapInfoTexture();
        Object other$mapInfoTexture = other.getMapInfoTexture();
        if (this$mapInfoTexture == null) {
            if (other$mapInfoTexture != null) {
                return false;
            }
        } else if (!this$mapInfoTexture.equals(other$mapInfoTexture)) {
            return false;
        }
        Object this$mapNormalTexture = getMapNormalTexture();
        Object other$mapNormalTexture = other.getMapNormalTexture();
        if (this$mapNormalTexture == null) {
            return other$mapNormalTexture == null;
        }
        return this$mapNormalTexture.equals(other$mapNormalTexture);
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof SCMap;
    }

    @Generated
    public int hashCode() {
        int result = (1 * 59) + Float.floatToIntBits(getHeightMapScale());
        int result2 = (((((((((((((((((((((result * 59) + getSize()) * 59) + getMinorVersion()) * 59) + (isGeneratePreview() ? 79 : 97)) * 59) + (isUnexplored() ? 79 : 97)) * 59) + Float.floatToIntBits(getNoRushRadius())) * 59) + getCartographicContourInterval()) * 59) + getCartographicDeepWaterColor()) * 59) + getCartographicMapContourColor()) * 59) + getCartographicMapShoreColor()) * 59) + getCartographicMapLandStartColor()) * 59) + getCartographicMapLandEndColor();
        Object $spawns = getSpawns();
        int result3 = (result2 * 59) + ($spawns == null ? 43 : $spawns.hashCode());
        Object $mexes = getMexes();
        int result4 = (result3 * 59) + ($mexes == null ? 43 : $mexes.hashCode());
        Object $hydros = getHydros();
        int result5 = (result4 * 59) + ($hydros == null ? 43 : $hydros.hashCode());
        Object $blankMarkers = getBlankMarkers();
        int result6 = (result5 * 59) + ($blankMarkers == null ? 43 : $blankMarkers.hashCode());
        Object $decalGroups = getDecalGroups();
        int result7 = (result6 * 59) + ($decalGroups == null ? 43 : $decalGroups.hashCode());
        Object $decals = getDecals();
        int result8 = (result7 * 59) + ($decals == null ? 43 : $decals.hashCode());
        Object $waveGenerators = getWaveGenerators();
        int result9 = (result8 * 59) + ($waveGenerators == null ? 43 : $waveGenerators.hashCode());
        Object $props = getProps();
        int result10 = (result9 * 59) + ($props == null ? 43 : $props.hashCode());
        Object $armies = getArmies();
        int result11 = (result10 * 59) + ($armies == null ? 43 : $armies.hashCode());
        Object $landAIMarkers = getLandAIMarkers();
        int result12 = (result11 * 59) + ($landAIMarkers == null ? 43 : $landAIMarkers.hashCode());
        Object $amphibiousAIMarkers = getAmphibiousAIMarkers();
        int result13 = (result12 * 59) + ($amphibiousAIMarkers == null ? 43 : $amphibiousAIMarkers.hashCode());
        Object $navyAIMarkers = getNavyAIMarkers();
        int result14 = (result13 * 59) + ($navyAIMarkers == null ? 43 : $navyAIMarkers.hashCode());
        Object $airAIMarkers = getAirAIMarkers();
        int result15 = (result14 * 59) + ($airAIMarkers == null ? 43 : $airAIMarkers.hashCode());
        Object $rallyMarkers = getRallyMarkers();
        int result16 = (result15 * 59) + ($rallyMarkers == null ? 43 : $rallyMarkers.hashCode());
        Object $expansionAIMarkers = getExpansionAIMarkers();
        int result17 = (result16 * 59) + ($expansionAIMarkers == null ? 43 : $expansionAIMarkers.hashCode());
        Object $largeExpansionAIMarkers = getLargeExpansionAIMarkers();
        int result18 = (result17 * 59) + ($largeExpansionAIMarkers == null ? 43 : $largeExpansionAIMarkers.hashCode());
        Object $navalAreaAIMarkers = getNavalAreaAIMarkers();
        int result19 = (result18 * 59) + ($navalAreaAIMarkers == null ? 43 : $navalAreaAIMarkers.hashCode());
        Object $navalRallyMarkers = getNavalRallyMarkers();
        int result20 = (((((result19 * 59) + ($navalRallyMarkers == null ? 43 : $navalRallyMarkers.hashCode())) * 59) + Arrays.hashCode(getCompressedNormal())) * 59) + Arrays.hashCode(getCompressedShadows());
        Object $name = getName();
        int result21 = (result20 * 59) + ($name == null ? 43 : $name.hashCode());
        Object $playableArea = getPlayableArea();
        int result22 = (result21 * 59) + ($playableArea == null ? 43 : $playableArea.hashCode());
        Object $description = getDescription();
        int result23 = (result22 * 59) + ($description == null ? 43 : $description.hashCode());
        Object $terrainShaderPath = getTerrainShaderPath();
        int result24 = (result23 * 59) + ($terrainShaderPath == null ? 43 : $terrainShaderPath.hashCode());
        Object $backgroundPath = getBackgroundPath();
        int result25 = (result24 * 59) + ($backgroundPath == null ? 43 : $backgroundPath.hashCode());
        Object $folderName = getFolderName();
        int result26 = (result25 * 59) + ($folderName == null ? 43 : $folderName.hashCode());
        Object $filePrefix = getFilePrefix();
        int result27 = (result26 * 59) + ($filePrefix == null ? 43 : $filePrefix.hashCode());
        Object $script = getScript();
        int result28 = (result27 * 59) + ($script == null ? 43 : $script.hashCode());
        Object $skyCubePath = getSkyCubePath();
        int result29 = (result28 * 59) + ($skyCubePath == null ? 43 : $skyCubePath.hashCode());
        Object $biome = getBiome();
        int result30 = (result29 * 59) + ($biome == null ? 43 : $biome.hashCode());
        Object $skyBox = getSkyBox();
        int result31 = (result30 * 59) + ($skyBox == null ? 43 : $skyBox.hashCode());
        Object $preview = getPreview();
        int result32 = (result31 * 59) + ($preview == null ? 43 : $preview.hashCode());
        Object $heightmap = getHeightmap();
        int result33 = (result32 * 59) + ($heightmap == null ? 43 : $heightmap.hashCode());
        Object $normalMap = getNormalMap();
        int result34 = (result33 * 59) + ($normalMap == null ? 43 : $normalMap.hashCode());
        Object $textureMasksLow = getTextureMasksLow();
        int result35 = (result34 * 59) + ($textureMasksLow == null ? 43 : $textureMasksLow.hashCode());
        Object $textureMasksHigh = getTextureMasksHigh();
        int result36 = (result35 * 59) + ($textureMasksHigh == null ? 43 : $textureMasksHigh.hashCode());
        Object $waterMap = getWaterMap();
        int result37 = (result36 * 59) + ($waterMap == null ? 43 : $waterMap.hashCode());
        Object $waterFoamMap = getWaterFoamMap();
        int result38 = (result37 * 59) + ($waterFoamMap == null ? 43 : $waterFoamMap.hashCode());
        Object $waterShadowMap = getWaterShadowMap();
        int result39 = (result38 * 59) + ($waterShadowMap == null ? 43 : $waterShadowMap.hashCode());
        Object $waterDepthBiasMap = getWaterDepthBiasMap();
        int result40 = (result39 * 59) + ($waterDepthBiasMap == null ? 43 : $waterDepthBiasMap.hashCode());
        Object $terrainType = getTerrainType();
        int result41 = (result40 * 59) + ($terrainType == null ? 43 : $terrainType.hashCode());
        Object $mapInfoTexture = getMapInfoTexture();
        int result42 = (result41 * 59) + ($mapInfoTexture == null ? 43 : $mapInfoTexture.hashCode());
        Object $mapNormalTexture = getMapNormalTexture();
        return (result42 * 59) + ($mapNormalTexture == null ? 43 : $mapNormalTexture.hashCode());
    }

    @Generated
    public List<Spawn> getSpawns() {
        return this.spawns;
    }

    @Generated
    public List<Marker> getMexes() {
        return this.mexes;
    }

    @Generated
    public List<Marker> getHydros() {
        return this.hydros;
    }

    @Generated
    public List<Marker> getBlankMarkers() {
        return this.blankMarkers;
    }

    @Generated
    public List<DecalGroup> getDecalGroups() {
        return this.decalGroups;
    }

    @Generated
    public List<Decal> getDecals() {
        return this.decals;
    }

    @Generated
    public List<WaveGenerator> getWaveGenerators() {
        return this.waveGenerators;
    }

    @Generated
    public List<Prop> getProps() {
        return this.props;
    }

    @Generated
    public List<Army> getArmies() {
        return this.armies;
    }

    @Generated
    public List<AIMarker> getLandAIMarkers() {
        return this.landAIMarkers;
    }

    @Generated
    public List<AIMarker> getAmphibiousAIMarkers() {
        return this.amphibiousAIMarkers;
    }

    @Generated
    public List<AIMarker> getNavyAIMarkers() {
        return this.navyAIMarkers;
    }

    @Generated
    public List<AIMarker> getAirAIMarkers() {
        return this.airAIMarkers;
    }

    @Generated
    public List<AIMarker> getRallyMarkers() {
        return this.rallyMarkers;
    }

    @Generated
    public List<AIMarker> getExpansionAIMarkers() {
        return this.expansionAIMarkers;
    }

    @Generated
    public List<AIMarker> getLargeExpansionAIMarkers() {
        return this.largeExpansionAIMarkers;
    }

    @Generated
    public List<AIMarker> getNavalAreaAIMarkers() {
        return this.navalAreaAIMarkers;
    }

    @Generated
    public List<AIMarker> getNavalRallyMarkers() {
        return this.navalRallyMarkers;
    }

    @Generated
    public byte[] getCompressedNormal() {
        return this.compressedNormal;
    }

    @Generated
    public byte[] getCompressedShadows() {
        return this.compressedShadows;
    }

    @Generated
    public float getHeightMapScale() {
        return this.heightMapScale;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public int getSize() {
        return this.size;
    }

    @Generated
    public Vector4 getPlayableArea() {
        return this.playableArea;
    }

    @Generated
    public int getMinorVersion() {
        return this.minorVersion;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public String getTerrainShaderPath() {
        return this.terrainShaderPath;
    }

    @Generated
    public String getBackgroundPath() {
        return this.backgroundPath;
    }

    @Generated
    public boolean isGeneratePreview() {
        return this.generatePreview;
    }

    @Generated
    public boolean isUnexplored() {
        return this.isUnexplored;
    }

    @Generated
    public float getNoRushRadius() {
        return this.noRushRadius;
    }

    @Generated
    public String getFolderName() {
        return this.folderName;
    }

    @Generated
    public String getFilePrefix() {
        return this.filePrefix;
    }

    @Generated
    public String getScript() {
        return this.script;
    }

    @Generated
    public String getSkyCubePath() {
        return this.skyCubePath;
    }

    @Generated
    public Biome getBiome() {
        return this.biome;
    }

    @Generated
    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    @Generated
    public BufferedImage getPreview() {
        return this.preview;
    }

    @Generated
    public BufferedImage getHeightmap() {
        return this.heightmap;
    }

    @Generated
    public BufferedImage getNormalMap() {
        return this.normalMap;
    }

    @Generated
    public BufferedImage getTextureMasksLow() {
        return this.textureMasksLow;
    }

    @Generated
    public BufferedImage getTextureMasksHigh() {
        return this.textureMasksHigh;
    }

    @Generated
    public BufferedImage getWaterMap() {
        return this.waterMap;
    }

    @Generated
    public BufferedImage getWaterFoamMap() {
        return this.waterFoamMap;
    }

    @Generated
    public BufferedImage getWaterShadowMap() {
        return this.waterShadowMap;
    }

    @Generated
    public BufferedImage getWaterDepthBiasMap() {
        return this.waterDepthBiasMap;
    }

    @Generated
    public BufferedImage getTerrainType() {
        return this.terrainType;
    }

    @Generated
    public BufferedImage getMapInfoTexture() {
        return this.mapInfoTexture;
    }

    @Generated
    public BufferedImage getMapNormalTexture() {
        return this.mapNormalTexture;
    }

    @Generated
    public int getCartographicContourInterval() {
        return this.cartographicContourInterval;
    }

    @Generated
    public int getCartographicDeepWaterColor() {
        return this.cartographicDeepWaterColor;
    }

    @Generated
    public int getCartographicMapContourColor() {
        return this.cartographicMapContourColor;
    }

    @Generated
    public int getCartographicMapShoreColor() {
        return this.cartographicMapShoreColor;
    }

    @Generated
    public int getCartographicMapLandStartColor() {
        return this.cartographicMapLandStartColor;
    }

    @Generated
    public int getCartographicMapLandEndColor() {
        return this.cartographicMapLandEndColor;
    }

    public SCMap(int size, Biome biome) {
        this.size = size;
        this.biome = biome;
        this.playableArea = new Vector4(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, size, size);
        this.heightmap = new BufferedImage(size + 1, size + 1, 11);
        this.normalMap = new BufferedImage(size, size, 2);
        this.textureMasksLow = new BufferedImage(size + 1, size + 1, 2);
        this.textureMasksHigh = new BufferedImage(size + 1, size + 1, 2);
        this.waterMap = new BufferedImage(size / 2, size / 2, 2);
        this.waterFoamMap = new BufferedImage(size / 2, size / 2, 10);
        this.waterShadowMap = new BufferedImage(size / 2, size / 2, 10);
        for (int y = 0; y < size / 2; y++) {
            for (int x = 0; x < size / 2; x++) {
                this.waterShadowMap.getRaster().setPixel(x, y, new int[]{255});
            }
        }
        this.waterDepthBiasMap = new BufferedImage(size / 2, size / 2, 10);
        this.terrainType = new BufferedImage(size, size, 10);
    }

    private static void checkImageSize(BufferedImage image, int size) {
        if (image.getWidth() != size) {
            throw new IllegalArgumentException("Image size does not match required size: Image size is " + image.getWidth() + " required size is " + size);
        }
    }

    private static void checkMaskSize(Mask<?, ?> mask, int size) {
        if (mask.getSize() != size) {
            throw new IllegalArgumentException("Image size does not match required size: Image size is " + mask.getSize() + " required size is " + size);
        }
    }

    public void setPreview(BufferedImage preview) {
        checkImageSize(preview, PreviewGenerator.PREVIEW_SIZE);
        this.preview = preview;
    }

    public AIMarker getAmphibiousMarker(String id) {
        return this.amphibiousAIMarkers.stream().filter(amphibiousMarker -> {
            return amphibiousMarker.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public void setHeightmap(BufferedImage heightmap) {
        checkImageSize(heightmap, this.size + 1);
        this.heightmap = heightmap;
    }

    public void setWaterFoamMap(BufferedImage waterFoamMap) {
        checkImageSize(waterFoamMap, this.size / 2);
        this.waterFoamMap = waterFoamMap;
    }

    public void setTerrainType(BufferedImage terrainType) {
        checkImageSize(terrainType, this.size);
        this.terrainType = terrainType;
    }

    public int getSpawnCount() {
        return this.spawns.size();
    }

    public Spawn getSpawn(int i) {
        return this.spawns.get(i);
    }

    public void addSpawn(Spawn spawn) {
        this.spawns.add(spawn);
    }

    public int getMexCount() {
        return this.mexes.size();
    }

    public Marker getMex(int i) {
        return this.mexes.get(i);
    }

    public void addMex(Marker mex) {
        this.mexes.add(mex);
    }

    public int getHydroCount() {
        return this.hydros.size();
    }

    public Marker getHydro(int i) {
        return this.hydros.get(i);
    }

    public void addHydro(Marker hydro) {
        this.hydros.add(hydro);
    }

    public int getDecalCount() {
        return this.decals.size();
    }

    public Decal getDecal(int i) {
        return this.decals.get(i);
    }

    public void addDecal(Decal decal) {
        this.decals.add(decal);
    }

    public int getDecalGroupCount() {
        return this.decalGroups.size();
    }

    public DecalGroup getDecalGroup(int i) {
        return this.decalGroups.get(i);
    }

    public void addDecalGroup(DecalGroup decalGroup) {
        this.decalGroups.add(decalGroup);
    }

    public int getPropCount() {
        return this.props.size();
    }

    public Prop getProp(int i) {
        return this.props.get(i);
    }

    public void addProp(Prop prop) {
        this.props.add(prop);
    }

    public int getArmyCount() {
        return this.armies.size();
    }

    public Army getArmy(int i) {
        return this.armies.get(i);
    }

    public Army getArmy(String id) {
        return this.armies.stream().filter(army -> {
            return army.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public void addArmy(Army army) {
        this.armies.add(army);
    }

    public void setArmyOrder(SequencedCollection<String> armyIds) {
        Map<String, Spawn> spawnsById = (Map) this.spawns.stream().collect(Collectors.toMap((v0) -> {
            return v0.getId();
        }, Function.identity()));
        this.spawns.clear();
        armyIds.forEach(armyId -> {
            this.spawns.add((Spawn) spawnsById.get(armyId));
        });
    }

    public int getBlankCount() {
        return this.blankMarkers.size();
    }

    public Marker getBlank(int i) {
        return this.blankMarkers.get(i);
    }

    public Marker getBlank(String id) {
        return this.blankMarkers.stream().filter(blankMarker -> {
            return blankMarker.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public void addBlank(Marker blank) {
        this.blankMarkers.add(blank);
    }

    public int getLandMarkerCount() {
        return this.landAIMarkers.size();
    }

    public AIMarker getLandMarker(int i) {
        return this.landAIMarkers.get(i);
    }

    public AIMarker getLandMarker(String id) {
        return this.landAIMarkers.stream().filter(landMarker -> {
            return landMarker.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public void addLandMarker(AIMarker aiMarker) {
        this.landAIMarkers.add(aiMarker);
    }

    public int getAmphibiousMarkerCount() {
        return this.amphibiousAIMarkers.size();
    }

    public AIMarker getAmphibiousMarker(int i) {
        return this.amphibiousAIMarkers.get(i);
    }

    public void changeStratumSize(int stratumSize) {
        this.textureMasksHigh = ImageUtil.scaleImage(this.textureMasksHigh, stratumSize, stratumSize);
        this.textureMasksLow = ImageUtil.scaleImage(this.textureMasksLow, stratumSize, stratumSize);
    }

    public void rotateMap(float radians) {
        if ((radians / 6.2831855f) % 1.0f == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return;
        }
        rotateMapContent(radians);
        rotateObjects(radians);
    }

    private void rotateMapContent(float radians) {
        rotateBiome(radians);
        this.heightmap = ImageUtil.rotateImage(this.heightmap, radians);
        this.normalMap = ImageUtil.rotateImage(this.normalMap, radians);
        this.waterMap = ImageUtil.rotateImage(this.waterMap, radians);
        this.waterFoamMap = ImageUtil.rotateImage(this.waterFoamMap, radians);
        this.waterShadowMap = ImageUtil.rotateImage(this.waterShadowMap, radians);
        this.waterDepthBiasMap = ImageUtil.rotateImage(this.waterDepthBiasMap, radians);
        this.terrainType = ImageUtil.rotateImage(this.terrainType, radians);
        this.textureMasksHigh = ImageUtil.rotateImage(this.textureMasksHigh, radians);
        this.textureMasksLow = ImageUtil.rotateImage(this.textureMasksLow, radians);
        this.preview = ImageUtil.rotateImage(this.preview, radians);
        if (this.mapNormalTexture != null) {
            this.mapNormalTexture = ImageUtil.rotateImage(this.mapNormalTexture, radians);
        }
        if (this.mapInfoTexture != null) {
            this.mapInfoTexture = ImageUtil.rotateImage(this.mapInfoTexture, radians);
        }
    }

    private void rotateBiome(float radians) {
        WaterSettings oldWaterSettings = this.biome.waterSettings();
        WaterSettings newWaterSettings = new WaterSettings(oldWaterSettings.waterPresent(), oldWaterSettings.elevation(), oldWaterSettings.elevationDeep(), oldWaterSettings.elevationAbyss(), oldWaterSettings.surfaceColor(), oldWaterSettings.colorLerp(), oldWaterSettings.refractionScale(), oldWaterSettings.fresnelBias(), oldWaterSettings.fresnelPower(), oldWaterSettings.unitReflection(), oldWaterSettings.skyReflection(), oldWaterSettings.sunShininess(), oldWaterSettings.sunStrength(), oldWaterSettings.sunDirection().rotateXZ(radians), oldWaterSettings.sunColor(), oldWaterSettings.sunReflection(), oldWaterSettings.sunGlow(), oldWaterSettings.texPathCubemap(), oldWaterSettings.texPathWaterRamp(), oldWaterSettings.waveTextures().stream().map(waveTexture -> {
            return new WaterSettings.WaveTexture(waveTexture.normalMovement().rotate(radians), waveTexture.texPath(), waveTexture.normalRepeat());
        }).toList());
        LightingSettings oldLightingSettings = this.biome.lightingSettings();
        LightingSettings newLightingSettings = new LightingSettings(oldLightingSettings.lightingMultiplier(), oldLightingSettings.sunDirection().rotateXZ(radians), oldLightingSettings.sunAmbience(), oldLightingSettings.sunColor(), oldLightingSettings.shadowFillColor(), oldLightingSettings.specularColor(), oldLightingSettings.bloom(), oldLightingSettings.fogColor(), oldLightingSettings.fogStart(), oldLightingSettings.fogEnd());
        this.biome = new Biome(this.biome.name(), this.biome.terrainMaterials(), this.biome.propMaterials(), this.biome.decalMaterials(), newWaterSettings, newLightingSettings);
    }

    private void rotateObjects(float radians) {
        rotateObjects(this.spawns, radians);
        rotateObjects(this.airAIMarkers, radians);
        rotateObjects(this.amphibiousAIMarkers, radians);
        rotateObjects(this.expansionAIMarkers, radians);
        rotateObjects(this.largeExpansionAIMarkers, radians);
        rotateObjects(this.navalAreaAIMarkers, radians);
        rotateObjects(this.navyAIMarkers, radians);
        rotateObjects(this.landAIMarkers, radians);
        rotateObjects(this.navalRallyMarkers, radians);
        rotateObjects(this.rallyMarkers, radians);
        rotateObjects(this.blankMarkers, radians);
        rotateObjects(this.hydros, radians);
        rotateObjects(this.mexes, radians);
        rotateObjects(this.props, radians);
        rotateObjects(this.decals, radians);
        rotateObjects(this.waveGenerators, radians);
        this.spawns.forEach(spawn -> {
            spawn.setNoRushOffset(spawn.getNoRushOffset().rotate(radians));
        });
        this.armies.forEach(army -> {
            army.getGroups().forEach(group -> {
                rotateObjects(group.getUnits(), radians);
                group.getUnits().forEach(unit -> {
                    unit.setRotation(unit.getRotation() - radians);
                });
            });
        });
        this.props.forEach(prop -> {
            prop.setRotation(prop.getRotation() - radians);
        });
        this.decals.forEach(decal -> {
            decal.setRotation(decal.getRotation().rotateXZ(radians));
            decal.setScale(decal.getScale().rotateXZ(radians));
        });
        this.waveGenerators.forEach(waveGenerator -> {
            waveGenerator.setRotation(waveGenerator.getRotation() - radians);
            waveGenerator.setVelocity(waveGenerator.getVelocity().rotateXZ(radians));
        });
        setHeights();
    }

    private <T extends PositionedObject> void rotateObjects(Collection<T> positionedObjects, float radians) {
        Vector2 halfPoint = new Vector2(this.size / 2.0f, this.size / 2.0f);
        ArrayList arrayList = new ArrayList();
        positionedObjects.forEach(positionedObject -> {
            Vector2 newPosition = new Vector2(positionedObject.getPosition()).subtract(halfPoint).rotate(radians).add(halfPoint);
            positionedObject.setPosition(new Vector3(newPosition));
            if (ImageUtil.inImageBounds(newPosition, this.heightmap)) {
                arrayList.add(positionedObject);
            }
        });
        positionedObjects.clear();
        positionedObjects.addAll(arrayList);
    }

    public void changeMapSize(int contentSize, int boundsSize, Vector2 boundOffset) {
        int oldSize = this.size;
        Vector2 topLeftOffset = new Vector2(boundOffset.x() - (contentSize / 2.0f), boundOffset.y() - (contentSize / 2.0f));
        float contentScale = contentSize / oldSize;
        float boundsScale = boundsSize / contentSize;
        if (contentScale != 1.0f) {
            scaleMapContent(contentScale);
            this.size = contentSize;
            this.playableArea = this.playableArea.multiply(contentScale);
        }
        if (boundsScale != 1.0f && topLeftOffset.x() != SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT && topLeftOffset.y() != SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            scaleMapBounds(boundsScale, topLeftOffset);
            this.playableArea = this.playableArea.add(topLeftOffset.x(), topLeftOffset.y(), topLeftOffset.x(), topLeftOffset.y());
            this.size = boundsSize;
        }
        if (contentScale != 1.0f || (boundsScale != 1.0f && topLeftOffset.x() != SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT && topLeftOffset.y() != SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT)) {
            moveObjects(contentScale, topLeftOffset);
        }
    }

    private void scaleMapContent(float contentScale) {
        scaleBiome(contentScale);
        RescaleOp heightRescale = new RescaleOp(contentScale, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (RenderingHints) null);
        heightRescale.filter(this.heightmap, this.heightmap);
        this.heightmap = ImageUtil.scaleImage(this.heightmap, StrictMath.round((this.heightmap.getWidth() - 1) * contentScale) + 1, StrictMath.round((this.heightmap.getHeight() - 1) * contentScale) + 1);
        this.normalMap = ImageUtil.scaleImage(this.normalMap, StrictMath.round(this.normalMap.getWidth() * contentScale), StrictMath.round(this.normalMap.getHeight() * contentScale));
        this.waterMap = ImageUtil.scaleImage(this.waterMap, StrictMath.round(this.waterMap.getWidth() * contentScale), StrictMath.round(this.waterMap.getHeight() * contentScale));
        this.waterFoamMap = ImageUtil.scaleImage(this.waterFoamMap, StrictMath.round(this.waterFoamMap.getWidth() * contentScale), StrictMath.round(this.waterFoamMap.getHeight() * contentScale));
        this.waterShadowMap = ImageUtil.scaleImage(this.waterShadowMap, StrictMath.round(this.waterShadowMap.getWidth() * contentScale), StrictMath.round(this.waterShadowMap.getHeight() * contentScale));
        this.waterDepthBiasMap = ImageUtil.scaleImage(this.waterDepthBiasMap, StrictMath.round(this.waterDepthBiasMap.getWidth() * contentScale), StrictMath.round(this.waterDepthBiasMap.getHeight() * contentScale));
        this.terrainType = ImageUtil.scaleImage(this.terrainType, StrictMath.round(this.terrainType.getWidth() * contentScale), StrictMath.round(this.terrainType.getHeight() * contentScale));
        this.textureMasksHigh = ImageUtil.scaleImage(this.textureMasksHigh, StrictMath.round(this.textureMasksHigh.getWidth() * contentScale), StrictMath.round(this.textureMasksHigh.getHeight() * contentScale));
        this.textureMasksLow = ImageUtil.scaleImage(this.textureMasksLow, StrictMath.round(this.textureMasksLow.getWidth() * contentScale), StrictMath.round(this.textureMasksLow.getHeight() * contentScale));
        if (this.mapNormalTexture != null) {
            this.mapNormalTexture = ImageUtil.scaleImage(this.mapNormalTexture, StrictMath.round(this.mapNormalTexture.getWidth() * contentScale), StrictMath.round(this.mapNormalTexture.getHeight() * contentScale));
        }
        if (this.mapInfoTexture != null) {
            this.mapInfoTexture = ImageUtil.scaleImage(this.mapInfoTexture, StrictMath.round(this.mapInfoTexture.getWidth() * contentScale), StrictMath.round(this.mapInfoTexture.getHeight() * contentScale));
        }
    }

    private void scaleBiome(float contentScale) {
        WaterSettings oldWaterSettings = this.biome.waterSettings();
        WaterSettings newWaterSettings = new WaterSettings(oldWaterSettings.waterPresent(), oldWaterSettings.elevation() * contentScale, oldWaterSettings.elevationDeep() * contentScale, oldWaterSettings.elevationAbyss() * contentScale, oldWaterSettings.surfaceColor(), oldWaterSettings.colorLerp(), oldWaterSettings.refractionScale(), oldWaterSettings.fresnelBias(), oldWaterSettings.fresnelPower(), oldWaterSettings.unitReflection(), oldWaterSettings.skyReflection(), oldWaterSettings.sunShininess(), oldWaterSettings.sunStrength(), oldWaterSettings.sunDirection(), oldWaterSettings.sunColor(), oldWaterSettings.sunReflection(), oldWaterSettings.sunGlow(), oldWaterSettings.texPathCubemap(), oldWaterSettings.texPathWaterRamp(), oldWaterSettings.waveTextures());
        this.biome = new Biome(this.biome.name(), this.biome.terrainMaterials(), this.biome.propMaterials(), this.biome.decalMaterials(), newWaterSettings, this.biome.lightingSettings());
    }

    private void scaleMapBounds(float boundsScale, Vector2 topLeftOffset) {
        float normalMapScale = this.normalMap.getWidth() / this.size;
        float waterMapScale = this.waterMap.getWidth() / this.size;
        float textureMaskHighScale = this.textureMasksHigh.getWidth() / this.size;
        float textureMaskLowScale = this.textureMasksLow.getWidth() / this.size;
        this.preview = ImageUtil.scaleImage(this.preview, StrictMath.round(256.0f / boundsScale), StrictMath.round(256.0f / boundsScale));
        Vector2 previewOffset = boundsScale > 1.0f ? new Vector2(128.0f - (128.0f / boundsScale), 128.0f - (128.0f / boundsScale)) : new Vector2((-64.0f) / boundsScale, (-64.0f) / boundsScale);
        this.preview = ImageUtil.insertImageIntoNewImageOfSize(this.preview, PreviewGenerator.PREVIEW_SIZE, PreviewGenerator.PREVIEW_SIZE, previewOffset);
        this.heightmap = ImageUtil.insertImageIntoNewImageOfSize(this.heightmap, StrictMath.round((this.heightmap.getWidth() - 1) * boundsScale) + 1, StrictMath.round((this.heightmap.getHeight() - 1) * boundsScale) + 1, topLeftOffset);
        this.normalMap = ImageUtil.insertImageIntoNewImageOfSize(this.normalMap, StrictMath.round(this.normalMap.getWidth() * boundsScale), StrictMath.round(this.normalMap.getHeight() * boundsScale), topLeftOffset.multiply(normalMapScale));
        this.waterMap = ImageUtil.insertImageIntoNewImageOfSize(this.waterMap, StrictMath.round(this.waterMap.getWidth() * boundsScale), StrictMath.round(this.waterMap.getHeight() * boundsScale), topLeftOffset.multiply(waterMapScale));
        Vector2 halvedTopLeftOffset = topLeftOffset.multiply(0.5f);
        this.waterFoamMap = ImageUtil.insertImageIntoNewImageOfSize(this.waterFoamMap, StrictMath.round(this.waterFoamMap.getWidth() * boundsScale), StrictMath.round(this.waterFoamMap.getHeight() * boundsScale), halvedTopLeftOffset);
        this.waterShadowMap = ImageUtil.insertImageIntoNewImageOfSize(this.waterShadowMap, StrictMath.round(this.waterShadowMap.getWidth() * boundsScale), StrictMath.round(this.waterShadowMap.getHeight() * boundsScale), halvedTopLeftOffset);
        this.waterDepthBiasMap = ImageUtil.insertImageIntoNewImageOfSize(this.waterDepthBiasMap, StrictMath.round(this.waterDepthBiasMap.getWidth() * boundsScale), StrictMath.round(this.waterDepthBiasMap.getHeight() * boundsScale), halvedTopLeftOffset);
        this.terrainType = ImageUtil.insertImageIntoNewImageOfSize(this.terrainType, StrictMath.round(this.terrainType.getWidth() * boundsScale), StrictMath.round(this.terrainType.getHeight() * boundsScale), topLeftOffset);
        this.textureMasksHigh = ImageUtil.insertImageIntoNewImageOfSize(this.textureMasksHigh, StrictMath.round(this.textureMasksHigh.getWidth() * boundsScale), StrictMath.round(this.textureMasksHigh.getHeight() * boundsScale), topLeftOffset.multiply(textureMaskHighScale));
        this.textureMasksLow = ImageUtil.insertImageIntoNewImageOfSize(this.textureMasksLow, StrictMath.round(this.textureMasksLow.getWidth() * boundsScale), StrictMath.round(this.textureMasksLow.getHeight() * boundsScale), topLeftOffset.multiply(textureMaskLowScale));
        if (this.mapNormalTexture != null) {
            float mapNormalTextureScale = this.mapNormalTexture.getWidth() / this.size;
            this.mapNormalTexture = ImageUtil.insertImageIntoNewImageOfSize(this.mapNormalTexture, StrictMath.round(this.mapNormalTexture.getWidth() * boundsScale), StrictMath.round(this.mapNormalTexture.getHeight() * boundsScale), topLeftOffset.multiply(mapNormalTextureScale));
        }
        if (this.mapInfoTexture != null) {
            float mapInfoTextureScale = this.mapInfoTexture.getWidth() / this.size;
            this.mapInfoTexture = ImageUtil.insertImageIntoNewImageOfSize(this.mapInfoTexture, StrictMath.round(this.mapInfoTexture.getWidth() * boundsScale), StrictMath.round(this.mapInfoTexture.getHeight() * boundsScale), topLeftOffset.multiply(mapInfoTextureScale));
        }
    }

    private void moveObjects(float contentScale, Vector2 offset) {
        repositionObjects(this.spawns, contentScale, offset);
        repositionObjects(this.airAIMarkers, contentScale, offset);
        repositionObjects(this.amphibiousAIMarkers, contentScale, offset);
        repositionObjects(this.expansionAIMarkers, contentScale, offset);
        repositionObjects(this.largeExpansionAIMarkers, contentScale, offset);
        repositionObjects(this.navalAreaAIMarkers, contentScale, offset);
        repositionObjects(this.navyAIMarkers, contentScale, offset);
        repositionObjects(this.landAIMarkers, contentScale, offset);
        repositionObjects(this.navalRallyMarkers, contentScale, offset);
        repositionObjects(this.rallyMarkers, contentScale, offset);
        repositionObjects(this.blankMarkers, contentScale, offset);
        repositionObjects(this.hydros, contentScale, offset);
        repositionObjects(this.mexes, contentScale, offset);
        repositionObjects(this.props, contentScale, offset);
        repositionObjects(this.decals, contentScale, offset);
        repositionObjects(this.waveGenerators, contentScale, offset);
        this.armies.forEach(army -> {
            army.getGroups().forEach(group -> {
                repositionObjects(group.getUnits(), contentScale, offset);
            });
        });
        this.decals.forEach(decal -> {
            Vector3 scale = decal.getScale();
            decal.setScale(new Vector3(scale.x() * contentScale, scale.y(), scale.z() * contentScale));
            decal.setCutOffLOD(decal.getCutOffLOD() * contentScale);
        });
        setHeights();
    }

    private <T extends PositionedObject> void repositionObjects(Collection<T> positionedObjects, float distanceScale, Vector2 offset) {
        ArrayList arrayList = new ArrayList();
        positionedObjects.forEach(positionedObject -> {
            Vector2 newPosition = new Vector2(positionedObject.getPosition()).multiply(distanceScale).add(offset).roundToNearestHalfPoint();
            positionedObject.setPosition(new Vector3(newPosition));
            if (ImageUtil.inImageBounds(newPosition, this.heightmap)) {
                arrayList.add(positionedObject);
            }
        });
        positionedObjects.clear();
        positionedObjects.addAll(arrayList);
    }

    private void setObjectHeights(Collection<? extends PositionedObject> positionedObjects) {
        positionedObjects.forEach(positionedObject -> {
            Vector2 position = new Vector2(positionedObject.getPosition());
            if (ImageUtil.inImageBounds(position, this.heightmap)) {
                positionedObject.setPosition(new Vector3(position.x(), this.heightmap.getRaster().getPixel((int) position.x(), (int) position.y(), new int[]{0})[0] * this.heightMapScale, position.y()));
            }
        });
    }

    public void setHeights() {
        setObjectHeights(getSpawns());
        setObjectHeights(getAirAIMarkers());
        setObjectHeights(getAmphibiousAIMarkers());
        setObjectHeights(getExpansionAIMarkers());
        setObjectHeights(getLargeExpansionAIMarkers());
        setObjectHeights(getNavalAreaAIMarkers());
        setObjectHeights(getNavyAIMarkers());
        setObjectHeights(getLandAIMarkers());
        setObjectHeights(getNavalRallyMarkers());
        setObjectHeights(getRallyMarkers());
        setObjectHeights(getBlankMarkers());
        setObjectHeights(getHydros());
        setObjectHeights(getMexes());
        setObjectHeights(getProps());
        setObjectHeights(getDecals());
        setObjectHeights(getWaveGenerators());
        this.armies.forEach(army -> {
            army.getGroups().forEach(group -> {
                setObjectHeights(group.getUnits());
            });
        });
    }

    public void addAmphibiousMarker(AIMarker aiMarker) {
        this.amphibiousAIMarkers.add(aiMarker);
    }

    public int getNavyMarkerCount() {
        return this.navyAIMarkers.size();
    }

    public AIMarker getNavyMarker(int i) {
        return this.navyAIMarkers.get(i);
    }

    public AIMarker getNavyMarker(String id) {
        return this.navyAIMarkers.stream().filter(navyMarker -> {
            return navyMarker.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public void addNavyMarker(AIMarker aiMarker) {
        this.navyAIMarkers.add(aiMarker);
    }

    public int getAirMarkerCount() {
        return this.airAIMarkers.size();
    }

    public AIMarker getAirMarker(int i) {
        return this.airAIMarkers.get(i);
    }

    public void addAirMarker(AIMarker aiMarker) {
        this.airAIMarkers.add(aiMarker);
    }

    public AIMarker getAirMarker(String id) {
        return this.airAIMarkers.stream().filter(airMarker -> {
            return airMarker.getId().equals(id);
        }).findFirst().orElse(null);
    }

    public int getRallyMarkerCount() {
        return this.rallyMarkers.size();
    }

    public AIMarker getRallyMarker(int i) {
        return this.rallyMarkers.get(i);
    }

    public void addRallyMarker(AIMarker aiMarker) {
        this.rallyMarkers.add(aiMarker);
    }

    public int getExpansionMarkerCount() {
        return this.expansionAIMarkers.size();
    }

    public AIMarker getExpansionMarker(int i) {
        return this.expansionAIMarkers.get(i);
    }

    public void addExpansionMarker(AIMarker aiMarker) {
        this.expansionAIMarkers.add(aiMarker);
    }

    public int getLargeExpansionMarkerCount() {
        return this.largeExpansionAIMarkers.size();
    }

    public AIMarker getLargeExpansionMarker(int i) {
        return this.largeExpansionAIMarkers.get(i);
    }

    public void addLargeExpansionMarker(AIMarker aiMarker) {
        this.largeExpansionAIMarkers.add(aiMarker);
    }

    public int getNavalAreaMarkerCount() {
        return this.navalAreaAIMarkers.size();
    }

    public AIMarker getNavalAreaMarker(int i) {
        return this.navalAreaAIMarkers.get(i);
    }

    public void addNavalAreaMarker(AIMarker aiMarker) {
        this.navalAreaAIMarkers.add(aiMarker);
    }

    public int getNavyRallyMarkerCount() {
        return this.navalRallyMarkers.size();
    }

    public AIMarker getNavyRallyMarker(int i) {
        return this.navalRallyMarkers.get(i);
    }

    public void addNavyRallyMarker(AIMarker aiMarker) {
        this.navalRallyMarkers.add(aiMarker);
    }

    public int getWaveGeneratorCount() {
        return this.waveGenerators.size();
    }

    public WaveGenerator getWaveGenerator(int i) {
        return this.waveGenerators.get(i);
    }

    public void addWaveGenerator(WaveGenerator waveGenerator) {
        this.waveGenerators.add(waveGenerator);
    }

    public void setWaterShadowMap(BufferedImage waterShadowMap) {
        checkImageSize(waterShadowMap, this.size / 2);
        this.waterShadowMap = waterShadowMap;
    }

    public void setWaterDepthBiasMap(BufferedImage waterDepthBiasMap) {
        checkImageSize(waterDepthBiasMap, this.size / 2);
        this.waterDepthBiasMap = waterDepthBiasMap;
    }

    public void setTextureMasksScaled(BufferedImage textureMasks, FloatMask mask0, FloatMask mask1, FloatMask mask2, FloatMask mask3) {
        int textureMasksWidth = textureMasks.getWidth();
        checkMaskSize(mask0, textureMasksWidth);
        checkMaskSize(mask1, textureMasksWidth);
        checkMaskSize(mask2, textureMasksWidth);
        checkMaskSize(mask3, textureMasksWidth);
        for (int x = 0; x < textureMasksWidth; x++) {
            for (int y = 0; y < textureMasksWidth; y++) {
                int val0 = convertToRawTextureValue(mask0.getPrimitive(x, y));
                int val1 = convertToRawTextureValue(mask1.getPrimitive(x, y));
                int val2 = convertToRawTextureValue(mask2.getPrimitive(x, y));
                int val3 = convertToRawTextureValue(mask3.getPrimitive(x, y));
                textureMasks.getRaster().setPixel(x, y, new int[]{val0, val1, val2, val3});
            }
        }
    }

    public void setTextureMasksScaled(BufferedImage textureMasks, Vector4Mask mask) {
        int textureMasksWidth = textureMasks.getWidth();
        checkMaskSize(mask, textureMasksWidth);
        for (int x = 0; x < textureMasksWidth; x++) {
            for (int y = 0; y < textureMasksWidth; y++) {
                Vector4 vector = mask.get(x, y);
                int val0 = convertToRawTextureValue(vector.x());
                int val1 = convertToRawTextureValue(vector.y());
                int val2 = convertToRawTextureValue(vector.z());
                int val3 = convertToRawTextureValue(vector.w());
                textureMasks.getRaster().setPixel(x, y, new int[]{val0, val1, val2, val3});
            }
        }
    }

    public void setTerrainType(BufferedImage terrainType, IntegerMask mask) {
        int terrainTypeWidth = terrainType.getWidth();
        checkMaskSize(mask, terrainTypeWidth);
        for (int x = 0; x < terrainTypeWidth; x++) {
            for (int y = 0; y < terrainTypeWidth; y++) {
                int val0 = mask.getPrimitive(x, y);
                terrainType.getRaster().setPixel(x, y, new int[]{val0});
            }
        }
    }

    public void setWaterShadowMap(BufferedImage waterShadowMap, FloatMask mask) {
        int waterShadowMapWidth = waterShadowMap.getWidth();
        checkMaskSize(mask, waterShadowMapWidth);
        for (int x = 0; x < waterShadowMapWidth; x++) {
            for (int y = 0; y < waterShadowMapWidth; y++) {
                int val0 = (int) (mask.getPrimitive(x, y) * 255.0f);
                waterShadowMap.getRaster().setPixel(x, y, new int[]{val0});
            }
        }
    }

    private int convertToRawTextureValue(float value) {
        if (value > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return StrictMath.round((StrictMath.min(1.0f, value) * 127.0f) + 128.0f);
        }
        return 0;
    }

    public void writeToFile(Path path) {
        Files.deleteIfExists(path);
        File outFile = path.toFile();
        outFile.createNewFile();
        FileOutputStream out = new FileOutputStream(outFile);
        out.write(toString().getBytes());
        out.flush();
        out.close();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("SCMap%n", new Object[0]));
        stringBuilder.append(String.format("Biome: %s%n", this.biome.name()));
        stringBuilder.append(String.format("%s%n", this.biome.lightingSettings()));
        stringBuilder.append(String.format("%s%n", this.biome.waterSettings()));
        stringBuilder.append(String.format("Terrain Materials: %s%n", this.biome.terrainMaterials()));
        stringBuilder.append(String.format("Size: %d%n", Integer.valueOf(this.size)));
        int numDecals = this.decals.size();
        for (int i = 0; i < numDecals; i++) {
            stringBuilder.append(String.format("Decal %d: %s%n", Integer.valueOf(i), this.decals.get(i)));
        }
        int numProps = this.props.size();
        for (int i2 = 0; i2 < numProps; i2++) {
            stringBuilder.append(String.format("Prop %d: %s%n", Integer.valueOf(i2), this.props.get(i2)));
        }
        return stringBuilder.toString();
    }
}
