package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.xml.Text2SAXReader;
import org.jsapar.schema.Schema;
import org.xml.sax.InputSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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

    public Text2XmlConverter(Schema parseSchema) {
        TextParser textParser = new TextParser(parseSchema);
        this.saxReader = new Text2SAXReader(textParser);
    }

    /**
     * Converts a text input to an xml output according to XMLDocumentFormat.xsd
     *
     * @param reader The reader to read text input from.
     * @param writer The writer to write xml to.
     */
    public void convert(Reader reader, Writer writer) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformXml(reader, writer, transformer);
        } catch (TransformerException e) {
            throw new JSaParException("Failed to transform text input to xml", e);
        }
    }

    /**
     * Converts a text input to an xml output and applies supplied xslt to the output xml.
     *  @param reader     The reader to read text input from.
     * @param writer     The writer to write xml to.
     * @param xsltReader Reader to the xslt.
     */
    public void convert(Reader reader, Writer writer, Reader xsltReader) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltReader));
            transformXml(reader, writer, transformer);
        } catch (TransformerException e) {
            throw new JSaParException("Failed to transform text input to xml", e);
        }
    }

    private void transformXml(Reader reader, Writer writer, Transformer transformer) throws TransformerException {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transform(reader, writer, transformer);
    }

    /**
     * Transforms the text input to any text output using the supplied transformer.
     *
     * @param reader      The writer to write the text output to.
     * @param writer      The reader to read text input from.
     * @param transformer The transformer to use during the transformation.
     * @throws TransformerException In case of transformation error.
     */
    @SuppressWarnings("WeakerAccess")
    public void transform(Reader reader, Writer writer, Transformer transformer) throws TransformerException {
        transformer.transform(new SAXSource(saxReader, new InputSource(reader)), new StreamResult(writer));
    }

}
