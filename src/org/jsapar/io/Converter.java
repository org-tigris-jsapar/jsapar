package org.jsapar.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.JSaParException;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.output.Outputter;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

public class Converter {

    public java.util.List<CellParseError> convert(java.io.Reader reader, ParseSchema inputSchema,
	    java.io.Writer writer, Schema outputSchema) throws IOException, JSaParException {

	DocumentWriter outputter = new DocumentWriter(outputSchema, writer);
	// TODO Create a DocumentWriter that supports line type by control cell.

	outputSchema.outputBefore(writer);
	inputSchema.parse(reader, outputter);
	outputSchema.outputAfter(writer);
	return outputter.getParseErrors();

    }

    /**
     * Internal class for handling output of one line at a time while receiving parsing events. 
     * 
     * @author stejon0
     * 
     */
    private class DocumentWriter implements ParsingEventListener {
	private List<CellParseError> parseErrors = new LinkedList<CellParseError>();
	private Schema outputSchema;
	private java.io.Writer writer;


	public DocumentWriter(Schema outputSchema, Writer writer) throws JSaParException {
	    this.outputSchema = outputSchema;
	    this.writer = writer;
	}

	@Override
	public void lineErrorErrorEvent(LineErrorEvent event) {
	    parseErrors.add(event.getCellParseError());
	}

	@Override
	public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
	    try {
		outputSchema.outputLine(event.getLine(), event.getLineNumber(), this.writer);
	    } catch (IOException e) {
		throw new JSaParException("Failed to write to writer", e);
	    }
	}

	/**
	 * @return the parseErrors
	 */
	public List<CellParseError> getParseErrors() {
	    return parseErrors;
	}

    }

}
