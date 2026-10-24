package org.addy.swing;

import org.addy.swing.text.MaxLengthDocumentFilter;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class UIHelper {
    public static final String DOUBLE_CLICK_COMMAND = "DOUBLE CLICK";

    private UIHelper() {}

    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        return width > 0 || height > 0
                ? new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH))
                : icon;
    }

    public static ImageIcon loadIcon(URL url, int width, int height) {
        return resizeIcon(new ImageIcon(url), width, height);
    }

    public static ImageIcon loadIcon(Class<?> clazz, String name, int width, int height) {
        return loadIcon(clazz.getResource(name), width, height);
    }

    public static void setFixedSized(Component c, Dimension size) {
        c.setMinimumSize(size);
        c.setMaximumSize(size);
        c.setPreferredSize(size);
    }

    public static void setText(JTextComponent textComponent, String text) {
        textComponent.setText(text == null ? "" : text);
    }

    public static void setMaxLength(JTextComponent textComponent, int maxLength) {
        var document = (AbstractDocument) textComponent.getDocument();
        document.setDocumentFilter(new MaxLengthDocumentFilter(maxLength));
    }

    public static <E, V> V getSelectedValue(JComboBox<E> comboBox, Function<E, V> valueExtractor) {
        int selectedIndex = comboBox.getSelectedIndex();
        return selectedIndex >= 0 ? valueExtractor.apply(comboBox.getItemAt(selectedIndex)) : null;
    }

    public static <V extends Serializable> V getSelectedValue(JComboBox<ListItem<V>> comboBox) {
        return getSelectedValue(comboBox, ListItem::getValue);
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
        setSelectedValue(comboBox, value, ListItem::getValue);
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
        comboBox.repaint();
    }

    public static <E> void setSelectedItem(JComboBox<E> comboBox, E selectedItem) {
        comboBox.setSelectedItem(selectedItem);
        comboBox.repaint();
    }

    public static LocalDate getDate(JCalendarCombo calendarCombo) {
        return calendarCombo.isChecked() ? calendarCombo.getDate() : null;
    }

    public static void setDate(JCalendarCombo calendarCombo, LocalDate date) {
        if (date == null)
            calendarCombo.setChecked(false);
        else
            calendarCombo.setDate(date);
    }

    public static void addDoubleClickListener(Component component, ActionListener listener) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
                    listener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), DOUBLE_CLICK_COMMAND));
            }
        });
    }

    public static int scale(int coord, boolean yAxis) {
        AffineTransform at = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice() // or cycle your getScreenDevices()
                .getDefaultConfiguration()
                .getDefaultTransform();
        double scaleFactor = yAxis ? at.getScaleY() : at.getScaleX();
        return (int) Math.round(coord * scaleFactor);
    }

    public static Dimension scale(Dimension dimension) {
        return new Dimension(
                scale(dimension.width, false),
                scale(dimension.height, true));
    }
}
