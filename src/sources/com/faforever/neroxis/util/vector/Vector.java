package com.faforever.neroxis.util.vector;

import com.faforever.neroxis.util.functional.FloatSupplier;
import com.faforever.neroxis.util.functional.FloatUnaryOperator;
import com.faforever.neroxis.util.vector.Vector;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/vector/Vector.class */
public interface Vector<T extends Vector<T>> {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int W = 3;
    public static final int R = 0;
    public static final int G = 1;
    public static final int B = 2;
    public static final int A = 3;

    int getDimension();

    float[] toArray();

    T transform(Transformer transformer);

    float get(int i);

    private default void assertEqualDimension(int dimension) {
        int thisDimension = getDimension();
        if (dimension != thisDimension) {
            throw new IllegalArgumentException(String.format("Dimensions do not match: This %d other %d", Integer.valueOf(thisDimension), Integer.valueOf(dimension)));
        }
    }

    default T withComponent(int i, float f) {
        return (T) transform(Transformer.matchingComponent(i, () -> {
            return f;
        }));
    }

    default T randomize(RandomGenerator randomGenerator, float f, float f2) {
        return (T) transform(Transformer.fromSupplier(() -> {
            return randomGenerator.nextFloat(f, f2);
        }));
    }

    default T randomize(RandomGenerator randomGenerator, float f) {
        return (T) transform(Transformer.fromSupplier(() -> {
            return randomGenerator.nextFloat(f);
        }));
    }

    default T max(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return StrictMath.max(f, oldValue);
        }));
    }

    default T max(float... fArr) {
        assertEqualDimension(fArr.length);
        return (T) transform((index, oldValue) -> {
            return StrictMath.max(fArr[index], oldValue);
        });
    }

    default T max(T t) {
        return (T) transform((index, oldValue) -> {
            return StrictMath.max(t.get(index), oldValue);
        });
    }

    default T clampMin(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return StrictMath.max(f, oldValue);
        }));
    }

    default T min(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return StrictMath.min(f, oldValue);
        }));
    }

    default T min(float... fArr) {
        assertEqualDimension(fArr.length);
        return (T) transform((index, oldValue) -> {
            return StrictMath.min(fArr[index], oldValue);
        });
    }

    default T min(T t) {
        return (T) transform((index, oldValue) -> {
            return StrictMath.min(t.get(index), oldValue);
        });
    }

    default T clampMax(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return StrictMath.min(f, oldValue);
        }));
    }

    default T round() {
        return (T) transform(Transformer.fromOldValue(StrictMath::round));
    }

    default T round(int i) {
        float fPow = (float) StrictMath.pow(10.0d, i);
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return StrictMath.round(oldValue * fPow) / fPow;
        }));
    }

    default T floor() {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return (float) StrictMath.floor(oldValue);
        }));
    }

    default T ceil() {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return (float) StrictMath.ceil(oldValue);
        }));
    }

    default T normalize() {
        return (T) divide(getMagnitude());
    }

    default T add(T t) {
        return (T) transform((index, oldValue) -> {
            return oldValue + t.get(index);
        });
    }

    default T add(float... fArr) {
        assertEqualDimension(fArr.length);
        return (T) transform((index, oldValue) -> {
            return oldValue + fArr[index];
        });
    }

    default T add(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return oldValue + f;
        }));
    }

    default T add(float f, int i) {
        return (T) transform(Transformer.matchingComponent(i, oldValue -> {
            return oldValue + f;
        }));
    }

    default T subtract(T t) {
        return (T) transform((index, oldValue) -> {
            return oldValue - t.get(index);
        });
    }

    default T subtract(float... fArr) {
        assertEqualDimension(fArr.length);
        return (T) transform((index, oldValue) -> {
            return oldValue - fArr[index];
        });
    }

    default T subtract(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return oldValue - f;
        }));
    }

    default T subtract(float f, int i) {
        return (T) transform(Transformer.matchingComponent(i, oldValue -> {
            return oldValue - f;
        }));
    }

    default T multiply(T t) {
        return (T) transform((index, oldValue) -> {
            return oldValue * t.get(index);
        });
    }

    default T multiply(float... fArr) {
        assertEqualDimension(fArr.length);
        return (T) transform((index, oldValue) -> {
            return oldValue * fArr[index];
        });
    }

    default T multiply(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return oldValue * f;
        }));
    }

    default T multiply(float f, int i) {
        return (T) transform(Transformer.matchingComponent(i, oldValue -> {
            return oldValue * f;
        }));
    }

    default T divide(T t) {
        return (T) transform((index, oldValue) -> {
            return oldValue / t.get(index);
        });
    }

    default T divide(float... fArr) {
        return (T) transform((index, oldValue) -> {
            return oldValue / fArr[index];
        });
    }

    default T divide(float f) {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return oldValue / f;
        }));
    }

    default T divide(float f, int i) {
        return (T) transform(Transformer.matchingComponent(i, oldValue -> {
            return oldValue / f;
        }));
    }

    default T roundToNearestHalfPoint() {
        return (T) transform(Transformer.fromOldValue(oldValue -> {
            return StrictMath.round(oldValue - 0.5f) + 0.5f;
        }));
    }

    default float getMagnitude() {
        float sum = 0.0f;
        int dimension = getDimension();
        for (int i = 0; i < dimension; i++) {
            sum += get(i) * get(i);
        }
        return (float) StrictMath.sqrt(sum);
    }

    default float getDistance(T other) {
        return (float) StrictMath.sqrt(getDistanceSquared(other));
    }

    default float getDistanceSquared(T other) {
        float sum = 0.0f;
        int dimension = getDimension();
        for (int i = 0; i < dimension; i++) {
            float diff = get(i) - other.get(i);
            sum += diff * diff;
        }
        return sum;
    }

    default float getAngle(T other) {
        return (float) StrictMath.acos((dot(other) / getMagnitude()) / other.getMagnitude());
    }

    default float dot(T other) {
        float sum = 0.0f;
        int dimension = getDimension();
        for (int i = 0; i < dimension; i++) {
            sum += get(i) * other.get(i);
        }
        return sum;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/vector/Vector$Transformer.class */
    public interface Transformer {
        float transform(int component, float currentValue);

        static Transformer fromOldValue(FloatUnaryOperator operator) {
            return (i, oldValue) -> {
                return operator.applyAsFloat(oldValue);
            };
        }

        static Transformer fromSupplier(FloatSupplier supplier) {
            return (i, f) -> {
                return supplier.getAsFloat();
            };
        }

        static Transformer matchingComponent(int component, FloatUnaryOperator operator) {
            return (index, oldValue) -> {
                return index == component ? operator.applyAsFloat(oldValue) : oldValue;
            };
        }

        static Transformer matchingComponent(int component, FloatSupplier supplier) {
            return (index, oldValue) -> {
                return index == component ? supplier.getAsFloat() : oldValue;
            };
        }
    }
}
