package ru.sbrf;

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
    private final ConcurrentMap<ComposedKey, Object> cache = new ConcurrentHashMap<>();

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
        final List<Class<?>> intrfaces = findAllClsByPredicate(delegate.getClass(), Class::isInterface);
        return intrfaces.toArray(new Class<?>[0]);
    }

    private static List<Class<?>> findAllClsByPredicate(Class<?> base, Predicate<Class<?>> condition) {
        final List<Class<?>> result = new ArrayList<>();
        findAllClsByPredicateHelper(base, condition, result);
        return result;
    }

    private static void findAllClsByPredicateHelper(Class<?> base, Predicate<Class<?>> condition, List<Class<?>> result) {
        final Class<?>[] intrf = base.getInterfaces();
        final Class<?> superCls = base.getSuperclass();

        Arrays.stream(intrf)
                .filter(condition::test)
                .forEach(result::add);

        if (superCls != null && condition.test(superCls)) {
            result.add(superCls);
        }

        Arrays.stream(intrf)
                .forEach(i -> findAllClsByPredicateHelper(i, condition, result));

        if (superCls != null) {
            findAllClsByPredicateHelper(superCls, condition, result);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(Cacheable.class)) {
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
