package org.jsapar.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This line event listener implementation works as a subscription hub where you can register line event listeners for
 * different line types. You may register any number of listeners for the same line type. Each listener with the matching
 * line type will be called in the same order as they were registered.
 * <p>
 * The default listener will be called if no other listener was registered for the line type.
 */
@SuppressWarnings("WeakerAccess")
public class ByLineTypeLineEventListener implements LineEventListener {

    /**
     * Current listeners
     */
    private final Map<String, List<LineEventListener>> listeners = new HashMap<>();

    /**
     * The default listener that gets called if no other listener was registered for the line type. If no default
     * listener is explicitly set, the default behavior is to simply ignore the line parsed event.
     */
    private LineEventListener defaultListener = e -> {
    };

    @Override
    public void lineParsedEvent(LineParsedEvent event) {
        List<LineEventListener> registeredListeners = listeners.get(event.getLine().getLineType());
        if (registeredListeners != null) {
            registeredListeners.forEach(l -> l.lineParsedEvent(event));
        } else {
            defaultListener.lineParsedEvent(event);
        }
    }

    /**
     * Adds a line event listener for the specified line type.
     * @param lineType The line type to match for this listener. Test is done by equals match.
     * @param lineEventListener  The line event listener to register.
     */
    public void register(String lineType, LineEventListener lineEventListener) {
        this.listeners.computeIfAbsent(lineType, k -> new ArrayList<>()).add(lineEventListener);
    }

    /**
     * Removes one specific listener matching the supplied line type and equals to the supplied line event listener.
     * @param lineType The line type to remove listener for.
     * @param lineEventListener The line event listener to remove. Test is done by equals match.
     * @return true if item was found and removed, false if there was no listener registered that matches.
     */
    public boolean remove(String lineType, LineEventListener lineEventListener) {
        AtomicBoolean removed = new AtomicBoolean(false);
        this.listeners.computeIfPresent(lineType, (k, v) -> {
            removed.set(v.remove(lineEventListener));
            return v;
        });
        return removed.get();
    }

    /**
     * Removes all line event listeners for specified line type
     * @param lineType  The line type to remove listeners for.
     * @return The number of removed listeners.
     */
    public int removeAll(String lineType){
        AtomicInteger count = new AtomicInteger(0);
        this.listeners.computeIfPresent(lineType, (k, v) -> {
            count.set(v.size());
            v.clear();
            return v;
        });
        return count.get();
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
     * @param lineEventListener The line event listener to use as default.
     */
    public void setDefault(LineEventListener lineEventListener) {
        this.defaultListener = lineEventListener;
    }
}
