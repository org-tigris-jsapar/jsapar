package org.jsapar.compose;

import org.jsapar.error.JSaParException;
import org.jsapar.schema.Schema;

import java.io.Writer;

/**
 * Interface for factory class that creates schema composer based on schema.
 */
public interface ComposerFactory {

    /** This method should return a {@link SchemaComposer} instance that is suitable to compose using provided schema.
     * @param schema The schema to use while composing
     * @param writer The writer to write output to.
     * @return a {@link SchemaComposer} instance that is suitable to compose using provided schema
     * @throws JSaParException
     */
    SchemaComposer makeComposer(Schema schema, Writer writer) throws JSaParException;
}
