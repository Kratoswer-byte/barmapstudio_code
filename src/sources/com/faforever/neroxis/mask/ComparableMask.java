package com.faforever.neroxis.mask;

import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.mask.ComparableMask;
import java.lang.Comparable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/ComparableMask.class */
public abstract class ComparableMask<T extends Comparable<T>, U extends ComparableMask<T, U>> extends OperationsMask<T, U> {
    private final AtomicInteger toBooleanCounter;

    public abstract T getMin();

    public abstract T getMax();

    protected ComparableMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(size, random, symmetrySettings, name);
        this.toBooleanCounter = new AtomicInteger();
    }

    protected ComparableMask(U other, String name) {
        super(other, name);
        this.toBooleanCounter = new AtomicInteger();
    }

    protected boolean valueAtEqualTo(int x, int y, T value) {
        return get(x, y).compareTo(value) == 0;
    }

    protected boolean valueAtLessThanEqualTo(int x, int y, T value) {
        return get(x, y).compareTo(value) <= 0;
    }

    protected boolean isLocalMax(int x, int y) {
        T value = get(x, y);
        return x > 0 && valueAtLessThanEqualTo(x - 1, y, value) && x < getSize() - 1 && valueAtLessThanEqualTo(x + 1, y, value) && y > 0 && valueAtLessThanEqualTo(x, y - 1, value) && y < getSize() - 1 && valueAtLessThanEqualTo(x, y + 1, value) && valueAtLessThanEqualTo(x - 1, y - 1, value) && valueAtLessThanEqualTo(x + 1, y - 1, value) && valueAtLessThanEqualTo(x - 1, y + 1, value) && valueAtLessThanEqualTo(x + 1, y + 1, value);
    }

    protected boolean isLocal1DMax(int x, int y) {
        T value = get(x, y);
        return (x > 0 && valueAtLessThanEqualTo(x - 1, y, value) && x < getSize() - 1 && valueAtLessThanEqualTo(x + 1, y, value)) || (y > 0 && valueAtLessThanEqualTo(x, y - 1, value) && y < getSize() - 1 && valueAtLessThanEqualTo(x, y + 1, value));
    }

    protected boolean isLocalMin(int x, int y) {
        T value = get(x, y);
        return x > 0 && valueAtGreaterThanEqualTo(x - 1, y, value) && x < getSize() - 1 && valueAtGreaterThanEqualTo(x + 1, y, value) && y > 0 && valueAtGreaterThanEqualTo(x, y - 1, value) && y < getSize() - 1 && valueAtGreaterThanEqualTo(x, y + 1, value) && valueAtGreaterThanEqualTo(x - 1, y - 1, value) && valueAtGreaterThanEqualTo(x + 1, y - 1, value) && valueAtGreaterThanEqualTo(x - 1, y + 1, value) && valueAtGreaterThanEqualTo(x + 1, y + 1, value);
    }

    protected boolean isLocal1DMin(int x, int y) {
        T value = get(x, y);
        return (x > 0 && valueAtGreaterThanEqualTo(x - 1, y, value) && x < getSize() - 1 && valueAtGreaterThanEqualTo(x + 1, y, value)) || (y > 0 && valueAtGreaterThanEqualTo(x, y - 1, value) && y < getSize() - 1 && valueAtGreaterThanEqualTo(x, y + 1, value));
    }

    public U max(U other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            set((x, y) -> {
                T thisVal = get(x, y);
                Comparable comparable = (Comparable) comparableMask.get(x, y);
                return thisVal.compareTo(comparable) > 0 ? thisVal : comparable;
            });
        }, other);
    }

    public U clampMax(BooleanMask other, T val) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            set((x, y) -> {
                T thisVal = get(x, y);
                if (source.getPrimitive(x, y) && thisVal.compareTo(val) < 0) {
                    return val;
                }
                return thisVal;
            });
        }, other);
    }

    public U clampMax(T val) {
        return set((x, y) -> {
            T thisVal = get(x, y);
            return thisVal.compareTo(val) < 0 ? thisVal : val;
        });
    }

    public U min(U other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            ComparableMask comparableMask = (ComparableMask) dependencies.getFirst();
            set((x, y) -> {
                T thisVal = get(x, y);
                Comparable comparable = (Comparable) comparableMask.get(x, y);
                return thisVal.compareTo(comparable) < 0 ? thisVal : comparable;
            });
        }, other);
    }

    public U clampMin(BooleanMask other, T val) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            set((x, y) -> {
                T thisVal = get(x, y);
                if (source.getPrimitive(x, y) && thisVal.compareTo(val) > 0) {
                    return val;
                }
                return thisVal;
            });
        }, other);
    }

    public U clampMin(T val) {
        return set((x, y) -> {
            T thisVal = get(x, y);
            return thisVal.compareTo(val) > 0 ? thisVal : val;
        });
    }

    public U threshold(T val) {
        return set((x, y) -> {
            T thisVal = get(x, y);
            return thisVal.compareTo(val) > 0 ? getZeroValue() : thisVal;
        });
    }

    public U zeroOutsideRange(T min, T max) {
        return set((x, y) -> {
            return (valueAtLessThan(x, y, min) || valueAtGreaterThan(x, y, max)) ? getZeroValue() : get(x, y);
        });
    }

    protected boolean valueAtLessThan(int x, int y, T value) {
        return get(x, y).compareTo(value) < 0;
    }

    protected boolean valueAtGreaterThan(int x, int y, T value) {
        return get(x, y).compareTo(value) > 0;
    }

    public U zeroInRange(T min, T max) {
        return set((x, y) -> {
            return (valueAtGreaterThanEqualTo(x, y, min) && valueAtLessThan(x, y, max)) ? getZeroValue() : get(x, y);
        });
    }

    protected boolean valueAtGreaterThanEqualTo(int x, int y, T value) {
        return get(x, y).compareTo(value) >= 0;
    }

    public BooleanMask copyAsBooleanMask(T minValue) {
        return copyAsBooleanMask((Comparable) minValue, getName() + "toBoolean" + this.toBooleanCounter.getAndIncrement());
    }

    private BooleanMask copyAsBooleanMask(T minValue, String name) {
        return new BooleanMask(this, minValue, name);
    }

    public BooleanMask copyAsLocalMaximums(T minValue, T maxValue) {
        BooleanMask localMaxima = new BooleanMask(getSize(), getNextRandomGenerator(), this.symmetrySettings, getName() + "Maximas");
        return localMaxima.initMaxima(this, minValue, maxValue);
    }

    public BooleanMask copyAsLocal1DMaximums(T minValue, T maxValue) {
        return copyAsLocal1DMaximums(minValue, maxValue, getName() + "1DMaximas");
    }

    public BooleanMask copyAsLocal1DMaximums(T minValue, T maxValue, String name) {
        BooleanMask localMaxima = new BooleanMask(getSize(), getNextRandomGenerator(), this.symmetrySettings, name);
        return localMaxima.init1DMaxima(this, minValue, maxValue);
    }

    public BooleanMask copyAsLocalMinimums(T minValue, T maxValue) {
        BooleanMask localMaxima = new BooleanMask(getSize(), getNextRandomGenerator(), this.symmetrySettings, getName() + "Minimas");
        return localMaxima.initMaxima(this, minValue, maxValue);
    }

    public BooleanMask copyAsLocal1DMinimums(T minValue, T maxValue) {
        return copyAsLocal1DMaximums(minValue, maxValue, getName() + "1DMinimas");
    }

    public BooleanMask copyAsLocal1DMinimums(T minValue, T maxValue, String name) {
        BooleanMask localMaxima = new BooleanMask(getSize(), getNextRandomGenerator(), this.symmetrySettings, name);
        return localMaxima.init1DMaxima(this, minValue, maxValue);
    }

    public FloatMask copyAsDistanceFieldForRange(T minValue, T maxValue) {
        return copyAsDistanceFieldForRange(minValue, maxValue, getName() + "DistanceField");
    }

    public FloatMask copyAsDistanceFieldForRange(T minValue, T maxValue, String name) {
        return copyAsBooleanMask(minValue, maxValue).copyAsDistanceField();
    }

    public BooleanMask copyAsBooleanMask(T minValue, T maxValue) {
        return copyAsBooleanMask(minValue, maxValue, getName() + "toBoolean");
    }

    public BooleanMask copyAsBooleanMask(T minValue, T maxValue, String name) {
        return new BooleanMask(this, minValue, maxValue, name);
    }
}
