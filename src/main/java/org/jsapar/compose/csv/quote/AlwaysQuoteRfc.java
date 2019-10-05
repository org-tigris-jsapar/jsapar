package org.jsapar.compose.csv.quote;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;

/**
 * Always quotes supplied value but escape any quote character with another quote character
 * then limits length to supplied max length.
 */
public class AlwaysQuoteRfc implements Quoter {
    private final ValueComposer valueComposer;
    private final char quoteChar;
    private final char escapeChar;
    private StringBuilder stringBuilder=new StringBuilder();

    public AlwaysQuoteRfc(char quoteChar, int maxLength) {
        this(quoteChar, quoteChar, maxLength);
    }

    public AlwaysQuoteRfc(char quoteChar, char escapeChar, int maxLength) {
        valueComposer = maxLength >=0 ? new MaxLengthComposer(maxLength-2) : new AtomicValueComposer();
        this.quoteChar = (quoteChar == 0 ? '"' : quoteChar);
        this.escapeChar = (escapeChar == 0 ? quoteChar : escapeChar);
    }

    @Override
    public void writeValue(Writer writer, String value) throws IOException {
        writer.write(quoteChar);
        if(value.indexOf(quoteChar)>=0)
            value=replaceQuotes(value);
        valueComposer.writeValue(writer, value);
        writer.write(quoteChar);
    }

    private String replaceQuotes(String value) {
        stringBuilder.delete(0, stringBuilder.length());
        for(int i=0; i<value.length(); i++){
            char ch = value.charAt(i);
            if(ch==quoteChar){
                stringBuilder.append(escapeChar);
            }
            stringBuilder.append(ch);
        }
        return stringBuilder.toString();
    }
}
