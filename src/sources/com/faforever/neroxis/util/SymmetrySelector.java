package com.faforever.neroxis.util;

import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/SymmetrySelector.class */
public class SymmetrySelector {
    public static Symmetry getValidTerrainSymmetry(RandomGenerator random, int spawnCount, int numTeams) {
        List<Symmetry> arrayList;
        switch (spawnCount) {
            case 2:
            case 4:
            case 8:
                arrayList = new ArrayList<>(Arrays.asList(Symmetry.POINT2, Symmetry.POINT4, Symmetry.POINT6, Symmetry.POINT8, Symmetry.QUAD, Symmetry.DIAG));
                break;
            default:
                arrayList = new ArrayList<>(Arrays.asList(Symmetry.values()));
                break;
        }
        List<Symmetry> terrainSymmetries = arrayList;
        terrainSymmetries.remove(Symmetry.X);
        terrainSymmetries.remove(Symmetry.Z);
        if (numTeams > 1) {
            terrainSymmetries.remove(Symmetry.NONE);
            terrainSymmetries.removeIf(symmetry -> {
                return symmetry.getNumSymPoints() % numTeams != 0 || symmetry.getNumSymPoints() > spawnCount * 4;
            });
        } else {
            terrainSymmetries.clear();
            terrainSymmetries.add(Symmetry.NONE);
        }
        if (numTeams == 2 && random.nextFloat() < 0.75f) {
            terrainSymmetries.removeIf(symmetry2 -> {
                return !symmetry2.isPerfectSymmetry();
            });
        }
        return terrainSymmetries.get(random.nextInt(terrainSymmetries.size()));
    }

    public static SymmetrySettings getSymmetrySettings(RandomGenerator random, int spawnCount, int numTeams) {
        return getSymmetrySettingsFromTerrainSymmetry(random, getValidTerrainSymmetry(random, spawnCount, numTeams), spawnCount, numTeams);
    }

    public static SymmetrySettings getSymmetrySettingsFromTerrainSymmetry(RandomGenerator random, Symmetry terrainSymmetry, int spawnCount, int numTeams) {
        List<Symmetry> spawns;
        List<Symmetry> teams;
        Symmetry spawnSymmetry;
        Symmetry teamSymmetry;
        switch (terrainSymmetry) {
            case POINT2:
            case POINT3:
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
                spawns = new ArrayList<>(List.of((Object[]) new Symmetry[]{Symmetry.POINT2, Symmetry.POINT3, Symmetry.POINT4, Symmetry.POINT5, Symmetry.POINT6, Symmetry.POINT7, Symmetry.POINT8, Symmetry.POINT9, Symmetry.POINT10, Symmetry.POINT11, Symmetry.POINT12, Symmetry.POINT13, Symmetry.POINT14, Symmetry.POINT15, Symmetry.POINT16}));
                teams = new ArrayList<>(List.of((Object[]) new Symmetry[]{Symmetry.POINT2, Symmetry.POINT3, Symmetry.POINT4, Symmetry.POINT5, Symmetry.POINT6, Symmetry.POINT7, Symmetry.POINT8, Symmetry.POINT9, Symmetry.POINT10, Symmetry.POINT11, Symmetry.POINT12, Symmetry.POINT13, Symmetry.POINT14, Symmetry.POINT15, Symmetry.POINT16, Symmetry.XZ, Symmetry.ZX, Symmetry.X, Symmetry.Z, Symmetry.QUAD, Symmetry.DIAG}));
                break;
            case QUAD:
                spawns = new ArrayList<>(List.of(Symmetry.POINT2, Symmetry.QUAD));
                teams = new ArrayList<>(List.of(Symmetry.X, Symmetry.Z, Symmetry.QUAD));
                break;
            case DIAG:
                spawns = new ArrayList<>(List.of(Symmetry.POINT2, Symmetry.DIAG));
                teams = new ArrayList<>(List.of(Symmetry.XZ, Symmetry.ZX, Symmetry.DIAG));
                break;
            default:
                spawns = new ArrayList<>(List.of(terrainSymmetry));
                teams = new ArrayList<>(List.of(terrainSymmetry));
                break;
        }
        if (numTeams > 1) {
            spawns.removeIf(symmetry -> {
                int numSymPoints = symmetry.getNumSymPoints();
                return (numSymPoints % numTeams == 0 && spawnCount % numSymPoints == 0 && terrainSymmetry.getNumSymPoints() % numSymPoints == 0 && (symmetry.isPerfectSymmetry() || numSymPoints == numTeams)) ? false : true;
            });
            spawnSymmetry = spawns.get(random.nextInt(spawns.size()));
            teams.removeIf(symmetry2 -> {
                int numSymPoints = symmetry2.getNumSymPoints();
                int spawnNumSymPoints = spawnSymmetry.getNumSymPoints();
                return (numSymPoints % spawnNumSymPoints == 0 && numSymPoints % numTeams == 0 && numSymPoints <= spawnNumSymPoints) ? false : true;
            });
            teamSymmetry = teams.get(random.nextInt(teams.size()));
        } else {
            spawnSymmetry = Symmetry.NONE;
            teamSymmetry = Symmetry.NONE;
        }
        return new SymmetrySettings(terrainSymmetry, teamSymmetry, spawnSymmetry);
    }
}
