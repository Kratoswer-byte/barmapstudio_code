package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Viewport 3D dello studio oggetti: usa lo stesso renderer software della vista mappa (z-buffer,
 * texture, luce), quindi il modello si vede con la sua texture e senza facce che si sovrappongono male.
 * Il pavimento e' una griglia in scala reale (elmi) con l'impronta dell'oggetto; accanto al modello
 * c'e' il vero Comandante di BAR come riferimento di grandezza.
 */
public final class Object3DCanvas extends JPanel {

    public enum RenderMode {
        SHADED(I18n.t("obj3d.solid")),
        WIREFRAME(I18n.t("obj.61"));

        private final String label;
        RenderMode(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private static final int BG_RGB = 0x0B0E14;
    private static final Color WIRE_COLOR = new Color(0, 220, 255);
    private static final int FLAT_DEFAULT = 0x508CBE;
    private static final double FOV = 40.0;

    // Comandante di riferimento: caricato una volta sola, in background, per tutti i viewport
    private static volatile SoftRenderer3D.RenderMesh commanderMesh;
    private static volatile SoftRenderer3D.Texture commanderTex;
    private static volatile float commanderHeight;
    private static volatile float commanderWidth;
    private static boolean commanderRequested;

    private ObjModel3D model;
    private ObjectEntity entity;
    private SoftRenderer3D.RenderMesh mesh;
    private SoftRenderer3D.Texture texture;
    private int wireTris;
    /** Modelli del gioco: l'origine e' gia' a terra e possono avere pezzi nascosti sotto, quindi non si riappoggiano. */
    private boolean gameModel;
    private float entityScale = 1.0f;
    private boolean showCommanderRef = true;
    private RenderMode renderMode = RenderMode.SHADED;
    private String loadingText;

    private SoftRenderer3D renderer;
    private final SoftRenderer3D.Camera cam = new SoftRenderer3D.Camera();

    // Pavimento a griglia (ricostruito solo quando cambiano dimensioni o impronta)
    private SoftRenderer3D.RenderMesh groundMesh;
    private SoftRenderer3D.Texture groundTex;
    private String groundKey = "";

    // Parametri camera
    private double yaw = 55.0;
    private double pitch = 22.0;
    private double zoom = 1.0;
    private double panX, panY, panZ;

    // Base della camera calcolata a ogni disegno (serve per le etichette e il wireframe)
    private double ex, ey, ez, focal;
    private final double[] fwd = new double[3], right = new double[3], up = new double[3];

    private Point dragStart = null;
    private boolean isRotating = false;
    private boolean isPanning = false;

    private JPanel hudDock;
    private JLabel infoLabel;
    private JButton cmdRefBtn;
    private JButton rotateBtn;
    private final javax.swing.Timer rotateTimer;

    public Object3DCanvas() {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(BG_RGB));
        setFocusable(true);

        rotateTimer = new javax.swing.Timer(33, e -> {
            if (isShowing() && mesh != null && !isRotating) {
                yaw -= 0.7;
                repaint();
            }
        });

        initMouseControls();
        initHudDock();
        requestCommander();
    }

    public void setModel(ObjModel3D model) {
        setModel(model, 1.0f);
    }

    public void setModel(ObjModel3D model, float scale) {
        show(null, model, model != null ? diffuseOf(model) : null, scale, true);
    }

    public void setEntity(ObjectEntity entity) {
        if (entity != null) {
            ObjModel3D m = entity.getModel3D();
            show(entity, m, m != null ? ObjectEntity.tinted(diffuseOf(m), entity.getTintRgb()) : null, entity.getScale(), true);
        } else {
            show(null, null, null, 1.0f, true);
        }
    }

    /**
     * Mostra un modello con la sua texture. doubleSided = true per le mesh importate, che possono avere
     * facce orientate al contrario; false per i modelli del gioco, che sono gia' corretti.
     */
    public void show(ObjectEntity entity, ObjModel3D model, BufferedImage diffuse, float scale, boolean doubleSided) {
        show(entity, model, diffuse, scale, doubleSided, null);
    }

