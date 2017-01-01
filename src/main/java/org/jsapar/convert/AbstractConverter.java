package org.jsapar.convert;

import org.jsapar.ConvertTask;
import org.jsapar.error.ErrorEventListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by stejon0 on 2016-12-30.
 */
public abstract class AbstractConverter {

    private List<LineManipulator> manipulators = new java.util.LinkedList<>();
    private ErrorEventListener errorListener;

    /**
     * Adds LineManipulator to this converter. All present line manipulators are executed for each
     * line.
     *
     * @param manipulator The line manipulator to add.
     */
    public void addLineManipulator(LineManipulator manipulator) {
        manipulators.add(manipulator);
    }

    /**
     * @param errorListener
     */
    public void setErrorEventListener(ErrorEventListener errorListener) {
        this.errorListener = errorListener;
    }

    protected void execute(ConvertTask convertTask) throws IOException {
        manipulators.forEach(convertTask::addLineManipulator);
        if(errorListener != null)
            convertTask.setErrorEventListener(errorListener);
        convertTask.execute();
    }
}
