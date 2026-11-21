package org.jsapar.compose.bean;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.bean.Bean2Cell;
import org.jsapar.bean.BeanMap;
import org.jsapar.parse.bean.BeanPropertyMap;

import java.lang.reflect.InvocationTargetException;

/**
 * Class that creates beans based on a {@link BeanMap}. The {@link BeanMap} have the information about how the beans
 * should be created and how properties can be assigned.
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 */
public final class BeanFactoryByMap<T> implements BeanFactory<T>{
    private final BeanMap beanMap;

    public BeanFactoryByMap(BeanMap beanMap) {
        this.beanMap = beanMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createBean(Line line) throws InstantiationException, IllegalAccessException, ClassCastException, NoSuchMethodException, InvocationTargetException {
        BeanPropertyMap optionalBeanPropertyMap = beanMap.getBeanPropertyMap(line.getLineType());
        if(optionalBeanPropertyMap != null){
            return (T) optionalBeanPropertyMap.createBean();
        }
        return null;
    }

    /**
     * Creates a builder of the bean of the line. Supports records, which are created with their canonical
     * constructor when all values are known, also as nested objects and detail beans.
     * @see BeanFactory#createBeanBuilder(Line)
     */
    @Override
    public BeanBuilder<T> createBeanBuilder(Line line) throws ReflectiveOperationException {
        BeanPropertyMap beanPropertyMap = beanMap.getBeanPropertyMap(line.getLineType());
        if (beanPropertyMap == null || beanPropertyMap.ignoreLine())
            return null;
        return new PropertyMapBeanBuilder<>(beanPropertyMap, false);
    }

    /**
     * Assigns the value of a cell to a mutable bean. If the cell belongs to a record nested within the bean, the record
     * is created again with the new value each time this method is called, so a record whose constructor validates
     * its values may fail before all its cells have been assigned. The {@link BeanComposer} uses
     * {@link #createBeanBuilder(Line)} instead, which creates records when all cells of the line have been assigned.
     * @see BeanFactory#assignCellToBean(String, Object, Cell)
     */
    @Override
    public void assignCellToBean(String lineType, T bean, Cell<?> cell) throws InvocationTargetException, InstantiationException, IllegalAccessException, BeanComposeException, NoSuchMethodException {
        BeanPropertyMap beanPropertyMap = beanMap.getBeanPropertyMap(lineType);
        Bean2Cell bean2Cell = beanPropertyMap.getBean2CellByName(cell.getName());
        if(bean2Cell != null)
            bean2Cell.assign(bean, cell);
    }

    @Override
    public boolean hasDetail(String lineType, String detailLineType) {
        BeanPropertyMap beanPropertyMap = beanMap.getBeanPropertyMap(lineType);
        return beanPropertyMap != null && beanPropertyMap.getBean2Detail(detailLineType) != null;
    }

    @Override
    public void assignDetailToBean(String lineType, T bean, Line detailLine, Object detailBean) throws BeanComposeException, InvocationTargetException, IllegalAccessException {
        beanMap.getBeanPropertyMap(lineType).getBean2Detail(detailLine.getLineType()).add(bean, detailLine, detailBean);
    }

}
