package org.jsapar;

import org.jsapar.schema.Schema;

import java.io.Reader;
import java.io.Writer;

/**
 * Created by stejon0 on 2016-10-15.
 */
public class Text2TextConverter extends Converter{

    public Text2TextConverter(TextParser parser, TextComposer composer) {
        super(parser, composer);
    }

    public Text2TextConverter(Schema inputSchema, Reader reader, Schema outputSchema, Writer writer) {
        super(new TextParser(inputSchema, reader), new TextComposer(outputSchema, writer));
    }

}
