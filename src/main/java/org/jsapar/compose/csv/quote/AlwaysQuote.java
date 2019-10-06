package org.jsapar.compose.csv.quote;

import java.io.IOException;
import java.io.Writer;

/**
 * Always quotes supplied value and limits length to supplied max length.
 */
public class AlwaysQuote implements Quoter {
    private ValueComposer valueComposer;
    private char quoteChar;

    public AlwaysQuote(char quoteChar, int maxLength, boolean complyRfc4180) {
        if(maxLength >=0){
            valueComposer = complyRfc4180 ? new MaxLengthValueComposerRfc(maxLength-2, quoteChar) : new MaxLengthComposer(maxLength-2);
        }
        else{
            valueComposer = complyRfc4180 ? new ValueComposerRfc(quoteChar) : new AtomicValueComposer();
        }
        this.quoteChar = (quoteChar == 0 ? '"' : quoteChar);
    }

    @Override
    public void writeValue(Writer writer, String value) throws IOException {
        writer.write(quoteChar);
        valueComposer.writeValue(writer, value);
        writer.write(quoteChar);
    }

    /**
     * Writes only supplied max length characters but makes sure that we never writes a single escape character. Either
     * both escape + quote or none should be written.
     */
    private class MaxLengthValueComposerRfc implements Quoter{
        private final int maxLength;
        private final char escapeChar;

        private MaxLengthValueComposerRfc(int maxLength, char escapeChar) {
            this.maxLength = maxLength;
            this.escapeChar = escapeChar;
        }

        @Override
        public void writeValue(Writer writer, String value) throws IOException {
            int written=0;
            for(int i=0; i<value.length() && written<maxLength; i++){
                char ch = value.charAt(i);
                if(ch == quoteChar) {
                    if(written+2>maxLength)
                        break;
                    writer.write(escapeChar);
                    written++;
                }
                writer.write(ch);
                written++;
            }
        }
    }

    /**
     * Writes value but escapes quotes according to RFC4180
     */
    private class ValueComposerRfc implements Quoter{
        private final char escapeChar;

        private ValueComposerRfc(char escapeChar) {
            this.escapeChar = escapeChar;
        }

        @Override
        public void writeValue(Writer writer, String value) throws IOException {
            for(int i=0; i<value.length(); i++){
                char ch = value.charAt(i);
                if(ch == quoteChar) {
                    writer.write(escapeChar);
                }
                writer.write(ch);
            }
        }
    }

}