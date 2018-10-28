package org.jsapar.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Format class that can be used to parse or format enum values based on the value.
 *
 */
public class EnumFormat extends Format {
    private Class<? extends Enum> enumClass;
    private final boolean ignoreCase = false;
    private Map<String, Enum> enumByValue=new HashMap<>();
    private Map<Enum, String> valueByEnum=new HashMap<>();


    /**
     *
     */
    private static final long serialVersionUID = -281569113302316449L;

    /**
     * Creates a default enum format where values are the same as the Enum constants.
     * @param enumClass The enum class to use.
     */
    @SuppressWarnings("WeakerAccess")
    public EnumFormat(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
        Arrays.stream(enumClass.getEnumConstants())
                .peek(v -> enumByValue.put(v.name(), v))
                .forEach(v -> valueByEnum.put(v, v.name()));
    }



    /* (non-Javadoc)
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(Object toFormat, StringBuffer appendToBuffer, FieldPosition pos) {
        int startPos = appendToBuffer.length();
        appendToBuffer.append(valueByEnum.get((Enum)toFormat));
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
        int endIndex = Math.max(toParse.indexOf(' ', pos.getIndex()), toParse.length());
        String value = toParse.substring(pos.getIndex(), endIndex);
        Enum enumObject = enumByValue.get(value);
        if(enumObject != null) {
            pos.setIndex(endIndex);
            return enumObject;
        }
        if(!ignoreCase){
            pos.setErrorIndex(pos.getIndex());
            return null;
        }
        return enumByValue.entrySet().stream().filter(e->toParse.regionMatches(ignoreCase, pos.getIndex(), e.getKey(), 0, e.getKey().length()))
                .peek(e->pos.setIndex(pos.getIndex() + e.getKey().length()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> {
                            pos.setErrorIndex(pos.getIndex());
                            return null;
                        });
    }

    /**
     * @param toParse The value to parse
     * @return true or false depending on value to parse.
     */
    @SuppressWarnings("unchecked")
    public <E extends Enum> E parse(String toParse) throws ParseException {
        return (E)parseObject(toParse);
    }

}
