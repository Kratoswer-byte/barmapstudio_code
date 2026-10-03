package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.map.SCMap;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Vista 3D proporzionata di MAPS (motore Java puro SoftRenderer3D).
 * Terreno in elmi BAR + entita realmente piazzate (custom + unita ufficiali via S3O).
 * Solo selezione + scala (tipo) + rotazione (istanza). Move resta nel 2D.
 */
public final class MapScene3DCanvas extends JPanel {
    private static final Color BG = new Color(11, 14, 20);
    private static final Color SEL = new Color(0, 229, 255);

    private final SoftRenderer3D.Camera cam = new SoftRenderer3D.Camera();
    private SoftRenderer3D renderer = new SoftRenderer3D(640, 480);
    private BufferedImage frame;

    private SCMap map;
    private BufferedImage groundPreview;
    private List<PlacedCustomEntity> entities = new ArrayList<>();
    private PlacedCustomEntity selected;
    private float[] terrainHeights;
    private int terrainW, terrainH;
    private float waterElmos = 0;

    private final ModelTextureCache texCache;
    private final Map<String, SoftRenderer3D.RenderMesh> meshCache = new HashMap<>();
    private final Map<String, SoftRenderer3D.Texture> diffCache = new HashMap<>();
    private S3OParser.S3OModel commanderRef;
    private boolean showCommanderRef = true;
    private boolean lowResWhileDrag = false;

