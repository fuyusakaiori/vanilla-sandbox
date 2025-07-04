package com.vanillaware.sandbox.redis.redisson.lock;

import org.redisson.api.RLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedissonFairLockDemo {

    private final static int THREAD_COUNT = 10;

    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        // 1. 初始化分布式锁
        RLock fairLock = RedissonClientFactory.getRedissonClient().getFairLock("REDISSON_FAIR_LOCK");

        for (int index = 0; index < THREAD_COUNT; index++) {
            THREAD_POOL.execute(() -> {
                // 2. 获取锁
                try {
                    if (!fairLock.tryLock(1, 5, TimeUnit.SECONDS)) {
                        return;
                    }
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }

                try {
                    System.out.println(Thread.currentThread().getName() + " RedissonFairLock start...");

                    TimeUnit.SECONDS.sleep(2);

                    System.out.println(Thread.currentThread().getName() + " RedissonFairLock done...");
                } catch (Exception exception) {
                    // TODO: 处理异常情况
                } finally {
                    // 3. 释放锁
                    fairLock.unlock();
                }
            });
        }
    }

}
