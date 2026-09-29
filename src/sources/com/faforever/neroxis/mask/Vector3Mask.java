package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.vector.Vector3;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Objects;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/Vector3Mask.class */
public final class Vector3Mask extends VectorMask<Vector3, Vector3Mask> {
    public Vector3Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(size, random, symmetrySettings, null);
    }

    public Vector3Mask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(new Vector3[size][size], random, symmetrySettings, name);
    }

    public Vector3Mask(Vector3Mask other) {
        this(other, (String) null);
    }

    public Vector3Mask(Vector3Mask other, String name) {
        super(other, name);
    }

    public Vector3Mask(NormalMask other) {
        this(other, (String) null);
    }

    public Vector3Mask(NormalMask other, String name) {
        super(new Vector3[other.getSize()][other.getSize()], other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        enqueue(dependencies -> {
            NormalMask source = (NormalMask) dependencies.getFirst();
            Objects.requireNonNull(source);
            set(source::get);
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector3 createValue(float scaleFactor, float... components) {
        assertMatchingDimension(components.length);
        return new Vector3(components[0], components[1], components[2]).multiply(scaleFactor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.VectorMask
    public Vector3[][] getNullMask(int size) {
        return new Vector3[size][size];
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Vector3Mask setComponents(FloatMask comp0, FloatMask comp1, FloatMask comp2) {
        assertCompatibleComponents(comp0, comp1, comp2);
        return (Vector3Mask) enqueue(dependencies -> {
            FloatMask source1 = (FloatMask) dependencies.get(0);
            FloatMask source2 = (FloatMask) dependencies.get(1);
            FloatMask source3 = (FloatMask) dependencies.get(2);
            apply((x, y) -> {
                setComponentAt(x, y, source1.get(x, y).floatValue(), 0);
                setComponentAt(x, y, source2.get(x, y).floatValue(), 1);
                setComponentAt(x, y, source3.get(x, y).floatValue(), 2);
            });
        }, comp0, comp1, comp2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Vector3Mask cross(Vector3Mask other) {
        assertCompatibleMask(other);
        return (Vector3Mask) enqueue(dependencies -> {
            Vector3Mask source = (Vector3Mask) dependencies.getFirst();
            set((x, y) -> {
                return get(x, y).cross(source.get(x, y));
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Vector3Mask cross(Vector3 vector) {
        return (Vector3Mask) set((x, y) -> {
            return get(x, y).cross(vector);
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 1);
        WritableRaster imageRaster = image.getRaster();
        Vector3 maxComponents = getMaxComponents();
        Vector3 minComponents = getMinComponents();
        Vector3 rangeComponents = maxComponents.subtract(minComponents);
        loop((x, y) -> {
            imageRaster.setPixel(x, y, get(x, y).subtract(minComponents).divide(rangeComponents).multiply(255.0f).toArray());
        });
        return image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Vector3 getZeroValue() {
        return new Vector3(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }
}
