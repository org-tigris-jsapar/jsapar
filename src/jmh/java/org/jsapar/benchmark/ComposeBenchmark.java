package org.jsapar.benchmark;

import org.jsapar.TextComposer;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Composing throughput. Score is lines per second. Output goes to a discarding writer so that only the library is
 * measured.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@OperationsPerInvocation(BenchmarkData.LINES)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@State(Scope.Benchmark)
public class ComposeBenchmark {

    @Param
    public BenchmarkData.Format format;

    @Param
    public BenchmarkData.Content content;

    private Schema<?> schema;
    private List<Line> lines;

    @Setup
    public void setup() throws IOException {
        schema = BenchmarkData.schema(format, content);
        lines = BenchmarkData.lines(format, content, BenchmarkData.input(format, content));
    }

    @Benchmark
    public void compose() {
        TextComposer composer = new TextComposer(schema, Writer.nullWriter());
        for (Line line : lines)
            composer.composeLine(line);
    }
}
