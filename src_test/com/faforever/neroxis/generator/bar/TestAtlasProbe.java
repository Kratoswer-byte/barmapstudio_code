package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.zip.*;

public class TestAtlasProbe {
    public static void main(String[] a) throws Exception {
        Path sdz = Path.of("test_export_tex/map_textest_1.0.sdz");
        try (ZipFile zf = new ZipFile(sdz.toFile())) {
            byte[] tga = zf.getInputStream(zf.getEntry("unittextures/custom_box_tex1.tga")).readAllBytes();
            BufferedImage t = ModelTextureCache.readTga(tga);
            System.out.println("tex1 decoded " + t.getWidth() + "x" + t.getHeight());
            for (int[] uv : new int[][]{{10,10},{256,256},{512,256},{600,300},{768,256},{1000,500},{520,260}}) {
                System.out.println("  px(" + uv[0] + "," + uv[1] + ")=" + Integer.toHexString(t.getRGB(uv[0], uv[1])));
            }
            byte[] s3o = zf.getInputStream(zf.getEntry("objects3d/custom_box.s3o")).readAllBytes();
            S3OParser.S3OModel sm = S3OParser.parseWithFaceUV(s3o);
            float[][] uv = sm.model.getFaceUVs();
            for (int i = 0; i < uv.length; i++) {
                System.out.println("face" + i + " uv=" + java.util.Arrays.toString(uv[i]));
            }
            // colori ai vertici delle UV
            for (int i = 0; i < uv.length; i++) {
                if (uv[i] == null) continue;
                StringBuilder sb = new StringBuilder("  face" + i + " colors:");
                for (int k = 0; k + 1 < uv[i].length; k += 2) {
                    int x = Math.min(t.getWidth() - 1, Math.max(0, (int) (uv[i][k] * t.getWidth())));
                    int y = Math.min(t.getHeight() - 1, Math.max(0, (int) (uv[i][k + 1] * t.getHeight())));
                    sb.append(" ").append(Integer.toHexString(t.getRGB(x, y)));
                }
                System.out.println(sb);
            }
        }
        System.exit(0);
    }
}
