package org.addy.swing.input;

import javax.swing.border.Border;
import java.util.function.Predicate;

public class PredicateValidationRule<T> extends ValidationRule<T> {
    private final Predicate<T> predicate;

    public PredicateValidationRule(Predicate<T> predicate, Border border, String message) {
        super(border, message);
        this.predicate = predicate;
    }

    @Override
    public boolean test(T value) {
        return predicate.test(value);
    }
}
