package com.vanillaware.sandbox.java.juc;

import java.util.concurrent.*;

/**
 * 线程池
 */
public class ThreadPoolExecutorDemo {

    private static class Task implements Delayed {

        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {
            return 0;
        }
    }

    /**
     * 单线程池执行任务
     */
    public static void executeBySingleThreadPool(Runnable task) throws Exception {
        // 1. 初始化线程池
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            // 2. 执行任务: 没有返回结果
            executorService.execute(task);
            // 3. 执行任务: 存在返回结果
            Future<String> future = executorService.submit(task, "");
            // 4. 同步等待获取结果
            String result = future.get(1, TimeUnit.SECONDS);
        }
    }

    /**
     * 固定线程池执行任务
     */
    public static void executeByFixedThreadPool(Runnable task) throws Exception {
        // 1. 初始化线程池
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            // 2. 执行任务: 没有返回结果
            executorService.execute(task);
            // 3. 执行任务: 存在返回结果
            Future<String> future = executorService.submit(task, "");
            // 4. 同步等待获取结果
            String result = future.get(1, TimeUnit.SECONDS);
        }
    }

    /**
     * 弹性数量线程池执行任务
     */
    public static void executeByCachedThreadPool(Runnable task) throws Exception {
        // 1. 初始化线程池
        try (ExecutorService executorService = Executors.newCachedThreadPool()) {
            // 2. 执行任务: 没有返回结果
            executorService.execute(task);
            // 3. 执行任务: 存在返回结果
            Future<String> future = executorService.submit(task, "");
            // 4. 同步等待获取结果
            String result = future.get(1, TimeUnit.SECONDS);
        }
    }

    /**
     * 定时线程池执行任务
     */
    public static void executeByScheduledThreadPool(Runnable task) throws Exception {
        // 1. 初始化线程池
        try (ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10)) {
            // 2. 延时执行任务
            executorService.schedule(task, 10, TimeUnit.SECONDS);
            // 3. 定时执行任务: 不需要等待上一个任务执行完成, 就可以继续定时执行下一个任务
            executorService.scheduleAtFixedRate(task, 10, 1, TimeUnit.SECONDS);
            // 4. 定时执行任务: 需要等待上一个任务执行完成, 并且再等待指定的延时时间后再执行
            executorService.scheduleWithFixedDelay(task, 10, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * 自定义线程池执行任务
     */
    public static void executeByThreadPoolExecutor(Runnable task) throws Exception {
        // 1. 初始化线程池
        try (ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                10,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedTransferQueue<>(),
                Executors.defaultThreadFactory())) {
            // 2. 允许核心线程被销毁
            executorService.allowCoreThreadTimeOut(true);
            // 3. 提前启动所有核心线程
            executorService.prestartAllCoreThreads();
            // 4. 执行任务: 没有返回结果
            executorService.execute(task);
            // 5. 执行任务: 存在返回结果
            Future<String> future = executorService.submit(task, "");
            // 6. 同步等待获取结果
            String result = future.get(1, TimeUnit.SECONDS);
        }
    }


    public static void main(String[] args) {

    }

}
