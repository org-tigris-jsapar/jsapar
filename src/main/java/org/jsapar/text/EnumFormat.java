package org.jsapar.text;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Format class that can be used to parse or format enum values based on the value.
 *
 */
public class EnumFormat<E extends Enum<E> > implements Format<E> {
    private final boolean ignoreCase;
    private Map<String, E> enumByValue=new HashMap<>();
    private Map<String, E> enumByUValue=new HashMap<>();
    private Map<E, String> valueByEnum=new HashMap<>();
    private final Class<E> enumClass;



    /**
     * Creates a default enum format where values are the same as the Enum constants.
     * @param enumClass The enum class to use.
     * @param ignoreCase If true, the case is ignored while parsing.
     */
    @SuppressWarnings("WeakerAccess")
    public EnumFormat(Class<E> enumClass, boolean ignoreCase) {
        this.enumClass = enumClass;
        this.ignoreCase = ignoreCase;
        Arrays.stream(enumClass.getEnumConstants())
                .peek(v -> enumByValue.put(v.name(), v))
                .forEach(v -> valueByEnum.put(v, v.name()));
    }

    @Override
    public String toString() {
        return
                "Enum class=" + enumClass + (ignoreCase ? ", IGNORE CASE, " : "") +
                        ", valid values=" + valueByEnum.keySet();
    }

    public void putEnumValueIfAbsent(String value, String enumConstantName){
        E enumConstant = enumByValue.get(enumConstantName);
        if(enumConstant == null)
            throw new IllegalArgumentException("The enum constant name " + enumConstantName + " is not a valid value of the enum " + enumClass.getName());
        putEnumValueIfAbsent(value, enumConstant);
    }

    /**
     * Associates a new string value with supplied enum constant, both from text to enum and from enum to text. If a
     * value already exists in either direction, the new value is ignored for that direction.
     * @param value  The string value
     * @param enumConstant The enum constant
     */
    private void putEnumValueIfAbsent(String value, E enumConstant){
        this.enumByValue.putIfAbsent(value, enumConstant);
        this.enumByUValue.putIfAbsent(value.toUpperCase(), enumConstant);
        this.valueByEnum.putIfAbsent(enumConstant, value);
    }


    /**
     * Formats an enum value.
     *
     * @param value The value to format
     * @return the string value that represents the supplied enum value.
     */
    @Override
    public String format(Object value) {
        if(value instanceof Enum){
            return valueByEnum.get(value);
        }
        if(value instanceof String && enumByValue.containsKey(value)) {
            return (String) value;
        }
        throw new IllegalArgumentException("Unable to format enum value from " + value + " of type " + value.getClass());
    }



    /**
     * @param toParse The value to parse
     * @return true or false depending on value to parse.
     */
    @Override
    public E parse(String toParse) throws ParseException {
        E enumValue = enumByValue.get(toParse);
        if(enumValue != null)
            return enumValue;

        if(ignoreCase){
            enumValue = enumByUValue.get(toParse.toUpperCase());
            if(enumValue != null)
                return enumValue;
        }

        throw new ParseException("There is no enum constant matching the value '" + toParse + "' for  enum class " + enumClass.getName(), 0 );
    }

    public Collection<String> textValues(){
        return enumByValue.keySet();
    }

    public E enumByTextValue(String value){
        return enumByValue.get(value);
    }

    /**
     * @return Number of allowed possibilities.
     */
    public int numberOfTextValues() {
        return enumByValue.size();
    }

    public Class<? extends E> getEnumClass() {
        return enumClass;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }
}
