package ru.sbrf.data;

import java.util.concurrent.atomic.AtomicInteger;

public class AImpl implements A {

    private AtomicInteger sumCounter = new AtomicInteger();
    public int getAumCounter() {
        return sumCounter.get();
    }

    private AtomicInteger sumStrCounter = new AtomicInteger();
    public int getSumStrCounter() {
        return sumStrCounter.get();
    }

    private AtomicInteger marryCounter = new AtomicInteger();
    public int getMarryCounter() {
        return marryCounter.get();
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

    @Override
    public String sumStr(String s, String s2) {
        sumStrCounter.incrementAndGet();
        return s + s2;
    }

    @Override
    public Person marry(Person s, Person s2) {
        marryCounter.incrementAndGet();
        return new Person(s.getName() + "_" + s2.getName(), s.getAge() + s2.getAge());
    }
}
