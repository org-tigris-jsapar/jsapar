package org.jsapar.convert;

import org.jsapar.compose.Composer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.ParseTask;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Reads from supplied parseTask and outputs each line to the composer. By adding
 * a transformer you are able to make modifications of each line before it is written to the
 * output.
 * <p>
 * For each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 */
public class ConvertTask {
    private ParseTask parseTask;

    /**
     * Creates a convert task with supplied parse task and composer.
     * @param parseTask The parse task to use.
     * @param composer The composer to use.
     * @param errorConsumer The error consumer to use.
     */
    protected ConvertTask(ParseTask parseTask, Composer composer, Consumer<Line> lineConsumer, Consumer<JSaParException> errorConsumer) {
        Objects.requireNonNull(parseTask);
        Objects.requireNonNull(composer);
        Objects.requireNonNull(lineConsumer);
        Objects.requireNonNull(errorConsumer);
        this.parseTask = parseTask;
        this.parseTask.setLineConsumer(lineConsumer);
        this.parseTask.setErrorConsumer(errorConsumer);
        composer.setErrorConsumer(errorConsumer);
    }

    public static ConvertTask of(ParseTask parseTask, Composer composer, Consumer<JSaParException> errorConsumer){
        return new ConvertTask(parseTask, composer, composer::composeLine, errorConsumer);
    }

    public static ConvertTask of(ParseTask parseTask, Composer composer, Consumer<JSaParException> errorConsumer, List<LineManipulator> lineManipulators){
        Objects.requireNonNull(lineManipulators, "Transformer cannot be null");
        return new ConvertTask(parseTask, composer, makeManipulateAndComposeConsumer(composer, lineManipulators), errorConsumer);
    }

    public static Consumer<Line> makeManipulateAndComposeConsumer(Composer composer, List<LineManipulator> lineManipulators) {
        if(lineManipulators.isEmpty()){
            return composer::composeLine;
        }
        if(lineManipulators.size() == 1){
            final LineManipulator manipulator = lineManipulators.get(0);
            return line->{
                if(manipulator.manipulate(line))
                    composer.composeLine(line);
            };
        }
        return line->{
            for (LineManipulator manipulator : lineManipulators) {
                if (!manipulator.manipulate(line))
                    return;
            }
            composer.composeLine(line);
        };
    }

    public static ConvertTask of(ParseTask parseTask, Composer composer, Consumer<JSaParException> errorConsumer, Function<Line, List<Line>> transformer){
        Objects.requireNonNull(transformer, "Transformer cannot be null");
        return new ConvertTask(parseTask, composer, line-> transformer.apply(line).forEach(composer::composeLine), errorConsumer);
    }

    /**
     * @return Number of converted lines.
     * @throws IOException In case of IO error.
     */
    public long execute() throws IOException {
        try {
            return parseTask.execute();
        }catch (UncheckedIOException e){
            throw e.getCause() != null ? e.getCause() : new IOException(e);
        }
    }


    public ParseTask getParseTask() {
        return parseTask;
    }

}
