package org.jsapar.compose.csv.quote;

import java.io.IOException;
import java.io.Writer;

/**
 * Quotes cell only if needed because it contains either cellSeparator, quote character or lineSeparator.
 */
public class QuoteIfNeeded implements Quoter {
    private char quoteChar;
    private Quoter alwaysQuote;
    private Quoter neverQuote;
    private String cellSeparator;
    private String lineSeparator;

    public QuoteIfNeeded(char quoteChar, int maxLength, String cellSeparator, String lineSeparator, boolean complyRfc4180) {
        this(quoteChar, maxLength, cellSeparator, lineSeparator, new AlwaysQuote(quoteChar, maxLength, complyRfc4180));
    }

    private QuoteIfNeeded(char quoteChar, int maxLength, String cellSeparator, String lineSeparator, Quoter alwaysQuoter) {
        this.quoteChar = quoteChar;
        this.alwaysQuote = alwaysQuoter;
        this.neverQuote = new NeverQuote(maxLength);
        this.cellSeparator = cellSeparator;
        this.lineSeparator = lineSeparator;
    }

    @Override
    public void writeValue(Writer writer, String value) throws IOException {
        if(value.isEmpty())
            neverQuote.writeValue(writer, value);
        else if (value.contains(cellSeparator)
                || value.indexOf(quoteChar) >=0
                || value.contains(lineSeparator)){
            alwaysQuote.writeValue(writer, value);
        }
        else
            neverQuote.writeValue(writer, value);
    }
}
