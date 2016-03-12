package org.jsapar.parse.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.jsapar.JSaParException;
import org.jsapar.parse.*;
import org.jsapar.parse.LineParser;
import org.jsapar.schema.CsvControlCellSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.SchemaLine;

public class CsvControlCellParser implements Parser {

    private BufferedLineReader lineReader;
    private CsvControlCellSchema schema;
    private Map<SchemaLine, LineParser> lineParserCache = new HashMap<>();

    public CsvControlCellParser(Reader reader, CsvControlCellSchema schema) {
        this.schema = schema;
        this.lineReader = new BufferedLineReader(schema.getLineSeparator(), reader);
    }

    
    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        CsvSchemaLine lineSchema = null;

        long nLineNumber = 0; // First line is 1
        try {
            do {
                String sControlCell;
                String sLine = lineReader.readLine();
                if (sLine == null || sLine.length() == 0)
                    break;

                int nIndex = sLine.indexOf(schema.getControlCellSeparator());
                if (nIndex >= 0) {
                    sControlCell = sLine.substring(0, nIndex);
                    sLine = sLine.substring(nIndex + schema.getControlCellSeparator().length(), sLine.length());
                } else { // There is no delimiter, the control cell is the
                    // complete line.
                    sControlCell = sLine;
                    sLine = "";
                }
                lineReader.putBackLine(sLine);

                if (lineSchema == null || !lineSchema.getLineTypeControlValue().equals(sControlCell))
                    lineSchema = schema.getSchemaLineByControlValue(sControlCell);

                if (lineSchema == null) {
                    CellParseError error = new CellParseError(nLineNumber, "Control cell", sControlCell, null,
                            "Invalid Line-type: " + sControlCell);
                    throw new ParseException(error);
                }

                LineParser lineParser = makeLineParser(lineSchema);
                boolean isLineParsed = lineParser.parse(nLineNumber, listener);
                if (!isLineParsed)
                    break;

            } while (true);

        } catch (IOException ex) {
            throw new JSaParException("Failed to read control cell.", ex);
        }
    }
    
    /**
     * @param lineSchema The line schema to create a parser for.
     * @return A line parser that can parse the supplied line schema. 
     */
    private LineParser makeLineParser(CsvSchemaLine lineSchema){
        LineParser lineParser = lineParserCache.get(lineSchema);
        if(lineParser == null){
            lineParser = new CsvLineParser(lineReader, lineSchema);
            lineParserCache.put(lineSchema, lineParser);
        }
        return lineParser;
    }

}
