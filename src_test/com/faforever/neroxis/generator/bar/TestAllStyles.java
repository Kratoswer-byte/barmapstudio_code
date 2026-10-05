package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.Symmetry;

public class TestAllStyles {
    public static void main(String[] a) {
        String[] styles = {"VALLEY","LAND_BRIDGE","MOUNTAIN_RANGE","BIG_ISLANDS","SMALL_ISLANDS",
            "CENTER_LAKE","FLOODED","DROP_PLATEAU","LITTLE_MOUNTAIN","MULTILEVEL_LAST",
            "ONE_ISLAND","RIVERS","RIVERS_AND_OCEANS","FRACTAL_LAND","FRACTAL_PLATEAU",
            "FRACTAL_NAVY","SETONS","BASIC"};
        for (String s : styles) {
            try {
                System.out.println("TRY " + s);
                GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
                    256, 256, TerrainStyle.valueOf(s), "FRITHEN", Symmetry.POINT2, 2, 2, 777L);
                System.out.println("OK " + s);
            } catch (Throwable t) {
                System.out.println("FAIL " + s + " : " + t.getMessage());
            }
        }
        System.exit(0);
    }
}
