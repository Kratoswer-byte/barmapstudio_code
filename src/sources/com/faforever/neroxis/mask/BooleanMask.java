package com.faforever.neroxis.mask;

import com.faforever.neroxis.brushes.Brushes;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.util.BezierCurve;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.functional.BiIntBooleanConsumer;
import com.faforever.neroxis.util.functional.ToBooleanBiIntFunction;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/BooleanMask.class */
public final class BooleanMask extends PrimitiveMask<Boolean, BooleanMask> {
    private static final int BOOLEANS_PER_LONG = 64;
    private static final long SINGLE_BIT_VALUE = 1;
    private long[] mask;
    private int maskBooleanSize;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !BooleanMask.class.desiredAssertionStatus();
    }

    public BooleanMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(size, random, symmetrySettings, (String) null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public BooleanMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(size, random, symmetrySettings, name);
        this.mask = new long[0];
        this.maskBooleanSize = 0;
    }

    BooleanMask(BooleanMask other) {
        this(other, (String) null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    BooleanMask(BooleanMask other, String name) {
        super(other, name);
        this.mask = new long[0];
        this.maskBooleanSize = 0;
    }

    private <T extends ComparableMask<U, ?>, U extends Comparable<U>> BooleanMask(T other, U minValue) {
        this((ComparableMask) other, (Comparable) minValue, (String) null);
    }

    <T extends ComparableMask<U, ?>, U extends Comparable<U>> BooleanMask(T other, U minValue, String name) {
        this(other.getSize(), other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            apply((x, y) -> {
                setPrimitive(x, y, comparableMask.valueAtGreaterThanEqualTo(x, y, minValue));
            });
        }, other);
    }

    public <T extends ComparableMask<U, ?>, U extends Comparable<U>> BooleanMask(T other, U minValue, U maxValue) {
        this(other, minValue, maxValue, (String) null);
    }

    public <T extends ComparableMask<U, ?>, U extends Comparable<U>> BooleanMask(T other, U minValue, U maxValue, String name) {
        this(other.getSize(), other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            apply((x, y) -> {
                setPrimitive(x, y, comparableMask.valueAtGreaterThanEqualTo(x, y, minValue) && comparableMask.valueAtLessThanEqualTo(x, y, maxValue));
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setBit(int x, int y, boolean value, int size, long[] mask) {
        setBit(bitIndex(x, y, size), value, mask);
    }

    private static void setBit(int bitIndex, boolean value, long[] mask) {
        if (value) {
            int iArrayIndex = arrayIndex(bitIndex);
            mask[iArrayIndex] = mask[iArrayIndex] | (SINGLE_BIT_VALUE << bitIndex);
        } else {
            int iArrayIndex2 = arrayIndex(bitIndex);
            mask[iArrayIndex2] = mask[iArrayIndex2] & ((SINGLE_BIT_VALUE << bitIndex) ^ (-1));
        }
    }

    private static int bitIndex(int x, int y, int size) {
        return (x * size) + y;
    }

    private static boolean getBit(int x, int y, int size, long[] mask) {
        return getBit(bitIndex(x, y, size), mask);
    }

    private static boolean getBit(int bitIndex, long[] mask) {
        return (mask[arrayIndex(bitIndex)] & (SINGLE_BIT_VALUE << bitIndex)) != 0;
    }

    private static int arrayIndex(int bitIndex) {
        return bitIndex / 64;
    }

    private static int minimumArraySize(int size) {
        int numberOfBooleans = size * size;
        return (numberOfBooleans / 64) + (numberOfBooleans % 64 == 0 ? 0 : 1);
    }

    private void setPrimitive(int x, int y, boolean value) {
        setBit(x, y, value, getSize(), this.mask);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BooleanMask blur(int radius) {
        return blur(radius, 0.5f);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public BooleanMask blur(int radius, BooleanMask other) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            int[][] innerCount = getInnerCount();
            BooleanMask limiter = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (limiter.getPrimitive(x, y)) {
                    setPrimitive(x, y, transformAverage(calculateAreaAverageAsInts(radius, x, y, innerCount), 0.5f));
                }
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public BooleanMask copyFrom(BooleanMask other) {
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            fill(source.mask, source.maskBooleanSize);
        }, other);
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected void initializeMask(int size) {
        enqueue(() -> {
            this.mask = new long[minimumArraySize(size)];
            this.maskBooleanSize = size;
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected int getImmediateSize() {
        return this.maskBooleanSize;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage writeToImage(BufferedImage image) {
        assertSize(image.getHeight());
        int size = getSize();
        DataBuffer imageBuffer = image.getRaster().getDataBuffer();
        loop((x, y) -> {
            imageBuffer.setElem(x + (y * size), getPrimitive(x, y) ? 255 : 0);
        });
        return image;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 10);
        writeToImage(image);
        return image;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public String toHash() throws NoSuchAlgorithmException {
        int size = getSize();
        ByteBuffer bytes = ByteBuffer.allocate(size * size);
        loopInSymmetryRegion(SymmetryType.SPAWN, (x, y) -> {
            bytes.put(getPrimitive(x, y) ? (byte) 1 : (byte) 0);
        });
        byte[] data = MessageDigest.getInstance("MD5").digest(bytes.array());
        return HexFormat.of().formatHex(data);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public Boolean get(int x, int y) {
        return Boolean.valueOf(getPrimitive(x, y));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public void set(int x, int y, Boolean value) {
        setPrimitive(x, y, value.booleanValue());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public BooleanMask fill(Boolean value) {
        return (BooleanMask) enqueue(() -> {
            int length = this.mask.length;
            long longValue = value.booleanValue() ? -1L : 0L;
            Arrays.fill(this.mask, longValue);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Boolean getZeroValue() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public BooleanMask setSizeInternal(int newSize) {
        int oldSize = getSize();
        if (oldSize == 1) {
            boolean value = getPrimitive(0, 0);
            initializeMask(newSize);
            fill(Boolean.valueOf(value));
        } else if (oldSize != newSize) {
            long[] oldMask = this.mask;
            initializeMask(newSize);
            Map<Integer, Integer> coordinateMap = getSymmetricScalingCoordinateMap(oldSize, newSize);
            applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                int newX = ((Integer) coordinateMap.get(Integer.valueOf(x))).intValue();
                int newY = ((Integer) coordinateMap.get(Integer.valueOf(y))).intValue();
                boolean value2 = getBit(newX, newY, oldSize, oldMask);
                applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                    setPrimitive(sx, sy, value2);
                });
            });
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public BooleanMask rotateInternal(float radians) {
        long[] oldMask = this.mask;
        int size = getSize();
        initializeMask(size);
        applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
            Vector2 rotatedPoint = SymmetryUtil.getRotatedPoint(x, y, size, radians);
            if (!inBounds(rotatedPoint)) {
                return;
            }
            int newX = StrictMath.round(rotatedPoint.x());
            int newY = StrictMath.round(rotatedPoint.y());
            boolean value = getBit(newX, newY, size, oldMask);
            applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                setPrimitive(sx, sy, value);
            });
        });
        return this;
    }

    public boolean getPrimitive(int x, int y) {
        return getBit(x, y, getSize(), this.mask);
    }

    private BooleanMask fill(long[] arrayToFillFrom, int maskBooleanSize) {
        int arraySize = arrayToFillFrom.length;
        this.mask = new long[arraySize];
        this.maskBooleanSize = maskBooleanSize;
        System.arraycopy(arrayToFillFrom, 0, this.mask, 0, arraySize);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask blur(int radius, float density) {
        return (BooleanMask) enqueue(() -> {
            int[][] innerCount = getInnerCount();
            apply((x, y) -> {
                setPrimitive(x, y, transformAverage(calculateAreaAverageAsInts(radius, x, y, innerCount), density));
            });
        });
    }

    @Override // com.faforever.neroxis.mask.PrimitiveMask
    protected int[][] getInnerCount() {
        int size = getSize();
        int[][] innerCount = new int[size][size];
        apply((x, y) -> {
            calculateInnerValue(innerCount, x, y, getPrimitive(x, y) ? 1 : 0);
        });
        return innerCount;
    }

    private boolean transformAverage(float value, float threshold) {
        return value >= threshold;
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public Boolean getSum() {
        throw new UnsupportedOperationException("Sum not supported for BooleanMask");
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask add(BooleanMask other) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] | source.mask[i];
            }
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void addValueAt(int x, int y, Boolean value) {
        setPrimitive(x, y, value.booleanValue() | getPrimitive(x, y));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask add(BooleanMask other, Boolean value) {
        assertCompatibleMask(other);
        long val = value.booleanValue() ? -1L : 0L;
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] | (source.mask[i] & val);
            }
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask add(BooleanMask other, BooleanMask values) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            BooleanMask val = (BooleanMask) dependencies.get(1);
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] | (source.mask[i] & val.mask[i]);
            }
        }, other, values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask addWithOffset(BooleanMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            applyWithOffset(source, this::addPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void subtractValueAt(int x, int y, Boolean value) {
        setPrimitive(x, y, (!value.booleanValue()) & getPrimitive(x, y));
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public Boolean getAvg() {
        checkNotPipelined();
        float size = getSize();
        return Boolean.valueOf((((float) getCount()) / size) / size > 0.5f);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask subtract(BooleanMask other) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] & (source.mask[i] ^ (-1));
            }
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask subtract(BooleanMask other, Boolean value) {
        assertCompatibleMask(other);
        long val = value.booleanValue() ? -1L : 0L;
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] & ((source.mask[i] & val) ^ (-1));
            }
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask subtract(BooleanMask other, BooleanMask values) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            BooleanMask val = (BooleanMask) dependencies.get(1);
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] & ((source.mask[i] & val.mask[i]) ^ (-1));
            }
        }, other, values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask subtractWithOffset(BooleanMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            applyWithOffset(source, this::subtractPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask multiply(BooleanMask other) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] & source.mask[i];
            }
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void multiplyValueAt(int x, int y, Boolean value) {
        setPrimitive(x, y, value.booleanValue() & getPrimitive(x, y));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask multiply(BooleanMask other, Boolean value) {
        assertCompatibleMask(other);
        long val = value.booleanValue() ? -1L : 0L;
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] & ((source.mask[i] ^ (-1)) | val);
            }
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask multiply(BooleanMask other, BooleanMask values) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            BooleanMask val = (BooleanMask) dependencies.get(1);
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] & ((source.mask[i] ^ (-1)) | val.mask[i]);
            }
        }, other, values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask multiplyWithOffset(BooleanMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            applyWithOffset(source, this::multiplyPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask divide(BooleanMask other) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] ^ source.mask[i];
            }
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void divideValueAt(int x, int y, Boolean value) {
        setPrimitive(x, y, value.booleanValue() ^ getPrimitive(x, y));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask divide(BooleanMask other, Boolean value) {
        assertCompatibleMask(other);
        long val = value.booleanValue() ? -1L : 0L;
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] ^ (source.mask[i] & val);
            }
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask divide(BooleanMask other, BooleanMask value) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            BooleanMask val = (BooleanMask) dependencies.get(1);
            for (int i = 0; i < this.mask.length; i++) {
                long[] jArr = this.mask;
                int i2 = i;
                jArr[i2] = jArr[i2] ^ (source.mask[i] & val.mask[i]);
            }
        }, other, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public BooleanMask divideWithOffset(BooleanMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            applyWithOffset(source, this::dividePrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public int getCount() {
        checkNotPipelined();
        int count = 0;
        for (int i = 0; i < this.mask.length; i++) {
            long longValue = this.mask[i];
            if (i == this.mask.length - 1) {
                int overshoot = 64 - ((getSize() * getSize()) % 64);
                longValue <<= overshoot;
            }
            count += Long.bitCount(longValue);
        }
        return count;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private BooleanMask add(ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) apply((x, y) -> {
            addPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private BooleanMask subtract(ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) apply((x, y) -> {
            subtractPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private BooleanMask multiply(ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) apply((x, y) -> {
            multiplyPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private BooleanMask divide(ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) apply((x, y) -> {
            dividePrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    private void addPrimitiveAt(int x, int y, boolean value) {
        setPrimitive(x, y, value | getPrimitive(x, y));
    }

    private void subtractPrimitiveAt(int x, int y, boolean value) {
        setPrimitive(x, y, (!value) & getPrimitive(x, y));
    }

    private void multiplyPrimitiveAt(int x, int y, boolean value) {
        setPrimitive(x, y, value & getPrimitive(x, y));
    }

    private void dividePrimitiveAt(int x, int y, boolean value) {
        setPrimitive(x, y, value ^ getPrimitive(x, y));
    }

    @Override // com.faforever.neroxis.mask.ComparableMask
    public Boolean getMin() {
        throw new UnsupportedOperationException("Min not supported for BooleanMask");
    }

    @Override // com.faforever.neroxis.mask.ComparableMask
    public Boolean getMax() {
        throw new UnsupportedOperationException("Max not supported for BooleanMask");
    }

    public boolean getPrimitive(Vector2 location) {
        return getPrimitive(StrictMath.round(location.x()), StrictMath.round(location.y()));
    }

    void setPrimitive(Vector2 location, boolean value) {
        setPrimitive(StrictMath.round(location.x()), StrictMath.round(location.y()), value);
    }

    public <T extends Comparable<T>, U extends ComparableMask<T, U>> BooleanMask init(ComparableMask<T, U> other, T minValue) {
        init(other.copyAsBooleanMask(minValue));
        return this;
    }

    public BooleanMask randomize(float density) {
        return randomize(density, SymmetryType.TERRAIN);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask randomize(float density, SymmetryType symmetryType) {
        if ($assertionsDisabled || this.random != null) {
            return (BooleanMask) setWithSymmetry(symmetryType, (x, y) -> {
                return Boolean.valueOf(this.random.nextFloat() < density);
            });
        }
        throw new AssertionError();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask flipValues(float density) {
        if ($assertionsDisabled || this.random != null) {
            return (BooleanMask) setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                return Boolean.valueOf(getPrimitive(x, y) && this.random.nextFloat() < density);
            });
        }
        throw new AssertionError();
    }

    public <T extends Comparable<T>, U extends ComparableMask<T, U>> BooleanMask init(ComparableMask<T, U> other, T minValue, T maxValue) {
        init(other.copyAsBooleanMask(minValue, maxValue));
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask randomWalk(int numWalkers, int numSteps) {
        if ($assertionsDisabled || this.random != null) {
            return (BooleanMask) enqueue(() -> {
                int size = getSize();
                int maxXBound = getMaxXBound(SymmetryType.TERRAIN);
                IntUnaryOperator maxYBoundFunction = getMaxYBoundFunction(SymmetryType.TERRAIN);
                IntUnaryOperator minYBoundFunction = getMinYBoundFunction(SymmetryType.TERRAIN);
                for (int i = 0; i < numWalkers; i++) {
                    int x = this.random.nextInt(maxXBound - 0) + 0;
                    int maxYBound = maxYBoundFunction.applyAsInt(x);
                    int minYBound = minYBoundFunction.applyAsInt(x);
                    int y = this.random.nextInt((maxYBound - minYBound) + 1) + minYBound;
                    for (int j = 0; j < numSteps; j++) {
                        if (inBounds(x, y, size)) {
                            applyAtSymmetryPoints(x, y, SymmetryType.TERRAIN, (sx, sy) -> {
                                setPrimitive(sx, sy, true);
                            });
                        }
                        switch (this.random.nextInt(4)) {
                            case 0:
                                x++;
                                break;
                            case 1:
                                x--;
                                break;
                            case 2:
                                y++;
                                break;
                            case 3:
                                y--;
                                break;
                        }
                    }
                }
            });
        }
        throw new AssertionError();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask guidedWalkWithBrush(Vector2 start, Vector2 target, String brushName, int size, int numberOfUses, float minValue, float maxValue, int maxStepSize, boolean wrapEdges) {
        if ($assertionsDisabled || this.random != null) {
            return (BooleanMask) enqueue(() -> {
                BooleanMask brush = ((FloatMask) Brushes.loadBrush(brushName).setSize(size)).copyAsBooleanMask(Float.valueOf(minValue), Float.valueOf(maxValue));
                float targetX = target.x();
                float targetY = target.y();
                if (wrapEdges) {
                    int maskSize = getSize();
                    int halfSize = maskSize / 2;
                    float startX = start.x();
                    float startY = start.y();
                    float distanceToMidX = targetX - startX;
                    float distanceToMidY = targetY - startY;
                    if (StrictMath.abs(distanceToMidX) > halfSize) {
                        if (distanceToMidX > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                            targetX -= maskSize;
                        } else {
                            targetX += maskSize;
                        }
                    }
                    if (StrictMath.abs(distanceToMidY) > halfSize) {
                        targetY = distanceToMidY > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT ? targetY - maskSize : targetY + maskSize;
                    }
                }
                for (int i = 0; i < numberOfUses; i++) {
                    addWithOffset(brush, start, true, wrapEdges);
                    int dx = (targetX > start.x() ? 1 : -1) * this.random.nextInt(maxStepSize + 1);
                    int dy = (targetY > start.y() ? 1 : -1) * this.random.nextInt(maxStepSize + 1);
                    start.add(new Vector2(dx, dy));
                }
            });
        }
        throw new AssertionError();
    }

    public BooleanMask pathBezier(Vector2 start, Vector2 end, int minOrder, int maxOrder, int numMiddlePoints, float midPointMaxDistance, float midPointMinDistance) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        int size = getSize();
        List<Vector2> rawPoints = new ArrayList<>();
        rawPoints.add(start);
        for (int i = 0; i < numMiddlePoints; i++) {
            Vector2 previousLoc = (Vector2) rawPoints.getLast();
            float angle = ((float) ((((double) ((this.random.nextFloat() - 0.5f) * 2.0f)) * 3.141592653589793d) / 2.0d)) + previousLoc.angleTo(end);
            float magnitude = (this.random.nextFloat() * start.getDistance(end)) / numMiddlePoints;
            Vector2 nextLoc = previousLoc.addPolar(angle, magnitude);
            rawPoints.add(nextLoc);
        }
        rawPoints.add(end);
        List<Vector2> checkPoints = rawPoints.stream().map(point -> {
            return point.round().clampMin(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT).clampMax(size - 1);
        }).toList();
        for (int i2 = 0; i2 < checkPoints.size() - 1; i2++) {
            Vector2 location = checkPoints.get(i2);
            Vector2 nextLoc2 = checkPoints.get(i2 + 1);
            BezierCurve bezierCurve = new BezierCurve(this.random.nextInt(maxOrder - minOrder) + minOrder, this.random.split()).transformTo(location, nextLoc2);
            List<Vector2> points = new ArrayList<>();
            float f = SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            while (true) {
                float j = f;
                if (j <= 1.0f) {
                    points.add(bezierCurve.getPoint(j));
                    f = j + (1.0f / size);
                }
            }
            fillCoordinates((Collection) points.stream().filter(this::inBounds).collect(Collectors.toList()), true);
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask connect(Vector2 start, Vector2 end, float maxStepSize, int numMiddlePoints, float midPointMaxDistance, float midPointMinDistance, float maxAngleError, SymmetryType symmetryType) {
        return (BooleanMask) enqueue(() -> {
            path(start, end, maxStepSize, numMiddlePoints, midPointMaxDistance, midPointMinDistance, maxAngleError, symmetryType);
            if (this.symmetrySettings.getSymmetry(symmetryType).getNumSymPoints() > 1) {
                List<Vector2> symmetryPoints = getSymmetryPointsWithOutOfBounds(end, symmetryType);
                path(start, (Vector2) symmetryPoints.getFirst(), maxStepSize, numMiddlePoints, midPointMaxDistance, midPointMinDistance, maxAngleError, symmetryType);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask path(Vector2 start, Vector2 end, float maxStepSize, int numMiddlePoints, float midPointMaxDistance, float midPointMinDistance, float maxAngleError, SymmetryType symmetryType) {
        if ($assertionsDisabled || this.random != null) {
            return (BooleanMask) enqueue(() -> {
                int size = getSize();
                List<Vector2> rawPoints = new ArrayList<>();
                rawPoints.add(start);
                for (int i = 0; i < numMiddlePoints; i++) {
                    Vector2 previousLoc = (Vector2) rawPoints.getLast();
                    float angle = ((float) ((((double) ((this.random.nextFloat() - 0.5f) * 2.0f)) * 3.141592653589793d) / 2.0d)) + previousLoc.angleTo(end);
                    if (this.symmetrySettings.terrainSymmetry() == Symmetry.POINT4 && ((double) angle) % 1.5707963267948966d < 0.39269908169872414d) {
                        int direction = this.random.nextBoolean() ? -1 : 1;
                        angle += (float) ((((double) ((direction * ((this.random.nextFloat() * 0.5f) + 0.5f)) * 2.0f)) * 3.141592653589793d) / 4.0d);
                    }
                    float magnitude = (this.random.nextFloat() * (midPointMaxDistance - midPointMinDistance)) + midPointMinDistance;
                    rawPoints.add(previousLoc.addPolar(angle, magnitude));
                }
                rawPoints.add(end);
                List<Vector2> checkPoints = rawPoints.stream().map(point -> {
                    return point.round().clampMin(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT).clampMax(size - 1);
                }).toList();
                int numSteps = 0;
                for (int i2 = 0; i2 < checkPoints.size() - 1; i2++) {
                    Vector2 location = checkPoints.get(i2);
                    Vector2 nextLoc = checkPoints.get(i2 + 1);
                    float oldAngle = location.angleTo(nextLoc) + ((this.random.nextFloat() - 0.5f) * 2.0f * maxAngleError);
                    while (location.getDistance(nextLoc) > maxStepSize && numSteps < size * size) {
                        if (inBounds(location)) {
                            applyAtSymmetryPoints((int) location.x(), (int) location.y(), SymmetryType.TERRAIN, (sx, sy) -> {
                                setPrimitive(sx, sy, true);
                            });
                        }
                        float magnitude2 = StrictMath.max(1.0f, this.random.nextFloat() * maxStepSize);
                        float angle2 = (oldAngle * 0.5f) + (location.angleTo(nextLoc) * 0.5f) + ((this.random.nextFloat() - 0.5f) * 2.0f * maxAngleError);
                        location = location.addPolar(angle2, magnitude2).round();
                        oldAngle = angle2;
                        numSteps++;
                    }
                    if (numSteps >= size * size) {
                        return;
                    }
                }
            });
        }
        throw new AssertionError();
    }

    private void drawLine(int x0, int y0, int x1, int y1) {
        int mapSize = getSize();
        int deltaX = StrictMath.abs(x1 - x0);
        int deltaY = StrictMath.abs(y1 - y0);
        int stepX = x0 < x1 ? 1 : -1;
        int stepY = y0 < y1 ? 1 : -1;
        int error = deltaX - deltaY;
        while (true) {
            if (x0 >= 0 && y0 >= 0 && x0 < mapSize && y0 < mapSize) {
                setPrimitive(x0, y0, true);
            }
            if (x0 != x1 || y0 != y1) {
                int error2 = 2 * error;
                if (error2 > (-deltaY)) {
                    error -= deltaY;
                    x0 += stepX;
                }
                if (error2 < deltaX) {
                    error += deltaX;
                    y0 += stepY;
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask drawSymmetryLines(Symmetry symmetry) {
        return (BooleanMask) enqueue(() -> {
            int mapSize = getSize();
            int halfX = (mapSize + 1) >> 1;
            int halfY = (mapSize + 1) >> 1;
            int numSymPoints = this.symmetrySettings.teamSymmetry().getNumSymPoints();
            if (numSymPoints == 3 || numSymPoints >= 5) {
                for (int slice = 0; slice < numSymPoints; slice++) {
                    Vector2 rotated = SymmetryUtil.getRotatedPoint(-mapSize, halfY, mapSize, (float) ((6.283185307179586d / ((double) numSymPoints)) * ((double) slice)));
                    drawLine(halfX, halfY, (int) rotated.x(), (int) rotated.y());
                }
                return;
            }
            switch (symmetry) {
                case X:
                case POINT2:
                case POINT4:
                case POINT6:
                case POINT8:
                case POINT10:
                case POINT12:
                case POINT14:
                case POINT16:
                    drawLine(0, halfY, mapSize, halfY);
                    break;
                case Z:
                    drawLine(halfX, 0, halfX, mapSize);
                    break;
                case XZ:
                    drawLine(0, 0, mapSize, mapSize);
                    break;
                case ZX:
                    drawLine(0, mapSize, mapSize, 0);
                    break;
                case QUAD:
                    drawLine(0, halfY, mapSize, halfY);
                    drawLine(halfX, 0, halfX, mapSize);
                    break;
                case DIAG:
                    drawLine(0, 0, mapSize, mapSize);
                    drawLine(0, mapSize, mapSize, 0);
                    break;
                case POINT3:
                case POINT5:
                case POINT7:
                case POINT9:
                case POINT11:
                case POINT13:
                case POINT15:
                    for (int slice2 = 0; slice2 < numSymPoints; slice2++) {
                        Vector2 rotated2 = SymmetryUtil.getRotatedPoint(-mapSize, halfY, mapSize, (float) ((6.283185307179586d / ((double) numSymPoints)) * ((double) slice2)));
                        drawLine(halfX, halfY, (int) rotated2.x(), (int) rotated2.y());
                    }
                    break;
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask space(float minSpacing, float maxSpacing) {
        return (BooleanMask) enqueue(() -> {
            List<Vector2> coordinates = getRandomCoordinates(minSpacing, maxSpacing);
            clear();
            fillCoordinates(coordinates, true);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask invert() {
        return (BooleanMask) enqueue(() -> {
            for (int i = 0; i < this.mask.length; i++) {
                this.mask[i] = this.mask[i] ^ (-1);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask inflate(int radius) {
        return (BooleanMask) enqueue(() -> {
            long[] maskCopy = getMaskCopy();
            apply((x, y) -> {
                if (getPrimitive(x, y) && isEdge(x, y)) {
                    markInRadius(radius, maskCopy, x, y, true);
                }
            });
            this.mask = maskCopy;
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask deflate(int radius) {
        return (BooleanMask) enqueue(() -> {
            long[] maskCopy = getMaskCopy();
            apply((x, y) -> {
                if (!getPrimitive(x, y) && isEdge(x, y)) {
                    markInRadius(radius, maskCopy, x, y, false);
                }
            });
            this.mask = maskCopy;
        });
    }

    public BooleanMask progressiveWalk(int numWalkers, int numSteps) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        int size = getSize();
        IntUnaryOperator maxYBoundFunction = getMaxYBoundFunction(SymmetryType.TERRAIN);
        IntUnaryOperator minYBoundFunction = getMinYBoundFunction(SymmetryType.TERRAIN);
        int maxXBound = getMaxXBound(SymmetryType.TERRAIN);
        for (int i = 0; i < numWalkers; i++) {
            int x = this.random.nextInt(maxXBound - 0) + 0;
            int maxYBound = maxYBoundFunction.applyAsInt(x);
            int minYBound = minYBoundFunction.applyAsInt(x);
            int y = this.random.nextInt((maxYBound - minYBound) + 1) + minYBound;
            List<Integer> directions = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
            int regressiveDir = this.random.nextInt(directions.size());
            directions.remove(regressiveDir);
            for (int j = 0; j < numSteps; j++) {
                if (inBounds(x, y, size)) {
                    applyAtSymmetryPoints(x, y, SymmetryType.TERRAIN, (sx, sy) -> {
                        setPrimitive(sx, sy, true);
                    });
                }
                switch (directions.get(this.random.nextInt(directions.size())).intValue()) {
                    case 0:
                        x++;
                        break;
                    case 1:
                        x--;
                        break;
                    case 2:
                        y++;
                        break;
                    case 3:
                        y--;
                        break;
                }
            }
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask cutCorners() {
        return (BooleanMask) enqueue(() -> {
            int size = getSize();
            long[] maskCopy = getMaskCopy();
            apply((x, y) -> {
                int count = 0;
                if (x > 0 && !getPrimitive(x - 1, y)) {
                    count = 0 + 1;
                }
                if (y > 0 && !getPrimitive(x, y - 1)) {
                    count++;
                }
                if (x < size - 1 && !getPrimitive(x + 1, y)) {
                    count++;
                }
                if (y < size - 1 && !getPrimitive(x, y + 1)) {
                    count++;
                }
                if (count > 1) {
                    setBit(bitIndex(x, y, size), false, maskCopy);
                }
            });
            this.mask = maskCopy;
        });
    }

    private long[] getMaskCopy() {
        checkNotPipelined();
        int arraySize = this.mask.length;
        long[] maskCopy = new long[arraySize];
        System.arraycopy(this.mask, 0, maskCopy, 0, arraySize);
        return maskCopy;
    }

    private void markInRadius(int radius, long[] maskCopy, int x, int y, boolean value) {
        float radius2 = (radius + 0.5f) * (radius + 0.5f);
        int size = getSize();
        int minX = StrictMath.max(x - radius, 0);
        int maxX = StrictMath.min(x + radius + 1, size);
        int minY = StrictMath.max(y - radius, 0);
        int maxY = StrictMath.min(y + radius + 1, size);
        for (int x2 = minX; x2 < maxX; x2++) {
            int xDistance = x - x2;
            int xDistanceSquared = xDistance * xDistance;
            for (int y2 = minY; y2 < maxY; y2++) {
                int yDistance = y - y2;
                int yDistanceSquared = yDistance * yDistance;
                if (xDistanceSquared + yDistanceSquared <= radius2) {
                    setBit(bitIndex(x2, y2, size), value, maskCopy);
                }
            }
        }
    }

    public BooleanMask erode(float strength) {
        return erode(strength, 1);
    }

    public BooleanMask acid(float strength, int size) {
        BooleanMask holes = new BooleanMask(this, getName() + "holes");
        holes.randomize(strength, SymmetryType.SPAWN).inflate(size);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            subtract(source);
        }, holes);
    }

    public BooleanMask splat(float strength, int size) {
        BooleanMask holes = new BooleanMask(this, getName() + "splat");
        holes.randomize(strength, SymmetryType.SPAWN).inflate(size);
        return (BooleanMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            add(source);
        }, holes);
    }

    public BooleanMask dilute(float strength) {
        return dilute(strength, 1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask outline() {
        return (BooleanMask) enqueue(() -> {
            int size = getSize();
            long[] maskCopy = new long[minimumArraySize(size)];
            apply((x, y) -> {
                setBit(x, y, isEdge(x, y), size, maskCopy);
            });
            this.mask = maskCopy;
        });
    }

    public boolean isEdge(int x, int y) {
        boolean value = getPrimitive(x, y);
        int size = getSize();
        return (x > 0 && getPrimitive(x - 1, y) != value) || (y > 0 && getPrimitive(x, y - 1) != value) || ((x < size - 1 && getPrimitive(x + 1, y) != value) || (y < size - 1 && getPrimitive(x, y + 1) != value));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask dilute(float strength, int count) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        SymmetryType symmetryType = SymmetryType.SPAWN;
        return (BooleanMask) enqueue(() -> {
            int size = getSize();
            for (int i = 0; i < count; i++) {
                long[] maskCopy = getMaskCopy();
                applyWithSymmetry(symmetryType, (x, y) -> {
                    if (!getPrimitive(x, y) && this.random.nextFloat() < strength && isEdge(x, y)) {
                        applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                            setBit(sx, sy, true, size, maskCopy);
                        });
                    }
                });
                this.mask = maskCopy;
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask erode(float strength, int count) {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        SymmetryType symmetryType = SymmetryType.SPAWN;
        return (BooleanMask) enqueue(() -> {
            int size = getSize();
            for (int i = 0; i < count; i++) {
                long[] maskCopy = getMaskCopy();
                applyWithSymmetry(symmetryType, (x, y) -> {
                    if (getPrimitive(x, y) && this.random.nextFloat() < strength && isEdge(x, y)) {
                        applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                            setBit(sx, sy, false, size, maskCopy);
                        });
                    }
                });
                this.mask = maskCopy;
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask addBrush(Vector2 location, String brushName, float minValue, float maxValue, int size) {
        return (BooleanMask) enqueue(() -> {
            FloatMask brush = (FloatMask) Brushes.loadBrush(brushName).setSize(size);
            addWithOffset(brush, Float.valueOf(minValue), Float.valueOf(maxValue), location, false);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T extends ComparableMask<U, ?>, U extends Comparable<U>> BooleanMask addWithOffset(T other, U minValue, U maxValue, Vector2 offset, boolean wrapEdges) {
        return (BooleanMask) addWithOffset(other.copyAsBooleanMask(minValue, maxValue), offset, true, wrapEdges);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends Comparable<T>, U extends ComparableMask<T, U>> BooleanMask initMaxima(ComparableMask<T, U> other, T minValue, T maxValue) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                Comparable comparable = (Comparable) comparableMask.get(x, y);
                return Boolean.valueOf(comparable.compareTo(minValue) >= 0 && comparable.compareTo(maxValue) < 0 && comparableMask.isLocalMax(x, y));
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends Comparable<T>, U extends ComparableMask<T, U>> BooleanMask initMinima(ComparableMask<T, U> other, T minValue, T maxValue) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                Comparable comparable = (Comparable) comparableMask.get(x, y);
                return Boolean.valueOf(comparable.compareTo(minValue) >= 0 && comparable.compareTo(maxValue) < 0 && comparableMask.isLocalMin(x, y));
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends Comparable<T>, U extends ComparableMask<T, U>> BooleanMask init1DMaxima(ComparableMask<T, U> other, T minValue, T maxValue) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                Comparable comparable = (Comparable) comparableMask.get(x, y);
                return Boolean.valueOf(comparable.compareTo(minValue) >= 0 && comparable.compareTo(maxValue) < 0 && comparableMask.isLocal1DMax(x, y));
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends Comparable<T>, U extends ComparableMask<T, U>> BooleanMask init1DMinima(ComparableMask<T, U> other, T minValue, T maxValue) {
        assertCompatibleMask(other);
        return (BooleanMask) enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                Comparable comparable = (Comparable) comparableMask.get(x, y);
                return Boolean.valueOf(comparable.compareTo(minValue) >= 0 && comparable.compareTo(maxValue) < 0 && comparableMask.isLocal1DMin(x, y));
            });
        }, other);
    }

    public BooleanMask limitToSymmetryRegion() {
        return limitToSymmetryRegion(SymmetryType.TEAM);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask limitToSymmetryRegion(SymmetryType symmetryType) {
        int minXBound = 0;
        int maxXBound = getMaxXBound(symmetryType);
        IntUnaryOperator minYBoundFunction = getMinYBoundFunction(symmetryType);
        IntUnaryOperator maxYBoundFunction = getMaxYBoundFunction(symmetryType);
        Stream<Integer> streamBoxed = IntStream.range(0, maxXBound).boxed();
        Function functionIdentity = Function.identity();
        Objects.requireNonNull(minYBoundFunction);
        Map<Integer, Integer> minYBoundMap = (Map) streamBoxed.collect(Collectors.toMap(functionIdentity, (v1) -> {
            return r2.applyAsInt(v1);
        }));
        Stream<Integer> streamBoxed2 = IntStream.range(0, maxXBound).boxed();
        Function functionIdentity2 = Function.identity();
        Objects.requireNonNull(maxYBoundFunction);
        Map<Integer, Integer> maxYBoundMap = (Map) streamBoxed2.collect(Collectors.toMap(functionIdentity2, (v1) -> {
            return r2.applyAsInt(v1);
        }));
        return (BooleanMask) apply((x, y) -> {
            if (!getPrimitive(x, y)) {
                return;
            }
            boolean value = x >= minXBound && x < maxXBound && y >= ((Integer) minYBoundMap.get(Integer.valueOf(x))).intValue() && y < ((Integer) maxYBoundMap.get(Integer.valueOf(x))).intValue();
            setPrimitive(x, y, value);
        });
    }

    public BooleanMask limitToCenteredCircle(float circleRadius) {
        int size = getSize();
        BooleanMask symmetryLimit = new BooleanMask(size, (RandomGenerator.SplittableGenerator) null, this.symmetrySettings, getName() + "symmetryLimit");
        symmetryLimit.fillCircle(size / 2.0f, size / 2.0f, circleRadius, true);
        return multiply(symmetryLimit);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask fillShape(Vector2 location) {
        return (BooleanMask) fillCoordinates(getShapeCoordinates(location), Boolean.valueOf(!getPrimitive(location)));
    }

    public BooleanMask fillGaps(int minDist) {
        BooleanMask filledGaps = copyAsDistanceField().copyAsLocalMaximums(Float.valueOf(1.0f), Float.valueOf(minDist / 2.0f));
        filledGaps.inflate(minDist / 2);
        return add(filledGaps);
    }

    public BooleanMask widenGaps(int minDist) {
        BooleanMask filledGaps = copyAsDistanceField().copyAsLocalMaximums(Float.valueOf(1.0f), Float.valueOf(minDist / 2.0f));
        filledGaps.inflate(minDist / 2);
        return subtract(filledGaps);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask removeAreasSmallerThan(int maxArea) {
        int size = getSize();
        Set<Vector2> seen = new HashSet<>(size * size, 1.0f);
        return (BooleanMask) applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
            Vector2 location = new Vector2(x, y);
            if (!seen.contains(location)) {
                boolean value = getPrimitive(location);
                LinkedHashSet<Vector2> shapeCoordinates = getShapeCoordinates(location, maxArea);
                seen.addAll(shapeCoordinates);
                if (shapeCoordinates.size() < maxArea) {
                    fillCoordinates(shapeCoordinates, Boolean.valueOf(!value));
                }
            }
        });
    }

    public FloatMask copyAsFloatMask(float low, float high) {
        return new FloatMask(this, low, high, getName() + "toFloat");
    }

    public FloatMask copyAsFloatMask(float low, float high, String name) {
        return new FloatMask(this, low, high, name);
    }

    public IntegerMask copyAsIntegerMask(int low, int high) {
        return copyAsIntegerMask(low, high, getName() + "toInteger");
    }

    public IntegerMask copyAsIntegerMask(int low, int high, String name) {
        return new IntegerMask(this, low, high, name);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask removeAreasBiggerThan(int minArea) {
        return subtract(((BooleanMask) copy()).removeAreasSmallerThan(minArea));
    }

    public BooleanMask removeAreasOutsideSizeRange(int minArea, int maxArea) {
        return removeAreasSmallerThan(minArea).removeAreasBiggerThan(maxArea);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask removeAreasInSizeRange(int minArea, int maxArea) {
        return subtract(((BooleanMask) copy()).removeAreasOutsideSizeRange(minArea, maxArea));
    }

    public LinkedHashSet<Vector2> getShapeCoordinates(Vector2 location) {
        int size = getSize();
        return getShapeCoordinates(location, size * size);
    }

    public LinkedHashSet<Vector2> getShapeCoordinates(Vector2 location, int maxSize) {
        checkNotPipelined();
        LinkedHashSet<Vector2> areaHash = new LinkedHashSet<>();
        LinkedHashSet<Vector2> edgeHash = new LinkedHashSet<>();
        List<Vector2> queue = new ArrayList<>();
        LinkedHashSet<Vector2> queueHash = new LinkedHashSet<>();
        List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1}, new int[]{1, 0});
        boolean value = getPrimitive(location);
        queue.add(location);
        queueHash.add(location);
        while (!queue.isEmpty()) {
            Vector2 next = (Vector2) queue.removeFirst();
            queueHash.remove(next);
            if (getPrimitive(next) == value && !areaHash.contains(next)) {
                areaHash.add(next);
                edges.forEach(e -> {
                    Vector2 newLocation = new Vector2(next.x() + e[0], next.y() + e[1]);
                    if (!queueHash.contains(newLocation) && !areaHash.contains(newLocation) && !edgeHash.contains(newLocation) && inBounds(newLocation)) {
                        queue.add(newLocation);
                        queueHash.add(newLocation);
                    }
                });
            } else if (getPrimitive(next) != value) {
                edgeHash.add(next);
            }
            if (areaHash.size() > maxSize) {
                break;
            }
        }
        return areaHash;
    }

    public FloatMask copyAsDistanceField() {
        return copyAsDistanceField(getName() + "DistanceField");
    }

    public FloatMask copyAsDistanceField(String name) {
        int size = getSize();
        FloatMask distanceField = new FloatMask(this, size * size, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, name);
        distanceField.parabolicMinimization();
        return distanceField;
    }

    public List<Vector2> getSpacedCoordinates(float radius, int spacing) {
        List<Vector2> coordinateList = getAllCoordinates(spacing);
        return spaceCoordinates(radius, coordinateList);
    }

    private List<Vector2> spaceCoordinates(float radius, List<Vector2> coordinateList) {
        List<Vector2> chosenCoordinates = new ArrayList<>();
        while (!coordinateList.isEmpty()) {
            Vector2 location = (Vector2) coordinateList.removeFirst();
            chosenCoordinates.add(location);
            coordinateList.removeIf(loc -> {
                return location.getDistance(loc) < radius;
            });
        }
        return chosenCoordinates;
    }

    public List<Vector2> getAllCoordinates(int spacing) {
        int size = getSize();
        List<Vector2> coordinates = new ArrayList<>(size * size);
        int i = 0;
        while (true) {
            int x = i;
            if (x < size) {
                int i2 = 0;
                while (true) {
                    int y = i2;
                    if (y < size) {
                        Vector2 location = new Vector2(x, y);
                        coordinates.add(location);
                        i2 = y + spacing;
                    }
                }
                i = x + spacing;
            } else {
                return coordinates;
            }
        }
    }

    public List<Vector2> getSpacedCoordinatesEqualTo(boolean value, float radius, int spacing) {
        List<Vector2> coordinateList = getAllCoordinatesEqualTo(value, spacing);
        return spaceCoordinates(radius, coordinateList);
    }

    public List<Vector2> getAllCoordinatesEqualTo(boolean value, int spacing) {
        int numPossibleCoordinates;
        checkNotPipelined();
        int size = getSize();
        int numTrue = getCount();
        if (value) {
            numPossibleCoordinates = numTrue;
        } else {
            numPossibleCoordinates = (size * size) - numTrue;
        }
        List<Vector2> coordinates = new ArrayList<>(numPossibleCoordinates / spacing);
        int i = 0;
        while (true) {
            int x = i;
            if (x < size) {
                int i2 = 0;
                while (true) {
                    int y = i2;
                    if (y < size) {
                        if (getPrimitive(x, y) == value) {
                            coordinates.add(new Vector2(x, y));
                        }
                        i2 = y + spacing;
                    }
                }
                i = x + spacing;
            } else {
                return coordinates;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<Vector2> getRandomCoordinates(float minSpacing, float maxSpacing, SymmetryType symmetryType) {
        List<Vector2> coordinateList;
        checkNotPipelined();
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        if (symmetryType != null) {
            coordinateList = ((BooleanMask) copy()).limitToSymmetryRegion(symmetryType).getAllCoordinatesEqualTo(true);
        } else {
            coordinateList = getAllCoordinatesEqualTo(true);
        }
        List<Vector2> chosenCoordinates = new ArrayList<>((int) (((double) coordinateList.size()) * 0.25d));
        while (!coordinateList.isEmpty()) {
            Vector2 location = coordinateList.remove(this.random.nextInt(coordinateList.size()));
            float spacing = (this.random.nextFloat() * (maxSpacing - minSpacing)) + minSpacing;
            float spacingSquared = spacing * spacing;
            chosenCoordinates.add(location);
            coordinateList.removeIf(loc -> {
                return location.getDistance(loc) < spacing;
            });
            if (symmetryType != null) {
                List<Vector2> list = coordinateList;
                applyAtSymmetryPoints(location, symmetryType, (x, y) -> {
                    list.removeIf(loc2 -> {
                        return new Vector2((float) x, (float) y).getDistanceSquared(loc2) < spacingSquared;
                    });
                });
            }
        }
        return chosenCoordinates;
    }

    public List<Vector2> getRandomCoordinates(float spacing) {
        return getRandomCoordinates(spacing, SymmetryType.TEAM);
    }

    public List<Vector2> getRandomCoordinates(float spacing, SymmetryType symmetryType) {
        return getRandomCoordinates(spacing, spacing, symmetryType);
    }

    public List<Vector2> getRandomCoordinates(float minSpacing, float maxSpacing) {
        return getRandomCoordinates(minSpacing, maxSpacing, SymmetryType.TEAM);
    }

    public List<Vector2> getAllCoordinatesEqualTo(boolean value) {
        return getAllCoordinatesEqualTo(value, 1);
    }

    public Vector2 getRandomPosition() {
        if (!$assertionsDisabled && this.random == null) {
            throw new AssertionError();
        }
        checkNotPipelined();
        int size = getSize();
        int numPossibleCoordinates = getCount();
        if (numPossibleCoordinates == 0) {
            return null;
        }
        int index = this.random.nextInt(numPossibleCoordinates);
        int count = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (getPrimitive(x, y)) {
                    int i = count;
                    count++;
                    if (i == index) {
                        return new Vector2(x, y);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Did not find a coordinate");
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask addPrimitiveWithSymmetry(SymmetryType symmetryType, ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) applyWithSymmetry(symmetryType, (x, y) -> {
            boolean value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                addPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask subtractPrimitiveWithSymmetry(SymmetryType symmetryType, ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) applyWithSymmetry(symmetryType, (x, y) -> {
            boolean value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                subtractPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask multiplyPrimitiveWithSymmetry(SymmetryType symmetryType, ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) applyWithSymmetry(symmetryType, (x, y) -> {
            boolean value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                multiplyPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public BooleanMask dividePrimitiveWithSymmetry(SymmetryType symmetryType, ToBooleanBiIntFunction valueFunction) {
        return (BooleanMask) applyWithSymmetry(symmetryType, (x, y) -> {
            boolean value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                dividePrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private BooleanMask applyWithOffset(BooleanMask other, BiIntBooleanConsumer action, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (BooleanMask) enqueue(() -> {
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
                            boolean value = other.getPrimitive(x, y);
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
