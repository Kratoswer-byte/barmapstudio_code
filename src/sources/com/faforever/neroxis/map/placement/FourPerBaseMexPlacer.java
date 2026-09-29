package com.faforever.neroxis.map.placement;

import com.faforever.neroxis.map.SCMap;
import java.util.random.RandomGenerator;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/map/placement/FourPerBaseMexPlacer.class */
public class FourPerBaseMexPlacer extends MexPlacer {
    public FourPerBaseMexPlacer(SCMap map, RandomGenerator.SplittableGenerator random) {
        super(map, random);
        this.minMexesPerPlayer = 4;
        this.maxMexesPerPlayer = 4;
    }
}
