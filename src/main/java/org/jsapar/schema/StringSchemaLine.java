package org.jsapar.schema;

import java.util.function.Function;

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

        /**
         * Convenience method that creates cell builder, applies defaults, calls the provided function before using that builder to create a schema cell.
         * @param cellName The name of the cell.
         * @param cellBuilderHandler The function that gets called with the created builder.
         * @return This builder instance.
         * @see SchemaLine.Builder#withCell(SchemaCell)
         */
        public StringSchemaLine.Builder withCell(String cellName, Function<StringSchemaCell.Builder<?>, StringSchemaCell.Builder<?>> cellBuilderHandler){
            return this.withCell(cellBuilderHandler.apply(StringSchemaCell.builder(cellName).applyDefaultsFrom(this)).build());
        }

        public Builder withCell(String cellName){
            return withCell(cellName, c->c);
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
