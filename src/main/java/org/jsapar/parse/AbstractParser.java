package org.jsapar.parse;

import org.jsapar.error.ErrorEventListener;

import java.io.IOException;

/**
 * Created by stejon0 on 2017-01-01.
 */
public class AbstractParser {

    ErrorEventListener errorEventListener;

    public void setErrorEventListener(ErrorEventListener errorEventListener) {
        this.errorEventListener = errorEventListener;
    }

    protected void execute(ParseTask parseTask, LineEventListener lineEventListener) throws IOException {
        parseTask.setLineEventListener(lineEventListener);
        if(errorEventListener != null)
            parseTask.setErrorEventListener(errorEventListener);
        parseTask.execute();
    }
}
