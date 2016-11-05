package org.jsapar.parse.fixed;

import org.jsapar.parse.ParseConfig;
import org.jsapar.schema.FixedWidthSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates fixed width line parsers based on schema.
 */
public class FWLineParserFactory {
    private List<FWLineParserMatcher> lineParserMatchers;

    public FWLineParserFactory(FixedWidthSchema schema, ParseConfig config) {
        lineParserMatchers = schema.getFixedWidthSchemaLines().stream()
                .map(schemaLine -> new FWLineParserMatcher(schemaLine, config)).collect(Collectors.toList());
    }

    /**
     * @param reader A buffered reader to read input from
     * @return A {@link LineParserResult} that contains both the lineParser that can be used and a result code indicating
     * the status of the input.
     * @throws IOException
     */
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
            else if(lineParserResult == LineParserMatcherResult.NO_OCCURS)
                iter.remove();

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

    /**
     * Internal class used to be able to return both a match result and a line parser as return value.
     */
    public class LineParserResult{
        public FixedWidthLineParser lineParser;
        public LineParserMatcherResult result;

        public LineParserResult(FixedWidthLineParser lineParser, LineParserMatcherResult result) {
            this.lineParser = lineParser;
            this.result = result;
        }
    }
}
