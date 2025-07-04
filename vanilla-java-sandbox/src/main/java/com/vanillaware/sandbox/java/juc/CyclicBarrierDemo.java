package com.vanillaware.sandbox.java.juc;

import java.util.concurrent.*;

/**
 * CyclicBarrier
 */
public class CyclicBarrierDemo {

    private final static int THREAD_COUNT = 11;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        // 1. 初始化时就必须设置线程的数量
        CyclicBarrier cyclicBarrier = new CyclicBarrier(THREAD_COUNT, () -> {
            System.out.println("CyclicBarrier done...");
        });
        // 2. 并发执行任务
        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                try {
                    // TODO: 执行业务逻辑
                    System.out.println(Thread.currentThread().getName() + " CyclicBarrier start...");

                    TimeUnit.SECONDS.sleep(10);
                    // 计数器减少, 等待其他线程到达屏障
                    cyclicBarrier.await();

                    System.out.println(Thread.currentThread().getName() + " CyclicBarrier done...");
                } catch (InterruptedException | BrokenBarrierException exception) {
                    // TODO: 处理异常情况
                }
            });
        }
        cyclicBarrier.await();
        // 5. 主线程不需要等待, 关闭线程池
        THREAD_POOL.shutdown();

    }

}
