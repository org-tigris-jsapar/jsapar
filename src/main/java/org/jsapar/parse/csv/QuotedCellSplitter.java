package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;

import java.io.IOException;
import java.util.List;

/**
 * Responsible for splitting quoted cells into a raw array of Strings.
 *
 */
class QuotedCellSplitter implements CellSplitter {
    private final BufferedLineReader lineReader;
    private final String       cellSeparator;
    private final char         quoteChar;
    private final CellSplitter cellSplitter;
    private final int maxLinesWithinCell;
    private final String separatorAndQuote;
    private final String quoteAndSeparator;

    /**
     * @param cellSeparator
     *            The string that separates the cells on the line, usually only one character but can be a combination
     *            of many characters.
     * @param quoteChar
     *            The quote character to use for quoted cells.
     */
    QuotedCellSplitter(String cellSeparator, char quoteChar) {
        this(cellSeparator, quoteChar, null, 25);
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
     * @param maxLinesWithinCell Maximum number of line breaks allowed within one cell.
     */
    QuotedCellSplitter(String cellSeparator, char quoteChar, BufferedLineReader lineReader, int maxLinesWithinCell) {
        this.cellSeparator = cellSeparator;
        this.quoteChar = quoteChar;
        this.lineReader = lineReader;
        this.cellSplitter = new UnquotedCellSplitter(cellSeparator);
        this.maxLinesWithinCell = maxLinesWithinCell;
        this.separatorAndQuote = cellSeparator + quoteChar;
        this.quoteAndSeparator = quoteChar + cellSeparator;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.input.parse.CellSplitter#split(java.lang.String)
     */
    @Override
    public List<String> split(String sLine, List<String> toAddTo) throws IOException, JSaParException {
        if(sLine.isEmpty()) {
            return toAddTo;
        }
        splitQuoted(sLine, 0, toAddTo);
        return toAddTo;
    }

    /**
     * Recursively find all quoted cells. A quoted cell is where the quote character is the first and last character in
     * the cell. Any other quote characters within the cells are ignored.
     * 
     * @param sToSplit String to split.
     * @param cells Resulting list of split cells built by this method.
     * @throws IOException In case there is an error reading the source.
     */
    private void splitQuoted(String sToSplit, int nIndex, List<String> cells) throws IOException {
        if (nIndex>=sToSplit.length())
            return;

        if (sToSplit.charAt(nIndex) == quoteChar) {
            if(sToSplit.length()==1) {
                // Quote is the only character in the string.
                cells.add(sToSplit);
                return;
            }
            // Quote is the first character in the string.
        } else {
            // Search for quote character at first position after a cell separator. Otherwise ignore quotes.
            int nFoundQuoteIndex = sToSplit.indexOf(separatorAndQuote, nIndex);

            if (nFoundQuoteIndex < 0) {
                if(nIndex == 0)
                    cellSplitter.split(sToSplit, cells);
                else
                    cellSplitter.split(sToSplit.substring(nIndex), cells);
                return;
            } else if (nFoundQuoteIndex > 0) {
                String sUnquoted = sToSplit.substring(nIndex, nFoundQuoteIndex);
                cellSplitter.split(sUnquoted, cells);
            } else {
                cells.add("");
            }
            nIndex = nFoundQuoteIndex + cellSeparator.length();  // Behind cell separator
        }

        int lineCounter = 1;
        // We do this in a do-while loop instead of recursive call since int will exhaust stack in case line separator
        // is not correctly specified.
        do {
            nIndex++; // Behind first quote
            int nFoundEnd = sToSplit.indexOf(quoteAndSeparator, nIndex);
            if (nFoundEnd < 0) {
                // Last character is quote
                if (nIndex < sToSplit.length() && sToSplit.length() > 1
                        && sToSplit.charAt(sToSplit.length() - 1) == quoteChar) {
                    final String sFound = sToSplit.substring(nIndex, sToSplit.length() - 1);
                    cells.add(sFound);
                    return;
                }
                if(lineCounter <= 1) {
                    int nextQuoteIndex = sToSplit.indexOf(quoteChar, nIndex);
                    if (nextQuoteIndex >= 0) {
                        // Find next cell separator after the end quote character
                        int endOfCellIndex = sToSplit.indexOf(cellSeparator, nextQuoteIndex + 1);
                        endOfCellIndex = endOfCellIndex >= 0 ? endOfCellIndex : sToSplit.length();
                        if (nextQuoteIndex < endOfCellIndex) {
                            // The end quote is within the same cell but not last character, consider quote to be part of string.
                            cells.add(sToSplit.substring(nIndex - 1, endOfCellIndex));
                            nIndex = endOfCellIndex + cellSeparator.length();
                            continue;
                        }
                    }
                }
                if(lineReader == null || maxLinesWithinCell <= 1){
                    int endOfCellIndex = sToSplit.indexOf(cellSeparator, nIndex);
                    cells.add(sToSplit.substring(nIndex - 1, endOfCellIndex));
                    nIndex = endOfCellIndex + cellSeparator.length();
                    continue;

                }
                else if (lineCounter > maxLinesWithinCell) {
                    throw new JSaParException(
                            "Searched "+lineCounter+" lines without finding an end of quoted cell. End quote is probably missing.");
                }
                String nextLine = lineReader.peekLine();
                if (nextLine == null) {
                    throw new JSaParException("End quote is missing for quoted cell. Reached end of file.");
                }
                lineCounter++;
                // Add next line and try again to find end quote
                sToSplit = sToSplit.substring(nIndex - 1) + lineReader.getLineSeparator() + nextLine;
                nIndex = 0;
                continue;
            }
            final String sFound = sToSplit.substring(nIndex, nFoundEnd);
            nIndex = nFoundEnd + 1; // Behind quote
            cells.add(sFound);

            // Continue to pick quoted cells.
            nIndex += cellSeparator.length();
        } while (nIndex < sToSplit.length() && sToSplit.charAt(nIndex) == quoteChar);

        // Reached end of line
        if (nIndex >= sToSplit.length()) {
            return;
        }

        // Next cell is not quoted
        // Now handle the rest of the string with a recursive call.
        splitQuoted(sToSplit, nIndex, cells);
    }

}
