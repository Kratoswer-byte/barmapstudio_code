package com.faforever.neroxis.generator.bar;

import java.nio.file.Path;

/**
 * Accesso pubblico ai modelli ufficiali BAR (S3O+DDS dal gioco installato).
 * Usato sia dalla vista 3D che dall'esportatore per le varianti scalate
 * (unita ufficiali modificate diventano nuovi tipi con S3O proprio).
 */
public final class OfficialModels {
    private OfficialModels() {}

    public static S3OParser.S3OModel loadFull(String unitId) {
        if (unitId == null || unitId.isBlank()) return null;
        String id = unitId.toLowerCase(java.util.Locale.ROOT);
        Path data;
        try {
            data = Platform.defaultData();
        } catch (Exception ex) { return null; }
        if (data == null) return null;
        String[] cands = {"objects3d/units/" + id + ".s3o", "objects3d/" + id + ".s3o"};
        for (String c : cands) {
            try {
                byte[] bytes = BarInstall.readGameBytes(data, c);
                if (bytes == null) continue;
                S3OParser.S3OModel sm = S3OParser.parseWithFaceUV(bytes);
                if (sm != null && sm.model.getFaceCount() > 0) return sm;
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static ObjModel3D load(String unitId) {
        S3OParser.S3OModel sm = loadFull(unitId);
        return sm != null ? sm.model : null;
    }
}
