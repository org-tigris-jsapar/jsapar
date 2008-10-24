package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;


/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed
 * number of characters. Each line is separated by the line separator defined in
 * the base class {@link Schema}. 
 * 
 * If the end of line is reached before all cells are parsed the remaining cells 
 * will not be set. 
 * 
 * If there are remaining characters when the end of line is reached, those 
 * characters will be omitted.
 * 
 * If the line separator is an empty string, the lines will be separated by the sum of the length of the cells within the 
 * schema.
 * 
 * @author Jonas
 * 
 */
/**
 * @author Jonas Stenberg
 * 
 */
public class FixedWidthSchema extends Schema {

	private boolean trimFillCharacters = false;
	private char fillCharacter = ' ';
	private int controlCellLength;
	private FixedWidthSchemaCell.Alignment controlCellAllignment;
	private java.util.List<FixedWidthSchemaLine> schemaLines = new java.util.LinkedList<FixedWidthSchemaLine>();

	/**
	 * @return the schemaLines
	 */
	public java.util.List<FixedWidthSchemaLine> getSchemaLines() {
		return schemaLines;
	}

	/**
	 * @param schemaLines
	 *            the schemaLines to set
	 */
	public void setSchemaLines(java.util.List<FixedWidthSchemaLine> schemaLines) {
		this.schemaLines = schemaLines;
	}

	/**
	 * @param schemaLine
	 *            the schemaLines to set
	 */
	public void addSchemaLine(FixedWidthSchemaLine schemaLine) {
		this.schemaLines.add(schemaLine);
	}

	/**
	 * @param sLineTypeControlValue
	 * @return A schema line of type FixedWitdthSchemaLine which has the
	 *         supplied line type.
	 */
	public FixedWidthSchemaLine getSchemaLine(String sLineTypeControlValue) {
		for (FixedWidthSchemaLine lineSchema : getSchemaLines()) {
			if (lineSchema.getLineTypeControlValue().equals(
					sLineTypeControlValue)) {
				return lineSchema;
			}
		}
		return null;
	}

	/**
	 * Builds a Document from a reader where each line is denoted by a control
	 * cell at the beginning of each line.
	 * 
	 * @param reader
	 * @return The read document.
	 * @throws JSaParException
	 */
	@Override
	protected Document buildByControlCell(java.io.Reader reader,
			List<CellParseError> parseErrors) throws JSaParException {
		Document doc = new Document();
		char[] lineSeparatorBuffer = new char[getLineSeparator().length()];
		char[] controlCellBuffer = new char[getControlCellLength()];
		FixedWidthSchemaLine lineSchema = null;
		long nLineNumber = 0; // First line is 1
		try {
			do {
				nLineNumber++;
				int nRead = reader.read(controlCellBuffer, 0,
						getControlCellLength());
				if (nRead < getControlCellLength()) {
					break; // End of stream.
				}
				String sControlCell = new String(controlCellBuffer);
				if (lineSchema == null
						|| !lineSchema.getLineTypeControlValue().equals(
								sControlCell)) {
					lineSchema = getSchemaLine(sControlCell);
				}
				if (lineSchema == null) {
					CellParseError error = new CellParseError(nLineNumber,
							"Control cell", sControlCell, null,
							"Invalid Line-type: " + sControlCell);
					throw new ParseException(error);
				}

				Line line = lineSchema.build(nLineNumber, reader, this,
						parseErrors);
				if (line != null) {
					line.setLineType(lineSchema.getLineType());
					doc.addLine(line);
				} else {
					break; // End of stream.
				}
				if (getLineSeparator().length() > 0) {
					nRead = reader.read(lineSeparatorBuffer, 0,
							getLineSeparator().length());
					if (nRead < getLineSeparator().length()) {
						break; // End of stream.
					}
					String sSeparator = new String(lineSeparatorBuffer);
					if (!sSeparator.equals(getLineSeparator())) {
						CellParseError error = new CellParseError(
								nLineNumber,
								"End-of-line",
								sSeparator,
								null,
								"Unexpected characters '"
										+ sSeparator
										+ "' found when expecting line separator.");
						throw new ParseException(error);
					}
				}

			} while (true);
			return doc;
		} catch (IOException ex) {
			throw new JSaParException("Failed to read control cell.", ex);
		}
	}

	/**
	 * Builds a document from a reader using a schema where the line types are
	 * denoted by the occurs field in the schema.
	 * 
	 * @param reader
	 * @param parseErrors
	 * @return
	 * @throws org.jsapar.input.ParseException
	 * @throws java.io.IOException
	 */
	@Override
	protected Document buildByOccurs(java.io.Reader reader,
			List<CellParseError> parseErrors) throws ParseException,
			IOException {
		if (getLineSeparator().length() > 0) {
			return buildByOccursLinesSeparated(reader, parseErrors);
		} else {
			return buildByOccursFlatFile(reader, parseErrors);
		}
	}

