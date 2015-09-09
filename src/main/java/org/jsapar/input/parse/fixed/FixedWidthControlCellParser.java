/**
 * 
 */
package org.jsapar.input.parse.fixed;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jsapar.JSaParException;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.input.parse.BufferedLineReader;
import org.jsapar.input.parse.LineReader;
import org.jsapar.input.parse.SchemaLineParser;
import org.jsapar.input.parse.SchemaParser;
import org.jsapar.schema.FixedWidthControlCellSchema;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaLine;

/**
 * @author stejon0
 *
 */
public class FixedWidthControlCellParser implements SchemaParser {
    
    private Reader reader;
    private FixedWidthControlCellSchema schema;
    private Map<SchemaLine, SchemaLineParser> lineParserCache = new HashMap<>();

    /**
     * 
     */
    public FixedWidthControlCellParser(Reader reader, FixedWidthControlCellSchema schema) {
        this.reader = reader;
        this.schema = schema;
    }

    /* (non-Javadoc)
     * @see org.jsapar.input.parse.SchemaParser#parse(org.jsapar.input.ParsingEventListener)
     */
    @Override
    public void parse(ParsingEventListener listener) throws JSaParException, IOException {
        if (schema.getLineSeparator().length() > 0) {
            parseByOccursLinesSeparated(listener);
        } else {
            parseByOccursFlatFile(listener);
        }
    }

    private void parseByOccursFlatFile(ParsingEventListener listener) throws IOException,
            JSaParException {

        long nLineNumber = 0; // First line is 1
        while (true) {
            nLineNumber++;

            FixedWidthSchemaLine lineSchema = findSchemaLine(reader, nLineNumber);
            if (lineSchema == null)
                return;
            SchemaLineParser lineParser = makeLineParserFlat(lineSchema);
            boolean isLineFound = lineParser.parse(nLineNumber, listener);
            if (!isLineFound) {
                return; // End of stream.
            }

        }
    }
    
    /**
     * @param lineSchema The line schema to create a parser for.
     * @return A line parser that can parse the supplied line schema. 
     */
    private SchemaLineParser makeLineParserFlat(FixedWidthSchemaLine lineSchema){
        SchemaLineParser lineParser = lineParserCache.get(lineSchema);
        if(lineParser == null){
            lineParser = new FixedWidthLineParser(reader, lineSchema);
            lineParserCache.put(lineSchema, lineParser);
        }
        return lineParser;
    }


    private void parseByOccursLinesSeparated(ParsingEventListener listener) throws IOException,
            JSaParException {
        long nLineNumber = 0; // First line is 1
        BufferedLineReader lineReader = new BufferedLineReader(schema.getLineSeparator(), reader);
        while (true) {
            nLineNumber++;
            String sLine = lineReader.readLine();
            if (sLine == null)
                return; // End of buffer
            if(sLine.isEmpty()) // Empty line
                continue;
            try(Reader lineStringReader = new StringReader(sLine)){
                FixedWidthSchemaLine lineSchema = findSchemaLine(lineStringReader, nLineNumber);
                if (lineSchema == null)
                    continue;
                SchemaLineParser lineParser = makeLineParserSeparated(lineSchema, lineReader);
                lineReader.putBackLine(sLine.substring(schema.getControlCellLength()));
                boolean isLineFound = lineParser.parse(nLineNumber, listener);
                if (!isLineFound) {
                    return; // End of stream.
                }
            }

        }
    }  
    /**
     * @param lineSchema The line schema to create a parser for.
     * @return A line parser that can parse the supplied line schema. 
     */
    private SchemaLineParser makeLineParserSeparated(FixedWidthSchemaLine lineSchema, LineReader lineReader){
        SchemaLineParser lineParser = lineParserCache.get(lineSchema);
        if(lineParser == null){
            lineParser = new FixedWidthSeparatedLineParser(lineReader, lineSchema);
            lineParserCache.put(lineSchema, lineParser);
        }
        return lineParser;
    }
    
    /**
     * @param reader
     * @param nLineNumber
     * @return a line schema that matches the control value at the beginning of the line. Returns null if end of stream is reached.
     * @throws JSaParException if no matching schema is found.
     */
    private FixedWidthSchemaLine findSchemaLine(Reader reader, long nLineNumber) throws JSaParException{
        String sControlCell = readControlCell(reader);
        if(sControlCell == null)
            return null;

        FixedWidthSchemaLine lineSchema = schema.getSchemaLineByControlValue(sControlCell);
        if (lineSchema == null) {
            if (schema.isErrorIfUndefinedLineType() || schema.getLineSeparator().isEmpty()) {
                CellParseError error = new CellParseError(nLineNumber, "Control cell", sControlCell, null,
                        "Invalid Line-type: " + sControlCell);
                throw new ParseException(error);
            } else
                
                return null;

        }
        return lineSchema;
    }
    
    /**
     * @param reader
     * @return The controll cell value that was read.
     * @throws JSaParException
     */
    private String readControlCell(Reader reader) throws JSaParException {
        try {
            char[] controlCellBuffer = new char[schema.getControlCellLength()];
            int nRead = reader.read(controlCellBuffer, 0, schema.getControlCellLength());
            if (nRead < schema.getControlCellLength()) {
                return null; // End of stream.
            }
            return new String(controlCellBuffer);
        } catch (IOException ex) {
            throw new JSaParException("Failed to read control cell.", ex);
        }
    }
        
}
