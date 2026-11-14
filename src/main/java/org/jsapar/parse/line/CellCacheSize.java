package org.jsapar.parse.line;

/**
 * Decides the cell cache size to use for the cells of a schema line.
 */
public final class CellCacheSize {

    private CellCacheSize() {
    }

    /**
     * @param configured The configured cache size, negative if not configured.
     * @param occurs     The number of times the schema line may occur.
     * @return 0 if the line occurs only once, since a cache can never be hit. Otherwise the configured size, but not
     * more than the number of times the line can occur, or a negative value if not configured so that a default is
     * chosen per cell type.
     */
    public static int of(int configured, int occurs) {
        if (occurs <= 1)
            return 0;
        return configured < 0 ? configured : Math.min(configured, occurs - 1);
    }
}
