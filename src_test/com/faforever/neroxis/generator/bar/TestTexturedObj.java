package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.nio.file.*;

public class TestTexturedObj {
    public static void main(String[] a) throws Exception {
        Path dir = Path.of("test_textured");
        Files.createDirectories(dir);
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) {
            boolean chk = ((x / 8) + (y / 8)) % 2 == 0;
            img.setRGB(x, y, chk ? 0xFFFF3030 : 0xFF3030FF);
        }
        javax.imageio.ImageIO.write(img, "png", dir.resolve("checker.png").toFile());
        // Box con facce CCW viste da fuori (come un OBJ esportato da Blender)
        String obj = "mtllib mat.mtl\n"
            + "v -8 0 -8\nv 8 0 -8\nv 8 16 -8\nv -8 16 -8\n"      // back  z=-8
            + "v -8 0 8\nv 8 0 8\nv 8 16 8\nv -8 16 8\n"          // front z=+8
            + "vt 0 0\nvt 1 0\nvt 1 1\nvt 0 1\n"
            + "usemtl texm\n"
            + "f 5/1 6/2 7/3 8/4\n"   // front, CCW da +z
            + "f 1/1 4/2 3/3 2/4\n"   // back,  CCW da -z
            + "f 4/1 8/2 7/3 3/4\n"   // top
            + "f 1/1 2/2 6/3 5/4\n";  // bottom
        Files.writeString(dir.resolve("box.obj"), obj);
        Files.writeString(dir.resolve("mat.mtl"), "newmtl texm\nKd 1 1 1\nmap_Kd checker.png\n");

        ObjModel3D m = ObjParser.parse(dir.resolve("box.obj").toFile());
        System.out.println("faces=" + m.getFaceCount() + " mats=" + m.getMaterials().keySet()
            + " hasDiffuse=" + m.hasAnyDiffuse() + " baked=" + (m.getBakedTexture() != null));
        BufferedImage d = m.firstDiffuse();
        System.out.println("firstDiffuse=" + (d == null ? "null" : d.getWidth() + "x" + d.getHeight()));
        System.out.println("face0 UV=" + java.util.Arrays.toString(m.getFaceUVs()[0]));

        SoftRenderer3D r = new SoftRenderer3D(640, 480);
        r.clear(0x0B0E14);
        SoftRenderer3D.Camera c = new SoftRenderer3D.Camera();
        c.tx = 0; c.ty = 8; c.tz = 0; c.dist = 60;
        SoftRenderer3D.RenderMesh rm = SoftRenderer3D.RenderMesh.fromModel(m);
        SoftRenderer3D.Texture t = m.firstDiffuse() != null ? new SoftRenderer3D.Texture(m.firstDiffuse()) : null;
        int n = r.drawMesh(rm, t, 0x808080, 0, 0, 0, 0.6, 1.0, c);
        javax.imageio.ImageIO.write(r.present(), "png", new java.io.File("test_textured_maps.png"));
        System.out.println("MAPS render tris=" + n + " tex=" + (t != null));
        System.exit(0);
    }
}
