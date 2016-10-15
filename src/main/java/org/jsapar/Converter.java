package org.jsapar;

import org.jsapar.convert.LineManipulator;
import org.jsapar.model.Line;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;

import java.io.IOException;
import java.util.List;

/**
 * Reads from supplied parser and outputs each line to the composer. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method manipulate() of all added LineManipulators are called for each line that are
 * parsed successfully.
 * <p/>
 * For each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 * <p/>
 * If your want lines to be discarded from the output depending of their contents, add a LineManipulator that returns
 * false for the lines that should not be composed.
 *
 * Created by stejon0 on 2016-10-02.
 */
public class Converter {
    private Parser parser;
    private Composer composer;
    private List<LineManipulator> manipulators = new java.util.LinkedList<>();

    public Converter(Parser parser, Composer composer) {
        this.parser = parser;
        this.composer = composer;
    }

    public void addErrorEventListener(ErrorEventListener errorListener){
        this.parser.addErrorEventListener(errorListener);
        this.composer.addErrorEventListener(errorListener);
    }

    /**
     * Adds LineManipulator to this converter. All present line manipulators are executed for each
     * line.
     *
     * @param manipulator
     */
    public void addLineManipulator(LineManipulator manipulator) {
        manipulators.add(manipulator);
    }


    public void convert() throws IOException{
        parser.addLineEventListener(new LineForwardListener());
        parser.parse();
    }


    /**
     * Internal class for handling output of one line at a time while receiving parsing events.
     *
     * @author stejon0
     *
     */
    protected class LineForwardListener implements LineEventListener {

        public LineForwardListener() throws JSaParException {
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) {
            try {
                Line line = event.getLine();
                for (LineManipulator manipulator : manipulators) {
                    if(!manipulator.manipulate(line))
                        return;
                }
                composer.composeLine(line);
            } catch (IOException e) {
                throw new JSaParException("Failed to write to writer", e);
            }
        }
    }

    public Parser getParser() {
        return parser;
    }

    public Composer getComposer() {
        return composer;
    }
}
