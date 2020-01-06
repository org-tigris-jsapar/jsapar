package org.jsapar.schema;

import org.jsapar.model.CellType;
import org.jsapar.text.EnumFormat;
import org.jsapar.text.Format;
import org.jsapar.utils.StringUtils;
import org.jsapar.utils.XmlTypes;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Builds a {@link Schema} instance from xml that conforms to the JSaPar xsd.
 */
public class Xml2SchemaBuilder implements SchemaXmlTypes, XmlTypes {

    /**
     * Utility function to retrieve first matching child element.
     * 
     * @param parentElement The parent element
     * @param sChildName The name of the child element to get.
     * @return The child element or null if none found.
     */
    private Element getChild(Element parentElement, String sChildName) {
        return getChild(JSAPAR_XML_SCHEMA, parentElement, sChildName).orElse(null);
    }

    private String getAttributeValue(Element parent, String name) {
        return parseAttribute(parent, name).orElse(null);
    }


    /**
     * Generates a schema from a xml file.
     * 
     * @param reader
     *            - A reader linked to the xml file.
     * @return The file parser schema.
     * @throws SchemaException When there is an error in the schema
     * @throws UncheckedIOException When there is an error reading the input
     */
    public Schema<? extends SchemaLine<? extends SchemaCell>> build(Reader reader) throws UncheckedIOException, SchemaException {
        String schemaFileName = "/xml/schema/JSaParSchema.xsd";

        try(InputStream schemaStream = Xml2SchemaBuilder.class.getResourceAsStream(schemaFileName)) {
            if (schemaStream == null)
                throw new FileNotFoundException("Could not find schema file: " + schemaFileName);
            // File schemaFile = new
            // File("resources/xml/schema/JSaParSchema.xsd");

            // javax.xml.validation.Schema xmlSchema;

            Element xmlRoot = parseXmlDocument(reader, schemaStream);
            // Element xmlRoot = (Element) xmlDocument.getFirstChild();

            Element xmlSchema = getChild(xmlRoot, ELEMENT_CSV_SCHEMA);
            if (null != xmlSchema)
                return buildCsvSchema(xmlSchema);

            xmlSchema = getChild(xmlRoot, ELEMENT_FIXED_WIDTH_SCHEMA);
            if (null != xmlSchema)
                return buildFixedWidthSchema(xmlSchema);

            xmlSchema = getChild(xmlRoot, ELEMENT_STRING_SCHEMA);
            if (null != xmlSchema)
                return buildStringSchema(xmlSchema);

            throw new SAXException(
                    "Failed to find specific schema XML element. Expected one of " + ELEMENT_CSV_SCHEMA + ", "
                            + ELEMENT_FIXED_WIDTH_SCHEMA+ " or " + ELEMENT_STRING_SCHEMA);
        }
        catch(ParserConfigurationException|SAXException e){
            throw new SchemaException("Failed to load schema from xml ", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load schema from xml", e);
        }
    }

    private StringSchema buildStringSchema(Element xmlSchema) {
        StringSchema.Builder schemaBuilder = StringSchema.builder();
        assignSchemaBase(schemaBuilder, xmlSchema, xmlSchemaLine->buildStringSchemaLine(schemaBuilder, xmlSchemaLine));
        return schemaBuilder.build();
    }

    private void buildStringSchemaLine(StringSchema.Builder schemaBuilder, Element xmlSchemaLine) {
        schemaBuilder.withLine(parseAttribute(xmlSchemaLine, ATTRIB_SCHEMA_LINE_LINETYPE).orElse("unknown"),
                l->buildStringSchemaLine(xmlSchemaLine, l));
    }
    private StringSchemaLine.Builder buildStringSchemaLine(Element xmlSchemaLine, StringSchemaLine.Builder schemaLineBuilder) {
        assignSchemaLineBase(schemaLineBuilder, xmlSchemaLine, xc->buildStringSchemaCell(schemaLineBuilder, xc));
        return schemaLineBuilder;
    }

    private void buildStringSchemaCell(StringSchemaLine.Builder schemaLineBuilder, Element xmlSchemaCell) {
        schemaLineBuilder.withCell(getAttributeValue(xmlSchemaCell, ATTRIB_SCHEMA_CELL_NAME), c->buildStringSchemaCell(xmlSchemaCell, c));
    }

    private StringSchemaCell.Builder<?> buildStringSchemaCell(Element xmlSchemaCell, StringSchemaCell.Builder<?> schemaCellBuilder) {
        assignSchemaCellBase(schemaCellBuilder, xmlSchemaCell);
        return schemaCellBuilder;
    }


    /**
     * @param xmlSchema The xml element to parse schema from
     * @return A parsed schema.
     * @throws SchemaException If there are errors in the schema configuration.
     */
    private FixedWidthSchema buildFixedWidthSchema(Element xmlSchema) throws SchemaException {
        FixedWidthSchema.Builder schemaBuilder = FixedWidthSchema.builder();

        assignFixedWidthSchema(schemaBuilder, xmlSchema);
        return schemaBuilder.build();
    }

    private void assignFixedWidthSchema(FixedWidthSchema.Builder schemaBuilder, Element xmlSchema) throws SchemaException {
        assignSchemaBase(schemaBuilder, xmlSchema, xmlSchemaLine->buildFixedWidthSchemaLine(schemaBuilder, xmlSchemaLine));
    }

    private void buildFixedWidthSchemaLine(FixedWidthSchema.Builder schemaBuilder, Element xmlSchemaLine) throws SchemaException {
        schemaBuilder.withLine(parseAttribute(xmlSchemaLine, ATTRIB_SCHEMA_LINE_LINETYPE).orElse("unknown"), l->buildFixedWidthSchemaLine(xmlSchemaLine, l));
    }

    private FixedWidthSchemaLine.Builder buildFixedWidthSchemaLine(Element xmlSchemaLine, FixedWidthSchemaLine.Builder schemaLineBuilder) throws SchemaException {
        assignSchemaLineBase(schemaLineBuilder, xmlSchemaLine, xc->buildFixedWidthSchemaCell(schemaLineBuilder, xc));

        parseAttribute(xmlSchemaLine, ATTRIB_FW_SCHEMA_PAD_CHARACTER)
                .map(s->s.charAt(0))
                .ifPresent(schemaLineBuilder::withPadCharacter);

        parseAttribute(xmlSchemaLine, ATTRIB_FW_SCHEMA_MIN_LENGTH)
                .filter(s->!s.isEmpty())
                .map(Integer::valueOf)
                .ifPresent(schemaLineBuilder::withMinLength);

        return schemaLineBuilder;
    }

    private void buildFixedWidthSchemaCell(FixedWidthSchemaLine.Builder schemaLineBuilder, Element xmlSchemaCell) throws SchemaException {
        int nLength = getIntValue(getMandatoryAttribute(xmlSchemaCell, ATTRIB_FW_SCHEMA_CELL_LENGTH));
        String sName = getAttributeValue(xmlSchemaCell, ATTRIB_SCHEMA_CELL_NAME);
        schemaLineBuilder.withCell(sName, nLength, c->buildFixedWidthSchemaCell(xmlSchemaCell, c));
    }

    private FixedWidthSchemaCell.Builder<?> buildFixedWidthSchemaCell(Element xmlSchemaCell, FixedWidthSchemaCell.Builder<?> cellBuilder) throws SchemaException {
        assignSchemaCellBase(cellBuilder, xmlSchemaCell);

        Node xmlAlignment = xmlSchemaCell.getAttributeNode(ATTRIB_FW_SCHEMA_CELL_ALIGNMENT);
        if(xmlAlignment != null) {
            switch (getStringValue(xmlAlignment)) {
            case "left":
                cellBuilder.withAlignment(FixedWidthSchemaCell.Alignment.LEFT);
                break;
            case "center":
                cellBuilder.withAlignment(FixedWidthSchemaCell.Alignment.CENTER);
                break;
            case "right":
                cellBuilder.withAlignment(FixedWidthSchemaCell.Alignment.RIGHT);
                break;
            default:
                throw new SchemaException(
                        "Invalid value for attribute: " + ATTRIB_FW_SCHEMA_CELL_ALIGNMENT + "=" + getStringValue(
                                xmlAlignment));
            }
        }
        parseAttribute(xmlSchemaCell, ATTRIB_FW_SCHEMA_PAD_CHARACTER)
                .map(s -> s.charAt(0))
                .ifPresent(cellBuilder::withPadCharacter);

        parseBooleanAttribute(xmlSchemaCell, ATTRIB_FW_SCHEMA_TRIM_PAD_CHARACTER)
                .ifPresent(cellBuilder::withTrimPadCharacter);

        parseBooleanAttribute(xmlSchemaCell, ATTRIB_FW_SCHEMA_TRIM_LEADING_SPACES)
                .ifPresent(cellBuilder::withTrimLeadingSpaces);

        return cellBuilder;
    }

    /**
     * Builds a CSV file schema object.
     * 
     * @param xmlSchema A schema xml element.
     * @return A newly created CSV schema.
     * @throws SchemaException If there is an error in the schema.
     */
    private Schema<?> buildCsvSchema(Element xmlSchema) throws SchemaException {
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        assignCsvSchema(schemaBuilder, xmlSchema);
        return schemaBuilder.build();
    }

    /**
     * Assigns values to a CSV schema object.
     * 
     * @param schemaBuilder    The schema to assign values to
     * @param xmlSchema A schema xml element
     * @throws SchemaException If there is an error in the schema.
     */
    private void assignCsvSchema(CsvSchema.Builder schemaBuilder, Element xmlSchema) throws SchemaException {

        assignSchemaBase(schemaBuilder, xmlSchema, xmlSchemaLine->buildCsvSchemaLine(schemaBuilder, xmlSchemaLine));

        parseAttribute(xmlSchema, ATTRIB_CSV_SCHEMA_QUOTE_SYNTAX)
                .map(QuoteSyntax::valueOf)
                .ifPresent(schemaBuilder::withQuoteSyntax);

    }


    private void buildCsvSchemaLine(CsvSchema.Builder schemaBuilder, Element xmlSchemaLine) throws SchemaException {
        schemaBuilder.withLine(
                parseAttribute(xmlSchemaLine, ATTRIB_SCHEMA_LINE_LINETYPE).orElse("unknown"),
                l->buildCsvSchemaLine(l, xmlSchemaLine));
    }

    private CsvSchemaLine.Builder buildCsvSchemaLine(CsvSchemaLine.Builder schemaLineBuilder, Element xmlSchemaLine) throws SchemaException {
        assignSchemaLineBase(schemaLineBuilder, xmlSchemaLine, xmlSchemaCell->buildCsvSchemaCell(schemaLineBuilder, xmlSchemaCell));

        parseAttribute(xmlSchemaLine, ATTRIB_CSV_SCHEMA_CELL_SEPARATOR)
                .map(StringUtils::replaceEscapes2Java)
                .ifPresent(schemaLineBuilder::withCellSeparator);

        Node xmlFirstLineAsSchema = xmlSchemaLine.getAttributeNode(ATTRIB_CSV_SCHEMA_LINE_FIRSTLINEASSCHEMA);
        if (xmlFirstLineAsSchema != null)
            schemaLineBuilder.withFirstLineAsSchema(getBooleanValue(xmlFirstLineAsSchema));

        parseAttribute(xmlSchemaLine, ATTRIB_CSV_QUOTE_CHAR).ifPresent(s->{
            if(s.equals(QUOTE_CHAR_NONE))
                schemaLineBuilder.withoutQuoteChar();
            else
                schemaLineBuilder.withQuoteChar(s.charAt(0));
        });

        parseAttribute(xmlSchemaLine, ATTRIB_CSV_QUOTE_BEHAVIOR)
                .map(QuoteBehavior::valueOf)
                .ifPresent(schemaLineBuilder::withDefaultQuoteBehavior);

        return schemaLineBuilder;
    }

    /**
     * Creates a CSV cell schema
     * @param schemaLineBuilder The schema line builder to add cell to.
     * @param xmlSchemaCell A cell schema xml element.
     * @throws SchemaException  When there is an error in the schema
     */
    private void buildCsvSchemaCell(CsvSchemaLine.Builder schemaLineBuilder, Element xmlSchemaCell) throws SchemaException {
        schemaLineBuilder.withCell(
                getAttributeValue(xmlSchemaCell, ATTRIB_SCHEMA_CELL_NAME),
                c->buildCsvSchemaCell(c, xmlSchemaCell));
    }

    private CsvSchemaCell.Builder<?> buildCsvSchemaCell(CsvSchemaCell.Builder<?> cellBuilder, Element xmlSchemaCell) throws SchemaException {

        Node xmlMaxLength = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_CELL_MAX_LENGTH);
        if (xmlMaxLength != null)
            cellBuilder.withMaxLength(getIntValue(xmlMaxLength));

        parseAttribute(xmlSchemaCell, ATTRIB_CSV_QUOTE_BEHAVIOR)
                .map(QuoteBehavior::valueOf)
                .ifPresent(cellBuilder::withQuoteBehavior);

        assignSchemaCellBase(cellBuilder, xmlSchemaCell);
        return cellBuilder;
    }

