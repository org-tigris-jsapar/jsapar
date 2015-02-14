package org.jsapar.schema.record;

import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaCell.Alignment;


public class FixedWidthControllCellRecordDescription extends RecordDescription{
    
    private int controlCellLength;
    private FixedWidthSchemaCell.Alignment controlCellAlignment = Alignment.LEFT;
    private boolean writeControlCell = true;

    /**
     * @return the writeControlCell
     */
    public boolean isWriteControlCell() {
        return writeControlCell;
    }

    /**
     * @param writeControlCell the writeControlCell to set
     */
    public void setWriteControlCell(boolean writeControlCell) {
        this.writeControlCell = writeControlCell;
    }

    public int getControlCellLength() {
        return controlCellLength;
    }

    public void setControlCellLength(int controlCellLength) {
        this.controlCellLength = controlCellLength;
    }

    public FixedWidthSchemaCell.Alignment getControlCellAlignment() {
        return controlCellAlignment;
    }

    public void setControlCellAlignment(FixedWidthSchemaCell.Alignment controlCellAlignment) {
        this.controlCellAlignment = controlCellAlignment;
    }
    

}
