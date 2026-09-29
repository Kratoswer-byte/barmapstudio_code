package com.faforever.neroxis.mask;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/mask/MapMaskMethods.class */
public class MapMaskMethods {
    private MapMaskMethods() {
    }

    public static BooleanMask connectLocationsThroughMiddle(List<Vector2> locations, RandomGenerator random, BooleanMask exec, int minMiddlePoints, int maxMiddlePoints, int numConnections, float maxStepSize) {
        int iNextInt;
        for (int i = 0; i < numConnections; i++) {
            if (maxMiddlePoints > minMiddlePoints) {
                iNextInt = random.nextInt(maxMiddlePoints - minMiddlePoints) + minMiddlePoints;
            } else {
                iNextInt = maxMiddlePoints;
            }
            int numMiddlePoints = iNextInt;
            Vector2 start = locations.get(random.nextInt(locations.size()));
            float maxMiddleDistance = start.getDistance(start);
            exec.connect(start, start, maxStepSize, numMiddlePoints, maxMiddleDistance, maxMiddleDistance / 2.0f, 1.5707964f, SymmetryType.TERRAIN);
        }
        return exec;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static BooleanMask connectLocationsAroundCenter(List<Vector2> locations, RandomGenerator random, BooleanMask exec, int minMiddlePoints, int maxMiddlePoints, int numConnections, float maxStepSize, int bound) {
        return (BooleanMask) exec.enqueue(() -> {
            int numMiddlePoints;
            for (int i = 0; i < numConnections; i++) {
                if (maxMiddlePoints > minMiddlePoints) {
                    numMiddlePoints = random.nextInt(maxMiddlePoints - minMiddlePoints) + minMiddlePoints;
                } else {
                    numMiddlePoints = maxMiddlePoints;
                }
                Vector2 start = (Vector2) locations.get(random.nextInt(locations.size()));
                float offCenterAngle = (float) (3.141592653589793d * ((double) (0.33333334f + (random.nextFloat() / 3.0f))));
                Vector2 end = start.addPolar((offCenterAngle * (random.nextBoolean() ? 1.0f : -1.0f)) + start.angleTo(new Vector2(exec.getSize() / 2.0f, exec.getSize() / 2.0f)), ((random.nextFloat() * exec.getSize()) / 2.0f) + (exec.getSize() / 2.0f)).clampMax(exec.getSize() - bound).clampMin(bound);
                float maxMiddleDistance = start.getDistance(end);
                exec.connect(start, end, maxStepSize, numMiddlePoints, maxMiddleDistance, maxMiddleDistance / 2.0f, 1.5707964f, SymmetryType.TERRAIN);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static BooleanMask connectLocations(List<Vector2> locations, RandomGenerator random, BooleanMask exec, int maxMiddlePoints, int numConnections, float maxStepSize) {
        return (BooleanMask) exec.enqueue(() -> {
            if (locations.size() > 1) {
                locations.forEach(startSpawn -> {
                    for (int i = 0; i < numConnections; i++) {
                        ArrayList<Vector2> otherSpawns = new ArrayList<>(locations);
                        otherSpawns.remove(startSpawn);
                        Vector2 endSpawn = otherSpawns.get(random.nextInt(otherSpawns.size()));
                        int numMiddlePoints = random.nextInt(maxMiddlePoints);
                        float maxMiddleDistance = (startSpawn.getDistance(endSpawn) / numMiddlePoints) * 2.0f;
                        exec.path(startSpawn, endSpawn, maxStepSize, numMiddlePoints, maxMiddleDistance, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.5707964f, SymmetryType.TERRAIN);
                    }
                });
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static BooleanMask pathInCenterBounds(RandomGenerator random, BooleanMask exec, float maxStepSize, int numPaths, int maxMiddlePoints, int bound, float maxAngleError) {
        return (BooleanMask) exec.enqueue(() -> {
            for (int i = 0; i < numPaths; i++) {
                Vector2 start = new Vector2(random.nextInt((exec.getSize() + 1) - (bound * 2)) + bound, random.nextInt((exec.getSize() + 1) - (bound * 2)) + bound);
                Vector2 end = new Vector2(random.nextInt((exec.getSize() + 1) - (bound * 2)) + bound, random.nextInt((exec.getSize() + 1) - (bound * 2)) + bound);
                int numMiddlePoints = random.nextInt(maxMiddlePoints);
                float maxMiddleDistance = (start.getDistance(end) / numMiddlePoints) * 2.0f;
                exec.path(start, end, maxStepSize, numMiddlePoints, maxMiddleDistance, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, maxAngleError, SymmetryType.TERRAIN);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static BooleanMask pathInEdgeBounds(RandomGenerator random, BooleanMask exec, float maxStepSize, int numPaths, int maxMiddlePoints, int bound, float maxAngleError) {
        return (BooleanMask) exec.enqueue(() -> {
            for (int i = 0; i < numPaths; i++) {
                int startX = random.nextInt(bound) + (random.nextBoolean() ? 0 : exec.getSize() - bound);
                int startY = random.nextInt(bound) + (random.nextBoolean() ? 0 : exec.getSize() - bound);
                int endX = (random.nextInt(bound * 2) - bound) + startX;
                int endY = (random.nextInt(bound * 2) - bound) + startY;
                Vector2 start = new Vector2(startX, startY);
                Vector2 end = new Vector2(endX, endY);
                int numMiddlePoints = random.nextInt(maxMiddlePoints);
                float maxMiddleDistance = (start.getDistance(end) / numMiddlePoints) * 2.0f;
                exec.path(start, end, maxStepSize, numMiddlePoints, maxMiddleDistance, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, maxAngleError, SymmetryType.TERRAIN);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static BooleanMask pathAroundLocations(List<Vector2> locations, RandomGenerator random, BooleanMask exec, float maxStepSize, int numPaths, int maxMiddlePoints, int bound, float maxAngleError) {
        return (BooleanMask) exec.enqueue(() -> {
            locations.forEach(location -> {
                for (int i = 0; i < numPaths; i++) {
                    int endX = (int) (random.nextFloat(bound) + location.x());
                    int endY = (int) (random.nextFloat(bound) + location.y());
                    Vector2 end = new Vector2(endX, endY);
                    int numMiddlePoints = random.nextInt(maxMiddlePoints);
                    float maxMiddleDistance = (location.getDistance(end) / numMiddlePoints) * 2.0f;
                    exec.path(location, end, maxStepSize, numMiddlePoints, maxMiddleDistance, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, maxAngleError, SymmetryType.SPAWN);
                }
            });
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static FloatMask flattenHeightBand(FloatMask exec, FloatMask noiseMap, float minHeight, float maxHeight, float destinationMinHeight, float destinationMaxHeight, float slope) {
        return (FloatMask) exec.enqueue(dependencies -> {
            FloatMask noise = (FloatMask) dependencies.getFirst();
            BooleanMask flattenMask = noise.copyAsBooleanMask(Float.valueOf(minHeight), Float.valueOf(maxHeight));
            exec.setPrimitiveWithSymmetry(SymmetryType.SPAWN, (x, y) -> {
                float value = noise.getPrimitive(x, y);
                if (!flattenMask.getPrimitive(x, y)) {
                    return exec.getPrimitive(x, y);
                }
                if (slope <= SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT || maxHeight <= minHeight) {
                    return destinationMaxHeight;
                }
                return remapWithSlope(value, minHeight, maxHeight, destinationMinHeight, destinationMaxHeight, slope);
            });
        }, noiseMap);
    }

    private static float remapWithSlope(float value, float minHeight, float maxHeight, float destinationMinHeight, float destinationMaxHeight, float slope) {
        if (maxHeight <= minHeight) {
            return destinationMaxHeight;
        }
        float normalized = (value - minHeight) / (maxHeight - minHeight);
        float curved = (float) StrictMath.pow(StrictMath.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, StrictMath.min(1.0f, normalized)), slope);
        return destinationMinHeight + ((destinationMaxHeight - destinationMinHeight) * curved);
    }
}
