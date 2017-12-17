package org.jsapar.parse.bean;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Used internally when converting beans to text.
 * A line implementation that will create Cell instances on-the-fly when requested upon. The created cells are cached
 * internally upon first access meaning that if the backed bean instance is changed externally after a cell has been
 * created for a bean property, the same cell will be returned upon successive access.
 *
 * @param <T> The bean type
 */
public class BeanBackedLine<T> extends Line{

    private T bean;
    private ErrorEventListener errorListener;

    BeanBackedLine(T bean, ErrorEventListener errorListener){
        super(bean.getClass().getName());
        this.bean = bean;
        this.errorListener = errorListener;
    }


    /**
     * Gets a cell with specified name. Name is specified by the schema.
     * Creates cells on-the-fly when requested upon.
     * @param name The name of the cell to get
     * @return A cell with the given name or empty if no property exists for the bean with the given name.
     */
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
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(names.get(0), bean.getClass());
            Method f = propertyDescriptor.getReadMethod();
            if (f == null)
                return Optional.empty();
            Optional<Cell> optionalCell = Bean2Cell.makeCell(bean, f, fullName);
            if (optionalCell.isPresent())
                return optionalCell;
            Object childBean = f.invoke(bean);
            if (childBean == null)
                return Optional.empty();
            return makeCellFromBean(childBean, fullName, names.subList(1, names.size()));

        }catch (IntrospectionException e){
            return Optional.empty(); // No such method.
        } catch (IllegalAccessException | InvocationTargetException e) {
            CellParseException error = new CellParseException(fullName, null, null, e.getMessage());
            addCellError(error);
            errorListener.errorEvent(new ErrorEvent(this, error));
            return Optional.empty();
        }
    }

    /**
     * @throws UnsupportedOperationException Always
     */
    @Override
    public List<Cell> getCells() {
        throw new UnsupportedOperationException("This implementation is backed by a bean.");
    }

    /**
     * @throws UnsupportedOperationException Always
     */
    @Override
    public Iterator<Cell> iterator() {
        throw new UnsupportedOperationException("This implementation is backed by a bean.");
    }

    /**
     * @throws UnsupportedOperationException Always
     */
    @Override
    public Stream<Cell> stream() {
        throw new UnsupportedOperationException("This implementation is backed by a bean.");
    }
}
