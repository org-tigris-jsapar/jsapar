package org.jsapar.compose.fixed;

import org.jsapar.JSaParException;
import org.jsapar.compose.SchemaLineComposer;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Created by stejon0 on 2016-01-31.
 */
public class FixedWidthLineComposer implements SchemaLineComposer{

    private final Writer writer;
    private final FixedWidthSchemaLine lineSchema;
    private final FixedWidthCellComposer cellComposer;

    public FixedWidthLineComposer(Writer writer, FixedWidthSchemaLine lineSchema) {
        this.writer = writer;
        this.lineSchema = lineSchema;
        this.cellComposer = new FixedWidthCellComposer(writer);
    }

    /**
     * Writes a line to the writer. Each cell is identified from the schema by the name of the cell.
     * If the schema-cell has no name, the cell at the same position in the line is used under the
     * condition that it also lacks name.
     *
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found
     * and the cell att the same position lacks name, it is used instead.
     *
     * If no corresponding cell is found for a schema-cell, the positions are filled with the schema
     * fill character.
     *
     * @param line
     *            The line to write to the writer
     * @throws IOException
     * @throws JSaParException
     */
    @Override
    public void compose(Line line) throws IOException, JSaParException {
       compose(line, 0);
    }

    /**
     * Writes a line to the writer. Each cell is identified from the schema by the name of the cell. If the schema-cell
     * has no name, the cell at the same position in the line is used under the condition that it also lacks name.
     *
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found and the cell att the
     * same position lacks name, it is used instead.
     *
     * If no corresponding cell is found for a schema-cell, the positions are filled with the schema fill character.
     *
     * @param line
     *            The line to write to the writer
     * @param offset
     *            The number of characters that has already been written on this line.
     *
     * @throws IOException
     * @throws JSaParException
     */
    public void compose(Line line, int offset) throws IOException, JSaParException {
        Iterator<FixedWidthSchemaCell> iter = lineSchema.getSchemaCells().iterator();

        // Iterate all schema cells.
        int totalLength = offset;
        for (int i = 0; iter.hasNext(); i++) {
            FixedWidthSchemaCell schemaCell = iter.next();
            totalLength += schemaCell.getLength();
            Cell cell = line.getCell(schemaCell.getName());
            cellComposer.compose(cell, schemaCell, lineSchema.getFillCharacter());
        }
        if(lineSchema.getMinLength() > totalLength){
            FixedWidthSchemaCell.fill(writer, lineSchema.getFillCharacter(), lineSchema.getMinLength() -totalLength);
        }
    }
}
