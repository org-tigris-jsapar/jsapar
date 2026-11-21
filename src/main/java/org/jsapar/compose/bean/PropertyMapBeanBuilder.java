package org.jsapar.compose.bean;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.bean.Bean2Cell;
import org.jsapar.parse.bean.Bean2Detail;
import org.jsapar.parse.bean.BeanPropertyMap;
import org.jsapar.parse.bean.ObjectNode;

/**
 * Builds a bean, mutable or record, according to a {@link BeanPropertyMap}.
 * @param <T> common base class of all the expected beans.
 */
final class PropertyMapBeanBuilder<T> implements BeanBuilder<T> {
    private final BeanPropertyMap beanPropertyMap;
    private final ObjectNode root;
    /** If true, a cell that is not mapped to a property is an error, otherwise it is ignored. */
    private final boolean failOnUnmappedCell;

    PropertyMapBeanBuilder(BeanPropertyMap beanPropertyMap, boolean failOnUnmappedCell) throws ReflectiveOperationException {
        this.beanPropertyMap = beanPropertyMap;
        this.root = beanPropertyMap.createObjectNode();
        this.failOnUnmappedCell = failOnUnmappedCell;
    }

    @Override
    public void assignCell(Cell<?> cell) throws BeanComposeException, ReflectiveOperationException {
        Bean2Cell bean2Cell = beanPropertyMap.getBean2CellByName(cell.getName());
        if (bean2Cell != null)
            root.assign(bean2Cell, cell);
        else if (failOnUnmappedCell)
            throw new BeanComposeException("Skipped assigning cell - There is no property " + cell.getName()
                    + " in " + beanPropertyMap.getLineClass().getName());
    }

    @Override
    public void assignDetail(Line detailLine, Object detailBean) throws BeanComposeException, ReflectiveOperationException {
        Bean2Detail bean2Detail = beanPropertyMap.getBean2Detail(detailLine.getLineType());
        if (bean2Detail != null)
            root.addDetail(bean2Detail, detailLine, detailBean);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T build() throws BeanComposeException, ReflectiveOperationException {
        root.initDetails(beanPropertyMap.getBean2Details());
        return (T) root.build();
    }
}
