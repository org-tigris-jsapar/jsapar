package org.jsapar.compose.bean;

import org.jsapar.model.Line;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * This bean consumer allows you to register different consumers for
 * different classes of the parsed beans. You may only register one consumer for each class.
 * <p>
 * The default listener will be called if no consumer was registered for the class.
 */
public class ByClassBeanConsumer<T> implements BiConsumer<T, Line> {
    private final Map<Class<?>, BiConsumer<T, Line>> beanEventListeners = new HashMap<>();

    /**
     * The default consumer that gets called if no other consumer was registered for the class. If no default
     * consumer is explicitly set, the default behavior is to simply ignore the event.
     */
    private BiConsumer<T, Line> defaultListener = (b, l) -> {
    };

    @Override
    public void accept(T bean, Line line) {
        BiConsumer<T, Line> listener = beanEventListeners.get(bean.getClass());
        if (listener != null) {
            listener.accept(bean, line);
        } else {
            defaultListener.accept(bean, line);
        }
    }

    /**
     * Puts a consumer for the specified class, replacing any existing consumer.
     *
     * @param c                 The class to match for this consumer. Test is done by equals match.
     * @param beanConsumer The consumer to put.
     * @return Optional with previously registered consumer for this class. Optional.empty if no previous
     * consumer was registered for this class.
     */
    public Optional<BiConsumer<T, Line>> put(Class<? extends T> c, BiConsumer<T, Line> beanConsumer) {
        return Optional.ofNullable(beanEventListeners.put(c, beanConsumer));
    }


    /**
     * Removes consumer for specified class
     *
     * @param c The class to remove listeners for.
     * @return Optional with previously registered consumer for this line type. Optional.empty if no previous
     * listener was registered for this class.
     */
    public Optional<BiConsumer<T, Line>> remove(Class<? extends T> c) {
        return Optional.ofNullable(beanEventListeners.remove(c));
    }

    /**
     * Removes all registered consumers but keeps the default consumer.
     */
    public void removeAll() {
        beanEventListeners.clear();
    }

    /**
     * Sets the default consumer that will be called if no matching registered class is found.
     * Replaces previous default consumer.
     *
     * @param beanEventListener The consumer to use as default.
     */
    public void setDefault(BiConsumer<T, Line> beanEventListener) {
        this.defaultListener = beanEventListener;
    }

}
