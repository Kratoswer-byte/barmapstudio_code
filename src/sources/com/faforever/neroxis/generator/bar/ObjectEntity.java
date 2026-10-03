package com.faforever.neroxis.generator.bar;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

/**
 * Entita' unificata per Objects & Units Studio:
 * rappresenta unita' ufficiali di BAR, props di scenario (alberi, rocce, relitti),
 * elementi animati (mulini a vento, radar), boss e mesh 3D esterne importate.
 */
public final class ObjectEntity {

    public enum Category {
        OFFICIAL_UNIT("Unita' BAR"),
        STATIC_PROP("Oggetto Statico"),
        ANIMATED_SCENERY("Scenery Animato"),
        BOSS_UNIT("Boss / Titano"),
        CUSTOM_MESH("Mesh Personalizzata");

        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private final String id;
    private String name;
    private final Category category;
    private String faction;
    private int tier;
    private int footprintX;
    private int footprintZ;
    private int health;
    private int metal;
    private int energy;
    private float speed;
    private String tooltip;

    private ObjModel3D model3D;
    private Path objFilePath;
    private Path texturePath;
    private BufferedImage previewImage;
    private String luaScript;
    private final Units.Unit officialUnit;
    private float scale = 1.0f;

    public ObjectEntity(String id, String name, Category category, String faction,
                        int footprintX, int footprintZ, int health, int metal, int energy,
                        String tooltip, ObjModel3D model3D, String luaScript) {
        this(id, name, category, faction, footprintX, footprintZ, health, metal, energy, tooltip, model3D, luaScript, null);
    }

    public ObjectEntity(String id, String name, Category category, String faction,
                        int footprintX, int footprintZ, int health, int metal, int energy,
                        String tooltip, ObjModel3D model3D, String luaScript, Units.Unit officialUnit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.faction = faction != null ? faction : "Neutral";
        this.tier = 0;
        this.footprintX = Math.max(1, footprintX);
        this.footprintZ = Math.max(1, footprintZ);
        this.health = Math.max(1, health);
        this.metal = metal;
        this.energy = energy;
        this.speed = 0.0f;
        this.tooltip = tooltip != null ? tooltip : "";
        this.model3D = model3D;
        this.luaScript = luaScript != null ? luaScript : "";
        this.officialUnit = officialUnit;
    }

    /**
     * Costruisce un ObjectEntity a partire da una Unit ufficiale di BAR.
     */
    public static ObjectEntity fromOfficialUnit(Units.Unit unit) {
        ObjectEntity entity = new ObjectEntity(
            unit.id(),
            unit.name(),
            Category.OFFICIAL_UNIT,
            unit.faction(),
            2, 2, // footprint standard base
            unit.health(),
            unit.metal(),
            unit.energy(),
            unit.tooltip(),
            null,
            "",
            unit
        );
        entity.tier = unit.tier();
        entity.speed = unit.speed();
        return entity;
    }

    /**
     * Crea un elemento di default: Mulino a Vento animato.
     */
    public static ObjectEntity createDefaultWindmill() {
        ObjModel3D model = ObjModel3D.createProceduralWindmill();
        String initialLua = "-- Script di animazione per mulino a vento\n" +
            "local blades = piece('blades')\n\n" +
            "function script.Create()\n" +
            "    StartThread(RotateBlades)\n" +
            "end\n\n" +
            "local function RotateBlades()\n" +
            "    Spin(blades, z_axis, math.rad(60))\n" +
            "end\n\n" +
            "function script.Killed(recentDamage, maxHealth)\n" +
            "    return 1\n" +
            "end\n";

        return new ObjectEntity(
            "custom_windmill_01",
            "Mulino a Vento (Animato)",
            Category.ANIMATED_SCENERY,
            "Scenery",
            4, 4,
            1200, 150, 0,
            "Struttura di scenario con pale rotanti continue tramite script Spring.",
            model,
            initialLua
        );
    }

    /**
     * Crea un elemento di default: Albero Pino.
     */
    public static ObjectEntity createDefaultPineTree() {
        return new ObjectEntity(
            "allpinesb_ad0_green_a_xl",
            "Pino Alpino Grande (Statico)",
            Category.STATIC_PROP,
            "Scenery",
            2, 2,
            300, 0, 80,
            "Albero ad alto fusto recuperabile per energia/metallo.",
            ObjModel3D.createProceduralCube(3.0f),
            ""
        );
    }

