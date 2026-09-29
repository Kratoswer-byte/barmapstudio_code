package com.faforever.neroxis.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import picocli.CommandLine;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/cli/MapFolderConverter.class */
public class MapFolderConverter implements CommandLine.ITypeConverter<Path> {
    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$TypeConversionException */
    /* JADX INFO: renamed from: convert, reason: merged with bridge method [inline-methods] */
    public Path m4convert(String value) throws CommandLine.TypeConversionException {
        Path path = Path.of(value, new String[0]);
        checkReadablePath(path);
        File mapFolder = path.toFile();
        if (!Files.isDirectory(path, new LinkOption[0])) {
            throw new CommandLine.TypeConversionException(String.format("%s is not a directory", path));
        }
        File[] files = mapFolder.listFiles(this::isRequiredMapFile);
        if (files == null) {
            throw new CommandLine.TypeConversionException(String.format("%s cannot be read", path));
        }
        if (Arrays.stream(files).noneMatch(file -> {
            return file.getName().endsWith(".scmap");
        })) {
            throw new CommandLine.TypeConversionException(String.format("%s does not contain an scmap file", path));
        }
        if (Arrays.stream(files).noneMatch(file2 -> {
            return file2.getName().endsWith("_scenario.lua");
        })) {
            throw new CommandLine.TypeConversionException(String.format("%s does not contain a scenario file", path));
        }
        if (Arrays.stream(files).noneMatch(file3 -> {
            return file3.getName().endsWith("_save.lua");
        })) {
            throw new CommandLine.TypeConversionException(String.format("%s does not contain a save file", path));
        }
        if (Arrays.stream(files).noneMatch(file4 -> {
            return file4.getName().endsWith("_script.lua");
        })) {
            throw new CommandLine.TypeConversionException(String.format("%s does not contain a script file", path));
        }
        for (File file5 : files) {
            if (!file5.canRead()) {
                throw new CommandLine.TypeConversionException(String.format("%s cannot be read", file5.getPath()));
            }
        }
        return path;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: picocli.CommandLine$TypeConversionException */
    public void checkReadablePath(Path path) throws CommandLine.TypeConversionException {
        if (!Files.exists(path, new LinkOption[0])) {
            throw new CommandLine.TypeConversionException(String.format("%s does not exist", path));
        }
        if (!Files.isReadable(path)) {
            throw new CommandLine.TypeConversionException(String.format("%s cannot be read", path));
        }
    }

    private boolean isRequiredMapFile(File file) {
        String filename = file.getName();
        return filename.endsWith(".scmap") || filename.endsWith("_scenario.lua") || filename.endsWith("_save.lua") || filename.endsWith("_script.lua");
    }
}
