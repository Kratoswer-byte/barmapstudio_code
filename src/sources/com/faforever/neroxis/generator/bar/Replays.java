package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Replays.class */
final class Replays {
    private static final String MAGIC = "spring demofile";
    private final Host host;
    private Thread reader;
    private JDialog details;
    private static final Map<String, String> ALIASES = new HashMap();
    private static final String[] CALLSIGNS = {"Nova", "Halcyon", "Vesper", "Corvid", "Sable", "Meridian", "Tycho", "Aurelia", "Kestrel", "Orin", "Lumen", "Rook"};
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Color CHIP = new Color(40, 44, 54);
    private static final Color HOVER = new Color(52, 57, 70);
    private final Map<Path, Replay> known = new ConcurrentHashMap();
    private final Map<String, ImageIcon> pictures = new ConcurrentHashMap();
    private final JPanel grid = new Grid();
    private final JTextField search = new JTextField();
    private final JLabel status = new JLabel(" ");
    private volatile List<Replay> shown = List.of();
    private final Timer redraw = new Timer(250, e -> {
        rebuild();
    });
    private final Map<String, Optional<Icon>> factionIcons = new ConcurrentHashMap();

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Replays$Host.class */
    interface Host {
        Path data();

        BufferedImage preview(String map);

        boolean mapInstalled(String map);

        boolean engineInstalled(String engine);

        boolean watch(Path replay, String engine);

        JFrame frame();
    }

