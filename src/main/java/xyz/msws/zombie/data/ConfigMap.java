package xyz.msws.zombie.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ConfigMap<K, V> implements Map<K, V> {
    private final Map<K, V> map;
    private final Class<K> key;
    private final Class<V> value;

    public ConfigMap(Map<K, V> map, Class<K> key, Class<V> value) {
        this.map = map;
        this.key = key;
        this.value = value;
    }

    public Class<K> getKey() {
        return key;
    }

    public Class<V> getValue() {
        return value;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsKey(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    public Object putObject(Object key, Object value) {
        return put((K) key, (V) value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
