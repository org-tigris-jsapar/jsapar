package org.jsapar.compose.bean;

import org.jsapar.model.Line;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This bean consumer allows you to register different listeners for
 * different line types. You may only register one consumer for each line type.
 * <p>
 * The default consumer will be called if no consumer was registered for the line type.
 */
@SuppressWarnings("WeakerAccess")
public class ByLineTypeBeanConsumer<T> implements BiConsumer<T, Line> {
    private Map<String, BiConsumer<T, Line>> beanConsumers = new HashMap<>();
    /**
     * The default consumer that gets called if no other consumer was registered for the line type. If no default
     * consumer is explicitly set, the default behavior is to simply do nothing.
     */
    private BiConsumer<T, Line> defaultConsumer = (b, l) -> {};

    @Override
    public void accept(T bean, Line line) {
        BiConsumer<T, Line> consumer = beanConsumers.get(line.getLineType());
        if (consumer != null) {
            consumer.accept(bean, line);
        } else {
            defaultConsumer.accept(bean, line);
        }
    }

    /**
     * Puts a consumer for the specified line type, replacing any existing consumer.
     *
     * @param lineType          The line type to match for this consumer. Test is done by equals match.
     * @param beanAndLineConsumer The bi-consumer to put. Takes two arguments: the bean and the line that was generated.
     * @return Optional with previously registered consumer for this line type. Optional.empty if no previous
     * consumer was registered for this line type.
     */
    public Optional<BiConsumer<T, Line>> put(String lineType, BiConsumer<T, Line> beanAndLineConsumer) {
        return Optional.ofNullable(beanConsumers.put(lineType, beanAndLineConsumer));
    }

    /**
     * Puts a consumer for the specified line type, replacing any existing consumer.
     *
     * @param lineType          The line type to match for this consumer. Test is done by equals match.
     * @param beanConsumer The consumer to put. Takes one argument: The bean that was generated.
     * @return Optional with previously registered consumer for this line type. Optional.empty if no previous
     * consumer was registered for this line type.
     */
    public Optional<BiConsumer<T, Line>> put(String lineType, Consumer<T> beanConsumer) {
        return Optional.ofNullable(beanConsumers.put(lineType, (b,l)->beanConsumer.accept(b)));
    }


    /**
     * Removes line consumer for specified line type
     * @param lineType  The line type to remove listeners for.
     * @return Optional with previously registered consumer for this line type. Optional.empty if no previous
     * consumer was registered for this line type.
     */
    public Optional<BiConsumer<T, Line>> remove(String lineType){
        return Optional.ofNullable(beanConsumers.remove(lineType));
    }

    /**
     * Removes all registered line listeners but keeps the default consumer.
     */
    public void removeAll(){
        beanConsumers.clear();
    }

    /**
     * Sets the default consumer that will be called if no matching registered line type will be found.
     * Replaces previous default consumer.
     * @param beanEventListener The consumer to use as default.
     */
    public void setDefault(BiConsumer<T, Line> beanEventListener) {
        this.defaultConsumer = beanEventListener;
    }
    
}
