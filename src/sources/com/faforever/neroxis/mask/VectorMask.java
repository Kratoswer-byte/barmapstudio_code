package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.VectorMask;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.functional.BiIntFloatIntConsumer;
import com.faforever.neroxis.util.functional.ToFloatBiIntFunction;
import com.faforever.neroxis.util.vector.Vector;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/VectorMask.class */
public abstract class VectorMask<T extends Vector<T>, U extends VectorMask<T, U>> extends OperationsMask<T, U> {
    protected T[][] mask;
    static final /* synthetic */ boolean $assertionsDisabled;

    protected abstract T createValue(float scaleFactor, float... components);

    protected abstract T[][] getNullMask(int size);

    static {
        $assertionsDisabled = !VectorMask.class.desiredAssertionStatus();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public VectorMask(T[][] initialMask, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(initialMask.length, random, symmetrySettings, name);
        this.mask = initialMask;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    protected VectorMask(U u, String str) {
        super(u, str);
        this.mask = (T[][]) u.getNullMask(u.getSize());
    }

    protected void assertMatchingDimension(int numImageComponents) {
        int dimension = getZeroValue().getDimension();
        if (numImageComponents != dimension) {
            throw new IllegalArgumentException(String.format("Image does not have matching number of components: image %d this %d", Integer.valueOf(numImageComponents), Integer.valueOf(dimension)));
        }
    }

    protected void assertCompatibleComponents(Mask<?, ?>... components) {
        Arrays.stream(components).forEach(this::assertCompatibleMask);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public U blur(int radius) {
        Vector[][] innerCount = getInnerCount();
        return set((x, y) -> {
            return calculateAreaAverage(radius, x, y, innerCount).round().divide(1000.0f);
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    public U blur(int radius, BooleanMask other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask limiter = (BooleanMask) dependencies.getFirst();
            Vector[][] innerCount = getInnerCount();
            set((x, y) -> {
                return limiter.get(x, y).booleanValue() ? calculateAreaAverage(radius, x, y, innerCount).round().divide(1000.0f) : get(x, y);
            });
        }, other);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public U copyFrom(U other) {
        return enqueue(dependencies -> {
            fill((Vector[][]) ((VectorMask) dependencies.getFirst()).mask);
        }, other);
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected void initializeMask(int size) {
        enqueue(() -> {
            this.mask = (T[][]) getNullMask(size);
            fill((Vector) getZeroValue());
        });
    }

    @Override // com.faforever.neroxis.mask.Mask
    protected int getImmediateSize() {
        return this.mask.length;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public BufferedImage writeToImage(BufferedImage image) {
        int numImageComponents = image.getColorModel().getNumComponents();
        assertMatchingDimension(numImageComponents);
        WritableRaster imageRaster = image.getRaster();
        loop((x, y) -> {
            imageRaster.setPixel(x, y, get(x, y).toArray());
        });
        return image;
    }

    @Override // com.faforever.neroxis.mask.Mask
    public String toHash() throws NoSuchAlgorithmException {
        int size = getSize();
        int dimension = get(0, 0).getDimension();
        ByteBuffer bytes = ByteBuffer.allocate(size * size * 4 * dimension);
        loopInSymmetryRegion(SymmetryType.SPAWN, (x, y) -> {
            Vector<?> value = get(x, y);
            for (int i = 0; i < dimension; i++) {
                bytes.putFloat(value.get(i));
            }
        });
        byte[] data = MessageDigest.getInstance("MD5").digest(bytes.array());
        return HexFormat.of().formatHex(data);
    }

    @Override // com.faforever.neroxis.mask.Mask
    public T get(int x, int y) {
        return this.mask[x][y];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public void set(int x, int y, T value) {
        this.mask[x][y] = value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public U fill(T value) {
        return set((x, y) -> {
            return value;
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public U setSizeInternal(int i) {
        int size = getSize();
        if (size == 1) {
            Vector vector = get(0, 0);
            this.mask = (T[][]) getNullMask(i);
            fill(vector);
        } else if (size != i) {
            T[][] tArr = this.mask;
            this.mask = (T[][]) getNullMask(i);
            Map<Integer, Integer> symmetricScalingCoordinateMap = getSymmetricScalingCoordinateMap(size, i);
            setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                int newX = ((Integer) symmetricScalingCoordinateMap.get(Integer.valueOf(x))).intValue();
                int newY = ((Integer) symmetricScalingCoordinateMap.get(Integer.valueOf(y))).intValue();
                return tArr[newX][newY];
            });
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.faforever.neroxis.mask.Mask
    public U rotateInternal(float radians) {
        T[][] oldMask = this.mask;
        int size = getSize();
        initializeMask(size);
        setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
            Vector2 rotatedPoint = SymmetryUtil.getRotatedPoint(x, y, size, radians);
            if (!inBounds(rotatedPoint)) {
                return getZeroValue();
            }
            int newX = StrictMath.round(rotatedPoint.x());
            int newY = StrictMath.round(rotatedPoint.y());
            return oldMask[newX][newY];
        });
        return this;
    }

    protected T[][] getInnerCount() {
        T[][] tArr = (T[][]) getNullMask(getSize());
        apply((x, y) -> {
            calculateInnerValue(tArr, x, y, get(x, y));
        });
        return tArr;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void calculateInnerValue(T[][] innerCount, int x, int y, T val) {
        innerCount[x][y] = val.multiply(1000.0f).round();
        if (x > 0) {
            innerCount[x][y] = innerCount[x][y].add(innerCount[x - 1][y]);
        }
        if (y > 0) {
            innerCount[x][y] = innerCount[x][y].add(innerCount[x][y - 1]);
        }
        if (x > 0 && y > 0) {
            innerCount[x][y] = innerCount[x][y].subtract(innerCount[x - 1][y - 1]);
        }
    }

    protected T calculateAreaAverage(int i, int i2, int i3, T[][] tArr) {
        T zeroValue = getZeroValue();
        int iMax = StrictMath.max(0, i2 - i);
        int size = getSize();
        int iMin = StrictMath.min(size - 1, i2 + i);
        int iMax2 = StrictMath.max(0, i3 - i);
        int iMin2 = StrictMath.min(size - 1, i3 + i);
        return (T) zeroValue.add(tArr[iMin][iMin2]).add((iMax <= 0 || iMax2 <= 0) ? getZeroValue() : tArr[iMax - 1][iMax2 - 1]).subtract(iMax2 > 0 ? tArr[iMin][iMax2 - 1] : getZeroValue()).subtract(iMax > 0 ? tArr[iMax - 1][iMin2] : getZeroValue()).divide(((iMin - iMax) + 1) * ((iMin2 - iMax2) + 1));
    }

    protected U fill(T[][] tArr) {
        int length = tArr.length;
        this.mask = (T[][]) getNullMask(length);
        for (int i = 0; i < length; i++) {
            for (int i2 = 0; i2 < length; i2++) {
                set(i, i2, (Vector) tArr[i][i2]);
            }
        }
        return this;
    }

    public float getMaxMagnitude() {
        return ((Float) Arrays.stream(this.mask).flatMap((v0) -> {
            return Arrays.stream(v0);
        }).map((v0) -> {
            return v0.getMagnitude();
        }).max(Comparator.comparing(magnitude -> {
            return magnitude;
        })).orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        })).floatValue();
    }

    public T getMaxComponents() {
        return (T) Arrays.stream(this.mask).flatMap((v0) -> {
            return Arrays.stream(v0);
        }).reduce((v0, v1) -> {
            return v0.max(v1);
        }).orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        });
    }

    public T getMinComponents() {
        return (T) Arrays.stream(this.mask).flatMap((v0) -> {
            return Arrays.stream(v0);
        }).reduce((v0, v1) -> {
            return v0.min(v1);
        }).orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        });
    }

    protected void setComponentAt(Vector2 loc, float value, int component) {
        setComponentAt((int) loc.x(), (int) loc.y(), value, component);
    }

    protected U addScalar(ToFloatBiIntFunction valueFunction) {
        return apply((x, y) -> {
            addScalarAt(x, y, valueFunction.apply(x, y));
        });
    }

    protected U subtractScalar(ToFloatBiIntFunction valueFunction) {
        return apply((x, y) -> {
            subtractScalarAt(x, y, valueFunction.apply(x, y));
        });
    }

    protected U multiplyScalar(ToFloatBiIntFunction valueFunction) {
        return enqueue(() -> {
            apply((x, y) -> {
                multiplyScalarAt(x, y, valueFunction.apply(x, y));
            });
        });
    }

    protected U divideScalar(ToFloatBiIntFunction valueFunction) {
        return apply((x, y) -> {
            divideScalarAt(x, y, valueFunction.apply(x, y));
        });
    }

    protected void addScalarAt(Vector2 loc, float value) {
        addScalarAt((int) loc.x(), (int) loc.y(), value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void addScalarAt(int i, int i2, float f) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].add(f);
    }

    protected void subtractScalarAt(Vector2 loc, float value) {
        subtractScalarAt((int) loc.x(), (int) loc.y(), value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void subtractScalarAt(int i, int i2, float f) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].subtract(f);
    }

    public U blurComponent(int radius, int component, BooleanMask other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask limiter = (BooleanMask) dependencies.getFirst();
            int[][] innerCount = getComponentInnerCount(component);
            setComponent((x, y) -> {
                return limiter.get(x, y).booleanValue() ? calculateComponentAreaAverage(radius, x, y, innerCount) / 1000.0f : get(x, y).get(component);
            }, component);
        }, other);
    }

    protected void multiplyScalarAt(Vector2 loc, float value) {
        multiplyScalarAt((int) loc.x(), (int) loc.y(), value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void multiplyScalarAt(int i, int i2, float f) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].multiply(f);
    }

    protected void divideScalarAt(Vector2 loc, float value) {
        divideScalarAt((int) loc.x(), (int) loc.y(), value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void divideScalarAt(int i, int i2, float f) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].divide(f);
    }

    protected int[][] getComponentInnerCount(int component) {
        int[][] innerCount = new int[getSize()][getSize()];
        apply((x, y) -> {
            calculateComponentInnerValue(innerCount, x, y, StrictMath.round(get(x, y).get(component) * 1000.0f));
        });
        return innerCount;
    }

    protected void calculateComponentInnerValue(int[][] innerCount, int x, int y, int val) {
        calculateScalarInnerValue(innerCount, x, y, val);
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public T getSum() {
        return (T) Arrays.stream(this.mask).flatMap((v0) -> {
            return Arrays.stream(v0);
        }).reduce((v0, v1) -> {
            return v0.add(v1);
        }).orElseThrow(() -> {
            return new IllegalStateException("Empty Mask");
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void addValueAt(int i, int i2, T t) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].add(t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void subtractValueAt(int i, int i2, T t) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].subtract(t);
    }

    @Override // com.faforever.neroxis.mask.OperationsMask
    public T getAvg() {
        checkNotPipelined();
        return (T) getSum().divide(getSize());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void multiplyValueAt(int i, int i2, T t) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].multiply(t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.mask.OperationsMask
    public void divideValueAt(int i, int i2, T t) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].divide(t);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void setComponentAt(int i, int i2, float f, int i3) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].withComponent(i3, f);
    }

    protected void addComponentAt(Vector2 loc, float value, int component) {
        addComponentAt((int) loc.x(), (int) loc.y(), value, component);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void addComponentAt(int i, int i2, float f, int i3) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].add(f, i3);
    }

    protected void subtractComponentAt(Vector2 loc, float value, int component) {
        subtractComponentAt((int) loc.x(), (int) loc.y(), value, component);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void subtractComponentAt(int i, int i2, float f, int i3) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].subtract(f, i3);
    }

    protected void multiplyComponentAt(Vector2 loc, float value, int component) {
        multiplyComponentAt((int) loc.x(), (int) loc.y(), value, component);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void multiplyComponentAt(int i, int i2, float f, int i3) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].multiply(f, i3);
    }

