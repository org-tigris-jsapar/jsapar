package org.jsapar.compose.csv.quote;

import org.jsapar.schema.QuoteSyntax;

import java.io.IOException;
import java.io.Writer;

/**
 * Quotes cell only if needed because it contains either cellSeparator, quote character or lineSeparator.
 */
public class QuoteIfNeeded implements Quoter {
    private final char quoteChar;
    private final Quoter alwaysQuote;
    private final Quoter neverQuote;
    private final String cellSeparator;
    private final String lineSeparator;
    private final char cellSeparatorFirst;
    private final char lineSeparatorFirst;

    public QuoteIfNeeded(char quoteChar, int maxLength, String cellSeparator, String lineSeparator, QuoteSyntax quoteSyntax) {
        this(quoteChar, maxLength, cellSeparator, lineSeparator, new AlwaysQuote(quoteChar, maxLength, quoteSyntax));
    }

    private QuoteIfNeeded(char quoteChar, int maxLength, String cellSeparator, String lineSeparator, Quoter alwaysQuoter) {
        this.quoteChar = quoteChar;
        this.alwaysQuote = alwaysQuoter;
        this.neverQuote = new NeverQuote(maxLength);
        this.cellSeparator = cellSeparator;
        this.lineSeparator = lineSeparator;
        this.cellSeparatorFirst = cellSeparator.charAt(0);
        this.lineSeparatorFirst = lineSeparator.charAt(0);
    }

    @Override
    public void writeValue(Writer writer, String value) throws IOException {
        if(value.isEmpty())
            neverQuote.writeValue(writer, value);
        else if (shouldQuote(value)){
            alwaysQuote.writeValue(writer, value);
        }
        else
            neverQuote.writeValue(writer, value);
    }

    /**
     * Only loop once unless some suspicious character is found.
     * @param value  The value to search
     * @return True if value should be quoted, false otherwise.
     */
    private boolean shouldQuote(String value){
        for(int i=0; i<value.length(); i++){
            final char ch = value.charAt(i);
            if(ch == quoteChar)
                return true;
            if(ch == cellSeparatorFirst){
                return cellSeparator.length() == 1 || containsSpecial(value);
            }
            if(ch == lineSeparatorFirst){
                return lineSeparator.length() == 1 || containsSpecial(value);
            }
        }
        return false;
    }

    private boolean containsSpecial(String value){
        return value.contains(cellSeparator)
                || value.indexOf(quoteChar) >=0
                || value.contains(lineSeparator);
    }
}
