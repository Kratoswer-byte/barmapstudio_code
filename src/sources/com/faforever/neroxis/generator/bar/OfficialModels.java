package com.faforever.neroxis.generator.bar;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Accesso pubblico ai modelli ufficiali BAR (S3O+DDS dal gioco installato).
 * Usato sia dalla vista 3D che dall'esportatore per le varianti scalate
 * (unita ufficiali modificate diventano nuovi tipi con S3O proprio).
 */
public final class OfficialModels {
    private OfficialModels() {}

    private static final Pattern OBJECT_NAME = Pattern.compile("(?im)^\\s*objectname\\s*=\\s*\"([^\"]+)\"");
    private static final Pattern SCRIPT_NAME = Pattern.compile("(?im)^\\s*script\\s*=\\s*\"([^\"]+)\"");

    /** id unita' -> objectname dichiarato nel suo file (molte unita' riusano il modello di un'altra). */
    private static Map<String, String> objectNames;
    /** id unita' -> script dichiarato nel suo file (es. "Units/ARMFLASH.cob"). */
    private static Map<String, String> scriptNames;
    /** percorso in minuscolo -> percorso reale nell'archivio (i nomi dei file hanno maiuscole diverse). */
    private static Map<String, String> modelFiles;
    /** id unita' -> percorso del suo file di definizione nell'archivio del gioco. */
    private static Map<String, String> unitFiles;
    private static Path indexedFrom;

