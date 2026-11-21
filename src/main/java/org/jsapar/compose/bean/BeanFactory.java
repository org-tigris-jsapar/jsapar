package org.jsapar.compose.bean;

import org.jsapar.error.ValidationAction;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;

import java.lang.reflect.InvocationTargetException;

/**
 * Interface for a bean factory that creates bean instances based on the line content. Create your own implementation of this
 * interface if you need to be able to control how beans are created in more details.
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 * @see BeanFactoryDefault
 * @see BeanFactoryByMap
 */
public interface BeanFactory<T> {

    /**
     * @param line The line to create a bean for.
     * @return A new instance of a java bean created for the supplied line. If this method returns null, the behavior of
     * the {@link BeanComposer} is denoted by the config parameter {@link BeanComposeConfig#setOnUndefinedLineType(ValidationAction)}
     * @throws ClassNotFoundException In case any of the classes described in the schema does not exist in the classpath.
     * @throws IllegalAccessException In case a setter method is not publicly accessible.
     * @throws InstantiationException If it was not possible to instantiate a bean.
     * @throws ClassCastException     In case an illegal cast is done while trying to use a setter.
     * @throws NoSuchMethodException  In case there is no public default constructor
     * @throws InvocationTargetException In case there was an exception while calling the default constructor.
     */
    T createBean(Line line) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, NoSuchMethodException, InvocationTargetException;


    /** Should assign the value of the specified cell to the proper bean property.
     *
     * @param lineType The name of the line type to use.
     * @param bean The bean to assign to.
     * @param cell The cell to assign.
     * @throws BeanComposeException In case there is an error assigning the cell. This exception will be caught by the
     * calling {@link BeanComposer} and converted into a call to the assigned error consumer.
     * @throws InvocationTargetException If not possible to invoke method.
     * @throws IllegalAccessException In case a setter method is not publicly accessible.
     * @throws InstantiationException If it was not possible to instantiate a bean.
     * @throws NoSuchMethodException In case the setter method does not exist.
     */
    void assignCellToBean(String lineType, T bean, Cell<?> cell) throws BeanComposeException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * Tells if detail lines of a line type are mapped to a property of the bean of a master line type. Only then
     * does the {@link BeanComposer} create a bean for the detail line and call
     * {@link #assignDetailToBean(String, Object, Line, Object)}. The default implementation returns false, which means
     * that detail lines are ignored.
     * @param lineType       The line type of the master line.
     * @param detailLineType The line type of the detail line.
     * @return True if detail lines of the type should be assigned to the bean of the master line.
     * @see org.jsapar.schema.SchemaLine.Builder#withParent(String)
     * @since 2.5
     */
    default boolean hasDetail(String lineType, String detailLineType) {
        return false;
    }

    /**
     * Should add the bean of a detail line to the proper property of the bean of the master line. Called only if
     * {@link #hasDetail(String, String)} returns true. The default implementation does nothing.
     * @param lineType   The line type of the master line.
     * @param bean       The bean of the master line.
     * @param detailLine The detail line.
     * @param detailBean The bean created for the detail line.
     * @throws BeanComposeException In case there is an error assigning the detail bean. This exception will be caught
     * by the calling {@link BeanComposer} and converted into a call to the assigned error consumer.
     * @throws InvocationTargetException If not possible to invoke method.
     * @throws IllegalAccessException In case a getter or setter method is not publicly accessible.
     * @since 2.5
     */
    default void assignDetailToBean(String lineType, T bean, Line detailLine, Object detailBean) throws BeanComposeException, InvocationTargetException, IllegalAccessException {
    }

    /**
     * Creates a builder of the bean of a line. This is what the {@link BeanComposer} uses to compose beans.
     * <p>
     * The default implementation calls {@link #createBean(Line)}, and the returned builder calls
     * {@link #assignCellToBean(String, Object, Cell)}, {@link #assignDetailToBean(String, Object, Line, Object)} and
     * returns the same bean when built. Override this method to support beans that cannot be changed after they are
     * created, such as records, as {@link BeanFactoryByMap} and {@link BeanFactoryDefault} do.
     * @param line The line to create a bean for.
     * @return A builder of the bean of the line. If this method returns null, the behavior of the
     * {@link BeanComposer} is denoted by the config parameter
     * {@link BeanComposeConfig#setOnUndefinedLineType(ValidationAction)}.
     * @throws ReflectiveOperationException In case the class of the bean cannot be found or the bean cannot be
     *                                      created.
     * @throws ClassCastException           In case the bean is not of the expected type.
     * @since 2.5
     */
    default BeanBuilder<T> createBeanBuilder(Line line) throws ReflectiveOperationException {
        T bean = createBean(line);
        if (bean == null)
            return null;
        return new DefaultBeanBuilder<>(this, line.getLineType(), bean);
    }

}
