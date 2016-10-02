package org.jsapar;

import org.jsapar.parse.*;
import org.jsapar.schema.Schema;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stejon0 on 2016-02-13.
 */
public class Text2BeanConverter {
    private TextParser parser;

    public Text2BeanConverter(Schema inputSchema) {
        this.parser = new TextParser(inputSchema);
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
    public List buildBeans(Reader reader, List<CellParseError> parseErrors) throws JSaParException {
        BeanBuilder beanBuilder = new BeanBuilder(parseErrors);
        return beanBuilder.build(reader);
    }

    /**
     * Reads characters from the reader and parses them into a list of java objects denoted by the
     * schema. See org.jsapar.BeanParser for more information about how to build java objects
     * from an input source.
     *
     * This method throws an exception when the first error occurs.
     *
     * @param reader
     *            Supply an empty list of CellParseError and this method will populate the list if
     *            errors are found while parsing.
     * @return A collection of java objects. The class of each java object is determined by the
     *         lineType of each line.
     * @throws JSaParException
     */
    public List buildBeans(Reader reader) throws JSaParException {
        BeanBuilder beanBuilder = new BeanBuilder();
        return beanBuilder.build(reader);
    }


    /**
     * Private class that converts the events from the parser into a list of Java object. This
     * builder adds errors to a list of CellParseErrors.
     *
     * @author stejon0
     *
     */
    private class BeanBuilder {
        @SuppressWarnings("rawtypes")
        private List objects = new LinkedList();
        private List<CellParseError> parseErrors;
        private BeanComposer composer = new BeanComposer();

        public BeanBuilder(List<CellParseError> parseErrors) {
            this.parseErrors = parseErrors;
        }

        public BeanBuilder() {
            this.parseErrors = null;
        }

        public List<?> build(Reader reader) throws JSaParException {
            parser.addParsingEventListener(new LineEventListener() {

                @Override
                public void lineErrorEvent(LineErrorEvent event) throws ParseException {
                    if(parseErrors != null)
                        parseErrors.add(event.getParseError());
                    else
                        throw new ParseException(event.getParseError());
                }

                @SuppressWarnings("unchecked")
                @Override
                public void lineParsedEvent(LineParsedEvent event) {
                    List<CellParseError> currentParseErrors = new LinkedList<CellParseError>();
                    try {
                        objects.add(composer.createBean(event.getLine(), currentParseErrors));
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

            parser.parse(reader);
            return this.objects;
        }
    }

}
