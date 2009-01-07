package org.jsapar.input;

import java.io.IOException;
import java.util.LinkedList;
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
 * on the other hand are slightly easier to use and understand.<br>
 * <br>
 * You have to add at least one ParseSchema before using an instance of Parser. You can do this
 * either by using the constructor with one or a list of ParseSchema, or you can add it after
 * construction with the addSchema() method.<br>
 * <br>
 * If more than one schema is present, they are used in the order they were added. The input buffer
 * is read until end of buffer is reached.
 * 
 * @author stejon0
 * 
 */
public class Parser implements ParsingEventListener {
    private List<ParsingEventListener> parsingEventListeners = new LinkedList<ParsingEventListener>();
    private List<ParseSchema> schemas = new LinkedList<ParseSchema>();

    /**
     * Creates an empty parser. You have to add at least one ParseSchema before using this Parser.
     */
    public Parser() {
    }

    /**
     * Creates a Parser with one schema. You can add more schemas by calling addSchema().
     * 
     * @param schema
     */
    public Parser(ParseSchema schema) {
        this.addSchema(schema);
    }

    /**
     * Creates a Parser with a list of schemas. You can add more schemas by calling addSchema().
     * 
     * @param schemas
     */
    public Parser(List<ParseSchema> schemas) {
        this.schemas.addAll(schemas);
    }

    /**
     * Adds a schema to this parser.
     * 
     * @param schema
     */
    public void addSchema(ParseSchema schema) {
        schemas.add(schema);
    }

    /**
     * Reads characters from the reader and parses them into a Document. If there is an error while
     * parsing, this method will throw an exception of type org.jsapar.input.ParseException. <br>
     * Use this method only if you are sure that the whole file can be parsed into memory. If the
     * file is too big, a OutOfMemory exception will be thrown. For large files use the parse method
     * instead.
     * 
     * @param reader
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    public org.jsapar.Document build(java.io.Reader reader) throws JSaParException {
        AbortingDocumentBuilder docBuilder = new AbortingDocumentBuilder();
        return docBuilder.build(reader);
    }

    /**
     * Reads characters from the reader and parses them into a Document. If there is an error while
     * parsing, this method will throw an exception of type org.jsapar.input.ParseException. <br>
     * Use this method only if you are sure that the whole file can be parsed into memory. If the
     * file is too big, a OutOfMemory exception will be thrown. For large files use the parse method
     * instead.<br>
     * <br>
     * This method will clear all existing ParseSchemas and replace them with the supplied schema.<br>
     * 
     * @deprecated Deprecated since version 0.4.0. Use build(Reader) instead. 
     * 
     * @param reader
     * @param schema
     *            The ParseSchema to use to build the document.
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    @Deprecated
    public org.jsapar.Document build(java.io.Reader reader, ParseSchema schema) throws JSaParException {
        this.schemas.clear();
        addSchema(schema);
        return build(reader);
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
     * @param parseErrors
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    public Document build(java.io.Reader reader, List<CellParseError> parseErrors)
            throws JSaParException {
        DocumentBuilder docBuilder = new DocumentBuilder(parseErrors);
        return docBuilder.build(reader);
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
     * <br>
     * This method will clear all existing ParseSchemas and replace them with the supplied schema.<br>
     * 
     * @deprecated Deprecated since version 0.4.0. Use build(Reader) instead.
     *  
     * @param reader
     * @param schema
     *            The ParseSchema to use to build the document.
     * @param parseErrors
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    @Deprecated
    public Document build(java.io.Reader reader, ParseSchema schema, List<CellParseError> parseErrors)
            throws JSaParException {
        this.schemas.clear();
        addSchema(schema);
        return build(reader, parseErrors);
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
     * @param reader
     * @throws JSaParException
     */
    public void parse(java.io.Reader reader) throws JSaParException {
        try {
            for (ParseSchema schema : Parser.this.schemas) {
                schema.parse(reader, Parser.this);
            }
        } catch (IOException e) {
            throw new ParseException("Failed to read input", e);
        }
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
     * <br>
     * This method will clear all existing ParseSchemas and replace them with the supplied schema.<br>
     * 
     * @deprecated Deprecated since version 0.4.0. Use parse(Reader) instead.
     * @param reader
     * @param schema
     * @throws JSaParException
     */
    @Deprecated
    public void parse(java.io.Reader reader, ParseSchema schema) throws JSaParException {
        this.schemas.clear();
        addSchema(schema);
        parse(reader);
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

        public Document build(java.io.Reader reader) throws JSaParException {
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
                for (ParseSchema schema : Parser.this.schemas) {
                    schema.parse(reader, Parser.this);
                }
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

        public Document build(java.io.Reader reader) throws JSaParException {
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
                for (ParseSchema schema : Parser.this.schemas) {
                    schema.parse(reader, Parser.this);
                }
            } catch (IOException e) {
                throw new ParseException("Failed to read input", e);
            }
            return this.document;
        }
    }
}
