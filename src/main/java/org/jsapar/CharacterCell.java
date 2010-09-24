package org.jsapar;

import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.jsapar.schema.SchemaException;

public class CharacterCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 8442587766024601673L;

    private Character characterValue;

    public CharacterCell() {
        super(CellType.CHARACTER);
    }

    public CharacterCell(String sName) {
        super(sName, CellType.CHARACTER);
    }

    public CharacterCell(String sName, Character value) {
        super(sName, CellType.CHARACTER);
        this.characterValue = value;
    }
    
    /**
     * Creates a character cell with the supplied name and value. The format parameter is used to parse the supplied value.
     * @param name
     * @param value
     * @param format
     * @throws ParseException
     */
    public CharacterCell(String name, String value, Format format)
            throws ParseException {
        super(name, CellType.CHARACTER);
        setValue(value, format);
    }
    

    /**
     * @param sName
     * @param sValue
     * @throws ParseException
     */
    public CharacterCell(String sName, String sValue) throws ParseException {
        super(sName, CellType.CHARACTER);
        setValue(sValue);
    }
    

    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        if (right instanceof CharacterCell) {
            Character chRight = ((CharacterCell) right).getCharacterValue();
            return characterValue.compareTo(chRight);
        } else {
            throw new SchemaException("Value of cell of type " + getCellType()
                    + " can not be compared to value of cell of type " + right.getCellType());
        }
    }

    /**
     * @return the value of this cell as a character
     */
    private Character getCharacterValue() {
        return characterValue;
    }

    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
        if (format != null)
            return format.format(this.characterValue);
        else
            return characterValue.toString();
    }

    @Override
    public Object getValue() {
        return characterValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#setValue(java.lang.String)
     */
    @Override
    public void setValue(String value) throws ParseException {
        if (value.length() > 1) {
            throw new java.text.ParseException("Invalid characters found while parsing single character.", 1);
        } else if (value.length() == 1)
            setCharacterValue(value.charAt(0));
        else
            throw new java.text.ParseException("Empty value found while parsing single character.", 0);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#setValue(java.lang.String, java.util.Locale)
     */
    @Override
    public void setValue(String value, Locale locale) throws ParseException {
        setValue(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Format format) throws ParseException {

        ParsePosition pos = new ParsePosition(0);
        if (format != null) {
            this.characterValue = (Character) format.parseObject(value, pos);

            if (pos.getIndex() < value.length())
                // It is not acceptable to parse only a part of the string.
                throw new java.text.ParseException("Invalid characters found while parsing single character.", pos
                        .getIndex());
        } else
            setValue(value);

    }

    /**
     * @param characterValue
     *            the characterValue to set
     */
    public void setCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

}
