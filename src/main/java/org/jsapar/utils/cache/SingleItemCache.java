package org.jsapar.utils.cache;

/**
 * A cache storing only one single value. Calling {@link #put(Object, Object)} consecutive times replaces the existing cached value with a new one.
 * @param <K> The key type
 * @param <V> The value type
 */
public class SingleItemCache<K, V> implements Cache<K, V> {
    private K key;
    private V value;

    @Override
    public V get(K key) {
        return key.equals(this.key) ? value : null;
    }

    @Override
    public void put(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
