package com.faforever.neroxis.generator.bar;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 * Grandezza dell'interfaccia. Java segue da solo la scala scelta in Windows, ma non quella di Linux, e su uno
 * schermo 4K lasciato al 100% le scritte restano piccole: qui l'utente puo' scegliere un ingrandimento, che vale
 * dall'avvio successivo.
 */
final class UiScale {
    /** Valore salvato quando si lascia decidere al sistema. */
    static final String AUTO = "auto";
    private static final Preferences PREFS = Preferences.userNodeForPackage(UiScale.class);
    private static final String KEY = "ui.scale";
    private static final String ASKED = "ui.scale.asked";
    /** Altezza dello schermo, in punti, oltre la quale un'interfaccia non ingrandita risulta piccola. */
    private static final int SMALL_TEXT_HEIGHT = 1800;

    private UiScale() {
    }

    /** Linux accetta solo ingrandimenti interi. */
    static String[] choices() {
        return linux() ? new String[]{AUTO, "1", "2", "3"} : new String[]{AUTO, "1", "1.25", "1.5", "1.75", "2", "2.5", "3"};
    }

    static String current() {
        return PREFS.get(KEY, AUTO);
    }

    static void save(String value) {
        PREFS.put(KEY, value);
        PREFS.putBoolean(ASKED, true);
    }

    static int percent(String value) {
        return Math.round(Float.parseFloat(value) * 100.0f);
    }

    /** Da chiamare per prima cosa in main: la scala va decisa prima che Java apra lo schermo. */
    static void applyAtStartup() {
        String value = current();
        if (AUTO.equals(value) || System.getProperty("sun.java2d.uiScale") != null) {
            return;
        }
        try {
            Float.parseFloat(value);
            System.setProperty("sun.java2d.uiScale", value);
        } catch (NumberFormatException e) {
            PREFS.remove(KEY);
        }
    }

    /** Una volta sola, su uno schermo ad alta risoluzione non ingrandito, propone di ingrandire l'interfaccia. */
    static void suggestIfSmall(Component window) {
        GraphicsConfiguration screen = window.getGraphicsConfiguration();
        if (screen == null || PREFS.getBoolean(ASKED, false) || !AUTO.equals(current())) {
            return;
        }
        if (screen.getDefaultTransform().getScaleX() > 1.0 || screen.getBounds().height < SMALL_TEXT_HEIGHT) {
            return;
        }
        PREFS.putBoolean(ASKED, true);
        String proposal = linux() ? "2" : "1.5";
        int answer = JOptionPane.showConfirmDialog(window, I18n.t("settings.scale.suggest", percent(proposal)),
            I18n.t("settings.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            save(proposal);
            JOptionPane.showMessageDialog(window, I18n.t("settings.scale.restart"), I18n.t("settings.title"),
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static boolean linux() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return !os.contains("win") && !os.contains("mac");
    }
}
