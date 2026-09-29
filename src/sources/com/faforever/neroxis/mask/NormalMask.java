package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.vector.Vector3;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/NormalMask.class */
public final class NormalMask extends VectorMask<Vector3, NormalMask> {
    public NormalMask(int size, RandomGenerator.SplittableGenerator random, String name) {
        super(new Vector3[size][size], random, new SymmetrySettings(Symmetry.NONE), name);
    }

    public NormalMask(NormalMask other) {
        this(other, (String) null);
    }

    public NormalMask(NormalMask other, String name) {
        super(other, name);
    }

    public NormalMask(FloatMask other) {
        this(other, 1.0f, (String) null);
    }

    public NormalMask(FloatMask other, float scale, String name) {
        this(other.getSize() - 1, other.getNextRandomGenerator(), name);
        enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            set((x, y) -> {
                return source.calculateNormalAt(x, y, scale);
            });
        }, other);
    }

    public NormalMask(FloatMask other, float scale) {
        this(other, scale, (String) null);
    }

    public NormalMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random) {
        this(sourceImage, random, (String) null);
    }

    public NormalMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, String name) {
        this(sourceImage.getHeight(), random, name);
        Raster imageRaster = sourceImage.getData();
        set((x, y) -> {
            float[] components = imageRaster.getPixel(x, y, new float[4]);
            return createValue(1.0f, components[3], components[0], components[1]);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector3 createValue(float scaleFactor, float... components) {
        assertMatchingDimension(components.length);
        return new Vector3(components[0], components[1], components[2]).multiply(scaleFactor);
    }

    @Override // com.faforever.neroxis.mask.VectorMask, com.faforever.neroxis.mask.Mask
    public BufferedImage writeToImage(BufferedImage image) {
        assertSize(image.getHeight());
        WritableRaster imageRaster = image.getRaster();
        loop((x, y) -> {
            Vector3 value = get(x, y);
            int xV = (byte) StrictMath.min(StrictMath.max((128.0f * value.x()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
            int yV = (byte) StrictMath.min(StrictMath.max((127.0f * value.y()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
            int zV = (byte) StrictMath.min(StrictMath.max((128.0f * value.z()) + 128.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 255.0f);
            imageRaster.setPixel(x, y, new int[]{xV, zV, yV});
        });
        return image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector3[][] getNullMask(int size) {
        return new Vector3[size][size];
    }

    /* JADX WARN: Multi-variable type inference failed */
    public NormalMask cross(NormalMask other) {
        assertCompatibleMask(other);
        return (NormalMask) enqueue(dependencies -> {
            Vector3Mask source = (Vector3Mask) dependencies.getFirst();
            set((x, y) -> {
                return get(x, y).cross(source.get(x, y));
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public NormalMask cross(Vector3 vector) {
        Vector3 normalizedVector = vector.normalize();
        return (NormalMask) set((x, y) -> {
            return get(x, y).cross(normalizedVector);
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 1);
        writeToImage(image);
        return image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Vector3 getZeroValue() {
        return new Vector3(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }
}
