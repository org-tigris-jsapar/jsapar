package org.jsapar.parse.bean;

import org.jsapar.compose.bean.BeanComposeException;
import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.parse.bean.reflect.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiFunction;

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
    @SuppressWarnings({"rawtypes"})
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
                return new StringCell(cellName, value.toString());
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

    /**
     * Converts the value of a cell to the type of the property.
     * @param paramType The type of the property, i.e. the parameter type of the setter or the type of the record
     *                  component.
     * @param cell      A non-empty cell.
     * @return The value to assign.
     * @throws BeanComposeException If the value cannot be converted.
     */
    @SuppressWarnings("unchecked")
    Object toPropertyValue(Class<?> paramType, Cell<?> cell) throws BeanComposeException {
        Class<?> valueType = cell.getValue().getClass();
        Object value = cell.getValue();
        if (paramType.isAssignableFrom(valueType))
            return value;
        if (paramType == String.class) {
            return cell.getStringValue();
        }
        if (value instanceof Number number) {
            if (paramType == Integer.TYPE || paramType == Integer.class)
                return number.intValue();
            else if (paramType == Long.TYPE || paramType == Long.class)
                return number.longValue();
            else if (paramType == Double.TYPE || paramType==Double.class)
                return number.doubleValue();
            else if (paramType == Float.TYPE || paramType==Float.class)
                return number.floatValue();
            else if (paramType == Short.TYPE || paramType==Short.class)
                return number.shortValue();
            else if (paramType == Byte.TYPE || paramType==Byte.class)
                return number.byteValue();
            else if (paramType == Boolean.TYPE || paramType==Boolean.class)
                return number.intValue() != 0;
            else if (paramType == Character.TYPE || paramType==Character.class)
                return (char) number.intValue();

        }
        // Will squeeze in first character of any datatype's string representation.
        else if (paramType == Character.TYPE || paramType == Character.class) {
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
        } else if(paramType.isAssignableFrom(Path.class)){
            return Path.of(cell.getStringValue());
        }
        throw new BeanComposeException(
                "Skipped assigning cell - The value could not be assigned to property " + this.propertyDescriptor.getName()
                        + " of type " + paramType.getName());
    }

    /**
     * Assigns the value of a cell to a mutable bean, possibly to a property of a nested object.
     * <p>
     * If the cell belongs to a record nested within the bean, the record is created again with the new value each time
     * this method is called, while the other components keep their current values, or null if there was no record.
     * A record whose constructor validates its values may therefore fail before all its cells have been assigned. Use
     * {@link BeanPropertyMap#createObjectNode()} to assign all cells of a line before the records are created.
     * @param bean The bean of the line.
     * @param cell The cell.
     * @throws BeanComposeException      If the value cannot be assigned.
     * @throws InvocationTargetException If a getter, setter or constructor fails.
     * @throws IllegalAccessException    If a getter, setter or constructor is not accessible.
     * @throws InstantiationException    If a nested object cannot be created.
     * @throws NoSuchMethodException     If a nested object has no public default constructor.
     */
    public void assign(Object bean, Cell<?> cell)
            throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        ObjectNode node = ObjectNode.ofBean(bean);
        node.assign(this, cell);
        node.build();
    }

}
