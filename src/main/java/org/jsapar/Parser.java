package org.jsapar;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.parse.*;
import org.jsapar.model.Document;
import org.jsapar.parse.SchemaParserFactory;
import org.jsapar.convert.MaxErrorsExceededException;

/**
 * This class is the starting point for parsing a file (or other source). <br>
 * <br>
 * You have the following options:
 * <ol>
 * <li>Use one of the build() methods to build a complete org.jsapar.model.Document from the input source.
 * </li>
 * <li>Use the parse() method. In that case you will receive a callback event for each line that is
 * parsed.</li>
 * <li>Use the buildJava() method to build java objects directly. There are quite a few requirement
 * that have to be fulfilled if that method is supposed to work.</li>
 * </ol>
 * 
 * For large sources of data when it is essential not over-load the memory, the parse method will be
 * the preferred choice since you never have to load everything into the memory. The build methods
 * on the other hand are slightly easier to use and understand.<br>
 * <br>
 * 
 * @see TextComposer
 * @see Converter
 * @see FilterConverter
 * @author stejon0
 * 
 */
public class Parser implements LineEventListener {
    private List<LineEventListener> parsingEventListeners = new LinkedList<LineEventListener>();
    private SchemaParserFactory     parserFactory         = new SchemaParserFactory();
    ParseSchema schema;

    /**
     * Creates a Parser with a schema.
     * 
     * @param schema
     */
    public Parser(ParseSchema schema) {
        this.schema = schema;
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
    public Document build(Reader reader) throws JSaParException {
        AbortingDocumentBuilder docBuilder = new AbortingDocumentBuilder();
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
     * @param reader
     * @param parseErrors
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    public Document build(Reader reader, List<CellParseError> parseErrors) throws JSaParException {
        DocumentBuilder docBuilder = new DocumentBuilder(parseErrors);
        return docBuilder.build(reader);
    }

    /**
     * Reads characters from the reader and parses them into a Document. If there is an error while
     * parsing a cell, this method will add a CellParseError object to the supplied parseError list.
     * If the number of errors exceeds the supplied maxNumberOfErrors, a MaxErrorsExceededException
     * is thrown. .<br/>
     * If there is an error reading the input or an error at the structure of the input, a
     * JSaParException will be thrown.<br/>
     * Use this method only if you are sure that the whole file can be parsed into memory. If the
     * file is too big, a OutOfMemory exception will be thrown. For large files use the parse method
     * instead.
     * 
     * @param reader
     * @param parseErrors
     * @param maxNumberOfErrors The maximum number of errors accepted.
     * @return A complete Document representing the parsed input buffer.
     * @throws JSaParException
     */
    public Document build(java.io.Reader reader, List<CellParseError> parseErrors, int maxNumberOfErrors)
            throws JSaParException {
        DocumentBuilder docBuilder = new DocumentBuilder(parseErrors, maxNumberOfErrors);
        return docBuilder.build(reader);
    }

    /**
     * Reads characters from the reader and parses them into a list of java objects denoted by the
     * schema. See org.jsapar.BeanParser for more information about how to build java objects
     * from an input source.
     * 
     * @param reader
     * @param parseErrors
     *            Supply an empty list of CellParseError and this method will populate the list if
     *            errors are found while parsing.
     * @return A collection of java objects. The class of each java object is determined by the
     *         lineType of each line.
     * @throws JSaParException
     */
    @SuppressWarnings("rawtypes")
	public List buildBeans(Reader reader, List<CellParseError> parseErrors) throws JSaParException {
        JavaBuilder javaBuilder = new JavaBuilder(parseErrors);
        return javaBuilder.build(reader);
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
            parserFactory.makeParser(this.schema, reader).parse(Parser.this);
        } catch (IOException e) {
            throw new ParseException("Failed to read input", e);
        }
    }

    /**
     * Registers an event listener to be able to receive events when a line has been parsed.
     * 
     * @param eventListener
     */
    public void addParsingEventListener(LineEventListener eventListener) {
        if (eventListener == null)
            return;
        this.parsingEventListeners.add(eventListener);
    }

    /**
     * Removes the first occurrence of a registered event listener if it is present (optional
     * operation). If the list does not contain the element, it is unchanged. Uses equals() for
     * comparison.
     * 
     * @return true if a listener was removed, false if there was no action.
     * @param eventListener
     */
    public boolean removeParsingEventListener(LineEventListener eventListener) {
        return this.parsingEventListeners.remove(eventListener);
    }

    /**
     * Forwards the event to all registered listeners.
     * 
     * @throws ParseException
     */
    @Override
    public void lineErrorEvent(LineErrorEvent event) throws ParseException {
        for (LineEventListener l : this.parsingEventListeners) {
            l.lineErrorEvent(event);
        }
    }

    /**
     * Forwards the event to all registered listeners.
     * 
     * @throws JSaParException
     */
    @Override
    public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
        for (LineEventListener l : this.parsingEventListeners) {
            l.lineParsedEvent(event);
        }
    }

