package org.jsapar.parse.bean;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BeanBackedLine<T> extends Line{

    private T bean;
    private static final String GET_PREFIX = "get";

    public BeanBackedLine(T bean){
        super(bean.getClass().getName());
        this.bean = bean;
    }


    @Override
    public Optional<Cell> getCell(String name) {
        Optional<Cell> oCell = super.getCell(name);
        if(oCell.isPresent())
            return oCell;

        String[] names = name.split("\\.");
        if(names.length <=0)
            return Optional.empty();

        oCell = makeCellFromBean(bean, name, Arrays.asList(names));
        oCell.ifPresent(this::addCell);
        return oCell;
    }

    private Optional<Cell> makeCellFromBean(Object bean, String fullName, List<String> names) {
        try {
            Method f = new PropertyDescriptor(names.get(0), bean.getClass()).getReadMethod();
            if(f == null)
                return Optional.empty();
            Optional<Cell> optionalCell = Bean2Cell.makeCell(bean, f, fullName);
            if(optionalCell.isPresent())
                return optionalCell;
            Object childBean = f.invoke(bean);
            if(childBean == null)
                return Optional.empty();
            return makeCellFromBean(childBean, fullName, names.subList(1, names.size()));

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            addCellError(new CellParseException(fullName, null, null, e.getMessage()));
            return Optional.empty();
        }
    }

}
