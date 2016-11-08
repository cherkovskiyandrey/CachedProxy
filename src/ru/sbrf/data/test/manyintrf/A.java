package ru.sbrf.data.test.manyintrf;

import ru.sbrf.cache.Cacheable;

public interface A {
    @Cacheable
    void a();
}
