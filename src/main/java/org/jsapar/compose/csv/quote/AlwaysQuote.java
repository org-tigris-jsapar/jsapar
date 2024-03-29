package org.jsapar.compose.csv.quote;

import org.jsapar.schema.QuoteSyntax;

import java.io.IOException;
import java.io.Writer;

/**
 * Always quotes supplied value and limits length to supplied max length.
 */
final public class AlwaysQuote implements Quoter {
    private final ValueComposer valueComposer;
    private final char quoteChar;

    public AlwaysQuote(char quoteChar, int maxLength, QuoteSyntax quoteSyntax) {
        switch (quoteSyntax) {
        case FIRST_LAST:
            valueComposer = (maxLength >= 0) ? new MaxLengthComposer(maxLength - 2) : new AtomicValueComposer();
            break;
        case RFC4180:
            valueComposer = (maxLength >= 0) ?
                    new MaxLengthValueComposerRfc(maxLength - 2, quoteChar) :
                    new ValueComposerRfc(quoteChar);
            break;
        default:
            throw new AssertionError("Unsupported quote syntax while composing: " + quoteSyntax);
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
            int start = 0;
            int found;
            while(written < maxLength && -1 != (found=value.indexOf(quoteChar, start))){
                final int len = Math.min(found - start, maxLength - written);
                if(len > 0) {
                    writer.write(value, start, len);
                    written += len;
                }
                if(written + 2 <= maxLength) {
                    writer.write(escapeChar);
                    writer.write(quoteChar);
                }
                written += 2; // Increment also when not actually adding quote to avoid appending more chars
                start = found +1;
            }

            final int len = Math.min(value.length() - start, maxLength - written);
            if(len > 0)
                writer.write(value, start, len);
        }
    }

    /**
     * Writes value but escapes quotes according to RFC4180.
     */
    private class ValueComposerRfc implements Quoter{
        private final char escapeChar;

        private ValueComposerRfc(char escapeChar) {
            this.escapeChar = escapeChar;
        }

        @Override
        public void writeValue(Writer writer, String value) throws IOException {
            int start = 0;
            int found;
            while(-1 != (found=value.indexOf(quoteChar, start))){
                final int len = found - start;
                if(len > 0)
                    writer.write(value, start, len);
                writer.write(escapeChar);
                writer.write(quoteChar);
                start = found +1;
            }

            final int len = value.length() - start;
            if(len > 0)
                writer.write(value, start, len);
        }
    }

}