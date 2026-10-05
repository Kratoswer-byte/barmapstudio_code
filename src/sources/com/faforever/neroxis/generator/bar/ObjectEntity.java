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
        OFFICIAL_UNIT(I18n.t("obj.74")),
        STATIC_PROP(I18n.t("obj.75")),
        ANIMATED_SCENERY(I18n.t("obj.76")),
        BOSS_UNIT(I18n.t("obj.77")),
        CUSTOM_MESH(I18n.t("obj.78"));

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

    /** Hook icone gameplay (UI completa dopo, in Studio). NONE = statica senza icona. */
    public enum IconKind { NONE, MOBILE, STRUCTURE }
    private IconKind iconKind = IconKind.NONE;
    private boolean showIcon = false;

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
     * Oggetti in dotazione con il programma: ogni sottocartella di <home>/default_objects che contiene
     * un file .obj (con il suo .mtl e, se c'e', lo script .lua con lo stesso nome) diventa un oggetto pronto.
     */
    public static java.util.List<ObjectEntity> loadDefaultObjects() {
        java.util.List<ObjectEntity> result = new java.util.ArrayList<>();
        java.io.File root = new java.io.File(System.getProperty("neroxis.home", System.getProperty("user.dir")), "default_objects");
        java.io.File[] folders = root.listFiles(java.io.File::isDirectory);
        if (folders == null) {
            return result;
        }
        java.util.Arrays.sort(folders);
        for (java.io.File folder : folders) {
            java.io.File[] objs = folder.listFiles((dir, name) -> name.toLowerCase(java.util.Locale.ROOT).endsWith(".obj"));
            if (objs == null || objs.length == 0) {
                continue;
            }
            java.io.File obj = objs[0];
            try {
                String baseName = obj.getName().replaceAll("(?i)\\.obj$", "");
                String id = "custom_" + baseName.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
                java.io.File lua = new java.io.File(folder, baseName + ".lua");
                String script = lua.isFile() ? java.nio.file.Files.readString(lua.toPath()) : "";
                String displayName = Character.toUpperCase(baseName.charAt(0)) + baseName.substring(1);
                ObjectEntity entity = new ObjectEntity(id, displayName,
                    script.isBlank() ? Category.STATIC_PROP : Category.BOSS_UNIT, "Custom",
                    3, 3, 1000, 200, 0, I18n.t("objdef.tooltip"), ObjParser.parse(obj), script);
                entity.setObjFilePath(obj.toPath());
                // circa il doppio di un Comandante (45 elmi)
                entity.autoScaleToHeight(90.0f);
                result.add(entity);
            } catch (Exception ex) {
                System.err.println("Oggetto predefinito non caricato (" + obj + "): " + ex.getMessage());
            }
        }
        return result;
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
    /** Accesso inter-package senza esporre il tipo package-private Units.Unit. */
    public boolean hasOfficialUnit() { return officialUnit != null; }

    /** Id dell'unita' di BAR di cui questo oggetto e' una copia personalizzata, null se non e' una copia. */
    private String cloneOf;

    public String getCloneOf() { return cloneOf; }

    /** Copie personalizzate: true = lo script dell'editor sostituisce quello dell'unita' originale. */
    private boolean customScriptEnabled = false;

    /**
     * Codice Lua che modifica la definizione dell'unita' (unitdef) quando il gioco la carica: aggiunge armi,
     * scudi, cambia movimento o qualsiasi altro valore. Vale per gli oggetti dello Studio e per le copie.
     */
    private String defLua = "";

    public String getDefLua() { return defLua; }
    public void setDefLua(String defLua) { this.defLua = defLua != null ? defLua : ""; }

    public boolean isCustomScriptEnabled() { return customScriptEnabled; }
    public void setCustomScriptEnabled(boolean enabled) { this.customScriptEnabled = enabled; }
    public boolean isClone() { return cloneOf != null; }

    /**
     * Copia personalizzata di un'unita' di BAR: un nuovo tipo di unita' con lo stesso modello, le stesse armi
     * e lo stesso comportamento dell'originale, ma con nome e valori propri. L'originale non viene toccata.
     */
    public static ObjectEntity cloneOfficial(ObjectEntity original, String newId, String newName) {
        Units.Unit unit = original.officialUnit;
        ObjectEntity copy = new ObjectEntity(newId, newName, Category.OFFICIAL_UNIT, original.faction,
            original.footprintX, original.footprintZ, original.health, original.metal, original.energy,
            original.tooltip, null, "", unit);
        copy.tier = original.tier;
        copy.speed = original.speed;
        copy.cloneOf = unit.id();
        copy.owner = original.owner;
        // 0 = il raggio visivo resta quello dell'unita' originale
        copy.sightRange = 0;
        return copy;
    }
    public String getOfficialUnitId() { return officialUnit != null ? officialUnit.id() : null; }
    /** A chi appartiene l'unita' quando compare in partita. */
    public enum Owner {
        /** Come prima: la squadra della sua fazione se c'e', altrimenti neutrale. */
        AUTO,
        /** Neutrale: non attacca e non viene attaccata in automatico. */
        NEUTRAL,
        /** Nemica di tutti i giocatori: attacca chiunque e viene attaccata. */
        HOSTILE,
        TEAM_A,
        TEAM_B,
        /** Squadra di quella fazione, se in partita ce n'e' una: il colore e' quello della squadra. */
        ARMADA,
        CORTEX,
        LEGION,
        /** Squadra dei Raptor o degli Scavenger se la partita li ha, altrimenti nemica di tutti. */
        RAPTORS,
        SCAVENGERS
    }

    /** true se con questo schieramento l'unita' non prende il colore di una squadra e se ne puo' scegliere uno. */
    public boolean isColourFree() {
        return owner == Owner.HOSTILE || owner == Owner.NEUTRAL;
    }

    private Owner owner = Owner.AUTO;
    /** Raggio visivo in elmi delle unita' create dallo Studio. */
    private int sightRange = 350;

    public int getSightRange() { return sightRange; }
    public void setSightRange(int sightRange) { this.sightRange = Math.max(0, Math.min(10000, sightRange)); }

    /** Valori originali dell'unita' di BAR (-1 se l'oggetto non e' un'unita' del gioco). */
    public int getOfficialHealth() { return officialUnit != null ? officialUnit.health() : -1; }
    public int getOfficialMetal() { return officialUnit != null ? officialUnit.metal() : -1; }
    public int getOfficialEnergy() { return officialUnit != null ? officialUnit.energy() : -1; }
    /** Colore con cui tingere la texture dei modelli importati (0xRRGGBB), null = colori originali. */
    private Integer tintRgb;

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner != null ? owner : Owner.AUTO; }
    public Integer getTintRgb() { return tintRgb; }
    public void setTintRgb(Integer tintRgb) { this.tintRgb = tintRgb; }

    /** Copia dell'immagine con i colori moltiplicati per la tinta (bianco diventa la tinta). L'alpha resta com'e'. */
    public static BufferedImage tinted(BufferedImage img, Integer tintRgb) {
        if (img == null || tintRgb == null) return img;
        int w = img.getWidth();
        int h = img.getHeight();
        int[] px = img.getRGB(0, 0, w, h, null, 0, w);
        int tr = (tintRgb >> 16) & 255, tg = (tintRgb >> 8) & 255, tb = tintRgb & 255;
        for (int i = 0; i < px.length; i++) {
            int c = px[i];
            int r = (((c >> 16) & 255) * tr) / 255;
            int g = (((c >> 8) & 255) * tg) / 255;
            int b = ((c & 255) * tb) / 255;
            px[i] = (c & 0xFF000000) | (r << 16) | (g << 8) | b;
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        out.setRGB(0, 0, w, h, px, 0, w);
        return out;
    }

    public float getScale() { return scale; }
    public void setScale(float scale) {
        this.scale = Math.max(0.01f, Math.min(500.0f, scale));
    }
    public IconKind getIconKind() { return iconKind; }
    public void setIconKind(IconKind k) { this.iconKind = k != null ? k : IconKind.NONE; }
    public boolean isShowIcon() { return showIcon; }
    public void setShowIcon(boolean b) { this.showIcon = b; }

    /** Crea variante con scala propria (nuovo tipo, nuovo id). Per differenziare dimensioni in BAR. */
    public ObjectEntity createVariant(float newScale) {
        ObjectEntity v = new ObjectEntity(id + "_v2", name + " (variante)", category, faction,
            footprintX, footprintZ, health, metal, energy, tooltip, model3D, luaScript, officialUnit);
        v.setScale(newScale);
        v.setTier(tier); v.setSpeed(speed);
        v.setObjFilePath(objFilePath); v.setTexturePath(texturePath);
        v.setPreviewImage(previewImage);
        v.setIconKind(iconKind); v.setShowIcon(showIcon);
        return v;
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
