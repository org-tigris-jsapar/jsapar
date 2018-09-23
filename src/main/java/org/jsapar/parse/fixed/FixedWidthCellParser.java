package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.parse.CellParser;
import org.jsapar.schema.FixedWidthSchemaCell;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Optional;

/**
 * Parses fixed width text source on cell level.
 */
public class FixedWidthCellParser extends CellParser<FixedWidthSchemaCell> {
    private FWFieldReader fieldReader = new FWFieldReader();

    FixedWidthCellParser(FixedWidthSchemaCell fixedWidthSchemaCell) throws ParseException {
        super(fixedWidthSchemaCell);
    }

    /**
     * Builds a Cell from a reader input.
     *
     * @param reader             The input reader
     * @param errorEventListener The error event listener to deliver errors to while parsing.
     * @return A Cell filled with the parsed cell value and with the name of this schema cell.
     * @throws IOException In case there is an error reading from the reader.
     */
    public Optional<Cell> parse(Reader reader, ErrorEventListener errorEventListener) throws IOException {

        String sValue = fieldReader.readToString(getSchemaCell(), reader, 0);
        if(sValue == null) {
            checkIfMandatory(errorEventListener);
            return Optional.empty();
        }
        return super.parse(sValue, errorEventListener);
    }

}
