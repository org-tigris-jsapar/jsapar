package org.jsapar.parse.bean;

import org.jsapar.TstPerson;
import org.jsapar.compose.bean.BeanComposeException;
import org.jsapar.model.*;
import org.jsapar.parse.bean.reflect.BeanInfo;
import org.jsapar.parse.bean.reflect.PropertyDescriptor;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;

public class Bean2CellTest {


     @Test
    public void getPropertyDescriptor_getCellName() {
        BeanInfo beanInfo = BeanInfo.ofClass(TstPerson.class);
        PropertyDescriptor propertyDescriptor = beanInfo.getPropertyDescriptorsByName().get("adult");
        Bean2Cell bean2Cell = Bean2Cell.ofCellName("adult", propertyDescriptor);
        assertSame(propertyDescriptor, bean2Cell.getPropertyDescriptor());
        assertEquals("adult", bean2Cell.getCellName());
    }

    @Test
    public void makeCell() throws InvocationTargetException, IllegalAccessException {
        Bean2Cell bean2Cell = makeBean2CellOfPropertyName(TstPerson.class, "adult");
        TstPerson tstPerson = new TstPerson();
        tstPerson.setAdult(true);
        Cell cell = bean2Cell.makeCell(tstPerson);
        assertEquals("adult", cell.getName());
        assertEquals(BooleanCell.class, cell.getClass());
        BooleanCell booleanCell = (BooleanCell) cell;
        assertEquals(Boolean.TRUE, booleanCell.getValue());
    }

    @SuppressWarnings("unused")
    private static class LocalDateTimeHolder{
         private LocalDateTime dateTime;

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }

    @Test
    public void makeCell_LocalDateTime() throws InvocationTargetException, IllegalAccessException {
        Bean2Cell bean2Cell = makeBean2CellOfPropertyName(LocalDateTimeHolder.class, "dateTime");
        LocalDateTimeHolder bean = new LocalDateTimeHolder();
        Cell cell = bean2Cell.makeCell(bean);
        assertTrue(cell.isEmpty());
        bean.setDateTime(LocalDateTime.of(2019, Month.APRIL, 1 , 12, 32));
        cell = bean2Cell.makeCell(bean);
        assertEquals("dateTime", cell.getName());
        assertEquals(LocalDateTimeCell.class, cell.getClass());
        assertEquals(LocalDateTime.of(2019, Month.APRIL, 1 , 12, 32), cell.getValue());
    }

    @Test
    public void assign_Boolean()
            throws InvocationTargetException, InstantiationException, IllegalAccessException,
            BeanComposeException, NoSuchMethodException {
        Bean2Cell bean2Cell = makeBean2CellOfPropertyName(TstPerson.class, "adult");
        TstPerson tstPerson = new TstPerson();
        assertFalse(tstPerson.isAdult());
        bean2Cell.assign(tstPerson, new BooleanCell("adult", true));
        assertTrue(tstPerson.isAdult());
        bean2Cell.assign(tstPerson, new BooleanCell("adult", false));
        assertFalse(tstPerson.isAdult());
    }

    @Test
    public void assign_Integer_from_Long_value()
            throws InvocationTargetException, InstantiationException, IllegalAccessException,
            BeanComposeException, NoSuchMethodException {
        Bean2Cell bean2Cell = makeBean2CellOfPropertyName(TstPerson.class, "optionalInt");
        TstPerson tstPerson = new TstPerson();
        assertNull(tstPerson.getOptionalInt());
        bean2Cell.assign(tstPerson, IntegerCell.emptyOf("optionalInt"));
        assertNull(tstPerson.getOptionalInt());
        bean2Cell.assign(tstPerson, new IntegerCell("optionalInt", 42L));
        assertEquals(Integer.valueOf(42), tstPerson.getOptionalInt());
    }

    @Test
    public void assign_String()
            throws InvocationTargetException, InstantiationException, IllegalAccessException,
            BeanComposeException, NoSuchMethodException {
        Bean2Cell bean2Cell = makeBean2CellOfPropertyName(TstPerson.class, "lastName");
        TstPerson tstPerson = new TstPerson();
        assertEquals("Nobody", tstPerson.getLastName());
        bean2Cell.assign(tstPerson, new StringCell("lastName", "Somebody"));
        assertEquals("Somebody", tstPerson.getLastName());
    }

    @SuppressWarnings("SameParameterValue")
    private Bean2Cell makeBean2CellOfPropertyName(Class<?> beanClass, String propertyName)  {
        BeanInfo beanInfo = BeanInfo.ofClass(beanClass);
        PropertyDescriptor propertyDescriptor = beanInfo.getPropertyDescriptorsByName().get(propertyName);
        return Bean2Cell.ofCellName(propertyName, propertyDescriptor);
    }

    @Test
    public void assign_empty()
            throws InvocationTargetException, InstantiationException, IllegalAccessException,
            BeanComposeException, NoSuchMethodException {
        Bean2Cell bean2Cell = makeBean2CellOfPropertyName(TstPerson.class, "lastName");
        TstPerson tstPerson = new TstPerson();
        assertEquals("Nobody", tstPerson.getLastName());
        bean2Cell.assign(tstPerson, StringCell.emptyOf("lastName"));
        assertEquals("Nobody", tstPerson.getLastName());
    }

}