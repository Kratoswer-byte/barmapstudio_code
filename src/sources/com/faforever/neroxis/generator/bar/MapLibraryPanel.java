package com.faforever.neroxis.generator.bar;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Libreria delle mappe installate in BAR, nello stile dello Studio: a sinistra ricerca e filtri, al centro la
 * griglia delle anteprime, a destra i dettagli della mappa scelta con le azioni.
 *
 * <p>Le mappe create con lo Studio (.sdz) si possono eliminare; quelle di BAR sono solo consultabili.</p>
 */
final class MapLibraryPanel extends JPanel {
    private static final Color BG_DARK = new Color(11, 14, 20);
    private static final Color CARD_BG = new Color(19, 24, 34);
    private static final Color CARD_INNER = new Color(26, 34, 48);
    private static final Color BORDER_COLOR = new Color(38, 50, 70);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_AMBER = new Color(255, 159, 28);
    private static final Color ACCENT_GREEN = new Color(0, 255, 136);
    private static final Color ACCENT_RED = new Color(255, 107, 107);
    private static final Color TEXT_MAIN = new Color(240, 244, 250);
    private static final Color TEXT_MUTED = new Color(140, 155, 180);
    private static final int CELL_W = 176;
    private static final int CELL_H = 204;
    private static final int THUMB = 156;

    /** Una mappa installata. */
    record Entry(Path archive, String name, String description, String author, String mapFile, boolean mine,
                 long size, long modified, int players, String skirmish) {
        BarMaps.InstalledMap installed() {
            return new BarMaps.InstalledMap(archive, name, "", "", mapFile);
        }
    }

    private final Consumer<File> onOpenInEditor;
    private BiConsumer<Path, String> scriptLauncher;

    private final List<Entry> all = new ArrayList<>();
    private final DefaultListModel<Entry> model = new DefaultListModel<>();
    private final JList<Entry> grid = new JList<>(model);
    private final Map<Path, BufferedImage> thumbs = new ConcurrentHashMap<>();
    private final Set<Path> pending = ConcurrentHashMap.newKeySet();
    private final ExecutorService loader = Executors.newFixedThreadPool(2, task -> {
        Thread thread = new Thread(task, "map-thumbnails");
        thread.setDaemon(true);
        return thread;
    });

    private final JTextField search = new JTextField();
    private final JToggleButton showAll = filterButton(I18n.t("lib.filter.all"));
    private final JToggleButton showMine = filterButton(I18n.t("lib.filter.mine"));
    private final JToggleButton showBar = filterButton(I18n.t("lib.filter.bar"));
    private final JComboBox<String> sort = new JComboBox<>(new String[]{I18n.t("lib.sort.recent"), I18n.t("lib.sort.name"), I18n.t("lib.sort.size")});
    private final JLabel count = new JLabel(" ");

    private final Preview preview = new Preview();
    private final JLabel title = new JLabel(" ");
    private final JLabel badge = new JLabel(" ");
    private final JPanel specs = new JPanel(new java.awt.GridBagLayout());
    private final JTextArea description = new JTextArea();
    private final JCheckBox showResources = new JCheckBox(I18n.t("lib.resources"), true);
    private final JButton playBtn = MapStudioPanel.createPrimaryButton(I18n.t("lib.play"), ACCENT_AMBER);
    private final JButton editBtn = MapStudioPanel.createPrimaryButton(I18n.t("lib.edit"), ACCENT_CYAN);
    private final JButton folderBtn = ghost(I18n.t("lib.folder"));
    private final JButton deleteBtn = ghost(I18n.t("lib.delete"));
    private final JLabel status = new JLabel(" ");
    private volatile long loadTicket;