    protected void divideComponentAt(Vector2 loc, float value, int component) {
        divideComponentAt((int) loc.x(), (int) loc.y(), value, component);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void divideComponentAt(int i, int i2, float f, int i3) {
        ((T[][]) this.mask)[i][i2] = this.mask[i][i2].divide(f, i3);
    }

    public U addScalar(float f) {
        return (U) addScalar((x, y) -> {
            return f;
        });
    }

    protected U setComponent(ToFloatBiIntFunction valueFunction, int component) {
        return apply((x, y) -> {
            setComponentAt(x, y, valueFunction.apply(x, y), component);
        });
    }

    public U subtractScalar(float f) {
        return (U) subtractScalar((x, y) -> {
            return f;
        });
    }

    protected float calculateComponentAreaAverage(int radius, int x, int y, int[][] innerCount) {
        int size = getSize();
        int xLeft = StrictMath.max(0, x - radius);
        int xRight = StrictMath.min(size - 1, x + radius);
        int yUp = StrictMath.max(0, y - radius);
        int yDown = StrictMath.min(size - 1, y + radius);
        int countA = (xLeft <= 0 || yUp <= 0) ? 0 : innerCount[xLeft - 1][yUp - 1];
        int countB = yUp > 0 ? innerCount[xRight][yUp - 1] : 0;
        int countC = xLeft > 0 ? innerCount[xLeft - 1][yDown] : 0;
        int countD = innerCount[xRight][yDown];
        int count = ((countD + countA) - countB) - countC;
        int area = ((xRight - xLeft) + 1) * ((yDown - yUp) + 1);
        return count / area;
    }

    public U multiplyScalar(float f) {
        return (U) multiplyScalar((x, y) -> {
            return f;
        });
    }

    protected U addComponent(ToFloatBiIntFunction valueFunction, int component) {
        return apply((x, y) -> {
            addComponentAt(x, y, valueFunction.apply(x, y), component);
        });
    }

    public U divideScalar(float f) {
        return (U) divideScalar((x, y) -> {
            return f;
        });
    }

    protected U subtractComponent(ToFloatBiIntFunction valueFunction, int component) {
        return enqueue(() -> {
            apply((x, y) -> {
                subtractComponentAt(x, y, valueFunction.apply(x, y), component);
            });
        });
    }

    public U clampComponentMin(float floor) {
        return set((x, y) -> {
            return get(x, y).clampMin(floor);
        });
    }

    public U clampComponentMax(float ceiling) {
        return set((x, y) -> {
            return get(x, y).clampMax(ceiling);
        });
    }

    public U randomize(float scale) {
        if ($assertionsDisabled || this.random != null) {
            return setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                return getZeroValue().randomize(this.random, scale);
            });
        }
        throw new AssertionError();
    }

    public U randomize(float minValue, float maxValue) {
        if ($assertionsDisabled || this.random != null) {
            return setWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                return getZeroValue().randomize(this.random, minValue, maxValue);
            });
        }
        throw new AssertionError();
    }

    public U normalize() {
        return enqueue(dependencies -> {
            set((x, y) -> {
                return get(x, y).normalize();
            });
        }, new Mask[0]);
    }

    public FloatMask copyAsDotProduct(U other) {
        return copyAsDotProduct(other, getName() + "dot" + other.getName());
    }

    public FloatMask copyAsDotProduct(U other, String name) {
        assertCompatibleMask(other);
        return new FloatMask(this, other, name);
    }

    public FloatMask copyAsDotProduct(T vector) {
        return copyAsDotProduct(vector, getName() + "Dot");
    }

    public FloatMask copyAsDotProduct(T vector, String name) {
        assertMatchingDimension(vector.getDimension());
        return new FloatMask(this, vector, name);
    }

    public U blurComponent(int i, int i2) {
        int[][] componentInnerCount = getComponentInnerCount(i2);
        return (U) setComponent((x, y) -> {
            return calculateComponentAreaAverage(i, x, y, componentInnerCount) / 1000.0f;
        }, i2);
    }

    public U multiplyComponent(ToFloatBiIntFunction valueFunction, int component) {
        return apply((x, y) -> {
            multiplyComponentAt(x, y, valueFunction.apply(x, y), component);
        });
    }

    public U divideComponent(ToFloatBiIntFunction valueFunction, int component) {
        return apply((x, y) -> {
            divideComponentAt(x, y, valueFunction.apply(x, y), component);
        });
    }

    public U addScalarWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                addScalarAt(sx, sy, value);
            });
        });
    }

    public U subtractScalarWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                subtractScalarAt(sx, sy, value);
            });
        });
    }

    public U multiplyScalarWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                multiplyScalarAt(sx, sy, value);
            });
        });
    }

    public U divideScalarWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                divideScalarAt(sx, sy, value);
            });
        });
    }

    public U addComponent(float f, int i) {
        return (U) addComponent((x, y) -> {
            return f;
        }, i);
    }

    public U setComponent(FloatMask other, int component) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            Objects.requireNonNull(source);
            setComponent(source::getPrimitive, component);
        }, other);
    }

    public U setComponentWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction, int component) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                setComponentAt(sx, sy, value, component);
            });
        });
    }

    public U addComponentWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction, int component) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                addComponentAt(sx, sy, value, component);
            });
        });
    }

    public U addComponent(BooleanMask other, float value, int component) {
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            addComponent((x, y) -> {
                return source.get(x, y).booleanValue() ? value : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            }, component);
        }, other);
    }

    public U addComponent(FloatMask other, int component) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            Objects.requireNonNull(source);
            addComponent(source::get, component);
        }, other);
    }

    public U subtractComponent(float f, int i) {
        return (U) subtractComponent((x, y) -> {
            return f;
        }, i);
    }

    public U subtractComponentWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction, int component) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                subtractComponentAt(sx, sy, value, component);
            });
        });
    }

    public U multiplyComponentWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction, int component) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                multiplyComponentAt(sx, sy, value, component);
            });
        });
    }

    public U subtractComponent(BooleanMask other, float value, int component) {
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            subtractComponent((x, y) -> {
                return source.get(x, y).booleanValue() ? value : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            }, component);
        }, other);
    }

    public U subtractComponent(FloatMask other, int component) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            Objects.requireNonNull(source);
            subtractComponent(source::get, component);
        }, other);
    }

    public U multiplyComponent(float f, int i) {
        return (U) multiplyComponent((x, y) -> {
            return f;
        }, i);
    }

    protected U divideComponentWithSymmetry(SymmetryType symmetryType, ToFloatBiIntFunction valueFunction, int component) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            float value = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                divideComponentAt(sx, sy, value, component);
            });
        });
    }

    public U multiplyComponent(BooleanMask other, float value, int component) {
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            multiplyComponent((x, y) -> {
                return source.get(x, y).booleanValue() ? value : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            }, component);
        }, other);
    }

    public U multiplyComponent(FloatMask other, int component) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            Objects.requireNonNull(source);
            multiplyComponent(source::get, component);
        }, other);
    }

    public U divideComponent(float f, int i) {
        return (U) divideComponent((x, y) -> {
            return f;
        }, i);
    }

    public U divideComponent(BooleanMask other, float value, int component) {
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            divideComponent((x, y) -> {
                return source.get(x, y).booleanValue() ? value : SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
            }, component);
        }, other);
    }

    public U divideComponent(FloatMask other, int component) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            Objects.requireNonNull(source);
            divideComponent(source::get, component);
        }, other);
    }

    public FloatMask copyAsComponentMask(int component) {
        return copyAsComponentMask(component, getName() + "Component" + component);
    }

    public FloatMask copyAsComponentMask(int component, String name) {
        return new FloatMask(this, component, name);
    }

    public FloatMask[] splitComponentMasks() {
        int dimension = getZeroValue().getDimension();
        String name = getName();
        FloatMask[] components = new FloatMask[dimension];
        for (int i = 0; i < dimension; i++) {
            components[i] = new FloatMask(getSize(), getNextRandomGenerator(), this.symmetrySettings, name + "Component" + i);
        }
        enqueue(dependencies -> {
            Mask<?, ?>[] sources = (Mask[]) dependencies.subList(0, dimension).toArray(x$0 -> {
                return new Mask[x$0];
            });
            apply((x, y) -> {
                for (int i2 = 0; i2 < dimension; i2++) {
                    ((FloatMask) sources[i2]).setPrimitive(x, y, get(x, y).get(i2));
                }
            });
        }, components);
        return components;
    }

    public U setComponentWithOffset(FloatMask other, int component, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyComponentWithOffset(source, this::setComponentAt, component, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U addComponentWithOffset(FloatMask other, int component, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyComponentWithOffset(source, this::addComponentAt, component, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U subtractComponentWithOffset(FloatMask other, int component, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyComponentWithOffset(source, this::subtractComponentAt, component, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U multiplyComponentWithOffset(FloatMask other, int component, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyComponentWithOffset(source, this::multiplyComponentAt, component, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U divideComponentWithOffset(FloatMask other, int component, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            FloatMask source = (FloatMask) dependencies.getFirst();
            applyComponentWithOffset(source, this::divideComponentAt, component, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    private U applyComponentWithOffset(FloatMask other, BiIntFloatIntConsumer action, int component, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(() -> {
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
                                action.accept(sx, sy, value, component);
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
                            action.accept(shiftX, shiftY, other.getPrimitive(x2, y2), component);
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
                    action.accept(x2, y2, other.getPrimitive(shiftX, shiftY), component);
                }
            });
        });
    }
}