    /** Come show, ma per un modello del gioco: usa le normali per vertice dell'S3O (superfici lisce). */
    public void showGameModel(ObjectEntity entity, S3OParser.S3OModel sm, BufferedImage diffuse, float scale) {
        show(entity, sm.model, diffuse, scale, false, smoothMesh(sm));
    }

    /** Mesh pronta per il renderer da un modello del gioco: normali lisce, facce orientate verso l'esterno. */
    static SoftRenderer3D.RenderMesh smoothMesh(S3OParser.S3OModel sm) {
        if (sm.vertexNormals == null || sm.vertexUVs == null) return null;
        int[][] faces = sm.model.getFaces();
        float[] v = sm.model.getVertices();
        int[] idx = new int[faces.length * 3];
        int o = 0;
        int flipped = 0;
        for (int[] f : faces) {
            if (f.length < 3) continue;
            // i modelli del gioco tengono sotto terra i pezzi che lo script mostra solo in certi casi
            // (medaglie e corona del Comandante, ad esempio): qui non si disegnano
            if (v[(f[0] * 3) + 1] < -4f && v[(f[1] * 3) + 1] < -4f && v[(f[2] * 3) + 1] < -4f) continue;
            // Il renderer considera "davanti" il verso orario e scarta l'altro lato. Ogni triangolo viene
            // quindi orientato con le normali del modello, altrimenti si vedrebbe l'interno al posto dell'esterno.
            int a = f[0] * 3, b = f[1] * 3, c = f[2] * 3;
            float ux = v[b] - v[a], uy = v[b + 1] - v[a + 1], uz = v[b + 2] - v[a + 2];
            float wx = v[c] - v[a], wy = v[c + 1] - v[a + 1], wz = v[c + 2] - v[a + 2];
            float gx = (uy * wz) - (uz * wy), gy = (uz * wx) - (ux * wz), gz = (ux * wy) - (uy * wx);
            float[] n = sm.vertexNormals;
            float outward = (gx * (n[a] + n[b] + n[c])) + (gy * (n[a + 1] + n[b + 1] + n[c + 1])) + (gz * (n[a + 2] + n[b + 2] + n[c + 2]));
            if (outward > 0) {
                flipped++;
                idx[o++] = f[0]; idx[o++] = f[2]; idx[o++] = f[1];
            } else {
                idx[o++] = f[0]; idx[o++] = f[1]; idx[o++] = f[2];
            }
        }
        if (Boolean.getBoolean("barstudio.debug3d")) {
            System.out.println("smoothMesh: triangoli " + (o / 3) + ", verso invertito " + flipped);
        }
        if (o != idx.length) idx = java.util.Arrays.copyOf(idx, o);
        float[] uv = sm.vertexUVs;
        // Le texture DDS del gioco sono salvate capovolte (il motore le legge dal basso): per quelle la V va
        // invertita, altrimenti ogni faccia pesca nel punto sbagliato dell'atlante. TGA e PNG sono gia' dritte.
        if (sm.tex1 != null && sm.tex1.toLowerCase(Locale.ROOT).endsWith(".dds")) {
            uv = uv.clone();
            for (int i = 1; i < uv.length; i += 2) uv[i] = 1.0f - uv[i];
        }
        return new SoftRenderer3D.RenderMesh(sm.model.getVertices(), sm.vertexNormals, uv, idx);
    }

