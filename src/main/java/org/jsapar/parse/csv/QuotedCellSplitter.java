package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for splitting quoted cells into a raw array of Strings.
 *
 */
public class QuotedCellSplitter implements CellSplitter {
    private static final String[] EMPTY_LINE = new String[0];

    private BufferedLineReader lineReader = null;
    private final String       cellSeparator;
    private final char         quoteChar;
    private CellSplitter cellSplitter;
    private final String separatorAndQuote;
    private final String quoteAndSeparator;

    /**
     * @param cellSeparator
     *            The string that separates the cells on the line, usually only one character but can be a combination
     *            of many characters.
     * @param quoteChar
     *            The quote character to use for quoted cells.
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
    public QuotedCellSplitter(String cellSeparator, char quoteChar, BufferedLineReader lineReader) {
        this.cellSeparator = cellSeparator;
        this.quoteChar = quoteChar;
        this.lineReader = lineReader;
        this.cellSplitter = new SimpleCellSplitter(cellSeparator);
        this.separatorAndQuote = cellSeparator + quoteChar;
        this.quoteAndSeparator = quoteChar + cellSeparator;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.CellSplitter#split(java.lang.String)
     */
    @Override
    public String[] split(String sLine) throws IOException, JSaParException {
        if(sLine.isEmpty()) {
            return EMPTY_LINE;
        }
        java.util.List<String> cells = new java.util.ArrayList<>(sLine.length() / 8);
        splitQuoted(cells, sLine);
        if(cells.size() == 1 && cells.get(0).trim().isEmpty())
            return EMPTY_LINE;

        return cells.toArray(new String[cells.size()]);
    }

    /**
     * Recursively find all quoted cells. A quoted cell is where the quote character is the first and last character in
     * the cell. Any other quote characters within the cells are ignored.
     * 
     * @param cells Resulting list of split cells built by this method.
     * @param sToSplit String to split.
     * @throws IOException In case there is an error reading the source.
     */
    private void splitQuoted(List<String> cells, String sToSplit) throws IOException {
        int nIndex = 0;
        if (sToSplit.length() <= 0)
            return;

        if (sToSplit.charAt(0) == quoteChar) {
            if(sToSplit.length()==1) {
                // Quote is the only character in the string.
                cells.add(sToSplit);
                return;
            }
            // Quote is the first character in the string.
            nIndex++;
        } else {
            // Search for quote character at first position after a cell separator. Otherwise ignore quotes.
            int nFoundQuoteIndex = sToSplit.indexOf(separatorAndQuote);

            if (nFoundQuoteIndex < 0) {
                cells.addAll(Arrays.asList(cellSplitter.split(sToSplit)));
                return;
            } else if (nFoundQuoteIndex > 0) {
                String sUnquoted = sToSplit.substring(0, nFoundQuoteIndex);
                String[] asCells = cellSplitter.split(sUnquoted);
                cells.addAll(Arrays.asList(asCells));
            } else {
                cells.add("");
            }
            nIndex = nFoundQuoteIndex + cellSeparator.length() + 1;
        }

        int lineCounter = 0;
        // We do this in a do-while loop instead of recursive call since int will exhaust stack in case line separator
        // is not correctly specified.
        do {
            String sFound;
            int nFoundEnd = sToSplit.indexOf(quoteAndSeparator, nIndex);
            if (nFoundEnd < 0) {
                // Last character is quote
                if (nIndex < sToSplit.length() && sToSplit.length() > 1
                        && sToSplit.charAt(sToSplit.length() - 1) == quoteChar) {
                    sFound = sToSplit.substring(nIndex, sToSplit.length() - 1);
                    cells.add(sFound);
                    return;
                }
                if(lineCounter == 0) {
                    int nextQuoteIndex = sToSplit.indexOf(quoteChar, nIndex);
                    if (nextQuoteIndex >= 0) {
                        int endOfCellIndex = sToSplit.indexOf(cellSeparator, nIndex);
                        endOfCellIndex = endOfCellIndex >= 0 ? endOfCellIndex : sToSplit.length();
                        if (nextQuoteIndex < endOfCellIndex) {
                            // The end quote is within the same cell but not last character, consider quote to be part of string.
                            cells.add(sToSplit.substring(nIndex - 1, endOfCellIndex));
                            nIndex = endOfCellIndex + cellSeparator.length();
                            if (nIndex >= sToSplit.length()) {
                                return;
                            }
                            sToSplit = sToSplit.substring(nIndex);
                            nIndex = 1;
                            continue;
                        }
                    }
                }
                if (lineReader == null)
                    throw new JSaParException(
                            "End quote is missing in line and multi-line cells are not supported for this line.");

                String nextLine = lineReader.peekLine();
                if (nextLine == null) {
                    throw new JSaParException("End quote is missing for quoted cell. Reached end of file.");
                }
                // Add next line and try again to find end quote
                sToSplit = sToSplit.substring(nIndex - 1) + lineReader.getLineSeparator() + nextLine;
                nIndex = 1;
                lineCounter++;
                if (lineCounter > 25) {
                    throw new JSaParException(
                            "Searched 25 lines without finding an end of quoted cell. End quote is probably missing.");
                }
                continue;
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

}
