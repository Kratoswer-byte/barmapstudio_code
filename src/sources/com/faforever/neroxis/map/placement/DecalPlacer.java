package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.Decal;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.SymmetryType;
import com.faforever.neroxis.mask.BooleanMask;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;
import java.util.List;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/DecalPlacer.class */
public class DecalPlacer {
    private final SCMap map;
    private final RandomGenerator.SplittableGenerator random;

    public DecalPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        this.map = map;
        this.random = random.split();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void placeDecals(BooleanMask spawnMask, List<String> paths, float minSeparation, float maxSeparation, float minScale, float maxScale) {
        if (!paths.isEmpty()) {
            BooleanMask spawnMaskCopy = (BooleanMask) spawnMask.copy();
            spawnMaskCopy.limitToSymmetryRegion();
            spawnMaskCopy.getRandomCoordinates(minSeparation, maxSeparation).stream().map((v0) -> {
                return v0.roundToNearestHalfPoint();
            }).forEach(location -> {
                float scale = (this.random.nextFloat() * (maxScale - minScale)) + minScale;
                Vector3 rotation = new Vector3(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, this.random.nextFloat() * 3.1415927f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                Decal decal = new Decal((String) paths.get(this.random.nextInt(paths.size())), location.roundToNearestHalfPoint(), rotation, scale, 1000.0f);
                this.map.addDecal(decal);
                List<Vector2> symmetryPoints = spawnMask.getSymmetryPoints(decal.getPosition(), SymmetryType.SPAWN).stream().map((v0) -> {
                    return v0.roundToNearestHalfPoint();
                }).toList();
                List<Float> symmetryRotation = spawnMask.getSymmetryRotations(decal.getRotation().y());
                for (int i = 0; i < symmetryPoints.size(); i++) {
                    Vector3 symVectorRotation = new Vector3(decal.getRotation().x(), symmetryRotation.get(i).floatValue(), decal.getRotation().z());
                    Decal symDecal = new Decal(decal.getPath(), symmetryPoints.get(i), symVectorRotation, scale, decal.getCutOffLOD());
                    this.map.addDecal(symDecal);
                }
            });
        }
    }
}