    private void show(ObjectEntity entity, ObjModel3D model, BufferedImage diffuse, float scale, boolean doubleSided, SoftRenderer3D.RenderMesh ready) {
        this.entity = entity;
        this.model = model;
        this.entityScale = Math.max(0.001f, scale);
        this.loadingText = null;
        if (model != null && model.getFaceCount() > 0) {
            this.gameModel = !doubleSided;
            SoftRenderer3D.RenderMesh m = ready != null ? ready : SoftRenderer3D.RenderMesh.fromModel(model, doubleSided);
            this.wireTris = m.triCount;
            if (doubleSided) {
                int n = m.idx.length;
                int[] both = new int[n * 2];
                System.arraycopy(m.idx, 0, both, 0, n);
                for (int i = 0; i < n; i += 3) {
                    both[n + i] = m.idx[i];
                    both[n + i + 1] = m.idx[i + 2];
                    both[n + i + 2] = m.idx[i + 1];
                }
                m = new SoftRenderer3D.RenderMesh(m.pos, m.nrm, m.uv, both);
            }
            this.mesh = m;
            this.texture = diffuse != null ? new SoftRenderer3D.Texture(diffuse) : null;
        } else {
            this.mesh = null;
            this.texture = null;
        }
        resetCamera();
    }

    /** Svuota il viewport e mostra un messaggio (es. mentre il modello viene letto dal gioco). */
    public void setLoading(String text) {
        this.entity = null;
        this.model = null;
        this.mesh = null;
        this.texture = null;
        this.loadingText = text;
        updateInfoText();
        repaint();
    }

    public void setEntityScale(float scale) {
        this.entityScale = Math.max(0.001f, scale);
        updateInfoText();
        repaint();
    }

    public float getEntityScale() {
        return entityScale;
    }

    public void setShowCommanderRef(boolean show) {
        this.showCommanderRef = show;
        if (cmdRefBtn != null) {
            cmdRefBtn.setText(showCommanderRef ? I18n.t("obj.62") : I18n.t("obj.63"));
        }
        repaint();
    }

    public boolean isShowCommanderRef() {
        return showCommanderRef;
    }

    public ObjModel3D getModel() {
        return model;
    }

