package org.jsapar;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Document;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.parse.Parser;

import java.io.IOException;

/**
 * Parses an input and bulds a Document while parsing. If there is an error while
 * parsing, an event is generated to the registered error event listener. If no error event listener is registered, a
 * runtime exception will be thrown. <br/>
 * Use this class only if you are sure that the whole file can be parsed into memory. If the
 * file is too big, a OutOfMemory exception will be thrown. For large files use any of the Parser implementations
 * directly instead. Dispose instances of this class when they have been used once.
 */
public class DocumentBuilder {
    private Parser parser;

    public DocumentBuilder(Parser parser, ErrorEventListener errorEventListener) {
        this.parser = parser;
        addErrorEventListener(errorEventListener);
    }

    public DocumentBuilder(Parser parser) {
        this.parser = parser;
    }

    public void addErrorEventListener(ErrorEventListener errorEventListener) {
        parser.addErrorEventListener(errorEventListener);
    }

    public Document build() throws IOException, JSaParException {
        try (DocumentBuilderLineEventListener eventListener = new DocumentBuilderLineEventListener()) {
            parser.addLineEventListener(eventListener);
            parser.parse();
            return eventListener.getDocument();
        }
    }
}
