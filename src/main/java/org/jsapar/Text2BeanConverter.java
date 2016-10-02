package org.jsapar;

import org.jsapar.error.RecordingErrorEventListener;
import org.jsapar.parse.*;
import org.jsapar.schema.Schema;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stejon0 on 2016-02-13.
 */
public class Text2BeanConverter {

    private Schema inputSchema;

    public Text2BeanConverter(Schema inputSchema) {
        this.inputSchema = inputSchema;
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
    public List buildBeans(Reader reader, List<ParseError> parseErrors) throws JSaParException {
        BeanComposer beanBuilder = new BeanComposer();
        TextParser2 textParser = new TextParser2(inputSchema, reader);
        textParser.addErrorEventListener(new RecordingErrorEventListener(parseErrors));
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



}
