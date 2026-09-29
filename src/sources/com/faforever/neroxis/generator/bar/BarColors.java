package com.faforever.neroxis.generator.bar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/BarColors.class */
final class BarColors {
    private static final Color SCAVENGER = new Color(104, 9, 161);
    private static final Color RAPTOR = new Color(204, 137, 20);
    private static final String[][][] TEAM = {new String[][]{new String[]{"#004DFF"}}, new String[][]{new String[]{"#0B3EF3", "#0CE908", "#00f5e5", "#6941f2", "#8fff94", "#1b702f", "#7cc2ff", "#a294ff", "#0B849B", "#689E3D", "#4F2684", "#2C32AC", "#6968A0", "#D8EEFF", "#3475FF", "#7EB900", "#4A4376", "#B7EA63", "#C4A9FF", "#37713A"}, new String[]{"#FF1005", "#FFD200", "#FF6107", "#F80889", "#FCEEA4", "#8a2828", "#F190B3", "#C88B2F", "#B04523", "#FFBB7C", "#A35274", "#773A01", "#F5A200", "#BBA28B", "#971C48", "#FF68EA", "#DD783F", "#FFAAF3", "#764A4A", "#9F0D05"}}, new String[][]{new String[]{"#004DFF", "#09F5F5", "#7CA1FF", "#2C32AC", "#D8EEFF", "#0B849B", "#3C7AFF", "#5F6492"}, new String[]{"#FF1005", "#FF6107", "#FFD200", "#FF6058", "#FFBB7C", "#C88B2F", "#F5A200", "#9F0D05"}, new String[]{"#0CE818", "#B4FF39", "#097E1C", "#3EFFA2", "#689E3D", "#7EB900", "#B7EA63", "#37713A"}}, new String[][]{new String[]{"#004DFF", "#7CA1FF", "#D8EEFF", "#09F5F5", "#3475FF", "#0B849B"}, new String[]{"#FF1005", "#FF6107", "#FF6058", "#B04523", "#F80889", "#971C48"}, new String[]{"#0CE818", "#B4FF39", "#097E1C", "#3EFFA2", "#689E3D", "#7EB900"}, new String[]{"#FFD200", "#F5A200", "#FCEEA4", "#FFBB7C", "#BBA28B", "#C88B2F"}}, new String[][]{new String[]{"#004DFF", "#7CA1FF", "#D8EEFF", "#09F5F5", "#3475FF"}, new String[]{"#FF1005", "#FF6107", "#FF6058", "#B04523", "#9F0D05"}, new String[]{"#0CE818", "#B4FF39", "#097E1C", "#3EFFA2", "#689E3D"}, new String[]{"#FFD200", "#F5A200", "#FCEEA4", "#FFBB7C", "#C88B2F"}, new String[]{"#F80889", "#FF68EA", "#FFAAF3", "#AA0092", "#701162"}}, new String[][]{new String[]{"#004DFF", "#7CA1FF", "#D8EEFF", "#2C32AC"}, new String[]{"#FF1005", "#FF6058", "#B04523", "#9F0D05"}, new String[]{"#0CE818", "#B4FF39", "#097E1C", "#3EFFA2"}, new String[]{"#FFD200", "#F5A200", "#FCEEA4", "#9B6408"}, new String[]{"#F80889", "#FF68EA", "#FFAAF3", "#971C48"}, new String[]{"#FF6107", "#FFBB7C", "#DD783F", "#773A01"}}, new String[][]{new String[]{"#004DFF", "#7CA1FF", "#2C32AC"}, new String[]{"#FF1005", "#FF6058", "#9F0D05"}, new String[]{"#0CE818", "#B4FF39", "#097E1C"}, new String[]{"#FFD200", "#F5A200", "#FCEEA4"}, new String[]{"#F80889", "#FF68EA", "#FFAAF3"}, new String[]{"#FF6107", "#FFBB7C", "#DD783F"}, new String[]{"#09F5F5", "#0B849B", "#D8EEFF"}}, new String[][]{new String[]{"#004DFF", "#7CA1FF", "#2C32AC"}, new String[]{"#FF1005", "#FF6058", "#9F0D05"}, new String[]{"#0CE818", "#B4FF39", "#097E1C"}, new String[]{"#FFD200", "#F5A200", "#FCEEA4"}, new String[]{"#F80889", "#FF68EA", "#971C48"}, new String[]{"#FF6107", "#FFBB7C", "#DD783F"}, new String[]{"#09F5F5", "#0B849B", "#D8EEFF"}, new String[]{"#872DFA", "#6809A1", "#C4A9FF"}}};
    private static final String[] FFA = {"#004DFF", "#FF1005", "#0CE908", "#FFD200", "#F80889", "#09F5F5", "#FF6107", "#F190B3", "#097E1C", "#C88B2F", "#7CA1FF", "#9F0D05", "#3EFFA2", "#F5A200", "#C4A9FF", "#0B849B", "#B4FF39", "#FF68EA", "#D8EEFF", "#689E3D", "#B04523", "#FFBB7C", "#3475FF", "#DD783F", "#FFAAF3", "#4A4376", "#773A01", "#B7EA63", "#9F0D05", "#7EB900"};

