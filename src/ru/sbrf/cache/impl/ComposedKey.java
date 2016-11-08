package ru.sbrf.cache.impl;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ComposedKey implements Serializable {
    private final String methodUuid;
    private final Object[] participants;

    ComposedKey(String methodUuid, Object[] participants) {
        this.methodUuid = methodUuid;
        this.participants = participants;
    }

    public static ComposedKey of(Method method, Object[] participants) throws UnsupportedEncodingException {
        return new ComposedKey(UUID.nameUUIDFromBytes(method.getName().getBytes("UTF-8")).toString(), participants);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComposedKey that = (ComposedKey) o;
        return Objects.equals(methodUuid, that.methodUuid) &&
                Arrays.deepEquals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{methodUuid, participants});
    }
}
