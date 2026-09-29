package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Army;
import com.faforever.neroxis.map.Group;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/SpawnPlacer.class */
public class SpawnPlacer {
    private final SCMap map;
    private final RandomGenerator.SplittableGenerator random;

    public SpawnPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        this.map = map;
        this.random = random.split();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void placeSpawns(int spawnCount, float teammateSeparation, int teamSeparation, SymmetrySettings symmetrySettings) {
        this.map.getLargeExpansionAIMarkers().clear();
        this.map.getSpawns().clear();
        BooleanMask spawnMask = new BooleanMask(this.map.getSize() + 1, this.random.split(), symmetrySettings).invert();
        ((BooleanMask) ((BooleanMask) ((BooleanMask) spawnMask.fillSides(((this.map.getSize() / spawnCount) * 3) / 2, false)).fillCenter(teamSeparation, false)).fillEdge(this.map.getSize() / 16, false)).limitToSymmetryRegion();
        if (!spawnMask.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            spawnMask.limitToCenteredCircle((spawnMask.getSize() / 2.0f) - (this.map.getSize() / 16.0f));
        }
        Vector2 location = spawnMask.getRandomPosition();
        while (this.map.getSpawnCount() < spawnCount) {
            if (location == null) {
                placeSpawns(spawnCount, StrictMath.max(teammateSeparation - 4.0f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), teamSeparation, symmetrySettings);
                return;
            }
            Vector2 location2 = location.roundToNearestHalfPoint();
            spawnMask.fillCircle(location2, teammateSeparation, false);
            List<Vector2> symmetryPoints = spawnMask.getSymmetryPoints(location2, SymmetryType.SPAWN).stream().map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).toList();
            symmetryPoints.forEach(symmetryPoint -> {
                spawnMask.fillCircle(symmetryPoint, teamSeparation, false);
            });
            addSpawn(location2, symmetryPoints);
            if (spawnMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints() != 1) {
                BooleanMask nextSpawn = new BooleanMask(spawnMask.getSize(), this.random.split(), spawnMask.getSymmetrySettings());
                ((BooleanMask) nextSpawn.fillCircle(location2, teammateSeparation * 2.0f, true)).multiply(spawnMask);
                location = nextSpawn.getRandomPosition();
                if (location == null) {
                    location = spawnMask.getRandomPosition();
                }
            } else {
                location = spawnMask.getRandomPosition();
            }
        }
    }

    public boolean placeSpawns(int spawnCount, BooleanMask spawnMask, float minTeammateSeparation, float maxTeammateSeparation, int teamSeparation) {
        while (!tryPlaceSpawns(spawnCount, spawnMask, minTeammateSeparation, maxTeammateSeparation, teamSeparation)) {
            if (minTeammateSeparation - 4.0f >= 4.0f) {
                minTeammateSeparation -= 4.0f;
                maxTeammateSeparation += 16.0f;
            } else if (teamSeparation - 16 >= 32) {
                teamSeparation -= 16;
            } else {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean tryPlaceSpawns(int spawnCount, BooleanMask spawnMask, float minTeammateSeparation, float maxTeammateSeparation, int teamSeparation) {
        BooleanMask spawnSource;
        this.map.getLargeExpansionAIMarkers().clear();
        this.map.getSpawns().clear();
        BooleanMask spawnMaskCopy = (BooleanMask) spawnMask.copy();
        ((BooleanMask) ((BooleanMask) ((BooleanMask) spawnMaskCopy.fillSides(((this.map.getSize() / spawnCount) * 3) / 2, false)).fillCenter(teamSeparation, false)).subtract(new BooleanMask(this.map.getSize() + 1, this.random.split(), spawnMask.getSymmetrySettings()).drawSymmetryLines(spawnMask.getSymmetrySettings().teamSymmetry()).inflate(teamSeparation / spawnMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints())).fillEdge(this.map.getSize() / 32, false)).limitToSymmetryRegion();
        Vector2 spawnLocation = null;
        do {
            if (spawnLocation == null || spawnMaskCopy.getSymmetrySettings().teamSymmetry() == Symmetry.NONE) {
                spawnSource = spawnMaskCopy;
            } else {
                BooleanMask maxSpawnSeparationFilter = (BooleanMask) new BooleanMask(spawnMask.getSize(), (RandomGenerator.SplittableGenerator) null, spawnMask.getSymmetrySettings()).fillCircle(spawnLocation, maxTeammateSeparation, true);
                spawnSource = ((BooleanMask) spawnMaskCopy.copy()).multiply(maxSpawnSeparationFilter);
            }
            Vector2 spawnLocation2 = spawnSource.getRandomPosition();
            if (spawnLocation2 == null) {
                return false;
            }
            spawnLocation = spawnLocation2.roundToNearestHalfPoint();
            List<Vector2> symmetryPoints = spawnMaskCopy.getSymmetryPoints(spawnLocation, SymmetryType.SPAWN).stream().map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).toList();
            spawnMaskCopy.fillCircle(spawnLocation, minTeammateSeparation, false);
            symmetryPoints.forEach(symmetryPoint -> {
                spawnMaskCopy.fillCircle(symmetryPoint, minTeammateSeparation, false);
            });
            if (spawnMaskCopy.getSymmetrySettings().spawnSymmetry() == Symmetry.POINT2) {
                symmetryPoints.forEach(symmetryPoint2 -> {
                    spawnMaskCopy.fillCircle(symmetryPoint2, minTeammateSeparation, false);
                });
            }
            addSpawn(spawnLocation, symmetryPoints);
        } while (this.map.getSpawnCount() < spawnCount);
        return true;
    }

    private void addSpawn(Vector2 location, List<Vector2> symmetryPoints) {
        this.map.addSpawn(new Spawn(String.format("ARMY_%d", Integer.valueOf(this.map.getSpawnCount() + 1)), location, new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), 0));
        Group initial = new Group("INITIAL");
        Army army = new Army(String.format("ARMY_%d", Integer.valueOf(this.map.getArmyCount() + 1)));
        army.addGroup(initial);
        this.map.addArmy(army);
        for (int i = 0; i < symmetryPoints.size(); i++) {
            Vector2 symmetryPoint = symmetryPoints.get(i);
            this.map.addSpawn(new Spawn(String.format("ARMY_%d", Integer.valueOf(this.map.getSpawnCount() + 1)), symmetryPoint, new Vector2(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT), i + 1));
            Group initialSym = new Group("INITIAL");
            Army armySym = new Army(String.format("ARMY_%d", Integer.valueOf(this.map.getArmyCount() + 1)));
            armySym.addGroup(initialSym);
            this.map.addArmy(armySym);
        }
    }
}
