package ru.sbrf.data.test.inherent;

import ru.sbrf.cache.Cacheable;

public interface A {
    @Cacheable
    void a();
}
