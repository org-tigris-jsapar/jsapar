package org.jsapar;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.*;

import java.io.IOException;

/**
 * Created by stejon0 on 2016-08-14.
 */
public interface Parser {

    void addLineEventListener(LineEventListener eventListener);

    void addErrorEventListener(ErrorEventListener errorEventListener);

    void parse() throws JSaParException, IOException;

}
