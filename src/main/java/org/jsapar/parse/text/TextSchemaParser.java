package org.jsapar.parse.text;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.csv.CsvParser;
import org.jsapar.parse.fixed.FixedWidthParser;
import org.jsapar.schema.*;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Internal interface for text parser that can parse text based on a schema.
 */
public interface TextSchemaParser {

    /**
     * This method should only be called by a TextParseTask class. Don't use this
     * directly in your code. Use a TextParseTask instead.
     * <p>
     * Sends line parse events to the supplied lineEventListener while parsing.
     *
     * @param lineConsumer      The line consumer which will receive events for each parsed line.
     * @param errorConsumer The error consumer that will receive events for each error.
     * @return Number of lines parsed
     * @throws IOException If there is an error reading from the input reader.
     */
    long parse(Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) throws IOException;

    /**
     * This method should only be called by a TextParseTask class. Don't use this
     * directly in your code. Use a TextParseTask instead.
     * <p>
     * Returns a stream of lines that are lazily populated by lines when pulled from the stream.
     * @param errorConsumer The error consumer that will receive events for each error.
     * @return a stream of lines that are lazily populated by lines when pulled from the stream.
     * @since 2.3
     * @throws IOException If there is an error reading from the input reader.
     */
    Stream<Line> stream(Consumer<JSaParException> errorConsumer) throws IOException;


        /**
         * Internal method to create a schema parser using this schema.
         * @param schema The schema to create a parser for.
         * @param reader The reader to use for the parser.
         * @param parseConfig Current parse configuration.
         * @return Create a schema based text parser. If the schema has detail lines, the parser groups detail lines
         * under their master lines.
         */
    static TextSchemaParser ofSchema(Schema<? extends SchemaLine<? extends SchemaCell>> schema, Reader reader, TextParseConfig parseConfig) {
        TextSchemaParser parser;
        if (schema instanceof CsvSchema csvSchema)
            parser = new CsvParser(reader, csvSchema, parseConfig);
        else if (schema instanceof FixedWidthSchema fixedWidthSchema)
            parser = new FixedWidthParser(reader, fixedWidthSchema, parseConfig);
        else
            throw new IllegalArgumentException("Unsupported schema type: " + schema.getClass() + " while parsing.");
        return schema.hasDetailLines() ? new MasterDetailTextParser(parser, schema, parseConfig) : parser;
    }
}
