package ru.sbrf.data;

import ru.sbrf.cache.Cacheable;

public interface A {

    @Cacheable(Cacheable.Type.FILE)
    int sum(int i, int i1);

    @Cacheable
    int div(int i, int i1);

    @Cacheable(Cacheable.Type.FILE)
    String sumStr(String s, String s2);

    @Cacheable(Cacheable.Type.FILE)
    Person marry(Person s, Person s2);
}
