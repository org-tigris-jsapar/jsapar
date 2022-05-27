package org.jsapar.concurrent;

import org.jsapar.Text2TextConverter;
import org.jsapar.compose.Composer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.ParseTask;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.Schema;

/**
 * A multi-threaded version of {@link org.jsapar.Text2TextConverter} where the composer is started in a separate worker
 * thread.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 * <p>
 * As a rule of thumb while working with normal files on disc, don't use this concurrent version unless your input
 * normally exceeds at least 1MB of data, as the overhead of starting
 * a new thread and synchronizing threads is otherwise greater than the gain by the concurrency.
 *
 * @see ConcurrentConvertTask
 * @see org.jsapar.Text2TextConverter
 */
public class ConcurrentText2TextConverter extends Text2TextConverter implements ConcurrentStartStop{
    private final ConcurrentConvertTaskFactory convertTaskFactory = new ConcurrentConvertTaskFactory();

    /**
     * Creates a concurrent text to text converter that can be used to convert between different text based formats.
     * @param parseSchema The schema to use while parsing
     * @param composeSchema The schema to use wile composing.
     */
    public ConcurrentText2TextConverter(Schema<?> parseSchema, Schema<?> composeSchema) {
        super(parseSchema, composeSchema);
    }

    /**
     * Creates a concurrent text to text converter that can be used to convert between different text based formats.
     * @param parseSchema The schema to use while parsing
     * @param composeSchema The schema to use wile composing.
     * @param parseConfig   Configuration about parsing behavior.
     */
    public ConcurrentText2TextConverter(Schema<?> parseSchema, Schema<?> composeSchema, TextParseConfig parseConfig) {
        super(parseSchema, composeSchema, parseConfig);
    }

    @Override
    protected ConvertTask makeConvertTask(ParseTask parseTask, Composer composer) {
        return convertTaskFactory.makeConvertTask(parseTask, composer, getErrorConsumer(), getTransformer(), getManipulators());
    }

    public void registerOnStart(Runnable onStart) {
        this.convertTaskFactory.registerOnStart(onStart);
    }

    public void registerOnStop(Runnable onStop) {
        this.convertTaskFactory.registerOnStop(onStop);
    }

}
