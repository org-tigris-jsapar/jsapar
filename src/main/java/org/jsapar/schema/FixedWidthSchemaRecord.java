package org.jsapar.schema;

public class FixedWidthSchemaRecord extends SchemaRecord {

    private java.util.List<FixedWidthSchemaCell> schemaCells = new java.util.ArrayList<FixedWidthSchemaCell>();
    private boolean trimFillCharacters = true;
    private char fillCharacter = ' ';

    public FixedWidthSchemaRecord() {
    }
    
    public FixedWidthSchemaRecord(String recordType) {
        super(recordType);
    }

    /**
     * @return the trimFillCharacters
     */
    public boolean isTrimFillCharacters() {
        return trimFillCharacters;
    }

    /**
     * @param trimFillCharacters the trimFillCharacters to set
     */
    public void setTrimFillCharacters(boolean trimFillCharacters) {
        this.trimFillCharacters = trimFillCharacters;
    }

    /**
     * @return the fillCharacter
     */
    public char getFillCharacter() {
        return fillCharacter;
    }

    /**
     * @param fillCharacter the fillCharacter to set
     */
    public void setFillCharacter(char fillCharacter) {
        this.fillCharacter = fillCharacter;
    }

    /**
     * @param schemaCell
     */
    public void addSchemaCell(FixedWidthSchemaCell schemaCell) {
        this.schemaCells.add(schemaCell);
    }
    
    
}