    /**
     * Assign common parts for base class.
     * 
     * @param schemaBuilder The schema to assign to
     * @param xmlSchema The xml element to parse
     * @throws SchemaException  When there is an error in the schema
     */
    private void assignSchemaBase(Schema.Builder<?,?,?> schemaBuilder, Element xmlSchema, Consumer<Element> applyToEachLine) throws SchemaException {
        parseAttribute(xmlSchema, ATTRIB_SCHEMA_LINESEPARATOR)
                .map(StringUtils::replaceEscapes2Java)
                .ifPresent(schemaBuilder::withLineSeparator);

        getChild(JSAPAR_XML_SCHEMA, xmlSchema, ELEMENT_LOCALE)
                .map(this::buildLocale)
                .ifPresent(schemaBuilder::withDefaultLocale);

        forEachLine(xmlSchema, applyToEachLine);
    }

    private void forEachLine(Element xmlSchema, Consumer<Element> applyToEachLine) {
        NodeList nodes = xmlSchema.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child instanceof Element)
                applyToEachLine.accept((Element) child);
        }
    }



    /**
     * Assign common pars for base class.
     *  @param lineBuilder The line to assign to
     * @param xmlSchemaLine The xml element to parse
     */
    private void assignSchemaLineBase(SchemaLine.Builder<?,?,?> lineBuilder, Element xmlSchemaLine, Consumer<Element> applyToEachCell)  {
        Node xmlOccurs = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_LINE_OCCURS);
        if (xmlOccurs != null) {
            if (getStringValue(xmlOccurs).equals("*"))
                lineBuilder.withOccursInfinitely();
            else
                lineBuilder.withOccurs(getIntValue(xmlOccurs));
        }

        Node xmlIgnoreRead = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_IGNOREREAD);
        if (xmlIgnoreRead != null)
            lineBuilder.withIgnoreRead(getBooleanValue(xmlIgnoreRead));

        Node xmlIgnoreWrite = xmlSchemaLine.getAttributeNode(ATTRIB_SCHEMA_IGNOREWRITE);
        if (xmlIgnoreWrite != null)
            lineBuilder.withIgnoreWrite(getBooleanValue(xmlIgnoreWrite));

        forEachCell(xmlSchemaLine, applyToEachCell);
    }

    private void forEachCell(Element xmlSchemaLine, Consumer<Element> applyToEachCell) {
        NodeList nodes = xmlSchemaLine.getElementsByTagNameNS(JSAPAR_XML_SCHEMA, ELEMENT_SCHEMA_LINE_CELL);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child instanceof Element)
                applyToEachCell.accept((Element) child);
        }
    }


    /**
     * Assign common parts for base class.
     * 
     * @param cellBuilder   The cellBuilder that builds the cell schema.
     * @param xmlSchemaCell The xml element to parse
     * @throws SchemaException  When there is an error in the schema
     */
    private void assignSchemaCellBase(SchemaCell.Builder<?, ?, ?> cellBuilder, Element xmlSchemaCell) throws SchemaException {
        Node xmlIgnoreRead = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_IGNOREREAD);
        if (xmlIgnoreRead != null)
            cellBuilder.withIgnoreRead(getBooleanValue(xmlIgnoreRead));

        Node xmlIgnoreWrite = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_IGNOREWRITE);
        if (xmlIgnoreWrite != null)
            cellBuilder.withIgnoreWrite(getBooleanValue(xmlIgnoreWrite));

        Node xmlMandatory = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_CELL_MANDATORY);
        if (xmlMandatory != null)
            cellBuilder.withMandatory(getBooleanValue(xmlMandatory));

        Element xmlLineCondition = getChild(xmlSchemaCell, ELEMENT_LINE_CONDITION);
        if (xmlLineCondition != null)
            cellBuilder.withLineCondition(extractCellValueCondition(xmlLineCondition));

        Element xmlEmptyCondition = getChild(xmlSchemaCell, ELEMENT_EMPTY_CONDITION);
        if (xmlEmptyCondition != null)
            cellBuilder.withEmptyCondition(extractCellValueCondition(xmlEmptyCondition));

        if(!parseCellFormat(xmlSchemaCell, cellBuilder)){
            if(!parseEnumCellFormat(xmlSchemaCell, cellBuilder)){
                parseImpliedDecimalFormat(xmlSchemaCell, cellBuilder);
            }
        }
        getChild(JSAPAR_XML_SCHEMA, xmlSchemaCell, ELEMENT_LOCALE).ifPresent(xmlLocale->
                cellBuilder.withLocale(buildLocale(xmlLocale)));

        Element xmlRange = getChild(xmlSchemaCell, ELEMENT_RANGE);
        if (xmlRange != null) {
            Node minValue = xmlRange.getAttributeNode(ATTRIB_SCHEMA_CELL_MIN);
            if (minValue != null)
                cellBuilder.withMinValue(getStringValue(minValue));
            Node maxValue = xmlRange.getAttributeNode(ATTRIB_SCHEMA_CELL_MAX);
            if (maxValue != null)
                cellBuilder.withMaxValue(getStringValue(maxValue));
        }

        Node xmlDefault = xmlSchemaCell.getAttributeNode(ATTRIB_SCHEMA_CELL_DEFAULT_VALUE);
        if (xmlDefault != null)
            cellBuilder.withDefaultValue(getStringValue(xmlDefault));


    }

    @SuppressWarnings({"rawtypes", "unchecked", "UnusedReturnValue"})
    private boolean parseImpliedDecimalFormat(Element xmlSchemaCell, SchemaCell.Builder cellBuilder) {
        return getChild(JSAPAR_XML_SCHEMA, xmlSchemaCell, ELEMENT_IMPLIED_DECIMALFORMAT)
                .map(xmlFormat -> {
                    int decimals = parseAttribute(xmlFormat, "decimals").map(Integer::parseInt).orElse(0);
                    cellBuilder.withFormat(Format.ofImpliedDecimalInstance(decimals));
                    return true;
                }).orElse(false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean parseEnumCellFormat(Element xmlSchemaCell, SchemaCell.Builder cellBuilder) {
        return getChild(JSAPAR_XML_SCHEMA, xmlSchemaCell, ELEMENT_ENUM_FORMAT)
                .map(xmlFormat -> {
                    String sEnumClass = getAttributeValue(xmlFormat, "class");
                    boolean ignoreCase = parseBooleanAttribute(xmlFormat, "ignorecase").orElse(false);
                    try {
                        EnumFormat format = new EnumFormat(Class.forName(sEnumClass), ignoreCase);
                        getChildrenStream(JSAPAR_XML_SCHEMA, xmlFormat, "value").forEach(xmlValue->
                                format.putEnumValueIfAbsent(getAttributeValue(xmlValue, "text"), getAttributeValue(xmlValue, "name")));

                        cellBuilder.withFormat(format);
                        return true;
                    } catch (ClassNotFoundException e) {
                        throw new SchemaException("Unable to find enum class " + sEnumClass + " within classpath. Make sure that the class is fully qualified.");
                    }
                }).orElse(false);
    }

    /**
     * Adds formatting to the cell.
     *
     * @param xmlSchemaCell The base element.
     * @param cellBuilder The cell builder to add format to.
     * @throws SchemaException  When there is an error in the schema
     * @return true if there was a format
     */
    private boolean parseCellFormat(Element xmlSchemaCell, SchemaCell.Builder<?,?,?> cellBuilder) {
        return getChild(JSAPAR_XML_SCHEMA, xmlSchemaCell, ELEMENT_FORMAT).map( xmlFormat->{
            parseAttribute(xmlFormat, "type").ifPresent(s->cellBuilder.withCellType(makeCellType(s)));
            parseAttribute(xmlFormat, "pattern").ifPresent(cellBuilder::withPattern);
            return true;
                }).orElse(false);
    }


    private Predicate<String> extractCellValueCondition(Element xmlCellValueCondition) throws SchemaException {
        Element xmlMatch = getChild(xmlCellValueCondition, ELEMENT_MATCH);
        if(xmlMatch != null){
            String pattern = getAttributeValue(xmlMatch, ATTRIB_PATTERN);
            return new MatchingCellValueCondition(pattern);
        }
        throw new SchemaException("Expected line condition is missing");
    }





    /**
     * Transforms from xml schema type to CellType enum.
     * 
     * @param sType
     *            The xml representation of the type.
     * @return The CellType value
     * @throws SchemaException  When there is an error in the schema
     */
    private CellType makeCellType(String sType) throws SchemaException {
        switch (sType) {
            case "string":
                return CellType.STRING;
            case "integer":
                return CellType.INTEGER;
            case "date":
                return CellType.DATE;
            case "local_date":
                return CellType.LOCAL_DATE;
            case "local_date_time":
                return CellType.LOCAL_DATE_TIME;
            case "local_time":
                return CellType.LOCAL_TIME;
            case "zoned_date_time":
                return CellType.ZONED_DATE_TIME;
            case "float":
                return CellType.FLOAT;
            case "decimal":
                return CellType.DECIMAL;
            case "boolean":
                return CellType.BOOLEAN;
            case "character":
                return CellType.CHARACTER;
            case "enum":
                return CellType.ENUM;
            default:
                throw new SchemaException("Unknown cell format type: " + sType);
        }

    }

    /**
     * Builds a java.util.Locale based on a Locale element.
     * 
     * @param xmlLocale The xml element to parse
     * @return The java Locale object.
     * @throws SchemaException  When there is an error in the schema
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
     * @param xmlElement The xml element to parse
     * @param sAttributeName The name of the attribute
     * @return The xml attribute.
     * @throws SchemaException  When there is an error in the schema
     */
    private static Attr getMandatoryAttribute(Element xmlElement, String sAttributeName) throws SchemaException {
        Attr xmlAttribute = xmlElement.getAttributeNode(sAttributeName);
        if (xmlAttribute == null)
            throw new SchemaException("Missing mandatory attribute: " + sAttributeName + " of element " + xmlElement);
        return xmlAttribute;
    }
    
    /**
     * Utility function for loading a schema from an xml file.
     * @param file The file to load schema from
     * @return A newly created schema from the xml file.
     * @throws SchemaException  When there is an error in the schema
     * @throws UncheckedIOException      When there is an error reading from input
     */
    public static Schema<?> loadSchemaFromXmlFile(File file)
            throws UncheckedIOException, SchemaException{
        return loadSchemaFromXmlFile(file, Charset.defaultCharset().name());
    }
    
    /**
     * Utility function for loading a schema from an xml file.
     * @param file      The file to load schema from
     * @param encoding The character encoding to use while reading file.
     * @return A newly created schema from the xml file.
     * @throws SchemaException  When there is an error in the schema
     * @throws UncheckedIOException      When there is an error reading from input
     */
    @SuppressWarnings("WeakerAccess")
    public static Schema<?> loadSchemaFromXmlFile(File file, String encoding)
            throws UncheckedIOException, SchemaException {

        try (InputStream is = new FileInputStream(file);
                Reader schemaReader = new InputStreamReader(is, encoding)) {
            Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
            return builder.build(schemaReader);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load schema from file", e);
        }
    }

    /**
     * Loads a schema from specified resource using default character encoding.
     * 
     * @param resourceBaseClass
     *            A class that specifies the base for the relative location of the resource. If this parameter is null,
     *            the resource name has to specify the resource with an absolute path.
     * @param resourceName
     *            The name of the resource to load.
     * @return A newly created schema from the supplied xml resource.
     * @throws SchemaException  When there is an error in the schema
     * @throws UncheckedIOException      When there is an error reading from input
     */
    @SuppressWarnings("unused")
    public static Schema<?> loadSchemaFromXmlResource(Class<?> resourceBaseClass, String resourceName)
            throws UncheckedIOException, SchemaException{
        return loadSchemaFromXmlResource(resourceBaseClass, resourceName, Charset.defaultCharset().name());
    }    
    
    /**
     * Loads a schema from specified resource using supplied character encoding.
     * 
     * @param resourceBaseClass
     *            A class that specifies the base for the relative location of the resource. If this parameter is null,
     *            the resource name has to specify the resource with an absolute path.
     * @param resourceName
     *            The name of the resource to load.
     * @param encoding
     *            The character encoding to use while reading resource.
     * @return A newly created schema from the supplied xml resource.
     * @throws SchemaException  When there is an error in the schema
     * @throws UncheckedIOException      When there is an error reading from input
     */
    @SuppressWarnings("WeakerAccess")
    public static Schema<?> loadSchemaFromXmlResource(Class<?> resourceBaseClass, String resourceName, String encoding)
            throws UncheckedIOException, SchemaException{
        if (resourceBaseClass == null)
            resourceBaseClass = Xml2SchemaBuilder.class;
        URL resource = resourceBaseClass.getResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException(
                    "Unable to get resource [" + resourceName + "] in package " + resourceBaseClass.getPackage().getName() + " - failed to load schema.");
        }
        try (InputStream is = resource.openStream()) {
            Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
            return schemaBuilder.build(new InputStreamReader(is, encoding));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load schema from xml resource", e);
        }
    }

}
