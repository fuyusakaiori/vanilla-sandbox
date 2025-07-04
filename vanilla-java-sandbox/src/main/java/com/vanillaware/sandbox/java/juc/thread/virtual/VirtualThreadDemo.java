package com.vanillaware.sandbox.java.juc.thread.virtual;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 虚拟线程示例
 */
public class VirtualThreadDemo {

    private static final String VIRTUAL_THREAD_NAME_PREFIX = "vanilla-java-sandbox-virtual-thread-";

    public static void main(String[] args) throws Exception {
        // 1. 控制所有线程的结束
        CountDownLatch countDownLatch = new CountDownLatch(4);
        // 2. 所有线程执行的任务
        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + ": Hello, World!");
            countDownLatch.countDown();
        };
        // 3. 初始化虚拟线程: 不立刻启动虚拟线程
        Thread virtualThread1 = Thread.ofVirtual()
                .name(VIRTUAL_THREAD_NAME_PREFIX + ThreadLocalRandom.current().nextInt(10))
                .unstarted(task);
        // 单独启动虚拟线程
        virtualThread1.start();
        // 4. 初始化虚拟线程: 立刻启动虚拟线程
        Thread virtualThread2 = Thread.ofVirtual()
                .name(VIRTUAL_THREAD_NAME_PREFIX + ThreadLocalRandom.current().nextInt(10))
                .start(task);
        // 5. 快速初始化虚拟线程
        Thread virtualThread3 = Thread.startVirtualThread(task);
        // 6. 工厂模式初始化虚拟线程
        ThreadFactory factory = Thread.ofVirtual()
                .name(VIRTUAL_THREAD_NAME_PREFIX + ThreadLocalRandom.current().nextInt(10))
                .factory();
        Thread virtualThread4 = factory.newThread(task);
        virtualThread4.start();

        countDownLatch.await();

    }

}
