package org.addy.swing;

import org.addy.swing.text.MaxLengthDocumentFilter;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class UIHelper {
    private UIHelper() {}

    public static ImageIcon loadIcon(Class<?> clazz, String filename, int width, int height) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(clazz.getResource(filename)));
        return width > 0 || height > 0
                ? new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH))
                : icon;
    }

    public static void setFixedSized(Component c, Dimension size) {
        c.setMinimumSize(size);
        c.setMaximumSize(size);
        c.setPreferredSize(size);
    }

    public static void setMaxLength(JTextComponent textComponent, int maxLength) {
        ((AbstractDocument) textComponent.getDocument())
                .setDocumentFilter(new MaxLengthDocumentFilter(maxLength));
    }

    public static <E, V> V getSelectedValue(JComboBox<E> comboBox, Function<E, V> valueExtractor) {
        int selectedIndex = comboBox.getSelectedIndex();
        return selectedIndex >= 0 ? valueExtractor.apply(comboBox.getItemAt(selectedIndex)) : null;
    }

    public static <V extends Serializable> V getSelectedValue(JComboBox<ListItem<V>> comboBox) {
        return getSelectedValue(comboBox, ListItem::value);
    }

    public static <E, V> void setSelectedValue(JComboBox<E> comboBox, V value, Function<E, V> valueExtractor) {
        int itemCount = comboBox.getItemCount();
        int selectedIndex = -1;

        for (int i = 0; i < itemCount; ++i) {
            E item = comboBox.getItemAt(i);
            if (Objects.equals(valueExtractor.apply(item), value)) {
                selectedIndex = i;
                break;
            }
        }

        comboBox.setSelectedIndex(selectedIndex);
    }

    public static <V extends Serializable> void setSelectedValue(JComboBox<ListItem<V>> comboBox, V value) {
        setSelectedValue(comboBox, value, ListItem::value);
    }

    public static <E, V> void setSelectedValue(JComboBox<E> comboBox, V value, BiPredicate<E, V> equality) {
        int itemCount = comboBox.getItemCount();
        int selectedIndex = -1;

        for (int i = 0; i < itemCount; ++i) {
            E item = comboBox.getItemAt(i);
            if (equality.test(item, value)) {
                selectedIndex = i;
                break;
            }
        }

        comboBox.setSelectedIndex(selectedIndex);
    }
}