    private Consumer<PlacedCustomEntity> selectionListener;
    private Runnable scaleChangeListener;
    private final ExecutorService bg = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "maps3d-tex");
        t.setDaemon(true);
        return t;
    });

    // HUD
    private JPanel hud;
    private JLabel infoLabel;
    private JSlider scaleSlider;
    private JSpinner scaleSpinner;
    private boolean syncingScale = false;

    private Point dragStart;
    private boolean orbiting, panning;

    public MapScene3DCanvas(java.util.function.Supplier<Path> dataSupplier) {
        texCache = new ModelTextureCache(dataSupplier);
        setLayout(null);
        setBackground(BG);
        setFocusable(true);
        initMouse();
        initKeys();
        buildHud();
        loadCommanderRefAsync();
    }

    public void setDataSupplier(java.util.function.Supplier<Path> s) { texCache.setDataSupplier(s); }

    private void initMouse() {
        MouseAdapter m = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isRightMouseButton(e)) {
                    panning = true; dragStart = e.getPoint();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    orbiting = true; dragStart = e.getPoint();
                    lowResWhileDrag = true;
                }
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (orbiting || panning) { orbiting = false; panning = false; lowResWhileDrag = false; repaint(); return; }
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown()) pickAt(e.getPoint());
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart == null) return;
                int dx = e.getX() - dragStart.x, dy = e.getY() - dragStart.y;
                dragStart = e.getPoint();
                if (orbiting) {
                    cam.yawDeg = (cam.yawDeg + dx * 0.5) % 360;
                    cam.pitchDeg = Math.max(5, Math.min(85, cam.pitchDeg + dy * 0.3));
                    repaint();
                } else if (panning) {
                    double s = cam.dist / 600.0;
                    cam.tx -= dx * 2.0 * s;
                    cam.tz -= dy * 2.0 * s;
                    repaint();
                }
            }
            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isShiftDown() && selected != null) {
                    // scala tipo +-5% per scatto
                    float sc = selected.getEntity().getScale();
                    sc *= (e.getWheelRotation() < 0 ? 1.05f : 0.95f);
                    applyScaleToType(selected.getEntity(), sc);
                    return;
                }
                cam.dist = Math.max(80, Math.min(6000, cam.dist * (e.getWheelRotation() < 0 ? 0.9 : 1.1)));
                repaint();
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
        addMouseWheelListener(m);
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutHud(); repaint(); }
        });
    }

    private void initKeys() {
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F') { frameSelection(); e.consume(); }
                else if (e.getKeyCode() == KeyEvent.VK_HOME) { frameAll(); e.consume(); }
                else if ((e.getKeyChar() == 'r' || e.getKeyChar() == 'R') && selected != null) {
                    float step = (float) Math.toRadians(15) * (e.isShiftDown() ? -1 : 1);
                    selected.setRotation(selected.getRotation() + step);
                    repaint();
                    if (scaleChangeListener != null) scaleChangeListener.run();
                    e.consume();
                }
            }
        });
    }

    private void buildHud() {
        hud = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        hud.setBackground(new Color(16, 22, 32, 230));
        hud.setBorder(BorderFactory.createLineBorder(new Color(38, 50, 70)));
        infoLabel = new JLabel("3D: click seleziona | Shift+rotella scala tipo | R ruota | F inquadra");
        infoLabel.setForeground(new Color(140, 155, 180));
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        scaleSlider = new JSlider(10, 500, 100);
        scaleSlider.setPreferredSize(new Dimension(110, 20));
        scaleSlider.setToolTipText("Scala del tipo selezionato (tutte le istanze)");
        scaleSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 50.0, 0.05));
        scaleSpinner.setPreferredSize(new Dimension(64, 22));
        JButton varBtn = new JButton("Crea variante");
        varBtn.setToolTipText("Clona il tipo con scala indipendente (nuovo id)");
        varBtn.addActionListener(ev -> createVariantFromSelection());
        JButton cmdBtn = new JButton(showCommanderRef ? "Cmd: ON" : "Cmd: OFF");
        cmdBtn.addActionListener(ev -> {
            showCommanderRef = !showCommanderRef;
            cmdBtn.setText(showCommanderRef ? "Cmd: ON" : "Cmd: OFF");
            repaint();
        });
        scaleSlider.addChangeListener(ev -> {
            if (syncingScale || selected == null || scaleSlider.getValueIsAdjusting()) return;
            applyScaleToType(selected.getEntity(), scaleSlider.getValue() / 100f);
        });
        scaleSpinner.addChangeListener(ev -> {
            if (syncingScale || selected == null) return;
            applyScaleToType(selected.getEntity(), ((Number) scaleSpinner.getValue()).floatValue());
        });
        hud.add(infoLabel); hud.add(new JLabel("Scala:")); hud.add(scaleSlider); hud.add(scaleSpinner); hud.add(varBtn); hud.add(cmdBtn);
        add(hud);
    }

    private void layoutHud() {
        if (hud == null) return;
        Dimension ps = hud.getPreferredSize();
        hud.setBounds(12, Math.max(8, getHeight() - ps.height - 10), Math.min(getWidth() - 24, ps.width), ps.height);
    }

    @Override public void doLayout() { super.doLayout(); layoutHud(); }

    // ---- data ----
    public void setMapAndGround(SCMap map, BufferedImage ground, List<PlacedCustomEntity> ents) {
        this.map = map;
        this.groundPreview = ground;
        this.entities = ents != null ? new ArrayList<>(ents) : new ArrayList<>();
        rebuildTerrain();
        frameAll();
        repaint();
    }

    public void setEntities(List<PlacedCustomEntity> ents) {
        this.entities = ents != null ? new ArrayList<>(ents) : new ArrayList<>();
        repaint();
    }

    public void setSelected(PlacedCustomEntity s) {
        this.selected = s;
        syncScaleUi();
        repaint();
    }

    public void setSelectionListener(Consumer<PlacedCustomEntity> l) { this.selectionListener = l; }
    public void setScaleChangeListener(Runnable r) { this.scaleChangeListener = r; }

    private void syncScaleUi() {
        syncingScale = true;
        try {
            if (selected != null) {
                float sc = selected.getEntity().getScale();
                scaleSlider.setValue(Math.max(10, Math.min(500, Math.round(sc * 100))));
                scaleSpinner.setValue((double) sc);
                infoLabel.setText(selText());
            } else infoLabel.setText("3D: click seleziona | Shift+rotella scala tipo | R ruota | F inquadra");
        } finally { syncingScale = false; }
    }

    private String selText() {
        if (selected == null) return "";
        ObjectEntity e = selected.getEntity();
        long n = entities.stream().filter(p -> p.getEntity() == e || p.getEntity().getId().equals(e.getId())).count();
        return e.getName() + " x" + String.format(java.util.Locale.ROOT, "%.2f", e.getScale())
            + " H" + Math.round(e.getScaledHeight()) + "m (" + n + " istanze) R=" + Math.round(Math.toDegrees(selected.getRotation())) + "deg";
    }

    private void rebuildTerrain() {
        terrainHeights = null;
        if (map == null || map.getHeightmap() == null) return;
        try {
            BufferedImage hm = map.getHeightmap();
            int w = hm.getWidth(), h = hm.getHeight();
            float hs = map.getHeightMapScale();
            float sea = map.getWaterElevation();
            float vs = 16f * 0.7f; // elmosPerCell(2)*heightScale
            terrainHeights = new float[w * h];
            java.awt.image.Raster r = hm.getRaster();
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    terrainHeights[y * w + x] = (r.getSample(x, y, 0) * hs - sea) * vs;
            terrainW = w; terrainH = h;
            waterElmos = 0; // acqua a quota 0 elmi dopo normalizzazione seaLevel
        } catch (Exception ex) { terrainHeights = null; }
    }

    public float getHeightElmosAt(float cellX, float cellZ) {
        if (terrainHeights == null) return 0;
        int x = Math.max(0, Math.min(terrainW - 1, Math.round(cellX)));
        int y = Math.max(0, Math.min(terrainH - 1, Math.round(cellZ)));
        return terrainHeights[y * terrainW + x];
    }

    private void frameAll() {
        if (map == null) { cam.tx = 0; cam.ty = 0; cam.tz = 0; cam.dist = 800; return; }
        float mw = map.getWidth() * 16f, mh = map.getHeight() * 16f;
        cam.tx = mw / 2.0; cam.tz = mh / 2.0; cam.ty = 0;
        cam.dist = Math.max(mw, mh) * 1.1;
    }

    private void frameSelection() {
        if (selected == null) { frameAll(); repaint(); return; }
        cam.tx = selected.getPosition().x() * 16.0;
        cam.tz = selected.getPosition().y() * 16.0;
        cam.ty = getHeightElmosAt(selected.getPosition().x(), selected.getPosition().y());
        cam.dist = Math.max(150, selected.getEntity().getScaledHeight() * 6);
        repaint();
    }

    // ---- scala/variante ----
    public void applyScaleToType(ObjectEntity type, float scale) {
        if (type == null) return;
        type.setScale(Math.max(0.01f, Math.min(50f, scale)));
        int fx = Math.max(1, Math.round(type.getScaledWidth() / 16f));
        int fz = Math.max(1, Math.min(50, Math.round(type.getScaledDepth() / 16f)));
        type.setFootprintX(fx); type.setFootprintZ(fz);
        syncScaleUi(); repaint();
        if (scaleChangeListener != null) scaleChangeListener.run();
    }

    public void createVariantFromSelection() {
        if (selected == null) return;
        ObjectEntity v = selected.getEntity().createVariant(selected.getEntity().getScale());
        ReadyObjectsManager.getInstance().addReadyObject(v);
        // riassegna solo istanza selezionata alla variante
        PlacedCustomEntity rep = new PlacedCustomEntity(v, selected.getPosition(), selected.getRotation());
        int idx = entities.indexOf(selected);
        if (idx >= 0) entities.set(idx, rep);
        selected = rep;
        syncScaleUi(); repaint();
        if (scaleChangeListener != null) scaleChangeListener.run();
        JOptionPane.showMessageDialog(this, "Variante creata: " + v.getId() + "\nSolo questa istanza usa la nuova scala.",
            "Variante oggetto", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---- picking ----
    private void pickAt(Point p) {
        PlacedCustomEntity best = null;
        double bestD = 24;
        for (PlacedCustomEntity e : entities) {
            java.awt.Point sp = renderer.project(e.getPosition().x() * 16.0,
                getHeightElmosAt(e.getPosition().x(), e.getPosition().y()) + e.getEntity().getScaledHeight() * 0.5,
                e.getPosition().y() * 16.0, cam);
            if (sp == null) continue;
            double d = sp.distance(p);
            if (d < bestD) { bestD = d; best = e; }
        }
        selected = best;
        syncScaleUi(); repaint();
        if (selectionListener != null) selectionListener.accept(selected);
    }

    // ---- model resolve ----
    private SoftRenderer3D.RenderMesh meshFor(ObjectEntity e) {
        String key = "mesh:" + e.getId();
        SoftRenderer3D.RenderMesh m = meshCache.get(key);
        if (m != null) return m.triCount == 0 ? null : m;
        ObjModel3D model = e.getModel3D();
        if (model == null && e.getOfficialUnit() != null) {
            model = loadOfficialModel(e);
            if (model != null) e.setModel3D(model);
        }
        if (model == null) { meshCache.put(key, new SoftRenderer3D.RenderMesh(new float[0], new float[0], new float[0], new int[0])); return null; }
        m = SoftRenderer3D.RenderMesh.fromModel(model);
        meshCache.put(key, m);
        return m.triCount == 0 ? null : m;
    }

    private SoftRenderer3D.Texture texFor(ObjectEntity e) {
        String key = "tex:" + e.getId();
        SoftRenderer3D.Texture t = diffCache.get(key);
        if (t != null) return t.w == 0 ? null : t;
        BufferedImage img = resolveDiffuse(e);
        if (img == null && e.getModel3D() != null && e.getModel3D().getBakedTexture() != null)
            img = e.getModel3D().getBakedTexture();
        if (img == null) { diffCache.put(key, new SoftRenderer3D.Texture(1, 1, 0xFF808080)); return null; }
        t = new SoftRenderer3D.Texture(img);
        diffCache.put(key, t);
        return t;
    }

    private BufferedImage resolveDiffuse(ObjectEntity e) {
        // 1. materiali OBJ con map_Kd
        if (e.getModel3D() != null && !e.getModel3D().getMaterials().isEmpty()) {
            for (ObjModel3D.Material m : e.getModel3D().getMaterials().values())
                if (m.diffuse != null) return m.diffuse;
        }
        // 2. texture path esplicito
        try {
            if (e.getTexturePath() != null && java.nio.file.Files.isRegularFile(e.getTexturePath()))
                return texCache.loadFileImage(e.getTexturePath().toFile());
            if (e.getObjFilePath() != null) {
                File dir = e.getObjFilePath().toFile().getParentFile();
                if (dir != null && dir.isDirectory()) {
                    File[] fs = dir.listFiles();
                    if (fs != null) for (File f : fs) {
                        String n = f.getName().toLowerCase();
                        if (n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".bmp") || n.endsWith(".tga")) {
                            BufferedImage img = texCache.loadFileImage(f);
                            if (img != null) return img;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        // 3. preview come fallback
        return e.getPreviewImage();
    }

    private ObjModel3D loadOfficialModel(ObjectEntity e) {
        if (e.getOfficialUnit() == null) return null;
        String id = e.getOfficialUnit().id().toLowerCase();
        String[] cands = {"objects3d/units/" + id + ".s3o", "objects3d/" + id + ".s3o"};
        Path data = null;
        try {
            // data supplier via texCache non esposto: rileggi da BarInstall default
            data = Platform.defaultData();
        } catch (Exception ignored) {}
        if (data == null) return null;
        for (String c : cands) {
            byte[] bytes = BarInstall.readGameBytes(data, c);
            if (bytes == null) continue;
            S3OParser.S3OModel sm = S3OParser.parseWithFaceUV(bytes);
            if (sm != null && sm.model.getFaceCount() > 0) {
                // texture tex1 in bg per uso futuro (cache)
                final String t1 = sm.tex1;
                if (t1 != null && !t1.isBlank()) bg.execute(() -> {
                    BufferedImage img = texCache.loadGameTexture(t1);
                    if (img != null) diffCache.put("tex:" + e.getId(), new SoftRenderer3D.Texture(img));
                });
                // default icon hook: unita ufficiali = MOBILE con icona
                e.setIconKind(ObjectEntity.IconKind.MOBILE);
                e.setShowIcon(true);
                return sm.model;
            }
        }
        return null;
    }

    private void loadCommanderRefAsync() {
        bg.execute(() -> {
            try {
                Path data = Platform.defaultData();
                if (data == null) return;
                byte[] b = BarInstall.readGameBytes(data, "objects3d/units/armcom.s3o");
                if (b == null) b = BarInstall.readGameBytes(data, "objects3d/armcom.s3o");
                if (b != null) {
                    commanderRef = S3OParser.parseWithFaceUV(b);
                    SwingUtilities.invokeLater(this::repaint);
                }
            } catch (Exception ignored) {}
        });
    }

    // ---- paint ----
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int W = getWidth(), H = getHeight();
        if (W < 16 || H < 16) return;
        int rw = lowResWhileDrag ? Math.max(16, W / 2) : W;
        int rh = lowResWhileDrag ? Math.max(16, H / 2) : H;
        renderer.resize(rw, rh);
        renderer.setBilinear(!lowResWhileDrag);
        renderer.clear(0x0B0E14);
        // terreno
        if (terrainHeights != null && map != null) {
            float cellElmos = 16f * ((float) map.getWidth() / Math.max(1, terrainW - 1));
            // origine 0,0; mappa centrata su cam target gia impostato
            renderer.drawTerrain(terrainHeights, terrainW, terrainH, cellElmos, groundPreview, cam, 0, 0);
        }
        // entita
        for (PlacedCustomEntity p : entities) {
            ObjectEntity e = p.getEntity();
            SoftRenderer3D.RenderMesh m = meshFor(e);
            if (m == null) {
                // fallback: box footprint
                continue;
            }
            SoftRenderer3D.Texture t = texFor(e);
            double wx = p.getPosition().x() * 16.0;
            double wz = p.getPosition().y() * 16.0;
            double wy = getHeightElmosAt(p.getPosition().x(), p.getPosition().y());
            // centra modello su footprint (S3O gia centrato; OBJ procedurali con minY=0 ok)
            renderer.drawMesh(m, t, 0x508CBE, wx, wy, wz, p.getRotation(), e.getScale(), cam);
        }
        // commander ref vicino a selezione
        if (showCommanderRef && commanderRef != null && selected != null) {
            SoftRenderer3D.RenderMesh cm = SoftRenderer3D.RenderMesh.fromModel(commanderRef.model);
            double wx = selected.getPosition().x() * 16.0 + selected.getEntity().getScaledWidth() * 0.5 + 40;
            double wz = selected.getPosition().y() * 16.0;
            renderer.drawMesh(cm, null, 0x9AA4B5, wx, getHeightElmosAt(selected.getPosition().x(), selected.getPosition().y()), wz, 0, 1.0, cam);
        }
        frame = renderer.present();
        Graphics2D g2 = (Graphics2D) g;
        if (lowResWhileDrag) g2.drawImage(frame, 0, 0, W, H, null);
        else g2.drawImage(frame, 0, 0, null);
        // overlay selezione: footprint + label
        for (PlacedCustomEntity p : entities) {
            if (p != selected) continue;
            java.awt.Point sp = renderer.project(p.getPosition().x() * 16.0,
                getHeightElmosAt(p.getPosition().x(), p.getPosition().y()) + p.getEntity().getScaledHeight() + 8,
                p.getPosition().y() * 16.0, cam);
            if (sp == null) continue;
            // adatta a risoluzione piena
            int sx = lowResWhileDrag ? sp.x * W / renderer.getWidth() : sp.x;
            int sy = lowResWhileDrag ? sp.y * H / renderer.getHeight() : sp.y;
            g2.setColor(SEL);
            g2.drawOval(sx - 8, sy - 8, 16, 16);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString(selText(), Math.max(4, Math.min(W - 320, sx + 12)), Math.max(14, sy));
        }
        if (entities.isEmpty()) {
            g2.setColor(new Color(140, 155, 180));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            String s = map == null ? "Genera il terreno in MAPS per vedere l'anteprima 3D" : "Nessun oggetto piazzato — posiziona oggetti nel 2D";
            g2.drawString(s, 16, 24);
        }
    }
}
