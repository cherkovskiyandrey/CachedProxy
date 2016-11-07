package ru.sbrf.data;

import ru.sbrf.Cacheable;

public interface A {

    @Cacheable
    int sum(int i, int i1);

    int div(int i, int i1);
}
