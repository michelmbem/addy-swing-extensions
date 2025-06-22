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
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static ImageIcon loadIcon(ClassLoader loader, String name, int width, int height) {
        return loadIcon(loader.getResource(name), width, height);
    }

    public static void setFixedSized(Component c, Dimension size) {
        c.setMinimumSize(size);
        c.setMaximumSize(size);
        c.setPreferredSize(size);
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
    
    public static LocalDateTime getDateTime(JCalendarCombo calendarCombo) {
        return calendarCombo.isChecked() ? calendarCombo.getDateTime() : null;
    }

    public static void setDateTime(JCalendarCombo calendarCombo, LocalDateTime dateTime) {
        if (dateTime == null)
            calendarCombo.setChecked(false);
        else 
            calendarCombo.setDateTime(dateTime);
    }

    public static LocalDate getDate(JCalendarCombo calendarCombo) {
        return calendarCombo.isChecked() ? calendarCombo.getDate() : null;
    }

    public static void setDate(JCalendarCombo calendarCombo, LocalDate dateTime) {
        if (dateTime == null)
            calendarCombo.setChecked(false);
        else
            calendarCombo.setDate(dateTime);
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
}
