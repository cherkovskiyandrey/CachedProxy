package ru.sbrf.cache.impl;

import java.util.function.Supplier;

@FunctionalInterface
public interface SupplierWrapper<T> {
    T get() throws Exception;

    static <U> Supplier<U> wrap(SupplierWrapper<U> action) {
        return () -> {
            try {
                return action.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
