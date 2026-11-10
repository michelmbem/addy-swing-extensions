package org.addy.swing.icon;

import org.addy.swing.KnownColor;

import javax.swing.*;
import java.awt.*;

public class TransparentIcon implements Icon {
    private final int width;
    private final int height;

    public TransparentIcon(int width, int height) {
        this.width = Math.max(width, 4);
        this.height = Math.max(height, 4);
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
        final int cell = 5;
        Graphics2D g = (Graphics2D) graphics.create();

        for (int row = 0; row < 4; ++row) {
            for (int column = 0; column < 4; ++column) {
                g.setColor((row + column) % 2 == 0 ? Color.WHITE : KnownColor.LIGHTSLATEGRAY);
                g.fillRect(x + column * cell, y + row * cell, cell, cell);
            }
        }

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        g.setColor(Color.RED);
        g.drawLine(x + 1, y + 1, x + width - 1, y + height - 1);
        g.dispose();
    }
}
