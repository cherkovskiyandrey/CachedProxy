package ru.sbrf.cache.impl.locks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class ConcurrentLazyResourceHolder<K, V> implements LazyResourceHolder<K, V> {
    private final Supplier<V> valueFactory;
    private final ConcurrentMap<K, Item<V>> storage = new ConcurrentHashMap<>();

    public ConcurrentLazyResourceHolder(Supplier<V> lockFactory) {
        this.valueFactory = lockFactory;
    }

    @Override
    public V getOrRecreate(K key) {
        return storage.compute(key, (k, v) -> {
            if(v == null) {
                return new Item<>(valueFactory.get(), 1);
            }
            v.counter++;
            return v;
        }).value;
    }

    @Override
    public void release(K key, V value) {
        storage.compute(key, (k, v) -> {
            if(--v.counter == 0) {
                return null;
            }
            return v;
        });
    }

    private static class Item<U> {
        U value;
        int counter;

        public Item(U value, int counter) {
            this.value = value;
            this.counter = counter;
        }
    }
}
