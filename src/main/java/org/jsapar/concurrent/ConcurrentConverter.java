package org.jsapar.concurrent;

import org.jsapar.Converter;
import org.jsapar.compose.Composer;
import org.jsapar.parse.Parser;

import java.io.IOException;

/**
 * Concurrent version of the {@link Converter}. The composer is executed in a separate worker thread. Also the line
 * manipulators are all executed in the worker thread.
 * <p>
 * Reads from supplied parser and outputs each line to the composer. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method manipulate() of all added LineManipulators are called for each line that are
 * parsed successfully.
 * <p>
 * For each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 * <p>
 * If your want lines to be discarded from the output depending of their contents, add a LineManipulator that returns
 * false for the lines that should not be composed.
 *
 */
public class ConcurrentConverter extends Converter {

    /** Creates a converter
     * @param parser The parser to use while parsing
     * @param composer The composer to use while composing.
     */
    public ConcurrentConverter(Parser parser, Composer composer) {
        super(parser, composer);
    }

    public void convert() throws IOException {
        try (ConcurrentLineEventListener concurrentLineEventListener = new ConcurrentLineEventListener()) {
            getParser().setLineEventListener(concurrentLineEventListener);
            concurrentLineEventListener.addLineEventListener(new LineForwardListener());
            concurrentLineEventListener.start();
            getParser().parse();
        }
    }

}
