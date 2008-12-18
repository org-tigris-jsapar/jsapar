package org.jsapar.input;

import java.io.IOException;
import java.util.List;

import org.jsapar.Document;
import org.jsapar.JSaParException;

/**
 * This class is the starting point for parsing a file (or other source). <br>
 * <br>
 * You have two alternatives: <br>
 * Either you use the build methods to build a complete Document from the input source. <br>
 * The second alternative is to use the parse method. In that case you will receive a callback event
 * for each line that is parsed. <br>
 * For large sources of data when it is essential not over-load the memory, the parse method will be
 * the preferred choice since you never have to load everything into the memory. The build methods
 * on the other hand are slightly easier to use and understand.
 * 
 * @author stejon0
 * 
 */
public class Parser implements ParsingEventListener {
    java.util.List<ParsingEventListener> parsingEventListeners = new java.util.LinkedList<ParsingEventListener>();

    /**
     * Reads characters from the reader and parses them into a Document. If there is an error while
     * parsing, this method will throw an exception of type org.jsapar.input.ParseException. <br>
     * Use this method only if you are sure that the whole file can be parsed into memory. If the
     * file is too big, a OutOfMemory exception will be thrown. For large files use the parse method
     * instead.
     * 
     * @param reader
     * @param parser
     *            The ParseSchema to use to build the document.
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    public org.jsapar.Document build(java.io.Reader reader, ParseSchema parser) throws JSaParException {
        AbortingDocumentBuilder docBuilder = new AbortingDocumentBuilder();
        return docBuilder.build(reader, parser);
    }

    /**
     * Reads characters from the reader and parses them into a Document. If there is an error while
     * parsing a cell, this method will add a CellParseError object to the supplied parseError list.
     * If there is an error reading the input or an error at the structure of the input, a
     * JSaParException will be thrown.<br>
     * Use this method only if you are sure that the whole file can be parsed into memory. If the
     * file is too big, a OutOfMemory exception will be thrown. For large files use the parse method
     * instead.
     * 
     * @param reader
     * @param parser
     *            The ParseSchema to use to build the document.
     * @param parseErrors
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    public Document build(java.io.Reader reader, ParseSchema parser, List<CellParseError> parseErrors)
            throws JSaParException {
        DocumentBuilder docBuilder = new DocumentBuilder(parseErrors);
        return docBuilder.build(reader, parser);
    }

    /**
     * Reads characters from the reader and parses them into a Line. Once a Line is completed, a
     * LineParsedEvent is generated to all registered event listeners. If there is an error while
     * parsing a line, a LineErrorEvent is generated to all registered event listeners <br>
     * Before calling this method you have to call addParsingEventListener to be able to handle the
     * result<br>
     * If there is an error reading the input or an error at the structure of the input, a
     * JSaParException will be thrown.
     * 
     */
    /**
     * @param reader
     * @param parser
     * @throws JSaParException
     */
    public void parse(java.io.Reader reader, ParseSchema parser) throws JSaParException {
        try {
            parser.parse(reader, this);
        } catch (IOException e) {
            throw new ParseException("Failed to read input", e);
        }
    }

    /**
     * Registers an event listener to be able to receive events when a line has been parsed.
     * 
     * @param eventListener
     */
    public void addParsingEventListener(ParsingEventListener eventListener) {
        this.parsingEventListeners.add(eventListener);
    }

    /**
     * Forwards the event to all registered listeners.
     * 
     * @throws ParseException
     */
    @Override
    public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
        for (ParsingEventListener l : this.parsingEventListeners) {
            l.lineErrorErrorEvent(event);
        }
    }

    /**
     * Forwards the event to all registered listeners.
     * 
     * @throws JSaParException
     */
    @Override
    public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
        for (ParsingEventListener l : this.parsingEventListeners) {
            l.lineParsedEvent(event);
        }
    }

    /**
     * Private class that converts the events from the parser into a Document object. This builder
     * adds errors to a list of CellParseErrors.
     * 
     * @author stejon0
     * 
     */
    private class DocumentBuilder {
        private Document document = new Document();
        private List<CellParseError> parseErrors;

        public DocumentBuilder(List<CellParseError> parseErrors) {
            this.parseErrors = parseErrors;
        }

        public Document build(java.io.Reader reader, ParseSchema documentBuilder) throws JSaParException {
            addParsingEventListener(new ParsingEventListener() {

                @Override
                public void lineErrorErrorEvent(LineErrorEvent event) {
                    parseErrors.add(event.getCellParseError());
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            });

            try {
                documentBuilder.parse(reader, Parser.this);
            } catch (IOException e) {
                throw new ParseException("Failed to read input", e);
            }
            return this.document;
        }
    }

    /**
     * Private class that converts the events from the parser into a Document object. This builder
     * throws an exception at first error that occurs.
     * 
     * @author stejon0
     * 
     */
    private class AbortingDocumentBuilder {
        private Document document = new Document();

        public AbortingDocumentBuilder() {
        }

        public Document build(java.io.Reader reader, ParseSchema documentBuilder) throws JSaParException {
            addParsingEventListener(new ParsingEventListener() {

                @Override
                public void lineErrorErrorEvent(LineErrorEvent event) throws ParseException {
                    throw new ParseException(event.getCellParseError());
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            });

            try {
                documentBuilder.parse(reader, Parser.this);
            } catch (IOException e) {
                throw new ParseException("Failed to read input", e);
            }
            return this.document;
        }
    }
}
