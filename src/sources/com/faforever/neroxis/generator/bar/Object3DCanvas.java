package com.faforever.neroxis.generator.bar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Viewport 3D vettoriale in Java2D puro con rendering Software real-time.
 * Supporta rotazione Orbit 360°, Pan su 2 assi, Zoom fluido, piano di terra
 * a griglia prospettica, Wireframe Blueprint e Faceted Shading con luce direzionale.
 */
public final class Object3DCanvas extends JPanel {

    public enum RenderMode {
        SHADED("Solido 3D"),
        WIREFRAME("Wireframe Tech");

        private final String label;
        RenderMode(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private static final Color BG_TOP = new Color(11, 14, 20);
    private static final Color BG_BOTTOM = new Color(16, 22, 32);
    private static final Color GRID_COLOR = new Color(30, 50, 70, 110);
    private static final Color GRID_AXIS = new Color(0, 190, 255, 140);
    private static final Color WIRE_COLOR = new Color(0, 220, 255);
    private static final Color WIRE_SECONDARY = new Color(255, 170, 40, 180);
    private static final Color SHADE_BASE = new Color(80, 140, 190);

    private ObjModel3D model;
    private RenderMode renderMode = RenderMode.SHADED;

    // Parametri Camera
    private double yaw = 45.0;   // rotazione orizzontale in gradi
    private double pitch = 25.0; // elevazione in gradi
    private double zoom = 1.0;
    private double panX = 0;
    private double panY = 0;

    // Mouse Dragging
    private Point dragStart = null;
    private boolean isRotating = false;
    private boolean isPanning = false;

    // Floating HUD Dock
    private JPanel hudDock;
    private JLabel infoLabel;

    public Object3DCanvas() {
        setLayout(null);
        setOpaque(true);
        setBackground(BG_TOP);
        setFocusable(true);

        initMouseControls();
        initHudDock();
    }

    public void setModel(ObjModel3D model) {
        this.model = model;
        resetCamera();
        updateInfoText();
        repaint();
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
        this.yaw = 45.0;
        this.pitch = 25.0;
        this.zoom = 1.0;
        this.panX = 0;
        this.panY = 0;
        updateInfoText();
        repaint();
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
                    yaw += dx * 0.6;
                    pitch = Math.max(-85.0, Math.min(85.0, pitch + dy * 0.6));
                    repaint();
                } else if (isPanning) {
                    panX += dx;
                    panY += dy;
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = e.getWheelRotation() < 0 ? 1.15 : 0.87;
                zoom = Math.max(0.15, Math.min(10.0, zoom * factor));
                updateInfoText();
                repaint();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
        addMouseWheelListener(adapter);
    }

    private void initHudDock() {
        hudDock = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
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

        JButton resetBtn = createHudButton("1:1", "Reimposta inquadratura e rotazione predefinita", e -> resetCamera());
        JButton zoomOutBtn = createHudButton("-", "Rimpicciolisci visuale [-]", e -> {
            zoom = Math.max(0.15, zoom * 0.85);
            updateInfoText();
            repaint();
        });
        JButton zoomInBtn = createHudButton("+", "Ingrandisci visuale [+]", e -> {
            zoom = Math.min(10.0, zoom * 1.18);
            updateInfoText();
            repaint();
        });

        JButton modeBtn = createHudButton("Modo", "Alterna tra visualizzazione Solida e Wireframe", e -> {
            renderMode = (renderMode == RenderMode.SHADED) ? RenderMode.WIREFRAME : RenderMode.SHADED;
            repaint();
        });

        infoLabel = new JLabel("Vertici: 0 | Facce: 0 | Zoom: 100%");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(160, 180, 205));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));

