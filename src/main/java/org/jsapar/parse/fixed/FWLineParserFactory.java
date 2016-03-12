package org.jsapar.parse.fixed;

import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stejon0 on 2016-03-12.
 */
public class FWLineParserFactory {
    FixedWidthSchema schema;
    int                       count              =0;
    List<FWLineParserMatcher> lineParserMatchers;

    public FWLineParserFactory(FixedWidthSchema schema) {
        this.schema = schema;
        lineParserMatchers = new LinkedList<>();
        for (FixedWidthSchemaLine schemaLine : schema.getFixedWidthSchemaLines()) {
            lineParserMatchers.add(new FWLineParserMatcher(schemaLine));
        }
    }

    public FixedWidthLineParser makeLineParser(BufferedReader reader) throws IOException {
        if(lineParserMatchers.isEmpty())
            return null;
        Iterator<FWLineParserMatcher> iter = lineParserMatchers.iterator();
        boolean first = true;
        while(iter.hasNext()){
            FWLineParserMatcher currentMatcher = iter.next();
            FixedWidthLineParser lineParser = currentMatcher.makeLineParserIfMatching(reader);
            if(lineParser != null) {
                if(!currentMatcher.isOccursLeft())
                    // No longer needed
                    iter.remove();
                else if (!first){
                    // Move current matching line first in list so that it is tested first next time.
                    iter.remove();
                    lineParserMatchers.add(0, currentMatcher);
                }
                return lineParser;
            }
            first = false;
        }
        return null;
    }
}
