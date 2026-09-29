package com.faforever.neroxis.importer;

import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/importer/ScriptImporter.class */
public class ScriptImporter {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ScriptImporter.class.desiredAssertionStatus();
    }

    public static void importScript(Path folderPath, SCMap map) throws IOException {
        File dir = folderPath.toFile();
        File[] mapFiles = dir.listFiles((file, filename) -> {
            return filename.endsWith("_script.lua");
        });
        if (!$assertionsDisabled && mapFiles == null) {
            throw new AssertionError();
        }
        if (mapFiles.length == 0) {
            System.out.println("No script file in map folder");
        } else {
            Path scriptPath = mapFiles[0].toPath();
            map.setScript(FileUtil.readFile(scriptPath.toString()));
        }
    }
}
