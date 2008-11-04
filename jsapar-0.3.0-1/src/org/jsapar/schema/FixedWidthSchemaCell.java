package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.jsapar.Cell;
import org.jsapar.EmptyCell;
import org.jsapar.JSaParException;
import org.jsapar.input.ParseException;
import org.jsapar.output.OutputException;

public class FixedWidthSchemaCell extends SchemaCell {

    public enum Alignment {

	LEFT, CENTER, RIGHT
    };

    private int length;
    private Alignment alignment = Alignment.LEFT;

    public FixedWidthSchemaCell(String sName, int nLength, Alignment alignment) {
	this(sName, nLength);
	this.alignment = alignment;
    }

    public FixedWidthSchemaCell(String sName, int nLength) {
	super(sName);
	this.length = nLength;
    }

    public FixedWidthSchemaCell(int nLength) {
	this.length = nLength;
    }

    Cell build(Reader reader, boolean trimFillCharacters, char fillCharacter)
	    throws IOException, ParseException {

	int nOffset = 0;
	int nLength = this.length; // The actuall length

	char[] buffer = new char[nLength];
	int nRead = reader.read(buffer, 0, nLength);
	if (nRead <= 0) {
	    if (this.length <= 0)
		return new EmptyCell(getName(), getCellFormat().getCellType());
	    else
		return null;
	}
	nLength = nRead;
	if (trimFillCharacters) {
	    while (nOffset < nLength && buffer[nOffset] == fillCharacter) {
		nOffset++;
	    }
	    while (nLength > nOffset && buffer[nLength - 1] == fillCharacter) {
		nLength--;
	    }
	    nLength -= nOffset;
	}
	Cell cell = makeCell(new String(buffer, nOffset, nLength));
	return cell;
    }

    /**
     * @return the length
     */
    public int getLength() {
	return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(int length) {
	this.length = length;
    }

    /**
     * @param writer
     * @param ch
     * @param nSize
     * @throws IOException
     */
    private static void fill(Writer writer, char ch, int nSize)
	    throws IOException {
	for (int i = 0; i < nSize; i++) {
	    writer.write(ch);
	}
    }

    /**
     * Writes an empty cell. Uses the fill character to fill the space.
     * 
     * @param writer
     * @param schema
     * @throws IOException
     * @throws JSaParException
     */
    public void outputEmptyCell(Writer writer, char fillCharacter)
	    throws IOException, JSaParException {
	this.fill(writer, fillCharacter, getLength());
    }

    /**
     * @param cell
     * @param writer
     * @param schema
     * @throws IOException
     * @throws OutputException
     */
    void output(Cell cell, Writer writer, char fillCharacter)
	    throws IOException, JSaParException {
	String sValue = cell.getStringValue(getCellFormat().getFormat());
	output(sValue, writer, fillCharacter, getLength(), getAlignment());
    }

    /**
     * @param cell
     * @param writer
     * @param fillCharacter
     * @param length
     * @param alignment
     * @param format
     * @throws IOException
     * @throws OutputException
     */
    static void output(String sValue, Writer writer, char fillCharacter,
	    int length, Alignment alignment)
	    throws IOException, OutputException {
	// If the cell value is larger than the cell length, we have to cut the
	// value.
	if (sValue.length() >= length) {
	    writer.write(sValue.substring(0, length));
	    return;
	}
	// Otherwise use the alignment of the schema.
	int nToFill = length - sValue.length();
	switch (alignment) {
	case LEFT:
	    writer.write(sValue);
	    fill(writer, fillCharacter, nToFill);
	    break;
	case RIGHT:
	    fill(writer, fillCharacter, nToFill);
	    writer.write(sValue);
	    break;
	case CENTER:
	    int nLeft = nToFill / 2;
	    fill(writer, fillCharacter, nLeft);
	    writer.write(sValue);
	    fill(writer, fillCharacter, nToFill - nLeft);
	    break;
	default:
	    throw new OutputException(
		    "Unknown allignment style for cell schema.");
	}
    }

    /**
     * @return the alignment
     */
    public Alignment getAlignment() {
	return alignment;
    }

    /**
     * @param alignment
     *            the alignment to set
     */
    public void setAlignment(Alignment alignment) {
	this.alignment = alignment;
    }

    public FixedWidthSchemaCell clone() throws CloneNotSupportedException {
	return (FixedWidthSchemaCell) super.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.SchemaCell#toString()
     */
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(super.toString());
	sb.append(" length=");
	sb.append(this.length);
	sb.append(" alignment=");
	sb.append(this.alignment);
	return sb.toString();
    }

}
