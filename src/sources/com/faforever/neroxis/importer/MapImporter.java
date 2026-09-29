package com.faforever.neroxis.importer;

import com.faforever.neroxis.map.SCMap;
import java.io.IOException;
import java.nio.file.Path;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/importer/MapImporter.class */
public class MapImporter {
    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static SCMap importMap(Path folderPath) throws IOException, MatchException {
        SCMap map = SCMapImporter.importSCMAP(folderPath);
        map.setFolderName(folderPath.getName(folderPath.getNameCount() - 1).toString());
        SaveImporter.importSave(folderPath, map);
        ScenarioImporter.importScenario(folderPath, map);
        ScriptImporter.importScript(folderPath, map);
        return map;
    }
}
