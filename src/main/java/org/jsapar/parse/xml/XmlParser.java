package org.jsapar.parse.xml;

import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;

import java.io.IOException;
import java.io.Reader;

/**
 * A parser that reads the internal xml format defined in XMLDocumentFormat.xsd.
 *
 * The default error handling is to throw an exception upon the first error that occurs. You can however change that
 * behavior by adding an {@link org.jsapar.error.ErrorEventListener}. There are several implementations to choose from such as
 * {@link org.jsapar.error.RecordingErrorEventListener} or
 * {@link org.jsapar.error.ThresholdRecordingErrorEventListener}, or you may implement your own..
 */
public class XmlParser extends AbstractParser {

    public void parse(Reader reader, LineEventListener lineEventListener) throws IOException {
        XmlParseTask parseTask = new XmlParseTask(reader);
        execute(parseTask, lineEventListener);
    }
}
