package org.jsapar.compose.csv;

import org.jsapar.compose.csv.quote.*;
import org.jsapar.compose.line.LineComposer;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composes csv line output based on schema and provided line.
 */
class CsvLineComposer implements LineComposer {

    private final Writer        writer;
    private final CsvSchemaLine schemaLine;
    private final String lineSeparator;
    private final QuoteSyntax quoteSyntax;
    private final List<CsvCellComposer> cellComposers;
    private boolean firstRow=true;

    CsvLineComposer(Writer writer, CsvSchemaLine schemaLine, String lineSeparator, QuoteSyntax quoteSyntax) {
        this.writer = writer;
        this.schemaLine = schemaLine;
        this.lineSeparator = lineSeparator;
        this.quoteSyntax = quoteSyntax;
        cellComposers = makeCellComposers(schemaLine);
    }

    private List<CsvCellComposer> makeCellComposers(CsvSchemaLine schemaLine) {
        return schemaLine.stream()
                .map(this::makeCellComposer)
                .collect(Collectors.toList());
    }

    private CsvCellComposer makeCellComposer(CsvSchemaCell schemaCell) {
        return new CsvCellComposer(schemaCell, makeQuoter(schemaLine, schemaCell, lineSeparator));
    }

    private Quoter makeQuoter(CsvSchemaLine schemaLine, CsvSchemaCell schemaCell, String lineSeparator) {
        char quoteChar = schemaLine.getQuoteChar();
        QuoteBehavior quoteBehavior = schemaCell.getQuoteBehavior();
        switch (quoteBehavior) {
            case AUTOMATIC:
                if (quoteChar != 0)
                    if (schemaCell.getCellFormat().getCellType().isAtomic())
                        return new NeverQuote(schemaCell.getMaxLength());
                    else
                        return new QuoteIfNeeded(quoteChar, schemaCell.getMaxLength(), schemaLine.getCellSeparator(), lineSeparator, quoteSyntax);
                else
                    return makeReplaceQuoter(schemaLine, schemaCell, lineSeparator);
            case NEVER:
                return new NeverQuote(schemaCell.getMaxLength());
            case REPLACE:
                return makeReplaceQuoter(schemaLine, schemaCell, lineSeparator);
            case ALWAYS:
                return new AlwaysQuote(quoteChar, schemaCell.getMaxLength(), quoteSyntax);
            default:
                throw new IllegalStateException("Unsupported quote behavior: " + quoteBehavior);
        }
    }

    private Quoter makeReplaceQuoter(CsvSchemaLine schemaLine, CsvSchemaCell schemaCell, String lineSeparator) {
        if (schemaCell.getCellFormat().getCellType().isAtomic())
            return new NeverQuote(schemaCell.getMaxLength());
        else
            return new NeverQuoteButReplace(schemaCell.getMaxLength(), schemaLine.getCellSeparator(), lineSeparator, "\u00A0");
    }

    /**
     * This implementation composes a csv output based on the line schema and provided line.
     * @param line The line to compose output of.
     * @throws UncheckedIOException If there is an error writing line to writer.
     */
    @Override
    public void compose(Line line) {
        try {
            if (schemaLine.isIgnoreWrite())
                return;
            if (firstRow && schemaLine.isFirstLineAsSchema()) {
                composeHeaderLine();
                writer.write(lineSeparator);
            }
            firstRow = false;
            String sCellSeparator = schemaLine.getCellSeparator();

            Iterator<CsvCellComposer> iter = cellComposers.iterator();
            while (iter.hasNext()) {
                CsvCellComposer cellComposer = iter.next();
                Cell<?> cell = line.getCell(cellComposer.getName()).orElse(cellComposer.makeEmptyCell());
                cellComposer.compose(writer, cell);

                if (iter.hasNext())
                    writer.write(sCellSeparator);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean ignoreWrite() {
        return schemaLine.isIgnoreWrite();
    }

    /**
     * Writes header line if first line is schema.
     */
    private void composeHeaderLine() {
        CsvSchemaLine headerLineSchema = CsvSchemaLine.builder("<schema>", schemaLine)
                .withCells(schemaLine.stream().map(SchemaCell::getName).toArray(String[]::new)) // Just use the names, not the format.
                .withFirstLineAsSchema(false)
                .build();
        CsvLineComposer headerLineComposer = new CsvLineComposer(writer, headerLineSchema, lineSeparator, quoteSyntax);
        headerLineComposer.compose(this.buildHeaderLineFromSchema(headerLineSchema));
    }

    /**
     * @return The header line
     *
     */
    private Line buildHeaderLineFromSchema(CsvSchemaLine headerSchemaLine)  {
        Line line = new Line(headerSchemaLine.getLineType(), headerSchemaLine.size());

        for (CsvSchemaCell schemaCell : headerSchemaLine) {
            line.addCell(new StringCell(schemaCell.getName(), schemaCell.getName()));
        }

        return line;
    }

}
