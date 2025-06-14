package org.addy.swing.input;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ValidationPredicates {
    private ValidationPredicates() {}

    public static Predicate<Object> notNull() {
        return Objects::nonNull;
    }

    public static Predicate<String> notEmpty() {
        return s -> !StringUtils.isEmpty(s);
    }

    public static Predicate<String> notBlank() {
        return s -> !StringUtils.isBlank(s);
    }

    public static Predicate<String> minLength(int length) {
        return s -> s == null || s.length() >= length;
    }

    public static Predicate<String> maxLength(int length) {
        return s -> s == null || s.length() <= length;
    }

    public static Predicate<String> pattern(String regex) {
        return s -> s == null || s.matches(regex);
    }

    public static Predicate<String> number() {
        return s -> s == null || StringUtils.isNumeric(s);
    }

    public static <T extends Comparable<T>> Predicate<T> range(T min, T max) {
        return value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    @SafeVarargs
    public static <T> Predicate<T> combine(Predicate<T>... predicates) {
        return value -> Stream.of(predicates).allMatch(p -> p.test(value));
    }
}
