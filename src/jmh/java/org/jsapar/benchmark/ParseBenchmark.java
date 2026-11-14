package org.jsapar.benchmark;

import org.jsapar.TextParser;
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

    private Schema<?> schema;
    private String input;

    @Setup
    public void setup() throws IOException {
        schema = data.schema();
        input = data.input();
        data.lines(input); // Verifies that the input parses as expected.
    }

    @Benchmark
    public long parse(Blackhole blackhole) throws IOException {
        return new TextParser(schema).parseForEach(new StringReader(input), blackhole::consume);
    }
}
