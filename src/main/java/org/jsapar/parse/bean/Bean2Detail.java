package org.jsapar.parse.bean;

import org.jsapar.compose.bean.BeanComposeException;
import org.jsapar.error.BeanException;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.bean.reflect.PropertyDescriptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

/**
 * Internal class that adds detail beans to a collection or map property of a master bean.
 * @since 2.5
 */
public final class Bean2Detail {

    /** Classes to create when a collection property is null, in order of preference. */
    private static final List<Class<?>> COLLECTION_CLASSES = List.of(
            ArrayList.class, LinkedHashSet.class, TreeSet.class, ArrayDeque.class);
    /** Classes to create when a map property is null, in order of preference. */
    private static final List<Class<?>> MAP_CLASSES = List.of(
            LinkedHashMap.class, TreeMap.class, ConcurrentHashMap.class, ConcurrentSkipListMap.class);

    private final String detailLineType;
    private final PropertyDescriptor propertyDescriptor;
    /** Null for collections. */
    private final String keyCellName;
    /** The declared key type of a map property, Object if unknown. Null for collections. */
    private final Class<?> keyType;
    /** Null if no instance can be created, in which case the property needs to be initialized by the bean. */
    private final Supplier<Object> containerFactory;
    private final Class<?> containerType;

    private Bean2Detail(String detailLineType,
                        PropertyDescriptor propertyDescriptor,
                        String keyCellName,
                        Class<?> keyType,
                        Class<?> containerType,
                        Supplier<Object> containerFactory) {
        this.detailLineType = detailLineType;
        this.propertyDescriptor = propertyDescriptor;
        this.keyCellName = keyCellName;
        this.keyType = keyType;
        this.containerType = containerType;
        this.containerFactory = containerFactory;
    }

    /**
     * @param detailLineType     The line type of the detail lines.
     * @param propertyDescriptor The collection or map property of the master bean.
     * @param keyCellName        The cell of the detail line to use as key for a map property. Null or empty for a
     *                           collection property.
     * @return A new instance.
     * @throws BeanException If the property is not a collection or map, or if the key cell does not fit the property.
     */
    static Bean2Detail of(String detailLineType, PropertyDescriptor propertyDescriptor, String keyCellName) {
        Method getter = propertyDescriptor.getReadMethod();
        if (getter == null)
            throw new BeanException("The property " + propertyDescriptor.getName()
                    + " for detail lines of type " + detailLineType + " has no getter method.");
        Class<?> type = getter.getReturnType();
        boolean hasKey = keyCellName != null && !keyCellName.isEmpty();
        if (Map.class.isAssignableFrom(type)) {
            if (!hasKey)
                throw new BeanException("The property " + propertyDescriptor.getName()
                        + " is a Map so a key cell is needed for detail lines of type " + detailLineType + ".");
            return new Bean2Detail(detailLineType, propertyDescriptor, keyCellName,
                    mapKeyType(getter.getGenericReturnType()), type, containerFactory(type, MAP_CLASSES));
        }
        if (Collection.class.isAssignableFrom(type)) {
            if (hasKey)
                throw new BeanException("The property " + propertyDescriptor.getName()
                        + " is not a Map so it cannot have a key cell for detail lines of type " + detailLineType + ".");
            return new Bean2Detail(detailLineType, propertyDescriptor, null, null, type,
                    containerFactory(type, COLLECTION_CLASSES));
        }
        throw new BeanException("The property " + propertyDescriptor.getName() + " for detail lines of type "
                + detailLineType + " needs to be a Collection or a Map but is " + type.getName() + ".");
    }

    /**
     * @param type             The declared type of the property.
     * @param defaultClasses   Classes to choose from, in order of preference.
     * @return A factory of instances that can be assigned to the property, or null if there is no way to create one.
     * Then the bean needs to initialize the property itself, which is checked first when a detail line is added.
     */
    private static Supplier<Object> containerFactory(Class<?> type, List<Class<?>> defaultClasses) {
        for (Class<?> defaultClass : defaultClasses) {
            if (type.isAssignableFrom(defaultClass))
                return constructorFactory(defaultClass);
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
            return null;
        return constructorFactory(type);
    }

    private static Supplier<Object> constructorFactory(Class<?> type) {
        Constructor<?> constructor;
        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
        return () -> {
            try {
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new BeanException("Failed to create an instance of " + type.getName(), e);
            }
        };
    }

    /**
     * @param mapType The generic type of a map property.
     * @return The class of the key type argument, or Object if it cannot be found, for instance for a raw Map or a
     * subclass of Map without type arguments.
     */
    private static Class<?> mapKeyType(Type mapType) {
        if (mapType instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 2) {
                Class<?> keyClass = rawClass(typeArguments[0]);
                if (keyClass != null)
                    return keyClass;
            }
        }
        return Object.class;
    }

    private static Class<?> rawClass(Type type) {
        if (type instanceof Class<?> c)
            return c;
        if (type instanceof ParameterizedType parameterizedType)
            return rawClass(parameterizedType.getRawType());
        if (type instanceof WildcardType wildcardType)
            return rawClass(wildcardType.getUpperBounds()[0]);
        if (type instanceof TypeVariable<?> typeVariable)
            return rawClass(typeVariable.getBounds()[0]);
        return null;
    }

