package org.jsapar;

import org.jsapar.bean.BeanMap;
import org.jsapar.compose.bean.*;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.error.BeanException;
import org.jsapar.model.Line;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Converts text input to Java bean objects. You can choose to use the standard behavior or you may customize assigning
 * of bean properties. The default behavior is to use the schema names where the line type name needs to match the class
 * name of the class to create and the cell names needs to match bean property names. Any sub-objects needs to be of a
 * class that provides a default constructor. By supplying a {@link BeanMap} instance to the constructor you can map
 * different line and cell names to property names. {@link BeanMap} instance can be created form an xml input by using
 * the {@link BeanMap#ofXml(Reader)} method.
 * <p>
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 * @see AbstractConverter
 */
public class Text2BeanConverter<T> extends AbstractConverter {

    private final Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema;
    private BeanFactory<T> beanFactory;
    private BeanComposeConfig composeConfig = new BeanComposeConfig();
    private TextParseConfig parseConfig = new TextParseConfig();

    /**
     * Creates a converter with supplied composer schema.
     * @param parseSchema The schema to use while reading the text input.
     * @param beanMap     The bean map to use to map schema names to bean properties. By supplying a {@link BeanMap} instance to the constructor you can map
     *                    different line and cell names to property names. {@link BeanMap} instance can be created either from a list of
     *                    annotated classes by using the {@link BeanMap#ofClasses(Class[])} or form a xml input by using
     * the {@link BeanMap#ofXml(Reader)} method. This {@link BeanMap} instance will be used as is, so it needs to contain
     * mapping for all values that should be assigned to the bean instances. If you want to use a {@link BeanMap} that is created
     *                    from a combination of the schema and an additional override {@link BeanMap} you can use the method {@link BeanMap#ofSchema(Schema, BeanMap)} to create such combined instance.
     */
    public Text2BeanConverter(Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema, BeanMap beanMap) {
        this.parseSchema = parseSchema;
        this.beanFactory = new BeanFactoryByMap<>(beanMap);
    }

    /**
     * Creates a converter with supplied composer schema.
     * @param parseSchema The schema to use while reading the text input.
     * @param annotatedBeanClass The annotated bean class to use to create an overriding bean map. This means that
     *                           the schema attributes are used as property names unless there is an  annotation in the
     *                           class. In that case the annotation is considered.
     * @since 2.3
     */
    public Text2BeanConverter(Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema, Class<T> annotatedBeanClass) {
        this(parseSchema, BeanMap.ofSchema(parseSchema, BeanMap.ofClass(annotatedBeanClass)));
    }

    /**
     * The default behavior is to use the schema names where the line type name needs to match the class
     * name of the class to create and the cell names needs to match bean property names.
     *
     * @param parseSchema The schema to use while reading the text input.
     * @throws BeanException In case of error when instantiating bean.
     */
    public Text2BeanConverter(Schema<? extends SchemaLine<? extends SchemaCell>> parseSchema) throws BeanException {
        this(parseSchema, BeanMap.ofSchema(parseSchema));
    }

    /**
     * Assigns a different bean factory. The bean factory is responsible for creating beans and assigning bean values. You may implement your own or use any of the existing:
     * <ul>
     *     <li>{@link BeanFactoryByMap} - Default for this class. Uses input schema with optional {@link BeanMap} to map properties.</li>
     *     <li>{@link BeanFactoryDefault} - Default if no schema is present. Uses cell names to guess the property names.</li>
     * </ul>
     * @param beanFactory The bean factory to use.
     */
    public void setBeanFactory(BeanFactory<T> beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Executes the actual convert. Will produce a callback to supplied eventListener for each bean that is parsed.
     * Deprecated since 2.2. Use {@link #convertForEach(Reader, Consumer)} or {@link #convertForEach(Reader, BiConsumer)} instead.
     * @param reader The reader to read the text from.
     * @param eventListener The callback interface which will receive a callback for each bean that is successfully parsed.
     * @return Number of converted beans.
     * @throws IOException In case of io error.
     */
    @Deprecated
    public long convert(Reader reader, BeanEventListener<T> eventListener) throws IOException {
        return convertForEach(reader, new BeanEventListenerConsumer<>(eventListener));
    }

    /**
     * Executes the actual convert. For each bean that is composed, the bean composer will be called. This method also
     * supplies the line to the consumer in case information is needed from the line that was lost while composing the
     * bean.
     * @param reader The reader to read the text from.
     * @param beanConsumer The bi-consumer that will be called for each bean that is composed. First argument is the bean, the second is the line.
     * @return Number of converted beans.
     * @throws IOException In case of io error.
     */
    public long convertForEach(Reader reader, BiConsumer<T, Line> beanConsumer) throws IOException {
        BeanComposer<T> composer = new BeanComposer<>(composeConfig, beanFactory);
        composer.setBeanConsumer(beanConsumer);
        if (beanFactory != null)
            composer.setBeanFactory(beanFactory);
        return execute(new TextParseTask(this.parseSchema, reader, parseConfig), composer);
    }


    /**
     * Returns a stream of beans that are lazily populated by lines when pulled from the stream. The reader is consumed
     * on the fly upon pulling items from the stream.
     * <br/>
     * This method is particularly efficient if you don't want to scan through the whole source since it will abort
     * parsing as soon as you stop pulling items from the stream.
     * @param reader The reader to parse from.
     * @return a stream of beans that are lazily populated by lines when pulled from the stream. The order of the stream is according to the order lines were parsed from the reader.
     * @since 2.3
     * @throws IOException If there is an error reading from the input reader.
     */
    public Stream<T> stream(Reader reader) throws IOException {
        try(BeanComposer<T> composer = new BeanComposer<>(composeConfig, beanFactory)) {
            TextSchemaParser parser = TextSchemaParser.ofSchema(parseSchema, reader, getParseConfig());
            return parser.stream(getErrorConsumer()).flatMap(line -> composer.toBean(line).stream());
        }
    }

    /**
     * Executes the actual convert. For each bean that is composed, the bean composer will be called.
     * @param reader The reader to read the text from.
     * @param beanConsumer The consumer that will be called for each bean that is composed.
     * @return Number of converted beans.
     * @since 2.2
     * @throws IOException In case of io error.
     */
    public long convertForEach(Reader reader, Consumer<T> beanConsumer) throws IOException {
        return convertForEach(reader, (bean, line)->beanConsumer.accept(bean));
    }

    public void setComposeConfig(BeanComposeConfig composeConfig) {
        this.composeConfig = composeConfig;
    }

    public BeanComposeConfig getComposeConfig() {
        return composeConfig;
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
