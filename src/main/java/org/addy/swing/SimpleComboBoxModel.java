package org.addy.swing;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SimpleComboBoxModel<E>
        extends AbstractListModel<E>
        implements ComboBoxModel<E> {

    private List<E> items;
    private E selectedItem;

    public SimpleComboBoxModel(List<E> items) {
        setItems(items);
    }

    public SimpleComboBoxModel(Collection<? extends E> items) {
        this(new ArrayList<>(items));
    }

    @SafeVarargs
    public SimpleComboBoxModel(E... items) {
        this(List.of(items));
    }

    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = Objects.requireNonNull(items);
        setSelectedItem(items.isEmpty() ? null : items.get(0));
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
    @SuppressWarnings("unchecked")
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

    public void removeItem(E anItem) {
        int index = items.indexOf(anItem);
        if (items.remove(anItem))
            fireIntervalRemoved(this, index, index);
    }

    public void removeItemAt(int index) {
        items.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void clearItems() {
        int size = getSize();
        items.clear();
        fireIntervalRemoved(this, 0, size);
    }
}
