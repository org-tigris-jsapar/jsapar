package org.jsapar.parse;

import org.jsapar.model.Line;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This line event listener allows you to register different listeners for
 * different line types. You may only register one listener for each line type. If you need to have multiple listeners
 * for a particular line type, register an instance of {@link MulticastConsumer} for that line type.
 * <p>
 * The default listener will be called if no listener was registered for the line type.
 */
@SuppressWarnings("WeakerAccess")
public class ByLineTypeLineConsumer implements Consumer<Line> {

    /**
     * Current listeners
     */
    private final Map<String, Consumer<Line>> listeners = new HashMap<>();

    /**
     * The default listener that gets called if no other listener was registered for the line type. If no default
     * listener is explicitly set, the default behavior is to simply ignore the line parsed event.
     */
    private Consumer<Line> defaultListener = e -> {
    };

    @Override
    public void accept(Line line) {
        Consumer<Line> listener = listeners.get(line.getLineType());
        if (listener != null) {
            listener.accept(line);
        } else {
            defaultListener.accept(line);
        }
    }

    /**
     * Puts a line event listener for the specified line type, replacing any existing listener.
     *
     * @param lineType          The line type to match for this listener. Test is done by equals match.
     * @param lineConsumer The line event listener to put.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<Consumer<Line>> put(String lineType, Consumer<Line> lineConsumer) {
        return Optional.ofNullable(listeners.put(lineType, lineConsumer));
    }


    /**
     * Removes line event listener for specified line type
     * @param lineType  The line type to remove listeners for.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<Consumer<Line>> remove(String lineType){
        return Optional.ofNullable(listeners.remove(lineType));
    }

    /**
     * Removes all registered line event listeners but keeps the default listener.
     */
    public void removeAll(){
        listeners.clear();
    }

    /**
     * Sets the default line event listener that will be called if no matching registered line type will be found.
     * Replaces previous default line event listener.
     * @param lineConsumer The line event listener to use as default.
     */
    public void setDefault(Consumer<Line> lineConsumer) {
        this.defaultListener = lineConsumer;
    }
}
