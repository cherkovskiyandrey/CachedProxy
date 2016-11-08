package ru.sbrf.cache.impl;


import ru.sbrf.cache.Cacheable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public enum CacheFactory {
    INSTANCE;

    private final Map<Cacheable.Type, Supplier<Cache<ComposedKey, Object>>> cacheFactory;

    CacheFactory() {
        final Map<Cacheable.Type, Supplier<Cache<ComposedKey, Object>>> map = new HashMap<>();
        map.put(Cacheable.Type.MEMORY, MemoryCache::new);
        map.put(Cacheable.Type.FILE, FileCache::new);
        cacheFactory = Collections.unmodifiableMap(map);
    }

    public Cache<ComposedKey, Object> newInstance(Cacheable.Type type) throws UnsupportedCacheTypeException {
        Objects.requireNonNull(type);
        final Supplier<Cache<ComposedKey, Object>> f = cacheFactory.get(type);
        if(f == null) {
            throw new UnsupportedCacheTypeException(type.toString());
        }
        return f.get();
    }
}
