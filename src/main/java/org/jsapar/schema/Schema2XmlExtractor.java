package org.jsapar.schema;

import java.io.Writer;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsapar.compose.CellComposer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Schema2XmlExtractor implements SchemaXmlTypes {

    private static final CellComposer cellComposer = new CellComposer();

    public org.w3c.dom.Document extractXmlDocument(Schema schema) throws SchemaException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);
            factory.setCoalescing(true);
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document xmlDocument = builder.newDocument();
            xmlDocument.setXmlStandalone(true);
            Element xmlRoot = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_ROOT);
            xmlDocument.appendChild(xmlRoot);

            Element xmlSchema;
            if (schema instanceof FixedWidthSchema) {
                xmlSchema = extractFixedWidthSchema(xmlDocument, (FixedWidthSchema) schema);
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
     * @param writer
     *            The writer to write to.
     * @param schema
     *            The schema to extract.
     * @throws SchemaException
     */
    public void extractXml(Writer writer, Schema schema) throws SchemaException {
        try {

            Document xmlDocument = extractXmlDocument(schema);

            Transformer t  = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.transform(new DOMSource(xmlDocument), new StreamResult(writer));
            
            // I can't get this to work with schema.
//            OutputFormat outputFormat = new OutputFormat("XML", "UTF-8", true);
//            outputFormat.setIndent(2);
//            outputFormat.setIndenting(true);
//            outputFormat.setStandalone(false);
////            outputFormat.setDoctype(JSAPAR_XML_SCHEMA, "JSaParSchema.xsd");
//            XMLSerializer serializer = new XMLSerializer(writer, outputFormat);
//            // As a DOM Serializer
//            serializer.asDOMSerializer();
//            serializer.serialize(xmlDocument.getDocumentElement());
//
//        } catch (IOException e) {
//            throw new SchemaException("Failed to generate schema. Failed to write to output.", e);
        } catch (TransformerException e) {
            throw new SchemaException("Failed to generate schema.", e);
        }
    }

    /**
     * @param xmlDocument
     * @param schema
     * @return The schema element
     * @throws SchemaException
     */
    private Element extractFixedWidthSchema(Document xmlDocument, FixedWidthSchema schema) throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FIXED_WIDTH_SCHEMA);

        assignFixedWidthSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * @param xmlDocument
     * @param xmlSchema
     * @param schema
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
     * Extracts the lines of a schema into an xml.
     * 
     * @param xmlDocument
     * @param schemaLine
     * @return The line schema element
     * @throws SchemaException
     */
    private Element extractFixedWidthSchemaLine(Document xmlDocument, FixedWidthSchemaLine schemaLine)
            throws SchemaException {
        Element xmlSchemaLine = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);

        assignSchemaLineBase(xmlSchemaLine, schemaLine);

        xmlSchemaLine.setAttribute(ATTRIB_FW_SCHEMA_FILL_CHARACTER, String.valueOf(schemaLine.getFillCharacter()));
        xmlSchemaLine.setAttribute(ATTRIB_FW_SCHEMA_TRIM_FILL_CHARACTERS, String.valueOf(schemaLine
                .isTrimFillCharacters()));
        if(schemaLine.getMinLength()>0)
            xmlSchemaLine.setAttribute(ATTRIB_FW_SCHEMA_MIN_LENGTH, String.valueOf(schemaLine.getMinLength()));

        for (FixedWidthSchemaCell schemaCell : schemaLine.getSchemaCells()) {
            Element xmlCell = extractFixedWidthSchemaCell(xmlDocument, schemaCell);
            xmlSchemaLine.appendChild(xmlCell);
        }
        return xmlSchemaLine;
    }

    /**
     * Builds the cell part of a file schema from an xml input
     * @param xmlDocument
     * @param schemaCell
     * @return The cell schema element
     * @throws SchemaException
     */
    private Element extractFixedWidthSchemaCell(Document xmlDocument, FixedWidthSchemaCell schemaCell)
            throws SchemaException {
        Element xmlSchemaCell = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);

        xmlSchemaCell.setAttribute(ATTRIB_FW_SCHEMA_CELL_LENGTH, String.valueOf(schemaCell.getLength()));
        xmlSchemaCell
                .setAttribute(ATTRIB_FW_SCHEMA_CELL_ALIGNMENT, schemaCell.getAlignment().toString().toLowerCase());

        assignSchemaCellBase(xmlDocument, xmlSchemaCell, schemaCell);
        return xmlSchemaCell;
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlDocument
     * @param schema
     * @return
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
     * @param xmlDocument
     * @param xmlSchema
     * @param schema
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
     * @param xmlSchemaLine
     * @return
     * @throws DataConversionException
     * @throws SchemaException
     */
    private Element extractCsvSchemaLine(Document xmlDocument, CsvSchemaLine schemaLine) throws SchemaException {

        Element xmlSchemaLine = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);

        assignSchemaLineBase(xmlSchemaLine, schemaLine);

        xmlSchemaLine.setAttribute(ATTRIB_CSV_SCHEMA_CELL_SEPARATOR, replaceJava2Escapes(schemaLine.getCellSeparator()));
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
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_MAX_LENGTH, String.valueOf(schemaCell.getMaxLength()));

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
        lineSeparator = replaceJava2Escapes(lineSeparator);
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

        if (line.getLineType() != null && !line.getLineType().isEmpty())
            xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_LINETYPE, line.getLineType());

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
        if(cell.isIgnoreRead())
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_IGNOREREAD, String.valueOf(cell.isIgnoreRead()));
        if(cell.isIgnoreWrite())
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_IGNOREWRITE, String.valueOf(cell.isIgnoreWrite()));
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_MANDATORY, String.valueOf(cell.isMandatory()));
        
        if(cell.getDefaultCell() != null)
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_DEFAULT_VALUE, cellComposer.format(cell.getDefaultCell(), cell));

        if(cell.getEmptyPattern() != null)
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_EMPTY_PATTERN, cell.getEmptyPattern().pattern());
        
        Element xmlFormat = extractCellFormat(xmlDocument, cell.getCellFormat());
        xmlSchemaCell.appendChild(xmlFormat);

        Element xmlRange = extractCellRange(xmlDocument, cell);
        if (xmlRange.hasChildNodes())
            xmlSchemaCell.appendChild(xmlRange);

        if(cell.getLocale() != null){
            xmlSchemaCell.appendChild(extractLocale(xmlDocument, cell.getLocale()));
        }

        if(cell.getLineCondition() != null){
            Element xmlLineCondition = extractLineCondition(xmlDocument, cell.getLineCondition());
            xmlSchemaCell.appendChild(xmlLineCondition);
        }
    }

    private Element extractLineCondition(Document xmlDocument, CellValueCondition lineCondition)
            throws SchemaException {
        Element xmlLineCondition = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_LINE_CONDITION);
        if (lineCondition instanceof MatchingCellValueCondition){
            MatchingCellValueCondition match = (MatchingCellValueCondition) lineCondition;
            Element xmlMatch = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_MATCH);
            xmlMatch.setAttribute(ATTRIB_PATTERN, match.getPattern());
            return xmlLineCondition;
        }
        throw new SchemaException("Unsupported line condition type: " + lineCondition.getClass() + ".");
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
            xmlRange.setAttribute(ATTRIB_SCHEMA_CELL_MIN, cellComposer.format(cell.getMinValue(), cell));
        if (cell.getMaxValue() != null)
            xmlRange.setAttribute(ATTRIB_SCHEMA_CELL_MAX,cellComposer.format( cell.getMaxValue(), cell));
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
     * @param sToReplace
     * @return
     */
    private String replaceJava2Escapes(String sToReplace) {
        sToReplace = sToReplace.replace("\r", "\\r");
        sToReplace = sToReplace.replace("\n", "\\n");
        sToReplace = sToReplace.replace("\t", "\\t");
        sToReplace = sToReplace.replace("\f", "\\f");
        return sToReplace;
    }
    
}
