package com.faforever.neroxis.cli;

import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/VersionProvider.class */
public class VersionProvider implements CommandLine.IVersionProvider {
    public String[] getVersion() {
        String appVersion = System.getProperty("jpackage.app-version");
        if (appVersion != null) {
            return new String[]{appVersion};
        }
        String version = VersionProvider.class.getPackage().getImplementationVersion();
        return new String[]{version != null ? version : "snapshot"};
    }
}
