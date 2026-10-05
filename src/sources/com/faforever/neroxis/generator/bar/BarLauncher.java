package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.exporter.bar.BarMapExporter;
import com.faforever.neroxis.generator.MapGeneratorCommand;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.map.placement.UnitPlacer;
import com.faforever.neroxis.util.dds.DDSHeader;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.SplashScreen;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher.class */
public final class BarLauncher {
    private static final String MAPFEATURES_COMMIT = "3b791639210dcc4f89875fce712bde8f460d38e1";
    private Tile selectedTile;
    private boolean tableMode;
    private boolean officialGames;
    private JPanel gamePeopleCard;
    private JPanel gameDetailsPanel;
    private String shownTileMap;
    private boolean inRoom;
    private final News news;
    private final Units units;
    private final StartScreen startScreen;
    private final Timer gamesRefresh;
    private volatile boolean engineRunning;
    private volatile long engineExited;
    private final PreviewPanel gamePreview;
    private final JLabel gameTitle;
    private final JLabel gameDetails;
    private final JButton playAgain;
    private final JButton removeGame;
    private JDialog createDialog;
    private boolean generatorGear;
    private BufferedImage shownSigil;
    private Runnable createUpdate;
    private boolean createForRoom;
    private JComponent createTypeColumn;
    private final JComboBox<String> createFilter;
    private final JComboBox<String> createSort;
    private final JComboBox<String> createGameType;
    private final JComboBox<String> createTerrain;
    private final JComboBox<String> createSize;
    private final JComboBox<String> createPlayers;
    private volatile Map<String, OfficialMap> officialInfo;
    private final Map<String, int[]> generatedSizes;
    private final JComboBox<String> createVersions;
    private boolean fillingVersions;
    private volatile Map<String, String> officialMaps;
    private final Map<String, MapEntry> officialEntries;
    private volatile Map<String, Integer> popularity;
    private final Properties versionChoices;
    private final JTextField gameName;
    private final JComboBox<String> gameMode;
    private final JTextField playerName;
    private final JTextField friends;
    private final JTextField password;
    private final JCheckBox coop;
    private final JSpinner officialPerTeam;
    private final JLabel officialLabel;
    private final JTextField createSearch;
    private final DefaultListModel<MapEntry> createModel;
    private final JList<MapEntry> createList;
    private final CardLayout createCards;
    private final JPanel createSide;
    private final PreviewPanel createPreview;
    private final JLabel createMapTitle;
    private final JLabel createMapDetails;
    private final JLabel createStatus;
    private final JButton createGame;
    private final JButton generateOnly;
    private Path newestGenerated;
    static volatile String preflight;
    static final String DISPLAY_FULL_SCREEN = "fullscreen";
    static final String DISPLAY_MAXIMIZED = "maximized";
    static final String DISPLAY_WINDOW = "window";
    private String displayMode;
    private String windowedMode;
    private JPanel headerWindowButtons;
    private boolean restoringDisplay;
    private boolean windowTracked;
    private static final String LAUNCHER_JVM_OPTIONS = "-Xms48m -Xmx1g -XX:+UseG1GC -XX:G1PeriodicGCInterval=20000 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:TieredStopAtLevel=1 -XX:+AutoCreateSharedArchive \"-XX:SharedArchiveFile=%ROOT%cache\\barmapstudio.jsa\"";
    private final Replays replays;
    private final Map<String, BufferedImage> officialThumbs;
    private final Set<String> officialThumbsLoading;
    private boolean onStartScreen;
    private boolean gameRefreshing;
    private MapStudioPanel studioPanel;
    static final Color BACKGROUND = new Color(11, 14, 20);
    static final Color CARD = new Color(19, 24, 34);
    static final Color CARD_BORDER = new Color(38, 50, 70);
    static final Color GLASS = new Color(15, 20, 30, 210);
    static final Color TEXT = new Color(240, 244, 250);
    static final Color MUTED = new Color(140, 155, 180);
    static final Color ACCENT = new Color(0, 229, 255);
    static final Color GREEN = new Color(0, 255, 136);
    static final Color[] TEAM_COLORS = {new Color(0, 229, 255), new Color(255, 56, 92), GREEN, new Color(255, 159, 28), new Color(190, 120, 255), new Color(70, 220, 220), new Color(255, 150, 200), new Color(200, 200, 200)};
    private static final String[] LAND_STYLES = {"BASIC", "VALLEY", "LAND_BRIDGE", "LITTLE_MOUNTAIN", "MOUNTAIN_RANGE", "MULTILEVEL", "FRACTAL_LAND", "DROP_PLATEAU", "CENTER_LAKE"};
    private static final String[] STYLES = {"Random land map", "Random (anything)", "BASIC", "VALLEY", "LAND_BRIDGE", "LITTLE_MOUNTAIN", "MOUNTAIN_RANGE", "MULTILEVEL", "DROP_PLATEAU", "CENTER_LAKE", "FRACTAL_LAND", "FRACTAL_PLATEAU", "ONE_ISLAND", "BIG_ISLANDS", "SMALL_ISLANDS", "FRACTAL_NAVY", "RIVERS", "RIVERS_AND_OCEANS", "FLOODED", "HIGH_RECLAIM", "LOW_MEX", "SETONISH", "Custom..."};
    private static final String[] TERRAIN_STYLES = {"Random", "BASIC", "BIG_ISLANDS", "CENTER_LAKE", "DROP_PLATEAU", "FLOODED", "LAND_BRIDGE", "LITTLE_MOUNTAIN", "MOUNTAIN_RANGE", "MULTILEVEL_LAST", "ONE_ISLAND", "SMALL_ISLANDS", "VALLEY", "RIVERS", "RIVERS_AND_OCEANS", "FRACTAL_LAND", "FRACTAL_PLATEAU", "FRACTAL_NAVY", "SETONS"};
    private static final String[] BIOMES = {"Random", "BRIMSTONE", "CRYSTALLINE", "DESERT", "EARLYAUTUMN", "FRITHEN", "MARS", "MOONLIGHT", "PRAYER", "STONES", "SUNSET", "SYRTIS", "WINDINGRIVER", "WONDER"};
    private static final String[] PROP_STYLES = {"Random", "BASIC", "BOULDER_FIELD", "ENEMY_CIV", "HIGH_RECLAIM", "LARGE_BATTLE", "NAVY_WRECKS", "NEUTRAL_CIV", "ROCK_FIELD", "SMALL_BATTLE"};
    private static final String[] SYMMETRIES = {"Any", "POINT2", "POINT3", "POINT4", "XZ", "ZX", "X", "Z", "QUAD", "DIAG"};
    private static final String[] SIZES = {"Recommended", "8x8", "12x12", "16x16", "20x20", "24x24", "32x32"};
    private static final int[] SIZE_VALUES = {0, PreviewGenerator.PREVIEW_SIZE, 384, DDSHeader.CUBEMAP_FLAG, 640, 768, DDSHeader.CUBEMAP_POSITIVEX_FLAG};
    private static final Map<String, int[]> STYLE_LIMITS = Map.ofEntries(Map.entry("VALLEY", new int[]{DDSHeader.CUBEMAP_FLAG, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 0, 16, 16}), Map.entry("LAND_BRIDGE", new int[]{768, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 2, 4, 16}), Map.entry("MOUNTAIN_RANGE", new int[]{PreviewGenerator.PREVIEW_SIZE, 640, 0, 16, 16}), Map.entry("CENTER_LAKE", new int[]{384, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 0, 16, 16}), Map.entry("ONE_ISLAND", new int[]{384, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 0, 16, 16}), Map.entry("BIG_ISLANDS", new int[]{768, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 0, 16, 16}), Map.entry("SMALL_ISLANDS", new int[]{768, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 0, 16, 16}), Map.entry("FLOODED", new int[]{384, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 0, 16, 16}), Map.entry("LOW_MEX", new int[]{PreviewGenerator.PREVIEW_SIZE, 640, 2, 2, 4}), Map.entry("SETONISH", new int[]{DDSHeader.CUBEMAP_FLAG, DDSHeader.CUBEMAP_POSITIVEX_FLAG, 2, 2, 16}));
    private static final Object[][] FORMATS = {new Object[]{"1v1", 1, 2}, new Object[]{"2v2", 2, 2}, new Object[]{"3v3", 3, 2}, new Object[]{"4v4", 4, 2}, new Object[]{"5v5", 5, 2}, new Object[]{"8v8", 8, 2}, new Object[]{"FFA 4", 4, 0}, new Object[]{"FFA 8", 8, 0}};
    private static final int[] PLAYER_CHOICES = {0, 2, 4, 6, 8, 10, 12, 16};
    private static final Pattern MAP_VERSION = Pattern.compile("^(.*?)[ _-]+[vV]?\\d+(?:\\.\\d+)*\\w?$");
    private static final ImageIcon EMPTY_ICON = new ImageIcon(new BufferedImage(52, 52, 1));
    private static final ImageIcon GENERATOR_ICON = generatorIcon();
    private static final MapEntry GENERATOR_ENTRY = new MapEntry(null, "Map generator", "A new random map with the settings on the right", List.of(), "", true);
    static final Color LIVE_GAME = new Color(255, 206, 64);
    static final Color OPEN_GAME = GREEN;
    static final Color LOCKED_GAME = new Color(232, 78, 70);
    private static final BufferedImage[] SIGILS = new BufferedImage[6];
    private final JLabel styleNote = new JLabel(" ");
    private final Path home = locateHome();
    private final JFrame frame = new JFrame("BAR Map Studio");
    private final List<JToggleButton> formatButtons = new ArrayList();
    private final JSpinner playersPerTeam = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    private final JComboBox<String> teams = new JComboBox<>(new String[]{"2 teams", "3 teams", "4 teams", "Free for all"});
    private final JComboBox<String> size = new JComboBox<>(SIZES);
    private final JComboBox<String> style = new JComboBox<>(STYLES);
    private final JComboBox<String> terrain = new JComboBox<>(TERRAIN_STYLES);
    private final JComboBox<String> biome = new JComboBox<>(BIOMES);
    private final JComboBox<String> props = new JComboBox<>(PROP_STYLES);
    private final JSlider resourceDensity = new JSlider(0, 100, 50);
    private final JSlider treeDensity = new JSlider(0, 100, 50);
    private final JComboBox<String> symmetry = new JComboBox<>(SYMMETRIES);
    private final JSpinner metalBase = new JSpinner(new SpinnerNumberModel(2.0d, 0.5d, 10.0d, 0.1d));
    private final JSpinner metalMax = new JSpinner(new SpinnerNumberModel(3.5d, 0.5d, 10.0d, 0.1d));
    private final JCheckBox metalUniform = new JCheckBox("All metal spots equal");
    private final JSlider reclaim = new JSlider(0, 80, 10);
    private final JLabel reclaimLabel = new JLabel();
    private final JLabel resourceLabel = new JLabel();
    private final JComboBox<String> startMexes = new JComboBox<>(new String[]{"Auto (3 to 5)", "1", "2", "3", "4", "5"});
    private final JSpinner windMin = new JSpinner(new SpinnerNumberModel(3, 0, 30, 1));
    private final JSpinner windMax = new JSpinner(new SpinnerNumberModel(16, 0, 30, 1));
    private final JSpinner tidal = new JSpinner(new SpinnerNumberModel(18, 0, 60, 1));
    private final JCheckBox biomeTrees = new JCheckBox("Trees that fit the biome", true);
    private final JTextField mapName = new JTextField();
    private final JTextField seed = new JTextField();
    private final JTextField barFolder = new JTextField();
    private final JLabel barStatus = new JLabel();
    private final JButton generate = new JButton("GENERATE MAP");
    private final JProgressBar progress = new JProgressBar();
    private final JButton play = new JButton("Play vs AI");
    private final JButton delete = new JButton("Delete");
    private final JTextField search = new JTextField();
    private final JTextArea log = new JTextArea();
    private final JLabel title = new JLabel("No map selected");
    private final JPanel chips = new JPanel(new FlowLayout(0, 6, 0));
    private final PreviewPanel preview = new PreviewPanel("Pick a map from the library");
    private final DefaultListModel<MapEntry> libraryModel = new DefaultListModel<>();
    private final JList<MapEntry> library = new JList<>(this.libraryModel);
    private final List<MapEntry> allMaps = new ArrayList();
    private final Map<Path, ImageIcon> thumbnails = new ConcurrentHashMap();
    private final JPanel customPanel = new JPanel(new GridLayout(0, 2, 8, 8));
    private final JPanel updateBar = new JPanel(new BorderLayout());
    private final CardLayout views = new CardLayout();
    private final JPanel viewPanel = new JPanel(this.views);
    private final List<JToggleButton> navigation = new ArrayList();
    private final Map<String, Map<Integer, float[][]>> startBoxes = new ConcurrentHashMap();
    private final JPanel tiles = new ScrollablePanel();
    private final List<GameRecord> games = new ArrayList();
    private volatile List<Tile> officialTiles = List.of();
    private volatile String officialNote = "Loading the games of the official servers...";
    private final Timer officialRefresh = new Timer(45000, e -> {
        loadOfficialBattles();
    });
    private final JLabel gamePeople = new JLabel(" ");
    private final JLabel onlineStatus = new JLabel();
    private final LobbyUi lobby = new LobbyUi(new LobbyUi.Host(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.1
        final /* synthetic */ BarLauncher this$0;

        {
            Objects.requireNonNull(this);
            this.this$0 = this;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public JFrame frame() {
            return this.this$0.frame;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public JComponent barFolderSetting() {
            return this.this$0.barFolderRow();
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public Path barData() {
            return Path.of(this.this$0.barFolder.getText().trim(), new String[0]);
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public Path home() {
            return this.this$0.home;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public boolean hasMap(String mapName) {
            return this.this$0.findMap(mapName) != null || this.this$0.officialFilePresent(mapName);
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public Path mapArchive(String mapName) {
            MapEntry entry = this.this$0.findMap(mapName);
            if (entry != null) {
                return entry.archive();
            }
            return null;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public BufferedImage mapImage(String mapName) {
            MapEntry entry = this.this$0.findMap(mapName);
            if (entry != null) {
                return this.this$0.readPreview(entry);
            }
            OfficialMap info = this.this$0.officialInfo.get(mapName.toLowerCase(Locale.ROOT));
            if (info != null) {
                return this.this$0.officialPreview(info);
            }
            return null;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public BarInstall.Marks mapMarks(String mapName) {
            MapEntry entry = this.this$0.findMap(mapName);
            return entry != null ? this.this$0.readMarks(entry) : BarInstall.Marks.NONE;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public BufferedImage gameImage(String file) {
            return BarInstall.readGameIcon(Path.of(this.this$0.barFolder.getText().trim(), new String[0]), file);
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public String gameText(String file) {
            return BarInstall.readGameText(Path.of(this.this$0.barFolder.getText().trim(), new String[0]), file);
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public String displayMode() {
            return this.this$0.displayMode;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public void setDisplay(String mode, int screen) {
            if (this.this$0.frame.isDisplayable()) {
                this.this$0.setDisplay(mode, screen);
            }
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public List<String> screenNames() {
            List<String> names = new ArrayList<>();
            GraphicsDevice[] devices = BarLauncher.screens();
            for (int i = 0; i < devices.length; i++) {
                Rectangle bounds = devices[i].getDefaultConfiguration().getBounds();
                names.add("Screen " + (i + 1) + "  (" + devices[i].getDisplayMode().getWidth() + " x " + devices[i].getDisplayMode().getHeight() + ((bounds.x == 0 && bounds.y == 0) ? ", main" : "") + ")");
            }
            return names;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public void setBackgroundBrightness(int brightness) {
            BackgroundPanel.setBrightness(brightness);
            this.this$0.frame.repaint();
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public boolean changeMap() {
            return this.this$0.openChangeMap();
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public List<String> mapNames() {
            return this.this$0.allMaps.stream().map((v0) -> {
                return v0.name();
            }).toList();
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public List<float[]> mapStarts(String mapName) {
            MapEntry entry = this.this$0.findMap(mapName);
            return entry != null ? entry.starts() : List.of();
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public void mapsChanged() {
            this.this$0.refreshLibrary(null);
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public boolean startEngine(Path script, String engineVersion) {
            return this.this$0.startEngine(script, engineVersion, true);
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public boolean engineRunning() {
            return this.this$0.engineRunning;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public long millisSinceEngineExit() {
            if (this.this$0.engineExited == 0) {
                return Long.MAX_VALUE;
            }
            return System.currentTimeMillis() - this.this$0.engineExited;
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public void lobbyChanged() {
            this.this$0.gamesRefresh.restart();
        }

        @Override // com.faforever.neroxis.generator.bar.LobbyUi.Host
        public void showRoom(boolean visible) {
            this.this$0.inRoom = visible;
            if (((JToggleButton) this.this$0.navigation.getFirst()).isSelected()) {
                this.this$0.views.show(this.this$0.viewPanel, visible ? "ROOM" : "PLAY");
            }
        }
    });

    public BarLauncher() {
        LobbyUi lobbyUi = this.lobby;
        Objects.requireNonNull(lobbyUi);
        Supplier supplier = lobbyUi::mapServiceUrl;
        Runnable runnable = () -> {
            this.lobby.showProfile(3);
        };
        LobbyUi lobbyUi2 = this.lobby;
        Objects.requireNonNull(lobbyUi2);
        this.news = new News(supplier, runnable, lobbyUi2::reportBug);
        this.units = new Units(() -> {
            return Path.of(this.barFolder.getText().trim(), new String[0]);
        });
        this.startScreen = new StartScreen(() -> {
            return Path.of(this.barFolder.getText().trim(), new String[0]);
        }, this.lobby.flag("start.screen", true), shown -> {
            this.lobby.remember("start.screen", String.valueOf(shown));
        }, this::leaveStartScreen);
        this.gamesRefresh = new Timer(180, e -> {
            showGames();
        });
        this.gamesRefresh.setRepeats(false);
        this.gamePreview = new PreviewPanel("Your games show up here");
        this.gameTitle = new JLabel(" ");
        this.gameDetails = new JLabel(" ");
        this.playAgain = new JButton("PLAY AGAIN");
        this.removeGame = new JButton("Remove from list");
        this.createFilter = new JComboBox<>(new String[]{"All maps", "Generated maps", "Official BAR maps", "Installed"});
        this.createSort = new JComboBox<>(new String[]{"A - Z", "Most played here", "Popular in BAR"});
        this.createGameType = new JComboBox<>(new String[]{"Any game type", "Team", "1v1", "FFA", "PvE"});
        this.createTerrain = new JComboBox<>(new String[]{"Any terrain"});
        this.createSize = new JComboBox<>(new String[]{"Any size", "Small (up to 10)", "Medium (11 - 16)", "Large (17 - 24)", "Huge (25 and more)"});
        this.createPlayers = new JComboBox<>(new String[]{"Any players", "2 players", "4 players", "6 players", "8 players", "10 players", "12 players", "16 players"});
        this.officialInfo = Map.of();
        this.generatedSizes = new ConcurrentHashMap();
        this.createVersions = new JComboBox<>();
        this.officialMaps = Map.of();
        this.officialEntries = new HashMap();
        this.popularity = Map.of();
        this.versionChoices = new Properties();
        this.gameName = new JTextField("My game");
        this.gameMode = new JComboBox<>(new String[]{"Online game (public room)", "Offline skirmish vs AI"});
        this.playerName = new JTextField("Player");
        this.friends = new JTextField();
        this.password = new JTextField();
        this.coop = new JCheckBox("All humans in one team against the AI", true);
        this.officialPerTeam = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        this.officialLabel = new JLabel("Players per team");
        this.createSearch = new JTextField();
        this.createModel = new DefaultListModel<>();
        this.createList = new JList<>(this.createModel);
        this.createCards = new CardLayout();
        this.createSide = new JPanel(this.createCards);
        this.createPreview = new PreviewPanel("Loading the minimap...");
        this.createMapTitle = new JLabel(" ");
        this.createMapDetails = new JLabel(" ");
        this.createStatus = new JLabel(" ");
        this.createGame = new JButton("CREATE GAME");
        this.generateOnly = new JButton("Generate only");
        this.displayMode = DISPLAY_WINDOW;
        this.windowedMode = DISPLAY_MAXIMIZED;
        this.replays = new Replays(new Replays.Host(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.11
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            @Override // com.faforever.neroxis.generator.bar.Replays.Host
            public Path data() {
                return Path.of(this.this$0.barFolder.getText().trim(), new String[0]);
            }

            @Override // com.faforever.neroxis.generator.bar.Replays.Host
            public BufferedImage preview(String map) {
                MapEntry entry = this.this$0.findMap(map);
                if (entry != null) {
                    return this.this$0.readPreview(entry);
                }
                return null;
            }

            @Override // com.faforever.neroxis.generator.bar.Replays.Host
            public boolean mapInstalled(String map) {
                return this.this$0.findMap(map) != null;
            }

            @Override // com.faforever.neroxis.generator.bar.Replays.Host
            public boolean engineInstalled(String engine) {
                return !engine.isEmpty() && Platform.hasEngine(data().resolve("engine").resolve("recoil_" + engine));
            }

            @Override // com.faforever.neroxis.generator.bar.Replays.Host
            public boolean watch(Path replay, String engine) {
                return !this.this$0.engineRunning && this.this$0.startEngine(replay, engine);
            }

            @Override // com.faforever.neroxis.generator.bar.Replays.Host
            public JFrame frame() {
                return this.this$0.frame;
            }
        });
        this.officialThumbs = new ConcurrentHashMap();
        this.officialThumbsLoading = ConcurrentHashMap.newKeySet();
    }

    private void loadOfficialBattles() {
        Thread loader = new Thread(() -> {
            boolean z;
            int iIntValue;
            String strValueOf;
            String str;
            try {
                try {
                    try {
                        HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build().send(HttpRequest.newBuilder(URI.create("https://api.bar-rts.com/battles")).header("User-Agent", "BAR Map Studio").timeout(Duration.ofSeconds(20L)).build(), HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            Object patt0$temp = MiniJson.parse((String) response.body());
                            if (patt0$temp instanceof List) {
                                List<?> battles = (List) patt0$temp;
                                List<Tile> found = new ArrayList<>();
                                for (Object entry : battles) {
                                    if (entry instanceof Map) {
                                        Map<?, ?> battle = (Map) entry;
                                        Object patt1$temp = battle.get("players");
                                        if (patt1$temp instanceof List) {
                                            List<?> people = (List) patt1$temp;
                                            List<String> names = new ArrayList<>();
                                            int playing = 0;
                                            for (Object person : people) {
                                                if (person instanceof Map) {
                                                    Map<?, ?> fields = (Map) person;
                                                    Object patt2$temp = fields.get("username");
                                                    if (patt2$temp instanceof String) {
                                                        String name = (String) patt2$temp;
                                                        boolean spectator = fields.get("teamId") == null || "Spectating".equals(fields.get("gameStatus"));
                                                        playing += spectator ? 0 : 1;
                                                        Object patt3$temp = fields.get("skill");
                                                        if (patt3$temp instanceof String) {
                                                            String skill = (String) patt3$temp;
                                                            if (spectator) {
                                                                str = "";
                                                            } else {
                                                                str = "  " + skill.replaceAll("[^0-9.]", "");
                                                            }
                                                        } else {
                                                            str = "";
                                                        }
                                                        names.add(name + str + (spectator ? "  (watching)" : ""));
                                                    }
                                                }
                                            }
                                            if (!names.isEmpty()) {
                                                Object patt2$temp2 = battle.get("founder");
                                                if (patt2$temp2 instanceof Map) {
                                                    Map<?, ?> founder = (Map) patt2$temp2;
                                                    Object patt3$temp2 = founder.get("status");
                                                    if (patt3$temp2 instanceof Map) {
                                                        Map<?, ?> status = (Map) patt3$temp2;
                                                        if (Boolean.TRUE.equals(status.get("ingame"))) {
                                                            z = true;
                                                        } else {
                                                            z = false;
                                                        }
                                                    } else {
                                                        z = false;
                                                    }
                                                } else {
                                                    z = false;
                                                }
                                                boolean running = z;
                                                boolean locked = Boolean.TRUE.equals(battle.get("locked")) || Boolean.TRUE.equals(battle.get("passworded"));
                                                Object patt2$temp3 = battle.get("maxPlayers");
                                                if (patt2$temp3 instanceof Double) {
                                                    Double number = (Double) patt2$temp3;
                                                    iIntValue = number.intValue();
                                                } else {
                                                    iIntValue = 16;
                                                }
                                                int max = iIntValue;
                                                String title = String.valueOf(battle.get("title"));
                                                Object patt2$temp4 = battle.get("founder");
                                                if (patt2$temp4 instanceof Map) {
                                                    Map<?, ?> founder2 = (Map) patt2$temp4;
                                                    strValueOf = String.valueOf(founder2.get("username"));
                                                } else {
                                                    strValueOf = "";
                                                }
                                                String founderName = strValueOf;
                                                found.add(new Tile(founderName, playing + "/" + max, locked, names, "bar:" + String.valueOf(battle.get("battleId")), title, String.valueOf(battle.get("map")), names.size() + " people  ·  official server", running ? "game is running" : locked ? "locked" : "open", running ? LIVE_GAME : locked ? LOCKED_GAME : OPEN_GAME, "", "OPEN BAR TO JOIN", this::openBar, null));
                                            }
                                        }
                                    }
                                }
                                found.sort(Comparator.comparingInt(tile -> {
                                    return -tile.people().size();
                                }));
                                this.officialTiles = found;
                                this.officialNote = found.isEmpty() ? "Nobody is playing on the official servers right now." : "";
                                SwingUtilities.invokeLater(() -> {
                                    if (this.officialGames) {
                                        showGames();
                                    }
                                });
                                return;
                            }
                        }
                        this.officialNote = "The list of official games is not available right now.";
                        SwingUtilities.invokeLater(() -> {
                            if (this.officialGames) {
                                showGames();
                            }
                        });
                    } catch (IOException | RuntimeException e) {
                        this.officialNote = "The list of official games is not available right now.";
                        SwingUtilities.invokeLater(() -> {
                            if (this.officialGames) {
                                showGames();
                            }
                        });
                    }
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    SwingUtilities.invokeLater(() -> {
                        if (this.officialGames) {
                            showGames();
                        }
                    });
                }
            } catch (Throwable th) {
                SwingUtilities.invokeLater(() -> {
                    if (this.officialGames) {
                        showGames();
                    }
                });
                throw th;
            }
        }, "official-battles");
        loader.setDaemon(true);
        loader.start();
    }

    /* JADX WARN: Code duplicated, block: B:14:0x006c A[Catch: IOException -> 0x0079, TryCatch #0 {IOException -> 0x0079, blocks: (B:11:0x0038, B:13:0x0043, B:14:0x006c), top: B:19:0x0038 }] */
    private void openBar() {
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        Path launcherExe = (!Platform.WINDOWS || data.getParent() == null) ? null : data.getParent().resolve("Beyond-All-Reason.exe");
        if (launcherExe == null) {
            JOptionPane.showMessageDialog(this.frame, "Open Beyond All Reason and join the game from its multiplayer list.");
        } else {
            try {
                if (Files.isRegularFile(launcherExe, new LinkOption[0])) {
                    new ProcessBuilder(launcherExe.toString()).directory(launcherExe.getParent().toFile()).start();
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Open Beyond All Reason and join the game from its multiplayer list.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this.frame, "Could not start Beyond All Reason: " + e.getMessage());
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$OfficialMap.class */
    private static final class OfficialMap extends Record {
        private final String springName;
        private final String displayName;
        private final String description;
        private final String author;
        private final int width;
        private final int height;
        private final int playersMin;
        private final int playersMax;
        private final List<String> gameTypes;
        private final List<String> terrain;
        private final List<String> pools;
        private final String previewUrl;
        private final String fileName;

        private OfficialMap(String springName, String displayName, String description, String author, int width, int height, int playersMin, int playersMax, List<String> gameTypes, List<String> terrain, List<String> pools, String previewUrl, String fileName) {
            this.springName = springName;
            this.displayName = displayName;
            this.description = description;
            this.author = author;
            this.width = width;
            this.height = height;
            this.playersMin = playersMin;
            this.playersMax = playersMax;
            this.gameTypes = gameTypes;
            this.terrain = terrain;
            this.pools = pools;
            this.previewUrl = previewUrl;
            this.fileName = fileName;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, OfficialMap.class), OfficialMap.class, "springName;displayName;description;author;width;height;playersMin;playersMax;gameTypes;terrain;pools;previewUrl;fileName", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->springName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->displayName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->width:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->height:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->playersMin:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->playersMax:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->gameTypes:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->terrain:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->pools:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->previewUrl:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->fileName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, OfficialMap.class), OfficialMap.class, "springName;displayName;description;author;width;height;playersMin;playersMax;gameTypes;terrain;pools;previewUrl;fileName", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->springName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->displayName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->width:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->height:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->playersMin:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->playersMax:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->gameTypes:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->terrain:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->pools:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->previewUrl:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->fileName:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, OfficialMap.class, Object.class), OfficialMap.class, "springName;displayName;description;author;width;height;playersMin;playersMax;gameTypes;terrain;pools;previewUrl;fileName", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->springName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->displayName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->author:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->width:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->height:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->playersMin:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->playersMax:I", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->gameTypes:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->terrain:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->pools:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->previewUrl:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$OfficialMap;->fileName:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String springName() {
            return this.springName;
        }

        public String displayName() {
            return this.displayName;
        }

        public String description() {
            return this.description;
        }

        public String author() {
            return this.author;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        public int playersMin() {
            return this.playersMin;
        }

        public int playersMax() {
            return this.playersMax;
        }

        public List<String> gameTypes() {
            return this.gameTypes;
        }

        public List<String> terrain() {
            return this.terrain;
        }

        public List<String> pools() {
            return this.pools;
        }

        public String previewUrl() {
            return this.previewUrl;
        }

        public String fileName() {
            return this.fileName;
        }
    }

    private boolean officialFilePresent(String mapName) {
        OfficialMap info = this.officialInfo.get(mapName.toLowerCase(Locale.ROOT));
        return (info == null || info.fileName().isBlank() || !Files.isRegularFile(Path.of(this.barFolder.getText().trim(), new String[0]).resolve("maps").resolve(info.fileName()), new LinkOption[0])) ? false : true;
    }

    private static String baseName(String mapName) {
        Matcher matcher = MAP_VERSION.matcher(mapName.trim());
        return (matcher.matches() ? matcher.group(1) : mapName.trim()).toLowerCase(Locale.ROOT);
    }

    private Path versionChoicesFile() {
        return this.home.resolve("map-versions.properties");
    }

    private void loadMapLists() {
        Thread loader = new Thread(() -> {
            Object obj;
            String str;
            String str2;
            HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
            String list = cachedDownload(http, "map_list.validated.json");
            String lobbyMaps = cachedDownload(http, "lobby_maps.validated.json");
            if (list != null) {
                try {
                    Map<String, Object[]> extras = new HashMap<>();
                    if (lobbyMaps != null) {
                        Object patt0$temp = MiniJson.parse(lobbyMaps);
                        if (patt0$temp instanceof List) {
                            List<?> entries = (List) patt0$temp;
                            for (Object entry : entries) {
                                if (entry instanceof Map) {
                                    Map<?, ?> fields = (Map) entry;
                                    Object patt1$temp = fields.get("springName");
                                    if (patt1$temp instanceof String) {
                                        String name = (String) patt1$temp;
                                        Object patt2$temp = fields.get("images");
                                        if (patt2$temp instanceof Map) {
                                            Map<?, ?> images = (Map) patt2$temp;
                                            obj = images.get("preview");
                                        } else {
                                            obj = null;
                                        }
                                        Object preview = obj;
                                        Object[] objArr = new Object[4];
                                        objArr[0] = Integer.valueOf(number(fields.get("mapWidth")));
                                        objArr[1] = Integer.valueOf(number(fields.get("mapHeight")));
                                        if (preview instanceof String) {
                                            String url = (String) preview;
                                            str = url;
                                        } else {
                                            str = "";
                                        }
                                        objArr[2] = str;
                                        Object patt2$temp2 = fields.get("filename");
                                        if (patt2$temp2 instanceof String) {
                                            String file = (String) patt2$temp2;
                                            str2 = file;
                                        } else {
                                            str2 = "";
                                        }
                                        objArr[3] = str2;
                                        extras.put(name, objArr);
                                    }
                                }
                            }
                        }
                    }
                    Map<String, OfficialMap> found = new HashMap<>();
                    Map<String, String> names = new HashMap<>();
                    TreeMap<String, Integer> terrainCounts = new TreeMap<>();
                    Object patt1$temp2 = MiniJson.parse(list);
                    if (patt1$temp2 instanceof Map) {
                        Map<?, ?> maps = (Map) patt1$temp2;
                        for (Object value : maps.values()) {
                            if (value instanceof Map) {
                                Map<?, ?> fields2 = (Map) value;
                                Object patt2$temp3 = fields2.get("springName");
                                if (patt2$temp3 instanceof String) {
                                    String name2 = (String) patt2$temp3;
                                    Object[] extra = extras.getOrDefault(name2, new Object[]{0, 0, "", ""});
                                    List<String> terrain = strings(fields2.get("terrain"));
                                    terrain.forEach(tag -> {
                                        terrainCounts.merge(tag, 1, (v0, v1) -> {
                                            return Integer.sum(v0, v1);
                                        });
                                    });
                                    found.put(name2.toLowerCase(Locale.ROOT), new OfficialMap(name2, String.valueOf(fields2.get("displayName")), String.valueOf(fields2.get("description")), String.valueOf(fields2.get("author")), ((Integer) extra[0]).intValue(), ((Integer) extra[1]).intValue(), number(fields2.get("minPlayerCount")), number(fields2.get("playerCount")), strings(fields2.get("gameType")), terrain, strings(fields2.get("mapLists")), (String) extra[2], (String) extra[3]));
                                    names.put(name2.toLowerCase(Locale.ROOT), name2);
                                }
                            }
                        }
                    }
                    if (!found.isEmpty()) {
                        this.officialInfo = found;
                        this.officialMaps = names;
                        this.officialEntries.clear();
                        List<String> tags = new ArrayList<>(terrainCounts.keySet());
                        tags.sort(Comparator.comparingInt(tag2 -> {
                            return -((Integer) terrainCounts.get(tag2)).intValue();
                        }).thenComparing(tag3 -> {
                            return tag3;
                        }));
                        SwingUtilities.invokeLater(() -> {
                            if (this.createTerrain.getItemCount() <= 1) {
                                tags.forEach(tag4 -> {
                                    this.createTerrain.addItem(tag4);
                                });
                            }
                        });
                    }
                } catch (ClassCastException | IllegalArgumentException e) {
                }
            }
            this.popularity = Map.of();
            SwingUtilities.invokeLater(() -> {
                if (this.createDialog != null) {
                    filterCreateList();
                }
                this.lobby.recheckMap();
            });
        }, "map-lists");
        loader.setDaemon(true);
        loader.start();
    }

    private String cachedDownload(HttpClient http, String file) {
        Path cache = this.home.resolve("cache").resolve("official-" + file);
        try {
            if (Files.isRegularFile(cache, new LinkOption[0]) && System.currentTimeMillis() - Files.getLastModifiedTime(cache, new LinkOption[0]).toMillis() < 43200000) {
                return Files.readString(cache);
            }
        } catch (IOException e) {
        }
        try {
            HttpResponse<String> response = http.send(HttpRequest.newBuilder(URI.create("https://maps-metadata.beyondallreason.dev/latest/" + file)).timeout(Duration.ofSeconds(30L)).build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 && ((String) response.body()).contains("springName")) {
                Files.createDirectories(cache.getParent(), new FileAttribute[0]);
                Files.writeString(cache, (CharSequence) response.body(), new OpenOption[0]);
                return (String) response.body();
            }
        } catch (IOException | RuntimeException e2) {
        } catch (InterruptedException e3) {
            Thread.currentThread().interrupt();
        }
        try {
            if (Files.isRegularFile(cache, new LinkOption[0])) {
                return Files.readString(cache);
            }
            return null;
        } catch (IOException e4) {
            return null;
        }
    }

    private static int number(Object value) {
        if (!(value instanceof Double)) {
            return 0;
        }
        Double number = (Double) value;
        return (int) Math.round(number.doubleValue());
    }

    private static List<String> strings(Object value) {
        List<String> result = new ArrayList<>();
        if (value instanceof List) {
            List<?> list = (List) value;
            list.forEach(item -> {
                result.add(String.valueOf(item));
            });
        }
        return result;
    }

    private OfficialMap officialInfo(MapEntry entry) {
        if (entry == null || entry == GENERATOR_ENTRY) {
            return null;
        }
        return this.officialInfo.get(entry.name().toLowerCase(Locale.ROOT));
    }

    private boolean passesFilters(MapEntry entry) {
        boolean z;
        boolean zContains;
        OfficialMap info = officialInfo(entry);
        int gameType = this.createGameType.getSelectedIndex();
        if (gameType > 0) {
            String wanted = new String[]{"", "team", "1v1", "ffa", "pve"}[gameType];
            if (info != null) {
                zContains = info.gameTypes().contains(wanted);
            } else {
                zContains = entry.generated() && (wanted.equals("team") || (wanted.equals("1v1") && entry.starts().size() == 2));
            }
            boolean fits = zContains;
            if (!fits) {
                return false;
            }
        }
        if (this.createTerrain.getSelectedIndex() > 0 && (info == null || !info.terrain().contains(String.valueOf(this.createTerrain.getSelectedItem())))) {
            return false;
        }
        int sizeChoice = this.createSize.getSelectedIndex();
        if (sizeChoice > 0) {
            int side = 0;
            if (info != null) {
                side = Math.max(info.width(), info.height());
            } else if (entry.archive() != null) {
                int[] size = this.generatedSizes.computeIfAbsent(entry.name(), key -> {
                    int[] read = BarInstall.readMapSize(new BarInstall.InstalledMap(entry.archive(), entry.name(), "", "", entry.mapFile()));
                    return read != null ? read : new int[]{0, 0};
                });
                side = Math.max(size[0], size[1]);
            }
            int[][] ranges = {new int[]{0, 0}, new int[]{1, 10}, new int[]{11, 16}, new int[]{17, 24}, new int[]{25, 999}};
            if (side < ranges[sizeChoice][0] || side > ranges[sizeChoice][1]) {
                return false;
            }
        }
        int players = PLAYER_CHOICES[this.createPlayers.getSelectedIndex()];
        if (players > 0) {
            if (info != null) {
                z = players >= Math.max(2, info.playersMin()) && players <= info.playersMax();
            } else {
                z = !entry.starts().isEmpty() && entry.starts().size() == players;
            }
            boolean fits2 = z;
            if (!fits2) {
                return false;
            }
            return true;
        }
        return true;
    }

    private MapEntry officialEntry(String name) {
        String str;
        OfficialMap info = this.officialInfo.get(name.toLowerCase(Locale.ROOT));
        if (info != null && !info.description().isBlank() && !info.description().equals("null")) {
            str = info.description() + ((info.author().isBlank() || info.author().equals("null")) ? "" : " By " + info.author() + ".");
        } else {
            str = "Official map of Beyond All Reason.";
        }
        String text = str;
        return new MapEntry(null, name, text + " It is downloaded when the game needs it.", List.of(), null, false);
    }

    private BufferedImage officialPreview(OfficialMap info) {
        if (info.previewUrl().isBlank()) {
            return null;
        }
        Path file = this.home.resolve("cache").resolve("official-previews").resolve(info.springName().replaceAll("[^A-Za-z0-9._-]", "_") + ".jpg");
        try {
            try {
                if (!Files.isRegularFile(file, new LinkOption[0])) {
                    String url = info.previewUrl().replace("format(webp)", "format(jpeg)").replace("1024x1024", "768x768");
                    HttpResponse<byte[]> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build().send(HttpRequest.newBuilder(URI.create(url.replace(" ", "%20"))).timeout(Duration.ofSeconds(25L)).build(), HttpResponse.BodyHandlers.ofByteArray());
                    if (response.statusCode() != 200) {
                        return null;
                    }
                    Files.createDirectories(file.getParent(), new FileAttribute[0]);
                    Files.write(file, (byte[]) response.body(), new OpenOption[0]);
                }
                return ImageIO.read(file.toFile());
            } catch (IOException | RuntimeException e) {
                return null;
            }
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private boolean isOfficial(MapEntry entry) {
        return (entry == null || entry == GENERATOR_ENTRY || !this.officialMaps.containsKey(entry.name().toLowerCase(Locale.ROOT))) ? false : true;
    }

    private int playCount(MapEntry entry) {
        return this.popularity.getOrDefault(entry.name().toLowerCase(Locale.ROOT), 0).intValue();
    }

    private List<MapEntry> pickerEntries() {
        if (this.versionChoices.isEmpty() && Files.isRegularFile(versionChoicesFile(), new LinkOption[0])) {
            try {
                Reader reader = Files.newBufferedReader(versionChoicesFile());
                try {
                    this.versionChoices.load(reader);
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Throwable th) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException e) {
            }
        }
        List<MapEntry> result = new ArrayList<>();
        Map<String, MapEntry> byBase = new LinkedHashMap<>();
        Map<String, MapEntry> installed = new HashMap<>();
        for (MapEntry entry : this.allMaps) {
            if (entry.generated()) {
                result.add(entry);
            } else {
                installed.put(entry.name().toLowerCase(Locale.ROOT), entry);
            }
        }
        for (String name : this.officialMaps.values()) {
            MapEntry entry2 = installed.get(name.toLowerCase(Locale.ROOT));
            if (entry2 == null) {
                entry2 = this.officialEntries.computeIfAbsent(name, this::officialEntry);
            }
            byBase.put(baseName(name), entry2);
        }
        for (MapEntry entry3 : installed.values()) {
            String base = baseName(entry3.name());
            String chosen = this.versionChoices.getProperty(base);
            if (!byBase.containsKey(base) || entry3.name().equalsIgnoreCase(chosen)) {
                byBase.put(base, entry3);
            }
        }
        result.addAll(byBase.values());
        return result;
    }

    private List<MapEntry> versionsOf(MapEntry entry) {
        List<MapEntry> versions = new ArrayList<>();
        if (entry == null || entry == GENERATOR_ENTRY || entry.generated()) {
            return versions;
        }
        String base = baseName(entry.name());
        for (String name : this.officialMaps.values()) {
            if (baseName(name).equals(base)) {
                MapEntry official = findMap(name);
                versions.add(official != null ? official : this.officialEntries.computeIfAbsent(name, this::officialEntry));
            }
        }
        for (MapEntry other : this.allMaps) {
            if (!other.generated() && baseName(other.name()).equals(base) && versions.stream().noneMatch(version -> {
                return version.name().equalsIgnoreCase(other.name());
            })) {
                versions.add(other);
            }
        }
        return versions;
    }

    static String mapLabel(String name) {
        return name.startsWith(BarMapExporter.OLD_GENERATED_PREFIX) ? "Map_" + name.substring(BarMapExporter.OLD_GENERATED_PREFIX.length()) : name;
    }

    static boolean isGeneratedName(String name) {
        return name.startsWith(BarMapExporter.GENERATED_PREFIX) || name.startsWith(BarMapExporter.OLD_GENERATED_PREFIX) || name.startsWith("Map_");
    }

    static String generatedDescription(String text) {
        return text.replace("Neroxis Map Generator", "BAR Map Studio").replaceAll("\\s*Regenerate with --map-name \\S+", "").trim();
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$MapEntry.class */
    private static final class MapEntry extends Record {
        private final Path archive;
        private final String name;
        private final String description;
        private final List<float[]> starts;
        private final String mapFile;
        private final boolean generated;

        private MapEntry(Path archive, String name, String description, List<float[]> starts, String mapFile, boolean generated) {
            this.archive = archive;
            this.name = name;
            this.description = description;
            this.starts = starts;
            this.mapFile = mapFile;
            this.generated = generated;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, MapEntry.class), MapEntry.class, "archive;name;description;starts;mapFile;generated", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->archive:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->starts:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->mapFile:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->generated:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, MapEntry.class, Object.class), MapEntry.class, "archive;name;description;starts;mapFile;generated", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->archive:Ljava/nio/file/Path;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->name:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->description:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->starts:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->mapFile:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$MapEntry;->generated:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
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

        public List<float[]> starts() {
            return this.starts;
        }

        public String mapFile() {
            return this.mapFile;
        }

        public boolean generated() {
            return this.generated;
        }

        @Override // java.lang.Record
        public String toString() {
            return this.name;
        }

        String label() {
            return this.generated ? BarLauncher.mapLabel(this.name) : this.name;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$GameRecord.class */
    private static final class GameRecord extends Record {
        private final long time;
        private final String title;
        private final String archive;
        private final String mapName;
        private final String summary;
        private final String script;

        private GameRecord(long time, String title, String archive, String mapName, String summary, String script) {
            this.time = time;
            this.title = title;
            this.archive = archive;
            this.mapName = mapName;
            this.summary = summary;
            this.script = script;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, GameRecord.class), GameRecord.class, "time;title;archive;mapName;summary;script", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->time:J", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->archive:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->summary:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->script:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, GameRecord.class), GameRecord.class, "time;title;archive;mapName;summary;script", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->time:J", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->archive:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->summary:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->script:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, GameRecord.class, Object.class), GameRecord.class, "time;title;archive;mapName;summary;script", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->time:J", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->archive:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->summary:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;->script:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public long time() {
            return this.time;
        }

        public String title() {
            return this.title;
        }

        public String archive() {
            return this.archive;
        }

        public String mapName() {
            return this.mapName;
        }

        public String summary() {
            return this.summary;
        }

        public String script() {
            return this.script;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$Tile.class */
    private static final class Tile extends Record {
        private final String host;
        private final String players;
        private final boolean locked;
        private final List<String> people;
        private final Object key;
        private final String title;
        private final String mapName;
        private final String summary;
        private final String status;
        private final Color statusColor;
        private final String details;
        private final String action;
        private final Runnable open;
        private final GameRecord record;

        private Tile(String host, String players, boolean locked, List<String> people, Object key, String title, String mapName, String summary, String status, Color statusColor, String details, String action, Runnable open, GameRecord record) {
            this.host = host;
            this.players = players;
            this.locked = locked;
            this.people = people;
            this.key = key;
            this.title = title;
            this.mapName = mapName;
            this.summary = summary;
            this.status = status;
            this.statusColor = statusColor;
            this.details = details;
            this.action = action;
            this.open = open;
            this.record = record;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Tile.class), Tile.class, "host;players;locked;people;key;title;mapName;summary;status;statusColor;details;action;open;record", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->host:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->players:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->locked:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->people:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->key:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->summary:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->status:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->statusColor:Ljava/awt/Color;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->details:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->action:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->open:Ljava/lang/Runnable;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->record:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Tile.class), Tile.class, "host;players;locked;people;key;title;mapName;summary;status;statusColor;details;action;open;record", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->host:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->players:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->locked:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->people:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->key:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->summary:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->status:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->statusColor:Ljava/awt/Color;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->details:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->action:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->open:Ljava/lang/Runnable;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->record:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Tile.class, Object.class), Tile.class, "host;players;locked;people;key;title;mapName;summary;status;statusColor;details;action;open;record", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->host:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->players:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->locked:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->people:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->key:Ljava/lang/Object;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->mapName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->summary:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->status:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->statusColor:Ljava/awt/Color;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->details:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->action:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->open:Ljava/lang/Runnable;", "FIELD:Lcom/faforever/neroxis/generator/bar/BarLauncher$Tile;->record:Lcom/faforever/neroxis/generator/bar/BarLauncher$GameRecord;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String host() {
            return this.host;
        }

        public String players() {
            return this.players;
        }

        public boolean locked() {
            return this.locked;
        }

        public List<String> people() {
            return this.people;
        }

        public Object key() {
            return this.key;
        }

        public String title() {
            return this.title;
        }

        public String mapName() {
            return this.mapName;
        }

        public String summary() {
            return this.summary;
        }

        public String status() {
            return this.status;
        }

        public Color statusColor() {
            return this.statusColor;
        }

        public String details() {
            return this.details;
        }

        public String action() {
            return this.action;
        }

        public Runnable open() {
            return this.open;
        }

        public GameRecord record() {
            return this.record;
        }
    }

    private static void applyStagedUpdate() {
        if (LauncherUpdater.steam() || LauncherUpdater.appStore()) {
            return;
        }
        try {
            Path running = Path.of(BarLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (!Platform.WINDOWS && Files.isRegularFile(running, new LinkOption[0]) && running.getFileName().toString().equals("launcher.jar")) {
                Path staged = running.resolveSibling("launcher-new.jar");
                if (Files.isRegularFile(staged, new LinkOption[0]) && Files.size(staged) > 1000000) {
                    Files.move(staged, running, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                    Path home = running.getParent().getParent();
                    if (Platform.MAC && home.getParent() != null && "Contents".equals(String.valueOf(home.getFileName()))) {
                        new ProcessBuilder("open", "-n", home.getParent().toString()).start();
                    } else {
                        new ProcessBuilder("/bin/sh", home.resolve("bar-map-launcher.sh").toString()).directory(home.toFile()).start();
                    }
                    System.exit(0);
                    return;
                }
                return;
            }
            if (!Files.isRegularFile(running, new LinkOption[0]) || !running.getFileName().toString().equals("launcher-new.jar")) {
                return;
            }
            Path target = running.resolveSibling("launcher.jar");
            for (int attempt = 0; attempt < 20; attempt++) {
                try {
                    Files.copy(running, target, StandardCopyOption.REPLACE_EXISTING);
                    return;
                } catch (IOException e) {
                    Thread.sleep(500L);
                }
            }
        } catch (Exception e2) {
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$LaunchScreen.class */
    static final class LaunchScreen {
        private static Window window;
        private static JLabel status;
        private static JProgressBar bar;

        LaunchScreen() {
        }

        static void show() {
            if (window != null || GraphicsEnvironment.isHeadless() || System.getProperty("neroxis.launcher.screenshot") != null) {
                return;
            }
            try {
                JWindow splash = new JWindow();
                JPanel panel = new JPanel(new BorderLayout(0, 12)) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.LaunchScreen.1
                    protected void paintComponent(Graphics graphics) {
                        Graphics2D g = (Graphics2D) graphics;
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(24, 26, 34), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, getHeight(), new Color(12, 13, 18)));
                        g.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                        g.setColor(new Color(240, 150, 46, 110));
                        g.setStroke(new BasicStroke(1.5f));
                        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                    }
                };
                panel.setOpaque(false);
                panel.setBorder(BorderFactory.createEmptyBorder(28, 34, 24, 34));
                JLabel picture = new JLabel();
                URL logo = BarLauncher.class.getResource("barmapstudio.png");
                if (logo != null) {
                    picture.setIcon(new ImageIcon(new ImageIcon(logo).getImage().getScaledInstance(200, 33, 4)));
                }
                picture.setHorizontalAlignment(0);
                JLabel title = new JLabel("BAR MAP STUDIO", 0);
                title.setForeground(BarLauncher.ACCENT);
                title.setFont(title.getFont().deriveFont(1, 30.0f));
                JLabel subtitle = new JLabel("for Beyond All Reason", 0);
                subtitle.setForeground(BarLauncher.MUTED);
                subtitle.setFont(subtitle.getFont().deriveFont(14.0f));
                status = new JLabel("Starting...", 0);
                status.setForeground(BarLauncher.TEXT);
                status.setFont(status.getFont().deriveFont(13.0f));
                bar = new JProgressBar(0, 1000);
                bar.setIndeterminate(true);
                bar.setForeground(new Color(58, 176, 84));
                bar.setBorderPainted(false);
                bar.setPreferredSize(new Dimension(280, 6));
                JPanel text = new JPanel(new GridLayout(0, 1, 0, 6));
                text.setOpaque(false);
                text.add(title);
                text.add(subtitle);
                JPanel bottom = new JPanel(new BorderLayout(0, 10));
                bottom.setOpaque(false);
                bottom.add(status, "North");
                bottom.add(bar, "South");
                panel.add(picture, "North");
                panel.add(text, "Center");
                panel.add(bottom, "South");
                splash.setContentPane(panel);
                if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
                    splash.setBackground(new Color(0, 0, 0, 0));
                } else {
                    panel.setOpaque(true);
                    panel.setBackground(new Color(12, 13, 18));
                }
                splash.setSize(420, 330);
                splash.setLocationRelativeTo((Component) null);
                splash.setAlwaysOnTop(true);
                splash.setVisible(true);
                window = splash;
                SplashScreen early = SplashScreen.getSplashScreen();
                if (early != null) {
                    early.close();
                }
            } catch (Error | Exception e) {
                window = null;
            }
        }

        static void say(String text) {
            if (status != null) {
                SwingUtilities.invokeLater(() -> {
                    status.setText(text);
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void say(String text, double fraction) {
            if (status == null) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                status.setText(text);
                if (fraction < 0.0d) {
                    bar.setIndeterminate(true);
                } else {
                    bar.setIndeterminate(false);
                    bar.setValue((int) Math.round(Math.max(bar.getValue(), fraction * 1000.0d)));
                }
            });
        }

        static void hide() {
            if (window != null) {
                window.setVisible(false);
                window.dispose();
                window = null;
            }
        }
    }

    private void writeSplashPicture() {
        Path file = this.home.resolve("cache").resolve("splash.png");
        if (Files.isRegularFile(file, new LinkOption[0])) {
            return;
        }
        try {
            Files.createDirectories(file.getParent(), new FileAttribute[0]);
            BufferedImage picture = new BufferedImage(420, 330, 2);
            Graphics2D g = picture.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(24, 26, 34), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 330.0f, new Color(12, 13, 18)));
            g.fillRoundRect(0, 0, 420, 330, 22, 22);
            g.setColor(new Color(240, 150, 46, 110));
            g.drawRoundRect(0, 0, 419, 329, 22, 22);
            URL logo = BarLauncher.class.getResource("barmapstudio.png");
            if (logo != null) {
                g.drawImage(new ImageIcon(logo).getImage(), 110, 45, 200, 33, (ImageObserver) null);
            }
            g.setColor(ACCENT);
            g.setFont(new Font("Segoe UI", 1, 26));
            g.drawString("BAR MAP STUDIO", (420 - g.getFontMetrics().stringWidth("BAR MAP STUDIO")) / 2, 195);
            g.setColor(MUTED);
            g.setFont(new Font("Segoe UI", 0, 14));
            g.drawString("for Beyond All Reason", (420 - g.getFontMetrics().stringWidth("for Beyond All Reason")) / 2, 222);
            g.setColor(TEXT);
            g.setFont(new Font("Segoe UI", 0, 13));
            g.drawString("Starting...", (420 - g.getFontMetrics().stringWidth("Starting...")) / 2, 270);
            g.dispose();
            ImageIO.write(picture, "png", file.toFile());
        } catch (IOException | RuntimeException e) {
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        LaunchScreen.show();
        applyStagedUpdate();
        LobbyClient.logTo(locateHome().resolve("lobby.log"));
        InstallReport.start(locateHome());
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#F0962E", "@background", "#1E2129"));
        try {
            Properties early = new Properties();
            Path file = Platform.settingsFile(locateHome());
            if (Files.isRegularFile(file, new LinkOption[0])) {
                Reader reader = Files.newBufferedReader(file);
                try {
                    early.load(reader);
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Throwable th) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            int scale = Integer.parseInt(early.getProperty("uiscale", "100"));
            if (scale != 100 && scale >= 80 && scale <= 200) {
                System.setProperty("flatlaf.uiScale", scale + "%");
            }
        } catch (IOException | RuntimeException e) {
        }
        preflightUpdate();
        FlatLaf.setup(new FlatDarkLaf() { // from class: com.faforever.neroxis.generator.bar.BarLauncher.2
            public void provideErrorFeedback(Component component) {
            }
        });
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", e2 -> {
            Object patt0$temp = e2.getNewValue();
            if (patt0$temp instanceof JFormattedTextField) {
                JFormattedTextField field = (JFormattedTextField) patt0$temp;
                if (field.getParent() instanceof JSpinner.DefaultEditor) {
                    Objects.requireNonNull(field);
                    SwingUtilities.invokeLater(field::selectAll);
                }
            }
        });
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ProgressBar.arc", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 11);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Button.borderWidth", 0);
        UIManager.put("Button.background", new Color(38, 42, 52));
        UIManager.put("Button.hoverBackground", new Color(48, 53, 66));
        UIManager.put("ToggleButton.background", new Color(38, 42, 52));
        UIManager.put("ToggleButton.selectedBackground", new Color(240, 150, 46));
        UIManager.put("ToggleButton.selectedForeground", new Color(25, 18, 8));
        UIManager.put("List.selectionBackground", new Color(44, 50, 64));
        UIManager.put("List.selectionInactiveBackground", new Color(44, 50, 64));
        UIManager.put("List.selectionForeground", TEXT);
        UIManager.put("List.selectionInactiveForeground", TEXT);
        UIManager.put("TextComponent.background", new Color(24, 27, 34));
        UIManager.put("TextField.background", new Color(24, 27, 34));
        UIManager.put("ComboBox.background", new Color(24, 27, 34));
        UIManager.put("Spinner.background", new Color(24, 27, 34));
        UIManager.put("Component.borderColor", new Color(45, 49, 60));
        UIManager.put("TabbedPane.background", CARD);
        UIManager.put("TabbedPane.foreground", MUTED);
        UIManager.put("TabbedPane.selectedForeground", TEXT);
        UIManager.put("TabbedPane.underlineColor", ACCENT);
        UIManager.put("TabbedPane.inactiveUnderlineColor", ACCENT);
        UIManager.put("TabbedPane.selectedBackground", new Color(48, 53, 65));
        UIManager.put("TabbedPane.hoverColor", new Color(40, 44, 55));
        UIManager.put("TabbedPane.focusColor", new Color(40, 44, 55));
        UIManager.put("TabbedPane.tabArc", 10);
        UIManager.put("TabbedPane.tabSelectionArc", 3);
        UIManager.put("TabbedPane.tabSelectionHeight", 3);
        UIManager.put("TabbedPane.tabSelectionInsets", new Insets(0, 10, 0, 10));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(4, 4, 4, 4));
        UIManager.put("TabbedPane.tabHeight", 34);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        LaunchScreen.say("Loading the launcher...", 0.45d);
        SwingUtilities.invokeLater(() -> {
            new BarLauncher().show();
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show() {
        LaunchScreen.say("Getting the client ready...", 0.62d);
        String chosen = this.lobby.setting("bar.data", "");
        MacOs.prepare(BarFolder.usual());
        Path found = BarFolder.find(chosen);
        this.barFolder.setText((found != null ? found : BarFolder.usual()).toString());
        MacOs.prepare(Path.of(this.barFolder.getText().trim(), new String[0]));
        refreshGame();
        if (found != null && !found.toString().equals(chosen)) {
            this.lobby.remember("bar.data", found.toString());
        }
        this.barFolder.getDocument().addDocumentListener(onChange(() -> {
            Path now = Path.of(this.barFolder.getText().trim(), new String[0]);
            if (BarFolder.isData(now)) {
                this.lobby.remember("bar.data", now.toString());
            }
        }));
        if (found == null && System.getProperty("neroxis.launcher.screenshot") == null) {
            SwingUtilities.invokeLater(this::askForBarFolder);
        }
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(buildHeader(), "North");
        north.add(buildUpdateBar(), "South");
        root.add(north, "North");
        this.viewPanel.setBackground(BACKGROUND);
        this.viewPanel.add(buildStudioView(), "STUDIO");
        this.viewPanel.add(buildPlayView(), "PLAY");
        this.viewPanel.add(buildMapsView(), "MAPS");
        this.viewPanel.add(buildReplaysView(), "REPLAYS");
        this.viewPanel.add(this.news.view(), "NEWS");
        this.viewPanel.add(this.units.view(), "UNITS");
        this.viewPanel.add(this.startScreen.view(), "START");
        this.onStartScreen = false;
        this.views.show(this.viewPanel, "STUDIO");
        this.lobby.startAfterChecks();
        URL iconImage = BarLauncher.class.getResource("barmapstudio.png");
        if (iconImage != null) {
            this.frame.setIconImage(new ImageIcon(iconImage).getImage());
        }
        this.viewPanel.add(this.lobby.room(), "ROOM");
        this.viewPanel.setMinimumSize(new Dimension(0, 0));
        root.add(this.lobby.split(1, this.viewPanel, this.lobby.dock(), "split.dock", -349, 1.0d), "Center");
        this.style.addActionListener(e -> {
            this.customPanel.setVisible("Custom...".equals(this.style.getSelectedItem()));
            updateStyleNote();
            this.frame.revalidate();
        });
        this.reclaim.addChangeListener(e2 -> {
            updateReclaimLabel();
        });
        updateReclaimLabel();
        this.generate.addActionListener(e3 -> {
            generate(null);
        });
        this.play.addActionListener(e4 -> {
            openCreateGame((MapEntry) this.library.getSelectedValue());
        });
        this.delete.addActionListener(e5 -> {
            deleteSelected();
        });
        this.play.setEnabled(false);
        this.delete.setEnabled(false);
        this.barFolder.getDocument().addDocumentListener(onChange(this::updateBarStatus));
        this.search.getDocument().addDocumentListener(onChange(this::filterLibrary));
        updateBarStatus();
        this.frame.setDefaultCloseOperation(3);
        this.frame.addWindowListener(new WindowAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.3
            {
                Objects.requireNonNull(this);
            }

            public void windowClosing(WindowEvent event) {
                Thread watchdog = new Thread(() -> {
                    try {
                        Thread.sleep(4000L);
                        Runtime.getRuntime().halt(0);
                    } catch (InterruptedException e6) {
                    }
                }, "close-watchdog");
                watchdog.setDaemon(true);
                watchdog.start();
            }
        });
        modernizeStartScript();
        writeSplashPicture();
        LaunchScreen.say("Reading your maps...", 0.84d);
        loadMapLists();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Thread goodbye = new Thread(() -> {
                this.lobby.client().leaveAndDisconnect();
            }, "lobby-goodbye");
            goodbye.setDaemon(true);
            goodbye.start();
            try {
                goodbye.join(1500L);
            } catch (InterruptedException e6) {
                Thread.currentThread().interrupt();
            }
        }, "lobby-goodbye-timeout"));
        this.frame.setContentPane(root);
        this.frame.setSize(1640, 940);
        this.frame.setMinimumSize(new Dimension(1280, 720));
        this.frame.setLocationRelativeTo((Component) null);
        if (System.getProperty("neroxis.launcher.screenshot") != null) {
            this.frame.setTitle("BAR Map Studio TEST RUN");
            this.frame.setLocation(-5000, -5000);
            this.frame.setFocusableWindowState(false);
            this.frame.setType(Window.Type.UTILITY);
            this.frame.setVisible(true);
        } else {
            this.frame.getRootPane().registerKeyboardAction(e6 -> {
                toggleFullScreen();
            }, KeyStroke.getKeyStroke(122, 0), 2);
            setDisplay(this.lobby.setting("display", DISPLAY_WINDOW), this.lobby.settingNumber("screen", -1));
        }
        refreshLibrary(null);
        loadGames();
        askForDesktopShortcut();
        Thread boxes = new Thread(() -> {
            Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
            PreviewPanel.setCommanderIcon(BarInstall.readCommanderIcon(data));
            PreviewPanel.setResourceIcons(BarInstall.readGameIcon(data, "icons/mex_t1.png"), BarInstall.readGameIcon(data, "icons/energy_geo_safe.png"));
            JFrame jFrame = this.frame;
            Objects.requireNonNull(jFrame);
            SwingUtilities.invokeLater(jFrame::repaint);
            this.startBoxes.putAll(BarInstall.readStartBoxes(data));
        }, "start-boxes");
        boxes.setDaemon(true);
        boxes.start();
        runTestHook();
    }

    private static void preflightUpdate() {
        preflight = "current";
    }

    private void restartLauncher() throws IOException {
        restartFrom(this.home);
    }

    static void restartFrom(Path home) throws IOException {
        Path parent;
        if (MacOs.isMac()) {
            Path bundle = MacOs.bundle();
            if (bundle == null || bundle.getParent() == null) {
                parent = (home.getParent() == null || !"Contents".equals(String.valueOf(home.getFileName()))) ? home : home.getParent();
            } else {
                parent = bundle.getParent();
            }
            Path app = parent;
            new ProcessBuilder("open", "-n", app.toString()).start();
            return;
        }
        Path java = Platform.runtimeJava(home);
        Path jar = home.resolve("app").resolve("launcher-new.jar");
        if (!Files.isRegularFile(jar, new LinkOption[0])) {
            jar = home.resolve("app").resolve("launcher.jar");
        }
        if (Files.isRegularFile(java, new LinkOption[0]) && Files.isRegularFile(jar, new LinkOption[0])) {
            List<String> command = new ArrayList<>(List.of(java.toString()));
            Path splash = home.resolve("cache").resolve("splash.png");
            if (Files.isRegularFile(splash, new LinkOption[0])) {
                command.add("-splash:" + String.valueOf(splash));
            }
            command.addAll(List.of((Object[]) new String[]{"-Xms48m", "-Xmx1g", "-XX:+UseG1GC", "-XX:G1PeriodicGCInterval=20000", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=25", "-XX:TieredStopAtLevel=1", "-XX:+AutoCreateSharedArchive", "--enable-native-access=ALL-UNNAMED", "-XX:SharedArchiveFile=" + String.valueOf(home.resolve("cache").resolve("barmapstudio.jsa")), "-Dneroxis.home=" + String.valueOf(home), "-cp", jar.toString(), BarLauncher.class.getName()}));
            new ProcessBuilder(command).directory(home.toFile()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            return;
        }
        if (Platform.WINDOWS) {
            new ProcessBuilder("cmd", "/c", "start", "", home.resolve("bar-map-launcher.bat").toString()).directory(home.toFile()).start();
        } else {
            new ProcessBuilder("/bin/sh", home.resolve("bar-map-launcher.sh").toString()).directory(home.toFile()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
        }
    }

    private JComponent buildUpdateBar() {
        this.updateBar.setVisible(false);
        this.updateBar.setBackground(new Color(40, 56, 40));
        this.updateBar.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        JLabel text = new JLabel("A new version of the launcher is ready.");
        text.setForeground(TEXT);
        JButton restart = new JButton("Restart to update");
        restart.putClientProperty("JButton.buttonType", "roundRect");
        restart.setBackground(new Color(60, 150, 80));
        restart.setForeground(Color.WHITE);
        restart.addActionListener(e -> {
            try {
                restartLauncher();
                System.exit(0);
            } catch (IOException e) {
                text.setText("Close the launcher and start it again to update.");
            }
        });
        this.updateBar.add(text, "West");
        this.updateBar.add(restart, "East");
        if (preflight != null) {
            String result = preflight;
            SwingUtilities.invokeLater(() -> {
                this.startScreen.launcher(result);
            });
        } else if (LauncherUpdater.runningJar() != null && System.getProperty("neroxis.launcher.testupdate") != null) {
            Thread updater = new Thread(() -> {
                try {
                    String downloads = this.lobby.mapServiceUrl().replaceFirst("/find/?$", "/download/");
                    if (LauncherUpdater.stageUpdate(downloads)) {
                        SwingUtilities.invokeLater(() -> {
                            if (this.onStartScreen) {
                                this.startScreen.launcher("installing");
                                Timer restartSoon = new Timer(1200, e2 -> {
                                    try {
                                        restartLauncher();
                                        System.exit(0);
                                    } catch (IOException e2) {
                                        this.startScreen.launcher("ready");
                                    }
                                });
                                restartSoon.setRepeats(false);
                                restartSoon.start();
                                return;
                            }
                            this.startScreen.launcher("ready");
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            this.startScreen.launcher("current");
                        });
                    }
                } catch (Exception e2) {
                    System.err.println("update check failed: " + e2.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        this.startScreen.launcher("failed");
                    });
                    Path jar = LauncherUpdater.runningJar();
                    if (Platform.MAC && jar != null && !Files.isWritable(jar.getParent())) {
                        SwingUtilities.invokeLater(() -> {
                            text.setText("BAR Map Studio cannot update itself from here: move BAR Map Studio.app into Applications with the Finder, then start it again.");
                            restart.setVisible(false);
                            this.updateBar.setVisible(true);
                            this.frame.revalidate();
                        });
                    }
                }
            }, "launcher-update");
            updater.setDaemon(true);
            updater.start();
            Timer slow = new Timer(15000, e2 -> {
                this.startScreen.launcher("slow");
            });
            slow.setRepeats(false);
            slow.start();
        } else {
            SwingUtilities.invokeLater(() -> {
                this.startScreen.launcher("current");
            });
        }
        return this.updateBar;
    }

    private Path writeIcon() {
        Path icon = this.home.resolve("barmapstudio.ico");
        try {
            InputStream stream = BarLauncher.class.getResourceAsStream("barmapstudio.ico");
            if (stream != null) {
                try {
                    byte[] bytes = stream.readAllBytes();
                    if (!Files.isRegularFile(icon, new LinkOption[0]) || Files.size(icon) != bytes.length) {
                        Files.write(icon, bytes, new OpenOption[0]);
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
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
        }
        return icon;
    }

    private void createShortcut(Path script) {
        Path icon = writeIcon();
        String command = "$d = [Environment]::GetFolderPath('Desktop'); Remove-Item -ErrorAction SilentlyContinue ($d + '\\Neroxis Launcher.lnk'); Remove-Item -ErrorAction SilentlyContinue ($d + '\\BARMapStudio-old.lnk'); $s = (New-Object -ComObject WScript.Shell).CreateShortcut($d + '\\BAR Map Studio.lnk'); $s.TargetPath = '" + script.toString().replace("'", "''") + "'; $s.WorkingDirectory = '" + this.home.toString().replace("'", "''") + "'; $s.WindowStyle = 7; " + (Files.isRegularFile(icon, new LinkOption[0]) ? "$s.IconLocation = '" + icon.toString().replace("'", "''") + "'; " : "") + "$s.Description = 'BAR Map Studio, map creator and launcher for Beyond All Reason'; $s.Save()";
        try {
            new ProcessBuilder("powershell", "-NoProfile", "-Command", command).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.frame, "Could not create the shortcut: " + e.getMessage());
        }
    }

    private void askForDesktopShortcut() {
        Path asked = this.home.resolve(".shortcut-asked");
        Path script = this.home.resolve("bar-map-launcher.bat");
        if (!Files.isRegularFile(script, new LinkOption[0]) || System.getProperty("neroxis.launcher.screenshot") != null || LauncherUpdater.steam() || !System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            return;
        }
        if (Files.exists(asked, new LinkOption[0])) {
            Path old = Path.of(System.getProperty("user.home"), "Desktop", "Neroxis Launcher.lnk");
            Path oldOneDrive = Path.of(System.getProperty("user.home"), "OneDrive", "Desktop", "Neroxis Launcher.lnk");
            if (Files.exists(old, new LinkOption[0]) || Files.exists(oldOneDrive, new LinkOption[0])) {
                createShortcut(script);
                return;
            }
            return;
        }
        try {
            Files.writeString(asked, "asked", new OpenOption[0]);
            SwingUtilities.invokeLater(() -> {
                if (askInstalled()) {
                    createShortcut(script);
                }
            });
        } catch (IOException e) {
        }
    }

    private boolean askInstalled() {
        return greetingCard(this.frame, "INSTALLATION COMPLETE", "Welcome to BAR Map Studio", "BAR Map Studio is ready and keeps itself up to date. Do you want a shortcut on your desktop so you find it again?", "Create desktop shortcut", "Not now");
    }

    static boolean greetingCard(JFrame frame, String smallLine, String headingText, String html, String primary, String secondary) {
        JDialog dialog = new JDialog(frame, "BAR Map Studio", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        boolean[] wanted = {false};
        JPanel card = new JPanel(new BorderLayout(22, 0)) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.4
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(34, 37, 47), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, getHeight(), new Color(20, 22, 29)));
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g.setPaint(new RadialGradientPaint(84.0f, getHeight() / 2.0f, 150.0f, new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f}, new Color[]{new Color(240, 150, 46, 70), new Color(240, 150, 46, 0)}));
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g.setColor(new Color(240, 150, 46, 150));
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(26, 30, 24, 30));
        JLabel mark = new JLabel();
        try {
            InputStream stream = BarLauncher.class.getResourceAsStream("barmapstudio.png");
            if (stream != null) {
                try {
                    mark.setIcon(new ImageIcon(ImageIO.read(stream).getScaledInstance(108, 108, 4)));
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
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
        }
        mark.setVerticalAlignment(0);
        card.add(mark, "West");
        JLabel small = new JLabel(smallLine);
        small.setForeground(GREEN);
        small.setFont(small.getFont().deriveFont(1, 11.0f));
        JLabel heading = new JLabel(headingText);
        heading.setForeground(TEXT);
        heading.setFont(heading.getFont().deriveFont(1, 24.0f));
        JLabel text = new JLabel("<html><div style='width:350px'>" + html + "</div></html>");
        text.setForeground(new Color(190, 196, 208));
        text.setFont(text.getFont().deriveFont(13.5f));
        JButton yes = new JButton(primary);
        yes.setFont(yes.getFont().deriveFont(1, 13.5f));
        yes.setPreferredSize(new Dimension(220, 40));
        BarButtonUi.install((AbstractButton) yes, BarButtonUi.ACTION);
        JButton no = new JButton(secondary != null ? secondary : "");
        no.setVisible(secondary != null);
        no.setFont(no.getFont().deriveFont(13.5f));
        no.setPreferredSize(new Dimension(120, 40));
        BarButtonUi.install((AbstractButton) no, BarButtonUi.PLAIN);
        yes.addActionListener(e2 -> {
            wanted[0] = true;
            dialog.dispose();
        });
        no.addActionListener(e3 -> {
            dialog.dispose();
        });
        JPanel buttons = new JPanel(new FlowLayout(0, 0, 0));
        buttons.setOpaque(false);
        buttons.add(yes);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(no);
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, 1));
        for (JComponent part : List.of(small, heading, text, buttons)) {
            part.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        }
        right.add(small);
        right.add(Box.createVerticalStrut(6));
        right.add(heading);
        right.add(Box.createVerticalStrut(10));
        right.add(text);
        right.add(Box.createVerticalStrut(20));
        right.add(buttons);
        card.add(right, "Center");
        dialog.setContentPane(card);
        dialog.getRootPane().setDefaultButton(yes);
        dialog.getRootPane().registerKeyboardAction(e4 -> {
            dialog.dispose();
        }, KeyStroke.getKeyStroke(27, 0), 2);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        if (System.getProperty("neroxis.launcher.screenshot") != null) {
            dialog.setModal(false);
        }
        dialog.setVisible(true);
        return wanted[0];
    }

    private static GraphicsDevice[] screens() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    private void setDisplay(String mode, int screen) {
        GraphicsConfiguration graphicsConfiguration;
        String str;
        GraphicsDevice[] devices = screens();
        if (screen >= 0 && screen < devices.length) {
            graphicsConfiguration = devices[screen].getDefaultConfiguration();
        } else {
            graphicsConfiguration = this.frame.isDisplayable() ? this.frame.getGraphicsConfiguration() : GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        GraphicsConfiguration target = graphicsConfiguration;
        boolean fullScreen = DISPLAY_FULL_SCREEN.equals(mode);
        if (fullScreen) {
            str = DISPLAY_FULL_SCREEN;
        } else {
            str = DISPLAY_WINDOW.equals(mode) ? DISPLAY_WINDOW : DISPLAY_MAXIMIZED;
        }
        this.displayMode = str;
        if (!fullScreen) {
            this.windowedMode = this.displayMode;
        }
        Rectangle area = target.getBounds();
        this.frame.dispose();
        this.frame.setUndecorated(fullScreen);
        if (this.headerWindowButtons != null) {
            this.headerWindowButtons.setVisible(fullScreen);
        }
        this.frame.setExtendedState(0);
        if (fullScreen) {
            this.frame.setBounds(area);
        } else {
            this.frame.setSize(Math.min(area.width - 80, Math.max(1280, (int) Math.round(((double) area.width) * 0.65d))), Math.min(area.height - 80, Math.max(760, (int) Math.round(((double) area.height) * 0.66d))));
            this.frame.setLocation(area.x + ((area.width - this.frame.getWidth()) / 2), area.y + ((area.height - this.frame.getHeight()) / 2));
            Rectangle saved = this.lobby.savedWindow();
            if (saved != null && screen < 0 && Arrays.stream(screens()).anyMatch(device -> {
                return device.getDefaultConfiguration().getBounds().intersection(saved).width > 200;
            })) {
                this.frame.setBounds(saved);
            }
        }
        this.restoringDisplay = true;
        LaunchScreen.hide();
        this.frame.setVisible(true);
        if (DISPLAY_MAXIMIZED.equals(this.displayMode)) {
            this.frame.setExtendedState(6);
        }
        this.restoringDisplay = false;
        this.lobby.displayChanged(this.displayMode, screen);
        if (!this.windowTracked) {
            this.windowTracked = true;
            final Timer save = new Timer(600, e -> {
                if (DISPLAY_WINDOW.equals(this.displayMode) && this.frame.getExtendedState() == 0 && !this.frame.isUndecorated()) {
                    this.lobby.saveWindow(this.frame.getBounds());
                }
            });
            save.setRepeats(false);
            this.frame.addComponentListener(new ComponentAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.5
                {
                    Objects.requireNonNull(this);
                }

                public void componentMoved(ComponentEvent event) {
                    save.restart();
                }

                public void componentResized(ComponentEvent event) {
                    save.restart();
                }
            });
            this.frame.addWindowStateListener(event -> {
                if (this.restoringDisplay || this.frame.isUndecorated()) {
                    return;
                }
                boolean maximized = (event.getNewState() & 6) == 6;
                boolean minimized = (event.getNewState() & 1) != 0;
                if (!minimized) {
                    this.displayMode = maximized ? DISPLAY_MAXIMIZED : DISPLAY_WINDOW;
                    this.windowedMode = this.displayMode;
                    this.lobby.displayChanged(this.displayMode, -1);
                }
            });
        }
    }

    private void toggleFullScreen() {
        setDisplay(DISPLAY_FULL_SCREEN.equals(this.displayMode) ? this.windowedMode : DISPLAY_FULL_SCREEN, -1);
    }

    private static int runGenerator(List<String> args) throws InterruptedException, IOException {
        String classPath = System.getProperty("java.class.path", "");
        Path java = Path.of(System.getProperty("java.home"), "bin", Platform.program("java"));
        if (classPath.isBlank() || !Files.isRegularFile(java, new LinkOption[0])) {
            return MapGeneratorCommand.execute((String[]) args.toArray(new String[0]));
        }
        List<String> command = new ArrayList<>(List.of(java.toString(), "-Xmx5g", "-XX:+UseParallelGC", "-cp", classPath, MapGeneratorCommand.class.getName()));
        command.addAll(args);
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        while (true) {
            try {
                String line = reader.readLine();
                if (line != null) {
                    System.out.println(line);
                } else {
                    reader.close();
                    return process.waitFor();
                }
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
    }

    private void modernizeStartScript() {
        Path script = this.home.resolve("bar-map-launcher.bat");
        try {
            Files.createDirectories(this.home.resolve("cache"), new FileAttribute[0]);
            if (!Files.isRegularFile(script, new LinkOption[0])) {
                return;
            }
            String text = Files.readString(script);
            String updated = text.contains("-Xmx6g") ? text.replace("-Xmx6g", LAUNCHER_JVM_OPTIONS) : text;
            if (!updated.contains("-splash:")) {
                updated = updated.replace("-Xms48m", "\"-splash:%ROOT%cache\\splash.png\" -Xms48m");
            }
            if (!updated.equals(text)) {
                Files.writeString(script, updated, new OpenOption[0]);
            }
        } catch (IOException e) {
        }
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(this, new BorderLayout()) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.6
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(36, 28, 20), getWidth(), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(20, 23, 30)));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BarLauncher.ACCENT);
                g.fillRect(0, getHeight() - 2, getWidth(), 2);
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        JPanel left = new JPanel(new FlowLayout(0, 0, 0));
        left.setOpaque(false);
        URL logoUrl = BarLauncher.class.getResource("barmapstudio.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image scaled = icon.getImage().getScaledInstance(180, 29, Image.SCALE_SMOOTH);
            left.add(new JLabel(new ImageIcon(scaled)));
        } else {
            JLabel name = new JLabel("BAR MAP STUDIO");
            name.setFont(name.getFont().deriveFont(1, 22.0f));
            name.setForeground(ACCENT);
            left.add(name);
        }
        JLabel subtitle = new JLabel("   for Beyond All Reason      ");
        subtitle.setFont(subtitle.getFont().deriveFont(13.0f));
        subtitle.setForeground(MUTED);
        left.add(subtitle);
        ButtonGroup group = new ButtonGroup();
        for (String view : new String[]{"STUDIO", "PLAY", "MAPS", "REPLAYS", "NEWS", "UNITS"}) {
            String tabLabel = switch (view) {
                case "STUDIO" -> "MAP STUDIO";
                case "PLAY" -> "BATTLES";
                case "MAPS" -> "MAP LIBRARY";
                default -> view;
            };
            JToggleButton button = new JToggleButton(this, tabLabel) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.7
                final /* synthetic */ BarLauncher this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                protected void paintComponent(Graphics graphics) {
                    Color color;
                    Graphics2D g = (Graphics2D) graphics;
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setFont(getFont());
                    if (isSelected()) {
                        color = BarLauncher.ACCENT;
                    } else {
                        color = getModel().isRollover() ? BarLauncher.TEXT : BarLauncher.MUTED;
                    }
                    g.setColor(color);
                    int width = g.getFontMetrics().stringWidth(getText());
                    g.drawString(getText(), (getWidth() - width) / 2, (getHeight() / 2) + 6);
                    if (isSelected()) {
                        g.fillRect((getWidth() - width) / 2, getHeight() - 3, width, 3);
                    }
                }
            };
            button.setFont(button.getFont().deriveFont(1, 15.0f));
            button.setPreferredSize(new Dimension(tabLabel.length() > 7 ? 130 : 92, 34));
            button.setRolloverEnabled(true);
            button.setCursor(Cursor.getPredefinedCursor(12));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                this.views.show(this.viewPanel, (view.equals("PLAY") && this.inRoom) ? "ROOM" : view);
                if (view.equals("REPLAYS")) {
                    showReplays();
                } else if (view.equals("NEWS")) {
                    this.news.refresh();
                } else if (view.equals("UNITS")) {
                    this.units.ensureLoaded();
                }
                this.lobby.remember("page", view);
            });
            group.add(button);
            this.navigation.add(button);
            left.add(button);
        }
        ((JToggleButton) this.navigation.getFirst()).setSelected(true);
        header.add(left, "West");
        this.barStatus.setFont(this.barStatus.getFont().deriveFont(13.0f));
        this.barStatus.setCursor(Cursor.getPredefinedCursor(12));
        this.barStatus.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.8
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mouseClicked(MouseEvent e2) {
                this.this$0.chooseBarFolder(this.this$0.frame);
            }
        });
        JPanel right = new JPanel(new FlowLayout(2, 16, 0));
        right.setOpaque(false);
        right.add(this.barStatus);
        right.add(this.lobby.badge());
        JPanel windowButtons = new JPanel(new FlowLayout(2, 2, 0));
        windowButtons.setOpaque(false);
        this.headerWindowButtons = windowButtons;
        String[][] controls = {new String[]{"–", "Minimize"}, new String[]{"□", "Window or full screen (F11)"}, new String[]{"✕", "Close"}};
        for (int i = 0; i < controls.length; i++) {
            final int action = i;
            final JButton button2 = new JButton(controls[i][0]);
            button2.setToolTipText(controls[i][1]);
            button2.setFocusable(false);
            button2.setBorderPainted(false);
            button2.setContentAreaFilled(false);
            button2.setForeground(MUTED);
            button2.setFont(button2.getFont().deriveFont(1, 15.0f));
            button2.setMargin(new Insets(2, 8, 2, 8));
            button2.setCursor(Cursor.getPredefinedCursor(12));
            button2.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.9
                {
                    Objects.requireNonNull(this);
                }

                public void mouseEntered(MouseEvent event) {
                    button2.setForeground(action == 2 ? new Color(255, 96, 84) : BarLauncher.TEXT);
                }

                public void mouseExited(MouseEvent event) {
                    button2.setForeground(BarLauncher.MUTED);
                }
            });
            button2.addActionListener(e2 -> {
                if (action == 0) {
                    this.frame.setExtendedState(this.frame.getExtendedState() | 1);
                } else if (action == 1) {
                    toggleFullScreen();
                } else {
                    this.frame.dispatchEvent(new WindowEvent(this.frame, 201));
                }
            });
            windowButtons.add(button2);
        }
        right.add(windowButtons);
        header.add(right, "East");
        return header;
    }

    private JComponent buildOptions() {
        JPanel options = new ScrollablePanel();
        options.setLayout(new BoxLayout(options, 1));
        options.setBackground(BACKGROUND);
        options.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        JPanel formatGrid = new JPanel(new GridLayout(2, 4, 6, 6));
        formatGrid.setOpaque(false);
        this.formatButtons.clear();
        ButtonGroup group = new ButtonGroup();
        for (Object[] format : FORMATS) {
            JToggleButton button = new JToggleButton((String) format[0]);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                this.playersPerTeam.setValue(format[1]);
                this.teams.setSelectedIndex(((Integer) format[2]).intValue() == 0 ? 3 : 0);
            });
            group.add(button);
            this.formatButtons.add(button);
            formatGrid.add(button);
        }
        ((JToggleButton) this.formatButtons.getFirst()).setSelected(true);
        options.add(card("Game format", formatGrid, grid(row("Players per team", this.playersPerTeam), row("Teams", this.teams))));
        this.customPanel.setOpaque(false);
        addRows(this.customPanel, row("Terrain", this.terrain), row("Biome", this.biome), row("Reclaim style", this.props), row("Tree density", this.treeDensity));
        this.customPanel.setVisible(false);
        this.styleNote.setForeground(MUTED);
        options.add(card("Map", grid(row("Size", this.size), row("Style", this.style), row("Symmetry", this.symmetry)), this.styleNote, this.customPanel));
        for (JComboBox jComboBox : new JComponent[]{this.size, this.teams}) {
            jComboBox.addActionListener(e2 -> {
                updateStyleNote();
            });
        }
        this.playersPerTeam.addChangeListener(e3 -> {
            updateStyleNote();
        });
        this.formatButtons.forEach(button2 -> {
            button2.addActionListener(e4 -> {
                updateStyleNote();
            });
        });
        this.reclaimLabel.setForeground(MUTED);
        this.resourceLabel.setForeground(MUTED);
        this.resourceDensity.addChangeListener(e4 -> {
            updateResourceLabel();
        });
        this.windMin.addChangeListener(e5 -> {
            if (((Integer) this.windMax.getValue()).intValue() < ((Integer) this.windMin.getValue()).intValue()) {
                this.windMax.setValue(this.windMin.getValue());
            }
        });
        this.windMax.addChangeListener(e6 -> {
            if (((Integer) this.windMax.getValue()).intValue() < ((Integer) this.windMin.getValue()).intValue()) {
                this.windMin.setValue(this.windMax.getValue());
            }
        });
        updateResourceLabel();
        options.add(card("Economy", grid(row("Metal spots on the map", this.resourceDensity)), this.resourceLabel, grid(row("Metal spots at each start", this.startMexes), row("Metal near start", this.metalBase), row("Richest metal spot", this.metalMax)), this.metalUniform, grid(row("Reclaim", this.reclaim)), this.reclaimLabel));
        options.add(card("Weather", grid(row("Wind minimum", this.windMin), row("Wind maximum", this.windMax), row("Tidal strength", this.tidal))));
        this.mapName.putClientProperty("JTextField.placeholderText", "optional, for example Hourglass Mere");
        this.seed.putClientProperty("JTextField.placeholderText", "optional seed or neroxis_map_generator_...");
        this.biomeTrees.setToolTipText("Pines, birches, palms, cacti... from BAR's certified map features (37 MB, once)");
        options.add(card("Extras", this.biomeTrees, grid(row("Map name", this.mapName), row("Seed / map code", this.seed), row("BAR data folder", this.barFolder))));
        options.add(Box.createVerticalGlue());
        return options;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(BACKGROUND);
        center.setBorder(BorderFactory.createEmptyBorder(16, 10, 16, 10));
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, 1));
        top.setOpaque(false);
        this.title.setFont(this.title.getFont().deriveFont(1, 20.0f));
        this.title.setForeground(TEXT);
        this.title.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        this.chips.setOpaque(false);
        this.chips.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        top.add(this.title);
        top.add(Box.createVerticalStrut(8));
        top.add(this.chips);
        center.add(top, "North");
        center.add(this.preview, "Center");
        this.log.setEditable(false);
        this.log.setRows(5);
        this.log.setBackground(new Color(13, 15, 19));
        this.log.setForeground(MUTED);
        this.log.setFont(new Font("Monospaced", 0, 12));
        this.log.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane logScroll = new JScrollPane(this.log);
        logScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        center.add(logScroll, "South");
        return center;
    }

    private JComponent buildLibrary() {
        JPanel side = new JPanel(new BorderLayout(0, 10));
        side.setBackground(BACKGROUND);
        side.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 18));
        side.setPreferredSize(new Dimension(330, 100));
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        JLabel libraryTitle = new JLabel("MAP LIBRARY");
        libraryTitle.setForeground(ACCENT);
        libraryTitle.setFont(libraryTitle.getFont().deriveFont(1, 12.0f));
        top.add(libraryTitle, "North");
        this.search.putClientProperty("JTextField.placeholderText", "Search maps...");
        this.search.putClientProperty("JTextField.showClearButton", true);
        top.add(this.search, "Center");
        side.add(top, "North");
        this.library.setSelectionMode(0);
        this.library.setBackground(CARD);
        this.library.setFixedCellHeight(64);
        this.library.setCellRenderer(new MapCellRenderer(this));
        this.library.addListSelectionListener(e -> {
            showSelected();
        });
        JScrollPane scroll = new JScrollPane(this.library);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        side.add(scroll, "Center");
        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 8));
        buttons.setOpaque(false);
        this.play.setText("Create game on this map");
        this.play.putClientProperty("JButton.buttonType", "roundRect");
        this.play.setFont(this.play.getFont().deriveFont(1, 15.0f));
        this.play.setBackground(new Color(60, 150, 80));
        this.play.setForeground(Color.WHITE);
        this.play.setPreferredSize(new Dimension(100, 44));
        buttons.add(this.play);
        JPanel small = new JPanel(new GridLayout(1, 2, 8, 0));
        small.setOpaque(false);
        JButton folder = new JButton("Open folder");
        folder.addActionListener(e2 -> {
            openFolder();
        });
        small.add(this.delete);
        small.add(folder);
        buttons.add(small);
        JButton newMap = new JButton("Generate a new map...");
        newMap.addActionListener(e3 -> {
            openCreateGame(GENERATOR_ENTRY);
        });
        buttons.add(newMap);
        side.add(buttons, "South");
        return side;
    }

    private void generate(Consumer<MapEntry> whenDone) {
        Path maps = Path.of(this.barFolder.getText().trim(), new String[0]).resolve("maps");
        if (!Files.isDirectory(maps, new LinkOption[0])) {
            JOptionPane.showMessageDialog(this.frame, "The BAR data folder has no maps folder:\n" + String.valueOf(maps) + "\n" + Platform.dataHint(), "BAR not found", 0);
            return;
        }
        List<String> args = buildArguments(maps);
        this.generate.setEnabled(false);
        this.createGame.setEnabled(false);
        this.generateOnly.setEnabled(false);
        this.progress.setVisible(true);
        this.createStatus.setText("Generating the map...");
        this.log.setText("");
        Thread worker = new Thread(() -> {
            int exitCode;
            Path features;
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            PrintStream tee = new PrintStream((OutputStream) new LogStream(this), true, StandardCharsets.UTF_8);
            System.setOut(tee);
            System.setErr(tee);
            try {
                if (this.biomeTrees.isSelected() && (features = ensureMapFeatures()) != null) {
                    args.add("--bar-features");
                    args.add(features.toString());
                }
                exitCode = runGenerator(args);
            } catch (Throwable e) {
                e.printStackTrace();
                exitCode = 1;
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
            int result = exitCode;
            SwingUtilities.invokeLater(() -> {
                this.generate.setEnabled(true);
                this.createGame.setEnabled(true);
                this.generateOnly.setEnabled(true);
                this.progress.setVisible(false);
                MapEntry created = null;
                if (result == 0) {
                    Path archive = newestArchive(maps);
                    refreshLibrary(archive);
                    created = this.allMaps.stream().filter(entry -> {
                        return entry.archive().equals(archive);
                    }).findFirst().orElse(null);
                    if (created != null) {
                        this.newestGenerated = created.archive();
                        filterCreateList();
                    }
                    this.createStatus.setText("Map ready.");
                } else {
                    this.createStatus.setText("Generation failed: some styles do not support every player count or size, try another style or size.");
                }
                if (whenDone != null) {
                    whenDone.accept(created);
                }
            });
        }, "map-generation");
        worker.setDaemon(true);
        worker.start();
    }

    private int[] chosenFormat() {
        int iMax;
        int perTeam = ((Integer) this.playersPerTeam.getValue()).intValue();
        switch (this.teams.getSelectedIndex()) {
            case 1:
                iMax = 3;
                break;
            case 2:
                iMax = 4;
                break;
            case 3:
                iMax = Math.max(2, perTeam);
                break;
            default:
                iMax = 2;
                break;
        }
        int teamCount = iMax;
        int spawns = this.teams.getSelectedIndex() == 3 ? Math.max(2, perTeam) : perTeam * teamCount;
        return new int[]{spawns, teamCount, SIZE_VALUES[this.size.getSelectedIndex()]};
    }

    private static int recommendedSize(int spawns) {
        if (spawns <= 2) {
            return 384;
        }
        if (spawns <= 6) {
            return DDSHeader.CUBEMAP_FLAG;
        }
        return spawns <= 10 ? 640 : 768;
    }

    private static String sizeName(int mapSize) {
        for (int i = 1; i < SIZE_VALUES.length; i++) {
            if (SIZE_VALUES[i] == mapSize) {
                return SIZES[i];
            }
        }
        return mapSize;
    }

    private static Integer fitStyle(String styleName, int spawns, int teamCount, int mapSize, StringBuilder note) {
        int[] limits = STYLE_LIMITS.get(styleName);
        int wanted = mapSize == 0 ? recommendedSize(spawns) : mapSize;
        if (limits == null) {
            return Integer.valueOf(wanted);
        }
        if (teamCount < limits[2] || teamCount > limits[3]) {
            note.append(styleName).append(limits[2] == limits[3] ? " is a " + limits[2] + " team style" : " is made for " + limits[2] + " to " + limits[3] + " teams");
            return null;
        }
        if (spawns > limits[4]) {
            note.append(styleName).append(" is made for up to ").append(limits[4]).append(" players");
            return null;
        }
        int fitted = Math.max(limits[0], Math.min(limits[1], wanted));
        if (fitted != wanted) {
            note.append(styleName).append(" is made for ").append(sizeName(limits[0])).append(" to ").append(sizeName(limits[1])).append(": the map will be ").append(sizeName(fitted));
        }
        return Integer.valueOf(fitted);
    }

    private static List<String> stylesThatFit(String[] pool, int spawns, int teamCount, int mapSize) {
        List<String> fitting = new ArrayList<>();
        for (String candidate : pool) {
            int[] limits = STYLE_LIMITS.get(candidate);
            int wanted = mapSize == 0 ? recommendedSize(spawns) : mapSize;
            if (limits == null || (teamCount >= limits[2] && teamCount <= limits[3] && spawns <= limits[4] && wanted >= limits[0] && wanted <= limits[1])) {
                fitting.add(candidate);
            }
        }
        return fitting.isEmpty() ? List.of("BASIC") : fitting;
    }

    private void updateStyleNote() {
        String selectedStyle = String.valueOf(this.style.getSelectedItem());
        StringBuilder note = new StringBuilder();
        if (STYLE_LIMITS.containsKey(selectedStyle)) {
            int[] format = chosenFormat();
            Integer fitted = fitStyle(selectedStyle, format[0], format[1], format[2], note);
            this.styleNote.setForeground(fitted == null ? new Color(255, 96, 84) : MUTED);
        }
        this.styleNote.setText(note.isEmpty() ? " " : "<html><div style='width:290px'>" + String.valueOf(note) + "</div></html>");
        if (!this.progress.isVisible()) {
            this.generate.setEnabled((STYLE_LIMITS.containsKey(selectedStyle) && fitStyle(selectedStyle, chosenFormat()[0], chosenFormat()[1], chosenFormat()[2], new StringBuilder()) == null) ? false : true);
        }
    }

    private List<String> buildArguments(Path maps) {
        Integer fitted;
        List<String> args = new ArrayList<>();
        args.add("--bar");
        args.add("--out-path");
        args.add(maps.toString());
        String seedText = this.seed.getText().trim();
        if (seedText.startsWith("neroxis_map_generator")) {
            args.add("--map-name");
            args.add(seedText);
        } else {
            int[] format = chosenFormat();
            int spawns = format[0];
            int teamCount = format[1];
            int mapSize = format[2] == 0 ? recommendedSize(spawns) : format[2];
            String selectedStyle = String.valueOf(this.style.getSelectedItem());
            if (STYLE_LIMITS.containsKey(selectedStyle) && (fitted = fitStyle(selectedStyle, spawns, teamCount, format[2], new StringBuilder())) != null) {
                mapSize = fitted.intValue();
            }
            args.addAll(List.of("--spawn-count", String.valueOf(spawns), "--num-teams", String.valueOf(teamCount), "--map-size", String.valueOf(mapSize)));
            if (!seedText.isEmpty()) {
                args.add("--seed");
                args.add(String.valueOf(seedText.matches("-?\\d{1,18}") ? Long.parseLong(seedText) : seedText.hashCode()));
            }
            if (selectedStyle.equals("Custom...")) {
                addChoice(args, "--terrain-style", this.terrain);
                addChoice(args, "--biome", this.biome);
                addChoice(args, "--prop-style", this.props);
                args.addAll(List.of("--resource-density", String.valueOf(this.resourceDensity.getValue() / 100.0f), "--reclaim-density", String.valueOf(this.treeDensity.getValue() / 100.0f)));
            } else if (selectedStyle.equals("Random land map")) {
                List<String> pool = stylesThatFit(LAND_STYLES, spawns, teamCount, mapSize);
                args.add("--style");
                args.add(pool.get((int) (Math.random() * ((double) pool.size()))));
            } else if (!selectedStyle.startsWith("Random")) {
                args.add("--style");
                args.add(selectedStyle);
            }
            if (this.symmetry.getSelectedIndex() > 0) {
                args.add("--terrain-symmetry");
                args.add(String.valueOf(this.symmetry.getSelectedItem()));
            }
        }
        double base = ((Number) this.metalBase.getValue()).doubleValue();
        double max = Math.max(base, ((Number) this.metalMax.getValue()).doubleValue());
        args.addAll(List.of("--bar-metal", String.valueOf(base), "--bar-metal-min", String.valueOf(Math.min(base, 2.0d)), "--bar-metal-max", String.valueOf(max), "--bar-reclaim", String.valueOf(this.reclaim.getValue() / 10.0f)));
        if (this.metalUniform.isSelected()) {
            args.add("--bar-metal-uniform");
        }
        args.addAll(barExtraArgs());
        String name = this.mapName.getText().trim();
        if (!name.isEmpty()) {
            args.add("--bar-name");
            args.add(name);
        }
        return args;
    }

    private List<String> barExtraArgs() {
        List<String> args = new ArrayList<>();
        boolean custom = "Custom...".equals(this.style.getSelectedItem());
        if (!custom && this.resourceDensity.getValue() != 50) {
            args.addAll(List.of("--bar-mexes", String.valueOf(this.resourceDensity.getValue() / 100.0f)));
        }
        if (this.startMexes.getSelectedIndex() > 0) {
            args.addAll(List.of("--bar-start-mexes", String.valueOf(this.startMexes.getSelectedIndex())));
        }
        int minWind = ((Integer) this.windMin.getValue()).intValue();
        int maxWind = ((Integer) this.windMax.getValue()).intValue();
        if (minWind != 3 || maxWind != 16) {
            args.addAll(List.of("--bar-wind-min", String.valueOf(minWind), "--bar-wind-max", String.valueOf(maxWind)));
        }
        if (((Integer) this.tidal.getValue()).intValue() != 18) {
            args.addAll(List.of("--bar-tidal", String.valueOf(this.tidal.getValue())));
        }
        return args;
    }

    private String genmapExtras() {
        StringBuilder text = new StringBuilder();
        if (this.resourceDensity.getValue() != 50) {
            text.append(String.format(Locale.ROOT, " mexes=%.2f", Float.valueOf(this.resourceDensity.getValue() / 100.0f)));
        }
        if (this.startMexes.getSelectedIndex() > 0) {
            text.append(" startmexes=").append(this.startMexes.getSelectedIndex());
        }
        int minWind = ((Integer) this.windMin.getValue()).intValue();
        int maxWind = ((Integer) this.windMax.getValue()).intValue();
        if (minWind != 3 || maxWind != 16) {
            text.append(" wind=").append(minWind).append(" maxwind=").append(maxWind);
        }
        if (((Integer) this.tidal.getValue()).intValue() != 18) {
            text.append(" tidal=").append(this.tidal.getValue());
        }
        return text.toString();
    }

    private void updateResourceLabel() {
        String str;
        int value = this.resourceDensity.getValue();
        JLabel jLabel = this.resourceLabel;
        if (value == 50) {
            str = "as usual for the map style";
        } else if (value < 25) {
            str = "few metal spots, every one counts";
        } else if (value < 50) {
            str = "fewer metal spots than usual";
        } else {
            str = value < 75 ? "more metal spots than usual" : "lots of metal spots";
        }
        jLabel.setText(str);
    }

    private static void addChoice(List<String> args, String option, JComboBox<String> choice) {
        if (choice.getSelectedIndex() > 0) {
            args.add(option);
            args.add(String.valueOf(choice.getSelectedItem()));
        }
    }

    private Path ensureMapFeatures() {
        Path target = this.home.resolve("mapfeatures");
        if (Files.isDirectory(target.resolve("features"), new LinkOption[0])) {
            return target;
        }
        System.out.println("Downloading the official BAR map features (trees, 37 MB, only once)...");
        try {
            ZipInputStream zip = new ZipInputStream(URI.create("https://github.com/beyond-all-reason/mapfeatures/archive/3b791639210dcc4f89875fce712bde8f460d38e1.zip").toURL().openStream());
            while (true) {
                try {
                    ZipEntry entry = zip.getNextEntry();
                    if (entry != null) {
                        String name = entry.getName();
                        int slash = name.indexOf(47);
                        if (slash >= 0 && !entry.isDirectory()) {
                            Path file = target.resolve(name.substring(slash + 1)).normalize();
                            if (file.startsWith(target)) {
                                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                                Files.copy(zip, file, new CopyOption[0]);
                            }
                        }
                    } else {
                        System.out.println("Map features ready.");
                        zip.close();
                        return target;
                    }
                } catch (Throwable th) {
                    try {
                        zip.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
                System.out.println("Could not download the map features, the map gets fir trees: " + e.getMessage());
                return null;
            }
        } catch (IOException e) {
            System.out.println("Could not download the map features, the map gets fir trees: " + e.getMessage());
            return null;
        }
    }

    private void refreshLibrary(Path select) {
        this.allMaps.clear();
        Path maps = Path.of(this.barFolder.getText().trim(), new String[0]).resolve("maps");
        if (Files.isDirectory(maps, new LinkOption[0])) {
            try {
                Stream<Path> files = Files.list(maps);
                try {
                    Stream streamFilter = files.filter(path -> {
                        return path.getFileName().toString().endsWith(".sdz");
                    }).sorted(Comparator.comparingLong(path2 -> {
                        return path2.toFile().lastModified();
                    }).reversed()).map(BarLauncher::readEntry).filter(entry -> {
                        return entry != null;
                    });
                    List<MapEntry> list = this.allMaps;
                    Objects.requireNonNull(list);
                    streamFilter.forEach((v1) -> {
                        r1.add(v1);
                    });
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
                this.log.append("Could not read the maps folder: " + e.getMessage() + "\n");
            }
            List<Path> generated = this.allMaps.stream().map((v0) -> {
                return v0.archive();
            }).toList();
            for (BarInstall.InstalledMap map : BarInstall.listMaps(maps.getParent())) {
                if (!generated.contains(map.archive())) {
                    this.allMaps.add(new MapEntry(map.archive(), map.name(), map.description() + (map.author().isEmpty() ? "" : " By " + map.author() + "."), List.of(), map.mapFile(), false));
                }
            }
        }
        if (select != null) {
            this.thumbnails.remove(select);
        }
        filterLibrary();
        for (int i = 0; i < this.libraryModel.size(); i++) {
            if (((MapEntry) this.libraryModel.get(i)).archive().equals(select)) {
                this.library.setSelectedIndex(i);
                this.library.ensureIndexIsVisible(i);
            }
        }
        loadThumbnails();
    }

    private void filterLibrary() {
        MapEntry selected = (MapEntry) this.library.getSelectedValue();
        String filter = this.search.getText().trim().toLowerCase(Locale.ROOT);
        this.libraryModel.clear();
        for (MapEntry entry : this.allMaps) {
            if (filter.isEmpty() || (entry.name() + " " + entry.description()).toLowerCase(Locale.ROOT).contains(filter)) {
                this.libraryModel.addElement(entry);
            }
        }
        if (selected != null && this.libraryModel.contains(selected)) {
            this.library.setSelectedValue(selected, false);
        } else if (this.libraryModel.size() > 0) {
            this.library.setSelectedIndex(0);
            this.library.ensureIndexIsVisible(0);
        }
        filterCreateList();
    }

    private void filterCreateList() {
        boolean zIsOfficial;
        MapEntry selected = (MapEntry) this.createList.getSelectedValue();
        String filter = this.createSearch.getText().trim().toLowerCase(Locale.ROOT);
        this.createModel.clear();
        this.createModel.addElement(GENERATOR_ENTRY);
        List<MapEntry> entries = pickerEntries();
        MapEntry newest = this.newestGenerated == null ? null : entries.stream().filter(entry -> {
            return this.newestGenerated.equals(entry.archive());
        }).findFirst().orElse(null);
        if (newest != null) {
            entries.remove(newest);
            this.createModel.addElement(newest);
        }
        if (this.createSort.getSelectedIndex() == 1) {
            entries.sort(Comparator.comparingInt(entry2 -> {
                return -playCount(entry2);
            }).thenComparing(entry3 -> {
                return entry3.label().toLowerCase(Locale.ROOT);
            }));
        } else if (this.createSort.getSelectedIndex() == 2) {
            entries.sort(Comparator.comparingInt(entry4 -> {
                OfficialMap info = officialInfo(entry4);
                if (info == null) {
                    return 3;
                }
                if (info.pools().stream().anyMatch(pool -> {
                    return pool.startsWith("popular");
                })) {
                    return 0;
                }
                return info.pools().isEmpty() ? 2 : 1;
            }).thenComparing(entry5 -> {
                return entry5.label().toLowerCase(Locale.ROOT);
            }));
        } else {
            entries.sort(Comparator.comparing(entry6 -> {
                return Boolean.valueOf(!entry6.generated());
            }).thenComparing(entry7 -> {
                return entry7.label().toLowerCase(Locale.ROOT);
            }));
        }
        for (MapEntry entry8 : entries) {
            switch (this.createFilter.getSelectedIndex()) {
                case 1:
                    zIsOfficial = entry8.generated();
                    break;
                case 2:
                    zIsOfficial = isOfficial(entry8);
                    break;
                case 3:
                    zIsOfficial = entry8.archive() != null;
                    break;
                default:
                    zIsOfficial = true;
                    break;
            }
            boolean kind = zIsOfficial;
            if (kind && passesFilters(entry8) && (filter.isEmpty() || entry8.label().toLowerCase(Locale.ROOT).contains(filter) || entry8.name().toLowerCase(Locale.ROOT).contains(filter))) {
                this.createModel.addElement(entry8);
            }
        }
        MapEntry keep = selected;
        if (keep != null && keep != GENERATOR_ENTRY && !this.createModel.contains(keep)) {
            String base = baseName(keep.name());
            keep = null;
            for (int i = 1; i < this.createModel.size(); i++) {
                if (!((MapEntry) this.createModel.get(i)).generated() && baseName(((MapEntry) this.createModel.get(i)).name()).equals(base)) {
                    keep = (MapEntry) this.createModel.get(i);
                }
            }
        }
        this.createList.setSelectedValue((keep == null || !this.createModel.contains(keep)) ? GENERATOR_ENTRY : keep, true);
    }

    private void loadThumbnails() {
        List<MapEntry> entries = new ArrayList<>(this.allMaps);
        Thread loader = new Thread(() -> {
            BufferedImage image;
            Iterator i$ = entries.iterator();
            while (i$.hasNext()) {
                MapEntry entry = (MapEntry) i$.next();
                if (!this.thumbnails.containsKey(entry.archive()) && (image = readPreview(entry)) != null) {
                    BufferedImage small = new BufferedImage(160, 160, 1);
                    Graphics2D g = small.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g.drawImage(image.getScaledInstance(320, 320, 16), 0, 0, 160, 160, (ImageObserver) null);
                    g.dispose();
                    this.thumbnails.put(entry.archive(), new ScaledIcon(small));
                    SwingUtilities.invokeLater(() -> {
                        this.library.repaint();
                        this.createList.repaint();
                        this.tiles.repaint();
                    });
                }
            }
        }, "thumbnails");
        loader.setDaemon(true);
        loader.start();
    }

    private static MapEntry readEntry(Path archive) {
        try {
            ZipFile zip = new ZipFile(archive.toFile());
            try {
                ZipEntry mapInfo = zip.getEntry("mapinfo.lua");
                if (mapInfo == null) {
                    zip.close();
                    return null;
                }
                String text = new String(zip.getInputStream(mapInfo).readAllBytes(), StandardCharsets.UTF_8);
                if (!text.contains("generator = \"neroxis\"")) {
                    zip.close();
                    return null;
                }
                Matcher name = Pattern.compile("name = \"([^\"]*)\"").matcher(text);
                Matcher description = Pattern.compile("description = \"([^\"]*)\"").matcher(text);
                float mapElmos = 0.0f;
                ZipEntry smf = zip.stream().filter(entry -> {
                    return entry.getName().endsWith(".smf");
                }).findFirst().orElse(null);
                if (smf != null) {
                    InputStream stream = zip.getInputStream(smf);
                    try {
                        byte[] header = stream.readNBytes(32);
                        mapElmos = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).getInt(24) * 8.0f;
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
                List<float[]> starts = new ArrayList<>();
                Matcher start = Pattern.compile("\\[(\\d+)] = \\{startPos = \\{x = (\\d+), z = (\\d+)}}").matcher(text);
                while (start.find() && mapElmos > SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
                    int index = Integer.parseInt(start.group(1));
                    Matcher ally = Pattern.compile("\\[team" + index + "]\\s*\\{[^}]*allyteam=(\\d+);").matcher(text);
                    float[] fArr = new float[3];
                    fArr[0] = Integer.parseInt(start.group(2)) / mapElmos;
                    fArr[1] = Integer.parseInt(start.group(3)) / mapElmos;
                    fArr[2] = ally.find() ? Integer.parseInt(ally.group(1)) : index;
                    starts.add(fArr);
                }
                MapEntry mapEntry = new MapEntry(archive, name.find() ? name.group(1) : archive.getFileName().toString(), description.find() ? generatedDescription(description.group(1)) : "", starts, smf != null ? smf.getName() : "", true);
                zip.close();
                return mapEntry;
            } catch (Throwable th3) {
                try {
                    zip.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
                throw th3;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private BarInstall.Marks readMarks(MapEntry entry) {
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        return BarInstall.readMarks(data, this.home.resolve("cache").resolve("minimaps"), new BarInstall.InstalledMap(entry.archive(), entry.name(), "", "", entry.mapFile()));
    }

    private BufferedImage readPreview(MapEntry entry) {
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        return BarInstall.readMinimap(data, this.home.resolve("cache").resolve("minimaps"), new BarInstall.InstalledMap(entry.archive(), entry.name(), "", "", entry.mapFile()));
    }

    private void showSelected() {
        MapEntry entry = (MapEntry) this.library.getSelectedValue();
        this.play.setEnabled(entry != null);
        this.delete.setEnabled(entry != null && entry.generated());
        if (entry == null) {
            this.preview.setMap(null, List.of());
            this.title.setText("No map selected");
            setChips(new String[0]);
            return;
        }
        this.preview.setMap(readPreview(entry), entry.starts());
        Thread marksLoader = new Thread(() -> {
            BarInstall.Marks marks = readMarks(entry);
            SwingUtilities.invokeLater(() -> {
                if (this.library.getSelectedValue() == entry) {
                    this.preview.setMarks(marks);
                }
            });
        }, "map-marks");
        marksLoader.setDaemon(true);
        marksLoader.start();
        this.title.setText(entry.name().replace('_', ' '));
        List<String> labels = new ArrayList<>();
        Matcher numbers = Pattern.compile("(\\d+) players, (\\d+) metal spots, (\\d+) geo vents").matcher(entry.description());
        try {
            labels.add(String.format("%.0f MB", Double.valueOf(Files.size(entry.archive()) / 1000000.0d)));
        } catch (IOException e) {
        }
        if (numbers.find()) {
            labels.add(numbers.group(1) + " players");
            labels.add(numbers.group(2) + " metal spots");
            labels.add(numbers.group(3) + " geo vents");
        }
        Matcher areas = Pattern.compile("Start areas: ([^.]*)\\.").matcher(entry.description());
        if (areas.find()) {
            labels.add(areas.group(1));
        }
        if (!entry.generated()) {
            labels.add("official BAR map");
        }
        setChips((String[]) labels.toArray(new String[0]));
    }

    private void setChips(String... labels) {
        this.chips.removeAll();
        for (String label : labels) {
            JLabel chip = new JLabel(this, label) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.10
                final /* synthetic */ BarLauncher this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                protected void paintComponent(Graphics graphics) {
                    Graphics2D g = (Graphics2D) graphics;
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(BarLauncher.CARD);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g.setColor(BarLauncher.CARD_BORDER);
                    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                    super.paintComponent(graphics);
                }
            };
            chip.setForeground(TEXT);
            chip.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            this.chips.add(chip);
        }
        this.chips.revalidate();
        this.chips.repaint();
    }

    private void deleteSelected() {
        MapEntry entry = (MapEntry) this.library.getSelectedValue();
        if (entry != null && JOptionPane.showConfirmDialog(this.frame, "Delete " + entry.name() + "?", "Delete map", 2) == 0) {
            try {
                Files.deleteIfExists(entry.archive());
            } catch (IOException e) {
                this.log.append("Could not delete the map, is BAR using it? " + e.getMessage() + "\n");
            }
            refreshLibrary(null);
        }
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(Path.of(this.barFolder.getText().trim(), new String[0]).resolve("maps").toFile());
        } catch (Exception e) {
            this.log.append("Could not open the folder: " + e.getMessage() + "\n");
        }
    }

    private static Path newestArchive(Path maps) {
        try {
            Stream<Path> files = Files.list(maps);
            try {
                Path pathOrElse = files.filter(path -> {
                    return path.getFileName().toString().endsWith(".sdz");
                }).max(Comparator.comparingLong(path2 -> {
                    return path2.toFile().lastModified();
                })).orElse(null);
                if (files != null) {
                    files.close();
                }
                return pathOrElse;
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
            return null;
        }
    }

    private boolean startEngine(Path script) {
        return startEngine(script, null);
    }

    private static void installGameWidgets(Path data) {
        for (String name : List.of("barmap_return_to_lobby.lua", "barmap_own_colour.lua")) {
            try {
                InputStream stream = BarLauncher.class.getResourceAsStream(name);
                if (stream != null) {
                    try {
                        byte[] wanted = stream.readAllBytes();
                        Path file = data.resolve("LuaUI").resolve("Widgets").resolve(name);
                        if (!Files.isRegularFile(file, new LinkOption[0]) || !Arrays.equals(Files.readAllBytes(file), wanted)) {
                            Files.createDirectories(file.getParent(), new FileAttribute[0]);
                            Files.write(file, wanted, new OpenOption[0]);
                            if (stream != null) {
                                stream.close();
                            }
                        } else if (stream != null) {
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
                } else if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private boolean startEngine(Path script, String engineVersion) {
        return startEngine(script, engineVersion, false);
    }

    private boolean startEngine(Path script, String engineVersion, boolean online) {
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        MacOs.prepare(data);
        installGameWidgets(data);
        Path wanted = engineVersion != null ? data.resolve("engine").resolve("recoil_" + engineVersion) : null;
        Path engine = (wanted == null || !Platform.hasEngine(wanted)) ? newestEngine(data) : wanted;
        if (engine == null) {
            this.createStatus.setText(MacOs.isMac() ? MacOs.noEngine() : "No engine found in " + String.valueOf(data.resolve("engine")));
            return false;
        }
        try {
            Path config = BarInstall.barStudioSettings(data, online ? this.lobby.settingsForGame() : Map.of());
            String executable = System.getProperty("neroxis.launcher.engine", Platform.program("spring"));
            List<String> command = new ArrayList<>(List.of(engine.resolve(executable).toString(), "--write-dir", data.toString(), "--isolation"));
            if (config != null) {
                command.add("--config");
                command.add(config.toString());
            }
            command.add(script.toString());
            Process game = new ProcessBuilder(command).directory(engine.toFile()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            this.engineRunning = true;
            game.onExit().thenRun(() -> {
                this.engineRunning = false;
                this.engineExited = System.currentTimeMillis();
            });
            return true;
        } catch (IOException e) {
            this.createStatus.setText("Could not start BAR: " + e.getMessage());
            return false;
        }
    }

    private void launch(MapEntry entry) {
        int[] allyOfSlot;
        float[][] boxes;
        List<String> humans = new ArrayList<>();
        humans.add(cleanName(this.playerName.getText(), "Player"));
        if (0 != 0) {
            for (String friend : this.friends.getText().split(",")) {
                if (!friend.isBlank()) {
                    humans.add(cleanName(friend, "Friend"));
                }
            }
        }
        if (!entry.starts().isEmpty()) {
            List<float[]> starts = new ArrayList<>(entry.starts());
            starts.sort(Comparator.comparingDouble(start -> {
                return start[2];
            }));
            allyOfSlot = new int[starts.size()];
            int teamCount = 0;
            for (int i = 0; i < starts.size(); i++) {
                allyOfSlot[i] = (int) starts.get(i)[2];
                teamCount = Math.max(teamCount, allyOfSlot[i] + 1);
            }
            boxes = new float[teamCount][];
            for (int team = 0; team < teamCount; team++) {
                float left = 1.0f;
                float top = 1.0f;
                float right = 0.0f;
                float bottom = 0.0f;
                for (float[] start2 : starts) {
                    if (((int) start2[2]) == team) {
                        left = Math.min(left, start2[0]);
                        right = Math.max(right, start2[0]);
                        top = Math.min(top, start2[1]);
                        bottom = Math.max(bottom, start2[1]);
                    }
                }
                boxes[team] = new float[]{Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, left - 0.09f), Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, top - 0.09f), Math.min(1.0f, right + 0.09f), Math.min(1.0f, bottom + 0.09f)};
            }
        } else {
            int perTeam = ((Integer) this.officialPerTeam.getValue()).intValue();
            allyOfSlot = new int[perTeam * 2];
            for (int i2 = perTeam; i2 < allyOfSlot.length; i2++) {
                allyOfSlot[i2] = 1;
            }
            Map<Integer, float[][]> known = this.startBoxes.get(entry.name().toLowerCase(Locale.ROOT));
            boxes = (known == null || known.get(2) == null) ? new float[][]{new float[]{SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 0.25f, 1.0f}, new float[]{0.75f, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, 1.0f, 1.0f}} : known.get(2);
        }
        boolean together = 0 != 0 && this.coop.isSelected();
        int hostPort = 0 != 0 ? BarInstall.freeHostPort() : 0;
        String script = BarInstall.buildScript(entry.name(), allyOfSlot, boxes, humans, together, false, this.password.getText().trim(), hostPort);
        long time = System.currentTimeMillis();
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        Path scriptFile = data.resolve("neroxis_scripts").resolve("games").resolve("game-" + time + ".txt");
        try {
            Files.createDirectories(scriptFile.getParent(), new FileAttribute[0]);
            Files.writeString(scriptFile, script, new OpenOption[0]);
            if (!startEngine(scriptFile)) {
                return;
            }
            int teams = boxes.length;
            String format = teams == 2 ? (allyOfSlot.length / 2) + "v" + (allyOfSlot.length - (allyOfSlot.length / 2)) : allyOfSlot.length + " players, " + teams + " teams";
            String summary = (0 != 0 ? "Hosted, " + humans.size() + " humans" : "Skirmish vs AI") + " · " + format;
            String name = this.gameName.getText().isBlank() ? "My game" : this.gameName.getText().trim().replace('\t', ' ');
            this.games.removeIf(game -> {
                return game.title().equals(name) && game.mapName().equals(entry.name());
            });
            this.games.addFirst(new GameRecord(time, name, entry.archive().getFileName().toString(), entry.name(), summary, scriptFile.toString()));
            saveGames();
            showGames();
            if (this.createDialog != null) {
                this.createDialog.setVisible(false);
            }
            if (0 != 0) {
                JOptionPane.showMessageDialog(this.frame, "The game is starting and waits for " + (humans.size() - 1) + " friend(s) on UDP port " + hostPort + (hostPort != 8452 ? " (8452 is in use on this PC)" : "") + ".\nThey join with \"Join game\": your IP address, port " + hostPort + " and their name:\n" + String.join(", ", humans.subList(1, humans.size())) + "\nThey need the same map, generated maps can be rebuilt from their code.", "Hosting", 1);
            }
        } catch (IOException e) {
            this.createStatus.setText("Could not write the start script: " + e.getMessage());
        }
    }

    private static String cleanName(String name, String fallback) {
        String clean = name.trim().replaceAll("[^A-Za-z0-9_\\[\\]]", "");
        return clean.isEmpty() ? fallback : clean;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$BackgroundPanel.class */
    static final class BackgroundPanel extends JPanel {
        private static final int PICTURE_WIDTH = 3840;
        private static final int PICTURE_HEIGHT = 2160;
        private static boolean missing;
        private static volatile int veil = 35;
        private BufferedImage scaled;
        private BufferedImage artwork;
        private double artworkDetail;

        static void setBrightness(int brightness) {
            veil = Math.max(0, Math.min(215, (int) Math.round(((double) (100 - brightness)) * 1.4d)));
        }

        void setArtwork(BufferedImage picture, double detail) {
            if (picture != this.artwork || detail != this.artworkDetail) {
                this.artwork = picture;
                this.artworkDetail = detail;
                this.scaled = null;
                repaint();
            }
        }

        BackgroundPanel(LayoutManager layout) {
            super(layout);
            this.artworkDetail = 0.25d;
            setBackground(BarLauncher.BACKGROUND);
        }

        protected void paintComponent(Graphics graphics) {
            Image image;
            super.paintComponent(graphics);
            if (missing && this.artwork == null) {
                return;
            }
            Graphics2D g = (Graphics2D) graphics;
            int pictureWidth = this.artwork != null ? this.artwork.getWidth() : PICTURE_WIDTH;
            int pictureHeight = this.artwork != null ? this.artwork.getHeight() : PICTURE_HEIGHT;
            double scale = Math.max(((double) getWidth()) / ((double) pictureWidth), ((double) getHeight()) / ((double) pictureHeight));
            int width = (int) Math.ceil(((double) pictureWidth) * scale);
            int height = (int) Math.ceil(((double) pictureHeight) * scale);
            double device = g.getTransform().getScaleX();
            double detail = this.artwork != null ? this.artworkDetail : 1.0d;
            int pixelWidth = Math.max(1, (int) Math.round(((double) width) * device * detail));
            int pixelHeight = Math.max(1, (int) Math.round(((double) height) * device * detail));
            Image image2 = this.scaled;
            if (image2 == null || image2.getWidth() != pixelWidth || image2.getHeight() != pixelHeight) {
                if (this.artwork != null) {
                    image2 = this.artwork;
                } else {
                    try {
                        InputStream stream = BarLauncher.class.getResourceAsStream("barmapstudio-background.jpg");
                        if (stream != null) {
                            try {
                                image = ImageIO.read(stream);
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
                        } else {
                            image = null;
                        }
                        image2 = image;
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        image2 = null;
                    }
                }
                if (image2 == null) {
                    missing = true;
                    return;
                }
                while (true) {
                    if (image2.getWidth() <= pixelWidth && image2.getHeight() <= pixelHeight) {
                        break;
                    }
                    int nextWidth = Math.max(pixelWidth, image2.getWidth() / 2);
                    int nextHeight = Math.max(pixelHeight, image2.getHeight() / 2);
                    Image bufferedImage = new BufferedImage(nextWidth, nextHeight, 1);
                    Graphics2D draw = bufferedImage.createGraphics();
                    draw.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    draw.drawImage(image2, 0, 0, nextWidth, nextHeight, (ImageObserver) null);
                    draw.dispose();
                    image2 = bufferedImage;
                }
                this.scaled = image2;
            }
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(image2, this.artwork != null ? (getWidth() - width) / 2 : getWidth() - width, (getHeight() - height) / 2, width, height, (ImageObserver) null);
            if (this.artwork != null) {
                int extra = this.artworkDetail >= 1.0d ? 0 : 45;
                g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(10, 12, 18, Math.min(235, Math.max(0, (veil - 20) + extra))), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, getHeight(), new Color(10, 12, 18, Math.min(245, veil + extra + 40))));
            } else {
                g.setColor(new Color(12, 14, 20, veil));
            }
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JComponent buildPlayView() {
        JPanel view = new BackgroundPanel(new BorderLayout());
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        JPanel actions = new JPanel(new FlowLayout(0, 10, 0));
        actions.setOpaque(false);
        JButton create = new JButton("Create game");
        create.setBackground(ACCENT);
        create.setForeground(new Color(25, 18, 8));
        create.setFont(create.getFont().deriveFont(1, 14.0f));
        create.setPreferredSize(new Dimension(150, 36));
        create.addActionListener(e -> {
            openCreateGame(null);
        });
        actions.add(create);
        JButton generateHere = new JButton("Generate map");
        generateHere.setFont(generateHere.getFont().deriveFont(1, 13.0f));
        generateHere.setPreferredSize(new Dimension(150, 36));
        generateHere.setToolTipText("Makes a new map right now (with the settings of the generator, a 4v4 when they are closed) and shows it on the MAPS page; from there it can be played or hosted");
        generateHere.addActionListener(e2 -> {
            if (!this.generate.isEnabled()) {
                return;
            }
            if (!this.generatorGear) {
                applyDefaultTemplate();
            }
            generateHere.setEnabled(false);
            generateHere.setText("Generating...");
            generate(created -> {
                generateHere.setEnabled(true);
                generateHere.setText("Generate map");
                if (created != null) {
                    this.navigation.get(2).doClick();
                    this.search.setText("");
                    this.library.setSelectedValue(created, true);
                    return;
                }
                JOptionPane.showMessageDialog(this.frame, "The map could not be generated. The log on the MAPS page says why.");
            });
        });
        toolbar.add(actions, "West");
        this.onlineStatus.setForeground(MUTED);
        JPanel right = new JPanel(new FlowLayout(2, 8, 0));
        right.setOpaque(false);
        right.add(this.onlineStatus);
        right.add(Box.createHorizontalStrut(8));
        JToggleButton ours = new JToggleButton("Studio games", true);
        JToggleButton official = new JToggleButton("Official BAR games");
        ButtonGroup whose = new ButtonGroup();
        for (JToggleButton button : List.of(ours, official)) {
            whose.add(button);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(150, 36));
            button.addActionListener(e3 -> {
                this.officialGames = official.isSelected();
                if (this.officialGames) {
                    loadOfficialBattles();
                    this.officialRefresh.start();
                } else {
                    this.officialRefresh.stop();
                }
                showGames();
            });
        }
        this.tableMode = Files.exists(this.home.resolve(".games-as-table"), new LinkOption[0]);
        JToggleButton table = new JToggleButton("▦  Table", this.tableMode);
        JToggleButton tileButton = new JToggleButton("▣  Tiles", !this.tableMode);
        ButtonGroup viewGroup = new ButtonGroup();
        for (JToggleButton button2 : List.of(table, tileButton)) {
            viewGroup.add(button2);
            button2.setFocusPainted(false);
            button2.setPreferredSize(new Dimension(96, 36));
            button2.addActionListener(e4 -> {
                this.tableMode = table.isSelected();
                try {
                    if (this.tableMode) {
                        Files.writeString(this.home.resolve(".games-as-table"), "", new OpenOption[0]);
                    } else {
                        Files.deleteIfExists(this.home.resolve(".games-as-table"));
                    }
                } catch (IOException e4) {
                }
                showGames();
            });
            right.add(button2);
        }
        toolbar.add(right, "East");
        view.add(toolbar, "North");
        this.tiles.setOpaque(false);
        JScrollPane scroll = new JScrollPane(this.tiles);
        scroll.setBorder((Border) null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(31);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        view.add(scroll, "Center");
        JPanel details = new JPanel(new BorderLayout(0, 12));
        details.setOpaque(false);
        this.gameDetailsPanel = details;
        details.setBorder(BorderFactory.createEmptyBorder(6, 8, 18, 22));
        details.setPreferredSize(new Dimension(340, 100));
        this.gamePreview.setPreferredSize(new Dimension(310, 310));
        details.add(this.gamePreview, "North");
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, 1));
        text.setOpaque(false);
        this.gameTitle.setFont(this.gameTitle.getFont().deriveFont(0, 19.0f));
        this.gameTitle.setForeground(TEXT);
        this.gameDetails.setForeground(new Color(200, 205, 214));
        this.gameDetails.setVerticalAlignment(1);
        JComponent jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, 1));
        jPanel.setOpaque(false);
        for (JButton button3 : List.of(this.playAgain, this.removeGame)) {
            button3.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            button3.setMaximumSize(new Dimension(4000, 38));
        }
        this.gamePeople.setVerticalAlignment(1);
        this.gamePeople.setForeground(TEXT);
        JComponent surface = new Surface(new BorderLayout(), CARD, 12);
        surface.setBorder(BorderFactory.createEmptyBorder(10, 14, 12, 12));
        surface.add(this.gamePeople, "Center");
        for (JComponent part : new JComponent[]{this.gameTitle, this.gameDetails, jPanel, surface}) {
            part.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        }
        text.add(this.gameTitle);
        text.add(Box.createVerticalStrut(6));
        text.add(this.gameDetails);
        text.add(Box.createVerticalStrut(12));
        text.add(jPanel);
        text.add(Box.createVerticalStrut(12));
        text.add(surface);
        this.gamePeopleCard = surface;
        JPanel textCard = new Surface(new BorderLayout(), new Color(14, 16, 22, 225), 14);
        textCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        textCard.add(text, "Center");
        JPanel textHolder = new JPanel(new BorderLayout());
        textHolder.setOpaque(false);
        textHolder.add(textCard, "North");
        details.add(textHolder, "Center");
        this.playAgain.setFont(this.playAgain.getFont().deriveFont(1, 14.0f));
        this.playAgain.setPreferredSize(new Dimension(100, 38));
        this.playAgain.addActionListener(e5 -> {
            if (this.selectedTile != null) {
                this.selectedTile.open().run();
            }
        });
        this.removeGame.addActionListener(e6 -> {
            if (this.selectedTile != null) {
                this.games.remove(this.selectedTile.record());
            }
            saveGames();
            showGames();
        });
        jPanel.add(this.playAgain);
        jPanel.add(Box.createVerticalStrut(6));
        jPanel.add(this.removeGame);
        JPanel east = new JPanel(new BorderLayout());
        east.setOpaque(false);
        east.setPreferredSize(new Dimension(340, 100));
        east.add(details, "Center");
        view.add(east, "East");
        return view;
    }

    private void showReplays() {
        this.replays.refresh();
    }

    private JComponent buildReplaysView() {
        return this.replays.view();
    }

    private JComponent buildStudioView() {
        if (this.studioPanel == null) {
            Path barData = Path.of(this.barFolder.getText().trim());
            this.studioPanel = new MapStudioPanel(
                this.home,
                barData,
                archive -> {
                    refreshLibrary(archive);
                },
                archive -> {
                    startEngine(barData.resolve("_neroxis_skirmish.txt"), null);
                }
            );
        }
        return this.studioPanel;
    }

    private JComponent buildMapsView() {
        JPanel view = new JPanel(new BorderLayout());
        view.setBackground(BACKGROUND);
        view.add(buildCenter(), "Center");
        view.add(buildLibrary(), "East");
        return view;
    }

    private Path gamesFile() {
        return this.home.resolve("launcher-games.tsv");
    }

    private void loadGames() {
        this.games.clear();
        try {
            if (Files.isRegularFile(gamesFile(), new LinkOption[0])) {
                for (String line : Files.readAllLines(gamesFile(), StandardCharsets.UTF_8)) {
                    String[] fields = line.split("\t");
                    if (fields.length == 6) {
                        this.games.add(new GameRecord(Long.parseLong(fields[0]), fields[1], fields[2], fields[3], fields[4], fields[5]));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            this.log.append("Could not read the game list: " + e.getMessage() + "\n");
        }
        showGames();
    }

    private void saveGames() {
        while (this.games.size() > 40) {
            this.games.removeLast();
        }
        try {
            Files.write(gamesFile(), this.games.stream().map(game -> {
                return String.join("\t", String.valueOf(game.time()), game.title(), game.archive(), game.mapName(), game.summary(), game.script());
            }).toList(), StandardCharsets.UTF_8, new OpenOption[0]);
        } catch (IOException e) {
            this.log.append("Could not save the game list: " + e.getMessage() + "\n");
        }
    }

    private void showGames() {
        String str;
        Color color;
        String str2;
        this.tiles.removeAll();
        if (this.tableMode) {
            this.tiles.setLayout(new BoxLayout(this.tiles, 1));
            this.tiles.setBorder(BorderFactory.createEmptyBorder(6, 22, 14, 10));
            this.tiles.add(new GameRow(this, null));
        } else {
            this.tiles.setLayout(new WrapLayout());
            this.tiles.setBorder(BorderFactory.createEmptyBorder(6, 14, 14, 6));
        }
        List<Tile> all = new ArrayList<>();
        if (this.officialGames) {
            List<Tile> official = this.officialTiles;
            int people = official.stream().mapToInt(tile -> {
                return tile.people().size();
            }).sum();
            JLabel jLabel = this.onlineStatus;
            if (this.officialNote.isEmpty()) {
                str2 = "●  official servers  ·  " + official.size() + " games  ·  " + people + " people";
            } else {
                str2 = this.officialNote;
            }
            jLabel.setText(str2);
            this.onlineStatus.setForeground(this.officialNote.isEmpty() ? GREEN : MUTED);
            for (Tile tile2 : official) {
                all.add(tile2);
                this.tiles.add(this.tableMode ? new GameRow(this, tile2) : new GameTile(this, tile2));
            }
            Object key = this.selectedTile != null ? this.selectedTile.key() : null;
            selectTile(all.stream().filter(tile3 -> {
                return tile3.key().equals(key);
            }).findFirst().orElse(all.isEmpty() ? null : (Tile) all.getFirst()));
            this.tiles.revalidate();
            this.tiles.repaint();
            return;
        }
        LobbyClient client = this.lobby.client();
        if (client.isLoggedIn()) {
            List<LobbyClient.Battle> battles = client.getBattles().stream().filter(battle -> {
                return battle.userCount() > 0;
            }).toList();
            JLabel jLabel2 = this.onlineStatus;
            String strServerName = this.lobby.serverName();
            long jCount = client.getUsers().stream().filter(user -> {
                return !user.bot();
            }).count();
            battles.size();
            jLabel2.setText("●  " + strServerName + "  ·  " + jCount + " online  ·  " + jLabel2 + " open games");
            this.onlineStatus.setForeground(GREEN);
            if (!this.tableMode && !battles.isEmpty()) {
                this.tiles.add(sectionLabel("OPEN GAMES"));
            }
            for (LobbyClient.Battle battle2 : battles) {
                LobbyClient.User founder = client.getUser(battle2.founder);
                boolean running = founder != null && founder.inGame();
                String players = battle2.userCount() + (battle2.userCount() == 1 ? " player" : " players") + (battle2.spectators > 1 ? ", " + (battle2.spectators - 1) + " watching" : "");
                List<String> people2 = battle2.users.stream().filter(name -> {
                    return !name.equalsIgnoreCase(battle2.founder);
                }).toList();
                String str3 = battle2.host != null ? battle2.host : battle2.founder;
                String str4 = people2.size() + "/16";
                boolean z = battle2.locked || battle2.passworded;
                String str5 = "battle:" + battle2.id;
                String str6 = battle2.title;
                String str7 = battle2.map;
                String str8 = players + "  ·  host " + battle2.founder;
                if (running) {
                    str = "game is running";
                } else if (battle2.locked) {
                    str = "locked";
                } else {
                    str = battle2.passworded ? "open, needs a password" : "open, join now";
                }
                if (running) {
                    color = LIVE_GAME;
                } else {
                    color = (battle2.locked || battle2.passworded) ? LOCKED_GAME : OPEN_GAME;
                }
                Tile tile4 = new Tile(str3, str4, z, people2, str5, str6, str7, str8, str, color, String.join(", ", battle2.users.stream().filter(name2 -> {
                    return !name2.equalsIgnoreCase(battle2.founder);
                }).toList()), "JOIN GAME", () -> {
                    this.lobby.join(battle2);
                }, null);
                all.add(tile4);
                this.tiles.add(this.tableMode ? new GameRow(this, tile4) : new GameTile(this, tile4));
            }
        } else {
            this.onlineStatus.setText("●  offline  ·  sign in to see the games of your server");
            this.onlineStatus.setForeground(MUTED);
        }
        if (!this.games.isEmpty()) {
            this.tiles.add(sectionLabel("MY GAMES"));
        }
        for (GameRecord game : this.games) {
            boolean present = findMap(game.mapName()) != null;
            Tile tile5 = new Tile("you, offline", game.summary(), false, List.of(), game, game.title(), game.mapName(), game.summary(), present ? "ready to play" : "map is missing", present ? GREEN : new Color(255, 96, 84), new SimpleDateFormat("d MMM yyyy, HH:mm").format(new Date(game.time())), "PLAY AGAIN", () -> {
                replay(game);
            }, game);
            all.add(tile5);
            this.tiles.add(this.tableMode ? new GameRow(this, tile5) : new GameTile(this, tile5));
        }
        Object key2 = this.selectedTile != null ? this.selectedTile.key() : null;
        selectTile(all.stream().filter(tile6 -> {
            return tile6.key().equals(key2);
        }).findFirst().orElse(all.isEmpty() ? null : (Tile) all.getFirst()));
        this.tiles.revalidate();
        this.tiles.repaint();
    }

    private static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ACCENT);
        label.setFont(label.getFont().deriveFont(1, 12.0f));
        label.setBorder(BorderFactory.createEmptyBorder(10, 4, 0, 0));
        label.setPreferredSize(new Dimension(3000, 28));
        label.setMaximumSize(new Dimension(4000, 34));
        label.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        return label;
    }

    private MapEntry findMap(String mapName) {
        return this.allMaps.stream().filter(entry -> {
            return entry.name().equalsIgnoreCase(mapName);
        }).findFirst().orElse(null);
    }

    private void selectTile(Tile tile) {
        String str;
        String strLabel;
        Object previous = this.selectedTile != null ? this.selectedTile.key() : null;
        this.selectedTile = tile;
        this.playAgain.setEnabled(tile != null);
        if (this.gameDetailsPanel != null) {
            this.gameDetailsPanel.setVisible(tile != null);
        }
        this.removeGame.setVisible((tile == null || tile.record() == null) ? false : true);
        if (tile == null) {
            this.gamePreview.setMap(null, List.of());
            this.gameTitle.setText(" ");
            this.gameDetails.setText(" ");
            this.gamePeopleCard.setVisible(false);
            this.playAgain.setText("PLAY");
        } else {
            MapEntry entry = findMap(tile.mapName());
            this.playAgain.setText(tile.action());
            this.gameTitle.setText(tile.title());
            if (entry != null) {
                strLabel = entry.label();
            } else {
                String strMapLabel = mapLabel(tile.mapName());
                if (tile.record() != null) {
                    str = " (map is missing)";
                } else {
                    str = String.valueOf(tile.key()).startsWith("bar:") ? "" : " (downloads when you join)";
                }
                strLabel = strMapLabel + str;
            }
            String mapLabel = strLabel;
            boolean online = tile.record() == null;
            boolean officialGame = String.valueOf(tile.key()).startsWith("bar:");
            this.gameDetails.setText("<html><div style='width:220px'><span style='color:#8c94a3'>Beyond All Reason</span><table cellpadding='1' cellspacing='0'><tr><td width='22'>▧</td><td>" + mapLabel + "</td></tr><tr><td>♟</td><td>" + tile.host() + "</td></tr><tr><td>⚇</td><td>" + (online ? tile.players() + " players" : tile.summary()) + "</td></tr><tr><td></td><td><span style='color:" + String.format("#%06x", Integer.valueOf(tile.statusColor().getRGB() & 16777215)) + "'>" + tile.status() + "</span></td></tr></table></div></html>");
            this.gamePeopleCard.setVisible(online);
            StringBuilder people = new StringBuilder(officialGame ? "<html><span style='color:#8c94a3'>A game on the official servers: it is joined with the lobby of Beyond All Reason.</span><br><br><b>People</b>" : "<html><b>Players</b>");
            for (String name : tile.people()) {
                LobbyClient.User person = this.lobby.client().getUser(name);
                people.append("<br>&nbsp;&nbsp;").append((person == null || !person.bot()) ? "" : "⚙ ").append(name).append(this.lobby.badgeHtml(name));
            }
            this.gamePeople.setText(people.append("</html>").toString());
            if (entry == null) {
                this.gamePreview.setMap(standIn(tile.mapName()), List.of());
                this.gamePreview.setMarks(BarInstall.Marks.NONE);
            } else if (!tile.key().equals(previous) || !tile.mapName().equals(this.shownTileMap)) {
                Thread loader = new Thread(() -> {
                    BufferedImage image = readPreview(entry);
                    BarInstall.Marks marks = readMarks(entry);
                    SwingUtilities.invokeLater(() -> {
                        if (this.selectedTile != null && this.selectedTile.key().equals(tile.key())) {
                            this.gamePreview.setMap(image, entry.starts());
                            this.gamePreview.setMarks(marks);
                        }
                    });
                }, "game-preview");
                loader.setDaemon(true);
                loader.start();
            }
            this.shownTileMap = tile.mapName();
        }
        this.tiles.repaint();
    }

    private void replay(GameRecord game) {
        if (game == null) {
            return;
        }
        if (findMap(game.mapName()) == null || !Files.isRegularFile(Path.of(game.script(), new String[0]), new LinkOption[0])) {
            JOptionPane.showMessageDialog(this.frame, "The map or the start script of this game is gone.");
        } else {
            startEngine(Path.of(game.script(), new String[0]));
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$GameTile.class */
    private final class GameTile extends JPanel {
        private static final int HEIGHT = 140;
        private static final int GLOW = 10;
        private final Tile tile;
        final /* synthetic */ BarLauncher this$0;

        GameTile(final BarLauncher this$0, final Tile tile) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.tile = tile;
            setOpaque(false);
            setPreferredSize(new Dimension(392, 160));
            setCursor(Cursor.getPredefinedCursor(12));
            addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.GameTile.1
                final /* synthetic */ GameTile this$1;

                {
                    Objects.requireNonNull(this);
                    this.this$1 = this;
                }

                public void mousePressed(MouseEvent event) {
                    this.this$1.this$0.selectTile(tile);
                    if (event.getClickCount() == 2) {
                        tile.open().run();
                    }
                }
            });
        }

        private static void person(Graphics2D g, int x, int y) {
            g.fillOval(x + 3, y, 7, 7);
            g.fillArc(x, y + 8, 13, 12, 0, 180);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            boolean selected = this.this$0.selectedTile != null && this.tile.key().equals(this.this$0.selectedTile.key());
            int width = getWidth() - 20;
            int height = getHeight() - 20;
            boolean glows = this.tile.record() == null;
            if (glows) {
                Color glow = this.tile.statusColor();
                for (int i = 10; i >= 1; i--) {
                    float fade = 1.0f - ((i - 1) / 10.0f);
                    g.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), Math.round(95.0f * fade * fade * fade) + 3));
                    g.setStroke(new BasicStroke(2.4f));
                    g.drawRoundRect(10 - i, 10 - i, (width + (2 * i)) - 1, (height + (2 * i)) - 1, 6 + (i * 2), 6 + (i * 2));
                }
            }
            g.translate(10, 10);
            g.setColor(selected ? new Color(50, 44, 36) : new Color(38, 41, 50));
            g.fillRect(0, 0, width, height);
            MapEntry entry = this.this$0.findMap(this.tile.mapName());
            ImageIcon icon = entry != null ? this.this$0.thumbnails.get(entry.archive()) : null;
            BufferedImage standIn = icon == null ? this.this$0.standIn(this.tile.mapName()) : null;
            g.setColor(new Color(13, 15, 19));
            g.fillRect(0, 0, height, height);
            if (icon != null) {
                g.drawImage(icon.getImage(), 0, 0, height, height, (ImageObserver) null);
            } else if (standIn != null) {
                g.drawImage(standIn, 0, 0, height, height, (ImageObserver) null);
            }
            g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, height - 46, new Color(0, 0, 0, 0), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, height, new Color(0, 0, 0, 215)));
            g.fillRect(0, height - 46, height, 46);
            g.setColor(Color.WHITE);
            g.setFont(getFont().deriveFont(12.0f));
            g.drawString(fit(g, entry != null ? entry.label() : BarLauncher.mapLabel(this.tile.mapName()), height - 20), 10, height - 10);
            if (this.tile.locked()) {
                g.setColor(new Color(0, 0, 0, 90));
                g.fillRoundRect(9, 15, 30, 25, 6, 6);
                g.setColor(new Color(235, 238, 242));
                g.setStroke(new BasicStroke(3.5f));
                g.drawArc(14, 4, 18, 24, 0, 180);
                g.fillRoundRect(8, 14, 30, 25, 6, 6);
                g.setColor(new Color(60, 64, 74));
                g.fillOval(20, 22, 6, 6);
                g.fillRect(22, 26, 2, 7);
            }
            int x = height + 12;
            int room = (width - x) - 10;
            g.setColor(BarLauncher.TEXT);
            g.setFont(getFont().deriveFont(1, 15.0f));
            g.drawString(fit(g, this.tile.title(), room), x, 30);
            g.setFont(getFont().deriveFont(13.0f));
            g.setColor(new Color(190, 196, 208));
            g.drawString("Beyond All Reason", x, 62);
            boolean online = this.tile.record() == null;
            g.setColor(new Color(225, 229, 236));
            person(g, x - 1, 73);
            person(g, x + 7, 73);
            g.drawString(fit(g, online ? this.tile.players() + " players" : this.tile.summary(), room - 28), x + 28, 88);
            person(g, x + 3, 96);
            String hostName = fit(g, this.tile.host(), room - 46);
            g.drawString(hostName, x + 28, 111);
            Color check = this.this$0.lobby.badgeColour(this.tile.host());
            if (check != null) {
                g.setColor(check);
                g.drawString("✔", x + 34 + g.getFontMetrics().stringWidth(hostName), 111);
            }
            g.setColor(this.tile.statusColor());
            g.fillOval(x + 5, 121, 8, 8);
            g.drawString(fit(g, this.tile.status() + ((online || this.tile.details().isBlank()) ? "" : "  ·  " + this.tile.details()), room - 28), x + 28, 131);
            if (glows) {
                Color glow2 = this.tile.statusColor();
                g.setColor(glow2);
                g.setStroke(new BasicStroke(selected ? 3.0f : 2.0f));
                g.drawRect(1, 1, width - 3, height - 3);
                if (selected) {
                    g.setColor(new Color(255, 255, 255, 120));
                    g.setStroke(new BasicStroke(1.0f));
                    g.drawRect(3, 3, width - 7, height - 7);
                } else {
                    for (int i2 = 2; i2 < 6; i2++) {
                        g.setColor(new Color(glow2.getRed(), glow2.getGreen(), glow2.getBlue(), 60 - (i2 * 10)));
                        g.setStroke(new BasicStroke(1.0f));
                        g.drawRect(i2, i2, (width - 1) - (2 * i2), (height - 1) - (2 * i2));
                    }
                }
            } else {
                g.setColor(selected ? BarLauncher.ACCENT : new Color(255, 255, 255, 28));
                g.setStroke(new BasicStroke(selected ? 2.0f : 1.0f));
                g.drawRect(selected ? 1 : 0, selected ? 1 : 0, width - (selected ? 2 : 1), height - (selected ? 2 : 1));
            }
            g.dispose();
        }

        private static String fit(Graphics2D g, String text, int width) {
            if (g.getFontMetrics().stringWidth(text) <= width) {
                return text;
            }
            while (text.length() > 1 && g.getFontMetrics().stringWidth(text + "...") > width) {
                text = text.substring(0, text.length() - 1);
            }
            return text + "...";
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$GameRow.class */
    private final class GameRow extends JPanel {
        private final Tile tile;
        final /* synthetic */ BarLauncher this$0;

        GameRow(final BarLauncher this$0, final Tile tile) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.tile = tile;
            setOpaque(false);
            setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            setPreferredSize(new Dimension(600, tile == null ? 38 : 36));
            setMaximumSize(new Dimension(4000, tile == null ? 38 : 36));
            if (tile != null) {
                setCursor(Cursor.getPredefinedCursor(12));
                addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.GameRow.1
                    final /* synthetic */ GameRow this$1;

                    {
                        Objects.requireNonNull(this);
                        this.this$1 = this;
                    }

                    public void mousePressed(MouseEvent event) {
                        this.this$1.this$0.selectTile(tile);
                        if (event.getClickCount() == 2) {
                            tile.open().run();
                        }
                    }
                });
            }
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int width = getWidth();
            int height = getHeight();
            int rest = width - 84;
            int[] columns = {84, 84 + ((rest * 30) / 100), 84 + ((rest * 42) / 100), 84 + ((rest * 62) / 100), 84 + ((rest * 84) / 100), width};
            int baseline = (height / 2) + 5;
            if (this.tile == null) {
                g.setColor(new Color(30, 33, 41));
                g.fillRect(0, 0, width, height - 2);
                g.setColor(BarLauncher.TEXT);
                g.setFont(getFont().deriveFont(1, 12.5f));
                g.drawString("Map", 40, baseline - 1);
                String[] titles = {"Title", "Players", "Host", "Map", "Status"};
                for (int i = 0; i < titles.length; i++) {
                    g.drawString(titles[i], columns[i], baseline - 1);
                }
                g.dispose();
                return;
            }
            boolean selected = this.this$0.selectedTile != null && this.tile.key().equals(this.this$0.selectedTile.key());
            if (selected) {
                g.setColor(new Color(92, 62, 22));
                g.fillRect(0, 0, width, height);
            }
            g.setColor(new Color(255, 255, 255, 14));
            g.fillRect(0, height - 1, width, 1);
            if (this.tile.locked()) {
                g.setColor(new Color(215, 220, 228));
                g.fillRoundRect(12, baseline - 9, 12, 10, 3, 3);
                g.setStroke(new BasicStroke(1.8f));
                g.drawArc(14, baseline - 16, 8, 12, 0, 180);
            }
            MapEntry entry = this.this$0.findMap(this.tile.mapName());
            ImageIcon icon = entry != null ? this.this$0.thumbnails.get(entry.archive()) : null;
            BufferedImage standIn = icon == null ? this.this$0.standIn(this.tile.mapName()) : null;
            g.setColor(new Color(13, 15, 19));
            g.fillRect(40, 1, height - 2, height - 2);
            if (icon != null) {
                g.drawImage(icon.getImage(), 40, 1, height - 2, height - 2, (ImageObserver) null);
            } else if (standIn != null) {
                g.drawImage(standIn, 40, 1, height - 2, height - 2, (ImageObserver) null);
            }
            g.setFont(getFont().deriveFont(13.0f));
            g.setColor(BarLauncher.TEXT);
            String[] cells = new String[5];
            cells[0] = this.tile.title();
            cells[1] = this.tile.players();
            cells[2] = this.tile.host();
            cells[3] = entry != null ? entry.label() : BarLauncher.mapLabel(this.tile.mapName());
            cells[4] = this.tile.status();
            int i2 = 0;
            while (i2 < cells.length) {
                g.setColor(i2 == 4 ? this.tile.statusColor() : BarLauncher.TEXT);
                g.drawString(GameTile.fit(g, cells[i2], (columns[i2 + 1] - columns[i2]) - 14), columns[i2], baseline);
                i2++;
            }
            g.dispose();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$WrapLayout.class */
    private static final class WrapLayout extends FlowLayout {
        WrapLayout() {
            super(0, 2, 2);
        }

        public Dimension preferredLayoutSize(Container target) {
            int width = target.getParent() != null ? target.getParent().getWidth() : target.getWidth();
            if (width <= 0) {
                width = 800;
            }
            Insets insets = target.getInsets();
            int available = ((width - insets.left) - insets.right) - getHgap();
            int x = 0;
            int rowHeight = 0;
            int height = insets.top + insets.bottom + getVgap();
            for (Component component : target.getComponents()) {
                Dimension size = component.getPreferredSize();
                if (x > 0 && x + size.width > available) {
                    height += rowHeight + getVgap();
                    x = 0;
                    rowHeight = 0;
                }
                x += size.width + getHgap();
                rowHeight = Math.max(rowHeight, size.height);
            }
            return new Dimension(width, height + rowHeight + getVgap());
        }

        public Dimension minimumLayoutSize(Container target) {
            return preferredLayoutSize(target);
        }
    }

    static synchronized BufferedImage sigil(String key) {
        int number = Math.floorMod(key == null ? 0 : key.toLowerCase(Locale.ROOT).hashCode(), SIGILS.length);
        if (SIGILS[number] == null) {
            try {
                InputStream stream = BarLauncher.class.getResourceAsStream("mapgen-sigil-" + (number + 1) + ".jpg");
                try {
                    SIGILS[number] = stream != null ? ImageIO.read(stream) : null;
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
            } catch (IOException e) {
                return null;
            }
        }
        return SIGILS[number];
    }

    private BufferedImage standIn(String mapName) {
        if (mapName == null) {
            return null;
        }
        if (isGeneratedName(mapName)) {
            return sigil(mapName);
        }
        String key = mapName.toLowerCase(Locale.ROOT);
        BufferedImage known = this.officialThumbs.get(key);
        OfficialMap info = this.officialInfo.get(key);
        if (known == null && info != null && this.officialThumbsLoading.add(key)) {
            Thread loader = new Thread(() -> {
                BufferedImage picture = officialPreview(info);
                if (picture != null) {
                    this.officialThumbs.put(key, picture);
                    SwingUtilities.invokeLater(() -> {
                        this.tiles.repaint();
                        if (this.selectedTile != null && mapName.equalsIgnoreCase(this.selectedTile.mapName()) && findMap(mapName) == null) {
                            this.gamePreview.setMap(picture, List.of());
                        }
                    });
                } else {
                    this.officialThumbsLoading.remove(key);
                }
            }, "official-thumb");
            loader.setDaemon(true);
            loader.start();
        }
        return known;
    }

    private BufferedImage generatorSigil() {
        BufferedImage bufferedImage;
        if (this.shownSigil == null) {
            int number = 1 + ((int) (Math.random() * 6.0d));
            try {
                InputStream stream = BarLauncher.class.getResourceAsStream("mapgen-sigil-" + number + ".jpg");
                if (stream != null) {
                    try {
                        bufferedImage = ImageIO.read(stream);
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
                } else {
                    bufferedImage = null;
                }
                this.shownSigil = bufferedImage;
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                this.shownSigil = null;
            }
        }
        return this.shownSigil;
    }

    private boolean openChangeMap() {
        this.createForRoom = true;
        openCreateGame(null);
        return true;
    }

    private void openCreateGame(MapEntry select) {
        this.shownSigil = null;
        loadMapLists();
        if (this.createDialog != null && !this.createDialog.isVisible() && select != null) {
            this.createForRoom = false;
        }
        if (this.createDialog == null) {
            buildCreateDialog();
        }
        String user = this.lobby.client().getUserName();
        if (user != null && !user.isBlank() && (this.gameName.getText().isBlank() || this.gameName.getText().equals("My game"))) {
            this.gameName.setText(user + " game");
        }
        if (select != null) {
            this.createSearch.setText("");
            this.createList.setSelectedValue(select, true);
        }
        this.createStatus.setText(" ");
        this.createDialog.setTitle(this.createForRoom ? "Change map" : "Create game");
        this.gameName.setVisible(!this.createForRoom);
        this.createTypeColumn.setVisible(!this.createForRoom);
        if (this.createUpdate != null) {
            this.createUpdate.run();
        }
        this.createDialog.setLocationRelativeTo(this.frame);
        this.createDialog.setVisible(true);
        this.createForRoom = false;
    }

    private void createOnline(MapEntry entry) {
        LobbyClient client = this.lobby.client();
        if (!client.isLoggedIn()) {
            this.createStatus.setText("Sign in to create an online game");
            this.lobby.showLogin();
            return;
        }
        if (!this.lobby.requirePrime("create a game")) {
            return;
        }
        LobbyClient.Battle room = null;
        for (LobbyClient.Battle battle : client.getBattles()) {
            LobbyClient.User founder = client.getUser(battle.founder);
            if (battle.userCount() == 0 && !battle.locked && !battle.passworded && founder != null && !founder.inGame()) {
                room = battle;
                break;
            }
        }
        if (room == null) {
            this.createStatus.setText("All rooms of the server are in use right now, join one of them or try again later");
            return;
        }
        List<String> lines = mapCommands(entry);
        if (lines == null) {
            return;
        }
        String roomTitle = this.gameName.getText().replaceAll("[^A-Za-z0-9 \\[\\]<>+|:-]", "").replaceAll(" +", " ").trim();
        if (!roomTitle.isEmpty() && !roomTitle.equalsIgnoreCase("My game")) {
            lines.add("$rename " + (roomTitle.length() > 40 ? roomTitle.substring(0, 40) : roomTitle));
        }
        this.lobby.joinAndSay(room, lines);
        this.createDialog.setVisible(false);
    }

    private List<String> mapCommands(MapEntry entry) {
        String str;
        List<String> lines = new ArrayList<>();
        if (entry == GENERATOR_ENTRY) {
            int perTeam = ((Integer) this.playersPerTeam.getValue()).intValue();
            boolean freeForAll = this.teams.getSelectedIndex() == 3;
            int teamCount = freeForAll ? Math.max(2, perTeam) : this.teams.getSelectedIndex() + 2;
            int size = freeForAll ? 1 : perTeam;
            if (size * teamCount > 16) {
                this.createStatus.setText("Generated maps have up to 16 start positions");
                return null;
            }
            String command = "!genmap " + String.join("v", Collections.nCopies(teamCount, String.valueOf(size)));
            String selectedStyle = String.valueOf(this.style.getSelectedItem());
            if (selectedStyle.equals("Random land map")) {
                List<String> pool = stylesThatFit(LAND_STYLES, size * teamCount, teamCount, SIZE_VALUES[this.size.getSelectedIndex()]);
                command = command + " " + pool.get((int) (Math.random() * ((double) pool.size())));
            } else if (!selectedStyle.startsWith("Random") && !selectedStyle.equals("Custom...")) {
                StringBuilder note = new StringBuilder();
                if (STYLE_LIMITS.containsKey(selectedStyle) && fitStyle(selectedStyle, size * teamCount, teamCount, SIZE_VALUES[this.size.getSelectedIndex()], note) == null) {
                    this.createStatus.setText(note.toString());
                    return null;
                }
                command = command + " " + selectedStyle;
            }
            if (SIZE_VALUES[this.size.getSelectedIndex()] > 0) {
                command = command + " size=" + SIZE_VALUES[this.size.getSelectedIndex()];
            }
            double base = ((Number) this.metalBase.getValue()).doubleValue();
            double max = Math.max(base, ((Number) this.metalMax.getValue()).doubleValue());
            if (base != 2.0d || max != 3.5d) {
                command = command + String.format(Locale.ROOT, " metal=%.1f maxmetal=%.1f", Double.valueOf(base), Double.valueOf(max));
            }
            if (this.metalUniform.isSelected()) {
                command = command + " uniform=1";
            }
            if (this.reclaim.getValue() != 10) {
                command = command + String.format(Locale.ROOT, " reclaim=%.1f", Float.valueOf(this.reclaim.getValue() / 10.0f));
            }
            if (this.symmetry.getSelectedIndex() > 0) {
                command = command + " symmetry=" + String.valueOf(this.symmetry.getSelectedItem());
            }
            lines.add(command + genmapExtras());
        } else {
            if (entry.generated()) {
                str = "!genmap map=" + entry.name();
            } else {
                str = isOfficial(entry) ? "!officialmap " + entry.name() : "!map " + entry.name();
            }
            lines.add(str);
        }
        return lines;
    }

    private static JLabel columnTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(0, 23.0f));
        label.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));
        return label;
    }

    private static void setTreeEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                setTreeEnabled(child, enabled);
            }
        }
    }

    private void buildCreateDialog() {
        this.createDialog = new JDialog(this.frame, "Create game", System.getProperty("neroxis.launcher.screenshot") == null);
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
        this.gameName.setFont(this.gameName.getFont().deriveFont(22.0f));
        this.gameName.setPreferredSize(new Dimension(100, 58));
        this.gameName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CARD_BORDER), BorderFactory.createEmptyBorder(4, 14, 4, 14)));
        this.gameName.putClientProperty("JTextField.placeholderText", "Title of the game");
        root.add(this.gameName, "North");
        JList<String> gameType = new JList<>(new String[]{"Online game", "Offline skirmish vs AI"});
        gameType.setSelectionMode(0);
        gameType.setBackground(CARD);
        gameType.setFixedCellHeight(30);
        gameType.setSelectedIndex(this.gameMode.getSelectedIndex());
        gameType.setCellRenderer(new DefaultListCellRenderer(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.12
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
                JLabel label = super.getListCellRendererComponent(list, value, index, selected, false);
                label.setText("<html>" + String.valueOf(value) + (index == 0 ? "&nbsp;&nbsp; <span style='color:#6f9bd8'>(default)</span>" : "") + "</html>");
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
                return label;
            }
        });
        JLabel typeHint = new JLabel();
        typeHint.setForeground(MUTED);
        typeHint.setVerticalAlignment(1);
        JPanel officialCard = card("Teams", grid(new JComponent[]{this.officialLabel, this.officialPerTeam}));
        JPanel offlineCard = card("Skirmish", grid(row("Your name", this.playerName)));
        JPanel typeExtras = new JPanel();
        typeExtras.setLayout(new BoxLayout(typeExtras, 1));
        typeExtras.setOpaque(false);
        typeHint.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        typeHint.setBorder(BorderFactory.createEmptyBorder(10, 4, 12, 0));
        typeExtras.add(typeHint);
        typeExtras.add(offlineCard);
        typeExtras.add(officialCard);
        JScrollPane typeScroll = new JScrollPane(gameType);
        typeScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        JPanel typeColumn = new JPanel(new BorderLayout());
        typeColumn.setOpaque(false);
        typeColumn.setPreferredSize(new Dimension(250, 100));
        typeColumn.add(columnTitle("Game type"), "North");
        typeColumn.add(typeScroll, "Center");
        typeColumn.add(typeExtras, "South");
        this.createTypeColumn = typeColumn;
        JComponent generatorOptions = buildOptions();
        JScrollPane optionScroll = new JScrollPane(generatorOptions);
        optionScroll.setBorder((Border) null);
        optionScroll.setHorizontalScrollBarPolicy(31);
        optionScroll.getViewport().setBackground(BACKGROUND);
        optionScroll.getVerticalScrollBar().setUnitIncrement(18);
        JPanel generatorColumn = new JPanel(new BorderLayout());
        generatorColumn.setOpaque(false);
        generatorColumn.setPreferredSize(new Dimension(350, 100));
        generatorColumn.add(columnTitle("Map generator"), "North");
        generatorColumn.add(optionScroll, "Center");
        this.generatorGear = Files.exists(this.home.resolve(".generator-settings-open"), new LinkOption[0]);
        this.playersPerTeam.setValue(4);
        this.teams.setSelectedIndex(0);
        this.formatButtons.forEach(button -> {
            button.setSelected(button.getText().equals("4v4"));
        });
        generatorColumn.setVisible(this.generatorGear);
        this.createSearch.putClientProperty("JTextField.placeholderText", "Search...");
        this.createSearch.putClientProperty("JTextField.showClearButton", true);
        this.createSearch.getDocument().addDocumentListener(onChange(this::filterCreateList));
        this.createList.setSelectionMode(0);
        this.createList.setBackground(CARD);
        this.createList.setFixedCellHeight(28);
        this.createList.setToolTipText("The gear next to \"Generate map\" opens the settings of the map generator");
        this.createList.setCellRenderer(new DefaultListCellRenderer(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.13
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
                Color color;
                JLabel label = super.getListCellRendererComponent(list, value, index, selected, false);
                MapEntry entry = (MapEntry) value;
                label.setText(entry == BarLauncher.GENERATOR_ENTRY ? "★  Generate map" : entry.label());
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
                if (entry != BarLauncher.GENERATOR_ENTRY) {
                    return label;
                }
                if (!selected) {
                    label.setForeground(BarLauncher.ACCENT);
                }
                JLabel gear = new JLabel("⚙");
                gear.setFont(gear.getFont().deriveFont(18.0f));
                if (this.this$0.generatorGear) {
                    color = BarLauncher.ACCENT;
                } else {
                    color = selected ? Color.WHITE : BarLauncher.MUTED;
                }
                gear.setForeground(color);
                gear.setBorder(BorderFactory.createEmptyBorder(0, 6, 2, 10));
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(label.getBackground());
                row.add(label, "Center");
                row.add(gear, "East");
                return row;
            }
        });
        JScrollPane listScroll = new JScrollPane(this.createList);
        listScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        listScroll.getVerticalScrollBar().setUnitIncrement(26);
        JButton randomMap = new JButton("Select random map");
        randomMap.setPreferredSize(new Dimension(100, 36));
        randomMap.addActionListener(e -> {
            if (this.createModel.size() > 1) {
                this.createList.setSelectedIndex(1 + ((int) (Math.random() * ((double) (this.createModel.size() - 1)))));
                this.createList.ensureIndexIsVisible(this.createList.getSelectedIndex());
            }
        });
        JPanel mapTop = new JPanel(new BorderLayout(0, 0));
        mapTop.setOpaque(false);
        mapTop.add(columnTitle("Map"), "North");
        mapTop.add(this.createSearch, "Center");
        this.createFilter.addActionListener(e2 -> {
            filterCreateList();
        });
        this.createSort.addActionListener(e3 -> {
            filterCreateList();
        });
        this.createSort.setToolTipText("Most played: how often a map was played on this server");
        JPanel filters = new JPanel(new GridLayout(3, 2, 6, 6));
        filters.setOpaque(false);
        filters.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        for (JComboBox<String> combo : List.of(this.createFilter, this.createSort, this.createGameType, this.createTerrain, this.createSize, this.createPlayers)) {
            if (combo != this.createFilter && combo != this.createSort) {
                combo.addActionListener(e4 -> {
                    filterCreateList();
                });
            }
            filters.add(combo);
        }
        this.createPlayers.setToolTipText("Maps that are made for this many players (between the minimum and the ideal maximum the game gives for the map)");
        mapTop.add(filters, "South");
        JPanel mapColumn = new JPanel(new BorderLayout(0, 10));
        mapColumn.setOpaque(false);
        mapColumn.add(mapTop, "North");
        mapColumn.add(listScroll, "Center");
        mapColumn.add(randomMap, "South");
        this.createPreview.setPreferredSize(new Dimension(330, 330));
        this.createMapTitle.setFont(this.createMapTitle.getFont().deriveFont(1, 16.0f));
        this.createMapTitle.setForeground(TEXT);
        this.createMapTitle.setBorder(BorderFactory.createEmptyBorder(8, 2, 2, 0));
        JLabel mapFacts = new JLabel(" ");
        mapFacts.setForeground(TEXT);
        mapFacts.setHorizontalAlignment(0);
        mapFacts.setBorder(BorderFactory.createEmptyBorder(2, 0, 6, 0));
        this.createMapDetails.setForeground(MUTED);
        this.createMapDetails.setVerticalAlignment(1);
        JPanel facts = new JPanel(new BorderLayout());
        facts.setOpaque(false);
        facts.add(this.createMapTitle, "North");
        facts.add(mapFacts, "Center");
        this.createVersions.setToolTipText("Version of the map. What you pick stays the version of this map for you.");
        this.createVersions.addActionListener(e5 -> {
            MapEntry current = (MapEntry) this.createList.getSelectedValue();
            if (this.fillingVersions || current == null || this.createVersions.getSelectedItem() == null) {
                return;
            }
            String wanted = String.valueOf(this.createVersions.getSelectedItem());
            if (wanted.equalsIgnoreCase(current.name())) {
                return;
            }
            String base = baseName(wanted);
            if (this.officialMaps.containsKey(wanted.toLowerCase(Locale.ROOT))) {
                this.versionChoices.remove(base);
            } else {
                this.versionChoices.setProperty(base, wanted);
            }
            try {
                Writer writer = Files.newBufferedWriter(versionChoicesFile(), new OpenOption[0]);
                try {
                    this.versionChoices.store(writer, "Versions of maps the player chose over the newest one");
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Throwable t$) {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Throwable x2) {
                            t$.addSuppressed(x2);
                        }
                    }
                    throw t$;
                }
            } catch (IOException e5) {
            }
            filterCreateList();
        });
        facts.add(this.createVersions, "South");
        JPanel mapText = new JPanel(new BorderLayout());
        mapText.setOpaque(false);
        mapText.add(facts, "North");
        mapText.add(this.createMapDetails, "Center");
        JButton newMap = new JButton("<html>Generate new map &nbsp;<span style='color:#F0962E'><b>NEW</b></span></html>");
        newMap.setPreferredSize(new Dimension(100, 36));
        newMap.setBackground(new Color(36, 62, 110));
        newMap.setForeground(Color.WHITE);
        newMap.setToolTipText("Generates a new map with the settings of the generator and selects it right under \"Generate map\": Create then makes the game with it");
        newMap.addActionListener(e6 -> {
            if (!this.generate.isEnabled()) {
                return;
            }
            if (!this.generatorGear) {
                applyDefaultTemplate();
            }
            generate(created -> {
                if (created != null) {
                    this.createList.setSelectedValue(created, true);
                    this.createStatus.setText("New map ready: " + this.createGame.getText() + " plays it, or generate another one");
                }
            });
        });
        JPanel previewColumn = new JPanel(new BorderLayout(0, 10));
        previewColumn.setOpaque(false);
        previewColumn.setPreferredSize(new Dimension(330, 100));
        JPanel previewTop = new JPanel(new BorderLayout());
        previewTop.setOpaque(false);
        previewTop.add(this.createPreview, "North");
        previewTop.add(mapText, "Center");
        previewColumn.add(previewTop, "Center");
        previewColumn.add(newMap, "South");
        JPanel middle = new JPanel(new BorderLayout(14, 0));
        middle.setOpaque(false);
        middle.add(generatorColumn, "West");
        middle.add(mapColumn, "Center");
        JPanel columns = new JPanel(new BorderLayout(14, 0));
        columns.setOpaque(false);
        columns.add(typeColumn, "West");
        columns.add(middle, "Center");
        columns.add(previewColumn, "East");
        root.add(columns, "Center");
        JPanel bottom = new JPanel(new BorderLayout(14, 6));
        bottom.setOpaque(false);
        this.progress.setIndeterminate(true);
        this.progress.setVisible(false);
        this.progress.setPreferredSize(new Dimension(100, 6));
        bottom.add(this.progress, "North");
        JButton cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(250, 40));
        cancel.addActionListener(e7 -> {
            this.createDialog.setVisible(false);
        });
        bottom.add(cancel, "West");
        this.createStatus.setForeground(MUTED);
        bottom.add(this.createStatus, "Center");
        JPanel buttons = new JPanel(new FlowLayout(2, 10, 0));
        buttons.setOpaque(false);
        this.generateOnly.setPreferredSize(new Dimension(140, 40));
        this.createGame.setPreferredSize(new Dimension(190, 40));
        this.createGame.setBackground(ACCENT);
        this.createGame.setForeground(new Color(25, 18, 8));
        this.createGame.setFont(this.createGame.getFont().deriveFont(1, 14.0f));
        buttons.add(this.generateOnly);
        buttons.add(this.createGame);
        bottom.add(buttons, "East");
        root.add(bottom, "South");
        final Runnable update = () -> {
            String str;
            String str2;
            String str3;
            String str4;
            String str5;
            MapEntry entry = (MapEntry) this.createList.getSelectedValue();
            boolean generator = entry == GENERATOR_ENTRY;
            boolean roomMode = this.createDialog != null && "Change map".equals(this.createDialog.getTitle());
            boolean online = roomMode || this.gameMode.getSelectedIndex() == 0;
            if (online) {
                str = "A public room on the server. Everybody can join, AIs and teams are set up in the room.";
            } else {
                str = "Starts right away on this computer against the AI.";
            }
            typeHint.setText("<html><div style='width:165px'>" + str + "</div></html>");
            offlineCard.setVisible(!online);
            officialCard.setVisible((online || entry == null || generator || !entry.starts().isEmpty()) ? false : true);
            JButton jButton = this.createGame;
            if (roomMode) {
                str2 = "Change map";
            } else {
                str2 = online ? "Create" : "Start skirmish";
            }
            jButton.setText(str2);
            this.generateOnly.setVisible((!generator || roomMode || online) ? false : true);
            this.createGame.setEnabled(entry != null && this.generate.isEnabled());
            setTreeEnabled(generatorOptions, generator && this.generate.isEnabled());
            if (generatorColumn.isVisible() != this.generatorGear) {
                generatorColumn.setVisible(this.generatorGear);
                this.createDialog.setSize(this.generatorGear ? 1320 : 980, UnitPlacer.MAX_UNIT_COUNT);
                this.createDialog.setLocationRelativeTo(this.frame);
            }
            List<MapEntry> versions = versionsOf(entry);
            this.fillingVersions = true;
            this.createVersions.removeAllItems();
            versions.forEach(version -> {
                this.createVersions.addItem(version.name());
            });
            if (entry != null) {
                this.createVersions.setSelectedItem(entry.name());
            }
            this.createVersions.setVisible(versions.size() > 1);
            this.fillingVersions = false;
            if (generator) {
                this.createMapTitle.setText("New generated map");
                mapFacts.setText(" ");
                JLabel jLabel = this.createMapDetails;
                if (this.generatorGear) {
                    str5 = "It is made with the settings of the map generator.";
                } else {
                    str5 = "A 4v4 map with slightly varied settings. The gear ⚙ next to \"Generate map\" opens the settings of the generator.";
                }
                jLabel.setText("<html><div style='width:225px'>A new random map is generated for this game, every player gets it automatically.<br><br>" + str5 + "</div></html>");
                this.createPreview.setPlaceholder("Generated when the game is created");
                this.createPreview.setMap(null, List.of());
                BufferedImage sigil = generatorSigil();
                if (sigil != null) {
                    this.createPreview.setMap(sigil, List.of());
                    this.createPreview.setMarks(BarInstall.Marks.NONE);
                }
            } else if (entry != null) {
                this.createMapTitle.setText(entry.label());
                boolean official = isOfficial(entry);
                OfficialMap info = officialInfo(entry);
                String officialMark = official ? "<span style='color:#3ab054'>✔ Official BAR map</span>" : "";
                if (info == null) {
                    str4 = "";
                } else {
                    String str6 = info.width() > 0 ? " &nbsp; ⛶ " + info.width() + " x " + info.height() : "";
                    if (info.playersMax() > 0) {
                        str3 = " &nbsp; ♟ " + ((info.playersMin() <= 0 || info.playersMin() >= info.playersMax()) ? "" : info.playersMin() + " - ") + info.playersMax() + " players";
                    } else {
                        str3 = "";
                    }
                    str4 = str6 + str3;
                }
                String officialFacts = str4;
                mapFacts.setText(official ? "<html>" + officialMark + officialFacts + "</html>" : " ");
                this.createMapDetails.setText("<html><div style='width:225px'>" + entry.description() + ((info == null || info.terrain().isEmpty()) ? "" : "<br><br><span style='color:#aab2c0'>" + String.join(" · ", info.gameTypes()) + "<br>" + String.join(", ", info.terrain()) + "</span>") + "</div></html>");
                this.createPreview.setPlaceholder(entry.archive() == null ? "Loading the preview..." : "Map preview");
                this.createPreview.setMap(null, List.of());
                Thread loader = entry.archive() == null ? new Thread(() -> {
                    BufferedImage picture = info != null ? officialPreview(info) : null;
                    SwingUtilities.invokeLater(() -> {
                        if (this.createList.getSelectedValue() == entry) {
                            this.createPreview.setPlaceholder("Preview after the download");
                            this.createPreview.setMap(picture, List.of());
                            this.createPreview.setMarks(BarInstall.Marks.NONE);
                        }
                    });
                }, "official-preview") : new Thread(() -> {
                    BufferedImage image = readPreview(entry);
                    BarInstall.Marks marks = readMarks(entry);
                    int[] mapSize = BarInstall.readMapSize(new BarInstall.InstalledMap(entry.archive(), entry.name(), "", "", entry.mapFile()));
                    SwingUtilities.invokeLater(() -> {
                        if (this.createList.getSelectedValue() == entry) {
                            this.createPreview.setMap(image, entry.starts());
                            this.createPreview.setMarks(marks);
                            if (!official) {
                                mapFacts.setText("<html>" + (mapSize != null ? "⛶ " + mapSize[0] + " x " + mapSize[1] : "") + (entry.starts().isEmpty() ? "" : " &nbsp; ♟ " + entry.starts().size() + " players") + "</html>");
                            }
                        }
                    });
                }, "create-preview");
                loader.setDaemon(true);
                loader.start();
            }
            typeColumn.revalidate();
        };
        this.createList.addListSelectionListener(e8 -> {
            update.run();
        });
        this.createList.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.14
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mousePressed(MouseEvent event) {
                int index = this.this$0.createList.locationToIndex(event.getPoint());
                if (index < 0 || this.this$0.createModel.get(index) != BarLauncher.GENERATOR_ENTRY || event.getX() < this.this$0.createList.getWidth() - 40 || !this.this$0.createList.getCellBounds(index, index).contains(event.getPoint())) {
                    return;
                }
                this.this$0.generatorGear = !this.this$0.generatorGear;
                try {
                    if (this.this$0.generatorGear) {
                        Files.writeString(this.this$0.home.resolve(".generator-settings-open"), "", new OpenOption[0]);
                    } else {
                        Files.deleteIfExists(this.this$0.home.resolve(".generator-settings-open"));
                    }
                } catch (IOException e9) {
                }
                this.this$0.createList.setSelectedValue(BarLauncher.GENERATOR_ENTRY, true);
                update.run();
                this.this$0.createList.repaint();
            }
        });
        gameType.addListSelectionListener(e9 -> {
            if (gameType.getSelectedIndex() >= 0 && gameType.getSelectedIndex() != this.gameMode.getSelectedIndex()) {
                this.gameMode.setSelectedIndex(gameType.getSelectedIndex());
            }
        });
        this.gameMode.addActionListener(e10 -> {
            update.run();
        });
        this.generateOnly.addActionListener(e11 -> {
            if (!this.generatorGear) {
                applyDefaultTemplate();
            }
        });
        this.generateOnly.addActionListener(e12 -> {
            generate(created -> {
                if (created != null) {
                    this.createList.setSelectedValue(created, true);
                }
            });
        });
        this.createGame.addActionListener(e13 -> {
            List<String> listMapCommands;
            MapEntry entry = (MapEntry) this.createList.getSelectedValue();
            if ("Change map".equals(this.createDialog.getTitle())) {
                if (entry == null) {
                    listMapCommands = null;
                } else {
                    listMapCommands = (entry != GENERATOR_ENTRY || this.generatorGear) ? mapCommands(entry) : new ArrayList<>(List.of("!genmap"));
                }
                List<String> lines = listMapCommands;
                if (lines != null) {
                    this.lobby.sayInRoom(lines);
                    this.createDialog.setVisible(false);
                    return;
                }
                return;
            }
            if (entry == GENERATOR_ENTRY && !this.generatorGear) {
                applyDefaultTemplate();
            }
            if (this.gameMode.getSelectedIndex() == 0) {
                if (entry != null) {
                    createOnline(entry);
                }
            } else {
                if (entry == GENERATOR_ENTRY) {
                    generate(created -> {
                        if (created != null) {
                            this.createList.setSelectedValue(created, true);
                            launch(created);
                        }
                    });
                    return;
                }
                if (entry != null && entry.archive() == null) {
                    this.createStatus.setText("This map is not on your computer yet: it downloads when you play it online");
                } else if (entry != null) {
                    launch(entry);
                }
            }
        });
        filterCreateList();
        update.run();
        this.createUpdate = update;
        this.createDialog.setContentPane(root);
        this.createDialog.setSize(this.generatorGear ? 1320 : 980, UnitPlacer.MAX_UNIT_COUNT);
    }

