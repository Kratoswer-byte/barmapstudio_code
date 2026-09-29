package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/PropPlacer.class */
public class PropPlacer {
    private final SCMap map;
    private final RandomGenerator.SplittableGenerator random;

    public PropPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        this.map = map;
        this.random = random.split();
    }

    public void placeProps(BooleanMask spawnMask, List<String> paths, float separation, boolean isBoulder) {
        placeProps(spawnMask, paths, separation, separation, isBoulder);
    }

    public void placeProps(BooleanMask spawnMask, List<String> paths, float minSeparation, float maxSeparation, boolean isBoulder) {
        if (!paths.isEmpty()) {
            spawnMask.limitToSymmetryRegion();
            List<Vector2> coordinates = spawnMask.getRandomCoordinates(minSeparation, maxSeparation);
            coordinates.stream().map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).forEach(location -> {
                Prop prop = new Prop((String) paths.get(this.random.nextInt(paths.size())), location, this.random.nextFloat() * 3.1415927f, isBoulder);
                this.map.addProp(prop);
                List<Vector2> symmetryPoints = spawnMask.getSymmetryPoints(prop.getPosition(), SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                List<Float> symmetryRotation = spawnMask.getSymmetryRotations(prop.getRotation());
                for (int i = 0; i < symmetryPoints.size(); i++) {
                    Prop symProp = new Prop(prop.getPath(), symmetryPoints.get(i), symmetryRotation.get(i).floatValue(), isBoulder);
                    this.map.addProp(symProp);
                }
            });
        }
    }
}
