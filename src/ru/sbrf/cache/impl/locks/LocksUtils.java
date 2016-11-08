package ru.sbrf.cache.impl.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

public class LocksUtils {

    public static <U, K> U doInLock(LazyResourceHolder<? super K, Lock> lockResource, K key, Supplier<U> action) {
        final Lock createLock = lockResource.getOrRecreate(key);
        createLock.lock();
        try {
            return action.get();
        } finally {
            createLock.unlock();
            lockResource.release(key, createLock);
        }
    }

    public static <U, K> U doInRWLock(LazyResourceHolder<? super K, ReadWriteLock> lockResource, K key, boolean onWrite, Supplier<U> action) {
        final ReadWriteLock rwLock = lockResource.getOrRecreate(key);
        final Lock lock = onWrite ? rwLock.writeLock() : rwLock.readLock();
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
            lockResource.release(key, rwLock);
        }
    }
}
