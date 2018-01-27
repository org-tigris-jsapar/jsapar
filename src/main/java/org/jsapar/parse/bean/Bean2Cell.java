package org.jsapar.parse.bean;

import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.schema.SchemaCell;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class Bean2Cell {

    private String cellName;
    private Bean2Line children;
    private PropertyDescriptor propertyDescriptor;
    private CellCreator cellCreator;

    private Bean2Cell(String cellName) {
        this.cellName = cellName;
    }

    private Bean2Cell(String cellName, PropertyDescriptor propertyDescriptor) {
        this.cellName = cellName;
        this.propertyDescriptor = propertyDescriptor;
    }

    public static Bean2Cell ofSchemaCell(SchemaCell schemaCell, PropertyDescriptor propertyDescriptor) {
        Bean2Cell bean2Cell = new Bean2Cell(schemaCell.getName(), propertyDescriptor);
        // Prepare best way to create cell depending on return type
        bean2Cell.cellCreator = bean2Cell.makeCellCreator();
        return bean2Cell;
    }

    static Bean2Cell ofBaseProperty(PropertyDescriptor propertyDescriptor, Bean2Line children) {
        // The name is not important here, just make sure there is no conflict with other names.
        Bean2Cell bean2Cell = new Bean2Cell("@@" + propertyDescriptor.getName());
        bean2Cell.propertyDescriptor = propertyDescriptor;
        bean2Cell.children = children;
        return bean2Cell;
    }

    public String getCellName() {
        return cellName;
    }

    public Bean2Line getChildren() {
        return children;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public Optional<Cell> makeCell(Object object) throws InvocationTargetException, IllegalAccessException {
        return cellCreator.makeCell(object);
    }

    /**
     * Creates a cell creator instance suitable for this instance.
     * @return a cell creator best fitted for the job depending on the return type of the bean property.
     */
    @SuppressWarnings("unchecked")
    private CellCreator makeCellCreator() {
        Method f = propertyDescriptor.getReadMethod();
        if(f==null)
            throw new JSaParException("The property " + propertyDescriptor.getName() + " has no getter method.");

        Class returnType = f.getReturnType();

        if (returnType.isAssignableFrom(String.class)) {
            return (bean) -> {
                String value = (String) f.invoke(bean);
                if (value != null)
                    return Optional.of(new StringCell(cellName, value));
                else
                    return Optional.of(StringCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(Character.TYPE)
                || returnType.isAssignableFrom(Character.class)) {
            return (bean)-> Optional.of(new StringCell(cellName, (Character) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(LocalDate.class)) {
            return (bean)-> {
                LocalDate value = (LocalDate) f.invoke(bean);
                if (value != null)
                    return Optional.of(new LocalDateCell(cellName, value));
                else
                    return Optional.of(LocalDateCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(LocalDateTime.class)) {
            return (bean)-> {
                LocalDateTime value = (LocalDateTime) f.invoke(bean);
                if (value != null)
                    return Optional.of(new LocalDateTimeCell(cellName, value));
                else
                    return Optional.of(LocalDateTimeCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(LocalTime.class)) {
            return (bean)-> {
                LocalTime value = (LocalTime) f.invoke(bean);
                if (value != null)
                    return Optional.of(new LocalTimeCell(cellName, value));
                else
                    return Optional.of(LocalTimeCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(ZonedDateTime.class)) {
            return (bean)-> {
                ZonedDateTime value = (ZonedDateTime) f.invoke(bean);
                if (value != null)
                    return Optional.of(new ZonedDateTimeCell(cellName, value));
                else
                    return Optional.of(ZonedDateTimeCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(Date.class)) {
            return (bean)-> {
                Date value = (Date) f.invoke(bean);
                if (value != null)
                    return Optional.of(new DateCell(cellName, value));
                else
                    return Optional.of(DateCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(Calendar.class)) {
            return (bean)-> {
                Calendar value = (Calendar) f.invoke(bean);
                if (value != null)
                    return Optional.of(new DateCell(cellName, value.getTime()));
                else
                    return Optional.of(DateCell.emptyOf(cellName));
            };
        } else if (returnType.isAssignableFrom(Integer.TYPE) || returnType.isAssignableFrom(Integer.class)) {
            return (bean)->Optional.of(new IntegerCell(cellName, (Integer) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Byte.TYPE) || returnType.isAssignableFrom(Byte.class)) {
            return (bean)->Optional.of(new IntegerCell(cellName, (Byte) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Short.TYPE) || returnType.isAssignableFrom(Short.class)) {
            return (bean)->Optional.of(new IntegerCell(cellName, (Short) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Long.TYPE) || returnType.isAssignableFrom(Long.class)) {
            return (bean)->Optional.of(new IntegerCell(cellName, (Long) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Boolean.TYPE) || returnType.isAssignableFrom(Boolean.class)) {
            return (bean)->Optional.of(new BooleanCell(cellName, (Boolean) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Float.TYPE) || returnType.isAssignableFrom(Float.class)) {
            return (bean)->Optional.of(new FloatCell(cellName, (Float) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Double.TYPE) || returnType.isAssignableFrom(Double.class)) {
            return (bean)->Optional.of(new FloatCell(cellName, (Double) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(BigDecimal.class)) {
            return (bean)->Optional.of(new BigDecimalCell(cellName, (BigDecimal) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(BigInteger.class)) {
            return (bean)->Optional.of(new BigDecimalCell(cellName, (BigInteger) f.invoke(bean)));
        }
        return (bean)->Optional.of(new StringCell(cellName, String.valueOf(f.invoke(bean))));
    }

    /**
     * Cell creator interface. Needed to be able to let makeCell method throw exception.
     */
    private interface CellCreator{
        Optional<Cell> makeCell(Object o) throws InvocationTargetException, IllegalAccessException;
    }

}