    static String alias(String name) {
        String strComputeIfAbsent;
        if (!Boolean.getBoolean("neroxis.launcher.aliases") || name.isBlank()) {
            return name;
        }
        synchronized (ALIASES) {
            strComputeIfAbsent = ALIASES.computeIfAbsent(name, key -> {
                return CALLSIGNS[ALIASES.size() % CALLSIGNS.length];
            });
        }
        return strComputeIfAbsent;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Replays$Player.class */
    static final class Player extends Record {
        private final String name;
        private final int team;
        private final int allyTeam;
        private final String side;
        private final boolean spectator;
        private final boolean ai;
        private final double skill;

        Player(String name, int team, int allyTeam, String side, boolean spectator, boolean ai, double skill) {
            this.name = name;
            this.team = team;
            this.allyTeam = allyTeam;
            this.side = side;
            this.spectator = spectator;
            this.ai = ai;
            this.skill = skill;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Player.class), Player.class, "name;team;allyTeam;side;spectator;ai;skill", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->team:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->allyTeam:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->side:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->spectator:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->ai:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->skill:D").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Player.class), Player.class, "name;team;allyTeam;side;spectator;ai;skill", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->team:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->allyTeam:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->side:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->spectator:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->ai:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->skill:D").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Player.class, Object.class), Player.class, "name;team;allyTeam;side;spectator;ai;skill", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->team:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->allyTeam:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->side:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->spectator:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->ai:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Player;->skill:D").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String name() {
            return this.name;
        }

        public int team() {
            return this.team;
        }

        public int allyTeam() {
            return this.allyTeam;
        }

        public String side() {
            return this.side;
        }

        public boolean spectator() {
            return this.spectator;
        }

        public boolean ai() {
            return this.ai;
        }

        public double skill() {
            return this.skill;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Replays$Replay.class */
    static final class Replay extends Record {
        private final Path file;
        private final long time;
        private final String map;
        private final String engine;
        private final long size;
        private final String gameId;
        private final int gameSeconds;
        private final int wallSeconds;
        private final String gameType;
        private final List<Player> players;
        private final boolean parsed;
        private final boolean broken;

        Replay(Path file, long time, String map, String engine, long size, String gameId, int gameSeconds, int wallSeconds, String gameType, List<Player> players, boolean parsed, boolean broken) {
            this.file = file;
            this.time = time;
            this.map = map;
            this.engine = engine;
            this.size = size;
            this.gameId = gameId;
            this.gameSeconds = gameSeconds;
            this.wallSeconds = wallSeconds;
            this.gameType = gameType;
            this.players = players;
            this.parsed = parsed;
            this.broken = broken;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Replay.class), Replay.class, "file;time;map;engine;size;gameId;gameSeconds;wallSeconds;gameType;players;parsed;broken", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->file:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->time:J", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->map:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->engine:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->size:J", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameId:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameSeconds:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->wallSeconds:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameType:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->players:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->parsed:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->broken:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Replay.class), Replay.class, "file;time;map;engine;size;gameId;gameSeconds;wallSeconds;gameType;players;parsed;broken", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->file:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->time:J", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->map:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->engine:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->size:J", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameId:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameSeconds:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->wallSeconds:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameType:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->players:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->parsed:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->broken:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Replay.class, Object.class), Replay.class, "file;time;map;engine;size;gameId;gameSeconds;wallSeconds;gameType;players;parsed;broken", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->file:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->time:J", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->map:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->engine:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->size:J", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameId:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameSeconds:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->wallSeconds:I", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->gameType:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->players:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->parsed:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/Replays$Replay;->broken:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Path file() {
            return this.file;
        }

        public long time() {
            return this.time;
        }

        public String map() {
            return this.map;
        }

        public String engine() {
            return this.engine;
        }

        public long size() {
            return this.size;
        }

        public String gameId() {
            return this.gameId;
        }

        public int gameSeconds() {
            return this.gameSeconds;
        }

        public int wallSeconds() {
            return this.wallSeconds;
        }

        public String gameType() {
            return this.gameType;
        }

        public List<Player> players() {
            return this.players;
        }

        public boolean parsed() {
            return this.parsed;
        }

        public boolean broken() {
            return this.broken;
        }

        List<Player> playing() {
            return this.players.stream().filter(player -> {
                return !player.spectator();
            }).toList();
        }

        Map<Integer, List<Player>> teams() {
            Map<Integer, List<Player>> teams = new TreeMap<>();
            for (Player player : playing()) {
                teams.computeIfAbsent(Integer.valueOf(player.allyTeam()), key -> {
                    return new ArrayList();
                }).add(player);
            }
            return teams;
        }

        String title() {
            Map<Integer, List<Player>> teams = teams();
            if (teams.isEmpty()) {
                return this.parsed ? "Nobody played" : "Reading...";
            }
            if (teams.size() == 1) {
                List<Player> alone = teams.values().iterator().next();
                return alone.size() == 1 ? alone.get(0).name() + " alone" : "Co-op " + alone.size();
            }
            if (teams.size() == 2 && teams.values().stream().allMatch(team -> {
                return team.size() == 1;
            })) {
                List<List<Player>> two = new ArrayList<>(teams.values());
                return two.get(0).get(0).name() + " Vs " + two.get(1).get(0).name();
            }
            if (teams.size() > 2 && teams.values().stream().allMatch(team2 -> {
                return team2.size() == 1;
            })) {
                return "FFA " + teams.size();
            }
            return String.join("v", teams.values().stream().map(team3 -> {
                return String.valueOf(team3.size());
            }).toList());
        }

        String shortId() {
            return this.gameId.length() >= 8 ? "#" + this.gameId.substring(0, 8) : "";
        }
    }

    Replays(Host host) {
        this.host = host;
        this.redraw.setRepeats(false);
    }

    Path folder() {
        return this.host.data().resolve("demos");
    }

    JComponent view() {
        JPanel view = new BarLauncher.BackgroundPanel(new BorderLayout(0, 12));
        view.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
        JLabel heading = new JLabel("Replays");
        heading.setFont(heading.getFont().deriveFont(1, 20.0f));
        heading.setForeground(BarLauncher.TEXT);
        this.search.setColumns(28);
        this.search.putClientProperty("JTextField.placeholderText", "Search: player, map, game ID");
        this.search.getDocument().addDocumentListener(new DocumentListener(this) { // from class: com.faforever.neroxis.generator.bar.Replays.1
            final /* synthetic */ Replays this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void insertUpdate(DocumentEvent e) {
                this.this$0.rebuild();
            }

            public void removeUpdate(DocumentEvent e) {
                this.this$0.rebuild();
            }

            public void changedUpdate(DocumentEvent e) {
                this.this$0.rebuild();
            }
        });
        JButton folder = new JButton("Open the replay folder");
        folder.addActionListener(e -> {
            try {
                Files.createDirectories(folder(), new FileAttribute[0]);
                Desktop.getDesktop().open(folder().toFile());
            } catch (IOException | RuntimeException e) {
                JOptionPane.showMessageDialog(this.host.frame(), "Could not open " + String.valueOf(folder()));
            }
        });
        JPanel tools = new JPanel(new FlowLayout(2, 8, 0));
        tools.setOpaque(false);
        tools.add(this.search);
        tools.add(folder);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(heading, "West");
        top.add(tools, "East");
        this.status.setForeground(BarLauncher.MUTED);
        this.status.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        top.add(this.status, "South");
        view.add(top, "North");
        this.grid.setOpaque(false);
        JScrollPane scroll = new JScrollPane(this.grid, 20, 31);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        view.add(scroll, "Center");
        return view;
    }

    void refresh() {
        List<Replay> replays = new ArrayList<>();
        try {
            Stream<Path> files = Files.list(folder());
            try {
                for (Path file : files.filter(path -> {
                    return path.getFileName().toString().endsWith(".sdfz");
                }).toList()) {
                    long time = Files.getLastModifiedTime(file, new LinkOption[0]).toMillis();
                    long size = Files.size(file);
                    Replay replay = this.known.get(file);
                    if (replay == null || replay.time() != time || replay.size() != size) {
                        replay = fromName(file, time, size);
                        this.known.put(file, replay);
                    }
                    replays.add(replay);
                }
                if (files != null) {
                    files.close();
                }
            } catch (Throwable th) {
                if (files != null) {
                    try {
                        files.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
        }
        this.known.keySet().retainAll(replays.stream().map((v0) -> {
            return v0.file();
        }).toList());
        replays.sort(Comparator.comparingLong((v0) -> {
            return v0.time();
        }).reversed());
        this.shown = replays;
        rebuild();
        readInBackground();
    }

    private static Replay fromName(Path file, long time, long size) {
        String name = file.getFileName().toString();
        String name2 = name.substring(0, name.length() - 5);
        String[] parts = name2.split("_", 3);
        String rest = parts.length == 3 ? parts[2] : name2;
        int split = rest.lastIndexOf(95);
        return new Replay(file, time, split > 0 ? rest.substring(0, split) : rest, split > 0 ? rest.substring(split + 1) : "", size, "", 0, 0, "", List.of(), false, false);
    }

    private void readInBackground() {
        if (this.reader != null && this.reader.isAlive()) {
            return;
        }
        Thread thread = new Thread(() -> {
            Replay read;
            for (Replay replay : new ArrayList(this.shown)) {
                if (!replay.parsed() && this.known.containsKey(replay.file())) {
                    try {
                        read = read(replay);
                    } catch (IOException | RuntimeException e) {
                        read = new Replay(replay.file(), replay.time(), replay.map(), replay.engine(), replay.size(), "", 0, 0, "", List.of(), true, true);
                    }
                    if (this.known.replace(replay.file(), replay, read)) {
                        Replay result = read;
                        SwingUtilities.invokeLater(() -> {
                            List<Replay> list = new ArrayList<>(this.shown);
                            list.replaceAll(item -> {
                                return item.file().equals(result.file()) ? result : item;
                            });
                            this.shown = list;
                            this.redraw.restart();
                        });
                    }
                }
            }
        }, "replay-reader");
        thread.setDaemon(true);
        thread.start();
        this.reader = thread;
    }

    static Replay read(Replay basic) throws IOException {
        DataInputStream in = open(basic.file());
        try {
            byte[] head = header(in);
            String engine = cString(head, 24, PreviewGenerator.PREVIEW_SIZE);
            String gameId = hex(head, 280, 16);
            int scriptSize = le(head, 304);
            int gameSeconds = le(head, 312);
            int wallSeconds = le(head, 316);
            if (scriptSize < 0 || scriptSize > 4194304) {
                throw new IOException("script of " + scriptSize + " bytes");
            }
            String script = new String(in.readNBytes(scriptSize), StandardCharsets.UTF_8);
            Map<String, Map<String, String>> sections = sections(script);
            Map<String, String> game = sections.getOrDefault("game", Map.of());
            String map = game.getOrDefault("mapname", basic.map());
            String gameType = game.getOrDefault("gametype", "");
            List<Player> players = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                Map<String, String> player = sections.get("game/player" + i);
                if (player == null) {
                    if (i > 64) {
                        break;
                    }
                } else {
                    int team = number(player.get("team"), -1);
                    boolean spectator = "1".equals(player.get("spectator")) || team < 0;
                    Map<String, String> teamInfo = team >= 0 ? sections.getOrDefault("game/team" + team, Map.of()) : Map.of();
                    players.add(new Player(alias(player.getOrDefault("name", "?")), team, number(teamInfo.get("allyteam"), team), teamInfo.getOrDefault("side", ""), spectator, false, skill(player.get("skill"))));
                }
            }
            for (int i2 = 0; i2 < 256; i2++) {
                Map<String, String> ai = sections.get("game/ai" + i2);
                if (ai == null) {
                    if (i2 > 64) {
                        break;
                    }
                } else {
                    int team2 = number(ai.get("team"), -1);
                    Map<String, String> teamInfo2 = team2 >= 0 ? sections.getOrDefault("game/team" + team2, Map.of()) : Map.of();
                    players.add(new Player(alias(ai.getOrDefault("name", ai.getOrDefault("shortname", "AI"))), team2, number(teamInfo2.get("allyteam"), team2), teamInfo2.getOrDefault("side", ""), team2 < 0, true, Double.NaN));
                }
            }
            Replay replay = new Replay(basic.file(), basic.time(), map, engine.isEmpty() ? basic.engine() : engine, basic.size(), gameId, gameSeconds, wallSeconds, gameType, List.copyOf(players), true, false);
            if (in != null) {
                in.close();
            }
            return replay;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    static int[] winners(Path file) throws IOException {
        try {
            DataInputStream in = open(file);
            try {
                byte[] head = header(in);
                long skip = ((long) le(head, 304)) + ((long) le(head, 308));
                int winnersSize = le(head, 348);
                if (skip >= 0 && winnersSize >= 0 && winnersSize <= 64) {
                    in.skipNBytes(skip);
                    byte[] bytes = in.readNBytes(winnersSize);
                    int[] result = new int[bytes.length];
                    for (int i = 0; i < bytes.length; i++) {
                        result[i] = bytes[i] & 255;
                    }
                    if (in != null) {
                        in.close();
                    }
                    return result;
                }
                if (in != null) {
                    in.close();
                }
                return null;
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (EOFException e) {
            return null;
        }
        return null;
    }

    private static DataInputStream open(Path file) throws IOException {
        InputStream stream = Files.newInputStream(file, new OpenOption[0]);
        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream, 65536), 65536));
    }

    private static byte[] header(DataInputStream in) throws IOException {
        byte[] start = in.readNBytes(24);
        if (start.length < 24 || !MAGIC.equals(new String(start, 0, MAGIC.length(), StandardCharsets.US_ASCII))) {
            throw new IOException("not a demo file");
        }
        int headerSize = le(start, 20);
        if (headerSize < 352 || headerSize > 4096) {
            throw new IOException("header of " + headerSize + " bytes");
        }
        byte[] head = new byte[headerSize];
        System.arraycopy(start, 0, head, 0, 24);
        byte[] rest = in.readNBytes(headerSize - 24);
        if (rest.length < headerSize - 24) {
            throw new IOException("file ends inside the header");
        }
        System.arraycopy(rest, 0, head, 24, rest.length);
        return head;
    }

    private static int le(byte[] bytes, int at) {
        return (bytes[at] & 255) | ((bytes[at + 1] & 255) << 8) | ((bytes[at + 2] & 255) << 16) | ((bytes[at + 3] & 255) << 24);
    }

    private static String cString(byte[] bytes, int at, int length) {
        int end = at;
        while (end < at + length && bytes[end] != 0) {
            end++;
        }
        return new String(bytes, at, end - at, StandardCharsets.UTF_8).trim();
    }

    private static String hex(byte[] bytes, int at, int length) {
        StringBuilder text = new StringBuilder();
        for (int i = at; i < at + length; i++) {
            text.append(String.format("%02x", Integer.valueOf(bytes[i] & 255)));
        }
        return text.toString();
    }

    private static int number(String text, int fallback) {
        if (text == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double skill(String text) {
        if (text == null) {
            return Double.NaN;
        }
        Matcher matcher = NUMBER.matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        }
        return Double.NaN;
    }

    static Map<String, Map<String, String>> sections(String script) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        List<String> path = new ArrayList<>();
        int i = 0;
        int length = script.length();
        while (i < length) {
            char c = script.charAt(i);
            if (c == '[') {
                int end = script.indexOf(93, i);
                int brace = end < 0 ? -1 : script.indexOf(123, end);
                if (brace < 0) {
                    break;
                }
                path.add(script.substring(i + 1, end).trim().toLowerCase(Locale.ROOT));
                result.putIfAbsent(String.join("/", path), new LinkedHashMap<>());
                i = brace + 1;
            } else if (c == '}') {
                if (!path.isEmpty()) {
                    path.remove(path.size() - 1);
                }
                i++;
            } else if (c == '/' && i + 1 < length && script.charAt(i + 1) == '/') {
                int end2 = script.indexOf(10, i);
                i = end2 < 0 ? length : end2 + 1;
            } else if (Character.isWhitespace(c) || c == '{' || c == ';') {
                i++;
            } else {
                int semi = script.indexOf(59, i);
                if (semi < 0) {
                    break;
                }
                int equals = script.indexOf(61, i);
                if (equals > 0 && equals < semi && !path.isEmpty()) {
                    result.computeIfAbsent(String.join("/", path), key -> {
                        return new LinkedHashMap();
                    }).put(script.substring(i, equals).trim().toLowerCase(Locale.ROOT), script.substring(equals + 1, semi).trim());
                }
                i = semi + 1;
            }
        }
        return result;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Replays$Grid.class */
    private static final class Grid extends JPanel implements Scrollable {
        Grid() {
            super(new GridLayout(0, 2, 14, 14));
        }

        public void doLayout() {
            int columns = Math.max(1, Math.min(3, getWidth() / 500));
            GridLayout layout = getLayout();
            if (layout.getColumns() != columns) {
                layout.setColumns(columns);
                revalidate();
            }
            super.doLayout();
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 40;
        }

        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return visible.height;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private void rebuild() {
        String str;
        String query = this.search.getText().trim().toLowerCase(Locale.ROOT);
        List<Replay> list = this.shown.stream().filter(replay -> {
            return matches(replay, query);
        }).toList();
        this.grid.removeAll();
        for (Replay replay2 : list) {
            this.grid.add(card(replay2));
        }
        int unread = (int) this.shown.stream().filter(replay3 -> {
            return !replay3.parsed();
        }).count();
        JLabel jLabel = this.status;
        if (this.shown.isEmpty()) {
            str = "No replays yet - every game you play is recorded by the engine.";
        } else {
            str = list.size() + (list.size() == 1 ? " replay" : " replays") + (query.isEmpty() ? "" : " match") + (unread > 0 ? ", reading " + unread + "..." : "") + "   ·   Click a card for the line-up. Replays need the engine version they were recorded with.";
        }
        jLabel.setText(str);
        this.grid.revalidate();
        this.grid.repaint();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean matches(Replay replay, String query) {
        if (replay.broken() && Boolean.getBoolean("neroxis.launcher.aliases")) {
            return false;
        }
        if (query.isEmpty()) {
            return true;
        }
        String id = query.startsWith("#") ? query.substring(1) : query;
        if ((!id.isEmpty() && replay.gameId().startsWith(id)) || replay.map().toLowerCase(Locale.ROOT).contains(query) || BarLauncher.mapLabel(replay.map()).toLowerCase(Locale.ROOT).contains(query) || replay.title().toLowerCase(Locale.ROOT).contains(query) || new SimpleDateFormat("d MMM yyyy").format(new Date(replay.time())).toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        return replay.players().stream().anyMatch(player -> {
            return player.name().toLowerCase(Locale.ROOT).contains(query);
        });
    }

    private JComponent card(final Replay replay) {
        final JPanel card = new JPanel(this, new BorderLayout(16, 0)) { // from class: com.faforever.neroxis.generator.bar.Replays.2
            final /* synthetic */ Replays this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g.setColor(getBackground() == Replays.HOVER ? BarLauncher.ACCENT : BarLauncher.CARD_BORDER);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBackground(BarLauncher.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));
        card.setCursor(Cursor.getPredefinedCursor(12));
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setOpaque(false);
        left.add(picture(replay.map(), 124), "North");
        JPanel facts = new JPanel(new GridLayout(0, 2, 10, 3));
        facts.setOpaque(false);
        facts.add(fact("Date", new SimpleDateFormat("d MMM yyyy").format(new Date(replay.time()))));
        facts.add(fact("Players", replay.parsed() ? String.valueOf(replay.playing().size()) : "..."));
        facts.add(fact("Time", new SimpleDateFormat("HH:mm").format(new Date(replay.time()))));
        facts.add(fact("Length", replay.parsed() ? length(replay.gameSeconds()) : "..."));
        facts.add(fact("Engine", replay.engine()));
        facts.add(fact("Size", Math.max(1L, replay.size() / 1024) + " KB"));
        left.add(facts, "Center");
        left.setPreferredSize(new Dimension(190, 10));
        card.add(left, "West");
        JPanel right = new JPanel(new BorderLayout(0, 6));
        right.setOpaque(false);
        JLabel title = new JLabel(replay.broken() ? "Could not read this replay" : replay.title());
        title.setFont(title.getFont().deriveFont(0, 19.0f));
        title.setForeground(BarLauncher.TEXT);
        JLabel map = new JLabel("on " + BarLauncher.mapLabel(replay.map()) + (this.host.mapInstalled(replay.map()) ? "" : "  (map not installed)"));
        map.setForeground(BarLauncher.MUTED);
        JPanel head = new JPanel(new GridLayout(0, 1, 0, 2));
        head.setOpaque(false);
        head.add(title);
        head.add(map);
        right.add(head, "North");
        right.add(lineUp(replay, 6, false), "Center");
        JPanel bottom = new JPanel(new FlowLayout(2, 8, 0));
        bottom.setOpaque(false);
        if (!replay.shortId().isEmpty()) {
            bottom.add(chip(replay.shortId(), "The ID of this game - click to copy the whole ID", () -> {
                copy(replay.gameId());
            }));
        }
        JButton play = new JButton("▶");
        play.setToolTipText("Watch the replay");
        play.setBackground(BarLauncher.ACCENT);
        play.setForeground(new Color(25, 18, 8));
        play.setFont(play.getFont().deriveFont(1, 13.0f));
        play.setPreferredSize(new Dimension(44, 30));
        play.setEnabled(this.host.engineInstalled(replay.engine()) && this.host.mapInstalled(replay.map()));
        play.addActionListener(e -> {
            this.host.watch(replay.file(), replay.engine());
        });
        bottom.add(play);
        right.add(bottom, "South");
        card.add(right, "Center");
        MouseAdapter click = new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.Replays.3
            final /* synthetic */ Replays this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mouseClicked(MouseEvent e2) {
                if (SwingUtilities.isLeftMouseButton(e2)) {
                    this.this$0.showDetails(replay);
                }
            }

            public void mouseEntered(MouseEvent e2) {
                card.setBackground(Replays.HOVER);
                card.repaint();
            }

            public void mouseExited(MouseEvent e2) {
                if (!card.contains(SwingUtilities.convertPoint((Component) e2.getSource(), e2.getPoint(), card))) {
                    card.setBackground(BarLauncher.CARD);
                    card.repaint();
                }
            }
        };
        listen(card, click);
        return card;
    }

    private static void listen(Component component, MouseAdapter listener) {
        if (component instanceof JButton) {
            return;
        }
        if (component instanceof JComponent) {
            JComponent part = (JComponent) component;
            if (Boolean.TRUE.equals(part.getClientProperty("ownClick"))) {
                return;
            }
        }
        component.addMouseListener(listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                listen(child, listener);
            }
        }
    }

    private static JComponent fact(String caption, String value) {
        JLabel label = new JLabel("<html><span style='color:#8c94a3'>" + caption + "</span>&nbsp; " + value + "</html>");
        label.setForeground(BarLauncher.TEXT);
        label.setFont(label.getFont().deriveFont(12.0f));
        return label;
    }

    private static JComponent chip(final String text, String tooltip, final Runnable action) {
        final JLabel chip = new JLabel(text);
        chip.setOpaque(true);
        chip.setBackground(CHIP);
        chip.setForeground(BarLauncher.TEXT);
        chip.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BarLauncher.CARD_BORDER), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        chip.setToolTipText(tooltip);
        chip.putClientProperty("ownClick", true);
        chip.setCursor(Cursor.getPredefinedCursor(12));
        chip.addMouseListener(new MouseAdapter() { // from class: com.faforever.neroxis.generator.bar.Replays.4
            public void mouseClicked(MouseEvent e) {
                action.run();
                chip.setText("copied");
                JLabel jLabel = chip;
                String str = text;
                Timer back = new Timer(1200, ev -> {
                    jLabel.setText(str);
                });
                back.setRepeats(false);
                back.start();
                e.consume();
            }
        });
        return chip;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void copy(String text) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), (ClipboardOwner) null);
        } catch (RuntimeException e) {
        }
    }

    static String length(int seconds) {
        if (seconds <= 0) {
            return "-";
        }
        int hours = seconds / 3600;
        int minutes = (seconds / 60) % 60;
        return hours > 0 ? hours + "h " + String.format("%02d", Integer.valueOf(minutes)) + "min" : minutes + "min " + (seconds % 60) + "s";
    }

    private JComponent lineUp(Replay replay, int rows, boolean detail) {
        Map<Integer, List<Player>> teams = replay.teams();
        JPanel panel = new JPanel(new GridLayout(1, 0, 14, 0));
        panel.setOpaque(false);
        if (teams.isEmpty()) {
            JLabel note = new JLabel(replay.parsed() ? "No players in the start script" : "Reading the players...");
            note.setForeground(BarLauncher.MUTED);
            panel.add(note);
            return panel;
        }
        int number = 1;
        for (Map.Entry<Integer, List<Player>> team : teams.entrySet()) {
            JPanel column = new JPanel(new GridLayout(0, 1, 0, 3));
            column.setOpaque(false);
            column.setAlignmentY(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            double sum = team.getValue().stream().mapToDouble((v0) -> {
                return v0.skill();
            }).filter(skill -> {
                return !Double.isNaN(skill);
            }).sum();
            int i = number;
            number++;
            JLabel heading = new JLabel(teamName(teams.size(), i) + ((!detail || sum <= 0.0d) ? "" : "  (" + Math.round(sum) + ")"));
            heading.setForeground(detail ? BarLauncher.TEXT : BarLauncher.MUTED);
            heading.setFont(heading.getFont().deriveFont(1, detail ? 13.0f : 12.0f));
            column.add(heading);
            List<Player> players = team.getValue();
            int shown = detail ? players.size() : Math.min(rows, players.size());
            for (Player player : players.subList(0, shown)) {
                JLabel row = new JLabel(player.name() + ((!detail || Double.isNaN(player.skill())) ? "" : "  (" + Math.round(player.skill()) + ")") + ((player.ai() && detail) ? "  AI" : ""));
                row.setIcon(factionIcon(player.side(), player.ai(), detail ? 16 : 14));
                row.setIconTextGap(6);
                row.setForeground(BarLauncher.TEXT);
                row.setFont(row.getFont().deriveFont(detail ? 13.0f : 12.0f));
                column.add(row);
            }
            if (shown < players.size()) {
                JLabel more = new JLabel("+" + (players.size() - shown) + " more");
                more.setForeground(BarLauncher.MUTED);
                more.setFont(more.getFont().deriveFont(12.0f));
                column.add(more);
            }
            if (detail) {
                JPanel box = new JPanel(new BorderLayout());
                box.setBackground(CHIP);
                box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BarLauncher.CARD_BORDER), BorderFactory.createEmptyBorder(10, 14, 10, 14)));
                box.add(column, "North");
                box.putClientProperty("allyTeam", team.getKey());
                panel.add(box);
            } else {
                JPanel holder = new JPanel(new BorderLayout());
                holder.setOpaque(false);
                holder.add(column, "North");
                panel.add(holder);
            }
        }
        return panel;
    }

    private static String teamName(int teams, int number) {
        return teams == 1 ? "Everybody" : "Team " + number;
    }

    private Icon factionIcon(String side, boolean ai, int size) {
        String str;
        String name = side.toLowerCase(Locale.ROOT);
        if (name.startsWith("arm")) {
            str = "armada";
        } else if (name.startsWith("cor")) {
            str = "cortex";
        } else {
            str = name.startsWith("leg") ? "legion" : "";
        }
        String faction = str;
        if (!faction.isEmpty()) {
            Optional<Icon> icon = this.factionIcons.computeIfAbsent(faction + "@" + size, key -> {
                try {
                    BufferedImage picture = BarInstall.readGameIcon(this.host.data(), "sidepics/" + faction + ".png");
                    if (picture == null) {
                        return Optional.empty();
                    }
                    BufferedImage small = new BufferedImage(size, size, 2);
                    Graphics2D g = small.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g.drawImage(picture, 0, 0, size, size, (ImageObserver) null);
                    g.dispose();
                    return Optional.of(new ImageIcon(small));
                } catch (Exception e) {
                    return Optional.empty();
                }
            });
            if (icon.isPresent()) {
                return icon.get();
            }
        }
        return new FactionMark(side, ai);
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/Replays$FactionMark.class */
    private static final class FactionMark implements Icon {
        private final Color color;
        private final boolean ai;

        FactionMark(String side, boolean ai) {
            Color color;
            String name = side.toLowerCase(Locale.ROOT);
            if (name.startsWith("arm")) {
                color = new Color(70, 140, 255);
            } else if (name.startsWith("cor")) {
                color = new Color(232, 70, 60);
            } else {
                color = name.startsWith("leg") ? new Color(80, 190, 90) : new Color(150, 156, 170);
            }
            this.color = color;
            this.ai = ai;
        }

        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(this.color);
            int[] xs = {x + 6, x + 12, x + 6, x};
            int[] ys = {y, y + 6, y + 12, y + 6};
            if (this.ai) {
                g.drawPolygon(xs, ys, 4);
            } else {
                g.fillPolygon(xs, ys, 4);
            }
            g.dispose();
        }

        public int getIconWidth() {
            return 13;
        }

        public int getIconHeight() {
            return 13;
        }
    }

    private JComponent picture(final String map, int size) {
        JLabel label = new JLabel(this) { // from class: com.faforever.neroxis.generator.bar.Replays.5
            final /* synthetic */ Replays this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(12, 14, 18));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (getIcon() != null) {
                    g.clipRect(0, 0, getWidth(), getHeight());
                    getIcon().paintIcon(this, g, (getWidth() - getIcon().getIconWidth()) / 2, (getHeight() - getIcon().getIconHeight()) / 2);
                } else {
                    g.setColor(BarLauncher.MUTED);
                    g.setFont(getFont().deriveFont(11.0f));
                    String text = BarLauncher.mapLabel(map);
                    if (g.getFontMetrics().stringWidth(text) > getWidth() - 12) {
                        text = text.substring(0, Math.max(1, Math.min(text.length(), 16))) + "…";
                    }
                    g.drawString(text, (getWidth() - g.getFontMetrics().stringWidth(text)) / 2, (getHeight() / 2) + 4);
                }
                g.setColor(BarLauncher.CARD_BORDER);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };
        label.setPreferredSize(new Dimension(size, size));
        label.setMinimumSize(new Dimension(size, size));
        String key = map + "@" + size;
        ImageIcon cached = this.pictures.get(key);
        if (cached != null) {
            label.setIcon(cached);
        } else if (this.host.mapInstalled(map)) {
            Thread loader = new Thread(() -> {
                BufferedImage image = this.host.preview(map);
                if (image != null) {
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(size, size, 4));
                    this.pictures.put(key, icon);
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(icon);
                        label.repaint();
                    });
                }
            }, "replay-picture");
            loader.setDaemon(true);
            loader.start();
        }
        return label;
    }

    private void showDetails(Replay replay) {
        String str;
        if (this.details != null) {
            this.details.dispose();
        }
        JDialog dialog = new JDialog(this.host.frame(), replay.title() + " - replay", false);
        this.details = dialog;
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BarLauncher.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));
        JPanel head = new JPanel(new BorderLayout(20, 0));
        head.setOpaque(false);
        head.add(picture(replay.map(), 128), "West");
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, 1));
        text.setOpaque(false);
        JComponent jLabel = new JLabel(replay.title());
        jLabel.setFont(jLabel.getFont().deriveFont(0, 26.0f));
        jLabel.setForeground(BarLauncher.TEXT);
        JComponent jLabel2 = new JLabel(BarLauncher.mapLabel(replay.map()) + (this.host.mapInstalled(replay.map()) ? "" : "  (map not installed)"));
        jLabel2.setForeground(BarLauncher.MUTED);
        JComponent jPanel = new JPanel(new GridLayout(2, 4, 18, 4));
        jPanel.setOpaque(false);
        jPanel.add(fact("Date", new SimpleDateFormat("d MMM yyyy").format(new Date(replay.time()))));
        jPanel.add(fact("Time", new SimpleDateFormat("HH:mm").format(new Date(replay.time()))));
        jPanel.add(fact("Game", length(replay.gameSeconds())));
        jPanel.add(fact("Real time", length(replay.wallSeconds())));
        jPanel.add(fact("Players", String.valueOf(replay.playing().size())));
        jPanel.add(fact("Engine", replay.engine()));
        jPanel.add(fact("Version", gameVersion(replay.gameType())));
        jPanel.add(fact("Size", Math.max(1L, replay.size() / 1024) + " KB"));
        JComponent[] jComponentArr = {jLabel, jLabel2, jPanel};
        int length = jComponentArr.length;
        for (int i = 0; i < length; i++) {
            JComponent part = jComponentArr[i];
            part.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            text.add(part);
            text.add(Box.createVerticalStrut(part == jLabel ? 4 : 12));
        }
        head.add(text, "Center");
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, 1));
        actions.setOpaque(false);
        JPanel id = new JPanel(new FlowLayout(2, 6, 0));
        id.setOpaque(false);
        if (!replay.gameId().isEmpty()) {
            id.add(chip(replay.shortId(), "The ID of this game", () -> {
                copy(replay.gameId());
            }));
            JButton copyId = new JButton("Copy game ID");
            copyId.setToolTipText("The whole ID, to search this game or hand it to somebody: " + replay.gameId());
            copyId.addActionListener(e -> {
                copy(replay.gameId());
                copyId.setText("Copied");
                Timer back = new Timer(1200, ev -> {
                    copyId.setText("Copy game ID");
                });
                back.setRepeats(false);
                back.start();
            });
            id.add(copyId);
        }
        JButton watch = new JButton("▶  Watch");
        watch.setBackground(BarLauncher.ACCENT);
        watch.setForeground(new Color(25, 18, 8));
        watch.setFont(watch.getFont().deriveFont(1, 14.0f));
        watch.setEnabled(this.host.engineInstalled(replay.engine()) && this.host.mapInstalled(replay.map()));
        if (watch.isEnabled()) {
            str = "Starts the game on this replay";
        } else {
            str = !this.host.engineInstalled(replay.engine()) ? "Engine " + replay.engine() + " is not installed" : "The map is not installed any more";
        }
        watch.setToolTipText(str);
        watch.addActionListener(e2 -> {
            this.host.watch(replay.file(), replay.engine());
        });
        JPanel watchRow = new JPanel(new FlowLayout(2, 0, 0));
        watchRow.setOpaque(false);
        watchRow.add(watch);
        actions.add(id);
        actions.add(Box.createVerticalGlue());
        actions.add(watchRow);
        head.add(actions, "East");
        root.add(head, "North");
        JPanel middle = new JPanel(new BorderLayout(0, 14));
        middle.setOpaque(false);
        JSeparator line = new JSeparator();
        line.setForeground(BarLauncher.CARD_BORDER);
        JPanel lineUpHead = new JPanel(new BorderLayout());
        lineUpHead.setOpaque(false);
        JLabel lineUpTitle = new JLabel("Line-up");
        lineUpTitle.setFont(lineUpTitle.getFont().deriveFont(0, 22.0f));
        lineUpTitle.setForeground(BarLauncher.TEXT);
        lineUpHead.add(lineUpTitle, "West");
        JButton result = new JButton("Show game result");
        JLabel verdict = new JLabel(" ");
        verdict.setForeground(BarLauncher.MUTED);
        JPanel resultRow = new JPanel(new FlowLayout(2, 10, 0));
        resultRow.setOpaque(false);
        resultRow.add(verdict);
        resultRow.add(result);
        lineUpHead.add(resultRow, "East");
        JComponent teams = lineUp(replay, Integer.MAX_VALUE, true);
        JPanel centred = new JPanel(new FlowLayout(1, 0, 10));
        centred.setOpaque(false);
        centred.add(teams);
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(line, "North");
        top.add(lineUpHead, "Center");
        middle.add(top, "North");
        JScrollPane scroll = new JScrollPane(centred, 20, 30);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        middle.add(scroll, "Center");
        List<Player> spectators = replay.players().stream().filter((v0) -> {
            return v0.spectator();
        }).toList();
        if (!spectators.isEmpty()) {
            JLabel watching = new JLabel("<html><span style='color:#8c94a3'>Spectators</span>&nbsp; " + String.join(", ", spectators.stream().map((v0) -> {
                return v0.name();
            }).toList()) + "</html>");
            watching.setForeground(BarLauncher.TEXT);
            middle.add(watching, "South");
        }
        root.add(middle, "Center");
        result.addActionListener(e3 -> {
            result.setEnabled(false);
            verdict.setText("Reading the end of the replay...");
            Thread thread = new Thread(() -> {
                int[] winners;
                try {
                    winners = winners(replay.file());
                } catch (IOException | RuntimeException e3) {
                    winners = null;
                }
                int[] won = winners;
                SwingUtilities.invokeLater(() -> {
                    if (won == null) {
                        verdict.setText("The file has no end - the game did not finish properly");
                    } else if (won.length == 0) {
                        verdict.setText("No result recorded - the game was not played to the end");
                    } else {
                        verdict.setText(" ");
                        markWinners(teams, won, replay);
                    }
                });
            }, "replay-result");
            thread.setDaemon(true);
            thread.start();
        });
        JPanel bottom = new JPanel(new FlowLayout(2, 8, 0));
        bottom.setOpaque(false);
        JButton folder = new JButton("Open the replay folder");
        folder.addActionListener(e4 -> {
            try {
                Desktop.getDesktop().open(folder().toFile());
            } catch (IOException | RuntimeException e4) {
                JOptionPane.showMessageDialog(dialog, "Could not open " + String.valueOf(folder()));
            }
        });
        JButton remove = new JButton("Delete replay");
        remove.addActionListener(e5 -> {
            if (JOptionPane.showConfirmDialog(dialog, "Delete this replay?", "Replays", 0) == 0) {
                try {
                    Files.deleteIfExists(replay.file());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Could not delete the replay: " + ex.getMessage());
                }
                dialog.dispose();
                refresh();
            }
        });
        bottom.add(folder);
        bottom.add(remove);
        root.add(bottom, "South");
        dialog.setContentPane(root);
        dialog.setSize(Math.min(960, this.host.frame().getWidth() - 40), Math.min(640, this.host.frame().getHeight() - 40));
        dialog.setLocationRelativeTo(this.host.frame());
        dialog.setVisible(true);
    }

    private static void markWinners(JComponent teams, int[] won, Replay replay) {
        for (JPanel jPanel : teams.getComponents()) {
            if (jPanel instanceof JPanel) {
                JPanel box = jPanel;
                Object clientProperty = box.getClientProperty("allyTeam");
                if (clientProperty instanceof Integer) {
                    Integer ally = (Integer) clientProperty;
                    boolean winner = Arrays.stream(won).anyMatch(number -> {
                        return number == ally.intValue();
                    });
                    if (winner) {
                        box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BarLauncher.ACCENT, 2), BorderFactory.createEmptyBorder(9, 13, 9, 13)));
                        JPanel component = box.getComponent(0);
                        if (component instanceof JPanel) {
                            JPanel rows = component;
                            if (rows.getComponentCount() > 0) {
                                JLabel component2 = rows.getComponent(0);
                                if (component2 instanceof JLabel) {
                                    JLabel heading = component2;
                                    heading.setText(heading.getText() + "   – Winner");
                                    heading.setForeground(BarLauncher.ACCENT);
                                }
                            }
                        }
                    } else {
                        for (Component row : box.getComponent(0).getComponents()) {
                            row.setForeground(BarLauncher.MUTED);
                        }
                    }
                }
            }
        }
        teams.revalidate();
        teams.repaint();
    }

    private static String gameVersion(String gameType) {
        String version = gameType.startsWith("Beyond All Reason") ? gameType.substring("Beyond All Reason".length()).trim() : gameType;
        return version.isEmpty() ? "-" : version;
    }
}
