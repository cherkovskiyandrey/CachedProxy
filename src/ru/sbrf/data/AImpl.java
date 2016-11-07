package ru.sbrf.data;

import java.util.concurrent.atomic.AtomicInteger;

public class AImpl implements A {

    private AtomicInteger sumCounter = new AtomicInteger();

    public int getAumCounter() {
        return sumCounter.get();
    }

    @Override
    public int sum(int i, int i1) {
        sumCounter.incrementAndGet();
        return i + i1;
    }

    @Override
    public int div(int i, int i1) {
        return i - i1;
    }
}
