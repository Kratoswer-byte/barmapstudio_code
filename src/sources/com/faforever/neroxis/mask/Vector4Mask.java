package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.vector.Vector4;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/Vector4Mask.class */
public final class Vector4Mask extends VectorMask<Vector4, Vector4Mask> {
    public Vector4Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(size, random, symmetrySettings, (String) null);
    }

    public Vector4Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(new Vector4[size][size], random, symmetrySettings, name);
    }

    public Vector4Mask(Vector4Mask other) {
        this(other, null);
    }

    public Vector4Mask(Vector4Mask other, String name) {
        super(other, name);
    }

    public Vector4Mask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, float scaleFactor) {
        this(sourceImage, random, symmetrySettings, scaleFactor, null);
    }

    public Vector4Mask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, float scaleFactor, String name) {
        super(new Vector4[sourceImage.getHeight()][sourceImage.getHeight()], random, symmetrySettings, name);
        int numImageComponents = sourceImage.getColorModel().getNumComponents();
        assertMatchingDimension(numImageComponents);
        Raster imageRaster = sourceImage.getData();
        set((x, y) -> {
            float[] components = imageRaster.getPixel(x, y, new float[numImageComponents]);
            return createValue(scaleFactor, components);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector4 createValue(float scaleFactor, float... components) {
        assertMatchingDimension(components.length);
        return new Vector4(components[0], components[1], components[2], components[3]).multiply(scaleFactor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector4[][] getNullMask(int size) {
        return new Vector4[size][size];
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Vector4Mask setComponents(FloatMask comp0, FloatMask comp1, FloatMask comp2, FloatMask comp3) {
        assertCompatibleComponents(comp0, comp1, comp2, comp3);
        return (Vector4Mask) enqueue(dependencies -> {
            FloatMask source1 = (FloatMask) dependencies.get(0);
            FloatMask source2 = (FloatMask) dependencies.get(1);
            FloatMask source3 = (FloatMask) dependencies.get(2);
            FloatMask source4 = (FloatMask) dependencies.get(3);
            apply((x, y) -> {
                setComponentAt(x, y, source1.get(x, y).floatValue(), 0);
                setComponentAt(x, y, source2.get(x, y).floatValue(), 1);
                setComponentAt(x, y, source3.get(x, y).floatValue(), 2);
                setComponentAt(x, y, source4.get(x, y).floatValue(), 3);
            });
        }, comp0, comp1, comp2, comp3);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 2);
        WritableRaster imageRaster = image.getRaster();
        Vector4 maxComponents = getMaxComponents();
        Vector4 minComponents = getMinComponents();
        Vector4 rangeComponents = maxComponents.subtract(minComponents);
        loop((x, y) -> {
            imageRaster.setPixel(x, y, get(x, y).subtract(minComponents).divide(rangeComponents).multiply(255.0f, 255.0f, 255.0f, 191.0f).add(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 64.0f).toArray());
        });
        return image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Vector4 getZeroValue() {
        return new Vector4(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }
}
