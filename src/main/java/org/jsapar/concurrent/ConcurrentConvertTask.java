package org.jsapar.concurrent;

import org.jsapar.compose.Composer;
import org.jsapar.convert.ConvertTask;
import org.jsapar.model.Line;
import org.jsapar.parse.ParseTask;

import java.io.IOException;

/**
 * Concurrent version of the {@link ConvertTask}. The composer is executed in a separate worker thread. Also the line
 * manipulators are all executed in the worker thread.
 * <p>
 * Reads from supplied parser and outputs each line to the composer. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method {@link org.jsapar.convert.LineManipulator#manipulate(Line)} of all added LineManipulators are
 * called for each line that is successfully parsed.
 * <p>
 * For each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 * <p>
 * If your want lines to be discarded from the output depending of their contents, add a LineManipulator that returns
 * false for the lines that should not be composed.
 *
 */
public class ConcurrentConvertTask extends ConvertTask {
    private ConcurrentLineEventListener concurrentLineEventListener = new ConcurrentLineEventListener(new LineForwardListener());
    /** Creates a converter
     * @param parseTask The parseTask to use while parsing
     * @param composer The composer to use while composing.
     */
    public ConcurrentConvertTask(ParseTask parseTask, Composer composer) {
        super(parseTask, composer);
    }

    public long execute() throws IOException {
        try (ConcurrentLineEventListener lineEventListener = this.concurrentLineEventListener) {
            getParseTask().setLineEventListener(lineEventListener);
            lineEventListener.start();
            return getParseTask().execute();
        }
    }


    /**
     * Each registered onStart runnable will be called in the same order that they were registered by consumer thread
     * when it starts up but before it starts handling any event. Use this in order to
     * implement initialization needed for the new
     * thread.
     * @param onStart The runnable that will be called by consumer thread when starting up.
     */
    public void registerOnStart(Runnable onStart){
        this.concurrentLineEventListener.registerOnStart(onStart);
    }

    /**
     * Each registered onStop runnable will be called in the same order that they were registered by consumer
     * thread just before it dies. Use this in order to
     * implement resource dealoccation etc. These handlers are called also when the thread is terminated with an exception so
     * be aware that you may end up here also when a serious error has occurred.
     * @param onStop The runnable that will be called by consumer thread when stopping.
     */
    public void registerOnStop(Runnable onStop){
        this.concurrentLineEventListener.registerOnStop(onStop);
    }

}
