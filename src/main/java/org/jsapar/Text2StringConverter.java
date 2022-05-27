package org.jsapar;

import org.jsapar.compose.Composer;
import org.jsapar.compose.string.StringComposedConsumer;
import org.jsapar.compose.string.StringComposedEventListener;
import org.jsapar.compose.string.StringComposer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Converts a text input to  {@link Stream} of {@link String} for each line that is parsed.
 * <p>
 * The {@link StringComposedConsumer} provides a
 * {@link java.util.stream.Stream} of {@link java.lang.String} for the current {@link org.jsapar.model.Line} where each
 * string is matches the cell in a schema. Each cell is formatted according to provided
 * {@link org.jsapar.schema.Schema}.
 * <p>
 * The schema can be of either CSV or FixedWith, the only thing that is significant is the order of the cells and the
 * cell formatting.
 * <p>
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 * @see org.jsapar.schema.StringSchema
 */
public class Text2StringConverter extends AbstractConverter {
    private final Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema;
    private final Schema<? extends SchemaLine<? extends SchemaCell>> composeSchema;
    private TextParseConfig parseConfig = new TextParseConfig();

    public Text2StringConverter(Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema, Schema<? extends SchemaLine<? extends SchemaCell>> composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    public Text2StringConverter(Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema, Schema<? extends SchemaLine<? extends SchemaCell>> composeSchema, TextParseConfig parseConfig) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
        this.parseConfig = parseConfig;
    }

    /**
     * Deprecated since 2.2. Use {@link #convertForEach(Reader, StringComposedConsumer)} instead.
     * @param reader                The reader to read input from
     * @param composedEventListener The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    @Deprecated
    public long convert(Reader reader, StringComposedEventListener composedEventListener) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        return execute(parseTask, new StringComposer(composeSchema, composedEventListener));
    }

    /**
     * This method provides apart from the string values, also the line type and line number.
     * @param reader                The reader to read input from
     * @param stringComposedConsumer The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     * @see #convertForEach(Reader, Consumer)
     * @since 2.2
     */
    public long convertForEach(Reader reader, StringComposedConsumer stringComposedConsumer) throws IOException {
        TextParseTask parseTask = new TextParseTask(this.parseSchema, reader, parseConfig);
        return execute(parseTask, makeComposer(composeSchema, stringComposedConsumer));
    }

    /**
     * Use this method if you are only interested in the values.
     * @param reader                The reader to read input from
     * @param stringComposedConsumer The string composed event listener that get notification of each line.
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     * @see #convertForEach(Reader, StringComposedConsumer)
     * @since 2.2
     */
    public long convertForEach(Reader reader, Consumer<Stream<String>> stringComposedConsumer) throws IOException {
        return this.convertForEach(reader, (cells, lineType, lineNumber)->stringComposedConsumer.accept(cells));
    }


    /**
     * Returns a stream of lines consisting of stream of cell values that are lazily populated by lines when pulled from the stream. The reader is consumed
     * on the fly upon pulling items from the stream.
     * <p/>
     * This method is particularly efficient if you don't want to scan through the whole source since it will abort
     * parsing as soon as you stop pulling items from the stream.
     * @param reader The reader to parse from.
     * @return a stream of lines consisting of stream of cell values that are lazily populated by lines when pulled from the stream. The order of the stream is according to the order lines were parsed from the reader.
     * @since 2.3
     * @throws IOException If there is an error reading from the input reader.
     */
    public Stream<Stream<String>> stream(Reader reader) throws IOException {
        try(StringComposer composer = new StringComposer(composeSchema, (c,l,n)->{})) {
            TextSchemaParser parser = TextSchemaParser.ofSchema(parseSchema, reader, getParseConfig());
            return parser.stream(getErrorConsumer()).flatMap(line -> composer.toStringLine(line).stream());
        }
    }

    /**
     * Creates the composer. Makes it possible to override with custom made implementation of {@link StringComposer}
     * @param schema                The output schema to use while composing.
     * @param stringComposedConsumer The consumer that will be called for each line.
     * @return The composer to use in this converter
     */
    @SuppressWarnings("WeakerAccess")
    protected Composer makeComposer(Schema<? extends SchemaLine<? extends SchemaCell>> schema, StringComposedConsumer stringComposedConsumer) {
        return new StringComposer(schema, stringComposedConsumer);
    }


    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
