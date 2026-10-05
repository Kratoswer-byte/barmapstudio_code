package com.faforever.neroxis.generator.bar;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * Traduzioni dell'interfaccia. Lingue: inglese (default), italiano, francese, tedesco.
 * I testi stanno in /i18n/messages_<lingua>.properties dentro il jar; una chiave che manca
 * nella lingua scelta ricade sull'inglese, e se manca anche li' viene mostrata la chiave.
 * La lingua si cambia dall'icona impostazioni nel footer e vale dal riavvio successivo.
 */
public final class I18n {
    public static final String[] LANGUAGES = {"en", "it", "fr", "de"};
    public static final String[] LANGUAGE_NAMES = {"English", "Italiano", "Francais", "Deutsch"};

    private static final Preferences PREFS = Preferences.userNodeForPackage(I18n.class);
    private static final Properties FALLBACK = load("en");
    private static String language = PREFS.get("language", "en");
    private static Properties messages = load(language);

    private I18n() {}

    public static String getLanguage() {
        return language;
    }

    /** Salva la lingua scelta; i testi gia' a schermo cambiano al prossimo avvio. */
    public static void setLanguage(String lang) {
        language = lang;
        messages = load(lang);
        PREFS.put("language", lang);
    }

    public static String t(String key, Object... args) {
        String text = messages.getProperty(key);
        if (text == null) text = FALLBACK.getProperty(key, key);
        return args.length == 0 ? text : MessageFormat.format(text, args);
    }

    private static Properties load(String lang) {
        Properties p = new Properties();
        try (InputStream in = I18n.class.getResourceAsStream("/i18n/messages_" + lang + ".properties")) {
            if (in != null) p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}
        return p;
    }
}
