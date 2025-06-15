package org.addy.swing.border;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Objects;

public class IconBorder implements Border {
    private final Icon icon;
    private final Direction direction;

    public IconBorder(Icon icon, Direction direction) {
        this.icon = Objects.requireNonNull(icon);
        this.direction = Objects.requireNonNull(direction);
    }

    public IconBorder(Icon icon) {
        this(icon, Direction.RIGHT);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets borderInsets = getBorderInsets(c);
        Rectangle iconBounds = switch (direction) {
            case TOP -> new Rectangle(x, y, width, borderInsets.top);
            case LEFT -> new Rectangle(x, y, borderInsets.left, height);
            case BOTTOM -> new Rectangle(x, y + height - borderInsets.bottom, width, borderInsets.bottom);
            case RIGHT -> new Rectangle(x + width - borderInsets.right, y, borderInsets.right, height);
        };
        int iconX = iconBounds.x + (iconBounds.width - icon.getIconWidth()) / 2;
        int iconY = iconBounds.y + (iconBounds.height - icon.getIconHeight()) / 2;
        icon.paintIcon(c, g,iconX, iconY);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int topInset = direction == Direction.TOP ? icon.getIconHeight() : 0;
        int leftInset = direction == Direction.LEFT ? icon.getIconWidth() : 0;
        int bottomInset = direction == Direction.BOTTOM ? icon.getIconHeight() : 0;
        int rightInset = direction == Direction.RIGHT ? icon.getIconWidth() : 0;
        return new Insets(topInset, leftInset, bottomInset, rightInset);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    public enum Direction {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }
}
