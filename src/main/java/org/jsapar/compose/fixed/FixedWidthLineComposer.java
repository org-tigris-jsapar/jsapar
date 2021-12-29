package org.jsapar.compose.fixed;

import org.jsapar.compose.fixed.pad.Filler;
import org.jsapar.compose.line.LineComposer;
import org.jsapar.model.Line;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composes line to a fixed width format based on line schema.
 */
class FixedWidthLineComposer implements LineComposer {

    private final Writer                       writer;
    private final FixedWidthSchemaLine         lineSchema;
    private final List<FixedWidthCellComposer> cellComposers;
    private final Filler                       filler;

    FixedWidthLineComposer(Writer writer, FixedWidthSchemaLine lineSchema) {
        if(writer == null)
            throw new IllegalArgumentException("Writer of line composer cannot be null");
        if(lineSchema == null)
            throw new IllegalArgumentException("Line schema of line composer cannot be null");
        this.writer = writer;
        this.lineSchema = lineSchema;
        this.cellComposers = lineSchema.stream().map(FixedWidthCellComposer::new).collect(Collectors.toList());
        filler = new Filler(lineSchema.getPadCharacter(), lineSchema.getMinLength());
    }

    /**
     * Composes an output from a line. Each cell is identified from the schema by the name of the cell.
     *
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found the positions are
     * filled with fill character defined by the schema.
     *
     * @param line
     *            The line to write to the writer
     * @throws UncheckedIOException If an IO error occurs.
     *
     */
    @Override
    public void compose(Line line)  {
        try {
            if (lineSchema.isIgnoreWrite())
                return;

            // Iterate all schema cells.
            int totalLength = 0;
            for (FixedWidthCellComposer composer : cellComposers) {
                totalLength += composer.compose(writer, line.getCell(composer.getName()).orElse(composer.makeEmptyCell()));
            }
            if (lineSchema.getMinLength() > totalLength) {
                filler.fill(writer, lineSchema.getMinLength() - totalLength);
            }
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean ignoreWrite() {
        return lineSchema.isIgnoreWrite();
    }
}
