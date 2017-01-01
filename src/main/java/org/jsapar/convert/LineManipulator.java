/**
 * 
 */
package org.jsapar.convert;

import org.jsapar.model.Line;

/**
 * You can register a class that implements the LineManipulator interface to the {@link ConvertTask}. Then, every time there is a
 * line event from the parser, the method manipulate gets called with a Line object. You may now modify the Line object
 * within the method and the modified values are then the ones that are fed to the output schema. You can register more than
 * one LineManipulator to the same ConvertTask and they will get called in the same order as they were registered.
 * 
 * @author stejon0
 *
 */
public interface LineManipulator {

    /**
     * Gets called every time that a line parsing event is fired within a converter. Changes of the line instance will
     * be reflected in the output.
     * 
     * @param line The line to manipulate
     * @return If this method returns false, the line will be omitted from the output.
     */
    boolean manipulate(Line line);

}
