package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.JSaParException;
import org.jsapar.parse.ParseException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.compose.ComposeException;

/**
 * Describes how a cell is represented for a fixed with schema.
 * 
 * @author stejon0
 */
public class FixedWidthSchemaCell extends SchemaCell {

    /**
     * Describes how a cell is aligned within its allocated space.
     * 
     * @author stejon0
     * 
     */
    public enum Alignment {

        LEFT{
            @Override
            public void fit(Writer writer, int length, String sValue) throws IOException {
                writer.write(sValue, 0, length);
            }

            @Override
            public void padd(Writer writer, int nToFill, String sValue, char fillCharacter) throws IOException {
                    writer.write(sValue);
                    FixedWidthSchemaCell.fill(writer, fillCharacter, nToFill);
            }
        },
        CENTER {
            @Override
            public void fit(Writer writer, int length, String sValue) throws IOException {
                writer.write(sValue, (sValue.length()-length)/2, length);
            }

            @Override
            public void padd(Writer writer, int nToFill, String sValue, char fillCharacter) throws IOException {
                int nLeft = nToFill / 2;
                FixedWidthSchemaCell.fill(writer, fillCharacter, nLeft);
                writer.write(sValue);
                FixedWidthSchemaCell.fill(writer, fillCharacter, nToFill - nLeft);
            }
        },
        RIGHT{
            @Override
            public void fit(Writer writer, int length, String sValue) throws IOException {
                writer.write(sValue, sValue.length() - length, length);
            }

            @Override
            public void padd(Writer writer, int nToFill, String sValue, char fillCharacter) throws IOException {
                FixedWidthSchemaCell.fill(writer, fillCharacter, nToFill);
                writer.write(sValue);
            }
            
        };
        
        
        /**
         * Fits supplied value to supplied length, cutting in the correct end.
         * @param writer
         * @param length
         * @param sValue
         * @throws IOException
         */
        public abstract void fit(Writer writer, int length, String sValue) throws IOException;
        
        /**
         * Padds supplied value in the correct end with the supplied number of characters
         * @param writer
         * @param nToFill
         * @param sValue
         * @param fillCharacter
         * @throws IOException
         */
        public abstract void padd(Writer writer, int nToFill, String sValue, char fillCharacter) throws IOException;
    };

    /**
     * The length of the cell.
     */
    private int       length;

    /**
     * The alignment of the cell content within the allocated space. Default is Alignment.LEFT.
     */
    private Alignment alignment = Alignment.LEFT;

    /**
     * Creates a fixed with schema cell with specified name, length and alignment.
     * 
     * @param sName
     *            The name of the cell
     * @param nLength
     *            The length of the cell
     * @param alignment
     *            The alignment of the cell content within the allocated space
     */
    public FixedWidthSchemaCell(String sName, int nLength, Alignment alignment) {
        this(sName, nLength);
        this.alignment = alignment;
    }

    /**
     * Creates a fixed with schema cell with specified name and length.
     * 
     * @param sName
     *            The name of the cell
     * @param nLength
     *            The length of the cell
     */
    public FixedWidthSchemaCell(String sName, int nLength) {
        super(sName);
        this.length = nLength;
    }

    /**
     * Creates a fixed with schema cell with specified name, length and format.
     * 
     * @param sName
     *            The name of the cell
     * @param nLength
     *            The length of the cell
     * @param cellFormat
     *            The format of the cell
     */
    public FixedWidthSchemaCell(String sName, int nLength, SchemaCellFormat cellFormat) {
        super(sName, cellFormat);
        this.length = nLength;
    }


    /**
     * Builds a Cell from a reader input.
     * 
     * @param reader
     *            The input reader
     * @param trimFillCharacters
     *            If true, fill characters are ignored while reading string values. If the cell is
     *            of any other type, the value is trimmed any way before parsing.
     * @param fillCharacter
     *            The fill character to ignore if trimFillCharacters is true.
     * @param nLineNumber
     * @param listener
     * @return A Cell filled with the parsed cell value and with the name of this schema cell.
     * @throws IOException
     * @throws ParseException
     */
    @Deprecated
    public Cell makeCell(Reader reader,
               boolean trimFillCharacters,
               char fillCharacter,
               LineEventListener listener,
               long nLineNumber) throws IOException, ParseException {

        int nOffset = 0;
        int nLength = this.length; // The actual length

        char[] buffer = new char[nLength];
        int nRead = reader.read(buffer, 0, nLength);
        if (nRead <= 0) {
            checkIfMandatory(listener, nLineNumber);
            if (this.length <= 0)
                return makeCell(EMPTY_STRING);
            else{
                return null;
            }
        }
        nLength = nRead;
        if (trimFillCharacters || getCellFormat().getCellType() != CellType.STRING) {
            while (nOffset < nLength && buffer[nOffset] == fillCharacter) {
                nOffset++;
            }
            while (nLength > nOffset && buffer[nLength - 1] == fillCharacter) {
                nLength--;
            }
            nLength -= nOffset;
        }
        Cell cell = makeCell(new String(buffer, nOffset, nLength), listener, nLineNumber);
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
    public static void fill(Writer writer, char ch, int nSize) throws IOException {
        for (int i = 0; i < nSize; i++) {
            writer.write(ch);
        }
    }

    /**
     * Writes an empty cell. Uses the fill character to fill the space.
     * 
     * @param writer
     * @param fillCharacter
     * @throws IOException
     * @throws JSaParException
     */
    public void outputEmptyCell(Writer writer, char fillCharacter) throws IOException, JSaParException {
        FixedWidthSchemaCell.fill(writer, fillCharacter, getLength());
    }

    /**
     * Writes a cell to the supplied writer using supplied fill character.
     * 
     * @param cell
     *            The cell to write
     * @param writer
     *            The writer to write to.
     * @param fillCharacter
     *            The fill character to fill empty spaces.
     * @throws IOException
     * @throws ComposeException
     */
    void output(Cell cell, Writer writer, char fillCharacter) throws IOException, JSaParException {
        String sValue = format(cell);
        output(sValue, writer, fillCharacter, getLength(), getAlignment());
    }

    /**
     * Writes a cell to the supplied writer using supplied fill character.
     * 
     * @param cell
     *            The cell to write
     * @param writer
     *            The writer to write to.
     * @param fillCharacter
     *            The fill character to fill empty spaces.
     * @param length
     *            The number of characters to write.
     * @param alignment
     *            The alignment of the cell content if the content is smaller than the cell length.
     * @param format
     *            The format to use.
     * @throws IOException
     * @throws ComposeException
     */
    static void output(String sValue, Writer writer, char fillCharacter, int length, Alignment alignment)
            throws IOException, ComposeException {
        if (sValue.length() == length) {
            writer.write(sValue);
            return;
        } else if (sValue.length() > length) {
            // If the cell value is larger than the cell length, we have to cut the value.
            alignment.fit(writer, length, sValue);
            return;
        } else {
            // Otherwise use the alignment of the schema.
            int nToFill = length - sValue.length();
            alignment.padd(writer, nToFill, sValue, fillCharacter);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.SchemaCell#clone()
     */
    public FixedWidthSchemaCell clone(){
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
