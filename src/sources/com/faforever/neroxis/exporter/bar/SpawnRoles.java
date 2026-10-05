package com.faforever.neroxis.exporter.bar;

import com.faforever.neroxis.map.SCMap;
import com.faforever.neroxis.map.Spawn;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * Ruoli consigliati dei punti di partenza (front, air, sea, tech...), cioe' i cerchi con la scritta
 * che BAR mostra prima della partita. Il gioco non li legge dalla mappa: li riceve nella modoption
 * "mapmetadata_startpos" (JSON compresso zlib e codificato base64), che per le mappe ufficiali arriva
 * dall'archivio maps-metadata di BAR. Qui vengono scritti nello script di avvio della partita e,
 * come promemoria, in un file YAML dentro la mappa nel formato di maps-metadata.
 */
public final class SpawnRoles {
    public static final String[] ROLES = {"front", "air", "sea", "tech", "front/tech", "air/front"};

    private static WeakReference<SCMap> mapRef = new WeakReference<>(null);
    private static Map<Spawn, String> roles = new IdentityHashMap<>();

    private SpawnRoles() {}

    private static Map<Spawn, String> rolesOf(SCMap map) {
        if (mapRef.get() != map) {
            mapRef = new WeakReference<>(map);
            roles = new IdentityHashMap<>();
        }
        return roles;
    }

    /** La rigenerazione del terreno crea una mappa nuova con gli stessi spawn: i ruoli la seguono. */
    public static synchronized void carryOver(SCMap from, SCMap to) {
        if (from != null && to != null && mapRef.get() == from) {
            mapRef = new WeakReference<>(to);
        }
    }

    /** Ruolo assegnato allo spawn, oppure null. */
    public static synchronized String get(SCMap map, Spawn spawn) {
        return map == null ? null : rolesOf(map).get(spawn);
    }

    public static synchronized void set(SCMap map, Spawn spawn, String role) {
        if (map == null) return;
        if (role == null || role.isBlank()) rolesOf(map).remove(spawn);
        else rolesOf(map).put(spawn, role);
    }

    /** Ruolo di partenza di ogni base: "front" per tutte. I ruoli diversi si scelgono a mano o con "Auto". */
    public static synchronized void assignAllFront(SCMap map) {
        if (map == null) return;
        Map<Spawn, String> target = rolesOf(map);
        target.clear();
        for (Spawn spawn : map.getSpawns()) {
            target.put(spawn, "front");
        }
    }

    /**
     * Assegna i ruoli in automatico, squadra per squadra: lo spawn piu' lontano dal centro e' "air",
     * il secondo piu' lontano "tech" (da 5 giocatori in su), quelli con molta acqua attorno "sea",
     * tutti gli altri "front".
     */
    public static synchronized void autoAssign(SCMap map) {
        if (map == null) return;
        Map<Spawn, String> target = rolesOf(map);
        target.clear();
        float cx = map.getWidth() / 2.0f;
        float cz = map.getHeight() / 2.0f;
        List<Integer> teamIds = map.getSpawns().stream().map(Spawn::getTeamID).distinct().toList();
        for (int teamId : teamIds) {
            List<Spawn> team = new ArrayList<>(map.getSpawns().stream().filter(s -> s.getTeamID() == teamId).toList());
            team.sort(Comparator.comparingDouble((Spawn s) -> -Math.hypot(s.getPosition().x() - cx, s.getPosition().z() - cz)));
            int seaLeft = 2;
            for (int i = 0; i < team.size(); i++) {
                Spawn spawn = team.get(i);
                String role = "front";
                if (i == 0 && team.size() >= 3) {
                    role = "air";
                } else if (i == 1 && team.size() >= 5) {
                    role = "tech";
                } else if (seaLeft > 0 && waterAround(map, spawn) > 0.3f) {
                    role = "sea";
                    seaLeft--;
                }
                target.put(spawn, role);
            }
        }
    }

    /** Frazione di terreno sotto il livello dell'acqua attorno allo spawn (0..1). */
    private static float waterAround(SCMap map, Spawn spawn) {
        if (map.getBiome() == null || map.getBiome().waterSettings() == null || !map.getBiome().waterSettings().waterPresent()) {
            return 0.0f;
        }
        Raster raster = map.getHeightmap().getRaster();
        float sea = map.getBiome().waterSettings().elevation();
        float scale = map.getHeightMapScale();
        int radius = 14;
        int wet = 0;
        int total = 0;
        for (int dz = -radius; dz <= radius; dz += 2) {
            for (int dx = -radius; dx <= radius; dx += 2) {
                int x = Math.round(spawn.getPosition().x()) + dx;
                int z = Math.round(spawn.getPosition().z()) + dz;
                if (x < 0 || z < 0 || x >= raster.getWidth() || z >= raster.getHeight()) continue;
                total++;
                if (raster.getSample(x, z, 0) * scale < sea) wet++;
            }
        }
        return total == 0 ? 0.0f : wet / (float) total;
    }

