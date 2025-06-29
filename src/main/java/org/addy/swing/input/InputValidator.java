package org.addy.swing.input;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.util.function.Function;

public class InputValidator<T> extends InputVerifier {
    private final ValidationRule<T> validationRule;
    private final Function<JComponent, T> valueExtractor;

    private Border originalBorder;
    private String originalToolTipText;
    private boolean initialized = false;

    public InputValidator(ValidationRule<T> validationRule, Function<JComponent, T> valueExtractor) {
        this.validationRule = validationRule;
        this.valueExtractor = valueExtractor;
    }

    public static InputValidator<String> ofText(ValidationRule<String> validationRule) {
        return new InputValidator<>(validationRule, c -> ((JTextComponent) c).getText());
    }

    public void save(JComponent input) {
        if (initialized) return;

        originalBorder = input.getBorder();
        originalToolTipText = input.getToolTipText();
        initialized = true;
    }

    public void restore(JComponent input) {
        input.setBorder(originalBorder);
        input.setToolTipText(originalToolTipText);
    }

    @Override
    public boolean verify(JComponent input) {
        save(input);

        T value = valueExtractor.apply(input);
        if (validationRule.test(value)) {
            restore(input);
            return true;
        }

        input.setBorder(BorderFactory.createCompoundBorder(originalBorder, validationRule.border()));
        input.setToolTipText(validationRule.message());
        input.requestFocus();

        return false;
    }
}
