package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.map.Army;
import com.faforever.neroxis.map.Group;
import com.faforever.neroxis.map.Marker;
import com.faforever.neroxis.map.Prop;
import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import com.faforever.neroxis.map.Unit;
import com.faforever.neroxis.util.serial.biome.TerrainMaterials;
import com.faforever.neroxis.util.vector.Vector3;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/**
 * Trasforma una mappa dell'editor in una mappa giocabile di Beyond All Reason: un archivio .sdz con il file
 * di descrizione, il terreno, le texture, gli oggetti e gli script.
 */
public final class BarMapExporter {
    /** Porta su cui il motore ascolta quando si ospita una partita. */
    public static final int HOST_PORT = 8452;
    /** Inizio del nome delle mappe generate senza un nome scelto dall'utente. */
    public static final String GENERATED_PREFIX = "Map_";

    // ---- terreno
    /** Una mappa senz'acqua viene alzata almeno a questa quota, in elmi. */
    private static final float DRY_MAP_MIN_HEIGHT = 48.0f;
    /** Pendenza massima che veicoli e bot riescono a salire (dislivello per elmo). */
    private static final float VEHICLE_MAX_GRADIENT = 0.48f;
    private static final float BOT_MAX_GRADIENT = 1.37f;
    /** Profondita' massima, in elmi, che le unita' di terra attraversano a guado. */
    private static final float MAX_WADING_DEPTH = 22.0f;

    // ---- risorse
    /** Due punti metallo piu' vicini di cosi', in elmi, vengono fusi in uno. */
    private static final float MIN_METAL_SPOT_SPACING = 112.0f;
    /** Metallo totale lasciato in rocce e relitti per ogni giocatore. */
    private static final float RECLAIM_METAL_PER_PLAYER = 600.0f;
    /** Raggio, in elmi, del segno disegnato a terra sotto un punto metallo e sotto un geyser. */
    private static final float MEX_MARK_RADIUS = 36.0f;
    private static final float GEO_MARK_RADIUS = 34.0f;
    /** Raggio, in elmi, entro cui il terreno intorno a un punto metallo appare consumato. */
    private static final float WORN_GROUND_RADIUS = 120.0f;

    // ---- colori
    /** Larghezza della spiaggia, in elmi dall'acqua. */
    private static final float BEACH_WIDTH = 90.0f;
    /** Dispersione del rumore usato per variare i colori. */
    private static final float NOISE_DEVIATION = 0.23f;
    /** Luminosita' e vivacita' medie a cui viene portato il terreno; oltre la terza la vivacita' viene ridotta. */
    private static final float TARGET_LUMINANCE = 0.38f;
    private static final float TARGET_SATURATION = 0.16f;
    private static final float MAX_SATURATION = 0.3f;

    // ---- relitti di BAR usati al posto delle unita' piazzate nell'editor, per taglia
    private static final String[] SMALL_WRECKS = {
        "armstump_dead", "corraid_dead", "armpw_dead", "corak_dead",
        "armflash_dead", "corgator_dead", "armham_dead", "corthud_dead"};
    private static final String[] MEDIUM_WRECKS = {"armbull_dead", "correap_dead", "armzeus_dead", "corcan_dead"};
    private static final String[] LARGE_WRECKS = {"armbull_dead", "correap_dead", "corcan_dead", "armzeus_dead"};
    private static final String[] SHIP_WRECKS = {
        "armpship_dead", "corpship_dead", "armcrus_dead",
        "corcrus_dead", "armroy_dead", "corroy_dead"};
    private static final String[] BUILDING_WRECKS = {
        "armsolar_dead", "corsolar_dead", "armllt_dead", "corllt_dead",
        "armwin_dead", "corwin_dead", "armmstor_dead", "cormstor_dead"};
    /** Metallo che si recupera da ogni relitto. */
    private static final Map<String, Integer> WRECK_METAL = Map.ofEntries(
        Map.entry("armstump_dead", 152), Map.entry("corraid_dead", 167), Map.entry("armpw_dead", 29),
        Map.entry("corak_dead", 22), Map.entry("armflash_dead", 71), Map.entry("corgator_dead", 77),
        Map.entry("armham_dead", 79), Map.entry("corthud_dead", 96), Map.entry("armbull_dead", 549),
        Map.entry("correap_dead", 350), Map.entry("armzeus_dead", 214), Map.entry("corcan_dead", 339),
        Map.entry("armpship_dead", 195), Map.entry("corpship_dead", 205), Map.entry("armcrus_dead", 500),
        Map.entry("corcrus_dead", 500), Map.entry("armroy_dead", 440), Map.entry("corroy_dead", 480),
        Map.entry("armsolar_dead", 75), Map.entry("corsolar_dead", 72), Map.entry("armllt_dead", 53),
        Map.entry("corllt_dead", 55), Map.entry("armwin_dead", 23), Map.entry("corwin_dead", 27),
        Map.entry("armmstor_dead", 198), Map.entry("cormstor_dead", 208));
    /** Codici delle unita' dell'editor considerate piccole (primo livello) e grandi (terzo livello). */
    private static final List<String> T1_LAND = List.of("UEL0201", "URL0107", "UAL0201", "XSL0201");
    private static final List<String> T3_LAND = List.of("XEL0305", "UEL0303", "URL0303", "XRL0305", "UAL0303", "XSL0303");

    // ---- la mappa dell'editor e le scelte di export
    private final SCMap map;
    private BarExportOptions options;
    /** Immagine scelta dall'utente come colore del terreno (null = colori generati). */
    private final BufferedImage customTexture;
    /** Materiali delle 4 zone del pannello Texture (null = texturizzazione automatica o immagine unica). */
    private final ZoneMaterials zoneMaterials;
    /** Bioma ricavato da una mappa BAR (null = bioma Neroxis): tavolozza, luce, atmosfera e acqua. */
    private final BarBiomes.BarBiome barBiome;

    // ---- dimensioni: celle dell'editor, quadrati di BAR (8 elmi), pixel della texture
    private final int faSize;
    private final int faWidth;
    private final int faHeight;
    private final int mapX;
    private final int mapY;
    private final int textureWidth;
    private final int textureHeight;
    private final float elmosPerCell;

    // ---- quote
    /** Livello dell'acqua nell'editor (NaN = mappa asciutta). */
    private final float waterElevation;
    /** Quote dell'editor, una per punto della sua griglia. */
    private float[] faHeights;
    /** Quote della mappa di BAR in elmi, una per vertice dei quadrati. */
    private final float[] heights;
    private float minHeight;
    private float maxHeight;
    /** Quote tipiche del terreno basso e di quello alto, per variare i colori con l'altitudine. */
    private float lowGround;
    private float highGround = 1.0f;
    /** Distanza di ogni quadrato dall'acqua piu' vicina. */
    private float[] shoreDistance = new float[0];

    // ---- colori
    /** Copertura di ognuno degli otto strati di texture dell'editor, e dimensioni delle loro maschere. */
    private final float[][] layerWeights;
    private final int layerWidth;
    private final int layerHeight;
    /** Colore dello strato di fondo e degli otto strati; tipo di ogni strato (0 suolo, 1 pendio, 2 roccia). */
    private final float[][] layerColors;
    private final int[] layerKinds;
    private final float[] noise;
    private final float[] sunDirection;
    private float[] rockColor = {0.42f, 0.4f, TARGET_LUMINANCE};
    private float[] sandColor = {0.62f, 0.56f, 0.42f};
    private float[] dirtColor = {0.4f, 0.33f, 0.24f};
    private boolean snowGround;
    private GroundDetail.Material groundMaterial;

    // ---- risorse e vegetazione; ogni punto: x, z in elmi, metallo, quanti punti riunisce
    private final List<float[]> mexPositions = new ArrayList<>();
    private final List<float[]> geoPositions = new ArrayList<>();
    private List<MapFlora.PlantSet> plantSets = List.of();

    /** Un oggetto da recuperare (roccia o relitto): quanto metallo da' e in che ordine tenerlo se sono troppi. */
    private static record ReclaimFeature(String name, float x, float z, float rotation, int metal, float order) {}

    private BarMapExporter(SCMap map, BarExportOptions options) {
        this.map = map;
        this.options = options;
        this.customTexture = map.getCustomTexture();
        this.zoneMaterials = ZoneMaterials.get(map);
        this.barBiome = BarBiomes.get(map);

        // dimensioni: l'editor lavora in celle, BAR in "quadrati" da 8 elmi con 8 pixel di texture ciascuno
        this.faSize = map.getSize();
        this.faWidth = map.getWidth() > 0 ? map.getWidth() : this.faSize;
        this.faHeight = map.getHeight() > 0 ? map.getHeight() : this.faSize;
        this.mapX = this.faWidth * options.squaresPerCell();
        this.mapY = this.faHeight * options.squaresPerCell();
        if (this.mapX % 128 != 0 || this.mapY % 128 != 0) {
            throw new IllegalArgumentException("The map size %dx%d can not be converted to a BAR map, size * %d must be a multiple of 128"
                .formatted(this.faWidth, this.faHeight, options.squaresPerCell()));
        }
        this.textureWidth = this.mapX * 8;
        this.textureHeight = this.mapY * 8;
        this.elmosPerCell = 8 * options.squaresPerCell();
        this.heights = new float[(this.mapX + 1) * (this.mapY + 1)];

        // cio' che viene dall'editor: livello dell'acqua, quote e strati di texture
        this.waterElevation = map.getBiome().waterSettings().waterPresent() ? map.getBiome().waterSettings().elevation() : Float.NaN;
        this.faHeights = readHeights(map.getHeightmap(), map.getHeightMapScale());
        this.layerWidth = map.getTextureMasksLow().getWidth();
        this.layerHeight = map.getTextureMasksLow().getHeight();
        this.layerWeights = readLayerWeights(map.getTextureMasksLow(), map.getTextureMasksHigh());
        this.layerColors = readLayerColors();
        // tipo di ogni strato: 0 suolo, 1 pendio, 2 roccia (il bioma "sunset" li ordina diversamente)
        boolean sunsetOrder = String.valueOf(map.getBiome().name()).equalsIgnoreCase("sunset");
        this.layerKinds = sunsetOrder ? new int[]{0, 2, 0, 0, 1, 1, 0, 0} : new int[]{0, 0, 1, 1, 0, 0, 2, 2};
        this.noise = createNoise();

        // direzione del sole: quella del bioma, ma mai troppo radente ne' a picco, e sempre da "dietro"
        Vector3 sun = map.getBiome().lightingSettings().sunDirection();
        float sunLength = (float) StrictMath.sqrt(sun.x() * sun.x() + sun.y() * sun.y() + sun.z() * sun.z());
        this.sunDirection = sunLength > 0.0f
            ? new float[]{sun.x() / sunLength, sun.y() / sunLength, sun.z() / sunLength}
            : new float[]{0.5f, 0.7f, -0.5f};
        this.sunDirection[2] = -Math.max(0.45f, Math.abs(this.sunDirection[2]));
        this.sunDirection[1] = Math.max(0.55f, Math.min(0.8f, this.sunDirection[1]));
        normalize(this.sunDirection);
        if (this.barBiome != null) {
            applyBarBiomePalette();
        }
    }

    /** Sostituisce i colori degli strati Neroxis con la tavolozza della mappa BAR, e il sole con il suo. */
    private void applyBarBiomePalette() {
        boolean alternate = false;
        for (int i = 0; i < this.layerColors.length; i++) {
            if (this.layerColors[i] == null) {
                continue;
            }
            int kind = i == 0 ? 0 : this.layerKinds[i - 1];
            float[] color;
            if (kind == 2) {
                color = this.barBiome.rock;
            } else if (kind == 1) {
                color = this.barBiome.slope;
            } else {
                color = i == 0 || !alternate ? this.barBiome.ground : this.barBiome.high;
                if (i > 0) {
                    alternate = !alternate;
                }
            }
            this.layerColors[i] = color.clone();
        }
        if (this.barBiome.sunDir != null) {
            float[] sun = this.barBiome.sunDir;
            float length = (float) StrictMath.sqrt((sun[0] * sun[0]) + (sun[1] * sun[1]) + (sun[2] * sun[2]));
            if (length > 0.01f) {
                this.sunDirection[0] = sun[0] / length;
                this.sunDirection[1] = Math.max(0.35f, sun[1] / length);
                this.sunDirection[2] = sun[2] / length;
                normalize(this.sunDirection);
            }
        }
    }

    /** Roccia, terra e sabbia dalla tavolozza BAR al posto di quelle stimate. */
    private void applyBarBiomeAccents() {
        if (this.barBiome == null) {
            return;
        }
        System.arraycopy(this.barBiome.rock, 0, this.rockColor, 0, 3);
        float[] dirt = BarBiomes.mix(this.barBiome.ground, this.barBiome.slope, 0.6f);
        for (int c = 0; c < 3; c++) {
            this.dirtColor[c] = dirt[c] * 0.9f;
        }
        if (this.barBiome.sand != null) {
            System.arraycopy(this.barBiome.sand, 0, this.sandColor, 0, 3);
        }
    }

    /** Esporta la mappa nella cartella indicata e torna il file creato. */
    public static Path exportMap(Path folderPath, SCMap map, BarExportOptions options) throws IOException {
        return new BarMapExporter(map, options).export(folderPath);
    }

    /** Come sopra, scrivendo anche lo script per provare la mappa contro l'IA. */
    public static Path exportMap(Path folderPath, SCMap map, BarExportOptions options, Path skirmishScriptPath) throws IOException {
        return exportMap(folderPath, map, options, skirmishScriptPath, null, List.of(), false, null);
    }

    /**
     * Esporta la mappa e scrive gli script di partita richiesti.
     *
     * @param skirmishScriptPath dove scrivere lo script contro l'IA, o null
     * @param hostScriptPath dove scrivere lo script per ospitare una partita, o null
     * @param players nomi dei giocatori della partita ospitata, il primo e' chi ospita
     * @param coop vero per mettere tutti i giocatori nella stessa squadra
     * @param password password chiesta a chi entra, o null
     */
    public static Path exportMap(Path folderPath, SCMap map, BarExportOptions options, Path skirmishScriptPath, Path hostScriptPath,
                                 List<String> players, boolean coop, String password) throws IOException {
        BarMapExporter exporter = new BarMapExporter(map, options);
        Path archive = exporter.export(folderPath);
        if (skirmishScriptPath != null) {
            writeScript(skirmishScriptPath, exporter.buildGameScript(List.of("Player"), true, false, null));
        }
        if (hostScriptPath != null) {
            if (players.isEmpty() || players.size() > map.getSpawnCount()) {
                throw new IllegalArgumentException("A hosted game needs between 1 and %d player names".formatted(map.getSpawnCount()));
            }
            for (String player : players) {
                if (!player.matches("[A-Za-z0-9_\\[\\]]{2,24}")) {
                    throw new IllegalArgumentException("Player name '%s' may only contain letters, digits, _ [ ]".formatted(player));
                }
            }
            writeScript(hostScriptPath, exporter.buildGameScript(players, coop, true, password));
        }
        return archive;
    }

