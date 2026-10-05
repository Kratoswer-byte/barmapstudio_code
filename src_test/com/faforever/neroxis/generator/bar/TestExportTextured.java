package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.bar.BarExportOptions;
import com.faforever.neroxis.exporter.bar.BarMapExporter;
import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.util.vector.Vector2;
import java.nio.file.*;
import java.util.*;

public class TestExportTextured {
    public static void main(String[] a) throws Exception {
        GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
            256, 256, TerrainStyle.VALLEY, "FRITHEN", Symmetry.POINT2, 2, 2, 777L);
        // OBJ textured (map_Kd) importato come nello Studio
        Path obj = Path.of("test_textured/box.obj");
        ObjectEntity e1 = new ObjectEntity("custom_box", "Box", ObjectEntity.Category.CUSTOM_MESH,
            "Custom", 3, 3, 1000, 200, 0, "obj texture", ObjParser.parse(obj.toFile()), "");
        e1.setObjFilePath(obj);
        res.map().getCustomEntities().add(new PlacedCustomEntity(e1, new Vector2(100, 100), 0f));
        // Little Man (solo Kd, nessuna immagine)
        Path lm = Path.of("Little Man/model.obj");
        ObjectEntity e2 = new ObjectEntity("custom_littleman", "Little Man", ObjectEntity.Category.CUSTOM_MESH,
            "Custom", 3, 3, 1000, 200, 0, "obj palette", ObjParser.parse(lm.toFile()), "");
        e2.setObjFilePath(lm);
        res.map().getCustomEntities().add(new PlacedCustomEntity(e2, new Vector2(150, 120), 0f));

        Path out = Path.of("test_export_tex");
        Files.createDirectories(out);
        BarExportOptions opt = new BarExportOptions(2, 0.7f, 2.0f, 2.0f, 3.5f, false, 2,
            "Map_TexTest", "1.0", "Test", 1.0f, null, 3, 16, 18);
        Path archive = BarMapExporter.exportMap(out, res.map(), opt, out.resolve("_neroxis_skirmish.txt"));
        System.out.println("EXPORT OK -> " + archive);
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(archive.toFile())) {
            List<String> names = new ArrayList<>();
            zf.stream().forEach(x -> names.add(x.getName()));
            for (String n : names) if (n.startsWith("unittextures/") || n.endsWith(".s3o")) System.out.println("  " + n);
            // rileggi l'S3O esportato e renderizzalo con la sua texture
            for (String id : new String[]{"custom_box", "custom_littleman"}) {
                byte[] s3o = zf.getInputStream(zf.getEntry("objects3d/" + id + ".s3o")).readAllBytes();
                S3OParser.S3OModel sm = S3OParser.parseWithFaceUV(s3o);
                System.out.println(id + " riletto faces=" + sm.model.getFaceCount() + " tex1=" + sm.tex1);
                byte[] tga = zf.getInputStream(zf.getEntry("unittextures/" + sm.tex1)).readAllBytes();
                java.awt.image.BufferedImage timg = ModelTextureCache.readTga(tga);
                System.out.println("   tex1 decoded=" + (timg == null ? "null" : timg.getWidth() + "x" + timg.getHeight()));
                SoftRenderer3D r = new SoftRenderer3D(400, 300);
                r.clear(0x0B0E14);
                SoftRenderer3D.Camera c = new SoftRenderer3D.Camera();
                float h = sm.height > 0 ? sm.height : 16;
                c.tx = 0; c.ty = h * 0.5; c.tz = 0; c.dist = h * 4;
                SoftRenderer3D.RenderMesh rm = SoftRenderer3D.RenderMesh.fromModel(sm.model);
                int n = r.drawMesh(rm, timg != null ? new SoftRenderer3D.Texture(timg) : null, 0x808080, 0, 0, 0, 0.6, 1.0, c);
                javax.imageio.ImageIO.write(r.present(), "png", new java.io.File("test_export_" + id + ".png"));
                System.out.println("   render tris=" + n);
            }
        }
        System.exit(0);
    }
}
