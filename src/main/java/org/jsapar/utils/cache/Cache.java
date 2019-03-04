package org.jsapar.utils.cache;

/**
 * Saves the value related to a number of keys. There is no expiration time but if max size is reached old items are
 * discarded as old values are entered.
 * @param <K> The key type
 * @param <V> The value type
 */
public interface Cache<K, V> {
    /**
     * @param key The key
     * @return A value stored in cache or null if there is none.
     */
    V get(K key);


    /**
     * Put a new value to the cache
     *
     * @param key   The key
     * @param value The value
     */
    void put(K key, V value);

    /**
     * Creates a new cache optimized according to supplied max size.
     *
     * @param maxSize The max cache size to optimize for.
     * @param <K>     The key type
     * @param <V>     The value type
     * @return A new cache optimized according to supplied max size.
     */
    static <K, V> Cache<K, V> ofMaxSize(int maxSize) {

        switch (maxSize) {
            case 0:
                return new DisabledCache<>();
            case 1:
                return new SingleItemCache<>();
            case 2:
                return new TwoItemsCache<>();
            default:
                return new LimitedSizeCache<>(maxSize);
        }
    }
}
