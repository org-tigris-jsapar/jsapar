package org.jsapar.parse.xml;

import org.jsapar.model.Line;
import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineEventListenerLineConsumer;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Parses xml text of the internal xml format and produces parse events. The xml needs to conform to the internal XML schema XMLDocumentFormat.xsd (http://jsapar.tigris.org/XMLDocumentFormat/2.0)
 * <p>
 * This class can for instance be used to parse xml text that was created by {@link org.jsapar.Text2XmlConverter}.
 * <p>
 * See {@link AbstractParser} about error handling.
 */
public final class XmlParser extends AbstractParser {

    /**
     * Reads xml from supplied reader and parses each line. Each parsed line generates a call-back to the lineEventListener.
     * <p>
     * Deprecated since 2.2. Use {@link #parseForEach(Reader, Consumer)} instead.
     *
     * @param reader            The reader to read xml from.
     * @param lineEventListener The call-back interface.
     * @return Number of parsed lines.
     * @throws IOException In case of IO error
     */
    @Deprecated
    public long parse(Reader reader, LineEventListener lineEventListener) throws IOException {
        return parseForEach(reader, new LineEventListenerLineConsumer(lineEventListener));
    }

    /**
     * Reads xml from supplied reader and parses each line. Each parsed line generates a call-back to the lineEventListener.
     * @param reader The reader to read xml from.
     * @param lineConsumer The line consumer that will be called for each line.
     * @return Number of parsed lines.
     * @throws IOException In case of IO error
     */
    public long parseForEach(Reader reader, Consumer<Line> lineConsumer) throws IOException {
        XmlParseTask parseTask = new XmlParseTask(reader);
        return execute(parseTask, lineConsumer);
    }

    public Stream<Line> stream(Reader reader){
        XmlParseSpliterator spliterator = new XmlParseSpliterator(reader);
        return StreamSupport.stream(spliterator, false);
    }
}
