package org.jsapar.utils.cache;

public class TwoItemsCache<K, V> implements Cache<K, V> {
    private K newKey;
    private V newValue;
    private K oldKey;
    private V oldValue;

    @Override
    public V get(K key) {
        if(newKey != null){
            return newKey.equals(key) ? newValue : (oldKey != null && oldKey.equals(key)? oldValue : null );
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        this.oldKey = newKey;
        this.oldValue = newValue;
        this.newKey = key;
        this.newValue = value;
    }
}
