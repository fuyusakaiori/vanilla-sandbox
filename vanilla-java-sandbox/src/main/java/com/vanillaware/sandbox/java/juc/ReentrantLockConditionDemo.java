package com.vanillaware.sandbox.java.juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition
 */
public class ReentrantLockConditionDemo {

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        // 1.  初始化锁
        ReentrantLock lock = new ReentrantLock();
        // 2. 初始化条件变量
        Condition firstCondition = lock.newCondition();
        Condition secondCondition = lock.newCondition();
        // 2. 并发执行任务
        for (int index = 0; index < THREAD_COUNT; index++) {
            final int seq = index;
            THREAD_POOL.execute(() -> {
                // 3. 获取锁
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " ReentrantLock Condition start...");
                    if (seq % 2 != 0) {
                        // 奇数线程唤醒偶数线程执行
                        secondCondition.signal();
                        // 奇数线程使用第一个条件变量
                        firstCondition.await();
                    } else {
                        // 偶数线程唤醒奇数线程执行
                        firstCondition.signal();
                        // 偶数线程使用第二个条件变量
                        secondCondition.await();
                    }
                    System.out.println(Thread.currentThread().getName() + " ReentrantLock Condition done...");
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
