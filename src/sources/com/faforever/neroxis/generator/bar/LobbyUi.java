package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.exporter.PreviewGenerator;
import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.MenuSelectionManager;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi.class */
final class LobbyUi implements LobbyClient.Listener {
    static final String DEFAULT_SERVER = "51.81.81.166";
    static final String DEFAULT_MAP_SERVICE = "http://51.81.81.166:8090/find";
    private KeybindEditor keybindEditor;
    private Font barFont;
    private boolean barFontLooked;
    private JLabel ownRow;
    private final Host host;
    static final int TAB_ONLINE = 0;
    static final int TAB_CHAT = 1;
    static final int TAB_PARTY = 2;
    static final int TAB_SETTINGS = 3;
    private long settingsOpenedAt;
    private boolean fittingDockTabs;
    private volatile Set<String> knownPlayers;
    private final Timer knownTimer;
    private volatile Set<String> publicOnline;
    private final Map<String, String> knownCountries;
    private volatile Set<String> publicInGame;
    private final Timer publicOnlineTimer;
    private final JTextField friendInput;
    private final BarLauncher.BackgroundPanel room;
    private String artworkShown;
    private int roomsJoined;
    private int artworkBattle;
    private final int artworkPick;
    private final JLabel roomTitle;
    private final JTextField roomTitleEditor;
    private final JPanel roomTitleCards;
    private boolean renaming;
    private String renamingFrom;
    private final JLabel roomMap;
    private final JLabel mapStatus;
    private final BarLauncher.PreviewPanel roomPreview;
    private final JTextPane roomChat;
    private final JTextField roomInput;
    private final JPanel teamsPanel;
    private final JToggleButton readyButton;
    private final JToggleButton spectateButton;
    private final JLabel gameStatus;
    private final JLabel engineStatus;
    private String shownEngine;
    private String downloadingEngine;
    private boolean engineReady;
    private final JLabel boxStatus;
    private final JPanel spectatorPanel;
    private final JLabel spectatorTitle;
    private JToggleButton playToggle;
    private final JLabel optionsLabel;
    private final Set<String> bosses;
    private String boxShape;
    private String sentBoxShape;
    private String lastBoxSignature;
    private final Timer boxShapeTimer;
    private List<ModOptions.Option> gameOptions;
    private final JPanel partyPanel;
    private final JButton createPartyButton;
    private final JPanel partiesHead;
    private final JTextField partySearch;
    private final JPanel partyCandidates;
    private final JLabel partyInviteTitle;
    private final JTextArea partyHint;
    private volatile Account account;
    private volatile Map<String, String> badges;
    private final Timer badgeTimer;
    private final Timer statusTimer;
    private boolean engineWasRunning;
    private JDialog profileDialog;
    private final JLabel profileName;
    private final JLabel profilePrime;
    private final PrimeBadge primeBadge;
    private final JLabel primeExplanation;
    private final JLabel primeCaption;
    private final JLabel profileCreator;
    private final JLabel profileStatus;
    private final JButton paypalButton;
    private final JPanel rankPanel;
    static final int ACCOUNT_TAB = 3;
    private final JPanel statsPanel;
    private volatile Map<String, Object> playerStats;
    private String previewName;
    private JTabbedPane profileTabs;
    private final CardLayout profileCards;
    private JPanel profileContent;
    private final JLabel leaderboard;
    private String leaderboardBoard;
    private final JButton primeButton;
    private long generatingSince;
    private final JProgressBar generatingBar;
    private final Timer generatingTick;
    private String generatingFrom;
    private final Properties playerNotes;
    private boolean playerNotesLoaded;
    private final JPanel voteBar;
    private final JLabel voteText;
    private final Timer voteTimeout;
    private final JComboBox<Integer> teamSize;
    private final JComboBox<Integer> teamCount;
    private final JLabel formatLabel;
    private String shownMap;
    private String downloadingMap;
    private final Map<String, String> verifiedMaps;
    private String verifyingMap;
    private String shownGame;
    private String downloadingGame;
    private boolean mapReady;
    private boolean gameReady;
    private JDialog loginDialog;
    private JLabel loginStatus;
    private JDialog registerDialog;
    private JLabel registerStatus;
    private boolean registering;
    private boolean staySignedIn;
    private String pendingUser;
    private String pendingHash;
    private int pendingRoom;
    private List<String> pendingLines;
    private boolean signingIn;
    private final Timer reconnectTimer;
    private int reconnectDelay;
    private int quickCloses;
    private long badgeMenuClosedAt;
    static final String RESET_PAGE = "https://eclipseforeverbeyond.com/reset-password.html";
    private boolean started;
    private int bugsUnread;
    private final JComboBox<String> displayChoice;
    private boolean displayUpdating;
    private int shownInvitations;
    private String greetedUser;
    private final Set<String> flagsLoading;
    private final Map<Path, Image> flagPictures;
    private Map<String, Color> gameColours;
    private boolean historyLoaded;
    private static final String CHAT_USER = "eclipse.user";
    private static final Color ONLINE = BarLauncher.GREEN;
    private static final Color BUSY = new Color(255, 170, 60);
    private static final Color ERROR = new Color(255, 96, 84);
    private static final String[][] ROOM_ARTWORK = {new String[]{"bitmaps/loadpictures/armada.jpg"}, new String[]{"bitmaps/loadpictures/cortex.jpg"}, new String[]{"bitmaps/loadpictures/manual/scavengers/bar4k_loadingscreen_5.jpg", "bitmaps/loadpictures/manual/scavengers/bar4k_loadingscreen_8.jpg"}};
    private static final String[][] GENERATING_STEPS = {new String[]{"0", "Asking the room for a new map"}, new String[]{"6", "Starting the generator"}, new String[]{"16", "Raising the terrain"}, new String[]{"32", "Flooding the oceans"}, new String[]{"44", "Carving ramps and plateaus"}, new String[]{"56", "Placing metal and geo vents"}, new String[]{"68", "Painting the ground"}, new String[]{"80", "Planting trees and rocks"}, new String[]{"90", "Packing the map"}, new String[]{"97", "Almost there"}};
    private static final List<String> HIDDEN_SECTIONS = List.of("", "options", "modes", "dev", "mapmetadata");
    static final Color CHAT_BACKGROUND = new Color(13, 14, 17);
    static final Color CHAT_LINE = new Color(52, 56, 66);
    static final Color CHAT_HOST = new Color(226, 84, 84);
    private static final Color OWN_NAME = new Color(204, 77, 230);
    private static final Color PARTY_NAME = new Color(108, 182, 255);
    private final LobbyClient client = new LobbyClient(this);
    private final Properties settings = new Properties();
    private final JButton badge = new JButton("Sign in");
    private int tabBeforeSettings = 0;
    private final JTabbedPane dockTabs = new JTabbedPane(this, 1, 1) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.1
        final /* synthetic */ LobbyUi this$0;

        {
            Objects.requireNonNull(this);
            this.this$0 = this;
        }

