package org.jsapar.text;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Format class that can be used to parse or format enum values based on the value.
 * Use builder class to build instances.
 * <p>
 * Example 1:
 * <p>
 * {@code EnumFormat.builder(MyEnum.class).build();}
 * <p>
 * Example 2:
 * <p>
 * {@code EnumFormat.builder(Weekdays.class).withIgnoreCase(true).withValue("fr", Weekdays.FRIDAY).build();}
 */
public class EnumFormat<E extends Enum<E>> implements Format<E> {
    private final boolean        ignoreCase;
    private       Map<String, E> enumByValue  = new HashMap<>();
    private       Map<String, E> enumByUValue = new HashMap<>();
    private       Map<E, String> valueByEnum  = new HashMap<>();
    private final Class<E>       enumClass;

    /**
     * Creates a default enum format where values are the same as the Enum constants.
     * Deprecated. Use builder instead.
     * @param enumClass The enum class to use.
     * @param ignoreCase If true, the case is ignored while parsing.
     */
    // TODO make private
    @Deprecated
    public EnumFormat(Class<E> enumClass, boolean ignoreCase) {
        this.enumClass = enumClass;
        this.ignoreCase = ignoreCase;
        Arrays.stream(enumClass.getEnumConstants())
                .forEach(v -> putEnumValueIfAbsent(v.name(), v));
    }

    private EnumFormat(Builder<E> builder) {
        this(builder.enumClass, builder.ignoreCase);
        builder.enumByValue.forEach(this::putEnumValue);
    }

    /**
     * Creates a builder that builds EnumFormat instances.
     * @param enumClass  The enum class.
     * @param <E> The enum type
     * @return A newly created enum format builder for supplied enum class.
     */
    public static <E extends Enum<E> > Builder<E> builder(Class<E> enumClass) {
        return new Builder<>(enumClass);
    }

    /**
     * Builder that builds EnumFormat instances.
     * @param <E>
     */
    public static class Builder<E extends Enum<E> >{
        private final Class<E> enumClass;
        private boolean ignoreCase;
        private Map<String, E> enumByValue=new HashMap<>();

        private Builder(Class<E> enumClass) {
            this.enumClass = enumClass;
        }

        /**
         * @param ignoreCase If true, upper/lower case is ignored in the text value.
         * @return This builder instance.
         */
        public Builder<E> withIgnoreCase(boolean ignoreCase){
            this.ignoreCase = ignoreCase;
            return this;
        }

        /**
         * Maps an enum value to a text value.
         * @param textValue The text value
         * @param value The enum value
         * @return This builder instance.
         */
        public Builder<E> withValue(String textValue, E value){
            this.enumByValue.put(textValue, value);
            return this;
        }

        public EnumFormat<E> build(){
            return new EnumFormat<>(this);
        }
    }

    @Override
    public String toString() {
        return
                "Enum class=" + enumClass + (ignoreCase ? ", IGNORE CASE, " : "") +
                        ", valid values=" + valueByEnum.keySet();
    }

    @Deprecated
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
     * Associates a new string value with supplied enum constant, both from text to enum and from enum to text. If a
     * value already exists in either direction, the old value will be overwritten.
     * @param value  The string value
     * @param enumConstant The enum constant
     */
    private void putEnumValue(String value, E enumConstant){
        this.enumByValue.put(value, enumConstant);
        this.enumByUValue.put(value.toUpperCase(), enumConstant);
        this.valueByEnum.put(enumConstant, value);
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
            return valueByEnum.get((E)value);
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
