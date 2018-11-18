package org.jsapar;

import org.jsapar.schema.Schema;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.junit.Test;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 */
public class Text2XmlConverterTest {


    @Test
    public void testConvert() throws IOException {
        try (Reader fileReader = new FileReader("examples/01_Names.csv");
                Reader schemaReader = new FileReader("examples/01_CsvSchema.xml")) {
            Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
            Schema schema = xmlBuilder.build(schemaReader);
            Text2XmlConverter converter = new Text2XmlConverter(schema);

            StringWriter w = new StringWriter();
            converter.convert(fileReader, w);
            System.out.print(w.toString());
        }

    }
    
    @Test
    public void testTransform_to_html() throws IOException, TransformerConfigurationException {
        String xslt= "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<html xsl:version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<body style='font-family:Arial;font-size:12pt;background-color:#EEEEEE'>\n" +
                "<table border='1pt'>\n" +
                "<xsl:for-each select='document/line'>\n" +
                "  <tr>\n" +
                "  <xsl:for-each select='cell'>\n" +
                "    <td><xsl:value-of select='current()'/></td>\n" +
                "  </xsl:for-each>\n" +
                "  </tr>\n" +
                "</xsl:for-each>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";
        try (Reader fileReader = new FileReader("examples/01_Names.csv");
                Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
                Reader xsltReader = new StringReader(xslt)) {
            Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
            Schema schema = xmlBuilder.build(schemaReader);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltReader));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");

            Text2XmlConverter converter = new Text2XmlConverter(schema, transformer);

            StringWriter w = new StringWriter();
            converter.convert(fileReader, w);
            String html = w.toString();
            assertTrue(html.startsWith("<html>"));
            assertTrue(html.contains("<td>Erik</td><td>Svensson</td><td>true</td>"));
            System.out.print(html);

        }

    }

    @Test
    public void testConvert_Xslt_xml() throws IOException {
        String xslt= "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<page xsl:version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "<table>\n" +
                "<xsl:for-each select='document/line'>\n" +
                "  <tr><xsl:for-each select='cell'><td><xsl:value-of select='current()'/></td></xsl:for-each></tr>\n" +
                "</xsl:for-each>\n" +
                "</table>\n" +
                "</page>";
        try (Reader fileReader = new FileReader("examples/01_Names.csv");
             Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
             Reader xsltReader = new StringReader(xslt)) {
            Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
            Schema schema = xmlBuilder.build(schemaReader);
            Text2XmlConverter converter = new Text2XmlConverter(schema);

            StringWriter w = new StringWriter();
            converter.applyXslt(xsltReader, "xml");
            converter.convert(fileReader, w);

            String xml = w.toString();
            System.out.print(xml);
            assertTrue(xml.contains("<td>Erik</td>"));
            assertTrue(xml.startsWith("<?xml version="));
        }

    }


    @Test
    public void testTransform_to_text() throws IOException, TransformerConfigurationException {
        String xslt= "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
                "   <xsl:output method='text' indent='yes'/>\n" +
                "<xsl:template match='/document/line'>\n" +
                "LINE: <xsl:for-each select='cell'><xsl:value-of select='current()'/>--|--</xsl:for-each>\n" +
                "</xsl:template>\n" +
                "</xsl:stylesheet>";
        try (Reader fileReader = new FileReader("examples/01_Names.csv");
             Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
             Reader xsltReader = new StringReader(xslt)) {
            Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
            Schema schema = xmlBuilder.build(schemaReader);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltReader));
            transformer.setOutputProperty(OutputKeys.METHOD, "text");

            Text2XmlConverter converter = new Text2XmlConverter(schema);
            converter.setTransformer(transformer);

            StringWriter w = new StringWriter();
            converter.convert(fileReader, w);
            String text = w.toString();
            System.out.print(text);
            assertTrue(text.contains("LINE: Erik--|--Svensson--|--true--|--"));

        }

    }

}