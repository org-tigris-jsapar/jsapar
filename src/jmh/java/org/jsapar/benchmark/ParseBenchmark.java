package org.jsapar.benchmark;

import org.jsapar.TextParser;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.Schema;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

/**
 * Parsing throughput. Score is lines per second.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@OperationsPerInvocation(BenchmarkData.LINES)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@State(Scope.Benchmark)
public class ParseBenchmark {

    @Param
    public BenchmarkData.Data data;

    /**
     * See {@link TextParseConfig#setMaxCellCacheSize(int)}. Negative means not set, so that the default is used. Run
     * with for instance -p cacheSize=-1,0,1,100 to compare.
     */
    @Param({"-1"})
    public int cacheSize;

    private TextParseConfig config;

    private Schema<?> schema;
    private String input;

    @Setup
    public void setup() throws IOException {
        schema = data.schema();
        config = new TextParseConfig();
        if (cacheSize >= 0)
            config.setMaxCellCacheSize(cacheSize);
        input = data.input();
        data.lines(input); // Verifies that the input parses as expected.
    }

    @Benchmark
    public long parse(Blackhole blackhole) throws IOException {
        return new TextParser(schema, config).parseForEach(new StringReader(input), blackhole::consume);
    }
}
