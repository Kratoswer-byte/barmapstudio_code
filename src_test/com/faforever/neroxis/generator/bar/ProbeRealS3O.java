package com.faforever.neroxis.generator.bar;

import java.nio.file.*;

/** Probe diagnostico: confronta S3O reali di BAR con quelli esportati. */
public class ProbeRealS3O {
    public static void main(String[] a) throws Exception {
        Path data = Platform.defaultData();
        System.out.println("BAR data = " + data);
        String[] ids = {"armcom", "armpw", "armnanotct2", "cormex", "armmex"};
        for (String id : ids) {
            for (String cand : new String[]{"objects3d/units/" + id + ".s3o", "objects3d/" + id + ".s3o"}) {
                byte[] b = BarInstall.readGameBytes(data, cand);
                if (b == null) continue;
                S3OParser.S3OModel sm = S3OParser.parseWithFaceUV(b);
                System.out.println("== " + id + " (" + b.length + " bytes) tex1=" + sm.tex1 + " tex2=" + sm.tex2
                    + " faces=" + sm.model.getFaceCount() + " h=" + sm.height + " r=" + sm.radius);
                float[][] uv = sm.model.getFaceUVs();
                int shown = 0;
                for (int i = 0; i < uv.length && shown < 3; i++) {
                    if (uv[i] == null) continue;
                    System.out.println("   face" + i + " uv=" + java.util.Arrays.toString(uv[i]));
                    shown++;
                }
                break;
            }
        }
        System.exit(0);
    }
}
