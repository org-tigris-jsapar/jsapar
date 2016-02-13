package org.jsapar.schema;

public interface SchemaXmlTypes {
    public final static String ELEMENT_ROOT = "schema";
    public final static String ELEMENT_CSV_SCHEMA = "csvschema";
    public final static String ELEMENT_CSV_CONTROL_CELL_SCHEMA = "csvcontrolcellschema";
    public final static String ELEMENT_FIXED_WIDTH_SCHEMA = "fixedwidthschema";
    public final static String ELEMENT_FIXED_WIDTH_CONTROL_CELL_SCHEMA = "fixedwidthcontrolcellschema";
    public final static String ELEMENT_LOCALE = "locale";
    public final static String ELEMENT_FORMAT = "format";
    public static final String ATTRIB_FW_SCHEMA_FILL_CHARACTER = "fillcharacter";
    public static final String ATTRIB_FW_SCHEMA_MIN_LENGTH= "minlength";
    public static final String ATTRIB_FW_SCHEMA_TRIM_FILL_CHARACTERS = "trimfillcharacters";
    public static final String ATTRIB_SCHEMA_CELL_NAME = "name";
    public static final String ATTRIB_FW_SCHEMA_CELL_LENGTH = "length";
    public static final String ATTRIB_SCHEMA_LINE_LINETYPE = "linetype";
    public static final String ATTRIB_SCHEMA_LINE_LINETYPE_CONTROL_VALUE = "linetypecontrolvalue";
    public static final String ATTRIB_SCHEMA_LINE_OCCURS = "occurs";
    public static final String ATTRIB_CSV_SCHEMA_CELL_SEPARATOR = "cellseparator";
    public static final String ATTRIB_CSV_SCHEMA_CONTROL_CELL_SEPARATOR = "controlcellseparator";
    public static final String ATTRIB_CSV_SCHEMA_LINE_FIRSTLINEASSCHEMA = "firstlineasschema";
    public static final String ATTRIB_LOCALE_LANGUAGE = "language";
    public static final String ATTRIB_LOCALE_COUNTRY = "country";
    public static final String ATTRIB_SCHEMA_LINESEPARATOR = "lineseparator";
    public static final String ATTRIB_SCHEMA_CELL_IGNOREREAD = "ignoreread";
    public static final String ATTRIB_SCHEMA_CELL_IGNOREWRITE = "ignorewrite";
    public static final String ATTRIB_FW_SCHEMA_CONTROLCELLL_LENGTH = "length";
    public static final String ATTRIB_FW_SCHEMA_ERROR_IF_UNDEFINED_LINE_TYPE = "errorifundefinedlinetype";
    public static final String ATTRIB_FW_SCHEMA_CELL_ALIGNMENT = "alignment";
    public static final String ELEMENT_FW_SCHEMA_CONTROLCELL = "controlcell";
    public static final String ELEMENT_SCHEMA_LINE = "line";
    public static final String ELEMENT_SCHEMA_LINE_CELL = "cell";
    public static final String ATTRIB_SCHEMA_CELL_MANDATORY = "mandatory";
    public static final String ELEMENT_RANGE = "range";
    public static final String ATTRIB_SCHEMA_CELL_MIN = "min";
    public static final String ATTRIB_SCHEMA_CELL_MAX = "max";
    public static final String ATTRIB_SCHEMA_CELL_MAX_LENGTH = "maxlength";
    public static final String ATTRIB_CSV_QUOTE_CHAR = "quotechar";
    public static final String ATTRIB_SCHEMA_WRITE_CONTROL_CELL = "writecontrolcell";
    public static final String ATTRIB_SCHEMA_CELL_DEFAULT_VALUE = "default";
    public static final String ATTRIB_SCHEMA_CELL_EMPTY_PATTERN = "emptypattern";

    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String JSAPAR_XML_SCHEMA = "http://jsapar.tigris.org/JSaParSchema/2.0";

}
