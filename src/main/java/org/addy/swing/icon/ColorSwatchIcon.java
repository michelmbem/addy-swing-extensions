package org.addy.swing.icon;

import java.awt.*;
import java.util.Objects;

/** An icon that represents a rectangular color swatch. */
public class ColorSwatchIcon extends DrawnIcon {
    protected final Color color;

    /**
     * Initializes a new instance of ArrowIcon.
     *
     * @param color the icon color
     * @param width the icon width in pixels
     * @param height the icon height in pixels
     */
    public ColorSwatchIcon(Color color, int width, int height) {
        super(width, height);
        this.color = Objects.requireNonNull(color);
    }

    /** Gets the icon color */
    public Color getColor() {
        return color;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        double luminance = (0.299 * color.getRed()
                + 0.587 * color.getGreen()
                + 0.114 * color.getBlue()) / 255.0;

        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(luminance < 0.5 ? Color.WHITE : Color.BLACK);
        g.drawRect(x, y, width, height);
        g.dispose();
    }

}