    public SchemaParserFactory getParserFactory() {
        return parserFactory;
    }

    public void setParserFactory(SchemaParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    /**
     * Private class that converts the events from the parser into a Document object. This builder
     * adds errors to a list of CellParseErrors.
     * 
     * @author stejon0
     * 
     */
    private class DocumentBuilder {
        private Document             document          = new Document();
        private List<CellParseError> parseErrors;
        private int                  maxNumberOfErrors = Integer.MAX_VALUE;

        public DocumentBuilder(List<CellParseError> parseErrors) {
            this.parseErrors = parseErrors;
        }

        public DocumentBuilder(List<CellParseError> parseErrors, int maxNumberOfErrors) {
            this(parseErrors);
            this.maxNumberOfErrors = maxNumberOfErrors;
        }

        public Document build(java.io.Reader reader) throws JSaParException {
            addParsingEventListener(new LineEventListener() {

                @Override
                public void lineErrorEvent(LineErrorEvent event) throws MaxErrorsExceededException {
                    parseErrors.add(event.getCellParseError());
                    if (parseErrors.size() > maxNumberOfErrors)
                        throw new MaxErrorsExceededException(parseErrors);
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            });

            try {
                getParserFactory().makeParser(schema, reader).parse(Parser.this);
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
            addParsingEventListener(new LineEventListener() {

                @Override
                public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                    throw new ParseException(event.getCellParseError());
                }

                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    document.addLine(event.getLine());
                }
            });

            try {
                getParserFactory().makeParser(schema, reader).parse(Parser.this);
            } catch (IOException e) {
                throw new ParseException("Failed to read input", e);
            }
            return this.document;
        }
    }

    /**
     * Private class that converts the events from the parser into a list of Java object. This
     * builder adds errors to a list of CellParseErrors.
     * 
     * @author stejon0
     * 
     */
    private class JavaBuilder {
        @SuppressWarnings("rawtypes")
		private List              objects   = new LinkedList();
        private List<CellParseError> parseErrors;
        private BeanComposer outputter = new BeanComposer();

        public JavaBuilder(List<CellParseError> parseErrors) {
            this.parseErrors = parseErrors;
        }

        public List<?> build(java.io.Reader reader) throws JSaParException {
            addParsingEventListener(new LineEventListener() {

                @Override
                public void lineErrorEvent(LineErrorEvent event) {
                    parseErrors.add(event.getCellParseError());
                }

                @SuppressWarnings("unchecked")
				@Override
                public void lineParsedEvent(LineParsedEvent event) {
                    List<CellParseError> currentParseErrors = new LinkedList<CellParseError>();
                    try {
                        objects.add(outputter.createBean(event.getLine(), currentParseErrors));
                    } catch (InstantiationException e) {
                        currentParseErrors.add(new CellParseError(event.getLineNumber(), "", "", null,
                                "Failed to instantiate object. Skipped creating object - " + e));
                    } catch (IllegalAccessException e) {
                        currentParseErrors.add(new CellParseError(event.getLineNumber(), "", "", null,
                                "Failed to call set method. Skipped creating object - " + e));
                    } catch (ClassNotFoundException e) {
                        currentParseErrors.add(new CellParseError(event.getLineNumber(), "", "", null,
                                "Class not found. Skipped creating object - " + e));
                    } catch (Throwable e) {
                        currentParseErrors.add(new CellParseError(event.getLineNumber(), "", "", null,
                                "Skipped creating object - " + e.getMessage(), e));
                    }
                    for (CellParseError parseError : currentParseErrors) {
                        parseErrors.add(new CellParseError(event.getLineNumber(), parseError));
                    }
                }
            });

            try {
                getParserFactory().makeParser(schema, reader).parse(Parser.this);
            } catch (IOException e) {
                throw new ParseException("Failed to read input", e);
            }
            return this.objects;
        }
    }

}
