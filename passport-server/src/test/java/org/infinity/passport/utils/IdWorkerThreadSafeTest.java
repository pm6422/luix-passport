package org.infinity.passport.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

class IdWorkerThread implements Callable<Long> {
    private IdWorker       idWorker;
    private CountDownLatch countDownLatch;

    public IdWorkerThread(IdWorker idWorker, CountDownLatch countDownLatch) {
        this.idWorker = idWorker;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Long call() {
        long l = idWorker.nextId();
        countDownLatch.countDown();
        return l;
    }
}

public class IdWorkerThreadSafeTest {
    @Test
    public void testConcurrency() throws InterruptedException {
        int threadNum = 1000;
        // 需要将future存储到列表之后然后遍历获取结果，而不是得到一个future就进行get
        List<Future<Long>> futures = new ArrayList<>(threadNum);
        List<Long> results = new ArrayList<>(threadNum);
        IdWorker idWorker = new IdWorker(10, 10);
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        IntStream.range(0, threadNum).forEach(i -> {
            Future<Long> future = threadPool.submit(new IdWorkerThread(idWorker, countDownLatch));
            futures.add(future);
        });
        countDownLatch.await();
        futures.forEach(future -> {
            try {
                Long result = future.get();
                results.add(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        System.out.println(results.stream().distinct().count());
    }
}
