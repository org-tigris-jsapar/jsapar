package org.jsapar.schema;

public class StringSchemaLine extends SchemaLine<StringSchemaCell> {

    private StringSchemaLine(Builder builder) {
        super(builder);
    }

    public static Builder builder(String lineType){
        return new Builder(lineType);
    }

    public static class Builder extends SchemaLine.Builder<StringSchemaCell, StringSchemaLine, StringSchemaLine.Builder>{

        public Builder(String lineType) {
            super(lineType);
        }

        public Builder withCell(String cellName){
            return this.withCell(StringSchemaCell.builder(cellName).build());
        }

        public Builder withCells(String ... cellNames){
            for(String cellName: cellNames){
                withCell(cellName);
            }
            return this;
        }

        @Override
        public StringSchemaLine build() {
            return new StringSchemaLine(this);
        }
    }

}
