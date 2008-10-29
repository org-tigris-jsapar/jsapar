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

    public java.util.List<CellParseError> convert(java.io.Reader reader,
	    ParseSchema inputSchema, java.io.Writer writer, Schema outputSchema)
	    throws IOException, JSaParException {

	DocumentWriterOccurs outputter = new DocumentWriterOccurs(outputSchema,
		writer);
	// TODO Create a DocumentWriter that supports line type by control cell.

	outputSchema.outputBefore(writer);
	inputSchema.parse(reader, outputter);
	outputSchema.outputAfter(writer);
	return outputter.getParseErrors();

    }

    /**
     * Internal class for handling output of one line at a time while receiving
     * parsing events. This class requires the schema to have
     * linetypeby="occurs"
     * 
     * @author stejon0
     * 
     */
    private class DocumentWriterOccurs implements ParsingEventListener {
	private List<CellParseError> parseErrors = new LinkedList<CellParseError>();
	private Outputter outputter = new Outputter();
	private Schema outputSchema;
	private java.io.Writer writer;
	private Iterator iterSchemaLine;
	private SchemaLine currentSchemaLine;
	private long currentSchemaLineLines = 0; // Number of lines written with
						 // current schema line.

	public DocumentWriterOccurs(Schema outputSchema, Writer writer)
		throws JSaParException {
	    if (Schema.LineTypeByTypes.OCCURS != outputSchema.getLineTypeBy())
		throw new JSaParException(
			"Only LineTypeBy=OCCURS is supported for output schema.");

	    this.outputSchema = outputSchema;
	    this.writer = writer;
	    this.iterSchemaLine = outputSchema.getSchemaLines().iterator();
	    if (this.iterSchemaLine.hasNext()) {
		this.currentSchemaLine = (SchemaLine) this.iterSchemaLine
			.next();
	    } else
		throw new JSaParException(
			"Output schema contains no schema lines.");
	}

	@Override
	public void lineErrorErrorEvent(LineErrorEvent event) {
	    parseErrors.add(event.getCellParseError());
	}

	@Override
	public void lineParsedEvent(LineParsedEvent event)
		throws JSaParException {
	    try {
		if (event.getLineNumber() != 1)
		    writer.append(outputSchema.getLineSeparator());

		outputter.output(event.getLine(), this.currentSchemaLine,
			this.writer);
		this.currentSchemaLineLines++;

		// Check if we should switch schema line for parsing.
		if (this.currentSchemaLineLines >= this.currentSchemaLine
			.getOccurs()) {
		    if (this.iterSchemaLine.hasNext()) {
			// Switch to next schmea line and reset counter.
			this.currentSchemaLine = (SchemaLine) this.iterSchemaLine
				.next();
			this.currentSchemaLineLines = 0;
		    }
		}
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
