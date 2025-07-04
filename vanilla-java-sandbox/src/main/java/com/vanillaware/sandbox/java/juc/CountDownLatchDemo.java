package com.vanillaware.sandbox.java.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch
 */
public class CountDownLatchDemo {

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) throws InterruptedException {
        // 1. 主线程等待其他线程执行结束
        mainThreadSyncWait();
        // 2. 线程彼此相互等待
        everyThreadSyncWait();
        // 3. 等待所有线程准备就绪

    }

    private static void mainThreadSyncWait() throws InterruptedException {
        // 1. 初始化时就必须设置线程的数量
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        // 2. 并发执行任务
        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                try {
                    // TODO: 执行业务逻辑
                    System.out.println(Thread.currentThread().getName() + " CountDownLatch start...");

                    TimeUnit.SECONDS.sleep(10);

                    System.out.println(Thread.currentThread().getName() + " CountDownLatch done...");

                } catch (InterruptedException exception) {
                    // TODO: 处理异常情况
                } finally {
                    // 计数器减少
                    countDownLatch.countDown();
                }
            });
        }
        // 4. 等待所有线程执行结束: count down latch 计数器为 0 时就证明所有线程执行结束
        boolean await = countDownLatch.await(1, TimeUnit.SECONDS);
        // 5. 关闭线程池
        THREAD_POOL.shutdown();
    }

    private static void everyThreadSyncWait() {
        // 1. 初始化时就必须设置线程的数量
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        // 2. 并发执行任务
        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                try {
                    // TODO: 执行业务逻辑
                    System.out.println(Thread.currentThread().getName() + " CountDownLatch start...");

                    TimeUnit.SECONDS.sleep(10);
                    // 计数器减少
                    countDownLatch.countDown();
                    // 等待其他线程执行结束
                    countDownLatch.await();

                    System.out.println(Thread.currentThread().getName() + " CountDownLatch done...");

                } catch (InterruptedException exception) {
                    // TODO: 处理异常情况
                }
            });
        }
        // 4. 关闭线程池
        THREAD_POOL.shutdown();
    }

    private static void everyThreadSyncWaitReady() {

    }

}
