package com.faforever.neroxis.generator.bar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;

/**
 * Impostazioni comuni delle finestre "Apri file": vista Dettagli con colonne larghe,
 * finestra ampia e ultima cartella usata ricordata per ogni tipo di file.
 */
public final class FileChoosers {
    private static final Preferences PREFS = Preferences.userNodeForPackage(FileChoosers.class);
    // Nome, Dimensione, Tipo, Ultima modifica (le eventuali colonne in piu' restano come sono)
    private static final int[] COLUMN_WIDTHS = {460, 100, 150, 170};

    private FileChoosers() {}

    public static JFileChooser detailed(JFileChooser chooser) {
        chooser.setPreferredSize(new Dimension(1040, 660));
        Action details = chooser.getActionMap().get("viewTypeDetails");
        if (details != null) details.actionPerformed(null);
        // titolo e filtro vengono impostati dopo questa chiamata: cartella e colonne si sistemano all'apertura
        chooser.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && chooser.isShowing()) {
                restoreLastDirectory(chooser);
                SwingUtilities.invokeLater(() -> widenColumns(chooser));
            }
        });
        chooser.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,
            e -> SwingUtilities.invokeLater(() -> widenColumns(chooser)));
        chooser.addActionListener(e -> {
            if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                File dir = chooser.getCurrentDirectory();
                if (dir != null) PREFS.put(key(chooser), dir.getAbsolutePath());
            }
        });
        return chooser;
    }

    /** Una cartella ricordata per tipo di file (modelli, texture, maschere, heightmap). */
    private static String key(JFileChooser chooser) {
        FileFilter filter = chooser.getFileFilter();
        String description = filter != null && filter.getDescription() != null ? filter.getDescription() : "file";
        return "lastDir." + Integer.toHexString(description.hashCode());
    }

    private static void restoreLastDirectory(JFileChooser chooser) {
        String saved = PREFS.get(key(chooser), null);
        if (saved == null) return;
        File dir = new File(saved);
        if (dir.isDirectory() && !dir.equals(chooser.getCurrentDirectory())) {
            chooser.setCurrentDirectory(dir);
        }
    }

    private static void widenColumns(Container root) {
        JTable table = findTable(root);
        if (table == null) return;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columns = table.getColumnModel();
        for (int i = 0; i < columns.getColumnCount() && i < COLUMN_WIDTHS.length; i++) {
            columns.getColumn(i).setPreferredWidth(COLUMN_WIDTHS[i]);
            columns.getColumn(i).setWidth(COLUMN_WIDTHS[i]);
        }
    }

    private static JTable findTable(Container root) {
        for (Component child : root.getComponents()) {
            if (child instanceof JTable table) return table;
            if (child instanceof Container container) {
                JTable found = findTable(container);
                if (found != null) return found;
            }
        }
        return null;
    }
}
