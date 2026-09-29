package com.faforever.neroxis.mask;

import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.functional.ToIntBiIntFunction;
import com.faforever.neroxis.util.functional.TriIntConsumer;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Map;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/IntegerMask.class */
public final class IntegerMask extends PrimitiveMask<Integer, IntegerMask> {
    private int[][] mask;

    public IntegerMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(size, random, symmetrySettings, (String) null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public IntegerMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(size, random, symmetrySettings, name);
        this.mask = new int[0][0];
    }

    IntegerMask(IntegerMask other) {
        this(other, null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    IntegerMask(IntegerMask other, String name) {
        super(other, name);
        this.mask = new int[0][0];
    }

    IntegerMask(BooleanMask other, int low, int high) {
        this(other, low, high, (String) null);
    }

    public IntegerMask(BooleanMask other, int low, int high, String name) {
        this(other.getSize(), other.getNextRandomGenerator(), other.getSymmetrySettings(), name);
        enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                setPrimitive(x, y, source.getPrimitive(x, y) ? high : low);
            });
        }, other);
    }

    public IntegerMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        this(sourceImage.getHeight(), random, symmetrySettings, name);
        DataBuffer imageBuffer = sourceImage.getRaster().getDataBuffer();
        int size = getSize();
        apply((x, y) -> {
            setPrimitive(x, y, imageBuffer.getElem(x + (y * size)));
        });
    }

    public IntegerMask(BufferedImage sourceImage, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings) {
        this(sourceImage, random, symmetrySettings, (String) null);
    }

    private void setPrimitive(int x, int y, int value) {
        this.mask[x][y] = value;
    }

    public int getPrimitive(Vector2 location) {
        return getPrimitive(StrictMath.round(location.x()), StrictMath.round(location.y()));
    }

    public int getPrimitive(int x, int y) {
        return this.mask[x][y];
    }

    private void setPrimitive(Vector2 location, int value) {
        setPrimitive(StrictMath.round(location.x()), StrictMath.round(location.y()), value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public IntegerMask scaleToPositive() {
        return (IntegerMask) enqueue(() -> {
            int min = getMin().intValue();
            if (min < 0) {
                subtract(Integer.valueOf(min));
            }
        });
    }

    @Override // com.faforever.neroxis.mask.ComparableMask
    public Integer getMin() {
        checkNotPipelined();
        return Integer.valueOf(Arrays.stream(this.mask).flatMapToInt(Arrays::stream).min().orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        }));
    }

    @Override // com.faforever.neroxis.mask.ComparableMask
    public Integer getMax() {
        checkNotPipelined();
        return Integer.valueOf(Arrays.stream(this.mask).flatMapToInt(Arrays::stream).max().orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        }));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public IntegerMask blur(int radius) {
        int[][] innerCount = getInnerCount();
        return (IntegerMask) apply((x, y) -> {
            setPrimitive(x, y, transformAverage(calculateAreaAverageAsInts(radius, x, y, innerCount)));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public IntegerMask blur(int radius, BooleanMask other) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
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
    public IntegerMask copyFrom(IntegerMask other) {
        return (IntegerMask) enqueue(dependencies -> {
            fill(((IntegerMask) dependencies.getFirst()).mask);
        }, other);
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected void initializeMask(int size) {
        enqueue(() -> {
            this.mask = new int[size][size];
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected int getImmediateSize() {
        return this.mask.length;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage writeToImage(BufferedImage image) {
        return writeToImage(image, 1.0f);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage toImage() {
        int size = getSize();
        BufferedImage image = new BufferedImage(size, size, 10);
        writeToImage(image, 255.0f / getMax().intValue());
        return image;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public String toHash() throws NoSuchAlgorithmException {
        ByteBuffer bytes = ByteBuffer.allocate(getSize() * getSize() * 4);
        loopInSymmetryRegion(SymmetryType.SPAWN, (x, y) -> {
            bytes.putInt(getPrimitive(x, y));
        });
        byte[] data = MessageDigest.getInstance("MD5").digest(bytes.array());
        return HexFormat.of().formatHex(data);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public Integer get(int x, int y) {
        return Integer.valueOf(getPrimitive(x, y));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public void set(int x, int y, Integer value) {
        setPrimitive(x, y, value.intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.Mask
    public IntegerMask fill(Integer value) {
        return (IntegerMask) enqueue(() -> {
            int length = this.mask.length;
            int[][] arr$ = this.mask;
            for (int[] ints : arr$) {
                Arrays.fill(ints, value.intValue());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public Integer getZeroValue() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public IntegerMask setSizeInternal(int newSize) {
        int oldSize = getSize();
        if (oldSize == 1) {
            int value = getPrimitive(0, 0);
            initializeMask(newSize);
            fill(Integer.valueOf(value));
        } else if (oldSize != newSize) {
            int[][] oldMask = this.mask;
            initializeMask(newSize);
            Map<Integer, Integer> coordinateMap = getSymmetricScalingCoordinateMap(oldSize, newSize);
            applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                int newX = ((Integer) coordinateMap.get(Integer.valueOf(x))).intValue();
                int newY = ((Integer) coordinateMap.get(Integer.valueOf(y))).intValue();
                int value2 = oldMask[newX][newY];
                applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                    setPrimitive(sx, sy, value2);
                });
            });
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public IntegerMask rotateInternal(float radians) {
        int[][] oldMask = this.mask;
        int size = getSize();
        initializeMask(size);
        applyWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
            Vector2 rotatedPoint = SymmetryUtil.getRotatedPoint(x, y, size, radians);
            if (!inBounds(rotatedPoint)) {
                return;
            }
            int newX = StrictMath.round(rotatedPoint.x());
            int newY = StrictMath.round(rotatedPoint.y());
            int value = oldMask[newX][newY];
            applyAtSymmetryPoints(x, y, SymmetryType.SPAWN, (sx, sy) -> {
                setPrimitive(sx, sy, value);
            });
        });
        return this;
    }

    private int transformAverage(float value) {
        return StrictMath.round(value);
    }

    private IntegerMask fill(int[][] maskToFillFrom) {
        checkNotPipelined();
        int maskSize = maskToFillFrom.length;
        this.mask = new int[maskSize][maskSize];
        for (int r = 0; r < maskSize; r++) {
            System.arraycopy(maskToFillFrom[r], 0, this.mask[r], 0, maskSize);
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private IntegerMask add(ToIntBiIntFunction valueFunction) {
        return (IntegerMask) apply((x, y) -> {
            addPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private IntegerMask subtract(ToIntBiIntFunction valueFunction) {
        return (IntegerMask) apply((x, y) -> {
            subtractPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private IntegerMask multiply(ToIntBiIntFunction valueFunction) {
        return (IntegerMask) apply((x, y) -> {
            multiplyPrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private IntegerMask divide(ToIntBiIntFunction valueFunction) {
        return (IntegerMask) apply((x, y) -> {
            dividePrimitiveAt(x, y, valueFunction.apply(x, y));
        });
    }

    private void addPrimitiveAt(int x, int y, int value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] + value;
    }

    private void subtractPrimitiveAt(int x, int y, int value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] - value;
    }

    private void multiplyPrimitiveAt(int x, int y, int value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] * value;
    }

    public BufferedImage writeToImage(BufferedImage image, float scaleFactor) {
        assertSize(image.getHeight());
        int size = getSize();
        DataBuffer imageBuffer = image.getRaster().getDataBuffer();
        loop((x, y) -> {
            imageBuffer.setElem(x + (y * size), (int) (getPrimitive(x, y) * scaleFactor));
        });
        return image;
    }

    private void dividePrimitiveAt(int x, int y, int value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] / value;
    }

    @Override // com.faforever.neroxis.mask.PrimitiveMask
    protected int[][] getInnerCount() {
        int[][] innerCount = new int[getSize()][getSize()];
        apply((x, y) -> {
            calculateInnerValue(innerCount, x, y, getPrimitive(x, y));
        });
        return innerCount;
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public Integer getSum() {
        return Integer.valueOf(Arrays.stream(this.mask).flatMapToInt(Arrays::stream).sum());
    }

    public Vector2 getRandomPosition() {
        checkNotPipelined();
        int size = getSize();
        int total = getSum().intValue();
        if (total == 0 || this.random == null) {
            return null;
        }
        int sum = this.random.nextInt(total);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int primitive = sum - getPrimitive(x, y);
                sum = primitive;
                if (primitive <= 0) {
                    return new Vector2(x, y);
                }
            }
        }
        throw new IllegalArgumentException("Did not find a coordinate");
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask add(IntegerMask other) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            apply((x, y) -> {
                int[] iArr = this.mask[x];
                iArr[y] = iArr[y] + source.mask[x][y];
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void addValueAt(int x, int y, Integer value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] + value.intValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask add(BooleanMask other, Integer value) {
        assertCompatibleMask(other);
        int val = value.intValue();
        return (IntegerMask) enqueue(dependencies -> {
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
    public IntegerMask add(BooleanMask other, IntegerMask values) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            IntegerMask val = (IntegerMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    addPrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask addWithOffset(IntegerMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            applyWithOffset(source, this::addPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void subtractValueAt(int x, int y, Integer value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] - value.intValue();
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public Integer getAvg() {
        checkNotPipelined();
        int size = getSize();
        return Integer.valueOf((getSum().intValue() / size) / size);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask subtract(IntegerMask other) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            apply((x, y) -> {
                int[] iArr = this.mask[x];
                iArr[y] = iArr[y] - source.mask[x][y];
            });
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask subtract(BooleanMask other, Integer values) {
        assertCompatibleMask(other);
        int val = values.intValue();
        return (IntegerMask) enqueue(dependencies -> {
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
    public IntegerMask subtract(BooleanMask other, IntegerMask values) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            IntegerMask val = (IntegerMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    subtractPrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, values);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask subtractWithOffset(IntegerMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            applyWithOffset(source, this::subtractPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask multiply(IntegerMask other) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            apply((x, y) -> {
                int[] iArr = this.mask[x];
                iArr[y] = iArr[y] * source.mask[x][y];
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void multiplyValueAt(int x, int y, Integer value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] * value.intValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask multiply(BooleanMask other, Integer value) {
        assertCompatibleMask(other);
        int val = value.intValue();
        return (IntegerMask) enqueue(dependencies -> {
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
    public IntegerMask multiply(BooleanMask other, IntegerMask value) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            IntegerMask val = (IntegerMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    multiplyPrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask multiplyWithOffset(IntegerMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            applyWithOffset(source, this::multiplyPrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask divide(IntegerMask other) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            apply((x, y) -> {
                int[] iArr = this.mask[x];
                iArr[y] = iArr[y] / source.mask[x][y];
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void divideValueAt(int x, int y, Integer value) {
        int[] iArr = this.mask[x];
        iArr[y] = iArr[y] / value.intValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask divide(BooleanMask other, Integer value) {
        assertCompatibleMask(other);
        int val = value.intValue();
        return (IntegerMask) enqueue(dependencies -> {
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
    public IntegerMask divide(BooleanMask other, IntegerMask value) {
        assertCompatibleMask(other);
        return (IntegerMask) enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            IntegerMask val = (IntegerMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    dividePrimitiveAt(x, y, val.getPrimitive(x, y));
                }
            });
        }, other, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public IntegerMask divideWithOffset(IntegerMask other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (IntegerMask) enqueue(dependencies -> {
            IntegerMask source = (IntegerMask) dependencies.getFirst();
            applyWithOffset(source, this::dividePrimitiveAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public IntegerMask addPrimitiveWithSymmetry(SymmetryType symmetryType, ToIntBiIntFunction valueFunction) {
        return (IntegerMask) applyWithSymmetry(symmetryType, (x, y) -> {
            int value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                addPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public IntegerMask subtractPrimitiveWithSymmetry(SymmetryType symmetryType, ToIntBiIntFunction valueFunction) {
        return (IntegerMask) applyWithSymmetry(symmetryType, (x, y) -> {
            int value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                subtractPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public IntegerMask multiplyPrimitiveWithSymmetry(SymmetryType symmetryType, ToIntBiIntFunction valueFunction) {
        return (IntegerMask) applyWithSymmetry(symmetryType, (x, y) -> {
            int value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                multiplyPrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public IntegerMask dividePrimitiveWithSymmetry(SymmetryType symmetryType, ToIntBiIntFunction valueFunction) {
        return (IntegerMask) applyWithSymmetry(symmetryType, (x, y) -> {
            int value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                dividePrimitiveAt(sx, sy, value);
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public IntegerMask applyWithOffset(IntegerMask other, TriIntConsumer action, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return (IntegerMask) enqueue(() -> {
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
                            int value = other.getPrimitive(x, y);
                            applyAtSymmetryPoints(shiftX, shiftY, SymmetryType.SPAWN, (sx, sy) -> {
                                action.accept(x, y, value);
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
