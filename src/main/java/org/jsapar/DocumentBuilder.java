package org.jsapar;

import org.jsapar.model.Document;
import org.jsapar.parse.ErrorEventListener;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;

import java.io.IOException;

/**
 * Created by stejon0 on 2016-08-14.
 */
public class DocumentBuilder {
    private Document document = new Document();
    private Parser parser;

    public DocumentBuilder(Parser parser, ErrorEventListener errorEventListener) {
        addErrorEventListener(errorEventListener);
    }

    public DocumentBuilder(Parser parser) {
    }

    public void addErrorEventListener(ErrorEventListener errorEventListener){
        parser.addErrorEventListener(errorEventListener);
    }

    public Document build() throws IOException, JSaParException {
        parser.addLineEventListener(new LineEventListener() {

            @Override
            public void lineParsedEvent(LineParsedEvent event) {
                document.addLine(event.getLine());
            }
        });

        parser.parse();
        return this.document;
    }
}
