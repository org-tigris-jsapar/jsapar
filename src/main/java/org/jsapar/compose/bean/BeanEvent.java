package org.jsapar.compose.bean;

import java.util.EventObject;

/**
 * Event that is generated each time a bean is composed by a {@link BeanComposer}
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 * @author stejon0
 */
public final class BeanEvent<T> extends EventObject {

    private static final long serialVersionUID = 9009392654758990079L;
    private final T bean;
    private final long   lineNumber;

    /**
     * The line type of the event that was parsed in order to create this event or the bean class name if the event did
     * not origin from parsing a line.
     */
    private final String lineType;

    /**
     * Creates an instance
     * @param source The sending source of this event.
     * @param bean The bean that was composed
     * @param lineNumber The line number from the source, if available or 0 if not available
     * @param lineType The line type of the event that was parsed in order to create this event or the bean class name
     *                 if the event did not origin from parsing a line.
     */
    public BeanEvent(Object source, T bean, long lineNumber, String lineType) {
        super(source);
        this.bean = bean;
        this.lineNumber = lineNumber;
        this.lineType = lineType;
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
        return lineNumber;
    }

    public String getLineType() {
        return lineType;
    }
}
