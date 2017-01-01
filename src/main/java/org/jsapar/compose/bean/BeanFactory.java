package org.jsapar.compose.bean;

import org.jsapar.error.ValidationAction;
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

    /**
     * Should find or create the child bean referred to by the specified child bean name in relation to the parent bean.
     * If a new child is created, it should also be assigned to the parentBean by this method. Dot notation should be
     * used in a cell name to access sub-levels. This method will be
     * called once for each level with the previous level as parentBean.
     * @param parentBean The parent of this specific child. Note that it might not be the base bean of type generic type T, it can also be a child bean thereof.
     * @param childBeanName
     *            A child bean name.
     * @return The child bean based on the supplied childBeanName. If no bean is assigned yet, a new instance is created
     *         and assigned by this method. If this method returns null, an error event is generated and the field is
     *         ignored.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    Object findOrCreateChildBean(Object parentBean, String childBeanName) throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException,
            InvocationTargetException;
}