    private static synchronized void ensureIndex(Path data) {
        if (objectNames != null && data.equals(indexedFrom)) return;
        Map<String, String> names = new HashMap<>();
        Map<String, String> scripts = new HashMap<>();
        Map<String, String> files = new HashMap<>();
        Map<String, String> defs = new HashMap<>();
        try {
            Map<String, byte[]> units = BarInstall.readGameFiles(data, file -> {
                String lower = file.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".s3o") || lower.startsWith("scripts/")) {
                    files.put(lower, file);
                    return false;
                }
                return lower.startsWith("units/") && lower.endsWith(".lua");
            });
            for (Map.Entry<String, byte[]> entry : units.entrySet()) {
                String path = entry.getKey();
                String id = path.substring(path.lastIndexOf('/') + 1, path.length() - 4).toLowerCase(Locale.ROOT);
                defs.put(id, path);
                String text = new String(entry.getValue(), StandardCharsets.UTF_8);
                Matcher matcher = OBJECT_NAME.matcher(text);
                if (matcher.find()) {
                    names.put(id, matcher.group(1).replace('\\', '/'));
                }
                Matcher script = SCRIPT_NAME.matcher(text);
                if (script.find()) {
                    scripts.put(id, script.group(1).replace('\\', '/'));
                }
            }
        } catch (RuntimeException ignored) {
        }
        objectNames = names;
        scriptNames = scripts;
        modelFiles = files;
        unitFiles = defs;
        indexedFrom = data;
    }

    private static Path indexedData() {
        Path data;
        try {
            data = Platform.defaultData();
        } catch (Exception ex) {
            return null;
        }
        if (data == null) return null;
        try {
            ensureIndex(data);
        } catch (Exception ignored) {
        }
        return data;
    }

    /** Legge un file del gioco senza badare a maiuscole e minuscole del percorso. */
    private static byte[] readIgnoreCase(Path data, String file) {
        Map<String, String> files;
        synchronized (OfficialModels.class) {
            files = modelFiles;
        }
        String real = files != null ? files.get(file.toLowerCase(Locale.ROOT)) : null;
        return BarInstall.readGameBytes(data, real != null ? real : file);
    }

    /** Byte del modello S3O di un'unita', cercato prima con il nome dichiarato nel suo file. */
    public static byte[] modelBytes(String unitId) {
        if (unitId == null || unitId.isBlank()) return null;
        String id = unitId.toLowerCase(Locale.ROOT);
        Path data = indexedData();
        if (data == null) return null;
        String declared;
        synchronized (OfficialModels.class) {
            declared = objectNames != null ? objectNames.get(id) : null;
        }
        String[] cands = {
            declared != null ? "objects3d/" + declared : null,
            declared != null && !declared.toLowerCase(Locale.ROOT).endsWith(".s3o") ? "objects3d/" + declared + ".s3o" : null,
            "objects3d/units/" + id + ".s3o",
            "objects3d/" + id + ".s3o"};
        for (String c : cands) {
            if (c == null) continue;
            try {
                byte[] bytes = readIgnoreCase(data, c);
                if (bytes != null && bytes.length > 52) return bytes;
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static S3OParser.S3OModel loadFull(String unitId) {
        try {
            byte[] bytes = modelBytes(unitId);
            if (bytes == null) return null;
            S3OParser.S3OModel sm = S3OParser.parseWithFaceUV(bytes);
            return sm != null && sm.model.getFaceCount() > 0 ? sm : null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static ObjModel3D load(String unitId) {
        S3OParser.S3OModel sm = loadFull(unitId);
        return sm != null ? sm.model : null;
    }

    /** Texture del gioco (cartella unittextures) al livello di dettaglio chiesto, o null. */
    public static java.awt.image.BufferedImage unitTexture(String name, int mipLevel) {
        Path data = indexedData();
        if (data == null || name == null || name.isBlank()) return null;
        for (String candidate : new String[]{"unittextures/" + name, name}) {
            try {
                byte[] bytes = BarInstall.readGameBytes(data, candidate);
                if (bytes != null && bytes.length > 128) {
                    java.awt.image.BufferedImage img = ModelTextureCache.decodeGameBytes(bytes, mipLevel);
                    if (img != null) return img;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /** Nomi dei pezzi del modello, nell'ordine in cui compaiono: sono quelli che uno script puo' muovere. */
    public static List<String> pieceNames(String unitId) {
        try {
            byte[] bytes = modelBytes(unitId);
            return bytes == null ? List.of() : S3OParser.pieceNames(bytes);
        } catch (Exception ex) {
            return List.of();
        }
    }

    /** Testo del file di definizione di un'unita' di BAR (units/.../<id>.lua), o null. */
    public static String unitDefText(String unitId) {
        Path data = indexedData();
        if (data == null || unitId == null) return null;
        String path;
        synchronized (OfficialModels.class) {
            path = unitFiles != null ? unitFiles.get(unitId.toLowerCase(Locale.ROOT)) : null;
        }
        if (path == null) return null;
        try {
            byte[] bytes = BarInstall.readGameBytes(data, path);
            return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    /** Script dichiarato dall'unita', come scritto nel suo file (es. "Units/ARMFLASH.cob"), o null. */
    public static String scriptName(String unitId) {
        if (unitId == null || indexedData() == null) return null;
        synchronized (OfficialModels.class) {
            return scriptNames != null ? scriptNames.get(unitId.toLowerCase(Locale.ROOT)) : null;
        }
    }

    /**
     * Sorgente BOS dello script compilato dell'unita' (il gioco lo distribuisce accanto al file .cob), o null.
     * Non e' Lua: serve come riferimento per riscrivere il comportamento.
     */
    public static String scriptBosSource(String unitId) {
        String name = scriptName(unitId);
        if (name == null) return null;
        int dot = name.lastIndexOf('.');
        String bos = (dot > 0 ? name.substring(0, dot) : name) + ".bos";
        try {
            byte[] bytes = readIgnoreCase(indexedData(), "scripts/" + bos);
            return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Script Lua di partenza per una copia personalizzata. Se l'unita' ha gia' uno script Lua e' quello,
     * identico. Altrimenti e' una bozza funzionante costruita sui pezzi del modello (mira e sparo di base),
     * seguita dal sorgente originale come commento, da usare come guida per riportare le animazioni.
     */
    public static String starterLua(String unitId, String unitName) {
        String lua = scriptLua(unitId);
        if (lua != null) return lua;
        List<String> pieces = pieceNames(unitId);
        String script = scriptName(unitId);
        StringBuilder sb = new StringBuilder();
        sb.append("-- Draft Lua script for a custom copy of ").append(unitName).append(" (").append(unitId).append(").\n");
        sb.append("-- The original unit runs a compiled script (").append(script != null ? script : "none").append("), which is not Lua.\n");
        sb.append("-- This draft declares the pieces of the model and gives the unit basic aiming and firing.\n");
        sb.append("-- The original source is at the bottom, as a comment, to help you port its animations.\n\n");
        boolean plainNames = pieces.size() <= 150;
        for (String p : pieces) {
            if (!p.matches("[A-Za-z_][A-Za-z0-9_]*") || LUA_KEYWORDS.contains(p)) plainNames = false;
        }
        if (pieces.isEmpty()) {
            sb.append("local base = piece(\"base\")\n");
        } else if (plainNames) {
            sb.append("local ").append(String.join(", ", pieces)).append(" = piece(\"").append(String.join("\", \"", pieces)).append("\")\n");
        } else {
            sb.append("-- piece names that are not valid Lua names are reached as p[\"name\"]\n");
            sb.append("local p = {}\n");
            sb.append("for _, name in ipairs({ \"").append(String.join("\", \"", pieces)).append("\" }) do\n    p[name] = piece(name)\nend\n");
        }
        String root = pieces.isEmpty() ? "base" : pieces.get(0);
        String turret = pick(pieces, "turret", "aimy1", "torso", "aimy", "head");
        String sleeve = pick(pieces, "sleeve", "aimx1", "aimx", "barrel", "gun");
        String flare = pick(pieces, "flare", "fire", "emit", "muzzle");
        java.util.function.Function<String, String> ref = name -> plainNamesFor(pieces) ? name : "p[\"" + name + "\"]";
        String aimFrom = ref.apply(turret != null ? turret : root);
        String query = ref.apply(flare != null ? flare : sleeve != null ? sleeve : turret != null ? turret : root);
        sb.append("\nfunction script.Create()\nend\n\n");
        sb.append("function script.AimFromWeapon(num)\n    return ").append(aimFrom).append("\nend\n\n");
        sb.append("function script.QueryWeapon(num)\n    return ").append(query).append("\nend\n\n");
        sb.append("function script.AimWeapon(num, heading, pitch)\n");
        if (turret != null) {
            sb.append("    Turn(").append(ref.apply(turret)).append(", y_axis, heading, math.rad(250))\n");
            if (sleeve != null) {
                sb.append("    Turn(").append(ref.apply(sleeve)).append(", x_axis, -pitch, math.rad(240))\n");
            }
            sb.append("    WaitForTurn(").append(ref.apply(turret)).append(", y_axis)\n");
        }
        sb.append("    return true\nend\n\n");
        sb.append("function script.FireWeapon(num)\nend\n\n");
        sb.append("function script.Killed(recentDamage, maxHealth)\n    return 1\nend\n");
        String bos = scriptBosSource(unitId);
        if (bos != null) {
            sb.append("\n--[==[ ORIGINAL SOURCE (BOS, reference only: this part is not executed)\n\n");
            sb.append(bos.replace("]==]", "] ==]").replace("\r", ""));
            sb.append("\n]==]\n");
        }
        return sb.toString();
    }

    private static final java.util.Set<String> LUA_KEYWORDS = java.util.Set.of("and", "break", "do", "else", "elseif", "end", "false", "for",
        "function", "if", "in", "local", "nil", "not", "or", "repeat", "return", "then", "true", "until", "while");

    private static boolean plainNamesFor(List<String> pieces) {
        if (pieces.isEmpty()) return true;
        if (pieces.size() > 150) return false;
        for (String p : pieces) {
            if (!p.matches("[A-Za-z_][A-Za-z0-9_]*") || LUA_KEYWORDS.contains(p)) return false;
        }
        return true;
    }

    /** Primo pezzo il cui nome contiene una delle parole, nell'ordine di preferenza dato. */
    private static String pick(List<String> pieces, String... words) {
        for (String word : words) {
            for (String p : pieces) {
                if (p.toLowerCase(Locale.ROOT).contains(word)) return p;
            }
        }
        return null;
    }

    /**
     * Testo dello script dell'unita' se e' scritto in Lua, altrimenti null: la maggior parte delle unita' di
     * BAR usa script COB compilati, che non si possono leggere come testo.
     */
    public static String scriptLua(String unitId) {
        String name = scriptName(unitId);
        if (name == null || !name.toLowerCase(Locale.ROOT).endsWith(".lua")) return null;
        try {
            byte[] bytes = readIgnoreCase(indexedData(), "scripts/" + name);
            return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }
}
