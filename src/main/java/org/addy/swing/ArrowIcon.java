package org.addy.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/** A filled triangular arrow icon. */
public final class ArrowIcon implements Icon {
    private final ArrowDirection direction;
    private final Color color;
    private final int width;
    private final int height;

    public ArrowIcon(ArrowDirection direction, Color color, int width, int height) {
        this.direction = Objects.requireNonNull(direction, "direction");
        this.color = Objects.requireNonNull(color, "color");
        this.width = Math.max(width, 8);
        this.height = Math.max(height, 8);
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        int minX = x + 2;
        int minY = y + 2;
        int maxX = x + width - 3;
        int maxY = y + height - 3;
        int midX = minX + (maxX - minX) / 2;
        int midY = minY + (maxY - minY) / 2;
        int[] xPoints;
        int[] yPoints;

        switch (direction) {
            case LEFT -> {
                xPoints = new int[]{minX, maxX, maxX};
                yPoints = new int[]{midY, minY, maxY};
            }
            case UP -> {
                xPoints = new int[]{midX, maxX, minX};
                yPoints = new int[]{minY, maxY, maxY};
            }
            case RIGHT -> {
                xPoints = new int[]{maxX, minX, minX};
                yPoints = new int[]{midY, minY, maxY};
            }
            case DOWN -> {
                xPoints = new int[]{midX, maxX, minX};
                yPoints = new int[]{maxY, minY, minY};
            }
            default -> throw new AssertionError("Unsupported arrow direction: " + direction);
        }

        Graphics g = graphics.create();
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 3);
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
}
