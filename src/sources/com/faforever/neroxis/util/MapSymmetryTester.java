package com.faforever.neroxis.util;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.PositionedObject;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.mask.FloatMask;
import com.faforever.neroxis.mask.IntegerMask;
import com.faforever.neroxis.mask.Mask;
import com.faforever.neroxis.mask.PrimitiveMask;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/MapSymmetryTester.class */
public class MapSymmetryTester {
    private MapSymmetryTester() {
    }

    public static Result evaluate(SCMap map, SymmetrySettings symmetrySettings) {
        FloatMask heightMask = new FloatMask(map.getHeightmap(), null, symmetrySettings, map.getHeightMapScale(), "heightMask");
        boolean oddVsEven = evaluateOddEven(map, heightMask);
        float terrainScore = evaluateTerrain(heightMask);
        float spawnScore = evaluateSpawns(map, heightMask);
        float mexScore = evaluateMexes(map, heightMask);
        float hydroScore = evaluateHydros(map, heightMask);
        float propScore = evaluateProps(map, heightMask);
        float unitScore = evaluateUnits(map, heightMask);
        return new Result(oddVsEven, terrainScore, spawnScore, mexScore, hydroScore, propScore, unitScore);
    }

    private static float evaluateTerrain(FloatMask heightMask) {
        return ((Float) DebugUtil.timedRun("evaluateTerrain", () -> {
            return Float.valueOf(getMaskScore(heightMask));
        })).floatValue();
    }

    private static boolean evaluateOddEven(SCMap map, FloatMask heightMask) {
        return ((Boolean) DebugUtil.timedRun("evaluateSpawns", () -> {
            return Boolean.valueOf(checkSpawnsOddEven(map.getSpawns(), heightMask));
        })).booleanValue();
    }

    private static float evaluateSpawns(SCMap map, FloatMask heightMask) {
        return ((Float) DebugUtil.timedRun("evaluateSpawns", () -> {
            return Float.valueOf(getPositionedObjectScore(map.getSpawns(), heightMask));
        })).floatValue();
    }

    private static float evaluateMexes(SCMap map, FloatMask heightMask) {
        return ((Float) DebugUtil.timedRun("evaluateMexes", () -> {
            return Float.valueOf(getPositionedObjectScore(map.getMexes(), heightMask));
        })).floatValue();
    }

    private static float evaluateHydros(SCMap map, FloatMask heightMask) {
        return ((Float) DebugUtil.timedRun("evaluateHydros", () -> {
            return Float.valueOf(getPositionedObjectScore(map.getHydros(), heightMask));
        })).floatValue();
    }

    private static float evaluateProps(SCMap map, FloatMask heightMask) {
        return ((Float) DebugUtil.timedRun("evaluateProps", () -> {
            return Float.valueOf((float) ((Map) map.getProps().stream().collect(Collectors.groupingBy((v0) -> {
                return v0.getPath();
            }))).values().stream().mapToDouble(props -> {
                return getPositionedObjectScore(props, heightMask);
            }).sum());
        })).floatValue();
    }

