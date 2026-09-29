package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.map.AIMarker;
import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/MexPlacer.class */
public class MexPlacer {
    private final SCMap map;
    private final RandomGenerator.SplittableGenerator random;
    protected int minMexesPerPlayer = 3;
    protected int maxMexesPerPlayer = 5;
    protected boolean fixedMexesPerPlayer;

    public void setMexesPerPlayer(int min, int max) {
        this.minMexesPerPlayer = Math.max(1, Math.min(min, max));
        this.maxMexesPerPlayer = Math.max(this.minMexesPerPlayer, max);
        this.fixedMexesPerPlayer = true;
    }

    public MexPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        this.map = map;
        this.random = random.split();
    }

    public void placeMexes(int mexCount, BooleanMask allowedMexMask, BooleanMask spawnMaskWater) {
        int mexSpacing = ((int) ((this.map.getSize() / 8.0f) * StrictMath.min(StrictMath.max(40.0f / (mexCount * this.map.getSpawnCount()), 0.25f), 2.0f))) / 2;
        placeMexes(mexCount, allowedMexMask, spawnMaskWater, mexSpacing, 24, 48);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void placeMexes(int mexCount, BooleanMask allowedMexMask, BooleanMask spawnMaskWater, int mexSpacing, int spawnMexRadius, int remainingMexRadius) {
        this.map.getMexes().clear();
        int keepAway = this.fixedMexesPerPlayer ? this.map.getSize() / 8 : 0;
        int spawnKeepAway = StrictMath.max(spawnMexRadius, keepAway);
        int remainingKeepAway = StrictMath.max(remainingMexRadius, keepAway);
        if (!allowedMexMask.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            allowedMexMask.limitToCenteredCircle(allowedMexMask.getSize() / 2.0f);
        }
        allowedMexMask.limitToSymmetryRegion();
        spawnMaskWater.limitToSymmetryRegion();
        int numSymPoints = allowedMexMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
        placeBaseMexes(allowedMexMask);
        int numMexesLeft = (mexCount - this.map.getMexCount()) / numSymPoints;
        this.map.getSpawns().stream().filter(spawn -> {
            return allowedMexMask.inTeam(spawn.getPosition(), false);
        }).forEach(spawn2 -> {
            allowedMexMask.fillCircle(spawn2.getPosition(), spawnKeepAway, false);
        });
        int previousMexCount = this.map.getMexCount();
        if (numMexesLeft > 8 && numMexesLeft > this.map.getSpawnCount()) {
            int possibleExpMexCount = this.random.nextInt(numMexesLeft / 2) + (numMexesLeft / this.map.getSpawnCount());
            placeMexExpansions(allowedMexMask, possibleExpMexCount, mexSpacing);
            spacePlacedMexes(allowedMexMask, mexSpacing, previousMexCount);
            numMexesLeft = mexCount - this.map.getMexCount();
            previousMexCount = this.map.getMexCount();
        }
        int numPlayerMexes = (int) (((numMexesLeft / this.map.getSpawnCount()) / numSymPoints) * 0.5f);
        int numSymPoints2 = 0;
        while (true) {
            int i = numSymPoints2;
            if (i < this.map.getSpawnCount()) {
                Spawn spawn3 = this.map.getSpawn(i);
                BooleanMask playerSpawnMask = new BooleanMask(allowedMexMask.getSize(), this.random.split(), allowedMexMask.getSymmetrySettings());
                ((BooleanMask) playerSpawnMask.fillCircle(spawn3.getPosition(), this.map.getSize() / 6.0f, true)).multiply(allowedMexMask).fillEdge(this.map.getSize() / 16, false);
                this.map.getSpawns().forEach(otherSpawn -> {
                    if (otherSpawn.getTeamID() == spawn3.getTeamID() && !spawn3.equals(otherSpawn)) {
                        playerSpawnMask.fillCircle(otherSpawn.getPosition(), this.map.getSize() / 8.0f, false);
                    }
                });
                if (mexCount < 6) {
                    placeIndividualMexes(playerSpawnMask, numPlayerMexes, mexSpacing * 2);
                } else {
                    placeIndividualMexes(playerSpawnMask, numPlayerMexes, mexSpacing);
                }
                spacePlacedMexes(allowedMexMask, mexSpacing, previousMexCount);
                previousMexCount = this.map.getMexCount();
                numSymPoints2 = i + allowedMexMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
            } else {
                int numMexesLeft2 = (mexCount - this.map.getMexCount()) / numSymPoints;
                this.map.getSpawns().stream().filter(spawn4 -> {
                    return allowedMexMask.inTeam(spawn4.getPosition(), false);
                }).forEach(spawn5 -> {
                    allowedMexMask.fillCircle(spawn5.getPosition(), remainingKeepAway, false);
                });
                placeIndividualMexes(allowedMexMask, numMexesLeft2, mexSpacing);
                spacePlacedMexes(allowedMexMask, mexSpacing, previousMexCount);
                placeIndividualMexes(spawnMaskWater, StrictMath.min((mexCount - this.map.getMexCount()) / numSymPoints, 10), mexSpacing);
                return;
            }
        }
    }

    private void spacePlacedMexes(BooleanMask allowedMexMask, int mexSpacing, int previousMexCount) {
        this.map.getMexes().stream().skip(previousMexCount).filter(mex -> {
            return allowedMexMask.inTeam(mex.getPosition(), false);
        }).forEach(mex2 -> {
            allowedMexMask.fillCircle(mex2.getPosition(), mexSpacing, false);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void placeBaseMexes(BooleanMask allowedMexMask) {
        int numBaseMexes = this.random.nextInt(this.minMexesPerPlayer, this.maxMexesPerPlayer + 1);
        int previousMexCount = 0;
        int numSymPoints = 0;
        while (true) {
            int i = numSymPoints;
            if (i < this.map.getSpawnCount()) {
                Spawn spawn = this.map.getSpawn(i);
                BooleanMask baseMexes = new BooleanMask(allowedMexMask.getSize(), this.random.split(), allowedMexMask.getSymmetrySettings());
                ((BooleanMask) ((BooleanMask) baseMexes.fillCircle(spawn.getPosition(), 15.0f, true)).fillCircle(spawn.getPosition(), 5.0f, false)).multiply(allowedMexMask);
                placeIndividualMexes(baseMexes, numBaseMexes, 10);
                spacePlacedMexes(allowedMexMask, 2, previousMexCount);
                previousMexCount += numBaseMexes;
                numSymPoints = i + allowedMexMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void placeMexExpansions(BooleanMask allowedMexMask, int possibleExpMexCount, int mexSpacing) {
        Vector2 expLocation;
        int expMexCountLeft = possibleExpMexCount;
        int expSpacing = (int) ((this.map.getSize() / 4.0f) * StrictMath.min(StrictMath.max(8.0f / possibleExpMexCount, 0.75f), 1.75f));
        BooleanMask expansionSpawnMask = new BooleanMask(allowedMexMask.getSize(), this.random.split(), allowedMexMask.getSymmetrySettings());
        ((BooleanMask) ((BooleanMask) expansionSpawnMask.invert().fillCenter(96, false)).fillEdge(32, false)).multiply(allowedMexMask);
        this.map.getSpawns().stream().filter(spawn -> {
            return expansionSpawnMask.inTeam(spawn.getPosition(), false);
        }).forEach(spawn2 -> {
            expansionSpawnMask.fillCircle(spawn2.getPosition(), this.map.getSize() / 6.0f, false);
        });
        int expMexCount = StrictMath.min(this.random.nextInt(2) + 3, expMexCountLeft);
        List<Vector2> expansionLocations = expansionSpawnMask.getRandomCoordinates(expSpacing);
        while (expMexCountLeft > expMexCount && !expansionLocations.isEmpty()) {
            Object objRemoveFirst = expansionLocations.removeFirst();
            while (true) {
                expLocation = (Vector2) objRemoveFirst;
                if (isMexExpValid(expLocation, 10, allowedMexMask)) {
                    break;
                }
                if (expansionLocations.isEmpty()) {
                    expLocation = null;
                    break;
                }
                objRemoveFirst = expansionLocations.removeFirst();
            }
            if (expLocation != null) {
                BooleanMask expansionMask = new BooleanMask(allowedMexMask.getSize(), this.random.split(), allowedMexMask.getSymmetrySettings());
                expansionMask.fillCircle(expLocation, 10, true);
                expansionMask.multiply(allowedMexMask);
                int expID = this.map.getLargeExpansionMarkerCount() / allowedMexMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
                List<Vector2> symmetryPoints = expansionSpawnMask.getSymmetryPoints(expLocation, SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                if (expMexCount >= 3) {
                    this.map.addLargeExpansionMarker(new AIMarker(String.format("Large Expansion Area %d", Integer.valueOf(expID)), expLocation));
                    for (int i = 0; i < symmetryPoints.size(); i++) {
                        this.map.addLargeExpansionMarker(new AIMarker(String.format("Large Expansion Area %d sym %d", Integer.valueOf(expID), Integer.valueOf(i)), symmetryPoints.get(i)));
                    }
                } else {
                    this.map.addExpansionMarker(new AIMarker(String.format("Expansion Area %d", Integer.valueOf(expID)), expLocation));
                    for (int i2 = 0; i2 < symmetryPoints.size(); i2++) {
                        this.map.addExpansionMarker(new AIMarker(String.format("Expansion Area %d sym %d", Integer.valueOf(expID), Integer.valueOf(i2)), symmetryPoints.get(i2)));
                    }
                }
                placeIndividualMexes(expansionMask, expMexCount, 10);
                allowedMexMask.fillCircle(expLocation, ((mexSpacing * 3.0f) * expMexCount) / 4.0f, false);
                expMexCountLeft -= expMexCount;
            } else {
                return;
            }
        }
    }

    private void placeIndividualMexes(BooleanMask individualMexMask, int numMexes, int mexSpacing) {
        if (numMexes > 0) {
            List<Vector2> mexLocations = individualMexMask.getRandomCoordinates(mexSpacing);
            mexLocations.stream().limit(numMexes).map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).forEach(location -> {
                int mexID = this.map.getMexCount() / individualMexMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
                Marker mex = new Marker(String.format("Mex %d", Integer.valueOf(mexID)), new Vector3(location));
                this.map.addMex(mex);
                List<Vector2> symmetryPoints = individualMexMask.getSymmetryPoints(mex.getPosition(), SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                for (int i = 0; i < symmetryPoints.size(); i++) {
                    Vector2 symmetryPoint = symmetryPoints.get(i);
                    Marker marker = new Marker(String.format("Mex %d sym %d", Integer.valueOf(mexID), Integer.valueOf(i)), new Vector3(symmetryPoint));
                    this.map.addMex(marker);
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isMexExpValid(Vector2 location, float size, BooleanMask allowedMexMask) {
        float count = 0.0f;
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                Vector2 loc = location.add(dx - (size / 2.0f), dy - (size / 2.0f));
                if (allowedMexMask.inBounds(loc) && ((Boolean) allowedMexMask.get(loc)).booleanValue()) {
                    count += 1.0f;
                }
            }
        }
        return ((double) (count / (size * size))) > 0.5d;
    }
}
