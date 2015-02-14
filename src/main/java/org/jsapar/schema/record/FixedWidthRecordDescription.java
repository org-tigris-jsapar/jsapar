package org.jsapar.schema.record;


public class FixedWidthRecordDescription extends RecordDescription {
    
    private FixedWidthSchemaRecord recordSchema;

    public FixedWidthSchemaRecord getRecordSchema() {
        return recordSchema;
    }

    public void setRecordSchema(FixedWidthSchemaRecord recordSchema) {
        this.recordSchema = recordSchema;
    }


}
