package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.util.dds.DDSHeader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarInstall.class */
final class BarInstall {
    private static final int SMF_HEADER = 80;
    private static final int PREVIEW_SIZE = 1024;
    private static final AtomicBoolean refreshing = new AtomicBoolean();
    static final int DEFAULT_HOST_PORT = 8452;

    private BarInstall() {
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarInstall$InstalledMap.class */
    static final class InstalledMap extends Record {
        private final Path archive;
        private final String name;
        private final String description;
        private final String author;
        private final String mapFile;

        InstalledMap(Path archive, String name, String description, String author, String mapFile) {
            this.archive = archive;
            this.name = name;
            this.description = description;
            this.author = author;
            this.mapFile = mapFile;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, InstalledMap.class), InstalledMap.class, "archive;name;description;author;mapFile", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->archive:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->mapFile:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, InstalledMap.class), InstalledMap.class, "archive;name;description;author;mapFile", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->archive:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->mapFile:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, InstalledMap.class, Object.class), InstalledMap.class, "archive;name;description;author;mapFile", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->archive:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$InstalledMap;->mapFile:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Path archive() {
            return this.archive;
        }

        public String name() {
            return this.name;
        }

        public String description() {
            return this.description;
        }

        public String author() {
            return this.author;
        }

        public String mapFile() {
            return this.mapFile;
        }
    }

    static List<InstalledMap> listMaps(Path data) {
        List<InstalledMap> maps = new ArrayList<>();
        try {
            Stream<Path> files = Files.list(data.resolve("cache"));
            try {
                Path cache = files.filter(path -> {
                    return path.getFileName().toString().matches("ArchiveCache\\d*\\.lua");
                }).max(Comparator.comparingLong(path2 -> {
                    return path2.toFile().lastModified();
                })).orElse(null);
                if (files != null) {
                    files.close();
                }
                if (cache == null) {
                    return maps;
                }
                try {
                    Stream<String> lines = Files.lines(cache, StandardCharsets.ISO_8859_1);
                    try {
                        String[] file = {null};
                        Map<String, String> values = new HashMap<>();
                        Pattern entry = Pattern.compile("^(\\t+)(\\w+) = \"?(.*?)\"?,$");
                        lines.forEach(line -> {
                            Matcher matcher = entry.matcher(line);
                            if (!matcher.find()) {
                                return;
                            }
                            int depth = matcher.group(1).length();
                            if (depth == 3 && matcher.group(2).equals("name")) {
                                addMap(maps, data, file[0], values);
                                file[0] = matcher.group(3);
                                values.clear();
                            } else if (depth == 4) {
                                values.put(matcher.group(2), matcher.group(3));
                            }
                        });
                        addMap(maps, data, file[0], values);
                        if (lines != null) {
                            lines.close();
                        }
                        maps.sort(Comparator.comparing(map -> {
                            return map.name().toLowerCase(Locale.ROOT);
                        }));
                        return maps;
                    } catch (Throwable th) {
                        if (lines != null) {
                            try {
                                lines.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (IOException | RuntimeException e) {
                    return maps;
                }
            } catch (Throwable th3) {
                if (files != null) {
                    try {
                        files.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (IOException e2) {
            return maps;
        }
    }

    private static void addMap(List<InstalledMap> maps, Path data, String file, Map<String, String> values) {
        if (file == null || !"3".equals(values.get("modtype")) || values.get("name") == null || values.get("mapfile") == null) {
            return;
        }
        Path archive = data.resolve("maps").resolve(file);
        if (Files.isRegularFile(archive, new LinkOption[0])) {
            maps.add(new InstalledMap(archive, values.get("name"), values.getOrDefault("description", ""), values.getOrDefault("author", ""), values.get("mapfile")));
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarInstall$Marks.class */
    static final class Marks extends Record {
        private final List<float[]> metal;
        private final List<float[]> geos;
        static final Marks NONE = new Marks(List.of(), List.of());

        Marks(List<float[]> metal, List<float[]> geos) {
            this.metal = metal;
            this.geos = geos;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Marks.class), Marks.class, "metal;geos", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$Marks;->metal:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$Marks;->geos:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Marks.class), Marks.class, "metal;geos", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$Marks;->metal:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$Marks;->geos:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Marks.class, Object.class), Marks.class, "metal;geos", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$Marks;->metal:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarInstall$Marks;->geos:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public List<float[]> metal() {
            return this.metal;
        }

        public List<float[]> geos() {
            return this.geos;
        }
    }

    static Marks readMarks(Path data, Path cacheFolder, InstalledMap map) {
        Path cached = cacheFolder.resolve(String.valueOf(map.archive().getFileName()) + ".marks");
        try {
            if (Files.isRegularFile(cached, new LinkOption[0]) && Files.getLastModifiedTime(cached, new LinkOption[0]).compareTo(Files.getLastModifiedTime(map.archive(), new LinkOption[0])) >= 0) {
                List<float[]> metal = new ArrayList<>();
                List<float[]> geos = new ArrayList<>();
                for (String line : Files.readAllLines(cached)) {
                    String[] fields = line.split(" ");
                    if (fields.length == 3) {
                        (fields[0].equals("m") ? metal : geos).add(new float[]{Float.parseFloat(fields[1]), Float.parseFloat(fields[2])});
                    }
                }
                return new Marks(metal, geos);
            }
        } catch (IOException | RuntimeException e) {
        }
        Marks marks = Marks.NONE;
        String fileName = map.archive().getFileName().toString().toLowerCase(Locale.ROOT);
        try {
            if (fileName.endsWith(".sdz")) {
                ZipFile zip = new ZipFile(map.archive().toFile());
                try {
                    ZipEntry smf = zip.getEntry(map.mapFile());
                    if (smf != null) {
                        InputStream stream = zip.getInputStream(smf);
                        try {
                            marks = parseMarks(stream.readAllBytes());
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    }
                    zip.close();
                } catch (Throwable th3) {
                    try {
                        zip.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
            } else {
                Path sevenZip = sevenZip(data);
                if (sevenZip != null) {
                    Process process = new ProcessBuilder(sevenZip.toString(), "e", "-so", map.archive().toString(), map.mapFile()).redirectError(ProcessBuilder.Redirect.DISCARD).start();
                    try {
                        InputStream stream2 = process.getInputStream();
                        try {
                            marks = parseMarks(stream2.readAllBytes());
                            if (stream2 != null) {
                                stream2.close();
                            }
                            process.destroy();
                        } catch (Throwable th5) {
                            if (stream2 != null) {
                                try {
                                    stream2.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                            }
                            throw th5;
                        }
                    } catch (Throwable th7) {
                        process.destroy();
                        throw th7;
                    }
                }
            }
            Files.createDirectories(cacheFolder, new FileAttribute[0]);
            List<String> lines = new ArrayList<>();
            marks.metal().forEach(spot -> {
                lines.add(String.format(Locale.ROOT, "m %.4f %.4f", Float.valueOf(spot[0]), Float.valueOf(spot[1])));
            });
            marks.geos().forEach(geo -> {
                lines.add(String.format(Locale.ROOT, "g %.4f %.4f", Float.valueOf(geo[0]), Float.valueOf(geo[1])));
            });
            Files.write(cached, lines, new OpenOption[0]);
            return marks;
        } catch (IOException | OutOfMemoryError | RuntimeException e2) {
            return Marks.NONE;
        }
    }

    private static Marks parseMarks(byte[] smf) {
        if (smf.length < SMF_HEADER) {
            return Marks.NONE;
        }
        ByteBuffer buffer = ByteBuffer.wrap(smf).order(ByteOrder.LITTLE_ENDIAN);
        int mapX = buffer.getInt(24);
        int mapY = buffer.getInt(28);
        int metalOffset = buffer.getInt(68);
        int featureOffset = buffer.getInt(72);
        int width = mapX / 2;
        int height = mapY / 2;
        List<float[]> metal = new ArrayList<>();
        if (metalOffset > 0 && metalOffset + (width * height) <= smf.length) {
            boolean[] seen = new boolean[width * height];
            int[] stack = new int[width * height];
            for (int start = 0; start < width * height; start++) {
                if (!seen[start] && smf[metalOffset + start] != 0) {
                    int size = 0;
                    long sumX = 0;
                    long sumY = 0;
                    int top = 0 + 1;
                    stack[0] = start;
                    seen[start] = true;
                    while (top > 0) {
                        top--;
                        int index = stack[top];
                        int x = index % width;
                        int y = index / width;
                        size++;
                        sumX += (long) x;
                        sumY += (long) y;
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dx = -1; dx <= 1; dx++) {
                                int nx = x + dx;
                                int ny = y + dy;
                                if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                                    int next = (ny * width) + nx;
                                    if (!seen[next] && smf[metalOffset + next] != 0) {
                                        seen[next] = true;
                                        int i = top;
                                        top++;
                                        stack[i] = next;
                                    }
                                }
                            }
                        }
                    }
                    if (size <= 400) {
                        metal.add(new float[]{((sumX / size) + 0.5f) / width, ((sumY / size) + 0.5f) / height});
                    }
                }
            }
            if (metal.size() > 600) {
                metal.clear();
            }
        }
        List<float[]> geos = new ArrayList<>();
        if (featureOffset > 0 && featureOffset + 8 <= smf.length) {
            int typeCount = buffer.getInt(featureOffset);
            int featureCount = buffer.getInt(featureOffset + 4);
            int position = featureOffset + 8;
            List<String> types = new ArrayList<>();
            for (int i2 = 0; i2 < typeCount && position < smf.length; i2++) {
                int end = position;
                while (end < smf.length && smf[end] != 0) {
                    end++;
                }
                types.add(new String(smf, position, end - position, StandardCharsets.ISO_8859_1).toLowerCase(Locale.ROOT));
                position = end + 1;
            }
            float mapWidth = mapX * 8.0f;
            float mapHeight = mapY * 8.0f;
            int i3 = 0;
            while (i3 < featureCount && position + 24 <= smf.length) {
                int type = buffer.getInt(position);
                if (type >= 0 && type < types.size() && types.get(type).contains("geovent")) {
                    geos.add(new float[]{buffer.getFloat(position + 4) / mapWidth, buffer.getFloat(position + 12) / mapHeight});
                }
                i3++;
                position += 24;
            }
        }
        return new Marks(metal, geos);
    }

    static BufferedImage readMinimap(Path data, Path cacheFolder, InstalledMap map) {
        Path cached = cacheFolder.resolve(String.valueOf(map.archive().getFileName()) + ".1024.png");
        try {
            if (Files.isRegularFile(cached, new LinkOption[0]) && Files.getLastModifiedTime(cached, new LinkOption[0]).compareTo(Files.getLastModifiedTime(map.archive(), new LinkOption[0])) >= 0) {
                return ImageIO.read(cached.toFile());
            }
        } catch (IOException e) {
        }
        BufferedImage image = null;
        String fileName = map.archive().getFileName().toString().toLowerCase(Locale.ROOT);
        try {
            if (fileName.endsWith(".sdz")) {
                ZipFile zip = new ZipFile(map.archive().toFile());
                try {
                    ZipEntry preview = zip.stream().filter(candidate -> {
                        return candidate.getName().endsWith("_preview.png");
                    }).findFirst().orElse(null);
                    if (preview != null) {
                        InputStream stream = zip.getInputStream(preview);
                        try {
                            BufferedImage bufferedImage = ImageIO.read(stream);
                            if (stream != null) {
                                stream.close();
                            }
                            zip.close();
                            return bufferedImage;
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    }
                    ZipEntry smf = zip.getEntry(map.mapFile());
                    if (smf != null) {
                        InputStream stream2 = zip.getInputStream(smf);
                        try {
                            image = decodeMinimap(stream2);
                            if (stream2 != null) {
                                stream2.close();
                            }
                        } catch (Throwable th3) {
                            if (stream2 != null) {
                                try {
                                    stream2.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            }
                            throw th3;
                        }
                    }
                    zip.close();
                } catch (Throwable th5) {
                    try {
                        zip.close();
                    } catch (Throwable th6) {
                        th5.addSuppressed(th6);
                    }
                    throw th5;
                }
            } else {
                Path sevenZip = sevenZip(data);
                if (sevenZip == null) {
                    return null;
                }
                Process process = new ProcessBuilder(sevenZip.toString(), "e", "-so", map.archive().toString(), map.mapFile()).redirectError(ProcessBuilder.Redirect.DISCARD).start();
                try {
                    InputStream stream3 = process.getInputStream();
                    try {
                        image = decodeMinimap(stream3);
                        if (stream3 != null) {
                            stream3.close();
                        }
                        process.destroy();
                    } catch (Throwable th7) {
                        if (stream3 != null) {
                            try {
                                stream3.close();
                            } catch (Throwable th8) {
                                th7.addSuppressed(th8);
                            }
                        }
                        throw th7;
                    }
                } catch (Throwable th9) {
                    process.destroy();
                    throw th9;
                }
            }
            if (image != null) {
                Files.createDirectories(cacheFolder, new FileAttribute[0]);
                ImageIO.write(image, "png", cached.toFile());
            }
            return image;
        } catch (IOException | RuntimeException e2) {
            return null;
        }
    }

    private static BufferedImage decodeMinimap(InputStream stream) throws IOException {
        byte[] header = stream.readNBytes(SMF_HEADER);
        if (header.length < SMF_HEADER) {
            return null;
        }
        int minimapOffset = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).getInt(64);
        long j = minimapOffset - SMF_HEADER;
        while (true) {
            long toSkip = j;
            if (toSkip > 0) {
                long skipped = stream.skip(toSkip);
                if (skipped <= 0) {
                    if (stream.read() < 0) {
                        return null;
                    }
                    skipped = 1;
                }
                j = toSkip - skipped;
            } else {
                byte[] blocks = stream.readNBytes(DDSHeader.LINEARSIZE_FLAG);
                if (blocks.length < 524288) {
                    return null;
                }
                BufferedImage image = new BufferedImage(1024, 1024, 1);
                ByteBuffer buffer = ByteBuffer.wrap(blocks).order(ByteOrder.LITTLE_ENDIAN);
                int[] colors = new int[4];
                for (int by = 0; by < 256; by++) {
                    for (int bx = 0; bx < 256; bx++) {
                        int c0 = buffer.getShort() & 65535;
                        int c1 = buffer.getShort() & 65535;
                        int bits = buffer.getInt();
                        colors[0] = rgb565(c0);
                        colors[1] = rgb565(c1);
                        colors[2] = c0 > c1 ? mix(colors[0], colors[1], 2, 1) : mix(colors[0], colors[1], 1, 1);
                        colors[3] = c0 > c1 ? mix(colors[0], colors[1], 1, 2) : 0;
                        for (int i = 0; i < 16; i++) {
                            image.setRGB((bx * 4) + (i % 4), (by * 4) + (i / 4), colors[(bits >>> (i * 2)) & 3]);
                        }
                    }
                }
                return image;
            }
        }
    }

    private static int rgb565(int color) {
        int r = (((color >> 11) & 31) * 255) / 31;
        int g = (((color >> 5) & 63) * 255) / 63;
        int b = ((color & 31) * 255) / 31;
        return (r << 16) | (g << 8) | b;
    }

    private static int mix(int a, int b, int weightA, int weightB) {
        int total = weightA + weightB;
        int r = ((((a >> 16) & 255) * weightA) + (((b >> 16) & 255) * weightB)) / total;
        int g = ((((a >> 8) & 255) * weightA) + (((b >> 8) & 255) * weightB)) / total;
        int blue = (((a & 255) * weightA) + ((b & 255) * weightB)) / total;
        return (r << 16) | (g << 8) | blue;
    }

    static int[] readMapSize(InstalledMap map) {
        if (!map.archive().getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".sdz")) {
            return null;
        }
        try {
            ZipFile zip = new ZipFile(map.archive().toFile());
            try {
                ZipEntry smf = zip.getEntry(map.mapFile());
                if (smf != null) {
                    InputStream stream = zip.getInputStream(smf);
                    try {
                        ByteBuffer header = ByteBuffer.wrap(stream.readNBytes(32)).order(ByteOrder.LITTLE_ENDIAN);
                        int[] iArr = {header.getInt(24) / 64, header.getInt(28) / 64};
                        if (stream != null) {
                            stream.close();
                        }
                        zip.close();
                        return iArr;
                    } catch (Throwable th) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                zip.close();
                return null;
            } catch (Throwable th3) {
                try {
                    zip.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    static Path sevenZip(Path data) {
        return Platform.sevenZip(data);
    }

    static Map<String, Map<Integer, float[][]>> readStartBoxes(Path data) {
        Map<String, Map<Integer, float[][]>> boxes = new HashMap<>();
        try {
            String text = readPackageFile(data, "byar-chobby", "luamenu/configs/gameconfig/byar/savedboxes.dat");
            if (text == null) {
                return boxes;
            }
            Pattern line = Pattern.compile("^(.*)\\.smf:(\\d+)\\|(.*)$");
            for (String raw : text.split("\\r?\\n")) {
                int comment = raw.indexOf(35);
                Matcher matcher = line.matcher(comment >= 0 ? raw.substring(0, comment) : raw);
                if (matcher.find()) {
                    String[] parts = matcher.group(3).trim().split(";");
                    float[][] teamBoxes = new float[parts.length][];
                    boolean valid = true;
                    for (int i = 0; i < parts.length; i++) {
                        String[] numbers = parts[i].trim().split("\\s+");
                        if (numbers.length != 4) {
                            valid = false;
                            break;
                        }
                        teamBoxes[i] = new float[4];
                        for (int j = 0; j < 4; j++) {
                            teamBoxes[i][j] = Float.parseFloat(numbers[j]) / 200.0f;
                        }
                    }
                    if (valid) {
                        boxes.computeIfAbsent(matcher.group(1).toLowerCase(Locale.ROOT), key -> {
                            return new HashMap();
                        }).put(Integer.valueOf(Integer.parseInt(matcher.group(2))), teamBoxes);
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
        }
        return boxes;
    }

    private static String readPackageFile(Path data, String repository, String file) throws IOException {
        byte[] bytes = readPackageBytes(data, repository, file);
        if (bytes != null) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    static BufferedImage readCommanderIcon(Path data) {
        return readGameIcon(data, "icons/armcom.png");
    }

    static String readGameText(Path data, String file) {
        try {
            return readPackageFile(data, "byar", file);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    static BufferedImage readGameIcon(Path data, String file) {
        try {
            byte[] bytes = readPackageBytes(data, "byar", file);
            if (bytes != null) {
                return ImageIO.read(new ByteArrayInputStream(bytes));
            }
            return null;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    static byte[] readMenuFile(Path data, String file) {
        try {
            return readPackageBytes(data, "byar-chobby", file);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    static void fixWindowPosition(Path data) {
        Path settings = data.resolve("springsettings.cfg");
        if (!Files.isRegularFile(settings, new LinkOption[0])) {
            return;
        }
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(settings));
            Map<String, String> values = new HashMap<>();
            for (String line : lines) {
                int equals = line.indexOf(61);
                if (equals > 0) {
                    values.put(line.substring(0, equals).trim(), line.substring(equals + 1).trim());
                }
            }
            boolean borderless = "1".equals(values.get("WindowBorderless")) && !"1".equals(values.get("Fullscreen"));
            if (!borderless) {
                return;
            }
            boolean changed = false;
            for (String key : new String[]{"WindowPosX", "WindowPosY"}) {
                if (!"0".equals(values.get(key))) {
                    lines.removeIf(line2 -> {
                        return line2.trim().matches(key + "\\s*=.*");
                    });
                    lines.add(key + " = 0");
                    changed = true;
                }
            }
            if (changed) {
                Files.write(settings, lines, new OpenOption[0]);
            }
        } catch (IOException | RuntimeException e) {
        }
    }

    static byte[] readGameBytes(Path data, String file) {
        try {
            return readPackageBytes(data, "byar", file);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    static Map<String, byte[]> readGameFiles(Path data, Predicate<String> which) {
        Map<String, byte[]> files = new LinkedHashMap<>();
        try {
            byte[] index = readPackageIndex(data, "byar");
            if (index == null) {
                return files;
            }
            int position = 0;
            while (position < index.length) {
                int i = position;
                int position2 = position + 1;
                int length = index[i] & 255;
                String name = new String(index, position2, length, StandardCharsets.UTF_8);
                int position3 = position2 + length;
                if (which.test(name)) {
                    StringBuilder md5 = new StringBuilder();
                    for (int j = 0; j < 16; j++) {
                        md5.append(String.format("%02x", Byte.valueOf(index[position3 + j])));
                    }
                    Path pooled = data.resolve("pool").resolve(md5.substring(0, 2)).resolve(md5.substring(2) + ".gz");
                    try {
                        InputStream stream = new GZIPInputStream(Files.newInputStream(pooled, new OpenOption[0]));
                        try {
                            files.put(name, stream.readAllBytes());
                            stream.close();
                        } catch (Throwable th) {
                            try {
                                stream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (IOException e) {
                    }
                }
                position = position3 + 24;
            }
            return files;
        } catch (IOException | RuntimeException e2) {
            return files;
        }
    }

    static void refreshGame(Path data, Runnable whenDone) {
        if (!refreshing.compareAndSet(false, true)) {
            return;
        }
        Path downloader = null;
        try {
            Stream<Path> engines = Files.list(data.resolve("engine"));
            try {
                downloader = (Path) engines.map(engine -> {
                    return engine.resolve(Platform.program("pr-downloader"));
                }).filter(x$0 -> {
                    return Files.isRegularFile(x$0, new LinkOption[0]);
                }).max(Comparator.comparingLong(path -> {
                    return path.toFile().lastModified();
                })).orElse(null);
                if (engines != null) {
                    engines.close();
                }
            } catch (Throwable th) {
                if (engines != null) {
                    try {
                        engines.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
        }
        if (downloader == null) {
            refreshing.set(false);
            whenDone.run();
        } else {
            Path program = downloader;
            Thread thread = new Thread(() -> {
                try {
                    ProcessBuilder builder = new ProcessBuilder(program.toString(), "--filesystem-writepath", data.toString(), "--download-game", "byar:test", "--download-game", "byar-chobby:test").redirectErrorStream(true).redirectOutput(data.resolve("barmapstudio-game-refresh.log").toFile());
                    builder.environment().put("PRD_RAPID_USE_STREAMER", "false");
                    builder.environment().put("PRD_RAPID_REPO_MASTER", "https://repos-cdn.beyondallreason.dev/repos.gz");
                    builder.start().waitFor();
                    refreshing.set(false);
                    whenDone.run();
                } catch (IOException | InterruptedException e2) {
                    refreshing.set(false);
                    whenDone.run();
                } catch (Throwable th3) {
                    refreshing.set(false);
                    whenDone.run();
                    throw th3;
                }
            }, "game-refresh");
            thread.setDaemon(true);
            thread.start();
        }
    }

    static String installedVersion(Path data, String repository) {
        try {
            Stream<Path> repos = Files.list(data.resolve("rapid"));
            try {
                Path versions = null;
                for (Path repo : repos.toList()) {
                    Path candidate = repo.resolve(repository).resolve("versions.gz");
                    if (Files.isRegularFile(candidate, new LinkOption[0])) {
                        versions = candidate;
                    }
                }
                if (versions == null) {
                    if (repos != null) {
                        repos.close();
                    }
                    return null;
                }
                InputStream stream = new GZIPInputStream(Files.newInputStream(versions, new OpenOption[0]));
                try {
                    List<String> lines = new String(stream.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
                    stream.close();
                    for (int i = lines.size() - 1; i >= 0; i--) {
                        String[] fields = lines.get(i).split(",");
                        if (fields.length >= 4 && Files.isRegularFile(data.resolve("packages").resolve(fields[1] + ".sdp"), new LinkOption[0])) {
                            String strTrim = fields[3].trim();
                            if (repos != null) {
                                repos.close();
                            }
                            return strTrim;
                        }
                    }
                    if (repos != null) {
                        repos.close();
                    }
                    return null;
                } catch (Throwable th) {
                    try {
                        stream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                if (repos != null) {
                    try {
                        repos.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private static byte[] readPackageIndex(Path data, String repository) throws IOException {
        Path versions = null;
        Stream<Path> repos = Files.list(data.resolve("rapid"));
        try {
            for (Path repo : repos.toList()) {
                Path candidate = repo.resolve(repository).resolve("versions.gz");
                if (Files.isRegularFile(candidate, new LinkOption[0])) {
                    versions = candidate;
                }
            }
            if (repos != null) {
                repos.close();
            }
            if (versions == null) {
                return null;
            }
            InputStream stream = new GZIPInputStream(Files.newInputStream(versions, new OpenOption[0]));
            try {
                List<String> lines = new String(stream.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
                stream.close();
                for (int i = lines.size() - 1; i >= 0; i--) {
                    String[] fields = lines.get(i).split(",");
                    if (fields.length >= 2) {
                        Path sdp = data.resolve("packages").resolve(fields[1] + ".sdp");
                        if (Files.isRegularFile(sdp, new LinkOption[0])) {
                            InputStream stream2 = new GZIPInputStream(Files.newInputStream(sdp, new OpenOption[0]));
                            try {
                                byte[] allBytes = stream2.readAllBytes();
                                stream2.close();
                                return allBytes;
                            } catch (Throwable th) {
                                try {
                                    stream2.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        }
                    }
                }
                return null;
            } catch (Throwable th3) {
                try {
                    stream.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (Throwable th5) {
            if (repos != null) {
                try {
                    repos.close();
                } catch (Throwable th6) {
                    th5.addSuppressed(th6);
                }
            }
            throw th5;
        }
    }

    private static byte[] readPackageBytes(Path data, String repository, String file) throws IOException {
        Path versions = null;
        Stream<Path> repos = Files.list(data.resolve("rapid"));
        try {
            for (Path repo : repos.toList()) {
                Path candidate = repo.resolve(repository).resolve("versions.gz");
                if (Files.isRegularFile(candidate, new LinkOption[0])) {
                    versions = candidate;
                }
            }
            if (repos != null) {
                repos.close();
            }
            if (versions == null) {
                return null;
            }
            InputStream stream = new GZIPInputStream(Files.newInputStream(versions, new OpenOption[0]));
            try {
                List<String> lines = new String(stream.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
                stream.close();
                for (int i = lines.size() - 1; i >= 0; i--) {
                    String[] fields = lines.get(i).split(",");
                    if (fields.length >= 2) {
                        Path sdp = data.resolve("packages").resolve(fields[1] + ".sdp");
                        if (Files.isRegularFile(sdp, new LinkOption[0])) {
                            InputStream stream2 = new GZIPInputStream(Files.newInputStream(sdp, new OpenOption[0]));
                            try {
                                byte[] index = stream2.readAllBytes();
                                stream2.close();
                                int position = 0;
                                while (position < index.length) {
                                    int i2 = position;
                                    int position2 = position + 1;
                                    int length = index[i2] & 255;
                                    String name = new String(index, position2, length, StandardCharsets.UTF_8);
                                    int position3 = position2 + length;
                                    if (name.equalsIgnoreCase(file)) {
                                        StringBuilder md5 = new StringBuilder();
                                        for (int j = 0; j < 16; j++) {
                                            md5.append(String.format("%02x", Byte.valueOf(index[position3 + j])));
                                        }
                                        Path pooled = data.resolve("pool").resolve(md5.substring(0, 2)).resolve(md5.substring(2) + ".gz");
                                        InputStream stream3 = new GZIPInputStream(Files.newInputStream(pooled, new OpenOption[0]));
                                        try {
                                            byte[] allBytes = stream3.readAllBytes();
                                            stream3.close();
                                            return allBytes;
                                        } catch (Throwable th) {
                                            try {
                                                stream3.close();
                                            } catch (Throwable th2) {
                                                th.addSuppressed(th2);
                                            }
                                            throw th;
                                        }
                                    }
                                    position = position3 + 24;
                                }
                                return null;
                            } catch (Throwable th3) {
                                try {
                                    stream2.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                                throw th3;
                            }
                        }
                    }
                }
                return null;
            } catch (Throwable th5) {
                try {
                    stream.close();
                } catch (Throwable th6) {
                    th5.addSuppressed(th6);
                }
                throw th5;
            }
        } catch (Throwable th7) {
            if (repos != null) {
                try {
                    repos.close();
                } catch (Throwable th8) {
                    th7.addSuppressed(th8);
                }
            }
            throw th7;
        }
    }

    static String buildScript(String mapName, int[] allyOfSlot, float[][] boxes, List<String> humans, boolean coop, boolean hosted, String password) {
        return buildScript(mapName, allyOfSlot, boxes, humans, coop, hosted, password, 8452);
    }

    static int freeHostPort() {
        for (int port = 8452; port < 8492; port++) {
            try {
                DatagramSocket socket = new DatagramSocket((SocketAddress) null);
                try {
                    socket.setReuseAddress(false);
                    socket.bind(new InetSocketAddress(port));
                    int i = port;
                    socket.close();
                    return i;
                } catch (Throwable th) {
                    try {
                        socket.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (IOException e) {
            }
        }
        return 8452;
    }

    static String buildScript(String mapName, int[] allyOfSlot, float[][] boxes, List<String> humans, boolean coop, boolean hosted, String password, int hostPort) {
        int[] playerOfSlot = new int[allyOfSlot.length];
        Arrays.fill(playerOfSlot, -1);
        int humanCount = Math.min(humans.size(), allyOfSlot.length);
        for (int player = 0; player < humanCount; player++) {
            int wanted = coop ? 0 : player % boxes.length;
            int slot = -1;
            for (int i = 0; i < allyOfSlot.length && slot < 0; i++) {
                if (playerOfSlot[i] < 0 && allyOfSlot[i] == wanted) {
                    slot = i;
                }
            }
            for (int i2 = 0; i2 < allyOfSlot.length && slot < 0; i2++) {
                if (playerOfSlot[i2] < 0) {
                    slot = i2;
                }
            }
            playerOfSlot[slot] = player;
        }
        StringBuilder script = new StringBuilder();
        script.append("[game]\n{\n\tgametype=rapid://byar:test;\n\tmapname=").append(mapName).append(";\n\tishost=1;\n").append("\thostip=").append(hosted ? "" : "127.0.0.1").append(";\n\thostport=").append(hosted ? hostPort : 0).append(";\n\tmyplayername=").append((String) humans.getFirst()).append(";\n\tstartpostype=2;\n\tnumplayers=").append(humanCount).append(";\n\t[modoptions]\n\t{\n\t\tdraft_mode=disabled;\n\t}\n");
        for (int player2 = 0; player2 < humanCount; player2++) {
            int slot2 = 0;
            for (int i3 = 0; i3 < playerOfSlot.length; i3++) {
                if (playerOfSlot[i3] == player2) {
                    slot2 = i3;
                }
            }
            script.append("\t[player").append(player2).append("]\n\t{\n\t\tname=").append(humans.get(player2)).append(";\n\t\tteam=").append(slot2).append(";\n\t\tspectator=0;\n");
            if (hosted && player2 > 0 && password != null && !password.isEmpty()) {
                script.append("\t\tpassword=").append(password).append(";\n");
            }
            script.append("\t}\n");
        }
        int ai = 0;
        for (int i4 = 0; i4 < allyOfSlot.length; i4++) {
            if (playerOfSlot[i4] < 0) {
                script.append("\t[ai").append(ai).append("]\n\t{\n\t\tname=BARbarian ").append(ai + 1).append(";\n\t\tshortname=BARb;\n\t\tversion=stable;\n\t\tteam=").append(i4).append(";\n\t\thost=0;\n\t}\n");
                ai++;
            }
            script.append("\t[team").append(i4).append("]\n\t{\n\t\tteamleader=").append(Math.max(0, playerOfSlot[i4])).append(";\n\t\tallyteam=").append(allyOfSlot[i4]).append(";\n\t\tside=").append(i4 % 2 == 0 ? "Armada" : "Cortex").append(";\n\t\thandicap=0;\n\t}\n");
        }
        for (int team = 0; team < boxes.length; team++) {
            script.append(String.format(Locale.ROOT, "\t[allyteam%d]\n\t{\n\t\tnumallies=0;\n\t\tstartrectleft=%.3f;\n\t\tstartrecttop=%.3f;\n\t\tstartrectright=%.3f;\n\t\tstartrectbottom=%.3f;\n\t}\n", Integer.valueOf(team), Float.valueOf(boxes[team][0]), Float.valueOf(boxes[team][1]), Float.valueOf(boxes[team][2]), Float.valueOf(boxes[team][3])));
        }
        return script.append("}\n").toString();
    }

    static Path barStudioSettingsFile(Path data) {
        return data.resolve("neroxis_scripts").resolve("springsettings.cfg");
    }

    static Path barStudioSettings(Path data, Map<String, String> extra) {
        Path own = data.resolve("springsettings.cfg");
        Path copy = barStudioSettingsFile(data);
        List<String> lines = new ArrayList<>();
        try {
            boolean fresh = !Files.isRegularFile(copy, new LinkOption[0]) || (Files.isRegularFile(own, new LinkOption[0]) && Files.getLastModifiedTime(own, new LinkOption[0]).toMillis() > Files.getLastModifiedTime(copy, new LinkOption[0]).toMillis());
            Path base = fresh ? own : copy;
            if (Files.isRegularFile(base, new LinkOption[0])) {
                lines.addAll(Files.readAllLines(base));
            }
        } catch (IOException e) {
        }
        Map<String, String> values = new LinkedHashMap<>();
        if ("1".equals(settingValue(lines, "WindowBorderless")) && !"1".equals(settingValue(lines, "Fullscreen"))) {
            values.put("WindowPosX", "0");
            values.put("WindowPosY", "0");
        }
        values.putAll(extra);
        for (String key : new String[]{"SimpleTeamColors", "SimpleTeamColorsPlayerR", "SimpleTeamColorsPlayerG", "SimpleTeamColorsPlayerB", "StudioOwnColour"}) {
            if (!extra.containsKey(key)) {
                lines.removeIf(line -> {
                    return line.trim().matches(key + "\\s*=.*");
                });
            }
        }
        lines.removeIf(line2 -> {
            return values.keySet().stream().anyMatch(key2 -> {
                return line2.trim().matches(key2 + "\\s*=.*");
            });
        });
        values.forEach((key2, value) -> {
            lines.add(key2 + " = " + value);
        });
        try {
            Files.createDirectories(copy.getParent(), new FileAttribute[0]);
            Files.write(copy, lines, new OpenOption[0]);
            return copy;
        } catch (IOException e2) {
            return null;
        }
    }

    private static String settingValue(List<String> lines, String key) {
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.matches(key + "\\s*=.*")) {
                return trimmed.substring(trimmed.indexOf(61) + 1).trim();
            }
        }
        return null;
    }

    static void removeSettings(Path data, List<String> keys) {
        Path own = data.resolve("springsettings.cfg");
        if (!Files.isRegularFile(own, new LinkOption[0])) {
            return;
        }
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(own));
            if (lines.removeIf(line -> {
                return keys.stream().anyMatch(key -> {
                    return line.trim().matches(key + "\\s*=.*");
                });
            })) {
                Files.write(own, lines, new OpenOption[0]);
            }
        } catch (IOException e) {
        }
    }
}
