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
import java.util.*;
import java.util.function.Supplier;

/**
 * Internal class that adds detail beans to a collection or map property of a master bean.
 * @since 2.5
 */
public final class Bean2Detail {

    private final String detailLineType;
    private final PropertyDescriptor propertyDescriptor;
    /** Null for collections. */
    private final String keyCellName;
    private final Supplier<Object> containerFactory;

    private Bean2Detail(String detailLineType, PropertyDescriptor propertyDescriptor, String keyCellName, Supplier<Object> containerFactory) {
        this.detailLineType = detailLineType;
        this.propertyDescriptor = propertyDescriptor;
        this.keyCellName = keyCellName;
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
            return new Bean2Detail(detailLineType, propertyDescriptor, keyCellName, containerFactory(type, LinkedHashMap::new, LinkedHashMap.class));
        }
        if (Collection.class.isAssignableFrom(type)) {
            if (hasKey)
                throw new BeanException("The property " + propertyDescriptor.getName()
                        + " is not a Map so it cannot have a key cell for detail lines of type " + detailLineType + ".");
            if (type.isAssignableFrom(ArrayList.class))
                return new Bean2Detail(detailLineType, propertyDescriptor, null, ArrayList::new);
            return new Bean2Detail(detailLineType, propertyDescriptor, null, containerFactory(type, LinkedHashSet::new, LinkedHashSet.class));
        }
        throw new BeanException("The property " + propertyDescriptor.getName() + " for detail lines of type "
                + detailLineType + " needs to be a Collection or a Map but is " + type.getName() + ".");
    }

    private static Supplier<Object> containerFactory(Class<?> type, Supplier<Object> defaultFactory, Class<?> defaultClass) {
        if (type.isAssignableFrom(defaultClass))
            return defaultFactory;
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
            throw new BeanException("Unable to create an instance of " + type.getName() + " for detail lines.");
        try {
            Constructor<?> constructor = type.getConstructor();
            return () -> {
                try {
                    return constructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new BeanException("Failed to create an instance of " + type.getName(), e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new BeanException("The class " + type.getName() + " needs a public default constructor to hold detail lines.", e);
        }
    }

    /**
     * Adds a detail bean to the collection or map property of the master bean. Creates the collection or map if the
     * property is null.
     * @param masterBean The bean of the master line.
     * @param detailLine The detail line, used for the key of a map.
     * @param detailBean The bean of the detail line.
     * @throws BeanComposeException If the key cell is missing or the key already exists in the map.
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
            container = containerFactory.get();
            setter.invoke(masterBean, container);
        }
        if (keyCellName == null) {
            ((Collection<Object>) container).add(detailBean);
            return;
        }
        Object key = detailLine.getNonEmptyCell(keyCellName).map(Cell::getValue).orElseThrow(() ->
                new BeanComposeException("The detail line has no value in key cell " + keyCellName + "."));
        if (((Map<Object, Object>) container).putIfAbsent(key, detailBean) != null)
            throw new BeanComposeException("Duplicate key " + key + " in cell " + keyCellName + " for property "
                    + propertyDescriptor.getName() + ". Only the first detail line is kept.");
    }

    /**
     * @return The line type of the detail lines.
     */
    public String getDetailLineType() {
        return detailLineType;
    }

    PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    String getKeyCellName() {
        return keyCellName;
    }
}
