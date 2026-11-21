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
    private final ParseTask parseTask;

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
        return line->{
            if (manipulate(line, lineManipulators))
                composer.composeLine(line);
        };
    }

    /**
     * Calls the line manipulators for a line and then, recursively, for each of its detail lines, see
     * {@link Line#getDetails()}. The manipulators are called for the master line before its detail lines, in the
     * same order as the lines were read. A detail line for which a manipulator returns false is removed from its
     * master line together with its own detail lines.
     * @param line             The line to manipulate.
     * @param lineManipulators The line manipulators to call, in order. The remaining manipulators are not called for
     *                         a line once one of them returns false.
     * @return False if one of the manipulators returned false for the line itself, which means that the line, and
     * all its detail lines, should be omitted.
     * @since 2.5
     */
    public static boolean manipulate(Line line, List<LineManipulator> lineManipulators) {
        for (LineManipulator manipulator : lineManipulators) {
            if (!manipulator.manipulate(line))
                return false;
        }
        if (line.hasDetails())
            line.removeDetailsIf(detail -> !manipulate(detail, lineManipulators));
        return true;
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
