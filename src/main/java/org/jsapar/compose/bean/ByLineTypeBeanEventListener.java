package org.jsapar.compose.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This bean event listener allows you to register different listeners for
 * different line types. You may only register one listener for each line type.
 * <p>
 * The default listener will be called if no listener was registered for the line type.
 */
@SuppressWarnings("WeakerAccess")
public class ByLineTypeBeanEventListener<T> implements BeanEventListener<T> {
    private Map<String, BeanEventListener<T>> beanEventListeners = new HashMap<>();
    /**
     * The default listener that gets called if no other listener was registered for the line type. If no default
     * listener is explicitly set, the default behavior is to simply ignore the event.
     */
    private BeanEventListener<T> defaultListener = event -> {};

    @Override
    public void beanComposedEvent(BeanEvent<T> event) {
        BeanEventListener<T> listener = beanEventListeners.get(event.getLine().getLineType());
        if (listener != null) {
            listener.beanComposedEvent(event);
        } else {
            defaultListener.beanComposedEvent(event);
        }
    }

    /**
     * Puts an event listener for the specified line type, replacing any existing listener.
     *
     * @param lineType          The line type to match for this listener. Test is done by equals match.
     * @param beanEventListener The line event listener to put.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<BeanEventListener<T>> put(String lineType, BeanEventListener<T> beanEventListener) {
        return Optional.ofNullable(beanEventListeners.put(lineType, beanEventListener));
    }


    /**
     * Removes line event listener for specified line type
     * @param lineType  The line type to remove listeners for.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<BeanEventListener<T>> remove(String lineType){
        return Optional.ofNullable(beanEventListeners.remove(lineType));
    }

    /**
     * Removes all registered line event listeners but keeps the default listener.
     */
    public void removeAll(){
        beanEventListeners.clear();
    }

    /**
     * Sets the default event listener that will be called if no matching registered line type will be found.
     * Replaces previous default event listener.
     * @param beanEventListener The event listener to use as default.
     */
    public void setDefault(BeanEventListener<T> beanEventListener) {
        this.defaultListener = beanEventListener;
    }
    
}
