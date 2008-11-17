package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;

/**
 * @author Jonas
 * 
 */
public abstract class Schema implements Cloneable, ParseSchema {

    public enum LineTypeByTypes {
	OCCURS, CONTROL_CELL
    };

    private String lineSeparator = System.getProperty("line.separator");

    /**
     * This method should only be called by a Outputter class. Don't use this directly in your code.
     * Use a Outputter instead.
     * 
     * @param document
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    public abstract void output(Document document, Writer writer) throws IOException, JSaParException;

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

    
    private java.util.Locale locale = Locale.getDefault();

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
     * @return The line as a string or an empty string if no line was found.
     * @throws IOException
     */
    protected String parseLine(java.io.Reader reader) throws IOException {
	char chLineSeparatorNext = getLineSeparator().charAt(0);
	StringBuilder lineBuilder = new StringBuilder();
	StringBuilder pending = new StringBuilder();
	while (true) {
	    int nRead = reader.read();
	    if (nRead == -1)
		return lineBuilder.toString(); // End of input buffer.
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
	}
	return lineBuilder.toString();
    }

    public Schema clone() throws CloneNotSupportedException {
	return (Schema) super.clone();
    }

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
	// ls.replaceAll("\\n", "/n");
	// ls.replaceAll("\\r", "/r");
	sb.append(ls);
	sb.append(" locale=");
	sb.append(this.locale);
	return sb.toString();
    }

    public abstract List getSchemaLines();

    /**
     * @param lineNumber
     * @return
     */
    private SchemaLine getSchemaLine(long lineNumber) {
	long nLineMax = 0;
	for (Object oSchemaLine : this.getSchemaLines()) {
	    SchemaLine schemaLine = (SchemaLine)oSchemaLine;
	    if(schemaLine.isOccursInfinitely())
		return schemaLine;
	    nLineMax += (long)schemaLine.getOccurs();
	    if(lineNumber <= nLineMax)
		return schemaLine;
	}
	return null;
    }

    /**
     * This method should only be called by a Outputter class. Don't use this directly in your code.
     * Use a Outputter instead.
     * @param line
     * @param lineNumber
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    public void outputLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
	SchemaLine schemaLine = getSchemaLine(lineNumber);
	if(schemaLine != null){
	    if(lineNumber > 1)
		writer.append(getLineSeparator());
	    schemaLine.output(line, writer);
	}
    }
    
}
