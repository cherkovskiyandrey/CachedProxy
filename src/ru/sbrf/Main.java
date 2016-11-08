package ru.sbrf;


import ru.sbrf.cache.CachedProxy;
import ru.sbrf.data.A;
import ru.sbrf.data.AImpl;
import ru.sbrf.data.Person;

import java.io.FileNotFoundException;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        final AImpl base = new AImpl();
        final A a = CachedProxy.of(base);
        System.out.println(a.sum(1, 2));
        System.out.println(a.sumStr("1", "2"));
        final Person married = a.marry(new Person("Andrey", 31), new Person("Sveta", 28));
        System.out.println(married);

        ExecutorService service = Executors.newFixedThreadPool(100);
        CyclicBarrier barrier = new CyclicBarrier(100);
        for(int i = 0; i < 100; ++i) {
            service.submit(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if(3 != a.sum(1, 2)) {
                    throw new RuntimeException();
                }
                if(!"12".equals(a.sumStr("1", "2"))) {
                    throw new RuntimeException();
                }
                if(!married.equals(a.marry(new Person("Andrey", 31), new Person("Sveta", 28)))) {
                    throw new RuntimeException();
                }
//                IntStream.range(1, 4500)
//                        .forEach(k -> {
//                            if((2*k + 1) != a.sum(k, k + 1)) {
//                                throw new RuntimeException();
//                            }
//                        });
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);

        System.out.println("Invoke counter: " + base.getAumCounter());
        System.out.println("Invoke str counter: " + base.getSumStrCounter());
        System.out.println("Invoke marry counter: " + base.getMarryCounter());
    }
}
