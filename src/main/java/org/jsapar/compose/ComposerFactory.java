package org.jsapar.compose;

import org.jsapar.JSaParException;
import org.jsapar.schema.Schema;

import java.io.Writer;

/**
 * Created by stejon0 on 2016-02-06.
 */
public interface ComposerFactory {
    Composer makeComposer(Schema schema, Writer writer) throws JSaParException;
}