    private static void writeScript(Path path, String script) throws IOException {
        Path folder = path.toAbsolutePath().getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }
        Files.writeString(path, script);
    }

    /**
     * Lo script con cui il motore avvia una partita su questa mappa.
     *
     * <p>Ogni punto di partenza diventa una squadra del motore; i giocatori le occupano (a turno tra le
     * alleanze, oppure tutti insieme se {@code coop}) e quelle rimaste libere vanno all'IA.</p>
     *
     * @param hosted vero per una partita a cui altri si collegano, falso per una partita locale
     */
    private String buildGameScript(List<String> players, boolean coop, boolean hosted, String password) {
        List<Spawn> spawns = new ArrayList<>(this.map.getSpawns());
        Map<Spawn, Integer> spawnTeams = resolveTeams(spawns);
        spawns.sort(Comparator.comparingInt(spawn -> spawnTeams.getOrDefault(spawn, 0)));
        List<Integer> alliances = spawns.stream().map(spawn -> spawnTeams.getOrDefault(spawn, 0)).distinct().toList();
        int slots = spawns.size();
        int[] allianceOf = new int[slots];
        for (int slot = 0; slot < slots; slot++) {
            allianceOf[slot] = alliances.indexOf(spawnTeams.getOrDefault(spawns.get(slot), 0));
        }

        // a ogni giocatore il primo posto libero della sua alleanza, altrimenti il primo libero
        int[] playerAt = new int[slots];
        Arrays.fill(playerAt, -1);
        for (int player = 0; player < players.size(); player++) {
            int wanted = coop ? -1 : player % alliances.size();
            int chosen = -1;
            for (int slot = 0; slot < slots && chosen < 0; slot++) {
                if (playerAt[slot] < 0 && (wanted < 0 || allianceOf[slot] == wanted)) {
                    chosen = slot;
                }
            }
            for (int slot = 0; slot < slots && chosen < 0; slot++) {
                if (playerAt[slot] < 0) {
                    chosen = slot;
                }
            }
            playerAt[chosen] = player;
        }

        StringBuilder script = new StringBuilder();
        script.append("[game]\n{\n")
            .append("\tgametype=rapid://byar:test;\n")
            .append("\tmapname=").append(getDisplayName()).append(";\n")
            .append("\tishost=1;\n")
            .append("\thostip=").append(hosted ? "" : "127.0.0.1").append(";\n")
            .append("\thostport=").append(hosted ? HOST_PORT : 0).append(";\n")
            .append("\tmyplayername=").append(players.get(0)).append(";\n")
            // posizioni statiche (3: ognuno sulla sua base) oppure zona di partenza (2: si sceglie in gioco)
            .append("\tstartpostype=").append(MapExtraOptions.get().startZones ? "2" : "3").append(";\n")
            .append("\tnumplayers=").append(players.size()).append(";\n")
            .append("\t[modoptions]\n\t{\n\t\tdraft_mode=disabled;\n")
            .append(startPosModoption()).append(extraUnitsModoptions())
            .append("\t}\n");
        for (int player = 0; player < players.size(); player++) {
            int slot = 0;
            for (int i = 0; i < slots; i++) {
                if (playerAt[i] == player) {
                    slot = i;
                }
            }
            script.append("\t[player").append(player).append("]\n\t{\n")
                .append("\t\tname=").append(players.get(player)).append(";\n")
                .append("\t\tteam=").append(slot).append(";\n")
                .append("\t\tspectator=0;\n");
            if (hosted && password != null && player > 0) {
                script.append("\t\tpassword=").append(password).append(";\n");
            }
            script.append("\t}\n");
        }
        int computers = 0;
        for (int slot = 0; slot < slots; slot++) {
            if (playerAt[slot] < 0) {
                script.append("\t[ai").append(computers).append("]\n\t{\n")
                    .append("\t\tname=BARbarian ").append(computers + 1).append(";\n")
                    .append("\t\tshortname=BARb;\n\t\tversion=stable;\n")
                    .append("\t\tteam=").append(slot).append(";\n")
                    .append("\t\thost=0;\n\t}\n");
                computers++;
            }
            Spawn spawn = spawns.get(slot);
            script.append("\t[team").append(slot).append("]\n\t{\n")
                .append("\t\tteamleader=").append(Math.max(0, playerAt[slot])).append(";\n")
                .append("\t\tallyteam=").append(allianceOf[slot]).append(";\n")
                .append("\t\tside=").append(slot % 2 == 0 ? "Armada" : "Cortex").append(";\n")
                .append("\t\thandicap=0;\n")
                .append(String.format(Locale.ROOT, "\t\tstartposx=%.0f;\n\t\tstartposz=%.0f;\n",
                    spawn.getPosition().x() * this.elmosPerCell, spawn.getPosition().z() * this.elmosPerCell))
                .append("\t}\n");
        }
        // la zona di partenza di ogni alleanza: il rettangolo che contiene le sue basi, allargato del margine scelto
        float margin = Math.max(0, Math.min(40, MapExtraOptions.get().startZoneMargin)) / 100.0f;
        for (int alliance = 0; alliance < alliances.size(); alliance++) {
            float left = 1.0f;
            float top = 1.0f;
            float right = 0.0f;
            float bottom = 0.0f;
            for (int slot = 0; slot < slots; slot++) {
                if (allianceOf[slot] == alliance) {
                    float x = spawns.get(slot).getPosition().x() / this.faWidth;
                    float z = spawns.get(slot).getPosition().z() / this.faHeight;
                    left = Math.min(left, x);
                    right = Math.max(right, x);
                    top = Math.min(top, z);
                    bottom = Math.max(bottom, z);
                }
            }
            script.append(String.format(Locale.ROOT,
                "\t[allyteam%d]\n\t{\n\t\tnumallies=0;\n\t\tstartrectleft=%.3f;\n\t\tstartrecttop=%.3f;\n\t\tstartrectright=%.3f;\n\t\tstartrectbottom=%.3f;\n\t}\n",
                alliance, Math.max(0.0f, left - margin), Math.max(0.0f, top - margin), Math.min(1.0f, right + margin), Math.min(1.0f, bottom + margin)));
        }
        script.append("}\n");
        return script.toString();
    }

    /** Punti di partenza in elmi, per squadra alleata, con il ruolo consigliato di ognuno (null se non applicabile). */
    private SpawnRoles.Guide buildStartGuide() {
        List<Spawn> spawns = new ArrayList<>(this.map.getSpawns());
        Map<Spawn, Integer> spawnTeams = resolveTeams(spawns);
        boolean anyRole = spawns.stream().anyMatch(spawn -> SpawnRoles.get(this.map, spawn) != null);
        if (!anyRole) {
            SpawnRoles.assignAllFront(this.map);
        }
        List<Integer> teamIds = spawns.stream().map(spawn -> spawnTeams.getOrDefault(spawn, 0)).distinct().sorted().toList();
        SpawnRoles.Guide guide = new SpawnRoles.Guide();
        for (int teamId : teamIds) {
            List<int[]> side = new ArrayList<>();
            for (Spawn spawn : spawns) {
                if (spawnTeams.getOrDefault(spawn, 0) == teamId) {
                    float[] elmos = toElmos(spawn);
                    side.add(new int[]{guide.positions.size()});
                    guide.positions.add(new float[]{elmos[0], elmos[1]});
                    String role = SpawnRoles.get(this.map, spawn);
                    guide.roleOfPosition.add(role != null ? role : "front");
                }
            }
            guide.sides.add(side);
        }
        return guide.usable() ? guide : null;
    }

    /**
     * BAR carica le unita' Legion, Scavenger e Raptor solo se la partita lo chiede con una modoption.
     * Se sulla mappa ne sono state piazzate, la partita avviata dallo Studio le abilita da sola.
     */
    private String extraUnitsModoptions() {
        boolean legion = false;
        boolean others = false;
        try {
            for (Object obj : this.map.getCustomEntities()) {
                if (!(obj instanceof com.faforever.neroxis.generator.bar.PlacedCustomEntity placed)) {
                    continue;
                }
                com.faforever.neroxis.generator.bar.ObjectEntity ent = placed.getEntity();
                if (ent == null || !ent.hasOfficialUnit()) {
                    continue;
                }
                String faction = ent.getFaction() == null ? "" : ent.getFaction();
                String id = ent.getOfficialUnitId() == null ? "" : ent.getOfficialUnitId().toLowerCase(Locale.ROOT);
                if (faction.equalsIgnoreCase("Legion") || id.startsWith("leg")) {
                    legion = true;
                } else if (faction.equalsIgnoreCase("Other")) {
                    others = true;
                }
            }
        } catch (RuntimeException ex) {
            return "";
        }
        return (legion ? "\t\texperimentallegionfaction=1;\n" : "") + (others ? "\t\tforceallunits=1;\n" : "");
    }

    /** Riga della modoption che fa comparire in BAR i cerchi con i ruoli (vuota se non servono). */
    private String startPosModoption() {
        try {
            SpawnRoles.Guide guide = buildStartGuide();
            return guide == null ? "" : "\t\tmapmetadata_startpos=" + guide.toModoption() + ";\n";
        } catch (RuntimeException ex) {
            return "";
        }
    }

    private static void addEntry(ZipOutputStream zip, String name, byte[] content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content);
        zip.closeEntry();
    }

    /**
     * Costruisce la mappa e la scrive come archivio .sdz nella cartella indicata.
     * Prima si calcola tutto cio' che serve in memoria (quote, metallo, oggetti, anteprima), poi si
     * scrivono i file nell'archivio.
     */
    private Path export(Path folderPath) throws IOException {
        Files.createDirectories(folderPath);
        String archiveName = toFileName(getDisplayName());
        Path archivePath = folderPath.resolve(archiveName + ".sdz");

        buildHeights();
        for (Marker mex : this.map.getMexes()) {
            this.mexPositions.add(toElmos(mex));
        }
        mergeCloseMetalSpots();
        assignMetalValues();
        // se un punto vale piu' del massimo previsto, il massimo si alza: nella mappa del metallo il valore
        // piu' alto deve corrispondere al punto piu' ricco
        float richest = this.options.maxMetal();
        for (float[] mex : this.mexPositions) {
            richest = Math.max(richest, mex[2]);
        }
        if (richest > this.options.maxMetal()) {
            BarExportOptions o = this.options;
            this.options = new BarExportOptions(o.squaresPerCell(), o.heightScale(), o.baseMetal(), o.minMetal(), richest, o.uniformMetal(),
                o.numTeams(), o.mapName(), o.mapVersion(), o.author(), o.reclaim(), o.featuresRepository(), o.minWind(), o.maxWind(), o.tidal());
        }
        for (Marker hydro : this.map.getHydros()) {
            this.geoPositions.add(toElmos(hydro));
        }
        List<String> featureTypes = new ArrayList<>();
        List<SpringMapFile.Feature> features = buildFeatures(featureTypes);
        byte[] metalMap = buildMetalMap();
        // nel file le quote sono numeri da 0 a 65535 tra la minima e la massima
        short[] storedHeights = new short[this.heights.length];
        float range = Math.max(this.maxHeight - this.minHeight, 1.0f);
        for (int i = 0; i < this.heights.length; i++) {
            storedHeights[i] = (short) Math.round((this.heights[i] - this.minHeight) / range * 65535.0f);
        }
        BufferedImage minimapImage = renderMinimap();
        byte[] minimap = compressWithMips(minimapImage, SpringMapFile.MINIMAP_MIP_LEVELS);

        // Riesportando una mappa con lo stesso nome il file esistente viene riscritto, non cancellato: nella
        // cartella mappe di BAR un utente normale puo' scrivere ma spesso non cancellare (per esempio un file
        // creato da un avvio come amministratore), e la cancellazione faceva fallire l'export.
        try {
            Files.deleteIfExists(archivePath);
        } catch (IOException | SecurityException ignored) {
        }
        BufferedImage previewImage;
        byte[] previewPng;
        String mapId = "maps/" + archiveName;
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(archivePath)))) {
            zip.setLevel(6);
            addEntry(zip, "mapinfo.lua", buildMapInfo().getBytes(StandardCharsets.UTF_8));

            zip.putNextEntry(new ZipEntry(mapId + ".smf"));
            SpringMapFile.write(zip, this.map.getName().hashCode() & Integer.MAX_VALUE, this.mapX, this.mapY, this.minHeight, this.maxHeight,
                storedHeights, metalMap, buildGrassMap(), minimap, archiveName + ".smt", featureTypes, features);
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry(mapId + ".smt"));
            writeTiles(zip);
            zip.closeEntry();

            GroundDetail.Material[] materials = getSplatMaterials();
            for (int i = 0; i < materials.length; i++) {
                byte[] detail = this.zoneMaterials != null
                    ? this.zoneMaterials.layers[i].createDetailTexture()
                    : GroundDetail.createTexture(materials[i], this.map.getName().hashCode() + i * 7919);
                addEntry(zip, mapId + "_dnts" + (i + 1) + ".tga", detail);
            }
            addEntry(zip, mapId + "_splats.dds", buildSplatDistribution());

            // alberi e piante della raccolta di BAR, se disponibile
            Path repository = this.options.featuresRepository();
            if (repository != null && this.plantSets.stream().anyMatch(MapFlora.PlantSet::fromRepository)) {
                for (Map.Entry<String, Path> file : MapFlora.collectFiles(this.plantSets, repository).entrySet()) {
                    zip.putNextEntry(new ZipEntry(file.getKey()));
                    Files.copy(file.getValue(), zip);
                    zip.closeEntry();
                }
            }

            // oggetti e unita' dello Studio, con i loro script
            writeCustomEntities(zip);
            SpawnRoles.Guide startGuide = buildStartGuide();
            if (startGuide != null) {
                addEntry(zip, "mapconfig/maps-metadata_startPos.yaml", startGuide.toYaml().getBytes(StandardCharsets.UTF_8));
            }
            MapExtraOptions extra = MapExtraOptions.get();
            if (extra.lavaMode > 0) {
                addEntry(zip, "mapconfig/lava.lua", extra.buildLavaConfig().getBytes(StandardCharsets.UTF_8));
            }
            if (extra.needsGadget()) {
                addEntry(zip, "luarules/gadgets/map_options.lua", extra.buildGadget().getBytes(StandardCharsets.UTF_8));
            }

            addEntry(zip, mapId + "_normals.tga", buildNormalsTexture());
            addEntry(zip, mapId + "_specular.dds", buildSpecularTexture());
            addEntry(zip, mapId + "_edge.dds", buildEdgeTexture(minimapImage));

            // la stessa anteprima con tre nomi: quello della mappa e i due che cercano i programmi esterni
            previewImage = renderPreview(minimapImage);
            ByteArrayOutputStream png = new ByteArrayOutputStream();
            ImageIO.write(previewImage, "png", png);
            previewPng = png.toByteArray();
            addEntry(zip, mapId + "_preview.png", previewPng);
            addEntry(zip, "preview.png", previewPng);
            addEntry(zip, "minimap.png", previewPng);
        }
        saveLobbyMinimaps(folderPath, archiveName, getDisplayName(), previewImage, previewPng);
        System.out.printf(Locale.ROOT, "BAR map: %dx%d, heights %.0f to %.0f, %d metal spots, %d geo vents, %d features%n",
            this.mapX / 64, this.mapY / 64, this.minHeight, this.maxHeight, this.mexPositions.size(), this.geoPositions.size(), features.size());
        System.out.println("Metal spot incomes: " + describeMetal());
        if (this.zoneMaterials != null) {
            System.out.println("Zone materials: " + this.zoneMaterials.describe());
        }
        return archivePath;
    }

    /** Le quote dell'editor, una per punto della griglia, gia' moltiplicate per la scala della mappa. */
    private static float[] readHeights(BufferedImage heightmap, float heightMapScale) {
        int width = heightmap.getWidth();
        int height = heightmap.getHeight();
        WritableRaster raster = heightmap.getRaster();
        float[] values = new float[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                values[y * width + x] = raster.getSample(x, y, 0) * heightMapScale;
            }
        }
        return values;
    }

    /**
     * Costruisce le quote della mappa di BAR: ripulisce quelle dell'editor, le porta alla risoluzione del gioco
     * (in elmi, con il mare a quota zero), poi applica i ritocchi e ricava minimo e massimo.
     */
    private void buildHeights() {
        int gridWidth = this.faWidth + 1;
        int gridHeight = this.faHeight + 1;
        this.faHeights = cleanHeights(this.faHeights, gridWidth, gridHeight);
        float lowest = Float.MAX_VALUE;
        for (float value : this.faHeights) {
            lowest = Math.min(lowest, value);
        }
        // senza acqua il livello zero sta un po' sotto il punto piu' basso
        float seaLevel = Float.isNaN(this.waterElevation) ? lowest - 4.0f : this.waterElevation;
        float verticalScale = this.elmosPerCell * this.options.heightScale();
        int squaresPerCell = this.options.squaresPerCell();
        int row = this.mapX + 1;
        IntStream.rangeClosed(0, this.mapY).parallel().forEach(y -> {
            float cellY = y / (float) squaresPerCell;
            for (int x = 0; x <= this.mapX; x++) {
                float cellX = x / (float) squaresPerCell;
                this.heights[y * row + x] = (sampleCubic(this.faHeights, gridWidth, gridHeight, cellX, cellY) - seaLevel) * verticalScale;
            }
        });
        sharpenCliffs();
        addRidges();
        ensureVehiclePaths();
        applyMaterialRelief();
        gradeColors();
        buildShoreDistance();
        pickRockAndSandColors();
        applyBarBiomeAccents();

        float low = Float.MAX_VALUE;
        float high = -Float.MAX_VALUE;
        for (float value : this.heights) {
            low = Math.min(low, value);
            high = Math.max(high, value);
        }
        // una mappa asciutta troppo vicina a quota zero viene alzata, per non far comparire acqua
        if (low >= 0.0f && low < DRY_MAP_MIN_HEIGHT) {
            float lift = DRY_MAP_MIN_HEIGHT - low;
            for (int i = 0; i < this.heights.length; i++) {
                this.heights[i] += lift;
            }
            low += lift;
            high += lift;
        }
        this.minHeight = (float) Math.floor(low);
        this.maxHeight = (float) Math.ceil(high);
    }

    /** Rilievo vero dei materiali di zona (es. bottoncini): sposta le quote, 1 punto ogni 8 elmi. */
    private void applyMaterialRelief() {
        if (this.zoneMaterials == null || !this.zoneMaterials.hasRelief()) {
            return;
        }
        IntStream.rangeClosed(0, this.mapY).parallel().forEach(y -> {
            float[] weights = new float[4];
            float z = y * 8.0f;
            for (int x = 0; x <= this.mapX; x++) {
                float wx = x * 8.0f;
                this.zoneMaterials.weights(wx / this.textureWidth, z / this.textureHeight, 0.0f, weights);
                float offset = 0.0f;
                for (int i = 0; i < 4; i++) {
                    ZoneMaterials.Layer layer = this.zoneMaterials.layers[i];
                    if (weights[i] > 0.0f && layer.hasRelief()) {
                        offset += weights[i] * layer.reliefAt(wx, z);
                    }
                }
                this.heights[(y * (this.mapX + 1)) + x] += offset;
            }
        });
    }

    /**
     * Ripulisce le quote dell'editor in tre passi: toglie i punti isolati (mediana 3x3), liscia le superfici
     * conservando i gradini (due passate che pesano poco i vicini a quota molto diversa), infine ammorbidisce.
     */
    private static float[] cleanHeights(float[] heights, int width, int height) {
        float[] despeckled = new float[heights.length];
        IntStream.range(0, height).parallel().forEach(y -> {
            float[] around = new float[9];
            for (int x = 0; x < width; x++) {
                int count = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        around[count++] = heights[clamp(y + dy, height) * width + clamp(x + dx, width)];
                    }
                }
                Arrays.sort(around);
                float own = heights[y * width + x];
                despeckled[y * width + x] = Math.abs(own - around[4]) > 0.35f ? around[4] : own;
            }
        });

        float[] current = despeckled;
        for (int pass = 0; pass < 2; pass++) {
            float[] input = current;
            float[] output = new float[heights.length];
            IntStream.range(0, height).parallel().forEach(y -> {
                for (int x = 0; x < width; x++) {
                    float own = input[y * width + x];
                    float sum = 0.0f;
                    float total = 0.0f;
                    for (int dy = -2; dy <= 2; dy++) {
                        for (int dx = -2; dx <= 2; dx++) {
                            float value = input[clamp(y + dy, height) * width + clamp(x + dx, width)];
                            float difference = value - own;
                            // conta di piu' chi e' vicino e a quota simile
                            float weight = (float) StrictMath.exp(-(dx * dx + dy * dy) / 3.4f - difference * difference / 1.3f);
                            sum += value * weight;
                            total += weight;
                        }
                    }
                    output[y * width + x] = sum / total;
                }
            });
            current = output;
        }

        float[] smoothed = current;
        float[] result = new float[heights.length];
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float sum = 0.0f;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        sum += smoothed[clamp(y + dy, height) * width + clamp(x + dx, width)] * (2 - Math.abs(dx)) * (2 - Math.abs(dy));
                    }
                }
                result[y * width + x] = sum / 16.0f;
            }
        });
        return result;
    }

    /**
     * Se i punti di partenza sono collegati per i bot ma non per i veicoli, abbassa tutto il rilievo quel tanto
     * che basta perche' anche i veicoli possano raggiungere ogni base.
     */
    private void ensureVehiclePaths() {
        if (this.map.getSpawns().size() < 2) {
            return;
        }
        int row = this.mapX + 1;
        float[] steepness = new float[this.mapX * this.mapY];
        float[] level = new float[this.mapX * this.mapY];
        IntStream.range(0, this.mapY).parallel().forEach(z -> {
            for (int x = 0; x < this.mapX; x++) {
                float nearLeft = this.heights[z * row + x];
                float nearRight = this.heights[z * row + x + 1];
                float farLeft = this.heights[(z + 1) * row + x];
                float farRight = this.heights[(z + 1) * row + x + 1];
                float slopeX = (nearRight + farRight - nearLeft - farLeft) / 16.0f;
                float slopeZ = (farLeft + farRight - nearLeft - nearRight) / 16.0f;
                steepness[z * this.mapX + x] = (float) StrictMath.sqrt(slopeX * slopeX + slopeZ * slopeZ);
                level[z * this.mapX + x] = (nearLeft + nearRight + farLeft + farRight) / 4.0f;
            }
        });
        boolean botsConnected = startsConnected(steepness, level, 1.0f, BOT_MAX_GRADIENT);
        if (!botsConnected || startsConnected(steepness, level, 1.0f, VEHICLE_MAX_GRADIENT)) {
            return;
        }
        for (float factor : new float[]{0.93f, 0.86f, 0.8f, 0.74f, 0.68f, 0.62f}) {
            if (startsConnected(steepness, level, factor, VEHICLE_MAX_GRADIENT)) {
                for (int i = 0; i < this.heights.length; i++) {
                    this.heights[i] *= factor;
                }
                System.out.printf(Locale.ROOT, "Flattened the terrain to %.0f%% so vehicles can reach every start position%n", factor * 100.0f);
                return;
            }
        }
    }

    /**
     * Vero se da un punto di partenza si raggiungono tutti gli altri passando solo per celle abbastanza piane
     * e non troppo sott'acqua, con il rilievo moltiplicato per {@code factor}.
     */
    private boolean startsConnected(float[] steepness, float[] level, float factor, float maxSteepness) {
        boolean[] reached = new boolean[steepness.length];
        int[] queue = new int[steepness.length];
        List<Spawn> spawns = this.map.getSpawns();
        int first = toSquareIndex(spawns.get(0));
        int head = 0;
        int tail = 0;
        queue[tail++] = first;
        reached[first] = true;
        int[] stepX = {1, -1, 0, 0};
        int[] stepZ = {0, 0, 1, -1};
        while (head < tail) {
            int cell = queue[head++];
            int x = cell % this.mapX;
            int z = cell / this.mapX;
            for (int direction = 0; direction < 4; direction++) {
                int nx = x + stepX[direction];
                int nz = z + stepZ[direction];
                if (nx < 0 || nz < 0 || nx >= this.mapX || nz >= this.mapY) {
                    continue;
                }
                int next = nz * this.mapX + nx;
                if (!reached[next] && steepness[next] * factor <= maxSteepness && level[next] * factor > -MAX_WADING_DEPTH) {
                    reached[next] = true;
                    queue[tail++] = next;
                }
            }
        }
        for (Spawn spawn : spawns) {
            if (!reached[toSquareIndex(spawn)]) {
                return false;
            }
        }
        return true;
    }

    /** La cella della mappa di BAR in cui cade un punto di partenza. */
    private int toSquareIndex(Spawn spawn) {
        int scale = this.options.squaresPerCell();
        int x = Math.max(0, Math.min(this.mapX - 1, Math.round(spawn.getPosition().x() * scale)));
        int z = Math.max(0, Math.min(this.mapY - 1, Math.round(spawn.getPosition().z() * scale)));
        return z * this.mapX + x;
    }

    /** Valore in un punto qualsiasi della griglia, interpolato con curve morbide sui 16 punti intorno. */
    private static float sampleCubic(float[] values, int width, int height, float x, float y) {
        int baseX = (int) Math.floor(x);
        int baseY = (int) Math.floor(y);
        float fractionX = x - baseX;
        float fractionY = y - baseY;
        if (fractionX == 0.0f && fractionY == 0.0f) {
            return values[clamp(baseY, height) * width + clamp(baseX, width)];
        }
        float result = 0.0f;
        for (int j = -1; j <= 2; j++) {
            float weightY = catmullRom(j - fractionY);
            int rowStart = clamp(baseY + j, height) * width;
            float rowSum = 0.0f;
            for (int i = -1; i <= 2; i++) {
                rowSum += values[rowStart + clamp(baseX + i, width)] * catmullRom(i - fractionX);
            }
            result += rowSum * weightY;
        }
        return result;
    }

    /** Peso di un punto a distanza {@code t} nell'interpolazione di Catmull-Rom. */
    private static float catmullRom(float t) {
        float d = Math.abs(t);
        if (d < 1.0f) {
            return 1.5f * d * d * d - 2.5f * d * d + 1.0f;
        }
        if (d < 2.0f) {
            return -0.5f * d * d * d + 2.5f * d * d - 4.0f * d + 2.0f;
        }
        return 0.0f;
    }

    private static int clamp(int value, int size) {
        return Math.max(0, Math.min(size - 1, value));
    }

    /** La quota della mappa in un punto dato in elmi. */
    private float heightAt(float x, float z) {
        float squareX = Math.max(0.0f, Math.min(this.mapX, x / 8.0f));
        float squareZ = Math.max(0.0f, Math.min(this.mapY, z / 8.0f));
        int x0 = Math.min((int) squareX, this.mapX - 1);
        int z0 = Math.min((int) squareZ, this.mapY - 1);
        float fractionX = squareX - x0;
        float fractionZ = squareZ - z0;
        int row = this.mapX + 1;
        float near = this.heights[z0 * row + x0] * (1.0f - fractionX) + this.heights[z0 * row + x0 + 1] * fractionX;
        float far = this.heights[(z0 + 1) * row + x0] * (1.0f - fractionX) + this.heights[(z0 + 1) * row + x0 + 1] * fractionX;
        return near * (1.0f - fractionZ) + far * fractionZ;
    }

    private float[] toElmos(Marker marker) {
        float metal = this.options.baseMetal();
        String id = marker.getId();
        if (id != null && id.contains("_val_")) {
            try {
                int start = id.indexOf("_val_") + 5;
                int end = id.indexOf('_', start);
                String valStr = (end != -1) ? id.substring(start, end) : id.substring(start);
                metal = Float.parseFloat(valStr);
            } catch (Exception ignored) {}
        }
        return new float[]{marker.getPosition().x() * this.elmosPerCell, marker.getPosition().z() * this.elmosPerCell, metal, 1.0f};
    }

    /**
     * Riunisce i gruppi di elementi collegati: {@code linked} dice se due elementi vanno insieme, e il
     * risultato assegna a ognuno il numero del suo gruppo (quello dell'elemento piu' basso che lo ha avviato).
     */
    private static int[] groupLinked(int count, java.util.function.IntBinaryOperator linked) {
        int[] group = new int[count];
        for (int i = 0; i < count; i++) {
            group[i] = i;
        }
        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                if (linked.applyAsInt(i, j) != 0) {
                    int from = group[j];
                    int to = group[i];
                    for (int k = 0; k < count; k++) {
                        if (group[k] == from) {
                            group[k] = to;
                        }
                    }
                }
            }
        }
        return group;
    }

    /**
     * Due punti metallo troppo vicini non si possono sfruttare entrambi: vengono fusi in uno solo, messo al
     * centro (allineato alla griglia di 16 elmi), con il metallo medio e il conto di quanti ne riunisce.
     */
    private void mergeCloseMetalSpots() {
        int count = this.mexPositions.size();
        int[] group = groupLinked(count, (i, j) -> {
            float[] other = this.mexPositions.get(j);
            return distance(this.mexPositions.get(i), other[0], other[1]) < MIN_METAL_SPOT_SPACING ? 1 : 0;
        });
        List<float[]> merged = new ArrayList<>();
        for (int g = 0; g < count; g++) {
            float sumX = 0.0f;
            float sumZ = 0.0f;
            float sumMetal = 0.0f;
            int members = 0;
            for (int i = 0; i < count; i++) {
                if (group[i] == g) {
                    float[] spot = this.mexPositions.get(i);
                    sumX += spot[0];
                    sumZ += spot[1];
                    sumMetal += spot[2];
                    members++;
                }
            }
            if (members > 0) {
                merged.add(new float[]{Math.round(sumX / members / 16.0f) * 16.0f, Math.round(sumZ / members / 16.0f) * 16.0f, sumMetal / members, members});
            }
        }
        this.mexPositions.clear();
        this.mexPositions.addAll(merged);
    }

    /**
     * Decide quanto metallo da' ogni punto, quando non l'ha scelto l'utente. I punti vicini alle basi hanno il
     * valore normale; gli altri valgono di piu' quanto piu' sono lontani ed esposti, e di meno dove ce ne sono
     * tanti insieme. I punti in posizioni equivalenti (specchiate) ricevono lo stesso valore.
     */
    private void assignMetalValues() {
        for (Marker marker : this.map.getMexes()) {
            if (marker.getId() != null && marker.getId().contains("_val_")) {
                return; // valori scelti a mano nell'editor: restano quelli
            }
        }
        float baseMetal = this.options.baseMetal();
        float minMetal = this.options.minMetal();
        float maxMetal = this.options.maxMetal();
        if (this.options.uniformMetal() || this.mexPositions.isEmpty()) {
            for (float[] spot : this.mexPositions) {
                spot[2] = Math.min(maxMetal, baseMetal * (1.0f + 0.5f * (spot[3] - 1.0f)));
            }
            return;
        }
        List<float[]> spawns = this.map.getSpawns().stream().map(this::toElmos).toList();
        float centreX = this.mapX * 8 / 2.0f;
        float centreZ = this.mapY * 8 / 2.0f;
        float baseRadius = 22.0f * this.elmosPerCell;
        float crowdRadius = 30.0f * this.elmosPerCell;
        int count = this.mexPositions.size();
        float[] fromSpawn = new float[count];
        float[] fromCentre = new float[count];
        float furthest = baseRadius + 1.0f;
        for (int i = 0; i < count; i++) {
            float[] spot = this.mexPositions.get(i);
            float nearest = Float.MAX_VALUE;
            for (float[] spawn : spawns) {
                nearest = Math.min(nearest, distance(spot, spawn[0], spawn[1]));
            }
            fromSpawn[i] = spawns.isEmpty() ? baseRadius * 2.0f : nearest;
            fromCentre[i] = distance(spot, centreX, centreZ);
            furthest = Math.max(furthest, fromSpawn[i]);
        }
        // una variazione che dipende dal nome della mappa: la stessa mappa da' sempre gli stessi valori
        int seed = this.map.getName().hashCode();
        float phaseA = (seed & 65535) / 65535.0f * 6.283f;
        float phaseB = ((seed >>> 16) & 65535) / 65535.0f * 6.283f;
        float[] values = new float[count];
        for (int i = 0; i < count; i++) {
            float[] spot = this.mexPositions.get(i);
            float mergeBonus = 1.0f + 0.35f * (spot[3] - 1.0f);
            if (fromSpawn[i] <= baseRadius) {
                values[i] = baseMetal * mergeBonus;
                continue;
            }
            float crowd = 0.0f;
            for (int j = 0; j < count; j++) {
                if (j != i) {
                    float[] other = this.mexPositions.get(j);
                    crowd += Math.max(0.0f, 1.0f - distance(spot, other[0], other[1]) / crowdRadius);
                }
            }
            float exposure = (fromSpawn[i] - baseRadius) / (furthest - baseRadius);
            float variation = 0.5f + 0.5f * (float) (StrictMath.sin(fromCentre[i] * 0.0131f + phaseA) * StrictMath.cos(fromSpawn[i] * 0.0173f + phaseB));
            float score = (float) StrictMath.pow(exposure, 1.2000000476837158d) * (0.55f + 0.6f * variation);
            float richness = score * (1.5f / (1.0f + 0.5f * crowd))
                + 0.35f * smoothStep(0.7f, 0.95f, exposure) * smoothStep(0.8f, 0.25f, crowd) * variation;
            values[i] = (minMetal + (maxMetal - minMetal) * Math.max(0.0f, Math.min(1.0f, richness))) * mergeBonus;
        }
        float tolerance = this.elmosPerCell * 1.5f;
        int[] group = new int[count];
        for (int i = 0; i < count; i++) {
            group[i] = i;
        }
        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                boolean equivalent = group[i] != group[j] && this.mexPositions.get(i)[3] == this.mexPositions.get(j)[3]
                    && Math.abs(fromSpawn[j] - fromSpawn[i]) < tolerance && Math.abs(fromCentre[j] - fromCentre[i]) < tolerance;
                if (equivalent) {
                    int from = group[j];
                    int to = group[i];
                    for (int k = 0; k < count; k++) {
                        if (group[k] == from) {
                            group[k] = to;
                        }
                    }
                }
            }
        }
        for (int g = 0; g < count; g++) {
            float sum = 0.0f;
            int members = 0;
            for (int i = 0; i < count; i++) {
                if (group[i] == g) {
                    sum += values[i];
                    members++;
                }
            }
            if (members == 0) {
                continue;
            }
            // arrotondato a un decimale, dentro i limiti scelti
            float shared = Math.round(Math.max(minMetal, Math.min(maxMetal, sum / members)) * 10.0f) / 10.0f;
            for (int i = 0; i < count; i++) {
                if (group[i] == g) {
                    this.mexPositions.get(i)[2] = shared;
                }
            }
        }
    }

    private static float distance(float[] position, float x, float z) {
        float dx = position[0] - x;
        float dz = position[1] - z;
        return (float) StrictMath.sqrt(dx * dx + dz * dz);
    }

    /** Passaggio morbido da 0 a 1 mentre {@code value} va da {@code from} a {@code to}. */
    private static float smoothStep(float from, float to, float value) {
        float t = Math.max(0.0f, Math.min(1.0f, (value - from) / (to - from)));
        return t * t * (3.0f - 2.0f * t);
    }

    /**
     * La mappa del metallo letta dal motore, a meta' risoluzione: ogni punto e' una macchia 4x4 senza gli
     * angoli, con un valore proporzionale al suo metallo.
     */
    private byte[] buildMetalMap() {
        int width = this.mapX / 2;
        int height = this.mapY / 2;
        byte[] metalMap = new byte[width * height];
        for (float[] spot : this.mexPositions) {
            int centreX = Math.round(spot[0] / 16.0f);
            int centreZ = Math.round(spot[1] / 16.0f);
            byte amount = (byte) Math.max(1, Math.round(255.0f * spot[2] / this.options.maxMetal()));
            for (int dz = -2; dz < 2; dz++) {
                for (int dx = -2; dx < 2; dx++) {
                    boolean corner = (dx == -2 || dx == 1) && (dz == -2 || dz == 1);
                    int x = centreX + dx;
                    int z = centreZ + dz;
                    if (!corner && x >= 0 && z >= 0 && x < width && z < height) {
                        metalMap[z * width + x] = amount;
                    }
                }
            }
        }
        return metalMap;
    }

    /** Riassunto dei punti metallo per il registro: quanti per ogni valore. */
    private String describeMetal() {
        TreeMap<Float, Integer> perValue = new TreeMap<>();
        for (float[] spot : this.mexPositions) {
            perValue.merge(spot[2], 1, Integer::sum);
        }
        if (perValue.isEmpty()) {
            return "none";
        }
        List<String> parts = new ArrayList<>();
        perValue.forEach((value, howMany) -> parts.add(String.format(Locale.ROOT, "%dx %.1f", howMany, value)));
        return String.join(", ", parts);
    }

    /** Il valore di "maxMetal" da scrivere nella mappa perche' un estrattore renda il metallo voluto. */
    private float getMaxMetal() {
        return this.options.maxMetal() / 3.0600002f;
    }

    /**
     * Gli oggetti da piazzare sulla mappa: geyser, alberi, e il materiale da recuperare (rocce e relitti), di
     * cui si tiene solo la parte che rientra nel totale di metallo previsto per il numero di giocatori.
     *
     * @param featureTypes elenco dei nomi usati, riempito man mano
     */
    private List<SpringMapFile.Feature> buildFeatures(List<String> featureTypes) {
        List<SpringMapFile.Feature> features = new ArrayList<>();
        String rockSet = getRockSet();
        this.plantSets = MapFlora.forBiome(String.valueOf(this.map.getBiome().name()), this.options.featuresRepository());
        List<ReclaimFeature> reclaim = new ArrayList<>();
        for (float[] geo : this.geoPositions) {
            addFeature(features, featureTypes, "geovent", geo[0], geo[1], 0.0f);
        }
        for (Prop prop : this.map.getProps()) {
            float x = prop.getPosition().x() * this.elmosPerCell;
            float z = prop.getPosition().z() * this.elmosPerCell;
            String path = prop.getPath().toLowerCase(Locale.ROOT);
            if (!path.contains("tree")) {
                // roccia: la variante dipende dalla posizione rispetto alle basi, cosi' i lati specchiati combaciano
                int rockHash = symmetricHash(x, z);
                int variant = prop.isBoulder() ? 12 + Math.floorMod(rockHash, 10) : 1 + Math.floorMod(rockHash, 8);
                String name = "rocks30_" + rockSet + "_" + (variant < 10 ? "0" : "") + variant;
                reclaim.add(new ReclaimFeature(name, x, z, prop.getRotation(), 9 + variant, ((rockHash >>> 8) & 65535) / 65536.0f));
                continue;
            }
            if (heightAt(x, z) < 4.0f) {
                continue; // niente alberi in acqua o sulla riva
            }
            int groveHash = symmetricHash(x, z);
            MapFlora.PlantSet plants = this.plantSets.isEmpty() ? null : this.plantSets.get(Math.floorMod(groveHash >>> 3, this.plantSets.size()));
            if (plants != null && ((groveHash >>> 9) & 255) / 256.0f >= plants.keep()) {
                continue; // questo insieme di piante e' piu' rado: il punto resta vuoto
            }
            int groveSize = plants != null ? plants.perGroup() : 4;
            int treeCount = path.contains("group") ? groveSize : 1;
            for (int tree = 0; tree < treeCount; tree++) {
                int treeHash = hash(x + tree * 31.7f, z - tree * 17.3f);
                // il primo albero sta nel punto indicato, gli altri sparsi intorno
                float treeX = tree == 0 ? x : x + ((treeHash & 255) / 255.0f - 0.5f) * 5.0f * this.elmosPerCell;
                float treeZ = tree == 0 ? z : z + (((treeHash >> 8) & 255) / 255.0f - 0.5f) * 5.0f * this.elmosPerCell;
                boolean goodSpot = tree <= 0 || (isInsideMap(treeX, treeZ, 32.0f) && !isNearResource(treeX, treeZ, 56.0f)
                    && heightAt(treeX, treeZ) >= 4.0f && slopeAt(treeX, treeZ) <= 0.35f);
                if (goodSpot) {
                    String name = plants != null
                        ? plants.names().get(Math.floorMod(groveHash * 31 + tree * 7919, plants.names().size()))
                        : "treetype" + ((treeHash >>> 16) % 16);
                    addFeature(features, featureTypes, name, treeX, treeZ, prop.getRotation());
                }
            }
        }
        for (Army army : this.map.getArmies()) {
            for (Group group : army.getGroups()) {
                for (Unit unit : group.getUnits()) {
                    float x = unit.getPosition().x() * this.elmosPerCell;
                    float z = unit.getPosition().z() * this.elmosPerCell;
                    String wreck = getWreck(unit.getType(), hash(x, z), heightAt(x, z) < -8.0f);
                    if (wreck != null && isInsideMap(x, z, 32.0f) && !isNearResource(x, z, 56.0f)) {
                        reclaim.add(new ReclaimFeature(wreck, x, z, unit.getRotation(), WRECK_METAL.getOrDefault(wreck, 150),
                            ((symmetricHash(x, z) >>> 8) & 65535) / 65536.0f));
                    }
                }
            }
        }
        long totalMetal = 0;
        for (ReclaimFeature feature : reclaim) {
            totalMetal += feature.metal();
        }
        float budget = RECLAIM_METAL_PER_PLAYER * Math.max(2, this.map.getSpawnCount()) * this.options.reclaim();
        float keep = totalMetal > 0 ? Math.min(1.0f, budget / totalMetal) : 0.0f;
        long keptMetal = 0;
        for (ReclaimFeature feature : reclaim) {
            if (feature.order() < keep) {
                addFeature(features, featureTypes, feature.name(), feature.x(), feature.z(), feature.rotation());
                keptMetal += feature.metal();
            }
        }
        // oggetti statici creati nello Studio e piazzati sulla mappa
        for (Object placed : this.map.getCustomEntities()) {
            if (placed instanceof com.faforever.neroxis.generator.bar.PlacedCustomEntity custom) {
                com.faforever.neroxis.generator.bar.ObjectEntity entity = custom.getEntity();
                if (entity.getCategory() == com.faforever.neroxis.generator.bar.ObjectEntity.Category.STATIC_PROP) {
                    addFeature(features, featureTypes, entity.getId(), custom.getPosition().x() * this.elmosPerCell,
                        custom.getPosition().y() * this.elmosPerCell, custom.getRotation());
                }
            }
        }
        System.out.printf(Locale.ROOT, "Reclaim: %d of %d metal in rocks and wrecks kept (budget %.0f)%n", keptMetal, totalMetal, budget);
        return features;
    }

    private void writeCustomEntities(ZipOutputStream zip) throws IOException {
        Set<String> writtenEntries = new HashSet<>();
        Map<String, com.faforever.neroxis.generator.bar.ObjectEntity> uniqueEntities = new LinkedHashMap<>();
        List<com.faforever.neroxis.generator.bar.PlacedCustomEntity> toSpawn = new ArrayList<>();

        for (Object obj : this.map.getCustomEntities()) {
            if (obj instanceof com.faforever.neroxis.generator.bar.PlacedCustomEntity ce) {
                com.faforever.neroxis.generator.bar.ObjectEntity ent = ce.getEntity();
                if (ent != null) {
                    uniqueEntities.putIfAbsent(ent.getId(), ent);
                    toSpawn.add(ce);
                }
            }
        }

        boolean defaultMatWritten = false;

        // 1. Scrittura asset unici (Modelli 3D nativi S3O per Spring/BAR, Texture, Script Lua, Definizioni)
        for (com.faforever.neroxis.generator.bar.ObjectEntity ent : uniqueEntities.values()) {
            String entId = ent.getId();

            // Modelli 3D personalizzati (unita ufficiali pure saltate; varianti _v2 esportate come nuovi tipi)
            if (ent.isClone()) {
                // copia personalizzata di un'unita' di BAR: basta la definizione, modello e script sono quelli del gioco
                String cloneEntry = "units/" + entId + ".lua";
                if (writtenEntries.add(cloneEntry)) {
                    // texture e modello colorati vanno scritti prima di aprire la voce della definizione
                    boolean ownModel = writeCloneModel(zip, ent, writtenEntries);
                    zip.putNextEntry(new ZipEntry(cloneEntry));
                    zip.write(buildCloneUnitDefLua(ent, ownModel).getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
                // script Lua scritto nello Studio: sostituisce quello dell'originale solo per questa copia
                if (ent.isCustomScriptEnabled() && hasLuaCode(ent.getLuaScript()) && writtenEntries.add("scripts/" + entId.toLowerCase(Locale.ROOT) + ".lua")) {
                    zip.putNextEntry(new ZipEntry("scripts/" + entId.toLowerCase(Locale.ROOT) + ".lua"));
                    zip.write(ent.getLuaScript().getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
                continue;
            }
            boolean isPureOfficial = ent.getCategory() == com.faforever.neroxis.generator.bar.ObjectEntity.Category.OFFICIAL_UNIT
                && !entId.endsWith("_v2");
            if (!isPureOfficial) {
                String s3oEntry = "objects3d/" + entId + ".s3o";
                if (writtenEntries.add(s3oEntry)) {
                    com.faforever.neroxis.generator.bar.ObjModel3D model = ent.getModel3D();
                    // Varianti di unita ufficiali: base = S3O originale del gioco (mai cubo fallback)
                    if (model == null && ent.hasOfficialUnit()) {
                        try {
                            model = com.faforever.neroxis.generator.bar.OfficialModels.load(ent.getOfficialUnitId());
                            if (model != null) ent.setModel3D(model);
                        } catch (Exception ex) {
                            System.err.println("Modello ufficiale non trovato per " + entId + ": " + ex.getMessage());
                        }
                    }
                    if (model == null && ent.getObjFilePath() != null && Files.isRegularFile(ent.getObjFilePath())) {
                        try {
                            model = com.faforever.neroxis.generator.bar.ObjParser.parse(ent.getObjFilePath());
                            ent.setModel3D(model);
                        } catch (Exception ex) {
                            System.err.println("Impossibile parsare OBJ per " + entId + ": " + ex.getMessage());
                        }
                    }
                    if (model == null) {
                        model = com.faforever.neroxis.generator.bar.ObjModel3D.createProceduralCube(3.0f);
                    }

                    String tex1Name = "default_mat.tga";
                    String tex2Name = entId + "_tex2.tga";
                    // Atlante tex1: materiali map_Kd + palette Kd in un'unica texture (BAR: una tex1 per modello)
                    com.faforever.neroxis.generator.bar.ObjModel3D exportModel = model;
                    try {
                        AtlasResult atlas = buildAtlasForEntity(ent, model);
                        if (atlas != null && atlas.image != null) {
                            tex1Name = entId + "_tex1.tga";
                            String texEntry = "unittextures/" + tex1Name;
                            if (writtenEntries.add(texEntry)) {
                                zip.putNextEntry(new ZipEntry(texEntry));
                                zip.write(com.faforever.neroxis.generator.bar.ObjModel3D.imageToTgaBytes(com.faforever.neroxis.generator.bar.ObjectEntity.tinted(atlas.image, ent.getTintRgb()), 0));
                                zip.closeEntry();
                            }
                            if (atlas.remapped != null) exportModel = atlas.remapped;
                        } else if (ent.getTexturePath() != null && Files.isRegularFile(ent.getTexturePath())) {
                            String ext = ent.getTexturePath().getFileName().toString();
                            tex1Name = entId + "_" + ext;
                        } else if (model.getBakedTexture() != null) {
                            tex1Name = entId + "_palette.tga";
                            String texEntry = "unittextures/" + tex1Name;
                            if (writtenEntries.add(texEntry)) {
                                zip.putNextEntry(new ZipEntry(texEntry));
                                zip.write(com.faforever.neroxis.generator.bar.ObjModel3D.imageToTgaBytes(com.faforever.neroxis.generator.bar.ObjectEntity.tinted(model.getBakedTexture(), ent.getTintRgb()), 0));
                                zip.closeEntry();
                            }
                        } else if (!defaultMatWritten) {
                            String defTexEntry = "unittextures/default_mat.tga";
                            if (writtenEntries.add(defTexEntry)) {
                                zip.putNextEntry(new ZipEntry(defTexEntry));
                                zip.write(createDefaultTgaBytes());
                                zip.closeEntry();
                                defaultMatWritten = true;
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Atlante texture fallito per " + entId + ": " + ex.getMessage());
                    }
                    // tex2 neutra (R30 G0 B40 A255): senza tex2 lo shader BAR rende il modello piatto
                    String tex2Entry = "unittextures/" + tex2Name;
                    if (writtenEntries.add(tex2Entry)) {
                        zip.putNextEntry(new ZipEntry(tex2Entry));
                        zip.write(createNeutralTex2Bytes());
                        zip.closeEntry();
                    }

                    zip.putNextEntry(new ZipEntry(s3oEntry));
                    zip.write(exportModel.toS3OBytes(ent.getName(), tex1Name, tex2Name, ent.getScale()));
                    zip.closeEntry();
                }

                // Texture personalizzata
                if (ent.getTexturePath() != null && Files.isRegularFile(ent.getTexturePath())) {
                    String ext = ent.getTexturePath().getFileName().toString();
                    String texEntry = "unittextures/" + entId + "_" + ext;
                    if (writtenEntries.add(texEntry)) {
                        zip.putNextEntry(new ZipEntry(texEntry));
                        Files.copy(ent.getTexturePath(), zip);
                        zip.closeEntry();
                    }
                }

                // Script di animazione / logica Lua
                if (ent.getLuaScript() != null && !ent.getLuaScript().isBlank()) {
                    String scriptEntry = "scripts/" + entId + ".lua";
                    if (writtenEntries.add(scriptEntry)) {
                        zip.putNextEntry(new ZipEntry(scriptEntry));
                        zip.write(ent.getLuaScript().getBytes(StandardCharsets.UTF_8));
                        zip.closeEntry();
                    }
                }

                // Definizioni unita' e feature
                String unitDefEntry = "units/" + entId + ".lua";
                if (writtenEntries.add(unitDefEntry)) {
                    zip.putNextEntry(new ZipEntry(unitDefEntry));
                    zip.write(buildCustomUnitDefLua(ent).getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
                String featDefEntry = "features/" + entId + ".lua";
                if (writtenEntries.add(featDefEntry)) {
                    zip.putNextEntry(new ZipEntry(featDefEntry));
                    zip.write(buildCustomFeatureDefLua(ent).getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
        }

        // Nomi e descrizioni: BAR mostra il nome di un'unita' leggendolo dai file di lingua, non dalla sua
        // definizione. Senza questo file le unita' create dallo Studio comparirebbero senza il loro nome.
        StringBuilder unitNames = new StringBuilder();
        StringBuilder unitDescriptions = new StringBuilder();
        StringBuilder featureNames = new StringBuilder();
        for (com.faforever.neroxis.generator.bar.ObjectEntity ent : uniqueEntities.values()) {
            boolean pureOfficial = ent.getCategory() == com.faforever.neroxis.generator.bar.ObjectEntity.Category.OFFICIAL_UNIT
                && !ent.getId().endsWith("_v2");
            if (pureOfficial && !ent.isClone()) {
                continue;
            }
            String key = jsonString(ent.getId().toLowerCase(Locale.ROOT));
            String shown = ent.getName() != null && !ent.getName().isBlank() ? ent.getName() : ent.getId();
            unitNames.append(unitNames.length() > 0 ? "," : "").append(key).append(":").append(jsonString(shown));
            unitDescriptions.append(unitDescriptions.length() > 0 ? "," : "").append(key).append(":").append(jsonString(ent.getTooltip() != null ? ent.getTooltip() : ""));
            featureNames.append(featureNames.length() > 0 ? "," : "").append(key).append(":").append(jsonString(shown));
        }
        if (unitNames.length() > 0 && writtenEntries.add("language/en/mapstudio_units.json")) {
            zip.putNextEntry(new ZipEntry("language/en/mapstudio_units.json"));
            zip.write(("{\"units\":{\"names\":{" + unitNames + "},\"descriptions\":{" + unitDescriptions + "}},\"features\":{\"names\":{" + featureNames + "}}}")
                .getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }

        // 2. Generazione del Map Gadget per spawnare le entita' in-game
        if (!toSpawn.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("if not gadgetHandler:IsSyncedCode() then return end\n\n");
            sb.append("function gadget:GetInfo()\n");
            sb.append("    return {\n");
            sb.append("        name = \"Map Custom Entities Spawner\",\n");
            sb.append("        desc = \"Spawns units, animated scenery and custom objects placed in BAR Map Studio\",\n");
            sb.append("        author = \"BAR Map Studio\",\n");
            sb.append("        date = \"2026\",\n");
            sb.append("        license = \"GNU GPL, v2 or later\",\n");
            sb.append("        layer = 1,\n");
            sb.append("        enabled = true\n");
            sb.append("    }\n");
            sb.append("end\n\n");

            sb.append("local placedEntities = {\n");
            for (com.faforever.neroxis.generator.bar.PlacedCustomEntity ce : toSpawn) {
                com.faforever.neroxis.generator.bar.ObjectEntity ent = ce.getEntity();
                float wx = ce.getPosition().x() * this.elmosPerCell;
                float wz = ce.getPosition().y() * this.elmosPerCell;
                float rot = ce.getRotation();
                String faction = ent.getFaction() != null ? ent.getFaction() : "Neutral";
                String cat = ent.getCategory().name();

                sb.append(String.format(Locale.ROOT,
                    "    { id = \"%s\", x = %.1f, z = %.1f, rot = %.4f, faction = \"%s\", cat = \"%s\", owner = \"%s\"%s },\n",
                    ent.getId(), wx, wz, rot, faction, cat, ent.getOwner().name(), officialOverrides(ent)
                ));
            }
            sb.append("}\n\n");

            sb.append("-- unita' indistruttibili (script con --@invulnerable): nessuna arma fa loro danno\n");
            sb.append("local invulnerableNames = {");
            for (com.faforever.neroxis.generator.bar.ObjectEntity ent : uniqueEntities.values()) {
                if (isInvulnerable(ent)) {
                    sb.append(" [\"").append(ent.getId().toLowerCase(Locale.ROOT)).append("\"] = true,");
                }
            }
            sb.append(" }\n");
            sb.append("local invulnerableDefs = {}\n");
            sb.append("for unitDefID, unitDef in pairs(UnitDefs) do\n");
            sb.append("    if invulnerableNames[unitDef.name] then invulnerableDefs[unitDefID] = true end\n");
            sb.append("end\n\n");
            sb.append("function gadget:UnitPreDamaged(unitID, unitDefID)\n");
            sb.append("    if invulnerableDefs[unitDefID] then\n");
            sb.append("        return 0, 0\n");
            sb.append("    end\n");
            sb.append("end\n\n");
            sb.append("function gadget:AllowUnitTransfer(unitID, unitDefID)\n");
            sb.append("    return not invulnerableDefs[unitDefID]\n");
            sb.append("end\n\n");
            sb.append("function gadget:GameStart()\n");
            sb.append("    local gaiaTeamID = Spring.GetGaiaTeamID()\n");
            sb.append("    local teamList = Spring.GetTeamList()\n");
            sb.append("    local armadaTeam = nil\n");
            sb.append("    local legionTeam = nil\n");
            sb.append("    local raptorTeam = nil\n");
            sb.append("    local scavTeam = nil\n");
            sb.append("    local cortexTeam = nil\n");
            sb.append("    local firstPlayerTeam = 0\n");
            sb.append("    for _, tId in ipairs(teamList) do\n");
            sb.append("        if tId ~= gaiaTeamID then\n");
            sb.append("            firstPlayerTeam = tId\n");
            sb.append("            local _, _, _, _, side = Spring.GetTeamInfo(tId)\n");
            sb.append("            if side and string.lower(side) == \"armada\" and not armadaTeam then armadaTeam = tId end\n");
            sb.append("            if side and string.lower(side) == \"cortex\" and not cortexTeam then cortexTeam = tId end\n");
            sb.append("            if side and string.lower(side) == \"legion\" and not legionTeam then legionTeam = tId end\n");
            sb.append("            local luaAI = string.lower(Spring.GetTeamLuaAI(tId) or \"\")\n");
            sb.append("            if string.find(luaAI, \"raptor\", 1, true) and not raptorTeam then raptorTeam = tId end\n");
            sb.append("            if string.find(luaAI, \"scav\", 1, true) and not scavTeam then scavTeam = tId end\n");
            sb.append("        end\n");
            sb.append("    end\n");
            sb.append("    -- prima squadra di ogni schieramento (alleanza), in ordine: serve per 'Squadra 1' e 'Squadra 2'\n");
            sb.append("    local sideTeams = {}\n");
            sb.append("    local seenAlly = {}\n");
            sb.append("    for _, tId in ipairs(teamList) do\n");
            sb.append("        if tId ~= gaiaTeamID then\n");
            sb.append("            local allyTeam = select(6, Spring.GetTeamInfo(tId, false))\n");
            sb.append("            if allyTeam and not seenAlly[allyTeam] then\n");
            sb.append("                seenAlly[allyTeam] = true\n");
            sb.append("                sideTeams[table.getn(sideTeams) + 1] = tId\n");
            sb.append("            end\n");
            sb.append("        end\n");
            sb.append("    end\n");
            sb.append("    if not armadaTeam then armadaTeam = firstPlayerTeam end\n");
            sb.append("    if not cortexTeam then cortexTeam = firstPlayerTeam end\n\n");

            sb.append("    for _, e in ipairs(placedEntities) do\n");
            sb.append("        local teamID = gaiaTeamID\n");
            sb.append("        local f = string.lower(e.faction or \"\")\n");
            sb.append("        if f == \"armada\" then\n");
            sb.append("            teamID = armadaTeam\n");
            sb.append("        elseif f == \"cortex\" then\n");
            sb.append("            teamID = cortexTeam\n");
            sb.append("        elseif f == \"player0\" then\n");
            sb.append("            teamID = 0\n");
            sb.append("        elseif f == \"player1\" then\n");
            sb.append("            teamID = 1\n");
            sb.append("        else\n");
            sb.append("            teamID = gaiaTeamID\n");
            sb.append("        end\n");
            sb.append("        -- schieramento scelto nello Studio: prevale su quello dedotto dalla fazione\n");
            sb.append("        if e.owner == \"NEUTRAL\" or e.owner == \"HOSTILE\" then\n");
            sb.append("            teamID = gaiaTeamID\n");
            sb.append("        elseif e.owner == \"ARMADA\" then\n");
            sb.append("            teamID = armadaTeam\n");
            sb.append("        elseif e.owner == \"CORTEX\" then\n");
            sb.append("            teamID = cortexTeam\n");
            sb.append("        elseif e.owner == \"LEGION\" then\n");
            sb.append("            teamID = legionTeam or firstPlayerTeam\n");
            sb.append("        elseif e.owner == \"RAPTORS\" then\n");
            sb.append("            -- senza una squadra Raptor in partita l'unita' resta nemica di tutti\n");
            sb.append("            teamID = raptorTeam or gaiaTeamID\n");
            sb.append("            if not raptorTeam then e.owner = \"HOSTILE\" end\n");
            sb.append("        elseif e.owner == \"SCAVENGERS\" then\n");
            sb.append("            teamID = scavTeam or gaiaTeamID\n");
            sb.append("            if not scavTeam then e.owner = \"HOSTILE\" end\n");
            sb.append("        elseif e.owner == \"TEAM_A\" and sideTeams[1] then\n");
            sb.append("            teamID = sideTeams[1]\n");
            sb.append("        elseif e.owner == \"TEAM_B\" and sideTeams[2] then\n");
            sb.append("            teamID = sideTeams[2]\n");
            sb.append("        end\n\n");

            sb.append("        local y = Spring.GetGroundHeight(e.x, e.z)\n");
            sb.append("        local heading = 0\n");
            sb.append("        local facing = \"s\"\n");
            sb.append("        if e.rot then\n");
            sb.append("            local deg = (math.deg(e.rot) % 360 + 360) % 360\n");
            sb.append("            heading = math.floor((deg / 360.0) * 65536) % 65536\n");
            sb.append("            if deg >= 45 and deg < 135 then facing = \"e\"\n");
            sb.append("            elseif deg >= 135 and deg < 225 then facing = \"n\"\n");
            sb.append("            elseif deg >= 225 and deg < 315 then facing = \"w\"\n");
            sb.append("            else facing = \"s\" end\n");
            sb.append("        end\n\n");

            sb.append("        local unitID = nil\n");
            sb.append("        local nameLower = string.lower(e.id)\n");
            sb.append("        if UnitDefNames and (UnitDefNames[nameLower] or UnitDefNames[e.id]) then\n");
            sb.append("            local actualDef = UnitDefNames[nameLower] and nameLower or e.id\n");
            sb.append("            local ok, uId = pcall(Spring.CreateUnit, actualDef, e.x, y, e.z, facing, teamID)\n");
            sb.append("            if ok and uId then\n");
            sb.append("                unitID = uId\n");
            sb.append("            else\n");
            sb.append("                local ok2, uId2 = pcall(Spring.CreateUnit, actualDef, e.x, y, e.z, facing, gaiaTeamID)\n");
            sb.append("                if ok2 and uId2 then\n");
            sb.append("                    unitID = uId2\n");
            sb.append("                    teamID = gaiaTeamID\n");
            sb.append("                end\n");
            sb.append("            end\n");
            sb.append("        end\n\n");

            sb.append("        if not unitID then\n");
            sb.append("            -- si prova come oggetto solo se esiste davvero: chiedere un FeatureDef che non c'e' stampa un errore a schermo\n");
            sb.append("            local featName = (FeatureDefNames[e.id] and e.id) or (FeatureDefNames[nameLower] and nameLower)\n");
            sb.append("            if featName then\n");
            sb.append("                pcall(Spring.CreateFeature, featName, e.x, y, e.z, heading, teamID)\n");
            sb.append("            elseif e.cat == \"OFFICIAL_UNIT\" then\n");
            sb.append("                Spring.Echo(\"[BAR Map Studio] Unit '\" .. e.id .. \"' is not loaded in this match. Legion, Scavenger and Raptor units need their game option enabled (for Legion: the Legion faction option in the lobby).\")\n");
            sb.append("            else\n");
            sb.append("                Spring.Echo(\"[BAR Map Studio] Object '\" .. e.id .. \"' was not found in the map.\")\n");
            sb.append("            end\n");
            sb.append("        else\n");
            sb.append("            -- unita' di BAR con valori cambiati nello Studio: si applicano a questa singola unita'\n");
            sb.append("            if e.hp then\n");
            sb.append("                pcall(Spring.SetUnitMaxHealth, unitID, e.hp)\n");
            sb.append("                pcall(Spring.SetUnitHealth, unitID, e.hp)\n");
            sb.append("            end\n");
            sb.append("            if e.metal or e.energy then\n");
            sb.append("                pcall(Spring.SetUnitCosts, unitID, { metalCost = e.metal, energyCost = e.energy })\n");
            sb.append("            end\n");
            sb.append("            if e.owner == \"HOSTILE\" then\n");
            sb.append("                -- nemica di tutti: resta della squadra Gaia (nemica di ogni alleanza) ma non e' neutrale,\n");
            sb.append("                -- quindi viene presa di mira; fuoco a volonta' e libera di inseguire\n");
            sb.append("                pcall(Spring.SetUnitNeutral, unitID, false)\n");
            sb.append("                pcall(Spring.GiveOrderToUnit, unitID, CMD.FIRE_STATE, { 2 }, 0)\n");
            sb.append("                pcall(Spring.GiveOrderToUnit, unitID, CMD.MOVE_STATE, { 2 }, 0)\n");
            sb.append("            elseif e.owner == \"NEUTRAL\" then\n");
            sb.append("                pcall(Spring.SetUnitNeutral, unitID, true)\n");
            sb.append("                pcall(Spring.GiveOrderToUnit, unitID, CMD.FIRE_STATE, { 0 }, 0)\n");
            sb.append("            elseif teamID == gaiaTeamID then\n");
            sb.append("                pcall(Spring.SetUnitNeutral, unitID, true)\n");
            sb.append("            end\n");
            sb.append("        end\n");
            sb.append("    end\n");
            sb.append("end\n");

            zip.putNextEntry(new ZipEntry("luarules/gadgets/map_custom_spawner.lua"));
            zip.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
    }

    private byte[] createDefaultTgaBytes() {
        int w = 16, h = 16;
        byte[] tga = new byte[18 + w * h * 4];
        tga[2] = 2; // uncompressed true-color
        tga[12] = (byte) (w & 0xFF);
        tga[13] = (byte) ((w >> 8) & 0xFF);
        tga[14] = (byte) (h & 0xFF);
        tga[15] = (byte) ((h >> 8) & 0xFF);
        tga[16] = 32; // 32 bits per pixel (BGRA)
        tga[17] = 8;  // 8 bits alpha
        int offset = 18;
        for (int i = 0; i < w * h; i++) {
            tga[offset++] = (byte) 160; // B
            tga[offset++] = (byte) 160; // G
            tga[offset++] = (byte) 160; // R
            tga[offset++] = (byte) 0;   // A = maschera colore squadra (0 = nessuno)
        }
        return tga;
    }

    private byte[] createNeutralTex2Bytes() {
        int w = 64, h = 64;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        int argb = (255 << 24) | (30 << 16) | (0 << 8) | 40;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) img.setRGB(x, y, argb);
        return com.faforever.neroxis.generator.bar.ObjModel3D.imageToTgaBytes(img);
    }

    private static final class AtlasResult {
        final java.awt.image.BufferedImage image;
        final com.faforever.neroxis.generator.bar.ObjModel3D remapped;
        AtlasResult(java.awt.image.BufferedImage image, com.faforever.neroxis.generator.bar.ObjModel3D remapped) {
            this.image = image; this.remapped = remapped;
        }
    }

    /** Costruisce atlante tex1 unico (max 2048) da materiali map_Kd + palette Kd; rimappa UV nella cella. */
    private AtlasResult buildAtlasForEntity(com.faforever.neroxis.generator.bar.ObjectEntity ent,
                                           com.faforever.neroxis.generator.bar.ObjModel3D model) throws Exception {
        java.util.LinkedHashMap<String, java.awt.image.BufferedImage> imgs = new java.util.LinkedHashMap<>();
        if (model != null && model.getMaterials() != null) {
            for (java.util.Map.Entry<String, com.faforever.neroxis.generator.bar.ObjModel3D.Material> e : model.getMaterials().entrySet()) {
                if (e.getValue() != null && e.getValue().diffuse != null) imgs.putIfAbsent(e.getKey(), e.getValue().diffuse);
            }
        }
        java.awt.image.BufferedImage fileImg = null;
        if (ent.getTexturePath() != null && Files.isRegularFile(ent.getTexturePath())) {
            try {
                String ln = ent.getTexturePath().getFileName().toString().toLowerCase();
                if (ln.endsWith(".tga")) fileImg = com.faforever.neroxis.generator.bar.ModelTextureCache.readTga(Files.readAllBytes(ent.getTexturePath()));
                else fileImg = javax.imageio.ImageIO.read(ent.getTexturePath().toFile());
            } catch (Exception ignored) {}
            if (fileImg != null && imgs.isEmpty()) {
                // singolo file: usa direttamente, nessuna atlas
                return new AtlasResult(fileImg, null);
            }
            if (fileImg != null) imgs.putIfAbsent("__file__", fileImg);
        }
        if (imgs.isEmpty()) return null;
        if (imgs.size() == 1 && (model.getBakedTexture() == null)) {
            return new AtlasResult(imgs.values().iterator().next(), null);
        }
        // pack a griglia: celle quadrate 512 (downscale), atlas max 2048 (4x4=16 materiali)
        java.util.List<String> keys = new java.util.ArrayList<>(imgs.keySet());
        boolean hasPalette = model.getBakedTexture() != null;
        int cells = keys.size() + (hasPalette ? 1 : 0);
        int cols = (int) Math.ceil(Math.sqrt(cells));
        cols = Math.max(1, Math.min(4, cols));
        int rows = (int) Math.ceil(cells / (double) cols);
        int cell = 512;
        int aw = cols * cell, ah = rows * cell;
        java.awt.image.BufferedImage atlas = new java.awt.image.BufferedImage(aw, ah, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = atlas.createGraphics();
        g.setColor(new java.awt.Color(128, 128, 128, 255));
        g.fillRect(0, 0, aw, ah);
        java.util.Map<String, java.awt.Rectangle> rects = new java.util.HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            int cx = (i % cols) * cell, cy = (i / cols) * cell;
            java.awt.image.BufferedImage src = imgs.get(keys.get(i));
            g.drawImage(src, cx, cy, cell, cell, null);
            rects.put(keys.get(i), new java.awt.Rectangle(cx, cy, cell, cell));
        }
        if (hasPalette) {
            int i = keys.size();
            int cx = (i % cols) * cell, cy = (i / cols) * cell;
            g.drawImage(model.getBakedTexture(), cx, cy, cell, cell, null);
            rects.put("__palette__", new java.awt.Rectangle(cx, cy, cell, cell));
        }
        g.dispose();
        // remap UV: parto SEMPRE dalle UV originali (vt) del file, non da getFaceUVs()
        // (che per i materiali con solo Kd punta alla cella palette).
        String[] faceMats = model.getFaceMaterials();
        float[][] faceUVs = model.getBaseUVs();
        if (faceUVs == null || faceUVs.length != model.getFaces().length) faceUVs = model.getFaceUVs();
        int[][] faces = model.getFaces();
        float[][] newUVs = new float[faceUVs.length][];
        for (int f = 0; f < faces.length; f++) {
            float[] src = (f < faceUVs.length) ? faceUVs[f] : null;
            String m = (faceMats != null && f < faceMats.length) ? faceMats[f] : null;
            java.awt.Rectangle r = (m != null) ? rects.get(m) : null;
            if (r == null) {
                // materiale con solo Kd o senza diffuse: usa palette se presente, altrimenti prima cella
                r = rects.get("__palette__");
                if (r == null && !rects.isEmpty()) r = rects.values().iterator().next();
            }
            if (r == null) { newUVs[f] = src; continue; }
            if (src == null) {
                // nessuna vt: centro cella (colore piatto del materiale)
                int vCount = faces[f].length;
                float[] dst = new float[vCount * 2];
                float cu = (r.x + r.width * 0.5f) / aw;
                float cv = (r.y + r.height * 0.5f) / ah;
                for (int vi = 0; vi < vCount; vi++) { dst[vi * 2] = cu; dst[vi * 2 + 1] = cv; }
                newUVs[f] = dst;
                continue;
            }
            float[] dst = new float[src.length];
            // UV traslate di tile interi (texture ripetute): riporta la faccia in 0..1 prima del clamp
            float minU = Float.MAX_VALUE, minV = Float.MAX_VALUE;
            for (int k = 0; k < src.length / 2; k++) {
                minU = Math.min(minU, src[k * 2]);
                minV = Math.min(minV, src[k * 2 + 1]);
            }
            float offU = (float) Math.floor(minU), offV = (float) Math.floor(minV);
            for (int k = 0; k < src.length / 2; k++) {
                float u = src[k * 2] - offU, v = src[k * 2 + 1] - offV;
                dst[k * 2] = (r.x + Math.min(0.9999f, Math.max(0, u)) * r.width) / aw;
                dst[k * 2 + 1] = (r.y + Math.min(0.9999f, Math.max(0, v)) * r.height) / ah;
            }
            newUVs[f] = dst;
        }
        // IMPORTANTE: materialColors vuoto -> il costruttore non ri-applica le celle palette
        // e le UV dell'atlante (newUVs) restano intatte.
        com.faforever.neroxis.generator.bar.ObjModel3D remapped = new com.faforever.neroxis.generator.bar.ObjModel3D(
            model.getVertices(), faces, model.getFaceNormals(), model.getFaceColors(), newUVs,
            new java.util.LinkedHashMap<>(), faceMats);
        remapped.setFacePieces(model.getFacePieces());
        return new AtlasResult(atlas, remapped);
    }

    /**
     * Definizione di una copia personalizzata: carica il file dell'unita' originale dal gioco, ne fa una copia
     * e cambia solo nome e valori scelti nello Studio. Armi, movimento, modello e script restano gli stessi.
     */
    /**
     * Modello proprio per una copia di unita' BAR, scritto solo quando serve: se la copia ha una scala diversa
     * da 1 (il modello del gioco viene ingrandito o rimpicciolito) o un colore suo (vedi writeCloneTexture).
     * Ritorna false se la copia puo' usare il modello originale cosi' com'e'.
     */
    private static boolean writeCloneModel(ZipOutputStream zip, com.faforever.neroxis.generator.bar.ObjectEntity ent, Set<String> written) {
        boolean recolour = ent.getTintRgb() != null && ent.isColourFree();
        boolean rescale = Math.abs(ent.getScale() - 1.0f) > 0.001f;
        if (!recolour && !rescale) {
            return false;
        }
        try {
            byte[] s3o = com.faforever.neroxis.generator.bar.OfficialModels.modelBytes(ent.getCloneOf());
            if (s3o == null || s3o.length < 52) {
                return false;
            }
            byte[] model = s3o.clone();
            if (rescale) {
                scaleS3o(model, ent.getScale());
            }
            if (recolour) {
                String texName = writeCloneTexture(zip, ent, s3o, written);
                if (texName != null) {
                    // la nuova stringa va in fondo al file e l'intestazione punta a quella
                    byte[] nameBytes = (texName + "\0").getBytes(StandardCharsets.US_ASCII);
                    byte[] patched = java.util.Arrays.copyOf(model, model.length + nameBytes.length);
                    System.arraycopy(nameBytes, 0, patched, model.length, nameBytes.length);
                    java.nio.ByteBuffer.wrap(patched).order(java.nio.ByteOrder.LITTLE_ENDIAN).putInt(44, model.length);
                    model = patched;
                } else if (!rescale) {
                    return false;
                }
            }
            String s3oEntry = "objects3d/" + ent.getId().toLowerCase(Locale.ROOT) + ".s3o";
            if (written.add(s3oEntry)) {
                zip.putNextEntry(new ZipEntry(s3oEntry));
                zip.write(model);
                zip.closeEntry();
            }
            return true;
        } catch (IOException | RuntimeException ex) {
            System.err.println("Modello della copia non scritto per " + ent.getId() + ": " + ex.getMessage());
            return false;
        }
    }

    /** Scala sul posto un modello S3O: vertici, posizione di ogni pezzo e misure dell'intestazione. */
    private static void scaleS3o(byte[] data, float scale) {
        java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(data).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        for (int off = 16; off <= 32; off += 4) {
            b.putFloat(off, b.getFloat(off) * scale);
        }
        scaleS3oPiece(data, b, b.getInt(36), scale, 0, new HashSet<>());
    }

    private static void scaleS3oPiece(byte[] data, java.nio.ByteBuffer b, int off, float scale, int depth, Set<Integer> doneVertexBlocks) {
        if (off < 0 || off + 52 > data.length || depth > 64) {
            return;
        }
        int numChildren = b.getInt(off + 4);
        int childrenOff = b.getInt(off + 8);
        int numVerts = b.getInt(off + 12);
        int vertsOff = b.getInt(off + 16);
        for (int k = 40; k <= 48; k += 4) {
            b.putFloat(off + k, b.getFloat(off + k) * scale);
        }
        // un blocco di vertici condiviso da piu' pezzi va scalato una volta sola
        if (numVerts > 0 && doneVertexBlocks.add(vertsOff)) {
            for (int i = 0; i < numVerts; i++) {
                int p = vertsOff + (i * 32);
                if (p + 12 > data.length) {
                    break;
                }
                b.putFloat(p, b.getFloat(p) * scale);
                b.putFloat(p + 4, b.getFloat(p + 4) * scale);
                b.putFloat(p + 8, b.getFloat(p + 8) * scale);
            }
        }
        for (int i = 0; i < numChildren; i++) {
            int p = childrenOff + (i * 4);
            if (p + 4 > data.length) {
                break;
            }
            scaleS3oPiece(data, b, b.getInt(p), scale, depth + 1, doneVertexBlocks);
        }
    }

    /**
     * Colore proprio per una copia di unita' BAR: il gioco colora le unita' con il colore della squadra usando
     * l'alpha della loro texture, quindi qui si scrive una copia della texture con il colore scelto gia' dipinto
     * (alpha a zero). Ritorna il nome della texture scritta, o null se non riesce.
     */
    private static String writeCloneTexture(ZipOutputStream zip, com.faforever.neroxis.generator.bar.ObjectEntity ent, byte[] s3o, Set<String> written) throws IOException {
        java.nio.ByteBuffer header = java.nio.ByteBuffer.wrap(s3o).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        int nameOff = header.getInt(44);
        int end = nameOff;
        while (end < s3o.length && s3o[end] != 0) {
            end++;
        }
        String tex1 = new String(s3o, nameOff, end - nameOff, StandardCharsets.US_ASCII);
        int tint = ent.getTintRgb();
        String base = tex1.toLowerCase(Locale.ROOT).replaceAll("\\.[a-z0-9]+$", "").replaceAll("[^a-z0-9_]", "_");
        String texName = "studio_" + base + String.format("_%06x.tga", tint);
        if (!written.add("unittextures/" + texName)) {
            return texName;
        }
        // meta' lato dell'originale: abbastanza dettaglio in gioco senza appesantire troppo la mappa
        java.awt.image.BufferedImage src = com.faforever.neroxis.generator.bar.OfficialModels.unitTexture(tex1, 1);
        if (src == null) {
            written.remove("unittextures/" + texName);
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int[] px = src.getRGB(0, 0, w, h, null, 0, w);
        // le DDS del gioco sono salvate capovolte rispetto a come il motore legge una TGA: si ribalta in verticale
        boolean dds = tex1.toLowerCase(Locale.ROOT).endsWith(".dds");
        int[] out = new int[px.length];
        int tr = (tint >> 16) & 255, tg = (tint >> 8) & 255, tb = tint & 255;
        for (int y = 0; y < h; y++) {
            int sy = dds ? h - 1 - y : y;
            for (int x = 0; x < w; x++) {
                int c = px[(sy * w) + x];
                int a = c >>> 24;
                int r = (c >> 16) & 255, g = (c >> 8) & 255, bl = c & 255;
                r += ((tr - r) * a) / 255;
                g += ((tg - g) * a) / 255;
                bl += ((tb - bl) * a) / 255;
                out[(y * w) + x] = 0xFF000000 | (r << 16) | (g << 8) | bl;
            }
        }
        java.awt.image.BufferedImage painted = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        painted.setRGB(0, 0, w, h, out, 0, w);
        zip.putNextEntry(new ZipEntry("unittextures/" + texName));
        zip.write(com.faforever.neroxis.generator.bar.ObjModel3D.imageToTgaBytes(painted, 0));
        zip.closeEntry();
        return texName;
    }

    private static String buildCloneUnitDefLua(com.faforever.neroxis.generator.bar.ObjectEntity ent, boolean ownModel) {
        String id = ent.getId().toLowerCase(Locale.ROOT);
        String source = ent.getCloneOf().toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        sb.append("-- Copia personalizzata dell'unita' BAR \"").append(source).append("\" creata con BAR Map Studio\n");
        sb.append("local source = \"").append(source).append("\"\n");
        sb.append("local unitName = \"").append(id).append("\"\n\n");
        sb.append("local function copy(value)\n");
        sb.append("    if type(value) ~= \"table\" then return value end\n");
        sb.append("    local result = {}\n");
        sb.append("    for k, v in pairs(value) do result[k] = copy(v) end\n");
        sb.append("    return result\n");
        sb.append("end\n\n");
        sb.append("local path = nil\n");
        sb.append("for _, file in ipairs(VFS.DirList(\"units/\", \"*.lua\", nil, true)) do\n");
        sb.append("    if string.match(string.lower(file), \"([^/]+)%.lua$\") == source then\n");
        sb.append("        path = file\n");
        sb.append("        break\n");
        sb.append("    end\n");
        sb.append("end\n");
        sb.append("if not path then return {} end\n");
        sb.append("local ok, defs = pcall(VFS.Include, path)\n");
        sb.append("if not ok or type(defs) ~= \"table\" then return {} end\n");
        sb.append("local original = nil\n");
        sb.append("for k, v in pairs(defs) do\n");
        sb.append("    if string.lower(k) == source then original = v end\n");
        sb.append("end\n");
        sb.append("if type(original) ~= \"table\" then return {} end\n\n");
        sb.append("local unitDef = copy(original)\n");
        sb.append("-- le chiavi dei file di BAR possono avere maiuscole diverse: si toglie la vecchia e si scrive la nuova\n");
        sb.append("local function set(key, value)\n");
        sb.append("    for k in pairs(unitDef) do\n");
        sb.append("        if type(k) == \"string\" and string.lower(k) == key then unitDef[k] = nil end\n");
        sb.append("    end\n");
        sb.append("    unitDef[key] = value\n");
        sb.append("end\n\n");
        sb.append("set(\"name\", ").append(jsonString(ent.getName() != null ? ent.getName() : id)).append(")\n");
        if (ent.getTooltip() != null && !ent.getTooltip().isBlank()) {
            sb.append("set(\"description\", ").append(jsonString(ent.getTooltip())).append(")\n");
        }
        if (ent.getHealth() != ent.getOfficialHealth()) {
            sb.append("set(\"maxdamage\", nil)\n");
            sb.append("set(\"health\", ").append(Math.max(1, ent.getHealth())).append(")\n");
        }
        if (ent.getMetal() != ent.getOfficialMetal()) {
            sb.append("set(\"buildcostmetal\", nil)\n");
            sb.append("set(\"metalcost\", ").append(Math.max(0, ent.getMetal())).append(")\n");
        }
        if (ent.getEnergy() != ent.getOfficialEnergy()) {
            sb.append("set(\"buildcostenergy\", nil)\n");
            sb.append("set(\"energycost\", ").append(Math.max(0, ent.getEnergy())).append(")\n");
        }
        if (ent.getSightRange() > 0) {
            sb.append("set(\"sightdistance\", ").append(ent.getSightRange()).append(")\n");
        }
        if (ownModel) {
            sb.append("set(\"objectname\", \"").append(id).append(".s3o\")\n");
        }
        if (Math.abs(ent.getScale() - 1.0f) > 0.001f) {
            // il volume di collisione segue la nuova grandezza del modello
            sb.append("local modelScale = ").append(String.format(Locale.ROOT, "%.4f", ent.getScale())).append("\n");
            sb.append("for k, v in pairs(unitDef) do\n");
            sb.append("    local key = type(k) == \"string\" and string.lower(k) or \"\"\n");
            sb.append("    if (key == \"collisionvolumescales\" or key == \"collisionvolumeoffsets\") and type(v) == \"string\" then\n");
            sb.append("        local parts = {}\n");
            sb.append("        for number in string.gmatch(v, \"[^%s]+\") do\n");
            sb.append("            parts[table.getn(parts) + 1] = tostring((tonumber(number) or 0) * modelScale)\n");
            sb.append("        end\n");
            sb.append("        unitDef[k] = table.concat(parts, \" \")\n");
            sb.append("    end\n");
            sb.append("end\n");
        }
        float cloneRadius = 30.0f;
        float cloneHeight = 30.0f;
        try {
            com.faforever.neroxis.generator.bar.S3OParser.S3OModel sm = com.faforever.neroxis.generator.bar.OfficialModels.loadFull(ent.getCloneOf());
            if (sm != null) {
                cloneRadius = sm.radius > 0 ? sm.radius : cloneRadius;
                cloneHeight = sm.height > 0 ? sm.height : cloneHeight;
            }
        } catch (RuntimeException ignored) {
        }
        appendStudioDef(sb, ent, cloneRadius * ent.getScale(), cloneHeight * ent.getScale(), ent.isCustomScriptEnabled());
        if (ent.isCustomScriptEnabled() && hasLuaCode(ent.getLuaScript())) {
            sb.append("set(\"script\", \"").append(id).append(".lua\")\n");
        }
        sb.append("\nreturn { [unitName] = unitDef }\n");
        return sb.toString();
    }

    /**
     * Strumenti messi a disposizione del codice di definizione scritto nello Studio (scheda "Definition").
     * Sono funzioni Lua che girano quando il gioco carica le unita': permettono di prendere pezzi di altre
     * unita' di BAR (armi, scudi, movimento) senza dover cambiare il programma.
     */
    private static final String STUDIO_DEF_HELPERS = String.join("\n",
        "function Studio.copy(value)",
        "    if type(value) ~= \"table\" then return value end",
        "    local result = {}",
        "    for k, v in pairs(value) do result[k] = Studio.copy(v) end",
        "    return result",
        "end",
        "-- legge un campo senza badare a maiuscole e minuscole: ritorna valore e nome vero della chiave",
        "function Studio.field(tbl, key)",
        "    if type(tbl) ~= \"table\" then return nil, nil end",
        "    key = string.lower(key)",
        "    for k, v in pairs(tbl) do",
        "        if type(k) == \"string\" and string.lower(k) == key then return v, k end",
        "    end",
        "    return nil, nil",
        "end",
        "function Studio.get(tbl, key)",
        "    local value = Studio.field(tbl, key)",
        "    return value",
        "end",
        "function Studio.set(tbl, key, value)",
        "    local _, existing = Studio.field(tbl, key)",
        "    tbl[existing or key] = value",
        "end",
        "-- sotto-tabella (es. weapondefs, customparams), creata se manca",
        "function Studio.section(tbl, key)",
        "    local value, existing = Studio.field(tbl, key)",
        "    if type(value) ~= \"table\" then",
        "        value = {}",
        "        tbl[existing or key] = value",
        "    end",
        "    return value",
        "end",
        "-- definizione di un'unita' di BAR, come copia modificabile (nil se non esiste)",
        "function Studio.unit(name)",
        "    name = string.lower(tostring(name))",
        "    for _, file in ipairs(VFS.DirList(\"units/\", \"*.lua\", nil, true)) do",
        "        if string.match(string.lower(file), \"([^/]+)%.lua$\") == name then",
        "            local ok, defs = pcall(VFS.Include, file)",
        "            if ok and type(defs) == \"table\" then",
        "                for k, v in pairs(defs) do",
        "                    if type(k) == \"string\" and string.lower(k) == name then return Studio.copy(v) end",
        "                end",
        "            end",
        "        end",
        "    end",
        "    return nil",
        "end",
        "-- aggiunge a def un'arma presa da un'unita' di BAR; ritorna il numero dell'arma nell'unita'",
        "function Studio.addWeapon(def, fromUnit, weaponName, overrides)",
        "    local source = Studio.unit(fromUnit)",
        "    if not source then error(\"unit not found: \" .. tostring(fromUnit)) end",
        "    local weapon = Studio.field(Studio.field(source, \"weapondefs\"), tostring(weaponName))",
        "    if type(weapon) ~= \"table\" then error(\"weapon '\" .. tostring(weaponName) .. \"' not found in \" .. tostring(fromUnit)) end",
        "    if type(overrides) == \"table\" then",
        "        for k, v in pairs(overrides) do Studio.set(weapon, k, v) end",
        "    end",
        "    local newName = string.lower(tostring(fromUnit) .. \"_\" .. tostring(weaponName))",
        "    Studio.section(def, \"weapondefs\")[newName] = weapon",
        "    local weapons = Studio.section(def, \"weapons\")",
        "    local count = 0",
        "    for _ in pairs(weapons) do count = count + 1 end",
        "    weapons[count + 1] = { def = string.upper(newName), onlytargetcategory = \"NOTSUB\" }",
        "    return count + 1",
        "end",
        "-- scudo al plasma con raggio e potenza scelti: BAR ne disegna la bolla. Se non si indica da quale unita'",
        "-- prenderlo, e' quello del Plasma Deflector (armgate, repulsor)",
        "function Studio.addShield(def, radius, power, fromUnit, weaponName)",
        "    radius = radius or (Studio.modelRadius * 2)",
        "    power = power or 20000",
        "    fromUnit = fromUnit or \"armgate\"",
        "    weaponName = weaponName or \"repulsor\"",
        "    local number = Studio.addWeapon(def, fromUnit, weaponName)",
        "    local shield = Studio.field(Studio.field(Studio.field(def, \"weapondefs\"), string.lower(fromUnit .. \"_\" .. weaponName)), \"shield\")",
        "    if type(shield) == \"table\" then",
        "        Studio.set(shield, \"radius\", radius)",
        "        Studio.set(shield, \"power\", power)",
        "        Studio.set(shield, \"startingpower\", power)",
        "        Studio.set(shield, \"powerregen\", power)",
        "        Studio.set(shield, \"powerregenenergy\", 0)",
        "        Studio.set(shield, \"energyupkeep\", 0)",
        "    end",
        "    local params = Studio.section(def, \"customparams\")",
        "    Studio.set(params, \"shield_radius\", radius)",
        "    Studio.set(params, \"shield_power\", power)",
        "    Studio.set(params, \"shield_color_mult\", 0.8)",
        "    -- BAR tiene carico e disegna lo scudo solo se l'unita' e' \"accesa\" (come il Plasma Deflector, che nasce",
        "    -- acceso): senza questi due valori lo scudo resterebbe sempre a zero e la bolla non comparirebbe mai",
        "    Studio.set(def, \"activatewhenbuilt\", true)",
        "    Studio.set(def, \"onoffable\", true)",
        "    return number",
        "end",
        "-- fa muovere def come un'altra unita' di BAR: velocita', sterzata, classe di movimento",
        "function Studio.moveLike(def, fromUnit)",
        "    local source = Studio.unit(fromUnit)",
        "    if not source then error(\"unit not found: \" .. tostring(fromUnit)) end",
        "    local keys = { \"movementclass\", \"speed\", \"maxvelocity\", \"maxacc\", \"acceleration\", \"maxdec\", \"brakerate\",",
        "        \"turnrate\", \"turninplace\", \"turninplaceanglelimit\", \"turninplacespeedlimit\", \"maxslope\", \"maxwaterdepth\",",
        "        \"minwaterdepth\", \"canfly\", \"cruisealtitude\", \"upright\", \"mass\", \"category\", \"footprintx\", \"footprintz\" }",
        "    for _, key in ipairs(keys) do",
        "        local value = Studio.field(source, key)",
        "        if value ~= nil then Studio.set(def, key, value) end",
        "    end",
        "    Studio.set(def, \"canmove\", true)",
        "    Studio.set(def, \"yardmap\", nil)",
        "    Studio.set(def, \"levelground\", nil)",
        "end",
        "");

    /**
     * Blocco finale di una definizione: gli strumenti Studio.*, lo scudo chiesto con "--@shield" nello script e
     * il codice scritto nella scheda "Definition". Un errore in quel codice non impedisce all'unita' di caricarsi:
     * viene scritto nel registro del gioco e la definizione resta com'era fino a quel punto.
     */
    private static void appendStudioDef(StringBuilder sb, com.faforever.neroxis.generator.bar.ObjectEntity ent, float modelRadius, float modelHeight, boolean scriptActive) {
        List<String> code = new ArrayList<>();
        String script = ent.getLuaScript();
        if (script != null && scriptActive) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?m)^\\s*--\\s*@shield\\b(.*)$").matcher(script);
            if (m.find()) {
                String radius = "nil";
                String power = "nil";
                java.util.regex.Matcher kv = java.util.regex.Pattern.compile("(\\w+)\\s*=\\s*([0-9.]+)").matcher(m.group(1));
                while (kv.find()) {
                    if (kv.group(1).equalsIgnoreCase("radius")) radius = kv.group(2);
                    if (kv.group(1).equalsIgnoreCase("power")) power = kv.group(2);
                }
                code.add("Studio.addShield(unitDef, " + radius + ", " + power + ")");
            }
        }
        if (hasLuaCode(ent.getDefLua())) {
            code.add(ent.getDefLua());
        }
        if (code.isEmpty()) {
            return;
        }
        sb.append("\n-- ===== BAR Map Studio: strumenti per il codice di definizione =====\n");
        sb.append(String.format(Locale.ROOT, "local Studio = { scale = %.4f, modelRadius = %.1f, modelHeight = %.1f, unitName = \"%s\" }\n",
            ent.getScale(), modelRadius, modelHeight, ent.getId().toLowerCase(Locale.ROOT)));
        sb.append(STUDIO_DEF_HELPERS);
        sb.append("-- ===== codice scritto nello Studio =====\n");
        sb.append("do\n");
        sb.append("    local ok, problem = pcall(function()\n");
        for (String block : code) {
            sb.append(block.replace("\r", "")).append("\n");
        }
        sb.append("    end)\n");
        sb.append("    if not ok then\n");
        sb.append("        Spring.Echo(\"[BAR Map Studio] definition code of '").append(ent.getId().toLowerCase(Locale.ROOT)).append("' failed: \" .. tostring(problem))\n");
        sb.append("    end\n");
        sb.append("end\n");
    }

    /** true se lo script contiene almeno una riga che non sia vuota o di solo commento. */
    private static boolean hasLuaCode(String script) {
        if (script == null) {
            return false;
        }
        for (String line : script.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                return true;
            }
        }
        return false;
    }

    private static String jsonString(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r', '\t' -> sb.append(' ');
                default -> {
                    if (c < 0x20) {
                        sb.append(' ');
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append("\"").toString();
    }

    /** Campi Lua (hp, metal, energy) per un'unita' di BAR i cui valori sono stati cambiati nello Studio. */
    private static String officialOverrides(com.faforever.neroxis.generator.bar.ObjectEntity ent) {
        if (!ent.hasOfficialUnit() || ent.getId().endsWith("_v2") || ent.isClone()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (ent.getHealth() != ent.getOfficialHealth()) {
            sb.append(", hp = ").append(Math.max(1, ent.getHealth()));
        }
        if (ent.getMetal() != ent.getOfficialMetal() || ent.getEnergy() != ent.getOfficialEnergy()) {
            sb.append(", metal = ").append(Math.max(0, ent.getMetal())).append(", energy = ").append(Math.max(0, ent.getEnergy()));
        }
        return sb.toString();
    }

    private String buildCustomUnitDefLua(com.faforever.neroxis.generator.bar.ObjectEntity ent) {
        StringBuilder sb = new StringBuilder();
        String id = ent.getId();
        int fx = Math.max(1, Math.round(ent.getScaledWidth() / 16.0f));
        int fz = Math.max(1, Math.round(ent.getScaledDepth() / 16.0f));
        String yard = String.join(" ", java.util.Collections.nCopies(fz, "o".repeat(fx)));
        String script = (ent.getLuaScript() != null && !ent.getLuaScript().isBlank()) ? ("\"" + id + ".lua\"") : "nil";

        sb.append("local unitName = \"").append(id).append("\"\n");
        sb.append("local unitDef = {\n");
        sb.append("    name = \"").append(ent.getName() != null ? ent.getName().replace("\"", "'") : id).append("\",\n");
        sb.append("    description = \"").append(ent.getTooltip() != null ? ent.getTooltip().replace("\"", "'") : "").append("\",\n");
        sb.append("    objectName = \"").append(id).append(".s3o\",\n");
        boolean invulnerable = isInvulnerable(ent);
        sb.append("    maxDamage = ").append(invulnerable ? 100000000 : Math.max(10, ent.getHealth())).append(",\n");
        // il motore attuale legge "health"; "maxDamage" resta per le versioni che usano ancora il vecchio nome
        sb.append("    health = ").append(invulnerable ? 100000000 : Math.max(10, ent.getHealth())).append(",\n");
        if (invulnerable) {
            sb.append("    capturable = false,\n");
            sb.append("    autoHeal = 1000000,\n");
            sb.append("    idleAutoHeal = 1000000,\n");
        }
        sb.append("    footprintX = ").append(fx).append(",\n");
        sb.append("    footprintZ = ").append(fz).append(",\n");
        sb.append("    metalCost = ").append(Math.max(0, ent.getMetal())).append(",\n");
        sb.append("    energyCost = ").append(Math.max(0, ent.getEnergy())).append(",\n");
        sb.append("    reclaimable = ").append(!invulnerable).append(",\n");
        sb.append("    sightDistance = ").append(ent.getSightRange()).append(",\n");
        sb.append("    category = \"BUILDING NOTAIR\",\n");
        sb.append("    yardMap = \"").append(yard).append("\",\n");
        sb.append("    levelGround = false,\n");
        sb.append("    useBuildingGroundDecal = false,\n");
        if (!"nil".equals(script)) {
            sb.append("    script = ").append(script).append(",\n");
        }
        appendWeapon(sb, ent.getLuaScript());
        sb.append("}\n");
        appendStudioDef(sb, ent, Math.max(ent.getScaledWidth(), ent.getScaledDepth()) * 0.5f, ent.getScaledHeight(), true);
        sb.append("return lowerkeys({ [unitName] = unitDef })\n");
        return sb.toString();
    }

    /**
     * Arma dell'unita', dichiarata nello script con una riga di commento:
     *   --@weapon missile range=900 reload=2.5 damage=120
     * Tipi: "rocket" (razzo dritto di armrock) e "missile" (stesso razzo, ma insegue il bersaglio).
     * Il weapondef ricalca quello di armrock in BAR, quindi modello, scia, esplosione e suoni esistono gia' nel gioco.
     */
    private static void appendWeapon(StringBuilder sb, String luaScript) {
        if (luaScript == null) {
            return;
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?m)^\\s*--\\s*@weapon\\s+(\\w+)(.*)$").matcher(luaScript);
        if (!m.find()) {
            return;
        }
        boolean homing = m.group(1).equalsIgnoreCase("missile");
        float range = 475.0f;
        float reload = 3.8f;
        float damage = 157.0f;
        java.util.regex.Matcher kv = java.util.regex.Pattern.compile("(\\w+)\\s*=\\s*([0-9.]+)").matcher(m.group(2));
        while (kv.find()) {
            try {
                float value = Float.parseFloat(kv.group(2));
                switch (kv.group(1).toLowerCase(Locale.ROOT)) {
                    case "range" -> range = Math.max(100.0f, Math.min(3000.0f, value));
                    case "reload" -> reload = Math.max(0.2f, Math.min(60.0f, value));
                    case "damage" -> damage = Math.max(1.0f, Math.min(10000.0f, value));
                    default -> { }
                }
            } catch (NumberFormatException ignored) {
            }
        }
        sb.append("    canAttack = true,\n");
        sb.append("    weapondefs = {\n");
        sb.append("        map_missile = {\n");
        sb.append("            areaofeffect = 48,\n");
        sb.append("            avoidfeature = false,\n");
        sb.append("            avoidfriendly = false,\n");
        sb.append("            castshadow = true,\n");
        sb.append("            cegtag = \"missiletrailsmall-simple\",\n");
        sb.append("            craterareaofeffect = 0,\n");
        sb.append("            craterboost = 0,\n");
        sb.append("            cratermult = 0,\n");
        sb.append("            edgeeffectiveness = 0.15,\n");
        sb.append("            explosiongenerator = \"custom:genericshellexplosion-small\",\n");
        sb.append("            firestarter = 70,\n");
        sb.append(String.format(Locale.ROOT, "            flighttime = %.1f,\n", (range / 190.0f) + 3.0f));
        sb.append("            impulsefactor = 0.123,\n");
        sb.append("            metalpershot = 0,\n");
        sb.append("            model = \"cormissile2.s3o\",\n");
        sb.append("            name = \"Map missile\",\n");
        sb.append("            noselfdamage = true,\n");
        sb.append(String.format(Locale.ROOT, "            range = %.0f,\n", range));
        sb.append(String.format(Locale.ROOT, "            reloadtime = %.2f,\n", reload));
        sb.append("            smokecolor = 1,\n");
        sb.append("            smokeperiod = 11,\n");
        sb.append("            smokesize = 3.3,\n");
        sb.append("            smoketime = 28,\n");
        sb.append("            smoketrail = true,\n");
        sb.append("            smoketrailcastshadow = false,\n");
        sb.append("            soundhit = \"xplosml2\",\n");
        sb.append("            soundhitwet = \"splshbig\",\n");
        sb.append("            soundstart = \"rocklit1\",\n");
        sb.append("            startvelocity = 190,\n");
        sb.append("            texture1 = \"null\",\n");
        sb.append("            texture2 = \"smoketraildark\",\n");
        sb.append("            tolerance = 60000,\n");
        if (homing) {
            sb.append("            tracks = true,\n");
            sb.append("            turnrate = 24000,\n");
        }
        sb.append("            turret = true,\n");
        sb.append("            weaponacceleration = 120,\n");
        sb.append("            weapontype = \"MissileLauncher\",\n");
        sb.append("            weaponvelocity = 190,\n");
        sb.append(String.format(Locale.ROOT, "            damage = { default = %.0f },\n", damage));
        sb.append("        },\n");
        sb.append("    },\n");
        sb.append("    weapons = {\n");
        sb.append("        [1] = { def = \"MAP_MISSILE\", onlytargetcategory = \"NOTSUB\" },\n");
        sb.append("    },\n");
    }

    /** Unita' indistruttibile: lo script contiene la riga di commento "--@invulnerable". */
    private static boolean isInvulnerable(com.faforever.neroxis.generator.bar.ObjectEntity ent) {
        String script = ent.getLuaScript();
        return script != null && java.util.regex.Pattern.compile("(?m)^\\s*--\\s*@invulnerable\\b").matcher(script).find();
    }

    private String buildCustomFeatureDefLua(com.faforever.neroxis.generator.bar.ObjectEntity ent) {
        StringBuilder sb = new StringBuilder();
        String id = ent.getId();
        int fx = Math.max(1, Math.round(ent.getScaledWidth() / 16.0f));
        int fz = Math.max(1, Math.round(ent.getScaledDepth() / 16.0f));

        sb.append("local featureDef = {\n");
        sb.append("    name = \"").append(id).append("\",\n");
        sb.append("    description = \"").append(ent.getName() != null ? ent.getName().replace("\"", "'") : id).append("\",\n");
        sb.append("    object = \"").append(id).append(".s3o\",\n");
        sb.append("    blocking = true,\n");
        sb.append("    burnable = false,\n");
        sb.append("    reclaimable = true,\n");
        sb.append("    energy = ").append(Math.max(0, ent.getEnergy())).append(",\n");
        sb.append("    metal = ").append(Math.max(0, ent.getMetal())).append(",\n");
        sb.append("    damage = ").append(Math.max(10, ent.getHealth())).append(",\n");
        sb.append("    footprintX = ").append(fx).append(",\n");
        sb.append("    footprintZ = ").append(fz).append(",\n");
        sb.append("    hitdensity = 1000,\n");
        sb.append("    mass = 200,\n");
        sb.append("}\n");
        sb.append("return { [\"").append(id).append("\"] = featureDef }\n");
        return sb.toString();
    }

    /**
     * Un numero che dipende solo dalla distanza del punto dalla base piu' vicina e dal centro della mappa:
     * punti in posizioni specchiate ottengono lo stesso numero, e quindi la stessa scelta.
     */
    private int symmetricHash(float x, float z) {
        float nearestSpawn = Float.MAX_VALUE;
        for (Spawn spawn : this.map.getSpawns()) {
            float dx = spawn.getPosition().x() * this.elmosPerCell - x;
            float dz = spawn.getPosition().z() * this.elmosPerCell - z;
            nearestSpawn = Math.min(nearestSpawn, (float) StrictMath.sqrt(dx * dx + dz * dz));
        }
        float dx = this.mapX * 8 / 2.0f - x;
        float dz = this.mapY * 8 / 2.0f - z;
        float fromCentre = (float) StrictMath.sqrt(dx * dx + dz * dz);
        return hash(Math.round(nearestSpawn / 24.0f), Math.round(fromCentre / 24.0f));
    }

    /** Aggiunge un oggetto appoggiato al terreno, se il punto e' dentro la mappa. */
    private void addFeature(List<SpringMapFile.Feature> features, List<String> featureTypes, String type, float x, float z, float rotationRadians) {
        if (!isInsideMap(x, z, 8.0f)) {
            return;
        }
        int typeIndex = featureTypes.indexOf(type);
        if (typeIndex < 0) {
            typeIndex = featureTypes.size();
            featureTypes.add(type);
        }
        // il motore vuole la direzione come numero da -32768 a 32767 per un giro completo
        float heading = (float) (rotationRadians / 6.283185307179586d % 1.0d * 65536.0d);
        if (heading > 32767.0f) {
            heading -= 65536.0f;
        } else if (heading < -32768.0f) {
            heading += 65536.0f;
        }
        features.add(new SpringMapFile.Feature(typeIndex, x, heightAt(x, z), z, heading, 1.0f));
    }

    /**
     * Il relitto di BAR che sostituisce un'unita' dell'editor, scelto in base al tipo (terzo carattere del
     * codice: S nave, A aereo, B edificio) e alla taglia. Null se in quel punto non ha senso un relitto.
     */
    private static String getWreck(String unitType, int hash, boolean underwater) {
        String type = unitType.toUpperCase(Locale.ROOT);
        char domain = type.length() > 2 ? type.charAt(2) : 'L';
        String[] choices;
        if (underwater) {
            choices = SHIP_WRECKS;
        } else if (domain == 'S' || domain == 'A') {
            return null; // navi all'asciutto e aerei non lasciano relitti
        } else if (domain == 'B') {
            choices = BUILDING_WRECKS;
        } else if (T1_LAND.contains(type)) {
            choices = SMALL_WRECKS;
        } else if (T3_LAND.contains(type)) {
            choices = LARGE_WRECKS;
        } else {
            choices = MEDIUM_WRECKS;
        }
        return choices[Math.floorMod(hash, choices.length)];
    }

    /** La famiglia di rocce di BAR intonata al bioma. */
    private String getRockSet() {
        String biome = String.valueOf(this.map.getBiome().name()).toLowerCase(Locale.ROOT);
        return switch (biome) {
            case "desert", "mars", "syrtis", "sunset" -> "desert";
            case "frithen", "crystalline" -> "snow";
            case "earlyautumn", "windingriver", "wonder", "prayer" -> "moss";
            default -> "def";
        };
    }

    private boolean isInsideMap(float x, float z, float margin) {
        return x >= margin && z >= margin && x <= (float) (this.mapX * 8) - margin && z <= (float) (this.mapY * 8) - margin;
    }

    /** Vero se il punto e' entro la distanza data da un punto metallo o da un geyser. */
    private boolean isNearResource(float x, float z, float distance) {
        float limit = distance * distance;
        for (List<float[]> resources : List.of(this.mexPositions, this.geoPositions)) {
            for (float[] resource : resources) {
                float dx = resource[0] - x;
                float dz = resource[1] - z;
                if (dx * dx + dz * dz < limit) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Pendenza del terreno nel punto, misurata su 16 elmi. */
    private float slopeAt(float x, float z) {
        float dx = heightAt(x + 8.0f, z) - heightAt(x - 8.0f, z);
        float dz = heightAt(x, z + 8.0f) - heightAt(x, z - 8.0f);
        return (float) StrictMath.sqrt(dx * dx + dz * dz) / 16.0f;
    }

    /** Un numero intero che sembra casuale ma dipende solo dalle due coordinate. */
    private static int hash(float x, float z) {
        int h = Float.floatToIntBits(x) * -1640531535 + Float.floatToIntBits(z) * -2048144777;
        h = (h ^ (h >>> 15)) * 739982445;
        return h ^ (h >>> 12);
    }

    /**
     * Quanto ogni strato di texture dell'editor copre ogni punto, da 0 a 1. Le due immagini portano quattro
     * strati ciascuna, uno per canale; nel canale 0 vuol dire assente e da 128 a 255 la copertura crescente.
     */
    private static float[][] readLayerWeights(BufferedImage low, BufferedImage high) {
        int width = low.getWidth();
        int height = low.getHeight();
        if (high.getWidth() != width || high.getHeight() != height) {
            throw new IllegalArgumentException("Texture masks have different sizes: low=" + width + "x" + height + ", high=" + high.getWidth() + "x" + high.getHeight());
        }
        float[][] weights = new float[8][width * height];
        int[] pixel = new int[4];
        BufferedImage[] masks = {low, high};
        for (int mask = 0; mask < 2; mask++) {
            WritableRaster raster = masks[mask].getRaster();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    raster.getPixel(x, y, pixel);
                    for (int channel = 0; channel < 4; channel++) {
                        int raw = pixel[channel];
                        weights[mask * 4 + channel][y * width + x] = raw > 0 ? Math.max(0.0f, (raw - 128) / 127.0f) : 0.0f;
                    }
                }
            }
        }
        return weights;
    }

    /**
     * Il colore di ogni strato di texture del bioma (quello di fondo piu' gli otto delle maschere), gia'
     * schiarito o scurito secondo la luce del bioma. Null per gli strati che il bioma non usa.
     */
    private float[][] readLayerColors() {
        List<String> previewColors = this.map.getBiome().terrainMaterials().previewColors();
        List<TerrainMaterials.TextureScale> textures = this.map.getBiome().terrainMaterials().textures();
        Vector3 sun = this.map.getBiome().lightingSettings().sunColor();
        Vector3 ambient = this.map.getBiome().lightingSettings().sunAmbience();
        float multiplier = this.map.getBiome().lightingSettings().lightingMultiplier();
        float light = ((sun.x() + sun.y() + sun.z()) / 3.0f * 0.85f + (ambient.x() + ambient.y() + ambient.z()) / 3.0f) * multiplier;
        float[][] colors = new float[9][];
        for (int layer = 0; layer < 9; layer++) {
            String hex = layer < previewColors.size() ? previewColors.get(layer) : "";
            boolean used = layer < textures.size() && !textures.get(layer).path().isBlank() && !hex.isBlank();
            if (!used) {
                continue;
            }
            int rgb = Integer.parseInt(hex, 16);
            float[] color = {((rgb >> 16) & 255) / 255.0f * light, ((rgb >> 8) & 255) / 255.0f * light, (rgb & 255) / 255.0f * light};
            // niente colori bruciati: il canale piu' forte non supera 0,92
            float brightest = Math.max(color[0], Math.max(color[1], color[2]));
            if (brightest > 0.92f) {
                for (int c = 0; c < 3; c++) {
                    color[c] *= 0.92f / brightest;
                }
            }
            colors[layer] = color;
        }
        return colors;
    }

    /** Un riquadro 512x512 di rumore ripetibile, a media zero e con dispersione fissa, usato per variare i colori. */
    private static float[] createNoise() {
        float[] values = new float[512 * 512];
        IntStream.range(0, 512).parallel().forEach(y -> {
            for (int x = 0; x < 512; x++) {
                values[y * 512 + x] = GroundDetail.tileableNoise((x + 0.5f) / 512.0f, (y + 0.5f) / 512.0f, 6, 6, 0.6f, 7919);
            }
        });
        double sum = 0.0d;
        for (float value : values) {
            sum += value;
        }
        double mean = sum / values.length;
        double variance = 0.0d;
        for (float value : values) {
            variance += (value - mean) * (value - mean);
        }
        float scale = NOISE_DEVIATION / (float) StrictMath.sqrt(variance / values.length);
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.max(-1.0f, Math.min(1.0f, (float) (values[i] - mean) * scale));
        }
        if (System.getProperty("neroxis.bar.debug") != null) {
            System.out.printf(Locale.ROOT, "noise deviation before rescale %.3f%n", NOISE_DEVIATION / scale);
        }
        return values;
    }

    private static float smooth(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    /** Chiazze di terra nuda: due letture del rumore a scale e direzioni diverse. */
    private float earthPatches(float x, float z) {
        float broad = noiseAt((x * 0.788f + z * 0.616f) * 0.031f + 77.0f, (z * 0.788f - x * 0.616f) * 0.031f + 5.0f);
        float fine = noiseAt((x * 0.342f - z * 0.94f) * 0.1f + 19.0f, (x * 0.94f + z * 0.342f) * 0.1f + 233.0f);
        return broad * 0.7f + fine * 0.45f;
    }

    /** Il rumore in un punto qualsiasi: il riquadro si ripete all'infinito e si interpola tra i quattro valori vicini. */
    private float noiseAt(float x, float y) {
        float wrappedX = x - (float) Math.floor(x / 512.0f) * 512.0f;
        float wrappedY = y - (float) Math.floor(y / 512.0f) * 512.0f;
        int x0 = (int) wrappedX & 511;
        int y0 = (int) wrappedY & 511;
        int x1 = (x0 + 1) & 511;
        int y1 = (y0 + 1) & 511;
        float fractionX = wrappedX - (int) wrappedX;
        float fractionY = wrappedY - (int) wrappedY;
        float top = this.noise[y0 * 512 + x0] * (1.0f - fractionX) + this.noise[y0 * 512 + x1] * fractionX;
        float bottom = this.noise[y1 * 512 + x0] * (1.0f - fractionX) + this.noise[y1 * 512 + x1] * fractionX;
        return top * (1.0f - fractionY) + bottom * fractionY;
    }

    /** Pendenza del terreno gia' ammorbidito in un punto della griglia, misurata su quattro celle. */
    private float broadSlope(float[] blurred, int x, int z) {
        int row = this.mapX + 1;
        int x0 = Math.max(0, x - 2);
        int x1 = Math.min(this.mapX, x + 2);
        int z0 = Math.max(0, z - 2);
        int z1 = Math.min(this.mapY, z + 2);
        float dx = (blurred[z * row + x1] - blurred[z * row + x0]) / ((x1 - x0) * 8);
        float dz = (blurred[z1 * row + x] - blurred[z0 * row + x]) / ((z1 - z0) * 8);
        return (float) StrictMath.sqrt(dx * dx + dz * dz);
    }

    /**
     * Rende le scarpate piu' nette: dove il terreno e' ripido, ogni punto viene spinto verso il piede o verso
     * la cima della scarpata, cosi' il pendio si concentra in una parete invece di una rampa.
     */
    private void sharpenCliffs() {
        int row = this.mapX + 1;
        int rows = this.mapY + 1;
        float[] blurred = boxBlur(this.heights, row, rows, 2);
        float[] top = extremeFilter(this.heights, row, rows, 3, true);
        float[] foot = extremeFilter(this.heights, row, rows, 3, false);
        float[] sharpened = new float[this.heights.length];
        IntStream.range(0, rows).parallel().forEach(z -> {
            for (int x = 0; x < row; x++) {
                float steep = smoothStep(0.55f, 0.95f, broadSlope(blurred, x, z));
                int i = z * row + x;
                float height = this.heights[i];
                if (steep <= 0.0f || top[i] - foot[i] < 12.0f) {
                    sharpened[i] = height;
                    continue;
                }
                float position = (height - foot[i]) / (top[i] - foot[i]);
                float curved = position * position * (3.0f - 2.0f * position);
                float target = foot[i] + (top[i] - foot[i]) * curved * curved * (3.0f - 2.0f * curved);
                sharpened[i] = height + (target - height) * steep * 0.75f;
            }
        });
        float[] softened = boxBlur(sharpened, row, rows, 1);
        for (int i = 0; i < this.heights.length; i++) {
            this.heights[i] = sharpened[i] * 0.5f + softened[i] * 0.5f;
        }
    }

    /** Aggiunge creste e canaloni sulle pareti ripide, perche' non sembrino lisce. */
    private void addRidges() {
        int row = this.mapX + 1;
        float[] blurred = boxBlur(this.heights, row, this.mapY + 1, 2);
        float[] ridged = new float[this.heights.length];
        IntStream.rangeClosed(0, this.mapY).parallel().forEach(z -> {
            for (int x = 0; x <= this.mapX; x++) {
                float steep = smoothStep(0.5f, 0.9f, broadSlope(blurred, x, z));
                int i = z * row + x;
                if (steep <= 0.0f) {
                    ridged[i] = this.heights[i];
                    continue;
                }
                float worldX = x * 8;
                float worldZ = z * 8;
                float large = Math.max(0.0f, 1.0f - Math.abs(smoothNoise(worldX, worldZ, 110.0f, 0.788f, 0.616f, 9)) * 2.2f);
                float small = Math.max(0.0f, 1.0f - Math.abs(smoothNoise(worldX, worldZ, 46.0f, 0.454f, -0.891f, 37)) * 2.2f);
                ridged[i] = this.heights[i] + ((large * large - 0.45f) * 14.0f + (small * small - 0.45f) * 5.0f) * steep;
            }
        });
        System.arraycopy(ridged, 0, this.heights, 0, this.heights.length);
    }

    /** Per ogni punto, il valore piu' alto (o piu' basso) nel quadrato di lato 2*radius+1 intorno. */
    private static float[] extremeFilter(float[] values, int width, int height, int radius, boolean maximum) {
        float[] rows = new float[values.length];
        float[] result = new float[values.length];
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float extreme = values[y * width + x];
                for (int d = -radius; d <= radius; d++) {
                    float value = values[y * width + clamp(x + d, width)];
                    extreme = maximum ? Math.max(extreme, value) : Math.min(extreme, value);
                }
                rows[y * width + x] = extreme;
            }
        });
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float extreme = rows[y * width + x];
                for (int d = -radius; d <= radius; d++) {
                    float value = rows[clamp(y + d, height) * width + x];
                    extreme = maximum ? Math.max(extreme, value) : Math.min(extreme, value);
                }
                result[y * width + x] = extreme;
            }
        });
        return result;
    }

    /** Media nel quadrato di lato 2*radius+1 intorno a ogni punto (i bordi ripetono l'ultimo valore). */
    private static float[] boxBlur(float[] values, int width, int height, int radius) {
        float[] rows = new float[values.length];
        float[] result = new float[values.length];
        int window = radius * 2 + 1;
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float sum = 0.0f;
                for (int d = -radius; d <= radius; d++) {
                    sum += values[y * width + clamp(x + d, width)];
                }
                rows[y * width + x] = sum / window;
            }
        });
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float sum = 0.0f;
                for (int d = -radius; d <= radius; d++) {
                    sum += rows[clamp(y + d, height) * width + x];
                }
                result[y * width + x] = sum / window;
            }
        });
        return result;
    }

    /**
     * Per ogni cella, la distanza in elmi dall'acqua piu' vicina (zero dentro l'acqua). Si ottiene con due
     * passate sulla griglia, una in avanti e una all'indietro, che propagano la distanza dai vicini:
     * 8 elmi per un passo dritto, 11,31 per uno in diagonale.
     */
    private void buildShoreDistance() {
        final float straight = 8.0f;
        final float diagonal = 11.3136f;
        int width = this.mapX;
        int height = this.mapY;
        int row = width + 1;
        float[] distance = new float[width * height];
        boolean hasWater = false;
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                float level = (this.heights[z * row + x] + this.heights[z * row + x + 1]
                    + this.heights[(z + 1) * row + x] + this.heights[(z + 1) * row + x + 1]) / 4.0f;
                boolean underwater = level < 0.0f;
                distance[z * width + x] = underwater ? 0.0f : 1000000.0f;
                hasWater |= underwater;
            }
        }
        if (hasWater) {
            for (int z = 0; z < height; z++) {
                for (int x = 0; x < width; x++) {
                    int i = z * width + x;
                    float nearest = distance[i];
                    if (x > 0) {
                        nearest = Math.min(nearest, distance[i - 1] + straight);
                    }
                    if (z > 0) {
                        nearest = Math.min(nearest, distance[i - width] + straight);
                    }
                    if (x > 0 && z > 0) {
                        nearest = Math.min(nearest, distance[i - width - 1] + diagonal);
                    }
                    if (x < width - 1 && z > 0) {
                        nearest = Math.min(nearest, distance[i - width + 1] + diagonal);
                    }
                    distance[i] = nearest;
                }
            }
            for (int z = height - 1; z >= 0; z--) {
                for (int x = width - 1; x >= 0; x--) {
                    int i = z * width + x;
                    float nearest = distance[i];
                    if (x < width - 1) {
                        nearest = Math.min(nearest, distance[i + 1] + straight);
                    }
                    if (z < height - 1) {
                        nearest = Math.min(nearest, distance[i + width] + straight);
                    }
                    if (x < width - 1 && z < height - 1) {
                        nearest = Math.min(nearest, distance[i + width + 1] + diagonal);
                    }
                    if (x > 0 && z < height - 1) {
                        nearest = Math.min(nearest, distance[i + width - 1] + diagonal);
                    }
                    distance[i] = nearest;
                }
            }
        }
        this.shoreDistance = distance;
    }

    /**
     * Quanto un punto e' spiaggia, da 0 a 1: vicino all'acqua, poco sopra il suo livello, non troppo
     * sott'acqua e non su una parete. {@code wobble} sposta il bordo per renderlo irregolare.
     */
    private float beachAt(float x, float z, float wobble) {
        if (this.shoreDistance.length == 0) {
            return 0.0f;
        }
        float cellX = Math.max(0.0f, Math.min(this.mapX - 1.001f, x / 8.0f - 0.5f));
        float cellZ = Math.max(0.0f, Math.min(this.mapY - 1.001f, z / 8.0f - 0.5f));
        int x0 = (int) cellX;
        int z0 = (int) cellZ;
        float fractionX = cellX - x0;
        float fractionZ = cellZ - z0;
        int i = z0 * this.mapX + x0;
        float near = Math.min(this.shoreDistance[i], 1000.0f) * (1.0f - fractionX) + Math.min(this.shoreDistance[i + 1], 1000.0f) * fractionX;
        float far = Math.min(this.shoreDistance[i + this.mapX], 1000.0f) * (1.0f - fractionX) + Math.min(this.shoreDistance[i + this.mapX + 1], 1000.0f) * fractionX;
        float fromWater = near * (1.0f - fractionZ) + far * fractionZ;
        float height = heightAt(x, z);
        float byDistance = smoothStep(BEACH_WIDTH, 31.5f, fromWater + wobble * 22.0f);
        float byHeight = smoothStep(34.0f, 16.0f, height);
        float notTooDeep = smoothStep(-70.0f, -12.0f, height);
        float notSteep = 1.0f - smoothStep(MAX_SATURATION, 0.6f, slopeAt(x, z));
        return byDistance * byHeight * notTooDeep * notSteep;
    }

    private static float luminance(float[] rgb) {
        return rgb[0] * 0.2126f + rgb[1] * 0.7152f + rgb[2] * 0.0722f;
    }

    /**
     * Sceglie i colori di roccia, terra e sabbia guardando la mappa: campiona il colore del terreno sui
     * pendii ripidi (roccia) e sui piani (suolo) e li usa come base, cosi' si intonano al bioma.
     * Ricava anche le quote di "terreno basso" e "terreno alto" usate per variare i colori.
     */
    private void pickRockAndSandColors() {
        float[] landHeights = new float[64 * 64];
        int landCount = 0;
        for (int gz = 0; gz < 64; gz++) {
            for (int gx = 0; gx < 64; gx++) {
                float h = heightAt((gx + 0.5f) / 64.0f * this.mapX * 8.0f, (gz + 0.5f) / 64.0f * this.mapY * 8.0f);
                if (h > 2.0f) {
                    landHeights[landCount++] = h;
                }
            }
        }
        if (landCount > 10) {
            Arrays.sort(landHeights, 0, landCount);
            this.lowGround = landHeights[landCount / 5];
            this.highGround = Math.max(this.lowGround + 40.0f, landHeights[landCount * 9 / 10]);
        }

        float[] rock = new float[3];
        float[] ground = new float[3];
        float rockWeight = 0.0f;
        float groundWeight = 0.0f;
        for (int gz = 0; gz < 160; gz++) {
            for (int gx = 0; gx < 160; gx++) {
                float x = (gx + 0.5f) / 160 * this.mapX * 8.0f;
                float z = (gz + 0.5f) / 160 * this.mapY * 8.0f;
                if (heightAt(x, z) < 6.0f) {
                    continue;
                }
                float slope = slopeAt(x, z);
                float[] color = blendLayers(x, z, 0.0f, 0.0f, slope);
                float steep = smoothStep(0.6f, 1.1f, slope);
                float flat = 1.0f - smoothStep(0.05f, 0.2f, slope);
                for (int c = 0; c < 3; c++) {
                    rock[c] += color[c] * steep;
                    ground[c] += color[c] * flat;
                }
                rockWeight += steep;
                groundWeight += flat;
            }
        }

        if (groundWeight > 0.0f) {
            float groundLight = luminance(ground) / groundWeight;
            float groundSaturation = (Math.max(ground[0], Math.max(ground[1], ground[2])) - Math.min(ground[0], Math.min(ground[1], ground[2]))) / groundWeight;
            this.snowGround = groundLight > 0.55f && groundSaturation < 0.14f;
            // terra: un marrone portato alla stessa luminosita' del suolo, con un terzo del suo colore
            float[] brown = {0.47f, 0.37f, 0.25f};
            float brownLight = luminance(brown);
            for (int c = 0; c < 3; c++) {
                this.dirtColor[c] = (ground[c] / groundWeight * 0.35f + brown[c] * (groundLight / brownLight) * 0.65f) * 0.92f;
            }
            // sabbia: calda, piu' chiara o piu' scura secondo il suolo
            float[] warmSand = {0.7f, 0.62f, 0.44f};
            float brightness = Math.max(0.75f, Math.min(1.15f, (groundLight + 0.22f) / 0.6f));
            for (int c = 0; c < 3; c++) {
                this.sandColor[c] = (warmSand[c] * 0.72f + ground[c] / groundWeight * 0.28f) * brightness;
            }
        }
        if (rockWeight <= 4.0f) {
            // quasi nessun pendio ripido da cui campionare: la roccia si ricava dal suolo
            if (groundWeight > 0.0f) {
                for (int c = 0; c < 3; c++) {
                    this.rockColor[c] = ground[c] / groundWeight * 0.45f + 0.2f;
                }
            }
            return;
        }
        float rockLight = luminance(rock) / rockWeight;
        float[] warmGrey = {1.04f, 1.0f, 0.94f};
        float targetLight = Math.max(0.24f, Math.min(TARGET_LUMINANCE, rockLight));
        for (int c = 0; c < 3; c++) {
            this.rockColor[c] = rock[c] / rockWeight * (targetLight / Math.max(rockLight, 0.01f)) * 0.4f + targetLight * warmGrey[c] * 0.6f;
        }
    }

    /**
     * Porta i colori del bioma a una luminosita' e a una vivacita' adatte al gioco: misura quelle medie del
     * terreno emerso e corregge tutti gli strati; gli strati di roccia vengono poi ingrigiti e scaldati.
     */
    private void gradeColors() {
        if (this.barBiome != null) {
            return; // i colori vengono da una mappa BAR gia' tarata per il gioco
        }
        double lightSum = 0.0d;
        double saturationSum = 0.0d;
        int samples = 0;
        for (int gz = 0; gz < 96; gz++) {
            for (int gx = 0; gx < 96; gx++) {
                float x = (gx + 0.5f) / 96 * this.mapX * 8.0f;
                float z = (gz + 0.5f) / 96 * this.mapY * 8.0f;
                if (heightAt(x, z) < 8.0f) {
                    continue;
                }
                float[] color = blendLayers(x, z, 0.0f, 0.0f, slopeAt(x, z));
                lightSum += luminance(color);
                saturationSum += Math.max(color[0], Math.max(color[1], color[2])) - Math.min(color[0], Math.min(color[1], color[2]));
                samples++;
            }
        }
        if (samples == 0) {
            return;
        }
        float light = (float) (lightSum / samples);
        float saturation = (float) (saturationSum / samples);
        float gain = Math.max(0.85f, Math.min(2.2f, TARGET_LUMINANCE / Math.max(light, 0.01f)));
        float saturationAfterGain = Math.max(saturation * gain, 0.01f);
        float boost = 1.0f;
        if (saturationAfterGain < TARGET_SATURATION) {
            boost = Math.min(1.6f, TARGET_SATURATION / saturationAfterGain);
        } else if (saturationAfterGain > MAX_SATURATION) {
            boost = Math.max(0.6f, MAX_SATURATION / saturationAfterGain);
        }
        for (float[] color : this.layerColors) {
            if (color == null) {
                continue;
            }
            float grey = luminance(color);
            for (int c = 0; c < 3; c++) {
                color[c] = Math.max(0.02f, Math.min(0.93f, (grey + (color[c] - grey) * boost) * gain));
            }
        }
        float[] warmGrey = {1.05f, 1.0f, 0.93f};
        for (int layer = 0; layer < 8; layer++) {
            float[] color = this.layerColors[layer + 1];
            if (color != null && this.layerKinds[layer] == 2) {
                float grey = luminance(color);
                for (int c = 0; c < 3; c++) {
                    color[c] = color[c] * MAX_SATURATION + grey * warmGrey[c] * 0.7f;
                }
            }
        }
        System.out.printf(Locale.ROOT, "Colour grading: brightness x%.2f, saturation x%.2f%n", gain, boost);
    }

    /**
     * Il colore del terreno in un punto, mescolando gli strati di texture dell'editor secondo quanto coprono
     * il punto. Gli strati di pendio e di roccia compaiono solo dove il terreno e' abbastanza ripido;
     * {@code medium} e {@code fine} spostano i bordi tra gli strati per renderli irregolari.
     */
    private float[] blendLayers(float x, float z, float medium, float fine, float slope) {
        float maskX = Math.max(0.0f, Math.min(this.layerWidth - 1.001f, x / this.elmosPerCell * (this.layerWidth - 1) / this.faWidth));
        float maskZ = Math.max(0.0f, Math.min(this.layerHeight - 1.001f, z / this.elmosPerCell * (this.layerHeight - 1) / this.faHeight));
        int x0 = (int) maskX;
        int z0 = (int) maskZ;
        float fractionX = maskX - x0;
        float fractionZ = maskZ - z0;
        int nearLeft = z0 * this.layerWidth + x0;
        int nearRight = nearLeft + 1;
        int farLeft = nearLeft + this.layerWidth;
        int farRight = farLeft + 1;
        float shareNearLeft = (1.0f - fractionX) * (1.0f - fractionZ);
        float shareNearRight = fractionX * (1.0f - fractionZ);
        float shareFarLeft = (1.0f - fractionX) * fractionZ;
        float shareFarRight = fractionX * fractionZ;

        float red = 0.4f;
        float green = 0.38f;
        float blue = 0.3f;
        float[] base = this.layerColors[0];
        if (base != null) {
            red = base[0];
            green = base[1];
            blue = base[2];
        }
        for (int layer = 0; layer < 8; layer++) {
            float[] color = this.layerColors[layer + 1];
            if (color == null) {
                continue;
            }
            float[] mask = this.layerWeights[layer];
            float cover = mask[nearLeft] * shareNearLeft + mask[nearRight] * shareNearRight + mask[farLeft] * shareFarLeft + mask[farRight] * shareFarRight;
            if (cover <= 0.0f) {
                continue;
            }
            int kind = this.layerKinds[layer];
            if (kind == 1) {
                cover *= smoothStep(0.07f, 0.26f, slope);
            } else if (kind == 2) {
                cover *= smoothStep(0.22f, 0.5f, slope);
            }
            if (cover <= 0.0f) {
                continue;
            }
            float amount = Math.max(0.0f, Math.min(1.0f, cover + (medium * 0.6f + fine * 0.25f) * cover * (1.0f - cover) * 2.0f));
            red += (color[0] - red) * amount;
            green += (color[1] - green) * amount;
            blue += (color[2] - blue) * amount;
        }
        return new float[]{red, green, blue};
    }

    /**
     * Il colore di un pixel della texture del terreno, nel punto (x, z) in elmi.
     *
     * <p>Si parte dal colore di base (materiali di zona, immagine dell'utente, oppure gli strati del bioma
     * con terra, quota, fondale e spiaggia), poi si applicano luce e rilievo, le pareti di roccia, la
     * variazione di tinta, il bagnato vicino all'acqua e infine i segni dei punti metallo e dei geyser.</p>
     *
     * @param nearMexes punti metallo abbastanza vicini da poter toccare questa riga di pixel
     * @param nearGeos geyser abbastanza vicini
     */
    private int shade(float x, float z, List<float[]> nearMexes, List<float[]> nearGeos) {
        // tre rumori a scale diverse: grana fine, chiazze medie, grandi variazioni
        float fine = this.noise[(((int) z) & 511) * 512 + (((int) x) & 511)];
        float medium = noiseAt((x * 0.891f - z * 0.454f) * 0.137f + 91.0f, (x * 0.454f + z * 0.891f) * 0.137f + 47.0f) * 0.6f
            + noiseAt((x * 0.602f + z * 0.799f) * 0.061f + 7.0f, (z * 0.602f - x * 0.799f) * 0.061f + 311.0f) * 0.4f;
        float coarse = noiseAt((x * 0.788f + z * 0.616f) * 0.0113f + 13.0f, (z * 0.788f - x * 0.616f) * 0.0113f + 173.0f) * 0.55f
            + noiseAt((x * 0.342f - z * 0.94f) * 0.0047f + 401.0f, (x * 0.94f + z * 0.342f) * 0.0047f + 59.0f) * 0.45f;
        float slopeX = (heightAt(x + 6.0f, z) - heightAt(x - 6.0f, z)) / 12.0f;
        float slopeZ = (heightAt(x, z + 6.0f) - heightAt(x, z - 6.0f)) / 12.0f;
        float slope = (float) StrictMath.sqrt(slopeX * slopeX + slopeZ * slopeZ);
        boolean ownColours = this.zoneMaterials != null || this.customTexture != null;

        float red;
        float green;
        float blue;
        if (this.zoneMaterials != null) {
            // materiali di zona del pannello Texture: maschere di zona per piastrelle ripetute in elmi
            float[] weights = new float[4];
            float[] rgb = new float[3];
            this.zoneMaterials.weights(x / this.textureWidth, z / this.textureHeight, medium, weights);
            for (int i = 0; i < 4; i++) {
                if (weights[i] > 0.0f) {
                    this.zoneMaterials.layers[i].addColor(x, z, weights[i], rgb);
                }
            }
            this.zoneMaterials.applyTint(x / this.textureWidth, z / this.textureHeight, rgb);
            // il motore moltiplica il colore per la luce del terreno: si compensa quella del terreno piano,
            // cosi' in gioco il materiale ha il colore del file
            float[] flat = flatGroundLight();
            red = rgb[0] / flat[0];
            green = rgb[1] / flat[1];
            blue = rgb[2] / flat[2];
        } else if (this.customTexture != null) {
            float[] rgb = sampleCustomTexture(x, z);
            red = rgb[0];
            green = rgb[1];
            blue = rgb[2];
        } else {
            float[] rgb = blendLayers(x, z, medium, fine, slope);
            red = rgb[0];
            green = rgb[1];
            blue = rgb[2];

            // terra nuda: a chiazze, sui pendii moderati e intorno ai punti metallo
            float bare = smoothStep(0.1f, 0.32f, earthPatches(x, z) + medium * 0.2f) * 0.7f
                + smoothStep(0.07f, 0.22f, slope) * (1.0f - smoothStep(MAX_SATURATION, 0.5f, slope)) * 0.55f;
            for (float[] mex : nearMexes) {
                float away = (float) StrictMath.sqrt((x - mex[0]) * (x - mex[0]) + (z - mex[1]) * (z - mex[1]));
                bare = Math.max(bare, smoothStep(95.0f, 40.0f, away + medium * 18.0f) * 0.6f);
            }
            bare = Math.min(0.8f, bare) * (this.snowGround ? MAX_SATURATION : 1.0f);
            if (bare > 0.0f) {
                float grain = 1.0f + fine * 0.1f + medium * 0.08f;
                red += (this.dirtColor[0] * grain - red) * bare;
                green += (this.dirtColor[1] * grain - green) * bare;
                blue += (this.dirtColor[2] * grain - blue) * bare;
            }

            // in alto il terreno e' un po' piu' secco e chiaro
            float altitude = Math.max(0.0f, Math.min(1.0f, (heightAt(x, z) - this.lowGround) / (this.highGround - this.lowGround)));
            float dry = altitude * 0.2f;
            float grey = red * 0.2126f + green * 0.7152f + blue * 0.0722f;
            float lift = 1.0f + altitude * 0.07f;
            red = (red + (grey * 1.06f - red) * dry) * lift;
            green = (green + (grey * 1.0f - green) * dry) * lift;
            blue = (blue + (grey * 0.9f - blue) * dry) * lift;

            if (this.barBiome != null && this.barBiome.seabed != null) {
                float depth = smoothStep(0.0f, -60.0f, heightAt(x, z));
                if (depth > 0.0f) {
                    red += (this.barBiome.seabed[0] - red) * depth;
                    green += (this.barBiome.seabed[1] - green) * depth;
                    blue += (this.barBiome.seabed[2] - blue) * depth;
                }
            }
            float beach = beachAt(x, z, medium);
            if (beach > 0.0f) {
                float grain = 1.0f + fine * 0.06f;
                red += (this.sandColor[0] * grain - red) * beach;
                green += (this.sandColor[1] * grain - green) * beach;
                blue += (this.sandColor[2] * grain - blue) * beach;
            }
        }

        // luce: quanto la superficie guarda il sole rispetto al terreno piano, piu' scuro nelle conche
        float normalLength = (float) StrictMath.sqrt(slopeX * slopeX + slopeZ * slopeZ + 1.0f);
        float normalX = -slopeX / normalLength;
        float normalY = 1.0f / normalLength;
        float normalZ = -slopeZ / normalLength;
        float facingSun = Math.max(0.0f, normalX * this.sunDirection[0] + normalY * this.sunDirection[1] + normalZ * this.sunDirection[2]);
        float steepness = Math.max(0.0f, Math.min(1.0f, (1.0f - normalY - 0.08f) / 0.35f));
        float height = heightAt(x, z);
        float curvature = (heightAt(x + 14.0f, z) + heightAt(x - 14.0f, z) + heightAt(x, z + 14.0f) + heightAt(x, z - 14.0f) - 4.0f * height) / 14.0f;
        float shading = (1.0f + (facingSun - this.sunDirection[1]) * 0.28f) * (1.0f - steepness * 0.08f);
        shading *= 1.0f - Math.max(-0.1f, Math.min(0.2f, curvature * TARGET_SATURATION));

        if (ownColours) {
            red *= shading;
            green *= shading;
            blue *= shading;
        } else {
            // pareti di roccia, con striature che seguono le curve di livello
            float rockFace = smoothStep(MAX_SATURATION, 0.62f, slope + medium * 0.05f);
            if (rockFace > 0.0f) {
                float alongContour = (x * -slopeZ + z * slopeX) / (slope + 1.0E-4f);
                float streaks = noiseAt(alongContour * 0.42f + 31.0f, height * 0.035f + 7.0f) * 0.65f
                    + noiseAt(alongContour * 0.17f + 113.0f, height * 0.02f + 59.0f) * 0.35f;
                float faceLight = 1.0f + streaks * 0.22f;
                red += (this.rockColor[0] * faceLight - red) * rockFace * 0.85f;
                green += (this.rockColor[1] * faceLight - green) * rockFace * 0.85f;
                blue += (this.rockColor[2] * faceLight - blue) * rockFace * 0.85f;
                shading = Math.max(shading * (1.0f - Math.max(-0.07f, Math.min(0.2f, curvature * 0.22f)) * rockFace), 0.78f);
            }
            // lenta variazione di tinta sul piano: un po' piu' calda o piu' fredda
            float drift = coarse * (1.0f - steepness);
            red *= 1.0f + drift * TARGET_SATURATION;
            green *= 1.0f + drift * 0.05f;
            blue *= 1.0f - drift * TARGET_SATURATION;
            if (steepness > 0.0f) {
                // strati orizzontali sulle pareti
                float band = (float) StrictMath.sin(height * 0.11f + medium * 2.5f + coarse * 4.0f) * 0.6f
                    + (float) StrictMath.sin(height * 0.37f + medium * 4.0f) * 0.4f;
                float strata = band * steepness;
                red *= 1.0f + strata * 0.08f;
                green *= 1.0f + strata * 0.065f;
                blue *= 1.0f + strata * 0.045f;
            }
            float detail = 1.0f + fine * (0.08f + 0.04f * steepness) + medium * 0.17f + coarse * 0.14f;
            float brightness = shading * detail;
            red *= brightness;
            green *= brightness;
            blue *= brightness;
        }

        if (height < 12.0f) {
            // bagnato vicino alla riva e sempre piu' scuro e blu con la profondita'
            float wet = Math.max(0.0f, Math.min(1.0f, ((this.zoneMaterials != null ? -2.0f : 5.0f) - height) / 14.0f));
            float depth = Math.max(0.0f, Math.min(1.0f, -height / 220.0f));
            if (this.barBiome != null && this.barBiome.seabed != null) {
                depth *= 0.25f;
            }
            float darker = 1.0f - wet * 0.22f;
            red *= darker * (1.0f - depth * 0.55f);
            green *= darker * (1.0f - depth * 0.4f);
            blue *= darker * (1.0f - depth * 0.25f);
        }

        for (float[] mex : nearMexes) {
            // macchia metallica, piu' grande e piu' chiara se il punto e' ricco
            float dx = x - mex[0];
            float dz = z - mex[1];
            float richness = Math.max(0.0f, Math.min(1.0f, (mex[2] - this.options.baseMetal()) / 2.0f));
            float away = (float) StrictMath.sqrt(dx * dx + dz * dz) / (MEX_MARK_RADIUS * (0.8f + 0.2f * richness));
            if (away < 1.0f) {
                float edge = away + medium * 0.18f;
                float cover = Math.max(0.0f, Math.min(1.0f, (1.0f - edge) / MAX_SATURATION)) * (0.7f + 0.25f * richness);
                float metal = 0.36f + TARGET_SATURATION * richness + fine * 0.1f + medium * 0.08f;
                float sparkle = fine > 0.45f ? 0.25f : 0.0f;
                red += (metal * 0.92f + sparkle - red) * cover;
                green += (metal * 0.95f + sparkle - green) * cover;
                blue += (metal * 1.04f + sparkle - blue) * cover;
            }
        }
        for (float[] geo : nearGeos) {
            // chiazza scura bruciata intorno al geyser
            float dx = x - geo[0];
            float dz = z - geo[1];
            float away = (float) StrictMath.sqrt(dx * dx + dz * dz) / GEO_MARK_RADIUS;
            if (away < 1.0f) {
                float edge = away + medium * 0.25f;
                float cover = Math.max(0.0f, Math.min(1.0f, (1.0f - edge) / 0.6f));
                cover *= cover;
                red += (0.1f - red) * cover * 0.9f;
                green += (0.07f - green) * cover * 0.9f;
                blue += (0.05f - blue) * cover * 0.9f;
            }
        }
        return (toByte(red) << 16) | (toByte(green) << 8) | toByte(blue);
    }

    /** Il colore dell'immagine scelta dall'utente nel punto dato, interpolato tra i quattro pixel vicini. */
    private float[] sampleCustomTexture(float x, float z) {
        float u = Math.max(0.0f, Math.min(1.0f, x / (float) this.textureWidth));
        float v = Math.max(0.0f, Math.min(1.0f, z / (float) this.textureHeight));
        int width = this.customTexture.getWidth();
        int height = this.customTexture.getHeight();
        float px = u * (width - 1);
        float py = v * (height - 1);
        int x0 = (int) px;
        int y0 = (int) py;
        int x1 = Math.min(width - 1, x0 + 1);
        int y1 = Math.min(height - 1, y0 + 1);
        float fractionX = px - x0;
        float fractionY = py - y0;
        int topLeft = this.customTexture.getRGB(x0, y0);
        int topRight = this.customTexture.getRGB(x1, y0);
        int bottomLeft = this.customTexture.getRGB(x0, y1);
        int bottomRight = this.customTexture.getRGB(x1, y1);
        float[] rgb = new float[3];
        for (int c = 0; c < 3; c++) {
            int shift = 16 - c * 8;
            float top = ((topLeft >> shift) & 255) * (1.0f - fractionX) + ((topRight >> shift) & 255) * fractionX;
            float bottom = ((bottomLeft >> shift) & 255) * (1.0f - fractionX) + ((bottomRight >> shift) & 255) * fractionX;
            rgb[c] = (top * (1.0f - fractionY) + bottom * fractionY) / 255.0f;
        }
        return rgb;
    }

    private float[] flatGroundLight;

    /** Luce che il motore applica al terreno piano: groundAmbientColor + groundDiffuseColor * sole (vedi buildMapInfo). */
    private float[] flatGroundLight() {
        float[] cached = this.flatGroundLight;
        if (cached != null) {
            return cached;
        }
        Vector3 sunColor = this.map.getBiome().lightingSettings().sunColor();
        float sunMax = Math.max(sunColor.x(), Math.max(sunColor.y(), Math.max(sunColor.z(), 0.001f)));
        float cos = this.sunDirection[1];
        float[] result = {
            Math.max(1.0f, 0.52f + ((sunColor.x() / sunMax) * 0.95f * cos)),
            Math.max(1.0f, 0.52f + ((sunColor.y() / sunMax) * 0.95f * cos)),
            Math.max(1.0f, 0.55f + ((sunColor.z() / sunMax) * 0.95f * cos))};
        this.flatGroundLight = result;
        return result;
    }

    /** I quattro materiali di dettaglio della mappa: roccia, ghiaia, il suolo tipico del bioma e il fondale. */
    private GroundDetail.Material[] getSplatMaterials() {
        return new GroundDetail.Material[]{GroundDetail.Material.ROCK, GroundDetail.Material.GRAVEL, getGroundMaterial(), GroundDetail.Material.SEABED};
    }

    /**
     * Il materiale di dettaglio piu' adatto al suolo della mappa, deciso dal colore medio intorno alle basi:
     * verde = erba, chiaro e neutro = neve, rossastro = polvere, altrimenti terra spaccata.
     */
    private GroundDetail.Material getGroundMaterial() {
        if (this.groundMaterial != null) {
            return this.groundMaterial;
        }
        float red = 0.0f;
        float green = 0.0f;
        float blue = 0.0f;
        int samples = 0;
        for (Spawn spawn : this.map.getSpawns()) {
            float[] base = toElmos(spawn);
            // nove punti in un quadrato di 180 elmi intorno alla base
            for (int i = 0; i < 9; i++) {
                int rgb = shade(base[0] + (i % 3 - 1) * 90, base[1] + (i / 3 - 1) * 90, List.of(), List.of());
                red += (rgb >> 16) & 255;
                green += (rgb >> 8) & 255;
                blue += rgb & 255;
                samples++;
            }
        }
        float darkest = Math.min(red, Math.min(green, blue));
        float brightest = Math.max(red, Math.max(green, blue));
        GroundDetail.Material material;
        if (samples == 0) {
            material = GroundDetail.Material.DUST;
        } else if (green > red * 1.06f && green > blue * 1.12f) {
            material = GroundDetail.Material.GRASS;
        } else if (darkest / samples > 150.0f && brightest < darkest * 1.25f) {
            material = GroundDetail.Material.SNOW;
        } else if (red > blue * 1.3f) {
            material = GroundDetail.Material.DUST;
        } else {
            material = GroundDetail.Material.CRACKED;
        }
        this.groundMaterial = material;
        return material;
    }

    /**
     * La mappa che dice al gioco quale materiale di dettaglio usare in ogni punto, a un quarto della
     * risoluzione della texture: rosso = roccia, verde = ghiaia, blu = suolo, trasparenza = sabbia e fondale.
     */
    private byte[] buildSplatDistribution() {
        int width = this.mapX * 2;
        int height = this.mapY * 2;
        final float texel = 4.0f;
        int[] argb = new int[width * height];
        IntStream.range(0, height).parallel().forEach(row -> {
            float z = (row + 0.5f) * texel;
            float[] zones = new float[4];
            for (int column = 0; column < width; column++) {
                float x = (column + 0.5f) * texel;
                if (this.zoneMaterials != null) {
                    // le zone 1..4 del pannello Texture vanno nei canali rosso, verde, blu, trasparenza
                    this.zoneMaterials.weights(x / this.textureWidth, z / this.textureHeight, 0.0f, zones);
                    argb[row * width + column] = (Math.round(zones[3] * 255.0f) << 24) | (Math.round(zones[0] * 255.0f) << 16)
                        | (Math.round(zones[1] * 255.0f) << 8) | Math.round(zones[2] * 255.0f);
                    continue;
                }
                float slope = slopeAt(x, z);
                float ground = heightAt(x, z);
                float patches = earthPatches(x, z);
                float rock = smoothStep(0.2f, VEHICLE_MAX_GRADIENT, slope);
                float sand = Math.max(smoothStep(5.0f, -10.0f, ground + patches * 3.0f), beachAt(x, z, patches));
                float gravel = Math.min(1.0f, smoothStep(0.06f, 0.2f, slope) + smoothStep(0.1f, 0.32f, patches) * 0.7f);
                float rest = (1.0f - rock) * (1.0f - sand);
                int red = Math.round(rock * 255.0f);
                int green = Math.round(rest * gravel * 255.0f);
                int blue = Math.round(rest * (1.0f - gravel) * 255.0f);
                int alpha = Math.round((1.0f - rock) * sand * 255.0f);
                argb[row * width + column] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
        return TextureCodec.dxt5File(argb, width, height);
    }

    /** Le posizioni che possono toccare la fascia di pixel tra {@code minZ} e {@code maxZ}. */
    private List<float[]> filterByRows(List<float[]> positions, float minZ, float maxZ, float radius) {
        List<float[]> close = new ArrayList<>();
        for (float[] position : positions) {
            if (position[1] + radius >= minZ && position[1] - radius <= maxZ) {
                close.add(position);
            }
        }
        return close;
    }

    /** Tra le posizioni date, quelle entro {@code radius} dalla colonna {@code x}. */
    private static List<float[]> filterByColumn(List<float[]> positions, float x, float radius) {
        List<float[]> close = new ArrayList<>();
        for (float[] position : positions) {
            if (Math.abs(position[0] - x) <= radius) {
                close.add(position);
            }
        }
        return close;
    }

    /**
     * Scrive la texture del terreno come piastrelle da 32x32 pixel, ognuna compressa con i suoi quattro
     * livelli di dettaglio (32, 16, 8, 4). Si lavora a blocchi di 64 righe di piastrelle per non tenere
     * tutta la texture in memoria.
     */
    private void writeTiles(OutputStream out) throws IOException {
        final int tile = SpringMapFile.TILE_SIZE;
        final int tileBytes = SpringMapFile.TILE_BYTES;
        int tilesAcross = this.textureWidth / tile;
        int tilesDown = this.textureHeight / tile;
        SpringMapFile.writeTileFileHeader(out, tilesAcross * tilesDown);
        for (int first = 0; first < tilesDown; first += 64) {
            int last = Math.min(tilesDown, first + 64);
            List<byte[]> rows = IntStream.range(first, last).parallel().mapToObj(tileY -> {
                byte[] rowBytes = new byte[tilesAcross * tileBytes];
                float top = tileY * tile;
                List<float[]> rowMexes = filterByRows(this.mexPositions, top, top + tile, WORN_GROUND_RADIUS);
                List<float[]> rowGeos = filterByRows(this.geoPositions, top, top + tile, GEO_MARK_RADIUS);
                int[] pixels = new int[tile * tile];
                for (int tileX = 0; tileX < tilesAcross; tileX++) {
                    float centreX = tileX * tile + tile / 2.0f;
                    List<float[]> tileMexes = filterByColumn(rowMexes, centreX, WORN_GROUND_RADIUS + tile);
                    List<float[]> tileGeos = filterByColumn(rowGeos, centreX, GEO_MARK_RADIUS + tile);
                    for (int y = 0; y < tile; y++) {
                        for (int x = 0; x < tile; x++) {
                            pixels[y * tile + x] = shade(tileX * tile + x + 0.5f, top + y + 0.5f, tileMexes, tileGeos);
                        }
                    }
                    int at = tileX * tileBytes;
                    int[] level = pixels;
                    for (int size = tile; size >= 4; size /= 2) {
                        at += TextureCodec.compressDxt1(level, size, size, rowBytes, at);
                        if (size > 4) {
                            level = TextureCodec.halve(level, size, size);
                        }
                    }
                }
                return rowBytes;
            }).toList();
            for (byte[] row : rows) {
                out.write(row);
            }
        }
    }

    /**
     * L'immagine di anteprima per le librerie di mappe: l'anteprima del gioco con il rilievo messo in
     * evidenza da una luce radente, come in una carta geografica.
     */
    private BufferedImage renderPreview(BufferedImage minimapImage) {
        int width = minimapImage.getWidth();
        int height = minimapImage.getHeight();
        int[] pixels = minimapImage.getRGB(0, 0, width, height, null, 0, width);
        float stepX = this.textureWidth / (float) width;
        float stepZ = this.textureHeight / (float) height;
        final float lightX = -0.6f;
        final float lightY = 0.64f;
        final float lightZ = -0.48f;
        IntStream.range(0, height).parallel().forEach(row -> {
            for (int column = 0; column < width; column++) {
                float x = (column + 0.5f) * stepX;
                float z = (row + 0.5f) * stepZ;
                // l'acqua conta come piana: le quote sotto zero valgono zero
                float slopeX = Math.max(0.0f, heightAt(x + stepX, z)) - Math.max(0.0f, heightAt(x - stepX, z));
                float slopeZ = Math.max(0.0f, heightAt(x, z + stepZ)) - Math.max(0.0f, heightAt(x, z - stepZ));
                float nx = -slopeX;
                float ny = 2.0f * stepX;
                float nz = -slopeZ;
                float length = (float) StrictMath.sqrt(nx * nx + ny * ny + nz * nz);
                float lit = (nx * lightX + ny * lightY + nz * lightZ) / length;
                float factor = Math.max(0.5f, Math.min(1.45f, 1.0f + (lit - lightY) * 1.7f));
                int rgb = pixels[row * width + column];
                int red = Math.min(255, Math.round(((rgb >> 16) & 255) * factor));
                int green = Math.min(255, Math.round(((rgb >> 8) & 255) * factor));
                int blue = Math.min(255, Math.round((rgb & 255) * factor));
                pixels[row * width + column] = (red << 16) | (green << 8) | blue;
            }
        });
        BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        preview.setRGB(0, 0, width, height, pixels, 0, width);
        return preview;
    }

    /** L'anteprima 1024x1024 mostrata dal gioco: la texture del terreno, con l'acqua tinta di blu secondo la profondita'. */
    private BufferedImage renderMinimap() {
        final int size = SpringMapFile.MINIMAP_SIZE;
        float stepX = this.textureWidth / size;
        float stepZ = this.textureHeight / size;
        int[] pixels = new int[size * size];
        IntStream.range(0, size).parallel().forEach(row -> {
            for (int column = 0; column < size; column++) {
                float x = (column + 0.5f) * stepX;
                float z = (row + 0.5f) * stepZ;
                int rgb = shade(x, z, this.mexPositions, this.geoPositions);
                float height = heightAt(x, z);
                if (height < 0.0f) {
                    float depth = Math.min(1.0f, -height / 160.0f);
                    float keep = 0.55f - depth * MAX_SATURATION;
                    int red = (int) (((rgb >> 16) & 255) * keep + 30.0f * (1.0f - keep));
                    int green = (int) (((rgb >> 8) & 255) * keep + 80.0f * (1.0f - keep));
                    int blue = (int) ((rgb & 255) * keep + 125.0f * (1.0f - keep));
                    rgb = (red << 16) | (green << 8) | blue;
                }
                pixels[row * size + column] = rgb;
            }
        });
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, size, size, pixels, 0, size);
        return image;
    }

    /**
     * Dove cresce l'erba, una cella ogni 32 elmi (solo se il suolo della mappa e' erboso): sul piano, lontano
     * da acqua, basi, risorse e chiazze di terra; il valore dice quanto e' fitta.
     */
    private byte[] buildGrassMap() {
        int width = this.mapX / 4;
        int height = this.mapY / 4;
        byte[] grass = new byte[width * height];
        if (getSplatMaterials()[2] != GroundDetail.Material.GRASS) {
            return grass;
        }
        List<float[]> bases = this.map.getSpawns().stream().map(this::toElmos).toList();
        IntStream.range(0, height).parallel().forEach(row -> {
            float z = (row + 0.5f) * 4.0f * 8.0f;
            for (int column = 0; column < width; column++) {
                float x = (column + 0.5f) * 4.0f * 8.0f;
                float clumps = noiseAt((x * 0.788f + z * 0.616f) * 0.017f + 211.0f, (z * 0.788f - x * 0.616f) * 0.017f + 97.0f);
                float bare = earthPatches(x, z);
                boolean nearBase = false;
                for (float[] base : bases) {
                    nearBase |= distance(base, x, z) < 260.0f;
                }
                boolean grows = bare < 0.26f && heightAt(x, z) > 4.0f && slopeAt(x, z) < 0.12f && beachAt(x, z, 0.0f) < 0.25f
                    && !nearBase && !isNearResource(x, z, BEACH_WIDTH);
                if (grows) {
                    float density = Math.max(0.0f, Math.min(1.0f, (clumps + 1.0f) / 2.0f)) * (1.0f - Math.max(0.0f, bare) * 3.0f);
                    grass[row * width + column] = (byte) (70 + Math.round(density * 185.0f));
                }
            }
        });
        return grass;
    }

    /**
     * La texture delle normali dell'intera mappa: piccole ondulazioni che danno grana alla luce, piu' marcate
     * sui pendii. Nella trasparenza c'e' quanto il punto riflette (poco sott'acqua, di piu' sulla roccia).
     */
    private byte[] buildNormalsTexture() {
        int sizeX = Math.min(2048, this.mapX * 2);
        int sizeY = Math.min(2048, this.mapY * 2);
        float stepX = this.mapX * 8.0f / sizeX;
        float stepZ = this.mapY * 8.0f / sizeY;
        int[] argb = new int[sizeX * sizeY];
        IntStream.range(0, sizeY).parallel().forEach(row -> {
            float z = (row + 0.5f) * stepZ;
            for (int column = 0; column < sizeX; column++) {
                float x = (column + 0.5f) * stepX;
                float steep = Math.min(1.0f, slopeAt(x, z) / 0.5f);
                float strength = 0.5f + steep * 2.4f;
                float dx = (relief(x + stepX, z) - relief(x - stepX, z)) / (2.0f * stepX) * strength;
                float dz = (relief(x, z + stepZ) - relief(x, z - stepZ)) / (2.0f * stepZ) * strength;
                float length = (float) StrictMath.sqrt(dx * dx + dz * dz + 1.0f);
                int red = toByte(-dx / length * 0.5f + 0.5f);
                int green = toByte(-dz / length * 0.5f + 0.5f);
                int blue = toByte(1.0f / length * 0.5f + 0.5f);
                int alpha = toByte(heightAt(x, z) < -20.0f ? 0.25f : 0.45f + steep * MAX_SATURATION);
                argb[row * sizeX + column] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
        return GroundDetail.encodeTga(argb, sizeX, sizeY);
    }

    /** Le piccole ondulazioni del suolo in un punto: due onde di rumore a passo diverso. */
    private float relief(float x, float z) {
        return smoothNoise(x, z, 24.0f, 0.891f, -0.454f, 17) * 2.2f + smoothNoise(x, z, 55.0f, 0.602f, 0.799f, 131) * 4.2f;
    }

    /**
     * Rumore morbido tra -1 e 1 con onde lunghe circa {@code wavelength} elmi, ruotato dell'angolo di cui
     * {@code cos} e {@code sin} sono coseno e seno (cosi' le onde non seguono gli assi della mappa).
     */
    private static float smoothNoise(float x, float z, float wavelength, float cos, float sin, int seed) {
        float domain = wavelength * 64.0f;
        float u = (x * cos - z * sin) / domain;
        float v = (x * sin + z * cos) / domain;
        return GroundDetail.tileableNoise(u - (float) Math.floor(u), v - (float) Math.floor(v), 64, 2, 0.5f, seed) * 2.0f - 1.0f;
    }

    /**
     * La texture dei riflessi, un pixel per cella: piu' lucido dove e' bagnato, sulla roccia e sulla neve.
     * Con i materiali di zona vale la lucentezza scelta per ciascuno.
     */
    private byte[] buildSpecularTexture() {
        int sizeX = this.mapX;
        int sizeY = this.mapY;
        final float texel = 8.0f;
        boolean snow = getSplatMaterials()[2] == GroundDetail.Material.SNOW;
        int[] argb = new int[sizeX * sizeY];
        IntStream.range(0, sizeY).parallel().forEach(row -> {
            float z = (row + 0.5f) * texel;
            float[] zones = new float[4];
            for (int column = 0; column < sizeX; column++) {
                float x = (column + 0.5f) * texel;
                float height = heightAt(x, z);
                float rock = smoothStep(0.2f, VEHICLE_MAX_GRADIENT, slopeAt(x, z));
                float wet = smoothStep(16.0f, -4.0f, height);
                float variation = noiseAt(x * 0.11f + 3.0f, z * 0.11f + 29.0f) * 0.02f;
                float shine = (snow ? 0.13f : 0.05f) + rock * 0.05f + wet * TARGET_SATURATION + variation;
                if (this.zoneMaterials != null) {
                    this.zoneMaterials.weights(x / this.textureWidth, z / this.textureHeight, 0.0f, zones);
                    float gloss = 0.0f;
                    for (int i = 0; i < 4; i++) {
                        gloss += zones[i] * this.zoneMaterials.layers[i].material.gloss;
                    }
                    shine = gloss + wet * TARGET_SATURATION + variation;
                    rock = 0.0f;
                }
                int red = toByte(shine * (1.0f - wet * 0.15f));
                int green = toByte(shine);
                int blue = toByte(shine * (1.0f + wet * 0.2f));
                int alpha = toByte(0.35f + wet * 0.4f + rock * 0.1f);
                argb[row * sizeX + column] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
        return TextureCodec.dxt5File(argb, sizeX, sizeY);
    }

    /** Da un valore tra 0 e 1 a un byte tra 0 e 255. */
    private static int toByte(float value) {
        return Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }

    /** La texture usata oltre il bordo della mappa: l'anteprima resa piu' grigia e piu' scura. */
    private byte[] buildEdgeTexture(BufferedImage minimapImage) {
        int width = minimapImage.getWidth();
        int height = minimapImage.getHeight();
        int[] pixels = minimapImage.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            int red = (pixels[i] >> 16) & 255;
            int green = (pixels[i] >> 8) & 255;
            int blue = pixels[i] & 255;
            float grey = red * MAX_SATURATION + green * 0.59f + blue * 0.11f;
            int fadedRed = Math.round((grey + (red - grey) * 0.35f) * 0.5f);
            int fadedGreen = Math.round((grey + (green - grey) * 0.35f) * 0.5f);
            int fadedBlue = Math.round((grey + (blue - grey) * 0.35f) * 0.5f);
            pixels[i] = 0xFF000000 | (fadedRed << 16) | (fadedGreen << 8) | fadedBlue;
        }
        return TextureCodec.dxt5File(pixels, width, height);
    }

    /** Comprime un'immagine in DXT1 con i suoi livelli di dettaglio, ognuno grande la meta' del precedente. */
    private static byte[] compressWithMips(BufferedImage image, int levels) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] &= 0xFFFFFF;
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int level = 0; level < levels; level++) {
            byte[] compressed = new byte[Math.max(1, width / 4) * Math.max(1, height / 4) * 8];
            TextureCodec.compressDxt1(pixels, width, height, compressed, 0);
            result.writeBytes(compressed);
            if (level < levels - 1) {
                pixels = TextureCodec.halve(pixels, width, height);
                width /= 2;
                height /= 2;
            }
        }
        return result.toByteArray();
    }

    /**
     * Il file mapinfo.lua, che descrive la mappa al gioco. I valori tra chiocciole vengono sostituiti
     * al momento dell'export.
     */
    private static final String MAPINFO_TEMPLATE = """
        -- Generated by the Neroxis Map Generator (BAR export)
        local mapinfo = {
        \tname = "@NAME@",
        \tshortname = "@SHORTNAME@",
        \tdescription = "@DESCRIPTION@",
        \tauthor = "@AUTHOR@",
        \tversion = "@VERSION@",
        \tmapfile = "maps/@MAPFILE@.smf",
        \tmodtype = 3,
        \tdepend = {"Map Helper v1"},

        \tmaphardness = 300,
        \tnotDeformable = false,
        \tgravity = 100,
        \ttidalStrength = @TIDAL@,
        \tmaxMetal = @MAXMETAL@,
        \textractorRadius = 100,
        \tvoidWater = false,
        \tvoidGround = false,
        \tautoShowMetal = true,

        \tsmf = {
        \t\tminheight = @MINHEIGHT@,
        \t\tmaxheight = @MAXHEIGHT@,
        \t\tsmtFileName0 = "maps/@BASE@.smt",
        \t\tminimapTex = "maps/@BASE@_preview.png",
        \t},

        \tresources = {
        \t\tgrassShadingTex = "@BASE@_edge.dds",
        \t\tdetailNormalTex = "@BASE@_normals.tga",
        \t\tspecularTex = "@BASE@_specular.dds",
        \t\tsplatDetailNormalDiffuseAlpha = 1,
        \t\tsplatDetailNormalTex1 = "@BASE@_dnts1.tga",
        \t\tsplatDetailNormalTex2 = "@BASE@_dnts2.tga",
        \t\tsplatDetailNormalTex3 = "@BASE@_dnts3.tga",
        \t\tsplatDetailNormalTex4 = "@BASE@_dnts4.tga",
        \t\tsplatDistrTex = "@BASE@_splats.dds",
        \t},

        \tsplats = {
        \t\ttexScales = {0.0045, 0.0071, 0.0062, 0.0058},
        \t\ttexMults = {0.6, 0.7, 0.65, 0.6},
        \t},

        \tatmosphere = {
        \t\tminWind = @MINWIND@,
        \t\tmaxWind = @MAXWIND@,
        \t\tfogStart = 0.6,
        \t\tfogEnd = 1.0,
        \t\tfogColor = {@FOG@},
        \t\tskyColor = {0.42, 0.55, 0.72},
        \t\tsunColor = {@SUN@},
        \t\tcloudColor = {0.9, 0.9, 0.9},
        \t\tcloudDensity = 0.4,
        \t},

        \tlighting = {
        \t\tsunDir = {@DIR@},
        \t\tgroundAmbientColor = {0.52, 0.52, 0.55},
        \t\tgroundDiffuseColor = {@GROUND@},
        \t\tgroundSpecularColor = {0.05, 0.05, 0.05},
        \t\tgroundShadowDensity = 0.75,
        \t\tunitAmbientColor = {0.6, 0.6, 0.6},
        \t\tunitDiffuseColor = {@UNIT@},
        \t\tunitSpecularColor = {0.8, 0.8, 0.8},
        \t\tunitShadowDensity = 0.75,
        \t\tspecularExponent = 100.0,
        \t},

        \twater = {
        \t\tdamage = 0,
        \t\tabsorb = {0.06, 0.025, 0.012},
        \t\tbaseColor = {0.75, 0.9, 0.95},
        \t\tminColor = {0.05, 0.12, 0.2},
        \t\tsurfaceColor = {0.6, 0.75, 0.85},
        \t\tsurfaceAlpha = 0.25,
        \t\tplaneColor = {0.02, 0.1, 0.16},
        \t\tfresnelMin = 0.2,
        \t\tfresnelMax = 0.8,
        \t\tfresnelPower = 4.0,
        \t\tshoreWaves = true,
        \t\tforceRendering = false,
        \t\thasWaterPlane = @WATERPLANE@,
        \t},

        \tteams = {
        @TEAMS@\t},

        \tterrainTypes = {
        \t\t[0] = {
        \t\t\tname = "Default",
        \t\t\thardness = 1.0,
        \t\t\treceiveTracks = true,
        \t\t\tmoveSpeeds = {
        \t\t\t\ttank = 1.0,
        \t\t\t\tkbot = 1.0,
        \t\t\t\thover = 1.0,
        \t\t\t\tship = 1.0,
        \t\t\t},
        \t\t},
        \t},

        \tgrass = {
        \t\tbladeWaveScale = 1.0,
        \t\tbladeWidth = 0.4,
        \t\tbladeHeight = 4.0,
        \t\tbladeAngle = 1.57,
        \t\tbladeColor = {0.55, 0.6, 0.3},
        \t},

        \tcustom = {
        \t\tgenerator = "neroxis",
        \t\t-- the grass shading texture is used to darken the map edge extension, the grass
        \t\t-- takes its colour from the minimap instead
        \t\tgrassconfig = {
        \t\t\tmapGrassColorModTex = "$minimap",
        \t\t\tgrassMinSize = 0.6,
        \t\t\tgrassMaxSize = 1.2,
        \t\t\tgrassShaderParams = {
        \t\t\t\tMAPCOLORFACTOR = 0.5,
        \t\t\t\tMAPCOLORBASE = 0.9,
        \t\t\t},
        \t\t},
        \t},
        }

        return mapinfo
        """;

    private static String triple(float first, float second, float third) {
        return String.format(Locale.ROOT, "%.3f, %.3f, %.3f", first, second, third);
    }

    /** Compila il mapinfo.lua della mappa e gli accoda, come commento, lo script per provarla contro l'IA. */
    private String buildMapInfo() {
        Vector3 sunColor = this.map.getBiome().lightingSettings().sunColor();
        // il colore del sole portato alla massima intensita': il canale piu' forte vale 1
        float strongest = Math.max(sunColor.x(), Math.max(sunColor.y(), Math.max(sunColor.z(), 0.001f)));
        float[] sun = {sunColor.x() / strongest, sunColor.y() / strongest, sunColor.z() / strongest};
        Vector3 fog = this.map.getBiome().lightingSettings().fogColor();

        List<Spawn> spawns = new ArrayList<>(this.map.getSpawns());
        Map<Spawn, Integer> spawnTeams = resolveTeams(spawns);
        spawns.sort(Comparator.comparingInt(spawn -> spawnTeams.getOrDefault(spawn, 0)));
        StringBuilder teams = new StringBuilder();
        for (int i = 0; i < spawns.size(); i++) {
            float[] position = toElmos(spawns.get(i));
            teams.append("\t\t[").append(i).append("] = {startPos = {x = ").append(Math.round(position[0]))
                .append(", z = ").append(Math.round(position[1])).append("}},\n");
        }
        String description = "Generated by the Neroxis Map Generator for BAR. " + spawns.size() + " players, "
            + this.mexPositions.size() + " metal spots, " + this.geoPositions.size() + " geo vents. "
            + describeTeams(spawns, spawnTeams) + " Regenerate with --map-name " + this.map.getName();
        String displayName = getDisplayName();
        String info = MAPINFO_TEMPLATE
            .replace("@NAME@", displayName)
            .replace("@SHORTNAME@", displayName)
            .replace("@DESCRIPTION@", description)
            .replace("@MAPFILE@", toFileName(displayName))
            .replace("@MAXMETAL@", String.format(Locale.ROOT, "%.4f", getMaxMetal()))
            .replace("@MINHEIGHT@", String.format(Locale.ROOT, "%.1f", this.minHeight))
            .replace("@MAXHEIGHT@", String.format(Locale.ROOT, "%.1f", this.maxHeight))
            .replace("@FOG@", triple(clampColor(fog.x()), clampColor(fog.y()), clampColor(fog.z())))
            .replace("@SUN@", triple(sun[0], sun[1], sun[2]))
            .replace("@DIR@", triple(this.sunDirection[0], this.sunDirection[1], this.sunDirection[2]))
            .replace("@GROUND@", triple(sun[0] * 0.95f, sun[1] * 0.95f, sun[2] * 0.95f))
            .replace("@UNIT@", triple(sun[0] * 0.85f, sun[1] * 0.85f, sun[2] * 0.85f))
            .replace("@WATERPLANE@", this.minHeight < 0.0f ? "true" : "false")
            .replace("@TEAMS@", teams)
            .replace("@BASE@", toFileName(displayName))
            .replace("@AUTHOR@", this.options.author())
            .replace("@VERSION@", this.options.mapName() != null ? this.options.mapVersion() : "")
            + "\n--[[skirmish\n" + buildGameScript(List.of("Player"), true, false, null) + "]]\n";
        if (this.zoneMaterials != null) {
            // una ripetizione del dettaglio ogni tile_elmos, allineata al colore della mappa
            StringBuilder scales = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                scales.append(i > 0 ? ", " : "").append(String.format(Locale.ROOT, "%.6f", 1.0f / this.zoneMaterials.layers[i].material.tileElmos));
            }
            info = info.replace("texScales = {0.0045, 0.0071, 0.0062, 0.0058}", "texScales = {" + scales + "}")
                .replace("texMults = {0.6, 0.7, 0.65, 0.6}", "texMults = {1.0, 1.0, 1.0, 1.0}");
        }
        if (this.barBiome != null) {
            BarBiomes.BarBiome bb = this.barBiome;
            info = setLua(info, "fogColor", bb.fogColor);
            info = setLua(info, "skyColor", bb.skyColor);
            info = setLua(info, "sunColor", bb.sunColor);
            info = setLua(info, "cloudColor", bb.cloudColor);
            info = setLua(info, "fogStart", bb.fogStart);
            info = setLua(info, "fogEnd", bb.fogEnd);
            info = setLua(info, "groundAmbientColor", bb.groundAmbient);
            info = setLua(info, "groundDiffuseColor", bb.groundDiffuse);
            info = setLua(info, "groundSpecularColor", bb.groundSpecular);
            info = setLua(info, "groundShadowDensity", bb.shadowDensity);
            info = setLua(info, "unitAmbientColor", bb.unitAmbient);
            info = setLua(info, "unitDiffuseColor", bb.unitDiffuse);
            info = setLua(info, "unitSpecularColor", bb.unitSpecular);
            info = setLua(info, "absorb", bb.waterAbsorb);
            info = setLua(info, "baseColor", bb.waterBase);
            info = setLua(info, "minColor", bb.waterMin);
            info = setLua(info, "surfaceColor", bb.waterSurface);
            info = setLua(info, "surfaceAlpha", bb.waterSurfaceAlpha);
            info = setLua(info, "planeColor", bb.waterPlane);
            info = info.replace("generator = \"neroxis\",", "generator = \"neroxis\",\n\t\tbiomeSource = \"" + bb.source.replace("\"", "'") + "\",");
        }
        MapExtraOptions extra = MapExtraOptions.get();
        info = setLua(info, "gravity", (float) extra.gravity);
        info = setLua(info, "extractorRadius", (float) extra.extractorRadius);
        info = setLua(info, "damage", (float) extra.waterDamage);
        if (extra.notDeformable) {
            info = info.replace("notDeformable = false", "notDeformable = true");
        }
        if (extra.voidWater) {
            info = info.replace("voidWater = false", "voidWater = true");
        }
        if (extra.night) {
            // notte: poca luce bluastra su terreno e unita', cielo e nebbia scuri
            info = setLua(info, "groundAmbientColor", new float[]{0.22f, 0.24f, 0.34f});
            info = setLua(info, "groundDiffuseColor", new float[]{0.30f, 0.34f, 0.48f});
            info = setLua(info, "groundSpecularColor", new float[]{0.10f, 0.12f, 0.18f});
            info = setLua(info, "unitAmbientColor", new float[]{0.30f, 0.32f, 0.42f});
            info = setLua(info, "unitDiffuseColor", new float[]{0.45f, 0.50f, 0.65f});
            info = setLua(info, "sunColor", new float[]{0.55f, 0.62f, 0.85f});
            info = setLua(info, "skyColor", new float[]{0.03f, 0.04f, 0.09f});
            info = setLua(info, "fogColor", new float[]{0.04f, 0.05f, 0.10f});
            info = setLua(info, "cloudColor", new float[]{0.20f, 0.22f, 0.30f});
        }
        return info.replace("@TIDAL@", String.valueOf(this.options.tidal())).replace("@MINWIND@", String.valueOf(this.options.minWind())).replace("@MAXWIND@", String.valueOf(this.options.maxWind()));
    }

    /** Sostituisce nel mapinfo il valore di "key = {...}" (null = lascia il valore generato). */
    private static String setLua(String info, String key, float[] value) {
        if (value == null) {
            return info;
        }
        String text = String.format(Locale.ROOT, "{%.3f, %.3f, %.3f}", value[0], value[1], value[2]);
        return info.replaceFirst("(\\b" + key + " = )\\{[^}]*\\}", "$1" + java.util.regex.Matcher.quoteReplacement(text));
    }

    /** Sostituisce nel mapinfo il valore numerico di "key = n" (NaN = lascia il valore generato). */
    private static String setLua(String info, String key, float value) {
        if (Float.isNaN(value)) {
            return info;
        }
        return info.replaceFirst("(\\b" + key + " = )[-0-9.]+", "$1" + String.format(Locale.ROOT, "%.3f", value));
    }

    /**
     * La squadra di ogni punto di partenza. Di norma e' quella scelta nell'editor; se pero' l'export chiede un
     * numero di squadre diverso, i punti vengono ridivisi: in due meta' lungo la linea che li separa meglio,
     * oppure, per piu' squadre, a spicchi intorno al centro.
     */
    private Map<Spawn, Integer> resolveTeams(List<Spawn> spawns) {
        Map<Spawn, Integer> teams = new IdentityHashMap<>();
        for (Spawn spawn : spawns) {
            teams.put(spawn, spawn.getTeamID());
        }
        int wanted = this.options.numTeams();
        long existing = spawns.stream().map(Spawn::getTeamID).distinct().count();
        if (wanted < 2 || existing == wanted || spawns.size() % wanted != 0) {
            return teams;
        }
        float centreX = this.faWidth / 2.0f;
        float centreZ = this.faHeight / 2.0f;
        if (wanted == 2) {
            // quattro linee possibili: verticale, orizzontale e le due diagonali
            float[][] directions = {{1.0f, 0.0f}, {0.0f, 1.0f}, {0.7071f, 0.7071f}, {0.7071f, -0.7071f}};
            float widestGap = 0.0f;
            float[] best = null;
            for (float[] direction : directions) {
                int onOneSide = 0;
                float gap = Float.MAX_VALUE;
                for (Spawn spawn : spawns) {
                    float side = (spawn.getPosition().x() - centreX) * direction[0] + (spawn.getPosition().z() - centreZ) * direction[1];
                    if (side > 0.0f) {
                        onOneSide++;
                    }
                    gap = Math.min(gap, Math.abs(side));
                }
                if (onOneSide * 2 == spawns.size() && gap > widestGap) {
                    widestGap = gap;
                    best = direction;
                }
            }
            if (best != null) {
                for (Spawn spawn : spawns) {
                    float side = (spawn.getPosition().x() - centreX) * best[0] + (spawn.getPosition().z() - centreZ) * best[1];
                    teams.put(spawn, side > 0.0f ? 1 : 0);
                }
            }
            return teams;
        }
        List<Spawn> aroundCentre = new ArrayList<>(spawns);
        aroundCentre.sort(Comparator.comparingDouble(spawn -> StrictMath.atan2(spawn.getPosition().z() - centreZ, spawn.getPosition().x() - centreX)));
        int perTeam = spawns.size() / wanted;
        for (int i = 0; i < aroundCentre.size(); i++) {
            teams.put(aroundCentre.get(i), i / perTeam);
        }
        return teams;
    }

    /** Dove parte ogni squadra, in parole (per la descrizione della mappa); vuoto se le squadre sono troppe. */
    private String describeTeams(List<Spawn> spawns, Map<Spawn, Integer> teams) {
        List<Integer> teamIds = teams.values().stream().distinct().sorted().toList();
        if (teamIds.size() < 2 || teamIds.size() > 4) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (int number = 0; number < teamIds.size(); number++) {
            int teamId = teamIds.get(number);
            float sumX = 0.0f;
            float sumZ = 0.0f;
            int members = 0;
            for (Spawn spawn : spawns) {
                if (teams.getOrDefault(spawn, -1) == teamId) {
                    sumX += spawn.getPosition().x() / this.faWidth;
                    sumZ += spawn.getPosition().z() / this.faHeight;
                    members++;
                }
            }
            float x = sumX / members;
            float z = sumZ / members;
            String vertical = z < 0.4f ? "north" : z > 0.6f ? "south" : "";
            String horizontal = x < 0.4f ? "west" : x > 0.6f ? "east" : "";
            String area = (vertical + " " + horizontal).trim();
            parts.add(" team " + (number + 1) + " " + (area.isEmpty() ? "center" : area));
        }
        return "Start areas:" + String.join(",", parts) + ".";
    }

    private static float clampColor(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    /** Porta un vettore a lunghezza 1. */
    private static void normalize(float[] vector) {
        float length = (float) StrictMath.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        for (int i = 0; i < 3; i++) {
            vector[i] /= length;
        }
    }

    /**
     * Il nome con cui la mappa compare in gioco: quello scelto dall'utente con la versione, oppure quello
     * generato (i nomi lunghi del generatore vengono accorciati a "Map_" piu' la parte finale).
     */
    private String getDisplayName() {
        if (this.options.mapName() != null) {
            return this.options.mapName() + " " + this.options.mapVersion();
        }
        String name = this.map.getName();
        String generatorPrefix = "neroxis_map_generator_";
        if (name.startsWith(generatorPrefix)) {
            int afterVersion = name.indexOf('_', generatorPrefix.length());
            if (afterVersion > 0 && afterVersion < name.length() - 1) {
                return GENERATED_PREFIX + name.substring(afterVersion + 1);
            }
        }
        return name.isBlank() ? "Map_map" : name;
    }

    private static void saveLobbyMinimaps(Path folderPath, String archiveName, String displayName, BufferedImage previewImage, byte[] previewPngBytes) {
        Set<Path> dataDirs = new LinkedHashSet<>();
        if (folderPath != null) {
            Path parent = folderPath.getParent();
            if (parent != null && "maps".equalsIgnoreCase(folderPath.getFileName().toString())) {
                dataDirs.add(parent);
            }
        }
        String progFiles = System.getenv("ProgramFiles");
        if (progFiles != null && !progFiles.isBlank()) {
            dataDirs.add(Path.of(progFiles, "Beyond-All-Reason", "data"));
        }
        dataDirs.add(Path.of("C:\\Program Files\\Beyond-All-Reason\\data"));
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            dataDirs.add(Path.of(localAppData, "Programs", "Beyond-All-Reason", "data"));
        }

        String chobbyName = displayName.replace(" ", "_");
        ByteArrayOutputStream jpgBytes = new ByteArrayOutputStream();
        byte[] previewJpgBytes = null;
        try {
            ImageIO.write(previewImage, "jpg", jpgBytes);
            previewJpgBytes = jpgBytes.toByteArray();
        } catch (Throwable ignored) {
        }

        for (Path dataDir : dataDirs) {
            if (!Files.isDirectory(dataDir)) {
                continue;
            }
            try {
                Path thumbDir = dataDir.resolve("LuaMenu/configs/gameconfig/byar/minimapthumbnail");
                Path overrideDir = dataDir.resolve("LuaMenu/configs/gameconfig/byar/minimapoverride");
                Path imagesMinimapsDir = dataDir.resolve("LuaMenu/Images/Minimaps");

                Files.createDirectories(thumbDir);
                Files.createDirectories(overrideDir);
                Files.createDirectories(imagesMinimapsDir);

                String[] names = {chobbyName, archiveName, displayName};
                for (String n : names) {
                    Files.write(thumbDir.resolve(n + ".png"), previewPngBytes);
                    if (previewJpgBytes != null) {
                        Files.write(overrideDir.resolve(n + ".jpg"), previewJpgBytes);
                        Files.write(imagesMinimapsDir.resolve(n + ".jpg"), previewJpgBytes);
                    }
                    Files.write(imagesMinimapsDir.resolve(n + ".png"), previewPngBytes);
                }
            } catch (Throwable t) {
                System.err.println("Warning: Could not save lobby minimap preview: " + t.getMessage());
            }
        }
    }

    /** Un nome adatto a un file: minuscolo, con solo lettere, cifre, punto, trattino e sottolineatura. */
    private static String toFileName(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
    }
}
