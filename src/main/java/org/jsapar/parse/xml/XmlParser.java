package org.jsapar.parse.xml;

import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by stejon0 on 2017-01-01.
 */
public class XmlParser extends AbstractParser {

    public void parse(Reader reader, LineEventListener lineEventListener) throws IOException {
        XmlParseTask parseTask = new XmlParseTask(reader);
        execute(parseTask, lineEventListener);
    }
}
