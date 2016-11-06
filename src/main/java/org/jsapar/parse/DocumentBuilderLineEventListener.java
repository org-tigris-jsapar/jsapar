package org.jsapar.parse;

import org.jsapar.model.Document;

/**
 * This line event listener can be used to build a document based on line events.
 * Use this class only if you are sure that the whole file can be parsed into memory. If the
 * file is too big, a OutOfMemory exception will be thrown. For large files use any of the Parser implementations
 * directly instead. Dispose instances of this class when they have been used once.
 */
public class DocumentBuilderLineEventListener implements LineEventListener, AutoCloseable {
    private Document document = new Document();

    @Override
    public void lineParsedEvent(LineParsedEvent event) {
        if(document == null)
            throw new IllegalStateException("The instance has been closed and cannot be used any more as event listener.");
        document.addLine(event.getLine());
    }

    /**
     * @return The document that was built by the line parsed events. Will return a Document with no lines in case there
     * were no lines. Will return null once the instance has been closed by calling the {@link #close()} method.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Closes this instance and clears all internal storage. After calling this method on an instance, the instance cannot be
     * used as event listener any more and {@link #getDocument()} will return null.
     */
    @Override
    public void close()  {
        document = null;
    }
}
