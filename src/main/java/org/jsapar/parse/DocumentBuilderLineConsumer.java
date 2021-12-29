package org.jsapar.parse;

import org.jsapar.model.Document;
import org.jsapar.model.Line;

import java.util.function.Consumer;

/**
 * This line event listener can be used to build a document based on line events.
 * Use this class only if you are sure that the whole data can be parsed into memory. If the
 * data source is too big, most likely a OutOfMemory exception will be thrown. For large data sources use your own {@link Consumer} implementation instead and handle lines one by one.
 */
public class DocumentBuilderLineConsumer implements Consumer<Line>, AutoCloseable {
    private Document document;

    /**
     * Creates an event listener that add lines to the provided document.
     * @param document The document to add lines to.
     */
    public DocumentBuilderLineConsumer(Document document) {
        this.document = document;
    }

    /**
     * Creates an event listener that add lines to an internally created document. Use {@link #getDocument()} to
     * retrieve the instance when done.
     */
    public DocumentBuilderLineConsumer() {
        this.document = new Document();
    }

    @Override
    public void accept(Line line) {
        if(document == null)
            throw new IllegalStateException("The instance has been closed and cannot be used any more as event listener.");
        document.addLine(line);
    }

    /**
     * @return The document that was built by the line parsed events. Will return a Document with no lines in case there
     * were no lines. Will return null once the instance has been closed by calling the {@link #close()} method.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Closes this instance and detaches all internal storage. After calling this method on an instance, the instance cannot be
     * used as event listener any more and {@link #getDocument()} will return null.
     */
    @Override
    public void close()  {
        document = null;
    }
}
