package org.jsapar.parse.bean;

import org.jsapar.compose.bean.BeanComposeException;
import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.parse.bean.reflect.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Bean2Cell {

    private final String             cellName;
    private BeanPropertyMap    children;
    private PropertyDescriptor propertyDescriptor;
    private CellCreator        cellCreator;

    private Bean2Cell(String cellName) {
        this.cellName = cellName;
    }

    private Bean2Cell(String cellName, PropertyDescriptor propertyDescriptor) {
        this.cellName = cellName;
        this.propertyDescriptor = propertyDescriptor;
    }

    static Bean2Cell ofCellName(String cellName, PropertyDescriptor propertyDescriptor) {
        Bean2Cell bean2Cell = new Bean2Cell(cellName, propertyDescriptor);
        // Prepare the best way to create cell depending on return type
        bean2Cell.cellCreator = bean2Cell.makeCellCreator();
        return bean2Cell;
    }

    static Bean2Cell ofBaseProperty(PropertyDescriptor propertyDescriptor, BeanPropertyMap children) {
        // The name is not important here, just make sure there is no conflict with other names.
        Bean2Cell bean2Cell = new Bean2Cell("@@" + propertyDescriptor.getName());
        bean2Cell.propertyDescriptor = propertyDescriptor;
        bean2Cell.children = children;
        return bean2Cell;
    }

    String getCellName() {
        return cellName;
    }

    BeanPropertyMap getChildren() {
        return children;
    }

    PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public Cell<?> makeCell(Object object) throws InvocationTargetException, IllegalAccessException {
        return cellCreator.makeCell(object);
    }

    /**
     * Creates a cell creator instance suitable for this instance.
     *
     * @return a cell creator best fitted for the job depending on the return type of the bean property.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private CellCreator makeCellCreator() {
        Method f = propertyDescriptor.getReadMethod();
        if (f == null)
            throw new JSaParException("The property " + propertyDescriptor.getName() + " has no getter method.");

        Class<?> returnType = f.getReturnType();

        if (returnType.isAssignableFrom(String.class)) {
            return (bean) -> this.<String>makeCellByInvocation(bean, f, CellType.STRING, StringCell::new);
        } else if (returnType.isAssignableFrom(LocalDate.class)) {
            return (bean) -> this.makeCellByInvocation(bean, f, CellType.LOCAL_DATE, LocalDateCell::new);
        } else if (returnType.isAssignableFrom(LocalDateTime.class)) {
            return (bean) -> this.makeCellByInvocation(bean, f, CellType.LOCAL_DATE_TIME, LocalDateTimeCell::new);
        } else if (returnType.isAssignableFrom(LocalTime.class)) {
            return (bean) -> this.makeCellByInvocation(bean, f, CellType.LOCAL_TIME, LocalTimeCell::new);
        } else if (returnType.isAssignableFrom(ZonedDateTime.class)) {
            return (bean) -> this.makeCellByInvocation(bean, f, CellType.ZONED_DATE_TIME, ZonedDateTimeCell::new);
        } else if (returnType.isAssignableFrom(Instant.class)) {
            return (bean) -> this.makeCellByInvocation(bean, f, CellType.INSTANT, InstantCell::new);
        } else if (returnType.isAssignableFrom(Date.class)) {
            return (bean) -> this.<Date>makeCellByInvocation(bean, f, CellType.DATE, DateCell::new);
        } else if (returnType.isAssignableFrom(Calendar.class)) {
            return (bean) -> this.<Calendar>makeCellByInvocation(bean, f, CellType.DATE, (n, v)->new DateCell(n, v.getTime()));
        } else if (returnType.isAssignableFrom(Integer.TYPE) || returnType.isAssignableFrom(Integer.class) || returnType.isAssignableFrom(
                Byte.TYPE) || returnType.isAssignableFrom(Byte.class) || returnType.isAssignableFrom(Short.TYPE) || returnType.isAssignableFrom(
                Short.class) || returnType.isAssignableFrom(Long.TYPE) || returnType.isAssignableFrom(Long.class)) {
            return (bean) -> makeCellByInvocation(bean, f, CellType.INTEGER, IntegerCell::new);
        } else if (returnType.isAssignableFrom(Boolean.TYPE) || returnType.isAssignableFrom(Boolean.class)) {
            return (bean) -> makeCellByInvocation(bean, f, CellType.BOOLEAN, BooleanCell::new);
        } else if (returnType.isAssignableFrom(Float.TYPE) || returnType.isAssignableFrom(Float.class) || returnType.isAssignableFrom(
                Double.TYPE) || returnType.isAssignableFrom(Double.class)) {
            return (bean) -> makeCellByInvocation(bean, f, CellType.FLOAT, FloatCell::new);
        } else if (returnType.isAssignableFrom(BigDecimal.class)) {
            return (bean) -> this.<BigDecimal>makeCellByInvocation(bean, f, CellType.DECIMAL, BigDecimalCell::new);
        } else if (returnType.isAssignableFrom(BigInteger.class)) {
            return (bean) -> this.<BigInteger>makeCellByInvocation(bean, f, CellType.DECIMAL, BigDecimalCell::new);
        } else if (returnType.isAssignableFrom(Character.TYPE) || returnType.isAssignableFrom(Character.class)) {
            return (bean) -> this.makeCellByInvocation(bean, f, CellType.CHARACTER, CharacterCell::new);
        } else if (Enum.class.isAssignableFrom(returnType)){
            return (bean) -> this.<Enum>makeCellByInvocation(bean, f, CellType.ENUM, EnumCell::new);
        }
        return (bean) -> {
            Object value = f.invoke(bean);
            if (value != null)
                return new StringCell(cellName, String.valueOf(value));
            else
                return StringCell.emptyOf(cellName);
        };
    }

    @SuppressWarnings("unchecked")
    private <T> Cell<?> makeCellByInvocation(Object o, Method f, CellType cellType, BiFunction<String, T, Cell<?>> cellByValue) throws
            InvocationTargetException, IllegalAccessException {
        T value = (T) f.invoke(o);
        if(value == null)
            return new EmptyCell<>(cellName, cellType);
        return cellByValue.apply(cellName, value);
    }

    /**
     * Cell creator interface. Needed to be able to let makeCell method throw exception.
     */
    private interface CellCreator {
        Cell<?> makeCell(Object o) throws InvocationTargetException, IllegalAccessException;
    }

    private void assignProperty(Object bean, Cell<?> cell)
            throws InvocationTargetException, IllegalAccessException, BeanComposeException {
        Method setter = this.propertyDescriptor.getWriteMethod();
        if (setter == null)
            throw new BeanComposeException(
                    "The property " + propertyDescriptor.getName() + " of class " + children.getLineClass().getName()
                            + " has no setter method.");
        Class<?> paramType = setter.getParameterTypes()[0];
        setter.invoke(bean, customCast(paramType, cell));
    }

    @SuppressWarnings("unchecked")
    private Object customCast(Class<?> paramType, Cell<?> cell) throws BeanComposeException {
        Class<?> valueType = cell.getValue().getClass();
        Object value = cell.getValue();
        if (paramType.isAssignableFrom(valueType))
            return value;
        if (paramType == String.class) {
            return cell.getStringValue();
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            if (paramType == Integer.TYPE)
                return number.intValue();
            else if (paramType == Long.TYPE)
                return number.longValue();
            else if (paramType == Double.TYPE)
                return number.doubleValue();
            else if (paramType == Float.TYPE)
                return number.floatValue();
            else if (paramType == Short.TYPE)
                return number.shortValue();
            else if (paramType == Byte.TYPE)
                return number.byteValue();
            else if (paramType == Boolean.TYPE)
                return number.intValue() != 0;
            else if (paramType == Character.TYPE)
                return number.intValue();

        }
        // Will squeeze in first character of any datatype's string representation.
        else if (paramType == Character.TYPE) {
            if (value instanceof Character)
                return value;
            return cell.getStringValue().charAt(0);
        } else if (paramType == Boolean.TYPE) {
            if (value instanceof Boolean) {
                return value;
            }
        } else if (Enum.class.isAssignableFrom(paramType)) {
            //noinspection rawtypes
            return Enum.valueOf((Class<Enum>) paramType, cell.getStringValue());
        }
        throw new BeanComposeException(
                "Skipped assigning cell - The setter for property " + this.propertyDescriptor.getName()
                        + " could not be used to assign cell");
    }

    public void assign(Object bean, Cell<?> cell)
            throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        if(cell.isEmpty()) {
            return;
        }
        if (isLeaf()) {
            assignProperty(bean, cell);
            return;
        }
        Bean2Cell childBean2Cell = children.getBean2CellByName(cell.getName());
        if (childBean2Cell != null) {
            Method getter = this.propertyDescriptor.getReadMethod();
            if (getter == null)
                throw new BeanComposeException(
                        "The property " + propertyDescriptor.getName() + " of class " + children.getLineClass()
                                .getName() + " has no getter method.");
            Object child = getter.invoke(bean);
            if (child == null) {
                child = children.getLineClass().getConstructor().newInstance();
                Method setter = this.propertyDescriptor.getWriteMethod();
                if (setter == null)
                    throw new BeanComposeException(
                            "The property " + propertyDescriptor.getName() + " of class " + children.getLineClass()
                                    .getName() + " has no setter method.");
                setter.invoke(bean, child);
            }
            childBean2Cell.assign(child, cell);
        }
    }

    private boolean isLeaf() {
        return this.children == null;
    }

}
