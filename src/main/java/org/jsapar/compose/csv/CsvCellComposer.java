package org.jsapar.compose.csv;

import org.jsapar.compose.cell.CellFormat;
import org.jsapar.compose.csv.quote.Quoter;
import org.jsapar.model.Cell;
import org.jsapar.model.EmptyCell;
import org.jsapar.schema.CsvSchemaCell;

import java.io.IOException;
import java.io.Writer;

/**
 * Composes cell values based on the schema of the cell.
 */
final class CsvCellComposer {
    private final CsvSchemaCell schemaCell;
    private final Quoter quoter;
    private final CellFormat cellFormat;

    CsvCellComposer(CsvSchemaCell schemaCell, Quoter quoter) {
        this.schemaCell = schemaCell;
        this.quoter = quoter;
        this.cellFormat = CellFormat.ofSchemaCell(schemaCell);
    }

    /**
     * Writes the cell to the supplied writer, including quote character if necessary.
     *
     * @param writer The writer to write result to.
     * @param cell   The cell to compose output for.
     * @throws IOException In case of error in underlying IO operation
     */
    void compose(Writer writer, Cell<?> cell) throws IOException {
        quoter.writeValue(writer, cellFormat.format(cell));
    }


    String getName() {
        return schemaCell.getName();
    }

    EmptyCell makeEmptyCell() {
        return schemaCell.makeEmptyCell();
    }
}
