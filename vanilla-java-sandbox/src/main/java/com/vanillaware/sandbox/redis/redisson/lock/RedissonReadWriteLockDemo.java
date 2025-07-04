package com.vanillaware.sandbox.redis.redisson.lock;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedissonReadWriteLockDemo {

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        // 1. 初始化读写锁
        RReadWriteLock lock = RedissonClientFactory.getRedissonClient().getReadWriteLock("REDISSON_READ_WRITE_LOCK");
        // 2. 获取读锁
        RLock readLock = lock.readLock();
        // 3. 获取写锁
        RLock writeLock = lock.writeLock();

        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                // 4. 获取读锁
                try {
                    if (!readLock.tryLock(1, 5, TimeUnit.SECONDS)) {
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
                    // 释放锁
                    readLock.unlock();
                }
            });
        }
    }

}
