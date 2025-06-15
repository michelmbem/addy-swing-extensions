package org.addy.swing.input;

import org.addy.swing.KnownColor;
import org.addy.swing.UIHelper;
import org.addy.swing.border.IconBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class ValidationBorders {
    private ValidationBorders() {}

    public static Border line(Color color) {
        return BorderFactory.createLineBorder(color);
    }

    public static Border icon(Icon icon) {
        return new IconBorder(icon);
    }

    private static Border icon(String name) {
        return icon(UIHelper.loadIcon(ValidationBorders.class, name + ".png", -1, -1));
    }

    public static final Border LINE_INFO = line(KnownColor.LIME);
    public static final Border LINE_WARNING = line(KnownColor.YELLOW);
    public static final Border LINE_DANGER = line(KnownColor.RED);
    public static final Border ICON_INFO = icon("information");
    public static final Border ICON_WARNING = icon("warning");
    public static final Border ICON_DANGER = icon("danger");
}
