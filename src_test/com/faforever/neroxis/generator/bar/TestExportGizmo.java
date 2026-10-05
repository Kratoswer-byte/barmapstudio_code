package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.bar.BarExportOptions;
import com.faforever.neroxis.exporter.bar.BarMapExporter;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;

public class TestExportGizmo {
    public static void main(String[] a) throws Exception {
        ObjModel3D arm = OfficialModels.load("armcom");
        System.out.println("official armcom faces=" + (arm == null ? "null" : arm.getFaceCount()));
        GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
            256, 256, TerrainStyle.VALLEY, "FRITHEN", Symmetry.POINT2, 2, 2, 777L);
        // entita 1: mulino procedurale (no materiali -> default_mat)
        res.map().getCustomEntities().add(new PlacedCustomEntity(
            ObjectEntity.createDefaultWindmill(), new Vector2(100, 100), 0.5f));
        // entita 2: modello a 2 materiali con diffuse -> atlante tex1
        ObjModel3D two = twoMatModel();
        ObjectEntity custom = new ObjectEntity("test_twomat", "Test TwoMat",
            ObjectEntity.Category.CUSTOM_MESH, "Neutral", 2, 2, 100, 0, 0,
            "test", two, "");
        res.map().getCustomEntities().add(new PlacedCustomEntity(custom, new Vector2(150, 120), 0f));
        Path out = Path.of("test_export_out");
        Files.createDirectories(out);
        BarExportOptions opt = new BarExportOptions(2, 0.7f, 2.0f, 2.0f, 3.5f, false, 2,
            "Map_TestGizmo", "1.0", "Test", 1.0f, null, 3, 16, 18);
        Path archive = BarMapExporter.exportMap(out, res.map(), opt, out.resolve("_neroxis_skirmish.txt"));
        System.out.println("EXPORT OK -> " + archive + " bytes=" + Files.size(archive));
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(archive.toFile())) {
            List<String> names = new ArrayList<>();
            zf.stream().forEach(e -> names.add(e.getName()));
            System.out.println("has windmill s3o=" + names.contains("objects3d/custom_windmill_01.s3o"));
            System.out.println("has twomat tex1=" + names.contains("unittextures/test_twomat_tex1.tga"));
            System.out.println("has twomat tex2=" + names.contains("unittextures/test_twomat_tex2.tga"));
            System.out.println("has spawner=" + names.contains("luarules/gadgets/map_custom_spawner.lua"));
        }
        System.exit(0);
    }

    static ObjModel3D twoMatModel() {
        float[] v = {-8,0,-8, 8,0,-8, 8,12,-8, -8,12,-8, -8,0,8, 8,0,8, 8,12,8, -8,12,8};
        int[][] f = {{0,1,2,3}, {4,5,6,7}};
        float[][] n = {{0,0,-1}, {0,0,1}};
        int[] c = {0xFF0000, 0x0000FF};
        Map<String, float[]> mc = new LinkedHashMap<>();
        mc.put("m1", new float[]{1, 0, 0});
        mc.put("m2", new float[]{0, 0, 1});
        String[] fm = {"m1", "m2"};
        ObjModel3D m = new ObjModel3D(v, f, n, c, null, mc, fm);
        BufferedImage red = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        BufferedImage blu = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 32; y++) for (int x = 0; x < 32; x++) {
            red.setRGB(x, y, 0xFFFF2020); blu.setRGB(x, y, 0xFF2020FF);
        }
        m.setMaterialDiffuse("m1", red);
        m.setMaterialDiffuse("m2", blu);
        return m;
    }
}
