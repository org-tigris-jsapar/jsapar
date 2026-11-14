package org.jsapar.utils.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SingleItemCacheTest {


    @Test
    public void get() {
        Cache<String, String> cache = new SingleItemCache<>();
        assertNull(cache.get("one"));
        cache.put("one", "1");
        assertEquals("1", cache.get("one"));
        cache.put("two", "2");
        assertNull(cache.get("one"));
        assertEquals("2", cache.get("two"));
        cache.put("three", "3");
        assertNull(cache.get("one"));
        assertNull(cache.get("two"));
        assertEquals("3", cache.get("three"));
    }

}