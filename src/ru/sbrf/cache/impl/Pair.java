package ru.sbrf.cache.impl;

import java.io.Serializable;

class Pair<K, V> implements Serializable {
    K key;
    V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
