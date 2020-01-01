package org.jsapar.schema;

import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.text.TextParseConfig;

import java.io.Reader;

public class StringSchema extends Schema<StringSchemaLine> {

    private StringSchema(Builder builder) {
        super(builder);
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder extends Schema.Builder<StringSchemaLine, StringSchema, Builder>{

        private Builder(){
        }

        @Override
        public StringSchema build() {
            return new StringSchema(this);
        }
    }
}
