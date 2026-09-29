package com.faforever.neroxis.mask;

import com.faforever.neroxis.brushes.Brushes;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.util.MathUtil;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.functional.BiIntFloatConsumer;
import com.faforever.neroxis.util.functional.ToFloatBiIntFunction;
import com.faforever.neroxis.util.vector.Vector;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/FloatMask.class */
public final class FloatMask extends PrimitiveMask<Float, FloatMask> {
    private float[][] mask;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !FloatMask.class.desiredAssertionStatus();
    }

    public FloatMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(size, random, symmetrySettings, (String) null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FloatMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(size, random, symmetrySettings, name);
        this.mask = new float[0][0];
    }

    public FloatMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(sourceImage, random, symmetrySettings, 1.0f, null);
    }

    public FloatMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, float scaleFactor, String name) {
        this(sourceImage.getHeight(), random, symmetrySettings, name);
        DataBuffer imageBuffer = sourceImage.getRaster().getDataBuffer();
        int size = getSize();
        apply((x, y) -> {
            setPrimitive(x, y, imageBuffer.getElemFloat(x + (y * size)) * scaleFactor);
        });
    }

    public FloatMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, float scaleFactor) {
        this(sourceImage, random, symmetrySettings, scaleFactor, null);
    }

    FloatMask(FloatMask other) {
        this(other, (String) null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    FloatMask(FloatMask other, String name) {
        super(other, name);
        this.mask = new float[0][0];
    }

    FloatMask(BooleanMask other, float low, float high) {
        this(other, low, high, (String) null);
    }

    FloatMask(BooleanMask other, float low, float high, String name) {
        this(other.getSize(), other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                setPrimitive(x, y, source.getPrimitive(x, y) ? high : low);
            });
        }, other);
    }

    private <T extends Vector<T>, U extends VectorMask<T, U>> FloatMask(VectorMask<T, U> other1, VectorMask<T, U> other2) {
        this(other1, other2, (String) null);
    }

    <T extends Vector<T>, U extends VectorMask<T, U>> FloatMask(VectorMask<T, U> other1, VectorMask<T, U> other2, String name) {
        this(other1.getSize(), other1.getNextRandomGenerator(), other1.getSymmetrySettings(), name);
        assertCompatibleMask(other1);
        assertCompatibleMask(other2);
        enqueue(dependencies -> {
            VectorMask vectorMask = (VectorMask) dependencies.get(0);
            VectorMask vectorMask2 = (VectorMask) dependencies.get(1);
            apply((x, y) -> {
                setPrimitive(x, y, vectorMask.get(x, y).dot(vectorMask2.get(x, y)));
            });
        }, other1, other2);
    }

    private <T extends Vector<T>, U extends VectorMask<T, U>> FloatMask(VectorMask<T, U> other, T vector) {
        this(other, vector, (String) null);
    }

    <T extends Vector<T>, U extends VectorMask<T, U>> FloatMask(VectorMask<T, U> other, T vector, String name) {
        this(other.getSize(), other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        assertCompatibleMask(other);
        enqueue(dependencies -> {
            VectorMask vectorMask = (VectorMask) dependencies.getFirst();
            apply((x, y) -> {
                setPrimitive(x, y, vectorMask.get(x, y).dot(vector));
            });
        }, other);
    }

    private <T extends Vector<T>, U extends VectorMask<T, U>> FloatMask(VectorMask<T, U> other, int index) {
        this(other, index, (String) null);
    }

    <T extends Vector<T>, U extends VectorMask<T, U>> FloatMask(VectorMask<T, U> other, int index, String name) {
        this(other.getSize(), other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        assertCompatibleMask(other);
        enqueue(dependencies -> {
            VectorMask vectorMask = (VectorMask) dependencies.getFirst();
            apply((x, y) -> {
                setPrimitive(x, y, vectorMask.get(x, y).get(index));
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPrimitive(int x, int y, float value) {
        this.mask[x][y] = value;
    }

    public FloatMask addPerlinNoise(int resolution, float scale) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        int size = getSize();
        int gradientSize = size / resolution;
        if (gradientSize <= 0) {
            System.err.println("FloatMask:addPerlinNoise(): resolution " + resolution + " can't be greater than mask size " + size);
        }
        float gradientScale = size / gradientSize;
        Vector2Mask gradientVectors = new Vector2Mask(gradientSize + 1, getNextRandomGenerator(), new SymmetrySettings(Symmetry.NONE), getName() + "PerlinVectors");
        gradientVectors.randomize(-1.0f, 1.0f).normalize();
        FloatMask noise = new FloatMask(size, (RandomGenerator.SplittableGenerator) null, this.symmetrySettings, getName() + "PerlinNoise");
        noise.enqueue(dependencies -> {
            Vector2Mask source = (Vector2Mask) dependencies.getFirst();
            noise.setPrimitiveWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                int xLow = (int) (x / gradientScale);
                float dXLow = (x / gradientScale) - xLow;
                int xHigh = xLow + 1;
                float dXHigh = (x / gradientScale) - xHigh;
                int yLow = (int) (y / gradientScale);
                float dYLow = (y / gradientScale) - yLow;
                int yHigh = yLow + 1;
                float dYHigh = (y / gradientScale) - yHigh;
                float topLeft = new Vector2(dXLow, dYLow).dot(source.get(xLow, yLow));
                float topRight = new Vector2(dXLow, dYHigh).dot(source.get(xLow, yHigh));
                float bottomLeft = new Vector2(dXHigh, dYLow).dot(source.get(xHigh, yLow));
                float bottomRight = new Vector2(dXHigh, dYHigh).dot(source.get(xHigh, yHigh));
                return MathUtil.smootherStep(MathUtil.smootherStep(topLeft, bottomLeft, dXLow), MathUtil.smootherStep(topRight, bottomRight, dXLow), dYLow);
            });
            float noiseMin = noise.getMin().floatValue();
            float noiseMax = noise.getMax().floatValue();
            float noiseRange = noiseMax - noiseMin;
            noise.apply((x2, y2) -> {
                noise.setPrimitive(x2, y2, ((noise.getPrimitive(x2, y2) - noiseMin) / noiseRange) * scale);
            });
        }, gradientVectors);
        return (FloatMask) enqueue(dependencies2 -> {
            add((FloatMask) dependencies2.getFirst());
        }, noise);
    }

    @Override // com.faforever.neroxis.mask.ComparableMask
    public Float getMin() {
        checkNotPipelined();
        return Float.valueOf((float) Arrays.stream(this.mask).flatMapToDouble(row -> {
            return IntStream.range(0, row.length).mapToDouble(i -> {
                return row[i];
            });
        }).min().orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        }));
    }

    @Override // com.faforever.neroxis.mask.ComparableMask
    public Float getMax() {
        checkNotPipelined();
        return Float.valueOf((float) Arrays.stream(this.mask).flatMapToDouble(row -> {
            return IntStream.range(0, row.length).mapToDouble(i -> {
                return row[i];
            });
        }).max().orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        }));
    }

    public float getPrimitive(int x, int y) {
        return this.mask[x][y];
    }

    private void setPrimitive(Vector2 location, float value) {
        setPrimitive(StrictMath.round(location.x()), StrictMath.round(location.y()), value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Vector3 calculateNormalAt(int x, int y, float scale) {
        float xNormal = ((getPrimitive(x, y) - getPrimitive(x + 1, y)) + (getPrimitive(x, y + 1) - getPrimitive(x + 1, y + 1))) * 0.5f * scale;
        float yNormal = ((getPrimitive(x, y) - getPrimitive(x, y + 1)) + (getPrimitive(x + 1, y) - getPrimitive(x + 1, y + 1))) * 0.5f * scale;
        return new Vector3(xNormal, 1.0f, yNormal).normalize();
    }

    public FloatMask addGaussianNoise(float scale) {
        if ($assertionsDisabled || this.random != null) {
            return addPrimitiveWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                return ((float) this.random.nextGaussian()) * scale;
            });
        }
        throw new AssertionError();
    }

    public FloatMask addWhiteNoise(float scale) {
        if ($assertionsDisabled || this.random != null) {
            return addPrimitiveWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                return this.random.nextFloat() * scale;
            });
        }
        throw new AssertionError();
    }

    public FloatMask addWhiteNoise(float minValue, float maxValue) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        float range = maxValue - minValue;
        return addPrimitiveWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
            return (this.random.nextFloat() * range) + minValue;
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask waterErode(int numDrops, int maxIterations, float friction, float speed, float erosionRate, float depositionRate, float maxOffset, float iterationScale) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        int size = getSize();
        for (int i = 0; i < numDrops; i++) {
            waterDrop(maxIterations, this.random.nextInt(size), this.random.nextInt(size), friction, speed, erosionRate, depositionRate, maxOffset, iterationScale);
        }
        return (FloatMask) forceSymmetry(SymmetryType.SPAWN);
    }

    private void waterDrop(int maxIterations, float x, float y, float friction, float gravity, float erosionRate, float depositionRate, float maxOffset, float iterationScale) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        float xOffset = ((this.random.nextFloat() * 2.0f) - 1.0f) * maxOffset;
        float yOffset = ((this.random.nextFloat() * 2.0f) - 1.0f) * maxOffset;
        float sediment = 0.0f;
        float xPrev = x;
        float yPrev = y;
        float xVelocity = 0.0f;
        float yVelocity = 0.0f;
        for (int i = 0; i < maxIterations; i++) {
            int sampleX = (int) (x + xOffset);
            int sampleY = (int) (y + yOffset);
            if (!inBounds(sampleX, sampleY) || !inBounds((int) xPrev, (int) yPrev)) {
                return;
            }
            Vector3 surfaceNormal = calculateNormalAt(sampleX, sampleY, 1.0f);
            if (surfaceNormal.y() < 1.0f || StrictMath.sqrt((xVelocity * xVelocity) + (yVelocity * yVelocity)) >= 1.0d) {
                float deposit = sediment * depositionRate * surfaceNormal.y();
                float erosion = erosionRate * (1.0f - surfaceNormal.y()) * StrictMath.min(1.0f, i * iterationScale);
                float sedimentChange = deposit - erosion;
                addValueAt((int) xPrev, (int) yPrev, Float.valueOf(sedimentChange));
                sediment -= sedimentChange;
                xVelocity = ((1.0f - friction) * xVelocity) + (surfaceNormal.x() * gravity);
                yVelocity = ((1.0f - friction) * yVelocity) + (surfaceNormal.z() * gravity);
                xPrev = x;
                yPrev = y;
                x += xVelocity;
                y += yVelocity;
            } else {
                return;
            }
        }
    }

    public FloatMask removeAreasOfSpecifiedSizeWithLocalMaximums(int minSize, int maxSize, int levelOfPrecision, float floatMax) {
        for (int x = 0; x < levelOfPrecision; x++) {
            removeAreasInIntensityAndSize(minSize, maxSize, (1.0f - (x / levelOfPrecision)) * floatMax, floatMax);
        }
        removeAreasInIntensityAndSize(minSize, maxSize, 1.0E-7f, floatMax);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask removeAreasInIntensityAndSize(int minSize, int maxSize, float minIntensity, float maxIntensity) {
        return subtract(((FloatMask) copy()).removeAreasOutsideRangeAndSize(minSize, maxSize, minIntensity, maxIntensity));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask removeAreasOutsideRangeAndSize(int minSize, int maxSize, float minValue, float maxValue) {
        FloatMask areasToRemove = ((FloatMask) copy()).copyAsBooleanMask(Float.valueOf(minValue), Float.valueOf(maxValue)).removeAreasOutsideSizeRange(minSize, maxSize).invert().copyAsFloatMask(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f);
        return (FloatMask) subtract(areasToRemove).clampMin(Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask useBrush(Vector2 location, String brushName, float intensity, int size, boolean wrapEdges) {
        return (FloatMask) enqueue(() -> {
            FloatMask brush = Brushes.loadBrush(brushName);
            ((FloatMask) brush.multiply(Float.valueOf(intensity / brush.getMax().floatValue()))).setSize(size);
            addWithOffset(brush, location, true, wrapEdges);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask addDistance(BooleanMask other, float scale) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            FloatMask distanceField = source.copyAsDistanceField();
            add((FloatMask) distanceField.multiply(Float.valueOf(scale)));
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask gradient() {
        return (FloatMask) enqueue(() -> {
            int size = getSize();
            float[][] newMask = new float[size][size];
            apply((x, y) -> {
                int xNeg = StrictMath.max(0, x - 1);
                int xPos = StrictMath.min(size - 1, x + 1);
                int yNeg = StrictMath.max(0, y - 1);
                int yPos = StrictMath.min(size - 1, y + 1);
                float xSlope = (getPrimitive(xPos, y) - getPrimitive(xNeg, y)) / (xPos - xNeg);
                float ySlope = (getPrimitive(x, yPos) - getPrimitive(x, yNeg)) / (yPos - yNeg);
                newMask[x][y] = (float) StrictMath.sqrt((xSlope * xSlope) + (ySlope * ySlope));
            });
            this.mask = newMask;
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask supcomGradient() {
        return (FloatMask) enqueue(() -> {
            int size = getSize();
            float[][] newMask = new float[size][size];
            apply((x, y) -> {
                int xPos = StrictMath.min(size - 1, x + 1);
                int yPos = StrictMath.min(size - 1, y + 1);
                int xNeg = StrictMath.max(0, x - 1);
                int yNeg = StrictMath.max(0, y - 1);
                float xPosSlope = StrictMath.abs(getPrimitive(x, y) - getPrimitive(xPos, y));
                float yPosSlope = StrictMath.abs(getPrimitive(x, y) - getPrimitive(x, yPos));
                float xNegSlope = StrictMath.abs(getPrimitive(x, y) - getPrimitive(xNeg, y));
                float yNegSlope = StrictMath.abs(getPrimitive(x, y) - getPrimitive(x, yNeg));
                newMask[x][y] = ((Float) Collections.max(List.of(Float.valueOf(xPosSlope), Float.valueOf(yPosSlope), Float.valueOf(xNegSlope), Float.valueOf(yNegSlope)))).floatValue();
            });
            this.mask = newMask;
        });
    }

    public FloatMask useBrushWithinAreaWithDensity(BooleanMask other, String brushName, int size, float density, float intensity, boolean wrapEdges) {
        enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            int frequency = (int) (((density * source.getCount()) / 26.21f) / this.symmetrySettings.spawnSymmetry().getNumSymPoints());
            useBrushWithinArea(source, brushName, size, frequency, intensity, wrapEdges);
        }, other);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask useBrushWithinArea(BooleanMask other, String brushName, int size, int numUses, float intensity, boolean wrapEdges) {
        if ($assertionsDisabled || this.random != null) {
            return (FloatMask) enqueue(dependencies -> {
                BooleanMask source = (BooleanMask) dependencies.getFirst();
                assertSmallerSize(size);
                ArrayList<Vector2> possibleLocations = new ArrayList<>(source.getAllCoordinatesEqualTo(true, 1));
                int length = possibleLocations.size();
                FloatMask brush = Brushes.loadBrush(brushName);
                ((FloatMask) brush.multiply(Float.valueOf(intensity / brush.getMax().floatValue()))).setSize(size);
                for (int i = 0; i < numUses; i++) {
                    Vector2 location = possibleLocations.get(this.random.nextInt(length));
                    addWithOffset(brush, location, true, wrapEdges);
                }
            }, other);
        }
        throw new AssertionError();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask useBrushWithCliffMap(FloatMask other, int size) {
        if ($assertionsDisabled || this.random != null) {
            return (FloatMask) enqueue(dependencies -> {
                FloatMask source = (FloatMask) dependencies.getFirst();
                assertSmallerSize(size);
                applyWithSymmetry(SymmetryType.TERRAIN, (x, y) -> {
                    float slope = source.get(x, y).floatValue();
                    if (slope >= 5.0f && this.random.nextInt(100) <= 10) {
                        String brushName = Brushes.GENERATOR_BRUSHES.get(this.random.nextInt(Brushes.GENERATOR_BRUSHES.size()));
                        FloatMask brush = Brushes.loadBrush(brushName);
                        brush.setSize(size + ((int) ((slope + 1.0f) * 4.0f)));
                        brush.multiply(Float.valueOf(0.1f));
                        addWithOffset(brush, new Vector2(x, y), true, false);
                    }
                });
            }, other);
        }
        throw new AssertionError();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask copyAsShadowMask(Vector3 lightDirection) {
        float angle = (float) ((((double) lightDirection.getAzimuth()) - 3.141592653589793d) % 6.283185307179586d);
        float slope = (float) StrictMath.tan(lightDirection.getElevation());
        BooleanMask shadowMask = new BooleanMask(getSize(), getNextRandomGenerator(), new SymmetrySettings(Symmetry.NONE), getName() + "Shadow");
        return ((BooleanMask) shadowMask.enqueue(dependencies -> {
            shadowMask.apply((x, y) -> {
                FloatMask source = (FloatMask) dependencies.getFirst();
                Vector2 location = new Vector2(x, y);
                if (shadowMask.getPrimitive(location)) {
                    return;
                }
                float startHeight = source.getPrimitive(location);
                Vector2 location2 = location.addPolar(angle, 1.0f);
                for (int dist = 1; source.inBounds(location2) && startHeight - source.getPrimitive(location2) > dist * slope; dist++) {
                    shadowMask.setPrimitive(location2, true);
                    location2 = location2.addPolar(angle, 1.0f);
                }
            });
        }, this)).inflate(1).deflate(1);
    }

    public float getPrimitive(Vector2 location) {
        return getPrimitive(StrictMath.round(location.x()), StrictMath.round(location.y()));
    }

    public NormalMask copyAsNormalMask() {
        return copyAsNormalMask(1.0f);
    }

    public NormalMask copyAsNormalMask(float scale) {
        return new NormalMask(this, scale, getName() + "Normals");
    }

    public BufferedImage writeToImage(BufferedImage image, float scaleFactor) {
        return writeToImage(image, scaleFactor, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    public BufferedImage writeToImage(BufferedImage image, float scaleFactor, float offsetFactor) {
        assertSize(image.getHeight());
        int size = getSize();
        DataBuffer imageBuffer = image.getRaster().getDataBuffer();
        loop((x, y) -> {
            imageBuffer.setElemFloat(x + (y * size), (getPrimitive(x, y) - offsetFactor) * scaleFactor);
        });
        return image;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask parabolicMinimization() {
        return (FloatMask) enqueue(() -> {
            addCalculatedParabolicDistance(false);
            addCalculatedParabolicDistance(true);
            sqrt();
        });
    }

    private void addCalculatedParabolicDistance(boolean useColumns) {
        float value;
        float value2;
        float xIntersect;
        checkNotPipelined();
        int size = getSize();
        for (int i = 0; i < size; i++) {
            List<Vector2> vertices = new ArrayList<>();
            List<Vector2> intersections = new ArrayList<>();
            int index = 0;
            if (!useColumns) {
                value = getPrimitive(i, 0);
            } else {
                value = getPrimitive(0, i);
            }
            vertices.add(new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, value));
            intersections.add(new Vector2(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY));
            intersections.add(new Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
            for (int j = 1; j < size; j++) {
                if (!useColumns) {
                    value2 = getPrimitive(i, j);
                } else {
                    value2 = getPrimitive(j, i);
                }
                Vector2 current = new Vector2(j, value2);
                Vector2 vertex = vertices.get(index);
                float fY = (current.y() + (current.x() * current.x())) - (vertex.y() + (vertex.x() * vertex.x()));
                float fX = 2.0f * current.x();
                float f = 2.0f;
                float fX2 = vertex.x();
                while (true) {
                    xIntersect = fY / (fX - (f * fX2));
                    if (xIntersect > intersections.get(index).x()) {
                        break;
                    }
                    index--;
                    Vector2 vertex2 = vertices.get(index);
                    fY = (current.y() + (current.x() * current.x())) - (vertex2.y() + (vertex2.x() * vertex2.x()));
                    fX = 2.0f * current.x();
                    f = 2.0f;
                    fX2 = vertex2.x();
                }
                index++;
                if (index < vertices.size()) {
                    vertices.set(index, current);
                } else {
                    vertices.add(current);
                }
                if (index < intersections.size() - 1) {
                    intersections.set(index, new Vector2(xIntersect, Float.POSITIVE_INFINITY));
                    intersections.set(index + 1, new Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
                } else {
                    intersections.set(index, new Vector2(xIntersect, Float.POSITIVE_INFINITY));
                    intersections.add(new Vector2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
                }
            }
            int index2 = 0;
            for (int j2 = 0; j2 < size; j2++) {
                while (intersections.get(index2 + 1).x() < j2) {
                    index2++;
                }
                Vector2 vertex3 = vertices.get(index2);
                float dx = j2 - vertex3.x();
                float height = (dx * dx) + vertex3.y();
                if (!useColumns) {
                    setPrimitive(i, j2, height);
                } else {
                    setPrimitive(j2, i, height);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask sqrt() {
        return (FloatMask) apply((x, y) -> {
            setPrimitive(x, y, (float) StrictMath.sqrt(getPrimitive(x, y)));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public FloatMask blur(int radius) {
        int[][] innerCount = getInnerCount();
        return (FloatMask) apply((x, y) -> {
            setPrimitive(x, y, transformAverage(calculateAreaAverageAsInts(radius, x, y, innerCount)));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public FloatMask blur(int radius, BooleanMask other) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask limiter = (BooleanMask) dependencies.getFirst();
            int[][] innerCount = getInnerCount();
            apply((x, y) -> {
                if (limiter.get(x, y).booleanValue()) {
                    setPrimitive(x, y, transformAverage(calculateAreaAverageAsInts(radius, x, y, innerCount)));
                }
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public FloatMask copyFrom(FloatMask other) {
        return (FloatMask) enqueue(dependencies -> {
            fill(((FloatMask) dependencies.getFirst()).mask);
        }, other);
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected void initializeMask(int size) {
        enqueue(() -> {
            this.mask = new float[size][size];
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected int getImmediateSize() {
        return this.mask.length;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage writeToImage(BufferedImage image) {
        return writeToImage(image, 1.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 10);
        float min = getMin().floatValue();
        float max = getMax().floatValue();
        float range = max - min;
        writeToImage(image, 255.0f / range, min);
        return image;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public String toHash() throws NoSuchAlgorithmException {
        int size = getSize();
        ByteBuffer bytes = ByteBuffer.allocate(size * size * 4);
        loopInSymmetryRegion(SymmetryType.SPAWN, (x, y) -> {
            bytes.putFloat(getPrimitive(x, y));
        });
        byte[] data = MessageDigest.getInstance("MD5").digest(bytes.array());
        return HexFormat.of().formatHex(data);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public Float get(int x, int y) {
        return Float.valueOf(getPrimitive(x, y));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public void set(int x, int y, Float value) {
        setPrimitive(x, y, value.floatValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public FloatMask fill(Float value) {
        return (FloatMask) enqueue(() -> {
            int length = this.mask.length;
            float[][] arr$ = this.mask;
            for (float[] floats : arr$) {
                Arrays.fill(floats, value.floatValue());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Float getZeroValue() {
        return Float.valueOf(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public FloatMask setSizeInternal(int newSize) {
        int oldSize = getSize();
        if (oldSize == 1) {
            float value = getPrimitive(0, 0);
            initializeMask(newSize);
            fill(Float.valueOf(value));
        } else if (oldSize != newSize) {
            float[][] oldMask = this.mask;
            initializeMask(newSize);
            Map<Integer, Integer> coordinateMap = getSymmetricScalingCoordinateMap(oldSize, newSize);
            applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                int newX = ((Integer) coordinateMap.get(Integer.valueOf(x))).intValue();
                int newY = ((Integer) coordinateMap.get(Integer.valueOf(y))).intValue();
                float value2 = oldMask[newX][newY];
                applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                    setPrimitive(sx, sy, value2);
                });
            });
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public FloatMask rotateInternal(float radians) {
        float[][] oldMask = this.mask;
        int size = getSize();
        initializeMask(size);
        applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
            Vector2 rotatedPoint = SymmetryUtil.getRotatedPoint(x, y, size, radians);
            if (!inBounds(rotatedPoint)) {
                return;
            }
            int newX = StrictMath.round(rotatedPoint.x());
            int newY = StrictMath.round(rotatedPoint.y());
            float value = oldMask[newX][newY];
            applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                setPrimitive(sx, sy, value);
            });
        });
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask scaleToNewMinAndMaxHeight(float newMin, float newMax) {
        return (FloatMask) enqueue(() -> {
            float oldMin = getMin().floatValue();
            float oldMax = getMax().floatValue();
            float scale = oldMin == oldMax ? 1.0f : (newMax - newMin) / (oldMax - oldMin);
            apply((x, y) -> {
                float oldValue = getPrimitive(x, y);
                float newValue = ((oldValue - oldMin) * scale) + newMin;
                setPrimitive(x, y, newValue);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask scaleExponentially(float exp) {
        return (FloatMask) enqueue(() -> {
            apply((x, y) -> {
                float oldValue = getPrimitive(x, y);
                float newValue = (float) StrictMath.pow(oldValue, exp);
                setPrimitive(x, y, newValue);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask shiftToPositive() {
        return (FloatMask) enqueue(() -> {
            float min = getMin().floatValue();
            if (min < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                subtract(Float.valueOf(min));
            }
        });
    }

    private FloatMask fill(float[][] maskToFillFrom) {
        int maskSize = maskToFillFrom.length;
        this.mask = new float[maskSize][maskSize];
        for (int r = 0; r < maskSize; r++) {
            System.arraycopy(maskToFillFrom[r], 0, this.mask[r], 0, maskSize);
        }
        return this;
    }

    private float transformAverage(float value) {
        return value / 1000.0f;
    }

    @Override // com.faforever.neroxis.mask.PrimitiveMask
    protected int[][] getInnerCount() {
        int size = getSize();
        int[][] innerCount = new int[size][size];
        apply((x, y) -> {
            calculateInnerValue(innerCount, x, y, StrictMath.round(getPrimitive(x, y) * 1000.0f));
        });
        return innerCount;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private FloatMask add(ToFloatBiIntFunction valueFunction) {
        return (FloatMask) apply((x, y) -> {
            addPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private FloatMask subtract(ToFloatBiIntFunction valueFunction) {
        return (FloatMask) apply((x, y) -> {
            subtractPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private FloatMask multiply(ToFloatBiIntFunction valueFunction) {
        return (FloatMask) apply((x, y) -> {
            multiplyPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private FloatMask divide(ToFloatBiIntFunction valueFunction) {
        return (FloatMask) apply((x, y) -> {
            dividePrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    private void multiplyPrimitiveAt(int x, int y, float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] * value;
    }

    private void addPrimitiveAt(int x, int y, float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] + value;
    }

    private void subtractPrimitiveAt(int x, int y, float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] - value;
    }

    private void dividePrimitiveAt(int x, int y, float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] / value;
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public Float getSum() {
        return Float.valueOf((float) Arrays.stream(this.mask).flatMapToDouble(row -> {
            return IntStream.range(0, row.length).mapToDouble(i -> {
                return row[i];
            });
        }).sum());
    }

    public Vector2 getRandomPosition() {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        checkNotPipelined();
        float min = getMin().floatValue();
        if (min < SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            throw new IllegalArgumentException("Cannot get random position from a mask with negative values");
        }
        int size = getSize();
        float total = getSum().floatValue();
        if (total == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return null;
        }
        float sum = this.random.nextFloat(total);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float primitive = sum - getPrimitive(x, y);
                sum = primitive;
                if (primitive <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    return new Vector2(x, y);
                }
            }
        }
        throw new IllegalArgumentException("Did not find a coordinate");
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask setWithOffset(FloatMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyWithOffset(source, this::setPrimitive, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask add(FloatMask other) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            apply((x, y) -> {
                float[] fArr = this.mask[x];
                fArr[y] = fArr[y] + source.mask[x][y];
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void addValueAt(int x, int y, Float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] + value.floatValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask add(BooleanMask other, Float value) {
        assertCompatibleMask(other);
        float val = value.floatValue();
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    addPrimitiveAt(x, y, val);
                }
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask add(BooleanMask other, FloatMask values) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            FloatMask val = (FloatMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    addPrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask addWithOffset(FloatMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyWithOffset(source, this::addPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void subtractValueAt(int x, int y, Float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] - value.floatValue();
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public Float getAvg() {
        checkNotPipelined();
        int size = getSize();
        return Float.valueOf((getSum().floatValue() / size) / size);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask subtract(FloatMask other) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            apply((x, y) -> {
                float[] fArr = this.mask[x];
                fArr[y] = fArr[y] - source.mask[x][y];
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask subtract(BooleanMask other, Float value) {
        assertCompatibleMask(other);
        float val = value.floatValue();
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    subtractPrimitiveAt(x, y, val);
                }
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask subtract(BooleanMask other, FloatMask value) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            FloatMask val = (FloatMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    subtractPrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask subtractWithOffset(FloatMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyWithOffset(source, this::subtractPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask multiply(FloatMask other) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            apply((x, y) -> {
                float[] fArr = this.mask[x];
                fArr[y] = fArr[y] * source.mask[x][y];
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void multiplyValueAt(int x, int y, Float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] * value.floatValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask multiply(BooleanMask other, Float value) {
        assertCompatibleMask(other);
        float val = value.floatValue();
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    multiplyPrimitiveAt(x, y, val);
                }
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask multiply(BooleanMask other, FloatMask value) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            FloatMask val = (FloatMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    multiplyPrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask multiplyWithOffset(FloatMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyWithOffset(source, this::multiplyPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask divide(FloatMask other) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            apply((x, y) -> {
                float[] fArr = this.mask[x];
                fArr[y] = fArr[y] / source.mask[x][y];
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void divideValueAt(int x, int y, Float value) {
        float[] fArr = this.mask[x];
        fArr[y] = fArr[y] / value.floatValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask divide(BooleanMask other, Float value) {
        assertCompatibleMask(other);
        float val = value.floatValue();
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    dividePrimitiveAt(x, y, val);
                }
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask divide(BooleanMask other, FloatMask value) {
        assertCompatibleMask(other);
        return (FloatMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            FloatMask val = (FloatMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    dividePrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public FloatMask divideWithOffset(FloatMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (FloatMask) enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyWithOffset(source, this::dividePrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask setPrimitiveWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return (FloatMask) applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                setPrimitive(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask addPrimitiveWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return (FloatMask) applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                addPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask subtractPrimitiveWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return (FloatMask) applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                subtractPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask multiplyPrimitiveWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return (FloatMask) applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                multiplyPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FloatMask dividePrimitiveWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return (FloatMask) applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                dividePrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private FloatMask applyWithOffset(FloatMask other, BiIntFloatConsumer action, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (FloatMask) enqueue(() -> {
            int size = getSize();
            int otherSize = other.getSize();
            int smallerSize = StrictMath.min(size, otherSize);
            StrictMath.max(size, otherSize);
            if (smallerSize == otherSize) {
                if (this.symmetrySettings.spawnSymmetry().isPerfectSymmetry()) {
                    Map<Integer, Integer> coordinateXMap = getShiftedCoordinateMap(xOffset, center, wrapEdges, otherSize, size);
                    Map<Integer, Integer> coordinateYMap = getShiftedCoordinateMap(yOffset, center, wrapEdges, otherSize, size);
                    other.apply((x, y) -> {
                        int shiftX = ((Integer) coordinateXMap.get(Integer.valueOf(x))).intValue();
                        int shiftY = ((Integer) coordinateYMap.get(Integer.valueOf(y))).intValue();
                        if (inBounds(shiftX, shiftY, size)) {
                            float value = other.getPrimitive(x, y);
                            applyAtSymmetryPoints(shiftX, shiftY, SymmetryType.SPAWN, (sx, sy) -> {
                                action.accept(sx, sy, value);
                            });
                        }
                    });
                    return;
                }
                applyAtSymmetryPointsWithOutOfBounds(xOffset, yOffset, SymmetryType.SPAWN, (sx, sy) -> {
                    Map<Integer, Integer> coordinateXMap2 = getShiftedCoordinateMap(sx, center, wrapEdges, otherSize, size);
                    Map<Integer, Integer> coordinateYMap2 = getShiftedCoordinateMap(sy, center, wrapEdges, otherSize, size);
                    other.apply((x2, y2) -> {
                        int shiftX = ((Integer) coordinateXMap2.get(Integer.valueOf(x2))).intValue();
                        int shiftY = ((Integer) coordinateYMap2.get(Integer.valueOf(y2))).intValue();
                        if (inBounds(shiftX, shiftY, size)) {
                            action.accept(shiftX, shiftY, other.getPrimitive(x2, y2));
                        }
                    });
                });
                return;
            }
            Map<Integer, Integer> coordinateXMap2 = getShiftedCoordinateMap(xOffset, center, wrapEdges, size, otherSize);
            Map<Integer, Integer> coordinateYMap2 = getShiftedCoordinateMap(yOffset, center, wrapEdges, size, otherSize);
            apply((x2, y2) -> {
                int shiftX = ((Integer) coordinateXMap2.get(Integer.valueOf(x2))).intValue();
                int shiftY = ((Integer) coordinateYMap2.get(Integer.valueOf(y2))).intValue();
                if (inBounds(shiftX, shiftY, otherSize)) {
                    action.accept(x2, y2, other.getPrimitive(shiftX, shiftY));
                }
            });
        });
    }
}