    /**
     * Adds a detail bean to the collection or map property of the master bean. Creates the collection or map if the
     * property is null.
     * @param masterBean The bean of the master line.
     * @param detailLine The detail line, used for the key of a map.
     * @param detailBean The bean of the detail line.
     * @throws BeanComposeException If the key cell is missing, the key already exists in the map or the collection or
     * map does not accept the detail bean, for instance because it is immutable.
     * @throws InvocationTargetException If the getter or setter fails.
     * @throws IllegalAccessException If the getter or setter cannot be accessed.
     */
    @SuppressWarnings("unchecked")
    public void add(Object masterBean, Line detailLine, Object detailBean) throws BeanComposeException, InvocationTargetException, IllegalAccessException {
        Object container = propertyDescriptor.getReadMethod().invoke(masterBean);
        if (container == null) {
            Method setter = propertyDescriptor.getWriteMethod();
            if (setter == null)
                throw new BeanComposeException("The property " + propertyDescriptor.getName() + " of "
                        + masterBean.getClass().getName() + " is null and has no setter method.");
            if (containerFactory == null)
                throw new BeanComposeException("The property " + propertyDescriptor.getName() + " of "
                        + masterBean.getClass().getName() + " is null and no instance of " + containerType.getName()
                        + " can be created. Initialize the property in the bean.");
            container = containerFactory.get();
            setter.invoke(masterBean, container);
        }
        try {
            if (keyCellName == null) {
                ((Collection<Object>) container).add(detailBean);
                return;
            }
            Cell<?> keyCell = detailLine.getNonEmptyCell(keyCellName).orElseThrow(() ->
                    new BeanComposeException("The detail line has no value in key cell " + keyCellName + "."));
            Object key = toKey(keyCell);
            if (((Map<Object, Object>) container).putIfAbsent(key, detailBean) != null)
                throw new BeanComposeException("Duplicate key " + key + " in cell " + keyCellName + " for property "
                        + propertyDescriptor.getName() + ". Only the first detail line is kept.");
        } catch (UnsupportedOperationException | ClassCastException | NullPointerException e) {
            // For instance an immutable collection, or a sorted collection with elements that cannot be compared.
            throw new BeanComposeException("Unable to add the detail bean to property " + propertyDescriptor.getName()
                    + " of " + masterBean.getClass().getName() + ": " + e, e);
        }
    }

    /**
     * @param keyCell The key cell of the detail line.
     * @return The value of the cell converted to the declared key type of the map property: numbers are converted
     * to other number types if the value fits, and any value is converted to its text for a String key.
     * @throws BeanComposeException If the value cannot be converted to the key type.
     */
    private Object toKey(Cell<?> keyCell) throws BeanComposeException {
        Object value = keyCell.getValue();
        if (keyType.isInstance(value))
            return value;
        if (keyType == String.class)
            return keyCell.getStringValue();
        try {
            if (value instanceof Number number) {
                Object key = convertNumber(number);
                if (key != null)
                    return key;
            }
        } catch (ArithmeticException e) {
            throw new BeanComposeException(keyMismatchMessage(value), e);
        }
        throw new BeanComposeException(keyMismatchMessage(value));
    }

    private String keyMismatchMessage(Object value) {
        return "The value " + value + " of key cell " + keyCellName + " is of type " + value.getClass().getName()
                + " which cannot be used as key of type " + keyType.getName() + " for property "
                + propertyDescriptor.getName() + ".";
    }

    /**
     * @param number The number to convert.
     * @return The number converted to the key type, or null if the key type is not a supported number type.
     * @throws ArithmeticException If the number does not fit the key type.
     */
    private Object convertNumber(Number number) {
        if (keyType == Long.class)
            return exactLong(number);
        if (keyType == Integer.class)
            return Math.toIntExact(exactLong(number));
        if (keyType == Short.class)
            return (short) exactInRange(number, Short.MIN_VALUE, Short.MAX_VALUE);
        if (keyType == Byte.class)
            return (byte) exactInRange(number, Byte.MIN_VALUE, Byte.MAX_VALUE);
        if (keyType == BigInteger.class)
            return toBigDecimal(number).toBigIntegerExact();
        if (keyType == BigDecimal.class)
            return toBigDecimal(number);
        if (keyType == Double.class)
            return number.doubleValue();
        if (keyType == Float.class)
            return number.floatValue();
        return null;
    }

    private static long exactInRange(Number number, long min, long max) {
        long value = exactLong(number);
        if (value < min || value > max)
            throw new ArithmeticException("Value out of range: " + value);
        return value;
    }

    private static long exactLong(Number number) {
        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte)
            return number.longValue();
        return toBigDecimal(number).longValueExact();
    }

    private static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal bigDecimal)
            return bigDecimal;
        if (number instanceof BigInteger bigInteger)
            return new BigDecimal(bigInteger);
        if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte)
            return BigDecimal.valueOf(number.longValue());
        return new BigDecimal(number.toString());
    }

    /**
     * @param masterBean The bean of the master line.
     * @return The detail beans of the master bean: the collection itself, or the values of the map, in their
     * iteration order. Empty if the property is null.
     * @throws InvocationTargetException If the getter fails.
     * @throws IllegalAccessException If the getter cannot be accessed.
     */
    public Collection<?> elements(Object masterBean) throws InvocationTargetException, IllegalAccessException {
        Object container = propertyDescriptor.getReadMethod().invoke(masterBean);
        if (container == null)
            return Collections.emptyList();
        return keyCellName == null ? (Collection<?>) container : ((Map<?, ?>) container).values();
    }

    /**
     * @return The line type of the detail lines.
     */
    public String getDetailLineType() {
        return detailLineType;
    }

    /**
     * @return The name of the collection or map property of the master bean.
     */
    public String getPropertyName() {
        return propertyDescriptor.getName();
    }

    PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    String getKeyCellName() {
        return keyCellName;
    }
}
