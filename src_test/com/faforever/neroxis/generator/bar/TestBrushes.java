package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.brushes.Brushes;
import com.faforever.neroxis.mask.FloatMask;
import java.util.*;

public class TestBrushes {
    public static void main(String[] a) {
        Set<String> all = new LinkedHashSet<>();
        all.addAll(Brushes.MOUNTAIN_BRUSHES);
        all.addAll(Brushes.HILL_BRUSHES);
        all.addAll(Brushes.GENERATOR_BRUSHES);
        all.addAll(Brushes.CLEAN_MOUNTAIN_BRUSHES);
        all.add("island.png");
        boolean fail = false;
        for (String b : all) {
            try {
                FloatMask m = Brushes.loadBrush(b);
                System.out.println("OK " + b + " max=" + m.getMax());
            } catch (Throwable t) {
                fail = true;
                System.out.println("FAIL " + b + " : " + t.getMessage());
            }
        }
        System.out.println(fail ? "RESULT: FAILURES" : "RESULT: ALL OK");
        System.exit(0);
    }
}
