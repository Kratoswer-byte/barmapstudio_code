package com.faforever.neroxis.util;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.util.functional.SymmetryRegionBoundsChecker;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/SymmetryUtil.class */
public class SymmetryUtil {
    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static SymmetryRegionBoundsChecker getSymmetryRegionBoundsChecker(Symmetry symmetry, int size) throws MatchException {
        int maxXBound = getMaxXBound(symmetry, size);
        IntUnaryOperator minYBoundFunction = getMinYBoundFunction(symmetry, size);
        IntUnaryOperator maxYBoundFunction = getMaxYBoundFunction(symmetry, size);
        return (x, y) -> {
            return x >= 0 && x < maxXBound && y >= minYBoundFunction.applyAsInt(x) && y < maxYBoundFunction.applyAsInt(x);
        };
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static int getMaxXBound(Symmetry symmetry, int size) throws MatchException {
        switch (symmetry) {
            case POINT4:
            case POINT5:
            case POINT6:
            case POINT7:
            case POINT8:
            case POINT9:
            case POINT10:
            case POINT11:
            case POINT12:
            case POINT13:
            case POINT14:
            case POINT15:
            case POINT16:
            case X:
            case QUAD:
            case DIAG:
                return (size / 2) + (size % 2);
            case POINT2:
            case POINT3:
            case XZ:
            case ZX:
            case Z:
            case NONE:
                return size;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static IntUnaryOperator getMinYBoundFunction(Symmetry symmetry, int size) throws MatchException {
        switch (symmetry) {
            case POINT4:
            case X:
            case QUAD:
            case POINT2:
            case POINT3:
            case ZX:
            case Z:
            case NONE:
                return x -> {
                    return 0;
                };
            case POINT5:
            case POINT6:
            case POINT7:
            case POINT8:
            case POINT9:
            case POINT10:
            case POINT11:
            case POINT12:
            case POINT13:
            case POINT14:
            case POINT15:
            case POINT16:
                double radians = convertToRotatedRadians(360.0f / symmetry.getNumSymPoints());
                double tan = StrictMath.tan(radians);
                int halfSizeBound = (size / 2) + (size % 2);
                float halfSize = size / 2.0f;
                return x2 -> {
                    if (x2 > halfSizeBound) {
                        return 0;
                    }
                    float dx = x2 - halfSize;
                    int y = (int) (((double) halfSize) + (tan * ((double) dx)));
                    return MathUtil.clamp(y, 0, size);
                };
            case DIAG:
            case XZ:
                return x3 -> {
                    return x3;
                };
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static IntUnaryOperator getMaxYBoundFunction(Symmetry symmetry, int size) throws MatchException {
        switch (symmetry) {
            case POINT4:
            case POINT5:
            case POINT6:
            case POINT7:
            case POINT8:
            case POINT9:
            case POINT10:
            case POINT11:
            case POINT12:
            case POINT13:
            case POINT14:
            case POINT15:
            case POINT16:
            case QUAD:
                int halfSizeBound = (size / 2) + (size % 2);
                return x -> {
                    if (x < halfSizeBound) {
                        return halfSizeBound;
                    }
                    return 0;
                };
            case X:
            case XZ:
            case NONE:
                return x2 -> {
                    return size;
                };
            case DIAG:
            case ZX:
                return x3 -> {
                    return size - x3;
                };
            case POINT2:
            case Z:
                int halfSizeBound2 = (size / 2) + (size % 2);
                return x4 -> {
                    return halfSizeBound2;
                };
            case POINT3:
                double tan = StrictMath.tan(convertToRotatedRadians(360.0f / symmetry.getNumSymPoints()));
                int halfSizeBound3 = (size / 2) + (size % 2);
                float halfSize = size / 2.0f;
                return x5 -> {
                    if (x5 < halfSizeBound3) {
                        return halfSizeBound3;
                    }
                    float dx = x5 - halfSize;
                    int y = (int) (((double) halfSize) + (tan * ((double) dx)));
                    return MathUtil.clamp(y, 0, halfSizeBound3);
                };
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static List<Vector2> getRandomPointsInBounds(RandomGenerator random, Symmetry symmetry, int size, int numPoints) throws MatchException {
        int maxX = getMaxXBound(symmetry, size);
        IntUnaryOperator minYBoundFunction = getMinYBoundFunction(symmetry, size);
        IntUnaryOperator maxYBoundFunction = getMaxYBoundFunction(symmetry, size);
        return IntStream.range(0, numPoints).mapToObj(i -> {
            int x = random.nextInt(0, maxX);
            int minY = minYBoundFunction.applyAsInt(x);
            int maxY = maxYBoundFunction.applyAsInt(x);
            int y = minY >= maxY ? minY : random.nextInt(minYBoundFunction.applyAsInt(x), maxYBoundFunction.applyAsInt(x));
            return new Vector2(x, y);
        }).toList();
    }

    static double convertToRotatedRadians(float angle) {
        float adjustedAngle = angle + 180.0f;
        return ((double) (adjustedAngle / 180.0f)) * 3.141592653589793d;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static List<Vector2> getSymmetryPoints(float x, float y, int size, Symmetry symmetry, Symmetry secondarySymmetry) throws MatchException {
        int numSymPoints = symmetry.getNumSymPoints();
        switch (symmetry) {
            case POINT4:
                return List.of(new Vector2((size - x) - 1.0f, (size - y) - 1.0f), new Vector2(y, (size - x) - 1.0f), new Vector2((size - y) - 1.0f, x));
            case POINT5:
            case POINT7:
            case POINT9:
            case POINT11:
            case POINT13:
            case POINT15:
            case POINT3:
                List<Vector2> symmetryPoints = new ArrayList<>(numSymPoints - 1);
                for (int i = 1; i < numSymPoints; i++) {
                    Vector2 rotated = getRotatedPoint(x, y, size, (float) ((6.283185307179586d * ((double) i)) / ((double) numSymPoints)));
                    symmetryPoints.add(rotated);
                }
                return symmetryPoints;
            case POINT6:
            case POINT8:
            case POINT10:
            case POINT12:
            case POINT14:
            case POINT16:
                List<Vector2> symmetryPoints2 = new ArrayList<>(numSymPoints - 1);
                symmetryPoints2.add(new Vector2((size - x) - 1.0f, (size - y) - 1.0f));
                for (int i2 = 1; i2 < numSymPoints / 2; i2++) {
                    float angle = (float) ((6.283185307179586d * ((double) i2)) / ((double) numSymPoints));
                    Vector2 rotated2 = getRotatedPoint(x, y, size, angle);
                    symmetryPoints2.add(rotated2);
                    Vector2 antiRotated = getRotatedPoint(x, y, size, (float) (((double) angle) + 3.141592653589793d));
                    symmetryPoints2.add(antiRotated);
                }
                return symmetryPoints2;
            case X:
                return List.of(new Vector2((size - x) - 1.0f, y));
            case QUAD:
                if (secondarySymmetry == Symmetry.Z) {
                    return List.of(new Vector2(x, (size - y) - 1.0f), new Vector2((size - x) - 1.0f, y), new Vector2((size - x) - 1.0f, (size - y) - 1.0f));
                }
                return List.of(new Vector2((size - x) - 1.0f, y), new Vector2(x, (size - y) - 1.0f), new Vector2((size - x) - 1.0f, (size - y) - 1.0f));
            case DIAG:
                if (secondarySymmetry == Symmetry.ZX) {
                    return List.of(new Vector2((size - y) - 1.0f, (size - x) - 1.0f), new Vector2(y, x), new Vector2((size - x) - 1.0f, (size - y) - 1.0f));
                }
                return List.of(new Vector2(y, x), new Vector2((size - y) - 1.0f, (size - x) - 1.0f), new Vector2((size - x) - 1.0f, (size - y) - 1.0f));
            case POINT2:
                return List.of(new Vector2((size - x) - 1.0f, (size - y) - 1.0f));
            case XZ:
                return List.of(new Vector2(y, x));
            case ZX:
                return List.of(new Vector2((size - y) - 1.0f, (size - x) - 1.0f));
            case Z:
                return List.of(new Vector2(x, (size - y) - 1.0f));
            case NONE:
                return List.of();
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public static List<Float> getSymmetryRotations(float rot, Symmetry symmetry, Symmetry secondarySymmetry) {
        List<Float> symmetryRotation = new ArrayList<>();
        float xRotation = (float) StrictMath.atan2(-StrictMath.sin(rot), StrictMath.cos(rot));
        float zRotation = (float) StrictMath.atan2(-StrictMath.cos(rot), StrictMath.sin(rot));
        float diagRotation = (float) StrictMath.atan2(-StrictMath.cos(rot), -StrictMath.sin(rot));
        switch (symmetry) {
            case POINT4:
                symmetryRotation.add(Float.valueOf(rot + 3.1415927f));
                symmetryRotation.add(Float.valueOf(rot + 1.5707964f));
                symmetryRotation.add(Float.valueOf(rot - 1.5707964f));
                break;
            case POINT5:
            case POINT6:
            case POINT7:
            case POINT8:
            case POINT9:
            case POINT10:
            case POINT11:
            case POINT12:
            case POINT13:
            case POINT14:
            case POINT15:
            case POINT16:
            case POINT3:
                int numSymPoints = symmetry.getNumSymPoints();
                for (int i = 1; i < numSymPoints; i++) {
                    symmetryRotation.add(Float.valueOf(rot + ((float) ((6.283185307179586d * ((double) i)) / ((double) numSymPoints)))));
                }
                break;
            case X:
            case POINT2:
            case Z:
                symmetryRotation.add(Float.valueOf(rot + 3.1415927f));
                break;
            case QUAD:
                if (secondarySymmetry == Symmetry.Z) {
                    symmetryRotation.add(Float.valueOf(zRotation));
                    symmetryRotation.add(Float.valueOf(xRotation));
                    symmetryRotation.add(Float.valueOf(rot + 3.1415927f));
                } else {
                    symmetryRotation.add(Float.valueOf(xRotation));
                    symmetryRotation.add(Float.valueOf(zRotation));
                    symmetryRotation.add(Float.valueOf(rot + 3.1415927f));
                }
                break;
            case DIAG:
                if (secondarySymmetry == Symmetry.ZX) {
                    symmetryRotation.add(Float.valueOf(diagRotation));
                    symmetryRotation.add(Float.valueOf(diagRotation));
                    symmetryRotation.add(Float.valueOf(rot + 3.1415927f));
                } else {
                    symmetryRotation.add(Float.valueOf(diagRotation));
                    symmetryRotation.add(Float.valueOf(diagRotation));
                    symmetryRotation.add(Float.valueOf(rot + 3.1415927f));
                }
                break;
            case XZ:
            case ZX:
                symmetryRotation.add(Float.valueOf(diagRotation));
                break;
        }
        return symmetryRotation;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static Vector2 getSourcePoint(int x, int y, int size, Symmetry symmetry) throws MatchException {
        int halfSizeBound = (size / 2) + (size % 2);
        switch (symmetry) {
            case POINT4:
                if (x >= halfSizeBound && y >= halfSizeBound) {
                    return new Vector2((size - x) - 1, (size - y) - 1);
                }
                if (x <= halfSizeBound && y >= halfSizeBound) {
                    return new Vector2((size - y) - 1, x);
                }
                if (x >= halfSizeBound) {
                    return new Vector2(y, (size - x) - 1);
                }
                return new Vector2(x, y);
            case POINT5:
            case POINT6:
            case POINT7:
            case POINT8:
            case POINT9:
            case POINT10:
            case POINT11:
            case POINT12:
            case POINT13:
            case POINT14:
            case POINT15:
            case POINT16:
            case POINT3:
                float baseRadians = (float) (6.283185307179586d / ((double) symmetry.getNumSymPoints()));
                float dx = x - (size / 2.0f);
                float dy = y - (size / 2.0f);
                float angle = (float) (StrictMath.atan2(dy, dx) + 3.141592653589793d);
                float rawSlice = angle / baseRadians;
                int slice = (int) rawSlice;
                if (rawSlice == slice) {
                    slice--;
                }
                if (slice == 0) {
                    return new Vector2(x, y);
                }
                float antiRotateAngle = (-slice) * baseRadians;
                return getRotatedPoint(x, y, size, antiRotateAngle);
            case X:
                if (x >= halfSizeBound) {
                    return new Vector2((size - x) - 1, y);
                }
                return new Vector2(x, y);
            case QUAD:
                if (x >= halfSizeBound) {
                    if (y >= halfSizeBound) {
                        return new Vector2((size - x) - 1, (size - y) - 1);
                    }
                    return new Vector2((size - x) - 1, y);
                }
                if (y >= halfSizeBound) {
                    return new Vector2(x, (size - y) - 1);
                }
                return new Vector2(x, y);
            case DIAG:
                if (x > y) {
                    if (y > (size - x) - 1) {
                        return new Vector2((size - x) - 1, (size - y) - 1);
                    }
                    return new Vector2(y, x);
                }
                if (y > (size - x) - 1) {
                    return new Vector2((size - y) - 1, (size - x) - 1);
                }
                return new Vector2(x, y);
            case POINT2:
                if (y >= halfSizeBound) {
                    return new Vector2((size - x) - 1, (size - y) - 1);
                }
                return new Vector2(x, y);
            case XZ:
                if (x > y) {
                    return new Vector2(y, x);
                }
                return new Vector2(x, y);
            case ZX:
                if (y > (size - x) - 1) {
                    return new Vector2((size - y) - 1, (size - x) - 1);
                }
                return new Vector2(x, y);
            case Z:
                if (y >= halfSizeBound) {
                    return new Vector2(x, (size - y) - 1);
                }
                return new Vector2(x, y);
            case NONE:
                return new Vector2(x, y);
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public static Vector2 getRotatedPoint(float x, float y, int size, float radians) {
        if (radians == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return new Vector2(x, y);
        }
        float halfSize = (size / 2.0f) - 0.5f;
        float xt = x - halfSize;
        float yt = y - halfSize;
        Vector2 result = rotateByShear(radians, xt, yt);
        return new Vector2(result.x() + halfSize, result.y() + halfSize);
    }

    public static Vector2 rotateByShear(float radians, float xt, float yt) {
        float xs;
        float ys;
        float sign = StrictMath.signum(radians);
        if (sign == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return new Vector2(xt, yt);
        }
        int numNinetyDegreeTurns = StrictMath.round(radians / 1.5707964f);
        switch (numNinetyDegreeTurns % 4) {
            case -3:
            case 1:
                xs = -yt;
                ys = xt;
                break;
            case -2:
            case 2:
                xs = -xt;
                ys = -yt;
                break;
            case -1:
            case 3:
                xs = yt;
                ys = -xt;
                break;
            case 0:
                xs = xt;
                ys = yt;
                break;
            default:
                throw new IllegalStateException("Unexpected number of 90 degree turns");
        }
        float residualRadians = radians - (numNinetyDegreeTurns * 1.5707964f);
        if (residualRadians == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            return new Vector2(xs, ys);
        }
        float tanHalf = (float) StrictMath.tan(((double) residualRadians) / 2.0d);
        float sin = (float) StrictMath.sin(residualRadians);
        float x1 = StrictMath.round(xs - (ys * tanHalf));
        float y1 = StrictMath.round(ys);
        float x2 = StrictMath.round(x1);
        float y2 = StrictMath.round(y1 + (x1 * sin));
        float xr = StrictMath.round(x2 - (y2 * tanHalf));
        float yr = StrictMath.round(y2);
        return new Vector2(xr, yr);
    }
}
