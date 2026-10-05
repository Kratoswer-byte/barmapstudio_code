package com.faforever.neroxis.exporter.bar;

/**
 * Opzioni aggiuntive della mappa esportata (finestra "Opzioni mappa" dell'editor).
 * Quelle che il motore legge da mapinfo.lua vengono scritte li'; le altre diventano
 * un gadget dentro l'archivio della mappa (luarules/gadgets/map_options.lua).
 */
public final class MapExtraOptions {
    /** Tutta la mappa sempre visibile a tutte le squadre (niente nebbia di guerra). */
    public boolean noFogOfWar = false;
    /** Metallo ed energia regalati a ogni squadra a inizio partita. */
    public int startMetalBonus = 0;
    public int startEnergyBonus = 0;
    /** Il terreno non si deforma con le esplosioni. */
    public boolean notDeformable = false;
    /** Gravita' (BAR usa 100 sulle mappe generate; piu' bassa = proiettili piu' lenti a cadere). */
    public int gravity = 100;
    /** Danno al secondo per le unita' in acqua (0 = acqua normale; >0 = acido/lava). */
    public int waterDamage = 0;
    /** L'acqua non viene disegnata (mappe nello spazio). */
    public boolean voidWater = false;
    public int minWind = 3;
    public int maxWind = 16;
    public int tidal = 18;
    /** Raggio degli estrattori di metallo, in elmi. */
    public int extractorRadius = 100;
    /** Mappa notturna: luce di terreno e unita' bassa e bluastra, cielo e nebbia scuri. */
    public boolean night = false;
    /** Lava o acido al posto dell'acqua: 0 = nessuno, 1 = lava, 2 = acido (sistema lava di BAR, mapconfig/lava.lua). */
    public int lavaMode = 0;
    /** Quota della superficie di lava, in elmi. */
    public int lavaLevel = 1;
    /** Danno al secondo per le unita' nella lava. */
    public int lavaDamage = 100;
    /** Marea della lava: di quanti elmi sale e scende (0 = ferma) e ogni quanti secondi. */
    public int lavaTide = 0;
    public int lavaPeriod = 120;
    /**
     * Come si parte nelle partite avviate dallo Studio: false = posizioni statiche (ogni squadra esattamente
     * sulla sua base dell'editor), true = zona di partenza (ogni giocatore sceglie il punto dentro la zona
     * della sua squadra, che contiene tutte le sue basi).
     */
    public boolean startZones = false;
    /** Di quanto la zona di partenza si allarga oltre le basi della squadra, in percentuale del lato della mappa. */
    public int startZoneMargin = 9;

    /** File mapconfig/lava.lua letto dal sistema lava di BAR. */
    String buildLavaConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Lava configuration written by BAR Map Studio\n");
        sb.append("return {\n");
        sb.append("\tlevel = ").append(lavaLevel).append(",\n");
        sb.append("\tgrow = 0,\n");
        sb.append("\tdamage = ").append(Math.max(0, lavaDamage)).append(",\n");
        if (lavaMode == 2) {
            sb.append("\tcolorCorrection = \"vec3(0.15, 1.0, 0.45)\",\n");
            sb.append("\tcoastColor = \"vec3(0.6, 0.8, 0.2)\",\n");
            sb.append("\tfogColor = \"vec3(0.4, 1.0, 0.3)\",\n");
        }
        // tideRhythm: { quota in elmi, velocita' in elmi/secondo, attesa in secondi }
        if (lavaTide > 0) {
            int wait = Math.max(5, lavaPeriod / 2);
            float speed = Math.max(0.2f, lavaTide / 10.0f);
            sb.append("\ttideRhythm = { { ").append(lavaLevel + lavaTide).append(", ").append(speed).append(", ").append(wait)
                .append(" }, { ").append(lavaLevel).append(", ").append(speed).append(", ").append(wait).append(" } },\n");
        } else {
            sb.append("\ttideRhythm = { { ").append(lavaLevel).append(", 0.1, 3600 } },\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static final MapExtraOptions CURRENT = new MapExtraOptions();

    public static MapExtraOptions get() {
        return CURRENT;
    }

    boolean needsGadget() {
        return noFogOfWar || startMetalBonus > 0 || startEnergyBonus > 0;
    }

    /** Gadget sincronizzato con le opzioni che mapinfo.lua non puo' esprimere. */
    String buildGadget() {
        StringBuilder sb = new StringBuilder();
        sb.append("if not gadgetHandler:IsSyncedCode() then return end\n\n");
        sb.append("function gadget:GetInfo()\n");
        sb.append("    return {\n");
        sb.append("        name = \"Map Options\",\n");
        sb.append("        desc = \"Map-wide options set in BAR Map Studio\",\n");
        sb.append("        author = \"BAR Map Studio\",\n");
        sb.append("        date = \"2026\",\n");
        sb.append("        license = \"GNU GPL, v2 or later\",\n");
        sb.append("        layer = 0,\n");
        sb.append("        enabled = true\n");
        sb.append("    }\n");
        sb.append("end\n\n");
        sb.append("local NO_FOG_OF_WAR = ").append(noFogOfWar).append("\n");
        sb.append("local START_METAL_BONUS = ").append(Math.max(0, startMetalBonus)).append("\n");
        sb.append("local START_ENERGY_BONUS = ").append(Math.max(0, startEnergyBonus)).append("\n\n");
        sb.append("local function RevealMap()\n");
        sb.append("    for _, allyTeamID in ipairs(Spring.GetAllyTeamList()) do\n");
        sb.append("        Spring.SetGlobalLos(allyTeamID, true)\n");
        sb.append("    end\n");
        sb.append("end\n\n");
        sb.append("local function Give(teamID, resource, storageKey, amount)\n");
        sb.append("    if amount <= 0 then return end\n");
        sb.append("    local current, storage = Spring.GetTeamResources(teamID, resource)\n");
        sb.append("    if current and storage and storage < current + amount then\n");
        sb.append("        Spring.SetTeamResource(teamID, storageKey, current + amount)\n");
        sb.append("    end\n");
        sb.append("    Spring.AddTeamResource(teamID, resource, amount)\n");
        sb.append("end\n\n");
        sb.append("function gadget:Initialize()\n");
        sb.append("    if NO_FOG_OF_WAR then RevealMap() end\n");
        sb.append("end\n\n");
        sb.append("function gadget:GameStart()\n");
        sb.append("    if NO_FOG_OF_WAR then RevealMap() end\n");
        sb.append("    local gaiaTeamID = Spring.GetGaiaTeamID()\n");
        sb.append("    for _, teamID in ipairs(Spring.GetTeamList()) do\n");
        sb.append("        if teamID ~= gaiaTeamID then\n");
        sb.append("            Give(teamID, \"metal\", \"ms\", START_METAL_BONUS)\n");
        sb.append("            Give(teamID, \"energy\", \"es\", START_ENERGY_BONUS)\n");
        sb.append("        end\n");
        sb.append("    end\n");
        sb.append("end\n");
        return sb.toString();
    }
}