        public void setTitleAt(int index, String title) {
            super.setTitleAt(index, title);
            this.this$0.fitDockTabs();
        }
    };
    private final JTextPane chat = chatPane();
    private final JTextField chatInput = new JTextField();
    private final JTabbedPane channelTabs = new JTabbedPane();
    private final Map<String, JTextPane> chatPanes = new LinkedHashMap();
    private final RowsModel friendModel = new RowsModel();
    private final JList<String> friendList = new JList<>(this.friendModel);
    private final RowsModel playerModel = new RowsModel();
    private final JList<String> playerList = new JList<>(this.playerModel);
    private final RowsModel onlineModel = new RowsModel();
    private final Timer onlineRebuild = new Timer(150, e -> {
        rebuildOnline();
    });
    private final Timer usersRebuild = new Timer(200, e -> {
        rebuildUsers();
    });

    void showTrialWelcome(String user, String until, String price) {
        BarLauncher.greetingCard(this.host.frame(), "YOUR FIRST 3 MONTHS ARE FREE", "Welcome, " + user + "!", "<span style='color:#e2e6ed'>You are all set. No card, nothing to fill in, just play.</span><br><br><table cellpadding='0' cellspacing='0'>" + "<tr><td style='color:#3ab054' valign='top'>&#10004;&nbsp;&nbsp;</td><td style='padding-bottom:6px'>" + "Generated maps, rooms and everything else of Eclipse, free until " + "<b style='color:#e2e6ed'>" + until + "</b></td></tr>" + "<tr><td style='color:#3ab054' valign='top'>&#10004;&nbsp;&nbsp;</td><td style='padding-bottom:6px'>" + "After that " + "<b style='color:#e2e6ed'>" + "Eclipse Prime</b> keeps you playing for " + "<b style='color:#e2e6ed'>" + price + " a month</b>, only if you want to stay</td></tr>" + "<tr><td style='color:#3ab054' valign='top'>&#10004;&nbsp;&nbsp;</td><td style='padding-bottom:6px'>" + "Prime and codes live under your name at the top right: " + "<b style='color:#e2e6ed'>" + "View profile</b></td></tr></table>", "Start playing", null);
    }

    private Font barFont(Font fallback, float size) {
        if (!this.barFontLooked) {
            this.barFontLooked = true;
            byte[] bytes = BarInstall.readMenuFile(this.host.barData(), "fonts/poppins-medium.otf");
            if (bytes != null) {
                try {
                    this.barFont = Font.createFont(0, new ByteArrayInputStream(bytes));
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(this.barFont);
                } catch (FontFormatException | IOException e) {
                    this.barFont = null;
                }
            }
        }
        return (this.barFont != null ? this.barFont : fallback).deriveFont(size);
    }

    void clickOwnFaction(int side) {
        if (this.ownRow == null) {
            System.err.println("test: no own row");
            return;
        }
        this.ownRow.dispatchEvent(new MouseEvent(this.ownRow, 502, System.currentTimeMillis(), 0, 30, 10, 1, false, 1));
        JPopupMenu[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
        System.err.println("test: menu path " + selectedPath.length + ", row showing " + this.ownRow.isShowing());
        if (selectedPath.length > 0) {
            JPopupMenu jPopupMenu = selectedPath[0];
            if (jPopupMenu instanceof JPopupMenu) {
                JPopupMenu menu = jPopupMenu;
                updateRoom();
                System.err.println("test: after a room update the menu is visible " + menu.isVisible() + ", path " + MenuSelectionManager.defaultManager().getSelectedPath().length);
                for (JMenuItem item : menu.getComponents()) {
                    System.err.println("test: entry " + (item instanceof JMenuItem ? item.getText() : "-"));
                }
                String shot = System.getProperty("neroxis.launcher.screenshot");
                if (shot != null && menu.getWidth() > 0) {
                    BufferedImage picture = new BufferedImage(menu.getWidth(), menu.getHeight(), 1);
                    menu.paint(picture.getGraphics());
                    try {
                        ImageIO.write(picture, "png", new File(shot.replace(".png", "-menu.png")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                JMenuItem component = menu.getComponent(side);
                if (component instanceof JMenuItem) {
                    JMenuItem item2 = component;
                    item2.doClick();
                    System.err.println("test: clicked " + item2.getText() + ", side wanted now " + this.client.getSide());
                }
            }
        }
    }

    void showKeybinds(boolean modal) {
        if (this.keybindEditor == null) {
            String catalog = this.host.gameText("common/configs/keybind_catalog.json");
            String defaults = this.host.gameText("common/configs/keybind_defaults.json");
            if (!KeybindEditor.available(catalog, defaults)) {
                JOptionPane.showMessageDialog(this.host.frame(), "The keybinds of the game were not found. Start Beyond All Reason once so it is up to date, then try again.");
                return;
            }
            this.keybindEditor = new KeybindEditor(this.host.frame(), this.host.barData(), catalog, defaults, this.host.gameText("language/en/commands.json"), this.host.gameText("language/en/interface.json"));
        }
        this.keybindEditor.show(modal);
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$Host.class */
    interface Host {
        JFrame frame();

        Path barData();

        Path home();

        boolean hasMap(String mapName);

        BufferedImage mapImage(String mapName);

        List<float[]> mapStarts(String mapName);

        List<String> mapNames();

        String gameText(String file);

        BufferedImage gameImage(String file);

        BarInstall.Marks mapMarks(String mapName);

        void mapsChanged();

        boolean startEngine(Path script, String engineVersion);

        boolean engineRunning();

        long millisSinceEngineExit();

        void lobbyChanged();

        void showRoom(boolean visible);

        default JComponent barFolderSetting() {
            return null;
        }

        default Path mapArchive(String mapName) {
            return null;
        }

        default boolean changeMap() {
            return false;
        }

        default String displayMode() {
            return "window";
        }

        default void setDisplay(String mode, int screen) {
        }

        default List<String> screenNames() {
            return List.of();
        }

        default void setBackgroundBrightness(int brightness) {
        }
    }

    private void fitDockTabs() {
        if (this.fittingDockTabs || this.dockTabs.getWidth() <= 0 || this.dockTabs.getTabCount() == 0) {
            return;
        }
        this.fittingDockTabs = true;
        try {
            float[] sizes = {10.5f, 10.0f, 9.5f, 9.0f, 8.5f};
            int[] gaps = {12, 10, 8, 7, 6, 5, 4, 3, 2};
            Font base = this.dockTabs.getFont();
            float size = sizes[sizes.length - 1];
            int gap = gaps[gaps.length - 1];
            loop0: for (float trySize : sizes) {
                FontMetrics metrics = this.dockTabs.getFontMetrics(base.deriveFont(trySize));
                int text = 0;
                for (int i = 0; i < this.dockTabs.getTabCount(); i++) {
                    String title = this.dockTabs.getTitleAt(i);
                    text += title.startsWith("<html>") ? Math.round(trySize * 1.7f) : metrics.stringWidth(title);
                }
                for (int tryGap : gaps) {
                    if ((tryGap >= 4 || trySize <= sizes[sizes.length - 1]) && text + (this.dockTabs.getTabCount() * 2 * tryGap) + 6 <= this.dockTabs.getWidth()) {
                        size = trySize;
                        gap = tryGap;
                        break loop0;
                    }
                }
            }
            if (base.getSize2D() != size) {
                this.dockTabs.setFont(base.deriveFont(size));
            }
            Insets insets = new Insets(8, gap, 8, gap);
            if (!insets.equals(this.dockTabs.getClientProperty("JTabbedPane.tabInsets"))) {
                this.dockTabs.putClientProperty("JTabbedPane.tabInsets", insets);
            }
        } finally {
            this.fittingDockTabs = false;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$RowsModel.class */
    static final class RowsModel extends DefaultListModel<String> {
        RowsModel() {
        }

        void setRows(List<String> rows) {
            if (size() == rows.size()) {
                boolean same = true;
                for (int i = 0; i < rows.size() && same; i++) {
                    same = ((String) get(i)).equals(rows.get(i));
                }
                if (same) {
                    if (!rows.isEmpty()) {
                        fireContentsChanged(this, 0, rows.size() - 1);
                        return;
                    }
                    return;
                }
            }
            clear();
            addAll(rows);
        }
    }

    private void updateArtwork() {
        String wanted = "";
        String choice = setting("room.background", "wartable");
        if (!choice.equals("none") && this.client.getJoinedBattle() != null) {
            if (choice.equals("bar")) {
                int side = this.client.getSide();
                String[] choices = ROOM_ARTWORK[(side == 0 || side == 1) ? side : 2];
                wanted = choices[this.artworkPick % choices.length];
            } else if (choice.equals("custom")) {
                String own = setting("room.background.file", "");
                wanted = own.isEmpty() ? "" : "file:" + own;
            } else {
                int index = Arrays.asList("battlefield", "wartable", "orbit").indexOf(choice);
                wanted = "res:lobby-art-" + ((index >= 0 ? index : (this.artworkPick + this.roomsJoined) % 3) + 1) + ".jpg";
            }
        }
        if (wanted.equals(this.artworkShown)) {
            return;
        }
        this.artworkShown = wanted;
        String file = wanted;
        if (file.isEmpty()) {
            this.room.setArtwork(null, 1.0d);
            return;
        }
        Thread loader = new Thread(() -> {
            BufferedImage bufferedImage;
            BufferedImage picture = null;
            boolean custom = file.startsWith("file:");
            if (custom) {
                try {
                    picture = ImageIO.read(new File(file.substring(5)));
                } catch (IOException | RuntimeException e) {
                    picture = null;
                }
            } else if (!file.startsWith("res:")) {
                picture = this.host.gameImage(file);
            }
            boolean own2 = picture == null && !custom;
            if (own2) {
                String resource = file.startsWith("res:") ? file.substring(4) : "lobby-art-2.jpg";
                try {
                    InputStream stream = LobbyUi.class.getResourceAsStream(resource);
                    if (stream != null) {
                        try {
                            bufferedImage = ImageIO.read(stream);
                        } catch (Throwable t$) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable x2) {
                                    t$.addSuppressed(x2);
                                }
                            }
                            throw t$;
                        }
                    } else {
                        bufferedImage = null;
                    }
                    picture = bufferedImage;
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e2) {
                    picture = null;
                }
            }
            if (own2 && picture != null) {
                BufferedImage lifted = new BufferedImage(picture.getWidth(), picture.getHeight(), 1);
                lifted.getGraphics().drawImage(picture, 0, 0, (ImageObserver) null);
                picture = new RescaleOp(2.1f, 6.0f, (RenderingHints) null).filter(lifted, (BufferedImage) null);
            }
            BufferedImage loaded = picture;
            SwingUtilities.invokeLater(() -> {
                if (file.equals(this.artworkShown)) {
                    this.room.setArtwork(loaded, (own2 || custom) ? 1.0d : 0.25d);
                }
            });
        }, "room-artwork");
        loader.setDaemon(true);
        loader.start();
    }

    private boolean amBoss() {
        String me = this.client.getUserName();
        return me != null && this.bosses.contains(me);
    }

    static JTextArea wrappingText() {
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setFocusable(false);
        text.setOpaque(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder((Border) null);
        text.setForeground(BarLauncher.MUTED);
        return text;
    }

    private void sendOwnStatus() {
        boolean running = this.host.engineRunning();
        this.client.setStatus(running, IdleTime.millis() > 240000);
        if (this.engineWasRunning && !running && this.client.isReady()) {
            this.client.setReady(false);
            this.readyButton.setSelected(false);
        }
        this.engineWasRunning = running;
    }

    private void showGeneratingProgress() {
        if (this.generatingSince <= 0) {
            this.generatingTick.stop();
            this.generatingBar.setVisible(false);
            return;
        }
        double seconds = (System.currentTimeMillis() - this.generatingSince) / 1000.0d;
        int percent = (int) Math.min(99L, Math.round(100.0d * (1.0d - Math.exp((-seconds) / 17.0d))));
        String step = GENERATING_STEPS[0][1];
        for (String[] candidate : GENERATING_STEPS) {
            if (percent >= Integer.parseInt(candidate[0])) {
                step = candidate[1];
            }
        }
        this.generatingBar.setVisible(true);
        this.generatingBar.setValue(percent);
        this.roomMap.setText(step + "...  " + percent + "%");
        this.roomPreview.setPlaceholder(step + "...");
        this.mapStatus.setForeground(BUSY);
        this.mapStatus.setText(step + "... " + percent + "%");
    }

    LobbyUi(Host host) {
        this.onlineRebuild.setRepeats(false);
        this.usersRebuild.setRepeats(false);
        this.knownPlayers = Set.of();
        this.knownTimer = new Timer(300000, e -> {
            refreshKnownPlayers();
        });
        this.publicOnline = Set.of();
        this.knownCountries = new ConcurrentHashMap();
        this.publicInGame = Set.of();
        this.publicOnlineTimer = new Timer(30000, e2 -> {
            refreshPublicOnline();
        });
        this.friendInput = new JTextField();
        this.room = new BarLauncher.BackgroundPanel(new BorderLayout(14, 12));
        this.artworkShown = "";
        this.artworkBattle = -1;
        this.artworkPick = new Random().nextInt(64);
        this.roomTitle = new JLabel(" ");
        this.roomTitleEditor = new JTextField();
        this.roomTitleCards = new JPanel(new CardLayout());
        this.renamingFrom = "";
        this.roomMap = new JLabel(" ");
        this.mapStatus = new JLabel(" ");
        this.roomPreview = new BarLauncher.PreviewPanel("Map preview");
        this.roomChat = chatPane();
        this.roomInput = new JTextField();
        this.teamsPanel = new JPanel();
        this.readyButton = new JToggleButton("READY");
        this.spectateButton = new JToggleButton("Spectate");
        this.gameStatus = new JLabel(" ");
        this.engineStatus = new JLabel(" ");
        this.boxStatus = new JLabel(" ");
        this.spectatorPanel = new JPanel();
        this.spectatorTitle = new JLabel("Spectators");
        this.optionsLabel = new JLabel(" ");
        this.bosses = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        this.boxShape = "square";
        this.lastBoxSignature = "";
        this.boxShapeTimer = new Timer(700, e3 -> {
            sendBoxShape();
        });
        this.partyPanel = new JPanel();
        this.createPartyButton = new JButton("Create party");
        this.partiesHead = new JPanel(new BorderLayout());
        this.partySearch = new JTextField();
        this.partyCandidates = new JPanel();
        this.partyInviteTitle = new JLabel("INVITE");
        this.partyHint = wrappingText();
        this.badges = Map.of();
        this.badgeTimer = new Timer(180000, e4 -> {
            refreshBadges();
        });
        this.statusTimer = new Timer(5000, e5 -> {
            sendOwnStatus();
        });
        this.profileName = new JLabel(" ");
        this.profilePrime = new JLabel(" ");
        this.primeBadge = new PrimeBadge(168);
        this.primeExplanation = new JLabel();
        this.primeCaption = new JLabel(" ", 0);
        this.profileCreator = new JLabel(" ");
        this.profileStatus = new JLabel(" ");
        this.paypalButton = new JButton("Pay with PayPal");
        this.rankPanel = new JPanel();
        this.statsPanel = new WidthTrackingPanel(null);
        this.profileCards = new CardLayout();
        this.leaderboard = new JLabel(" ");
        this.leaderboardBoard = "global";
        this.primeButton = new JButton("★  Prime");
        this.generatingBar = new JProgressBar(0, 100);
        this.generatingTick = new Timer(350, e6 -> {
            showGeneratingProgress();
        });
        this.playerNotes = new Properties();
        this.voteBar = new BarLauncher.Surface(new BorderLayout(12, 0), new Color(52, 44, 30), 16);
        this.voteText = new JLabel(" ");
        this.voteTimeout = new Timer(70000, e7 -> {
            this.voteBar.setVisible(false);
        });
        this.teamSize = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        this.teamCount = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7, 8});
        this.formatLabel = new JLabel(" ");
        this.verifiedMaps = new ConcurrentHashMap();
        this.staySignedIn = true;
        this.pendingRoom = -1;
        this.pendingLines = new ArrayList();
        this.reconnectTimer = new Timer(5000, e8 -> {
            reconnect();
        });
        this.displayChoice = new JComboBox<>();
        this.flagsLoading = ConcurrentHashMap.newKeySet();
        this.flagPictures = new HashMap();
        this.gameColours = Map.of();
        this.host = host;
        Path file = Platform.settingsFile(host.home());
        if (Files.isRegularFile(file, new LinkOption[0])) {
            try {
                InputStream stream = Files.newInputStream(file, new OpenOption[0]);
                try {
                    this.settings.load(stream);
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
            } catch (IOException e9) {
            }
        }
        buildBadge();
        buildRoom();
        Timer friendsTimer = new Timer(10000, e10 -> {
            if (this.client.isLoggedIn()) {
                this.client.refreshFriends();
            }
        });
        friendsTimer.start();
    }

    LobbyClient client() {
        return this.client;
    }

    String mapServiceUrl() {
        return this.settings.getProperty("mapservice", DEFAULT_MAP_SERVICE);
    }

    String serverName() {
        return this.settings.getProperty("server", DEFAULT_SERVER);
    }

    JComponent badge() {
        return this.badge;
    }

    private void buildBadge() {
        this.badge.putClientProperty("JButton.buttonType", "roundRect");
        this.badge.setFocusPainted(false);
        this.badge.setCursor(Cursor.getPredefinedCursor(12));
        this.badge.addActionListener(e -> {
            if (!this.client.isLoggedIn()) {
                showLogin();
                return;
            }
            if (System.currentTimeMillis() - this.badgeMenuClosedAt < 250) {
                return;
            }
            JPopupMenu menu = new JPopupMenu();
            menu.addPopupMenuListener(new PopupMenuListener(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.2
                final /* synthetic */ LobbyUi this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
                    this.this$0.badgeMenuClosedAt = System.currentTimeMillis();
                }

                public void popupMenuCanceled(PopupMenuEvent event) {
                    this.this$0.badgeMenuClosedAt = System.currentTimeMillis();
                }
            });
            menu.add(item("View profile", this::showProfile));
            menu.add(item("Account and Prime", () -> {
                showProfile(3);
            }));
            if (!setting("bugs.inbox.key", "").isBlank()) {
                menu.add(item("Bug reports" + (this.bugsUnread > 0 ? "  (" + this.bugsUnread + " new)" : ""), this::showBugInbox));
            }
            menu.add(item("Report a bug", this::showBugReport));
            JMenuItem signOut = new JMenuItem("Sign out");
            signOut.addActionListener(event -> {
                this.settings.remove("login");
                saveSettings();
                this.reconnectTimer.stop();
                this.client.disconnect();
                this.account = null;
                onState(null);
                onUsersChanged();
                onFriendsChanged();
                onBattleRoomChanged();
            });
            menu.add(signOut);
            menu.show(this.badge, 0, this.badge.getHeight());
        });
        updateBadge();
    }

    private void updateBadge() {
        if (this.client.isLoggedIn()) {
            this.badge.setText("<html>●&nbsp; " + this.client.getUserName() + badgeHtml(this.client.getUserName()) + "</html>");
            this.badge.setForeground(ONLINE);
        } else {
            this.badge.setText(this.signingIn ? "Signing in..." : "Sign in to play online");
            this.badge.setForeground(BarLauncher.TEXT);
        }
    }

    void showLogin() {
        if (this.loginDialog == null) {
            this.loginDialog = new JDialog(this.host.frame(), "Sign in", System.getProperty("neroxis.launcher.screenshot") == null);
            JTextField server = new JTextField(this.settings.getProperty("server", DEFAULT_SERVER));
            JTextField port = new JTextField(this.settings.getProperty("port", "8200"));
            JCheckBox tls = new JCheckBox("Encrypted connection (TLS, port 8201)", Boolean.parseBoolean(this.settings.getProperty("tls", "false")));
            JTextField user = new JTextField(this.settings.getProperty("user", ""));
            JPasswordField password = new JPasswordField();
            JTextField mapService = new JTextField(this.settings.getProperty("mapservice", DEFAULT_MAP_SERVICE));
            mapService.setToolTipText("Where maps of this server are downloaded from (find API)");
            this.loginStatus = new JLabel(" ");
            this.loginStatus.setForeground(BarLauncher.MUTED);
            JPanel form = new JPanel();
            form.setLayout(new BoxLayout(form, 1));
            form.setOpaque(false);
            form.add(formRows(new Object[][]{new Object[]{"User name", user}, new Object[]{"Password", password}}));
            JCheckBox stay = new JCheckBox("Stay signed in on this computer", true);
            stay.setOpaque(false);
            stay.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            stay.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            stay.addActionListener(e -> {
                this.staySignedIn = stay.isSelected();
            });
            JButton forgot = linkButton("Forgot password?");
            forgot.addActionListener(e2 -> {
                openResetPage(this.loginDialog);
            });
            JPanel stayRow = new JPanel(new BorderLayout());
            stayRow.setOpaque(false);
            stayRow.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            stayRow.add(stay, "West");
            stayRow.add(forgot, "East");
            stayRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, stayRow.getPreferredSize().height));
            form.add(stayRow);
            JPanel serverRows = formRows(new Object[][]{new Object[]{"Server", server}, new Object[]{"Port", port}, new Object[]{"", tls}, new Object[]{"Map service", mapService}});
            serverRows.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            serverRows.setVisible(false);
            JButton serverToggle = new JButton("Server settings");
            serverToggle.setBorderPainted(false);
            serverToggle.setContentAreaFilled(false);
            serverToggle.setForeground(BarLauncher.MUTED);
            serverToggle.setCursor(Cursor.getPredefinedCursor(12));
            serverToggle.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            serverToggle.setMargin(new Insets(8, 0, 0, 0));
            serverToggle.addActionListener(e3 -> {
                serverRows.setVisible(!serverRows.isVisible());
                this.loginDialog.pack();
                this.loginDialog.setSize(Math.max(430, this.loginDialog.getWidth()), this.loginDialog.getHeight());
            });
            form.add(serverToggle);
            form.add(serverRows);
            JButton signIn = new JButton("SIGN IN");
            signIn.putClientProperty("JButton.buttonType", "roundRect");
            signIn.setBackground(BarLauncher.ACCENT);
            signIn.setForeground(new Color(25, 18, 8));
            signIn.setFont(signIn.getFont().deriveFont(1, 14.0f));
            JButton register = new JButton("Create account");
            register.putClientProperty("JButton.buttonType", "roundRect");
            Runnable store = () -> {
                this.settings.setProperty("server", server.getText().trim());
                this.settings.setProperty("port", port.getText().trim());
                this.settings.setProperty("tls", String.valueOf(tls.isSelected()));
                this.settings.setProperty("user", user.getText().trim());
                this.settings.setProperty("mapservice", mapService.getText().trim());
                saveSettings();
            };
            signIn.addActionListener(e4 -> {
                store.run();
                connect(user.getText().trim(), password.getPassword(), null);
            });
            register.addActionListener(e5 -> {
                store.run();
                this.loginDialog.setVisible(false);
                showRegister();
            });
            password.addActionListener(e6 -> {
                signIn.doClick();
            });
            JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
            buttons.setOpaque(false);
            buttons.add(register);
            buttons.add(signIn);
            JLabel heading = new JLabel("Sign in");
            heading.setFont(heading.getFont().deriveFont(1, 20.0f));
            heading.setForeground(BarLauncher.TEXT);
            JPanel top = new JPanel(new BorderLayout(0, 6));
            top.setOpaque(false);
            top.add(heading, "North");
            JPanel bottom = new JPanel(new BorderLayout(0, 10));
            bottom.setOpaque(false);
            bottom.add(this.loginStatus, "North");
            bottom.add(buttons, "Center");
            JPanel root = new JPanel(new BorderLayout(0, 16));
            root.setBackground(BarLauncher.BACKGROUND);
            root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
            root.add(top, "North");
            root.add(form, "Center");
            root.add(bottom, "South");
            this.loginDialog.setContentPane(root);
            this.loginDialog.pack();
            this.loginDialog.setSize(Math.max(430, this.loginDialog.getWidth()), this.loginDialog.getHeight() + 10);
        }
        this.loginStatus.setText(" ");
        this.loginDialog.setLocationRelativeTo(this.host.frame());
        this.loginDialog.setVisible(true);
    }

    static JButton linkButton(String text) {
        JButton link = new JButton(text);
        link.setBorderPainted(false);
        link.setContentAreaFilled(false);
        link.setFocusPainted(false);
        link.setForeground(BarLauncher.ACCENT);
        link.setCursor(Cursor.getPredefinedCursor(12));
        link.setMargin(new Insets(10, 0, 0, 0));
        return link;
    }

    static void openResetPage(Component parent) {
        try {
            Desktop.getDesktop().browse(URI.create(RESET_PAGE));
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(parent, "Open this page in your browser to choose a new password:\nhttps://eclipseforeverbeyond.com/reset-password.html", "New password", 1);
        }
    }

    private static JPanel formRows(Object[][] rows) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 10));
        panel.setOpaque(false);
        panel.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        for (Object[] row : rows) {
            JLabel label = new JLabel((String) row[0]);
            label.setForeground(BarLauncher.TEXT);
            panel.add(label);
            panel.add((Component) row[1]);
        }
        return panel;
    }

    void showRegister() {
        if (this.registerDialog == null) {
            this.registerDialog = new JDialog(this.host.frame(), "Create account", System.getProperty("neroxis.launcher.screenshot") == null);
            JTextField user = new JTextField();
            JTextField email = new JTextField();
            JPasswordField password = new JPasswordField();
            JPasswordField confirm = new JPasswordField();
            this.registerStatus = new JLabel(" ");
            JButton create = new JButton("CREATE ACCOUNT");
            create.putClientProperty("JButton.buttonType", "roundRect");
            create.setBackground(BarLauncher.ACCENT);
            create.setForeground(new Color(25, 18, 8));
            create.setFont(create.getFont().deriveFont(1, 14.0f));
            create.setPreferredSize(new Dimension(100, 40));
            create.addActionListener(e -> {
                String str;
                String name = user.getText().trim();
                if (!name.matches("[A-Za-z0-9_\\[\\]]{2,20}")) {
                    str = "User name: 2 to 20 letters, digits or _";
                } else if (!email.getText().trim().matches("\\S+@\\S+\\.\\S+")) {
                    str = "Enter your email address";
                } else if (password.getPassword().length < 6) {
                    str = "The password needs at least 6 characters";
                } else {
                    str = !Arrays.equals(password.getPassword(), confirm.getPassword()) ? "The passwords are not the same" : null;
                }
                String problem = str;
                if (problem != null) {
                    this.registerStatus.setForeground(ERROR);
                    this.registerStatus.setText(problem);
                    return;
                }
                this.settings.setProperty("user", name);
                saveSettings();
                this.registering = true;
                this.registerStatus.setForeground(BarLauncher.MUTED);
                this.registerStatus.setText("Creating the account...");
                connect(name, password.getPassword(), email.getText().trim());
                password.setText("");
                confirm.setText("");
            });
            confirm.addActionListener(e2 -> {
                create.doClick();
            });
            JLabel heading = new JLabel("Create account");
            heading.setFont(heading.getFont().deriveFont(1, 20.0f));
            heading.setForeground(BarLauncher.TEXT);
            JPanel bottom = new JPanel(new BorderLayout(0, 10));
            bottom.setOpaque(false);
            bottom.add(this.registerStatus, "North");
            bottom.add(create, "Center");
            JPanel root = new JPanel(new BorderLayout(0, 16));
            root.setBackground(BarLauncher.BACKGROUND);
            root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
            root.add(heading, "North");
            root.add(formRows(new Object[][]{new Object[]{"User name", user}, new Object[]{"Email address", email}, new Object[]{"Password", password}, new Object[]{"Confirm password", confirm}}), "Center");
            root.add(bottom, "South");
            this.registerDialog.setContentPane(root);
            this.registerDialog.pack();
            this.registerDialog.setSize(Math.max(430, this.registerDialog.getWidth()), this.registerDialog.getHeight() + 10);
        }
        this.registerStatus.setText(" ");
        this.registerDialog.setLocationRelativeTo(this.host.frame());
        this.registerDialog.setVisible(true);
    }

    void connect(String user, char[] password, String registerEmail) {
        int port;
        try {
            port = Integer.parseInt(this.settings.getProperty("port", "8200").trim());
        } catch (NumberFormatException e) {
            port = 8200;
        }
        if (user.isEmpty() || password.length == 0) {
            if (this.loginStatus != null) {
                this.loginStatus.setText("Enter a user name and a password");
                return;
            }
            return;
        }
        this.signingIn = true;
        updateBadge();
        if (this.loginStatus != null) {
            this.loginStatus.setForeground(BarLauncher.MUTED);
            this.loginStatus.setText("Connecting to " + serverName() + "...");
        }
        this.pendingUser = user;
        this.pendingHash = LobbyClient.hashPassword(password);
        Arrays.fill(password, (char) 0);
        this.client.connectWithHash(serverName(), port, Boolean.parseBoolean(this.settings.getProperty("tls", "false")), user, this.pendingHash, registerEmail);
    }

    private boolean canReconnect() {
        return (this.settings.getProperty("login") == null || this.settings.getProperty("user", "").isEmpty() || (this.loginDialog != null && this.loginDialog.isVisible()) || ((this.registerDialog != null && this.registerDialog.isVisible()) || System.getProperty("neroxis.launcher.screenshot") != null)) ? false : true;
    }

    private void scheduleReconnect() {
        if (!canReconnect() || this.reconnectTimer.isRunning()) {
            return;
        }
        this.reconnectDelay = this.reconnectDelay == 0 ? 5000 : Math.min(this.reconnectDelay * 2, 20000);
        this.reconnectTimer.setInitialDelay(this.reconnectDelay);
        this.reconnectTimer.setRepeats(false);
        this.reconnectTimer.start();
        append(this.chat, null, "Signing in again in " + (this.reconnectDelay / 1000) + " seconds...", BarLauncher.MUTED);
    }

    void startAfterChecks() {
        if (this.started) {
            return;
        }
        this.started = true;
        if (this.settings.getProperty("login") != null && !this.settings.getProperty("user", "").isEmpty() && System.getProperty("neroxis.launcher.screenshot") == null && !this.client.isLoggedIn()) {
            SwingUtilities.invokeLater(this::signInWithStoredLogin);
        }
    }

    private void reconnect() {
        if (this.client.isLoggedIn() || this.signingIn || !canReconnect()) {
            return;
        }
        signInWithStoredLogin();
    }

    private void signInWithStoredLogin() {
        int port;
        try {
            port = Integer.parseInt(this.settings.getProperty("port", "8200").trim());
        } catch (NumberFormatException e) {
            port = 8200;
        }
        this.signingIn = true;
        updateBadge();
        this.client.connectWithHash(serverName(), port, Boolean.parseBoolean(this.settings.getProperty("tls", "false")), this.settings.getProperty("user"), this.settings.getProperty("login"), null);
    }

    private void saveSettings() {
        try {
            OutputStream stream = Files.newOutputStream(Platform.settingsFile(this.host.home()), new OpenOption[0]);
            try {
                this.settings.store(stream, "Eclipse launcher (the password is never stored; \"login\" is the hash the lobby protocol sends, sign out removes it)");
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
            System.err.println("settings not saved: " + e.getMessage());
        }
    }

    private void updateOnline() {
        this.onlineRebuild.restart();
    }

    private void rebuildOnline() {
        List<String> rows = new ArrayList<>();
        String filter = this.friendInput.getText().trim().toLowerCase(Locale.ROOT);
        Predicate<String> shown = name -> {
            return filter.isEmpty() || name.toLowerCase(Locale.ROOT).contains(filter);
        };
        List<String> requests = this.client.getFriendRequests();
        requests.removeIf(name2 -> {
            return !shown.test(name2);
        });
        if (!requests.isEmpty()) {
            rows.add("#FRIEND REQUESTS");
            requests.forEach(name3 -> {
                rows.add("?" + name3);
            });
        }
        List<LobbyClient.User> users = this.client.getUsers();
        users.removeIf(user -> {
            return user.bot() || !shown.test(user.name);
        });
        Set<String> friends = new TreeSet<>((Comparator<? super String>) String.CASE_INSENSITIVE_ORDER);
        friends.addAll(this.client.getFriends());
        friends.removeIf(name4 -> {
            return !shown.test(name4);
        });
        Set<String> onlineNames = new TreeSet<>((Comparator<? super String>) String.CASE_INSENSITIVE_ORDER);
        List<String> online = new ArrayList<>();
        List<String> others = new ArrayList<>();
        users.sort(Comparator.comparing(user2 -> {
            return user2.name.toLowerCase(Locale.ROOT);
        }));
        for (LobbyClient.User user3 : users) {
            onlineNames.add(user3.name);
            (friends.contains(user3.name) ? online : others).add(user3.name);
        }
        if (!this.client.isLoggedIn()) {
            for (String name5 : this.publicOnline) {
                if (shown.test(name5) && !onlineNames.contains(name5)) {
                    onlineNames.add(name5);
                    others.add(name5);
                }
            }
        }
        rows.add("#FRIENDS  " + online.size());
        Objects.requireNonNull(rows);
        online.forEach((v1) -> {
            r1.add(v1);
        });
        rows.add("#ONLINE  " + others.size());
        Objects.requireNonNull(rows);
        others.forEach((v1) -> {
            r1.add(v1);
        });
        Set<String> offline = new TreeSet<>((Comparator<? super String>) String.CASE_INSENSITIVE_ORDER);
        for (String name6 : this.knownPlayers) {
            if (!onlineNames.contains(name6) && !name6.equalsIgnoreCase(this.client.getUserName()) && shown.test(name6)) {
                offline.add(name6);
            }
        }
        List<String> offlineFriends = new ArrayList<>();
        for (String friend : friends) {
            if (!onlineNames.contains(friend)) {
                offlineFriends.add(friend);
                offline.remove(friend);
            }
        }
        if (!offlineFriends.isEmpty() || !offline.isEmpty()) {
            rows.add("#OFFLINE  " + (offlineFriends.size() + offline.size()));
            offlineFriends.forEach(name7 -> {
                rows.add("~" + name7);
            });
            offline.forEach(name8 -> {
                rows.add("~" + name8);
            });
        }
        if (!filter.isEmpty() && rows.isEmpty()) {
            rows.add("#NOBODY CALLED \"" + this.friendInput.getText().trim().toUpperCase(Locale.ROOT) + "\"");
        }
        this.onlineModel.setRows(rows);
        if (filter.isEmpty() && this.dockTabs.getTabCount() > 0) {
            int count = this.client.isLoggedIn() ? users.size() : onlineNames.size();
            this.dockTabs.setTitleAt(0, "ONLINE" + (count == 0 ? "" : "  " + count) + (requests.isEmpty() ? "" : " ●"));
        }
    }

    private String bugsUrl(String path) {
        return mapServiceUrl().replaceAll("/find/?$", "") + "/bugs/" + path;
    }

    private static String postJson(String url, String json) throws InterruptedException, IOException {
        HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofMinutes(2L)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
        return (String) response.body();
    }

    private static String jsonString(String text) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\t':
                    out.append("\\t");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\"':
                    out.append("\\\"");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                default:
                    if (c < ' ') {
                        out.append(String.format("\\u%04x", Integer.valueOf(c)));
                    } else {
                        out.append(c);
                    }
                    break;
            }
        }
        return out.append('\"').toString();
    }

    void reportBug() {
        showBugReport();
    }

    private void showBugReport() {
        String user = this.client.getUserName();
        if (user == null) {
            JOptionPane.showMessageDialog(this.host.frame(), "Sign in first, so the answer can find you.", "Report a bug", 1);
            return;
        }
        JDialog dialog = new JDialog(this.host.frame(), "Report a bug", true);
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        panel.setBackground(BarLauncher.BACKGROUND);
        JLabel intro = new JLabel("<html><div style='width:440px'>What went wrong, and what you did before. Screenshots help a lot. The report goes to the people who run Eclipse; their answer shows up in your chat the next time you sign in.</div></html>");
        intro.setForeground(BarLauncher.TEXT);
        JTextArea text = new JTextArea(9, 50);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBackground(CHAT_BACKGROUND);
        text.setForeground(BarLauncher.TEXT);
        text.setCaretColor(BarLauncher.TEXT);
        text.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CHAT_LINE), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        List<Path> pictures = new ArrayList<>();
        List<BufferedImage> captured = new ArrayList<>();
        JPanel thumbs = new JPanel(new FlowLayout(0, 8, 0));
        thumbs.setOpaque(false);
        JLabel status = new JLabel(" ");
        status.setForeground(BarLauncher.MUTED);
        Runnable showThumbs = () -> {
            thumbs.removeAll();
            Iterator i$ = pictures.iterator();
            while (i$.hasNext()) {
                Path file = (Path) i$.next();
                thumbs.add(thumb(file.getFileName().toString(), null, file));
            }
            for (int i = 0; i < captured.size(); i++) {
                thumbs.add(thumb("Eclipse " + (i + 1), (BufferedImage) captured.get(i), null));
            }
            thumbs.revalidate();
            thumbs.repaint();
        };
        JButton addFile = new JButton("Add screenshot...");
        addFile.addActionListener(e -> {
            if (pictures.size() + captured.size() >= 4) {
                status.setText("Four screenshots at most.");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Pictures (PNG, JPG)", new String[]{"png", "jpg", "jpeg"}));
            chooser.setMultiSelectionEnabled(true);
            if (chooser.showOpenDialog(dialog) == 0) {
                File[] arr$ = chooser.getSelectedFiles();
                for (File file : arr$) {
                    if (pictures.size() + captured.size() < 4 && file.isFile()) {
                        pictures.add(file.toPath());
                    }
                }
                showThumbs.run();
            }
        });
        JButton capture = new JButton("Screenshot of Eclipse now");
        capture.addActionListener(e2 -> {
            if (pictures.size() + captured.size() >= 4) {
                status.setText("Four screenshots at most.");
                return;
            }
            JFrame frame = this.host.frame();
            BufferedImage image = new BufferedImage(Math.max(1, frame.getWidth()), Math.max(1, frame.getHeight()), 1);
            Graphics2D g = image.createGraphics();
            frame.paint(g);
            g.dispose();
            captured.add(image);
            showThumbs.run();
        });
        JButton send = new JButton("Send");
        send.setBackground(BarLauncher.ACCENT);
        send.setForeground(Color.BLACK);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e3 -> {
            dialog.dispose();
        });
        send.addActionListener(e4 -> {
            String what = text.getText().trim();
            if (what.length() < 10) {
                status.setText("A few words more, please.");
                return;
            }
            send.setEnabled(false);
            status.setText("Sending...");
            Thread sender = new Thread(() -> {
                String result;
                try {
                    StringBuilder json = new StringBuilder("{\"user\": " + jsonString(user) + ", \"text\": " + jsonString(what) + ", \"launcher\": " + jsonString(launcherVersion()) + ", \"system\": " + jsonString(System.getProperty("os.name", "") + " " + System.getProperty("os.version", "")) + ", \"pictures\": [");
                    boolean first = true;
                    Iterator i$ = pictures.iterator();
                    while (i$.hasNext()) {
                        Path file = (Path) i$.next();
                        byte[] bytes = Files.readAllBytes(file);
                        if (bytes.length > 3145728) {
                            BufferedImage image = ImageIO.read(file.toFile());
                            bytes = image != null ? jpeg(image) : bytes;
                        }
                        json.append(first ? "" : ", ").append("{\"data\": \"").append(Base64.getEncoder().encodeToString(bytes)).append("\"}");
                        first = false;
                    }
                    Iterator i$2 = captured.iterator();
                    while (i$2.hasNext()) {
                        json.append(first ? "" : ", ").append("{\"data\": \"").append(Base64.getEncoder().encodeToString(jpeg((BufferedImage) i$2.next()))).append("\"}");
                        first = false;
                    }
                    json.append("]}");
                    result = postJson(bugsUrl("report"), json.toString());
                } catch (IOException | RuntimeException e4) {
                    result = "{\"error\": " + jsonString("could not send: " + e4.getMessage()) + "}";
                } catch (InterruptedException e5) {
                    Thread.currentThread().interrupt();
                    return;
                }
                String answer = result;
                SwingUtilities.invokeLater(() -> {
                    String strValueOf;
                    Object parsed = MiniJson.parse(answer);
                    if (parsed instanceof Map) {
                        Map<?, ?> map = (Map) parsed;
                        if (map.get("id") != null) {
                            String ticket = String.valueOf(map.get("id")).replaceAll("\\.0$", "");
                            String keys = setting("bugs.tickets", "");
                            remember("bugs.tickets", (keys.isEmpty() ? "" : keys + ",") + ticket + ":" + String.valueOf(map.get("key")));
                            dialog.dispose();
                            append(this.chat, null, "Bug report #" + ticket + " sent. Thank you! The answer shows up here at a later sign in.", ONLINE);
                            return;
                        }
                    }
                    send.setEnabled(true);
                    if (parsed instanceof Map) {
                        Map<?, ?> map2 = (Map) parsed;
                        if (map2.get("error") != null) {
                            strValueOf = String.valueOf(map2.get("error"));
                        } else {
                            strValueOf = "The server did not take the report, try again later.";
                        }
                    } else {
                        strValueOf = "The server did not take the report, try again later.";
                    }
                    status.setText(strValueOf);
                });
            }, "bug-report");
            sender.setDaemon(true);
            sender.start();
        });
        JPanel buttons = new JPanel(new FlowLayout(0, 8, 0));
        buttons.setOpaque(false);
        buttons.add(addFile);
        buttons.add(capture);
        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(thumbs, "North");
        bottom.add(buttons, "Center");
        JPanel actions = new JPanel(new FlowLayout(2, 8, 0));
        actions.setOpaque(false);
        actions.add(status);
        actions.add(cancel);
        actions.add(send);
        bottom.add(actions, "South");
        panel.add(intro, "North");
        panel.add(new JScrollPane(text), "Center");
        panel.add(bottom, "South");
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this.host.frame());
        dialog.setVisible(true);
    }

    private static byte[] jpeg(BufferedImage image) throws IOException {
        BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), 1);
        rgb.getGraphics().drawImage(image, 0, 0, (ImageObserver) null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(rgb, "jpg", out);
        return out.toByteArray();
    }

    private static JComponent thumb(String title, BufferedImage image, Path file) {
        BufferedImage bufferedImage;
        JLabel label = new JLabel(title);
        label.setForeground(BarLauncher.MUTED);
        label.setVerticalTextPosition(3);
        label.setHorizontalTextPosition(0);
        if (image != null) {
            bufferedImage = image;
        } else if (file != null) {
            try {
                bufferedImage = ImageIO.read(file.toFile());
            } catch (IOException e) {
            }
        } else {
            bufferedImage = null;
        }
        BufferedImage source = bufferedImage;
        if (source != null) {
            label.setIcon(new ImageIcon(source.getScaledInstance(96, -1, 4)));
        }
        return label;
    }

    private String launcherVersion() {
        Path jar = LauncherUpdater.runningJar();
        if (jar == null) {
            return "dev";
        }
        try {
            return LauncherUpdater.hash(jar).substring(0, 8);
        } catch (IOException | RuntimeException e) {
            return "?";
        }
    }

    private void checkBugAnswers() {
        String tickets = setting("bugs.tickets", "");
        if (tickets.isBlank()) {
            return;
        }
        StringBuilder query = new StringBuilder();
        for (String pair : tickets.split(",")) {
            query.append(query.isEmpty() ? "?" : "&").append("t=").append(pair.trim());
        }
        Thread loader = new Thread(() -> {
            Object answer = fetchJson(bugsUrl("mine") + String.valueOf(query));
            if (answer instanceof Map) {
                Map<?, ?> map = (Map) answer;
                Object patt0$temp = map.get("tickets");
                if (!(patt0$temp instanceof List)) {
                    return;
                }
                List<?> list = (List) patt0$temp;
                SwingUtilities.invokeLater(() -> {
                    for (Object entry : list) {
                        if (entry instanceof Map) {
                            Map<?, ?> ticket = (Map) entry;
                            Object patt0$temp2 = ticket.get("replies");
                            if (patt0$temp2 instanceof List) {
                                List<?> replies = (List) patt0$temp2;
                                String id = String.valueOf(ticket.get("id")).replaceAll("\\.0$", "");
                                int seen = settingNumber("bugs.seen." + id, 0);
                                for (int i = seen; i < replies.size(); i++) {
                                    Object patt1$temp = replies.get(i);
                                    if (patt1$temp instanceof Map) {
                                        Map<?, ?> reply = (Map) patt1$temp;
                                        append(this.chat, null, "Eclipse support on your bug report #" + id + ": " + String.valueOf(reply.get("text")), BarLauncher.ACCENT);
                                    }
                                }
                                if (replies.size() > seen) {
                                    remember("bugs.seen." + id, String.valueOf(replies.size()));
                                    if (this.dockTabs.getSelectedIndex() != 1) {
                                        this.dockTabs.setTitleAt(1, "CHAT ●");
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, "bug-answers");
        loader.setDaemon(true);
        loader.start();
    }

    private void refreshBugInbox() {
        String key = setting("bugs.inbox.key", "");
        if (key.isBlank()) {
            return;
        }
        Thread loader = new Thread(() -> {
            Object answer = fetchJson(bugsUrl("inbox") + "?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8));
            if (answer instanceof Map) {
                Map<?, ?> map = (Map) answer;
                Object patt0$temp = map.get("unread");
                if (patt0$temp instanceof Number) {
                    Number unread = (Number) patt0$temp;
                    SwingUtilities.invokeLater(() -> {
                        if (unread.intValue() > this.bugsUnread) {
                            append(this.chat, null, unread.intValue() + " bug report" + (unread.intValue() == 1 ? "" : "s") + " waiting: profile menu > Bug reports.", BarLauncher.ACCENT);
                        }
                        this.bugsUnread = unread.intValue();
                    });
                }
            }
        }, "bug-inbox");
        loader.setDaemon(true);
        loader.start();
    }

    private void showBugInbox() {
        String key = setting("bugs.inbox.key", "");
        JDialog dialog = new JDialog(this.host.frame(), "Bug reports", false);
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, 1));
        list.setBackground(BarLauncher.BACKGROUND);
        list.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setPreferredSize(new Dimension(760, 620));
        dialog.setContentPane(scroll);
        Runnable[] reload = new Runnable[1];
        reload[0] = () -> {
            Thread loader = new Thread(() -> {
                Object answer = fetchJson(bugsUrl("inbox") + "?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8));
                SwingUtilities.invokeLater(() -> {
                    int iIntValue;
                    list.removeAll();
                    if (answer instanceof Map) {
                        Map<?, ?> map = (Map) answer;
                        Object patt0$temp = map.get("tickets");
                        if (patt0$temp instanceof List) {
                            List<?> tickets = (List) patt0$temp;
                            if (tickets.isEmpty()) {
                                JLabel none = new JLabel("No bug reports yet.");
                                none.setForeground(BarLauncher.MUTED);
                                list.add(none);
                            } else {
                                for (Object entry : tickets) {
                                    if (entry instanceof Map) {
                                        Map<?, ?> ticket = (Map) entry;
                                        list.add(ticketCard(ticket, key, reload[0]));
                                        list.add(Box.createVerticalStrut(12));
                                    }
                                }
                                Object patt1$temp = map.get("unread");
                                if (patt1$temp instanceof Number) {
                                    Number n = (Number) patt1$temp;
                                    iIntValue = n.intValue();
                                } else {
                                    iIntValue = 0;
                                }
                                this.bugsUnread = iIntValue;
                            }
                        } else {
                            JLabel none2 = new JLabel("The server did not answer (is the inbox key right?).");
                            none2.setForeground(ERROR);
                            list.add(none2);
                        }
                    } else {
                        JLabel none3 = new JLabel("The server did not answer (is the inbox key right?).");
                        none3.setForeground(ERROR);
                        list.add(none3);
                    }
                    list.revalidate();
                    list.repaint();
                });
            }, "bug-inbox");
            loader.setDaemon(true);
            loader.start();
        };
        reload[0].run();
        dialog.pack();
        dialog.setLocationRelativeTo(this.host.frame());
        dialog.setVisible(true);
    }

    private JComponent ticketCard(Map<?, ?> ticket, String key, Runnable reload) {
        long jDoubleValue;
        String id = String.valueOf(ticket.get("id")).replaceAll("\\.0$", "");
        boolean closed = Boolean.TRUE.equals(ticket.get("closed"));
        boolean read = Boolean.TRUE.equals(ticket.get("read"));
        JPanel card = new BarLauncher.Surface(new BorderLayout(0, 8), BarLauncher.CARD, 12);
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        Object obj = ticket.get("time");
        if (obj instanceof Number) {
            Number n = (Number) obj;
            jDoubleValue = (long) (n.doubleValue() * 1000.0d);
        } else {
            jDoubleValue = 0;
        }
        long when = jDoubleValue;
        JLabel head = new JLabel("<html><b style='font-size:13px'>#" + id + "  " + escape(String.valueOf(ticket.get("user"))) + "</b>  <span style='color:#8c94a3'>" + new SimpleDateFormat("d MMM HH:mm").format(new Date(when)) + "  ·  launcher " + escape(String.valueOf(ticket.get("launcher"))) + "  ·  " + escape(String.valueOf(ticket.get("system"))) + (closed ? "  ·  closed" : read ? "" : "  ·  <b style='color:#F0962E'>new</b>") + "</span></html>");
        head.setForeground(BarLauncher.TEXT);
        JTextArea body = new JTextArea(String.valueOf(ticket.get("text")));
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setEditable(false);
        body.setOpaque(false);
        body.setForeground(BarLauncher.TEXT);
        body.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, 1));
        middle.setOpaque(false);
        body.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        middle.add(body);
        Object obj2 = ticket.get("replies");
        if (obj2 instanceof List) {
            List<?> replies = (List) obj2;
            for (Object entry : replies) {
                if (entry instanceof Map) {
                    Map<?, ?> reply = (Map) entry;
                    boolean owner = "owner".equals(reply.get("from"));
                    JTextArea line = new JTextArea((owner ? "You: " : String.valueOf(ticket.get("user")) + ": ") + String.valueOf(reply.get("text")));
                    line.setLineWrap(true);
                    line.setWrapStyleWord(true);
                    line.setEditable(false);
                    line.setOpaque(false);
                    line.setForeground(owner ? BarLauncher.MUTED : BarLauncher.TEXT);
                    line.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                    middle.add(line);
                }
            }
        }
        Object obj3 = ticket.get("pictures");
        if (obj3 instanceof List) {
            List<?> pictures = (List) obj3;
            if (!pictures.isEmpty()) {
                JPanel shots = new JPanel(new FlowLayout(0, 8, 4));
                shots.setOpaque(false);
                shots.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                for (Object name : pictures) {
                    JLabel shot = new JLabel("loading " + String.valueOf(name) + "...");
                    shot.setForeground(BarLauncher.MUTED);
                    shots.add(shot);
                    String url = bugsUrl("picture/" + String.valueOf(name)) + "?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
                    Thread loader = new Thread(() -> {
                        try {
                            BufferedImage image = ImageIO.read(URI.create(url).toURL());
                            if (image != null) {
                                SwingUtilities.invokeLater(() -> {
                                    shot.setText((String) null);
                                    shot.setIcon(new ImageIcon(image.getScaledInstance(220, -1, 4)));
                                    shot.setCursor(Cursor.getPredefinedCursor(12));
                                    shot.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.3
                                        final /* synthetic */ LobbyUi this$0;

                                        {
                                            Objects.requireNonNull(this);
                                            this.this$0 = this;
                                        }

                                        public void mouseClicked(MouseEvent e) {
                                            JDialog full = new JDialog(this.this$0.host.frame(), String.valueOf(name), false);
                                            JLabel big = new JLabel(new ImageIcon(image));
                                            full.setContentPane(new JScrollPane(big));
                                            full.setSize(Math.min(image.getWidth() + 40, 1500), Math.min(image.getHeight() + 60, 950));
                                            full.setLocationRelativeTo(this.this$0.host.frame());
                                            full.setVisible(true);
                                        }
                                    });
                                    shots.revalidate();
                                });
                            }
                        } catch (IOException | RuntimeException e) {
                            SwingUtilities.invokeLater(() -> {
                                shot.setText("(" + String.valueOf(name) + " not loaded)");
                            });
                        }
                    }, "bug-picture");
                    loader.setDaemon(true);
                    loader.start();
                }
                middle.add(shots);
            }
        }
        JTextField answer = new JTextField();
        chatInputStyle(answer, true);
        answer.putClientProperty("JTextField.placeholderText", "Answer (the reporter sees it as \"Eclipse support\")");
        JButton send = new JButton("Answer");
        JButton close = new JButton(closed ? "Reopen" : "Close");
        BiConsumer<String, Boolean> post = (text, closeIt) -> {
            Thread poster = new Thread(() -> {
                try {
                    postJson(bugsUrl("reply") + "?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8), "{\"id\": " + id + ", \"text\": " + jsonString(text) + ", \"close\": " + closeIt + "}");
                } catch (IOException | RuntimeException e) {
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    return;
                }
                SwingUtilities.invokeLater(reload);
            }, "bug-reply");
            poster.setDaemon(true);
            poster.start();
        };
        send.addActionListener(e -> {
            if (!answer.getText().isBlank()) {
                post.accept(answer.getText().trim(), false);
            }
        });
        answer.addActionListener(e2 -> {
            send.doClick();
        });
        close.addActionListener(e3 -> {
            post.accept("", Boolean.valueOf(!closed));
        });
        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setOpaque(false);
        bottom.add(answer, "Center");
        JPanel buttons = new JPanel(new FlowLayout(2, 6, 0));
        buttons.setOpaque(false);
        buttons.add(send);
        buttons.add(close);
        bottom.add(buttons, "East");
        card.add(head, "North");
        card.add(middle, "Center");
        card.add(bottom, "South");
        card.setMaximumSize(new Dimension(4000, card.getPreferredSize().height + 400));
        return card;
    }

    private void refreshPublicOnline() {
        if (this.client.isLoggedIn()) {
            return;
        }
        String base = mapServiceUrl().replaceAll("/find/?$", "");
        Thread loader = new Thread(() -> {
            Set<String> online = new TreeSet<>((Comparator<? super String>) String.CASE_INSENSITIVE_ORDER);
            Set<String> inGame = new TreeSet<>((Comparator<? super String>) String.CASE_INSENSITIVE_ORDER);
            Object patt0$temp = fetchJson(base + "/stats/online");
            if (patt0$temp instanceof Map) {
                Map<?, ?> map = (Map) patt0$temp;
                Object patt1$temp = map.get("online");
                if (patt1$temp instanceof List) {
                    List<?> names = (List) patt1$temp;
                    names.forEach(name -> {
                        addPlayerName(online, name);
                    });
                }
                Object patt2$temp = map.get("inGame");
                if (patt2$temp instanceof List) {
                    List<?> names2 = (List) patt2$temp;
                    names2.forEach(name2 -> {
                        addPlayerName(inGame, name2);
                    });
                }
                rememberCountries(map.get("countries"));
            }
            if (!online.equals(this.publicOnline) || !inGame.equals(this.publicInGame)) {
                this.publicOnline = online;
                this.publicInGame = inGame;
                SwingUtilities.invokeLater(this::updateOnline);
            }
        }, "public-online");
        loader.setDaemon(true);
        loader.start();
    }

    private void refreshKnownPlayers() {
        String base = mapServiceUrl().replaceAll("/find/?$", "");
        Thread loader = new Thread(() -> {
            Set<String> found = new TreeSet<>((Comparator<? super String>) String.CASE_INSENSITIVE_ORDER);
            Object players = fetchJson(base + "/stats/players");
            if (players instanceof Map) {
                Map<?, ?> map = (Map) players;
                Object patt0$temp = map.get("players");
                if (patt0$temp instanceof List) {
                    List<?> names = (List) patt0$temp;
                    names.forEach(name -> {
                        addPlayerName(found, name);
                    });
                    rememberCountries(map.get("countries"));
                }
            }
            if (found.isEmpty()) {
                Object patt1$temp = fetchJson(base + "/stats/ranks");
                if (patt1$temp instanceof Map) {
                    Map<?, ?> ranks = (Map) patt1$temp;
                    ranks.keySet().forEach(name2 -> {
                        addPlayerName(found, name2);
                    });
                }
            }
            if (!found.isEmpty() && !found.equals(this.knownPlayers)) {
                this.knownPlayers = found;
                SwingUtilities.invokeLater(this::updateOnline);
            }
        }, "known-players");
        loader.setDaemon(true);
        loader.start();
    }

    private void rememberCountries(Object countries) {
        if (countries instanceof Map) {
            Map<?, ?> map = (Map) countries;
            map.forEach((name, country) -> {
                if (String.valueOf(country).matches("[A-Za-z]{2}")) {
                    this.knownCountries.put(String.valueOf(name).toLowerCase(Locale.ROOT), String.valueOf(country));
                }
            });
        }
    }

    private String countryOf(String name) {
        LobbyClient.User user = this.client.getUser(name);
        if (user != null && user.country != null && user.country.matches("[A-Za-z]{2}")) {
            return user.country;
        }
        return this.knownCountries.get(name.toLowerCase(Locale.ROOT));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addPlayerName(Set<String> into, Object name) {
        if (String.valueOf(name).matches("[A-Za-z0-9_\\[\\]]{1,40}")) {
            into.add(String.valueOf(name));
        }
    }

    private static Object fetchJson(String url) {
        try {
            HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8L)).build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return MiniJson.parse((String) response.body());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    JComponent dock() {
        JPanel dock = new JPanel(new BorderLayout());
        dock.setBackground(BarLauncher.BACKGROUND);
        dock.setBorder(BorderFactory.createEmptyBorder(10, 6, 14, 16));
        dock.setPreferredSize(new Dimension(340, 100));
        dock.setMinimumSize(new Dimension(0, 0));
        JPanel chatTab = new JPanel(new BorderLayout(0, 0));
        chatTab.setOpaque(false);
        chatTab.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        installNameClicks(this.roomChat);
        this.channelTabs.setFont(this.channelTabs.getFont().deriveFont(1, 11.0f));
        this.channelTabs.setTabLayoutPolicy(1);
        this.channelTabs.putClientProperty("JTabbedPane.tabInsets", new Insets(4, 8, 4, 6));
        this.channelTabs.putClientProperty("JTabbedPane.tabHeight", 26);
        this.channelTabs.putClientProperty("JTabbedPane.tabClosable", true);
        this.channelTabs.putClientProperty("JTabbedPane.tabCloseToolTipText", "Close");
        this.channelTabs.putClientProperty("JTabbedPane.tabCloseCallback", (tabs, index) -> {
            closeChatTab(index.intValue());
        });
        JButton joinButton = new JButton("+");
        joinButton.setToolTipText("Join a channel");
        joinButton.putClientProperty("JButton.buttonType", "toolBarButton");
        joinButton.addActionListener(e -> {
            joinChannelDialog();
        });
        this.channelTabs.putClientProperty("JTabbedPane.trailingComponent", joinButton);
        this.channelTabs.addChangeListener(e2 -> {
            chatTabSelected();
        });
        openChatTab("#main");
        chatTab.add(this.channelTabs, "Center");
        chatInputStyle(this.chatInput);
        this.chatInput.putClientProperty("JTextField.placeholderText", "Message #main");
        this.chatInput.setToolTipText("/w name message sends a private message, /join name joins a channel");
        this.chatInput.addActionListener(e3 -> {
            sendChat();
        });
        chatTab.add(this.chatInput, "South");
        JPanel friendsTab = new JPanel(new BorderLayout(0, 0));
        friendsTab.setOpaque(false);
        friendsTab.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JList<String> onlineTab = new JList<>(this.onlineModel);
        onlineTab.setCellRenderer(new PersonRenderer(this, false));
        onlineTab.setBackground(BarLauncher.BACKGROUND);
        onlineTab.setFixedCellHeight(34);
        onlineTab.setSelectionMode(0);
        onlineTab.addMouseListener(personMenu(onlineTab, false));
        friendsTab.add(flatScroll(onlineTab), "Center");
        this.friendList.setCellRenderer(new PersonRenderer(this, true));
        this.playerList.setCellRenderer(new PersonRenderer(this, false));
        this.partyPanel.setLayout(new BoxLayout(this.partyPanel, 1));
        this.partyPanel.setOpaque(false);
        JPanel add = new JPanel(new BorderLayout(6, 0));
        add.setOpaque(false);
        chatInputStyle(this.friendInput, true);
        this.friendInput.putClientProperty("JTextField.placeholderText", "Add a friend by name");
        this.friendInput.setToolTipText("Typing filters the list: everybody whose name contains what you typed");
        this.friendInput.getDocument().addDocumentListener(new DocumentListener(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.4
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void insertUpdate(DocumentEvent e4) {
                this.this$0.updateOnline();
            }

            public void removeUpdate(DocumentEvent e4) {
                this.this$0.updateOnline();
            }

            public void changedUpdate(DocumentEvent e4) {
                this.this$0.updateOnline();
            }
        });
        JButton addButton = new JButton("Add");
        Runnable request = () -> {
            if (!this.friendInput.getText().isBlank() && this.client.isLoggedIn()) {
                this.client.requestFriend(this.friendInput.getText());
                append(this.chat, null, "Friend request sent to " + this.friendInput.getText().trim(), BarLauncher.MUTED);
                this.friendInput.setText("");
            }
        };
        addButton.addActionListener(e4 -> {
            request.run();
        });
        this.friendInput.addActionListener(e5 -> {
            request.run();
        });
        add.add(this.friendInput, "Center");
        add.add(addButton, "East");
        friendsTab.add(add, "South");
        this.dockTabs.addTab("ONLINE", friendsTab);
        this.dockTabs.addTab("CHAT", chatTab);
        JLabel partiesTitle = new JLabel("Party");
        partiesTitle.setForeground(BarLauncher.TEXT);
        partiesTitle.setFont(partiesTitle.getFont().deriveFont(0, 19.0f));
        this.createPartyButton.setFont(this.createPartyButton.getFont().deriveFont(12.0f));
        this.createPartyButton.setMargin(new Insets(3, 14, 3, 14));
        this.createPartyButton.setCursor(Cursor.getPredefinedCursor(12));
        BarButtonUi.install((AbstractButton) this.createPartyButton, BarButtonUi.PLAIN);
        this.createPartyButton.addActionListener(e6 -> {
            if (!this.client.isLoggedIn()) {
                showLogin();
            } else if (this.client.getPartyMembers().isEmpty()) {
                this.client.createParty();
            } else {
                this.client.leaveParty();
            }
        });
        this.partiesHead.setOpaque(false);
        this.partiesHead.add(partiesTitle, "West");
        this.partiesHead.add(this.createPartyButton, "East");
        this.partiesHead.setMaximumSize(new Dimension(4000, 34));
        this.partyHint.setFont(new JLabel().getFont());
        this.partyHint.setText("A party plays on one team. Invite somebody below and the party is made for you.");
        this.partyInviteTitle.setForeground(BarLauncher.MUTED);
        this.partyInviteTitle.setFont(this.partyInviteTitle.getFont().deriveFont(1, 11.0f));
        this.partyInviteTitle.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        chatInputStyle(this.partySearch, true);
        this.partySearch.putClientProperty("JTextField.placeholderText", "Find a player to invite");
        this.partySearch.setMaximumSize(new Dimension(4000, 32));
        this.partySearch.getDocument().addDocumentListener(new DocumentListener(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.5
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void insertUpdate(DocumentEvent e7) {
                this.this$0.showPartyCandidates();
            }

            public void removeUpdate(DocumentEvent e7) {
                this.this$0.showPartyCandidates();
            }

            public void changedUpdate(DocumentEvent e7) {
                this.this$0.showPartyCandidates();
            }
        });
        this.partyCandidates.setLayout(new BoxLayout(this.partyCandidates, 1));
        this.partyCandidates.setOpaque(false);
        JPanel partiesTop = new JPanel();
        partiesTop.setLayout(new BoxLayout(partiesTop, 1));
        partiesTop.setOpaque(false);
        Component[] componentArr = {this.partiesHead, this.partyHint, this.partyPanel, this.partyInviteTitle, this.partySearch, this.partyCandidates};
        int[] gaps = {8, 12, 4, 6, 4, 0};
        for (int i = 0; i < componentArr.length; i++) {
            componentArr[i].setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            partiesTop.add(componentArr[i]);
            partiesTop.add(Box.createVerticalStrut(gaps[i]));
        }
        JPanel partiesTab = new JPanel(new BorderLayout());
        partiesTab.setOpaque(false);
        partiesTab.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        partiesTab.add(partiesTop, "North");
        this.dockTabs.addTab("PARTY", partiesTab);
        this.dockTabs.addTab("<html><span style='font-size:12px'>&#9881;</span></html>", buildSettingsTab());
        this.dockTabs.setToolTipTextAt(3, "Settings");
        this.dockTabs.addChangeListener(e7 -> {
            if (this.dockTabs.getSelectedIndex() == 2) {
                this.dockTabs.setTitleAt(2, "PARTY");
                showPartyCandidates();
            }
            if (this.dockTabs.getSelectedIndex() == 1) {
                this.dockTabs.setTitleAt(1, "CHAT");
                chatTabSelected();
            }
            if (this.dockTabs.getSelectedIndex() >= 0 && this.dockTabs.getSelectedIndex() < 3) {
                remember("dock.tab", String.valueOf(this.dockTabs.getSelectedIndex()));
                this.tabBeforeSettings = this.dockTabs.getSelectedIndex();
            } else if (this.dockTabs.getSelectedIndex() == 3) {
                this.settingsOpenedAt = System.currentTimeMillis();
            }
        });
        this.dockTabs.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.6
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mouseClicked(MouseEvent event) {
                if (this.this$0.dockTabs.indexAtLocation(event.getX(), event.getY()) == 3 && this.this$0.dockTabs.getSelectedIndex() == 3 && System.currentTimeMillis() - this.this$0.settingsOpenedAt > 400) {
                    this.this$0.dockTabs.setSelectedIndex(this.this$0.tabBeforeSettings);
                }
            }
        });
        int rememberedTab = settingNumber("dock.tab", 0);
        if (rememberedTab > 0 && rememberedTab < 3) {
            this.dockTabs.setSelectedIndex(rememberedTab);
        }
        this.knownTimer.start();
        this.publicOnlineTimer.start();
        refreshPublicOnline();
        refreshKnownPlayers();
        showParty();
        this.dockTabs.setFont(this.dockTabs.getFont().deriveFont(1, 10.5f));
        this.dockTabs.putClientProperty("JTabbedPane.tabInsets", new Insets(8, 9, 8, 9));
        this.dockTabs.addComponentListener(new ComponentAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.7
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void componentResized(ComponentEvent event) {
                this.this$0.fitDockTabs();
            }
        });
        this.dockTabs.putClientProperty("JTabbedPane.tabWidthMode", "preferred");
        this.dockTabs.putClientProperty("JTabbedPane.tabAreaAlignment", "fill");
        this.dockTabs.putClientProperty("JTabbedPane.scrollButtonsPolicy", "asNeededSingle");
        this.dockTabs.putClientProperty("JTabbedPane.tabsPopupPolicy", "never");
        dock.add(this.dockTabs, "Center");
        return dock;
    }

    private boolean chooseOwnBackground() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Picture for the room background");
        chooser.setFileFilter(new FileNameExtensionFilter("Pictures (JPG, PNG)", new String[]{"jpg", "jpeg", "png"}));
        String before = setting("room.background.file", "");
        if (!before.isEmpty()) {
            chooser.setSelectedFile(new File(before));
        } else {
            String pictures = System.getProperty("user.home") + File.separator + "Pictures";
            if (new File(pictures).isDirectory()) {
                chooser.setCurrentDirectory(new File(pictures));
            }
        }
        if (chooser.showDialog(this.host.frame(), "Use this picture") != 0 || chooser.getSelectedFile() == null || !chooser.getSelectedFile().isFile()) {
            return false;
        }
        this.settings.setProperty("room.background.file", chooser.getSelectedFile().getAbsolutePath());
        saveSettings();
        this.artworkShown = "";
        return true;
    }

    String setting(String key, String fallback) {
        return this.settings.getProperty(key, fallback);
    }

    void remember(String key, String value) {
        if (value == null) {
            this.settings.remove(key);
        } else {
            this.settings.setProperty(key, value);
        }
        saveSettings();
    }

    int settingNumber(String key, int fallback) {
        try {
            return Integer.parseInt(this.settings.getProperty(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    Rectangle savedWindow() {
        String[] parts = this.settings.getProperty("window", "").split(",");
        try {
            if (parts.length == 4) {
                return new Rectangle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Math.max(1280, Integer.parseInt(parts[2])), Math.max(720, Integer.parseInt(parts[3])));
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    void saveWindow(Rectangle bounds) {
        this.settings.setProperty("window", bounds.x + "," + bounds.y + "," + bounds.width + "," + bounds.height);
        saveSettings();
    }

    void displayChanged(String mode, int screen) {
        int i;
        this.settings.setProperty("display", mode);
        if (screen >= 0) {
            this.settings.setProperty("screen", String.valueOf(screen));
        }
        saveSettings();
        if (this.displayChoice.getItemCount() < 3) {
            return;
        }
        this.displayUpdating = true;
        JComboBox<String> jComboBox = this.displayChoice;
        if ("fullscreen".equals(mode)) {
            i = 0;
        } else {
            i = "maximized".equals(mode) ? 1 : 2;
        }
        jComboBox.setSelectedIndex(i);
        this.displayUpdating = false;
    }

    JSplitPane split(final int orientation, JComponent first, JComponent second, String key, int fallback, double resizeWeight) {
        final JSplitPane pane = new JSplitPane(orientation, true, first, second);
        pane.setOpaque(false);
        pane.setBorder((Border) null);
        pane.setOneTouchExpandable(true);
        pane.setDividerSize(9);
        pane.setResizeWeight(resizeWeight);
        pane.putClientProperty("JSplitPane.expandableSide", (Object) null);
        final int saved = settingNumber(key, fallback);
        if (saved >= 0) {
            pane.setDividerLocation(saved);
        } else {
            pane.addComponentListener(new ComponentAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.8
                private boolean placed;

                {
                    Objects.requireNonNull(this);
                }

                public void componentResized(ComponentEvent event) {
                    int length = orientation == 1 ? pane.getWidth() : pane.getHeight();
                    if (!this.placed && length > 0) {
                        this.placed = true;
                        pane.setDividerLocation(Math.max(200, length + saved));
                    }
                }
            });
        }
        Timer save = new Timer(700, e -> {
            int length = orientation == 1 ? pane.getWidth() : pane.getHeight();
            int location = pane.getDividerLocation();
            this.settings.setProperty(key, String.valueOf(saved >= 0 ? location : Math.min(-1, location - length)));
            saveSettings();
        });
        save.setRepeats(false);
        pane.addPropertyChangeListener("dividerLocation", e2 -> {
            if (pane.isShowing()) {
                save.restart();
            }
        });
        return pane;
    }

    void recheckMap() {
        if (this.client.getJoinedBattle() != null && !this.mapReady) {
            this.shownMap = null;
            updateRoom();
        }
    }

    boolean flag(String key, boolean fallback) {
        return Boolean.parseBoolean(this.settings.getProperty(key, String.valueOf(fallback)));
    }

    private void applyChatFont() {
        float size = settingNumber("chatfont", 13);
        for (JTextPane pane : new JTextPane[]{this.chat, this.roomChat}) {
            pane.putClientProperty("JEditorPane.honorDisplayProperties", true);
            pane.setFont(pane.getFont().deriveFont(size));
        }
    }

    private int soundVolume() {
        try {
            return Integer.parseInt(this.settings.getProperty("soundvolume", "70"));
        } catch (NumberFormatException e) {
            return 70;
        }
    }

    private JComponent buildSettingsTab() {
        JPanel rows = new JPanel(new GridBagLayout());
        rows.setOpaque(false);
        int[] row = {0};
        BiConsumer<String, JComponent> add = (text, control) -> {
            GridBagConstraints left = new GridBagConstraints();
            left.gridx = 0;
            left.gridy = row[0];
            left.anchor = 17;
            left.insets = new Insets(7, 0, 7, 10);
            GridBagConstraints right = new GridBagConstraints();
            right.gridx = 1;
            int i = row[0];
            row[0] = i + 1;
            right.gridy = i;
            right.weightx = 1.0d;
            right.fill = 2;
            right.insets = new Insets(7, 0, 7, 0);
            JLabel label = new JLabel(text);
            label.setForeground(BarLauncher.TEXT);
            if (!(control instanceof JCheckBox)) {
                control.setPreferredSize(new Dimension(80, 28));
                control.setMinimumSize(new Dimension(60, 28));
                left.gridwidth = 2;
                left.insets = new Insets(7, 0, 2, 0);
                right.gridx = 0;
                int i2 = row[0];
                row[0] = i2 + 1;
                right.gridy = i2;
                right.gridwidth = 2;
                right.insets = new Insets(0, 0, 8, 0);
                label.setForeground(BarLauncher.MUTED);
            } else {
                label.setMinimumSize(new Dimension(40, 20));
                right.weightx = 0.0d;
                right.fill = 0;
                right.anchor = 13;
                left.weightx = 1.0d;
                left.fill = 2;
            }
            rows.add(label, left);
            rows.add(control, right);
        };
        String[] modes = {"fullscreen", "maximized", "window"};
        this.displayChoice.setModel(new DefaultComboBoxModel(new String[]{"Full screen (borderless)", "Window, maximized", "Window"}));
        this.displayChoice.setSelectedIndex(Math.max(0, Arrays.asList(modes).indexOf(setting("display", "window"))));
        this.displayChoice.setToolTipText("Full screen has no window frame and covers the screen, other programs stay reachable with alt-tab. F11 switches as well.");
        List<String> screens = this.host.screenNames();
        JComboBox<String> screenChoice = new JComboBox<>((String[]) screens.toArray(new String[0]));
        int savedScreen = settingNumber("screen", -1);
        if (savedScreen >= 0 && savedScreen < screens.size()) {
            screenChoice.setSelectedIndex(savedScreen);
        }
        this.displayChoice.addActionListener(e -> {
            if (!this.displayUpdating) {
                this.host.setDisplay(modes[Math.max(0, this.displayChoice.getSelectedIndex())], screens.size() > 1 ? screenChoice.getSelectedIndex() : -1);
            }
        });
        screenChoice.addActionListener(e2 -> {
            this.host.setDisplay(modes[Math.max(0, this.displayChoice.getSelectedIndex())], screenChoice.getSelectedIndex());
        });
        add.accept("Display", this.displayChoice);
        if (screens.size() > 1) {
            add.accept("Monitor", screenChoice);
        }
        JSlider brightness = new JSlider(0, 100, backgroundBrightness());
        brightness.addChangeListener(e3 -> {
            this.host.setBackgroundBrightness(brightness.getValue());
            if (!brightness.getValueIsAdjusting()) {
                this.settings.setProperty("background", String.valueOf(brightness.getValue()));
                saveSettings();
            }
        });
        add.accept("Background brightness", brightness);
        JSlider volume = new JSlider(0, 100, soundVolume());
        volume.addChangeListener(e4 -> {
            if (!volume.getValueIsAdjusting()) {
                this.settings.setProperty("soundvolume", String.valueOf(volume.getValue()));
                saveSettings();
                Sounds.message(volume.getValue());
            }
        });
        add.accept("Notification volume", volume);
        JSlider chatFont = new JSlider(10, 20, Math.max(10, Math.min(20, settingNumber("chatfont", 13))));
        chatFont.addChangeListener(e5 -> {
            this.settings.setProperty("chatfont", String.valueOf(chatFont.getValue()));
            applyChatFont();
            if (!chatFont.getValueIsAdjusting()) {
                saveSettings();
            }
        });
        add.accept("Chat font size", chatFont);
        JComboBox<String> scale = new JComboBox<>(new String[]{"100 %", "110 %", "125 %", "150 %", "175 %"});
        scale.setSelectedItem(settingNumber("uiscale", 100) + " %");
        scale.setToolTipText("Size of the whole interface, used from the next start of the launcher");
        scale.addActionListener(e6 -> {
            this.settings.setProperty("uiscale", String.valueOf(scale.getSelectedItem()).replace(" %", ""));
            saveSettings();
        });
        add.accept("Interface scale (restart)", scale);
        String[] joinStates = {"last", "player", "spectator"};
        JComboBox<String> joinState = new JComboBox<>(new String[]{"Remember last", "Player", "Spectator"});
        joinState.setSelectedIndex(Math.max(0, Arrays.asList(joinStates).indexOf(setting("joinstate", "last"))));
        joinState.addActionListener(e7 -> {
            this.settings.setProperty("joinstate", joinStates[joinState.getSelectedIndex()]);
            saveSettings();
        });
        add.accept("Battle join state", joinState);
        String[] backgrounds = {"eclipse", "battlefield", "wartable", "orbit", "bar", "custom", "none"};
        JComboBox<String> background = new JComboBox<>(new String[]{"Eclipse art (changes per room)", "Eclipse: Battlefield", "Eclipse: War table", "Eclipse: Orbit", "Beyond All Reason artwork", "Own picture...", "None"});
        background.setSelectedIndex(Math.max(0, Arrays.asList(backgrounds).indexOf(setting("room.background", "wartable"))));
        background.addActionListener(e8 -> {
            String picked = backgrounds[background.getSelectedIndex()];
            if (picked.equals("custom") && !chooseOwnBackground()) {
                background.setSelectedIndex(Math.max(0, Arrays.asList(backgrounds).indexOf(setting("room.background", "wartable"))));
                return;
            }
            this.settings.setProperty("room.background", picked);
            saveSettings();
            updateArtwork();
        });
        String ownPicture = setting("room.background.file", "");
        background.setToolTipText(ownPicture.isEmpty() ? "Own picture: any JPG or PNG from your computer" : ownPicture);
        add.accept("Room background", background);
        JComponent barFolderRow = this.host.barFolderSetting();
        if (barFolderRow != null) {
            add.accept("Beyond All Reason folder", barFolderRow);
        }
        final JPasswordField inboxKey = new JPasswordField(setting("bugs.inbox.key", ""));
        inboxKey.setToolTipText("Only for the owner of the server: the key from accounts.sh bugs-key shows the bug reports of the players in the profile menu");
        inboxKey.putClientProperty("JPasswordField.cutCopyAllowed", true);
        inboxKey.getDocument().addDocumentListener(new DocumentListener(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.9
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            private void changed() {
                this.this$0.remember("bugs.inbox.key", new String(inboxKey.getPassword()).trim());
                this.this$0.bugsUnread = 0;
                this.this$0.refreshBugInbox();
            }

            public void insertUpdate(DocumentEvent e9) {
                changed();
            }

            public void removeUpdate(DocumentEvent e9) {
                changed();
            }

            public void changedUpdate(DocumentEvent e9) {
                changed();
            }
        });
        add.accept("Bug reports inbox key", inboxKey);
        JButton hotkeys = new JButton("Shortcut keys...");
        hotkeys.setToolTipText("The keys of the game on a keyboard, and changing single keys");
        hotkeys.addActionListener(e9 -> {
            showKeybinds(true);
        });
        add.accept("Keys of the game", hotkeys);
        Object[][] toggles = {new Object[]{"login.auto", "Sign in automatically", "true", null}, new Object[]{"autolaunch.spectating", "Auto-launch when spectating", "true", null}, new Object[]{"show.flags", "Show country flags", "true", "room"}, new Object[]{"show.ratings", "Show ratings", "true", "room"}, new Object[]{"show.bots", "Show the bots of the server", "false", "users"}, new Object[]{"filter.bots", "Filter bot chatter", "true", null}, new Object[]{"dnd", "Do not disturb", "false", null}, new Object[]{"sound.pm", "Sound: private messages", "true", null}, new Object[]{"sound.ring", "Sound: rings and invites", "true", null}, new Object[]{"sound.friendsonly", "Sounds only from friends", "false", null}, new Object[]{"notify.ingame", "Sounds during a game", "false", null}};
        for (Object[] toggle : toggles) {
            JCheckBox box = new JCheckBox();
            box.setOpaque(false);
            box.setSelected(flag((String) toggle[0], Boolean.parseBoolean((String) toggle[2])));
            box.addActionListener(e10 -> {
                this.settings.setProperty((String) toggle[0], String.valueOf(box.isSelected()));
                if (toggle[0].equals("login.auto") && !box.isSelected()) {
                    this.settings.remove("login");
                }
                saveSettings();
                if ("room".equals(toggle[3]) && this.client.getJoinedBattle() != null) {
                    updateRoom();
                } else if ("users".equals(toggle[3])) {
                    onUsersChanged();
                }
            });
            add.accept((String) toggle[1], box);
        }
        applyChatFont();
        JLabel title = new JLabel("Settings");
        title.setForeground(BarLauncher.TEXT);
        title.setFont(title.getFont().deriveFont(0, 19.0f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, "North");
        top.add(rows, "Center");
        JPanel holder = new WidthTrackingPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(12, 4, 8, 12));
        holder.add(top, "North");
        JScrollPane tab = new JScrollPane(holder);
        tab.setBorder((Border) null);
        tab.setOpaque(false);
        tab.getViewport().setOpaque(false);
        tab.setHorizontalScrollBarPolicy(31);
        tab.getVerticalScrollBar().setUnitIncrement(18);
        this.host.setBackgroundBrightness(backgroundBrightness());
        return tab;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$WidthTrackingPanel.class */
    private static final class WidthTrackingPanel extends JPanel implements Scrollable {
        WidthTrackingPanel(LayoutManager layout) {
            super(layout);
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 18;
        }

        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return Math.max(18, visible.height - 36);
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private int backgroundBrightness() {
        try {
            return Math.max(0, Math.min(100, Integer.parseInt(this.settings.getProperty("background", "75"))));
        } catch (NumberFormatException e) {
            return 75;
        }
    }

    private void notifySound(String from, boolean attention) {
        boolean wanted = Boolean.parseBoolean(this.settings.getProperty(attention ? "sound.ring" : "sound.pm", "true"));
        boolean friendsOnly = Boolean.parseBoolean(this.settings.getProperty("sound.friendsonly", "false"));
        boolean inGame = Boolean.parseBoolean(this.settings.getProperty("notify.ingame", "false"));
        if (!wanted || flag("dnd", false)) {
            return;
        }
        if (!friendsOnly || this.client.getFriends().contains(from)) {
            if (this.host.engineRunning() && !inGame) {
                return;
            }
            if (attention) {
                Sounds.attention(soundVolume());
            } else {
                Sounds.message(soundVolume());
            }
        }
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onRing(String user) {
        SwingUtilities.invokeLater(() -> {
            append(this.chat, null, user + " is ringing you.", BarLauncher.ACCENT);
            notifySound(user, true);
        });
    }

    private void sendChat() {
        String text = this.chatInput.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        if (!this.client.isLoggedIn()) {
            showLogin();
            return;
        }
        Matcher whisper = Pattern.compile("^/(?:w|whisper|msg)\\s+(\\S+)\\s+(.+)$").matcher(text);
        Matcher join = Pattern.compile("^/(?:join|j)\\s+#?(\\S+)$").matcher(text);
        if (whisper.find()) {
            openChatTab("@" + whisper.group(1));
            this.client.sayPrivate(whisper.group(1), whisper.group(2));
        } else if (join.find()) {
            joinChannel(join.group(1));
        } else {
            String key = currentChatKey();
            if (key.startsWith("@")) {
                this.client.sayPrivate(key.substring(1), text);
            } else {
                this.client.say(key.substring(1), text);
            }
        }
        this.chatInput.setText("");
    }

    private JTextPane openChatTab(String key) {
        JTextPane pane = this.chatPanes.get(key);
        if (pane == null) {
            pane = key.equals("#main") ? this.chat : chatPane();
            installNameClicks(pane);
            this.chatPanes.put(key, pane);
            JComponent box = chatScroll(pane);
            box.putClientProperty("chat.key", key);
            if (key.equals("#main")) {
                box.putClientProperty("JTabbedPane.tabClosable", false);
            }
            this.channelTabs.addTab(chatTabTitle(key), box);
            if (key.startsWith("#")) {
                rememberChannels();
            }
        }
        return pane;
    }

    private static String chatTabTitle(String key) {
        return key.startsWith("@") ? key.substring(1) : key;
    }

    private String chatKeyAt(int index) {
        if (index >= 0 && index < this.channelTabs.getTabCount()) {
            JComponent componentAt = this.channelTabs.getComponentAt(index);
            if (componentAt instanceof JComponent) {
                JComponent box = componentAt;
                Object clientProperty = box.getClientProperty("chat.key");
                if (clientProperty instanceof String) {
                    String key = (String) clientProperty;
                    return key;
                }
            }
        }
        return null;
    }

    private String currentChatKey() {
        String key = chatKeyAt(this.channelTabs.getSelectedIndex());
        return key != null ? key : "#main";
    }

    private void selectChatTab(String key) {
        openChatTab(key);
        for (int i = 0; i < this.channelTabs.getTabCount(); i++) {
            if (key.equals(chatKeyAt(i))) {
                this.channelTabs.setSelectedIndex(i);
                return;
            }
        }
    }

    private void closeChatTab(int index) {
        String key = chatKeyAt(index);
        if (key == null || key.equals("#main")) {
            return;
        }
        this.channelTabs.removeTabAt(index);
        this.chatPanes.remove(key);
        if (key.startsWith("#")) {
            if (this.client.isLoggedIn()) {
                this.client.leaveChannel(key.substring(1));
            }
            rememberChannels();
        }
    }

    private void chatTabSelected() {
        int index = this.channelTabs.getSelectedIndex();
        String key = chatKeyAt(index);
        if (key == null) {
            return;
        }
        this.channelTabs.setTitleAt(index, chatTabTitle(key));
        this.chatInput.putClientProperty("JTextField.placeholderText", "Message " + chatTabTitle(key));
    }

    private void markChatUnread(String key) {
        boolean dockShowsChat = this.dockTabs.getTabCount() > 1 && this.dockTabs.getSelectedIndex() == 1;
        for (int i = 0; i < this.channelTabs.getTabCount(); i++) {
            if (key.equals(chatKeyAt(i)) && ((i != this.channelTabs.getSelectedIndex() || !dockShowsChat) && !this.channelTabs.getTitleAt(i).endsWith("●"))) {
                this.channelTabs.setTitleAt(i, this.channelTabs.getTitleAt(i) + " ●");
            }
        }
        if (this.dockTabs.getTabCount() > 1 && !dockShowsChat) {
            this.dockTabs.setTitleAt(1, "CHAT ●");
        }
    }

    private void joinChannelDialog() {
        if (!this.client.isLoggedIn()) {
            showLogin();
        } else {
            joinChannel(JOptionPane.showInputDialog(this.dockTabs, "Channel to join:", "Join a channel", -1));
        }
    }

    private void joinChannel(String name) {
        if (name == null) {
            return;
        }
        String name2 = name.trim().replaceFirst("^#", "");
        if (!name2.matches("[A-Za-z0-9_\\-]{1,40}")) {
            return;
        }
        if (this.client.isLoggedIn()) {
            this.client.joinChannel(name2);
        }
        selectChatTab("#" + name2);
        this.chatInput.requestFocusInWindow();
    }

    private void rememberChannels() {
        remember("chat.channels", (String) this.chatPanes.keySet().stream().filter(key -> {
            return key.startsWith("#") && !key.equals("#main");
        }).map(key2 -> {
            return key2.substring(1);
        }).collect(Collectors.joining(",")));
    }

    private void rejoinChannels() {
        for (String name : setting("chat.channels", "").split(",")) {
            if (!name.isBlank()) {
                this.client.joinChannel(name.trim());
                openChatTab("#" + name.trim());
            }
        }
    }

    private MouseAdapter personMenu(final JList<String> list, final boolean friendsList) {
        return new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.10
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mouseReleased(MouseEvent event) {
                int index = list.locationToIndex(event.getPoint());
                if (index < 0 || !list.getCellBounds(index, index).contains(event.getPoint())) {
                    return;
                }
                list.setSelectedIndex(index);
                String entry = (String) list.getModel().getElementAt(index);
                if (entry.startsWith("#")) {
                    return;
                }
                boolean request = entry.startsWith("?");
                String name = (request || entry.startsWith("~")) ? entry.substring(1) : entry;
                if (event.getClickCount() == 2 && !request) {
                    this.this$0.whisperTo(name);
                    return;
                }
                if (!SwingUtilities.isRightMouseButton(event) && !request) {
                    return;
                }
                JPopupMenu menu = new JPopupMenu();
                if (request) {
                    menu.add(LobbyUi.item("Accept friend request", () -> {
                        this.this$0.client.answerFriendRequest(name, true);
                    }));
                    menu.add(LobbyUi.item("Decline", () -> {
                        this.this$0.client.answerFriendRequest(name, false);
                    }));
                } else {
                    menu.add(LobbyUi.item("Message", () -> {
                        this.this$0.whisperTo(name);
                    }));
                    LobbyClient.Battle battle = this.this$0.battleOf(name);
                    if (battle != null) {
                        menu.add(LobbyUi.item("Join their game", () -> {
                            this.this$0.join(battle);
                        }));
                    }
                    if (friendsList) {
                        menu.add(LobbyUi.item("Remove friend", () -> {
                            this.this$0.client.removeFriend(name);
                        }));
                    } else if (!this.this$0.client.getFriends().contains(name) && !name.equalsIgnoreCase(this.this$0.client.getUserName())) {
                        menu.add(LobbyUi.item("Add friend", () -> {
                            this.this$0.client.requestFriend(name);
                        }));
                    }
                    this.this$0.addSocialItems(menu, name);
                }
                menu.show(list, event.getX(), event.getY());
            }
        };
    }

    private void addSocialItems(JPopupMenu menu, String name) {
        if (name.equalsIgnoreCase(this.client.getUserName())) {
            return;
        }
        LobbyClient.User user = this.client.getUser(name);
        if (user != null && !user.bot()) {
            Stream<String> stream = this.client.getPartyMembers().stream();
            Objects.requireNonNull(name);
            if (stream.noneMatch(name::equalsIgnoreCase)) {
                menu.add(item("Invite to Party", () -> {
                    this.client.inviteToParty(name);
                    append(this.chat, null, "Party invitation sent to " + name + ".", BarLauncher.MUTED);
                }));
            }
        }
        menu.add(item("Report User", () -> {
            showReport(name);
        }));
        menu.add(item(noteOf(name).isEmpty() ? "Add Player Notes" : "Edit Player Notes", () -> {
            showNotes(name);
        }));
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

    private Path notesFile() {
        return this.host.home().resolve("player-notes.properties");
    }

    private String noteOf(String name) {
        if (!this.playerNotesLoaded) {
            this.playerNotesLoaded = true;
            if (Files.isRegularFile(notesFile(), new LinkOption[0])) {
                try {
                    Reader reader = Files.newBufferedReader(notesFile());
                    try {
                        this.playerNotes.load(reader);
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
        }
        return this.playerNotes.getProperty(name.toLowerCase(Locale.ROOT), "");
    }

    private void showNotes(String name) {
        JTextArea area = new JTextArea(noteOf(name), 7, 34);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        if (JOptionPane.showConfirmDialog(this.host.frame(), new Object[]{"Your notes about " + name + " (only you can see them)", new JScrollPane(area)}, "Player Notes", 2, -1) != 0) {
            return;
        }
        if (area.getText().isBlank()) {
            this.playerNotes.remove(name.toLowerCase(Locale.ROOT));
        } else {
            this.playerNotes.setProperty(name.toLowerCase(Locale.ROOT), area.getText().trim());
        }
        try {
            Writer writer = Files.newBufferedWriter(notesFile(), new OpenOption[0]);
            try {
                this.playerNotes.store(writer, "Notes about players, kept on this computer only");
                if (writer != null) {
                    writer.close();
                }
            } catch (Throwable th) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            append(this.chat, null, "The note could not be saved: " + e.getMessage(), ERROR);
        }
        updateRoom();
        this.playerList.repaint();
        this.friendList.repaint();
    }

    private void showReport(String name) {
        JComboBox<String> kind = new JComboBox<>(new String[]{"Cheating", "Griefing or team killing", "Abusive chat", "Leaving games on purpose", "Smurfing", "Something else"});
        JTextArea area = new JTextArea(5, 34);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        if (JOptionPane.showConfirmDialog(this.host.frame(), new Object[]{"Report " + name + " to the owner of the server", kind, "What happened?", new JScrollPane(area)}, "Report User", 2, -1) != 0) {
            return;
        }
        String text = "[" + String.valueOf(kind.getSelectedItem()) + "] " + area.getText().replaceAll("\\s+", " ").trim();
        if (this.client.report(name, text.length() > 500 ? text.substring(0, 500) : text)) {
            append(this.chat, null, "Report about " + name + " sent.", BarLauncher.MUTED);
        } else {
            append(this.chat, null, "The report could not be sent: no room of the server is online.", ERROR);
        }
    }

    private void showParty() {
        this.partyPanel.removeAll();
        List<String> members = this.client.getPartyMembers();
        List<String> invited = this.client.getPartyInvited();
        Map<String, List<String>> invitations = this.client.getPartyInvitations();
        invitations.forEach((party, people) -> {
            String str;
            JPanel strip = new BarLauncher.Surface(new BorderLayout(8, 0), new Color(30, 44, 62), 12);
            strip.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 7));
            if (people.isEmpty()) {
                str = "Somebody";
            } else {
                str = escape((String) people.getFirst()) + (people.size() > 1 ? " and " + (people.size() - 1) + " more" : "");
            }
            String who = str;
            JLabel text = new JLabel("<html><b>" + who + "</b> <span style='color:#aab2c0'>invite" + (people.size() == 1 ? "s" : "") + " you</span></html>");
            text.setForeground(BarLauncher.TEXT);
            JButton accept = smallButton("Accept", () -> {
                this.client.answerPartyInvitation(party, true);
            });
            BarButtonUi.install((AbstractButton) accept, BarButtonUi.POSITIVE);
            JButton decline = smallButton("Decline", () -> {
                this.client.answerPartyInvitation(party, false);
            });
            JPanel buttons = new JPanel(new FlowLayout(2, 6, 0));
            buttons.setOpaque(false);
            buttons.add(accept);
            buttons.add(decline);
            strip.add(text, "Center");
            strip.add(buttons, "East");
            strip.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            strip.setMaximumSize(new Dimension(4000, 44));
            this.partyPanel.add(strip);
            this.partyPanel.add(Box.createVerticalStrut(8));
        });
        if (!members.isEmpty()) {
            members.sort(Comparator.comparing(name -> {
                return Boolean.valueOf(!name.equalsIgnoreCase(this.client.getUserName()));
            }).thenComparing(String.CASE_INSENSITIVE_ORDER));
            JLabel title = new JLabel("YOUR PARTY  ·  " + members.size());
            title.setForeground(BarLauncher.MUTED);
            title.setFont(title.getFont().deriveFont(1, 11.0f));
            title.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 0));
            title.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            this.partyPanel.add(title);
            for (String member : members) {
                this.partyPanel.add(partyRow(member, false));
            }
            for (String name2 : invited) {
                this.partyPanel.add(partyRow(name2, true));
            }
        }
        this.partyPanel.revalidate();
        this.partyPanel.repaint();
        boolean inParty = !members.isEmpty();
        this.createPartyButton.setText(inParty ? "Leave party" : "Create party");
        this.partyHint.setText(inParty ? "Join a room together and press Balance: the party ends up on one team." : "A party plays on one team. Invite somebody below and the party is made for you.");
        showPartyCandidates();
        if (this.dockTabs.getTabCount() > 3 && !invitations.isEmpty() && this.dockTabs.getSelectedIndex() != 3) {
            this.dockTabs.setTitleAt(2, "PARTY ●");
        }
    }

    private JComponent partyRow(String name, boolean pending) {
        String str;
        String str2;
        String str3;
        LobbyClient.User user = this.client.getUser(name);
        boolean me = name.equalsIgnoreCase(this.client.getUserName());
        if (pending) {
            str2 = "<span style='color:#6b7280'>○</span>";
        } else {
            if (user == null) {
                str = "#6b7280";
            } else if (user.inGame()) {
                str = "#ff5a4f";
            } else {
                str = user.away() ? "#f2c230" : "#3ab054";
            }
            str2 = "<span style='color:" + str + "'>●</span>";
        }
        String dot = str2;
        LobbyClient.Battle where = (pending || me) ? null : battleOf(name);
        if (pending) {
            str3 = "invited";
        } else if (me) {
            str3 = "you";
        } else if (where != null) {
            str3 = where.title;
        } else if (user == null) {
            str3 = "offline";
        } else if (user.inGame()) {
            str3 = "in game";
        } else {
            str3 = user.away() ? "away" : "";
        }
        String note = str3;
        JLabel label = new JLabel("<html>" + dot + "&nbsp; " + (pending ? escape(name) : "<b>" + escape(name) + "</b>") + (pending ? "" : badgeHtml(name)) + (note.isEmpty() ? "" : " &nbsp;<span style='color:#8c94a3'>" + escape(note) + "</span>") + "</html>");
        label.setForeground(pending ? BarLauncher.MUTED : BarLauncher.TEXT);
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 2));
        row.add(label, "Center");
        if (pending) {
            JButton cancel = smallButton("✕", () -> {
                this.client.cancelPartyInvitation(name);
            });
            cancel.setToolTipText("Take the invitation back");
            cancel.setMargin(new Insets(1, 8, 1, 8));
            row.add(cancel, "East");
        }
        row.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        row.setMaximumSize(new Dimension(4000, 30));
        return row;
    }

    private void showPartyCandidates() {
        String str;
        this.partyCandidates.removeAll();
        String typed = this.partySearch.getText().trim().toLowerCase(Locale.ROOT);
        List<String> members = this.client.getPartyMembers();
        List<String> invited = this.client.getPartyInvited();
        List<String> names = new ArrayList<>();
        for (String friend : this.client.getFriends()) {
            if (this.client.getUser(friend) != null) {
                names.add(friend);
            }
        }
        for (LobbyClient.User user : this.client.getUsers()) {
            if (!user.bot()) {
                Stream<String> stream = names.stream();
                String str2 = user.name;
                Objects.requireNonNull(str2);
                if (stream.noneMatch(str2::equalsIgnoreCase)) {
                    names.add(user.name);
                }
            }
        }
        names.removeIf(name -> {
            if (!name.equalsIgnoreCase(this.client.getUserName())) {
                Stream stream2 = members.stream();
                Objects.requireNonNull(name);
                if (!stream2.anyMatch(name::equalsIgnoreCase)) {
                    Stream stream3 = invited.stream();
                    Objects.requireNonNull(name);
                    if (!stream3.anyMatch(name::equalsIgnoreCase) && (typed.isEmpty() || name.toLowerCase(Locale.ROOT).contains(typed))) {
                        return false;
                    }
                }
            }
            return true;
        });
        if (names.isEmpty()) {
            if (this.client.isLoggedIn()) {
                str = typed.isEmpty() ? "Nobody else is online right now" : "Nobody online is called that";
            } else {
                str = "Sign in to invite people";
            }
            JLabel none = new JLabel(str);
            none.setForeground(BarLauncher.MUTED);
            none.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 0));
            none.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            this.partyCandidates.add(none);
        }
        int shown = 0;
        for (String name2 : names) {
            int i = shown;
            shown++;
            if (i == 8) {
                JLabel more = new JLabel((names.size() - 8) + " more - type a name to find them");
                more.setForeground(BarLauncher.MUTED);
                more.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 0));
                more.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                this.partyCandidates.add(more);
                break;
            }
            JLabel label = new JLabel("<html>" + (this.client.getFriends().contains(name2) ? "<span style='color:#f0c040'>★</span> " : "") + escape(name2) + badgeHtml(name2) + "</html>");
            label.setForeground(BarLauncher.TEXT);
            JButton invite = smallButton("Invite", () -> {
                if (!this.client.isLoggedIn()) {
                    showLogin();
                } else {
                    this.client.inviteToParty(name2);
                    this.partySearch.setText("");
                }
            });
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 2));
            row.add(label, "Center");
            row.add(invite, "East");
            row.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            row.setMaximumSize(new Dimension(4000, 30));
            this.partyCandidates.add(row);
        }
        this.partyCandidates.revalidate();
        this.partyCandidates.repaint();
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onPartyChanged() {
        SwingUtilities.invokeLater(() -> {
            if (System.getProperty("neroxis.launcher.acceptparty") != null) {
                this.client.getPartyInvitations().keySet().stream().findFirst().ifPresent(party -> {
                    this.client.answerPartyInvitation(party, true);
                });
            }
            int invitations = this.client.getPartyInvitations().size();
            if (invitations > this.shownInvitations) {
                notifySound("", true);
                append(this.chat, null, "You were invited to a party: open PARTY to answer.", BarLauncher.ACCENT);
            }
            this.shownInvitations = invitations;
            showParty();
            if (this.client.getJoinedBattle() != null) {
                updateRoom();
            }
        });
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$Account.class */
    private static final class Account extends Record {
        private final boolean prime;
        private final boolean lifetime;
        private final long until;
        private final String source;
        private final String creator;
        private final boolean primeRequired;
        private final String checkout;
        private final String checkoutPaypal;
        private final double price;
        private final String creatorCode;
        private final int creatorSales;
        private final double creatorEarned;
        private final double creatorPaid;

        private Account(boolean prime, boolean lifetime, long until, String source, String creator, boolean primeRequired, String checkout, String checkoutPaypal, double price, String creatorCode, int creatorSales, double creatorEarned, double creatorPaid) {
            this.prime = prime;
            this.lifetime = lifetime;
            this.until = until;
            this.source = source;
            this.creator = creator;
            this.primeRequired = primeRequired;
            this.checkout = checkout;
            this.checkoutPaypal = checkoutPaypal;
            this.price = price;
            this.creatorCode = creatorCode;
            this.creatorSales = creatorSales;
            this.creatorEarned = creatorEarned;
            this.creatorPaid = creatorPaid;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Account.class), Account.class, "prime;lifetime;until;source;creator;primeRequired;checkout;checkoutPaypal;price;creatorCode;creatorSales;creatorEarned;creatorPaid", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->prime:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->lifetime:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->until:J", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->source:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creator:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->primeRequired:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->checkout:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->checkoutPaypal:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->price:D", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorCode:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorSales:I", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorEarned:D", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorPaid:D").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Account.class), Account.class, "prime;lifetime;until;source;creator;primeRequired;checkout;checkoutPaypal;price;creatorCode;creatorSales;creatorEarned;creatorPaid", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->prime:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->lifetime:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->until:J", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->source:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creator:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->primeRequired:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->checkout:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->checkoutPaypal:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->price:D", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorCode:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorSales:I", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorEarned:D", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorPaid:D").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Account.class, Object.class), Account.class, "prime;lifetime;until;source;creator;primeRequired;checkout;checkoutPaypal;price;creatorCode;creatorSales;creatorEarned;creatorPaid", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->prime:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->lifetime:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->until:J", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->source:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creator:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->primeRequired:Z", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->checkout:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->checkoutPaypal:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->price:D", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorCode:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorSales:I", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorEarned:D", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$Account;->creatorPaid:D").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public boolean prime() {
            return this.prime;
        }

        public boolean lifetime() {
            return this.lifetime;
        }

        public long until() {
            return this.until;
        }

        public String source() {
            return this.source;
        }

        public String creator() {
            return this.creator;
        }

        public boolean primeRequired() {
            return this.primeRequired;
        }

        public String checkout() {
            return this.checkout;
        }

        public String checkoutPaypal() {
            return this.checkoutPaypal;
        }

        public double price() {
            return this.price;
        }

        public String creatorCode() {
            return this.creatorCode;
        }

        public int creatorSales() {
            return this.creatorSales;
        }

        public double creatorEarned() {
            return this.creatorEarned;
        }

        public double creatorPaid() {
            return this.creatorPaid;
        }

        static Account parse(String json) {
            return new Account(value(json, "prime").equals("true"), value(json, "lifetime").equals("true"), number(value(json, "until")).longValue(), value(json, "source"), value(json, "creator"), value(json, "primeRequired").equals("true"), value(json, "checkout"), value(json, "checkoutPaypal"), number(value(json, "price")).doubleValue(), value(json, "code"), number(value(json, "sales")).intValue(), number(value(json, "earned")).doubleValue(), number(value(json, "paidOut")).doubleValue());
        }

        private static String value(String json, String key) {
            Matcher matcher = Pattern.compile("\"" + key + "\":\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|[^,}\\s]+)").matcher(json);
            if (!matcher.find()) {
                return "";
            }
            String text = matcher.group(2) != null ? matcher.group(2) : matcher.group(1);
            return text.equals("null") ? "" : text.replace("\\/", "/");
        }

        private static Double number(String text) {
            try {
                return Double.valueOf(Double.parseDouble(text));
            } catch (NumberFormatException e) {
                return Double.valueOf(0.0d);
            }
        }
    }

    String badgeHtml(String name) {
        String kind = name == null ? null : this.badges.get(name.toLowerCase(Locale.ROOT));
        if (kind == null) {
            return "";
        }
        return " <span style='color:" + (kind.equals("gold") ? "#f0c040" : "#3b9dff") + "'>✔</span>";
    }

    Color badgeColour(String name) {
        String kind = name == null ? null : this.badges.get(name.toLowerCase(Locale.ROOT));
        if (kind == null) {
            return null;
        }
        return kind.equals("gold") ? new Color(240, 192, 64) : new Color(59, 157, 255);
    }

    private void refreshBadges() {
        if (!this.client.isLoggedIn()) {
            return;
        }
        String url = mapServiceUrl().replaceAll("/find/?$", "") + "/account/badges";
        Thread loader = new Thread(() -> {
            try {
                try {
                    HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8L)).build(), HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) {
                        return;
                    }
                    Map<String, String> found = new HashMap<>();
                    String[] arr$ = {"blue", "gold"};
                    for (String kind : arr$) {
                        Matcher list = Pattern.compile("\"" + kind + "\":\\s*\\[([^\\]]*)\\]").matcher((CharSequence) response.body());
                        if (list.find()) {
                            Matcher name = Pattern.compile("\"([^\"]+)\"").matcher(list.group(1));
                            while (name.find()) {
                                found.put(name.group(1).toLowerCase(Locale.ROOT), kind);
                            }
                        }
                    }
                    if (!found.equals(this.badges)) {
                        String own = this.client.getUserName() == null ? "" : this.client.getUserName().toLowerCase(Locale.ROOT);
                        boolean mine = !String.valueOf(found.get(own)).equals(String.valueOf(this.badges.get(own)));
                        this.badges = found;
                        SwingUtilities.invokeLater(() -> {
                            if (mine) {
                                refreshAccount(null);
                            }
                            updateBadge();
                            this.playerList.repaint();
                            this.friendList.repaint();
                            this.host.lobbyChanged();
                            if (this.client.getJoinedBattle() != null) {
                                updateRoom();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException | RuntimeException e2) {
            }
        }, "badges");
        loader.setDaemon(true);
        loader.start();
    }

    private void refreshAccount(Runnable after) {
        refreshBadges();
        if (!this.badgeTimer.isRunning()) {
            this.badgeTimer.start();
        }
        if (!this.statusTimer.isRunning()) {
            Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
                IdleTime.touched();
            }, 56L);
            this.statusTimer.start();
        }
        String user = this.client.getUserName();
        if (user == null || !this.client.isLoggedIn()) {
            return;
        }
        String url = mapServiceUrl().replaceAll("/find/?$", "") + "/account?user=" + URLEncoder.encode(user, StandardCharsets.UTF_8);
        Thread loader = new Thread(() -> {
            try {
                HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8L)).build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200 && ((String) response.body()).contains("\"prime\"")) {
                    this.account = Account.parse((String) response.body());
                }
            } catch (IOException | RuntimeException e) {
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }
            SwingUtilities.invokeLater(() -> {
                showAccount();
                greetAccount();
                celebratePrime();
                enterReferral();
                if (after != null) {
                    after.run();
                }
            });
        }, "account");
        loader.setDaemon(true);
        loader.start();
    }

    private void enterReferral() {
        Account current = this.account;
        Path file = this.host.home().resolve("referral.txt");
        if (current == null || !Files.isRegularFile(file, new LinkOption[0])) {
            return;
        }
        try {
            String code = Files.readString(file).trim();
            if (!current.creator().isEmpty() || code.equalsIgnoreCase(current.creatorCode()) || !code.matches("[A-Za-z0-9_-]{3,32}") || this.client.redeem(code)) {
                Files.delete(file);
            }
        } catch (IOException | RuntimeException e) {
        }
    }

    private void celebratePrime() {
        String str;
        Account current = this.account;
        String user = this.client.getUserName();
        if (current == null || user == null) {
            return;
        }
        if (current.lifetime()) {
            str = "2";
        } else {
            str = (!current.prime() || current.source().equals("trial") || current.source().equals("expired")) ? "0" : "1";
        }
        String level = str;
        String key = "prime.level." + user.toLowerCase(Locale.ROOT);
        String before = setting(key, "");
        if (!level.equals(before)) {
            this.settings.setProperty(key, level);
            saveSettings();
        }
        if (!before.isEmpty() && level.compareTo(before) > 0) {
            Confetti.burst(this.host.frame(), true);
            append(this.chat, null, level.equals("2") ? "Prime for life. Welcome to the gold club!" : "You are Eclipse Prime now. Thank you for supporting Eclipse!", ONLINE);
        }
    }

    private void greetAccount() {
        Account current = this.account;
        String user = this.client.getUserName();
        if (current != null && user != null && current.primeRequired()) {
            if ((System.getProperty("neroxis.launcher.screenshot") != null && System.getProperty("neroxis.launcher.greet") == null) || user.equalsIgnoreCase(this.greetedUser)) {
                return;
            }
            this.greetedUser = user;
            String key = "trial.welcomed." + user.toLowerCase(Locale.ROOT);
            String until = new SimpleDateFormat("d MMMM yyyy").format(new Date(current.until() * 1000));
            long daysLeft = ((current.until() * 1000) - System.currentTimeMillis()) / 86400000;
            if (current.prime() && current.source().equals("trial") && !flag(key, false)) {
                this.settings.setProperty(key, "true");
                saveSettings();
                showTrialWelcome(user, until, "$" + String.format(Locale.ROOT, "%.0f", Double.valueOf(current.price())));
            } else if (current.prime() && current.source().equals("trial") && daysLeft <= 7) {
                append(this.chat, null, "Your free time on Eclipse ends " + until + ". Prime keeps you playing: your name, View profile.", BUSY);
            } else if (!current.prime()) {
                Object[] choices = {"Get Prime", "Later"};
                if (JOptionPane.showOptionDialog(this.host.frame(), "<html><div style='width:340px'>Your free time on Eclipse ended " + until + ".<br><br>Eclipse Prime is $" + String.format(Locale.ROOT, "%.0f", Double.valueOf(current.price())) + " a month and keeps you playing: games on generated maps and the rooms of this server.</div></html>", "Prime account", -1, 1, (Icon) null, choices, choices[0]) == 0) {
                    showProfile(3);
                }
            }
        }
    }

    boolean requirePrime(String what) {
        Account current = this.account;
        if (current == null || !current.primeRequired() || current.prime()) {
            return true;
        }
        Object[] choices = {"Get Prime", "Cancel"};
        if (JOptionPane.showOptionDialog(this.host.frame(), "You need a Prime account to " + what + " on Eclipse.\nYour free time is over. Prime is the access to the custom map generator and its rooms;\nBeyond All Reason itself stays free on the official servers.", "Prime account", -1, 1, (Icon) null, choices, choices[0]) == 0) {
            showProfile(3);
            return false;
        }
        return false;
    }

    private void showAccount() {
        int i;
        String str;
        String str2;
        Account current = this.account;
        this.paypalButton.setVisible((current == null || current.lifetime() || current.checkoutPaypal().isBlank()) ? false : true);
        boolean paid = (current == null || !current.prime() || current.source().equals("trial")) ? false : true;
        if (current == null || !current.lifetime()) {
            i = paid ? 1 : 0;
        } else {
            i = 2;
        }
        int level = i;
        this.primeBadge.setLevel(level);
        this.primeCaption.setVisible(level != 0);
        this.profilePrime.setVisible(level == 0);
        this.primeExplanation.setVisible(level == 0);
        this.primeButton.setVisible(level != 2);
        if (current != null && level != 0) {
            this.primeCaption.setText(level == 2 ? "Prime for life" : "Prime until " + new SimpleDateFormat("d MMMM yyyy").format(new Date(current.until() * 1000)));
        }
        JButton jButton = this.primeButton;
        if (current == null || !current.lifetime()) {
            str = paid ? "★  Primed" : "★  Prime";
        } else {
            str = "★  Primed for life";
        }
        jButton.setText(str);
        this.primeButton.setCursor(Cursor.getPredefinedCursor((current == null || !current.lifetime()) ? 12 : 0));
        this.profileName.setText(this.client.getUserName() != null ? this.client.getUserName() : " ");
        if (current == null) {
            this.profilePrime.setText("<html><span style='color:#8c94a3'>This server has no accounts service, everybody plays for free.</span></html>");
            this.profileCreator.setText(" ");
            return;
        }
        String until = new SimpleDateFormat("d MMMM yyyy").format(new Date(current.until() * 1000));
        if (current.lifetime()) {
            str2 = "<b style='color:#f0c040'>★ Prime</b> for life";
        } else if (!current.prime()) {
            str2 = "<b style='color:#ff6054'>No Prime</b>, your free time ended " + until;
        } else if (current.source().equals("trial")) {
            str2 = "<b style='color:#3ab054'>Free time</b> until " + until + ", every new account starts with three months";
        } else {
            str2 = "<b style='color:#f0c040'>★ Prime</b> until " + until;
        }
        String state = str2;
        this.profilePrime.setText("<html><div style='width:330px'>" + state + (current.creator().isEmpty() ? "" : "<br><span style='color:#8c94a3'>Supporting the creator code " + current.creator() + "</span>") + "</div></html>");
        this.profileCreator.setText(current.creatorCode().isEmpty() ? " " : "<html><div style='width:330px'><b>Your creator code: " + current.creatorCode() + "</b><br>" + current.creatorSales() + " payments with your code, " + String.format(Locale.ROOT, "$%.2f earned, $%.2f paid out", Double.valueOf(current.creatorEarned()), Double.valueOf(current.creatorPaid())) + "</div></html>");
    }

    void previewProfile(Map<String, Object> stats, int tab) {
        this.previewName = String.valueOf(stats.get("name"));
        this.playerStats = stats;
        showProfile(tab);
    }

    void showProfile() {
        showProfile(-1);
    }

    void showProfile(int tab) {
        if (!this.client.isLoggedIn() && this.previewName == null) {
            showLogin();
            return;
        }
        if (this.profileDialog == null) {
            this.profileDialog = new JDialog(this.host.frame(), "Profile", System.getProperty("neroxis.launcher.screenshot") == null);
            this.profileName.setFont(this.profileName.getFont().deriveFont(0, 26.0f));
            this.profileName.setForeground(BarLauncher.TEXT);
            this.profilePrime.setForeground(BarLauncher.TEXT);
            this.profileCreator.setForeground(BarLauncher.TEXT);
            JButton prime = this.primeButton;
            prime.setBackground(BarLauncher.ACCENT);
            prime.setForeground(new Color(25, 18, 8));
            prime.setFont(prime.getFont().deriveFont(1, 15.0f));
            prime.addActionListener(e -> {
                if (this.account == null || !this.account.lifetime()) {
                    openCheckout(false);
                }
            });
            this.paypalButton.setBackground(new Color(0, 112, 186));
            this.paypalButton.setForeground(Color.WHITE);
            this.paypalButton.setFont(this.paypalButton.getFont().deriveFont(1, 13.0f));
            this.paypalButton.addActionListener(e2 -> {
                openCheckout(true);
            });
            JButton jButton = this.primeExplanation;
            jButton.setText("<html><div style='width:330px'>Prime is your access to Eclipse: games on custom generated maps, the rooms and everything around them. $10 a month, it starts when your free time is over. Beyond All Reason itself stays free on the official servers.</div></html>");
            jButton.setForeground(BarLauncher.MUTED);
            JTextField code = new JTextField();
            code.putClientProperty("JTextField.placeholderText", "Promo code or creator code");
            JButton redeem = new JButton("Redeem");
            Runnable send = () -> {
                String text = code.getText().trim();
                if (!text.matches("[A-Za-z0-9_-]{3,32}")) {
                    this.profileStatus.setForeground(BUSY);
                    this.profileStatus.setText("Codes have 3 to 32 letters and digits");
                } else if (this.client.redeem(text)) {
                    this.profileStatus.setForeground(BarLauncher.MUTED);
                    this.profileStatus.setText("Checking the code...");
                    code.setText("");
                } else {
                    this.profileStatus.setForeground(ERROR);
                    this.profileStatus.setText("No room of the server is online to check the code");
                }
            };
            redeem.addActionListener(e3 -> {
                send.run();
            });
            code.addActionListener(e4 -> {
                send.run();
            });
            JButton jPanel = new JPanel(new BorderLayout(8, 0));
            jPanel.setOpaque(false);
            jPanel.add(code, "Center");
            jPanel.add(redeem, "East");
            JButton jLabel = new JLabel("Password");
            jLabel.setForeground(BarLauncher.MUTED);
            JButton password = new JButton("Change password");
            password.setToolTipText("Sends a link to the email address of your account");
            password.addActionListener(e5 -> {
                openResetPage(this.profileDialog);
            });
            password.setMaximumSize(password.getPreferredSize());
            JPanel accountTab = new JPanel();
            accountTab.setLayout(new BoxLayout(accountTab, 1));
            accountTab.setBackground(BarLauncher.BACKGROUND);
            accountTab.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
            int[] gaps = {18, 6, 6, 18, 8, 16, 24, 6};
            JButton jPanel2 = new JPanel();
            jPanel2.setLayout(new BoxLayout(jPanel2, 1));
            jPanel2.setOpaque(false);
            this.primeBadge.setAlignmentX(0.5f);
            this.primeCaption.setAlignmentX(0.5f);
            this.primeCaption.setForeground(BarLauncher.TEXT);
            this.primeCaption.setFont(this.primeCaption.getFont().deriveFont(1, 17.0f));
            this.profilePrime.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            jPanel2.add(this.primeBadge);
            jPanel2.add(Box.createVerticalStrut(6));
            jPanel2.add(this.primeCaption);
            jPanel2.add(this.profilePrime);
            jPanel2.setMaximumSize(new Dimension(4000, 230));
            JButton[] jButtonArr = {jPanel2, prime, this.paypalButton, jButton, jPanel, this.profileStatus, this.profileCreator, jLabel, password};
            for (int i = 0; i < jButtonArr.length; i++) {
                jButtonArr[i].setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
                if (jButtonArr[i] == prime || jButtonArr[i] == this.paypalButton || jButtonArr[i] == jPanel) {
                    jButtonArr[i].setMaximumSize(new Dimension(4000, jButtonArr[i] == prime ? 46 : 36));
                }
                accountTab.add(jButtonArr[i]);
                if (i < gaps.length) {
                    accountTab.add(Box.createVerticalStrut(gaps[i]));
                }
            }
            this.rankPanel.setLayout(new BoxLayout(this.rankPanel, 1));
            this.rankPanel.setBackground(BarLauncher.BACKGROUND);
            this.rankPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
            this.statsPanel.setLayout(new BoxLayout(this.statsPanel, 1));
            this.statsPanel.setBackground(BarLauncher.BACKGROUND);
            this.statsPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            JScrollPane statsScroll = new JScrollPane(this.statsPanel);
            statsScroll.setBorder(BorderFactory.createEmptyBorder());
            statsScroll.getVerticalScrollBar().setUnitIncrement(16);
            statsScroll.getViewport().setBackground(BarLauncher.BACKGROUND);
            JTabbedPane tabs = new JTabbedPane();
            this.profileTabs = tabs;
            tabs.addTab("Trophy room", this.rankPanel);
            tabs.addTab("Stats", statsScroll);
            JPanel boardTab = new WidthTrackingPanel(new BorderLayout(0, 10));
            boardTab.setBackground(BarLauncher.BACKGROUND);
            boardTab.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
            JPanel boardSwitch = new JPanel(new GridLayout(1, 2, 8, 0));
            boardSwitch.setOpaque(false);
            JToggleButton globalBoard = new JToggleButton("Global", true);
            JToggleButton duelBoard = new JToggleButton("1v1");
            ButtonGroup boards = new ButtonGroup();
            boards.add(globalBoard);
            boards.add(duelBoard);
            globalBoard.addActionListener(e6 -> {
                loadLeaderboard("global");
            });
            duelBoard.addActionListener(e7 -> {
                loadLeaderboard("1v1");
            });
            boardSwitch.add(globalBoard);
            boardSwitch.add(duelBoard);
            boardTab.add(boardSwitch, "North");
            this.leaderboard.setVerticalAlignment(1);
            boardTab.add(this.leaderboard, "Center");
            JScrollPane boardScroll = new JScrollPane(boardTab);
            boardScroll.setBorder(BorderFactory.createEmptyBorder());
            boardScroll.getVerticalScrollBar().setUnitIncrement(16);
            boardScroll.getViewport().setBackground(BarLauncher.BACKGROUND);
            tabs.insertTab("Leaderboard", (Icon) null, boardScroll, (String) null, 2);
            tabs.addChangeListener(e8 -> {
                if (tabs.getSelectedIndex() == 2) {
                    loadLeaderboard(this.leaderboardBoard);
                }
            });
            JPanel content = new JPanel(this.profileCards);
            content.setBackground(BarLauncher.BACKGROUND);
            content.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            content.add(tabs, "profile");
            content.add(accountTab, "account");
            this.profileContent = content;
            this.profileDialog.setContentPane(content);
            this.profileDialog.getRootPane().registerKeyboardAction(e9 -> {
                this.profileDialog.setVisible(false);
            }, KeyStroke.getKeyStroke(27, 0), 2);
            this.profileDialog.setSize(540, 640);
        }
        this.profileStatus.setText(" ");
        showAccount();
        showStats();
        refreshAccount(null);
        loadStats();
        boolean accountView = tab == 3;
        if (this.profileContent != null) {
            this.profileCards.show(this.profileContent, accountView ? "account" : "profile");
        }
        this.profileDialog.setTitle(accountView ? "Prime" : "Profile");
        if (!accountView && tab >= 0 && this.profileTabs != null) {
            this.profileTabs.setSelectedIndex(tab);
        }
        this.profileDialog.setLocationRelativeTo(this.host.frame());
        this.profileDialog.setVisible(true);
    }

    private void loadLeaderboard(String board) {
        this.leaderboardBoard = board;
        String url = mapServiceUrl().replaceAll("/find/?$", "") + "/stats/leaderboard?limit=100&board=" + board;
        String testBoard = System.getProperty("neroxis.launcher.testboard");
        if (testBoard != null) {
            try {
                this.leaderboard.setText(leaderboardHtml(MiniJson.parse(Files.readString(Path.of(testBoard, new String[0])))));
                return;
            } catch (IOException | RuntimeException e) {
                this.leaderboard.setText(e.toString());
                return;
            }
        }
        Thread loader = new Thread(() -> {
            String html;
            try {
                HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8L)).build(), HttpResponse.BodyHandlers.ofString());
                html = leaderboardHtml(MiniJson.parse((String) response.body()));
            } catch (IOException | RuntimeException e2) {
                html = "<html><span style='color:#8f96a3'>The leaderboard cannot be loaded right now.</span></html>";
            } catch (InterruptedException e3) {
                Thread.currentThread().interrupt();
                return;
            }
            String text = html;
            SwingUtilities.invokeLater(() -> {
                this.leaderboard.setText(text);
            });
        }, "leaderboard");
        loader.setDaemon(true);
        loader.start();
    }

    String leaderboardHtml(Object rows) {
        if (!(rows instanceof List)) {
            return "<html><div style='width:400px;color:#8f96a3'>Nobody has played a rated game yet. Rated games have two teams, no AI and last at least 5 minutes.</div></html>";
        }
        List<?> list = (List) rows;
        if (list.isEmpty()) {
            return "<html><div style='width:400px;color:#8f96a3'>Nobody has played a rated game yet. Rated games have two teams, no AI and last at least 5 minutes.</div></html>";
        }
        String me = this.previewName != null ? this.previewName : this.client.getUserName() != null ? this.client.getUserName() : "";
        StringBuilder html = new StringBuilder("<html><table cellpadding=3 cellspacing=0 width=440><tr style='color:#8f96a3'><td>#</td><td>Player</td><td>Rank</td><td align=right>Rating</td><td align=right>Games</td><td align=right>Won</td></tr>");
        for (Object row : list) {
            if (row instanceof Map) {
                Map<?, ?> entry = (Map) row;
                int level = Math.max(1, (int) num(entry, "level"));
                Color colour = Trophy.GRADE_COLOURS[(level - 1) % 3];
                String name = String.valueOf(entry.get("name"));
                double games = num(entry, "games");
                html.append("<tr").append(name.equalsIgnoreCase(me) ? " bgcolor='#2a2438'" : "").append(">").append("<td style='color:#8f96a3'>").append(whole(num(entry, "place"))).append("</td>").append("<td style='color:").append(name.equalsIgnoreCase(me) ? "#cc4de6" : "#e6e8ec").append("'>").append(escape(name)).append("</td>").append(String.format("<td style='color:#%02x%02x%02x'>", Integer.valueOf(colour.getRed()), Integer.valueOf(colour.getGreen()), Integer.valueOf(colour.getBlue()))).append(Trophy.title(level)).append("</td>").append("<td align=right style='color:#e6e8ec'><b>").append(whole(num(entry, "rating"))).append("</b></td><td align=right style='color:#8f96a3'>").append(whole(games)).append("</td><td align=right style='color:#8f96a3'>").append(games > 0.0d ? Math.round((100.0d * num(entry, "wins")) / games) + "%" : "-").append("</td></tr>");
            }
        }
        return html.append("</table></html>").toString();
    }

    private void loadStats() {
        String user = this.client.getUserName();
        if (user == null) {
            return;
        }
        String url = mapServiceUrl().replaceAll("/find/?$", "") + "/stats?user=" + URLEncoder.encode(user, StandardCharsets.UTF_8);
        Thread loader = new Thread(() -> {
            try {
                HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8L)).build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Object patt0$temp = MiniJson.parse((String) response.body());
                    if (patt0$temp instanceof Map) {
                        Map<String, Object> map = (Map) patt0$temp;
                        if (map.containsKey("rank")) {
                            this.playerStats = map;
                            SwingUtilities.invokeLater(this::showStats);
                        }
                    }
                }
            } catch (IOException | RuntimeException e) {
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }
        }, "stats");
        loader.setDaemon(true);
        loader.start();
    }

    private static double num(Map<?, ?> map, String key) {
        Object value = map == null ? null : map.get(key);
        if (!(value instanceof Number)) {
            return 0.0d;
        }
        Number number = (Number) value;
        return number.doubleValue();
    }

    private static Map<?, ?> sub(Map<?, ?> map, String key) {
        Object value = map == null ? null : map.get(key);
        if (!(value instanceof Map)) {
            return Map.of();
        }
        Map<?, ?> inner = (Map) value;
        return inner;
    }

    private static String whole(double value) {
        return String.format(Locale.US, "%,d", Long.valueOf(Math.round(value)));
    }

    private static String big(double value) {
        if (value >= 1000000.0d) {
            return String.format(Locale.US, "%.1fM", Double.valueOf(value / 1000000.0d));
        }
        return value >= 10000.0d ? String.format(Locale.US, "%.1fk", Double.valueOf(value / 1000.0d)) : whole(value);
    }

    private static String duration(double seconds) {
        long minutes = Math.round(seconds / 60.0d);
        if (minutes < 60) {
            return minutes + " min";
        }
        long j = minutes / 60;
        long j2 = minutes % 60;
        return j + " h " + j + " min";
    }

    private static JLabel text(String html, float size, Color colour) {
        JLabel label = new JLabel(html);
        label.setFont(label.getFont().deriveFont(size));
        label.setForeground(colour);
        label.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        return label;
    }

    private void showStats() {
        String str;
        String str2;
        String str3;
        String str4;
        double dDoubleValue;
        Map<?, ?> mapOf;
        Map<String, Object> stats = this.playerStats;
        String user = this.previewName != null ? this.previewName : this.client.getUserName() != null ? this.client.getUserName() : "";
        if (stats != null && !user.equalsIgnoreCase(String.valueOf(stats.get("name")))) {
            stats = null;
        }
        Map<?, ?> rank = sub(stats, "rank");
        int level = Math.max(1, (int) num(rank, "level"));
        int trophy = ((level - 1) / 3) + 1;
        int grade = (level - 1) % 3;
        this.rankPanel.removeAll();
        this.rankPanel.add(Box.createVerticalGlue());
        JLabel name = text(user, 26.0f, new Color(204, 77, 230));
        name.setAlignmentX(0.5f);
        this.rankPanel.add(name);
        this.rankPanel.add(Box.createVerticalStrut(6));
        JLabel cup = new JLabel(new ImageIcon(Trophy.image(trophy, grade, 190, false)));
        cup.setAlignmentX(0.5f);
        this.rankPanel.add(cup);
        JLabel title = text(Trophy.title(level), 22.0f, Trophy.GRADE_COLOURS[grade]);
        title.setFont(title.getFont().deriveFont(1));
        title.setAlignmentX(0.5f);
        this.rankPanel.add(title);
        Map<?, ?> ratings = sub(stats, "ratings");
        Map<?, ?> global = sub(ratings, "global");
        Map<?, ?> duel = sub(ratings, "1v1");
        JLabel line = text("Global rating " + whole(num(global, "rating")) + "  ·  " + whole(num(global, "games")) + " rated games", 14.0f, BarLauncher.TEXT);
        line.setAlignmentX(0.5f);
        this.rankPanel.add(Box.createVerticalStrut(2));
        this.rankPanel.add(line);
        JLabel duelLine = text("1v1 rating " + whole(num(duel, "rating")) + "  ·  " + whole(num(duel, "games")) + " duels  ·  " + Trophy.title((int) num(sub(stats, "duelRank"), "level")), 12.0f, BarLauncher.MUTED);
        duelLine.setAlignmentX(0.5f);
        this.rankPanel.add(Box.createVerticalStrut(2));
        this.rankPanel.add(duelLine);
        this.rankPanel.add(Box.createVerticalStrut(12));
        JProgressBar progress = new JProgressBar(0, Math.max(1, (int) num(rank, "pointsForLevel")));
        progress.setValue((int) num(rank, "pointsInLevel"));
        progress.setStringPainted(true);
        progress.setForeground(Trophy.GRADE_COLOURS[grade]);
        boolean top = Boolean.TRUE.equals(rank.get("max"));
        boolean newTrophy = level % 3 == 0;
        if (stats == null) {
            str = "Play a rated game to start your rating";
        } else if (top) {
            str = "Top rank reached";
        } else {
            str = (newTrophy ? "A new trophy" : Trophy.title(level + 1)) + " at rating " + whole(num(rank, "nextAt"));
        }
        progress.setString(str);
        progress.setAlignmentX(0.5f);
        progress.setMaximumSize(new Dimension(4000, 22));
        this.rankPanel.add(progress);
        this.rankPanel.add(Box.createVerticalGlue());
        this.rankPanel.revalidate();
        this.rankPanel.repaint();
        this.statsPanel.removeAll();
        if (stats == null) {
            this.statsPanel.add(text("<html><div style='width:400px'>No games counted yet. Finished games on Eclipse show up here: wins, losses, time played, damage, units and more.</div></html>", 14.0f, BarLauncher.MUTED));
        } else {
            double games = num(stats, "games");
            double streak = num(stats, "streak");
            Object rate = stats.get("winRate");
            Map<?, ?> totals = sub(stats, "totals");
            Map<?, ?> best = sub(stats, "best");
            String[][] tiles = new String[18][];
            tiles[0] = new String[]{"Global rating", whole(num(global, "rating"))};
            tiles[1] = new String[]{"1v1 rating", whole(num(duel, "rating"))};
            tiles[2] = new String[]{"Peak rating", whole(num(global, "peak"))};
            tiles[3] = new String[]{"Games", whole(games)};
            tiles[4] = new String[]{"Wins", whole(num(stats, "wins"))};
            tiles[5] = new String[]{"Losses", whole(num(stats, "losses"))};
            String[] strArr = new String[2];
            strArr[0] = "Win rate";
            if (rate instanceof Number) {
                Number number = (Number) rate;
                str2 = String.format(Locale.US, "%.1f%%", Double.valueOf(number.doubleValue()));
            } else {
                str2 = "-";
            }
            strArr[1] = str2;
            tiles[6] = strArr;
            tiles[7] = new String[]{"Time played", duration(num(stats, "seconds"))};
            String[] strArr2 = new String[2];
            strArr2[0] = "Streak";
            strArr2[1] = streak > 0.0d ? whole(streak) + " won" : streak < 0.0d ? whole(-streak) + " lost" : "-";
            tiles[8] = strArr2;
            tiles[9] = new String[]{"Best streak", whole(num(stats, "bestStreak")) + " wins"};
            tiles[10] = new String[]{"Damage dealt", big(num(totals, "damageDealt"))};
            tiles[11] = new String[]{"Damage taken", big(num(totals, "damageReceived"))};
            tiles[12] = new String[]{"Units killed", big(num(totals, "unitsKilled"))};
            tiles[13] = new String[]{"Units built", big(num(totals, "unitsProduced"))};
            tiles[14] = new String[]{"Units lost", big(num(totals, "unitsDied"))};
            tiles[15] = new String[]{"Metal made", big(num(totals, "metalProduced"))};
            tiles[16] = new String[]{"Energy made", big(num(totals, "energyProduced"))};
            tiles[17] = new String[]{"Most kills", big(num(best, "unitsKilled"))};
            JPanel grid = new JPanel(new GridLayout(0, 3, 8, 8));
            grid.setOpaque(false);
            for (String[] tile : tiles) {
                JPanel card = new JPanel(this, new BorderLayout(0, 2)) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.11
                    final /* synthetic */ LobbyUi this$0;

                    {
                        Objects.requireNonNull(this);
                        this.this$0 = this;
                    }

                    protected void paintComponent(Graphics graphics) {
                        Graphics2D g = graphics.create();
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setColor(new Color(34, 37, 45));
                        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g.dispose();
                    }
                };
                card.setOpaque(false);
                card.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                card.add(text(tile[0], 12.0f, BarLauncher.MUTED), "North");
                JLabel value = text(tile[1], 19.0f, BarLauncher.TEXT);
                value.setFont(value.getFont().deriveFont(1));
                card.add(value, "Center");
                grid.add(card);
            }
            grid.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            this.statsPanel.add(grid);
            StringBuilder html = new StringBuilder("<html><div style='width:400px'>");
            Map<?, ?> types = sub(stats, "types");
            if (!types.isEmpty()) {
                html.append("<p style='color:#e6e8ec;font-size:13pt'><b>By game type</b></p><table cellpadding=2>");
                for (Map.Entry<?, ?> type : types.entrySet()) {
                    Object value2 = type.getValue();
                    if (value2 instanceof Map) {
                        Map<?, ?> inner = (Map) value2;
                        mapOf = inner;
                    } else {
                        mapOf = Map.of();
                    }
                    Map<?, ?> counts = mapOf;
                    html.append("<tr><td style='color:#c8ccd4;width:140px'>").append(escape(String.valueOf(type.getKey()))).append("</td><td style='color:#8f96a3'>").append(whole(num(counts, "games"))).append(" games, ").append(whole(num(counts, "wins"))).append(" won</td></tr>");
                }
                html.append("</table>");
            }
            Object obj = stats.get("favouriteMaps");
            if (obj instanceof List) {
                List<?> maps = (List) obj;
                if (!maps.isEmpty()) {
                    html.append("<p style='color:#e6e8ec;font-size:13pt'><br><b>Favourite maps</b></p><table cellpadding=2>");
                    for (Object entry : maps) {
                        if (entry instanceof List) {
                            List<?> pair = (List) entry;
                            if (pair.size() == 2) {
                                StringBuilder sbAppend = html.append("<tr><td style='color:#c8ccd4;width:260px'>").append(escape(String.valueOf(pair.get(0)))).append("</td><td style='color:#8f96a3'>");
                                Object obj2 = pair.get(1);
                                if (obj2 instanceof Number) {
                                    Number n = (Number) obj2;
                                    dDoubleValue = n.doubleValue();
                                } else {
                                    dDoubleValue = 0.0d;
                                }
                                sbAppend.append(whole(dDoubleValue)).append(" games</td></tr>");
                            }
                        }
                    }
                    html.append("</table>");
                }
            }
            Object obj3 = stats.get("recent");
            if (obj3 instanceof List) {
                List<?> recent = (List) obj3;
                if (!recent.isEmpty()) {
                    html.append("<p style='color:#e6e8ec;font-size:13pt'><br><b>Last games</b></p><table cellpadding=2>");
                    SimpleDateFormat day = new SimpleDateFormat("d MMM");
                    for (Object entry2 : recent) {
                        if (entry2 instanceof Map) {
                            Map<?, ?> game = (Map) entry2;
                            int result = (int) num(game, "result");
                            StringBuilder sbAppend2 = html.append("<tr><td style='color:#8f96a3;width:50px'>").append(day.format(new Date((long) (num(game, "date") * 1000.0d)))).append("</td><td style='color:");
                            if (result == 1) {
                                str3 = "#3ab054'>Win";
                            } else if (result == 0) {
                                str3 = "#ff6054'>Loss";
                            } else {
                                str3 = "#8f96a3'>Draw";
                            }
                            sbAppend2.append(str3).append("</td><td style='color:#c8ccd4'>").append(escape(String.valueOf(game.get("map")))).append("</td><td style='color:#8f96a3'>").append(escape(String.valueOf(game.get("structure")))).append(", ").append(duration(num(game, "seconds"))).append("</td>");
                            double delta = num(game, "delta");
                            if (Boolean.TRUE.equals(game.get("rated"))) {
                                str4 = delta >= 0.0d ? "<td style='color:#3ab054'>+" + whole(delta) + "</td>" : "<td style='color:#ff6054'>" + whole(delta) + "</td>";
                            } else {
                                str4 = "<td style='color:#8f96a3'>unrated</td>";
                            }
                            html.append(str4).append("</tr>");
                        }
                    }
                    html.append("</table>");
                }
            }
            html.append("</div></html>");
            this.statsPanel.add(Box.createVerticalStrut(10));
            this.statsPanel.add(text(html.toString(), 12.0f, BarLauncher.TEXT));
        }
        this.statsPanel.revalidate();
        this.statsPanel.repaint();
    }

    private void openCheckout(boolean paypal) {
        String strCheckoutPaypal;
        Account current = this.account;
        if (current == null) {
            strCheckoutPaypal = "";
        } else {
            strCheckoutPaypal = paypal ? current.checkoutPaypal() : current.checkout();
        }
        String page = strCheckoutPaypal;
        if (page.isBlank()) {
            JOptionPane.showMessageDialog(this.profileDialog, "The checkout is not open yet.\nUntil it is, nobody needs Prime to play on Eclipse.", "Prime account", 1);
            return;
        }
        try {
            String separator = page.contains("?") ? "&" : "?";
            Desktop.getDesktop().browse(URI.create(page + separator + (paypal ? "custom_id=" : "client_reference_id=") + URLEncoder.encode(this.client.getUserName(), StandardCharsets.UTF_8)));
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(this.profileDialog, "Could not open the browser: " + page);
        }
    }

    private static String image(Path file, int width, int height) {
        return file == null ? "" : "<td valign='middle' style='padding-right:5px'><img src='" + String.valueOf(file.toUri()) + "' width='" + width + "' height='" + height + "'></td>";
    }

    private String inGameMark() {
        byte[] bytes;
        Path official = this.host.home().resolve("cache").resolve("badges").resolve("bar-ingame.png");
        if (!Files.isRegularFile(official, new LinkOption[0]) && (bytes = BarInstall.readMenuFile(this.host.barData(), "luamenu/images/ingame.png")) != null) {
            try {
                Files.createDirectories(official.getParent(), new FileAttribute[0]);
                Files.write(official, bytes, new OpenOption[0]);
            } catch (IOException e) {
            }
        }
        if (Files.isRegularFile(official, new LinkOption[0])) {
            return "<img src='" + String.valueOf(official.toUri()) + "' width='17' height='17'>";
        }
        Path file = this.host.home().resolve("cache").resolve("badges").resolve("in-game-1.png");
        if (!Files.isRegularFile(file, new LinkOption[0])) {
            try {
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                BufferedImage picture = new BufferedImage(64, 64, 2);
                Graphics2D g = picture.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                for (int sword = 0; sword < 2; sword++) {
                    AffineTransform before = g.getTransform();
                    if (sword == 1) {
                        g.translate(64, 0);
                        g.scale(-1.0d, 1.0d);
                    }
                    g.setStroke(new BasicStroke(9.0f, 1, 1));
                    g.setColor(new Color(20, 24, 32));
                    g.drawLine(9, 9, 43, 43);
                    g.setStroke(new BasicStroke(5.5f, 1, 1));
                    g.setColor(new Color(214, 222, 234));
                    g.drawLine(9, 9, 43, 43);
                    g.setStroke(new BasicStroke(6.0f, 1, 1));
                    g.setColor(new Color(255, 170, 60));
                    g.drawLine(36, 51, 51, 36);
                    g.setStroke(new BasicStroke(5.0f, 1, 1));
                    g.drawLine(45, 45, 54, 54);
                    g.fillOval(51, 51, 9, 9);
                    g.setTransform(before);
                }
                g.dispose();
                ImageIO.write(picture, "png", file.toFile());
            } catch (IOException e2) {
                return "<span style='color:#ffaa3c'>&#9876;</span>";
            }
        }
        return "<img src='" + String.valueOf(file.toUri()) + "' width='17' height='17'>";
    }

    private String readyMark(boolean ready) {
        Path official = this.host.home().resolve("cache").resolve("badges").resolve(ready ? "bar-ready.png" : "bar-unready.png");
        if (!Files.isRegularFile(official, new LinkOption[0])) {
            byte[] bytes = BarInstall.readMenuFile(this.host.barData(), "luamenu/images/" + (ready ? "ready.png" : "unready.png"));
            if (bytes != null) {
                try {
                    Files.createDirectories(official.getParent(), new FileAttribute[0]);
                    Files.write(official, bytes, new OpenOption[0]);
                } catch (IOException e) {
                }
            }
        }
        if (Files.isRegularFile(official, new LinkOption[0])) {
            return "<img src='" + String.valueOf(official.toUri()) + "' width='17' height='17'>";
        }
        Path file = this.host.home().resolve("cache").resolve("badges").resolve(ready ? "ready-2.png" : "not-ready-2.png");
        if (!Files.isRegularFile(file, new LinkOption[0])) {
            try {
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                BufferedImage picture = new BufferedImage(64, 64, 2);
                Graphics2D g = picture.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                if (ready) {
                    g.setColor(new Color(74, 190, 96));
                    g.fillOval(3, 3, 58, 58);
                    g.setColor(new Color(16, 40, 22));
                    g.setStroke(new BasicStroke(8.5f, 1, 1));
                    Path2D.Float check = new Path2D.Float();
                    check.moveTo(18.0f, 33.0f);
                    check.lineTo(28.0f, 43.0f);
                    check.lineTo(46.0f, 22.0f);
                    g.draw(check);
                } else {
                    g.setColor(new Color(214, 72, 66, 46));
                    g.fillOval(4, 4, 56, 56);
                    g.setColor(new Color(224, 84, 76));
                    g.setStroke(new BasicStroke(5.5f));
                    g.drawOval(5, 5, 54, 54);
                    g.setStroke(new BasicStroke(7.5f, 1, 1));
                    g.drawLine(22, 22, 42, 42);
                    g.drawLine(42, 22, 22, 42);
                }
                g.dispose();
                ImageIO.write(picture, "png", file.toFile());
            } catch (IOException e2) {
                return ready ? "<span style='color:#3ab054'>&#10004;</span>" : "<span style='color:#d05a50'>&#10005;</span>";
            }
        }
        return "<img src='" + String.valueOf(file.toUri()) + "' width='15' height='15'>";
    }

    private Path factionBadge(int side) {
        String[] letters = {"A", "C", "?", "L"};
        Color[] colours = {new Color(54, 120, 220), new Color(208, 62, 56), new Color(110, 116, 128), new Color(70, 170, 90)};
        int index = (side < 0 || side >= letters.length) ? 2 : side;
        String[] names = {"armada", "cortex", "random", "legion"};
        Path official = this.host.home().resolve("cache").resolve("badges").resolve("side-" + names[index] + ".png");
        if (Files.isRegularFile(official, new LinkOption[0])) {
            return official;
        }
        try {
            BufferedImage icon = BarInstall.readGameIcon(this.host.barData(), "sidepics/" + names[index] + ".png");
            if (icon != null) {
                Files.createDirectories(official.getParent(), new FileAttribute[0]);
                ImageIO.write(icon, "png", official.toFile());
                return official;
            }
        } catch (IOException | RuntimeException e) {
        }
        Path file = this.host.home().resolve("cache").resolve("badges").resolve("faction-" + index + ".png");
        if (!Files.isRegularFile(file, new LinkOption[0])) {
            try {
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                BufferedImage picture = new BufferedImage(72, 72, 2);
                Graphics2D g = picture.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(colours[index]);
                g.fillRoundRect(2, 2, 68, 68, 22, 22);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", 1, 50));
                int width = g.getFontMetrics().stringWidth(letters[index]);
                g.drawString(letters[index], (72 - width) / 2, 54);
                g.dispose();
                ImageIO.write(picture, "png", file.toFile());
            } catch (IOException e2) {
                return null;
            }
        }
        return file;
    }

    private Path flag(String country) {
        if (country == null || !country.matches("[A-Za-z]{2}")) {
            return null;
        }
        String code = country.toLowerCase(Locale.ROOT);
        Path file = this.host.home().resolve("cache").resolve("flags").resolve(code + ".png");
        if (Files.isRegularFile(file, new LinkOption[0])) {
            return file;
        }
        if (this.flagsLoading.add(code)) {
            Thread loader = new Thread(() -> {
                try {
                    HttpResponse<byte[]> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create("https://flagcdn.com/40x30/" + code + ".png")).timeout(Duration.ofSeconds(10L)).build(), HttpResponse.BodyHandlers.ofByteArray());
                    if (response.statusCode() == 200 && ((byte[]) response.body()).length > 100) {
                        Files.createDirectories(file.getParent(), new FileAttribute[0]);
                        Files.write(file, (byte[]) response.body(), new OpenOption[0]);
                        SwingUtilities.invokeLater(this::updateRoom);
                    }
                } catch (IOException | RuntimeException e) {
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
            }, "flag");
            loader.setDaemon(true);
            loader.start();
            return null;
        }
        return null;
    }

    private static JMenuItem item(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> {
            action.run();
        });
        return item;
    }

    private void whisperTo(String name) {
        this.dockTabs.setSelectedIndex(1);
        JTextPane pane = openChatTab("@" + name);
        selectChatTab("@" + name);
        if (this.client.getUser(name) == null && pane.getDocument().getLength() == 0) {
            append(pane, null, name + " is offline. Messages only reach people while they are signed in.", BarLauncher.MUTED);
        }
        this.chatInput.requestFocusInWindow();
    }

    private LobbyClient.Battle battleOf(String user) {
        for (LobbyClient.Battle battle : this.client.getBattles()) {
            if (battle.users.contains(user) && !battle.founder.equalsIgnoreCase(user)) {
                return battle;
            }
        }
        return null;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$PersonRenderer.class */
    private final class PersonRenderer extends DefaultListCellRenderer {
        private final boolean friendsList;
        private final Map<String, JLabel> rows;
        final /* synthetic */ LobbyUi this$0;

        PersonRenderer(final LobbyUi this$0, boolean friendsList) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.rows = new LinkedHashMap<String, JLabel>(this, PreviewGenerator.PREVIEW_SIZE, 0.75f, true) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.PersonRenderer.1
                final /* synthetic */ PersonRenderer this$1;

                {
                    Objects.requireNonNull(this);
                    this.this$1 = this;
                }

                @Override // java.util.LinkedHashMap
                protected boolean removeEldestEntry(Map.Entry<String, JLabel> eldest) {
                    return size() > 3000;
                }
            };
            this.friendsList = friendsList;
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
            String str;
            String str2;
            String str3;
            String entry = (String) value;
            JLabel label = super.getListCellRendererComponent(list, value, index, selected && !entry.startsWith("#"), focused);
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            if (entry.startsWith("#")) {
                label.setText(entry.substring(1));
                label.setForeground(BarLauncher.ACCENT);
                label.setFont(label.getFont().deriveFont(1, 11.0f));
                return label;
            }
            boolean request = entry.startsWith("?");
            boolean offline = entry.startsWith("~");
            String name = (request || offline) ? entry.substring(1) : entry;
            LobbyClient.User user = offline ? null : this.this$0.client.getUser(name);
            boolean publicOn = user == null && !offline && !request && this.this$0.publicOnline.contains(name);
            boolean publicGame = publicOn && this.this$0.publicInGame.contains(name);
            if (request) {
                str = "#F0962E";
            } else if (publicOn) {
                str = publicGame ? "#ff5a4f" : "#3ab054";
            } else if (user == null) {
                str = "#6b7280";
            } else if (user.inGame()) {
                str = "#ff5a4f";
            } else {
                str = user.away() ? "#f2c230" : "#3ab054";
            }
            String colour = str;
            if (request) {
                str2 = "wants to be your friend, click to answer";
            } else if (publicOn) {
                str2 = publicGame ? "in a game" : "online";
            } else if (user == null) {
                str2 = "offline";
            } else if (user.inGame()) {
                str2 = "in a game";
            } else if (user.away()) {
                str2 = "away";
            } else {
                str2 = this.this$0.battleOf(name) != null ? "in a battle room" : "online";
            }
            String state = str2;
            String note = request ? "" : this.this$0.noteOf(name);
            String tip = note.isEmpty() ? null : "<html><div style='width:280px'>Your note: " + LobbyUi.escape(note) + "</div></html>";
            Color personal = this.this$0.personColour(name, null);
            String shownName = personal == null ? name : "<span style='color:" + String.format("#%06x", Integer.valueOf(personal.getRGB() & 16777215)) + "'>" + name + "</span>";
            if (offline) {
                shownName = "<span style='color:#7c8494'>" + name + "</span>";
            }
            String country = this.this$0.flag("show.flags", true) ? this.this$0.countryOf(name) : null;
            Path flagFile = country != null ? this.this$0.flag(country) : null;
            Image flagPicture = flagFile != null ? this.this$0.flagPicture(flagFile) : null;
            String str4 = shownName;
            String strBadgeHtml = this.this$0.badgeHtml(name);
            String str5 = name.equalsIgnoreCase(this.this$0.client.getUserName()) ? " <span style='color:#8c94a3'>(you)</span>" : "";
            String str6 = note.isEmpty() ? "" : " <span style='color:#8c94a3'>&#9998;</span>";
            if (this.friendsList || request) {
                str3 = "<br><span style='color:#8c94a3'>" + state + "</span>";
            } else if (user == null || !user.bot()) {
                str3 = (user == null || !user.inGame()) ? "" : " <span style='color:#8c94a3'>in game</span>";
            } else {
                str3 = " <span style='color:#8c94a3'>bot</span>";
            }
            String text = "<html><b>" + str4 + "</b>" + strBadgeHtml + str5 + str6 + str3 + "</html>";
            String key = colour + ((flagFile == null || flagPicture == null) ? "-" : flagFile.toString()) + text;
            JLabel row = this.rows.get(key);
            if (row == null) {
                row = new JLabel(text);
                row.setOpaque(true);
                row.setFont(label.getFont());
                row.setForeground(list.getForeground());
                row.setBorder(label.getBorder());
                row.setIcon(new StateAndFlag(Color.decode(colour), flagPicture));
                row.setIconTextGap(0);
                this.rows.put(key, row);
            }
            row.setBackground(selected ? list.getSelectionBackground() : list.getBackground());
            row.setToolTipText(tip);
            return row;
        }
    }

    private Image flagPicture(Path file) {
        if (this.flagPictures.containsKey(file)) {
            return this.flagPictures.get(file);
        }
        BufferedImage bufferedImage = null;
        try {
            BufferedImage read = ImageIO.read(file.toFile());
            if (read != null) {
                BufferedImage small = new BufferedImage(40, 28, 2);
                Graphics2D g = small.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(read, 0, 0, 40, 28, (ImageObserver) null);
                g.dispose();
                bufferedImage = small;
            }
        } catch (IOException e) {
        }
        this.flagPictures.put(file, bufferedImage);
        return bufferedImage;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$StateAndFlag.class */
    private static final class StateAndFlag implements Icon {
        private final Color dot;
        private final Image flag;

        StateAndFlag(Color dot, Image flag) {
            this.dot = dot;
            this.flag = flag;
        }

        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setColor(this.dot);
            g.fillOval(x, y + 3, 8, 8);
            if (this.flag != null) {
                g.drawImage(this.flag, x + 15, y, 20, 14, (ImageObserver) null);
            }
            g.dispose();
        }

        public int getIconWidth() {
            return 42;
        }

        public int getIconHeight() {
            return 14;
        }
    }

    JComponent room() {
        return this.room;
    }

    private void buildRoom() {
        this.room.setBackground(BarLauncher.BACKGROUND);
        this.room.setLayout(new BorderLayout(0, 10));
        this.room.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 14));
        Component surface = new BarLauncher.Surface(new BorderLayout(), BarLauncher.GLASS, 12);
        surface.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 6));
        this.roomTitle.setFont(this.roomTitle.getFont().deriveFont(1, 16.0f));
        this.roomTitle.setForeground(BarLauncher.TEXT);
        this.roomTitle.setHorizontalAlignment(0);
        this.roomTitle.setCursor(Cursor.getPredefinedCursor(12));
        this.roomTitle.setToolTipText("Click to rename the room");
        this.roomTitle.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.12
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mousePressed(MouseEvent event) {
                this.this$0.startRenaming();
            }
        });
        this.roomTitleEditor.setFont(this.roomTitle.getFont());
        this.roomTitleEditor.setForeground(BarLauncher.TEXT);
        this.roomTitleEditor.setCaretColor(BarLauncher.ACCENT);
        this.roomTitleEditor.setSelectionColor(new Color(240, 150, 46, 150));
        this.roomTitleEditor.setSelectedTextColor(Color.WHITE);
        this.roomTitleEditor.setBackground(new Color(14, 16, 22));
        this.roomTitleEditor.setHorizontalAlignment(0);
        this.roomTitleEditor.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BarLauncher.ACCENT), BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        this.roomTitleEditor.setPreferredSize(new Dimension(420, 30));
        this.roomTitleEditor.getDocument().setDocumentFilter(new RoomNameFilter());
        this.roomTitleEditor.addActionListener(e -> {
            finishRenaming(true);
        });
        this.roomTitleEditor.registerKeyboardAction(e2 -> {
            finishRenaming(false);
        }, KeyStroke.getKeyStroke(27, 0), 0);
        this.roomTitleEditor.addFocusListener(new FocusAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.13
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void focusLost(FocusEvent event) {
                this.this$0.finishRenaming(this.this$0.amBoss());
            }
        });
        JPanel editorHolder = new JPanel(new GridBagLayout());
        editorHolder.setOpaque(false);
        editorHolder.add(this.roomTitleEditor);
        this.roomTitleCards.setOpaque(false);
        this.roomTitleCards.add(this.roomTitle, "label");
        this.roomTitleCards.add(editorHolder, "edit");
        JButton leave = new JButton("Leave Lobby");
        leave.putClientProperty("JButton.buttonType", "roundRect");
        leave.setBackground(new Color(150, 40, 36));
        leave.setForeground(Color.WHITE);
        leave.setFont(leave.getFont().deriveFont(1, 13.0f));
        leave.setPreferredSize(new Dimension(130, 32));
        leave.addActionListener(e3 -> {
            this.client.leaveBattle();
        });
        BarButtonUi.install((AbstractButton) leave, BarButtonUi.NEGATIVE);
        surface.add(this.roomTitleCards, "Center");
        surface.add(leave, "East");
        this.room.add(surface, "North");
        this.teamsPanel.setOpaque(false);
        this.teamsPanel.setLayout(new BoxLayout(this.teamsPanel, 1));
        JScrollPane teamScroll = new JScrollPane(this.teamsPanel);
        teamScroll.setBorder(BorderFactory.createEmptyBorder());
        teamScroll.setOpaque(false);
        teamScroll.getViewport().setOpaque(false);
        teamScroll.setHorizontalScrollBarPolicy(31);
        teamScroll.getVerticalScrollBar().setUnitIncrement(18);
        JPanel teamsSurface = new BarLauncher.Surface(new BorderLayout(), BarLauncher.GLASS, 12);
        teamsSurface.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 6));
        teamsSurface.add(teamScroll, "Center");
        JPanel setup = new BarLauncher.Surface(null, BarLauncher.GLASS, 12);
        setup.setLayout(new BoxLayout(setup, 1));
        setup.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));
        setup.setPreferredSize(new Dimension(206, 100));
        this.teamCount.setRenderer(suffixRenderer(" Teams"));
        this.teamSize.setRenderer(suffixRenderer(" per Team"));
        JComponent jButton = new JButton("Change Map");
        jButton.setBackground(BarLauncher.ACCENT);
        jButton.setForeground(new Color(25, 18, 8));
        jButton.setFont(jButton.getFont().deriveFont(1, 13.0f));
        jButton.setToolTipText("Pick another map or generate a new one");
        jButton.addActionListener(e4 -> {
            changeMap();
        });
        BarButtonUi.install((AbstractButton) jButton, BarButtonUi.ACTION);
        JComponent jButton2 = new JButton("Adv Options");
        jButton2.addActionListener(e5 -> {
            showAdvancedOptions();
        });
        BarButtonUi.install((AbstractButton) jButton2, BarButtonUi.PLAIN);
        Runnable describe = () -> {
            int size = ((Integer) this.teamSize.getSelectedItem()).intValue();
            int count = ((Integer) this.teamCount.getSelectedItem()).intValue();
            this.formatLabel.setForeground(size * count > 16 ? ERROR : BarLauncher.MUTED);
            this.formatLabel.setText(size * count > 16 ? "Up to 16 start positions in total" : String.join("v", Collections.nCopies(count, String.valueOf(size))));
        };
        this.teamSize.addActionListener(e6 -> {
            describe.run();
            if (((Integer) this.teamSize.getSelectedItem()).intValue() * ((Integer) this.teamCount.getSelectedItem()).intValue() <= 16) {
                this.client.sayBattle("!set teamSize " + String.valueOf(this.teamSize.getSelectedItem()));
            }
        });
        this.teamCount.addActionListener(e7 -> {
            describe.run();
            if (((Integer) this.teamSize.getSelectedItem()).intValue() * ((Integer) this.teamCount.getSelectedItem()).intValue() <= 16) {
                this.client.sayBattle("!set nbTeams " + String.valueOf(this.teamCount.getSelectedItem()));
            }
        });
        describe.run();
        for (JComponent control : new JComponent[]{this.teamCount, this.teamSize, jButton, jButton2}) {
            control.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            control.setMaximumSize(new Dimension(190, 32));
            control.setPreferredSize(new Dimension(190, 32));
            setup.add(control);
            setup.add(Box.createVerticalStrut(6));
        }
        this.formatLabel.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        setup.add(this.formatLabel);
        setup.add(Box.createVerticalStrut(10));
        for (JLabel line : new JLabel[]{this.gameStatus, this.engineStatus, this.mapStatus, this.boxStatus}) {
            line.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            line.setForeground(BarLauncher.MUTED);
            line.setFont(line.getFont().deriveFont(12.0f));
            setup.add(line);
            setup.add(Box.createVerticalStrut(3));
        }
        this.optionsLabel.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        this.optionsLabel.setForeground(BarLauncher.MUTED);
        this.optionsLabel.setFont(this.optionsLabel.getFont().deriveFont(11.5f));
        this.optionsLabel.setVerticalAlignment(1);
        setup.add(Box.createVerticalStrut(6));
        setup.add(this.optionsLabel);
        setup.add(Box.createVerticalGlue());
        JPanel mapColumn = new JPanel(new BorderLayout(0, 6));
        mapColumn.setOpaque(false);
        mapColumn.setPreferredSize(new Dimension(340, 100));
        this.roomPreview.setPreferredSize(new Dimension(340, 340));
        mapColumn.add(this.roomPreview, "North");
        JPanel underMap = new JPanel();
        underMap.setLayout(new BoxLayout(underMap, 1));
        underMap.setOpaque(false);
        this.roomMap.setFont(this.roomMap.getFont().deriveFont(1, 13.0f));
        this.roomMap.setForeground(BarLauncher.TEXT);
        this.roomMap.setAlignmentX(0.5f);
        underMap.add(this.roomMap);
        this.generatingBar.setVisible(false);
        this.generatingBar.setBorderPainted(false);
        this.generatingBar.setForeground(BarLauncher.ACCENT);
        this.generatingBar.setPreferredSize(new Dimension(300, 6));
        this.generatingBar.setMaximumSize(new Dimension(300, 6));
        this.generatingBar.setAlignmentX(0.5f);
        underMap.add(Box.createVerticalStrut(5));
        underMap.add(this.generatingBar);
        underMap.add(Box.createVerticalStrut(6));
        JComponent boxSelector = buildBoxControls();
        boxSelector.setAlignmentX(0.5f);
        underMap.add(boxSelector);
        mapColumn.add(underMap, "Center");
        JPanel upperRight = new JPanel(new BorderLayout(10, 0));
        upperRight.setOpaque(false);
        upperRight.add(setup, "West");
        upperRight.add(mapColumn, "Center");
        JPanel upper = new JPanel(new BorderLayout(10, 0));
        upper.setOpaque(false);
        upper.add(teamsSurface, "Center");
        upper.add(upperRight, "East");
        JPanel balance = new JPanel(new GridLayout(1, 3, 8, 0));
        balance.setOpaque(false);
        for (String[] action : new String[][]{new String[]{"Balance", "!balance"}, new String[]{"Lock", "!lock"}, new String[]{"Unlock", "!unlock"}}) {
            JButton button = new JButton(action[0]);
            button.addActionListener(e8 -> {
                this.client.sayBattle(action[1]);
            });
            BarButtonUi.install((AbstractButton) button, BarButtonUi.PLAIN);
            balance.add(button);
        }
        JToggleButton playButton = new JToggleButton("Play", true);
        this.spectateButton.setText("Spectating");
        ButtonGroup mode = new ButtonGroup();
        mode.add(playButton);
        mode.add(this.spectateButton);
        playButton.addActionListener(e9 -> {
            this.settings.setProperty("last.spectator", "false");
            saveSettings();
            this.client.setPlayer(true);
            this.readyButton.setEnabled(true);
        });
        this.spectateButton.addActionListener(e10 -> {
            if (this.client.queuePosition() > 0) {
                this.client.leaveQueue();
            }
            this.settings.setProperty("last.spectator", "true");
            saveSettings();
            this.client.setPlayer(false);
            this.readyButton.setSelected(false);
            this.readyButton.setEnabled(false);
        });
        this.playToggle = playButton;
        this.readyButton.setText("Ready");
        this.readyButton.setFont(this.readyButton.getFont().deriveFont(1, 14.0f));
        this.readyButton.addActionListener(e11 -> {
            this.client.setReady(this.readyButton.isSelected());
        });
        JButton start = new JButton("Start");
        start.setBackground(new Color(40, 150, 60));
        start.setForeground(Color.WHITE);
        start.setFont(start.getFont().deriveFont(1, 15.0f));
        start.addActionListener(e12 -> {
            LobbyClient.Battle battle = this.client.getJoinedBattle();
            LobbyClient.User founder = battle != null ? this.client.getUser(battle.founder) : null;
            if (founder != null && founder.inGame()) {
                if (!this.host.engineRunning()) {
                    launchGame(battle, this.client.getScriptPassword());
                }
            } else {
                boolean everybodyReady = this.client.getParticipants().stream().filter(person -> {
                    return person.owner == null && person.player();
                }).allMatch((v0) -> {
                    return v0.ready();
                });
                boolean host = amBoss();
                this.client.sayBattle((host && everybodyReady) ? "!forcestart" : "!start");
            }
        });
        for (JToggleButton toggle : List.of(playButton, this.spectateButton)) {
            BarButtonUi.install((AbstractButton) toggle, (Function<AbstractButton, Color>) button2 -> {
                return button2.isSelected() ? BarButtonUi.HIGHLIGHT : BarButtonUi.PLAIN;
            });
        }
        BarButtonUi.install((AbstractButton) this.readyButton, (Function<AbstractButton, Color>) button3 -> {
            return button3.isSelected() ? BarButtonUi.POSITIVE : BarButtonUi.PLAIN;
        });
        BarButtonUi.install((AbstractButton) start, BarButtonUi.POSITIVE);
        JPanel playGrid = new JPanel(new GridLayout(2, 2, 8, 6));
        playGrid.setOpaque(false);
        playGrid.setPreferredSize(new Dimension(340, 74));
        playGrid.add(playButton);
        playGrid.add(this.spectateButton);
        playGrid.add(this.readyButton);
        playGrid.add(start);
        JPanel hostSettings = new JPanel(new GridLayout(1, 3, 8, 0));
        hostSettings.setOpaque(false);
        String[][][] choices = {new String[][]{new String[]{"Autobalance"}, new String[]{"advanced", "off"}, new String[]{"!set autoBalance "}}, new String[][]{new String[]{"BalanceMode"}, new String[]{"clan;skill", "skill", "random"}, new String[]{"!set balanceMode "}}, new String[][]{new String[]{"Preset"}, new String[]{"team", "duel", "ffa"}, new String[]{"!preset "}}};
        for (String[][] choice : choices) {
            JButton chooser = new JButton();
            String[] current = {choice[1][0]};
            Runnable show = () -> {
                chooser.setText("<html><span style='color:#b4bbc8'>" + choice[0][0] + "</span>&nbsp;&nbsp;<b>" + current[0] + "</b>&nbsp;&#9662;</html>");
            };
            show.run();
            chooser.setFont(chooser.getFont().deriveFont(11.5f));
            chooser.setToolTipText("The host sets this directly, everybody else calls a vote");
            BarButtonUi.install((AbstractButton) chooser, BarButtonUi.PLAIN);
            chooser.addActionListener(e13 -> {
                JPopupMenu menu = new JPopupMenu();
                String[] arr$ = choice[1];
                for (String value : arr$) {
                    menu.add(item(value.equals(current[0]) ? "✓ " + value : "   " + value, () -> {
                        current[0] = value;
                        show.run();
                        this.client.sayBattle(choice[2][0] + value);
                    }));
                }
                menu.show(chooser, 0, chooser.getHeight());
            });
            hostSettings.add(chooser);
        }
        hostSettings.setPreferredSize(new Dimension(100, 30));
        JPanel balanceHolder = new JPanel(new BorderLayout(0, 6));
        balanceHolder.setOpaque(false);
        balanceHolder.add(hostSettings, "Center");
        balanceHolder.add(balance, "South");
        JPanel strip = new JPanel(new BorderLayout(10, 0));
        strip.setOpaque(false);
        strip.add(balanceHolder, "Center");
        JPanel stripRight = new JPanel(new BorderLayout(10, 0));
        stripRight.setOpaque(false);
        stripRight.add(playGrid, "Center");
        strip.add(stripRight, "East");
        JPanel chatArea = new JPanel(new BorderLayout(0, 0));
        chatArea.setOpaque(false);
        chatArea.add(chatScroll(this.roomChat), "Center");
        chatInputStyle(this.roomInput);
        this.roomInput.putClientProperty("JTextField.placeholderText", "Message the room");
        this.roomInput.setToolTipText("!help lists the commands of the host");
        this.roomInput.addActionListener(e14 -> {
            this.client.sayBattle(this.roomInput.getText());
            this.roomInput.setText("");
        });
        this.voteBar.setVisible(false);
        this.voteBar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 10));
        this.voteText.setForeground(BarLauncher.TEXT);
        this.voteText.setFont(this.voteText.getFont().deriveFont(1, 13.0f));
        JButton yes = new JButton("YES");
        yes.putClientProperty("JButton.buttonType", "roundRect");
        yes.setBackground(new Color(60, 150, 80));
        yes.setForeground(Color.WHITE);
        yes.setFont(yes.getFont().deriveFont(1, 13.0f));
        yes.addActionListener(e15 -> {
            this.client.sayBattle("!vote y");
            this.voteBar.setVisible(false);
        });
        JButton no = new JButton("NO");
        no.putClientProperty("JButton.buttonType", "roundRect");
        no.setBackground(new Color(170, 66, 58));
        no.setForeground(Color.WHITE);
        no.setFont(no.getFont().deriveFont(1, 13.0f));
        no.addActionListener(e16 -> {
            this.client.sayBattle("!vote n");
            this.voteBar.setVisible(false);
        });
        JPanel voteButtons = new JPanel(new GridLayout(1, 2, 8, 0));
        voteButtons.setOpaque(false);
        voteButtons.setPreferredSize(new Dimension(170, 32));
        voteButtons.add(yes);
        voteButtons.add(no);
        this.voteBar.add(this.voteText, "Center");
        this.voteBar.add(voteButtons, "East");
        JPanel chatBottom = new JPanel(new BorderLayout(0, 0));
        chatBottom.setOpaque(false);
        chatBottom.add(this.voteBar, "North");
        chatBottom.add(this.roomInput, "South");
        chatArea.add(chatBottom, "South");
        this.spectatorPanel.setOpaque(false);
        this.spectatorPanel.setLayout(new BoxLayout(this.spectatorPanel, 1));
        JPanel spectatorSurface = new BarLauncher.Surface(new BorderLayout(0, 6), BarLauncher.GLASS, 12);
        spectatorSurface.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
        spectatorSurface.setPreferredSize(new Dimension(340, 100));
        this.spectatorTitle.setForeground(BarLauncher.TEXT);
        this.spectatorTitle.setFont(this.spectatorTitle.getFont().deriveFont(1, 12.0f));
        spectatorSurface.add(this.spectatorTitle, "North");
        JScrollPane spectatorScroll = new JScrollPane(this.spectatorPanel);
        spectatorScroll.setBorder(BorderFactory.createEmptyBorder());
        spectatorScroll.setOpaque(false);
        spectatorScroll.getViewport().setOpaque(false);
        spectatorSurface.add(spectatorScroll, "Center");
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setOpaque(false);
        bottom.add(chatArea, "Center");
        JPanel bottomRight = new JPanel(new BorderLayout(10, 0));
        bottomRight.setOpaque(false);
        bottomRight.add(spectatorSurface, "Center");
        bottom.add(bottomRight, "East");
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(100, 500));
        top.add(upper, "Center");
        top.add(strip, "South");
        top.setMinimumSize(new Dimension(100, 0));
        bottom.setMinimumSize(new Dimension(100, 0));
        this.room.add(split(0, top, bottom, "split.room2", -250, 1.0d), "Center");
    }

    private static ListCellRenderer<Object> suffixRenderer(final String suffix) {
        return new DefaultListCellRenderer() { // from class: com.faforever.neroxis.generator.bar.LobbyUi.14
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focused) {
                JLabel label = super.getListCellRendererComponent(list, value, index, selected, focused);
                label.setText(String.valueOf(value) + suffix);
                label.setHorizontalAlignment(0);
                return label;
            }
        };
    }

    private List<ModOptions.Option> gameOptions() {
        if (this.gameOptions == null || this.gameOptions.isEmpty()) {
            String lua = this.host.gameText("modoptions.lua");
            this.gameOptions = lua != null ? ModOptions.parse(lua) : List.of();
        }
        return this.gameOptions;
    }

    private static boolean sameValue(String value, String def) {
        String str;
        String str2;
        if (value.equals("true")) {
            str = "1";
        } else {
            str = value.equals("false") ? "0" : value;
        }
        String a = str;
        if (def.equals("true")) {
            str2 = "1";
        } else {
            str2 = def.equals("false") ? "0" : def;
        }
        String b = str2;
        if (a.equals(b)) {
            return true;
        }
        try {
            return Double.parseDouble(a) == Double.parseDouble(b);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    void showDockTab(int index) {
        this.dockTabs.setSelectedIndex(index);
    }

    BarLauncher.PreviewPanel roomPreview() {
        return this.roomPreview;
    }

    void showAdvancedOptions() {
        String strDef;
        JCheckBox jCheckBox;
        List<ModOptions.Option> options = gameOptions();
        if (options.isEmpty()) {
            JOptionPane.showMessageDialog(this.host.frame(), "The game options could not be read from the BAR installation.");
            return;
        }
        Map<String, String> current = this.client.getModOptions();
        JDialog dialog = new JDialog(this.host.frame(), "Adv Options", System.getProperty("neroxis.launcher.screenshot") == null);
        JTabbedPane tabs = new JTabbedPane();
        Map<String, JPanel> sections = new LinkedHashMap<>();
        Map<String, Supplier<String>> readers = new LinkedHashMap<>();
        Map<String, Runnable> resets = new LinkedHashMap<>();
        Map<String, String> startValues = new LinkedHashMap<>();
        for (ModOptions.Option option : options) {
            if (option.type().equals("section") && !HIDDEN_SECTIONS.contains(option.key()) && options.stream().anyMatch(other -> {
                return other.section().equals(option.key());
            })) {
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(BarLauncher.BACKGROUND);
                panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
                sections.put(option.key(), panel);
                JPanel holder = new JPanel(new BorderLayout());
                holder.setBackground(BarLauncher.BACKGROUND);
                holder.add(panel, "North");
                JScrollPane scroll = new JScrollPane(holder);
                scroll.getVerticalScrollBar().setUnitIncrement(20);
                tabs.addTab(option.name(), scroll);
            }
        }
        int[] row = {0};
        for (ModOptions.Option option2 : options) {
            JPanel panel2 = sections.get(option2.section());
            if (panel2 != null && !option2.type().equals("section")) {
                GridBagConstraints left = new GridBagConstraints();
                left.gridx = 0;
                left.gridy = row[0];
                left.anchor = 17;
                left.insets = new Insets(4, 0, 4, 18);
                GridBagConstraints right = new GridBagConstraints();
                right.gridx = 1;
                int i = row[0];
                row[0] = i + 1;
                right.gridy = i;
                right.anchor = 17;
                right.weightx = 1.0d;
                right.insets = new Insets(4, 0, 4, 0);
                if (option2.type().equals("separator")) {
                    left.gridwidth = 2;
                    left.fill = 2;
                    panel2.add(new JSeparator(), left);
                } else if (option2.type().equals("subheader")) {
                    left.gridwidth = 2;
                    JLabel header = new JLabel(option2.name().replaceFirst("^[-\\s]+", ""));
                    header.setForeground(BarLauncher.ACCENT);
                    header.setFont(header.getFont().deriveFont(1, 13.0f));
                    panel2.add(header, left);
                } else {
                    JLabel label = new JLabel(option2.name());
                    label.setForeground(BarLauncher.TEXT);
                    label.setToolTipText("<html><div style='width:360px'>" + option2.description() + "</div></html>");
                    if (option2.type().equals("bool")) {
                        strDef = (option2.def().equals("true") || option2.def().equals("1")) ? "1" : "0";
                    } else {
                        strDef = option2.def();
                    }
                    String def = strDef;
                    String value = current.getOrDefault(option2.key().toLowerCase(Locale.ROOT), def);
                    startValues.put(option2.key(), value);
                    switch (option2.type()) {
                        case "bool":
                            JCheckBox box = new JCheckBox();
                            box.setOpaque(false);
                            box.setSelected(value.equals("1") || value.equals("true"));
                            readers.put(option2.key(), () -> {
                                return box.isSelected() ? "1" : "0";
                            });
                            resets.put(option2.key(), () -> {
                                box.setSelected(def.equals("1"));
                            });
                            jCheckBox = box;
                            break;
                        case "list":
                            JCheckBox jComboBox = new JComboBox((String[]) option2.items().values().toArray(new String[0]));
                            List<String> keys = new ArrayList<>(option2.items().keySet());
                            jComboBox.setSelectedIndex(Math.max(0, keys.indexOf(value)));
                            readers.put(option2.key(), () -> {
                                return jComboBox.getSelectedIndex() >= 0 ? (String) keys.get(jComboBox.getSelectedIndex()) : value;
                            });
                            resets.put(option2.key(), () -> {
                                jComboBox.setSelectedIndex(Math.max(0, keys.indexOf(def)));
                            });
                            jCheckBox = jComboBox;
                            break;
                        default:
                            JCheckBox jTextField = new JTextField(value, 14);
                            readers.put(option2.key(), () -> {
                                return jTextField.getText().trim();
                            });
                            resets.put(option2.key(), () -> {
                                jTextField.setText(def);
                            });
                            jCheckBox = jTextField;
                            break;
                    }
                    if (!(jCheckBox instanceof JCheckBox)) {
                        jCheckBox.setPreferredSize(new Dimension(300, 28));
                    }
                    panel2.add(label, left);
                    panel2.add(jCheckBox, right);
                }
            }
        }
        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> {
            resets.values().forEach((v0) -> {
                v0.run();
            });
        });
        JButton apply = new JButton("Apply");
        apply.setBackground(new Color(40, 150, 60));
        apply.setForeground(Color.WHITE);
        apply.addActionListener(e2 -> {
            List<String> commands = new ArrayList<>();
            readers.forEach((key, reader) -> {
                String value2 = (String) reader.get();
                if (!value2.equals(startValues.get(key)) && value2.matches("[\\w .:+-]{0,60}")) {
                    commands.add("!bSet " + key + " " + value2);
                }
            });
            dialog.setVisible(false);
            int[] next = {0};
            Timer sender = new Timer(900, (ActionListener) null);
            sender.addActionListener(event -> {
                if (next[0] < commands.size() && this.client.getJoinedBattle() != null) {
                    LobbyClient lobbyClient = this.client;
                    int i2 = next[0];
                    next[0] = i2 + 1;
                    lobbyClient.sayBattle((String) commands.get(i2));
                    return;
                }
                sender.stop();
            });
            sender.setInitialDelay(0);
            sender.start();
        });
        JButton cancel = new JButton("Cancel");
        cancel.setBackground(new Color(150, 40, 36));
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(e3 -> {
            dialog.setVisible(false);
        });
        JPanel buttons = new JPanel(new FlowLayout(2, 10, 0));
        buttons.setBackground(BarLauncher.BACKGROUND);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 14, 12, 14));
        for (JButton button : new JButton[]{reset, apply, cancel}) {
            button.setPreferredSize(new Dimension(120, 40));
            button.setFont(button.getFont().deriveFont(1, 13.0f));
            buttons.add(button);
        }
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BarLauncher.BACKGROUND);
        content.add(tabs, "Center");
        content.add(buttons, "South");
        dialog.setContentPane(content);
        dialog.setSize(900, 640);
        dialog.setLocationRelativeTo(this.host.frame());
        dialog.setVisible(true);
    }

    private void startRenaming() {
        LobbyClient.Battle battle = this.client.getJoinedBattle();
        if (battle == null || this.renaming) {
            return;
        }
        this.renaming = true;
        this.roomTitleEditor.setToolTipText(amBoss() ? "Enter renames the room, Esc keeps the name" : "Enter asks the room in a vote, Esc keeps the name");
        this.roomTitleEditor.setText(battle.title);
        this.renamingFrom = this.roomTitleEditor.getText();
        this.roomTitleCards.getLayout().show(this.roomTitleCards, "edit");
        this.roomTitleEditor.requestFocusInWindow();
        this.roomTitleEditor.selectAll();
    }

    private void finishRenaming(boolean apply) {
        if (!this.renaming) {
            return;
        }
        this.renaming = false;
        this.roomTitleCards.getLayout().show(this.roomTitleCards, "label");
        LobbyClient.Battle battle = this.client.getJoinedBattle();
        String title = this.roomTitleEditor.getText().replaceAll(" +", " ").trim();
        if (apply && battle != null && !title.isEmpty() && !title.equals(this.renamingFrom.replaceAll(" +", " ").trim())) {
            if (amBoss()) {
                this.roomTitle.setText(title);
            }
            this.client.sayBattle("!rename " + title);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$RoomNameFilter.class */
    private static final class RoomNameFilter extends DocumentFilter {
        private RoomNameFilter() {
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.swing.text.BadLocationException */
        public void insertString(DocumentFilter.FilterBypass bypass, int offset, String text, AttributeSet attributes) throws BadLocationException {
            replace(bypass, offset, 0, text, attributes);
        }

        public void replace(DocumentFilter.FilterBypass bypass, int offset, int length, String text, AttributeSet attributes) throws BadLocationException {
            String allowed = text == null ? "" : text.replaceAll("[^A-Za-z0-9 \\[\\]<>+|:-]", "");
            int room = 40 - (bypass.getDocument().getLength() - length);
            if (allowed.length() > room) {
                allowed = allowed.substring(0, Math.max(0, room));
            }
            super.replace(bypass, offset, length, allowed, attributes);
        }
    }

    private void changeMap() {
        if (this.host.changeMap()) {
            return;
        }
        List<String> names = this.host.mapNames();
        if (names.isEmpty()) {
            return;
        }
        JList<String> list = new JList<>((String[]) names.toArray(new String[0]));
        list.setSelectionMode(0);
        list.setVisibleRowCount(16);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(420, 360));
        if (JOptionPane.showConfirmDialog(this.host.frame(), scroll, "Change map", 2, -1) == 0 && list.getSelectedValue() != null) {
            String name = (String) list.getSelectedValue();
            this.client.sayBattle(BarLauncher.isGeneratedName(name) ? "!genmap map=" + name : "!map " + name);
        }
    }

    private JComponent buildBoxControls() {
        final String[] choices = {"Default Boxes", "Custom Boxes", "East vs West", "North vs South", "NW vs SE", "NE vs SW", "4 Corners", "4 Sides"};
        String[] commands = {"!resetboxes", null, "!split v 25", "!split h 25", "!split c1 25", "!split c2 25", "!split c 25", "!split s 25"};
        JComboBox<String> selector = new JComboBox<>(choices);
        selector.setRenderer(new DefaultListCellRenderer(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.15
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                JLabel label = super.getListCellRendererComponent(list, value, index, selected, focus);
                int choice = Arrays.asList(choices).indexOf(String.valueOf(value));
                label.setIcon(new BoxLayoutIcon(choice));
                label.setIconTextGap(14);
                return label;
            }
        });
        boolean[] quiet = {false};
        selector.addActionListener(e -> {
            this.roomPreview.setBoxDrawing(null);
            String command = commands[Math.max(0, selector.getSelectedIndex())];
            if (!quiet[0] && command != null) {
                this.client.sayBattle(command);
            }
        });
        this.roomPreview.setBoxEditing((team, box) -> {
            quiet[0] = true;
            selector.setSelectedIndex(1);
            quiet[0] = false;
            this.client.sayBattle(String.format(Locale.ROOT, "!addbox %d %d %d %d %d", Integer.valueOf(Math.round(box[0] * 200.0f)), Integer.valueOf(Math.round(box[1] * 200.0f)), Integer.valueOf(Math.round(box[2] * 200.0f)), Integer.valueOf(Math.round(box[3] * 200.0f)), Integer.valueOf(team.intValue() + 1)));
        });
        JButton add = new JButton("+");
        add.setToolTipText("Draw a start box for one more team: drag on the map");
        add.addActionListener(e2 -> {
            int team2 = Math.min(16, this.client.getStartBoxes().size() + 1);
            quiet[0] = true;
            selector.setSelectedIndex(1);
            quiet[0] = false;
            this.roomPreview.setBoxDrawing(box2 -> {
                this.client.sayBattle(String.format(Locale.ROOT, "!addbox %d %d %d %d %d", Integer.valueOf(Math.round(box2[0] * 200.0f)), Integer.valueOf(Math.round(box2[1] * 200.0f)), Integer.valueOf(Math.round(box2[2] * 200.0f)), Integer.valueOf(Math.round(box2[3] * 200.0f)), Integer.valueOf(team2)));
                this.roomPreview.setBoxDrawing(null);
            });
        });
        JButton remove = new JButton("−");
        remove.setToolTipText("Remove the start box of the last team");
        remove.addActionListener(e3 -> {
            this.roomPreview.setBoxDrawing(null);
            int last = this.client.getStartBoxes().keySet().stream().mapToInt((v0) -> {
                return v0.intValue();
            }).max().orElse(-1);
            if (last >= 0) {
                quiet[0] = true;
                selector.setSelectedIndex(1);
                quiet[0] = false;
                this.client.sayBattle("!clearbox " + (last + 1));
            }
        });
        for (JButton button : new JButton[]{add, remove}) {
            button.setFont(button.getFont().deriveFont(1, 16.0f));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setPreferredSize(new Dimension(34, 30));
            button.setFocusable(false);
        }
        this.boxShape = setting("boxes.shape", "square").equals("triangle") ? "triangle" : "square";
        JComboBox<String> shape = new JComboBox<>(new String[]{"Rectangle", "Triangle"});
        shape.setSelectedIndex(this.boxShape.equals("triangle") ? 1 : 0);
        shape.setToolTipText("The shape of the start boxes: the boxes as they are, or triangles pointing at the middle of the map (every preset and drawn box keeps its place)");
        shape.setPrototypeDisplayValue("Rectangle ");
        shape.addActionListener(e4 -> {
            this.boxShape = shape.getSelectedIndex() == 1 ? "triangle" : "square";
            remember("boxes.shape", this.boxShape);
            this.boxShapeTimer.restart();
        });
        this.boxShapeTimer.setRepeats(false);
        JPanel choices2 = new JPanel(new BorderLayout(6, 0));
        choices2.setOpaque(false);
        choices2.add(selector, "Center");
        choices2.add(shape, "East");
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(340, 30));
        row.add(add, "West");
        row.add(choices2, "Center");
        row.add(remove, "East");
        return row;
    }

    private void sendBoxShape() {
        LobbyClient.Battle battle = this.client.getJoinedBattle();
        if (battle == null || !amBoss()) {
            return;
        }
        Map<Integer, float[]> boxes = this.client.getStartBoxes();
        String current = this.client.getModOptions().getOrDefault("mapmetadata_startbox_override", "");
        if (boxes.isEmpty()) {
            return;
        }
        if (this.boxShape.equals("square") && current.isEmpty()) {
            return;
        }
        String value = StartBoxShapes.encode(StartBoxShapes.shapes(boxes, this.boxShape), 16);
        if (value.equals(current) || value.equals(this.sentBoxShape)) {
            return;
        }
        this.sentBoxShape = value;
        this.client.sayBattle("!bSet mapmetadata_startbox_override " + value);
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyUi$BoxLayoutIcon.class */
    private static final class BoxLayoutIcon extends Record implements Icon {
        private final int choice;

        private BoxLayoutIcon(int choice) {
            this.choice = choice;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, BoxLayoutIcon.class), BoxLayoutIcon.class, "choice", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$BoxLayoutIcon;->choice:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, BoxLayoutIcon.class), BoxLayoutIcon.class, "choice", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$BoxLayoutIcon;->choice:I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, BoxLayoutIcon.class, Object.class), BoxLayoutIcon.class, "choice", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyUi$BoxLayoutIcon;->choice:I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public int choice() {
            return this.choice;
        }

        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(225, 228, 235));
            g.translate(x, y);
            switch (this.choice) {
                case 0:
                    g.setStroke(new BasicStroke(1.6f));
                    g.drawRoundRect(2, 2, 12, 12, 5, 5);
                    g.fillOval(6, 6, 4, 4);
                    break;
                case 1:
                    dots(g, new int[][]{new int[]{3, 2}, new int[]{3, 8}, new int[]{3, 13}, new int[]{12, 2}, new int[]{12, 8}, new int[]{12, 13}});
                    break;
                case 2:
                    g.fillRect(3, 2, 3, 12);
                    g.fillRect(10, 2, 3, 12);
                    break;
                case 3:
                    g.fillRect(2, 3, 12, 3);
                    g.fillRect(2, 10, 12, 3);
                    break;
                case 4:
                    dots(g, new int[][]{new int[]{2, 2}, new int[]{7, 7}, new int[]{12, 12}});
                    break;
                case 5:
                    dots(g, new int[][]{new int[]{12, 2}, new int[]{7, 7}, new int[]{2, 12}});
                    break;
                case 6:
                    dots(g, new int[][]{new int[]{2, 2}, new int[]{12, 2}, new int[]{2, 12}, new int[]{12, 12}});
                    break;
                default:
                    dots(g, new int[][]{new int[]{7, 1}, new int[]{1, 7}, new int[]{13, 7}, new int[]{7, 13}});
                    break;
            }
            g.dispose();
        }

        private static void dots(Graphics2D g, int[][] points) {
            for (int[] point : points) {
                g.fillRect(point[0], point[1], 3, 3);
            }
        }

        public int getIconWidth() {
            return 16;
        }

        public int getIconHeight() {
            return 16;
        }
    }

    private void showTeams(LobbyClient.Battle battle) {
        List<LobbyClient.Participant> all = this.client.getParticipants();
        this.gameColours = BarColors.predict(all);
        Color mine = this.client.getUserName() != null ? this.gameColours.get(this.client.getUserName()) : null;
        if (mine != null && pickedColour() < 0 && this.client.getColour() != PlayerColors.toProtocol(mine)) {
            this.client.setColour(PlayerColors.toProtocol(mine));
        }
        int teamBoxes = Math.max(2, this.client.getStartBoxes().size());
        List<LobbyClient.Participant> spectators = new ArrayList<>();
        Map<Integer, List<LobbyClient.Participant>> byTeam = new TreeMap<>();
        for (int team = 0; team < teamBoxes; team++) {
            byTeam.put(Integer.valueOf(team), new ArrayList<>());
        }
        for (LobbyClient.Participant participant : all) {
            if (!participant.name.equalsIgnoreCase(battle.founder)) {
                if (participant.player()) {
                    byTeam.computeIfAbsent(Integer.valueOf(participant.allyTeam()), team2 -> {
                        return new ArrayList();
                    }).add(participant);
                } else {
                    spectators.add(participant);
                }
            }
        }
        this.teamsPanel.removeAll();
        this.teamsPanel.setLayout(byTeam.size() <= 4 ? new GridLayout(byTeam.size(), 1, 0, 0) : new BoxLayout(this.teamsPanel, 1));
        for (Map.Entry<Integer, List<LobbyClient.Participant>> entry : byTeam.entrySet()) {
            int team3 = entry.getKey().intValue();
            JPanel section = new JPanel();
            section.setLayout(new BoxLayout(section, 1));
            section.setOpaque(false);
            section.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            JPanel header = new JPanel(new BorderLayout(8, 0));
            header.setOpaque(false);
            header.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            header.setMaximumSize(new Dimension(4000, 30));
            header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BarLauncher.CARD_BORDER), BorderFactory.createEmptyBorder(2, 4, 4, 4)));
            JLabel name = new JLabel("Team " + (team3 + 1));
            name.setForeground(BarLauncher.TEXT);
            name.setFont(name.getFont().deriveFont(1, 13.0f));
            JPanel left = new JPanel(new FlowLayout(0, 8, 0));
            left.setOpaque(false);
            left.add(name);
            JButton addAi = smallButton("Add AI ▾", null);
            addAi.setToolTipText("An AI of BAR for this team: BARbarIAn in four difficulties, the simple AIs, Scavengers, Raptors");
            addAi.addActionListener(e -> {
                JPopupMenu menu = new JPopupMenu();
                for (LobbyClient.AiKind kind : LobbyClient.AI_KINDS) {
                    menu.add(item(kind.label(), () -> {
                        this.client.addBot(team3, kind);
                    }));
                }
                menu.show(addAi, 0, addAi.getHeight());
            });
            left.add(addAi);
            left.add(smallButton("Join", () -> {
                if (this.playToggle != null) {
                    this.playToggle.setSelected(true);
                }
                this.readyButton.setEnabled(true);
                this.client.setAllyTeam(team3);
            }));
            JLabel count = new JLabel(String.valueOf(entry.getValue().size()));
            count.setForeground(BarLauncher.MUTED);
            header.add(left, "West");
            header.add(count, "East");
            section.add(header);
            for (LobbyClient.Participant participant2 : entry.getValue()) {
                section.add(personRow(participant2, true));
            }
            section.add(Box.createVerticalStrut(14));
            section.add(Box.createVerticalGlue());
            this.teamsPanel.add(section);
        }
        this.teamsPanel.revalidate();
        this.teamsPanel.repaint();
        this.spectatorTitle.setText("Spectators   " + spectators.size());
        this.spectatorPanel.removeAll();
        List<String> queue = this.client.getJoinQueue();
        if (!queue.isEmpty()) {
            StringBuilder line = new StringBuilder("<html><span style='color:#ffce40'><b>Join queue</b></span>");
            for (int i = 0; i < queue.size(); i++) {
                boolean me = queue.get(i).equalsIgnoreCase(this.client.getUserName());
                line.append("<br>").append(i + 1).append(".&nbsp; ").append(me ? "<b>" : "").append(queue.get(i)).append(me ? "</b>" : "");
            }
            JLabel waiting = new JLabel(line.append("</html>").toString());
            waiting.setForeground(BarLauncher.TEXT);
            waiting.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 4));
            waiting.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
            this.spectatorPanel.add(waiting);
        }
        for (LobbyClient.Participant participant3 : spectators) {
            this.spectatorPanel.add(personRow(participant3, false));
        }
        this.spectatorPanel.add(Box.createVerticalGlue());
        this.spectatorPanel.revalidate();
        this.spectatorPanel.repaint();
    }

    private static JButton smallButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(11.5f));
        button.setMargin(new Insets(1, 12, 1, 12));
        button.setCursor(Cursor.getPredefinedCursor(12));
        if (action != null) {
            button.addActionListener(e -> {
                action.run();
            });
        }
        BarButtonUi.install((AbstractButton) button, BarButtonUi.PLAIN);
        return button;
    }

    private String gameColourHex(LobbyClient.Participant participant) {
        Color colour = this.gameColours.get(participant.name);
        return colour != null ? String.format("#%02x%02x%02x", Integer.valueOf(colour.getRed()), Integer.valueOf(colour.getGreen()), Integer.valueOf(colour.getBlue())) : PlayerColors.hex(participant.colour);
    }

    private JComponent personRow(final LobbyClient.Participant participant, boolean player) {
        String strInGameMark;
        final boolean ai = participant.owner != null;
        final boolean own = participant.name.equalsIgnoreCase(this.client.getUserName());
        LobbyClient.User user = this.client.getUser(participant.name);
        if (!player) {
            strInGameMark = "<span style='color:#8c94a3'>&#9679;</span>";
        } else if (ai) {
            strInGameMark = "<span style='color:#8c94a3'>&#9881;</span>";
        } else {
            strInGameMark = (user == null || !user.inGame()) ? readyMark(participant.ready()) : inGameMark();
        }
        String mark = strInGameMark;
        boolean isBoss = this.bosses.contains(participant.name);
        String skill = (ai || !flag("show.ratings", true)) ? "" : this.client.getSkill(participant.name);
        Font font = barFont(new JLabel().getFont(), 13.5f);
        Color foreground = personColour(participant.name, BarLauncher.TEXT);
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 2, 4));
        row.setAlignmentX(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT);
        row.setMaximumSize(new Dimension(4000, 26));
        JLabel head = new JLabel("<html><table cellpadding='0' cellspacing='0'><tr><td valign='middle' style='padding-right:6px' nowrap>" + mark + "</td>" + ((ai || user == null || !flag("show.flags", true)) ? "" : image(flag(user.country), 20, 14)) + (skill.isEmpty() ? "" : "<td valign='middle' align='right' width='30' style='padding-right:8px;color:#e2b23e' nowrap>" + skill + "</td>") + "</tr></table></html>");
        head.setFont(font);
        head.setForeground(foreground);
        head.setMaximumSize(head.getPreferredSize());
        row.add(head);
        if (player) {
            final JLabel badge = new JLabel();
            Path picture = factionBadge(participant.side());
            if (picture != null) {
                badge.setIcon(new ImageIcon(new ImageIcon(picture.toString()).getImage().getScaledInstance(18, 18, 4)));
            }
            badge.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
            if (own && !ai) {
                this.ownRow = badge;
                badge.setToolTipText("Your faction");
                badge.setCursor(Cursor.getPredefinedCursor(12));
                badge.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.16
                    final /* synthetic */ LobbyUi this$0;

                    {
                        Objects.requireNonNull(this);
                        this.this$0 = this;
                    }

                    public void mouseReleased(MouseEvent event) {
                        this.this$0.showFactionMenu(badge, event);
                    }
                });
            }
            row.add(badge);
            final JComponent square = colourSquare(PlayerColors.fromProtocol(participant.colour));
            square.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
            if (own && !ai) {
                square.setToolTipText("Your colour: " + PlayerColors.describe(participant.colour));
                square.setCursor(Cursor.getPredefinedCursor(12));
                square.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.17
                    final /* synthetic */ LobbyUi this$0;

                    {
                        Objects.requireNonNull(this);
                        this.this$0 = this;
                    }

                    public void mouseReleased(MouseEvent event) {
                        this.this$0.showColourMenu(square, event);
                    }
                });
            }
            row.add(square);
        }
        final JLabel label = new JLabel("<html><span nowrap>" + participant.name + (ai ? "" : badgeHtml(participant.name)) + (noteOf(participant.name).isEmpty() ? "" : " <span style='color:#8c94a3'>&#9998;</span>") + (ai ? " <span style='color:#8c94a3'>AI of " + participant.owner + "</span>" : "") + ((user == null || !user.inGame()) ? "" : " <span style='color:#ffaa3c'>in game</span>") + "</span></html>");
        label.setForeground(foreground);
        label.setFont(font);
        label.setCursor(Cursor.getPredefinedCursor(12));
        if (!noteOf(participant.name).isEmpty()) {
            label.setToolTipText("<html><div style='width:280px'>Your note: " + escape(noteOf(participant.name)) + "</div></html>");
        }
        label.addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.18
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void mouseReleased(MouseEvent event) {
                JPopupMenu menu = new JPopupMenu();
                String name = participant.name;
                if (ai) {
                    if (participant.owner.equalsIgnoreCase(this.this$0.client.getUserName())) {
                        menu.add(LobbyUi.item("Remove", () -> {
                            this.this$0.client.removeBot(name);
                        }));
                    } else {
                        menu.add(LobbyUi.item("AI of " + participant.owner, () -> {
                        }));
                    }
                } else if (!own) {
                    menu.add(LobbyUi.item("Message", () -> {
                        this.this$0.whisperTo(name);
                    }));
                    menu.add(LobbyUi.item("Ring", () -> {
                        this.this$0.client.ring(name);
                    }));
                    if (this.this$0.client.getFriends().contains(name)) {
                        menu.add(LobbyUi.item("Unfriend", () -> {
                            this.this$0.client.removeFriend(name);
                        }));
                    } else {
                        menu.add(LobbyUi.item("Friend", () -> {
                            this.this$0.client.requestFriend(name);
                        }));
                    }
                    menu.add(LobbyUi.item("Ignore", () -> {
                        this.this$0.client.ignore(name);
                    }));
                    this.this$0.addSocialItems(menu, name);
                }
                if (this.this$0.bosses.isEmpty() && !ai) {
                    if (menu.getComponentCount() > 0) {
                        menu.addSeparator();
                    }
                    menu.add(LobbyUi.item(own ? "Vote: make me host" : "Vote: make host", () -> {
                        this.this$0.client.sayBattle("!cv boss " + name);
                    }));
                }
                if (this.this$0.amBoss() && !own) {
                    menu.addSeparator();
                    if (ai) {
                        menu.add(LobbyUi.item("Kick AI", () -> {
                            this.this$0.client.sayBattle("!kickai " + name);
                        }));
                    } else {
                        menu.add(LobbyUi.item("Kick", () -> {
                            this.this$0.client.sayBattle("!kick " + name);
                        }));
                        menu.add(LobbyUi.item("Ban from this room", () -> {
                            this.this$0.client.sayBattle("!kickban " + name);
                        }));
                        if (this.this$0.bosses.contains(name)) {
                            menu.add(LobbyUi.item("Remove as host", () -> {
                                this.this$0.client.sayBattle("!unboss " + name);
                            }));
                        } else {
                            menu.add(LobbyUi.item("Make host too", () -> {
                                this.this$0.client.sayBattle("!boss " + name);
                            }));
                        }
                    }
                }
                if (own && !ai && this.this$0.amBoss()) {
                    menu.addSeparator();
                    menu.add(LobbyUi.item("Stop being host", () -> {
                        this.this$0.client.sayBattle("!unboss " + name);
                    }));
                }
                if (menu.getComponentCount() > 0) {
                    menu.addSeparator();
                }
                menu.add(LobbyUi.item("Copy Name", () -> {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(name), (ClipboardOwner) null);
                }));
                menu.show(label, event.getX(), event.getY());
            }
        });
        row.add(label);
        if (isBoss) {
            JLabel crown = new JLabel(new Crown(16));
            crown.setBorder(BorderFactory.createEmptyBorder(0, 6, 1, 0));
            crown.setToolTipText("Host of the room");
            row.add(crown);
        }
        row.add(Box.createHorizontalGlue());
        return row;
    }

    private void showFactionMenu(JComponent at, MouseEvent event) {
        JPopupMenu menu = new JPopupMenu();
        String legion = this.client.getModOptions().getOrDefault("experimentallegionfaction", "0");
        String[] factions = {"Armada", "Cortex", "Random", "Legion"};
        int side = 0;
        while (true) {
            if (side < ((legion.equals("1") || legion.equalsIgnoreCase("true")) ? 4 : 3)) {
                int chosen = side;
                JMenuItem faction = item("Play " + factions[side], () -> {
                    this.client.setSide(chosen);
                });
                Path badge = factionBadge(side);
                if (badge != null) {
                    faction.setIcon(new ImageIcon(new ImageIcon(badge.toString()).getImage().getScaledInstance(18, 18, 4)));
                }
                menu.add(faction);
                side++;
            } else {
                menu.show(at, event.getX(), event.getY());
                return;
            }
        }
    }

    private void showColourMenu(JComponent at, MouseEvent event) {
        JPopupMenu menu = new JPopupMenu();
        int current = this.client.getColour();
        for (PlayerColors.Named named : PlayerColors.FAF) {
            int value = PlayerColors.toProtocol(named.color());
            JMenuItem choice = item(named.name() + (value == current ? "  ✓" : ""), () -> {
                chooseColour(value);
            });
            choice.setIcon(colourIcon(named.color()));
            menu.add(choice);
        }
        menu.addSeparator();
        menu.add(item("The game's colour" + (pickedColour() < 0 ? "  ✓" : ""), this::forgetColour));
        menu.show(at, event.getX(), event.getY());
    }

    private static JComponent colourSquare(final Color color) {
        JComponent square = new JComponent() { // from class: com.faforever.neroxis.generator.bar.LobbyUi.19
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Insets insets = getInsets();
                int size = Math.min((getWidth() - insets.left) - insets.right, (getHeight() - insets.top) - insets.bottom);
                int x = insets.left;
                int y = insets.top + ((((getHeight() - insets.top) - insets.bottom) - size) / 2);
                g.setColor(color);
                g.fillRoundRect(x, y, size, size, 4, 4);
                g.setColor(new Color(0, 0, 0, 90));
                g.drawRoundRect(x, y, size - 1, size - 1, 4, 4);
                g.dispose();
            }
        };
        square.setPreferredSize(new Dimension(21, 14));
        square.setMinimumSize(square.getPreferredSize());
        square.setMaximumSize(square.getPreferredSize());
        square.setOpaque(false);
        return square;
    }

    private void chooseColour(int colour) {
        remember("room.colour", String.valueOf(colour));
        this.client.setColour(colour);
        remember("room.colour.ingame", "true");
    }

    private int pickedColour() {
        return settingNumber("room.colour", -1);
    }

    private void forgetColour() {
        remember("room.colour", null);
        remember("room.colour.ingame", null);
        Color game = this.client.getUserName() != null ? this.gameColours.get(this.client.getUserName()) : null;
        this.client.setColour(game != null ? PlayerColors.toProtocol(game) : 16744192);
    }

    Map<String, String> settingsForGame() {
        Map<String, String> values = new LinkedHashMap<>();
        boolean playing = this.client.getParticipants().stream().anyMatch(p -> {
            return p.name.equalsIgnoreCase(this.client.getUserName()) && p.player();
        });
        if (playing && flag("room.colour.ingame", false) && pickedColour() >= 0) {
            Color own = PlayerColors.fromProtocol(pickedColour());
            values.put("SimpleTeamColors", "1");
            values.put("SimpleTeamColorsPlayerR", String.valueOf(own.getRed()));
            values.put("SimpleTeamColorsPlayerG", String.valueOf(own.getGreen()));
            values.put("SimpleTeamColorsPlayerB", String.valueOf(own.getBlue()));
            values.put("EclipseOwnColour", "1");
        }
        return values;
    }

    private static Icon colourIcon(Color color) {
        BufferedImage image = new BufferedImage(14, 14, 2);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillRoundRect(0, 0, 14, 14, 4, 4);
        g.setColor(new Color(0, 0, 0, 90));
        g.drawRoundRect(0, 0, 13, 13, 4, 4);
        g.dispose();
        return new ImageIcon(image);
    }

    private void addAi() {
        int teamsWanted = Math.max(2, this.client.getStartBoxes().size());
        int[] counts = new int[teamsWanted];
        for (LobbyClient.Participant participant : this.client.getParticipants()) {
            if (participant.player() && participant.allyTeam() < teamsWanted) {
                int iAllyTeam = participant.allyTeam();
                counts[iAllyTeam] = counts[iAllyTeam] + 1;
            }
        }
        int smallest = 0;
        for (int team = 1; team < teamsWanted; team++) {
            if (counts[team] < counts[smallest]) {
                smallest = team;
            }
        }
        this.client.addBot(smallest);
    }

    private void removeAi() {
        String mine = null;
        for (LobbyClient.Participant participant : this.client.getParticipants()) {
            if (participant.owner != null && participant.owner.equalsIgnoreCase(this.client.getUserName())) {
                mine = participant.name;
            }
        }
        if (mine != null) {
            this.client.removeBot(mine);
        } else {
            append(this.roomChat, null, "There is no AI of yours in this room (ask its owner or the host: !kick <name>)", BarLauncher.MUTED);
        }
    }

    void sayInRoom(List<String> lines) {
        LobbyClient.Battle battle = this.client.getJoinedBattle();
        if (battle == null) {
            return;
        }
        int[] next = {0};
        Timer sender = new Timer(1200, (ActionListener) null);
        sender.addActionListener(e -> {
            if (next[0] < lines.size() && this.client.getJoinedBattle() != null) {
                LobbyClient lobbyClient = this.client;
                int i = next[0];
                next[0] = i + 1;
                lobbyClient.sayBattle((String) lines.get(i));
                return;
            }
            sender.stop();
        });
        sender.setInitialDelay(0);
        sender.start();
    }

    void joinAndSay(LobbyClient.Battle battle, List<String> lines) {
        this.pendingRoom = battle.id;
        this.pendingLines = new ArrayList(lines);
        if (lines.stream().anyMatch(line -> {
            return line.startsWith("!genmap");
        })) {
            this.generatingSince = System.currentTimeMillis();
            this.generatingFrom = battle.map;
        }
        join(battle);
    }

    void join(LobbyClient.Battle battle) {
        if (!requirePrime("join a game")) {
            return;
        }
        String password = null;
        if (battle.passworded) {
            password = JOptionPane.showInputDialog(this.host.frame(), "Password of \"" + battle.title + "\":");
            if (password == null) {
                return;
            }
        }
        this.roomChat.setText("");
        this.client.joinBattle(battle.id, password);
        String joinAs = setting("joinstate", "last");
        if (joinAs.equals("spectator") || (joinAs.equals("last") && flag("last.spectator", false))) {
            this.client.setPlayer(false);
        }
    }

    private void updateRoom() {
        String orDefault;
        LobbyClient.Battle artworkRoom = this.client.getJoinedBattle();
        if (artworkRoom != null && artworkRoom.id != this.artworkBattle) {
            this.artworkBattle = artworkRoom.id;
            this.roomsJoined++;
        }
        updateArtwork();
        LobbyClient.Battle battle = this.client.getJoinedBattle();
        this.host.showRoom(battle != null);
        if (battle == null) {
            this.shownMap = null;
            this.shownGame = null;
            this.shownEngine = null;
            this.bosses.clear();
            this.sentBoxShape = null;
            if (this.pendingRoom < 0) {
                this.generatingSince = 0L;
            }
            this.voteBar.setVisible(false);
            return;
        }
        if (!battle.game.equals(this.shownGame)) {
            this.shownGame = battle.game;
            checkGame(battle.game);
        }
        if (!battle.engine.equals(this.shownEngine)) {
            this.shownEngine = battle.engine;
            checkEngine(battle.engine);
        }
        if (this.renaming && !this.roomTitleEditor.isShowing()) {
            finishRenaming(false);
        }
        if (!this.renaming) {
            this.roomTitle.setText(battle.title);
        }
        this.roomTitle.setToolTipText(amBoss() ? "Click to rename the room" : "Click to suggest a new name, the room votes on it");
        if (this.playToggle != null) {
            int position = this.client.queuePosition();
            this.playToggle.setText(position > 0 ? "In queue  #" + position : "Play");
            this.playToggle.setToolTipText(position > 0 ? "The game is full. You play as soon as it is your turn, Spectating leaves the line." : null);
            if (this.client.isPlayer()) {
                this.playToggle.setSelected(true);
            } else {
                this.spectateButton.setSelected(true);
            }
        }
        if (battle.id == this.pendingRoom && !this.pendingLines.isEmpty()) {
            List<String> lines = this.pendingLines;
            this.pendingLines = new ArrayList();
            this.pendingRoom = -1;
            int[] next = {0};
            Timer setup = new Timer(1500, (ActionListener) null);
            setup.addActionListener(e -> {
                if (next[0] < lines.size() && this.client.getJoinedBattle() != null) {
                    LobbyClient lobbyClient = this.client;
                    int i = next[0];
                    next[0] = i + 1;
                    lobbyClient.sayBattle((String) lines.get(i));
                    return;
                }
                setup.stop();
            });
            setup.start();
        }
        this.readyButton.setSelected(this.client.isReady());
        this.spectateButton.setSelected(!this.client.isPlayer());
        showTeams(battle);
        Map<String, String> set = this.client.getModOptions();
        String rankedValue = set.getOrDefault("ranked_game", "1");
        boolean ranked = rankedValue.equals("1") || rankedValue.equalsIgnoreCase("true");
        StringBuilder optionText = new StringBuilder("<html>Ranked Game: <font color='" + (ranked ? "#3ab054" : "#f0c040") + "'>" + (ranked ? "Yes" : "No") + "</font><br>");
        int shown = 0;
        for (ModOptions.Option option : set.isEmpty() ? List.of() : gameOptions()) {
            String value = set.get(option.key().toLowerCase(Locale.ROOT));
            if (value != null && !option.key().equalsIgnoreCase("ranked_game") && !HIDDEN_SECTIONS.contains(option.section()) && !option.key().startsWith("dummy") && !sameValue(value, option.def()) && (!option.type().equals("list") || option.items().containsKey(value))) {
                shown++;
                if (shown <= 14) {
                    if (option.type().equals("bool")) {
                        orDefault = (value.equals("1") || value.equalsIgnoreCase("true")) ? "Yes" : "No";
                    } else {
                        orDefault = option.items().getOrDefault(value, value);
                    }
                    String text = orDefault;
                    optionText.append(option.name()).append(": <font color='#f0c040'>").append(text).append("</font><br>");
                }
            }
        }
        this.optionsLabel.setText(optionText.append(shown > 14 ? "..." : "").append("</html>").toString());
        Map<Integer, float[]> boxes = this.client.getStartBoxes();
        this.roomPreview.setBoxes(boxes);
        String shapeOption = this.client.getModOptions().getOrDefault("mapmetadata_startbox_override", "");
        this.roomPreview.setPolygons(StartBoxShapes.decode(shapeOption));
        String boxSignature = boxes.toString().length() + ":" + String.valueOf(boxes.keySet()) + ":" + Arrays.deepToString(boxes.values().toArray()) + ":" + shapeOption;
        if (!boxSignature.equals(this.lastBoxSignature)) {
            this.lastBoxSignature = boxSignature;
            this.boxShapeTimer.restart();
        }
        this.boxStatus.setForeground(boxes.isEmpty() ? BUSY : ONLINE);
        this.boxStatus.setText(boxes.isEmpty() ? "No start boxes" : "✔ " + boxes.size() + " start boxes");
        if (this.generatingSince > 0 && (System.currentTimeMillis() - this.generatingSince > 150000 || (this.generatingFrom != null && !battle.map.equals(this.generatingFrom)))) {
            this.generatingSince = 0L;
        }
        if (this.generatingSince > 0) {
            this.shownMap = null;
            this.roomPreview.setMap(BarLauncher.sigil(battle.title + battle.id), List.of());
            this.roomPreview.setMarks(BarInstall.Marks.NONE);
            this.roomPreview.setBoxes(Map.of());
            this.boxStatus.setText(" ");
            showGeneratingProgress();
            if (!this.generatingTick.isRunning()) {
                this.generatingTick.start();
                return;
            }
            return;
        }
        this.generatingTick.stop();
        this.generatingBar.setVisible(false);
        this.roomPreview.setPlaceholder("Map preview");
        if (!battle.map.equals(this.shownMap)) {
            this.shownMap = battle.map;
            String mapName = battle.map;
            this.roomMap.setText(BarLauncher.mapLabel(mapName));
            this.roomPreview.setMap(null, List.of());
            checkMap(mapName);
        }
    }

    private void checkMap(String mapName) {
        if (this.host.hasMap(mapName)) {
            if (BarLauncher.isGeneratedName(mapName) && !this.verifiedMaps.containsKey(mapName.toLowerCase(Locale.ROOT))) {
                verifyMapCopy(mapName);
                return;
            }
            this.mapReady = true;
            this.client.setSynced(this.mapReady && this.gameReady && this.engineReady);
            this.mapStatus.setForeground(ONLINE);
            this.mapStatus.setText("✔ Map Ready");
            Thread loader = new Thread(() -> {
                BufferedImage image = this.host.mapImage(mapName);
                BarInstall.Marks marks = this.host.mapMarks(mapName);
                SwingUtilities.invokeLater(() -> {
                    if (mapName.equals(this.shownMap)) {
                        this.roomPreview.setMap(image, List.of());
                        this.roomPreview.setMarks(marks);
                    }
                });
            }, "room-preview");
            loader.setDaemon(true);
            loader.start();
            return;
        }
        this.mapReady = false;
        this.client.setSynced(false);
        downloadMap(mapName);
    }

    private void downloadMap(String mapName) {
        if (mapName.equals(this.downloadingMap)) {
            return;
        }
        this.downloadingMap = mapName;
        this.mapStatus.setForeground(BUSY);
        this.mapStatus.setText("Downloading the map...");
        Thread download = new Thread(() -> {
            String error = download("--download-map", mapName, this.mapStatus, "Downloading the map... ");
            SwingUtilities.invokeLater(() -> {
                this.downloadingMap = null;
                if (error == null) {
                    this.host.mapsChanged();
                    if (mapName.equals(this.shownMap)) {
                        checkMapAfterDownload(mapName);
                        return;
                    }
                    return;
                }
                if (mapName.equals(this.shownMap)) {
                    this.mapStatus.setForeground(ERROR);
                    this.mapStatus.setText("<html>Could not download the map: " + error + "</html>");
                }
            });
        }, "map-download");
        download.setDaemon(true);
        download.start();
    }

    private void checkMapAfterDownload(String mapName) {
        if (this.host.hasMap(mapName)) {
            checkMap(mapName);
        } else {
            this.mapStatus.setForeground(ERROR);
            this.mapStatus.setText("The download finished, but the map is not there");
        }
    }

    private void verifyMapCopy(String mapName) {
        this.mapReady = false;
        this.client.setSynced(false);
        if (mapName.equals(this.verifyingMap)) {
            return;
        }
        this.verifyingMap = mapName;
        this.mapStatus.setForeground(BUSY);
        this.mapStatus.setText("Checking the map against the server...");
        Thread check = new Thread(() -> {
            Path archive = this.host.mapArchive(mapName);
            String server = serverMapMd5(mapName);
            String local = archive != null ? fileMd5(archive) : null;
            boolean same = server == null || local == null || server.equalsIgnoreCase(local);
            Path aside = same ? null : putMapAside(archive);
            SwingUtilities.invokeLater(() -> {
                this.verifyingMap = null;
                if (same) {
                    this.verifiedMaps.put(mapName.toLowerCase(Locale.ROOT), server == null ? "" : server);
                    if (mapName.equals(this.shownMap)) {
                        checkMap(mapName);
                        return;
                    }
                    return;
                }
                if (aside == null) {
                    if (mapName.equals(this.shownMap)) {
                        this.mapStatus.setForeground(ERROR);
                        this.mapStatus.setText("<html>The copy of this map on this computer differs from the server's and could not be moved away: " + String.valueOf(archive) + "</html>");
                        return;
                    }
                    return;
                }
                this.host.mapsChanged();
                append(this.roomChat, null, "The copy of " + BarLauncher.mapLabel(mapName) + " on this computer differs from the server's (moved to " + String.valueOf(aside.getParent()) + "). The server's copy is downloaded so that everybody plays the same map.", BUSY);
                if (mapName.equals(this.shownMap)) {
                    downloadMap(mapName);
                }
            });
        }, "map-check");
        check.setDaemon(true);
        check.start();
    }

    private String serverMapMd5(String mapName) {
        String url = mapServiceUrl().replaceAll("/find/?$", "") + "/find?category=map&springname=" + URLEncoder.encode(mapName, StandardCharsets.UTF_8);
        try {
            HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(150L)).build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            Object obj = MiniJson.parse((String) response.body());
            if (obj instanceof List) {
                List<?> entries = (List) obj;
                for (Object entry : entries) {
                    if (entry instanceof Map) {
                        Map<?, ?> fields = (Map) entry;
                        if (mapName.equalsIgnoreCase(String.valueOf(fields.get("springname")))) {
                            Object obj2 = fields.get("md5");
                            if (obj2 instanceof String) {
                                String md5 = (String) obj2;
                                if (!md5.isBlank()) {
                                    return md5;
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            return null;
        } catch (IOException | InterruptedException | RuntimeException e) {
            return null;
        }
    }

    private static String fileMd5(Path file) {
        try {
            InputStream in = Files.newInputStream(file, new OpenOption[0]);
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[65536];
                while (true) {
                    int read = in.read(buffer);
                    if (read <= 0) {
                        break;
                    }
                    digest.update(buffer, 0, read);
                }
                String hex = HexFormat.of().formatHex(digest.digest());
                if (in != null) {
                    in.close();
                }
                return hex;
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
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    private Path putMapAside(Path archive) {
        try {
            Path folder = this.host.barData().resolve("eclipse-replaced-maps");
            Files.createDirectories(folder, new FileAttribute[0]);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path target = folder.resolve(String.valueOf(archive.getFileName()) + "." + stamp);
            Files.move(archive, target, StandardCopyOption.REPLACE_EXISTING);
            Path note = archive.resolveSibling(String.valueOf(archive.getFileName()) + ".md5.gz");
            if (Files.exists(note, new LinkOption[0])) {
                Files.move(note, folder.resolve(String.valueOf(note.getFileName()) + "." + stamp), StandardCopyOption.REPLACE_EXISTING);
            }
            return target;
        } catch (IOException e) {
            return null;
        }
    }

    private String mapCopyProblem(String mapName) {
        Path archive;
        String local;
        if (mapName == null || !BarLauncher.isGeneratedName(mapName) || (archive = this.host.mapArchive(mapName)) == null) {
            return null;
        }
        String server = this.verifiedMaps.get(mapName.toLowerCase(Locale.ROOT));
        if (server == null || server.isEmpty()) {
            server = serverMapMd5(mapName);
        }
        if (server == null || (local = fileMd5(archive)) == null || server.equalsIgnoreCase(local)) {
            return null;
        }
        return "The game was not started: the copy of " + BarLauncher.mapLabel(mapName) + " on this computer differs from the server's. The server's copy is fetched now - press START GAME once the map is ready.";
    }

    private void checkGame(String gameName) {
        this.gameReady = isGameInstalled(gameName);
        this.client.setSynced(this.mapReady && this.gameReady && this.engineReady);
        if (this.gameReady) {
            this.gameStatus.setForeground(ONLINE);
            this.gameStatus.setText("✔ Game Ready");
        } else {
            if (gameName.equals(this.downloadingGame)) {
                return;
            }
            this.downloadingGame = gameName;
            this.gameStatus.setForeground(BUSY);
            this.gameStatus.setText("Updating the game to " + gameName.replace("Beyond All Reason ", "") + "...");
            Thread download = new Thread(() -> {
                String error = download("--download-game", gameName, this.gameStatus, "Updating the game... ");
                SwingUtilities.invokeLater(() -> {
                    this.downloadingGame = null;
                    if (!gameName.equals(this.shownGame)) {
                        return;
                    }
                    if (error == null && isGameInstalled(gameName)) {
                        checkGame(gameName);
                    } else {
                        this.gameStatus.setForeground(ERROR);
                        this.gameStatus.setText("<html>Could not update the game: " + (error != null ? error : "not found") + "</html>");
                    }
                });
            }, "game-download");
            download.setDaemon(true);
            download.start();
        }
    }

    private void checkEngine(String version) {
        Path folder = this.host.barData().resolve("engine").resolve("recoil_" + version);
        this.engineReady = Platform.hasEngine(folder);
        this.client.setSynced(this.mapReady && this.gameReady && this.engineReady);
        if (this.engineReady) {
            this.engineStatus.setForeground(ONLINE);
            this.engineStatus.setText("✔ Engine " + version);
            return;
        }
        if (!version.matches("[0-9A-Za-z._-]{3,40}")) {
            this.engineStatus.setForeground(ERROR);
            this.engineStatus.setText("Unknown engine version " + version);
            return;
        }
        if (version.equals(this.downloadingEngine)) {
            return;
        }
        if (MacOs.isMac()) {
            MacOs.prepare(this.host.barData());
            if (Platform.hasEngine(folder)) {
                checkEngine(version);
                return;
            }
            this.downloadingEngine = version;
            this.engineStatus.setForeground(BUSY);
            this.engineStatus.setText("Downloading the engine " + version + " for Mac...");
            String downloads = this.settings.getProperty("mapservice", DEFAULT_MAP_SERVICE).replaceFirst("/find/?$", "/download/");
            Thread download = new Thread(() -> {
                String error = MacOs.downloadEngine(version, downloads, this.host.barData(), text -> {
                    SwingUtilities.invokeLater(() -> {
                        if (version.equals(this.downloadingEngine)) {
                            this.engineStatus.setText(text);
                        }
                    });
                });
                SwingUtilities.invokeLater(() -> {
                    this.downloadingEngine = null;
                    if (!version.equals(this.shownEngine)) {
                        return;
                    }
                    if (error == null) {
                        checkEngine(version);
                    } else {
                        this.engineStatus.setForeground(ERROR);
                        this.engineStatus.setText("<html>Could not get the engine: " + error + "<br>" + MacOs.engineMissing(version) + "</html>");
                    }
                });
            }, "engine-download");
            download.setDaemon(true);
            download.start();
            return;
        }
        this.downloadingEngine = version;
        this.engineStatus.setForeground(BUSY);
        this.engineStatus.setText("Downloading the engine " + version + "...");
        Thread download2 = new Thread(() -> {
            String error = downloadEngine(version, folder);
            SwingUtilities.invokeLater(() -> {
                this.downloadingEngine = null;
                if (!version.equals(this.shownEngine)) {
                    return;
                }
                if (error == null) {
                    checkEngine(version);
                } else {
                    this.engineStatus.setForeground(ERROR);
                    this.engineStatus.setText("<html>Could not get the engine: " + error + "<br>Start the official BAR launcher once, it installs it too.</html>");
                }
            });
        }, "engine-download");
        download2.setDaemon(true);
        download2.start();
    }

    private String downloadEngine(String version, Path folder) {
        Path sevenZip = Platform.sevenZip(this.host.barData());
        if (sevenZip == null) {
            return Platform.sevenZipMissing();
        }
        String url = Platform.engineUrl(version, mapServiceUrl());
        try {
            try {
                Path archive = Files.createTempFile("recoil-" + version, ".7z", new FileAttribute[0]);
                HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(15L)).build();
                HttpResponse<InputStream> response = http.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() != 200) {
                    String str = "the download server answered " + response.statusCode();
                    if (archive != null) {
                        try {
                            Files.deleteIfExists(archive);
                        } catch (IOException e) {
                        }
                    }
                    return str;
                }
                long total = response.headers().firstValueAsLong("Content-Length").orElse(-1L);
                InputStream stream = (InputStream) response.body();
                try {
                    OutputStream out = Files.newOutputStream(archive, new OpenOption[0]);
                    try {
                        byte[] buffer = new byte[65536];
                        long done = 0;
                        long shown = 0;
                        while (true) {
                            int read = stream.read(buffer);
                            if (read <= 0) {
                                break;
                            }
                            out.write(buffer, 0, read);
                            done += (long) read;
                            if (done - shown > 2000000) {
                                shown = done;
                                long j = done / 1000000;
                                if (total > 0) {
                                    String str2 = " of " + (total / 1000000);
                                }
                                String text = "Downloading the engine " + version + "... " + j + version + " MB";
                                SwingUtilities.invokeLater(() -> {
                                    if (version.equals(this.downloadingEngine)) {
                                        this.engineStatus.setText(text);
                                    }
                                });
                            }
                        }
                        if (out != null) {
                            out.close();
                        }
                        if (stream != null) {
                            stream.close();
                        }
                        SwingUtilities.invokeLater(() -> {
                            this.engineStatus.setText("Unpacking the engine " + version + "...");
                        });
                        Path unpacking = folder.resolveSibling(String.valueOf(folder.getFileName()) + ".installing");
                        if (!Platform.unpackEngine(archive, unpacking, sevenZip)) {
                            if (archive != null) {
                                try {
                                    Files.deleteIfExists(archive);
                                } catch (IOException e2) {
                                }
                            }
                            return "unpacking failed";
                        }
                        Files.move(unpacking, folder, new CopyOption[0]);
                        if (archive != null) {
                            try {
                                Files.deleteIfExists(archive);
                            } catch (IOException e3) {
                            }
                        }
                        return null;
                    } catch (Throwable th) {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                if (0 != 0) {
                    try {
                        Files.deleteIfExists(null);
                    } catch (IOException e4) {
                    }
                }
                throw th5;
            }
        } catch (IOException | InterruptedException e5) {
            String message = e5.getMessage();
            if (0 != 0) {
                try {
                    Files.deleteIfExists(null);
                } catch (IOException e6) {
                }
            }
            return message;
        }
    }

    private boolean isGameInstalled(String gameName) {
        Path data = this.host.barData();
        try {
            Stream<Path> files = Files.walk(data.resolve("rapid"), 4, new FileVisitOption[0]);
            try {
                for (Path versions : files.filter(path -> {
                    return path.getFileName().toString().equals("versions.gz");
                }).toList()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(versions, new OpenOption[0])), StandardCharsets.UTF_8));
                    while (true) {
                        try {
                            String line = reader.readLine();
                            if (line != null) {
                                String[] fields = line.split(",", 4);
                                if (fields.length == 4 && fields[3].equals(gameName) && Files.isRegularFile(data.resolve("packages").resolve(fields[1] + ".sdp"), new LinkOption[0])) {
                                    reader.close();
                                    if (files != null) {
                                        files.close();
                                    }
                                    return true;
                                }
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
                    reader.close();
                }
                if (files != null) {
                    files.close();
                }
                return false;
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
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    private String download(String what, String name, JLabel status, String progressText) {
        Path data = this.host.barData();
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
                if (downloader == null) {
                    return "no engine with a pr-downloader found";
                }
                try {
                    ProcessBuilder builder = new ProcessBuilder(downloader.toString(), "--filesystem-writepath", data.toString(), what, name).redirectErrorStream(true);
                    builder.environment().put("PRD_HTTP_SEARCH_URL", this.settings.getProperty("mapservice", DEFAULT_MAP_SERVICE));
                    builder.environment().put("PRD_RAPID_USE_STREAMER", "false");
                    builder.environment().put("PRD_RAPID_REPO_MASTER", "https://repos-cdn.beyondallreason.dev/repos.gz");
                    Process process = builder.start();
                    Pattern percent = Pattern.compile("(\\d+)%");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                    while (true) {
                        try {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            Matcher matcher = percent.matcher(line);
                            if (matcher.find()) {
                                String progress = matcher.group(1);
                                SwingUtilities.invokeLater(() -> {
                                    if (name.equals(this.downloadingMap) || name.equals(this.downloadingGame)) {
                                        status.setText(progressText + progress + "%");
                                    }
                                });
                            }
                        } catch (Throwable th) {
                            try {
                                reader.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                        return e.getMessage();
                    }
                    reader.close();
                    if (process.waitFor() == 0) {
                        return null;
                    }
                    return "the download server does not have it";
                } catch (IOException | InterruptedException e) {
                    return e.getMessage();
                }
            } catch (Throwable th3) {
                if (engines != null) {
                    try {
                        engines.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (IOException e2) {
        }
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onState(String message) {
        if (message != null && message.startsWith("Could not join the battle")) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this.host.frame(), message, "Join game", 2);
            });
        }
        SwingUtilities.invokeLater(() -> {
            this.signingIn = false;
            updateBadge();
            boolean wasRegistering = this.registering;
            this.registering = false;
            if (this.client.isLoggedIn()) {
                if (this.pendingHash != null && this.staySignedIn) {
                    this.settings.setProperty("user", this.pendingUser);
                    this.settings.setProperty("login", this.pendingHash);
                    saveSettings();
                }
                this.pendingHash = null;
                if (this.loginDialog != null) {
                    this.loginDialog.setVisible(false);
                }
                if (this.registerDialog != null) {
                    this.registerDialog.setVisible(false);
                }
                this.reconnectDelay = 0;
                refreshAccount(null);
                loadChatHistory();
                rejoinChannels();
                checkBugAnswers();
                refreshBugInbox();
                if (pickedColour() >= 0) {
                    this.client.setColour(pickedColour());
                }
                if (flag("room.colour.ingame", false) && !flag("room.colour.cleaned", false)) {
                    BarInstall.removeSettings(this.host.barData(), List.of("SimpleTeamColors", "SimpleTeamColorsPlayerR", "SimpleTeamColorsPlayerG", "SimpleTeamColorsPlayerB"));
                    remember("room.colour.cleaned", "true");
                }
            } else if (message != null) {
                if (wasRegistering && this.registerDialog != null && this.registerDialog.isVisible()) {
                    this.registerStatus.setForeground(ERROR);
                    this.registerStatus.setText("<html><div style='width:340px'>" + message + "</div></html>");
                } else if (this.loginDialog != null && this.loginDialog.isVisible()) {
                    this.loginStatus.setForeground(ERROR);
                    this.loginStatus.setText("<html><div style='width:340px'>" + message + "</div></html>");
                } else {
                    append(this.chat, null, message, ERROR);
                }
                if (message.startsWith("The server closed the connection")) {
                    long lived = this.client.lastSessionMillis();
                    this.quickCloses = lived >= 60000 ? 1 : this.quickCloses + 1;
                    if (this.quickCloses >= 2 && lived >= 0) {
                        this.quickCloses = 0;
                        this.settings.remove("login");
                        saveSettings();
                        this.reconnectTimer.stop();
                        append(this.chat, null, "Signed in on another device: this launcher was signed out. Sign in here again to take the account back.", ERROR);
                        updateBadge();
                        return;
                    }
                } else {
                    this.quickCloses = 0;
                }
                if (message.startsWith("Signed in too often")) {
                    this.reconnectDelay = 10000;
                    scheduleReconnect();
                } else if (!message.startsWith("Login denied") && !message.startsWith("Wrong password") && !message.startsWith("That code") && !message.startsWith("Registration denied")) {
                    scheduleReconnect();
                }
            }
            if (message != null && this.client.isLoggedIn()) {
                append(this.client.getJoinedBattle() != null ? this.roomChat : this.chat, null, message, BUSY);
            }
            this.host.lobbyChanged();
        });
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onAgreement(String text) {
        SwingUtilities.invokeLater(() -> {
            showEmailCode(null);
        });
    }

    void showEmailCode(String problem) {
        JDialog dialog = new JDialog(this.host.frame(), "Confirm your email", System.getProperty("neroxis.launcher.screenshot") == null);
        JLabel heading = new JLabel("Check your email");
        heading.setFont(heading.getFont().deriveFont(1, 20.0f));
        heading.setForeground(BarLauncher.TEXT);
        JLabel explain = new JLabel("<html><div style='width:320px'>We sent a 6-digit code to the email address of your new account. Type it here to finish signing up.<br><br><span style='color:#8f96a3'>Nothing there after a minute? Look in the spam folder.</span></div></html>");
        explain.setForeground(BarLauncher.TEXT);
        JTextField code = new JTextField(8);
        code.setFont(code.getFont().deriveFont(1, 22.0f));
        code.setHorizontalAlignment(0);
        code.putClientProperty("JTextField.placeholderText", "000000");
        JLabel status = new JLabel(problem == null ? " " : problem);
        status.setForeground(ERROR);
        JButton confirm = new JButton("CONFIRM");
        confirm.putClientProperty("JButton.buttonType", "roundRect");
        confirm.setBackground(BarLauncher.ACCENT);
        confirm.setForeground(new Color(25, 18, 8));
        confirm.setFont(confirm.getFont().deriveFont(1, 14.0f));
        JButton cancel = new JButton("Cancel");
        cancel.putClientProperty("JButton.buttonType", "roundRect");
        final boolean[] answered = {false};
        confirm.addActionListener(e -> {
            String digits = code.getText().replaceAll("\\s", "");
            if (!digits.matches("\\d{6}")) {
                status.setText("The code has 6 digits");
                return;
            }
            answered[0] = true;
            dialog.dispose();
            this.client.confirmAgreement(digits);
        });
        code.addActionListener(e2 -> {
            confirm.doClick();
        });
        cancel.addActionListener(e3 -> {
            dialog.dispose();
        });
        dialog.addWindowListener(new WindowAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.20
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            public void windowClosed(WindowEvent e4) {
                if (!answered[0]) {
                    this.this$0.client.disconnect();
                    this.this$0.onState("Sign in cancelled");
                }
            }
        });
        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(confirm);
        JPanel middle = new JPanel(new BorderLayout(0, 10));
        middle.setOpaque(false);
        middle.add(explain, "North");
        middle.add(code, "Center");
        middle.add(status, "South");
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BarLauncher.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        root.add(heading, "North");
        root.add(middle, "Center");
        root.add(buttons, "South");
        dialog.setDefaultCloseOperation(2);
        dialog.setContentPane(root);
        dialog.pack();
        dialog.setSize(Math.max(400, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this.host.frame());
        dialog.setVisible(true);
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onUsersChanged() {
        Timer timer = this.usersRebuild;
        Objects.requireNonNull(timer);
        SwingUtilities.invokeLater(timer::restart);
    }

    private void rebuildUsers() {
        String selected = (String) this.playerList.getSelectedValue();
        List<LobbyClient.User> users = this.client.getUsers();
        if (!flag("show.bots", false)) {
            users.removeIf((v0) -> {
                return v0.bot();
            });
        }
        users.sort(Comparator.comparing(user -> {
            return Boolean.valueOf(user.bot());
        }).thenComparing(user2 -> {
            return user2.name.toLowerCase(Locale.ROOT);
        }));
        List<String> rows = new ArrayList<>();
        users.forEach(user3 -> {
            rows.add(user3.name);
        });
        this.playerModel.setRows(rows);
        if (selected != null && !selected.equals(this.playerList.getSelectedValue())) {
            this.playerList.setSelectedValue(selected, false);
        }
        this.friendList.repaint();
        rebuildOnline();
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onBattlesChanged() {
        Host host = this.host;
        Objects.requireNonNull(host);
        SwingUtilities.invokeLater(host::lobbyChanged);
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onChat(String channel, String user, String message, boolean action) {
        if (message.startsWith("@PROTOCOL_EXTENSIONS@")) {
            return;
        }
        LobbyClient.User sender = this.client.getUser(user);
        if (sender != null && sender.bot() && flag("filter.bots", true)) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            JTextPane pane = openChatTab("#" + channel);
            append(pane, action ? null : user, action ? "* " + user + " " + message : message, action ? BarLauncher.MUTED : null);
            markChatUnread("#" + channel);
        });
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onPrivateChat(String user, String message, boolean outgoing) {
        if (!outgoing && (message.startsWith("Code: ") || message.startsWith("Prime: "))) {
            SwingUtilities.invokeLater(() -> {
                this.profileStatus.setForeground((message.contains("added") || message.contains("is set") || message.contains("for life")) ? ONLINE : BUSY);
                this.profileStatus.setText("<html><div style='width:330px'>" + message.substring(message.indexOf(32) + 1) + "</div></html>");
                append(this.chat, null, message, BarLauncher.ACCENT);
                refreshAccount(null);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                LobbyClient.User sender = this.client.getUser(user);
                if (!outgoing && (sender == null || !sender.bot())) {
                    notifySound(user, false);
                }
                JTextPane pane = openChatTab("@" + user);
                append(pane, outgoing ? this.client.getUserName() : user, message, new Color(200, 160, 255));
                if (!outgoing) {
                    markChatUnread("@" + user);
                }
            });
        }
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onFriendsChanged() {
        SwingUtilities.invokeLater(() -> {
            List<String> rows = new ArrayList<>();
            List<String> requests = this.client.getFriendRequests();
            if (!requests.isEmpty()) {
                rows.add("#FRIEND REQUESTS");
                requests.forEach(name -> {
                    rows.add("?" + name);
                });
            }
            List<String> online = new ArrayList<>();
            List<String> offline = new ArrayList<>();
            for (String friend : this.client.getFriends()) {
                (this.client.getUser(friend) != null ? online : offline).add(friend);
            }
            rows.add("#ONLINE  " + online.size());
            rows.addAll(online);
            rows.add("#OFFLINE  " + offline.size());
            rows.addAll(offline);
            this.friendModel.setRows(rows);
            updateOnline();
        });
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onBattleRoomChanged() {
        SwingUtilities.invokeLater(this::updateRoom);
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onBattleChat(String user, String message, boolean action) {
        SwingUtilities.invokeLater(() -> {
            LobbyClient.Battle battle = this.client.getJoinedBattle();
            boolean fromHost = battle != null && battle.founder.equalsIgnoreCase(user);
            if (fromHost) {
                if (message.contains("this takes up to a minute")) {
                    LobbyClient.Battle joined = this.client.getJoinedBattle();
                    this.generatingSince = System.currentTimeMillis();
                    this.generatingFrom = joined != null ? joined.map : null;
                    updateRoom();
                } else if (message.contains("Map generation failed") || message.contains("New map:")) {
                    this.generatingSince = 0L;
                    updateRoom();
                }
                Matcher bossOn = Pattern.compile("Boss mode enabled for (\\S+)").matcher(message);
                Matcher hosts = Pattern.compile("host of this room is (\\S+)").matcher(message);
                Matcher bossOff = Pattern.compile("Boss mode removed from (\\S+)").matcher(message);
                if (bossOn.find()) {
                    this.bosses.add(bossOn.group(1).replaceAll("[.,)]+$", ""));
                    updateRoom();
                    this.boxShapeTimer.restart();
                } else if (hosts.find()) {
                    this.bosses.clear();
                    String[] arr$ = hosts.group(1).split(",");
                    for (String name : arr$) {
                        String clean = name.replaceAll("[.,)]+$", "");
                        if (!clean.isEmpty()) {
                            this.bosses.add(clean);
                        }
                    }
                    updateRoom();
                    this.boxShapeTimer.restart();
                } else if (bossOff.find()) {
                    this.bosses.remove(bossOff.group(1).replaceAll("[.,)]+$", ""));
                    updateRoom();
                } else if (message.contains("Boss mode disabled")) {
                    this.bosses.clear();
                    updateRoom();
                }
                Matcher called = Pattern.compile("^\\* (\\S+) called a vote for command \"(.+?)\"").matcher(message);
                if (called.find()) {
                    boolean mine = called.group(1).equalsIgnoreCase(this.client.getUserName());
                    String command = called.group(2).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                    if (command.startsWith("rename ")) {
                        command = "rename the room to “" + command.substring(7) + "”";
                    }
                    this.voteText.setText("<html>Vote: <span style='color:#F0962E'>" + command + "</span><span style='color:#8c94a3'> &nbsp;called by " + called.group(1) + "</span></html>");
                    this.voteBar.setVisible(!mine);
                    this.voteTimeout.setRepeats(false);
                    this.voteTimeout.restart();
                } else if (message.matches("^\\* Vote (for command .*(passed|failed).*|cancelled.*)")) {
                    this.voteBar.setVisible(false);
                }
            }
            append(this.roomChat, (action && fromHost) ? null : user, (!action || fromHost) ? message : "* " + message, fromHost ? CHAT_HOST : null);
        });
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onGameEnd(LobbyClient.Battle battle) {
        SwingUtilities.invokeLater(() -> {
            this.readyButton.setSelected(false);
            if (this.client.isPlayer()) {
                append(this.roomChat, null, "The game is over. Press Ready when you want to play another one.", BUSY);
            }
            updateRoom();
        });
    }

    @Override // com.faforever.neroxis.generator.bar.LobbyClient.Listener
    public void onGameStart(LobbyClient.Battle battle, String scriptPassword) {
        SwingUtilities.invokeLater(() -> {
            if (this.host.engineRunning()) {
                return;
            }
            if (!this.client.isPlayer() && !flag("autolaunch.spectating", true)) {
                append(this.roomChat, null, "The game started. Press Start to watch it.", BUSY);
            } else if (this.host.millisSinceEngineExit() < 90000) {
                append(this.roomChat, null, "A new game started in this room. Press START GAME to join it.", BUSY);
            } else {
                launchGame(battle, scriptPassword);
            }
        });
    }

    private void launchGame(LobbyClient.Battle battle, String scriptPassword) {
        Thread check = new Thread(() -> {
            String problem = mapCopyProblem(battle.map);
            SwingUtilities.invokeLater(() -> {
                if (problem != null) {
                    append(this.roomChat, null, problem, ERROR);
                    this.verifiedMaps.remove(battle.map.toLowerCase(Locale.ROOT));
                    checkMap(battle.map);
                    return;
                }
                startGame(battle, scriptPassword);
            });
        }, "map-check-start");
        check.setDaemon(true);
        check.start();
    }

    private void startGame(LobbyClient.Battle battle, String scriptPassword) {
        SwingUtilities.invokeLater(() -> {
            String script = "[game]\n{\n\thostip=" + battle.ip + ";\n\thostport=" + battle.port + ";\n\tishost=0;\n\tmyplayername=" + this.client.getUserName() + ";\n\tmypasswd=" + scriptPassword + ";\n}\n";
            Path file = this.host.barData().resolve("neroxis_scripts").resolve("online.txt");
            try {
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                Files.writeString(file, script, new OpenOption[0]);
                append(this.roomChat, null, this.host.startEngine(file, battle.engine) ? "The game is starting..." : "Could not start the engine", BUSY);
                sendOwnStatus();
            } catch (IOException e) {
                append(this.roomChat, null, "Could not write the start script: " + e.getMessage(), ERROR);
            }
        });
    }

    private static JTextPane chatPane() {
        JTextPane pane = new JTextPane() { // from class: com.faforever.neroxis.generator.bar.LobbyUi.21
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
        pane.setEditable(false);
        pane.setBackground(CHAT_BACKGROUND);
        pane.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return pane;
    }

    private static JComponent flatScroll(JComponent component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        component.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(31);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private static JComponent chatScroll(JTextPane pane) {
        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(BorderFactory.createLineBorder(CHAT_LINE));
        scroll.setBackground(CHAT_BACKGROUND);
        scroll.getViewport().setBackground(CHAT_BACKGROUND);
        scroll.setHorizontalScrollBarPolicy(31);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private static void chatInputStyle(JTextField input) {
        chatInputStyle(input, false);
    }

    private static void chatInputStyle(JTextField input, boolean alone) {
        input.setBackground(CHAT_BACKGROUND);
        input.setForeground(BarLauncher.TEXT);
        input.setCaretColor(BarLauncher.TEXT);
        input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(alone ? 1 : 0, 1, 1, 1, CHAT_LINE), BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    private static JComponent scroll(JComponent component) {
        Component jScrollPane = new JScrollPane(component);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane.setOpaque(false);
        jScrollPane.getViewport().setOpaque(false);
        component.setOpaque(false);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        BarLauncher.Surface surface = new BarLauncher.Surface(new BorderLayout(), BarLauncher.CARD, 16);
        surface.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        surface.add(jScrollPane, "Center");
        return surface;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.swing.text.BadLocationException */
    private void append(JTextPane pane, String user, String message, Color colour) {
        StyledDocument document = pane.getStyledDocument();
        try {
            insertLine(document, document.getLength(), System.currentTimeMillis(), user, message, colour);
            if (document.getLength() > 120000) {
                document.remove(0, 40000);
            }
            pane.setCaretPosition(document.getLength());
        } catch (BadLocationException e) {
        }
        if (pane == this.chatPanes.get(currentChatKey()) && this.dockTabs.getTabCount() > 1 && this.dockTabs.getSelectedIndex() == 1) {
            this.dockTabs.setTitleAt(1, "CHAT");
        }
    }

    private int insertLine(StyledDocument document, int offset, long when, String user, String message, Color colour) throws BadLocationException {
        SimpleAttributeSet time = new SimpleAttributeSet();
        StyleConstants.setForeground(time, new Color(110, 116, 130));
        String stamp = "[" + new SimpleDateFormat("HH:mm").format(new Date(when)) + "] ";
        document.insertString(offset, stamp, time);
        int offset2 = offset + stamp.length();
        if (user != null) {
            SimpleAttributeSet name = new SimpleAttributeSet();
            String speaker = user.substring(user.lastIndexOf(32) + 1);
            name.addAttribute(CHAT_USER, speaker);
            StyleConstants.setBold(name, true);
            StyleConstants.setForeground(name, personColour(speaker, nameColour(user)));
            document.insertString(offset2, user + "  ", name);
            offset2 += user.length() + 2;
        }
        SimpleAttributeSet text = new SimpleAttributeSet();
        StyleConstants.setForeground(text, colour != null ? colour : BarLauncher.TEXT);
        document.insertString(offset2, message + "\n", text);
        return offset2 + message.length() + 1;
    }

    private void loadChatHistory() {
        if (this.historyLoaded) {
            return;
        }
        this.historyLoaded = true;
        long signedIn = System.currentTimeMillis();
        String url = mapServiceUrl().replaceAll("/find/?$", "") + "/chat/history";
        Thread loader = new Thread(() -> {
            try {
                try {
                    HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10L)).build(), HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        Object patt0$temp = MiniJson.parse((String) response.body());
                        if (patt0$temp instanceof List) {
                            List<?> messages = (List) patt0$temp;
                            if (messages.isEmpty()) {
                                return;
                            }
                            SwingUtilities.invokeLater(() -> {
                                StyledDocument document = this.chat.getStyledDocument();
                                int offset = 0;
                                int shown = 0;
                                try {
                                    for (Object entry : messages) {
                                        if (entry instanceof Map) {
                                            Map<?, ?> message = (Map) entry;
                                            Object patt0$temp2 = message.get("time");
                                            if (patt0$temp2 instanceof Double) {
                                                Double seconds = (Double) patt0$temp2;
                                                long when = (long) (seconds.doubleValue() * 1000.0d);
                                                String user = String.valueOf(message.get("user"));
                                                LobbyClient.User sender = this.client.getUser(user);
                                                if (when < signedIn - 1500 && (sender == null || !sender.bot() || !flag("filter.bots", true))) {
                                                    boolean action = Boolean.TRUE.equals(message.get("action"));
                                                    String text = String.valueOf(message.get("text"));
                                                    offset = insertLine(document, offset, when, action ? null : user, action ? "* " + user + " " + text : text, action ? BarLauncher.MUTED : new Color(190, 196, 208));
                                                    shown++;
                                                }
                                            }
                                        }
                                    }
                                    if (shown > 0) {
                                        SimpleAttributeSet rule = new SimpleAttributeSet();
                                        StyleConstants.setForeground(rule, new Color(95, 102, 116));
                                        StyleConstants.setFontSize(rule, 11);
                                        document.insertString(offset, "───  earlier in #main  ───\n", rule);
                                    }
                                    this.chat.setCaretPosition(document.getLength());
                                } catch (BadLocationException e) {
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException | RuntimeException e2) {
            }
        }, "chat-history");
        loader.setDaemon(true);
        loader.start();
    }

    private Color personColour(String name, Color other) {
        if (name.equalsIgnoreCase(this.client.getUserName())) {
            return OWN_NAME;
        }
        Stream<String> stream = this.client.getPartyMembers().stream();
        Objects.requireNonNull(name);
        return stream.anyMatch(name::equalsIgnoreCase) ? PARTY_NAME : other;
    }

    private void installNameClicks(final JTextPane pane) {
        MouseAdapter mouse = new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.LobbyUi.22
            final /* synthetic */ LobbyUi this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            private String nameAt(MouseEvent event) {
                int position = pane.viewToModel2D(event.getPoint());
                if (position < 0) {
                    return null;
                }
                Object user = pane.getStyledDocument().getCharacterElement(position).getAttributes().getAttribute(LobbyUi.CHAT_USER);
                if (user instanceof String) {
                    String name = (String) user;
                    if (!name.isBlank() && !name.equalsIgnoreCase(this.this$0.client.getUserName())) {
                        return name;
                    }
                }
                return null;
            }

            public void mouseClicked(MouseEvent event) {
                String name = SwingUtilities.isLeftMouseButton(event) ? nameAt(event) : null;
                if (name != null) {
                    this.this$0.whisperTo(name);
                }
            }

            public void mouseMoved(MouseEvent event) {
                pane.setCursor(Cursor.getPredefinedCursor(nameAt(event) != null ? 12 : 2));
            }
        };
        pane.addMouseListener(mouse);
        pane.addMouseMotionListener(mouse);
    }

    private static Color nameColour(String name) {
        return Color.getHSBColor((Math.abs(name.toLowerCase(Locale.ROOT).hashCode()) % 360) / 360.0f, 0.45f, 0.95f);
    }
}
