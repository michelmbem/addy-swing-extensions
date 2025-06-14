package org.addy.swing;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ListItem<T extends Serializable>
        implements Serializable, Comparable<ListItem<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final T value;
    private final String label;

    public ListItem(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public ListItem(T value) {
        this(value, value != null ? value.toString() : "");
    }

    public T value() {
        return value;
    }

    public String label() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        return o instanceof ListItem<?> other && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return label != null ? label : "";
    }

    @Override
    public int compareTo(ListItem<T> other) {
        return toString().compareTo(other.toString());
    }
}
