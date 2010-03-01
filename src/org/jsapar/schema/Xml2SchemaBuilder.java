package org.jsapar.schema;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsapar.Cell.CellType;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Xml2SchemaBuilder implements SchemaXmlTypes {

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

    /**
     * Generates a schema from a xml file.
     * 
     * @param reader
     *            - A reader linked to the xml file.
     * @return The file parser schema.
     * @throws SchemaException
     */
    public Schema build(java.io.Reader reader) throws SchemaException {
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

            // factory.setSchema(xmlSchema);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException e) throws SAXException {
                    throw e;
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }

                @Override
                public void warning(SAXParseException e) throws SAXException {
                    // System.out.println("Warning while validating schema" +
                    // e);
                }
            });

            org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
            org.w3c.dom.Document xmlDocument = builder.parse(is);

            Element xmlRoot = xmlDocument.getDocumentElement();
            // Element xmlRoot = (Element) xmlDocument.getFirstChild();

            Element xmlSchema = getChild(xmlRoot, ELEMENT_CSV_SCHEMA);
            if (null != xmlSchema)
                return buildCsvSchema(xmlSchema);

            xmlSchema = getChild(xmlRoot, ELEMENT_FIXED_WIDTH_SCHEMA);
            if (null != xmlSchema)
                return buildFixedWidthSchema(xmlSchema);

            xmlSchema = getChild(xmlRoot, ELEMENT_CSV_CONTROL_CELL_SCHEMA);
            if (null != xmlSchema)
                return buildCsvControlCellSchema(xmlSchema);

            xmlSchema = getChild(xmlRoot, ELEMENT_FIXED_WIDTH_CONTROL_CELL_SCHEMA);
            if (null != xmlSchema)
                return buildFixedWidthControlCellSchema(xmlSchema);

            throw new SchemaException("Failed to find specific schema XML element. Expected one of "
                    + ELEMENT_CSV_SCHEMA + " or " + ELEMENT_FIXED_WIDTH_SCHEMA);
        } catch (IOException e) {
            throw new SchemaException("Failed to generate schema. Failed to read input.", e);
        } catch (ParserConfigurationException e) {
            throw new SchemaException("Failed to generate schema from XML file. XML parsing error.", e);
        } catch (SAXParseException e) {
            throw new SchemaException("Failed to read XML file. Error at line " + e.getLineNumber(), e);
        } catch (SAXException e) {
            throw new SchemaException("Failed to generate schema from XML file. XML parsing error.", e);
        }
    }

    /**
     * @param xmlSchema
     * @return
     * @throws SchemaException
     */
    private Schema buildFixedWidthSchema(Element xmlSchema) throws SchemaException {
        FixedWidthSchema schema = new FixedWidthSchema();

        assignFixedWidthSchema(schema, xmlSchema);
        return schema;
    }

    /**
     * @param xmlSchema
     * @return
     * @throws SchemaException
     */
    private void assignFixedWidthSchema(FixedWidthSchema schema, Element xmlSchema) throws SchemaException {

        assignSchemaBase(schema, xmlSchema);

        NodeList nodes = xmlSchema.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node child = nodes.item(i);
            if (child instanceof Element)
                schema.addSchemaLine(buildFixedWidthSchemaLine((Element) child, schema.getLocale()));
        }
    }

    /**
     * @param xmlSchema
     * @return
     * @throws SchemaException
     */
    private Schema buildFixedWidthControlCellSchema(Element xmlSchema) throws SchemaException {
        FixedWidthControlCellSchema schema = new FixedWidthControlCellSchema();

        assignFixedWidthSchema(schema, xmlSchema);

        Node xmlWriteControlCell = xmlSchema.getAttributeNode(ATTRIB_SCHEMA_WRITE_CONTROL_CELL);
        if (xmlWriteControlCell != null)
            schema.setWriteControlCell(getBooleanValue(xmlWriteControlCell));

        Element xmlControlCell = getChild(xmlSchema, ELEMENT_FW_SCHEMA_CONTROLCELL);
        Node xmlControlCellLength = xmlControlCell.getAttributeNode(ATTRIB_FW_SCHEMA_CONTROLCELLL_LENGTH);
        if (xmlControlCellLength != null)
            schema.setControlCellLength(getIntValue(xmlControlCellLength));

        Node xmlControlCellAllignment = xmlControlCell.getAttributeNode(ATTRIB_FW_SCHEMA_CELL_ALIGNMENT);
        if (xmlControlCellAllignment != null) {
            String sControlCellAllignment = getStringValue(xmlControlCellAllignment);
            if (sControlCellAllignment.equals("left"))
                schema.setControlCellAlignment(FixedWidthSchemaCell.Alignment.LEFT);
            else if (sControlCellAllignment.equals("center"))
                schema.setControlCellAlignment(FixedWidthSchemaCell.Alignment.CENTER);
            else if (sControlCellAllignment.equals("right"))
                schema.setControlCellAlignment(FixedWidthSchemaCell.Alignment.RIGHT);
            else {
                throw new SchemaException("Invalid value for attribute: " + ATTRIB_FW_SCHEMA_CELL_ALIGNMENT
                        + "=" + sControlCellAllignment);
            }
        }
        return schema;
    }

    /**
     * Builds the lines of a file schema from an xml input.
     * 
     * @param xmlSchemaLine
     * @return
     * @throws SchemaException
     * @throws DataConversionException
     */
    private FixedWidthSchemaLine buildFixedWidthSchemaLine(Element xmlSchemaLine, Locale locale) throws SchemaException {
        FixedWidthSchemaLine schemaLine = new FixedWidthSchemaLine();

        assignSchemaLineBase(schemaLine, xmlSchemaLine);

        String sFillChar = getAttributeValue(xmlSchemaLine, ATTRIB_FW_SCHEMA_FILL_CHARACTER);
        if (sFillChar != null)
            schemaLine.setFillCharacter(sFillChar.charAt(0));

        Node xmlTrimFill = xmlSchemaLine.getAttributeNode(ATTRIB_FW_SCHEMA_TRIM_FILL_CHARACTERS);
        if (xmlTrimFill != null)
            schemaLine.setTrimFillCharacters(getBooleanValue(xmlTrimFill));

        NodeList nodes = xmlSchemaLine.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node child = nodes.item(i);
            if (child instanceof Element)
                schemaLine.addSchemaCell(buildFixedWidthSchemaCell((Element) child, locale));
        }
        return schemaLine;
    }

    /**
     * Builds the cell part of a file schema from an xml input
     * 
     * @param xmlSchemaCell
     * @return
     * @throws SchemaException
     * @throws DataConversionException
     */
    private FixedWidthSchemaCell buildFixedWidthSchemaCell(Element xmlSchemaCell, Locale locale) throws SchemaException {

        int nLength = getIntValue(getMandatoryAttribute(xmlSchemaCell, ATTRIB_FW_SCHEMA_CELL_LENGTH));

        FixedWidthSchemaCell cell = new FixedWidthSchemaCell(nLength);

        Node xmlAllignment = xmlSchemaCell.getAttributeNode(ATTRIB_FW_SCHEMA_CELL_ALIGNMENT);
        if (xmlAllignment == null || getStringValue(xmlAllignment).equals("left"))
            cell.setAlignment(FixedWidthSchemaCell.Alignment.LEFT);
        else if (getStringValue(xmlAllignment).equals("center"))
            cell.setAlignment(FixedWidthSchemaCell.Alignment.CENTER);
        else if (getStringValue(xmlAllignment).equals("right"))
            cell.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);
        else {
            throw new SchemaException("Invalid value for attribute: " + ATTRIB_FW_SCHEMA_CELL_ALIGNMENT + "="
                    + getStringValue(xmlAllignment));
        }

        assignSchemaCellBase(cell, xmlSchemaCell, locale);
        return cell;
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlSchema
     * @return
     * @throws DataConversionException
     * @throws SchemaException
     */
    private Schema buildCsvSchema(Element xmlSchema) throws SchemaException {
        CsvSchema schema = new CsvSchema();
        assignCsvSchema(schema, xmlSchema);
        return schema;
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlSchema
     * @return
     * @throws DataConversionException
     * @throws SchemaException
     */
    private void assignCsvSchema(CsvSchema schema, Element xmlSchema) throws SchemaException {

        assignSchemaBase(schema, xmlSchema);

        NodeList nodes = xmlSchema.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node child = nodes.item(i);
            if (child instanceof Element)
                schema.addSchemaLine(buildCsvSchemaLine((Element) child, schema.getLocale()));
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
    private Schema buildCsvControlCellSchema(Element xmlSchema) throws SchemaException {
        CsvControlCellSchema schema = new CsvControlCellSchema();

        assignCsvSchema(schema, xmlSchema);

        String sSeparator = getAttributeValue(xmlSchema, ATTRIB_CSV_SCHEMA_CONTROL_CELL_SEPARATOR);
        if (sSeparator != null){
            sSeparator = replaceEscapes2Java(sSeparator);
            schema.setControlCellSeparator(sSeparator);
        }
        
        Node xmlWriteControlCell = xmlSchema.getAttributeNode(ATTRIB_SCHEMA_WRITE_CONTROL_CELL);
        if (xmlWriteControlCell != null)
            schema.setWriteControlCell(getBooleanValue(xmlWriteControlCell));

        return schema;
    }

    /**
     * @param xmlSchemaLine
     * @return
     * @throws DataConversionException
     * @throws SchemaException
     */
    private CsvSchemaLine buildCsvSchemaLine(Element xmlSchemaLine, Locale locale) throws SchemaException {

        CsvSchemaLine schemaLine = new CsvSchemaLine();
        assignSchemaLineBase(schemaLine, xmlSchemaLine);

        String sSeparator = getAttributeValue(xmlSchemaLine, ATTRIB_CSV_SCHEMA_CELL_SEPARATOR);
        if (sSeparator != null){
            sSeparator = replaceEscapes2Java(sSeparator);
            schemaLine.setCellSeparator(sSeparator);
        }
        Node xmlFirstLineAsSchema = xmlSchemaLine.getAttributeNode(ATTRIB_CSV_SCHEMA_LINE_FIRSTLINEASSCHEMA);
        if (xmlFirstLineAsSchema != null)
            schemaLine.setFirstLineAsSchema(getBooleanValue(xmlFirstLineAsSchema));

        String sQuoteChar = getAttributeValue(xmlSchemaLine, ATTRIB_CSV_QUOTE_CHAR);
        if (sQuoteChar != null)
            schemaLine.setQuoteChar(sQuoteChar.charAt(0));

        NodeList nodes = xmlSchemaLine.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node child = nodes.item(i);
            if (child instanceof Element)
                schemaLine.addSchemaCell(buildCsvSchemaCell((Element) child, locale));
        }

        return schemaLine;
    }

    /**
     * @param xmlSchemaCell
     * @return
     * @throws SchemaException
     */
    private CsvSchemaCell buildCsvSchemaCell(Element xmlSchemaCell, Locale locale) throws SchemaException {

        CsvSchemaCell cell = new CsvSchemaCell();
        assignSchemaCellBase(cell, xmlSchemaCell, locale);
        return cell;

    }

    /**
     * Assign common parts for base class.
     * 
     * @param schema
     * @param xmlSchemaCell
     * @throws SchemaException
     */
    private void assignSchemaBase(Schema schema, Element xmlSchema) throws SchemaException {
        String sSeparator = getAttributeValue(xmlSchema, ATTRIB_SCHEMA_LINESEPARATOR);
        if (sSeparator != null) {
            sSeparator = replaceEscapes2Java(sSeparator);
            schema.setLineSeparator(sSeparator);
        }

        Element xmlLocale = getChild(xmlSchema, ELEMENT_LOCALE);
        if (xmlLocale != null)
            schema.setLocale(buildLocale(xmlLocale));

    }

    /**
     * @param sToReplace
     * @return
     */
    private String replaceEscapes2Java(String sToReplace) {
        sToReplace = sToReplace.replaceAll("\\\\r", "\r");
        sToReplace = sToReplace.replaceAll("\\\\n", "\n");
        sToReplace = sToReplace.replaceAll("\\\\t", "\t");
        sToReplace = sToReplace.replaceAll("\\\\f", "\f");
        return sToReplace;
    }

    /**
     * Assign common pars for base class.
     * 
     * @param line
     * @param xmlSchemaLine
     * @throws SchemaException
     * @throws DataConversionException
     */
    private void assignSchemaLineBase(SchemaLine line, Element xmlSchemaLine) throws SchemaException {
        Node xmlOccurs = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_LINE_OCCURS);
        if (xmlOccurs != null) {
            if (getStringValue(xmlOccurs).equals("*"))
                line.setOccursInfinitely();
            else
                line.setOccurs(getIntValue(xmlOccurs));
        }

        Node xmlLineType = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_LINE_LINETYPE);
        if (xmlLineType != null)
            line.setLineType(getStringValue(xmlLineType));

        Node xmlLineTypeControlValue = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_LINE_LINETYPE_CONTROL_VALUE);
        if (xmlLineTypeControlValue != null)
            line.setLineTypeControlValue(getStringValue(xmlLineTypeControlValue));

        Node xmlIgnoreReadEmptyLines = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_LINE_IGNORE_READ_EMPTY_LINES);
        if (xmlIgnoreReadEmptyLines != null)
            line.setIgnoreReadEmptyLines(getBooleanValue(xmlIgnoreReadEmptyLines));
    }

    /**
     * Assign common parts for base class.
     * 
     * @param schema
     * @param xmlSchemaCell
     * @throws SchemaException
     */
    private void assignSchemaCellBase(SchemaCell cell, Element xmlSchemaCell, Locale locale) throws SchemaException {
        try {
            String sName = getAttributeValue(xmlSchemaCell, ATTRIB_SCHEMA_CELL_NAME);
            if (sName != null)
                cell.setName(sName);

            Node xmlIgnoreRead = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_CELL_IGNOREREAD);
            if (xmlIgnoreRead != null)
                cell.setIgnoreRead(getBooleanValue(xmlIgnoreRead));

            Node xmlMandatory = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_CELL_MANDATORY);
            if (xmlMandatory != null)
                cell.setMandatory(getBooleanValue(xmlMandatory));

            Node xmlDefault = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_CELL_DEFAULT_VALUE);
            if (xmlDefault != null)
                cell.setDefaultCell(cell.makeCell(getStringValue(xmlDefault)));

            Element xmlFormat = getChild(xmlSchemaCell, ELEMENT_FORMAT);
            if (xmlFormat != null)
                assignCellFormat(cell, xmlFormat, locale);

            Element xmlLocale = getChild(xmlSchemaCell, ELEMENT_LOCALE);
            if (xmlLocale != null)
                locale = buildLocale(xmlLocale);
            cell.setLocale(locale);

            Element xmlRange = getChild(xmlSchemaCell, ELEMENT_RANGE);
            if (xmlRange != null) {
                Node minValue = xmlRange.getAttributeNode(ATTRIB_SCHEMA_CELL_MIN);
                if (minValue != null)
                    cell.setMinValue(getStringValue(minValue));
                Node maxValue = xmlRange.getAttributeNode(ATTRIB_SCHEMA_CELL_MAX);
                if (maxValue != null)
                    cell.setMaxValue(getStringValue(maxValue));
            }

        } catch (ParseException e) {
            throw new SchemaException("Failed to parse value within xml schema. ", e);
        } catch (org.jsapar.input.ParseException e) {
            throw new SchemaException("Failed to parse value within xml schema. ", e);
        }

    }

    /**
     * Adds formatting to the cell.
     * 
     * @param cell
     * @param xmlFormat
     * @param locale
     * @throws SchemaException
     */
    private void assignCellFormat(SchemaCell cell, Element xmlFormat, Locale locale) throws SchemaException {
        String sType = getAttributeValue(xmlFormat, "type");
        String sPattern = getAttributeValue(xmlFormat, "pattern");

        cell.setCellFormat(new SchemaCellFormat(getCellType(sType), sPattern, locale));
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
     * Builds a java.util.Locale based on a Locale element.
     * 
     * @param xmlLocale
     * @return The java Locale object.
     * @throws SchemaException
     */
    private Locale buildLocale(Element xmlLocale) throws SchemaException {
        String sLanguage = getMandatoryAttribute(xmlLocale, ATTRIB_LOCALE_LANGUAGE).getValue();
        String sCountry = getAttributeValue(xmlLocale, ATTRIB_LOCALE_COUNTRY);
        if (sCountry != null)
            return new Locale(sLanguage, sCountry);
        else
            return new Locale(sLanguage);
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
    
    /**
     * Utility function for loading a schema from an xml file.
     * @param fileName
     * @return A newly created schema from the xml file.
     * @throws SchemaException
     * @throws IOException
     */
    public static Schema loadSchemaFromXmlFile(File file) throws SchemaException, IOException {
        Reader schemaReader = new FileReader(file);
        try {
            Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
            Schema schema = builder.build(schemaReader);
            return schema;
        } finally {
            schemaReader.close();
        }
    }

    /**
     * @param resourceBaseClass
     *            A class that specifies the base for the relative location of the resource. If this
     *            parameter is null, the resource name has to specify the resource with an absolute
     *            path.
     * @param resouceName The name of the resouce to load.
     * @return A newly created schema from the xml resource.
     * @throws SchemaException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static Schema loadSchemaFromXmlResource(Class resourceBaseClass, String resouceName) throws SchemaException,
            IOException {
        if (resourceBaseClass == null)
            resourceBaseClass = Xml2SchemaBuilder.class;
        InputStream is = resourceBaseClass.getResourceAsStream("ixformatter_inputschema.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        try {
            Schema schema = schemaBuilder.build(new InputStreamReader(is));
            return schema;
        } finally {
            is.close();
        }
    }

}
