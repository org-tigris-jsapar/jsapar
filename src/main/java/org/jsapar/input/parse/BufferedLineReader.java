package org.jsapar.input.parse;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.JSaParException;

public class BufferedLineReader extends ReaderLineReader {

    private String buffer;

    public BufferedLineReader(String lineSeparator, Reader reader) {
        super(lineSeparator, reader);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String readLine() throws IOException, JSaParException {
        try {
            if (buffer != null) {
                return buffer;
            }
            return super.readLine();
        } finally {
            buffer = null;
        }
    }
    
    public void putBackLine(String line){
        if(buffer != null)
            throw new IllegalStateException("Only one line can be put back in buffered line reader.");
        buffer = line;
    }

}
