package org.jsapar;

import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.ParseSchema;
import org.jsapar.parse.SchemaParserFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by stejon0 on 2016-08-14.
 */
public class TextParser2  extends AbstractParser implements Parser {

    private final ParseSchema schema;
    private Reader reader;
    private SchemaParserFactory parserFactory = new SchemaParserFactory();

    public TextParser2(ParseSchema schema, Reader reader) {
        this.schema = schema;
        this.reader = reader;
    }

    @Override
    public void parse() throws JSaParException, IOException {
        parserFactory.makeSchemaParser(this.schema, reader).parse(this, this);

    }
}
