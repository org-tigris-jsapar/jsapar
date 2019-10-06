package org.jsapar.schema;

import org.jsapar.parse.csv.CsvParser;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextSchemaParser;

import java.io.Reader;
import java.util.*;
import java.util.stream.Stream;

/**
 * Defines a schema for a delimited input text. Each cell is delimited by a delimiter character sequence.
 * Lines are separated by the line separator defined by {@link Schema#getLineSeparator()}.
 * @see Schema
 * @see SchemaLine
 * @see CsvSchemaLine
 */
public class CsvSchema extends Schema implements Cloneable{

    /**
     * The schema lines
     */
    private LinkedHashMap<String, CsvSchemaLine> schemaLines = new LinkedHashMap<>();

    /**
     * Specifies if parsing and composing of quoted cells should comply to <a href="https://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     * False is the default value since it is the most common scenario.
     * <p/>
     * If false, quoted cells are considered quoted if and only if it begins and ends with a
     * quote character and all the intermediate characters are treated as is.
     * "aaa","b""bb","ccc" will be treated as three cells with the values `aaa`, `b""bb` and `ccc`.
     * No characters will be replaced between the quotes. Be aware that this mode will treat the input
     * "aaa","b"","ccc" as three cells with the values `aaa`, `b"` and `ccc`.
     * <p/>
     * If true, parsing and composing will consider the <a href="https://tools.ietf.org/html/rfc4180">RFC 4180</a> regarding quotes.
     * Any double occurrences of quote characters will be treated as if one quote character will be part of the cell value.
     * For instance "aaa","b""bb","ccc" will still be treated as three cells but with the values `aaa`, `b"bb` and `ccc`. This mode will treat the input
     * "aaa","b"",bbb" as two cells with the values `aaa` and , `b",bbb`. The double occurrences of quotes escapes it and it will be treated as part of the cell value.
     * When composing quoted cells, all quotes within cell value will be escaped with an additional quote character in order to make the output compliant.
     * <p/>
     * According to RFC 4180, single quotes may not occur inside a quoted cell. This parser will however allow it and
     * treat it as part of the cell value as long as it is not followed by the cell separator.
     * <p/>
     */
    private boolean complyRfc4180 = false;

    /**
     * @param schemaLine the schemaLine to add
     */
    public void addSchemaLine(CsvSchemaLine schemaLine) {
        this.schemaLines.put(schemaLine.getLineType(), schemaLine);
    }

    @Override
    public boolean isEmpty() {
        return this.schemaLines.isEmpty();
    }

    @Override
    public CsvSchema clone() {
        CsvSchema schema;
        schema = (CsvSchema) super.clone();

        schema.schemaLines = new LinkedHashMap<>();
        this.stream().map(CsvSchemaLine::clone).forEach(schema::addSchemaLine);
        return schema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#toString()
     */
    @Override
    public String toString() {
        return "CsvSchema" + super.toString() +
                " schemaLines=" +
                this.schemaLines;
    }

    @Override
    public Collection<CsvSchemaLine> getSchemaLines() {
        return this.schemaLines.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#getSchemaLine(java.lang.String)
     */
    @Override
    public Optional<CsvSchemaLine> getSchemaLine(String lineType) {
        return Optional.ofNullable(schemaLines.get(lineType));
    }

    @Override
    public int size() {
        return this.schemaLines.size();
    }

    @Override
    public Stream<CsvSchemaLine> stream() {
        return this.schemaLines.values().stream();
    }

    @Override
    public Iterator<CsvSchemaLine> iterator() {
        return schemaLines.values().iterator();
    }

    @Override
    public TextSchemaParser makeSchemaParser(Reader reader, TextParseConfig parseConfig) {
        return new CsvParser(reader, this, parseConfig);
    }

    public boolean isComplyRfc4180() {
        return complyRfc4180;
    }

    public void setComplyRfc4180(boolean complyRfc4180) {
        this.complyRfc4180 = complyRfc4180;
    }


}