    public void setRenderMode(RenderMode mode) {
        this.renderMode = mode != null ? mode : RenderMode.SHADED;
        repaint();
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void resetCamera() {
        this.yaw = 55.0;
        this.pitch = 22.0;
        this.zoom = 1.0;
        this.panX = 0;
        this.panY = 0;
        this.panZ = 0;
        updateInfoText();
        repaint();
    }

    private float modelHeight() {
        return (gameModel ? model.getMaxY() : model.getMaxY() - model.getMinY()) * entityScale;
    }

    /** Texture da mostrare per una mesh importata: la prima immagine map_Kd, altrimenti la tavolozza dei colori Kd. */
    private static BufferedImage diffuseOf(ObjModel3D m) {
        BufferedImage img = m.firstDiffuse();
        return img != null ? img : m.getBakedTexture();
    }

    /**
     * Applica il colore squadra come fa il gioco: l'alpha della texture 1 dice quanto colore squadra
     * mettere in quel punto. Restituisce un'immagine opaca pronta per il viewport.
     */
    public static BufferedImage mixTeamColour(BufferedImage tex1, Color team) {
        if (tex1 == null) return null;
        int w = tex1.getWidth();
        int h = tex1.getHeight();
        int[] px = tex1.getRGB(0, 0, w, h, null, 0, w);
        int tr = team.getRed(), tg = team.getGreen(), tb = team.getBlue();
        // le texture del gioco sono scure perche' in partita si sommano riflessi e luci: qui si schiariscono
        int[] lift = new int[256];
        for (int i = 0; i < 256; i++) {
            lift[i] = Math.min(255, (int) Math.round(Math.pow(i / 255.0, 0.72) * 255.0 * 1.12));
        }
        for (int i = 0; i < px.length; i++) {
            int c = px[i];
            int a = c >>> 24;
            int r = lift[(c >> 16) & 255], g = lift[(c >> 8) & 255], b = lift[c & 255];
            if (a != 0) {
                r += ((tr - r) * a) / 255;
                g += ((tg - g) * a) / 255;
                b += ((tb - b) * a) / 255;
            }
            px[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        out.setRGB(0, 0, w, h, px, 0, w);
        return out;
    }

    private void requestCommander() {
        synchronized (Object3DCanvas.class) {
            if (commanderRequested) return;
            commanderRequested = true;
        }
        Thread t = new Thread(() -> {
            try {
                S3OParser.S3OModel sm = OfficialModels.loadFull("armcom");
                if (sm == null) return;
                SoftRenderer3D.Texture tex = null;
                try {
                    Path data = Platform.defaultData();
                    if (data != null && sm.tex1 != null && !sm.tex1.isBlank()) {
                        BufferedImage img = new ModelTextureCache(() -> data).loadGameTexture(sm.tex1, 1);
                        if (img != null) tex = new SoftRenderer3D.Texture(mixTeamColour(img, new Color(74, 158, 255)));
                    }
                } catch (Exception ignored) {
                }
                commanderHeight = sm.model.getMaxY();
                commanderWidth = sm.model.getMaxX() - sm.model.getMinX();
                commanderTex = tex;
                SoftRenderer3D.RenderMesh smooth = smoothMesh(sm);
                commanderMesh = smooth != null ? smooth : SoftRenderer3D.RenderMesh.fromModel(sm.model, false);
                SwingUtilities.invokeLater(this::repaint);
            } catch (Exception ignored) {
            }
        }, "commander-ref-loader");
        t.setDaemon(true);
        t.start();
    }

    private void initMouseControls() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                dragStart = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isRotating = true;
                } else if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isRotating = false;
                isPanning = false;
                dragStart = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart == null) return;
                int dx = e.getX() - dragStart.x;
                int dy = e.getY() - dragStart.y;
                dragStart = e.getPoint();

                if (isRotating) {
                    yaw -= dx * 0.5;
                    pitch = Math.max(2.0, Math.min(88.0, pitch + dy * 0.5));
                    repaint();
                } else if (isPanning) {
                    // sposta il punto guardato sul piano dello schermo, in elmi per pixel
                    double perPixel = (cam.dist * 2.0 * Math.tan(Math.toRadians(FOV * 0.5))) / Math.max(1, getHeight());
                    panX += ((-dx * right[0]) + (dy * up[0])) * perPixel;
                    panY += ((-dx * right[1]) + (dy * up[1])) * perPixel;
                    panZ += ((-dx * right[2]) + (dy * up[2])) * perPixel;
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    resetCamera();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = e.getWheelRotation() < 0 ? 1.15 : 0.87;
                zoom = Math.max(0.15, Math.min(12.0, zoom * factor));
                updateInfoText();
                repaint();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
        addMouseWheelListener(adapter);
    }

    private void initHudDock() {
        hudDock = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 20, 28, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(40, 56, 75));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        hudDock.setOpaque(false);

        JButton resetBtn = createHudButton("1:1", I18n.t("obj.64"), e -> resetCamera());
        JButton zoomOutBtn = createHudButton("-", I18n.t("obj.65"), e -> {
            zoom = Math.max(0.15, zoom * 0.85);
            updateInfoText();
            repaint();
        });
        JButton zoomInBtn = createHudButton("+", I18n.t("obj.66"), e -> {
            zoom = Math.min(12.0, zoom * 1.18);
            updateInfoText();
            repaint();
        });

        JButton modeBtn = createHudButton(I18n.t("obj3d.mode"), I18n.t("obj.67"), e -> {
            renderMode = (renderMode == RenderMode.SHADED) ? RenderMode.WIREFRAME : RenderMode.SHADED;
            repaint();
        });

        cmdRefBtn = createHudButton(I18n.t("obj.62"), I18n.t("obj.68"), e -> {
            setShowCommanderRef(!showCommanderRef);
        });

        rotateBtn = createHudButton(I18n.t("obj3d.rotate.off"), I18n.t("obj3d.rotate.tip"), e -> {
            if (rotateTimer.isRunning()) {
                rotateTimer.stop();
            } else {
                rotateTimer.start();
            }
            rotateBtn.setText(rotateTimer.isRunning() ? I18n.t("obj3d.rotate.on") : I18n.t("obj3d.rotate.off"));
        });

        infoLabel = new JLabel(I18n.t("obj.69"));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(160, 180, 205));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
        infoLabel.setVisible(false); // il testo viene disegnato in alto nel viewport, la barra resta solo per i pulsanti

