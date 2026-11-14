package org.jsapar.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextParseConfigTest {

    @Test
    void maxCellCacheSize_default() {
        assertEquals(TextParseConfig.AUTOMATIC_CELL_CACHE_SIZE, new TextParseConfig().getMaxCellCacheSize());
    }

    @Test
    void setMaxCellCacheSize_withinRange() {
        TextParseConfig config = new TextParseConfig();
        config.setMaxCellCacheSize(10);
        assertEquals(10, config.getMaxCellCacheSize());
    }

    @Test
    void setMaxCellCacheSize_zeroDisables() {
        TextParseConfig config = new TextParseConfig();
        config.setMaxCellCacheSize(0);
        assertEquals(0, config.getMaxCellCacheSize());
    }

    @Test
    void setMaxCellCacheSize_negativeIsZero() {
        TextParseConfig config = new TextParseConfig();
        config.setMaxCellCacheSize(-5);
        assertEquals(0, config.getMaxCellCacheSize());
    }

    @Test
    void setMaxCellCacheSize_cappedAt100() {
        TextParseConfig config = new TextParseConfig();
        config.setMaxCellCacheSize(1000);
        assertEquals(100, config.getMaxCellCacheSize());
    }
}
