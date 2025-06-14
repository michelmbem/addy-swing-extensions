package org.addy.swing.input;

import javax.swing.border.Border;

public abstract class ValidationRule<T> {
    private final Border border;
    private final String message;

    protected ValidationRule(Border border, String message) {
        this.border = border;
        this.message = message;
    }

    public Border border() {
        return border;
    }

    public String message() {
        return message;
    }

    public abstract boolean test(T value);
}
