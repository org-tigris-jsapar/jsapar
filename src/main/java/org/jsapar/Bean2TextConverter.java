package org.jsapar;

import org.jsapar.convert.LineManipulator;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.bean.BeanMap;
import org.jsapar.error.JSaParException;
import org.jsapar.parse.bean.BeanMarshaller;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.function.Consumer;

/**
 * Converts from beans to text output. This implementation accepts beans pushed one by one to be converted. See
 * {@link BeanCollection2TextConverter} for an implementation where you provide a stream or a collection of beans from which
 * beans are pulled. This means that this class acts more like a Composer compared to other Converter in the sense that you need to provide the writer
 * in the constructor and each call to {@link #convert(Object)} method is done without supplying any writer.
 * <p>
 * The Generic type T should be set to a common base class of all the expected beans. Use Object as
 * base class if there is no common base class for all beans.
 * <p>
 * An instance of this class can only be used once for one writer, then it needs to be disposed. Instances of {@link BeanCollection2TextConverter} on
 * the other hand can be used multiple times for multiple writers.
 * <p>
 * ExampleUsage:
 * <pre>{@code
 * Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(schema, writer);
 * converter.convert(person1);
 * converter.convert(person2);
 * }</pre>
 * <p>
 * The default error behavior is to throw an exception upon the first error that occurs. You can however change that
 * behavior by adding an error consumer using {@link #setErrorConsumer(Consumer)}. There are several implementations to choose from such as
 * {@link org.jsapar.error.ThresholdCollectingErrorConsumer}, or you may implement your own.
 *
 * @see BeanCollection2TextConverter
 * @param <T> The base class that should be allowed to convert. Use {@code <Object>} in case you want to allow all types.
 */
public class Bean2TextConverter<T> implements AutoCloseable{

    private final BeanMarshaller<T>         beanMarshaller;
    private final TextComposer              textComposer;
    private       long                      lineNumber         = 1;
    private final List<LineManipulator>     manipulators  = new java.util.LinkedList<>();
    private       Consumer<JSaParException> errorConsumer = new ExceptionErrorConsumer();

    /**
     * Creates a converter with supplied composer schema.
     *
     * @param composerSchema The schema to use while composing text output.
     * @param writer         The writer to write text output to. Caller is responsible for either closing the writer or call the close method of the created instance.
     */
    public Bean2TextConverter(Schema<?> composerSchema, Writer writer){
        this(composerSchema, BeanMap.ofSchema(composerSchema), writer);
    }

    /**
     * Creates a converter with supplied composer schema.
     *
     * @param composerSchema The schema to use while composing text output.
     * @param annotatedBeanClass The annotated bean class to use to create an overriding bean map. This means that
     *                           the schema attributes are used as property names unless there is an  annotation in the
     *                           class. In that case the annotation is considered.
     * @param writer         The writer to write text output to. Caller is responsible for either closing the writer or call the close method of the created instance.
     * @since 2.3
     */
    public Bean2TextConverter(Schema<?> composerSchema, Class<T> annotatedBeanClass, Writer writer) {
        this(composerSchema, BeanMap.ofSchema(composerSchema, BeanMap.ofClass(annotatedBeanClass)), writer);
    }

    /**
     * Creates a converter with supplied composer schema.
     *
     * @param composerSchema The schema to use while composing text output.
     * @param beanMap        The bean map to use to map schema names to bean properties. This {@link BeanMap} instance will be used as is,
     *                       so it needs to contain
     *                       mapping for all values that should be converted to text. If you want to use a {@link BeanMap} that is created
     *                       from a combination of the schema and an additional override {@link BeanMap} you can use the method {@link BeanMap#ofSchema(Schema, BeanMap)} to create such combined instance.
     * @param writer         The writer to write text output to. Caller is responsible for either closing the writer or call the close method of the created instance.
     */
    @SuppressWarnings("WeakerAccess")
    public Bean2TextConverter(Schema<?> composerSchema, BeanMap beanMap, Writer writer) {
        assert composerSchema != null;
        beanMarshaller = new BeanMarshaller<>(beanMap);
        textComposer = new TextComposer(composerSchema, writer);
    }

    /**
     * Converts supplied bean into a text output. To be called once for each bean that should be written to the output.
     *
     * @param bean The bean to convert
     * @return True if successfully composed an output line. False if no line was composed.
     */
    public boolean convert(T bean) {
        return beanMarshaller.marshal(bean, errorConsumer, lineNumber++).map(line -> {
            for (LineManipulator manipulator : manipulators) {
                if (!manipulator.manipulate(line))
                    return false;
            }
            textComposer.composeLine(line);
            return true;
        }).orElse(false);
    }

    /**
     * Use {@link #setErrorConsumer(Consumer) instead!}
     * @param errorEventListener The error event listener to send error events to.
     */
    @Deprecated
    public void setErrorEventListener(ErrorEventListener errorEventListener) {
        this.errorConsumer = e->errorEventListener.errorEvent(new ErrorEvent(this, e));
    }

    /**
     * @param errorConsumer The error consumer that will handle errors. By default, an exception is thrown.
     */
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    /**
     * Adds LineManipulator to this converter. All present line manipulators are executed for each
     * line in the same order that they were added.
     *
     * @param manipulator The line manipulator to add.
     */
    public void addLineManipulator(LineManipulator manipulator) {
        manipulators.add(manipulator);
    }

    /**
     * Closes the attached writer.
     * @throws IOException In case of failing to close
     */
    @Override
    public void close() throws IOException {
        this.textComposer.close();
    }
}
