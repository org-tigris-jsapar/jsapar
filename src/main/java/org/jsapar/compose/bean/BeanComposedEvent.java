/**
 *
 */
package org.jsapar.compose.bean;

import org.jsapar.model.Line;

import java.util.EventObject;

/**
 * @author stejon0
 */
public final class BeanComposedEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 9009392654758990079L;
    private final Object bean;
    private final long   lineNumber;

    /**
     * @param source
     * @param bean
     * @param lineNumber
     */
    public BeanComposedEvent(Object source, Object bean, long lineNumber) {
        super(source);
        this.bean = bean;
        this.lineNumber = lineNumber;
    }

    public Object getBean() {
        return bean;
    }

    public long getLineNumber() {
        return lineNumber;
    }
}
