package org.jsapar;

import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.LineManipulator;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ExceptionErrorEventListener;
import org.jsapar.parse.bean.BeanMap;
import org.jsapar.parse.bean.BeanParser;
import org.jsapar.schema.Schema;

import java.beans.IntrospectionException;
import java.io.Writer;

/**
 * Converts from beans to text output. This implementation accepts beans pushed one by one to be converted. See
 * {@link BeanCollection2TextConverter} for an implementation where you provide a stream or a collection of beans from which
 * beans are pulled. This means that this class acts more like a Composer compared to other Converter in the sense that you need to provide the writer
 * in the constructor and each call to {@link #convert(Object)} method is done without supplying any writer.
 * <p>
 * The Generic type T should be set to a common base class of all the expected beans. Use Object as
 * base class if there is no common base class for all beans.
 * <p/>
 * Instances of this class should only be used once, then disposed.
 * <p>
 * ExampleUsage:
 * <pre>{@code
 * Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(schema, writer);
 * converter.convert(person1);
 * converter.convert(person2);
 * }</pre>
 *
 * @see BeanCollection2TextConverter
 */
@SuppressWarnings("WeakerAccess")
public class Bean2TextConverter<T> extends AbstractConverter {

    private final BeanParser<T> beanParser;
    private final TextComposer  textComposer;
    private long               lineNumber         = 1;
    private ErrorEventListener errorEventListener = new ExceptionErrorEventListener();

    /**
     * Creates a converter with supplied composer schema.
     *
     * @param composerSchema The schema to use while composing text output.
     * @param writer         The writer to write text output to
     * @throws IntrospectionException If string names of properties could not be mapped to actual properties.
     * @throws ClassNotFoundException In case any of the classes described in the schema does not exist in the classpath.
     */
    public Bean2TextConverter(Schema composerSchema, Writer writer)
            throws IntrospectionException, ClassNotFoundException {
        this(composerSchema, BeanMap.ofSchema(composerSchema), writer);
    }

    /**
     * Creates a converter with supplied composer schema.
     *
     * @param composerSchema The schema to use while composing text output.
     * @param beanMap        The bean map to use to map schema names to bean properties.
     */
    public Bean2TextConverter(Schema composerSchema, BeanMap beanMap, Writer writer) {
        assert composerSchema != null;
        beanParser = new BeanParser<>(beanMap);
        textComposer = new TextComposer(composerSchema, writer);
    }

    /**
     * Converts supplied bean into a text output. To be called once for each bean that should be written to the output.
     *
     * @param bean The bean to convert
     */
    public void convert(T bean) {
        beanParser.parseBean(bean, errorEventListener, lineNumber++).ifPresent(line -> {
            for (LineManipulator manipulator : getManipulators()) {
                if (!manipulator.manipulate(line))
                    return;
            }
            textComposer.composeLine(line);
        });
    }

    public ErrorEventListener getErrorEventListener() {
        return errorEventListener;
    }

    @Override
    public void setErrorEventListener(ErrorEventListener errorEventListener) {
        this.errorEventListener = errorEventListener;
    }
}
