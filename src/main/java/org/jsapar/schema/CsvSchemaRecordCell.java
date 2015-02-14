package org.jsapar.schema;

import org.jsapar.schema.record.RecordDescription;

public class CsvSchemaRecordCell extends CsvSchemaCell {
    
    private RecordDescription record;

    public CsvSchemaRecordCell(String sName) {
        super(sName);
    }

    public CsvSchemaRecordCell(String sName, SchemaCellFormat cellFormat) {
        super(sName, cellFormat);
    }

    public CsvSchemaRecordCell() {
    }

}
