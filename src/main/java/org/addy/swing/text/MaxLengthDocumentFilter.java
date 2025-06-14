package org.addy.swing.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class MaxLengthDocumentFilter extends DocumentFilter {
    private final int maxLength;

    public MaxLengthDocumentFilter(int maxLength) {
        if (maxLength <= 0)
            throw new IllegalArgumentException("maxLength can not be <= 0");

        this.maxLength = maxLength;
    }

    @Override
    public void replace(FilterBypass bypass, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        int currentLength = bypass.getDocument().getLength();
        int nextLength = currentLength + text.length() - length;

        if (nextLength > maxLength) {
            text = text.substring(0, text.length() - nextLength + maxLength);
        }

        if (!text.isEmpty()) {
            super.replace(bypass, offset, length, text, attrs);
        }
    }
}
