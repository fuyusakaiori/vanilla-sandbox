package com.vanillaware.sandbox.java.juc;

import java.util.concurrent.*;

/**
 * Semaphore
 */
public class SemaphoreDemo {

    private final static int SEMAPHORE_COUNT = 5;

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(SEMAPHORE_COUNT);

    public static void main(String[] args) {
        // 1. 初始化时就必须设置线程的数量
        Semaphore semaphore = new Semaphore(SEMAPHORE_COUNT);
        // 2. 并发执行任务
        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                try {
                    // 获取信号量
                    semaphore.acquire();

                    System.out.println(Thread.currentThread().getName() + " Semaphore start...");
                    // System.out.println(Thread.currentThread().getName() + " drainPermits " + semaphore.drainPermits());
                    System.out.println(Thread.currentThread().getName() + " availablePermits " + semaphore.availablePermits());

                    TimeUnit.SECONDS.sleep(10);

                    System.out.println(Thread.currentThread().getName() + " Semaphore done...");
                } catch (Exception exception) {
                    // TODO: 处理异常情况
                } finally {
                    // 释放信号量
                    semaphore.release();
                }
            });
        }
        // 4. 主线程不需要等待, 关闭线程池
        THREAD_POOL.shutdown();
    }

}
