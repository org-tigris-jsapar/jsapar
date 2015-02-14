package org.jsapar.schema;

import org.jsapar.schema.record.RecordDescription;

public class FixedWidthSchemaRecordCell extends FixedWidthSchemaCell {
    
    private RecordDescription record;

    public FixedWidthSchemaRecordCell(String sName, int nLength, Alignment alignment) {
        super(sName, nLength, alignment);
    }

    public FixedWidthSchemaRecordCell(String sName, int nLength) {
        super(sName, nLength);
    }

    public FixedWidthSchemaRecordCell(String sName, int nLength, SchemaCellFormat cellFormat) {
        super(sName, nLength, cellFormat);
    }

    public FixedWidthSchemaRecordCell(int nLength) {
        super(nLength);
    }

    /**
     * @return the record
     */
    public RecordDescription getRecord() {
        return record;
    }

    /**
     * @param record the record to set
     */
    public void setRecord(RecordDescription record) {
        this.record = record;
    }

    
}
