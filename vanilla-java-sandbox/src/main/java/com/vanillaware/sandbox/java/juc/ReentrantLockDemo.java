package com.vanillaware.sandbox.java.juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock
 */
public class ReentrantLockDemo {

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        // 1.  初始化锁
        ReentrantLock lock = new ReentrantLock();
        // 2. 初始化条件变量
        Condition condition = lock.newCondition();
        // 2. 并发执行任务
        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                // 3. 获取锁
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " ReentrantLock start...");
                    // 当前线程是否持有锁
                    System.out.println(Thread.currentThread().getName() + " ReentrantLock isHeldByCurrentThread " + lock.isHeldByCurrentThread());
                    // 等待获取锁的线程数量
                    System.out.println(Thread.currentThread().getName() + " ReentrantLock getQueueLength " + lock.getQueueLength());
                    // 当前线程持有锁的次数
                    System.out.println(Thread.currentThread().getName() + " ReentrantLock getHoldCount " + lock.getHoldCount());

                    TimeUnit.SECONDS.sleep(2);

                    System.out.println(Thread.currentThread().getName() + " ReentrantLock done...");
                } catch (Exception exception) {
                    // TODO: 处理异常情况
                } finally {
                    // 释放锁
                    lock.unlock();
                }
            });
        }
        // 4. 主线程不需要等待, 关闭线程池
        THREAD_POOL.shutdown();
    }

}
