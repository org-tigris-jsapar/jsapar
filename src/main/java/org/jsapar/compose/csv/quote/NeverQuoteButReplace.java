package org.jsapar.compose.csv.quote;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Will never quote the cell but instead replaces cellSeparator or lineSeparator within a cell with supplied replace string.
 */
public class NeverQuoteButReplace implements Quoter {
    private final NeverQuote neverQuote;
    private final String cellSeparator;
    private final String lineSeparator;
    private final char cellSeparatorFirst;
    private final char lineSeparatorFirst;
    private final Pattern cellSeparatorPattern;
    private final Pattern lineSeparatorPattern;
    private final String quotedReplacement;


    public NeverQuoteButReplace(int maxLength, String cellSeparator, String lineSeparator, String replaceString) {
        this.neverQuote = new NeverQuote(maxLength);
        if(cellSeparator == null || cellSeparator.isEmpty()) {
            throw new IllegalArgumentException("Cell separator cannot be null or empty.");
        }
        this.cellSeparator = cellSeparator;
        if(lineSeparator == null || lineSeparator.isEmpty())
            throw new IllegalArgumentException("Line separator cannot be null or empty.");
        this.lineSeparator = lineSeparator;

        cellSeparatorFirst = cellSeparator.charAt(0);
        lineSeparatorFirst = lineSeparator.charAt(0);

        cellSeparatorPattern = Pattern.compile(cellSeparator, Pattern.LITERAL);
        lineSeparatorPattern = Pattern.compile(lineSeparator, Pattern.LITERAL);

        quotedReplacement = Matcher.quoteReplacement(replaceString);
    }


    @Override
    public void writeValue(Writer writer, String value) throws IOException {
        if(shouldReplace(value)) {
            value = cellSeparatorPattern.matcher(value).replaceAll(quotedReplacement);
            value = lineSeparatorPattern.matcher(value).replaceAll(quotedReplacement);
        }
        neverQuote.writeValue(writer, value);
    }

    /**
     * Slight optimization. Avoid the overhead of doing full replacement if none of the first characters are present.
     * This way we normally only have to loop once but we take a penalty if we hit one of the first characters.
     * @param value The value to write.
     * @return True if there is a risk that there needs to be a replacement. False if it is safe to use the original
     * value.
     */
    private boolean shouldReplace(String value) {
        for(int i=0; i<value.length();i++){
            if(value.charAt(i)== cellSeparatorFirst || value.charAt(i)==lineSeparatorFirst){
                return true;
            }
        }
        return false;
    }
}
