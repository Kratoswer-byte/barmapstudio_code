package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/HydroPlacer.class */
public class HydroPlacer {
    protected final SCMap map;
    protected final RandomGenerator.SplittableGenerator random;
    protected final int hydroSpacing = 64;

    public HydroPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        this.map = map;
        this.random = random.split();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void placeHydros(int hydroCount, BooleanMask allowedHydroMask) {
        this.map.getHydros().clear();
        if (hydroCount <= 0) {
            return;
        }
        int numSymPoints = allowedHydroMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
        if (!allowedHydroMask.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            allowedHydroMask.limitToCenteredCircle(allowedHydroMask.getSize() / 2.0f);
        }
        ((BooleanMask) allowedHydroMask.fillCenter(64, false)).limitToSymmetryRegion();
        this.map.getMexes().stream().filter(mex -> {
            return allowedHydroMask.inTeam(mex.getPosition(), false);
        }).forEach(mex2 -> {
            allowedHydroMask.fillCircle(mex2.getPosition(), 10.0f, false);
        });
        if (hydroCount >= this.map.getSpawnCount()) {
            placeBaseHydros(allowedHydroMask);
        }
        this.map.getSpawns().stream().filter(spawn -> {
            return allowedHydroMask.inTeam(spawn.getPosition(), false);
        }).forEach(spawn2 -> {
            allowedHydroMask.fillCircle(spawn2.getPosition(), 30.0f, false);
        });
        int numHydrosLeft = (hydroCount - this.map.getHydroCount()) / numSymPoints;
        if (this.map.getHydroCount() == 0) {
            numHydrosLeft = Math.max(1, numHydrosLeft);
        }
        placeIndividualHydros(allowedHydroMask, allowedHydroMask, numHydrosLeft, this.hydroSpacing);
    }

    public void placeRemainingHydros(int hydroCount, BooleanMask relaxedMask) {
        int missing = hydroCount - this.map.getHydroCount();
        if (missing <= 0) {
            return;
        }
        int numSymPoints = relaxedMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
        if (!relaxedMask.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            relaxedMask.limitToCenteredCircle(relaxedMask.getSize() / 2.0f);
        }
        relaxedMask.limitToSymmetryRegion();
        this.map.getMexes().stream().filter(mex -> {
            return relaxedMask.inTeam(mex.getPosition(), false);
        }).forEach(mex2 -> {
            relaxedMask.fillCircle(mex2.getPosition(), 6.0f, false);
        });
        this.map.getSpawns().stream().filter(spawn -> {
            return relaxedMask.inTeam(spawn.getPosition(), false);
        }).forEach(spawn2 -> {
            relaxedMask.fillCircle(spawn2.getPosition(), 16.0f, false);
        });
        this.map.getHydros().stream().filter(hydro -> {
            return relaxedMask.inTeam(hydro.getPosition(), false);
        }).forEach(hydro2 -> {
            relaxedMask.fillCircle(hydro2.getPosition(), 16.0f, false);
        });
        placeIndividualHydros(relaxedMask, relaxedMask, Math.max(1, missing / numSymPoints), this.hydroSpacing / 2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void placeBaseHydros(BooleanMask allowedHydroMask) {
        boolean spawnHydro = this.random.nextBoolean();
        if (spawnHydro) {
            int numSymPoints = 0;
            while (true) {
                int i = numSymPoints;
                if (i < this.map.getSpawnCount()) {
                    Spawn spawn = this.map.getSpawn(i);
                    BooleanMask baseHydro = new BooleanMask(allowedHydroMask.getSize(), this.random.split(), allowedHydroMask.getSymmetrySettings());
                    ((BooleanMask) ((BooleanMask) baseHydro.fillCircle(spawn.getPosition(), 30.0f, true)).fillCircle(spawn.getPosition(), 10.0f, false)).multiply(allowedHydroMask);
                    this.map.getSpawns().stream().filter(otherSpawn -> {
                        return allowedHydroMask.inTeam(otherSpawn.getPosition(), false);
                    }).forEach(otherSpawn2 -> {
                        baseHydro.fillCircle(otherSpawn2.getPosition(), 16.0f, false);
                    });
                    this.map.getHydros().stream().filter(hydro -> {
                        return allowedHydroMask.inTeam(hydro.getPosition(), false);
                    }).forEach(hydro2 -> {
                        baseHydro.fillCircle(hydro2.getPosition(), 16.0f, false);
                    });
                    placeIndividualHydros(baseHydro, allowedHydroMask, 1, this.hydroSpacing);
                    numSymPoints = i + allowedHydroMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
                } else {
                    return;
                }
            }
        }
    }

    protected void placeIndividualHydros(BooleanMask spawnHydroMask, BooleanMask allowedHydroMask, int numHydros, int hydroSpacing) {
        if (numHydros > 0) {
            List<Vector2> hydroLocations = spawnHydroMask.getRandomCoordinates(hydroSpacing);
            hydroLocations.stream().limit(numHydros).map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).forEach(location -> {
                int hydroId = this.map.getHydroCount() / spawnHydroMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
                Marker hydro = new Marker(String.format("Hydro %d", Integer.valueOf(hydroId)), new Vector3(location));
                this.map.addHydro(hydro);
                allowedHydroMask.fillCircle(hydro.getPosition(), 5.0f, false);
                List<Vector2> symmetryPoints = spawnHydroMask.getSymmetryPoints(hydro.getPosition(), SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                for (int i = 0; i < symmetryPoints.size(); i++) {
                    Vector2 symmetryPoint = symmetryPoints.get(i);
                    this.map.addHydro(new Marker(String.format("Hydro %d sym %d", Integer.valueOf(hydroId), Integer.valueOf(i)), new Vector3(symmetryPoint)));
                }
            });
        }
    }
}
