package ru.sbrf;


import ru.sbrf.data.A;
import ru.sbrf.data.AImpl;

import java.io.FileNotFoundException;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        final AImpl base = new AImpl();
        final A a = CachedProxy.of(base);
        a.sum(1, 2);
        a.sum(1, 2);
        a.sum(1, 2);
        a.sum(1, 2);


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
                a.sum(1, 2);
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);

        System.out.println(base.getAumCounter());
    }
}
