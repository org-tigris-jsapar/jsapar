package org.jsapar.compose.bean;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;

/**
 * Builds the bean of one line. The {@link BeanComposer} first assigns all cells of the line, then all detail beans,
 * and finally calls {@link #build()} to get the bean. This makes it possible to create beans that cannot be changed
 * after they are created, such as records, since they can be created when all values are known.
 * <p>
 * Instances are created by {@link BeanFactory#createBeanBuilder(Line)}.
 *
 * @param <T> common base class of all the expected beans.
 * @since 2.5
 */
public interface BeanBuilder<T> {

    /**
     * Assigns the value of a cell to the proper bean property. Called only for cells that have a name and are not
     * empty.
     * @param cell The cell to assign.
     * @throws BeanComposeException         In case there is an error assigning the cell. This exception will be
     *                                      caught by the calling {@link BeanComposer} and converted into a call to
     *                                      the assigned error consumer.
     * @throws ReflectiveOperationException If a getter, setter or constructor fails or cannot be found or accessed.
     *                                      Also caught by the calling {@link BeanComposer}.
     */
    void assignCell(Cell<?> cell) throws BeanComposeException, ReflectiveOperationException;

    /**
     * Adds the bean of a detail line to the proper property of the bean. Called only if
     * {@link BeanFactory#hasDetail(String, String)} returns true for the line types.
     * @param detailLine The detail line.
     * @param detailBean The bean created for the detail line.
     * @throws BeanComposeException         In case there is an error assigning the detail bean. This exception will
     *                                      be caught by the calling {@link BeanComposer} and converted into a call
     *                                      to the assigned error consumer.
     * @throws ReflectiveOperationException If a getter or setter fails or cannot be accessed. Also caught by the
     *                                      calling {@link BeanComposer}.
     */
    void assignDetail(Line detailLine, Object detailBean) throws BeanComposeException, ReflectiveOperationException;

    /**
     * @return The bean with all assigned values.
     * @throws BeanComposeException         In case the bean cannot be built. The {@link BeanComposer} then reports
     *                                      an error and skips the bean.
     * @throws ReflectiveOperationException If a constructor or setter fails or cannot be accessed, for instance if
     *                                      the constructor of a record throws an exception because of invalid
     *                                      values. The {@link BeanComposer} then reports an error and skips the bean.
     */
    T build() throws BeanComposeException, ReflectiveOperationException;
}
