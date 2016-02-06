package org.jsapar.compose;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by stejon0 on 2016-01-30.
 */
public interface LineComposer {

    /**
     * Composes an output from a line. Each cell is identified from the schema by the name of the cell.
     * If the schema-cell has no name, the cell at the same position in the line is used under the
     * condition that it also lacks name.
     *
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found
     * and the cell at the same position lacks name, it is used instead.
     *
     * If no corresponding cell is found for a schema-cell, the cell is left empty. Sub-class
     * decides how to treat empty cells.
     *
     * @param line    The line to compose output of.
     * @throws IOException
     * @throws JSaParException
     */
    void compose(Line line) throws IOException, JSaParException;




}
