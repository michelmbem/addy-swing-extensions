package org.addy.swing.icon;

import org.addy.swing.KnownColor;

import java.awt.*;

public class TransparentIcon extends DrawnIcon {

    /**
     * Initializes an instance of TransparentIcon
     *
     * @param width the icon width in pixels
     * @param height the icon height in pixels
     */
    public TransparentIcon(int width, int height) {
        super(width, height);
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
