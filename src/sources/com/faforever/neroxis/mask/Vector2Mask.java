package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/Vector2Mask.class */
public final class Vector2Mask extends VectorMask<Vector2, Vector2Mask> {
    public Vector2Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(size, random, symmetrySettings, null);
    }

    public Vector2Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(new Vector2[size][size], random, symmetrySettings, name);
    }

    public Vector2Mask(Vector2Mask other) {
        this(other, null);
    }

    public Vector2Mask(Vector2Mask other, String name) {
        super(other, name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector2 createValue(float scaleFactor, float... components) {
        assertMatchingDimension(components.length);
        return new Vector2(components[0], components[1]).multiply(scaleFactor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector2[][] getNullMask(int size) {
        return new Vector2[size][size];
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Vector2Mask setComponents(FloatMask comp0, FloatMask comp1) {
        assertCompatibleComponents(comp0, comp1);
        return (Vector2Mask) enqueue(dependencies -> {
            FloatMask source1 = (FloatMask) dependencies.get(0);
            FloatMask source2 = (FloatMask) dependencies.get(1);
            apply((x, y) -> {
                setComponentAt(x, y, source1.get(x, y).floatValue(), 0);
                setComponentAt(x, y, source2.get(x, y).floatValue(), 1);
            });
        }, comp0, comp1);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 1);
        WritableRaster imageRaster = image.getRaster();
        Vector2 maxComponents = getMaxComponents();
        Vector2 minComponents = getMinComponents();
        Vector2 rangeComponents = maxComponents.subtract(minComponents);
        loop((x, y) -> {
            imageRaster.setPixel(x, y, get(x, y).subtract(minComponents).divide(rangeComponents).multiply(255.0f).toArray());
        });
        return image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Vector2 getZeroValue() {
        return new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }
}
