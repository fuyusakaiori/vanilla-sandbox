package com.vanillaware.sandbox.java.juc.thread.virtual;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 虚拟线程池
 */
public class VirtualThreadExecutorServiceDemo {

    private static final String VIRTUAL_THREAD_NAME_PREFIX = "vanilla-java-sandbox-virtual-thread-";

    public static void main(String[] args) {
        // 1. 初始化虚拟线程池: 不可以自定义虚拟线程
        try (ExecutorService virtualExecutorService = Executors.newVirtualThreadPerTaskExecutor()) {
            virtualExecutorService.execute(() -> {
                System.out.println(Thread.currentThread().getName() + ": Hello, Virtual Thread!");
            });
        }
        // 2. 初始化虚拟线程池: 可以自定义虚拟线程
        ThreadFactory factory = Thread.ofVirtual()
                .name(VIRTUAL_THREAD_NAME_PREFIX + ThreadLocalRandom.current().nextInt(10))
                .factory();
        try (ExecutorService virtualExecutorService = Executors.newThreadPerTaskExecutor(factory)) {
            virtualExecutorService.execute(() -> {
                System.out.println(Thread.currentThread().getName() + ": Hello, Virtual Thread!");
            });
        }


    }

}
