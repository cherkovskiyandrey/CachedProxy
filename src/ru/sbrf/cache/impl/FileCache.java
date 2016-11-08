package ru.sbrf.cache.impl;

import ru.sbrf.cache.impl.locks.ConcurrentLazyResourceHolder;
import ru.sbrf.cache.impl.locks.LazyResourceHolder;
import ru.sbrf.cache.impl.locks.LocksUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class FileCache<K, V> implements Cache<K, V> {
    private final File basePath = new File("").getAbsoluteFile();
    private final LazyResourceHolder<File, Lock> createFileLock = new ConcurrentLazyResourceHolder<>(ReentrantLock::new);
    private final LazyResourceHolder<File, ReadWriteLock> rwFileLock = new ConcurrentLazyResourceHolder<>(ReentrantReadWriteLock::new);
    private final LazyResourceHolder<K, Lock> calcLocks = new ConcurrentLazyResourceHolder<>(ReentrantLock::new);

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        final Optional<File> path = getOrCreateFileByKey(key);
        if (!path.isPresent()) {
            return mappingFunction.apply(key);
        }

        final Optional<V> value = readFromFile(path.get(), key, mappingFunction);
        if (value.isPresent()) {
            return value.get();
        }

        return calcWriteAndGet(path.get(), key, mappingFunction);
    }

    private Optional<V> readFromFile(File file, K key, Function<? super K, ? extends V> mappingFunction) {
        try {
            final Optional<Pair<K, V>> data = findInFileByKey(file, key);
            if (data.isPresent()) {
                return Optional.of(data.get().value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.ofNullable(mappingFunction.apply(key));
        }
        return Optional.empty();
    }

    private V calcWriteAndGet(File file, K key, Function<? super K, ? extends V> mappingFunction) {
        return LocksUtils.doInLock(calcLocks, key, () -> {
            final Optional<V> val = readFromFile(file, key, mappingFunction);
            if (val.isPresent()) {
                return val.get();
            }

            V value = mappingFunction.apply(key);
            writeToFile(file, new Pair<>(key, value));
            return value;
        });
    }

    private Optional<Pair<K, V>> findInFileByKey(File file, K key) throws IOException, ClassNotFoundException {
        return getByKey(readFile(file), key);
    }

    private void writeToFile(File path, Pair<K, V> data) {
        LocksUtils.doInRWLock(rwFileLock, path, true, () -> {
            try {
                List<Pair<K, V>> list = readData(path);
                list.add(data);
                try (OutputStream s = new FileOutputStream(path)) {
                    s.write(asSerialazableByteArray(list));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        });
    }

    private byte[] asSerialazableByteArray(List<Pair<K, V>> list) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
            out.writeObject(list);
            return outputStream.toByteArray();
        }
    }

    private Optional<Pair<K, V>> getByKey(List<Pair<K, V>> data, K key) {
        return data.stream()
                .filter(e -> e.key.equals(key))
                .findAny();
    }

    @SuppressWarnings("unchecked")
    private List<Pair<K, V>> readData(File path) throws IOException, ClassNotFoundException {
        try (InputStream s = new FileInputStream(path)) {
            if (s.available() == 0) {
                return new ArrayList<>();
            }
            try (BufferedInputStream b = new BufferedInputStream(s);
                 ObjectInputStream o = new ObjectInputStream(b)) {
                return (List<Pair<K, V>>) o.readObject();
            }
        }
    }

    private List<Pair<K, V>> readFile(File path) throws IOException, ClassNotFoundException {
        return LocksUtils.doInRWLock(rwFileLock, path, false, SupplierWrapper.wrap(() -> readData(path)));
    }

    private Optional<File> getOrCreateFileByKey(K key) {
        final File path = new File(basePath, Integer.toString(key.hashCode()) + ".cache");

        if (path.exists()) {
            return Optional.of(path);
        }

        LocksUtils.doInLock(createFileLock, path, () -> {
            try {
                if (!path.exists()) {
                    path.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        return path.exists() ? Optional.of(path) : Optional.empty();
    }
}
