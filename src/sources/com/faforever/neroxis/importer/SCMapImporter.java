package com.faforever.neroxis.importer;

import com.faforever.neroxis.biomes.Biome;
import com.faforever.neroxis.map.CubeMap;
import com.faforever.neroxis.map.Decal;
import com.faforever.neroxis.map.DecalGroup;
import com.faforever.neroxis.map.DecalType;
import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SkyBox;
import com.faforever.neroxis.map.WaveGenerator;
import com.faforever.neroxis.util.EndianSwapper;
import com.faforever.neroxis.util.dds.DDSHeader;
import com.faforever.neroxis.util.jsquish.Squish;
import com.faforever.neroxis.util.serial.biome.DecalMaterials;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.serial.biome.PropMaterials;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.serial.biome.WaterSettings;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import com.faforever.neroxis.util.vector.Vector4;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NullUnmarked;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/importer/SCMapImporter.class */
@NullUnmarked
public class SCMapImporter {
    private static DataInputStream in;

    public static SCMap importSCMAP(Path folderPath) throws IOException {
        File dir = folderPath.toFile();
        File[] mapFiles = dir.listFiles((dir1, filename) -> {
            return filename.endsWith(".scmap");
        });
        if (mapFiles == null || mapFiles.length == 0) {
            throw new IllegalArgumentException("Folder does not contain an scmap file");
        }
        File file = mapFiles[0];
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        if (readInt() != 443572557) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        if (readInt() != 2) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        if (readInt() != -1091567891) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        if (readInt() != 2) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        readFloat();
        readFloat();
        if (readInt() != 0) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        if (readShort() != 0) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        int[] previewImageData = readRawImage();
        int version = readInt();
        if (version != 56 && version != 60) {
            throw new UnsupportedEncodingException(String.format("SCMap version %d not supported", Integer.valueOf(version)));
        }
        int widthInt = readInt();
        int heightInt = readInt();
        float heightMapScale = readFloat();
        short[] heightMapData = readShorts((widthInt + 1) * (heightInt + 1));
        if (readByte() != 0) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        String shaderPath = readStringNull();
        String backgroundPath = readStringNull();
        String skyCubePath = readStringNull();
        int cubeMapCount = readInt();
        CubeMap[] cubeMaps = new CubeMap[cubeMapCount];
        for (int i = 0; i < cubeMapCount; i++) {
            String name = readStringNull();
            String path = readStringNull();
            cubeMaps[i] = new CubeMap(name, path);
        }
        LightingSettings mapLightingSettings = readLightingSettings();
        WaterSettings mapWaterSettings = readWaterSettings();
        int waveGeneratorCount = readInt();
        WaveGenerator[] waveGenerators = new WaveGenerator[waveGeneratorCount];
        for (int i2 = 0; i2 < waveGeneratorCount; i2++) {
            waveGenerators[i2] = readWaveGenerator();
        }
        int miniMapContourInterval = readInt();
        int miniMapDeepWaterColor = readInt();
        int miniMapContourColor = readInt();
        int miniMapShoreColor = readInt();
        int miniMapLandStartColor = readInt();
        int miniMapLandEndColor = readInt();
        if (version > 56) {
            readFloat();
        }
        TerrainMaterials.TextureScale[] textures = new TerrainMaterials.TextureScale[10];
        for (int i3 = 0; i3 < 10; i3++) {
            String texturePath = readStringNull();
            float textureScale = readFloat();
            textures[i3] = new TerrainMaterials.TextureScale(texturePath, textureScale);
        }
        TerrainMaterials.TextureScale[] normals = new TerrainMaterials.TextureScale[9];
        for (int i4 = 0; i4 < 9; i4++) {
            String path2 = readStringNull();
            float scale = readFloat();
            normals[i4] = new TerrainMaterials.TextureScale(path2, scale);
        }
        TerrainMaterials mapTerrainMaterials = new TerrainMaterials(List.of((Object[]) cubeMaps), List.of((Object[]) textures), List.of((Object[]) normals), List.of(), List.of());
        readInt();
        readInt();
        int decalCount = readInt();
        Decal[] decals = new Decal[decalCount];
        for (int i5 = 0; i5 < decalCount; i5++) {
            decals[i5] = readDecal();
        }
        int groupCount = readInt();
        DecalGroup[] decalGroups = new DecalGroup[groupCount];
        for (int i6 = 0; i6 < groupCount; i6++) {
            decalGroups[i6] = readDecalGroup();
        }
        readInt();
        readInt();
        if (readInt() != 1) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        int[] normalMapData = readCompressedImage();
        int[] textureMaskLowData = readRawImage();
        int[] textureMaskHighData = readRawImage();
        if (readInt() != 1) {
            throw new UnsupportedEncodingException("File not valid SCMap");
        }
        int[] waterMapData = readCompressedImage();
        int halfSize = (heightInt / 2) * (widthInt / 2);
        byte[] waterFoamMaskData = readBytes(halfSize);
        byte[] waterFlatnessData = readBytes(halfSize);
        byte[] waterDepthBiasMaskData = readBytes(halfSize);
        byte[] terrainTypeData = readBytes(widthInt * heightInt);
        SkyBox skyBox = null;
        if (version >= 60) {
            skyBox = readSkyBox();
        }
        int propCount = readInt();
        Prop[] props = new Prop[propCount];
        for (int i7 = 0; i7 < propCount; i7++) {
            props[i7] = readProp();
        }
        in.close();
        PropMaterials propMaterials = new PropMaterials(List.of(), List.of(), List.of());
        DecalMaterials decalMaterials = new DecalMaterials(List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        SCMap map = new SCMap(widthInt, new Biome(null, mapTerrainMaterials, propMaterials, decalMaterials, mapWaterSettings, mapLightingSettings));
        map.setFilePrefix(file.getName().replace(".scmap", ""));
        map.setMinorVersion(version);
        map.setTerrainShaderPath(shaderPath);
        map.setBackgroundPath(backgroundPath);
        map.setSkyCubePath(skyCubePath);
        map.setHeightMapScale(heightMapScale);
        map.setSkyBox(skyBox);
        map.setCartographicContourInterval(miniMapContourInterval);
        map.setCartographicDeepWaterColor(miniMapDeepWaterColor);
        map.setCartographicMapContourColor(miniMapContourColor);
        map.setCartographicMapShoreColor(miniMapShoreColor);
        map.setCartographicMapLandStartColor(miniMapLandStartColor);
        map.setCartographicMapLandEndColor(miniMapLandEndColor);
        map.setPreview(getBufferedImageFromRawData(2, previewImageData));
        map.setHeightmap(getBufferedImageFromRawData(11, getIntegerArray(heightMapData)));
        map.setNormalMap(getBufferedImageFromRawData(2, normalMapData));
        map.setTextureMasksLow(getBufferedImageFromRawData(2, textureMaskLowData));
        map.setTextureMasksHigh(getBufferedImageFromRawData(2, textureMaskHighData));
        map.setWaterMap(getBufferedImageFromRawData(2, waterMapData));
        map.setWaterFoamMap(getBufferedImageFromRawData(10, getIntegerArray(waterFoamMaskData)));
        map.setWaterShadowMap(getBufferedImageFromRawData(10, getIntegerArray(waterFlatnessData)));
        map.setWaterDepthBiasMap(getBufferedImageFromRawData(10, getIntegerArray(waterDepthBiasMaskData)));
        map.setTerrainType(getBufferedImageFromRawData(10, getIntegerArray(terrainTypeData)));
        for (WaveGenerator waveGenerator : waveGenerators) {
            map.addWaveGenerator(waveGenerator);
        }
        for (Prop prop : props) {
            map.addProp(prop);
        }
        for (Decal decal : decals) {
            map.addDecal(decal);
        }
        for (DecalGroup decalGroup : decalGroups) {
            map.addDecalGroup(decalGroup);
        }
        return map;
    }

    private static float readFloat() throws IOException {
        return Float.intBitsToFloat(EndianSwapper.swap(in.readInt()));
    }

    private static int readInt() throws IOException {
        return EndianSwapper.swap(in.readInt());
    }

    private static short readShort() throws IOException {
        return EndianSwapper.swap(in.readShort());
    }

    private static byte readByte() throws IOException {
        return in.readByte();
    }

    private static byte[] readBytes(int numBytes) throws IOException {
        byte[] readBytes = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            readBytes[i] = readByte();
        }
        return readBytes;
    }

