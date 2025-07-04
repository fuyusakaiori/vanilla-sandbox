package com.vanillaware.sandbox.java.juc;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.*;

/**
 * CompletableFuture
 */
public class CompletableFutureDemo {

    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(
            10,
            10,
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(String.format("%s supply async task start...", Thread.currentThread().getName()));
                    return String.format("%s supply async task start...", Thread.currentThread().getName());
                }, THREAD_POOL)
                // thenCombine 是并发执行
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(String.format("%s combine task done...", Thread.currentThread().getName()));
                            return String.format("%s combine task done...", Thread.currentThread().getName());
                        }, THREAD_POOL), (message1, message2) -> message1 + "\n" + message2)
                // thenCompose 是依次执行
                .thenCompose(message ->
                        CompletableFuture.supplyAsync(() -> message + String.format("\n%s, compose task done...", Thread.currentThread().getName()), THREAD_POOL))
                // thenApply 拥有返回值, thenAccept 没有返回值, thenRun 也没有返回值且无法处理异步传递的结果
                .thenApply(message -> message + String.format("\n%s thenApply task done...", Thread.currentThread().getName()))
                // whenComplete 可以同时接收结果和异常, 但是没有返回值, 仅作记录使用, 不会对结果造成任何影响
                .whenComplete((result, exception) -> {
                    System.out.println(result);
                })
                // handle 可以同时接收结果和异常, 可以返回和之前不同类型的结果, 可以改变结果
                .handle((result, exception) -> {
                    if (exception != null) {
                        System.out.println(result += String.format("%s handle exception task done...", Thread.currentThread().getName()));
                        return result;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException interruptedException) {
                        throw new RuntimeException(interruptedException);
                    }
                    System.out.println(result += String.format("\n%s handle task done...", Thread.currentThread().getName()));
                    return result;
                })
                // exceptionally 只能接收异常, 只能返回和之前相同类型的结果, 可以重新抛出异常
                .exceptionally(exception -> String.format("\n%s exceptionally task done...", Thread.currentThread().getName()));

        TimeUnit.SECONDS.sleep(5);

        if (!completableFuture.isDone()) {
            // 主动结束任务并提供一个返回值
            completableFuture.complete(generateString());
        }
        completableFuture.thenAccept(System.out::println);
    }

    private static void initCompletableFuture() {
        // 1. 静态工厂初始化: 使用默认线程池, 不关心返回值
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " run async task start...");
        });
        // 2. 静态工厂初始化: 使用自定义线程池, 不关心返回值
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " run async task start...");
        }, THREAD_POOL);
        // 3. 静态工厂初始化: 使用默认线程池, 需要返回值
        CompletableFuture.supplyAsync(
                () -> String.format("%s supply async task start...", Thread.currentThread().getName()));
        // 4. 静态工厂初始化: 使用自定义线程池, 需要返回值
        CompletableFuture.supplyAsync(
                () -> String.format("%s supply async task start...", Thread.currentThread().getName()), THREAD_POOL);
        // 5. 静态工厂初始化: 可以传递多个 completableFuture, 等待所有任务执行完成才可以继续执行
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(
                        () -> String.format("%s allOf task start...", Thread.currentThread().getName()), THREAD_POOL),
                CompletableFuture.supplyAsync(
                        () -> String.format("%s allOf task start...", Thread.currentThread().getName()), THREAD_POOL)
        );
        // 6. 静态工厂初始化: 可以传递多个 completableFuture, 只需要等待一个任务执行完成就可以继续执行
        CompletableFuture.anyOf(
                CompletableFuture.supplyAsync(
                        () -> String.format("%s anyOf task start...", Thread.currentThread().getName()), THREAD_POOL),
                CompletableFuture.supplyAsync(
                        () -> String.format("%s anyOf task start...", Thread.currentThread().getName()), THREAD_POOL)
        );
        // 7. 通过 new 关键字创建: 已知结果可以使用, 通常都是不符合条件时直接返回使用
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture("completable future");
    }

    private static String generateString() {
        return RandomStringUtils.randomAlphanumeric(6).toLowerCase();
    }

}
