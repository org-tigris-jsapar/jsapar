package org.jsapar.concurrent;

import org.jsapar.compose.Composer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.parse.ParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A multi threaded version of {@link AbstractConverter} where the composer is started in a separate worker
 * thread.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 *
 * @see ConcurrentConvertTask
 *
 */
public abstract class AbstractConcurrentConverter extends AbstractConverter{
    private final Schema parseSchema;
    private final Schema composeSchema;
    private List<Runnable>  onStart     = new LinkedList<>();
    private List<Runnable>  onStop      = new LinkedList<>();

    public AbstractConcurrentConverter(Schema parseSchema, Schema composeSchema) {
        this.parseSchema = parseSchema;
        this.composeSchema = composeSchema;
    }

    /**
     * @param parseTask The parse task to use
     * @param composer The composer to use
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    protected long convert(ParseTask parseTask, Composer composer) throws IOException {
        ConcurrentConvertTask convertTask = new ConcurrentConvertTask(parseTask, composer);
        onStart.forEach(convertTask::registerOnStart);
        onStop.forEach(convertTask::registerOnStop);
        return execute(convertTask);
    }


    /**
     * Each registered onStart runnable will be called in the same order that they were registered by consumer thread
     * when it starts up but before it starts handling any event. Use this in order to
     * implement initialization needed for the new
     * thread.
     * @param onStart The runnable that will be called by consumer thread when starting up.
     */
    public void registerOnStart(Runnable onStart){
        this.onStart.add(onStart);
    }

    /**
     * Each registered onStop runnable will be called in the same order that they were registered by consumer
     * thread just before it dies. Use this in order to
     * implement resource dealoccation etc. These handlers are called also when the thread is terminated with an exception so
     * be aware that you may end up here also when a serious error has occurred.
     * @param onStop The runnable that will be called by consumer thread when stopping.
     */
    public void registerOnStop(Runnable onStop){
        this.onStop.add(onStop);
    }

    public Schema getParseSchema() {
        return parseSchema;
    }

    public Schema getComposeSchema() {
        return composeSchema;
    }
}
