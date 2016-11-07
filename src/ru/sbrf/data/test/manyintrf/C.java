package ru.sbrf.data.test.manyintrf;

import ru.sbrf.Cacheable;

public interface C extends A, D {
    @Cacheable
    void c();
}
