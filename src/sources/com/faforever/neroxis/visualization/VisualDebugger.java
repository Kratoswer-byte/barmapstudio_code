package com.faforever.neroxis.visualization;

import com.faforever.neroxis.mask.Mask;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/visualization/VisualDebugger.class */
public class VisualDebugger {
    private static final DefaultListModel<MaskListItem> listModel = new DefaultListModel<>();
    private static final JList<MaskListItem> list = new JList<>(listModel);
    private static final EntryPanel canvas = new EntryPanel();
    private static JFrame frame;

    public static void visualizeMask(Mask<?, ?> mask) {
        visualizeMask(mask, null, null);
    }

    public static void visualizeMask(Mask<?, ?> mask, String method) {
        visualizeMask(mask, method, null);
    }

    public static void visualizeMask(Mask<?, ?> mask, String method, String line) {
        visualizeMask(mask, method, line, null);
    }

    public static void visualizeMask(Mask<?, ?> mask, String method, String line, Integer index) {
        Mask<?, ?> copyOfmask = mask.immutableCopy();
        copyOfmask.setVisualName(mask.getVisualName());
        SwingUtilities.invokeLater(() -> {
            createGui();
            String name = copyOfmask.getVisualName();
            updateList(name + " " + method + " " + line, copyOfmask.immutableCopy(), index);
        });
    }

    public static void createGui() {
        if (isCreated()) {
            return;
        }
        frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        setupList();
        setupCanvas();
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(3);
    }

    public static boolean isCreated() {
        return frame != null;
    }

    private static void setupList() {
        list.setSelectionMode(0);
        list.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                MaskListItem selectedItem = (MaskListItem) list.getSelectedValue();
                updateVisibleCanvas(selectedItem);
            }
        });
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setMinimumSize(new Dimension(350, 0));
        listScroller.setPreferredSize(new Dimension(550, 0));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = 1;
        constraints.gridx = 0;
        constraints.weightx = 0.0d;
        constraints.gridy = 0;
        constraints.weighty = 1.0d;
        frame.add(listScroller, constraints);
    }

    private static void updateVisibleCanvas(MaskListItem maskListItem) {
        if (maskListItem == null) {
            canvas.setMask(null);
            frame.setTitle("");
        } else {
            String maskName = maskListItem.maskName();
            Mask<?, ?> mask = maskListItem.mask();
            canvas.setMask(mask);
            frame.setTitle(String.format("Mask: %s MaskSize: %d", maskName, Integer.valueOf(mask.getSize())));
        }
    }

    private static void setupCanvas() {
        canvas.setPreferredSize(new Dimension(650, 650));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = 1;
        constraints.gridx = 1;
        constraints.weightx = 1.0d;
        constraints.gridy = 0;
        constraints.weighty = 1.0d;
        frame.add(canvas, constraints);
    }

    public static synchronized void updateList(String uniqueMaskName, Mask<?, ?> mask, Integer index) {
        if (!uniqueMaskName.isBlank()) {
            if (index == null) {
                int ind = listModel.getSize();
                for (int i = listModel.getSize() - 1; i >= 0; i--) {
                    if (((MaskListItem) listModel.get(i)).maskName.split(" ")[0].equals(uniqueMaskName.split(" ")[0])) {
                        ind = i + 1;
                        break;
                    }
                }
                listModel.add(ind, new MaskListItem(uniqueMaskName, mask));
                if (list.getSelectedIndex() == -1) {
                    list.setSelectedIndex(ind);
                }
            } else {
                if (listModel.getSize() < index.intValue() + 1) {
                    listModel.setSize(index.intValue() + 1);
                }
                listModel.set(index.intValue(), new MaskListItem(uniqueMaskName, mask));
                if (list.getSelectedIndex() == -1) {
                    list.setSelectedIndex(index.intValue());
                }
            }
            list.revalidate();
            list.repaint();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/visualization/VisualDebugger$MaskListItem.class */
    public static final class MaskListItem extends Record {
        private final String maskName;
        private final Mask<?, ?> mask;

        public MaskListItem(String maskName, Mask<?, ?> mask) {
            this.maskName = maskName;
            this.mask = mask;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, MaskListItem.class), MaskListItem.class, "maskName;mask", "FIELD:Lcom/faforever/neroxis/visualization/VisualDebugger$MaskListItem;->maskName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/visualization/VisualDebugger$MaskListItem;->mask:Lcom/faforever/neroxis/mask/Mask;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, MaskListItem.class, Object.class), MaskListItem.class, "maskName;mask", "FIELD:Lcom/faforever/neroxis/visualization/VisualDebugger$MaskListItem;->maskName:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/visualization/VisualDebugger$MaskListItem;->mask:Lcom/faforever/neroxis/mask/Mask;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String maskName() {
            return this.maskName;
        }

        public Mask<?, ?> mask() {
            return this.mask;
        }

        @Override // java.lang.Record
        public String toString() {
            return this.maskName;
        }
    }
}
