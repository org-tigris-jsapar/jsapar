package org.jsapar.utils.cache;

public interface Cache<K, V> {
    /**
     * @param key The key
     * @return A value stored in cache or null if there is none.
     */
    V get(K key);


    /**
     * Put a new value to the cache
     * @param key The key
     * @param value The value
     */
    void put(K key, V value);

    /**
     * Creates a new cache optimized according to supplied max size.
     * @param maxSize The max cache size to optimize for.
     * @param <K> The key type
     * @param <V> The value type
     * @return A new cache optimized according to supplied max size.
     */
    static <K, V> Cache<K, V> ofMaxSize(int maxSize){
        if(maxSize == 0)
            return new DisabledCache<>();
        if(maxSize == 1)
            return new SingleItemCache<>();
        else
            return new LimitedSizeCache<>(maxSize);
    }
}
