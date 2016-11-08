package ru.sbrf.cache;

import ru.sbrf.cache.impl.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

public class CachedProxy implements InvocationHandler {
    private final Object delegate;
    private final ConcurrentMap<Cacheable.Type, Cache<ComposedKey, Object>> cacheByType = new ConcurrentHashMap<>();

    private CachedProxy(Object delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static <T, U extends T> T of(U delegate) {
        return (T) Proxy.newProxyInstance(delegate.getClass().getClassLoader(),
                getCachedInterfaces(delegate),
                new CachedProxy(delegate));
    }

    private static Class<?>[] getCachedInterfaces(Object delegate) {
        final List<Class<?>> intrfaces = ScanUtils.findAllClsByPredicate(delegate.getClass(), Class::isInterface);
        return intrfaces.toArray(new Class<?>[0]);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Cacheable metaInfo = method.getAnnotation(Cacheable.class);
        if (metaInfo == null ||
                method.getReturnType().equals(void.class) ||
                method.getReturnType().equals(Void.class)) {
            return method.invoke(delegate, args);
        }

        Cache<ComposedKey, Object> cache;
        try {
            cache = cacheByType.computeIfAbsent(metaInfo.value(), CacheFactory.INSTANCE::newInstance);
        } catch (UnsupportedCacheTypeException ex) {
            ex.printStackTrace();
            return method.invoke(delegate, args);
        }

        ComposedKey key;
        try {
            key = ComposedKey.of(method, args);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return method.invoke(delegate, args);
        }

        return cache.computeIfAbsent(key, k -> {
            try {
                return method.invoke(delegate, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
