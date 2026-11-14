package org.jsapar.parse.cell;

import org.jsapar.TstGender;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.parse.line.CellCacheSize;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.text.TextParseConfig;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A cache hit returns the same cell instance as the previous parse of the same text.
 */
class CellParserCacheTest {

    private static final Consumer<JSaParException> FAIL = e -> fail(e);
    private static final int AUTOMATIC = TextParseConfig.AUTOMATIC_CELL_CACHE_SIZE;

    private static boolean cached(CsvSchemaCell schemaCell, int cacheSize, String... values) {
        CellParser<CsvSchemaCell> parser = CellParser.ofSchemaCell(schemaCell, cacheSize);
        Cell<?> first = parser.parse(values[0], FAIL);
        Cell<?> last = first;
        for (int i = 1; i < values.length; i++)
            last = parser.parse(values[i], FAIL);
        assertEquals(first, last);
        return first == last;
    }

    @Test
    void string_notCachedByDefault() {
        assertFalse(cached(CsvSchemaCell.builder("s").build(), AUTOMATIC, new String("a"), new String("a")));
    }

    @Test
    void string_cachedWhenConfigured() {
        assertTrue(cached(CsvSchemaCell.builder("s").build(), 1, new String("a"), new String("a")));
    }

    @Test
    void character_notCachedByDefault() {
        assertFalse(cached(CsvSchemaCell.builder("c").withType(CellType.CHARACTER).build(), AUTOMATIC, "x", "x"));
    }

    @Test
    void date_cachedByDefault() {
        assertTrue(cached(CsvSchemaCell.builder("d").withType(CellType.LOCAL_DATE).build(), AUTOMATIC, "2024-01-01", "2024-01-01"));
    }

    @Test
    void date_notCachedWhenDisabled() {
        assertFalse(cached(CsvSchemaCell.builder("d").withType(CellType.LOCAL_DATE).build(), 0, "2024-01-01", "2024-01-01"));
    }

    @Test
    void boolean_twoValuesByDefault() {
        assertTrue(cached(CsvSchemaCell.builder("b").withType(CellType.BOOLEAN).build(), AUTOMATIC, "true", "false", "true"));
    }

    @Test
    void boolean_notCachedWhenDisabled() {
        assertFalse(cached(CsvSchemaCell.builder("b").withType(CellType.BOOLEAN).build(), 0, "true", "true"));
    }

    @Test
    void boolean_neverMoreThanTwo() {
        BooleanCellFactory factory = new BooleanCellFactory();
        CsvSchemaCell schemaCell = CsvSchemaCell.builder("b").withType(CellType.BOOLEAN).build();
        assertEquals(2, factory.actualCacheMaxSize(schemaCell, 100));
        assertEquals(1, factory.actualCacheMaxSize(schemaCell, 1));
        assertEquals(0, factory.actualCacheMaxSize(schemaCell, 0));
    }

    @Test
    void enum_lastValueByDefault() {
        CsvSchemaCell schemaCell = CsvSchemaCell.builder("e").withEnumFormat(TstGender.class).build();
        assertTrue(cached(schemaCell, AUTOMATIC, "M", "M"));
        assertFalse(cached(schemaCell, AUTOMATIC, "M", "F", "M"));
    }

    @Test
    void enum_notCachedWhenDisabled() {
        CsvSchemaCell schemaCell = CsvSchemaCell.builder("e").withEnumFormat(TstGender.class).build();
        assertFalse(cached(schemaCell, 0, "M", "M"));
    }

    @Test
    void enum_neverMoreThanNumberOfValues() {
        CsvSchemaCell schemaCell = CsvSchemaCell.builder("e").withEnumFormat(TstGender.class).build();
        int values = ((org.jsapar.text.format.EnumFormat<?>) schemaCell.getFormat()).numberOfTextValues();
        assertEquals(values, new EnumCellFactory<>().actualCacheMaxSize(schemaCell, 100));
        assertEquals(0, new EnumCellFactory<>().actualCacheMaxSize(schemaCell, 0));
    }

    @Test
    void cellCacheSize() {
        assertEquals(0, CellCacheSize.of(AUTOMATIC, 1));
        assertEquals(0, CellCacheSize.of(10, 1));
        assertEquals(AUTOMATIC, CellCacheSize.of(AUTOMATIC, Integer.MAX_VALUE));
        assertEquals(10, CellCacheSize.of(10, Integer.MAX_VALUE));
        assertEquals(2, CellCacheSize.of(10, 3));
        assertEquals(0, CellCacheSize.of(0, Integer.MAX_VALUE));
    }
}