    // ---------------------------------------------------------------------------------------
    // Export
    // ---------------------------------------------------------------------------------------

    /** Dati pronti per l'export: spawn gia' in elmi, raggruppati per squadra alleata. */
    static final class Guide {
        final List<float[]> positions = new ArrayList<>(); // x, z in elmi (P1, P2, ...)
        final List<List<int[]>> sides = new ArrayList<>(); // per squadra: {indice posizione}
        final List<String> roleOfPosition = new ArrayList<>();

        boolean usable() {
            if (sides.size() < 2) return false;
            for (List<int[]> side : sides) {
                if (side.size() < 2 || side.size() != sides.get(0).size()) return false;
            }
            return true;
        }

        private int priority(String role) {
            return switch (role) {
                case "front" -> 0;
                case "front/tech" -> 1;
                case "tech" -> 2;
                case "air/front" -> 3;
                case "air" -> 4;
                default -> 5;
            };
        }

        /** Una configurazione per ogni numero di giocatori per squadra, da 1 al massimo. */
        String toJson() {
            StringBuilder sb = new StringBuilder("{\"positions\":{");
            for (int i = 0; i < positions.size(); i++) {
                sb.append(i > 0 ? "," : "").append(String.format(Locale.ROOT, "\"P%d\":{\"x\":%d,\"y\":%d}", i + 1, Math.round(positions.get(i)[0]), Math.round(positions.get(i)[1])));
            }
            sb.append("},\"team\":[");
            int perTeam = sides.get(0).size();
            for (int k = perTeam; k >= 1; k--) {
                sb.append(k < perTeam ? "," : "").append("{\"playersPerTeam\":").append(k).append(",\"teamCount\":").append(sides.size()).append(",\"sides\":[");
                for (int s = 0; s < sides.size(); s++) {
                    List<int[]> ordered = new ArrayList<>(sides.get(s));
                    ordered.sort(Comparator.comparingInt((int[] p) -> priority(roleOfPosition.get(p[0]))));
                    sb.append(s > 0 ? "," : "").append("{\"starts\":[");
                    for (int i = 0; i < k; i++) {
                        int position = ordered.get(i)[0];
                        sb.append(i > 0 ? "," : "").append("{\"role\":\"").append(roleOfPosition.get(position)).append("\",\"spawnPoint\":\"P").append(position + 1).append("\"}");
                    }
                    sb.append("]}");
                }
                sb.append("]}");
            }
            return sb.append("]}").toString();
        }

        /** Valore della modoption mapmetadata_startpos: base64(zlib(json)). */
        String toModoption() {
            byte[] json = toJson().getBytes(StandardCharsets.UTF_8);
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(json);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (!deflater.finished()) {
                out.write(buffer, 0, deflater.deflate(buffer));
            }
            deflater.end();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        }

        /** Stessi dati nel formato dell'archivio maps-metadata di BAR (chiave startPos). */
        String toYaml() {
            StringBuilder sb = new StringBuilder();
            sb.append("# Start position guide for BAR maps-metadata (https://github.com/beyond-all-reason/maps-metadata)\n");
            sb.append("# Paste under the map entry in map_list.yaml to show the role circles in multiplayer lobbies.\n");
            sb.append("startPos:\n  positions:\n");
            for (int i = 0; i < positions.size(); i++) {
                sb.append("    P").append(i + 1).append(":\n      x: ").append(Math.round(positions.get(i)[0])).append("\n      y: ").append(Math.round(positions.get(i)[1])).append("\n");
            }
            sb.append("  team:\n    - playersPerTeam: ").append(sides.get(0).size()).append("\n      sides:\n");
            for (List<int[]> side : sides) {
                sb.append("        - starts:\n");
                for (int[] p : side) {
                    sb.append("            - role: ").append(roleOfPosition.get(p[0])).append("\n              spawnPoint: P").append(p[0] + 1).append("\n");
                }
            }
            sb.append("      teamCount: ").append(sides.size()).append("\nstartPosActive: true\n");
            return sb.toString();
        }
    }
}
