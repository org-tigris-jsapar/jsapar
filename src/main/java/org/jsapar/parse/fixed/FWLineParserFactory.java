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
    private FixedWidthSchema schema;
    private List<FWLineParserMatcher> lineParserMatchers;

    public FWLineParserFactory(FixedWidthSchema schema) {
        this.schema = schema;
        lineParserMatchers = new LinkedList<>();
        for (FixedWidthSchemaLine schemaLine : schema.getFixedWidthSchemaLines()) {
            lineParserMatchers.add(new FWLineParserMatcher(schemaLine));
        }
    }

    public LineParserResult makeLineParser(BufferedReader reader) throws IOException {
        if(lineParserMatchers.isEmpty())
            return null;
        Iterator<FWLineParserMatcher> iter = lineParserMatchers.iterator();
        boolean first = true;
        boolean eof = true;
        while(iter.hasNext()){
            FWLineParserMatcher currentMatcher = iter.next();
            LineParserMatcherResult lineParserResult = currentMatcher.testLineParserIfMatching(reader);
            if(lineParserResult == LineParserMatcherResult.SUCCESS) {
                if(!currentMatcher.isOccursLeft())
                    // No longer needed
                    iter.remove();
                else if (!first){
                    // Move current matching line first in list so that it is tested first next time.
                    iter.remove();
                    lineParserMatchers.add(0, currentMatcher);
                }
                return new LineParserResult(currentMatcher.getLineParser(), LineParserMatcherResult.SUCCESS);
            }
            if(lineParserResult != LineParserMatcherResult.EOF){
                eof = false;
            }
            first = false;
        }
        LineParserMatcherResult reason;
        if(eof)
            reason =  LineParserMatcherResult.EOF;
        else if(lineParserMatchers.isEmpty())
            reason = LineParserMatcherResult.NO_OCCURS;
        else
            reason = LineParserMatcherResult.NOT_MATCHING;
        return new LineParserResult(null, reason);
    }

    public boolean isEmpty() {
        return lineParserMatchers.isEmpty();
    }

    public class LineParserResult{
        public FixedWidthLineParser lineParser;
        public LineParserMatcherResult result;

        public LineParserResult(FixedWidthLineParser lineParser, LineParserMatcherResult result) {
            this.lineParser = lineParser;
            this.result = result;
        }
    }
}
