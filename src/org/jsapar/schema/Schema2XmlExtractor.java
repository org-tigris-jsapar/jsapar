package org.jsapar.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsapar.Cell.CellType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Schema2XmlExtractor implements SchemaXmlTypes {

    /**
     * Utility function to retreive first matching child element.
     * 
     * @param parentElement
     * @param sChildName
     * @return
     */
    private Element getChild(Element parentElement, String sChildName) {
        org.w3c.dom.NodeList nodes = parentElement.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, sChildName);
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node child = nodes.item(i);
            if (child instanceof org.w3c.dom.Element) {
                return (org.w3c.dom.Element) child;
            }
        }
        return null;
    }

    /**
     * Utility function to convert a boolean xml node into a boolean.
     * 
     * @param node
     * @return
     * @throws SchemaException
     */
    private boolean getBooleanValue(Node node) throws SchemaException {
        final String value = node.getNodeValue().trim();
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
            return true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
            return false;
        } else {
            throw new SchemaException("Failed to parse boolean node: " + node);
        }
    }

    private String getStringValue(Node node) {
        return node.getNodeValue().trim();
    }

    private String getAttributeValue(Element parent, String name) {
        Node child = parent.getAttributeNode(name);
        if (child == null)
            return null;
        else
            return child.getNodeValue();
    }

    private int getIntValue(Node node) {
        return Integer.parseInt(node.getNodeValue().trim());
    }

    public org.w3c.dom.Document extractXmlDocument(Schema schema) throws SchemaException {
        try {
            String schemaFileName = "/xml/schema/JSaParSchema.xsd";
            InputStream schemaStream = Xml2SchemaBuilder.class.getResourceAsStream(schemaFileName);

            if (schemaStream == null)
                throw new SchemaException("Could not find schema file: " + schemaFileName);
            // File schemaFile = new
            // File("resources/xml/schema/JSaParSchema.xsd");

            // javax.xml.validation.Schema xmlSchema;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);
            factory.setCoalescing(true);
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaStream);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document xmlDocument = builder.newDocument();
            Element xmlRoot = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_ROOT);
            xmlDocument.appendChild(xmlRoot);

            Element xmlSchema = null;
            if (schema instanceof FixedWidthControlCellSchema) {
                xmlSchema = extractFixedWidthControlCellSchema(xmlDocument, (FixedWidthControlCellSchema) schema);
            } else if (schema instanceof FixedWidthSchema) {
                xmlSchema = extractFixedWidthSchema(xmlDocument, (FixedWidthSchema) schema);
            } else if (schema instanceof CsvControlCellSchema) {
                xmlSchema = extractCsvControlCellSchema(xmlDocument, (CsvControlCellSchema) schema);
            } else if (schema instanceof CsvSchema) {
                xmlSchema = extractCsvSchema(xmlDocument, (CsvSchema) schema);
            } else
                throw new SchemaException("Failed to generate xml. Unsupported schema type.");

            xmlRoot.appendChild(xmlSchema);
            return xmlDocument;
        } catch (ParserConfigurationException e) {
            throw new SchemaException("Failed to extract XML from schema. ", e);
        }
    }

    /**
     * Generates a schema from a xml file.
     * 
     * @param reader
     *            - A reader linked to the xml file.
     * @return The file parser schema.
     * @throws SchemaException
     */
    public void extractXml(java.io.Writer writer, Schema schema) throws SchemaException {
        try {

            Document xmlDocument = extractXmlDocument(schema);

            Transformer t  = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(xmlDocument), new StreamResult(writer));
            
            // XERCES 1 or 2 additionnal classes.
//            OutputFormat outputFormat = new OutputFormat("XML", "UTF-8", true);
//            outputFormat.setIndent(2);
//            outputFormat.setIndenting(true);
////            outputFormat.setDoctype(JSAPAR_XML_SCHEMA, "JSaParSchema.xsd");
//            XMLSerializer serializer = new XMLSerializer(writer, outputFormat);
//            // As a DOM Serializer
//            serializer.asDOMSerializer();
//            serializer.serialize(xmlDocument.getDocumentElement());

//        } catch (IOException e) {
//            throw new SchemaException("Failed to generate schema. Failed to write to output.", e);
        } catch (TransformerException e) {
            throw new SchemaException("Failed to generate schema.", e);
        }
    }

    /**
     * @param xmlSchema
     * @return
     * @throws SchemaException
     */
    private Element extractFixedWidthSchema(Document xmlDocument, FixedWidthSchema schema) throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FIXED_WIDTH_SCHEMA);

        assignFixedWidthSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * @param xmlSchema
     * @return
     * @throws SchemaException
     */
    private void assignFixedWidthSchema(Document xmlDocument, Element xmlSchema, FixedWidthSchema schema)
            throws SchemaException {

        assignSchemaBase(xmlDocument, xmlSchema, schema);

        for (FixedWidthSchemaLine schemaLine : schema.getFixedWidthSchemaLines()) {
            Element xmlLine = extractFixedWidthSchemaLine(xmlDocument, schemaLine);
            xmlSchema.appendChild(xmlLine);
        }
    }

    /**
     * @param xmlSchema
     * @return
     * @throws SchemaException
     */
    private Element extractFixedWidthControlCellSchema(Document xmlDocument, FixedWidthControlCellSchema schema)
            throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FIXED_WIDTH_CONTROL_CELL_SCHEMA);

        xmlSchema.setAttribute(ATTRIB_SCHEMA_WRITE_CONTROL_CELL, String.valueOf(schema.isWriteControlCell()));

        Element xmlControlCell = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FW_SCHEMA_CONTROLCELL);
        xmlSchema.appendChild(xmlControlCell);

        xmlControlCell.setAttribute(ATTRIB_FW_SCHEMA_CONTROLCELLL_ENGTH, String.valueOf(schema.getControlCellLength()));
        xmlControlCell.setAttribute(ATTRIB_FW_SCHEMA_CONTROLCELL_ALLIGNMENT, schema.getControlCellAlignment()
                .toString().toLowerCase());

        assignFixedWidthSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * Extracts the lines of a schema into an xml.
     * 
     * @param xmlDocument
     * @param schemaLine
     * @return
     * @throws SchemaException
     */
    private Element extractFixedWidthSchemaLine(Document xmlDocument, FixedWidthSchemaLine schemaLine)
            throws SchemaException {
        Element xmlSchemaLine = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);

        assignSchemaLineBase(xmlSchemaLine, schemaLine);

        xmlSchemaLine.setAttribute(ATTRIB_FW_SCHEMA_FILL_CHARACTER, String.valueOf(schemaLine.getFillCharacter()));
        xmlSchemaLine.setAttribute(ATTRIB_FW_SCHEMA_TRIM_FILL_CHARACTERS, String.valueOf(schemaLine
                .isTrimFillCharacters()));

        for (FixedWidthSchemaCell schemaCell : schemaLine.getSchemaCells()) {
            Element xmlCell = extractFixedWidthSchemaCell(xmlDocument, schemaCell);
            xmlSchemaLine.appendChild(xmlCell);
        }
        return xmlSchemaLine;
    }

    /**
     * Builds the cell part of a file schema from an xml input
     * 
     * @param xmlSchemaCell
     * @return
     * @throws SchemaException
     * @throws DataConversionException
     */
    private Element extractFixedWidthSchemaCell(Document xmlDocument, FixedWidthSchemaCell schemaCell)
            throws SchemaException {
        Element xmlSchemaCell = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);

        xmlSchemaCell.setAttribute(ATTRIB_FW_SCHEMA_CELL_LENGTH, String.valueOf(schemaCell.getLength()));
        xmlSchemaCell
                .setAttribute(ATTRIB_FW_SCHEMA_CELL_ALLIGNMENT, schemaCell.getAlignment().toString().toLowerCase());

        assignSchemaCellBase(xmlDocument, xmlSchemaCell, schemaCell);
        return xmlSchemaCell;
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlSchema
     * @return
     * @throws SchemaException
     * @throws DataConversionException
     * @throws SchemaException
     */
    private Element extractCsvSchema(Document xmlDocument, CsvSchema schema) throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_CSV_SCHEMA);

        assignCsvSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlSchema
     * @return
     * @throws SchemaException
     * @throws DataConversionException
     * @throws SchemaException
     */
    private void assignCsvSchema(Document xmlDocument, Element xmlSchema, CsvSchema schema) throws SchemaException {
        assignSchemaBase(xmlDocument, xmlSchema, schema);

        for (CsvSchemaLine schemaLine : schema.getCsvSchemaLines()) {
            Element xmlLine = extractCsvSchemaLine(xmlDocument, schemaLine);
            xmlSchema.appendChild(xmlLine);
        }
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlSchema
     * @return
     * @throws DataConversionException
     * @throws SchemaException
     */
    private Element extractCsvControlCellSchema(Document xmlDocument, CsvControlCellSchema schema)
            throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_CSV_CONTROL_CELL_SCHEMA);

        xmlSchema.setAttribute(ATTRIB_CSV_SCHEMA_CONTROL_CELL_SEPARATOR, schema.getControlCellSeparator());
        xmlSchema.setAttribute(ATTRIB_SCHEMA_WRITE_CONTROL_CELL, String.valueOf(schema.isWriteControlCell()));

        assignCsvSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * @param xmlSchemaLine
     * @return
     * @throws DataConversionException
     * @throws SchemaException
     */
    private Element extractCsvSchemaLine(Document xmlDocument, CsvSchemaLine schemaLine) throws SchemaException {

        Element xmlSchemaLine = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);

        assignSchemaLineBase(xmlSchemaLine, schemaLine);

        xmlSchemaLine.setAttribute(ATTRIB_CSV_SCHEMA_CELL_SEPARATOR, schemaLine.getCellSeparator());
        xmlSchemaLine.setAttribute(ATTRIB_CSV_SCHEMA_LINE_FIRSTLINEASSCHEMA, String.valueOf(schemaLine.isFirstLineAsSchema()));
        
        if(schemaLine.isQuoteCharUsed())
            xmlSchemaLine.setAttribute(ATTRIB_CSV_QUOTE_CHAR, String.valueOf(schemaLine.getQuoteChar()));

        for (CsvSchemaCell schemaCell : schemaLine.getSchemaCells()) {
            Element xmlCell = extractCsvSchemaCell(xmlDocument, schemaCell);
            xmlSchemaLine.appendChild(xmlCell);
        }

        return xmlSchemaLine;
    }

    /**
     * @param xmlSchemaCell
     * @return
     * @throws SchemaException
     */
    private Element extractCsvSchemaCell(Document xmlDocument, CsvSchemaCell schemaCell) throws SchemaException {

        Element xmlSchemaCell = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);
        assignSchemaCellBase(xmlDocument, xmlSchemaCell, schemaCell);
        return xmlSchemaCell;

    }

    /**
     * Assign common parts for base class.
     * 
     * @param schema
     * @param xmlSchemaCell
     * @throws SchemaException
     */
    private void assignSchemaBase(Document xmlDocument, Element xmlSchema, Schema schema) throws SchemaException {
        String lineSeparator = schema.getLineSeparator();
        xmlSchema.setAttribute(ATTRIB_SCHEMA_LINESEPARATOR, lineSeparator);

        Element xmlLocale = extractLocale(xmlDocument, schema.getLocale());
        xmlSchema.appendChild(xmlLocale);
    }

    /**
     * Assign common pars for base class.
     * 
     * @param line
     * @param xmlSchemaLine
     * @throws SchemaException
     * @throws DataConversionException
     */
    private void assignSchemaLineBase(Element xmlSchemaLine, SchemaLine line) throws SchemaException {
        String sOccurs = line.isOccursInfinitely() ? "*" : String.valueOf(line.getOccurs());
        xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_OCCURS, sOccurs);

        if (line.getLineType() != null)
            xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_LINETYPE, line.getLineType());

        if (line.getLineTypeControlValue() != null)
            xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_LINETYPE_CONTROL_VALUE, line.getLineTypeControlValue());

        xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_IGNORE_READ_EMPTY_LINES, String.valueOf(line
                .isIgnoreReadEmptyLines()));
    }

    /**
     * @param xmlDocument
     * @param xmlSchemaCell
     * @param cell
     * @throws SchemaException
     */
    private void assignSchemaCellBase(Document xmlDocument, Element xmlSchemaCell, SchemaCell cell)
            throws SchemaException {
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_NAME, cell.getName());
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_IGNOREREAD, String.valueOf(cell.isIgnoreRead()));
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_MANDATORY, String.valueOf(cell.isMandatory()));

        Element xmlFormat = extractCellFormat(xmlDocument, cell.getCellFormat());
        xmlSchemaCell.appendChild(xmlFormat);

        Element xmlRange = extractCellRange(xmlDocument, cell);
        if (xmlRange.hasChildNodes())
            xmlSchemaCell.appendChild(xmlRange);
    }

    /**
     * @param xmlDocument
     * @param cell
     * @return
     * @throws SchemaException
     */
    private Element extractCellRange(Document xmlDocument, SchemaCell cell) throws SchemaException {
        Element xmlRange = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_RANGE);
        if (cell.getMinValue() != null)
            xmlRange.setAttribute(ATTRIB_SCHEMA_CELL_MIN, cell.getMinValue().getStringValue());
        if (cell.getMaxValue() != null)
            xmlRange.setAttribute(ATTRIB_SCHEMA_CELL_MAX, cell.getMaxValue().getStringValue());
        return xmlRange;
    }

    private Element extractLocale(Document xmlDocument, Locale locale) throws SchemaException {
        Element xmlLocale = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_LOCALE);
        xmlLocale.setAttribute(ATTRIB_LOCALE_COUNTRY, locale.getCountry());
        xmlLocale.setAttribute(ATTRIB_LOCALE_LANGUAGE, locale.getLanguage());
        return xmlLocale;
    }

    /**
     * @param xmlDocument
     * @param format
     * @return
     * @throws SchemaException
     */
    private Element extractCellFormat(Document xmlDocument, SchemaCellFormat format) throws SchemaException {
        Element xmlFormat = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FORMAT);
        xmlFormat.setAttribute("type", format.getCellType().toString().toLowerCase());
        if (format.getPattern() != null && format.getPattern().length() > 0)
            xmlFormat.setAttribute("pattern", format.getPattern());
        return xmlFormat;
    }

    /**
     * Transforms from xml schema type to CellType enum.
     * 
     * @param sType
     *            The xml representation of the type.
     * @return The CellType value
     * @throws SchemaException
     */
    private CellType getCellType(String sType) throws SchemaException {
        if (sType.equals("string"))
            return CellType.STRING;

        if (sType.equals("integer"))
            return CellType.INTEGER;

        if (sType.equals("date"))
            return CellType.DATE;

        if (sType.equals("float"))
            return CellType.FLOAT;

        if (sType.equals("decimal"))
            return CellType.DECIMAL;

        if (sType.equals("boolean"))
            return CellType.BOOLEAN;

        throw new SchemaException("Unknown cell format type: " + sType);

    }


    /**
     * Gets a mandatory xml attribute from a xml element. If the element does not exist, a
     * SchemaException is thrown.
     * 
     * @param xmlElement
     * @param sAttributeName
     * @return The xml attribute.
     * @throws SchemaException
     */
    private static Attr getMandatoryAttribute(Element xmlElement, String sAttributeName) throws SchemaException {
        Node xmlAttribute = xmlElement.getAttributeNode(sAttributeName);
        if (xmlAttribute == null || !(xmlAttribute instanceof Attr))
            throw new SchemaException("Missing mandatory attribute: " + sAttributeName + " of element " + xmlElement);
        return (Attr) xmlAttribute;
    }
}
