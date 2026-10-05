package com.faforever.neroxis.generator.bar;

import java.nio.file.*;

public class TestParseUv {
    public static void main(String[] a) throws Exception {
        ObjModel3D m = ObjParser.parse(Path.of("test_textured/box.obj").toFile());
        System.out.println("faces=" + m.getFaceCount() + " mats=" + m.getMaterials().keySet()
            + " hasDiffuse=" + m.hasAnyDiffuse() + " colors=" + (m.getMaterialColors() == null ? "null" : m.getMaterialColors().keySet()));
        float[][] uv = m.getFaceUVs();
        for (int i = 0; i < uv.length; i++) System.out.println("parse face" + i + " uv=" + java.util.Arrays.toString(uv[i]));
        // atlante
        java.lang.reflect.Method bm = com.faforever.neroxis.exporter.bar.BarMapExporter.class
            .getDeclaredMethod("buildAtlasForEntity", ObjectEntity.class, ObjModel3D.class);
        bm.setAccessible(true);
        // serve un'istanza: l'helper e' privato d'istanza -> uso il percorso pubblico via export, qui invece ispeziono la regola UV
        ObjectEntity e = new ObjectEntity("custom_box", "Box", ObjectEntity.Category.CUSTOM_MESH, "Custom",
            3, 3, 1000, 200, 0, "x", m, "");
        e.setObjFilePath(Path.of("test_textured/box.obj"));
        System.out.println("entity hasDiffuse=" + e.getModel3D().hasAnyDiffuse());
        System.exit(0);
    }
}
