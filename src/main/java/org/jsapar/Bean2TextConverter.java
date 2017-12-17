package org.jsapar;

import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.bean.BeanParseConfig;
import org.jsapar.parse.bean.BeanParseTask;
import org.jsapar.parse.bean.BeanParseTaskForConvert;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Converts from beans to text output.
 * The Generic type T should be set to a common base class of all the expected beans. Use Object as
 * base class if there is no common base class for all beans.
 * <p/>
 * ExampleUsage:
 * <pre>{@code
  Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(schema);
  converter.convert(people, writer);
 * }</pre>
 */
@SuppressWarnings("WeakerAccess")
public class Bean2TextConverter<T> extends AbstractConverter {

    private final Schema composerSchema;

    /**
     * Creates a converter with supplied composer schema.
     * @param composerSchema The schema to use while composing text output.
     */
    public Bean2TextConverter(Schema composerSchema) {
        assert composerSchema != null;
        this.composerSchema = composerSchema;
    }

    /**
     * Converts objects referenced by supplied stream into a text output written to supplied writer.
     * @param stream The stream to get beans from.
     * @param writer The text writer to write text output to.
     * @throws IOException If there is an error writing text output.
     */
    public void convert(Stream<? extends T> stream, Writer writer) throws IOException {
        TextComposer composer = new TextComposer(this.composerSchema, writer);
        BeanParseTaskForConvert<T> parseTask = new BeanParseTaskForConvert<>(stream);
        ConvertTask convertTask = new ConvertTask(parseTask, composer);
        execute(convertTask);
    }

    /**
     * Converts objects referenced by supplied iterator into a text output written to supplied writer.
     * @param iterator The iterator to get beans from.
     * @param writer The text writer to write text output to.
     * @throws IOException If there is an error writing text output.
     */
    public void convert(Iterator<? extends T> iterator, Writer writer) throws IOException {
        TextComposer composer = new TextComposer(this.composerSchema, writer);
        BeanParseTaskForConvert<T> parseTask = new BeanParseTaskForConvert<>(iterator);
        ConvertTask convertTask = new ConvertTask(parseTask, composer);
        execute(convertTask);
    }

    /**
     * Converts objects of supplied collection into a text output written to supplied writer.
     * @param collection The collection of beans to convert
     * @param writer The text writer to write text output to.
     * @throws IOException If there is an error writing text output.
     */
    public void convert(Collection<? extends T> collection, Writer writer) throws IOException {
        convert(collection.stream(), writer);
    }

}
