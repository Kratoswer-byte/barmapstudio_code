package com.faforever.neroxis.exporter;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.CubeMap;
import com.faforever.neroxis.map.Decal;
import com.faforever.neroxis.map.DecalGroup;
import com.faforever.neroxis.map.DecalType;
import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SkyBox;
import com.faforever.neroxis.map.WaveGenerator;
import com.faforever.neroxis.util.EndianSwapper;
import com.faforever.neroxis.util.ImageUtil;
import com.faforever.neroxis.util.ResourceUtil;
import com.faforever.neroxis.util.dds.DDSHeader;
import com.faforever.neroxis.util.jsquish.Squish;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.serial.biome.WaterSettings;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.jspecify.annotations.NullUnmarked;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/SCMapExporter.class */
@NullUnmarked
public class SCMapExporter {
    public static final String PBR_DDS = "roughnessAndHeight.dds";
    public static final String MAP_INFO_DDS = "mapInfo.dds";
    public static final String MAP_NORMAL_DDS = "mapNormal.dds";
    private static DataOutputStream out;

    public static void exportSCMAP(Path folderPath, SCMap map) throws IOException {
        File file = folderPath.resolve(map.getFilePrefix() + ".scmap").toFile();
        file.createNewFile();
        out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        writeInt(SCMap.SIGNATURE);
        writeInt(2);
        writeInt(-1091567891);
        writeInt(2);
        writeFloat(map.getSize());
        writeFloat(map.getSize());
        writeInt(0);
        writeShort((short) 0);
        DDSHeader previewDDSHeader = new DDSHeader();
        previewDDSHeader.setWidth(map.getPreview().getWidth());
        previewDDSHeader.setHeight(map.getPreview().getHeight());
        previewDDSHeader.setRGBBitCount(32);
        previewDDSHeader.setRBitMask(16711680);
        previewDDSHeader.setGBitMask(65280);
        previewDDSHeader.setBBitMask(255);
        previewDDSHeader.setABitMask(-16777216);
        writeRawImage(map.getPreview(), previewDDSHeader);
        writeInt(map.getMinorVersion());
        writeInt(map.getSize());
        writeInt(map.getSize());
        writeFloat(map.getHeightMapScale());
        writeShorts(map.getHeightmap().getData().getDataBuffer().getData());
        writeByte((byte) 0);
        writeStringNull(map.getTerrainShaderPath());
        writeStringNull(map.getBackgroundPath());
        writeStringNull(map.getSkyCubePath());
        List<CubeMap> cubeMaps = map.getBiome().terrainMaterials().cubeMaps();
        writeInt(cubeMaps.size());
        for (CubeMap cubeMap : cubeMaps) {
            writeStringNull(cubeMap.name());
            writeStringNull(cubeMap.path());
        }
        writeLightingSettings(map.getBiome().lightingSettings());
        writeWaterSettings(map.getBiome().waterSettings());
        writeInt(map.getWaveGeneratorCount());
        for (WaveGenerator waveGenerator : map.getWaveGenerators()) {
            writeWaveGenerator(waveGenerator);
        }
        TerrainMaterials mapTerrainMaterials = map.getBiome().terrainMaterials();
        writeInt(map.getCartographicContourInterval());
        writeInt(map.getCartographicDeepWaterColor());
        writeInt(map.getCartographicMapContourColor());
        writeInt(map.getCartographicMapShoreColor());
        writeInt(map.getCartographicMapLandStartColor());
        writeInt(map.getCartographicMapLandEndColor());
        if (map.getMinorVersion() > 56) {
            writeFloat(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        }
        for (int i = 0; i < 10; i++) {
            if (i == 8 && SCMap.PBR_SHADER_NAME.equals(map.getTerrainShaderPath())) {
                writeStringNull(Path.of("/maps", map.getFolderName(), "env", "layers", MAP_INFO_DDS).toString().replace("\\", "/"));
                writeFloat(map.getSize() + 1);
            } else if (i == 9 && SCMap.PBR_SHADER_NAME.equals(map.getTerrainShaderPath())) {
                writeStringNull(Path.of("/maps", map.getFolderName(), "env", "layers", PBR_DDS).toString().replace("\\", "/"));
                writeFloat(map.getSize() + 1);
            } else {
                TerrainMaterials.TextureScale textureScale = mapTerrainMaterials.textures().get(i);
                writeStringNull(textureScale.path());
                writeFloat(textureScale.scale());
            }
        }
        for (int i2 = 0; i2 < 9; i2++) {
            TerrainMaterials.TextureScale textureScale2 = mapTerrainMaterials.normals().get(i2);
            if (i2 == 8 && SCMap.PBR_SHADER_NAME.equals(map.getTerrainShaderPath())) {
                writeStringNull(Path.of("/maps", map.getFolderName(), "env", "layers", MAP_NORMAL_DDS).toString().replace("\\", "/"));
                writeFloat(map.getSize() + 1);
            } else {
                writeStringNull(textureScale2.path());
                writeFloat(textureScale2.scale());
            }
        }
        writeInt(0);
        writeInt(0);
        if (!map.isUnexplored()) {
            writeInt(map.getDecalCount());
            for (Decal decal : map.getDecals()) {
                writeDecal(decal, map.getDecals().indexOf(decal));
            }
        } else {
            writeInt(0);
        }
        writeInt(map.getDecalGroupCount());
        for (DecalGroup decalGroup : map.getDecalGroups()) {
            writeDecalGroup(decalGroup, map.getDecalGroups().indexOf(decalGroup));
        }
        writeInt(map.getSize());
        writeInt(map.getSize());
        DDSHeader normalDDSHeader = new DDSHeader();
        normalDDSHeader.setWidth(map.getNormalMap().getWidth());
        normalDDSHeader.setHeight(map.getNormalMap().getHeight());
        normalDDSHeader.setFourCC("DXT5");
        writeInt(1);
        writeCompressedImage(map.getNormalMap(), normalDDSHeader);
        DDSHeader textureMaskLowDDSHeader = new DDSHeader();
        textureMaskLowDDSHeader.setWidth(map.getTextureMasksLow().getWidth());
        textureMaskLowDDSHeader.setHeight(map.getTextureMasksLow().getHeight());
        textureMaskLowDDSHeader.setRGBBitCount(32);
        textureMaskLowDDSHeader.setRBitMask(16711680);
        textureMaskLowDDSHeader.setGBitMask(65280);
        textureMaskLowDDSHeader.setBBitMask(255);
        textureMaskLowDDSHeader.setABitMask(-16777216);
        writeRawImage(map.getTextureMasksLow(), textureMaskLowDDSHeader);
        DDSHeader textureMaskHighDDSHeader = new DDSHeader();
        textureMaskHighDDSHeader.setWidth(map.getTextureMasksHigh().getWidth());
        textureMaskHighDDSHeader.setHeight(map.getTextureMasksHigh().getHeight());
        textureMaskHighDDSHeader.setRGBBitCount(32);
        textureMaskHighDDSHeader.setRBitMask(16711680);
        textureMaskHighDDSHeader.setGBitMask(65280);
        textureMaskHighDDSHeader.setBBitMask(255);
        textureMaskHighDDSHeader.setABitMask(-16777216);
        writeRawImage(map.getTextureMasksHigh(), textureMaskHighDDSHeader);
        DDSHeader waterDDSHeader = new DDSHeader();
        waterDDSHeader.setWidth(map.getWaterMap().getWidth());
        waterDDSHeader.setHeight(map.getWaterMap().getHeight());
        waterDDSHeader.setFourCC("DXT5");
        writeInt(1);
        writeCompressedImage(map.getWaterMap(), waterDDSHeader);
        writeImageBytes(map.getWaterFoamMap());
        writeImageBytes(map.getWaterShadowMap());
        writeImageBytes(map.getWaterDepthBiasMap());
        writeImageBytes(map.getTerrainType());
        if (map.getMinorVersion() >= 60) {
            writeSkyBox(map.getSkyBox());
        }
        if (!map.isUnexplored()) {
            writeInt(map.getPropCount());
            for (Prop prop : map.getProps()) {
                writeProp(prop);
            }
        } else {
            writeInt(0);
        }
        out.flush();
        out.close();
    }

    public static void exportSCMapString(Path folderPath, String mapname, SCMap map) {
        map.writeToFile(folderPath.resolve(mapname).resolve("debug").resolve(mapname + ".txt"));
    }

    public static void exportPreview(Path folderPath, SCMap map) throws IOException {
        File previewFile = folderPath.resolve(map.getFilePrefix() + "_preview.png").toFile();
        BufferedImage mapPreview = map.getPreview();
        BufferedImage previewCopy = new BufferedImage(mapPreview.getWidth(), mapPreview.getHeight(), 2);
        Graphics previewCopyGraphics = previewCopy.getGraphics();
        previewCopyGraphics.drawImage(mapPreview, 0, 0, (ImageObserver) null);
        previewCopyGraphics.dispose();
        try {
            ImageIO.write(PreviewGenerator.addMarkers(previewCopy, map), "png", previewFile);
        } catch (IOException e) {
            System.out.print("Could not write the preview image\n" + String.valueOf(e));
        }
    }

    public static void exportNormals(Path folderPath, SCMap map) throws IOException {
        float size = map.getPlayableArea().w() - map.getPlayableArea().x();
        Vector2 topLeftOffset = new Vector2(map.getPlayableArea().x(), map.getPlayableArea().y());
        byte[] compressedNormal = map.getCompressedNormal();
        Path decalsPath = Paths.get("env", "decals");
        Path decalParent = Paths.get("/maps", new String[0]).resolve(map.getFolderName());
        Path decalPath = decalsPath.resolve(String.format("map_normal.%s", "dds"));
        Path writingPath = folderPath.resolve(decalPath);
        Files.createDirectories(writingPath.getParent(), new FileAttribute[0]);
        map.getDecals().add(new Decal(decalParent.resolve(decalPath).toString().replace('\\', '/'), topLeftOffset, new Vector3(), size, 1000.0f));
        try {
            Files.write(writingPath, compressedNormal, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.print("Could not write the normal map image\n" + String.valueOf(e));
        }
    }

    public static void exportShadows(Path folderPath, SCMap map) throws IOException {
        float size = map.getPlayableArea().w() - map.getPlayableArea().x();
        Vector2 topLeftOffset = new Vector2(map.getPlayableArea().x(), map.getPlayableArea().y());
        byte[] compressedShadows = map.getCompressedShadows();
        Path decalsPath = Paths.get("env", "decals");
        Path decalParent = Paths.get("/maps", new String[0]).resolve(map.getFolderName());
        Path decalPath = decalsPath.resolve(String.format("map_shadows.%s", "dds"));
        Path writingPath = folderPath.resolve(decalPath);
        Files.createDirectories(writingPath.getParent(), new FileAttribute[0]);
        Decal shadowDecal = new Decal(decalParent.resolve(decalPath).toString().replace('\\', '/'), topLeftOffset, new Vector3(), size, 1000.0f);
        shadowDecal.setType(DecalType.Known.WATER_ALBEDO);
        map.getDecals().add(shadowDecal);
        try {
            Files.write(writingPath, compressedShadows, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.print("Could not write the shadow map image\n" + String.valueOf(e));
        }
    }

    public static void exportMapInfoTexture(Path folderPath, SCMap map) throws IOException {
        BufferedImage image = map.getMapInfoTexture();
        Path textureDirectory = Paths.get("env", "layers");
        Path filePath = textureDirectory.resolve(MAP_INFO_DDS);
        Path writingPath = folderPath.resolve(filePath);
        Files.createDirectories(writingPath.getParent(), new FileAttribute[0]);
        try {
            ImageUtil.writeRawDDS(image, writingPath);
        } catch (IOException e) {
            System.out.print("Could not write the map info texture\n" + String.valueOf(e));
        }
    }

    public static void exportMapNormalTexture(Path folderPath, SCMap map) throws IOException {
        BufferedImage image = map.getMapNormalTexture();
        Path textureDirectory = Paths.get("env", "layers");
        Path filePath = textureDirectory.resolve(MAP_NORMAL_DDS);
        Path writingPath = folderPath.resolve(filePath);
        Files.createDirectories(writingPath.getParent(), new FileAttribute[0]);
        try {
            ImageUtil.writeRawDDS(image, writingPath);
        } catch (IOException e) {
            System.out.print("Could not write the map normal texture\n" + String.valueOf(e));
        }
    }

    public static void exportPBR(Path folderPath, SCMap map) throws IOException {
        Path textureDirectory = Path.of("env", "layers");
        Path filePath = textureDirectory.resolve(PBR_DDS);
        Path outPath = folderPath.resolve(filePath);
        try {
            InputStream inputStream = ResourceUtil.getResourceAsStream("/images/roughnessAndHeight.dds");
            try {
                Files.createDirectories(outPath.getParent(), new FileAttribute[0]);
                Files.copy(inputStream, outPath, StandardCopyOption.REPLACE_EXISTING);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            System.out.print("Could not copy the pbr texture\n" + String.valueOf(e));
        }
    }

    private static void writeFloat(float f) throws IOException {
        out.writeInt(EndianSwapper.swap(Float.floatToRawIntBits(f)));
    }

    private static void writeInt(int i) throws IOException {
        out.writeInt(EndianSwapper.swap(i));
    }

    private static void writeShort(short s) throws IOException {
        out.writeShort(EndianSwapper.swap(s));
    }

    private static void writeByte(byte b) throws IOException {
        out.writeByte(b);
    }

    private static void writeBytes(byte[] b) throws IOException {
        out.write(b);
    }

    private static void writeShorts(short[] s) throws IOException {
        for (short value : s) {
            writeShort(value);
        }
    }

    private static void writeInts(int[] data) throws IOException {
        for (int i : data) {
            writeInt(i);
        }
    }

    private static void writeStringNull(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.writeByte(s.charAt(i));
        }
        out.writeByte(0);
    }

    private static void writeString(String s) throws IOException {
        writeInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            out.writeByte(s.charAt(i));
        }
    }

    private static void writeVector3f(Vector3 v) throws IOException {
        writeFloat(v.x());
        writeFloat(v.y());
        writeFloat(v.z());
    }

    private static void writeVector4f(Vector4 v) throws IOException {
        writeFloat(v.x());
        writeFloat(v.y());
        writeFloat(v.z());
        writeFloat(v.w());
    }

    private static void writeVector2f(Vector2 v) throws IOException {
        writeFloat(v.x());
        writeFloat(v.y());
    }

    private static void writeProp(Prop prop) throws IOException {
        writeStringNull(prop.getPath());
        writeVector3f(prop.getPosition());
        writeVector3f(new Vector3((float) StrictMath.cos(prop.getRotation()), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) StrictMath.sin(prop.getRotation())));
        writeVector3f(new Vector3(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
        writeVector3f(new Vector3((float) (-StrictMath.sin(prop.getRotation())), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, (float) StrictMath.cos(prop.getRotation())));
        writeVector3f(new Vector3(1.0f, 1.0f, 1.0f));
    }

    private static void writeDecal(Decal decal, int id) throws IOException {
        writeInt(id);
        writeInt(decal.getType().typeNum());
        writeInt(2);
        writeString(decal.getPath());
        writeString("");
        writeVector3f(decal.getScale());
        writeVector3f(decal.getPosition());
        writeVector3f(decal.getRotation());
        writeFloat(decal.getCutOffLOD());
        writeFloat(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        writeInt(-1);
    }

    private static void writeDecalGroup(DecalGroup decalGroup, int id) throws IOException {
        writeInt(id);
        writeStringNull(decalGroup.name());
        writeInt(decalGroup.data().size());
        Iterator<Integer> it = decalGroup.data().iterator();
        while (it.hasNext()) {
            int datum = it.next().intValue();
            writeInt(datum);
        }
    }

    private static void writeWaveGenerator(WaveGenerator waveGenerator) throws IOException {
        writeStringNull(waveGenerator.getTextureName());
        writeStringNull(waveGenerator.getRampName());
        writeVector3f(waveGenerator.getPosition());
        writeFloat(waveGenerator.getRotation());
        writeVector3f(waveGenerator.getVelocity());
        writeFloat(waveGenerator.getLifeTimeFirst());
        writeFloat(waveGenerator.getLifeTimeSecond());
        writeFloat(waveGenerator.getPeriodFirst());
        writeFloat(waveGenerator.getPeriodSecond());
        writeFloat(waveGenerator.getScaleFirst());
        writeFloat(waveGenerator.getScaleSecond());
        writeFloat(waveGenerator.getFrameCount());
        writeFloat(waveGenerator.getFrameRateFirst());
        writeFloat(waveGenerator.getFrameRateSecond());
        writeFloat(waveGenerator.getStripCount());
    }

    private static void writeWaterSettings(WaterSettings waterSettings) throws IOException {
        writeByte((byte) (waterSettings.waterPresent() ? 1 : 0));
        writeFloat(waterSettings.elevation());
        writeFloat(waterSettings.elevationDeep());
        writeFloat(waterSettings.elevationAbyss());
        writeVector3f(waterSettings.surfaceColor());
        writeVector2f(waterSettings.colorLerp());
        writeFloat(waterSettings.refractionScale());
        writeFloat(waterSettings.fresnelBias());
        writeFloat(waterSettings.fresnelPower());
        writeFloat(waterSettings.unitReflection());
        writeFloat(waterSettings.skyReflection());
        writeFloat(waterSettings.sunShininess());
        writeFloat(waterSettings.sunStrength());
        writeVector3f(waterSettings.sunDirection());
        writeVector3f(waterSettings.sunColor());
        writeFloat(waterSettings.sunReflection());
        writeFloat(waterSettings.sunGlow());
        writeStringNull(waterSettings.texPathCubemap());
        writeStringNull(waterSettings.texPathWaterRamp());
        Iterator<WaterSettings.WaveTexture> it = waterSettings.waveTextures().iterator();
        while (it.hasNext()) {
            writeFloat(it.next().normalRepeat());
        }
        for (WaterSettings.WaveTexture waveTexture : waterSettings.waveTextures()) {
            writeVector2f(waveTexture.normalMovement());
            writeStringNull(waveTexture.texPath());
        }
    }

    private static void writeLightingSettings(LightingSettings lightingSettings) throws IOException {
        writeFloat(lightingSettings.lightingMultiplier());
        writeVector3f(lightingSettings.sunDirection());
        writeVector3f(lightingSettings.sunAmbience());
        writeVector3f(lightingSettings.sunColor());
        writeVector3f(lightingSettings.shadowFillColor());
        writeVector4f(lightingSettings.specularColor());
        writeFloat(lightingSettings.bloom());
        writeVector3f(lightingSettings.fogColor());
        writeFloat(lightingSettings.fogStart());
        writeFloat(lightingSettings.fogEnd());
    }

    private static void writeSkyBox(SkyBox skyBox) throws IOException {
        writeVector3f(skyBox.getPosition());
        writeFloat(skyBox.getHorizonHeight());
        writeFloat(skyBox.getScale());
        writeFloat(skyBox.getSubHeight());
        writeInt(skyBox.getSubDivAx());
        writeInt(skyBox.getSubDivHeight());
        writeFloat(skyBox.getZenithHeight());
        writeVector3f(skyBox.getHorizonColor());
        writeVector3f(skyBox.getZenithColor());
        writeFloat(skyBox.getDecalGlowMultiplier());
        writeStringNull(skyBox.getAlbedo());
        writeStringNull(skyBox.getGlow());
        writeInt(skyBox.getPlanets().length);
        for (SkyBox.Planet planet : skyBox.getPlanets()) {
            writeVector3f(planet.getPosition());
            writeFloat(planet.getRotation());
            writeVector2f(planet.getScale());
            writeVector4f(planet.getUv());
        }
        writeByte((byte) skyBox.getMidRgbColor().getRed());
        writeByte((byte) skyBox.getMidRgbColor().getBlue());
        writeByte((byte) skyBox.getMidRgbColor().getGreen());
        writeFloat(skyBox.getCirrusMultiplier());
        writeVector3f(skyBox.getCirrusColor());
        writeStringNull(skyBox.getCirrusTexture());
        writeInt(skyBox.getCirrusLayers().length);
        for (SkyBox.Cirrus cirrus : skyBox.getCirrusLayers()) {
            writeVector2f(cirrus.getFrequency());
            writeFloat(cirrus.getSpeed());
            writeVector2f(cirrus.getDirection());
        }
        writeFloat(skyBox.getClouds7());
    }

    private static void writeRawImage(BufferedImage image, DDSHeader ddsHeader) throws IOException {
        byte[] headerBytes = ddsHeader.toBytes();
        writeInt(headerBytes.length + (image.getWidth() * image.getHeight() * 4));
        writeBytes(headerBytes);
        writeInts(image.getData().getDataBuffer().getData());
    }

    private static void writeCompressedImage(BufferedImage image, DDSHeader ddsHeader) throws IOException {
        int[] imageData = image.getData().getDataBuffer().getData();
        byte[] headerBytes = ddsHeader.toBytes();
        ByteBuffer imageBytes = ByteBuffer.allocate(imageData.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        imageBytes.asIntBuffer().put(imageData);
        byte[] compressedData = Squish.compressImage(imageBytes.array(), ddsHeader.getWidth(), ddsHeader.getHeight(), null, Squish.CompressionType.DXT5);
        writeInt(headerBytes.length + compressedData.length);
        writeBytes(headerBytes);
        writeBytes(compressedData);
    }

    private static void writeImageBytes(BufferedImage image) throws IOException {
        writeBytes(image.getData().getDataBuffer().getData());
    }
}
