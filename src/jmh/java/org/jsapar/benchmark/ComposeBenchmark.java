package org.jsapar.benchmark;

import org.jsapar.TextComposer;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Composing throughput. Score is lines per second. {@link #compose()} writes to a discarding writer so that only the
 * library is measured, {@link #composeEncoded()} includes buffering and UTF-8 encoding as when writing to a file.
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
    public BenchmarkData.Data data;

    private Schema<?> schema;
    private List<Line> lines;

    @Setup
    public void setup() throws IOException {
        schema = data.schema();
        lines = data.lines(data.input());
    }

    @Benchmark
    public void compose() {
        TextComposer composer = new TextComposer(schema, Writer.nullWriter());
        for (Line line : lines)
            composer.composeLine(line);
    }

    /**
     * Composes through a buffered UTF-8 encoding writer, like when writing to a file, but discards the bytes.
     */
    @Benchmark
    public void composeEncoded() throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream(), StandardCharsets.UTF_8))) {
            TextComposer composer = new TextComposer(schema, writer);
            for (Line line : lines)
                composer.composeLine(line);
        }
    }
}
