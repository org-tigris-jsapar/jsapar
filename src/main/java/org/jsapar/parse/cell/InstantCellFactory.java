package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.InstantCell;
import org.jsapar.text.format.DateTimeFormat;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Parses instant time values into {@link Cell} objects
 */
public class InstantCellFactory extends AbstractDateTimeCellFactory {

    public InstantCellFactory() {
        super(DateTimeFormatter.ISO_INSTANT, CellType.INSTANT);
    }

    @Override
    public Cell makeCell(String name, String value, Format<TemporalAccessor> format) throws ParseException {
        if (format == null)
            format = getDefaultFormat();
        if(format instanceof DateTimeFormat){
            DateTimeFormat dateTimeFormat = ((DateTimeFormat) format);
            TemporalAccessor temporalValue = format.parse(value);
            if(temporalValue.isSupported(ChronoField.INSTANT_SECONDS))
                return new InstantCell(name, Instant.from(temporalValue));
            else
                return new InstantCell(name,
                        LocalDateTime.from(temporalValue).atZone(dateTimeFormat.getZoneId()).toInstant());
        }

        // Give it a try anyway. This will probably fail.
        return new InstantCell(name, Instant.from(format.parse(value)));
    }

    @Override
    public Format<TemporalAccessor> makeFormat(Locale locale, String pattern) {
        // TODO When not given time zone explicitly, it should be given by the parser/composer instead of system default
        if(pattern==null)
            return Format.ofInstantInstance(DateTimeFormatter.ISO_INSTANT, ZoneId.systemDefault());
        String[] parts = pattern.split("\\h*[@|]\\h*");
        if(parts.length == 2)
            return Format.ofInstantInstance(DateTimeFormatter.ofPattern(parts[0], locale), ZoneId.of(parts[1]));
        else
            return Format.ofInstantInstance(DateTimeFormatter.ofPattern(pattern, locale), ZoneId.systemDefault());
    }
}
