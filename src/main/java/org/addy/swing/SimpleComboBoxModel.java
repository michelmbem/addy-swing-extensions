package org.addy.swing;

import org.addy.util.CollectionUtil;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleComboBoxModel<E> extends AbstractListModel<E> implements ComboBoxModel<E> {
    private List<E> items;
    private E selectedItem = null;

    public SimpleComboBoxModel(E[] items) {
        this.items = Arrays.asList(items);
    }

    public SimpleComboBoxModel(Collection<E> items) {
        this.items = CollectionUtil.toList(items);
    }

    public SimpleComboBoxModel() {
        this(Collections.emptyList());
    }

    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = Objects.requireNonNull(items);
        fireContentsChanged(this, 0, getSize());
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public E getElementAt(int index) {
        return items.get(index);
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem = (E) anItem;
    }

    public void addItem(E anItem) {
        int index = getSize();
        items.add(anItem);
        fireIntervalAdded(this, index, index);
    }

    public void insertItemAt(int index, E anItem) {
        items.add(index, anItem);
        fireIntervalAdded(this, index, index);
    }

    public void setItemAt(int index, E anItem) {
        items.set(index, anItem);
        fireContentsChanged(this, index, index);
    }

    public void removeItemAt(int index) {
        items.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void removeAllItems() {
        int size = getSize();
        items.clear();
        fireIntervalRemoved(this, 0, size);
    }
}
