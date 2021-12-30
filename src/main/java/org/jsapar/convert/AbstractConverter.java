package org.jsapar.convert;

import org.jsapar.compose.Composer;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.ParseTask;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Abstract base class for all converters.
 * <p>
 * By adding
 * a transformer or a LineManipulator you are able to make modifications of each line before it is forwarded to the
 * composer. The method {@link LineManipulator#manipulate(Line)} of all added LineManipulators are called for each line that are
 * parsed successfully.
 * <p>
 * For each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 * <p>
 * If your want lines to be discarded from the output depending of their contents, add a LineManipulator that returns
 * false for the lines that should not be composed. An alternative is to use a transformer that also allows to split
 * the line into multiple line instances before they are forwarded to the composer.
 * <p>
 * The default error handling is to throw an exception upon the first error that occurs. You can however change that
 * behavior by adding an error consumer with {@link #setErrorConsumer(Consumer)}. There are several implementations to
 * choose from such as
 * {@link org.jsapar.parse.CollectingConsumer} or
 * {@link org.jsapar.error.ThresholdCollectingErrorConsumer}, or you may implement your own.
 */
public abstract class AbstractConverter {

    private final List<LineManipulator>      manipulators  = new java.util.LinkedList<>();
    private Consumer<JSaParException>  errorConsumer = new ExceptionErrorConsumer();
    private Function<Line, List<Line>> transformer;

    public AbstractConverter() {
    }

    /**
     * Adds LineManipulator to this converter. All present line manipulators are executed for each
     * line.
     * <b>You cannot combine line manipulators with a transformer. Once a transformer is assigned, all line manipulators
     * are ignored.</b>
     *
     * @param manipulator The line manipulator to add.
     * @see #setTransformer(Function)
     */
    public void addLineManipulator(LineManipulator manipulator) {
        manipulators.add(manipulator);
    }

    /**
     * Replaces existing error event listener.
     * @param errorListener The new error event listener to use.
     */
    @Deprecated
    public void setErrorEventListener(ErrorEventListener errorListener) {
        this.errorConsumer = e-> errorListener.errorEvent(new ErrorEvent(this, e));
    }

    /**
     * Replaces existing error event listener.
     * @param errorConsumer The new error consumer
     */
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer){
        this.errorConsumer = errorConsumer;
    }

    /**
     * Executes the convert task and applies all manipulators in order..
     * @param parseTask The parse task to parse from
     * @param composer The composer that should compose output.
     * @return The number of lines converted.
     * @throws IOException In case of io error while writing output.
     */
    protected long execute(ParseTask parseTask, Composer composer) throws IOException {
        return makeConvertTask(parseTask, composer).execute();
    }

    protected ConvertTask makeConvertTask(ParseTask parseTask, Composer composer){
        if(transformer != null)
            return ConvertTask.of(parseTask, composer, errorConsumer, transformer);
        if(!manipulators.isEmpty())
            return ConvertTask.of(parseTask, composer, errorConsumer, manipulators);
        return ConvertTask.of(parseTask, composer, errorConsumer);
    }
    /**
     * Assigns a line transformer to this converter. The transformer should return a stream of all the lines that should
     * be forwarded to the composer. Default is to iterate the line manipulators and use the result if all of them return true.
     * <p>
     * <b>Assigning a new line transformer will completely disable all registered line manipulators.</b>
     *
     * @param transformer The transformer to use.
     */
    public void setTransformer(Function<Line, List<Line>> transformer) {
        Objects.requireNonNull(transformer);
        this.transformer = transformer;
    }

    protected List<LineManipulator> getManipulators() {
        return manipulators;
    }

    protected Function<Line, List<Line>> getTransformer() {
        return transformer;
    }

    protected Consumer<JSaParException> getErrorConsumer() {
        return errorConsumer;
    }
}