        hudDock.add(resetBtn);
        hudDock.add(zoomOutBtn);
        hudDock.add(zoomInBtn);
        hudDock.add(modeBtn);
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
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(text.length() > 2 ? 46 : 28, 22));
        btn.addActionListener(action);
        return btn;
    }

    private void updateInfoText() {
        if (infoLabel == null) return;
        int verts = model != null ? model.getVertexCount() : 0;
        int faces = model != null ? model.getFaceCount() : 0;
        int zoomPct = (int) Math.round(zoom * 100);
        infoLabel.setText(String.format(Locale.ROOT, "Vertici: %d | Facce: %d | Zoom: %d%%", verts, faces, zoomPct));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (hudDock != null) {
            Dimension ps = hudDock.getPreferredSize();
            hudDock.setBounds(14, Math.max(10, getHeight() - ps.height - 12), ps.width + 10, ps.height);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Sfondo a gradiente dark-tech
        Paint bgPaint = new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM);
        g2.setPaint(bgPaint);
        g2.fillRect(0, 0, w, h);

        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);
        double cosY = Math.cos(radYaw), sinY = Math.sin(radYaw);
        double cosP = Math.cos(radPitch), sinP = Math.sin(radPitch);

        int cx = w / 2 + (int) panX;
        int cy = h / 2 + (int) panY;
        double viewDist = 4.5;
        double scaleBase = Math.min(w, h) * 0.45;

        // 2. Disegno Piano Terra (Griglia prospettica a Y = 0)
        drawGroundGrid(g2, cx, cy, cosY, sinY, cosP, sinP, viewDist, scaleBase);

        // 3. Rendering Modello 3D (se presente)
        if (model != null && model.getVertexCount() > 0) {
            render3DModel(g2, cx, cy, cosY, sinY, cosP, sinP, viewDist, scaleBase);
        } else {
            g2.setColor(new Color(100, 125, 155));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            String msg = "Nessun modello 3D caricato. Seleziona o importa un file .obj";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, cx - fm.stringWidth(msg) / 2, cy);
        }

        // 4. Hotkey Guide in basso a destra
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(110, 130, 155));
        String guide = "[LMB Drag] Ruota 3D   [RMB Drag] Pan   [Wheel] Zoom";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(guide, w - fm.stringWidth(guide) - 16, h - 16);

        g2.dispose();
    }

    private void drawGroundGrid(Graphics2D g2, int cx, int cy, double cosY, double sinY, double cosP, double sinP, double viewDist, double scaleBase) {
        int gridSize = 6;
        float gridStep = 0.5f;

        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1.0f));

        for (int i = -gridSize; i <= gridSize; i++) {
            float coord = i * gridStep;
            // Linea parallela asse Z (da -gridSize*gridStep a +gridSize*gridStep su Z a X fisso)
            Point p1 = project(coord, 0.0f, -gridSize * gridStep, cx, cy, cosY, sinY, cosP, sinP, viewDist, scaleBase);
            Point p2 = project(coord, 0.0f, gridSize * gridStep, cx, cy, cosY, sinY, cosP, sinP, viewDist, scaleBase);
            if (p1 != null && p2 != null) {
                if (i == 0) {
                    g2.setColor(GRID_AXIS);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    g2.setColor(GRID_COLOR);
                } else {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            // Linea parallela asse X
            Point p3 = project(-gridSize * gridStep, 0.0f, coord, cx, cy, cosY, sinY, cosP, sinP, viewDist, scaleBase);
            Point p4 = project(gridSize * gridStep, 0.0f, coord, cx, cy, cosY, sinY, cosP, sinP, viewDist, scaleBase);
            if (p3 != null && p4 != null) {
                if (i == 0) {
                    g2.setColor(GRID_AXIS);
                    g2.drawLine(p3.x, p3.y, p4.x, p4.y);
                    g2.setColor(GRID_COLOR);
                } else {
                    g2.drawLine(p3.x, p3.y, p4.x, p4.y);
                }
            }
        }
    }

    private Point project(float x, float y, float z, int cx, int cy, double cosY, double sinY, double cosP, double sinP, double viewDist, double scaleBase) {
        // Rotazione Y (Yaw)
        double rx = x * cosY + z * sinY;
        double rz = -x * sinY + z * cosY;
        double ry = y;

        // Rotazione X (Pitch)
        double rry = ry * cosP - rz * sinP;
        double rrz = ry * sinP + rz * cosP;
        double rrx = rx;

        double zCam = rrz + viewDist;
        if (zCam < 0.2) return null; // Clipping retro camera

        double p = (scaleBase * zoom) / zCam;
        int sx = cx + (int) Math.round(rrx * p);
        int sy = cy - (int) Math.round(rry * p);
        return new Point(sx, sy);
    }

    private static class FaceDepth {
        int faceIndex;
        double depth;
        Point[] screenPts;
        float lightIntensity;
    }

    private void render3DModel(Graphics2D g2, int cx, int cy, double cosY, double sinY, double cosP, double sinP, double viewDist, double scaleBase) {
        float[] verts = model.getVertices();
        int[][] faces = model.getFaces();
        float[][] normals = model.getFaceNormals();
        int vertCount = model.getVertexCount();

        float cX = model.getCenterX();
        float cY = model.getCenterY();
        float cZ = model.getCenterZ();
        float normScale = 2.2f / model.getMaxDimension();

        // 1. Proiezione di tutti i vertici normalizzati
        Point[] screenVerts = new Point[vertCount];
        double[] camZ = new double[vertCount];

        for (int i = 0; i < vertCount; i++) {
            float vx = (verts[i * 3] - cX) * normScale;
            float vy = (verts[i * 3 + 1] - cY) * normScale;
            float vz = (verts[i * 3 + 2] - cZ) * normScale;

            // Yaw
            double rx = vx * cosY + vz * sinY;
            double rz = -vx * sinY + vz * cosY;
            double ry = vy;

            // Pitch
            double rry = ry * cosP - rz * sinP;
            double rrz = ry * sinP + rz * cosP;
            double rrx = rx;

            double zC = rrz + viewDist;
            camZ[i] = zC;
            if (zC >= 0.15) {
                double p = (scaleBase * zoom) / zC;
                screenVerts[i] = new Point(cx + (int) Math.round(rrx * p), cy - (int) Math.round(rry * p));
            }
        }

        // Vettore luce direzionale standard (angolata da sopra-destra)
        double lx = 0.55, ly = 0.75, lz = 0.35;
        double lLen = Math.sqrt(lx * lx + ly * ly + lz * lz);
        lx /= lLen; ly /= lLen; lz /= lLen;

        // 2. Calcolo facce e profondità per Depth Sorting
        List<FaceDepth> faceList = new ArrayList<>(faces.length);
        for (int f = 0; f < faces.length; f++) {
            int[] face = faces[f];
            if (face.length < 3) continue;

            double sumZ = 0;
            boolean allVisible = true;
            Point[] pts = new Point[face.length];

            for (int i = 0; i < face.length; i++) {
                int vi = face[i];
                if (vi < 0 || vi >= vertCount || screenVerts[vi] == null) {
                    allVisible = false;
                    break;
                }
                sumZ += camZ[vi];
                pts[i] = screenVerts[vi];
            }

            if (!allVisible) continue;

            FaceDepth fd = new FaceDepth();
            fd.faceIndex = f;
            fd.depth = sumZ / face.length;
            fd.screenPts = pts;

            // Calcolo orientamento e illuminazione normale
            float[] fn = (normals != null && f < normals.length) ? normals[f] : new float[]{0, 1, 0};
            // Rotazione normale
            double nrx = fn[0] * cosY + fn[2] * sinY;
            double nrz = -fn[0] * sinY + fn[2] * cosY;
            double nry = fn[1];
            double nrry = nry * cosP - nrz * sinP;
            double nrrz = nry * sinP + nrz * cosP;
            double nrrx = nrx;

            // Lambert dot product
            double dot = nrrx * lx + nrry * ly + nrrz * lz;
            fd.lightIntensity = (float) Math.max(0.18, Math.min(1.0, 0.25 + 0.75 * Math.max(0.0, dot)));

            faceList.add(fd);
        }

        // 3. Ordinamento dal più lontano al più vicino (Painter's Algorithm)
        faceList.sort((a, b) -> Double.compare(b.depth, a.depth));

        // 4. Disegno Facce
        if (renderMode == RenderMode.SHADED) {
            for (FaceDepth fd : faceList) {
                GeneralPath path = new GeneralPath();
                path.moveTo(fd.screenPts[0].x, fd.screenPts[0].y);
                for (int i = 1; i < fd.screenPts.length; i++) {
                    path.lineTo(fd.screenPts[i].x, fd.screenPts[i].y);
                }
                path.closePath();

                // Shading sfumato con luce
                int r = Math.min(255, (int) (SHADE_BASE.getRed() * fd.lightIntensity));
                int g = Math.min(255, (int) (SHADE_BASE.getGreen() * fd.lightIntensity));
                int b = Math.min(255, (int) (SHADE_BASE.getBlue() * fd.lightIntensity));

                g2.setColor(new Color(r, g, b, 235));
                g2.fill(path);

                // Sottile wireframe di contorno per evidenziare i bordi delle facce
                g2.setColor(new Color(Math.min(255, r + 40), Math.min(255, g + 40), Math.min(255, b + 50), 120));
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(path);
            }
        } else {
            // Modalità Wireframe Blueprint
            g2.setColor(WIRE_COLOR);
            g2.setStroke(new BasicStroke(1.1f));
            for (FaceDepth fd : faceList) {
                GeneralPath path = new GeneralPath();
                path.moveTo(fd.screenPts[0].x, fd.screenPts[0].y);
                for (int i = 1; i < fd.screenPts.length; i++) {
                    path.lineTo(fd.screenPts[i].x, fd.screenPts[i].y);
                }
                path.closePath();
                g2.draw(path);
            }
        }
    }
}
