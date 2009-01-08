package org.jsapar;

import java.io.Serializable;
import java.util.Iterator;

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
    java.util.ArrayList<Line> lines = null;

    /**
     * Creates an empty document.
     */
    public Document() {
        this.lines = new java.util.ArrayList<Line>();
    }

    /**
     * Creates a document with an initial capacity to contain nInitialCapacity
     * lines.
     * 
     * @param nInitialCapacity
     */
    public Document(int nInitialCapacity) {
        this.lines = new java.util.ArrayList<Line>(nInitialCapacity);
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
    public java.util.Collection<Line> getLines() {
        return (java.util.Collection<Line>) lines.clone();
    }

    /**
     * Returns the line at the specified index. First line has index 0.
     * @param index
     * @return The line at the specified index. 
     */
    public Line getLine(int index) {
        return this.lines.get(index);
    }

    /**
     * Removes the first line and returns it.
     * 
     * @return The line that was just removed.
     */
    public Line removeFirstLine() {
        return this.lines.remove(0);
    }

    /**
     * Adds a line last in the list of lines.
     * 
     * @param line
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
}
