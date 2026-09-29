package com.faforever.neroxis.visualization;

import com.faforever.neroxis.generator.terrain.SetonishLastTerrainGenerator;
import com.faforever.neroxis.mask.Mask;
import com.faforever.neroxis.util.MathUtil;
import com.faforever.neroxis.util.dds.DDSHeader;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/visualization/MaskPanel.class */
class MaskPanel extends JPanel {
    private final EntryPanel entryPanel;
    private float userZoomLevel = SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT;
    private Vector2 fractionalImageOffset = new Vector2();
    private Vector2 imageZoomFactor = new Vector2();
    private Vector2 lastMousePosition = new Vector2();
    private BufferedImage image;
    private Mask<?, ?> mask;

    @Generated
    public Mask<?, ?> getMask() {
        return this.mask;
    }

    public MaskPanel(EntryPanel entryPanel) {
        this.entryPanel = entryPanel;
        CanvasMouseListener canvasMouseListener = new CanvasMouseListener(this);
        addMouseListener(canvasMouseListener);
        addMouseMotionListener(canvasMouseListener);
        addMouseWheelListener(canvasMouseListener);
        this.image = new BufferedImage(DDSHeader.CUBEMAP_FLAG, DDSHeader.CUBEMAP_FLAG, 1);
        paintCheckerboard();
    }

    private void paintCheckerboard() {
        for (int x = 0; x < this.image.getWidth(); x++) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                if (((x / 4) + (y / 4)) % 2 == 0) {
                    this.image.setRGB(x, y, Color.GRAY.getRGB());
                } else {
                    this.image.setRGB(x, y, Color.DARK_GRAY.getRGB());
                }
            }
        }
    }

    public void setMask(Mask<?, ?> mask) {
        this.mask = mask;
        if (mask != null) {
            this.image = new BufferedImage(mask.getSize(), mask.getSize(), 1);
            paintCheckerboard();
            Graphics g = this.image.getGraphics();
            g.drawImage(mask.toImage(), 0, 0, this);
            return;
        }
        this.image = new BufferedImage(1, 1, 1);
    }

    public Vector2 getMouseOnMask() {
        return canvasCoordinatesToMaskCoordinates(this.lastMousePosition).floor();
    }

    private Vector2 canvasCoordinatesToMaskCoordinates(Vector2 canvasCoords) {
        return canvasCoordinatesToFractionalMaskCoordinates(canvasCoords).multiply(this.mask.getSize());
    }

    private Vector2 canvasCoordinatesToFractionalMaskCoordinates(Vector2 canvasCoords) {
        return canvasCoords.divide(getFullScalingVector().multiply(this.mask.getSize())).subtract(this.fractionalImageOffset);
    }

    private Vector2 getFullScalingVector() {
        return new Vector2().add(getUserScaleFactor()).multiply(this.imageZoomFactor);
    }

    private float getUserScaleFactor() {
        return (float) StrictMath.pow(2.0d, this.userZoomLevel);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.mask != null) {
            this.imageZoomFactor = new Vector2(getWidth() / this.image.getWidth(), getHeight() / this.image.getHeight());
            Vector2 fullScalingVector = getFullScalingVector();
            Vector2 imageOffset = this.fractionalImageOffset.multiply(this.mask.getSize());
            AffineTransform at = new AffineTransform();
            at.scale(fullScalingVector.x(), fullScalingVector.y());
            at.translate(imageOffset.x(), imageOffset.y());
            AffineTransformOp op = new AffineTransformOp(at, 1);
            BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), 1);
            op.filter(this.image, newImage);
            Graphics2D g2d = g.create();
            g2d.drawImage(newImage, 0, 0, this);
            g2d.dispose();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/visualization/MaskPanel$CanvasMouseListener.class */
    private class CanvasMouseListener extends MouseInputAdapter {
        final /* synthetic */ MaskPanel this$0;

        private CanvasMouseListener(final MaskPanel this$0) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (this.this$0.mask != null) {
                this.this$0.lastMousePosition = new Vector2(e.getPoint());
                Vector2 oldMousePositionOnMask = this.this$0.canvasCoordinatesToFractionalMaskCoordinates(this.this$0.lastMousePosition);
                this.this$0.userZoomLevel = (float) StrictMath.min(StrictMath.max(((double) this.this$0.userZoomLevel) - (((double) e.getWheelRotation()) * 0.25d), 0.0d), MathUtil.log2(this.this$0.mask.getSize()));
                Vector2 newMousePositionOnMask = this.this$0.canvasCoordinatesToFractionalMaskCoordinates(this.this$0.lastMousePosition);
                this.this$0.fractionalImageOffset = this.this$0.fractionalImageOffset.subtract(oldMousePositionOnMask).add(newMousePositionOnMask);
                boundOffset();
                this.this$0.repaint();
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (this.this$0.mask != null) {
                Vector2 newMousePosition = new Vector2(e.getPoint());
                this.this$0.fractionalImageOffset = this.this$0.fractionalImageOffset.subtract(this.this$0.lastMousePosition.subtract(newMousePosition).divide(this.this$0.getFullScalingVector().multiply(this.this$0.mask.getSize())));
                boundOffset();
                this.this$0.lastMousePosition = newMousePosition;
                this.this$0.repaint();
            }
        }

        public void mouseMoved(MouseEvent e) {
            this.this$0.lastMousePosition = new Vector2(e.getPoint());
            this.this$0.entryPanel.setValueLabel();
        }

        private void boundOffset() {
            this.this$0.fractionalImageOffset = this.this$0.fractionalImageOffset.min(SetonishLastTerrainGenerator.WATER_LAYER_MIN_HEIGHT).max((1.0f / this.this$0.getUserScaleFactor()) - 1.0f);
        }
    }
}
