package com.faforever.neroxis.generator.bar;

import java.nio.file.*;

/** Stampa le UV realmente usate dall'export, senza passare per il file .sdz. */
public class TestUvDirect {
    public static void main(String[] a) throws Exception {
        ObjModel3D m = ObjParser.parse(Path.of("test_textured/box.obj").toFile());
        System.out.println("A faceUVs[0] = " + java.util.Arrays.toString(m.getFaceUVs()[0]));
        float[][] base = m.getBaseUVs();
        System.out.println("B baseUVs[0] = " + (base == null ? "null" : java.util.Arrays.toString(base[0])));
        // replica della logica di buildAtlasForEntity
        int aw = 1024, ah = 512;
        java.awt.Rectangle r = new java.awt.Rectangle(0, 0, 512, 512);
        float[] src = (base != null && base[0] != null) ? base[0] : m.getFaceUVs()[0];
        float[] dst = new float[src.length];
        for (int k = 0; k < src.length / 2; k++) {
            dst[k * 2] = (r.x + src[k * 2] * r.width) / aw;
            dst[k * 2 + 1] = (r.y + src[k * 2 + 1] * r.height) / ah;
        }
        System.out.println("C remap[0]  = " + java.util.Arrays.toString(dst));
        // che UV finiscono nell'S3O?
        byte[] s3o = m.toS3OBytes("box", "t.tga", "t2.tga", 1.0f);
        S3OParser.S3OModel back = S3OParser.parseWithFaceUV(s3o);
        System.out.println("D s3o faceUVs[0] = " + java.util.Arrays.toString(back.model.getFaceUVs()[0]));
        System.exit(0);
    }
}
