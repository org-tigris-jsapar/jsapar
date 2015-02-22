package org.jsapar;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsapar.input.ParseSchema;
import org.jsapar.input.Parser;
import org.jsapar.input.XmlDocumentParser;

/**
 * A document contains multiple lines where each line corresponds to a line of
 * the input buffer. Lines can be retreived by index O(1), where first line has
 * index 0. Note that the class is not synchronized internally. If multiple
 * threads access the same instance, external synchronization is required.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Document implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6098681751483565285L;
    ArrayList<Line> lines = null;

    /**
     * Creates an empty document.
     */
    public Document() {
        this.lines = new ArrayList<>();
    }

    /**
     * Creates a document with an initial capacity to contain nInitialCapacity
     * lines.
     * 
     * @param nInitialCapacity
     */
    public Document(int nInitialCapacity) {
        this.lines = new ArrayList<>(nInitialCapacity);
    }

    /**
     * For better performance while iterating multiple lines, it is better to
     * call the {@link #getLineIterator()} method.
     * 
     * @return A clone of the internal collection that contains all the lines of
     *         this documents. Altering the returned collection will not alter
     *         the original collection of the Document.
     * @see #getLineIterator()
     */
    @SuppressWarnings("unchecked")
    public List<Line> getLines() {
        return (List<Line>) lines.clone();
    }

    /**
     * Returns the line at the specified index. First line has index 0.
     * @param index
     * @return The line at the specified index. 
     * @throws IndexOutOfBoundsException
     *             - if the index is out of range (index < 0 || index >= size())
     */
    public Line getLine(int index) {
        return this.lines.get(index);
    }

    /**
     * Removes the first line of this document and returns it.
     * 
     * @return The line that was just removed.
     * @throws IndexOutOfBoundsException
     *             - if the document is empty, i.e. it has not lines.
     */
    public Line removeFirstLine() {
        return removeLineAt(0);
    }

    /**
     * Adds the given line at the end of the collection of lines within the document.
     * 
     * @param line
     *            The line to add.
     */
    public void addLine(Line line) {
        this.lines.add(line);
    }

    /**
     * Returns an iterator that will iterate all the lines of this document.
     * @return An iterator that will iterate all the lines of this document.
     */
    public Iterator<Line> getLineIterator() {
        return this.lines.iterator();
    }

    /**
     * Gets the number of lines contained by this document.
     * @return The number of lines.
     */
    public int getNumberOfLines() {
        return this.lines.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i=1;
        for(Line line : this.lines){
            sb.append("{Line ");
            sb.append(i++);
            sb.append(": ");
            sb.append(line);
            sb.append("}, ");
        }
        return sb.toString();
    }
    
    /**
     * @return True if there are no lines within this document, false otherwise.
     */
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /**
     * This implementation is quite slow if there are a lot of lines and the line type asked for is not among the top
     * lines. It will iterate through all lines in order to find supplied line type.
     * 
     * @param lineType
     *            The line type to test.
     * @return True if there is at least one line of the supplied line type, false otherwise.
     */
    public boolean containsLineType(String lineType) {
        return findFirstLineOfType(lineType) != null;
    }

    /**
     * This implementation is quite slow if there are a lot of lines and the line type asked for is not among the top
     * lines. It will iterate through all lines in order to find supplied line type.
     * 
     * @param lineType
     *            The line type to find first line of.
     * @return The first line in the document that has the supplied line type.
     */
    public Line findFirstLineOfType(String lineType) {
        assert lineType != null;
        for (Line line : lines) {
            if (lineType.equals(line.getLineType()))
                return line;
        }
        return null;
    }
    
    /**
     * @param lineType
     * @return A list of all lines with a line type that equals supplied line type or an empty list if no such line
     *         exist within this document.
     */
    public List<Line> findLinesOfType(String lineType) {
        List<Line> result = new ArrayList<>();
        assert lineType != null;

        for (Line line : lines) {
            if (lineType.equals(line.getLineType()))
                result.add(line);
        }
        return result;
    }

    /**
     * @return The number of lines within this document. The same as getNumberOfLines()
     */
    public int size() {
        return lines.size();
    }

    /**
     * Removes supplied line from this document. Only if the supplied line instance is part of the document it will be
     * removed since equality will be tested upon same instance and not by using equals() method.
     * 
     * @param line
     * @return true if line was found and removed from document. False otherwise.
     */
    public boolean removeLine(Line line) {
        assert line != null;
        Iterator<Line> i = lines.iterator();
        while (i.hasNext()) {
            if (i.next() == line) {
                i.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes line at supplied index.
     * 
     * @param index
     * @return The line that was removed.
     * @throws IndexOutOfBoundsException
     *             - if the index is out of range (index < 0 || index >= size())
     */
    public Line removeLineAt(int index){
        return lines.remove(index);
    }

    /**
     * Loads a Document from xml where the format is defined in XMLDocumentFormat.xsd. If you are dealing with a very
     * large file or data source you should consider to use the XmlDocumentParser class as schema and use the event
     * based model instead to get an event for each line.
     * 
     * @param reader
     * @return A Document with lines and cells as defined in the input xml.
     * @throws JSaParException If there is an error while reading the xml.
     */
    public static Document loadFromXml(Reader reader) throws JSaParException{
        ParseSchema schema = new XmlDocumentParser();
        Parser docBuilder = new Parser(schema);
        return docBuilder.build(reader);
    }
    
    /**
     * TODO This feature remains to be implemented.
     * @param writer
     */
    @SuppressWarnings("unused")
    private void saveAsXml(Writer writer){
        //...
    }
}
