package org.jsapar.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stejon0 on 2016-10-09.
 */
public class ErrorEventSource  implements ErrorEventListener{

    private List<ErrorEventListener> eventListeners = new ArrayList<>();
    private ErrorEventListener defaultEventListener = new ExceptionErrorEventListener();

    public void addEventListener(ErrorEventListener errorEventListener){
        if (errorEventListener == null)
            return;
        eventListeners.add(errorEventListener);
    }

    public void removeEventListener(ErrorEventListener errorEventListener){
        eventListeners.remove(errorEventListener);
    }

    @Override
    public void errorEvent(ErrorEvent event) {
        if(eventListeners.isEmpty()){
            defaultEventListener.errorEvent(event);
            return;
        }

        for (ErrorEventListener eventListener : eventListeners) {
            eventListener.errorEvent(event);
        }
    }
}
