package org.addy.swing.icon;

import java.awt.*;
import java.util.Objects;

/** A filled triangular arrow icon. */
public class ArrowIcon extends ColorSwatchIcon {
    protected final ArrowDirection direction;

    /**
     * Initializes a new instance of ArrowIcon.
     *
     * @param direction the direction where the arrow points at
     * @param color the icon color
     * @param width the icon width in pixels
     * @param height the icon height in pixels
     */
    public ArrowIcon(ArrowDirection direction, Color color, int width, int height) {
        super(color, width, height);
        this.direction = Objects.requireNonNull(direction, "direction");
    }

    /** Gets the direction where the arrow points at */
    public ArrowDirection getDirection() {
        return direction;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        int maxX = x + width;
        int maxY = y + height;
        int midX = x + width / 2;
        int midY = y + height / 2;
        int[] xPoints;
        int[] yPoints;

        switch (direction) {
            case LEFT -> {
                xPoints = new int[]{x, maxX, maxX};
                yPoints = new int[]{midY, y, maxY};
            }
            case UP -> {
                xPoints = new int[]{midX, maxX, x};
                yPoints = new int[]{y, maxY, maxY};
            }
            case RIGHT -> {
                xPoints = new int[]{maxX, x, x};
                yPoints = new int[]{midY, y, maxY};
            }
            case DOWN -> {
                xPoints = new int[]{midX, maxX, x};
                yPoints = new int[]{maxY, y, y};
            }
            default -> throw new AssertionError("Unsupported arrow direction: " + direction);
        }

        Graphics g = graphics.create();
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 3);
        g.dispose();
    }
}
