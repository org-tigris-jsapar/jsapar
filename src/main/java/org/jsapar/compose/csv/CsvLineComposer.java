package org.jsapar.compose.csv;

import org.jsapar.compose.LineComposer;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.SchemaCellFormat;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Composes csv line output based on schema and provided line.
 * Created by stejon0 on 2016-01-30.
 */
public class CsvLineComposer implements LineComposer {

    Writer        writer;
    CsvSchemaLine schemaLine;
    private String lineSeparator;
    CsvCellComposer cellComposer;
    boolean firstRow=true;

    public CsvLineComposer(Writer writer, CsvSchemaLine schemaLine, String lineSeparator) {
        this.writer = writer;
        this.schemaLine = schemaLine;
        this.lineSeparator = lineSeparator;
        this.cellComposer = new CsvCellComposer(writer);
    }

    /**
     * This implementation composes a csv output based on the line schema and provided line.
     * @param line The line to compose output of.
     * @throws IOException
     */
    @Override
    public void compose(Line line) throws IOException {
        if(firstRow && schemaLine.isFirstLineAsSchema()){
            composeHeaderLine();
            writer.write(lineSeparator);
        }
        firstRow = false;
        String sCellSeparator = schemaLine.getCellSeparator();

        Iterator<CsvSchemaCell> iter = schemaLine.getSchemaCells().iterator();
        while(iter.hasNext()) {
            CsvSchemaCell schemaCell = iter.next();
            Cell cell = line.getCell(schemaCell.getName());
            char quoteChar = schemaLine.getQuoteChar();

            cellComposer.compose(cell, schemaCell, sCellSeparator, quoteChar);

            if (iter.hasNext())
                writer.write(sCellSeparator);
        }
    }

    /**
     * Writes header line if first line is schema.
     *
     * @throws IOException
     *
     */
    public void composeHeaderLine() throws IOException {
        CsvSchemaLine unformattedSchemaLine = schemaLine.clone();
        unformattedSchemaLine.setFirstLineAsSchema(false);
        for (CsvSchemaCell schemaCell : unformattedSchemaLine.getSchemaCells()) {
            schemaCell.setCellFormat(new SchemaCellFormat(CellType.STRING));
        }
        CsvLineComposer headerLineComposer = new CsvLineComposer(writer, unformattedSchemaLine, lineSeparator);
        headerLineComposer.compose(this.buildHeaderLineFromSchema(unformattedSchemaLine));
    }

    /**
     * @return The header line
     *
     */
    private Line buildHeaderLineFromSchema(CsvSchemaLine headerSchemaLine)  {
        Line line = new Line();

        for (CsvSchemaCell schemaCell : headerSchemaLine.getSchemaCells()) {
            line.addCell(new StringCell(schemaCell.getName(), schemaCell.getName()));
        }

        return line;
    }

}
