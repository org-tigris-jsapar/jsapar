package org.jsapar.compose.bean;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.bean.Bean2Cell;
import org.jsapar.parse.bean.BeanMap;
import org.jsapar.parse.bean.BeanPropertyMap;

import java.lang.reflect.InvocationTargetException;

public class BeanFactoryByMap<T> implements BeanFactory<T>{
    private BeanMap beanMap;

    public BeanFactoryByMap(BeanMap beanMap) {
        this.beanMap = beanMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createBean(Line line) throws InstantiationException, IllegalAccessException, ClassCastException {
        BeanPropertyMap optionalBeanPropertyMap = beanMap.getBeanPropertyMap(line.getLineType());
        if(optionalBeanPropertyMap != null){
            return (T) optionalBeanPropertyMap.createBean();
        }
        return null;
    }

    @Override
    public void assignCellToBean(String lineType, T bean, Cell cell) throws InvocationTargetException, InstantiationException, IllegalAccessException, BeanComposeException {
        BeanPropertyMap beanPropertyMap = beanMap.getBeanPropertyMap(lineType);
        Bean2Cell bean2Cell = beanPropertyMap.getBean2CellByName(cell.getName());
        if(bean2Cell != null)
            bean2Cell.assign(bean, cell);
    }

}
