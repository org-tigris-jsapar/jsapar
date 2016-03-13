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

        },
        CENTER {
            @Override
            public void fit(Writer writer, int length, String sValue) throws IOException {
                writer.write(sValue, (sValue.length()-length)/2, length);
            }

        },
        RIGHT{
            @Override
            public void fit(Writer writer, int length, String sValue) throws IOException {
                writer.write(sValue, sValue.length() - length, length);
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
        
    }

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