    private BarColors() {
    }

    static Map<String, Color> predict(List<LobbyClient.Participant> participants) {
        List<LobbyClient.Participant> teams = new ArrayList<>();
        for (LobbyClient.Participant participant : participants) {
            if (participant.player()) {
                teams.add(participant);
            }
        }
        teams.sort(Comparator.comparingInt((v0) -> {
            return v0.team();
        }));
        Map<String, Color> result = new LinkedHashMap<>();
        if (teams.isEmpty()) {
            return result;
        }
        TreeSet<Integer> allies = new TreeSet<>();
        Iterator<LobbyClient.Participant> it = teams.iterator();
        while (it.hasNext()) {
            allies.add(Integer.valueOf(it.next().allyTeam()));
        }
        int allyCount = allies.size();
        boolean ffa = allyCount > 2 && teams.stream().mapToInt((v0) -> {
            return v0.team();
        }).distinct().count() == ((long) allyCount);
        Map<Integer, Integer> allyNumber = new LinkedHashMap<>();
        Map<Integer, Integer> nextInAlly = new LinkedHashMap<>();
        Map<Integer, Color> ofTeam = new LinkedHashMap<>();
        for (LobbyClient.Participant team : teams) {
            Color colour = ofTeam.get(Integer.valueOf(team.team()));
            if (colour == null) {
                colour = colourOf(team, allyCount, ffa, allyNumber, nextInAlly);
                if (colour != null) {
                    ofTeam.put(Integer.valueOf(team.team()), colour);
                }
            }
            result.put(team.name, colour);
        }
        return result;
    }

    private static Color colourOf(LobbyClient.Participant team, int allyCount, boolean ffa, Map<Integer, Integer> allyNumber, Map<Integer, Integer> nextInAlly) {
        String ai = team.owner != null ? team.name.toLowerCase(Locale.ROOT) : "";
        if (ai.contains("scavenger")) {
            return SCAVENGER;
        }
        if (ai.contains("raptor")) {
            return RAPTOR;
        }
        int allyIndex = allyNumber.computeIfAbsent(Integer.valueOf(team.allyTeam()), key -> {
            return Integer.valueOf(allyNumber.size());
        }).intValue();
        if (ffa || allyCount > TEAM.length) {
            if (allyIndex < FFA.length) {
                return hex(FFA[allyIndex]);
            }
            return null;
        }
        String[][] palette = TEAM[allyCount - 1];
        if (allyIndex >= palette.length) {
            return null;
        }
        int slot = nextInAlly.merge(Integer.valueOf(team.allyTeam()), 1, (v0, v1) -> {
            return Integer.sum(v0, v1);
        }).intValue() - 1;
        String[] colours = palette[allyIndex];
        return hex(colours[slot % colours.length]);
    }

    static Color hex(String value) {
        return new Color(Integer.parseInt(value.substring(1), 16));
    }

    static String describe(Color colour) {
        PlayerColors.Named best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (PlayerColors.Named named : PlayerColors.FAF) {
            int distance = Math.abs(named.color().getRed() - colour.getRed()) + Math.abs(named.color().getGreen() - colour.getGreen()) + Math.abs(named.color().getBlue() - colour.getBlue());
            if (distance < bestDistance) {
                bestDistance = distance;
                best = named;
            }
        }
        return (best == null || bestDistance >= 120) ? String.format("#%02x%02x%02x", Integer.valueOf(colour.getRed()), Integer.valueOf(colour.getGreen()), Integer.valueOf(colour.getBlue())) : best.name();
    }
}
