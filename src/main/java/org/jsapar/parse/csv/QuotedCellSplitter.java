/**
 * 
 */
package org.jsapar.parse.csv;

import java.io.IOException;
import java.util.Arrays;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineReader;

/**
 * @author stejon0
 *
 */
public class QuotedCellSplitter implements CellSplitter {

    private LineReader   lineReader = null;
    private String       cellSeparator;
    private char         quoteChar;
    private CellSplitter simpleCellSplitter;

    /**
     * @param cellSeparator
     *            The string that separates the cells on the line, usually only one character but can be a combination
     *            of many characters.
     * @param quoteChar
     *            The quote character to use for quoted cells.
     * @param lineReader
     *            The line reader to read next line from in case multi-line cells are supported. Set to null if
     *            multi-line cells are not supported.
     */
    public QuotedCellSplitter(String cellSeparator, char quoteChar) {
        this(cellSeparator, quoteChar, null);
    }

    /**
     * @param cellSeparator
     *            The string that separates the cells on the line, usually only one character but can be a combination
     *            of many characters.
     * @param quoteChar
     *            The quote character to use for quoted cells.
     * @param lineReader
     *            The line reader to read next line from in case multi-line cells are supported. Set to null if
     *            multi-line cells are not supported.
     */
    public QuotedCellSplitter(String cellSeparator, char quoteChar, LineReader lineReader) {
        this.cellSeparator = cellSeparator;
        this.quoteChar = quoteChar;
        this.lineReader = lineReader;
        this.simpleCellSplitter = new SimpleCellSplitter(cellSeparator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.CellSplitter#split(java.lang.String)
     */
    @Override
    public String[] split(String sLine) throws IOException, JSaParException {
        java.util.List<String> cells = new java.util.ArrayList<String>(sLine.length() / 8);
        splitQuoted(cells, sLine);
        return cells.toArray(new String[cells.size()]);
    }

    /**
     * Recursively find all quoted cells. A quoted cell is where the quote character is the first and last character in
     * the cell. Any other quote characters within the cells are ignored.
     * 
     * @param cells
     * @param sToSplit
     * @throws JSaParException
     * @throws IOException
     */
    private void splitQuoted(java.util.List<String> cells, String sToSplit) throws IOException, JSaParException {
        int nIndex = 0;
        if (sToSplit.length() <= 0)
            return;

        int nFoundQuote = -1;
        if (sToSplit.charAt(0) == quoteChar) {
            // Quote is the first character in the string.
            nFoundQuote = 0;
            nIndex++;
        } else {
            // Search for quote character at first position after a cell separator. Otherwise ignore quotes.
            nFoundQuote = sToSplit.indexOf(cellSeparator + quoteChar);

            if (nFoundQuote < 0) {
                cells.addAll(Arrays.asList(simpleCellSplitter.split(sToSplit)));
                return;
            } else if (nFoundQuote > 0) {
                String sUnquoted = sToSplit.substring(0, nFoundQuote);
                String[] asCells = simpleCellSplitter.split(sUnquoted);
                cells.addAll(Arrays.asList(asCells));
            } else {
                cells.add("");
            }
            nIndex = nFoundQuote + cellSeparator.length() + 1;
        }

        String quoteSeparator = quoteChar + cellSeparator;
        int lineCounter = 0;
        do {
            String sFound;
            int nFoundEnd = sToSplit.indexOf(quoteSeparator, nIndex);
            if (nFoundEnd < 0) {
                // Last character is quote
                if (nIndex < sToSplit.length() && sToSplit.length() > 1
                        && sToSplit.charAt(sToSplit.length() - 1) == quoteChar) {
                    sFound = sToSplit.substring(nIndex, sToSplit.length() - 1);
                    cells.add(sFound);
                    return;
                } else {
                    if (lineReader == null)
                        throw new JSaParException(
                                "End quote is missing in line and multi-line cells are not supported for this line.");

                    String nextLine = lineReader.readLine();
                    if (nextLine == null) {
                        throw new JSaParException("End quote is missing for quoted cell. Reached end of file.");
                    }
                    // Add next line and try again to find end quote
                    sToSplit = sToSplit.substring(nIndex - 1) + lineReader.getLineSeparator() + nextLine;
                    nIndex = 1;
                    lineCounter++;
                    if (lineCounter > 100) {
                        throw new JSaParException(
                                "Searched 100 lines without finding an end of quoted cell. End quote is probably missing.");
                    }
                    continue;

                }
            }
            sFound = sToSplit.substring(nIndex, nFoundEnd);
            nIndex = nFoundEnd + 1;
            cells.add(sFound);

            // Reached end of line
            if (nIndex >= sToSplit.length()) {
                return;
            }

            nIndex += cellSeparator.length();
            sToSplit = sToSplit.substring(nIndex);
            // Continue to pick quoted cells but ignore the first quote since we require it in the condition.
            nIndex = 1;
        } while (!sToSplit.isEmpty() && sToSplit.charAt(0) == quoteChar);

        // Next cell is not quoted
        // Now handle the rest of the string with a recursive call.
        splitQuoted(cells, sToSplit);
    }

    /**
     * @param lineReader
     *            Assigns line reader to be able to split multi line cells.
     */
    public void setLineReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

}
