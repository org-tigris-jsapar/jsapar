package org.jsapar.model;

import org.jsapar.schema.SchemaException;

import java.text.Format;

public class CharacterCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 8442587766024601673L;

    private Character characterValue;

    public CharacterCell(String sName) {
        super(sName, CellType.CHARACTER);
    }

    public CharacterCell(String sName, Character value) {
        super(sName, CellType.CHARACTER);
        this.characterValue = value;
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
    public Character getCharacterValue() {
        return characterValue;
    }

    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
        if(characterValue == null)
            return null;
        if (format != null)
            return format.format(this.characterValue);
        else
            return characterValue.toString();
    }

    @Override
    public Object getValue() {
        return characterValue;
    }



    /**
     * @param characterValue
     *            the characterValue to set
     */
    public void setCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

}
