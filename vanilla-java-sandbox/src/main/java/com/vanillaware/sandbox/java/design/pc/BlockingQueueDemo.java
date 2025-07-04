package com.vanillaware.sandbox.java.design.pc;

import java.util.concurrent.*;

/**
 * BlockingQueue 实现生产者消费者模式
 */
public class BlockingQueueDemo {

    /**
     * 生产者
     */
    private static class Producer implements Runnable {

        private final BlockingQueue<Integer> container;

        public Producer(BlockingQueue<Integer> container) {
            this.container = container;
        }

        @Override
        public void run() {
            // 1. 随机生成数据
            int element = ThreadLocalRandom.current().nextInt(100);
            // 2. 生产数据
            System.out.println(Thread.currentThread().getName() + " put element " + element);

            container.offer(element);
        }
    }

    /**
     * 消费者
     */
    private static class Consumer implements Runnable {

        private final BlockingQueue<Integer> container;

        public Consumer(BlockingQueue<Integer> container) {
            this.container = container;
        }

        @Override
        public void run() {
            // 1. 消费数据
            Integer element = container.poll();
            System.out.println(Thread.currentThread().getName() + " take element " + element);
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Integer> container = new ArrayBlockingQueue<>(10);
        // 1. 定时线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        // 2. 定时生产
        scheduledExecutorService.scheduleAtFixedRate(new Producer(container), 0, 1, TimeUnit.SECONDS);
        // 3. 定时消费
        scheduledExecutorService.scheduleAtFixedRate(new Consumer(container), 0 ,3, TimeUnit.SECONDS);
    }

}
