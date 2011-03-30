/**
 * 
 */
package org.jsapar.schema;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * @author stejon0
 *
 */
public class BooleanFormat extends Format {
    
    String trueValue;
    String falseValue;

    /**
     * 
     */
    private static final long serialVersionUID = -281569113302316449L;

    /**
     * 
     */
    public BooleanFormat(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(Object toFormat, StringBuffer appendToBuffer, FieldPosition pos) {
        int startPos = appendToBuffer.length();
        String value="?";
        if(toFormat.equals(Boolean.TRUE))
            value = trueValue;
        else if (toFormat.equals(Boolean.FALSE))
            value = falseValue;
        else
            throw new IllegalArgumentException("Only boolean objects can be formatted whith this formatter.");

        appendToBuffer.append(value);
        int endPos = appendToBuffer.length();
        pos.setBeginIndex(startPos);
        pos.setEndIndex(endPos);
        return appendToBuffer;
    }
    
    /**
     * Formats a boolean value.
     * @param value
     * @return
     */
    public String format(boolean value){
        return value ? trueValue : falseValue;
    }

    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(String toParse, ParsePosition pos) {
        toParse = toParse.substring(pos.getIndex());
        if(toParse.startsWith(trueValue)){
            pos.setIndex(pos.getIndex() + trueValue.length());
            return Boolean.TRUE;
        }
        if(toParse.startsWith(falseValue)){
            pos.setIndex(pos.getIndex() + falseValue.length());
            return Boolean.FALSE;
        }
        pos.setErrorIndex(pos.getIndex());
        return null;
    }
    
    /**
     * @param toParse
     * @return true or false depending on value to parse.
     */
    public boolean parse(String toParse){
        if(toParse.equals(trueValue))
            return true;
        else if(toParse.equals(falseValue))
            return false;
        else 
            throw new NumberFormatException("Faled to parse [" + toParse + "] to boolean value only [" + trueValue +"] or [" + falseValue + "] is allowed.");
    }

}
