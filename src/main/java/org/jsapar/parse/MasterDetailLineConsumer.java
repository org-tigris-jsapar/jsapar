package org.jsapar.parse;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Line;
import org.jsapar.parse.line.ValidationHandler;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Groups detail lines under their master line and passes only top level lines on to the downstream line consumer.
 * Which line types are detail lines, and of which master line type, is given by
 * {@link org.jsapar.schema.SchemaLine.Builder#withParent(String)}.
 * <p>
 * Lines are expected to come as a tree written top-down: each master line followed by its detail lines. Each detail
 * line is added, see {@link Line#addDetail(Line)}, to the closest preceding line of its parent line type that is
 * still open. A line closes all open lines below the level it is added to, so in the sequence Order, OrderLine,
 * HistoryLog, Discount (where OrderLine and HistoryLog have parent Order and Discount has parent OrderLine) the
 * HistoryLog line closes the OrderLine line and the Discount line has no master to belong to.
 * <p>
 * A line that is omitted, for instance because of a validation error, is reported with {@link #lineSkipped(String)}.
 * It closes the open lines at and below its own level, so the detail lines that follow it are orphans instead of being
 * added to a previous master line. A line of a line type with ignoreread is reported with
 * {@link #lineIgnored(String)}. It closes the open lines the same way, and its detail lines, recursively, are ignored
 * too, without errors. A line of an undefined line type has no known level and leaves the open lines as they are.
 * <p>
 * A top level line is passed on to the downstream consumer when the next top level line arrives or when
 * {@link #flush()} is called, so {@link #flush()} must be called when there are no more lines, or the last top level
 * line is never passed on.
 * <p>
 * The text parsers use this class automatically when the schema has detail lines, so normally there is no need to
 * use it directly.
 * <p>
 * Note that one top level line together with all of its detail lines is kept in memory until it is complete.
 * @since 2.5
 */
public final class MasterDetailLineConsumer implements Consumer<Line> {

    private final Consumer<Line> downstream;
    private final Consumer<JSaParException> errorConsumer;
    private final ValidationAction onOrphanDetailLine;
    /** Parent line type by detail line type. */
    private final Map<String, String> parentOf = new HashMap<>();
    /** All line types of the schema. */
    private final Set<String> lineTypes = new HashSet<>();
    /** The open lines: index 0 is the current top level line, the last one is the deepest open detail line. */
    private final List<Line> open = new ArrayList<>(4);
    private final ValidationHandler validationHandler = new ValidationHandler();

    /**
     * @param schema             The schema that defines the parent line type of each detail line type.
     * @param downstream         The consumer that receives the top level lines, with their detail lines added.
     * @param errorConsumer      The consumer that receives errors about detail lines without a master line.
     * @param onOrphanDetailLine The action to take when a detail line has no preceding master line to belong to.
     *                           See {@link org.jsapar.text.TextParseConfig#getOnOrphanDetailLine()}.
     * @throws org.jsapar.schema.SchemaException If a parent line type does not exist in the schema or if parent line
     *                           types form a loop, see {@link Schema#validateParentLineTypes()}.
     */
    public MasterDetailLineConsumer(Schema<? extends SchemaLine<?>> schema,
                                    Consumer<Line> downstream,
                                    Consumer<JSaParException> errorConsumer,
                                    ValidationAction onOrphanDetailLine) {
        this.downstream = Objects.requireNonNull(downstream);
        this.errorConsumer = Objects.requireNonNull(errorConsumer);
        this.onOrphanDetailLine = Objects.requireNonNull(onOrphanDetailLine);
        schema.validateParentLineTypes(); // Lines may have been added or changed after the schema was built.
        for (SchemaLine<?> schemaLine : schema) {
            lineTypes.add(schemaLine.getLineType());
            schemaLine.getParentLineType().ifPresent(parent -> parentOf.put(schemaLine.getLineType(), parent));
        }
    }

    /**
     * Adds a line. A top level line completes the previous top level line, which is then passed on to the downstream
     * consumer. A detail line is added to its master line.
     * @param line the input argument
     */
    @Override
    public void accept(Line line) {
        String parent = parentOf.get(line.getLineType());
        if (parent == null) {
            flush();
            open.add(line);
            return;
        }
        Line master = closeBelow(parent);
        if (master instanceof IgnoredLine) {
            open.add(new IgnoredLine(line.getLineType())); // Ignored together with its master, and so are its details.
            return;
        }
        if (master != null) {
            master.addDetail(line);
            open.add(line); // May become master of other detail lines.
            return;
        }
        validationHandler.lineValidation(line.getLineNumber(), onOrphanDetailLine, errorConsumer,
                () -> "The detail line of type '" + line.getLineType() + "' has no preceding line of type '" + parent
                        + "' to belong to. The line is skipped.");
    }

    /**
     * Tells that a line of a line type was read but not passed on, for instance because it was omitted due to a
     * validation error. The open lines at and below the level of the line type are closed, so that its detail lines
     * that follow become orphans instead of being added to a previous master line. If the line type is a top level
     * line type, the current top level line is passed on to the downstream consumer.
     * @param lineType The line type of the skipped line. Line types that are not in the schema are ignored.
     */
    public void lineSkipped(String lineType) {
        if (!lineTypes.contains(lineType))
            return;
        String parent = parentOf.get(lineType);
        if (parent == null)
            flush();
        else
            closeBelow(parent);
    }

    /**
     * Tells that a line of a line type with ignoreread was read. The open lines at and below the level of the line
     * type are closed, as for {@link #lineSkipped(String)}, but the detail lines that follow it, and their detail lines,
     * are ignored too, without being reported as orphans. This is the same as when composing, where the detail lines of
     * a line that is not written are not written either.
     * @param lineType The line type of the ignored line. Line types that are not in the schema are ignored.
     */
    public void lineIgnored(String lineType) {
        if (!lineTypes.contains(lineType))
            return;
        String parent = parentOf.get(lineType);
        if (parent == null)
            flush();
        else
            closeBelow(parent);
        open.add(new IgnoredLine(lineType));
    }

    /**
     * Closes all open lines below the closest open line of the given line type.
     * @param lineType The line type of the master line to find.
     * @return The closest open line of the line type, or null if there is none, in which case nothing is closed.
     */
    private Line closeBelow(String lineType) {
        for (int i = open.size() - 1; i >= 0; i--) {
            Line master = open.get(i);
            if (master.getLineType().equals(lineType)) {
                open.subList(i + 1, open.size()).clear(); // Deeper levels are complete.
                return master;
            }
        }
        return null;
    }

    /**
     * Passes the current top level line, if any, on to the downstream consumer. Must be called when there are no more
     * lines to add.
     */
    public void flush() {
        if (open.isEmpty())
            return;
        Line topLevelLine = open.getFirst();
        open.clear();
        if (!(topLevelLine instanceof IgnoredLine))
            downstream.accept(topLevelLine);
    }

    /**
     * Holds the place of an ignored line among the open lines, so that its detail lines are ignored too.
     */
    private static final class IgnoredLine extends Line {
        private IgnoredLine(String lineType) {
            super(lineType);
        }
    }
}
