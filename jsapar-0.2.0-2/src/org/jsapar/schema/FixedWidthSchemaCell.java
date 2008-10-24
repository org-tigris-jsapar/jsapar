package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.jsapar.Cell;
import org.jsapar.JSaParException;
import org.jsapar.input.ParseException;
import org.jsapar.output.OutputException;


public class FixedWidthSchemaCell extends SchemaCell {

    public enum Alignment {

        LEFT, CENTER, RIGHT
    };
    private int length;
    private Alignment alignment = Alignment.LEFT;

    public FixedWidthSchemaCell(String sName, int nLength) {
        super(sName);
        this.length = nLength;
    }

    public FixedWidthSchemaCell(int nLength) {
        this.length = nLength;
    }

    Cell build(Reader reader, FixedWidthSchema schema) throws IOException,
            ParseException {

        int nOffset = 0;
        int nLength = this.length; // The actuall length

        char[] buffer = new char[nLength];
        int nRead = reader.read(buffer, 0, nLength);
        if (nRead <= 0) {
            return null;
        }
        nLength = nRead;
        if (schema.isTrimFillCharacters()) {
            while (nOffset < nLength && buffer[nOffset] == schema.getFillCharacter()) {
                nOffset++;
            }
            while (nLength > nOffset && buffer[nLength - 1] == schema.getFillCharacter()) {
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
     *                the length to set
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
    private void fill(Writer writer, char ch, int nSize) throws IOException {
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
    public void outputEmptyCell(Writer writer, FixedWidthSchema schema)
            throws IOException, JSaParException {
        this.fill(writer, schema.getFillCharacter(), getLength());
    }

    /**
     * @param cell
     * @param writer
     * @param schema
     * @throws IOException
     * @throws OutputException
     */
    void output(Cell cell, Writer writer, FixedWidthSchema schema)
            throws IOException, JSaParException {
        // If the cell value is larger than the cell length, we have to cut the
        // value.
        String sValue = cell.getStringValue(null != getCellFormat() ? getCellFormat().getFormat() : null);
        if (sValue.length() >= getLength()) {
            writer.write(sValue.substring(0, getLength()));
            return;
        }
        // Otherwise use the alignment of the schema.
        int nToFill = getLength() - sValue.length();
        switch (alignment) {
            case LEFT:
                writer.write(sValue);
                this.fill(writer, schema.getFillCharacter(), nToFill);
                break;
            case RIGHT:
                this.fill(writer, schema.getFillCharacter(), nToFill);
                writer.write(sValue);
                break;
            case CENTER:
                int nLeft = nToFill / 2;
                this.fill(writer, schema.getFillCharacter(), nLeft);
                writer.write(sValue);
                this.fill(writer, schema.getFillCharacter(), nToFill - nLeft);
                break;
            default:
                throw new OutputException("Unknown allignment style for cell schema.");
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
     *                the alignment to set
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    
    public FixedWidthSchemaCell clone() throws CloneNotSupportedException{
    	return (FixedWidthSchemaCell)super.clone();
    }

	/* (non-Javadoc)
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
