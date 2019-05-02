package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.parse.cell.CellParser;
import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Reader;

/**
 * Parses fixed width text source on cell level.
 */
class FixedWidthCellParser extends CellParser<FixedWidthSchemaCell> {

    FixedWidthCellParser(FixedWidthSchemaCell fixedWidthSchemaCell, int maxCacheSize) {
        super(fixedWidthSchemaCell, maxCacheSize);
    }

    /**
     * Builds a Cell from a reader input.
     *
     * @param lineReader             The input reader
     * @param errorEventListener The error event listener to deliver errors to while parsing.
     * @return A Cell filled with the parsed cell value and with the name of this schema cell.
     * @throws IOException In case there is an error reading from the reader.
     */
    Cell parse(ReadBuffer lineReader, ErrorEventListener errorEventListener) throws IOException {
        String sValue = lineReader.readToString(getSchemaCell(),  0);
        // If EOF
        if(sValue == null) {
            checkIfMandatory(errorEventListener);
            return null;
        }
        // The expected behaviour when facing an empty numeric field is to use the default value also when the field is
        // filled with space, regardless of pad character.
        if(!getSchemaCell().isMandatory() && getSchemaCell().getPadCharacter() != ' ' && isDefaultValue()){
            if(!sValue.isEmpty() && sValue.trim().isEmpty())
                sValue = "";
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