    MapLibraryPanel(Consumer<File> onOpenInEditor) {
        super(new BorderLayout(10, 0));
        this.onOpenInEditor = onOpenInEditor;
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        add(buildFilters(), BorderLayout.WEST);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildDetails(), BorderLayout.EAST);
        showSelected();
    }

    void setScriptLauncher(BiConsumer<Path, String> scriptLauncher) {
        this.scriptLauncher = scriptLauncher;
    }

    // ------------------------------------------------------------------ costruzione

    private JComponent buildFilters() {
        JPanel card = card();
        card.setPreferredSize(new Dimension(250, 100));
        card.add(sectionTitle(I18n.t("lib.title")));
        card.add(Box.createVerticalStrut(10));

        search.putClientProperty("JTextField.placeholderText", I18n.t("lib.search"));
        search.putClientProperty("JTextField.showClearButton", true);
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        search.setAlignmentX(0f);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(null); }
            public void removeUpdate(DocumentEvent e) { applyFilter(null); }
            public void changedUpdate(DocumentEvent e) { applyFilter(null); }
        });
        card.add(search);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel(I18n.t("lib.show")));
        card.add(Box.createVerticalStrut(4));
        ButtonGroup group = new ButtonGroup();
        JPanel toggles = new JPanel(new GridLayout(3, 1, 0, 4));
        toggles.setOpaque(false);
        toggles.setAlignmentX(0f);
        toggles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        for (JToggleButton button : new JToggleButton[]{showAll, showMine, showBar}) {
            group.add(button);
            toggles.add(button);
            button.addActionListener(e -> applyFilter(null));
        }
        showAll.setSelected(true);
        card.add(toggles);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel(I18n.t("lib.sort")));
        card.add(Box.createVerticalStrut(4));
        sort.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        sort.setAlignmentX(0f);
        sort.addActionListener(e -> applyFilter(null));
        card.add(sort);
        card.add(Box.createVerticalStrut(12));

        count.setForeground(TEXT_MUTED);
        count.setFont(BarMapStudio.font(Font.PLAIN, 11));
        count.setAlignmentX(0f);
        card.add(count);
        card.add(Box.createVerticalGlue());

        JButton refresh = ghost(I18n.t("lib.refresh"));
        refresh.addActionListener(e -> refresh(null));
        JButton openMaps = ghost(I18n.t("lib.openMaps"));
        openMaps.addActionListener(e -> open(AppPaths.barData().resolve("maps")));
        for (JButton button : new JButton[]{refresh, openMaps}) {
            button.setAlignmentX(0f);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            card.add(button);
            card.add(Box.createVerticalStrut(6));
        }
        return card;
    }

    private JComponent buildGrid() {
        grid.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        grid.setVisibleRowCount(-1);
        grid.setFixedCellWidth(CELL_W);
        grid.setFixedCellHeight(CELL_H);
        grid.setBackground(BG_DARK);
        grid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grid.setCellRenderer(new CardRenderer());
        grid.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelected();
            }
        });
        grid.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && grid.getSelectedValue() != null) {
                    openInEditor();
                }
            }
        });
        JScrollPane scroll = new JScrollPane(grid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        // le schede si allargano quanto basta a riempire la riga, senza lasciare una striscia vuota a destra
        scroll.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = scroll.getViewport().getWidth();
                int columns = Math.max(1, width / CELL_W);
                int cell = Math.max(CELL_W, width / columns);
                if (grid.getFixedCellWidth() != cell) {
                    grid.setFixedCellWidth(cell);
                    grid.setFixedCellHeight(CELL_H + (cell - CELL_W));
                }
            }
        });
        return scroll;
    }

    private JComponent buildDetails() {
        JPanel card = card();
        card.setPreferredSize(new Dimension(380, 100));

        preview.setAlignmentX(0f);
        preview.setPreferredSize(new Dimension(356, 300));
        preview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        card.add(preview);
        card.add(Box.createVerticalStrut(6));
        showResources.setOpaque(false);
        showResources.setForeground(TEXT_MUTED);
        showResources.setFont(BarMapStudio.font(Font.PLAIN, 11));
        showResources.setAlignmentX(0f);
        showResources.addActionListener(e -> preview.repaint());
        card.add(showResources);
        card.add(Box.createVerticalStrut(8));

        badge.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        badge.setAlignmentX(0f);
        card.add(badge);
        title.setFont(BarMapStudio.fontCond(Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);
        title.setAlignmentX(0f);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        // specifiche e descrizione scorrono insieme: su schermi bassi i pulsanti restano sempre visibili
        JPanel sheet = new MapStudioPanel.ScrollableTrackWidthPanel();
        sheet.setLayout(new BoxLayout(sheet, BoxLayout.Y_AXIS));
        sheet.setOpaque(false);
        JLabel specsTitle = sectionTitle(I18n.t("lib.spec.title"));
        specsTitle.setFont(BarMapStudio.fontCond(Font.BOLD, 12));
        sheet.add(specsTitle);
        sheet.add(Box.createVerticalStrut(4));
        specs.setOpaque(false);
        specs.setAlignmentX(0f);
        sheet.add(specs);
        sheet.add(Box.createVerticalStrut(8));
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setOpaque(false);
        description.setForeground(TEXT_MUTED);
        description.setFont(BarMapStudio.font(Font.PLAIN, 12));
        description.setBorder(null);
        description.setAlignmentX(0f);
        sheet.add(description);
        JScrollPane text = new JScrollPane(sheet, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        text.setBorder(null);
        text.setOpaque(false);
        text.getViewport().setOpaque(false);
        text.getVerticalScrollBar().setUnitIncrement(16);
        text.setAlignmentX(0f);
        card.add(text);
        card.add(Box.createVerticalStrut(8));

        playBtn.addActionListener(e -> play());
        editBtn.addActionListener(e -> openInEditor());
        editBtn.setToolTipText(I18n.t("lib.edit.tip"));
        folderBtn.addActionListener(e -> showInFolder());
        deleteBtn.addActionListener(e -> delete());
        deleteBtn.setForeground(ACCENT_RED);
        for (JButton button : new JButton[]{playBtn, editBtn}) {
            button.setAlignmentX(0f);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.add(button);
            card.add(Box.createVerticalStrut(6));
        }
        JPanel small = new JPanel(new GridLayout(1, 2, 6, 0));
        small.setOpaque(false);
        small.setAlignmentX(0f);
        small.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        small.add(folderBtn);
        small.add(deleteBtn);
        card.add(small);
        card.add(Box.createVerticalStrut(6));
        status.setForeground(TEXT_MUTED);
        status.setFont(BarMapStudio.font(Font.PLAIN, 11));
        status.setAlignmentX(0f);
        card.add(status);
        return card;
    }

    private static JPanel card() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), new EmptyBorder(12, 12, 10, 12)));
        return card;
    }

    private static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BarMapStudio.fontCond(Font.BOLD, 14));
        label.setForeground(ACCENT_CYAN);
        label.setAlignmentX(0f);
        return label;
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BarMapStudio.font(Font.BOLD, 12));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(0f);
        return label;
    }

    private static JButton ghost(String text) {
        JButton button = new JButton(text);
        button.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        button.setBackground(CARD_INNER);
        button.setForeground(TEXT_MAIN);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true), new EmptyBorder(4, 8, 4, 8)));
        return button;
    }

    private static JToggleButton filterButton(String text) {
        JToggleButton button = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(isSelected() ? new Color(0, 229, 255, 35) : CARD_INNER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(isSelected() ? ACCENT_CYAN : BORDER_COLOR);
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(isSelected() ? ACCENT_CYAN : TEXT_MAIN);
                FontMetrics metrics = g2.getFontMetrics();
                g2.drawString(getText(), 10, (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2);
                g2.dispose();
            }
        };
        button.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ------------------------------------------------------------------ dati

    /** Rilegge la cartella mappe di BAR. Se indicata, alla fine seleziona quella mappa. */
    void refresh(Path select) {
        Path data = AppPaths.barData();
        Path keep = select != null ? select : (grid.getSelectedValue() != null ? grid.getSelectedValue().archive() : null);
        new Thread(() -> {
            List<Entry> found = scan(data);
            SwingUtilities.invokeLater(() -> {
                all.clear();
                all.addAll(found);
                // un'anteprima vecchia non deve restare se la mappa e' stata rifatta
                thumbs.keySet().removeIf(path -> found.stream().noneMatch(entry -> entry.archive().equals(path)));
                if (select != null) {
                    thumbs.remove(select);
                }
                applyFilter(keep);
            });
        }, "map-library").start();
    }

    private static List<Entry> scan(Path data) {
        List<Entry> result = new ArrayList<>();
        Path maps = data == null ? null : data.resolve("maps");
        if (maps == null || !Files.isDirectory(maps)) {
            return result;
        }
        Map<Path, BarMaps.InstalledMap> known = new HashMap<>();
        try {
            for (BarMaps.InstalledMap map : BarMaps.list(data)) {
                known.put(map.archive(), map);
            }
        } catch (RuntimeException ignored) {
        }
        try (Stream<Path> files = Files.list(maps)) {
            for (Path file : files.toList()) {
                String lower = file.getFileName().toString().toLowerCase(Locale.ROOT);
                boolean mine = lower.endsWith(".sdz");
                if (!mine && !lower.endsWith(".sd7")) {
                    continue;
                }
                BarMaps.InstalledMap map = known.get(file);
                String name = map != null ? map.name() : stripExtension(file.getFileName().toString());
                String text = map != null ? map.description() : "";
                String author = map != null ? map.author() : "";
                String mapFile = map != null ? map.mapFile() : "";
                int players = 0;
                String skirmish = null;
                if (mine) {
                    String info = readMapInfo(file);
                    if (info != null) {
                        name = firstGroup(info, "\\bname = \"([^\"]*)\"", name);
                        mapFile = firstGroup(info, "\\bmapfile = \"([^\"]*)\"", mapFile);
                        text = firstGroup(info, "\\bdescription = \"([^\"]*)\"", text);
                        author = firstGroup(info, "\\bauthor = \"([^\"]*)\"", author);
                        Matcher starts = Pattern.compile("startPos = \\{").matcher(info);
                        while (starts.find()) {
                            players++;
                        }
                        int from = info.indexOf("--[[skirmish");
                        int to = from < 0 ? -1 : info.indexOf("]]", from);
                        if (to > from) {
                            skirmish = info.substring(info.indexOf('\n', from) + 1, to);
                        }
                    }
                }
                if (mapFile == null || mapFile.isBlank()) {
                    continue; // BAR non la conosce ancora e non si riesce a leggerla
                }
                result.add(new Entry(file, name, text, author, mapFile, mine, Files.size(file), Files.getLastModifiedTime(file).toMillis(), players, skirmish));
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    private static String readMapInfo(Path archive) {
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            ZipEntry entry = zip.getEntry("mapinfo.lua");
            if (entry == null) {
                return null;
            }
            try (InputStream in = zip.getInputStream(entry)) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private static String firstGroup(String text, String pattern, String fallback) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        return matcher.find() && !matcher.group(1).isBlank() ? matcher.group(1) : fallback;
    }

    private static String stripExtension(String file) {
        int dot = file.lastIndexOf('.');
        return dot > 0 ? file.substring(0, dot) : file;
    }

    private void applyFilter(Path select) {
        Path keep = select != null ? select : (grid.getSelectedValue() != null ? grid.getSelectedValue().archive() : null);
        String query = search.getText().trim().toLowerCase(Locale.ROOT);
        List<Entry> shown = new ArrayList<>();
        for (Entry entry : all) {
            if (showMine.isSelected() && !entry.mine() || showBar.isSelected() && entry.mine()) {
                continue;
            }
            if (!query.isEmpty() && !entry.name().toLowerCase(Locale.ROOT).contains(query)
                && !entry.author().toLowerCase(Locale.ROOT).contains(query)
                && !entry.archive().getFileName().toString().toLowerCase(Locale.ROOT).contains(query)) {
                continue;
            }
            shown.add(entry);
        }
        Comparator<Entry> order = switch (sort.getSelectedIndex()) {
            case 1 -> Comparator.comparing(entry -> entry.name().toLowerCase(Locale.ROOT));
            case 2 -> Comparator.comparingLong(Entry::size).reversed();
            default -> Comparator.comparingLong(Entry::modified).reversed();
        };
        shown.sort(order);
        model.clear();
        int index = -1;
        for (Entry entry : shown) {
            if (entry.archive().equals(keep)) {
                index = model.size();
            }
            model.addElement(entry);
        }
        long mine = all.stream().filter(Entry::mine).count();
        count.setText(I18n.t("lib.count", String.valueOf(shown.size()), String.valueOf(all.size()), String.valueOf(mine)));
        if (index >= 0) {
            grid.setSelectedIndex(index);
            grid.ensureIndexIsVisible(index);
        } else if (!shown.isEmpty()) {
            grid.setSelectedIndex(0);
            grid.ensureIndexIsVisible(0);
        } else {
            showSelected();
        }
    }

    private Path minimapCache() {
        return AppPaths.userFolder("cache/minimaps");
    }

    private BufferedImage thumbnail(Entry entry) {
        BufferedImage ready = thumbs.get(entry.archive());
        if (ready == null && pending.add(entry.archive())) {
            loader.submit(() -> {
                try {
                    // la miniatura viene salvata: alle aperture successive la griglia si riempie subito
                    File small = minimapCache().resolve(entry.archive().getFileName() + ".thumb.png").toFile();
                    BufferedImage thumb = null;
                    if (small.isFile() && small.lastModified() >= entry.modified()) {
                        thumb = javax.imageio.ImageIO.read(small);
                    }
                    if (thumb == null) {
                        BufferedImage full = BarMaps.minimap(AppPaths.barData(), minimapCache(), entry.installed());
                        if (full != null) {
                            thumb = fit(full, THUMB * 2);
                            javax.imageio.ImageIO.write(thumb, "png", small);
                        }
                    }
                    if (thumb != null) {
                        thumbs.put(entry.archive(), thumb);
                        SwingUtilities.invokeLater(grid::repaint);
                    }
                } catch (Throwable ignored) {
                } finally {
                    pending.remove(entry.archive());
                }
            });
        }
        return ready;
    }

    /** Riduce l'immagine entro un quadrato, mantenendo le proporzioni. */
    private static BufferedImage fit(BufferedImage image, int side) {
        double scale = Math.min(side / (double) image.getWidth(), side / (double) image.getHeight());
        int w = Math.max(1, (int) Math.round(image.getWidth() * scale));
        int h = Math.max(1, (int) Math.round(image.getHeight() * scale));
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
        return out;
    }

    // ------------------------------------------------------------------ dettagli

    private void showSelected() {
        Entry entry = grid.getSelectedValue();
        long ticket = ++loadTicket;
        status.setText(" ");
        boolean any = entry != null;
        playBtn.setEnabled(any);
        editBtn.setEnabled(any);
        folderBtn.setEnabled(any);
        deleteBtn.setEnabled(any && entry.mine());
        deleteBtn.setToolTipText(any && !entry.mine() ? I18n.t("lib.delete.readonly") : null);
        if (!any) {
            title.setText(all.isEmpty() ? I18n.t("lib.empty") : I18n.t("lib.none"));
            badge.setText(" ");
            description.setText("");
            preview.show(null, null);
            fillSpecs(null, null, null);
            return;
        }
        title.setText(entry.name());
        title.setToolTipText(entry.archive().getFileName().toString());
        badge.setText(entry.mine() ? I18n.t("lib.badge.mine") : I18n.t("lib.badge.bar"));
        badge.setForeground(entry.mine() ? ACCENT_GREEN : ACCENT_AMBER);
        description.setText(entry.description().replaceAll("\\s*Regenerate with.*$", ""));
        description.setCaretPosition(0);
        fillSpecs(entry, null, null);
        preview.show(thumbs.get(entry.archive()), null);
        loader.submit(() -> {
            BufferedImage full = null;
            BarMaps.Marks marks = null;
            MapSpecs read = null;
            try {
                full = BarMaps.minimap(AppPaths.barData(), minimapCache(), entry.installed());
                marks = BarMaps.marks(AppPaths.barData(), minimapCache(), entry.installed());
                read = MapSpecs.read(AppPaths.barData(), minimapCache(), entry.archive(), entry.mapFile());
            } catch (Throwable ignored) {
            }
            BufferedImage image = full;
            BarMaps.Marks found = marks;
            MapSpecs mapSpecs = read;
            SwingUtilities.invokeLater(() -> {
                if (ticket != loadTicket) {
                    return; // nel frattempo e' stata scelta un'altra mappa
                }
                fillSpecs(entry, mapSpecs, found);
                double aspect = mapSpecs != null && mapSpecs.has("mapx") && mapSpecs.has("mapy") && mapSpecs.number("mapy") > 0
                    ? mapSpecs.number("mapx") / mapSpecs.number("mapy") : 0;
                preview.show(image != null ? image : thumbs.get(entry.archive()), found, aspect);
            });
        });
    }

    /** Riempie la tabella delle specifiche. Finche' l'archivio non e' stato letto mostra solo i dati del file. */
    private void fillSpecs(Entry entry, MapSpecs map, BarMaps.Marks marks) {
        specs.removeAll();
        if (entry != null) {
            int row = 0;
            if (map != null) {
                if (map.has("mapx") && map.has("mapy")) {
                    row = specRow(row, "lib.spec.size", number(map.number("mapx")) + " x " + number(map.number("mapy"))
                        + "  (" + number(map.number("mapx") * 512) + " x " + number(map.number("mapy") * 512) + " elmo)");
                }
                if (map.has("minheight") && map.has("maxheight")) {
                    row = specRow(row, "lib.spec.height", I18n.t("lib.spec.range", number(map.number("minheight")), number(map.number("maxheight"))));
                }
            }
            int players = map != null && map.has("starts") ? Math.round(map.number("starts")) : entry.players();
            if (players > 0) {
                row = specRow(row, "lib.spec.players", String.valueOf(players));
            }
            if (marks != null) {
                row = specRow(row, "lib.spec.metal", String.valueOf(marks.metal().size()));
                row = specRow(row, "lib.spec.geos", String.valueOf(marks.geos().size()));
            }
            if (map != null) {
                if (map.has("maxmetal")) {
                    row = specRow(row, "lib.spec.maxmetal", number(map.number("maxmetal")));
                }
                if (map.has("extractor")) {
                    row = specRow(row, "lib.spec.extractor", number(map.number("extractor")));
                }
                if (map.has("minwind") && map.has("maxwind")) {
                    row = specRow(row, "lib.spec.wind", I18n.t("lib.spec.range", number(map.number("minwind")), number(map.number("maxwind"))));
                }
                if (map.has("tidal")) {
                    row = specRow(row, "lib.spec.tidal", number(map.number("tidal")));
                }
                if (map.has("gravity")) {
                    row = specRow(row, "lib.spec.gravity", number(map.number("gravity")));
                }
                if (map.has("hardness")) {
                    row = specRow(row, "lib.spec.hardness", number(map.number("hardness")));
                }
                String water = map.flag("voidwater") ? I18n.t("lib.spec.water.void")
                    : map.has("waterdamage") && map.number("waterdamage") > 0 ? I18n.t("lib.spec.water.damage", number(map.number("waterdamage")))
                    : map.has("minheight") && map.number("minheight") >= 0 ? I18n.t("lib.spec.water.none")
                    : I18n.t("lib.spec.water.normal");
                row = specRow(row, "lib.spec.water", water);
                if (map.has("notdeformable")) {
                    row = specRow(row, "lib.spec.deform", map.flag("notdeformable") ? I18n.t("lib.spec.no") : I18n.t("lib.spec.yes"));
                }
            } else {
                row = specRow(row, "lib.spec.reading", "");
            }
            String author = map != null && map.has("author") ? map.text("author") : entry.author();
            if (!author.isBlank()) {
                row = specRow(row, "lib.spec.author", author);
            }
            if (map != null && map.has("version")) {
                row = specRow(row, "lib.spec.version", map.text("version"));
            }
            row = specRow(row, "lib.spec.file", entry.archive().getFileName().toString());
            row = specRow(row, "lib.spec.filesize", String.format(Locale.ROOT, "%.0f MB", Math.max(1, entry.size() / 1048576.0)));
            specRow(row, "lib.spec.date", DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(entry.modified())));
        }
        specs.revalidate();
        specs.repaint();
    }

    private int specRow(int row, String labelKey, String value) {
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.gridy = row;
        c.anchor = java.awt.GridBagConstraints.NORTHWEST;
        c.insets = new java.awt.Insets(1, 0, 1, 10);
        JLabel label = new JLabel(I18n.t(labelKey));
        label.setFont(BarMapStudio.font(Font.PLAIN, 11));
        label.setForeground(TEXT_MUTED);
        specs.add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.insets = new java.awt.Insets(1, 0, 1, 0);
        JLabel text = new JLabel(value);
        text.setFont(BarMapStudio.font(Font.BOLD, 11));
        text.setForeground(TEXT_MAIN);
        text.setToolTipText(value);
        text.setMinimumSize(new Dimension(40, 14));
        text.setPreferredSize(new Dimension(180, text.getPreferredSize().height));
        specs.add(text, c);
        return row + 1;
    }

    private static String number(float value) {
        return Math.abs(value - Math.round(value)) < 0.005f ? String.valueOf(Math.round(value)) : String.format(Locale.ROOT, "%.1f", value);
    }

    // ------------------------------------------------------------------ azioni

    private void openInEditor() {
        Entry entry = grid.getSelectedValue();
        if (entry != null && onOpenInEditor != null) {
            onOpenInEditor.accept(entry.archive().toFile());
        }
    }

    private void play() {
        Entry entry = grid.getSelectedValue();
        if (entry == null || scriptLauncher == null) {
            return;
        }
        String script = entry.skirmish() != null && entry.skirmish().contains("[game]") ? entry.skirmish() : genericSkirmish(entry.name());
        Path target = AppPaths.barData().resolve("_neroxis_skirmish.txt");
        try {
            Files.writeString(target, script);
        } catch (IOException denied) {
            // cartella di BAR protetta: il motore accetta lo script anche da un'altra cartella
            target = AppPaths.userFolder("exports").resolve("_neroxis_skirmish.txt");
            try {
                Files.writeString(target, script);
            } catch (IOException e) {
                status.setText(I18n.t("lib.play.failed"));
                return;
            }
        }
        status.setText(I18n.t("lib.play.starting", entry.name()));
        scriptLauncher.accept(target, null);
    }

    /** Partita uno contro l'IA su una mappa qualsiasi: ognuno sceglie il punto nella propria meta' della mappa. */
    private static String genericSkirmish(String mapName) {
        return "[game]\n{\n\tgametype=rapid://byar:test;\n\tmapname=" + mapName + ";\n\tishost=1;\n\thostip=127.0.0.1;\n\thostport=0;\n"
            + "\tmyplayername=Player;\n\tstartpostype=2;\n\tnumplayers=1;\n"
            + "\t[modoptions]\n\t{\n\t\tdraft_mode=disabled;\n\t}\n"
            + "\t[player0]\n\t{\n\t\tname=Player;\n\t\tteam=0;\n\t\tspectator=0;\n\t}\n"
            + "\t[ai0]\n\t{\n\t\tname=BARbarian 1;\n\t\tshortname=BARb;\n\t\tversion=stable;\n\t\tteam=1;\n\t\thost=0;\n\t}\n"
            + "\t[team0]\n\t{\n\t\tteamleader=0;\n\t\tallyteam=0;\n\t\tside=Armada;\n\t\thandicap=0;\n\t}\n"
            + "\t[team1]\n\t{\n\t\tteamleader=0;\n\t\tallyteam=1;\n\t\tside=Cortex;\n\t\thandicap=0;\n\t}\n"
            + "\t[allyteam0]\n\t{\n\t\tnumallies=0;\n\t\tstartrectleft=0;\n\t\tstartrecttop=0;\n\t\tstartrectright=0.3;\n\t\tstartrectbottom=1;\n\t}\n"
            + "\t[allyteam1]\n\t{\n\t\tnumallies=0;\n\t\tstartrectleft=0.7;\n\t\tstartrecttop=0;\n\t\tstartrectright=1;\n\t\tstartrectbottom=1;\n\t}\n"
            + "}\n";
    }

    private void showInFolder() {
        Entry entry = grid.getSelectedValue();
        if (entry == null) {
            return;
        }
        if (BarLocator.isWindows()) {
            try {
                new ProcessBuilder("explorer.exe", "/select,", entry.archive().toString()).start();
                return;
            } catch (IOException ignored) {
            }
        }
        open(entry.archive().getParent());
    }

    private void open(Path folder) {
        try {
            java.awt.Desktop.getDesktop().open(folder.toFile());
        } catch (Exception e) {
            status.setText(I18n.t("lib.folder.failed"));
        }
    }

    private void delete() {
        Entry entry = grid.getSelectedValue();
        if (entry == null || !entry.mine()) {
            return;
        }
        Object[] options = {I18n.t("lib.delete"), I18n.t("host.cancel")};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(this), I18n.t("lib.delete.confirm", entry.name()),
            I18n.t("lib.delete.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        if (answer != 0) {
            return;
        }
        try {
            Files.delete(entry.archive());
            thumbs.remove(entry.archive());
            refresh(null);
        } catch (IOException e) {
            status.setText(I18n.t("lib.delete.failed"));
        }
    }

    // ------------------------------------------------------------------ disegno

    /** Scheda di una mappa nella griglia. */
    private final class CardRenderer extends JComponent implements ListCellRenderer<Entry> {
        private Entry entry;
        private boolean selected;

        @Override
        public Component getListCellRendererComponent(JList<? extends Entry> list, Entry value, int index, boolean isSelected, boolean focus) {
            entry = value;
            selected = isSelected;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int x = 4, y = 4, w = getWidth() - 8, h = getHeight() - 8;
            g2.setColor(selected ? CARD_INNER : CARD_BG);
            g2.fillRect(x, y, w, h);
            g2.setColor(selected ? ACCENT_CYAN : BORDER_COLOR);
            g2.setStroke(new BasicStroke(selected ? 1.6f : 1f));
            g2.drawRect(x, y, w - 1, h - 1);

            int box = w - 12;
            int boxX = x + 6, boxY = y + 6;
            g2.setColor(BG_DARK);
            g2.fillRect(boxX, boxY, box, box);
            BufferedImage image = entry == null ? null : thumbnail(entry);
            if (image != null) {
                double scale = Math.min(box / (double) image.getWidth(), box / (double) image.getHeight());
                int iw = (int) Math.round(image.getWidth() * scale), ih = (int) Math.round(image.getHeight() * scale);
                g2.drawImage(image, boxX + (box - iw) / 2, boxY + (box - ih) / 2, iw, ih, null);
            } else {
                g2.setColor(BORDER_COLOR);
                g2.setFont(BarMapStudio.font(Font.PLAIN, 11));
                String wait = "...";
                g2.drawString(wait, boxX + (box - g2.getFontMetrics().stringWidth(wait)) / 2, boxY + box / 2);
            }
            if (entry != null) {
                if (entry.mine()) {
                    String tag = I18n.t("lib.tag.mine");
                    g2.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
                    int tw = g2.getFontMetrics().stringWidth(tag) + 10;
                    g2.setColor(new Color(11, 14, 20, 215));
                    g2.fillRect(boxX + 4, boxY + 4, tw, 16);
                    g2.setColor(ACCENT_GREEN);
                    g2.drawRect(boxX + 4, boxY + 4, tw - 1, 15);
                    g2.drawString(tag, boxX + 9, boxY + 16);
                }
                g2.setFont(BarMapStudio.font(Font.BOLD, 12));
                g2.setColor(TEXT_MAIN);
                g2.drawString(elide(entry.name(), g2.getFontMetrics(), box), boxX, boxY + box + 16);
                g2.setFont(BarMapStudio.font(Font.PLAIN, 10));
                g2.setColor(TEXT_MUTED);
                String sub = String.format(Locale.ROOT, "%.0f MB", Math.max(1, entry.size() / 1048576.0))
                    + (entry.players() > 0 ? "  ·  " + I18n.t("lib.players", String.valueOf(entry.players())) : "");
                g2.drawString(elide(sub, g2.getFontMetrics(), box), boxX, boxY + box + 30);
            }
            g2.dispose();
        }
    }

    private static String elide(String text, FontMetrics metrics, int width) {
        if (metrics.stringWidth(text) <= width) {
            return text;
        }
        String dots = "...";
        int end = text.length();
        while (end > 1 && metrics.stringWidth(text.substring(0, end) + dots) > width) {
            end--;
        }
        return text.substring(0, end) + dots;
    }

    /** Anteprima grande con i punti del metallo e i geyser. */
    private final class Preview extends JComponent {
        private BufferedImage image;
        private BarMaps.Marks marks;
        private double aspect;

        void show(BufferedImage image, BarMaps.Marks marks) {
            show(image, marks, 0);
        }

        /** @param aspect larghezza / altezza vera della mappa (le miniature di BAR sono sempre quadrate), 0 se non nota */
        void show(BufferedImage image, BarMaps.Marks marks, double aspect) {
            this.image = image;
            this.marks = marks;
            this.aspect = aspect;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setColor(BG_DARK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (image != null) {
                double shape = aspect > 0 ? aspect : image.getWidth() / (double) image.getHeight();
                int w = (int) Math.round(Math.min(getWidth(), getHeight() * shape)), h = (int) Math.round(w / shape);
                int x = (getWidth() - w) / 2, y = (getHeight() - h) / 2;
                g2.drawImage(image, x, y, w, h, null);
                if (marks != null && showResources.isSelected()) {
                    for (float[] spot : marks.metal()) {
                        dot(g2, x + spot[0] * w, y + spot[1] * h, 3.2f, new Color(190, 235, 255));
                    }
                    for (float[] geo : marks.geos()) {
                        dot(g2, x + geo[0] * w, y + geo[1] * h, 4.2f, ACCENT_AMBER);
                    }
                }
            }
            g2.setColor(BORDER_COLOR);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
        }

        private void dot(Graphics2D g2, float cx, float cy, float radius, Color color) {
            java.awt.geom.Ellipse2D.Float shape = new java.awt.geom.Ellipse2D.Float(cx - radius, cy - radius, radius * 2, radius * 2);
            g2.setColor(color);
            g2.fill(shape);
            g2.setColor(new Color(0, 0, 0, 200));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(shape);
        }
    }

}
