/**
 * 
 */
package org.jsapar;

import org.jsapar.schema.Schema;

/**
 * @author stejon0
 *
 */
public class FilterConverterMain extends ConverterMain {

    /**
     * 
     */
    public FilterConverterMain() {
    }
    
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        ConverterMain main = new FilterConverterMain();
        main.run(args);
    }



    /* (non-Javadoc)
     * @see org.jsapar.io.ConverterMain#makeConverter(org.jsapar.schema.Schema, org.jsapar.schema.Schema)
     */
    @Override
    protected Converter makeConverter(Schema inputSchema, Schema outputSchema) {
        return new FilterConverter(inputSchema, outputSchema);
    }

}
