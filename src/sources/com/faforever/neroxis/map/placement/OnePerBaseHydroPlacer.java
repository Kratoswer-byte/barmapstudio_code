package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.mask.BooleanMask;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/OnePerBaseHydroPlacer.class */
public class OnePerBaseHydroPlacer extends HydroPlacer {
    public OnePerBaseHydroPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        super(map, random);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.faforever.neroxis.map.placement.HydroPlacer
    public void placeHydros(int hydroCount, BooleanMask allowedHydroMask) {
        if (hydroCount < this.map.getSpawnCount()) {
            super.placeHydros(hydroCount, allowedHydroMask);
            return;
        }
        this.map.getHydros().clear();
        if (!allowedHydroMask.getSymmetrySettings().spawnSymmetry().isPerfectSymmetry()) {
            allowedHydroMask.limitToCenteredCircle(allowedHydroMask.getSize() / 2.0f);
        }
        ((BooleanMask) allowedHydroMask.fillCenter(64, false)).limitToSymmetryRegion();
        this.map.getMexes().stream().filter(mex -> {
            return allowedHydroMask.inTeam(mex.getPosition(), false);
        }).forEach(mex2 -> {
            allowedHydroMask.fillCircle(mex2.getPosition(), 10.0f, false);
        });
        int numSymPoints = 0;
        while (true) {
            int i = numSymPoints;
            if (i < this.map.getSpawnCount()) {
                Spawn spawn = this.map.getSpawn(i);
                BooleanMask spawnHydroMask = new BooleanMask(allowedHydroMask.getSize(), this.random.split(), allowedHydroMask.getSymmetrySettings());
                ((BooleanMask) ((BooleanMask) spawnHydroMask.fillCircle(spawn.getPosition(), 25.0f, true)).fillCircle(spawn.getPosition(), 7.0f, false)).multiply(allowedHydroMask);
                placeIndividualHydros(spawnHydroMask, allowedHydroMask, 1, this.hydroSpacing);
                numSymPoints = i + allowedHydroMask.getSymmetrySettings().spawnSymmetry().getNumSymPoints();
            } else {
                return;
            }
        }
    }
}
