package com.faforever.neroxis.generator.bar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Gestore condiviso per la Palette degli Oggetti & Unità Pronti per la Mappa.
 * Collega lo Studio (Objects & Units) con il vassoio di piazzamento nel tab MAPS.
 */
public final class ReadyObjectsManager {
    private static final ReadyObjectsManager INSTANCE = new ReadyObjectsManager();

    private final List<ObjectEntity> readyObjects = new CopyOnWriteArrayList<>();
    private final List<Consumer<List<ObjectEntity>>> listeners = new CopyOnWriteArrayList<>();

    private ReadyObjectsManager() {
        // Pre-popoliamo con un elemento di default per immediatezza
        readyObjects.add(ObjectEntity.createDefaultWindmill());
    }

    public static ReadyObjectsManager getInstance() {
        return INSTANCE;
    }

    public List<ObjectEntity> getReadyObjects() {
        return Collections.unmodifiableList(new ArrayList<>(readyObjects));
    }

    public void addReadyObject(ObjectEntity entity) {
        if (entity == null) return;
        // Evita duplicati identici per ID, sostituendo se presente
        readyObjects.removeIf(e -> e.getId().equals(entity.getId()));
        readyObjects.add(entity);
        notifyListeners();
    }

    public void removeReadyObject(ObjectEntity entity) {
        if (entity == null) return;
        readyObjects.remove(entity);
        notifyListeners();
    }

    public void removeReadyObjectById(String id) {
        if (id == null) return;
        readyObjects.removeIf(e -> e.getId().equals(id));
        notifyListeners();
    }

    public void clear() {
        readyObjects.clear();
        notifyListeners();
    }

    public void addListener(Consumer<List<ObjectEntity>> listener) {
        if (listener != null) {
            listeners.add(listener);
            listener.accept(getReadyObjects());
        }
    }

    public void removeListener(Consumer<List<ObjectEntity>> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        List<ObjectEntity> snapshot = getReadyObjects();
        for (Consumer<List<ObjectEntity>> listener : listeners) {
            try {
                listener.accept(snapshot);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
