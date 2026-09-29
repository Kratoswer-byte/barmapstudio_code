package com.faforever.neroxis.exporter;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.Vector4Mask;
import com.faforever.neroxis.util.ImageUtil;
import com.faforever.neroxis.util.serial.biome.LightingSettings;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.serial.biome.WaterSettings;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/PreviewGenerator.class */
public class PreviewGenerator {
    public static final int PREVIEW_SIZE = 256;
    public static final String BLANK_PREVIEW = "/images/generatedMapIcon.png";
    private static final String MASS_IMAGE = "/images/map_markers/mass.png";
    private static final String HYDRO_IMAGE = "/images/map_markers/hydro.png";
    private static final String ARMY_IMAGE = "/images/map_markers/army.png";

    public static void generatePreview(SCMap map, SymmetrySettings symmetrySettings) throws IOException {
        FloatMask heightmap = new FloatMask(map.getHeightmap(), (RandomGenerator.SplittableGenerator) null, symmetrySettings);
        FloatMask sunReflectance = heightmap.copyAsNormalMask().copyAsDotProduct(map.getBiome().lightingSettings().sunDirection());
        Vector4Mask textureMasksLow = new Vector4Mask(map.getTextureMasksLow(), (RandomGenerator.SplittableGenerator) null, symmetrySettings, 1.0f);
        Vector4Mask textureMasksHigh = new Vector4Mask(map.getTextureMasksHigh(), (RandomGenerator.SplittableGenerator) null, symmetrySettings, 1.0f);
        generatePreview(heightmap, sunReflectance, map, textureMasksLow, textureMasksHigh);
    }

    public static void generatePreview(FloatMask heightmap, FloatMask irradiance, SCMap map, Vector4Mask textureMasksLow, Vector4Mask textureMasksHigh) throws IOException {
        if (!map.isGeneratePreview()) {
            generateBlankPreview(map);
            return;
        }
        BufferedImage previewImage = map.getPreview();
        Graphics2D graphics = previewImage.createGraphics();
        TerrainMaterials materials = map.getBiome().terrainMaterials();
        List<FloatMask> scaledTextures = new ArrayList<>();
        FloatMask baseLayer = (FloatMask) new FloatMask(PREVIEW_SIZE, (RandomGenerator.SplittableGenerator) null, new SymmetrySettings(Symmetry.NONE)).add(Float.valueOf(1.0f));
        scaledTextures.addFirst(baseLayer);
        scaledTextures.addAll(Arrays.asList(textureMasksLow.splitComponentMasks()));
        scaledTextures.addAll(Arrays.asList(textureMasksHigh.splitComponentMasks()));
        for (int i = 0; i < materials.previewColors().size(); i++) {
            if (!materials.textures().get(i).path().isBlank() && !materials.previewColors().get(i).isBlank()) {
                BufferedImage layer = new BufferedImage(PREVIEW_SIZE, PREVIEW_SIZE, 2);
                Graphics2D layerGraphics = layer.createGraphics();
                layerGraphics.setColor(new Color(Integer.parseInt(materials.previewColors().get(i), 16)));
                layerGraphics.fillRect(0, 0, PREVIEW_SIZE, PREVIEW_SIZE);
                shadeLayer(layer, map, irradiance, scaledTextures.get(i));
                TexturePaint layerPaint = new TexturePaint(layer, new Rectangle2D.Float(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 256.0f, 256.0f));
                graphics.setPaint(layerPaint);
                graphics.fillRect(0, 0, previewImage.getWidth(), previewImage.getHeight());
            }
        }
        BufferedImage waterLayer = getWaterLayer(map, heightmap);
        TexturePaint layerPaint2 = new TexturePaint(waterLayer, new Rectangle2D.Float(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 256.0f, 256.0f));
        graphics.setPaint(layerPaint2);
        graphics.fillRect(0, 0, previewImage.getWidth(), previewImage.getHeight());
        addReclaimProps(previewImage, map);
    }

    public static void generateBlankPreview(SCMap map) throws IOException {
        BufferedImage blindPreview = ImageUtil.readImage(BLANK_PREVIEW);
        map.getPreview().setData(blindPreview.getData());
    }

