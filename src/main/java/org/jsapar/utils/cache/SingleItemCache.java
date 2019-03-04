package org.jsapar.utils.cache;

public class SingleItemCache<K, V> implements Cache<K, V> {
    private K key;
    private V value;

    @Override
    public V get(K key) {
        return this.key != null && this.key.equals(key) ? value : null;
    }

    @Override
    public void put(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
