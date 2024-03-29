package org.jsapar.schema;

import org.jsapar.compose.cell.CellFormat;
import org.jsapar.model.CellType;
import org.jsapar.text.format.EnumFormat;
import org.jsapar.text.format.ImpliedDecimalFormat;
import org.jsapar.utils.StringUtils;
import org.jsapar.utils.XmlTypes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Extracts xml representation for a {@link Schema} and writes it to a writer.
 */
@SuppressWarnings("WeakerAccess")
public class Schema2XmlExtractor implements SchemaXmlTypes, XmlTypes {

    /**
     * Writes supplied schema as xml to supplied writer.
     * 
     * @param writer
     *            The writer to write to.
     * @param schema
     *            The schema to extract.
     * @throws SchemaException If there is an error in the schema
     */
    public void extractXml(Writer writer, Schema schema) throws SchemaException {
        try {

            Document xmlDocument = extractXmlDocument(schema);

            Transformer t  = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.transform(new DOMSource(xmlDocument), new StreamResult(writer));
            
        } catch (TransformerException e) {
            throw new SchemaException("Failed to generate schema.", e);
        }
    }

    /**
     * Extract xml document from schema.
     * @param schema The schema to extract
     * @return The xml document
     * @throws SchemaException If there is an error in the schema
     */
    private Document extractXmlDocument(Schema schema) throws SchemaException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);
            factory.setCoalescing(true);
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDocument = builder.newDocument();
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
     * Extract xml DOM from fixed width schema
     * @param xmlDocument The xml DOM document to extract to
     * @param schema The schema to extract
     * @return The schema element
     * @throws SchemaException If there is an error in the schema
     */
    private Element extractFixedWidthSchema(Document xmlDocument, FixedWidthSchema schema) throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FIXED_WIDTH_SCHEMA);

        assignFixedWidthSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * @param xmlDocument The root xml DOM document.
     * @param xmlSchema The xml element to assign to.
     * @param schema The schema to extract.
     * @throws SchemaException If there is an error in the schema
     */
    private void assignFixedWidthSchema(Document xmlDocument, Element xmlSchema, FixedWidthSchema schema)
            throws SchemaException {

        assignSchemaBase(xmlDocument, xmlSchema, schema);

        for (FixedWidthSchemaLine schemaLine : schema.getSchemaLines()) {
            Element xmlLine = extractFixedWidthSchemaLine(xmlDocument, schemaLine);
            xmlSchema.appendChild(xmlLine);
        }
    }


    /**
     * Extracts the lines of a schema into an xml.
     *
     * @param xmlDocument The root DOM document
     * @param schemaLine The schema line to extract
     * @return The line schema element
     * @throws SchemaException If there is an error in the schema
     */
    private Element extractFixedWidthSchemaLine(Document xmlDocument, FixedWidthSchemaLine schemaLine)
            throws SchemaException {
        Element xmlSchemaLine = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);

        assignSchemaLineBase(xmlSchemaLine, schemaLine);

        if(schemaLine.getMinLength()>0)
            xmlSchemaLine.setAttribute(ATTRIB_FW_SCHEMA_MIN_LENGTH, String.valueOf(schemaLine.getMinLength()));

        for (FixedWidthSchemaCell schemaCell : schemaLine) {
            Element xmlCell = extractFixedWidthSchemaCell(xmlDocument, schemaCell);
            xmlSchemaLine.appendChild(xmlCell);
        }
        return xmlSchemaLine;
    }

    /**
     * Builds the cell part of a file schema from an xml input
     * @param xmlDocument The root xml document
     * @param schemaCell The schema cell to extract.
     * @return The cell schema element
     * @throws SchemaException If there is an error in the schema
     */
    private Element extractFixedWidthSchemaCell(Document xmlDocument, FixedWidthSchemaCell schemaCell)
            throws SchemaException {
        Element xmlSchemaCell = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);

        xmlSchemaCell.setAttribute(ATTRIB_FW_SCHEMA_CELL_LENGTH, String.valueOf(schemaCell.getLength()));
        xmlSchemaCell
                .setAttribute(ATTRIB_FW_SCHEMA_CELL_ALIGNMENT, schemaCell.getAlignment().toString().toLowerCase());

        xmlSchemaCell.setAttribute(ATTRIB_FW_SCHEMA_PAD_CHARACTER, String.valueOf(schemaCell.getPadCharacter()));
        xmlSchemaCell.setAttribute(ATTRIB_FW_SCHEMA_TRIM_PAD_CHARACTER, String.valueOf(Boolean.valueOf(schemaCell.isTrimPadCharacter())));
        xmlSchemaCell.setAttribute(ATTRIB_FW_SCHEMA_TRIM_LEADING_SPACES, String.valueOf(Boolean.valueOf(schemaCell.isTrimLeadingSpaces())));

        assignSchemaCellBase(xmlDocument, xmlSchemaCell, schemaCell);
        return xmlSchemaCell;
    }

    /**
     * Builds a CSV file schema object.
     *
     * @param xmlDocument The root xml document
     * @param schema The schema to extract
     * @return The resulting xml element.
     * @throws SchemaException If there is an error in the schema
     */
    private Element extractCsvSchema(Document xmlDocument, CsvSchema schema) throws SchemaException {
        Element xmlSchema = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_CSV_SCHEMA);
        if(schema.getQuoteSyntax() != QuoteSyntax.FIRST_LAST)
            xmlSchema.setAttribute(ATTRIB_CSV_SCHEMA_QUOTE_SYNTAX, schema.getQuoteSyntax().name());

        assignCsvSchema(xmlDocument, xmlSchema, schema);

        return xmlSchema;
    }

    /**
     * Builds a CSV file schema object.
     *
     * @param xmlDocument The root xml document
     * @param xmlSchema The xml schema element to assign to
     * @param schema The schema to extract
     * @throws SchemaException If there is an error in the schema
     */
    private void assignCsvSchema(Document xmlDocument, Element xmlSchema, CsvSchema schema) throws SchemaException {
        assignSchemaBase(xmlDocument, xmlSchema, schema);

        for (CsvSchemaLine schemaLine : schema.getSchemaLines()) {
            Element xmlLine = extractCsvSchemaLine(xmlDocument, schemaLine);
            xmlSchema.appendChild(xmlLine);
        }
    }


    /**
     * @param xmlDocument The root xml document
     * @param schemaLine The schema line to extract
     * @return The schema line xml element
     * @throws SchemaException If there is an error in the schema
     */
    private Element extractCsvSchemaLine(Document xmlDocument, CsvSchemaLine schemaLine) throws SchemaException {

        Element xmlSchemaLine = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);

        assignSchemaLineBase(xmlSchemaLine, schemaLine);

        xmlSchemaLine.setAttribute(ATTRIB_CSV_SCHEMA_CELL_SEPARATOR, StringUtils.replaceJava2Escapes(schemaLine.getCellSeparator()));
        xmlSchemaLine.setAttribute(ATTRIB_CSV_SCHEMA_LINE_FIRSTLINEASSCHEMA, String.valueOf(schemaLine.isFirstLineAsSchema()));

        xmlSchemaLine.setAttribute(ATTRIB_CSV_QUOTE_CHAR, schemaLine.isQuoteCharUsed() ? String.valueOf(schemaLine.getQuoteChar()): QUOTE_CHAR_NONE);

        for (CsvSchemaCell schemaCell : schemaLine) {
            Element xmlCell = extractCsvSchemaCell(xmlDocument, schemaCell);
            xmlSchemaLine.appendChild(xmlCell);
        }

        return xmlSchemaLine;
    }

    /**
     * @param xmlDocument The root xml document
     * @param schemaCell The schema cell to extract
     * @return The schema cell xml element
     * @throws SchemaException If there is an error in the schema
     */
    private Element extractCsvSchemaCell(Document xmlDocument, CsvSchemaCell schemaCell) throws SchemaException {

        Element xmlSchemaCell = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_MAX_LENGTH, String.valueOf(schemaCell.getMaxLength()));
        xmlSchemaCell.setAttribute(ATTRIB_CSV_QUOTE_BEHAVIOR, String.valueOf(schemaCell.getQuoteBehavior()));

        assignSchemaCellBase(xmlDocument, xmlSchemaCell, schemaCell);
        return xmlSchemaCell;

    }

    /**
     * Assign common parts for base class.
     *
     * @param xmlDocument The root xml document
     * @param xmlSchema The schema xml element to assign to
     * @param schema The schema to extract
     */
    private void assignSchemaBase(Document xmlDocument, Element xmlSchema, Schema schema) {
        String lineSeparator = schema.getLineSeparator();
        lineSeparator = StringUtils.replaceJava2Escapes(lineSeparator);
        xmlSchema.setAttribute(ATTRIB_SCHEMA_LINESEPARATOR, lineSeparator);
    }

    /**
     * Assign common pars for base class.
     *
     * @param xmlSchemaLine    The schema line xml element to assign to
     * @param schemaLine The schema line to extract
     */
    private void assignSchemaLineBase(Element xmlSchemaLine, SchemaLine schemaLine) {
        String sOccurs = schemaLine.isOccursInfinitely() ? "*" : String.valueOf(schemaLine.getOccurs());
        xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_OCCURS, sOccurs);

        if (schemaLine.getLineType() != null && !schemaLine.getLineType().isEmpty())
            xmlSchemaLine.setAttribute(ATTRIB_SCHEMA_LINE_LINETYPE, schemaLine.getLineType());

    }

    /**
     * @param xmlDocument The root xml document
     * @param xmlSchemaCell The schema cell xml element to assign to
     * @param schemaCell The schema cell to extract
     * @throws SchemaException If there is an error in the schema
     */
    private void assignSchemaCellBase(Document xmlDocument, Element xmlSchemaCell, SchemaCell schemaCell)
            throws SchemaException {
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_NAME, schemaCell.getName());
        if(schemaCell.isIgnoreRead())
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_IGNOREREAD, String.valueOf(schemaCell.isIgnoreRead()));
        if(schemaCell.isIgnoreWrite())
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_IGNOREWRITE, String.valueOf(schemaCell.isIgnoreWrite()));
        xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_MANDATORY, String.valueOf(schemaCell.isMandatory()));

        if(schemaCell.isDefaultValue())
            xmlSchemaCell.setAttribute(ATTRIB_SCHEMA_CELL_DEFAULT_VALUE, schemaCell.getDefaultValue());

        Element xmlFormat = extractCellFormat(xmlDocument, schemaCell.getCellFormat());
        xmlSchemaCell.appendChild(xmlFormat);

        Element xmlRange = extractCellRange(xmlDocument, schemaCell);
        if (xmlRange.hasChildNodes())
            xmlSchemaCell.appendChild(xmlRange);

        if(schemaCell.getLocale() != null){
            xmlSchemaCell.appendChild(extractLocale(xmlDocument, schemaCell.getLocale()));
        }

        if(schemaCell.hasLineCondition()){
            Element xmlLineCondition = extractCellValueCondition(xmlDocument, schemaCell.getLineCondition(), ELEMENT_LINE_CONDITION);
            xmlSchemaCell.appendChild(xmlLineCondition);
        }

        if(schemaCell.hasEmptyCondition()){
            Element xmlLineCondition = extractCellValueCondition(xmlDocument, schemaCell.getLineCondition(), ELEMENT_EMPTY_CONDITION);
            xmlSchemaCell.appendChild(xmlLineCondition);
        }
    }

    private Element extractCellValueCondition(Document xmlDocument, Predicate<String> lineCondition, String elementName)
            throws SchemaException {
        Element xmlLineCondition = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, elementName);
        if (lineCondition instanceof MatchingCellValueCondition){
            MatchingCellValueCondition match = (MatchingCellValueCondition) lineCondition;
            Element xmlMatch = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_MATCH);
            xmlMatch.setAttribute(ATTRIB_PATTERN, match.getPattern());
            return xmlLineCondition;
        }
        throw new SchemaException("Unsupported line condition type: " + lineCondition.getClass() + ".");
    }

    /**
     * @param xmlDocument The root xml document
     * @param schemaCell The schema cell to extract
     * @return The schema cell xml element
     */
    private Element extractCellRange(Document xmlDocument, SchemaCell schemaCell) {
        Element xmlRange = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_RANGE);
        CellFormat cellFormat = CellFormat.ofSchemaCell(schemaCell);
        if (schemaCell.getMinValue() != null)
            xmlRange.setAttribute(ATTRIB_SCHEMA_CELL_MIN, cellFormat.format(schemaCell.getMinValue()));
        if (schemaCell.getMaxValue() != null)
            xmlRange.setAttribute(ATTRIB_SCHEMA_CELL_MAX, cellFormat.format(schemaCell.getMaxValue()));
        return xmlRange;
    }


    private Element extractLocale(Document xmlDocument, Locale locale) {
        Element xmlLocale = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_LOCALE);
        xmlLocale.setAttribute(ATTRIB_LOCALE_COUNTRY, locale.getCountry());
        xmlLocale.setAttribute(ATTRIB_LOCALE_LANGUAGE, locale.getLanguage());
        return xmlLocale;
    }

    /**
     * @param xmlDocument The root xml document
     * @param format The format to extract
     * @return The format xml element
     */
    @SuppressWarnings("unchecked")
    private Element extractCellFormat(Document xmlDocument, SchemaCellFormat format) {
        if(format.getCellType() == CellType.ENUM){
            EnumFormat enumFormat = (EnumFormat) format.getFormat();
            Element xmlFormat = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_ENUM_FORMAT);
            xmlFormat.setAttribute("class", enumFormat.getEnumClass().getName());
            xmlFormat.setAttribute("ignorecase", String.valueOf(enumFormat.isIgnoreCase()));
            Collection<String> textValues = enumFormat.textValues();
            textValues.forEach(v->{
                Element xmlValue = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, "value");
                xmlValue.setAttribute("name", enumFormat.enumByTextValue(v).name());
                xmlValue.setAttribute("text", v);
                xmlFormat.appendChild(xmlValue);
            });
            return xmlFormat;
        }
        else if(format.getCellType() == CellType.DECIMAL && format.getFormat() instanceof ImpliedDecimalFormat){
            ImpliedDecimalFormat f = (ImpliedDecimalFormat) format.getFormat();
            Element xmlFormat = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_IMPLIED_DECIMALFORMAT);
            xmlFormat.setAttribute("decimals", String.valueOf(f.getDecimals()));
            return xmlFormat;
        }
        else{
            Element xmlFormat = xmlDocument.createElementNS(JSAPAR_XML_SCHEMA, ELEMENT_FORMAT);
            xmlFormat.setAttribute("type", format.getCellType().toString().toLowerCase());
            if (format.getPattern() != null && format.getPattern().length() > 0)
                xmlFormat.setAttribute("pattern", format.getPattern());
            return xmlFormat;
        }
    }


}
