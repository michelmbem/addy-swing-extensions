package org.addy.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public final class ColorSwatchIcon implements Icon {
    private final Color color;
    private final int width;
    private final int height;

    public ColorSwatchIcon(Color color, int width, int height) {
        this.color = Objects.requireNonNull(color);
        this.width = Math.max(width, 8);
        this.height = Math.max(height, 8);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        double luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255.0;
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(color);
        g.fillRect(x + 2, y + 2, width - 4, height - 4);
        g.setColor(luminance < 0.5 ? Color.WHITE : Color.BLACK);
        g.drawRect(x + 2, y + 2, width - 5, height - 5);
        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

}