    /**
     * Crea un elemento di default: Roccia Granitica.
     */
    public static ObjectEntity createDefaultRock() {
        return new ObjectEntity(
            "rock_granite_01",
            "Roccia Granito (Statica)",
            Category.STATIC_PROP,
            "Scenery",
            3, 3,
            800, 120, 0,
            "Grande masso di roccia recuperabile per metallo.",
            ObjModel3D.createProceduralCube(2.5f),
            ""
        );
    }

    /**
     * Crea un elemento di default: Boss Lanciamissili.
     */
    public static ObjectEntity createDefaultMissileBoss() {
        String bossLua = "-- Script logica bersagliamento Boss Globale\n" +
            "function gadget:GameFrame(n)\n" +
            "    if n % 300 == 0 then -- Ogni 10 secondi\n" +
            "        -- Cerca bersaglio ed esegue fuoco a lungo raggio\n" +
            "    end\n" +
            "end\n";

        return new ObjectEntity(
            "boss_titan_missile",
            "Boss Titano Balistico",
            Category.BOSS_UNIT,
            "Gaia",
            8, 8,
            25000, 4500, 2000,
            "Enorme cannone mobile a gittata globale che spara missili sull'intera mappa.",
            ObjModel3D.createProceduralCube(5.0f),
            bossLua
        );
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getCategory() { return category; }
    public String getFaction() { return faction; }
    public void setFaction(String faction) { this.faction = faction; }
    public int getTier() { return tier; }
    public void setTier(int tier) { this.tier = tier; }
    public int getFootprintX() { return footprintX; }
    public void setFootprintX(int footprintX) { this.footprintX = footprintX; }
    public int getFootprintZ() { return footprintZ; }
    public void setFootprintZ(int footprintZ) { this.footprintZ = footprintZ; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getMetal() { return metal; }
    public void setMetal(int metal) { this.metal = metal; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public String getTooltip() { return tooltip; }
    public void setTooltip(String tooltip) { this.tooltip = tooltip; }

    public ObjModel3D getModel3D() { return model3D; }
    public void setModel3D(ObjModel3D model3D) { this.model3D = model3D; }
    public Path getObjFilePath() { return objFilePath; }
    public void setObjFilePath(Path objFilePath) { this.objFilePath = objFilePath; }
    public Path getTexturePath() { return texturePath; }
    public void setTexturePath(Path texturePath) { this.texturePath = texturePath; }
    public BufferedImage getPreviewImage() { return previewImage; }
    public void setPreviewImage(BufferedImage previewImage) { this.previewImage = previewImage; }
    public String getLuaScript() { return luaScript; }
    public void setLuaScript(String luaScript) { this.luaScript = luaScript; }
    public Units.Unit getOfficialUnit() { return officialUnit; }
    public float getScale() { return scale; }
    public void setScale(float scale) {
        this.scale = Math.max(0.01f, Math.min(500.0f, scale));
    }

    public float getBaseWidth() {
        if (model3D != null && model3D.getVertexCount() > 0) {
            return Math.max(0.1f, model3D.getMaxX() - model3D.getMinX());
        }
        return footprintX * 16.0f;
    }

    public float getBaseHeight() {
        if (model3D != null && model3D.getVertexCount() > 0) {
            return Math.max(0.1f, model3D.getMaxY() - model3D.getMinY());
        }
        return 20.0f;
    }

    public float getBaseDepth() {
        if (model3D != null && model3D.getVertexCount() > 0) {
            return Math.max(0.1f, model3D.getMaxZ() - model3D.getMinZ());
        }
        return footprintZ * 16.0f;
    }

    public float getScaledWidth() { return getBaseWidth() * scale; }
    public float getScaledHeight() { return getBaseHeight() * scale; }
    public float getScaledDepth() { return getBaseDepth() * scale; }

    public void autoScaleToHeight(float targetHeightElmos) {
        float bh = getBaseHeight();
        if (bh > 0.0001f) {
            setScale(targetHeightElmos / bh);
        }
    }

    @Override
    public String toString() {
        return name + " (" + category.getLabel() + ")";
    }
}