	/**
	 * Builds a document from a reader using a schema where the line types are
	 * denoted by the occurs field in the schema and the lines are not separated
	 * by any line separator character.
	 * 
	 * @param reader
	 * @param parseErrors
	 * @return
	 * @throws org.jsapar.JSaParException
	 * @throws java.io.IOException
	 */
	protected Document buildByOccursFlatFile(java.io.Reader reader,
			List<CellParseError> parseErrors) throws ParseException,
			IOException {
		Document doc = new Document();
		long nLineNumber = 0;
		for (FixedWidthSchemaLine lineSchema : getSchemaLines()) {
			for (int i = 0; i < lineSchema.getOccurs(); i++) {
				nLineNumber++;
				Line line = lineSchema.build(nLineNumber, reader, this,
						parseErrors);

				if (line != null) {
					line.setLineType(lineSchema.getLineType());
					doc.addLine(line);
				} else {
					break; // End of stream.
				}
			}
		}
		return doc;
	}

	protected Document buildByOccursLinesSeparated(java.io.Reader reader,
			List<CellParseError> parseErrors) throws ParseException,
			IOException {
		Document doc = new Document();

		long nLineNumber = 0; // First line is 1
		for (FixedWidthSchemaLine lineSchema : getSchemaLines()) {
			for (int i = 0; i < lineSchema.getOccurs(); i++) {
				nLineNumber++;
				String sLine = parseLine(reader);
				if (sLine.length() == 0) {
					if (lineSchema.isOccursInfinitely()) {
						break;
					} else {
						throw new ParseException(
								"Unexpected end of input buffer. Was expecting "
										+ lineSchema.getOccurs()
										+ " lines of this type. Found " + i
										+ " lines");
					}
				}

				Line line = lineSchema.build(nLineNumber, sLine, this,
						parseErrors);
				if (line == null)
					return doc;

				line.setLineType(lineSchema.getLineType());
				doc.addLine(line);
			}
		}
		return doc;
	}

	/**
	 * @return the fillCharacter
	 */
	public char getFillCharacter() {
		return fillCharacter;
	}

	/**
	 * @param fillCharacter
	 *            the fillCharacter to set
	 */
	public void setFillCharacter(char fillCharacter) {
		this.fillCharacter = fillCharacter;
	}

	/**
	 * @return the trimFillCharacters
	 */
	public boolean isTrimFillCharacters() {
		return trimFillCharacters;
	}

	/**
	 * @param trimFillCharacters
	 *            the trimFillCharacters to set
	 */
	public void setTrimFillCharacters(boolean trimFillerCharacters) {
		this.trimFillCharacters = trimFillerCharacters;
	}

	@Override
	public void output(Document document, Writer writer) throws IOException,
			JSaParException {

		Iterator<Line> itLines = document.getLineIterator();
		for (SchemaLine lineSchema : getSchemaLines()) {
			for (int i = 0; i < lineSchema.getOccurs(); i++) {
				if (!itLines.hasNext()) {
					return;
				}
				Line line = itLines.next();
				((FixedWidthSchemaLine) lineSchema).output(line, writer, this);

				if (itLines.hasNext()) {
					if (getLineSeparator().length() > 0) {
						writer.write(getLineSeparator());
					}
				} else {
					return;
				}
			}
		}
	}

	/**
	 * @return the controlCellAllignment
	 */
	public FixedWidthSchemaCell.Alignment getControlCellAllignment() {
		return controlCellAllignment;
	}

	/**
	 * @param controlCellAllignment
	 *            the controlCellAllignment to set
	 */
	public void setControlCellAllignment(
			FixedWidthSchemaCell.Alignment controlCellAllignment) {
		this.controlCellAllignment = controlCellAllignment;
	}

	/**
	 * @return the controlCellLength
	 */
	public int getControlCellLength() {
		return controlCellLength;
	}

	/**
	 * @param controlCellLength
	 *            the controlCellLength to set
	 */
	public void setControlCellLength(int controlCellLength) {
		this.controlCellLength = controlCellLength;
	}

	public FixedWidthSchema clone() throws CloneNotSupportedException {
		FixedWidthSchema schema = (FixedWidthSchema) super.clone();

		schema.schemaLines = new java.util.LinkedList<FixedWidthSchemaLine>();
		for (FixedWidthSchemaLine line : this.schemaLines) {
			schema.addSchemaLine(line.clone());
		}
		return schema;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsapar.schema.Schema#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		if (getLineTypeBy() == LineTypeByTypes.CONTROL_CELL) {
			sb.append(" controlCellAllignment=");
			sb.append(this.controlCellAllignment);
			sb.append(" controlCellLength=");
			sb.append(this.controlCellLength);
		}
		sb.append(" trimFillCharacters=");
		sb.append(this.trimFillCharacters);
		if (this.trimFillCharacters) {
			sb.append(" fillCharacter='");
			sb.append(this.fillCharacter);
			sb.append("'");
		}
		sb.append(" schemaLines=");
		sb.append(this.schemaLines);
		return sb.toString();
	}

}
