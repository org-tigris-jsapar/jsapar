package org.jsapar.parse;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.Line;
import org.jsapar.parse.line.ValidationHandler;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * HistoryLog line closes the OrderLine line and the Discount line has no master to belong to. A top level line is passed on to the downstream consumer when the next top
 * level line arrives or when {@link #flush()} is called, so {@link #flush()} must be called when there are no more
 * lines, or the last top level line is never passed on.
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
    /** The open lines: index 0 is the current top level line, the last one is the deepest open detail line. */
    private final List<Line> open = new ArrayList<>(4);
    private final ValidationHandler validationHandler = new ValidationHandler();

    /**
     * @param schema             The schema that defines the parent line type of each detail line type.
     * @param downstream         The consumer that receives the top level lines, with their detail lines added.
     * @param errorConsumer      The consumer that receives errors about detail lines without a master line.
     * @param onOrphanDetailLine The action to take when a detail line has no preceding master line to belong to.
     *                           See {@link org.jsapar.text.TextParseConfig#getOnOrphanDetailLine()}.
     */
    public MasterDetailLineConsumer(Schema<? extends SchemaLine<?>> schema,
                                    Consumer<Line> downstream,
                                    Consumer<JSaParException> errorConsumer,
                                    ValidationAction onOrphanDetailLine) {
        this.downstream = Objects.requireNonNull(downstream);
        this.errorConsumer = Objects.requireNonNull(errorConsumer);
        this.onOrphanDetailLine = Objects.requireNonNull(onOrphanDetailLine);
        for (SchemaLine<?> schemaLine : schema) {
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
        for (int i = open.size() - 1; i >= 0; i--) {
            Line master = open.get(i);
            if (master.getLineType().equals(parent)) {
                open.subList(i + 1, open.size()).clear(); // Deeper levels are complete.
                master.addDetail(line);
                open.add(line); // May become master of other detail lines.
                return;
            }
        }
        validationHandler.lineValidation(line.getLineNumber(), onOrphanDetailLine, errorConsumer,
                () -> "The detail line of type '" + line.getLineType() + "' has no preceding line of type '" + parent
                        + "' to belong to. The line is skipped.");
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
        downstream.accept(topLevelLine);
    }
}
