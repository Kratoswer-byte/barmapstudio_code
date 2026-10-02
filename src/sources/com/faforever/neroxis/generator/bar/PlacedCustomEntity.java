package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.util.vector.Vector2;
import java.util.UUID;

/**
 * Rappresenta un'istanza posizionata di un oggetto o unità custom sulla mappa.
 */
public final class PlacedCustomEntity {
    private final String instanceId;
    private final ObjectEntity entity;
    private Vector2 position;
    private float rotation;

    public PlacedCustomEntity(ObjectEntity entity, Vector2 position, float rotation) {
        this.instanceId = "placed_" + entity.getId() + "_" + UUID.randomUUID().toString().substring(0, 8);
        this.entity = entity;
        this.position = position != null ? position : new Vector2(0, 0);
        this.rotation = rotation;
    }

    public String getInstanceId() { return instanceId; }
    public ObjectEntity getEntity() { return entity; }
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position = position; }
    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }

    @Override
    public String toString() {
        return entity.getName() + " (" + (int) position.x() + ", " + (int) position.y() + ")";
    }
}
