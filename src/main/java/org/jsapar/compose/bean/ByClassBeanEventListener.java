package org.jsapar.compose.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This bean event listener allows you to register different listeners for
 * different classes of the parsed beans. You may only register one listener for each class.
 * <p>
 * The default listener will be called if no listener was registered for the class.
 *
 * Deprecated since 2.2. Use {@link ByClassBeanConsumer} instead.
 */
@Deprecated
public class ByClassBeanEventListener<T> implements BeanEventListener<T> {
    private final Map<Class<?>, BeanEventListener<T>> beanEventListeners = new HashMap<>();

    /**
     * The default listener that gets called if no other listener was registered for the class. If no default
     * listener is explicitly set, the default behavior is to simply ignore the event.
     */
    private BeanEventListener<T> defaultListener = event -> {};

    @Override
    public void beanComposedEvent(BeanEvent<T> event) {
        BeanEventListener<T> listener = beanEventListeners.get(event.getBean().getClass());
        if (listener != null) {
            listener.beanComposedEvent(event);
        } else {
                defaultListener.beanComposedEvent(event);
        }
    }

    /**
     * Puts an event listener for the specified class, replacing any existing listener.
     *
     * @param c          The class to match for this listener. Test is done by equals match.
     * @param beanEventListener The line event listener to put.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<BeanEventListener<T>> put(Class<? extends T> c, BeanEventListener<T> beanEventListener) {
        return Optional.ofNullable(beanEventListeners.put(c, beanEventListener));
    }


    /**
     * Removes line event listener for specified class
     * @param c  The class to remove listeners for.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<BeanEventListener<T>> remove(Class<? extends T> c){
        return Optional.ofNullable(beanEventListeners.remove(c));
    }

    /**
     * Removes all registered event listeners but keeps the default listener.
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
