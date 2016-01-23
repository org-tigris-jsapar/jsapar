package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jsapar.JSaParException;
import org.jsapar.Composer;
import org.jsapar.model.Line;
import org.jsapar.input.ParseSchema;
import org.jsapar.model.Document;
import org.jsapar.compose.ComposeException;

/**
 * Abstract base class for all type of jsapar schemas. A schema describes how the buffer should be
 * parsed or how the lines of a {@link Document} should be written. Usually the parse and output methods are
 * called from one of the in, out or io classes.
 * 
 * @see Xml2SchemaBuilder
 * @see Schema2XmlExtractor
 * 
 * @author Jonas
 * 
 */
public abstract class Schema implements Cloneable, ParseSchema {

    /**
     *  Defines how to determine which line type to use. 
     */
    public enum LineTypeByTypes {
        /**
         * The occurs field in {@link SchemaLine} determine how many lines that are expected of the type. 
         */
        OCCURS, 
        /**
         * There is a control cell on each line that determines the line type.
         */
        CONTROL_CELL
    };

    private Locale locale = Locale.getDefault();
    private String lineSeparator = System.getProperty("line.separator");

    /**
     * This method should only be called by a Composer class. Don't use this directly in your code.
     * Use a Composer instead.
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
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale
     *            the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
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
     * This method should only be called by a Composer class. Don't use this directly in your code.
     * Use a Composer instead.
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
     * This method should only be called by a {@link Composer} class. Don't use this directly in your code.
     * Use a {@link Composer} instead.
     * 
     * This method writes a line according to the schema line, with the same line type, which is
     * found in this schema. 
     * 
     * Line is terminated with line separator; 
     * 
     * @param line
     * @param writer
     * @throws IOException
     * @throws JSaParException
     * @return false if no matching schema line was found. true otherwise.
     */
    public boolean outputLineLn(Line line, Writer writer) throws IOException, JSaParException {
        if( outputLine(line, writer) ){
            writer.append(getLineSeparator());
            return true;
        }
        return false;
    }

    /**
     * This method should only be called by a {@link Composer} class. Don't use this directly in your code.
     * Use a {@link Composer} instead.
     * 
     * This method writes a line according to the schema line, with the same line type, which is
     * found in this schema. 
     * 
     * No line separator is written;
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
     * @throws ComposeException
     */
    public void writeLinePrefix(SchemaLine schemaLine, Writer writer) throws ComposeException, IOException {
    }

    /**
     * Loads a schema instance from an xml that is read from the supplied reader. The xml needs to comply to the
     * JSaParSchema.xsd otherwise a SchemaException is thrown.
     * 
     * @param reader
     *            The reader to read the xml from. Caller is responsible for closing the reader.
     * @return A new schema instance created based on the xml.
     * @throws SchemaException
     *             if the supplied xml does not comply to the JSaParSchema.xsd or if there is any other error while
     *             loading a schema.
     */
    public static Schema importFromXml(Reader reader) throws SchemaException {
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        return schemaBuilder.build(reader);
    }

    /**
     * Exports this schema instance as xml that complies to the JSaParSchema.xsd.
     * 
     * @param writer
     *            The writer where the xml is written. Caller is responsible for closing the writer.
     * @throws SchemaException
     *             if there is an error while writing the xml.
     */
    public void exportToXml(Writer writer) throws SchemaException {
        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, this);
    }
}