    private static BufferedImage shadeLayer(BufferedImage image, SCMap map, FloatMask irradiance, FloatMask textureLayer) {
        LightingSettings lightingSettings = map.getBiome().lightingSettings();
        int[] origRGBA = new int[4];
        int[] newRGBA = new int[4];
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                image.getRaster().getPixel(x, y, origRGBA);
                float coefficient = irradiance.getPrimitive(x, y) + 0.5f;
                newRGBA[0] = (int) (origRGBA[0] * ((lightingSettings.sunColor().x() * coefficient) + lightingSettings.sunAmbience().x()) * lightingSettings.lightingMultiplier());
                newRGBA[1] = (int) (origRGBA[1] * ((lightingSettings.sunColor().y() * coefficient) + lightingSettings.sunAmbience().y()) * lightingSettings.lightingMultiplier());
                newRGBA[2] = (int) (origRGBA[2] * ((lightingSettings.sunColor().z() * coefficient) + lightingSettings.sunAmbience().z()) * lightingSettings.lightingMultiplier());
                newRGBA[0] = StrictMath.max(StrictMath.min(newRGBA[0], 255), 0);
                newRGBA[1] = StrictMath.max(StrictMath.min(newRGBA[1], 255), 0);
                newRGBA[2] = StrictMath.max(StrictMath.min(newRGBA[2], 255), 0);
                newRGBA[3] = (int) StrictMath.max(StrictMath.min(textureLayer.getPrimitive(x, y) * 255.0f, 255.0f), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                image.getRaster().setPixel(x, y, newRGBA);
            }
        }
        return image;
    }

    private static BufferedImage getWaterLayer(SCMap map, FloatMask heightmap) {
        Color shallowColor = new Color(53, 85, 117);
        Color abyssColor = new Color(60, 67, 137);
        WaterSettings waterSettings = map.getBiome().waterSettings();
        BufferedImage waterLayer = new BufferedImage(PREVIEW_SIZE, PREVIEW_SIZE, 2);
        Graphics2D waterLayerGraphics = waterLayer.createGraphics();
        float waterheight = waterSettings.elevation();
        float abyssheight = waterSettings.elevationAbyss();
        int[] newRGBA = new int[4];
        BufferedImage layer = new BufferedImage(PREVIEW_SIZE, PREVIEW_SIZE, 2);
        int height = waterLayer.getHeight();
        int width = waterLayer.getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float weight = StrictMath.min(StrictMath.max((waterheight - heightmap.getPrimitive(x, y)) / (waterheight - abyssheight), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 1.0f);
                newRGBA[0] = (int) ((shallowColor.getRed() * (1.0f - weight)) + (abyssColor.getRed() * weight));
                newRGBA[1] = (int) ((shallowColor.getGreen() * (1.0f - weight)) + (abyssColor.getGreen() * weight));
                newRGBA[2] = (int) ((shallowColor.getBlue() * (1.0f - weight)) + (abyssColor.getBlue() * weight));
                newRGBA[0] = StrictMath.min(255, newRGBA[0]);
                newRGBA[1] = StrictMath.min(255, newRGBA[1]);
                newRGBA[2] = StrictMath.min(255, newRGBA[2]);
                newRGBA[3] = waterheight > heightmap.getPrimitive(x, y) ? (int) ((64.0f * weight) + 92.0f) : 0;
                layer.getRaster().setPixel(x, y, newRGBA);
            }
        }
        TexturePaint layerPaint = new TexturePaint(layer, new Rectangle2D.Float(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 256.0f, 256.0f));
        waterLayerGraphics.setPaint(layerPaint);
        waterLayerGraphics.fillRect(0, 0, PREVIEW_SIZE, PREVIEW_SIZE);
        return waterLayer;
    }

    public static BufferedImage addMarkers(BufferedImage image, SCMap map) throws IOException {
        BufferedImage mexImage = ImageUtil.scaleImage(ImageUtil.readImage(MASS_IMAGE), 5, 5);
        BufferedImage hydroImage = ImageUtil.scaleImage(ImageUtil.readImage(HYDRO_IMAGE), 5, 5);
        BufferedImage spawnImage = ImageUtil.scaleImage(ImageUtil.readImage(ARMY_IMAGE), 5, 5);
        addMarkerImages(map.getMexes(), mexImage, image, map);
        addMarkerImages(map.getHydros(), hydroImage, image, map);
        addMarkerImages(map.getSpawns(), spawnImage, image, map);
        return image;
    }

    private static void addMarkerImages(Collection<? extends Marker> markers, BufferedImage markerImage, BufferedImage preview, SCMap map) {
        markers.forEach(marker -> {
            int x = (int) (((marker.getPosition().x() / map.getSize()) * 256.0f) - (markerImage.getWidth((ImageObserver) null) / 2.0f));
            int y = (int) (((marker.getPosition().z() / map.getSize()) * 256.0f) - (markerImage.getHeight((ImageObserver) null) / 2.0f));
            if (ImageUtil.inImageBounds(x, y, preview)) {
                preview.getGraphics().drawImage(markerImage, x, y, (ImageObserver) null);
            }
        });
    }

    public static void addReclaimProps(BufferedImage image, SCMap map) {
        int[] boulderRGBA = new int[4];
        for (int i = 0; i < 6; i += 2) {
            boulderRGBA[i / 2] = (byte) ((Character.digit("d18e37".charAt(i), 16) << 4) + Character.digit("d18e37".charAt(i + 1), 16));
        }
        boulderRGBA[3] = 255;
        int[] boulderRGBAOutline = {75, 75, 75, 255};
        addRGB(boulderRGBAOutline, boulderRGBA, 0.3f);
        for (Prop prop : map.getProps()) {
            if (prop.isBoulder()) {
                int x = (int) ((prop.getPosition().x() / map.getSize()) * 256.0f);
                int y = (int) ((prop.getPosition().z() / map.getSize()) * 256.0f);
                if (x >= 1 && y >= 1 && x < image.getRaster().getWidth() - 1 && y < image.getRaster().getHeight() - 1) {
                    image.getRaster().setPixel(x, y, boulderRGBA);
                    image.getRaster().setPixel(x + 1, y, addRGB(image.getRaster().getPixel(x + 1, y, (int[]) null), boulderRGBAOutline, 0.3f));
                    image.getRaster().setPixel(x - 1, y, addRGB(image.getRaster().getPixel(x - 1, y, (int[]) null), boulderRGBAOutline, 0.3f));
                    image.getRaster().setPixel(x, y + 1, addRGB(image.getRaster().getPixel(x, y + 1, (int[]) null), boulderRGBAOutline, 0.3f));
                    image.getRaster().setPixel(x, y - 1, addRGB(image.getRaster().getPixel(x, y - 1, (int[]) null), boulderRGBAOutline, 0.3f));
                }
            }
        }
    }

    private static int[] addRGB(int[] original, int[] overlayRGB, float opacity) {
        for (int i = 0; i < 3; i++) {
            original[i] = (int) (((overlayRGB[i] - original[i]) * opacity) + original[i]);
        }
        return original;
    }
}
