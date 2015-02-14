package org.jsapar.schema;

public class FixedWidthRecord extends Record {
    
    private FixedWidthSchemaRecord schemaRecord;

    /**
     * @return the schemaRecord
     */
    public FixedWidthSchemaRecord getSchemaRecord() {
        return schemaRecord;
    }

    /**
     * @param schemaRecord the schemaRecord to set
     */
    public void setSchemaRecord(FixedWidthSchemaRecord schemaRecord) {
        this.schemaRecord = schemaRecord;
    }

}
