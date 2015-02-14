package org.jsapar.schema;

public class CsvSchemaRecordCell extends CsvSchemaCell {
    
    Record record;

    public CsvSchemaRecordCell(String sName) {
        super(sName);
    }

    public CsvSchemaRecordCell(String sName, SchemaCellFormat cellFormat) {
        super(sName, cellFormat);
    }

    public CsvSchemaRecordCell() {
    }

}
