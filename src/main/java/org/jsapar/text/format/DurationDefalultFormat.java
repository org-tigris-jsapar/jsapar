package org.jsapar.text.format;

import org.jsapar.model.CellType;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.Duration;

public class DurationDefalultFormat implements Format<Duration> {

    public DurationDefalultFormat() {

    }

    @Override
    public CellType cellType() {
        return CellType.DURATION;
    }

    @Override
    public Duration parse(String stringValue) throws ParseException {
        
        return Duration.parse(stringValue);
    }

    @Override
    public String format(Object value) throws IllegalArgumentException {
        return value.toString();
    }
}
