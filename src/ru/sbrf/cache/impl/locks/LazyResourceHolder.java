package ru.sbrf.cache.impl.locks;

public interface LazyResourceHolder<K, V> {

    V getOrRecreate(K key);

    void release(K key, V value);
}