    private static float evaluateUnits(SCMap map, FloatMask heightMask) {
        return ((Float) DebugUtil.timedRun("evaluateUnits", () -> {
            return Float.valueOf((float) map.getArmies().stream().flatMap(army -> {
                return army.getGroups().stream().flatMap(group -> {
                    return ((Map) group.getUnits().stream().collect(Collectors.groupingBy((v0) -> {
                        return v0.getType();
                    }))).values().stream();
                });
            }).mapToDouble(units -> {
                return getPositionedObjectScore(units, heightMask);
            }).sum());
        })).floatValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private static <T extends PrimitiveMask<?, T>> float getMaskScore(T mask) throws MatchException {
        float totalError;
        PrimitiveMask primitiveMask = (PrimitiveMask) mask.copy();
        primitiveMask.forceSymmetry(SymmetryType.SPAWN, false);
        Objects.requireNonNull(mask);
        switch ((int) SwitchBootstraps.typeSwitch(MethodHandles.lookup(), "typeSwitch", MethodType.methodType(Integer.TYPE, PrimitiveMask.class, Integer.TYPE), BooleanMask.class, FloatMask.class, IntegerMask.class).dynamicInvoker().invoke(mask, 0) /* invoke-custom */) {
            case 0:
                BooleanMask booleanMask = (BooleanMask) mask;
                ((BooleanMask) primitiveMask).subtract(booleanMask);
                totalError = ((BooleanMask) primitiveMask).getCount();
                break;
            case 1:
                FloatMask floatMask = (FloatMask) mask;
                ((FloatMask) primitiveMask).subtract(floatMask).multiply((FloatMask) primitiveMask);
                totalError = (float) StrictMath.sqrt(((FloatMask) primitiveMask).getSum().floatValue());
                break;
            case 2:
                IntegerMask integerMask = (IntegerMask) mask;
                ((IntegerMask) primitiveMask).subtract(integerMask).multiply((IntegerMask) primitiveMask);
                totalError = (float) StrictMath.sqrt(((IntegerMask) primitiveMask).getSum().intValue());
                break;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
        return (totalError / mask.getSize()) / mask.getSize();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float getPositionedObjectScore(List<? extends PositionedObject> objects, Mask<?, ?> mask) {
        if (objects.isEmpty()) {
            return SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
        }
        float locationScore = 0.0f;
        List<Vector3> locations = (List) objects.stream().map((v0) -> {
            return v0.getPosition();
        }).collect(Collectors.toList());
        Set<Vector3> locationsSet = Collections.newSetFromMap(new IdentityHashMap());
        locationsSet.addAll(locations);
        while (!locationsSet.isEmpty()) {
            Vector3 location = (Vector3) locations.removeFirst();
            for (Vector2 symmetryPoint : mask.getSymmetryPointsWithOutOfBounds(location, SymmetryType.SPAWN)) {
                Vector3 closestLoc = null;
                float minDist = (float) StrictMath.sqrt(mask.getSize() * mask.getSize());
                for (Vector3 other : locations) {
                    float dist = other.getXZDistance(symmetryPoint);
                    if (dist < minDist) {
                        closestLoc = other;
                        minDist = dist;
                    }
                }
                locationsSet.remove(location);
                if (closestLoc != null) {
                    locationsSet.remove(closestLoc);
                }
                locationScore += minDist;
                locations = new ArrayList<>(locationsSet);
            }
        }
        return locationScore / (objects.size() / 2.0f);
    }

    private static boolean checkSpawnsOddEven(List<Spawn> spawns, Mask<?, ?> mask) {
        for (Spawn spawn : spawns) {
            Spawn closestSpawn = null;
            float minDist = (float) StrictMath.sqrt(mask.getSize() * mask.getSize());
            Vector2 symmetrySpawn = (Vector2) mask.getSymmetryPoints(spawn.getPosition(), SymmetryType.SPAWN).getFirst();
            for (Spawn otherSpawn : spawns) {
                if (!otherSpawn.equals(spawn)) {
                    float dist = otherSpawn.getPosition().getXZDistance(symmetrySpawn);
                    if (dist < minDist) {
                        closestSpawn = otherSpawn;
                        minDist = dist;
                    }
                }
            }
            if (closestSpawn == null) {
                return false;
            }
            int spawnId = Integer.parseInt(spawn.getId().split("_")[1]);
            int closestSpawnId = Integer.parseInt(closestSpawn.getId().split("_")[1]);
            if (spawnId % 2 == 0) {
                if (spawnId != closestSpawnId + 1) {
                    return false;
                }
            } else if (spawnId != closestSpawnId - 1) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/MapSymmetryTester$Result.class */
    public static final class Result extends Record {
        private final boolean oddVsEven;
        private final float terrainScore;
        private final float spawnScore;
        private final float mexScore;
        private final float hydroScore;
        private final float propScore;
        private final float unitScore;
        private static final double TOLERANCE = 0.001d;

        public Result(boolean oddVsEven, float terrainScore, float spawnScore, float mexScore, float hydroScore, float propScore, float unitScore) {
            this.oddVsEven = oddVsEven;
            this.terrainScore = terrainScore;
            this.spawnScore = spawnScore;
            this.mexScore = mexScore;
            this.hydroScore = hydroScore;
            this.propScore = propScore;
            this.unitScore = unitScore;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Result.class), Result.class, "oddVsEven;terrainScore;spawnScore;mexScore;hydroScore;propScore;unitScore", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->oddVsEven:Z", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->terrainScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->spawnScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->mexScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->hydroScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->propScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->unitScore:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Result.class), Result.class, "oddVsEven;terrainScore;spawnScore;mexScore;hydroScore;propScore;unitScore", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->oddVsEven:Z", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->terrainScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->spawnScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->mexScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->hydroScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->propScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->unitScore:F").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Result.class, Object.class), Result.class, "oddVsEven;terrainScore;spawnScore;mexScore;hydroScore;propScore;unitScore", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->oddVsEven:Z", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->terrainScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->spawnScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->mexScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->hydroScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->propScore:F", "FIELD:Lcom/faforever/neroxis/util/MapSymmetryTester$Result;->unitScore:F").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public boolean oddVsEven() {
            return this.oddVsEven;
        }

        public float terrainScore() {
            return this.terrainScore;
        }

        public float spawnScore() {
            return this.spawnScore;
        }

        public float mexScore() {
            return this.mexScore;
        }

        public float hydroScore() {
            return this.hydroScore;
        }

        public float propScore() {
            return this.propScore;
        }

        public float unitScore() {
            return this.unitScore;
        }

        public boolean isSymmetric() {
            return this.oddVsEven && ((double) StrictMath.abs(this.terrainScore)) < TOLERANCE && ((double) StrictMath.abs(this.spawnScore)) < TOLERANCE && ((double) StrictMath.abs(this.mexScore)) < TOLERANCE && ((double) StrictMath.abs(this.hydroScore)) < TOLERANCE && ((double) StrictMath.abs(this.propScore)) < TOLERANCE && ((double) StrictMath.abs(this.unitScore)) < TOLERANCE;
        }
    }
}
