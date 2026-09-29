package com.faforever.neroxis.mask;

import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.OperationsMask;
import com.faforever.neroxis.util.functional.BiIntFunction;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.Objects;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/OperationsMask.class */
public abstract class OperationsMask<T, U extends OperationsMask<T, U>> extends Mask<T, U> {
    public abstract T getSum();

    protected abstract void addValueAt(int x, int y, T value);

    protected abstract void subtractValueAt(int x, int y, T value);

    public abstract T getAvg();

    protected abstract void multiplyValueAt(int x, int y, T value);

    protected abstract void divideValueAt(int x, int y, T value);

    protected OperationsMask(int size, RandomGenerator.SplittableGenerator random, SymmetrySettings symmetrySettings, String name) {
        super(size, random, symmetrySettings, name);
    }

    protected OperationsMask(U other, String name) {
        super(other, name);
    }

    public U add(U other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            OperationsMask operationsMask = (OperationsMask) dependencies.getFirst();
            Objects.requireNonNull(operationsMask);
            add((BiIntFunction) operationsMask::get);
        }, other);
    }

    protected U add(BiIntFunction<T> valueFunction) {
        return apply((x, y) -> {
            addValueAt(x, y, valueFunction.apply(x, y));
        });
    }

    public U add(BooleanMask other, T value) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    addValueAt(x, y, value);
                }
            });
        }, other);
    }

    public U add(BooleanMask other, U values) {
        assertCompatibleMask(other);
        assertCompatibleMask(values);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            OperationsMask operationsMask = (OperationsMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    addValueAt(x, y, operationsMask.get(x, y));
                }
            });
        }, other, values);
    }

    public U add(T t) {
        return (U) add((BiIntFunction) (x, y) -> {
            return t;
        });
    }

    public U addWithOffset(U u, Vector2 vector2, boolean z, boolean z2) {
        return (U) addWithOffset(u, (int) vector2.x(), (int) vector2.y(), z, z2);
    }

    public U addWithOffset(U other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            applyWithOffset((OperationsMask) dependencies.getFirst(), this::addValueAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U subtractAvg() {
        return enqueue(() -> {
            subtract(getAvg());
        });
    }

    public U subtract(T t) {
        return (U) subtract((BiIntFunction) (x, y) -> {
            return t;
        });
    }

    protected U subtract(BiIntFunction<T> valueFunction) {
        return apply((x, y) -> {
            subtractValueAt(x, y, valueFunction.apply(x, y));
        });
    }

    public U subtract(U other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            OperationsMask operationsMask = (OperationsMask) dependencies.getFirst();
            Objects.requireNonNull(operationsMask);
            subtract((BiIntFunction) operationsMask::get);
        }, other);
    }

    public U subtract(BooleanMask other, T value) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    subtractValueAt(x, y, value);
                }
            });
        }, other);
    }

    public U subtract(BooleanMask other, U values) {
        assertCompatibleMask(other);
        assertCompatibleMask(values);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            OperationsMask operationsMask = (OperationsMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    subtractValueAt(x, y, operationsMask.get(x, y));
                }
            });
        }, other, values);
    }

    public U subtractWithOffset(U u, Vector2 vector2, boolean z, boolean z2) {
        return (U) subtractWithOffset(u, (int) vector2.x(), (int) vector2.y(), z, z2);
    }

    public U subtractWithOffset(U other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            applyWithOffset((OperationsMask) dependencies.getFirst(), this::subtractValueAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U multiply(U other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            OperationsMask operationsMask = (OperationsMask) dependencies.getFirst();
            Objects.requireNonNull(operationsMask);
            multiply((BiIntFunction) operationsMask::get);
        }, other);
    }

    protected U multiply(BiIntFunction<T> valueFunction) {
        return apply((x, y) -> {
            multiplyValueAt(x, y, valueFunction.apply(x, y));
        });
    }

    public U multiply(T t) {
        return (U) multiply((BiIntFunction) (x, y) -> {
            return t;
        });
    }

    public U multiply(BooleanMask other, T value) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    multiplyValueAt(x, y, value);
                }
            });
        }, other);
    }

    public U multiply(BooleanMask other, U values) {
        assertCompatibleMask(other);
        assertCompatibleMask(values);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            OperationsMask operationsMask = (OperationsMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    multiplyValueAt(x, y, operationsMask.get(x, y));
                }
            });
        }, other, values);
    }

    public U multiplyWithOffset(U u, Vector2 vector2, boolean z, boolean z2) {
        return (U) multiplyWithOffset(u, (int) vector2.x(), (int) vector2.y(), z, z2);
    }

    public U multiplyWithOffset(U other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            applyWithOffset((OperationsMask) dependencies.getFirst(), this::multiplyValueAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U divide(U other) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            OperationsMask operationsMask = (OperationsMask) dependencies.getFirst();
            Objects.requireNonNull(operationsMask);
            divide((BiIntFunction) operationsMask::get);
        }, other);
    }

    protected U divide(BiIntFunction<T> valueFunction) {
        return apply((x, y) -> {
            divideValueAt(x, y, valueFunction.apply(x, y));
        });
    }

    public U divide(T t) {
        return (U) divide((BiIntFunction) (x, y) -> {
            return t;
        });
    }

    public U divide(BooleanMask other, T value) {
        assertCompatibleMask(other);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.getFirst();
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    divideValueAt(x, y, value);
                }
            });
        }, other);
    }

    public U divide(BooleanMask other, U values) {
        assertCompatibleMask(other);
        assertCompatibleMask(values);
        return enqueue(dependencies -> {
            BooleanMask source = (BooleanMask) dependencies.get(0);
            OperationsMask operationsMask = (OperationsMask) dependencies.get(1);
            apply((x, y) -> {
                if (source.getPrimitive(x, y)) {
                    divideValueAt(x, y, operationsMask.get(x, y));
                }
            });
        }, other, values);
    }

    public U divideWithOffset(U u, Vector2 vector2, boolean z, boolean z2) {
        return (U) divideWithOffset(u, (int) vector2.x(), (int) vector2.y(), z, z2);
    }

    public U divideWithOffset(U other, int xOffset, int yOffset, boolean center, boolean wrapEdges) {
        return enqueue(dependencies -> {
            applyWithOffset((OperationsMask) dependencies.getFirst(), this::divideValueAt, xOffset, yOffset, center, wrapEdges);
        }, other);
    }

    public U addWithSymmetry(SymmetryType symmetryType, BiIntFunction<T> valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            Object objApply = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                addValueAt(sx, sy, objApply);
            });
        });
    }

    public U subtractWithSymmetry(SymmetryType symmetryType, BiIntFunction<T> valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            Object objApply = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                subtractValueAt(sx, sy, objApply);
            });
        });
    }

    public U multiplyWithSymmetry(SymmetryType symmetryType, BiIntFunction<T> valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            Object objApply = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                multiplyValueAt(sx, sy, objApply);
            });
        });
    }

    public U divideWithSymmetry(SymmetryType symmetryType, BiIntFunction<T> valueFunction) {
        return applyWithSymmetry(symmetryType, (x, y) -> {
            Object objApply = valueFunction.apply(x, y);
            applyAtSymmetryPoints(x, y, symmetryType, (sx, sy) -> {
                divideValueAt(sx, sy, objApply);
            });
        });
    }

    protected void calculateScalarInnerValue(int[][] innerCount, int x, int y, int val) {
        innerCount[x][y] = val;
        int[] iArr = innerCount[x];
        iArr[y] = iArr[y] + (x > 0 ? innerCount[x - 1][y] : 0);
        int[] iArr2 = innerCount[x];
        iArr2[y] = iArr2[y] + (y > 0 ? innerCount[x][y - 1] : 0);
        int[] iArr3 = innerCount[x];
        iArr3[y] = iArr3[y] - ((x <= 0 || y <= 0) ? 0 : innerCount[x - 1][y - 1]);
    }
}
