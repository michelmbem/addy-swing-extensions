package org.addy.swing.icon;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public final class ColorSwatchIcon implements Icon {
    private final Color color;
    private final int width;
    private final int height;

    public ColorSwatchIcon(Color color, int width, int height) {
        this.color = Objects.requireNonNull(color);
        this.width = Math.max(width, 4);
        this.height = Math.max(height, 4);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        double luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255.0;
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(luminance < 0.5 ? Color.WHITE : Color.BLACK);
        g.drawRect(x, y, width, height);
        g.dispose();
    }

}
