package org.addy.swing.input;

import org.addy.swing.JCalendar;
import org.addy.swing.JCalendarCombo;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Validator {
    private static final Set<Class<?>> VALIDATABLE_COMPONENT_TYPES = new HashSet<>();

    static {
        registerValidatableComponentTypes(
                JTextComponent.class,
                JToggleButton.class,
                JList.class,
                JComboBox.class,
                JSpinner.class,
                JCalendar.class,
                JCalendarCombo.class);
    }

    private Validator() {}

    public static void registerValidatableComponentTypes(Class<?>... componentTypes) {
        Collections.addAll(VALIDATABLE_COMPONENT_TYPES, componentTypes);
    }

    public static boolean validate(Container container) {
        for (Component c : container.getComponents()) {
            if (isValidatable(c)) {
                JComponent jc = (JComponent) c;
                InputVerifier verifier = jc.getInputVerifier();

                if (!(verifier == null || verifier.verify(jc))) {
                    Toolkit.getDefaultToolkit().beep();
                    jc.requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    public static void restore(Container container) {
        for (Component c : container.getComponents()) {
            if (!isValidatable(c)) continue;

            var jc = (JComponent) c;

            if (jc.getInputVerifier() instanceof InputValidator<?> inputValidator)
                inputValidator.restore(jc);
        }
    }

    private static boolean isValidatable(Component c) {
        return VALIDATABLE_COMPONENT_TYPES.stream()
                .anyMatch(cls -> cls.isAssignableFrom(c.getClass()));
    }
}
