package org.jsapar.concurrent;

import org.jsapar.compose.Composer;
import org.jsapar.convert.ConvertTask;
import org.jsapar.convert.LineManipulator;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.ParseTask;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Internal class for creating concurrent convert task with registered runnable. Makes it possible to first register
 * runnables and then create many convert tasks using the same runnables.
 *
 * @see ConcurrentConvertTask
 *
 */
class ConcurrentConvertTaskFactory implements ConcurrentStartStop{
    private final List<Runnable>  onStart     = new LinkedList<>();
    private final List<Runnable>  onStop      = new LinkedList<>();

    /**
     * @param parseTask     The parse task to use
     * @param composer      The composer to use
     * @param errorConsumer The error consumer to use.
     * @return Number of converted lines.
     */
    public ConvertTask makeConvertTask(ParseTask parseTask,
                                       Composer composer,
                                       Consumer<JSaParException> errorConsumer,
                                       Function<Line, List<Line>> transformer,
                                       List<LineManipulator> lineManipulators) {
        if (transformer != null)
            return makeConvertTask(parseTask, composer, line -> transformer.apply(line).forEach(composer::composeLine),
                    errorConsumer);
        if (!lineManipulators.isEmpty())
            return makeConvertTask(parseTask, composer,
                    ConvertTask.makeManipulateAndComposeConsumer(composer, lineManipulators), errorConsumer);
        return makeConvertTask(parseTask, composer, composer::composeLine, errorConsumer);
    }


    private ConvertTask makeConvertTask(ParseTask parseTask, Composer composer, Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer)  {
        ConcurrentConvertTask convertTask = new ConcurrentConvertTask(parseTask, composer, lineConsumer, errorConsumer);
        onStart.forEach(convertTask::registerOnStart);
        onStop.forEach(convertTask::registerOnStop);
        return convertTask;
    }


    public void registerOnStart(Runnable onStart){
        this.onStart.add(onStart);
    }

    public void registerOnStop(Runnable onStop){
        this.onStop.add(onStop);
    }

}
