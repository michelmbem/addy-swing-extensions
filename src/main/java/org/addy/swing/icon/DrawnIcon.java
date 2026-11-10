package org.addy.swing.icon;

import javax.swing.*;

/** A base class for programmatically drawn icons. */
public abstract class DrawnIcon implements Icon {
    protected final int width;
    protected final int height;

    /**
     * Initializes an instance of DrawnIcon
     *
     * @param width the icon width in pixels
     * @param height the icon height in pixels
     */
    protected DrawnIcon(int width, int height) {
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
}
