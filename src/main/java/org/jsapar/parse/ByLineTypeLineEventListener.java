package org.jsapar.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This line event listener allows you to register different listeners for
 * different line types. You may only register one listener for each line type. If you need to have multiple listeners
 * for a particular line type, register an instance of {@link MulticastLineEventListener} for that line type.
 * <p>
 * The default listener will be called if no listener was registered for the line type.
 * Deprecated since 2.2. Use {@link ByLineTypeLineConsumer} instead
 */
@SuppressWarnings("WeakerAccess")
@Deprecated
public class ByLineTypeLineEventListener implements LineEventListener {

    /**
     * Current listeners
     */
    private final Map<String, LineEventListener> listeners = new HashMap<>();

    /**
     * The default listener that gets called if no other listener was registered for the line type. If no default
     * listener is explicitly set, the default behavior is to simply ignore the line parsed event.
     */
    private LineEventListener defaultListener = e -> {
    };

    @Override
    public void lineParsedEvent(LineParsedEvent event) {
        LineEventListener listener = listeners.get(event.getLine().getLineType());
        if (listener != null) {
            listener.lineParsedEvent(event);
        } else {
            defaultListener.lineParsedEvent(event);
        }
    }

    /**
     * Puts a line event listener for the specified line type, replacing any existing listener.
     *
     * @param lineType          The line type to match for this listener. Test is done by equals match.
     * @param lineEventListener The line event listener to put.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<LineEventListener> put(String lineType, LineEventListener lineEventListener) {
        return Optional.ofNullable(listeners.put(lineType, lineEventListener));
    }


    /**
     * Removes line event listener for specified line type
     * @param lineType  The line type to remove listeners for.
     * @return Optional with previously registered event listener for this line type. Optional.empty if no previous
     * listener was registered for this line type.
     */
    public Optional<LineEventListener> remove(String lineType){
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
     * @param lineEventListener The line event listener to use as default.
     */
    public void setDefault(LineEventListener lineEventListener) {
        this.defaultListener = lineEventListener;
    }
}
