package org.jsapar.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Format class that can be used to parse or format enum values based on the value.
 *
 */
public class EnumFormat extends Format {
    private final boolean ignoreCase;
    private Map<String, Enum> enumByValue=new HashMap<>();
    private Map<Object, String> valueByEnum=new HashMap<>();
    private final Class<? extends Enum> enumClass;


    /**
     *
     */
    private static final long serialVersionUID = -281569113302316449L;

    /**
     * Creates a default enum format where values are the same as the Enum constants.
     * @param enumClass The enum class to use.
     * @param ignoreCase
     */
    @SuppressWarnings("WeakerAccess")
    public EnumFormat(Class<? extends Enum> enumClass, boolean ignoreCase) {
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
        Enum enumConstant = enumByValue.get(enumConstantName);
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
    public void putEnumValueIfAbsent(String value, Enum enumConstant){
        this.enumByValue.putIfAbsent(value, enumConstant);
        this.valueByEnum.putIfAbsent(enumConstant, value);
    }


    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(Object toFormat, StringBuffer appendToBuffer, FieldPosition pos) {
        if(!enumClass.isAssignableFrom(toFormat.getClass()))
            throw new ClassCastException("Unable to cast object to enum class " + enumClass.getName());
        int startPos = appendToBuffer.length();
        appendToBuffer.append(valueByEnum.get(toFormat));
        int endPos = appendToBuffer.length();
        pos.setBeginIndex(startPos);
        pos.setEndIndex(endPos);
        return appendToBuffer;
    }


    /**
     * Formats an enum value.
     *
     * @param value The value to format
     * @return the string value that represents the supplied enum value.
     */
    public String format(Enum value) {
        return valueByEnum.get(value);
    }

    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(String toParse, ParsePosition pos) {
        int endIndex = toParse.indexOf(' ', pos.getIndex());
        endIndex = endIndex > 0 ? endIndex : toParse.length();
        String value = toParse.substring(pos.getIndex(), endIndex);
        Enum enumValue = enumByValue.get(value);
        if(enumValue != null) {
            pos.setIndex(endIndex);
            return enumValue;
        }
        if(!ignoreCase){
            pos.setErrorIndex(pos.getIndex());
            return null;
        }
        return enumByValueEntries().filter(e->toParse.regionMatches(ignoreCase, pos.getIndex(), e.getKey(), 0, e.getKey().length()))
                .peek(e->pos.setIndex(pos.getIndex() + e.getKey().length()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> {
                            pos.setErrorIndex(pos.getIndex());
                            return null;
                        });
    }

    @Override
    public Object parseObject(String toParse) throws ParseException {
        Enum enumValue = enumByValue.get(toParse);
        if(enumValue != null)
            return enumValue;

        if(!ignoreCase)
            throw new ParseException("There is no enum constant matching the value '" + toParse + "' for  enum class " + enumClass.getName(), 0 );

        return enumByValueEntries().filter(e->toParse.regionMatches(true, 0, e.getKey(), 0, e.getKey().length()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() ->
                    new ParseException("There is no enum constant matching the value '" + toParse + "' for  enum class " + enumClass.getName(), 0 )
                );
    }

    public Stream<Map.Entry<String, Enum>> enumByValueEntries() {
        return enumByValue.entrySet().stream();
    }

    /**
     * @param toParse The value to parse
     * @return true or false depending on value to parse.
     */
    @SuppressWarnings("unchecked")
    public <E extends Enum> E parse(String toParse) throws ParseException {
        return (E)parseObject(toParse);
    }

    /**
     * @return Number of allowed possibilities.
     */
    public int numberOfTextValues() {
        return enumByValue.size();
    }

    public Class<? extends Enum> getEnumClass() {
        return enumClass;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }
}
