package org.jsapar.parse;

import java.io.Reader;

import org.jsapar.JSaParException;
import org.jsapar.parse.csv.CsvControlCellParser;
import org.jsapar.parse.csv.CsvParser;
import org.jsapar.parse.fixed.FixedWidthControlCellParser;
import org.jsapar.parse.fixed.FixedWidthParser;
import org.jsapar.parse.xml.XmlParser;
import org.jsapar.schema.CsvControlCellSchema;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.FixedWidthControlCellSchema;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.XmlSchema;

public class SchemaParserFactory {

    public SchemaParserFactory() {
    }
    
    public SchemaParser makeParser(ParseSchema schema, Reader reader) throws JSaParException{
        if(schema instanceof CsvSchema){
            if(schema instanceof CsvControlCellSchema){
                return new CsvControlCellParser(reader, (CsvControlCellSchema)schema);
            }
            return new CsvParser(reader, (CsvSchema)schema);
        }
        if(schema instanceof FixedWidthSchema){
            if(schema instanceof FixedWidthControlCellSchema){
                return new FixedWidthControlCellParser(reader, (FixedWidthControlCellSchema)schema);
            }
            return new FixedWidthParser(reader, (FixedWidthSchema)schema);
        }
        if(schema instanceof XmlSchema){
            return new XmlParser(reader, (XmlSchema) schema);
        }
        
        throw new JSaParException("Unknown schema type. Unable to create parser class for it.");
    }

}
