package org.jsapar.compose.bean;

import org.jsapar.error.ValidationAction;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;

import java.lang.reflect.InvocationTargetException;

/**
 * Interface for a bean factory that creates bean instances based on line content. Create your own implementation of this
 * interface if you need to be able to control how beans are created in more details.
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 * @see BeanFactoryDefault
 */
public interface BeanFactory<T> {

    /**
     * @param line The line to create a bean for.
     * @return A new instance of a java bean created for the supplied line. If this method returns null, the behavior of
     * the {@link BeanComposer} is denoted by the config parameter {@link BeanComposeConfig#setOnUndefinedLineType(ValidationAction)}
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    T createBean(Line line) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException;


    /** Should assign the value of the specified cell to the proper bean property.
     *
     * @param lineType
     * @param bean The bean to assign to.
     * @param cell The cell to assign.
     * @throws BeanComposeException In case there is an error assigning the cell. This exception will be caught by the
     * calling {@link BeanComposer} and converted into an {@link org.jsapar.error.ErrorEvent}.
     */
    void assignCellToBean(String lineType, T bean, Cell cell) throws BeanComposeException, InvocationTargetException, InstantiationException, IllegalAccessException;

}
