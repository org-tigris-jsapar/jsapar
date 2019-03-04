package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.xml.Text2SAXReader;
import org.jsapar.schema.Schema;
import org.xml.sax.InputSource;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.Reader;
import java.io.Writer;

/**
 * Can be used to convert a CSV or fixed with file to xml output or any other text output by applying transformation.
 */
public class Text2XmlConverter {

    private final Text2SAXReader saxReader;
    private Transformer transformer;

    /**
     * Creates a {@link Text2XmlConverter} that converts a text source into xml. The output xml will be according to
     * jsapar standard defined in <a href="http://jsapar.tigris.org/XMLDocumentFormat/2.0">http://jsapar.tigris.org/XMLDocumentFormat/2.0</a> unless a {@link Transformer} is applied by calling {@link #setTransformer(Transformer)}. or
     * {@link #applyXslt(Reader, String)}
     * @param parseSchema The parsing schema to use.
     */
    public Text2XmlConverter(Schema parseSchema) {
        TextParser textParser = new TextParser(parseSchema);
        this.saxReader = new Text2SAXReader(textParser);
        try {
            this.transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            throw new JSaParException("Failed to create xml transformer", e);
        }
    }

    /**
     * Creates a {@link Text2XmlConverter} that converts a text source into xml. The output xml will be according to
     * jsapar standard and with the supplied {@link Transformer} applied to it.
     * @param parseSchema The parsing schema to use.
     * @param transformer The {@link Transformer} to apply to the xml before generating the output.
     */
    public Text2XmlConverter(Schema parseSchema, Transformer transformer) {
        TextParser textParser = new TextParser(parseSchema);
        this.saxReader = new Text2SAXReader(textParser);
        this.transformer = transformer;
    }

    /**
     * Replaces the current {@link Transformer} with the supplied transformer.
     * @param transformer The transformer to use from now on.
     */
    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    /**
     * Convenience method that creates and sets a transformer that uses xslt to transform the output.
     * @param xsltReader The reader to read xslt from
     * @param method The transformation method. See {@link OutputKeys#METHOD}
     */
    public void applyXslt(Reader xsltReader, String method){
        try {
            transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltReader));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, method);
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            throw new JSaParException("Failed to create xml transformer", e);
        }
    }

    /**
     * Converts a text input to an xml output according to XMLDocumentFormat.xsd
     *
     * @param reader The reader to read text input from.
     * @param writer The writer to write xml to.
     */
    public void convert(Reader reader, Writer writer) {
        try {
            transform(reader, writer, transformer);
        } catch (TransformerException e) {
            throw new JSaParException("Failed to transform text input to xml", e);
        }
    }

    /**
     * Transforms the text input to any text output using the supplied transformer.
     *
     * @param reader      The writer to write the text output to.
     * @param writer      The reader to read text input from.
     * @param transformer The transformer to use during the transformation.
     * @throws TransformerException In case of transformation error.
     */
    private void transform(Reader reader, Writer writer, Transformer transformer) throws TransformerException {
        transformer.transform(new SAXSource(saxReader, new InputSource(reader)), new StreamResult(writer));
    }

}
