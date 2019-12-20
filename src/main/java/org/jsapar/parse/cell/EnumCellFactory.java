package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.EnumCell;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.jsapar.text.EnumFormat;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses string values into {@link Cell} objects
 */
public class EnumCellFactory implements CellFactory{

    @SuppressWarnings("unchecked")
    @Override
    public Cell makeCell(String name, String value, Format format) throws ParseException {
        if (format == null)
            throw new ParseException("Format is required while parsing enum cell values.", 0);
        Enum enumValue = (Enum) format.parse(value);
        return new EnumCell(name, enumValue);
    }

    @Override
    public Format makeFormat(Locale locale) {
        return null;
    }

    /**
     * Create a {@link Format} instance for the enum cell type given the locale and a specified pattern.
     * @param locale  For enum cell type, the locale is insignificant.
     * @param pattern A pattern to use for the format object.
     *                The pattern have to contain the class name of the Enum to create a format for.
     * Example: pattern="org.jsapar.model.CellType" will create a format that can parse and compose enum values for the
     *                enum class org.jsapar.model.CellType. Full class name, including package names is required.
     * @return A format object to use for the enum type cells.
     */
    @SuppressWarnings("unchecked")
    @Override
    public org.jsapar.text.Format makeFormat(Locale locale, String pattern) {
        try {
            Class<Enum> enumClass = (Class<Enum>) Class.forName(pattern);
            return new EnumFormat(enumClass, false);
        } catch (ClassNotFoundException e) {
            throw new SchemaException("There is no Enum class with class name: " + pattern);
        }
    }

    @Override
    public int actualCacheMaxSize(SchemaCell schemaCell, int configuredCacheMaxSize) {
        Format format = schemaCell.getFormat();
        if(format instanceof EnumFormat){
            EnumFormat enumFormat = (EnumFormat) format;
            return enumFormat.numberOfTextValues();
        }
        return configuredCacheMaxSize;
    }
}
