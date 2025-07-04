package com.vanillaware.sandbox.java.juc.thread.virtual;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 虚拟线程简单性能测试
 */
public class VirtualThreadSimpleBenchMark {

    private static final int TASK_COUNT = 10000;

    public static void main(String[] args) throws InterruptedException {
        // 1. 监控真实线程和虚拟线程的线程池
        monitorExecutorService();

        TimeUnit.SECONDS.sleep(1);

        // 2. 使用真实线程执行任务: 可以尝试预热缩减时间
        executeTaskByThread();

        TimeUnit.SECONDS.sleep(5);

        // 3. 使用虚拟线程执行任务: 可以尝试使用非阻塞调用缩减时间
        executeTaskByVirtualThread();
    }

    private static void monitorExecutorService() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            // 1.1 获取所有线程信息
            ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
            // 1.2 记录线程数量
            System.out.println("当前线程数量: " + threadInfos.length);
        }, 0 , 1, TimeUnit.SECONDS);
    }

    private static void executeTaskByThread() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(TASK_COUNT);
        // 1. 开始时间戳
        long startTimestamp = System.currentTimeMillis();
        // 2. 执行任务
        try (ExecutorService executorService = Executors.newFixedThreadPool(6000)) {
            IntStream.range(0, TASK_COUNT).forEach(threadId -> {
               executorService.submit(() -> {
                   try {
                       TimeUnit.SECONDS.sleep(1);
                       // System.out.println(Thread.currentThread().getName() + ": " + startTimestamp);
                   } catch (InterruptedException exception) {
                       throw new RuntimeException(exception);
                   } finally {
                       countDownLatch.countDown();
                   }
               });
            });
        }
        countDownLatch.await();
        System.out.println("真实线程耗时: " + (System.currentTimeMillis() - startTimestamp));
    }

    private static void executeTaskByVirtualThread() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(TASK_COUNT);
        // 1. 开始时间戳
        long startTimestamp = System.currentTimeMillis();
        // 2. 执行任务
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, TASK_COUNT).forEach(threadId -> {
                executorService.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        // System.out.println(threadId + ": " + startTimestamp);
                    } catch (InterruptedException exception) {
                        throw new RuntimeException(exception);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            });
        }
        countDownLatch.await();
        System.out.println("虚拟线程耗时: " + (System.currentTimeMillis() - startTimestamp));
    }
}
