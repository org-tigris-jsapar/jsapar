package org.jsapar.parse.bean;

import org.jsapar.model.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

public class Bean2Cell {

    @SuppressWarnings("unchecked")
    static Optional<Cell> makeCell(Object bean, Method f, String name) throws InvocationTargetException, IllegalAccessException {
        @SuppressWarnings("rawtypes")
        Class returnType = f.getReturnType();

        if (returnType.isAssignableFrom(String.class)) {
            String value = (String) f.invoke(bean);
            if (value != null)
                return Optional.of(new StringCell(name, value));
            else
                return Optional.of(StringCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(Character.TYPE)
                || returnType.isAssignableFrom(Character.class)) {
            return Optional.of(new StringCell(name, (Character) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(LocalDate.class)) {
            LocalDate value = (LocalDate) f.invoke(bean);
            if (value != null)
                return Optional.of(new LocalDateCell(name, value));
            else
                return Optional.of(LocalDateCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(LocalDateTime.class)) {
            LocalDateTime value = (LocalDateTime) f.invoke(bean);
            if (value != null)
                return Optional.of(new LocalDateTimeCell(name, value));
            else
                return Optional.of(LocalDateTimeCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(LocalTime.class)) {
            LocalTime value = (LocalTime) f.invoke(bean);
            if (value != null)
                return Optional.of(new LocalTimeCell(name, value));
            else
                return Optional.of(LocalTimeCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(ZonedDateTime.class)) {
            ZonedDateTime value = (ZonedDateTime) f.invoke(bean);
            if (value != null)
                return Optional.of(new ZonedDateTimeCell(name, value));
            else
                return Optional.of(ZonedDateTimeCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(Date.class)) {
            Date value = (Date) f.invoke(bean);
            if (value != null)
                return Optional.of(new DateCell(name, value));
            else
                return Optional.of(DateCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(Calendar.class)) {
            Calendar value = (Calendar) f.invoke(bean);
            if (value != null)
                return Optional.of(new DateCell(name, value.getTime()));
            else
                return Optional.of(DateCell.emptyOf(name));
        } else if (returnType.isAssignableFrom(Integer.TYPE) || returnType.isAssignableFrom(Integer.class)) {
            return Optional.of(new IntegerCell(name, (Integer) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Byte.TYPE) || returnType.isAssignableFrom(Byte.class)) {
            return Optional.of(new IntegerCell(name, (Byte) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Short.TYPE) || returnType.isAssignableFrom(Short.class)) {
            return Optional.of(new IntegerCell(name, (Short) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Long.TYPE) || returnType.isAssignableFrom(Long.class)) {
            return Optional.of(new IntegerCell(name, (Long) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Boolean.TYPE) || returnType.isAssignableFrom(Boolean.class)) {
            return Optional.of(new BooleanCell(name, (Boolean) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Float.TYPE) || returnType.isAssignableFrom(Float.class)) {
            return Optional.of(new FloatCell(name, (Float) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(Double.TYPE) || returnType.isAssignableFrom(Double.class)) {
            return Optional.of(new FloatCell(name, (Double) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(BigDecimal.class)) {
            return Optional.of(new BigDecimalCell(name, (BigDecimal) f.invoke(bean)));
        } else if (returnType.isAssignableFrom(BigInteger.class)) {
            return Optional.of(new BigDecimalCell(name, (BigInteger) f.invoke(bean)));
        }
        return Optional.empty();
    }
}
