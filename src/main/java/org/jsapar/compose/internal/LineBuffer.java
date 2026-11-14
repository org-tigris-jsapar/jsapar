package org.jsapar.compose.internal;

import java.io.IOException;
import java.io.Writer;

/**
 * Collects the output of one line so that it can be written to the target writer in a single call. Composing a line
 * makes many small writes, one or more per cell, which can be expensive on writers that synchronize, encode or check
 * state on each call.
 * <p>
 * Not thread safe. Calls to flush and close have no effect.
 */
final class LineBuffer extends Writer {
    private char[] buffer = new char[256];
    private int size = 0;

    @Override
    public void write(int c) {
        ensureCapacity(size + 1);
        buffer[size++] = (char) c;
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        ensureCapacity(size + length);
        System.arraycopy(chars, offset, buffer, size, length);
        size += length;
    }

    @Override
    public void write(String s) {
        write(s, 0, s.length());
    }

    @Override
    public void write(String s, int offset, int length) {
        ensureCapacity(size + length);
        s.getChars(offset, offset + length, buffer, size);
        size += length;
    }

    @Override
    public Writer append(CharSequence csq) {
        String s = String.valueOf(csq);
        write(s, 0, s.length());
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) {
        return append(String.valueOf(csq).subSequence(start, end));
    }

    @Override
    public Writer append(char c) {
        write(c);
        return this;
    }

    private void ensureCapacity(int required) {
        if (required > buffer.length) {
            char[] newBuffer = new char[Math.max(required, buffer.length * 2)];
            System.arraycopy(buffer, 0, newBuffer, 0, size);
            buffer = newBuffer;
        }
    }

    /**
     * Writes the buffered content to the writer and clears this buffer.
     *
     * @param writer The writer to write to.
     * @throws IOException If the writer fails.
     */
    void writeTo(Writer writer) throws IOException {
        if (size > 0)
            writer.write(buffer, 0, size);
        size = 0;
    }

    /**
     * Discards any buffered content.
     */
    void clear() {
        size = 0;
    }

    int size() {
        return size;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
