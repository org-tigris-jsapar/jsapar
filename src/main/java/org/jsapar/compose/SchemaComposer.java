package org.jsapar.compose;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by stejon0 on 2016-01-24.
 */
public interface SchemaComposer {

    /**
     * This method should only be called by a TextComposer class. Don't use this directly in your code.
     * Use a TextComposer instead.
     *
     * @param iterator
     * @throws IOException
     * @throws JSaParException
     */
    void compose(Iterator<Line> iterator) throws IOException, JSaParException;

    /**
     * Called before compose() in order to set up or write file header.
     *
     * @throws IOException
     * @throws JSaParException
     */
    void beforeCompose() throws IOException, JSaParException;

    /**
     * Called after compose() in order to clean up or write file footer.
     *
     * @throws IOException
     * @throws JSaParException
     */
    void afterCompose() throws IOException, JSaParException;

    /**
     * Create a schema line composer for this schema composer.
     *
     * @param line
     * @return A schema line composer.
     */
    boolean composeLine(Line line) throws IOException;
}
