/**
 * 
 */
package org.jsapar.compose.bean;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineParsedEvent;

import java.util.EventListener;

/**
 * Interface for receiving event call-backs while parsing.
 * 
 * @author stejon0
 * 
 */
public interface BeanComposedEventListener extends EventListener {

    /**
     * Called every time that a bean, on root level, is successfully composed. Child beans do not generate events.
     * 
     * @param event
     *            The event that contains the composed bean.
     * @throws JSaParException
     */
    void beanComposedEvent(BeanComposedEvent event) ;


}
