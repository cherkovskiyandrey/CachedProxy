package ru.sbrf.cache.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class MemoryCache<K, V> implements Cache<K, V> {
    private final ConcurrentMap<K, V> core = new ConcurrentHashMap<>();

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return core.computeIfAbsent(key, mappingFunction);
    }
}
