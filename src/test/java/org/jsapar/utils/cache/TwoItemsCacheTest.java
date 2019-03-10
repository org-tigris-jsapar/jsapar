package org.jsapar.utils.cache;

import org.junit.Test;

import static org.junit.Assert.*;

public class TwoItemsCacheTest {

    @Test
    public void get() {
        Cache<String, String> cache = new TwoItemsCache<>();
        assertNull(cache.get("one"));
        cache.put("one", "1");
        assertEquals("1", cache.get("one"));
        cache.put("two", "2");
        assertEquals("1", cache.get("one"));
        assertEquals("2", cache.get("two"));
        cache.put("three", "3");
        assertNull(cache.get("one"));
        assertEquals("2", cache.get("two"));
        assertEquals("3", cache.get("three"));
    }

}