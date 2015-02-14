package org.jsapar.schema;

public class FixedWidthControllCellRecord extends FixedWidthRecord{
    
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
    

}