        hudDock.add(resetBtn);
        hudDock.add(zoomOutBtn);
        hudDock.add(zoomInBtn);
        hudDock.add(modeBtn);
        hudDock.add(cmdRefBtn);
        hudDock.add(rotateBtn);
        hudDock.add(infoLabel);

        add(hudDock);
    }

    private JButton createHudButton(String text, String tooltip, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 160, 220));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 45, 62));
                } else {
                    g2.setColor(new Color(20, 28, 38));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(new Color(50, 70, 95));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(210, 230, 250));
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // larghezza sul testo piu' lungo che il pulsante puo' mostrare, cosi' non cambia misura quando commuta
        int textWidth = btn.getFontMetrics(btn.getFont()).stringWidth(text);
        btn.setPreferredSize(new Dimension(Math.max(28, textWidth + 22), 22));
        btn.addActionListener(action);
        return btn;
    }

    private void updateInfoText() {
        if (infoLabel == null) return;
        int verts = model != null ? model.getVertexCount() : 0;
        int faces = model != null ? model.getFaceCount() : 0;
        int zoomPct = (int) Math.round(zoom * 100);
        float scaledH = model != null ? modelHeight() : 0f;
        float scaledW = model != null ? (model.getMaxX() - model.getMinX()) * entityScale : 0f;

        infoLabel.setText(String.format(Locale.ROOT,
            I18n.t("obj.70"),
            verts, faces, zoomPct, scaledH, scaledW, entityScale));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (hudDock != null) {
            Dimension ps = hudDock.getPreferredSize();
            int avail = Math.max(60, getWidth() - 28);
            // se il viewport e' stretto i pulsanti vanno a capo su piu' righe invece di essere tagliati
            int rows = Math.max(1, (int) Math.ceil((ps.width + 10) / (double) avail));
            int height = (rows * 25) + 3;
            hudDock.setBounds(14, Math.max(10, getHeight() - height - 12), Math.min(avail, ps.width + 10), height);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        if (w < 8 || h < 8) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (mesh == null || model == null) {
            g2.setColor(new Color(100, 125, 155));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            String msg = loadingText != null ? loadingText : I18n.t("obj.71");
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            g2.dispose();
            return;
        }

        if (renderer == null) {
            renderer = new SoftRenderer3D(w, h);
        } else {
            renderer.resize(w, h);
        }
        renderer.clear(BG_RGB);

        float modelH = modelHeight();
        float modelW = (model.getMaxX() - model.getMinX()) * entityScale;
        float modelD = (model.getMaxZ() - model.getMinZ()) * entityScale;
        SoftRenderer3D.RenderMesh cmdMesh = showCommanderRef ? commanderMesh : null;
        // il Comandante sta di fianco al modello: si inquadra il gruppo, centrato sul suo punto medio
        double gap = 14.0;
        double cmdX = 0;
        double groupW = modelW;
        double centreX = 0;
        float groupH = modelH;
        if (cmdMesh != null) {
            cmdX = -((modelW * 0.5) + (commanderWidth * 0.5) + gap);
            groupW = modelW + commanderWidth + gap;
            centreX = ((modelW * 0.5) + (cmdX - (commanderWidth * 0.5))) * 0.5;
            groupH = Math.max(modelH, commanderHeight);
        }
        float frame = (float) Math.max(8.0, Math.max(groupH, Math.max(groupW, modelD)));

        cam.yawDeg = yaw;
        cam.pitchDeg = pitch;
        cam.fovY = FOV;
        // il campo visivo e' verticale: la distanza deve far stare sia l'altezza sia la larghezza del gruppo
        double visible = 2.0 * Math.tan(Math.toRadians(FOV * 0.5));
        double needV = Math.max(8.0, groupH) / visible;
        double needH = Math.max(8.0, Math.max(groupW, modelD)) / (visible * (w / (double) h));
        cam.dist = (Math.max(needV, needH) * 1.25) / zoom;
        cam.tx = centreX + panX;
        cam.ty = (groupH * 0.42) + panY;
        cam.tz = panZ;
        updateBasis(h);
        // luce da studio: arriva da dietro la camera, un po' dall'alto e di lato, cosi' il lato che si guarda e' sempre illuminato
        renderer.setAmbient(0.5f);
        renderer.setLightDir((-fwd[0] * 0.6) + (up[0] * 0.7) - (right[0] * 0.45),
            Math.max(0.25, (-fwd[1] * 0.6) + (up[1] * 0.7)),
            (-fwd[2] * 0.6) + (up[2] * 0.7) - (right[2] * 0.45));

        // pavimento
        int fx = entity != null ? entity.getFootprintX() : 0;
        int fz = entity != null ? entity.getFootprintZ() : 0;
        ensureGround(frame, fx, fz);
        renderer.drawMesh(groundMesh, groundTex, 0x101622, 0, 0, 0, 0, 1.0, cam);

        if (renderMode == RenderMode.SHADED) {
            renderer.drawMesh(mesh, texture, FLAT_DEFAULT, 0, 0, 0, 0, entityScale, cam);
        }
        if (cmdMesh != null) {
            // sagoma di riferimento: tinta unita scura, senza texture, cosi' non sembra un secondo modello caricato
            renderer.drawMesh(cmdMesh, null, 0x12384C, cmdX, 0, 0, 0, 1.0, cam);
        }

        g2.drawImage(renderer.present(), 0, 0, null);

        if (renderMode == RenderMode.WIREFRAME) {
            drawWireframe(g2);
        }
        if (cmdMesh != null) {
            drawReferenceWire(g2, cmdMesh, cmdX);
        }

        // etichette di altezza
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        Point top = project(0, modelH, 0);
        if (top != null) {
            drawTag(g2, top.x, top.y - 12, String.format(Locale.ROOT, I18n.t("obj3d.model"), modelH), new Color(255, 205, 50));
        }
        if (cmdMesh != null) {
            Point cmdTop = project(cmdX, commanderHeight, 0);
            if (cmdTop != null) {
                drawTag(g2, cmdTop.x, cmdTop.y - 12, String.format(Locale.ROOT, I18n.t("obj3d.cmd"), commanderHeight), new Color(0, 220, 255));
            }
        }

        // guida comandi in alto a destra (in basso c'e' la barra degli strumenti)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(110, 130, 155));
        g2.drawString(I18n.t("obj.72"), 14, 36);
        g2.setColor(new Color(160, 180, 205));
        g2.drawString(infoLabel.getText(), 14, 20);

        g2.dispose();
    }

    private void drawTag(Graphics2D g2, int x, int y, String label, Color colour) {
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(label) + 12;
        g2.setColor(new Color(11, 14, 20, 215));
        g2.fillRoundRect(x - (tw / 2), y - 9, tw, 18, 9, 9);
        g2.setColor(colour);
        g2.drawRoundRect(x - (tw / 2), y - 9, tw, 18, 9, 9);
        g2.drawString(label, x - (fm.stringWidth(label) / 2), y + 4);
    }

    /** Reticolo ciano sopra la sagoma del Comandante: solo le facce rivolte verso chi guarda. */
    private void drawReferenceWire(Graphics2D g2, SoftRenderer3D.RenderMesh ref, double offsetX) {
        g2.setColor(new Color(0, 220, 255, 95));
        g2.setStroke(new BasicStroke(0.7f));
        float[] pos = ref.pos;
        int n = pos.length / 3;
        int[] sx = new int[n];
        int[] sy = new int[n];
        boolean[] ok = new boolean[n];
        for (int i = 0; i < n; i++) {
            Point p = project(pos[i * 3] + offsetX, pos[(i * 3) + 1], pos[(i * 3) + 2]);
            if (p != null) {
                sx[i] = p.x;
                sy[i] = p.y;
                ok[i] = true;
            }
        }
        for (int t = 0; t < ref.triCount; t++) {
            int a = ref.idx[t * 3], b = ref.idx[(t * 3) + 1], c = ref.idx[(t * 3) + 2];
            if (!ok[a] || !ok[b] || !ok[c]) continue;
            // stesso criterio del renderer: area negativa = faccia rivolta verso la camera
            long area = ((long) (sx[b] - sx[a]) * (sy[c] - sy[a])) - ((long) (sx[c] - sx[a]) * (sy[b] - sy[a]));
            if (area >= 0) continue;
            g2.drawLine(sx[a], sy[a], sx[b], sy[b]);
            g2.drawLine(sx[b], sy[b], sx[c], sy[c]);
            g2.drawLine(sx[c], sy[c], sx[a], sy[a]);
        }
    }

    private void drawWireframe(Graphics2D g2) {
        g2.setColor(WIRE_COLOR);
        g2.setStroke(new BasicStroke(0.8f));
        float[] pos = mesh.pos;
        int n = pos.length / 3;
        int[] sx = new int[n];
        int[] sy = new int[n];
        boolean[] ok = new boolean[n];
        for (int i = 0; i < n; i++) {
            Point p = project(pos[i * 3] * entityScale, pos[(i * 3) + 1] * entityScale, pos[(i * 3) + 2] * entityScale);
            if (p != null) {
                sx[i] = p.x;
                sy[i] = p.y;
                ok[i] = true;
            }
        }
        // le mesh a doppia faccia hanno i triangoli ripetuti nella seconda meta' degli indici
        int tris = Math.min(mesh.triCount, wireTris);
        for (int t = 0; t < tris; t++) {
            int a = mesh.idx[t * 3], b = mesh.idx[(t * 3) + 1], c = mesh.idx[(t * 3) + 2];
            if (!ok[a] || !ok[b] || !ok[c]) continue;
            g2.drawLine(sx[a], sy[a], sx[b], sy[b]);
            g2.drawLine(sx[b], sy[b], sx[c], sy[c]);
            g2.drawLine(sx[c], sy[c], sx[a], sy[a]);
        }
    }

    /** Stessa camera del renderer: occhio = bersaglio + distanza lungo (yaw, pitch). */
    private void updateBasis(int h) {
        double y = Math.toRadians(cam.yawDeg);
        double p = Math.toRadians(cam.pitchDeg);
        double dx = Math.cos(p) * Math.cos(y);
        double dy = Math.sin(p);
        double dz = Math.cos(p) * Math.sin(y);
        ex = cam.tx + (dx * cam.dist);
        ey = cam.ty + (dy * cam.dist);
        ez = cam.tz + (dz * cam.dist);
        fwd[0] = -dx; fwd[1] = -dy; fwd[2] = -dz;
        double rl = Math.sqrt((fwd[2] * fwd[2]) + (fwd[0] * fwd[0]));
        if (rl < 1e-9) {
            right[0] = 1; right[1] = 0; right[2] = 0;
        } else {
            right[0] = fwd[2] / rl; right[1] = 0; right[2] = -fwd[0] / rl;
        }
        up[0] = (fwd[1] * right[2]) - (fwd[2] * right[1]);
        up[1] = (fwd[2] * right[0]) - (fwd[0] * right[2]);
        up[2] = (fwd[0] * right[1]) - (fwd[1] * right[0]);
        focal = (h * 0.5) / Math.tan(Math.toRadians(cam.fovY * 0.5));
    }

    private Point project(double wx, double wy, double wz) {
        double dx = wx - ex, dy = wy - ey, dz = wz - ez;
        double vz = (dx * fwd[0]) + (dy * fwd[1]) + (dz * fwd[2]);
        if (vz < 1.0) return null;
        double vx = (dx * right[0]) + (dy * right[1]) + (dz * right[2]);
        double vy = (dx * up[0]) + (dy * up[1]) + (dz * up[2]);
        return new Point((int) ((getWidth() * 0.5) + ((vx * focal) / vz)), (int) ((getHeight() * 0.5) - ((vy * focal) / vz)));
    }

    /**
     * Pavimento: griglia in elmi (passo 16 = un quadretto di impronta, raddoppiato per i modelli grandi)
     * con gli assi evidenziati e il rettangolo dell'impronta dell'oggetto.
     */
    private void ensureGround(float frame, int footX, int footZ) {
        int step = 16;
        while ((frame * 1.7f) / step > 14) {
            step *= 2;
        }
        int cells = Math.max(4, (int) Math.ceil((frame * 1.7f) / step));
        String key = step + ":" + cells + ":" + footX + ":" + footZ;
        if (key.equals(groundKey) && groundMesh != null) return;
        groundKey = key;
        float half = cells * step;

        int size = 768;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(19, 26, 38));
        g.fillRect(0, 0, size, size);
        float pxPerElmo = size / (half * 2.0f);
        g.setStroke(new BasicStroke(1.4f));
        for (int i = -cells; i <= cells; i++) {
            int p = Math.round((i * step + half) * pxPerElmo);
            g.setColor(i == 0 ? new Color(0, 190, 255) : new Color(52, 78, 106));
            g.drawLine(p, 0, p, size);
            g.drawLine(0, p, size, p);
        }
        if (footX > 0 && footZ > 0) {
            float fw = footX * 16 * pxPerElmo;
            float fh = footZ * 16 * pxPerElmo;
            java.awt.geom.Rectangle2D.Float foot = new java.awt.geom.Rectangle2D.Float((size - fw) / 2.0f, (size - fh) / 2.0f, fw, fh);
            g.setColor(new Color(255, 159, 28, 60));
            g.fill(foot);
            g.setColor(new Color(255, 159, 28));
            g.setStroke(new BasicStroke(2.2f));
            g.draw(foot);
        }
        g.dispose();
        groundTex = new SoftRenderer3D.Texture(img);

        // suddiviso in quadretti: il renderer scarta i triangoli con un vertice dietro la camera
        int n = 16;
        float[] pos = new float[(n + 1) * (n + 1) * 3];
        float[] nrm = new float[pos.length];
        float[] uv = new float[(n + 1) * (n + 1) * 2];
        for (int j = 0; j <= n; j++) {
            for (int i = 0; i <= n; i++) {
                int k = (j * (n + 1)) + i;
                float u = i / (float) n;
                float v = j / (float) n;
                pos[k * 3] = (u * 2.0f * half) - half;
                pos[(k * 3) + 1] = 0.0f;
                pos[(k * 3) + 2] = (v * 2.0f * half) - half;
                nrm[(k * 3) + 1] = 1.0f;
                uv[k * 2] = u * 0.999f;
                uv[(k * 2) + 1] = v * 0.999f;
            }
        }
        int[] idx = new int[n * n * 12];
        int o = 0;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                int a = (j * (n + 1)) + i, b = a + 1, c = a + n + 1, d = c + 1;
                // entrambi i versi: il pavimento resta visibile qualunque sia il verso considerato frontale
                idx[o++] = a; idx[o++] = c; idx[o++] = b;
                idx[o++] = b; idx[o++] = c; idx[o++] = d;
                idx[o++] = a; idx[o++] = b; idx[o++] = c;
                idx[o++] = b; idx[o++] = d; idx[o++] = c;
            }
        }
        groundMesh = new SoftRenderer3D.RenderMesh(pos, nrm, uv, idx);
    }
}
