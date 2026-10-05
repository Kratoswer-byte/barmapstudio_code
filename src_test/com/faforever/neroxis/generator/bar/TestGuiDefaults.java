package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.Symmetry;

public class TestGuiDefaults {
    public static void main(String[] a) {
        Object[][] cases = {
            {512, 512, "VALLEY", "DESERT", Symmetry.POINT2, 42L},
            {256, 384, "VALLEY", "DESERT", Symmetry.POINT2, 42L},
            {384, 256, "MOUNTAIN_RANGE", "FRITHEN", Symmetry.X, 42L},
            {768, 768, "SETONS", "DESERT", Symmetry.QUAD, 42L},
            {512, 384, "RIVERS", "DESERT", Symmetry.Z, 42L},
        };
        for (Object[] c : cases) {
            try {
                System.out.println("TRY " + c[0] + "x" + c[1] + " " + c[2] + " " + c[3] + " " + c[4]);
                GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
                    (int) c[0], (int) c[1], TerrainStyle.valueOf((String) c[2]),
                    (String) c[3], (Symmetry) c[4], 2, 2, (long) c[5]);
                System.out.println("OK " + c[0] + "x" + c[1]);
            } catch (Throwable t) {
                System.out.println("FAIL " + c[0] + "x" + c[1] + " : " + t);
            }
        }
        System.exit(0);
    }
}
