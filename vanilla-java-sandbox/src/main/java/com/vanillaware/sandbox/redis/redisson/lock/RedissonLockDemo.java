package com.vanillaware.sandbox.redis.redisson.lock;

import org.redisson.api.RLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 可重入锁
 */
public class RedissonLockDemo {

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        // 1. 初始化分布式锁
        RLock lock = RedissonClientFactory.getRedissonClient().getLock("REDISSON_LOCK");

        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                // 2. 获取锁
                try {
                    lock.lock();
                    if (!lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                        return;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    System.out.println(Thread.currentThread().getName() + " RedissonLock start...");

                    TimeUnit.SECONDS.sleep(2);

                    System.out.println(Thread.currentThread().getName() + " RedissonLock done...");
                } catch (Exception exception) {
                    // TODO: 处理异常情况
                } finally {
                    // 3. 释放锁
                    lock.unlock();
                }
            });
        }
    }

}
