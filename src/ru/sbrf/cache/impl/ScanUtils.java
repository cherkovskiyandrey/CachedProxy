package ru.sbrf.cache.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ScanUtils {

    public static List<Class<?>> findAllClsByPredicate(Class<?> base, Predicate<Class<?>> condition) {
        final List<Class<?>> result = new ArrayList<>();
        findAllClsByPredicateHelper(base, condition, result);
        return result;
    }

    public static void findAllClsByPredicateHelper(Class<?> base, Predicate<Class<?>> condition, List<Class<?>> result) {
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
}
