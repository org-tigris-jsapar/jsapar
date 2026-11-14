package org.jsapar.text;

import org.jsapar.error.ValidationAction;

/**
 * Configuration that controls behavior while parsing text.
 */
public class TextParseConfig {

    public TextParseConfig() {}

    /**
     * The action to take if the cell value conditions of the line does not match any of the defined line types
     * within the schema. Default is to throw exception.
     */
    private ValidationAction onUndefinedLineType = ValidationAction.EXCEPTION;
    /**
     * The action to take if there is insufficient input to build a complete line. For Csv, this happens for
     * instance when there are too few columns. For fixed width files this happens when the line is too short.
     * Default is no action.
     */
    private ValidationAction onLineInsufficient = ValidationAction.NONE;

    /**
     * The action to take if there is too much data in the input after parsing a complete line. For Csv, this happens for
     * instance when there are too many columns. For fixed width files this happens when the line is too long compared to what is defined in the schema.
     * Default is no action.
     */
    private ValidationAction onLineOverflow = ValidationAction.NONE;

    /**
     * @see #setMaxCellCacheSize(int)
     */
    private int maxCellCacheSize = AUTOMATIC_CELL_CACHE_SIZE;

    /**
     * Value of {@link #getMaxCellCacheSize()} when no cache size is set and a default is chosen per cell type.
     * @since 2.5
     */
    public static final int AUTOMATIC_CELL_CACHE_SIZE = -1;

    /**
     * The maximum number of characters that can occur on one line. Default is 8k. Since this size is used to allocate
     * buffer needed while parsing, setting a smaller number will have positive impact on memory usage and speed while
     * parsing a large number of small files. While parsing a few files of larger size, it can be an advantage to use a
     * larger number here since this allows the parser to allocate a larger buffer.
     */
    private int maxLineLength = 1024 * 8;

    /**
     * @return The action to take if the cell value conditions of the line does not match any of the defined line types
     * within the schema. Default is to throw exception.
     */
    public ValidationAction getOnUndefinedLineType() {
        return onUndefinedLineType;
    }

    /**
     * @param onUndefinedLineType The action to take if the cell value conditions of the line does not match any of the defined line types
     *                            within the schema. Default is to throw exception.
     */
    public void setOnUndefinedLineType(ValidationAction onUndefinedLineType) {
        this.onUndefinedLineType = onUndefinedLineType;
    }

    /**
     * @return The action to take if there is insufficient input to build a complete line.
     * @see #setOnLineInsufficient(ValidationAction)
     */
    public ValidationAction getOnLineInsufficient() {
        return onLineInsufficient;
    }

    /**
     * @param onLineInsufficient The action to take if there is insufficient input to build a complete line. For Csv, this happens for
     *                           instance when there are too few columns. For fixed width files this happens when the line is too short.
     *                           Default is no action.
     */
    public void setOnLineInsufficient(ValidationAction onLineInsufficient) {
        this.onLineInsufficient = onLineInsufficient;
    }

    /**
     * The action to take if there is too much data in the input after parsing a complete line. For Csv, this happens for
     * instance when there are too many columns. For fixed width files this happens when the line is too long compared to what is defined in the schema.
     * Default is no action.
     *
     * @return The action to take if there is too much data in the input after parsing a complete line.
     */
    public ValidationAction getOnLineOverflow() {
        return onLineOverflow;
    }

    /**
     * @param onLineOverflow The action to take if there is too much data in the input after parsing a complete line. For Csv, this happens for
     *                       instance when there are too many columns. For fixed width files this happens when the line is too long compared to what is defined in the schema.
     *                       Default is no action.
     */
    public void setOnLineOverflow(ValidationAction onLineOverflow) {
        this.onLineOverflow = onLineOverflow;
    }

    /**
     * @return The maximum number of cell values that are cached per cell while parsing, 0 if caching is disabled or
     * {@link #AUTOMATIC_CELL_CACHE_SIZE} if not set, in which case a default is chosen per cell type.
     * @see #setMaxCellCacheSize(int)
     */
    public int getMaxCellCacheSize() {
        return maxCellCacheSize;
    }

    /**
     * The maximum number of cell values that are cached per cell (column) while parsing.
     * <p>
     * For each cell, the same text always results in the same cell value, so the library can remember the last parsed
     * values of a cell and re-use those cell instances instead of parsing again. The cell is immutable so it cannot be
     * altered after creation.
     * <p>
     * If not set, a default is chosen per cell type: 0 (no cache) for string and character cells, where parsing is
     * cheap, 2 for boolean cells and 1 for all other cell types. Caching the last value only is cheap and pays off for
     * columns where the same value often occurs on consecutive lines, for instance dates in a sorted file.
     * <p>
     * A set value applies to all cell types, except that boolean cells never use more than 2 and enum cells never more
     * than the number of enum text values. 0 disables the cache for all cell types. The maximum value is 100.
     * <p>
     * Larger values do not make parsing faster, measurements show the opposite. They can however reduce memory usage
     * when all parsed lines are kept in memory, for instance in a {@link org.jsapar.model.Document}, and the same
     * values occur repeatedly but not on consecutive lines, since lines will then share the same cell instances.
     *
     * @param maxCellCacheSize The maximum number of cell values that are cached per cell while parsing. Limited to the
     *                         range 0-100 where 0 disables caching.
     */
    public void setMaxCellCacheSize(int maxCellCacheSize) {
        this.maxCellCacheSize = Math.clamp(maxCellCacheSize, 0, 100);
    }

    /**
     * @return The maximum number of characters that can occur on one line.
     * @see #setMaxLineLength(int)
     */
    public int getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * Default is 8k. Since this size is used to allocate
     * buffer needed while parsing, setting a smaller number will have positive impact on memory usage and speed while
     * parsing a large number of small files. While parsing a few files of larger size, it can be an advantage to use a
     * larger number here since this allows the parser to allocate a larger buffer.
     * @param maxLineLength The maximum number of characters that can occur on one line.
     */
    public void setMaxLineLength(int maxLineLength) {
        this.maxLineLength = maxLineLength;
    }
}
