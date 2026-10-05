package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.Symmetry;

public class TestGround {
    public static void main(String[] a) {
        try {
            System.out.println("generating...");
            GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
                256, 256, TerrainStyle.VALLEY, "FRITHEN", Symmetry.POINT2, 2, 2, 123L);
            System.out.println("OK map=" + (res.map() != null) + " preview=" + (res.preview() != null));
        } catch (Throwable t) {
            System.out.println("FAIL: " + t);
            t.printStackTrace(System.out);
        }
        System.exit(0);
    }
}
