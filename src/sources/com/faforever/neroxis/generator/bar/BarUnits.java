package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.util.dds.DDSReader;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Le unita' di Beyond All Reason lette dall'installazione dell'utente: una voce per ogni file
 * {@code units/.../<id>.lua} del gioco, con il nome e la descrizione presi dal file di lingua inglese.
 */
final class BarUnits {
    private static final List<String> FACTION_ORDER = List.of("Armada", "Cortex", "Legion", "Other");
    private static final Color ARMADA = new Color(74, 158, 255);
    private static final Color CORTEX = new Color(255, 84, 72);
    private static final Color LEGION = new Color(63, 200, 90);
    private static final Color NEUTRAL = new Color(240, 244, 250);
    /** Valori numerici e di testo scritti a inizio riga nella definizione di un'unita'. */
    private static final Pattern VALUE = Pattern.compile(
        "(?m)^\\s*(\\w+)\\s*=\\s*(?:\"([^\"]*)\"|([0-9]+(?:\\.[0-9]+)?))\\s*,?\\s*$");

    /** Un'unita' del gioco. */
    record Unit(String id, String name, String tooltip, String faction, int tier, int metal, int energy,
                int buildTime, int health, float speed, String folder, String picture) {}

    private BarUnits() {}

    static Color factionColour(String faction) {
        if ("Armada".equals(faction)) {
            return ARMADA;
        }
        if ("Cortex".equals(faction)) {
            return CORTEX;
        }
        return "Legion".equals(faction) ? LEGION : NEUTRAL;
    }

    /** Tutte le unita' del gioco installato, in ordine di fazione, livello e nome. Lista vuota se BAR non c'e'. */
    static List<Unit> read(Path data) {
        List<Unit> units = new ArrayList<>();
        try {
            Map<String, byte[]> files = BarGameFiles.readAll(data,
                file -> (file.startsWith("units/") && file.endsWith(".lua")) || file.equals("language/en/units.json"));
            Map<String, String> names = new HashMap<>();
            Map<String, String> tooltips = new HashMap<>();
            byte[] language = files.remove("language/en/units.json");
            if (language != null) {
                try {
                    JsonNode root = new ObjectMapper().readTree(new String(language, StandardCharsets.UTF_8)).path("units");
                    root.path("names").properties().forEach(entry -> names.put(entry.getKey(), entry.getValue().asString("")));
                    root.path("descriptions").properties().forEach(entry -> tooltips.put(entry.getKey(), entry.getValue().asString("")));
                } catch (RuntimeException ignored) {
                    // senza il file di lingua restano i nomi scritti nelle definizioni
                }
            }
            for (Map.Entry<String, byte[]> file : files.entrySet()) {
                String path = file.getKey();
                int slash = path.lastIndexOf('/');
                String id = path.substring(slash + 1, path.length() - ".lua".length()).toLowerCase(Locale.ROOT);
                String folder = slash > "units/".length() ? path.substring("units/".length(), slash) : "";
                String text = new String(file.getValue(), StandardCharsets.UTF_8).replaceAll("(?m)--.*$", "");
                Map<String, String> values = new HashMap<>();
                Matcher matcher = VALUE.matcher(text);
                while (matcher.find()) {
                    // vale la prima occorrenza: quelle successive appartengono ad armi e relitti
                    values.putIfAbsent(matcher.group(1).toLowerCase(Locale.ROOT), matcher.group(2) != null ? matcher.group(2) : matcher.group(3));
                }
                if (!values.containsKey("metalcost") && !values.containsKey("buildcostmetal")) {
                    continue; // non e' un'unita' costruibile
                }
                String lowerFolder = folder.toLowerCase(Locale.ROOT);
                String faction = id.startsWith("arm") ? "Armada" : id.startsWith("cor") ? "Cortex" : id.startsWith("leg") ? "Legion" : "Other";
                if (lowerFolder.contains("scav") || lowerFolder.contains("raptor") || lowerFolder.contains("other") || id.endsWith("_scav")) {
                    faction = "Other";
                }
                units.add(new Unit(id,
                    names.getOrDefault(id, first(values, id, "i18n_en_humanname", "name")),
                    tooltips.getOrDefault(id, first(values, "", "i18n_en_tooltip", "description")),
                    faction,
                    (int) number(values, 1, "techlevel"),
                    (int) number(values, 0, "metalcost", "buildcostmetal"),
                    (int) number(values, 0, "energycost", "buildcostenergy"),
                    (int) number(values, 0, "buildtime"),
                    (int) number(values, 0, "health", "maxdamage"),
                    number(values, 0, "speed", "maxvelocity"),
                    values.getOrDefault("subfolder", folder),
                    values.getOrDefault("buildpic", "")));
            }
        } catch (RuntimeException e) {
            return List.of();
        }
        units.sort(Comparator.comparingInt((Unit unit) -> {
            int order = FACTION_ORDER.indexOf(unit.faction());
            return order < 0 ? FACTION_ORDER.size() : order;
        }).thenComparingInt(Unit::tier).thenComparing(Unit::name, String.CASE_INSENSITIVE_ORDER));
        return units;
    }

    /** L'immagine dell'unita' mostrata nei menu di costruzione del gioco, o null se non c'e'. */
    static BufferedImage readPicture(Path data, Unit unit) {
        for (String candidate : new String[]{unit.picture(), unit.id() + ".dds", unit.id() + ".png"}) {
            if (candidate.isEmpty()) {
                continue;
            }
            byte[] bytes = BarGameFiles.read(data, "unitpics/" + candidate);
            if (bytes == null) {
                continue;
            }
            try {
                if (candidate.toLowerCase(Locale.ROOT).endsWith(".dds")) {
                    int[] pixels = DDSReader.read(bytes, DDSReader.ARGB, 0);
                    if (pixels != null) {
                        int width = DDSReader.getWidth(bytes);
                        int height = DDSReader.getHeight(bytes);
                        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        image.setRGB(0, 0, width, height, pixels, 0, width);
                        return image;
                    }
                } else {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
                    if (image != null) {
                        return image;
                    }
                }
            } catch (Exception ignored) {
                // immagine illeggibile: si prova la successiva
            }
        }
        return null;
    }

    private static String first(Map<String, String> values, String fallback, String... keys) {
        for (String key : keys) {
            String value = values.get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return fallback;
    }

    private static float number(Map<String, String> values, float fallback, String... keys) {
        for (String key : keys) {
            try {
                String value = values.get(key);
                if (value != null) {
                    return Float.parseFloat(value);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return fallback;
    }
}
