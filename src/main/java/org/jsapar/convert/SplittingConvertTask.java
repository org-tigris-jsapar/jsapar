package org.jsapar.convert;

import org.jsapar.compose.Composer;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.MulticastConsumer;
import org.jsapar.parse.ParseTask;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Reads from supplied parseTask and outputs each line to the composer. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. This is a flavor of {@link ConvertTask} that allows to also split one line into several lines before
 * forwarding them to the converter.
 * <p>
 * For each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 * <p>
 * If your want lines to be discarded tranformed or splitted depending of their contents, use a line manipulator that returns
 * a stream of the lines that you want to forward to the composer.
 * @see ConvertTask
 */
public class SplittingConvertTask {
    private ParseTask                    parseTask;
    private Composer                     composer;
    private Function<Line, Stream<Line>> manipulator = Stream::of;

    /**
     * Creates a convert task with supplied parse task and composer.
     *
     * @param parseTask The parse task to use.
     * @param composer  The composer to use.
     */
    public SplittingConvertTask(ParseTask parseTask, Composer composer) {
        this.parseTask = parseTask;
        this.composer = composer;
    }

    /**
     * Sets new error listener to both the parse task and the composer. Can be called after creation but before calling
     * {@link #execute()}.
     *
     * @param errorListener The new error event listener to use for both parsing and composing.
     */
    @Deprecated
    public void setErrorEventListener(ErrorEventListener errorListener) {
        this.parseTask.setErrorEventListener(errorListener);
        this.composer.setErrorEventListener(errorListener);
    }

    /**
     * Sets an error consumer to this parser. Default behavior otherwise is to throw an exception upon the first
     * error. If you want more than one consumer to get each error event, use a {@link MulticastConsumer}.
     *
     * @param errorConsumer The error consumer.
     */
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        this.parseTask.setErrorConsumer(errorConsumer);
        this.composer.setErrorConsumer(errorConsumer);
    }

    /**
     * Assigns a line manipulator to this converter. The manipulator should return a stream of all the lines that should
     * be forwarded to the composer. Default is just Stream::of
     *
     * @param manipulator The line manipulator to use.
     */
    public void setLineManipulator(Function<Line, Stream<Line>> manipulator) {
        manipulator = manipulator;
    }

    /**
     * @return Number of converted lines.
     * @throws IOException In case of IO error.
     */
    public long execute() throws IOException {
        try {
            parseTask.setLineConsumer(this::forEachLine);
            return parseTask.execute();
        } catch (UncheckedIOException e) {
            throw e.getCause() != null ? e.getCause() : new IOException(e);
        }
    }

    public ParseTask getParseTask() {
        return parseTask;
    }

    public Composer getComposer() {
        return composer;
    }

    protected void forEachLine(Line line) {
        manipulator.apply(line).forEach(composer::composeLine);
    }
}
