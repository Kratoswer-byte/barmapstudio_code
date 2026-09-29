package com.faforever.neroxis.exporter;

import com.faforever.neroxis.map.SCMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/exporter/MapExporter.class */
public class MapExporter {
    public static void exportMap(Path folderPath, SCMap map, boolean exportPreview) {
        try {
            Path mapPath = folderPath.resolve(map.getFolderName());
            Files.createDirectories(mapPath, new FileAttribute[0]);
            if (map.getTerrainShaderPath().equals(SCMap.LEGACY_SHADER_NAME)) {
                if (map.getCompressedNormal() != null) {
                    SCMapExporter.exportNormals(mapPath, map);
                }
                if (map.getCompressedShadows() != null) {
                    SCMapExporter.exportShadows(mapPath, map);
                }
            } else if (map.getTerrainShaderPath().equals(SCMap.PBR_SHADER_NAME)) {
                SCMapExporter.exportMapInfoTexture(folderPath.resolve(map.getFolderName()), map);
                SCMapExporter.exportMapNormalTexture(folderPath.resolve(map.getFolderName()), map);
                SCMapExporter.exportPBR(folderPath.resolve(map.getFolderName()), map);
            }
            if (exportPreview) {
                SCMapExporter.exportPreview(mapPath, map);
            }
            SCMapExporter.exportSCMAP(mapPath, map);
            SaveExporter.exportSave(mapPath, map);
            ScenarioExporter.exportScenario(mapPath, map);
            ScriptExporter.exportScript(mapPath, map);
        } catch (IOException e) {
            System.err.println("Error while saving the map.");
            e.printStackTrace();
        }
    }
}