    private void applyDefaultTemplate() {
        Random random = new Random();
        this.playersPerTeam.setValue(4);
        this.teams.setSelectedIndex(0);
        this.formatButtons.forEach(button -> {
            button.setSelected(button.getText().equals("4v4"));
        });
        this.size.setSelectedIndex(0);
        this.style.setSelectedIndex(0);
        this.symmetry.setSelectedIndex(0);
        this.metalUniform.setSelected(false);
        this.metalBase.setValue(Double.valueOf(Math.round((2.0d + ((random.nextDouble() - 0.5d) * 0.4d)) * 10.0d) / 10.0d));
        this.metalMax.setValue(Double.valueOf(Math.round((3.5d + ((random.nextDouble() - 0.5d) * 0.6d)) * 10.0d) / 10.0d));
        this.reclaim.setValue((10 + random.nextInt(5)) - 2);
        this.resourceDensity.setValue(50);
        this.startMexes.setSelectedIndex(0);
        this.windMin.setValue(3);
        this.windMax.setValue(16);
        this.tidal.setValue(18);
    }

    private static Path newestEngine(Path data) {
        try {
            Stream<Path> engines = Files.list(data.resolve("engine"));
            try {
                Path pathOrElse = engines.filter(Platform::hasEngine).max(Comparator.comparingLong(path -> {
                    return path.toFile().lastModified();
                })).orElse(null);
                if (engines != null) {
                    engines.close();
                }
                return pathOrElse;
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
            return null;
        }
    }

    private void refreshGame() {
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        if (!BarFolder.isData(data)) {
            this.startScreen.gameRefreshing(false, null);
            return;
        }
        this.gameRefreshing = true;
        updateBarStatus();
        this.startScreen.gameRefreshing(true, null);
        BarInstall.refreshGame(data, () -> {
            SwingUtilities.invokeLater(() -> {
                this.gameRefreshing = false;
                updateBarStatus();
                this.startScreen.gameRefreshing(false, BarInstall.installedVersion(data, "byar"));
            });
        });
    }

    private void leaveStartScreen() {
        this.onStartScreen = false;
        this.lobby.startAfterChecks();
        this.views.show(this.viewPanel, this.inRoom ? "ROOM" : "PLAY");
        ((JToggleButton) this.navigation.getFirst()).setSelected(true);
    }

    private void updateBarStatus() {
        String str;
        String str2;
        String str3;
        Path data = Path.of(this.barFolder.getText().trim(), new String[0]);
        boolean found = Files.isDirectory(data.resolve("maps"), new LinkOption[0]) || (MacOs.isMac() && BarFolder.isData(data));
        String game = found ? BarInstall.installedVersion(data, "byar") : null;
        Path engine = found ? newestEngine(data) : null;
        if (found) {
            str = "●  " + (game != null ? game.replaceFirst("^Beyond All Reason ", "BAR ") : "Beyond All Reason found") + (engine != null ? "  ·  engine " + engine.getFileName().toString().replaceFirst("^recoil_", "") : "") + (this.gameRefreshing ? "  ·  checking for BAR updates..." : "");
        } else {
            str = "●  Beyond All Reason not found - choose folder";
        }
        String text = str;
        this.barStatus.setText(text);
        JLabel jLabel = this.barStatus;
        String strTrim = this.barFolder.getText().trim();
        String str4 = game != null ? "Game " + game + "<br>" : "";
        String str5 = engine != null ? "Engine folder " + String.valueOf(engine.getFileName()) + "<br>" : "";
        if (LauncherUpdater.steam()) {
            str2 = "Steam build ";
        } else {
            str2 = LauncherUpdater.appStore() ? "App Store build " : "Launcher build ";
        }
        String strRunningCommit = LauncherUpdater.runningCommit();
        if (LauncherUpdater.runningBuiltAt() > 0) {
            str3 = " of " + Instant.ofEpochMilli(LauncherUpdater.runningBuiltAt()).toString().substring(0, 16).replace('T', ' ') + " UTC";
        } else {
            str3 = "";
        }
        jLabel.setToolTipText("<html>" + strTrim + "<br>" + str4 + str5 + str2 + strRunningCommit + str3 + "<br>Click to choose another folder</html>");
        this.barStatus.setForeground(found ? GREEN : new Color(255, 96, 84));
    }

    private void updateReclaimLabel() {
        String str;
        float value = this.reclaim.getValue() / 10.0f;
        int metal = Math.round(value * 600.0f);
        JLabel jLabel = this.reclaimLabel;
        if (value == SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT) {
            str = "no reclaim";
        } else if (value <= 1.5f) {
            str = "BAR like, about " + metal + " metal per player";
        } else if (value < 5.0f) {
            str = "plenty, about " + metal + " metal per player";
        } else {
            str = "Supreme Commander like, about " + metal + " metal per player";
        }
        jLabel.setText(str);
    }

    private static DocumentListener onChange(final Runnable action) {
        return new DocumentListener() { // from class: com.faforever.neroxis.generator.bar.BarLauncher.15
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$Surface.class */
    static final class Surface extends JPanel {
        private final Color fill;
        private final int arc;

        Surface(LayoutManager layout, Color fill, int arc) {
            super(layout);
            this.fill = fill;
            this.arc = arc;
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            BarLauncher.paintRaised((Graphics2D) graphics, getWidth(), getHeight(), this.fill, this.arc);
        }
    }

    static void paintRaised(Graphics2D graphics, int width, int height, Color fill, int arc) {
        Graphics2D g = graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (width < 24 || height < 24) {
            g.setColor(fill);
            g.fillRoundRect(0, 0, width, height, arc, arc);
            g.dispose();
            return;
        }
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(0, 0, 0, 26 + ((3 - i) * 22)));
            g.fillRoundRect(0, i, width, height - 3, arc, arc);
        }
        int bodyHeight = height - 3;
        RoundRectangle2D.Float body = new RoundRectangle2D.Float(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, width, bodyHeight, arc, arc);
        int alpha = fill.getAlpha();
        Color top = new Color(Math.min(255, fill.getRed() + 14), Math.min(255, fill.getGreen() + 15), Math.min(255, fill.getBlue() + 19), alpha);
        Color bottom = new Color(Math.max(0, fill.getRed() - 9), Math.max(0, fill.getGreen() - 9), Math.max(0, fill.getBlue() - 8), alpha);
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, top, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, bodyHeight, bottom));
        g.fill(body);
        Shape clip = g.getClip();
        g.clip(body);
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(255, 255, 255, 16), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(46.0f, bodyHeight / 2.0f), new Color(255, 255, 255, 0)));
        g.fillRect(0, 0, width, Math.min(46, bodyHeight / 2));
        g.setClip(clip);
        g.setStroke(new BasicStroke(1.0f));
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(255, 255, 255, 46), SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, bodyHeight, new Color(0, 0, 0, 120)));
        g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, width - 1, bodyHeight - 1, arc, arc));
        g.dispose();
    }

    private JPanel card(String heading, JComponent... content) {
        return card(heading, true, content);
    }

    private JPanel card(String heading, boolean foldable, JComponent... content) {
        final JPanel card = new JPanel(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.16
            final /* synthetic */ BarLauncher this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(BarLauncher.CARD);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, 1));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));
        JLabel label = new JLabel(heading.toUpperCase(Locale.ROOT));
        label.setForeground(ACCENT);
        label.setFont(label.getFont().deriveFont(1, 12.0f));
        label.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height + 6));
        card.add(label);
        final JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, 1));
        body.setOpaque(false);
        body.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        for (JComponent component : content) {
            body.add(Box.createVerticalStrut(10));
            component.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            if ((component instanceof JCheckBox) || (component instanceof JLabel)) {
                component.setOpaque(false);
            }
            if (component instanceof JLabel) {
                component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getMaximumSize().height));
            }
            body.add(component);
        }
        card.add(body);
        if (foldable) {
            final String key = "section." + heading.toLowerCase(Locale.ROOT).replace(' ', '-');
            final Runnable show = () -> {
                boolean open = body.isVisible();
                label.setText((open ? "▾  " : "▸  ") + heading.toUpperCase(Locale.ROOT));
                card.setBorder(BorderFactory.createEmptyBorder(12, 14, open ? 14 : 12, 14));
            };
            body.setVisible(Boolean.parseBoolean(this.lobby.setting(key, "true")));
            show.run();
            label.setCursor(Cursor.getPredefinedCursor(12));
            label.setToolTipText("Click to fold or unfold");
            label.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.17
                final /* synthetic */ BarLauncher this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                public void mouseClicked(MouseEvent e) {
                    Container parent;
                    body.setVisible(!body.isVisible());
                    show.run();
                    this.this$0.lobby.remember(key, String.valueOf(body.isVisible()));
                    card.revalidate();
                    card.repaint();
                    Container parent2 = card.getParent();
                    while (true) {
                        parent = parent2;
                        if (parent == null || (parent instanceof JScrollPane)) {
                            break;
                        } else {
                            parent2 = parent.getParent();
                        }
                    }
                    if (parent != null) {
                        parent.revalidate();
                    }
                }
            });
        }
        card.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(card);
        wrapper.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        return wrapper;
    }

    private static JComponent[] row(String text, JComponent component) {
        JComponent jLabel = new JLabel(text);
        jLabel.setForeground(TEXT);
        if (component instanceof JSlider) {
            component.setOpaque(false);
        }
        return new JComponent[]{jLabel, component};
    }

    private static JPanel grid(JComponent[]... rows) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setOpaque(false);
        addRows(panel, rows);
        return panel;
    }

    private static void addRows(JPanel panel, JComponent[]... rows) {
        for (JComponent[] row : rows) {
            panel.add(row[0]);
            panel.add(row[1]);
        }
        panel.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rows.length * 36));
    }

    private void askForBarFolder() {
        String str;
        Object[] choices = {"Choose folder...", "Download BAR", "Later"};
        if (MacOs.isMac()) {
            str = "There is no official BAR for Macs. Install the Apple Silicon port of the engine (Download BAR opens its page), start it once so it downloads the game, then start BAR Map Studio again. If it is installed already, choose its data folder (Library/Application Support/Beyond-All-Reason-mac).";
        } else {
            str = "If it is installed, choose its folder (the one with Beyond-All-Reason.exe and a data folder). If not, get it for free from beyondallreason.info first.";
        }
        String hint = str;
        int answer = JOptionPane.showOptionDialog(this.frame, "<html><div style='width:360px'><b>BAR Map Studio cannot find Beyond All Reason.</b><br><br>" + hint + "</div></html>", "Beyond All Reason", -1, 3, (Icon) null, choices, choices[0]);
        if (answer == 0) {
            chooseBarFolder(this.frame);
        } else if (answer == 1) {
            try {
                Desktop.getDesktop().browse(URI.create(MacOs.isMac() ? "https://github.com/Vandomas/RecoilEngine-AppleSilicon/releases/latest" : "https://www.beyondallreason.info/download"));
            } catch (IOException | RuntimeException e) {
                JOptionPane.showMessageDialog(this.frame, "Get it at " + (MacOs.isMac() ? "https://github.com/Vandomas/RecoilEngine-AppleSilicon/releases/latest" : "https://www.beyondallreason.info/download"));
            }
        }
    }

    private void chooseBarFolder(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose the Beyond All Reason folder");
        chooser.setFileSelectionMode(1);
        chooser.setAcceptAllFileFilterUsed(false);
        Path current = Path.of(this.barFolder.getText().trim(), new String[0]);
        Path start = Files.isDirectory(current, new LinkOption[0]) ? current : BarFolder.usual().getParent().getParent();
        if (start != null && Files.isDirectory(start, new LinkOption[0])) {
            chooser.setCurrentDirectory(start.toFile());
        }
        if (chooser.showDialog(parent, "Use this folder") != 0 || chooser.getSelectedFile() == null) {
            return;
        }
        Path data = BarFolder.fromChoice(chooser.getSelectedFile().toPath());
        if (data == null) {
            JOptionPane.showMessageDialog(parent, "<html><div style='width:360px'>That folder does not look like Beyond All Reason.<br><br>Choose the folder the game is installed in: the one with Beyond-All-Reason.exe and a <b>data</b> folder.</div></html>", "Beyond All Reason", 2);
            return;
        }
        this.barFolder.setText(data.toString());
        this.lobby.remember("bar.data", data.toString());
        updateBarStatus();
        refreshLibrary(null);
    }

    private JComponent barFolderRow() {
        JTextField shown = new JTextField(this.barFolder.getText());
        shown.setEditable(false);
        shown.setToolTipText("Where BAR Map Studio finds the game, its maps and replays");
        this.barFolder.getDocument().addDocumentListener(onChange(() -> {
            shown.setText(this.barFolder.getText());
        }));
        JButton choose = new JButton("Choose...");
        choose.addActionListener(e -> {
            chooseBarFolder(this.frame);
        });
        JButton again = new JButton("Find");
        again.setToolTipText("Look for Beyond All Reason again in the usual places");
        again.addActionListener(e2 -> {
            Path found = BarFolder.find(null);
            if (found == null) {
                JOptionPane.showMessageDialog(this.frame, "Beyond All Reason was not found. Choose its folder by hand.", "Beyond All Reason", 1);
                return;
            }
            this.barFolder.setText(found.toString());
            this.lobby.remember("bar.data", found.toString());
            updateBarStatus();
            refreshLibrary(null);
        });
        JPanel buttons = new JPanel(new GridLayout(1, 2, 6, 0));
        buttons.setOpaque(false);
        buttons.add(choose);
        buttons.add(again);
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.add(shown, "Center");
        row.add(buttons, "East");
        return row;
    }

    private static Path defaultBarData() {
        return Platform.defaultData();
    }

    private static Path locateHome() {
        String override = System.getProperty("neroxis.home");
        return Path.of(override != null ? override : System.getProperty("user.dir"), new String[0]).toAbsolutePath();
    }

    private void runTestHook() {
        String screenshot = System.getProperty("neroxis.launcher.screenshot");
        if (screenshot == null) {
            return;
        }
        if ("confetti-frames".equals(System.getProperty("neroxis.launcher.view"))) {
            try {
                boolean[] zArr = {false, true};
                int length = zArr.length;
                for (int i = 0; i < length; i++) {
                    boolean prime = zArr[i];
                    List<BufferedImage> frames = Confetti.frames(1100, 620, prime, 30, prime ? 3.4d : 2.8d);
                    for (int i2 = 0; i2 < frames.size(); i2++) {
                        ImageIO.write(frames.get(i2), "png", Path.of(screenshot.replace(".png", (prime ? "-prime-" : "-signin-") + String.format("%03d", Integer.valueOf(i2)) + ".png"), new String[0]).toFile());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
        if ("trophies".equals(System.getProperty("neroxis.launcher.view"))) {
            BufferedImage sheet = new BufferedImage(150 * 10, (150 * 3) + 40, 1);
            Graphics2D g = sheet.createGraphics();
            g.setColor(new Color(18, 20, 25));
            g.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (int trophy = 1; trophy <= 10; trophy++) {
                for (int grade = 0; grade < 3; grade++) {
                    g.drawImage(Trophy.image(trophy, grade, 150 - 20, false), ((trophy - 1) * 150) + 10, (grade * 150) + 6, (ImageObserver) null);
                }
                g.setColor(new Color(200, 204, 212));
                g.drawString(Trophy.NAMES[trophy - 1], ((trophy - 1) * 150) + 12, (150 * 3) + 24);
            }
            g.dispose();
            try {
                ImageIO.write(sheet, "png", Path.of(screenshot, new String[0]).toFile());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            System.exit(0);
        }
        if ("confetti".equals(System.getProperty("neroxis.launcher.view"))) {
            try {
                boolean[] zArr2 = {false, true};
                int length2 = zArr2.length;
                for (int i3 = 0; i3 < length2; i3++) {
                    boolean prime2 = zArr2[i3];
                    for (double at : new double[]{0.45d, 1.3d}) {
                        ImageIO.write(Confetti.preview(1280, 720, prime2, at), "png", Path.of(screenshot.replace(".png", (prime2 ? "-prime-" : "-signin-") + at + ".png"), new String[0]).toFile());
                    }
                }
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            System.exit(0);
        }
        String view = System.getProperty("neroxis.launcher.view", "play");
        if (view.equals("official")) {
            this.officialGames = true;
            loadOfficialBattles();
        } else if (view.equals("replays")) {
            this.navigation.get(1).doClick();
        } else if (view.equals("maps")) {
            this.navigation.get(2).doClick();
            if (this.libraryModel.size() > 0) {
                this.library.setSelectedIndex(0);
            }
        } else if (view.equals("register")) {
            this.lobby.showRegister();
        } else if (view.equals("signin")) {
            this.lobby.showLogin();
        } else if (view.equals("rank") || view.equals("rank-stats") || view.equals("rank-board")) {
            try {
                Object stats = MiniJson.parse(Files.readString(Path.of(System.getProperty("neroxis.launcher.teststats"), new String[0])));
                if (stats instanceof Map) {
                    Map<?, ?> map = (Map) stats;
                    this.lobby.previewProfile(map, view.equals("rank") ? 0 : view.equals("rank-stats") ? 1 : 2);
                }
            } catch (IOException | RuntimeException e4) {
                e4.printStackTrace();
            }
        } else if (view.equals("emailcode")) {
            this.lobby.showEmailCode(null);
        } else if (view.equals("profile")) {
            Timer profile = new Timer(5000, e5 -> {
                this.lobby.showProfile();
            });
            profile.setRepeats(false);
            profile.start();
        } else if (view.equals("options")) {
            this.lobby.showAdvancedOptions();
        } else if (view.equals("installed")) {
            askInstalled();
        } else if (view.equals("trial")) {
            this.lobby.showTrialWelcome("test", "19 December 2026", "$10");
        } else if (view.equals("keys")) {
            this.lobby.showKeybinds(false);
        } else if (view.startsWith("create")) {
            openCreateGame((!view.equals("create-map") || this.allMaps.isEmpty()) ? GENERATOR_ENTRY : (MapEntry) this.allMaps.getLast());
        }
        String testLogin = System.getProperty("neroxis.launcher.testlogin");
        if (testLogin != null) {
            this.lobby.connect(testLogin, "password".toCharArray(), null);
            if (view.equals("room")) {
                boolean busy = System.getProperty("neroxis.launcher.joinbusy") != null;
                Timer join = new Timer(3000, e6 -> {
                    Optional<LobbyClient.Battle> optionalReduce = this.lobby.client().getBattles().stream().filter(battle -> {
                        if (busy) {
                            return battle.userCount() > 0;
                        }
                        return battle.userCount() == 0;
                    }).reduce((first, second) -> {
                        return (!busy || first.userCount() < second.userCount()) ? second : first;
                    });
                    LobbyUi lobbyUi = this.lobby;
                    Objects.requireNonNull(lobbyUi);
                    optionalReduce.ifPresent(lobbyUi::join);
                });
                join.setRepeats(false);
                join.start();
                Integer testSide = Integer.getInteger("neroxis.launcher.testside");
                if (testSide != null) {
                    Timer side = new Timer(Integer.getInteger("neroxis.launcher.testsidedelay", 6500).intValue(), e7 -> {
                        this.lobby.clickOwnFaction(testSide.intValue());
                    });
                    side.setRepeats(false);
                    side.start();
                }
                int testAis = Integer.getInteger("neroxis.launcher.addai", 0).intValue();
                if (testAis > 0) {
                    Timer ais = new Timer(5500, e8 -> {
                        for (int i4 = 0; i4 < testAis; i4++) {
                            this.lobby.client().addBot(1);
                        }
                    });
                    ais.setRepeats(false);
                    ais.start();
                }
                if (System.getProperty("neroxis.launcher.testdrag") != null) {
                    Timer drag = new Timer(11000, e9 -> {
                        if (this.lobby.client().getStartBoxes().isEmpty()) {
                            return;
                        }
                        PreviewPanel preview = this.lobby.roomPreview();
                        int side2 = Math.min(preview.getWidth(), preview.getHeight());
                        float[] edges = this.lobby.client().getStartBoxes().values().iterator().next();
                        int gripX = (((preview.getWidth() - side2) / 2) + Math.round(edges[2] * side2)) - 5;
                        int gripY = (((preview.getHeight() - side2) / 2) + Math.round(edges[3] * side2)) - 5;
                        int[][] steps = {new int[]{501, gripX, gripY}, new int[]{506, gripX + (side2 / 5), gripY - (side2 / 2)}, new int[]{502, gripX + (side2 / 5), gripY - (side2 / 2)}};
                        for (int[] step : steps) {
                            preview.dispatchEvent(new MouseEvent(preview, step[0], System.currentTimeMillis(), 0, step[1], step[2], 1, false, 1));
                        }
                    });
                    drag.setRepeats(false);
                    drag.start();
                }
                String say = System.getProperty("neroxis.launcher.say");
                if (say != null) {
                    String[] lines = say.split("\\|");
                    int[] next = {0};
                    Timer talk = new Timer(7000, e10 -> {
                        if (System.getProperty("neroxis.launcher.ready") != null && !this.lobby.client().isReady()) {
                            this.lobby.client().setReady(true);
                        }
                        LobbyClient lobbyClientClient = this.lobby.client();
                        int i4 = next[0];
                        next[0] = i4 + 1;
                        lobbyClientClient.sayBattle(lines[i4 % lines.length]);
                    });
                    talk.start();
                }
            }
        }
        Integer dockTab = Integer.getInteger("neroxis.launcher.dock");
        if (dockTab != null) {
            Timer dock = new Timer(Integer.getInteger("neroxis.launcher.delay", 9000).intValue() - 1500, e11 -> {
                this.lobby.showDockTab(dockTab.intValue());
            });
            dock.setRepeats(false);
            dock.start();
        }
        Timer timer = new Timer(Integer.getInteger("neroxis.launcher.delay", testLogin != null ? 9000 : 4000).intValue(), (ActionListener) null);
        timer.addActionListener(e12 -> {
            timer.stop();
            Container content = this.frame.getContentPane();
            for (JDialog jDialog : Window.getWindows()) {
                if (jDialog instanceof JDialog) {
                    JDialog dialog = jDialog;
                    if (dialog.isVisible()) {
                        content = dialog.getContentPane();
                    }
                }
            }
            BufferedImage image = new BufferedImage(content.getWidth(), content.getHeight(), 1);
            content.paint(image.getGraphics());
            try {
                ImageIO.write(image, "png", Path.of(screenshot, new String[0]).toFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });
        timer.start();
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$LogStream.class */
    private final class LogStream extends OutputStream {
        private final StringBuilder line;
        final /* synthetic */ BarLauncher this$0;

        private LogStream(final BarLauncher this$0) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.line = new StringBuilder();
        }

        @Override // java.io.OutputStream
        public synchronized void write(int b) {
            if (b != 10) {
                if (b != 13) {
                    this.line.append((char) b);
                }
            } else {
                String text = this.line.toString();
                this.line.setLength(0);
                if (!text.startsWith("Starting pipeline") && !text.startsWith("Pipeline completed")) {
                    SwingUtilities.invokeLater(() -> {
                        this.this$0.log.append(text + "\n");
                        this.this$0.log.setCaretPosition(this.this$0.log.getDocument().getLength());
                    });
                }
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$ScaledIcon.class */
    private static final class ScaledIcon extends ImageIcon {
        ScaledIcon(BufferedImage image) {
            super(image);
        }

        public int getIconWidth() {
            return 52;
        }

        public int getIconHeight() {
            return 52;
        }

        public synchronized void paintIcon(Component component, Graphics graphics, int x, int y) {
            ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(getImage(), x, y, 52, 52, (ImageObserver) null);
        }
    }

    private static ImageIcon generatorIcon() {
        BufferedImage image = new BufferedImage(52, 52, 1);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, new Color(240, 150, 46), 52.0f, 52.0f, new Color(150, 70, 20)));
        g.fillRect(0, 0, 52, 52);
        g.setColor(new Color(25, 18, 8));
        g.setStroke(new BasicStroke(3.0f, 1, 1));
        g.drawPolyline(new int[]{8, 20, 28, 36, 44}, new int[]{38, 20, 30, 14, 38}, 5);
        g.dispose();
        return new ImageIcon(image);
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$MapCellRenderer.class */
    private final class MapCellRenderer extends DefaultListCellRenderer {
        final /* synthetic */ BarLauncher this$0;

        private MapCellRenderer(final BarLauncher this$0) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
            String str;
            JLabel label = super.getListCellRendererComponent(list, value, index, selected, focused);
            MapEntry entry = (MapEntry) value;
            Matcher numbers = Pattern.compile("(\\d+) players, (\\d+) metal spots").matcher(entry.description());
            if (entry == BarLauncher.GENERATOR_ENTRY) {
                str = "new random map, settings on the right";
            } else if (numbers.find()) {
                str = "generated · " + numbers.group(1) + " players · " + numbers.group(2) + " metal spots";
            } else {
                str = entry.generated() ? "generated" : "BAR map";
            }
            String details = str;
            label.setText("<html><b" + (entry == BarLauncher.GENERATOR_ENTRY ? " style='color:#F0962E'>★ " : ">") + entry.label() + "</b><br><span style='color:#8c94a3'>" + details + "</span></html>");
            label.setIcon(entry == BarLauncher.GENERATOR_ENTRY ? BarLauncher.GENERATOR_ICON : (Icon) this.this$0.thumbnails.getOrDefault(entry.archive(), BarLauncher.EMPTY_ICON));
            label.setIconTextGap(12);
            label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            if (!selected) {
                label.setBackground(index % 2 == 0 ? BarLauncher.CARD : new Color(34, 37, 46));
            }
            return label;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$ScrollablePanel.class */
    private static final class ScrollablePanel extends JPanel implements Scrollable {
        private ScrollablePanel() {
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 18;
        }

        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return 140;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarLauncher$PreviewPanel.class */
    static final class PreviewPanel extends JPanel {
        private static volatile BufferedImage commanderIcon;
        private String placeholder;
        private BufferedImage image;
        private Map<Integer, float[][]> polygons;
        private Consumer<float[]> boxDrawn;
        private Point dragStart;
        private Point dragEnd;
        private static final int GRIP = 11;
        private BiConsumer<Integer, float[]> boxEdited;
        private int editTeam;
        private boolean editResize;
        private float[] editEdges;
        private static volatile BufferedImage commanderHalo;
        private static volatile BufferedImage metalIcon;
        private static volatile BufferedImage geoIcon;
        private List<float[]> starts = List.of();
        private Map<Integer, float[]> boxes = Map.of();
        private BarInstall.Marks marks = BarInstall.Marks.NONE;

        PreviewPanel(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setPreferredSize(new Dimension(560, 560));
        }

        void setBoxDrawing(Consumer<float[]> listener) {
            this.boxDrawn = listener;
            setCursor(Cursor.getPredefinedCursor(listener != null ? 1 : 0));
            installMouse();
        }

        void setBoxEditing(BiConsumer<Integer, float[]> listener) {
            this.boxEdited = listener;
            installMouse();
            repaint();
        }

        private float[] mapPoint(Point point) {
            int side = Math.min(getWidth(), getHeight());
            return new float[]{(point.x - ((getWidth() - side) / 2.0f)) / side, (point.y - ((getHeight() - side) / 2.0f)) / side};
        }

        private int[] boxAt(Point point) {
            int side = Math.max(1, Math.min(getWidth(), getHeight()));
            float[] at = mapPoint(point);
            float grip = 14.0f / side;
            int[] found = null;
            for (Map.Entry<Integer, float[]> box : this.boxes.entrySet()) {
                float[] edges = box.getValue();
                if (at[0] >= edges[0] && at[0] <= edges[2] && at[1] >= edges[1] && at[1] <= edges[3]) {
                    boolean onGrip = at[0] >= edges[2] - grip && at[1] >= edges[3] - grip;
                    if (found == null || onGrip) {
                        int[] iArr = new int[2];
                        iArr[0] = box.getKey().intValue();
                        iArr[1] = onGrip ? 1 : 0;
                        found = iArr;
                    }
                }
            }
            return found;
        }

        private void installMouse() {
            if (getMouseListeners().length > 0) {
                return;
            }
            MouseAdapter mouse = new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.BarLauncher.PreviewPanel.1
                private float[] grabbed;
                private float[] grabPoint;
                final /* synthetic */ PreviewPanel this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                public void mousePressed(MouseEvent event) {
                    this.this$0.dragStart = this.this$0.boxDrawn != null ? event.getPoint() : null;
                    this.this$0.dragEnd = this.this$0.dragStart;
                    int[] hit = (this.this$0.boxDrawn != null || this.this$0.boxEdited == null) ? null : this.this$0.boxAt(event.getPoint());
                    if (hit != null) {
                        this.this$0.editTeam = hit[0];
                        this.this$0.editResize = hit[1] == 1;
                        this.grabbed = (float[]) this.this$0.boxes.get(Integer.valueOf(this.this$0.editTeam)).clone();
                        this.grabPoint = this.this$0.mapPoint(event.getPoint());
                        this.this$0.editEdges = (float[]) this.grabbed.clone();
                    }
                }

                public void mouseMoved(MouseEvent event) {
                    int i;
                    if (this.this$0.boxDrawn != null || this.this$0.boxEdited == null) {
                        return;
                    }
                    int[] hit = this.this$0.boxAt(event.getPoint());
                    PreviewPanel previewPanel = this.this$0;
                    if (hit == null) {
                        i = 0;
                    } else {
                        i = hit[1] == 1 ? 5 : 13;
                    }
                    previewPanel.setCursor(Cursor.getPredefinedCursor(i));
                }

                public void mouseDragged(MouseEvent event) {
                    if (this.this$0.dragStart != null) {
                        this.this$0.dragEnd = event.getPoint();
                        this.this$0.repaint();
                        return;
                    }
                    if (this.this$0.editEdges != null) {
                        float[] at = this.this$0.mapPoint(event.getPoint());
                        float dx = at[0] - this.grabPoint[0];
                        float dy = at[1] - this.grabPoint[1];
                        if (this.this$0.editResize) {
                            this.this$0.editEdges[2] = Math.max(this.grabbed[0] + 0.05f, Math.min(1.0f, this.grabbed[2] + dx));
                            this.this$0.editEdges[3] = Math.max(this.grabbed[1] + 0.05f, Math.min(1.0f, this.grabbed[3] + dy));
                        } else {
                            float dx2 = Math.max(-this.grabbed[0], Math.min(1.0f - this.grabbed[2], dx));
                            float dy2 = Math.max(-this.grabbed[1], Math.min(1.0f - this.grabbed[3], dy));
                            this.this$0.editEdges = new float[]{this.grabbed[0] + dx2, this.grabbed[1] + dy2, this.grabbed[2] + dx2, this.grabbed[3] + dy2};
                        }
                        this.this$0.repaint();
                    }
                }

                public void mouseReleased(MouseEvent event) {
                    if (this.this$0.dragStart != null && this.this$0.boxDrawn != null) {
                        float[] from = this.this$0.mapPoint(this.this$0.dragStart);
                        float[] to = this.this$0.mapPoint(event.getPoint());
                        float left = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, Math.min(from[0], to[0])));
                        float right = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, Math.max(from[0], to[0])));
                        float top = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, Math.min(from[1], to[1])));
                        float bottom = Math.max(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT, Math.min(1.0f, Math.max(from[1], to[1])));
                        if (right - left > 0.04f && bottom - top > 0.04f) {
                            this.this$0.boxDrawn.accept(new float[]{left, top, right, bottom});
                        }
                    }
                    if (this.this$0.editEdges != null && this.this$0.boxEdited != null && !Arrays.equals(this.this$0.editEdges, this.grabbed)) {
                        Map<Integer, float[]> changed = new TreeMap<>(this.this$0.boxes);
                        changed.put(Integer.valueOf(this.this$0.editTeam), this.this$0.editEdges);
                        this.this$0.boxes = changed;
                        this.this$0.boxEdited.accept(Integer.valueOf(this.this$0.editTeam), (float[]) this.this$0.editEdges.clone());
                    }
                    this.this$0.editEdges = null;
                    this.this$0.dragStart = null;
                    this.this$0.repaint();
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        static void setResourceIcons(BufferedImage metal, BufferedImage geo) {
            metalIcon = metal != null ? tint(metal, 205, 212, 222) : null;
            geoIcon = geo != null ? tint(geo, 240, 176, 84) : null;
        }

        private static BufferedImage tint(BufferedImage icon, int red, int green, int blue) {
            BufferedImage tinted = new BufferedImage(icon.getWidth(), icon.getHeight(), 2);
            for (int y = 0; y < icon.getHeight(); y++) {
                for (int x = 0; x < icon.getWidth(); x++) {
                    int argb = icon.getRGB(x, y);
                    int r = (((argb >> 16) & 255) * red) / 255;
                    int g = (((argb >> 8) & 255) * green) / 255;
                    int b = ((argb & 255) * blue) / 255;
                    tinted.setRGB(x, y, (argb & (-16777216)) | (r << 16) | (g << 8) | b);
                }
            }
            return tinted;
        }

        static void setCommanderIcon(BufferedImage icon) {
            if (icon == null) {
                commanderIcon = null;
                return;
            }
            BufferedImage grey = new BufferedImage(icon.getWidth(), icon.getHeight(), 2);
            for (int y = 0; y < icon.getHeight(); y++) {
                for (int x = 0; x < icon.getWidth(); x++) {
                    int argb = icon.getRGB(x, y);
                    int r = (((argb >> 16) & 255) * 70) / 255;
                    int g = (((argb >> 8) & 255) * 74) / 255;
                    int b = ((argb & 255) * 82) / 255;
                    grey.setRGB(x, y, (argb & (-16777216)) | (r << 16) | (g << 8) | b);
                }
            }
            commanderIcon = grey;
            commanderHalo = tint(icon, 235, 238, 242);
        }

        void setPlaceholder(String text) {
            if (!text.equals(this.placeholder)) {
                this.placeholder = text;
                repaint();
            }
        }

        void setBoxes(Map<Integer, float[]> boxes) {
            this.boxes = boxes;
            repaint();
        }

        void setPolygons(Map<Integer, float[][]> polygons) {
            this.polygons = (polygons == null || polygons.isEmpty()) ? null : polygons;
            repaint();
        }

        void setMarks(BarInstall.Marks marks) {
            this.marks = marks != null ? marks : BarInstall.Marks.NONE;
            repaint();
        }

        void setMap(BufferedImage image, List<float[]> starts) {
            if (image == null) {
                this.marks = BarInstall.Marks.NONE;
            }
            this.image = image;
            this.starts = starts;
            repaint();
        }

        protected void paintComponent(Graphics graphics) {
            Path2D.Float rectangle;
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int side = Math.min(getWidth(), getHeight());
            int x = (getWidth() - side) / 2;
            int y = (getHeight() - side) / 2;
            RoundRectangle2D.Float r0 = new RoundRectangle2D.Float(x, y, side, side, 22.0f, 22.0f);
            g.setColor(new Color(13, 15, 19));
            g.fill(r0);
            if (this.image == null) {
                g.setColor(BarLauncher.MUTED);
                g.setFont(g.getFont().deriveFont(15.0f));
                String text = this.placeholder;
                g.drawString(text, x + ((side - g.getFontMetrics().stringWidth(text)) / 2), y + (side / 2));
            } else {
                g.setClip(r0);
                g.drawImage(this.image, x, y, side, side, (ImageObserver) null);
                float dot = Math.max(4.0f, side / 110.0f);
                BufferedImage metalMark = metalIcon;
                BufferedImage geoMark = geoIcon;
                Math.max(11, Math.min(20, side / 30));
                int geoSize = Math.max(13, Math.min(22, side / 30));
                for (float[] spot : this.marks.metal()) {
                    float px = x + (spot[0] * side);
                    float py = y + (spot[1] * side);
                    float ring = Math.max(4.5f, Math.min(8.0f, side / 85.0f));
                    Arc2D.Float arc = new Arc2D.Float();
                    int pass = 0;
                    while (pass < 2) {
                        g.setStroke(new BasicStroke(pass == 0 ? 2.6f : 1.3f, 0, 0));
                        g.setColor(pass == 0 ? new Color(0, 0, 0, 200) : new Color(120, 200, 235));
                        for (int segment = 0; segment < 3; segment++) {
                            arc.setArc(px - (ring / 2.0f), py - (ring / 2.0f), ring, ring, 100 + (segment * 120), 80.0d, 0);
                            g.draw(arc);
                        }
                        pass++;
                    }
                    if (metalMark == null) {
                        g.setColor(new Color(0, 0, 0, 170));
                        g.fill(new Ellipse2D.Float((px - (dot / 2.0f)) - 1.0f, (py - (dot / 2.0f)) - 1.0f, dot + 2.0f, dot + 2.0f));
                        g.setColor(new Color(225, 232, 240));
                        g.fill(new Ellipse2D.Float(px - (dot / 2.0f), py - (dot / 2.0f), dot, dot));
                    }
                }
                for (float[] geo : this.marks.geos()) {
                    float px2 = x + (geo[0] * side);
                    float py2 = y + (geo[1] * side);
                    if (geoMark != null) {
                        g.drawImage(geoMark, Math.round(px2 - (geoSize / 2.0f)), Math.round(py2 - (geoSize / 2.0f)), geoSize, geoSize, (ImageObserver) null);
                    } else {
                        float r = dot * 0.95f;
                        Path2D.Float r1 = new Path2D.Float();
                        r1.moveTo(px2, (py2 - r) - 1.0f);
                        r1.lineTo(px2 + r + 1.0f, py2);
                        r1.lineTo(px2, py2 + r + 1.0f);
                        r1.lineTo((px2 - r) - 1.0f, py2);
                        r1.closePath();
                        g.setColor(new Color(0, 0, 0, 190));
                        g.setStroke(new BasicStroke(2.5f));
                        g.draw(r1);
                        g.setColor(new Color(255, 170, 60));
                        g.fill(r1);
                    }
                }
                for (Map.Entry<Integer, float[]> box : this.boxes.entrySet()) {
                    float[] edges = (this.editEdges == null || box.getKey().intValue() != this.editTeam) ? box.getValue() : this.editEdges;
                    int left = x + Math.round(edges[0] * side);
                    int top = y + Math.round(edges[1] * side);
                    int width = Math.round((edges[2] - edges[0]) * side);
                    int height = Math.round((edges[3] - edges[1]) * side);
                    float[][] polygon = (this.polygons == null || this.editEdges != null) ? null : this.polygons.get(box.getKey());
                    if (polygon != null && polygon.length >= 3) {
                        Path2D.Float r2 = new Path2D.Float();
                        for (int i = 0; i < polygon.length; i++) {
                            float px3 = x + (polygon[i][0] * side);
                            float py3 = y + (polygon[i][1] * side);
                            if (i == 0) {
                                r2.moveTo(px3, py3);
                            } else {
                                r2.lineTo(px3, py3);
                            }
                        }
                        r2.closePath();
                        rectangle = r2;
                    } else {
                        rectangle = new Rectangle(left, top, width - 1, height - 1);
                    }
                    g.setColor(new Color(255, 255, 255, 34));
                    g.fill(rectangle);
                    g.setColor(new Color(255, 255, 255, 225));
                    g.setStroke(new BasicStroke(1.4f));
                    g.draw(rectangle);
                    String number = String.valueOf(box.getKey().intValue() + 1);
                    g.setFont(getFont().deriveFont(1, Math.max(12.0f, side / 26.0f)));
                    if (g.getFontMetrics().stringWidth("Team " + number) + 10 <= width) {
                        number = "Team " + number;
                    }
                    int textWidth = g.getFontMetrics().stringWidth(number);
                    int textX = left + ((width - textWidth) / 2);
                    int textY = top + g.getFontMetrics().getAscent() + 3;
                    if (polygon != null && polygon.length >= 3) {
                        float sumX = 0.0f;
                        float sumY = 0.0f;
                        for (float[] point : polygon) {
                            sumX += point[0];
                            sumY += point[1];
                        }
                        textX = Math.round(x + ((sumX / polygon.length) * side)) - (textWidth / 2);
                        textY = Math.round(y + ((sumY / polygon.length) * side)) + (g.getFontMetrics().getAscent() / 2);
                    }
                    g.setColor(new Color(0, 0, 0, 170));
                    g.drawString(number, textX + 1, textY + 1);
                    g.setColor(Color.WHITE);
                    g.drawString(number, textX, textY);
                    if (this.boxEdited != null && polygon == null && width > 22 && height > 22) {
                        int right = (left + width) - 3;
                        int bottom = (top + height) - 3;
                        g.setStroke(new BasicStroke(1.3f));
                        for (int line = 1; line <= 3; line++) {
                            int reach = (line * 11) / 3;
                            g.setColor(new Color(0, 0, 0, 150));
                            g.drawLine((right - reach) + 1, bottom + 1, right + 1, (bottom - reach) + 1);
                            g.setColor(new Color(255, 255, 255, 235));
                            g.drawLine(right - reach, bottom, right, bottom - reach);
                        }
                    }
                }
                if (this.dragStart != null && this.dragEnd != null) {
                    g.setColor(new Color(255, 255, 255, 60));
                    int left2 = Math.min(this.dragStart.x, this.dragEnd.x);
                    int top2 = Math.min(this.dragStart.y, this.dragEnd.y);
                    g.fillRect(left2, top2, Math.abs(this.dragEnd.x - this.dragStart.x), Math.abs(this.dragEnd.y - this.dragStart.y));
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(1.5f));
                    g.drawRect(left2, top2, Math.abs(this.dragEnd.x - this.dragStart.x), Math.abs(this.dragEnd.y - this.dragStart.y));
                }
                g.setClip((Shape) null);
                for (float[] start : this.starts) {
                    int px4 = x + Math.round(start[0] * side);
                    int py4 = y + Math.round(start[1] * side);
                    BufferedImage commander = commanderIcon;
                    if (commander != null) {
                        int iconSize = Math.max(22, Math.min(40, side / 13));
                        BufferedImage halo = commanderHalo;
                        if (halo != null) {
                            int haloSize = iconSize + 4;
                            g.drawImage(halo, px4 - (haloSize / 2), py4 - (haloSize / 2), haloSize, haloSize, (ImageObserver) null);
                        }
                        g.drawImage(commander, px4 - (iconSize / 2), py4 - (iconSize / 2), iconSize, iconSize, (ImageObserver) null);
                    } else {
                        g.setColor(new Color(0, 0, 0, 120));
                        g.fillOval(px4 - 11, py4 - 11, 22, 22);
                        g.setColor(BarLauncher.TEAM_COLORS[((int) start[2]) % BarLauncher.TEAM_COLORS.length]);
                        g.fillOval(px4 - 8, py4 - 8, 16, 16);
                        g.setColor(Color.WHITE);
                        g.setStroke(new BasicStroke(2.0f));
                        g.drawOval(px4 - 8, py4 - 8, 16, 16);
                    }
                }
            }
            g.setColor(BarLauncher.CARD_BORDER);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(r0);
            g.dispose();
        }
    }
}
