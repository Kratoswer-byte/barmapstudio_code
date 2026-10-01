package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.map.SymmetrySettings;
import com.faforever.neroxis.util.SymmetryUtil;
import com.faforever.neroxis.util.vector.Vector2;
import com.faforever.neroxis.util.vector.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class MapStudioCanvas extends JPanel {
    public enum Tool {
        SELECT,
        MEX,
        GEO,
        SPAWN,
        RECLAIM_ROCK,
        RECLAIM_TREE,
        RECLAIM_WRECK,
        ERASE
    }

    private BufferedImage groundPreview;
    private BufferedImage slopeOverlay;
    private boolean showSlopeMap = false;
    private float slopeOpacity = 0.85f;
    private Consumer<String> onTerrainHover = null;
    private SCMap map;
    private SymmetrySettings symmetrySettings = new SymmetrySettings(Symmetry.POINT2);
    private boolean symmetryEnabled = true;
    private Tool activeTool = Tool.SELECT;

    private double zoom = 1.0;
    private double panX = 0;
    private double panY = 0;
    private Point mousePos = new Point(0, 0);
    private Point dragStart = null;
    private boolean isPanning = false;

    private Object draggedEntity = null;
    private Object selectedEntity = null;
    private final List<Object> dragSiblings = new ArrayList<>();
    private final List<Integer> dragSiblingSymIndices = new ArrayList<>();

    private float currentMexMetal = 2.0f;
    private final Map<Marker, Float> mexMetalValues = new HashMap<>();
    private Consumer<Float> onMexSelected = null;

    private Runnable changeListener = null;

    private static final record CanvasState(
        List<Spawn> spawns,
        List<Marker> mexes,
        List<Marker> hydros,
        List<Prop> props,
        Map<Marker, Float> mexMetalValues
    ) {}
    private final Deque<CanvasState> undoStack = new ArrayDeque<>();
    private final Deque<CanvasState> redoStack = new ArrayDeque<>();

    public MapStudioCanvas() {
        setOpaque(true);
        setBackground(new Color(11, 14, 20));
        setFocusable(true);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (SwingUtilities.isMiddleMouseButton(e) || (SwingUtilities.isLeftMouseButton(e) && e.isAltDown())) {
                    isPanning = true;
                    dragStart = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Quick erase on right click
                    saveState();
                    eraseAt(e.getPoint());
                    repaint();
                    notifyChange();
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftClick(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isPanning) {
                    isPanning = false;
                    dragStart = null;
                    updateCursor();
                }
                if (draggedEntity != null) {
                    draggedEntity = null;
                    dragSiblings.clear();
                    dragSiblingSymIndices.clear();
                    notifyChange();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePos = e.getPoint();
                if (isPanning && dragStart != null) {
                    panX += (e.getX() - dragStart.x);
                    panY += (e.getY() - dragStart.y);
                    dragStart = e.getPoint();
                    repaint();
                    return;
                }
                if (draggedEntity != null && map != null) {
                    float[] norm = screenToNorm(e.getPoint());
                    if (norm != null) {
                        float wx = norm[0] * map.getWidth();
                        float wy = norm[1] * map.getHeight();
                        moveEntity(draggedEntity, wx, wy);
                        moveSiblingsSymmetrically(wx, wy);
                        repaint();
                    }
                    return;
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                if (map != null && onTerrainHover != null) {
                    float[] norm = screenToNorm(e.getPoint());
                    if (norm != null) {
                        float wx = norm[0] * map.getWidth();
                        float wy = norm[1] * map.getHeight();
                        float h = map.getHeightAtWorld(wx, wy);
                        float s = map.getSlopeAtWorld(wx, wy);
                        double deg = Math.toDegrees(Math.atan(s));
                        float wElev = map.getWaterElevation();
                        String desc;
                        if (wElev > -500f && h < wElev) {
                            desc = "Subacqueo (" + String.format(Locale.ROOT, "%.1fm sotto", wElev - h) + ")";
                        } else if (s < 0.22f) {
                            desc = "Veicoli OK & Costruibile";
                        } else if (s < 0.35f) {
                            desc = "Veicoli Lenti / No Costruz.";
                        } else if (s < 0.65f) {
                            desc = "Solo Kbot / Bot Bipedi";
                        } else {
                            desc = "Invalicabile / Dirupo";
                        }
                        String info = String.format(Locale.ROOT, "Pos: [%d, %d] | Quota: %.1fm | Pendenza: %.2f (%.1f°) | %s", Math.round(wx), Math.round(wy), h, s, deg, desc);
                        onTerrainHover.accept(info);
                    } else {
                        onTerrainHover.accept("");
                    }
                }
                repaint();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldZoom = zoom;
                if (e.getWheelRotation() < 0) {
                    zoom = Math.min(4.0, zoom * 1.15);
                } else {
                    zoom = Math.max(1.0, zoom / 1.15);
                }
                if (zoom == 1.0) {
                    panX = 0;
                    panY = 0;
                } else {
                    // Center zoom on mouse
                    double factor = zoom / oldZoom;
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    panX = (panX - (e.getX() - cx)) * factor + (e.getX() - cx);
                    panY = (panY - (e.getY() - cy)) * factor + (e.getY() - cy);
                }
                repaint();
            }
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);
    }

    public void setChangeListener(Runnable r) {
        this.changeListener = r;
    }

    private void notifyChange() {
        if (changeListener != null) {
            changeListener.run();
        }
    }

    public void setMapAndGround(SCMap map, BufferedImage ground, SymmetrySettings symmetrySettings) {
        setMapAndGround(map, ground, null, symmetrySettings);
    }

    public void setMapAndGround(SCMap map, BufferedImage ground, BufferedImage slopeOverlay, SymmetrySettings symmetrySettings) {
        this.map = map;
        this.groundPreview = ground;
        if (slopeOverlay != null) {
            this.slopeOverlay = slopeOverlay;
        } else if (map != null) {
            this.slopeOverlay = GroundGenerator.generateSlopeOverlayImage(map, map.getWaterElevation());
        } else {
            this.slopeOverlay = null;
        }
        this.symmetrySettings = symmetrySettings != null ? symmetrySettings : new SymmetrySettings(Symmetry.POINT2);
        this.undoStack.clear();
        this.redoStack.clear();
        this.mexMetalValues.clear();
        if (map != null) {
            for (Marker m : map.getMexes()) {
                mexMetalValues.put(m, getMexMetal(m));
            }
        }
        this.draggedEntity = null;
        this.selectedEntity = null;
        this.dragSiblings.clear();
        this.dragSiblingSymIndices.clear();
        this.zoom = 1.0;
        this.panX = 0;
        this.panY = 0;
        repaint();
        notifyChange();
    }

    public void setShowSlopeMap(boolean show) {
        this.showSlopeMap = show;
        if (show && this.slopeOverlay == null && map != null) {
            updateSlopeOverlay();
        }
        repaint();
    }

    public boolean isShowSlopeMap() {
        return this.showSlopeMap;
    }

    public void toggleSlopeMap() {
        setShowSlopeMap(!this.showSlopeMap);
    }

    public void setSlopeOpacity(float opacity) {
        this.slopeOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        repaint();
    }

    public float getSlopeOpacity() {
        return this.slopeOpacity;
    }

    public void setSlopeOverlay(BufferedImage overlay) {
        this.slopeOverlay = overlay;
        repaint();
    }

    public BufferedImage getSlopeOverlay() {
        return this.slopeOverlay;
    }

    public void updateSlopeOverlay() {
        if (map != null) {
            this.slopeOverlay = GroundGenerator.generateSlopeOverlayImage(map, map.getWaterElevation());
            repaint();
        }
    }

    public void setOnTerrainHover(Consumer<String> listener) {
        this.onTerrainHover = listener;
    }

    public void setOnMexSelected(Consumer<Float> listener) {
        this.onMexSelected = listener;
    }

    public void setCurrentMexMetal(float val) {
        this.currentMexMetal = Math.max(0.5f, Math.min(10.0f, val));
        if (selectedEntity instanceof Marker m && map != null && map.getMexes().contains(m)) {
            setMexMetal(m, this.currentMexMetal);
            repaint();
            notifyChange();
        }
    }

    public float getCurrentMexMetal() {
        return currentMexMetal;
    }

    public float getMexMetal(Marker m) {
        Float v = mexMetalValues.get(m);
        if (v != null) return v;
        String id = m.getId();
        if (id != null && id.contains("_val_")) {
            try {
                int start = id.indexOf("_val_") + 5;
                int end = id.indexOf('_', start);
                return Float.parseFloat((end != -1) ? id.substring(start, end) : id.substring(start));
            } catch (Exception ignored) {}
        }
        return 2.0f;
    }

    public void setMexMetal(Marker m, float val) {
        mexMetalValues.put(m, val);
        m.setId("mex_val_" + String.format(Locale.ROOT, "%.1f", val) + "_" + UUID.randomUUID().toString().substring(0, 8));
    }

    public SCMap getMap() {
        return map;
    }

    public BufferedImage getGroundPreview() {
        return groundPreview;
    }

    public void setTool(Tool tool) {
        this.activeTool = tool;
        updateCursor();
        repaint();
    }

    public Tool getTool() {
        return activeTool;
    }

    public void setSymmetryEnabled(boolean enabled) {
        this.symmetryEnabled = enabled;
        repaint();
    }

    public boolean isSymmetryEnabled() {
        return symmetryEnabled;
    }

    public void setSymmetrySettings(SymmetrySettings settings) {
        this.symmetrySettings = settings;
        repaint();
    }

    public SymmetrySettings getSymmetrySettings() {
        return symmetrySettings;
    }

    private void updateCursor() {
        if (activeTool == Tool.SELECT) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (activeTool == Tool.ERASE) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    public void resetZoom() {
        zoom = 1.0;
        panX = 0;
        panY = 0;
        repaint();
    }

    public void zoomIn() {
        zoom = Math.min(4.0, zoom * 1.25);
        repaint();
    }

    public void zoomOut() {
        zoom = Math.max(1.0, zoom / 1.25);
        if (zoom <= 1.0) {
            panX = 0;
            panY = 0;
        }
        repaint();
    }

    private Rectangle getMapBounds() {
        int mapW = map != null ? map.getWidth() : 512;
        int mapH = map != null ? map.getHeight() : 512;
        double aspect = (double) mapW / mapH;

        int availW = (int) (getWidth() * 0.94);
        int availH = (int) (getHeight() * 0.94);
        int bWidth, bHeight;
        if (availW / aspect <= availH) {
            bWidth = (int) (availW * zoom);
            bHeight = (int) ((availW / aspect) * zoom);
        } else {
            bHeight = (int) (availH * zoom);
            bWidth = (int) ((availH * aspect) * zoom);
        }
        int cx = getWidth() / 2 + (int) panX;
        int cy = getHeight() / 2 + (int) panY;
        return new Rectangle(cx - bWidth / 2, cy - bHeight / 2, bWidth, bHeight);
    }

    private float[] screenToNorm(Point pt) {
        Rectangle b = getMapBounds();
        if (pt.x < b.x || pt.x > b.x + b.width || pt.y < b.y || pt.y > b.y + b.height) {
            return null;
        }
        float nx = (float) (pt.x - b.x) / b.width;
        float ny = (float) (pt.y - b.y) / b.height;
        return new float[]{Math.max(0f, Math.min(1f, nx)), Math.max(0f, Math.min(1f, ny))};
    }

    private Point normToScreen(float nx, float ny) {
        Rectangle b = getMapBounds();
        return new Point(b.x + Math.round(nx * b.width), b.y + Math.round(ny * b.height));
    }

    private void saveState() {
        if (map == null) return;
        undoStack.push(new CanvasState(
            new ArrayList<>(map.getSpawns()),
            new ArrayList<>(map.getMexes()),
            new ArrayList<>(map.getHydros()),
            new ArrayList<>(map.getProps()),
            new HashMap<>(mexMetalValues)
        ));
        redoStack.clear();
        if (undoStack.size() > 50) {
            undoStack.removeLast();
        }
    }

    public void undo() {
        if (undoStack.isEmpty() || map == null) return;
        redoStack.push(new CanvasState(
            new ArrayList<>(map.getSpawns()),
            new ArrayList<>(map.getMexes()),
            new ArrayList<>(map.getHydros()),
            new ArrayList<>(map.getProps()),
            new HashMap<>(mexMetalValues)
        ));
        CanvasState state = undoStack.pop();
        map.getSpawns().clear();
        map.getSpawns().addAll(state.spawns());
        map.getMexes().clear();
        map.getMexes().addAll(state.mexes());
        map.getHydros().clear();
        map.getHydros().addAll(state.hydros());
        map.getProps().clear();
        map.getProps().addAll(state.props());
        mexMetalValues.clear();
        mexMetalValues.putAll(state.mexMetalValues());
        selectedEntity = null;
        repaint();
        notifyChange();
    }

    public void redo() {
        if (redoStack.isEmpty() || map == null) return;
        undoStack.push(new CanvasState(
            new ArrayList<>(map.getSpawns()),
            new ArrayList<>(map.getMexes()),
            new ArrayList<>(map.getHydros()),
            new ArrayList<>(map.getProps()),
            new HashMap<>(mexMetalValues)
        ));
        CanvasState state = redoStack.pop();
        map.getSpawns().clear();
        map.getSpawns().addAll(state.spawns());
        map.getMexes().clear();
        map.getMexes().addAll(state.mexes());
        map.getHydros().clear();
        map.getHydros().addAll(state.hydros());
        map.getProps().clear();
        map.getProps().addAll(state.props());
        mexMetalValues.clear();
        mexMetalValues.putAll(state.mexMetalValues());
        selectedEntity = null;
        repaint();
        notifyChange();
    }

    private void handleLeftClick(Point screenPt) {
        if (map == null) return;
        float[] norm = screenToNorm(screenPt);
        if (norm == null) return;

        saveState();

        if (activeTool == Tool.SELECT) {
            Object hit = findEntityAt(screenPt, 16);
            selectedEntity = hit;
            if (hit != null) {
                draggedEntity = hit;
                initDragSiblings(hit);
                if (hit instanceof Marker m && map.getMexes().contains(m)) {
                    if (onMexSelected != null) {
                        onMexSelected.accept(getMexMetal(m));
                    }
                }
            }
            repaint();
            return;
        }

        if (activeTool == Tool.ERASE) {
            eraseAt(screenPt);
            repaint();
            notifyChange();
            return;
        }

        List<Vector2> points = getSymmetricalPoints(norm[0] * map.getWidth(), norm[1] * map.getHeight());

        switch (activeTool) {
            case MEX -> {
                for (Vector2 p : points) {
                    Marker m = new Marker("mex_val_" + String.format(Locale.ROOT, "%.1f", currentMexMetal) + "_" + UUID.randomUUID().toString().substring(0, 8), p);
                    map.getMexes().add(m);
                    mexMetalValues.put(m, currentMexMetal);
                }
            }
            case GEO -> {
                for (Vector2 p : points) {
                    map.getHydros().add(new Marker("hydro_" + UUID.randomUUID().toString().substring(0, 8), p));
                }
            }
            case SPAWN -> {
                int nextId = map.getSpawnCount();
                for (Vector2 p : points) {
                    int team = (nextId % 2);
                    map.getSpawns().add(new Spawn("spawn_" + nextId, p, new Vector2(0, 0), team));
                    nextId++;
                }
            }
            case RECLAIM_ROCK -> {
                for (Vector2 p : points) {
                    map.getProps().add(new Prop("/env/evergreen/props/rocks/rock01.bp", p, (float) (Math.random() * Math.PI * 2), true));
                }
            }
            case RECLAIM_TREE -> {
                for (Vector2 p : points) {
                    map.getProps().add(new Prop("/env/evergreen/props/trees/pine01.bp", p, (float) (Math.random() * Math.PI * 2), false));
                }
            }
            case RECLAIM_WRECK -> {
                for (Vector2 p : points) {
                    map.getProps().add(new Prop("corraid_dead", p, (float) (Math.random() * Math.PI * 2), false));
                }
            }
            default -> {}
        }

        repaint();
        notifyChange();
    }

    private List<Vector2> getSymmetricalPoints(float wx, float wy) {
        List<Vector2> res = new ArrayList<>();
        Vector2 primary = new Vector2(wx, wy);
        res.add(primary);

        if (!symmetryEnabled || symmetrySettings == null || symmetrySettings.terrainSymmetry() == Symmetry.NONE || map == null) {
            return res;
        }

        int mapW = map.getWidth();
        int mapH = map.getHeight();
        Symmetry sym = symmetrySettings.terrainSymmetry();

        if (sym == Symmetry.Z) {
            // Solo Specchio Asse Z (Nord vs Sud / Top vs Bottom)
            res.add(new Vector2(wx, mapH - wy));
            return res;
        } else if (sym == Symmetry.X) {
            // Solo Specchio Asse X (Ovest vs Est / Left vs Right)
            res.add(new Vector2(mapW - wx, wy));
            return res;
        } else if (sym == Symmetry.POINT2 || sym == Symmetry.DIAG) {
            // Specchio Diagonale (Doppia specchiatura sia in X che in Z / Angoli Opposti)
            res.add(new Vector2(mapW - wx, mapH - wy));
            return res;
        } else if (sym == Symmetry.QUAD) {
            // Quadrupla Specchio (Sia X che Z insieme: tutti e 4 i quadranti)
            res.add(new Vector2(mapW - wx, wy));
            res.add(new Vector2(wx, mapH - wy));
            res.add(new Vector2(mapW - wx, mapH - wy));
            return res;
        }

        if (mapW == mapH) {
            try {
                List<Vector2> syms = SymmetryUtil.getSymmetryPoints(primary.x(), primary.y(), map.getSize(), symmetrySettings.terrainSymmetry(), symmetrySettings.spawnSymmetry());
                if (syms != null) {
                    res.addAll(syms);
                }
            } catch (Exception e) {
                res.add(new Vector2(mapW - wx, mapH - wy));
            }
        } else {
            res.add(new Vector2(mapW - wx, mapH - wy));
        }
        return res;
    }

    private void initDragSiblings(Object primary) {
        dragSiblings.clear();
        dragSiblingSymIndices.clear();

        if (!symmetryEnabled || map == null || symmetrySettings == null || symmetrySettings.terrainSymmetry() == Symmetry.NONE) {
            return;
        }

        Vector3 pos = getEntityPosition(primary);
        if (pos == null) return;

        float px = pos.x();
        float py = pos.z();

        List<Vector2> allPoints = getSymmetricalPoints(px, py);
        if (allPoints.size() <= 1) {
            return;
        }
        List<Vector2> symPoints = allPoints.subList(1, allPoints.size());

        List<?> candidates;
        if (primary instanceof Spawn) {
            candidates = map.getSpawns();
        } else if (primary instanceof Marker m) {
            candidates = map.getMexes().contains(m) ? map.getMexes() : map.getHydros();
        } else if (primary instanceof Prop) {
            candidates = map.getProps();
        } else {
            return;
        }

        Set<Object> used = new HashSet<>();
        used.add(primary);

        float maxSearchDist = 80.0f;
        for (int i = 0; i < symPoints.size(); i++) {
            Vector2 target = symPoints.get(i);
            Object closest = null;
            float bestDist = maxSearchDist;

            for (Object cand : candidates) {
                if (used.contains(cand)) continue;
                Vector3 cPos = getEntityPosition(cand);
                if (cPos == null) continue;
                float dist = (float) Math.hypot(cPos.x() - target.x(), cPos.z() - target.y());
                if (dist < bestDist) {
                    bestDist = dist;
                    closest = cand;
                }
            }

            if (closest != null) {
                dragSiblings.add(closest);
                dragSiblingSymIndices.add(i);
                used.add(closest);
            }
        }
    }

    private void moveSiblingsSymmetrically(float wx, float wy) {
        if (dragSiblings.isEmpty() || map == null) return;

        List<Vector2> allPoints = getSymmetricalPoints(wx, wy);
        if (allPoints.size() <= 1) return;
        List<Vector2> newSymPoints = allPoints.subList(1, allPoints.size());

        for (int i = 0; i < dragSiblings.size(); i++) {
            if (i >= dragSiblingSymIndices.size()) break;
            int symIdx = dragSiblingSymIndices.get(i);
            if (symIdx >= 0 && symIdx < newSymPoints.size()) {
                Vector2 pt = newSymPoints.get(symIdx);
                moveEntity(dragSiblings.get(i), pt.x(), pt.y());
            }
        }
    }

    private Vector3 getEntityPosition(Object entity) {
        if (entity instanceof Spawn s) return s.getPosition();
        if (entity instanceof Marker m) return m.getPosition();
        if (entity instanceof Prop p) return p.getPosition();
        return null;
    }

    private Object findEntityAt(Point screenPt, int radiusPx) {
        if (map == null) return null;
        for (Spawn s : map.getSpawns()) {
            Point sp = normToScreen(s.getPosition().x() / map.getWidth(), s.getPosition().z() / map.getHeight());
            if (screenPt.distance(sp) <= radiusPx) return s;
        }
        for (Marker m : map.getMexes()) {
            Point sp = normToScreen(m.getPosition().x() / map.getWidth(), m.getPosition().z() / map.getHeight());
            if (screenPt.distance(sp) <= radiusPx) return m;
        }
        for (Marker h : map.getHydros()) {
            Point sp = normToScreen(h.getPosition().x() / map.getWidth(), h.getPosition().z() / map.getHeight());
            if (screenPt.distance(sp) <= radiusPx) return h;
        }
        for (Prop pr : map.getProps()) {
            Point sp = normToScreen(pr.getPosition().x() / map.getWidth(), pr.getPosition().z() / map.getHeight());
            if (screenPt.distance(sp) <= radiusPx) return pr;
        }
        return null;
    }

    private void moveEntity(Object entity, float wx, float wy) {
        if (map == null) return;
        float cx = Math.max(4f, Math.min(map.getWidth() - 4f, wx));
        float cy = Math.max(4f, Math.min(map.getHeight() - 4f, wy));
        if (entity instanceof Spawn s) {
            s.setPosition(new Vector3(cx, 0, cy));
        } else if (entity instanceof Marker m) {
            m.setPosition(new Vector3(cx, 0, cy));
        } else if (entity instanceof Prop p) {
            p.setPosition(new Vector3(cx, 0, cy));
        }
    }

    public void deleteSelectedEntity() {
        if (selectedEntity == null || map == null) return;
        saveState();
        List<Object> toRemove = new ArrayList<>();
        toRemove.add(selectedEntity);
        if (symmetryEnabled && symmetrySettings != null && symmetrySettings.terrainSymmetry() != Symmetry.NONE) {
            initDragSiblings(selectedEntity);
            toRemove.addAll(dragSiblings);
            dragSiblings.clear();
            dragSiblingSymIndices.clear();
        }
        for (Object obj : toRemove) {
            if (obj instanceof Spawn s) {
                map.getSpawns().remove(s);
            } else if (obj instanceof Marker m) {
                if (map.getMexes().remove(m)) {
                    mexMetalValues.remove(m);
                } else {
                    map.getHydros().remove(m);
                }
            } else if (obj instanceof Prop p) {
                map.getProps().remove(p);
            }
        }
        selectedEntity = null;
        repaint();
        notifyChange();
    }

    private void eraseAt(Point screenPt) {
        Object hit = findEntityAt(screenPt, 18);
        if (hit == null) return;

        List<Object> toRemove = new ArrayList<>();
        toRemove.add(hit);

        if (symmetryEnabled && symmetrySettings != null && symmetrySettings.terrainSymmetry() != Symmetry.NONE) {
            initDragSiblings(hit);
            toRemove.addAll(dragSiblings);
            dragSiblings.clear();
            dragSiblingSymIndices.clear();
        }

        for (Object obj : toRemove) {
            if (obj == selectedEntity) selectedEntity = null;
            if (obj instanceof Spawn s) {
                map.getSpawns().remove(s);
            } else if (obj instanceof Marker m) {
                if (map.getMexes().remove(m)) {
                    mexMetalValues.remove(m);
                } else {
                    map.getHydros().remove(m);
                }
            } else if (obj instanceof Prop p) {
                map.getProps().remove(p);
            }
        }
    }

    // Auto-Assist Utilities
    public void autoPlaceBaseMexes(int perSpawn) {
        autoPlaceBaseMexes(perSpawn, 2.0f);
    }

    public void autoPlaceBaseMexes(int perSpawn, float metalVal) {
        if (map == null || map.getSpawns().isEmpty()) return;
        saveState();
        int radius = 18;
        for (Spawn spawn : map.getSpawns()) {
            float sx = spawn.getPosition().x();
            float sy = spawn.getPosition().z();
            for (int i = 0; i < perSpawn; i++) {
                double angle = (2 * Math.PI * i) / perSpawn + 0.35;
                float mx = (float) (sx + Math.cos(angle) * radius);
                float my = (float) (sy + Math.sin(angle) * radius);
                if (mx >= 8 && mx < map.getWidth() - 8 && my >= 8 && my < map.getHeight() - 8) {
                    Marker m = new Marker("mex_val_" + String.format(Locale.ROOT, "%.1f", metalVal) + "_" + UUID.randomUUID().toString().substring(0, 8), new Vector2(mx, my));
                    map.getMexes().add(m);
                    mexMetalValues.put(m, metalVal);
                }
            }
        }
        repaint();
        notifyChange();
    }

    public void autoPlaceContestedMexes(int count) {
        autoPlaceContestedMexes(count, 3.5f);
    }

    public void autoPlaceContestedMexes(int count, float metalVal) {
        if (map == null) return;
        saveState();
        float cx = map.getWidth() / 2f;
        float cy = map.getHeight() / 2f;
        int placed = 0;
        int attempts = 0;
        float minDim = Math.min(map.getWidth(), map.getHeight());
        while (placed < count && attempts < 100) {
            attempts++;
            double r = (minDim * 0.15) + (Math.random() * (minDim * 0.25));
            double angle = Math.random() * Math.PI * 2;
            float mx = (float) (cx + Math.cos(angle) * r);
            float my = (float) (cy + Math.sin(angle) * r);

            List<Vector2> pts = getSymmetricalPoints(mx, my);
            for (Vector2 p : pts) {
                Marker m = new Marker("mex_val_" + String.format(Locale.ROOT, "%.1f", metalVal) + "_" + UUID.randomUUID().toString().substring(0, 8), p);
                map.getMexes().add(m);
                mexMetalValues.put(m, metalVal);
                placed++;
            }
        }
        repaint();
        notifyChange();
    }

    public void autoSeedReclaim(float density) {
        if (map == null) return;
        saveState();
        int targetProps = (int) (Math.sqrt(map.getWidth() * map.getHeight()) * density * 0.25f);
        for (int i = 0; i < targetProps; i++) {
            float rx = (float) (16 + Math.random() * (map.getWidth() - 32));
            float ry = (float) (16 + Math.random() * (map.getHeight() - 32));
            boolean isRock = Math.random() < 0.45;
            List<Vector2> pts = getSymmetricalPoints(rx, ry);
            for (Vector2 p : pts) {
                String path = isRock ? "/env/evergreen/props/rocks/rock01.bp" : "/env/evergreen/props/trees/pine01.bp";
                map.getProps().add(new Prop(path, p, (float) (Math.random() * Math.PI * 2), isRock));
            }
        }
        repaint();
        notifyChange();
    }

    public void clearMexes() {
        if (map == null) return;
        saveState();
        map.getMexes().clear();
        repaint();
        notifyChange();
    }

    public void clearGeos() {
        if (map == null) return;
        saveState();
        map.getHydros().clear();
        repaint();
        notifyChange();
    }

    public void clearReclaim() {
        if (map == null) return;
        saveState();
        map.getProps().clear();
        repaint();
        notifyChange();
    }

    public void clearSpawns() {
        if (map == null) return;
        saveState();
        map.getSpawns().clear();
        repaint();
        notifyChange();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Rectangle b = getMapBounds();

        // 1. Draw Map Canvas Background & Frame
        g.setColor(new Color(16, 22, 32));
        g.fillRoundRect(b.x - 3, b.y - 3, b.width + 6, b.height + 6, 14, 14);
        g.setColor(new Color(0, 229, 255, 60));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(b.x - 3, b.y - 3, b.width + 6, b.height + 6, 14, 14);

        if (groundPreview != null) {
            Shape clip = new RoundRectangle2D.Float(b.x, b.y, b.width, b.height, 12, 12);
            g.setClip(clip);
            g.drawImage(groundPreview, b.x, b.y, b.width, b.height, null);

            // Render Slope Map overlay if active
            if (showSlopeMap && slopeOverlay != null) {
                Composite oldComp = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, Math.max(0.05f, slopeOpacity))));
                g.drawImage(slopeOverlay, b.x, b.y, b.width, b.height, null);
                g.setComposite(oldComp);
            }

            // Subtle tactical grid
            g.setColor(new Color(0, 229, 255, 20));
            g.setStroke(new BasicStroke(1.0f));
            int cells = 8;
            for (int i = 1; i < cells; i++) {
                int gx = b.x + (b.width * i) / cells;
                int gy = b.y + (b.height * i) / cells;
                g.drawLine(gx, b.y, gx, b.y + b.height);
                g.drawLine(b.x, gy, b.x + b.width, gy);
            }

            // Symmetry Axis Indicator
            if (symmetryEnabled && symmetrySettings != null && symmetrySettings.terrainSymmetry() != Symmetry.NONE) {
                g.setColor(new Color(0, 229, 255, 95));
                g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
                Symmetry sym = symmetrySettings.terrainSymmetry();
                int midX = b.x + b.width / 2;
                int midY = b.y + b.height / 2;
                if (sym == Symmetry.X) {
                    // Solo Asse X (Ovest vs Est): linea verticale
                    g.drawLine(midX, b.y, midX, b.y + b.height);
                } else if (sym == Symmetry.Z) {
                    // Solo Asse Z (Nord vs Sud): linea orizzontale
                    g.drawLine(b.x, midY, b.x + b.width, midY);
                } else if (sym == Symmetry.QUAD) {
                    // Quadrupla: sia X che Z (croce centrale completa)
                    g.drawLine(midX, b.y, midX, b.y + b.height);
                    g.drawLine(b.x, midY, b.x + b.width, midY);
                } else if (sym == Symmetry.DIAG || sym == Symmetry.POINT2) {
                    // Specchio Diagonale (doppia specchiatura X + Z): linee da angolo ad angolo e cerchio centrale
                    g.drawLine(b.x, b.y, b.x + b.width, b.y + b.height);
                    g.drawLine(b.x + b.width, b.y, b.x, b.y + b.height);
                    g.drawOval(midX - 6, midY - 6, 12, 12);
                }
            }

            // 2. Draw Reclaim (Props / Trees / Rocks)
            if (map != null) {
                int mw = map.getWidth();
                int mh = map.getHeight();
                for (Prop p : map.getProps()) {
                    Point sp = normToScreen(p.getPosition().x() / mw, p.getPosition().z() / mh);
                    if (p.isBoulder()) {
                        g.setColor(new Color(130, 140, 155, 220));
                        g.fillOval(sp.x - 3, sp.y - 3, 6, 6);
                        g.setColor(new Color(20, 25, 35));
                        g.drawOval(sp.x - 3, sp.y - 3, 6, 6);
                    } else if (p.getPath().contains("tree") || p.getPath().contains("pine")) {
                        g.setColor(new Color(38, 170, 80, 220));
                        g.fillOval(sp.x - 2, sp.y - 2, 5, 5);
                    } else {
                        // Wreck
                        g.setColor(new Color(230, 90, 50, 220));
                        g.fillRect(sp.x - 3, sp.y - 3, 6, 6);
                    }
                }
            }

            // 3. Draw Geos
            if (map != null) {
                int mw = map.getWidth();
                int mh = map.getHeight();
                for (Marker h : map.getHydros()) {
                    Point sp = normToScreen(h.getPosition().x() / mw, h.getPosition().z() / mh);
                    drawGeoMarker(g, sp.x, sp.y);
                }
            }

            // 4. Draw Mexes
            if (map != null) {
                int mw = map.getWidth();
                int mh = map.getHeight();
                for (Marker m : map.getMexes()) {
                    Point sp = normToScreen(m.getPosition().x() / mw, m.getPosition().z() / mh);
                    float metal = getMexMetal(m);
                    boolean isSelected = (m == selectedEntity);
                    drawMexMarker(g, sp.x, sp.y, metal, isSelected);
                }
            }

            // 5. Draw Spawns
            if (map != null) {
                int mw = map.getWidth();
                int mh = map.getHeight();
                for (int i = 0; i < map.getSpawns().size(); i++) {
                    Spawn s = map.getSpawns().get(i);
                    Point sp = normToScreen(s.getPosition().x() / mw, s.getPosition().z() / mh);
                    drawSpawnMarker(g, sp.x, sp.y, s.getTeamID(), i + 1);
                }
            }

            // 6. Draw Symmetry Ghosts if active placement tool
            if (isPlacementTool(activeTool) && b.contains(mousePos)) {
                float[] norm = screenToNorm(mousePos);
                if (norm != null && map != null) {
                    int mw = map.getWidth();
                    int mh = map.getHeight();
                    List<Vector2> pts = getSymmetricalPoints(norm[0] * mw, norm[1] * mh);
                    for (int i = 0; i < pts.size(); i++) {
                        Vector2 pt = pts.get(i);
                        Point sp = normToScreen(pt.x() / mw, pt.y() / mh);
                        boolean isPrimary = (i == 0);
                        drawGhostMarker(g, sp.x, sp.y, activeTool, isPrimary);
                    }
                }
            }

            g.setClip(null);
        } else {
            // Placeholder text
            g.setColor(new Color(0, 229, 255, 120));
            g.setFont(BarMapStudio.fontCond(Font.BOLD, 18));
            String msg = "Nessun terreno generato - Clicca su 'GENERA TERRENO'";
            int sw = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, b.x + (b.width - sw) / 2, b.y + b.height / 2);
        }

        // HUD Overlay in top corner
        drawHud(g, b);

        g.dispose();
    }

    private boolean isPlacementTool(Tool t) {
        return t == Tool.MEX || t == Tool.GEO || t == Tool.SPAWN || t == Tool.RECLAIM_ROCK || t == Tool.RECLAIM_TREE || t == Tool.RECLAIM_WRECK;
    }

    public static Color getMexFillColor(float metal) {
        float t = Math.max(0.0f, Math.min(1.0f, (metal - 1.0f) / 4.0f));
        int r = Math.round(215 * (1.0f - t) + 8 * t);
        int g = Math.round(250 * (1.0f - t) + 48 * t);
        int b = Math.round(255 * (1.0f - t) + 80 * t);
        return new Color(r, g, b, 245);
    }

    public static Color getMexRingColor(float metal) {
        float t = Math.max(0.0f, Math.min(1.0f, (metal - 1.0f) / 4.0f));
        int r = Math.round(230 * (1.0f - t) + 0 * t);
        int g = Math.round(255 * (1.0f - t) + 160 * t);
        int b = Math.round(255 * (1.0f - t) + 210 * t);
        return new Color(r, g, b, 180);
    }

    private void drawMexMarker(Graphics2D g, int x, int y, float metalValue, boolean isSelected) {
        int r = Math.max(6, (int) (7 * zoom));

        Color fillColor = getMexFillColor(metalValue);
        Color ringColor = getMexRingColor(metalValue);
        float t = Math.max(0.0f, Math.min(1.0f, (metalValue - 1.0f) / 4.0f));

        // 1. Selection or outer glow ring
        if (isSelected) {
            g.setColor(new Color(255, 255, 255, 220));
            g.setStroke(new BasicStroke(2.2f));
            g.drawOval(x - r - 4, y - r - 4, (r + 4) * 2, (r + 4) * 2);
        } else {
            g.setColor(new Color(ringColor.getRed(), ringColor.getGreen(), ringColor.getBlue(), 90));
            g.setStroke(new BasicStroke(1.4f));
            g.drawOval(x - r - 2, y - r - 2, (r + 2) * 2, (r + 2) * 2);
        }

        // 2. Main body fill (shaded based on metal: lighter for low, darker for high)
        g.setColor(fillColor);
        g.fillOval(x - r, y - r, r * 2, r * 2);

        // 3. Crisp outline
        Color outline = (t < 0.35f) ? new Color(0, 140, 180, 240) : new Color(0, 229, 255, 220);
        g.setColor(outline);
        g.setStroke(new BasicStroke(1.3f));
        g.drawOval(x - r, y - r, r * 2, r * 2);

        // 4. Center pip
        Color pipColor = (t > 0.55f) ? new Color(0, 240, 255) : new Color(11, 14, 20);
        g.setColor(pipColor);
        int pr = Math.max(2, (int) (2.5 * zoom));
        g.fillOval(x - pr, y - pr, pr * 2, pr * 2);

        // 5. Metal value text pill badge
        String valStr = String.format(Locale.ROOT, "%.1f", metalValue);
        g.setFont(BarMapStudio.font(Font.BOLD, Math.max(9, (int) (10 * zoom))));
        int strW = g.getFontMetrics().stringWidth(valStr);
        int badgeH = 13;
        int badgeW = strW + 6;
        int bx = x - badgeW / 2;
        int by = y + r + 2;

        g.setColor(new Color(11, 14, 20, 220));
        g.fillRoundRect(bx, by, badgeW, badgeH, 6, 6);
        g.setColor(outline);
        g.setStroke(new BasicStroke(0.8f));
        g.drawRoundRect(bx, by, badgeW, badgeH, 6, 6);

        Color textColor = (t < 0.3f) ? new Color(200, 245, 255) : new Color(0, 240, 255);
        if (t > 0.8f) textColor = new Color(100, 255, 218);
        g.setColor(textColor);
        g.drawString(valStr, bx + 3, by + 10);
    }

    private void drawGeoMarker(Graphics2D g, int x, int y) {
        int r = Math.max(6, (int) (7 * zoom));
        Path2D.Float d = new Path2D.Float();
        d.moveTo(x, y - r);
        d.lineTo(x + r, y);
        d.lineTo(x, y + r);
        d.lineTo(x - r, y);
        d.closePath();

        g.setColor(new Color(255, 159, 28, 230));
        g.fill(d);
        g.setColor(new Color(11, 14, 20));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(d);
    }

    private void drawSpawnMarker(Graphics2D g, int x, int y, int teamId, int playerNum) {
        int r = Math.max(10, (int) (12 * zoom));
        Color teamColor = (teamId == 0) ? new Color(0, 229, 255) : new Color(255, 56, 92);
        if (teamId == 1) teamColor = new Color(255, 159, 28);
        if (teamId >= 2) teamColor = new Color(0, 255, 136);

        // Halo
        g.setColor(new Color(teamColor.getRed(), teamColor.getGreen(), teamColor.getBlue(), 60));
        g.fillOval(x - r - 4, y - r - 4, (r + 4) * 2, (r + 4) * 2);

        // Core
        g.setColor(teamColor);
        g.fillOval(x - r, y - r, r * 2, r * 2);
        g.setColor(new Color(11, 14, 20));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(x - r, y - r, r * 2, r * 2);

        // Text
        g.setColor(Color.WHITE);
        g.setFont(BarMapStudio.fontCond(Font.BOLD, 10));
        String txt = "P" + playerNum;
        int tw = g.getFontMetrics().stringWidth(txt);
        g.drawString(txt, x - tw / 2, y + 4);
    }

    private void drawGhostMarker(Graphics2D g, int x, int y, Tool tool, boolean isPrimary) {
        int alpha = isPrimary ? 180 : 100;
        int r = Math.max(7, (int) (8 * zoom));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{3, 3}, 0));

        switch (tool) {
            case MEX -> {
                Color fill = getMexFillColor(currentMexMetal);
                g.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), alpha));
                g.fillOval(x - r, y - r, r * 2, r * 2);
                g.setColor(Color.WHITE);
                g.drawOval(x - r, y - r, r * 2, r * 2);
                String valStr = String.format(Locale.ROOT, "+%.1f M", currentMexMetal);
                g.setFont(BarMapStudio.font(Font.BOLD, 10));
                g.drawString(valStr, x + r + 4, y + 4);
            }
            case GEO -> {
                g.setColor(new Color(255, 159, 28, alpha));
                g.drawRect(x - r, y - r, r * 2, r * 2);
            }
            case SPAWN -> {
                g.setColor(new Color(0, 255, 136, alpha));
                g.drawOval(x - r - 2, y - r - 2, (r + 2) * 2, (r + 2) * 2);
            }
            case RECLAIM_ROCK -> {
                g.setColor(new Color(180, 190, 200, alpha));
                g.drawOval(x - 4, y - 4, 8, 8);
            }
            case RECLAIM_TREE -> {
                g.setColor(new Color(50, 200, 90, alpha));
                g.drawOval(x - 4, y - 4, 8, 8);
            }
            default -> {}
        }
    }

    private void drawHud(Graphics2D g, Rectangle b) {
        // Status chip at bottom
        float[] norm = screenToNorm(mousePos);
        int mw = map != null ? map.getWidth() : 512;
        int mh = map != null ? map.getHeight() : 512;
        int wx = (norm != null && map != null) ? Math.round(norm[0] * mw) : 0;
        int wy = (norm != null && map != null) ? Math.round(norm[1] * mh) : 0;
        int sizeUnitW = mw / 64;
        int sizeUnitH = mh / 64;

        String coordStr = String.format("Pos: [%d, %d] | Mappa: %dx%d (%d x %d km) | Zoom: %.0f%% | Tool: %s | Simm: %s",
            wx, wy, sizeUnitW, sizeUnitH, sizeUnitW, sizeUnitH, zoom * 100, activeTool.name(),
            (symmetryEnabled ? (symmetrySettings != null ? symmetrySettings.terrainSymmetry().name() : "ON") : "OFF")
        );

        if (norm != null && map != null) {
            float hAt = map.getHeightAtWorld(wx, wy);
            float sAt = map.getSlopeAtWorld(wx, wy);
            double deg = Math.toDegrees(Math.atan(sAt));
            coordStr += String.format(Locale.ROOT, " | Quota: %.1fm | Pend: %.2f (%.1f°)", hAt, sAt, deg);
        }

        if (selectedEntity instanceof Marker m && map != null && map.getMexes().contains(m)) {
            coordStr += String.format(Locale.ROOT, " | MEX SELEZIONATO: %.1f M", getMexMetal(m));
        } else if (selectedEntity instanceof Spawn s) {
            coordStr += String.format(" | SPAWN SELEZIONATO: Player %d", s.getTeamID() + 1);
        }

        g.setFont(BarMapStudio.font(Font.BOLD, 11));
        int strW = g.getFontMetrics().stringWidth(coordStr);
        int hx = 16;
        int hy = getHeight() - 28;

        g.setColor(new Color(11, 14, 20, 230));
        g.fillRoundRect(hx - 6, hy - 14, strW + 16, 22, 8, 8);
        g.setColor(new Color(0, 229, 255, 140));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(hx - 6, hy - 14, strW + 16, 22, 8, 8);

        g.setColor(new Color(240, 244, 250));
        g.drawString(coordStr, hx + 2, hy + 2);

        // Hotkey cheatsheet bar in top-left corner
        String hotkeys = "[S] Seleziona  [M] Mex  [G] Geo  [R] Rocce  [E] Cancella  [DEL] Elimina  [Ctrl+Z] Annulla  [L] Slope Map  [Space] Simm  [1-5] Metallo  [+/-] Zoom";
        g.setFont(BarMapStudio.font(Font.BOLD, 10));
        int hkW = g.getFontMetrics().stringWidth(hotkeys);
        int hkX = 16;
        int hkY = 24;

        g.setColor(new Color(11, 14, 20, 220));
        g.fillRoundRect(hkX - 6, hkY - 13, hkW + 14, 20, 6, 6);
        g.setColor(new Color(255, 255, 255, 40));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(hkX - 6, hkY - 13, hkW + 14, 20, 6, 6);

        g.setColor(new Color(0, 229, 255, 200));
        g.drawString(hotkeys, hkX, hkY + 2);

        // Slope Map HUD Legend (bottom right)
        if (showSlopeMap) {
            int legW = 220;
            int legH = 130;
            int legX = getWidth() - legW - 20;
            int legY = getHeight() - legH - 28;

            g.setColor(new Color(11, 14, 20, 235));
            g.fillRoundRect(legX, legY, legW, legH, 10, 10);
            g.setColor(new Color(255, 159, 28, 180));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(legX, legY, legW, legH, 10, 10);

            g.setFont(BarMapStudio.fontCond(Font.BOLD, 11));
            g.setColor(new Color(255, 159, 28));
            drawMountainVector(g, legX + 12, legY + 8, 13, 10, new Color(255, 159, 28));
            g.drawString("LEGENDA PENDENZE (BAR)", legX + 30, legY + 18);

            g.setFont(BarMapStudio.font(Font.BOLD, 10));
            int cy = legY + 36;

            // 1. Green
            g.setColor(new Color(34, 197, 94));
            g.fillRect(legX + 12, cy - 8, 12, 10);
            g.setColor(new Color(240, 244, 250));
            g.drawString("< 12°  Veicoli OK & Costruibile", legX + 30, cy);
            cy += 18;

            // 2. Yellow
            g.setColor(new Color(234, 179, 8));
            g.fillRect(legX + 12, cy - 8, 12, 10);
            g.setColor(new Color(240, 244, 250));
            g.drawString("12°-20° Veicoli Rallentati", legX + 30, cy);
            cy += 18;

            // 3. Orange
            g.setColor(new Color(249, 115, 22));
            g.fillRect(legX + 12, cy - 8, 12, 10);
            g.setColor(new Color(240, 244, 250));
            g.drawString("20°-33° Solo Kbot / Bot", legX + 30, cy);
            cy += 18;

            // 4. Red
            g.setColor(new Color(239, 68, 68));
            g.fillRect(legX + 12, cy - 8, 12, 10);
            g.setColor(new Color(240, 244, 250));
            g.drawString("> 33°  Invalicabile (Scarpata)", legX + 30, cy);
            cy += 18;

            // 5. Blue
            g.setColor(new Color(14, 165, 233));
            g.fillRect(legX + 12, cy - 8, 12, 10);
            g.setColor(new Color(240, 244, 250));
            g.drawString("Acqua / Subacqueo", legX + 30, cy);
        }
    }

    private void drawMountainVector(Graphics2D g, int x, int y, int w, int h, Color c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D.Float p1 = new Path2D.Float();
        p1.moveTo(x, y + h);
        p1.lineTo(x + w * 0.45f, y);
        p1.lineTo(x + w * 0.75f, y + h);
        p1.closePath();
        g2.draw(p1);

        Path2D.Float p2 = new Path2D.Float();
        p2.moveTo(x + w * 0.45f, y + h);
        p2.lineTo(x + w * 0.75f, y + h * 0.35f);
        p2.lineTo(x + w, y + h);
        g2.draw(p2);
        g2.dispose();
    }
}
