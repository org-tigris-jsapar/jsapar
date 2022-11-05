package org.jsapar.compose.string;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Saves all lines that was composed into a list of strings that can be retrieved by calling {@link #getCollected()}
 * when done composing.
 */
public final class CollectingStringConsumer implements StringComposedConsumer {
    private final List< List <String> > collected = new ArrayList<>();

    /**
     * @return All composed lines.
     */
    public List< List<String> > getCollected() {
        return collected;
    }

    /**
     * Called every time that a string, is successfully composed.
     * This implementation saves the composed strings to an internal list to be retrieved later by calling
     * {@link #getCollected()}
     */
    @Override
    public void accept(Stream<String> cellStream, String lineType, long lineNumber) {
        collected.add(cellStream.collect(Collectors.toList()));
    }

    /**
     * Clears all collected lines.
     */
    public void clear(){
        collected.clear();
    }

    /**
     * @return Number of lines collected so far.
     */
    public int size(){
        return collected.size();
    }

    /**
     * @return True if there were no collected lines, false otherwise.
     */
    public boolean isEmpty(){
        return collected.isEmpty();
    }

}
