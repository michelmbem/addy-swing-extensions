package org.addy.swing.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class MaxLengthDocumentFilter extends DocumentFilter {
    private final int maxLength;

    public MaxLengthDocumentFilter(int maxLength) {
        if (maxLength <= 0)
            throw new IllegalArgumentException("maxLength should be positive");

        this.maxLength = maxLength;
    }

    @Override
    public void replace(FilterBypass bypass,
                        int offset,
                        int deletedTextLength,
                        String insertedText,
                        AttributeSet attributes) throws BadLocationException {

        int currentLength = bypass.getDocument().getLength();
        int nextLength = currentLength + insertedText.length() - deletedTextLength;

        if (nextLength > maxLength)
            insertedText = insertedText.substring(0, insertedText.length() - nextLength + maxLength);

        super.replace(bypass, offset, deletedTextLength, insertedText, attributes);
    }
}
