package org.jsapar.parse.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.parse.cell.CellParser;
import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Parses fixed width text source on cell level.
 */
class FixedWidthCellParser extends CellParser<FixedWidthSchemaCell> {
    private final ReadBuffer.Trimmer trimmer;

    FixedWidthCellParser(FixedWidthSchemaCell fixedWidthSchemaCell, int maxCacheSize) {
        super(fixedWidthSchemaCell, maxCacheSize);
        this.trimmer = ReadBuffer.makeTrimmer(fixedWidthSchemaCell);
    }

    /**
     * Builds a Cell from a reader input.
     *
     * @param lineReader             The input reader
     * @param errorEventListener The error event listener to deliver errors to while parsing.
     * @return A Cell filled with the parsed cell value and with the name of this schema cell.
     * @throws IOException In case there is an error reading from the reader.
     */
    Cell parse(ReadBuffer lineReader, Consumer<JSaParException> errorEventListener) throws IOException {
        String sValue = lineReader.readToString(trimmer,  0, getSchemaCell().getLength());
        // If EOF
        if(sValue == null) {
            checkIfMandatory(errorEventListener);
            return null;
        }
        return super.parse(sValue, errorEventListener);
    }

    /**
     * Creates fixed width cell parser according to supplied schema and with a maximum cache size.
     * @param schemaCell The schema to use.
     * @param maxCacheSize The maximum number of cells to keep in cache while parsing. The value 0 will disable cache.
     */
    static FixedWidthCellParser ofSchemaCell(FixedWidthSchemaCell schemaCell, int maxCacheSize) {
        return new FixedWidthCellParser(schemaCell, maxCacheSize);
    }

}
