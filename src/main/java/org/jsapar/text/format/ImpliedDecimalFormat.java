package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Class that can be used to parse and format <i>implied decimals</i>.
 * The text representation is always an integer but when parsing the decimal point is shifted left and when composing it is shifted right.
 */
public class ImpliedDecimalFormat implements Format<BigDecimal> {

    private final int decimals;
    private final java.text.DecimalFormat integerFormat = new java.text.DecimalFormat("0");

    /**
     * @param decimals Number of decimals to imply
     */
    public ImpliedDecimalFormat(int decimals) {
        this.decimals = decimals;
    }

    /**
     * @return The number of decimals to imply.
     */
    public int getDecimals() {
        return decimals;
    }

    /**
     * Formats supplied object.
     * @param o The object to format. Can be of type BigDecimal, BigInteger, Number or a string containing a decimal value.
     * @return The formatted string.
     */
    @Override
    public String format(Object o) {
        BigDecimal value;
        if(o instanceof BigDecimal){
            value = (BigDecimal) o;
        }
        else if (o instanceof BigInteger){
            value = new BigDecimal((BigInteger) o);
        }
        else if (o instanceof Number){
            value = BigDecimal.valueOf(((Number) o).doubleValue());
        }
        else if (o instanceof String){
            value = new BigDecimal((String)o);
        }
        else{
            throw new IllegalArgumentException("Unable to format an implied decimal value from " + o + ". Unsupported type.");
        }
        value = value.movePointRight(decimals);
        return integerFormat.format(value);
    }

    @Override
    public String toString() {
        return "Implied decimal with " + decimals + " decimals";
    }

    @Override
    public CellType cellType() {
        return CellType.DECIMAL;
    }

    @Override
    public BigDecimal parse(CharSequence s) {
        return new BigDecimal(s).movePointLeft(decimals);
    }

}
