package org.jsapar.schema;

public class SchemaRecord {

    private static final String NOT_SET              = "";

    private String              recordType             = NOT_SET;
    private String              recordTypeControlValue = NOT_SET;
    
    /**
     * @return the recordType
     */
    public String getRecordType() {
        return recordType;
    }
    
    /**
     * 
     */
    public SchemaRecord() {
        super();
    }

    /**
     * @param recordType
     */
    public SchemaRecord(String recordType) {
        super();
        this.recordType = recordType;
    }


    /**
     * @param recordType the recordType to set
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    /**
     * @return the recordTypeControlValue
     */
    public String getRecordTypeControlValue() {
        return recordTypeControlValue;
    }
    /**
     * @param recordTypeControlValue the recordTypeControlValue to set
     */
    public void setRecordTypeControlValue(String recordTypeControlValue) {
        this.recordTypeControlValue = recordTypeControlValue;
    }
    
}
