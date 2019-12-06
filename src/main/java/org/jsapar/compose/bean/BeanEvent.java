package org.jsapar.compose.bean;

import org.jsapar.model.Line;

/**
 * Event that is generated each time a bean is composed by a {@link BeanComposer}
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 */
public final class BeanEvent<T>  {

    private final T bean;

    /**
     * The line that was parsed in order to create this event. Can be used to gain access to errors and raw cells that
     * were not mapped to any bean property.
     */
    private final Line line;

    /**
     * Creates an instance
     * @param bean The bean that was composed
     * @param line The line that was parsed in order to create this event.
     */
    BeanEvent(T bean, Line line) {
        this.bean = bean;
        this.line = line;
    }

    /**
     * @return The bean that was composed
     */
    public T getBean() {
        return bean;
    }

    /**
     * @return The line number from the source, if available or 0 if not available
     */
    public long getLineNumber() {
        return line.getLineNumber();
    }

    /**
     * @return The type of the line that was parsed in order to create this event.
     */
    public String getLineType() {
        return line.getLineType();
    }

    public Line getLine() {
        return line;
    }
}
