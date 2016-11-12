package org.jsapar.concurrent;

import org.jsapar.TextComposer;
import org.jsapar.TextParser;
import org.jsapar.schema.Schema;

import java.io.Reader;
import java.io.Writer;

/**
 * Created by stejon0 on 2016-10-15.
 */
public class ConcurrentText2TextConverter extends ConcurrentConverter{

    public ConcurrentText2TextConverter(TextParser parser, TextComposer composer) {
        super(parser, composer);
    }

    public ConcurrentText2TextConverter(Schema inputSchema, Reader reader, Schema outputSchema, Writer writer) {
        super(new TextParser(inputSchema, reader), new TextComposer(outputSchema, writer));
    }

}
