package org.jsapar;

import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.xml.XmlParseTask;

import java.io.IOException;
import java.io.Reader;


/**
 * Parses xml file and produces parse events. The xml file needs to conform to the XMLDocumentFormat.xsd (http://jsapar.tigris.org/XMLDocumentFormat/1.0)
 * See {@link AbstractParser} for error handling.
 */
public class XmlParser extends AbstractParser {
    public XmlParser() {
    }

    public void parse(Reader reader, LineEventListener lineEventListener) throws IOException {
        XmlParseTask parseTask = new XmlParseTask(reader);
        execute(parseTask, lineEventListener);
    }

}
