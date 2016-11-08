package ru.sbrf.cache.impl;

import java.util.function.Function;

public interface Cache<K, V>  {

    V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);
}
