package org.jsapar.compose.fixed;

import org.jsapar.JSaParException;
import org.jsapar.compose.LineComposer;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Composes line to a fixed width format based on line schema.
 * Created by stejon0 on 2016-01-31.
 */
public class FixedWidthLineComposer implements LineComposer {

    private final Writer writer;
    private final FixedWidthSchemaLine lineSchema;
    private final FixedWidthCellComposer cellComposer;

    public FixedWidthLineComposer(Writer writer, FixedWidthSchemaLine lineSchema) {
        this.writer = writer;
        this.lineSchema = lineSchema;
        this.cellComposer = new FixedWidthCellComposer(writer);
    }

    /**
     * Composes an output from a line. Each cell is identified from the schema by the name of the cell.
     *
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found the positions are
     * filled with fill character defined by the schema.
     *
     * @param line
     *            The line to write to the writer
     * @throws IOException
     * @throws JSaParException
     */
    @Override
    public void compose(Line line) throws IOException {
       compose(line, 0);
    }

    /**
     * Composes an output from a line. Each cell is identified from the schema by the name of the cell.
     *
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found the positions are
     * filled with fill character defined by the schema.
     *
     * @param line
     *            The line to write to the writer
     * @param offset
     *            The number of characters that has already been written on this line.
     *
     * @throws IOException
     * @throws JSaParException
     */
    public void compose(Line line, int offset) throws IOException {
        Iterator<FixedWidthSchemaCell> iter = lineSchema.getSchemaCells().iterator();

        // Iterate all schema cells.
        int totalLength = offset;
        while(iter.hasNext()) {
            FixedWidthSchemaCell schemaCell = iter.next();
            totalLength += schemaCell.getLength();
            Cell cell = line.getCell(schemaCell.getName());
            cellComposer.compose(cell, schemaCell, lineSchema.getFillCharacter());
        }
        if(lineSchema.getMinLength() > totalLength){
            FixedWidthCellComposer.fill(writer, lineSchema.getFillCharacter(), lineSchema.getMinLength() -totalLength);
        }
    }
}