    private static short[] readShorts(int numShorts) throws IOException {
        short[] readShorts = new short[numShorts];
        for (int i = 0; i < numShorts; i++) {
            readShorts[i] = readShort();
        }
        return readShorts;
    }

    private static int[] readInts(int numInts) throws IOException {
        int[] readInts = new int[numInts];
        for (int i = 0; i < numInts; i++) {
            readInts[i] = readInt();
        }
        return readInts;
    }

    private static String readStringNull() throws IOException {
        StringBuilder readString = new StringBuilder();
        byte b = readByte();
        while (true) {
            byte read = b;
            if (read != 0) {
                readString.append((char) read);
                b = readByte();
            } else {
                return readString.toString();
            }
        }
    }

    private static String readString(int length) throws IOException {
        StringBuilder readString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte read = readByte();
            readString.append((char) read);
        }
        return readString.toString();
    }

    private static Vector3 readVector3f() throws IOException {
        return new Vector3(readFloat(), readFloat(), readFloat());
    }

    private static Vector4 readVector4f() throws IOException {
        return new Vector4(readFloat(), readFloat(), readFloat(), readFloat());
    }

    private static Vector2 readVector2f() throws IOException {
        return new Vector2(readFloat(), readFloat());
    }

    private static LightingSettings readLightingSettings() throws IOException {
        float lightingMultiplier = readFloat();
        Vector3 sunDirection = readVector3f();
        Vector3 sunAmbience = readVector3f();
        Vector3 sunColor = readVector3f();
        Vector3 shadowFillColor = readVector3f();
        Vector4 specularColor = readVector4f();
        float bloom = readFloat();
        Vector3 fogColor = readVector3f();
        float fogStart = readFloat();
        float fogEnd = readFloat();
        return new LightingSettings(lightingMultiplier, sunDirection, sunAmbience, sunColor, shadowFillColor, specularColor, bloom, fogColor, fogStart, fogEnd);
    }

    private static WaterSettings readWaterSettings() throws IOException {
        boolean waterPresent = readByte() == 1;
        float elevation = readFloat();
        float elevationDeep = readFloat();
        float elevationAbyss = readFloat();
        Vector3 surfaceColor = readVector3f();
        Vector2 colorLerp = readVector2f();
        float refractionScale = readFloat();
        float fresnelBias = readFloat();
        float fresnelPower = readFloat();
        float unitReflection = readFloat();
        float skyReflection = readFloat();
        float sunShininess = readFloat();
        float sunStrength = readFloat();
        Vector3 sunDirection = readVector3f();
        Vector3 sunColor = readVector3f();
        float sunReflection = readFloat();
        float sunGlow = readFloat();
        String texPathCubeMap = readStringNull();
        String texPathWaterRamp = readStringNull();
        float[] waveNormalRepeats = new float[4];
        for (int i = 0; i < 4; i++) {
            waveNormalRepeats[i] = readFloat();
        }
        List<WaterSettings.WaveTexture> waveTextures = new ArrayList<>();
        for (int i2 = 0; i2 < 4; i2++) {
            Vector2 waveNormalMovement = readVector2f();
            String waveTexPath = readStringNull();
            waveTextures.add(new WaterSettings.WaveTexture(waveNormalMovement, waveTexPath, waveNormalRepeats[i2]));
        }
        return new WaterSettings(waterPresent, elevation, elevationDeep, elevationAbyss, surfaceColor, colorLerp, refractionScale, fresnelBias, fresnelPower, unitReflection, skyReflection, sunShininess, sunStrength, sunDirection, sunColor, sunReflection, sunGlow, texPathCubeMap, texPathWaterRamp, waveTextures);
    }

    private static WaveGenerator readWaveGenerator() throws IOException {
        String textureName = readStringNull();
        String rampName = readStringNull();
        Vector3 position = readVector3f();
        float rotation = readFloat();
        Vector3 velocity = readVector3f();
        WaveGenerator waveGenerator = new WaveGenerator(textureName, rampName, position, rotation, velocity);
        waveGenerator.setLifeTimeFirst(readFloat());
        waveGenerator.setLifeTimeSecond(readFloat());
        waveGenerator.setPeriodFirst(readFloat());
        waveGenerator.setPeriodSecond(readFloat());
        waveGenerator.setScaleFirst(readFloat());
        waveGenerator.setScaleSecond(readFloat());
        waveGenerator.setFrameCount(readFloat());
        waveGenerator.setFrameRateFirst(readFloat());
        waveGenerator.setFrameRateSecond(readFloat());
        waveGenerator.setStripCount(readFloat());
        return waveGenerator;
    }

    private static Decal readDecal() throws IOException {
        readInt();
        int type = readInt();
        int textureCount = readInt();
        String[] texturePaths = new String[textureCount];
        for (int j = 0; j < textureCount; j++) {
            int stringLength = readInt();
            texturePaths[j] = readString(stringLength);
        }
        Vector3 scale = readVector3f();
        Vector3 position = readVector3f();
        Vector3 rotation = readVector3f();
        float cutOffLOD = readFloat();
        readFloat();
        readInt();
        return new Decal(texturePaths[0], position, rotation, scale, cutOffLOD, DecalType.of(type));
    }

    private static DecalGroup readDecalGroup() throws IOException {
        readInt();
        String name = readStringNull();
        int length = readInt();
        Integer[] data = new Integer[length];
        for (int j = 0; j < length; j++) {
            data[j] = Integer.valueOf(readInt());
        }
        return new DecalGroup(name, List.of((Object[]) data));
    }

    private static int[] readRawImage() throws IOException {
        int byteCount = readInt() - 128;
        DDSHeader ddsHeader = DDSHeader.parseHeader(readBytes(128));
        if (ddsHeader.getWidth() * ddsHeader.getHeight() * 4 != byteCount) {
            throw new UnsupportedEncodingException("Not a recognized dds image format");
        }
        return readInts(byteCount / 4);
    }

    private static int[] readCompressedImage() throws IOException {
        int byteCount = readInt() - 128;
        int[] data = new int[byteCount / 4];
        DDSHeader ddsHeader = DDSHeader.parseHeader(readBytes(128));
        ByteBuffer.wrap(Squish.decompressImage(null, ddsHeader.getWidth(), ddsHeader.getHeight(), readBytes(byteCount), Squish.CompressionType.DXT5)).asIntBuffer().get(data);
        return data;
    }

    private static SkyBox readSkyBox() throws IOException {
        SkyBox skyBox = new SkyBox();
        skyBox.setPosition(readVector3f());
        skyBox.setHorizonHeight(readFloat());
        skyBox.setScale(readFloat());
        skyBox.setSubHeight(readFloat());
        skyBox.setSubDivAx(readInt());
        skyBox.setSubDivHeight(readInt());
        skyBox.setZenithHeight(readFloat());
        skyBox.setHorizonColor(readVector3f());
        skyBox.setZenithColor(readVector3f());
        skyBox.setDecalGlowMultiplier(readFloat());
        skyBox.setAlbedo(readStringNull());
        skyBox.setGlow(readStringNull());
        int length = readInt();
        SkyBox.Planet[] planets = new SkyBox.Planet[length];
        for (int i = 0; i < length; i++) {
            planets[i] = new SkyBox.Planet();
            planets[i].setPosition(readVector3f());
            planets[i].setRotation(readFloat());
            planets[i].setScale(readVector2f());
            planets[i].setUv(readVector4f());
        }
        skyBox.setPlanets(planets);
        skyBox.setMidRgbColor(new Color(readByte(), readByte(), readByte(), 0));
        skyBox.setCirrusMultiplier(readFloat());
        skyBox.setCirrusColor(readVector3f());
        skyBox.setCirrusTexture(readStringNull());
        int cirrusLayerCount = readInt();
        SkyBox.Cirrus[] cirrusLayers = new SkyBox.Cirrus[cirrusLayerCount];
        for (int i2 = 0; i2 < cirrusLayerCount; i2++) {
            cirrusLayers[i2] = new SkyBox.Cirrus();
            cirrusLayers[i2].setFrequency(readVector2f());
            cirrusLayers[i2].setSpeed(readFloat());
            cirrusLayers[i2].setDirection(readVector2f());
        }
        skyBox.setCirrusLayers(cirrusLayers);
        skyBox.setClouds7(readFloat());
        return skyBox;
    }

    private static Prop readProp() throws IOException {
        String path = readStringNull();
        Vector3 position = readVector3f();
        Vector3 rotationX = readVector3f();
        readVector3f();
        readVector3f();
        readVector3f();
        float rotation = (float) StrictMath.atan2(rotationX.z(), rotationX.x());
        return new Prop(path, position, rotation, false);
    }

    private static BufferedImage getBufferedImageFromRawData(int bufferedImageType, int[] imageData) {
        int imageSize = (int) StrictMath.sqrt(imageData.length);
        BufferedImage image = new BufferedImage(imageSize, imageSize, bufferedImageType);
        DataBuffer imageDataBuffer = image.getRaster().getDataBuffer();
        for (int i = 0; i < imageDataBuffer.getSize(); i++) {
            imageDataBuffer.setElem(i, imageData[i]);
        }
        return image;
    }

    private static int[] getIntegerArray(byte[] bytes) {
        int[] intArray = new int[bytes.length];
        int i = 0;
        for (byte b : bytes) {
            int i2 = i;
            i++;
            intArray[i2] = b;
        }
        return intArray;
    }

    private static int[] getIntegerArray(short[] shorts) {
        int[] intArray = new int[shorts.length];
        int i = 0;
        for (short s : shorts) {
            int i2 = i;
            i++;
            intArray[i2] = s;
        }
        return intArray;
    }
}
