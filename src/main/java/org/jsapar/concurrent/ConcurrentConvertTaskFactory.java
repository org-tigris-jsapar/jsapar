package org.jsapar.concurrent;

import org.jsapar.compose.Composer;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.ParseTask;
import org.jsapar.schema.Schema;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Internal class for creating concurrent convert task with registered runnable. Makes it possible to first register
 * runnables and then create many convert tasks using the same runnables.
 *
 * @see ConcurrentConvertTask
 *
 */
public class ConcurrentConvertTaskFactory {
    private final List<Runnable>  onStart     = new LinkedList<>();
    private final List<Runnable>  onStop      = new LinkedList<>();


    /**
     * @param parseTask The parse task to use
     * @param composer The composer to use
     * @return Number of converted lines.
     * @throws IOException In case of IO error
     */
    protected ConvertTask makeConvertTask(ParseTask parseTask, Composer composer)  {
        ConcurrentConvertTask convertTask = new ConcurrentConvertTask(parseTask, composer);
        onStart.forEach(convertTask::registerOnStart);
        onStop.forEach(convertTask::registerOnStop);
        return convertTask;
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
     * implement resource deallocation etc. These handlers are called also when the thread is terminated with an exception so
     * be aware that you may end up here also when a serious error has occurred.
     * @param onStop The runnable that will be called by consumer thread when stopping.
     */
    public void registerOnStop(Runnable onStop){
        this.onStop.add(onStop);
    }

}
