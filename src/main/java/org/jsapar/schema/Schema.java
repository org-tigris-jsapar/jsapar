package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.output.OutputException;

/**
 * Abstract base class for all type of jsapar schemas. A schema describes how the buffer should be
 * parsed or how the lines of a Document should be written. Usually the parse and output methods are
 * called from one of the in, out or io classes.
 * 
 * @see Xml2SchemaBuilder
 * @see Schema2XmlExtractor
 * 
 * @author Jonas
 * 
 */
public abstract class Schema implements Cloneable, ParseSchema {

    public enum LineTypeByTypes {
        OCCURS, CONTROL_CELL
    };

    private java.util.Locale locale = Locale.getDefault();
    private String lineSeparator = System.getProperty("line.separator");

    /**
     * This method should only be called by a Outputter class. Don't use this directly in your code.
     * Use a Outputter instead.
     * 
     * @param iterator
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    public abstract void output(Iterator<Line> iterator, Writer writer) throws IOException, JSaParException;

    /**
     * Called before output() in order to set up or write file header.
     * 
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    public abstract void outputBefore(Writer writer) throws IOException, JSaParException;

    /**
     * Called after output() in order to clean up or write file footer.
     * 
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    public abstract void outputAfter(Writer writer) throws IOException, JSaParException;

    /**
     * @return the lineSeparator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Sets the line separator string. Default value is the system default (Retrieved by
     * System.getProperty("line.separator")).
     * 
     * @param lineSeparator
     *            the lineSeparator to set.
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * @return the locale
     */
    public java.util.Locale getLocale() {
        return locale;
    }

    /**
     * @param locale
     *            the locale to set
     */
    public void setLocale(java.util.Locale locale) {
        this.locale = locale;
    }

    @Override
    public abstract void parse(java.io.Reader reader, ParsingEventListener listener) throws JSaParException,
            IOException;

    /**
     * Reads a line from the reader.
     * 
     * @param reader
     * @return The line as a string or null if end of buffer was encountered.
     * @throws IOException
     * @throws JSaParException 
     */
    protected String parseLine(java.io.Reader reader) throws IOException, JSaParException {
        char chLineSeparatorNext = getLineSeparator().charAt(0);
        StringBuilder lineBuilder = new StringBuilder();
        StringBuilder pending = new StringBuilder();
        while (true) {
            int nRead = reader.read();
            if (nRead == -1) {
                // End of input buffer.
                if (lineBuilder.length() > 0)
                    return lineBuilder.toString();
                else
                    return null;
            }
            char chRead = (char) nRead;
            if (chRead == chLineSeparatorNext) {
                pending.append(chRead);
                if (getLineSeparator().length() > pending.length())
                    chLineSeparatorNext = getLineSeparator().charAt(pending.length());
                else
                    break; // End of line found.
            }
            // It was not a complete line separator.
            else if (pending.length() > 0) {
                // Move pending characters to lineBuilder.
                lineBuilder.append(pending);
                pending.setLength(0);
                lineBuilder.append(chRead);
            } else
                lineBuilder.append(chRead);
            if(lineBuilder.length() > 1000000)
                throw new JSaParException("Line size exceeds 1M characters. Probably wrong line-separator for the line type within the schema.");
        }
        return lineBuilder.toString();
    }

    public Schema clone(){
        try {
            return (Schema) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @return list of all schema lines.
     */
    protected abstract List<? extends SchemaLine> getSchemaLines();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" lineSeparator=");
        String ls = this.lineSeparator;
        // ls.replaceAll("\n", "\\\\n");
        // ls.replaceAll("\r", "\\\\r");
        sb.append(ls);
        sb.append(" locale=");
        sb.append(this.locale);
        return sb.toString();
    }

    /**
     * @param lineNumber
     * @return The line-schema for supplied line number.
     */
    private SchemaLine getSchemaLine(long lineNumber) {
        long nLineMax = 0;
        for (Object oSchemaLine : this.getSchemaLines()) {
            SchemaLine schemaLine = (SchemaLine) oSchemaLine;
            if (schemaLine.isOccursInfinitely())
                return schemaLine;
            nLineMax += (long) schemaLine.getOccurs();
            if (lineNumber <= nLineMax)
                return schemaLine;
        }
        return null;
    }

    /**
     * @param lineType
     * @return The schema line with the supplied line type name. Null if no such schema line was
     *         found.
     */
    public abstract SchemaLine getSchemaLine(String lineType);

    /**
     * This method should only be called by a Outputter class. Don't use this directly in your code.
     * Use a Outputter instead.
     * 
     * @param line
     * @param lineNumber
     * @param writer
     * @throws IOException
     * @throws JSaParException
     * @return false if no matching schema line was found. true otherwise.
     */
    public boolean outputLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
        SchemaLine schemaLine = getSchemaLine(lineNumber);
        if (schemaLine == null) {
            return false;
        }
        if (lineNumber > 1)
            writer.append(getLineSeparator());
        outputLine(schemaLine, line, writer);
        return true;
    }

    /**
     * This method should only be called by a Outputter class. Don't use this directly in your code.
     * Use a Outputter instead.
     * 
     * This method writes a line according to the schema line, with the same line type, which is
     * found in this schema.
     * 
     * @param line
     * @param writer
     * @throws IOException
     * @throws JSaParException
     * @return false if no matching schema line was found. true otherwise.
     */
    public boolean outputLine(Line line, Writer writer) throws IOException, JSaParException {
        SchemaLine schemaLine = getSchemaLine(line.getLineType());
        if (schemaLine == null) {
            return false;
        }
        outputLine(schemaLine, line, writer);
        writer.append(getLineSeparator());
        return true;
    }

    protected void outputLine(SchemaLine schemaLine, Line line, Writer writer) throws IOException, JSaParException {
        schemaLine.output(line, writer);
    }

    /**
     * @return Number of schema lines of this schema.
     */
    public abstract int getSchemaLinesCount();

    /**
     * @param index
     * @return The schema line with the specified index.
     */
    public abstract SchemaLine getSchemaLineAt(int index);

    /**
     * @param line
     *            The line to find index for.
     * @return The index (starting from 0) of which the supplied line have within the schema. -1 if
     *         no such line is found.
     */
    public int getIndexOf(SchemaLine line) {
        for (int i = 0; i < getSchemaLinesCount(); i++) {
            if (line.equals(getSchemaLineAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param schemaLine
     * @param writer
     * @throws IOException 
     * @throws OutputException 
     */
    public void writeLinePrefix(SchemaLine schemaLine, Writer writer) throws OutputException, IOException {
    }

}
