package com.faforever.neroxis.visualization;

import com.faforever.neroxis.mask.Mask;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/visualization/EntryPanel.class */
public class EntryPanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JLabel valueLabel = new JLabel();
    private final MaskPanel maskPanel = new MaskPanel(this);

    public EntryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createBevelBorder(1));
        setupImagePanel();
        setupLabels();
    }

    private void setupImagePanel() {
        add(this.maskPanel, "Center");
    }

    private void setupLabels() {
        this.titleLabel.setHorizontalAlignment(0);
        add(this.titleLabel, "North");
        this.valueLabel.setHorizontalAlignment(0);
        this.valueLabel.setMinimumSize(new Dimension(100, 25));
        this.valueLabel.setPreferredSize(new Dimension(100, 25));
        this.valueLabel.setMaximumSize(new Dimension(100, 25));
        add(this.valueLabel, "South");
    }

    public void setMask(Mask<?, ?> mask) {
        this.maskPanel.setMask(mask);
        this.titleLabel.setText(String.format("Name: %s Size: %d", mask.getVisualName(), Integer.valueOf(mask.getSize())));
        repaint();
        setValueLabel();
    }

    public void setValueLabel() {
        if (this.maskPanel.getMask() != null) {
            Vector2 maskCoords = this.maskPanel.getMouseOnMask();
            if (this.maskPanel.getMask().inBounds(maskCoords)) {
                this.valueLabel.setText(String.format("X: %5.0f, Y: %5.0f Value: %s", Float.valueOf(maskCoords.x()), Float.valueOf(maskCoords.y()), this.maskPanel.getMask().get(maskCoords)));
            }
        }
    }
}
