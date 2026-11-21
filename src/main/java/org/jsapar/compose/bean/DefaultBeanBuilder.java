package org.jsapar.compose.bean;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;

/**
 * Builder of a mutable bean that is created before its values are assigned. Assigns cells and detail beans with the
 * methods {@link BeanFactory#assignCellToBean(String, Object, Cell)} and
 * {@link BeanFactory#assignDetailToBean(String, Object, Line, Object)} of the bean factory, and returns the same bean
 * when built.
 * @param <T> common base class of all the expected beans.
 */
final class DefaultBeanBuilder<T> implements BeanBuilder<T> {
    private final BeanFactory<T> beanFactory;
    private final String lineType;
    private final T bean;

    DefaultBeanBuilder(BeanFactory<T> beanFactory, String lineType, T bean) {
        this.beanFactory = beanFactory;
        this.lineType = lineType;
        this.bean = bean;
    }

    @Override
    public void assignCell(Cell<?> cell) throws BeanComposeException, ReflectiveOperationException {
        beanFactory.assignCellToBean(lineType, bean, cell);
    }

    @Override
    public void assignDetail(Line detailLine, Object detailBean) throws BeanComposeException, ReflectiveOperationException {
        beanFactory.assignDetailToBean(lineType, bean, detailLine, detailBean);
    }

    @Override
    public T build() {
        return bean;
    }
}
