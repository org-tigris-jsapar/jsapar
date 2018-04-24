package org.jsapar.parse.xml;

import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;

import java.io.IOException;
import java.io.Reader;

/**
 * Parses xml text of the internal xml format and produces parse events. The xml needs to conform to the internal XML schema XMLDocumentFormat.xsd (http://jsapar.tigris.org/XMLDocumentFormat/1.0)
 * <p>
 * This class can for instance be used to parse xml text that was created by {@link org.jsapar.Text2XmlConverter}.
 * <p>
 * See {@link AbstractParser} about error handling.
 */
public class XmlParser extends AbstractParser {

    public void parse(Reader reader, LineEventListener lineEventListener) throws IOException {
        XmlParseTask parseTask = new XmlParseTask(reader);
        execute(parseTask, lineEventListener);
    }
}
