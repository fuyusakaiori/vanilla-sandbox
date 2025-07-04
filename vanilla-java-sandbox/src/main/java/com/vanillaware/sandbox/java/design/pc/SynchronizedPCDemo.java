package com.vanillaware.sandbox.java.design.pc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * synchronized 实现生产者消费者模式
 */
public class SynchronizedPCDemo {

    /**
     * 线程安全的容器
     */
    private static class Container<T> {

        private final static int MAX_SIZE = 10;

        private final Queue<T> elements = new LinkedList<>();

        public void put(T element) throws InterruptedException {
            // 1. 获取锁: 保证并发安全
            synchronized (elements) {
                // 2. 判断容器是否已满: 循环判断, 避免虚假唤醒
                while (elements.size() == MAX_SIZE) {
                    elements.wait();
                }
                System.out.println(Thread.currentThread().getName() + " put element " + element);
                // 3. 放入元素
                elements.offer(element);
                // 4. 唤醒所有消费者
                elements.notifyAll();
            }
        }

        public T take() throws InterruptedException {
            // 1. 获取锁: 保证并发安全
            synchronized (elements) {
                // 2. 判断容器是否为空: 循环判断, 避免虚假唤醒
                while (elements.isEmpty()) {
                    elements.wait();
                }
                // 3. 取出元素消费
                T element = elements.poll();
                System.out.println(Thread.currentThread().getName() + " take element " + element);
                // 4. 唤醒所有生产者
                elements.notifyAll();
                return element;
            }
        }

    }

    /**
     * 生产者
     */
    private static class Producer implements Runnable {

        private final Container<Integer> container;

        public Producer(Container<Integer> container) {
            this.container = container;
        }

        @Override
        public void run() {
            try {
                container.put(ThreadLocalRandom.current().nextInt(100));
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        }

    }

    /**
     * 消费者
     */
    private static class Consumer implements Runnable {

        private final Container<Integer> container;

        public Consumer(Container<Integer> container) {
            this.container = container;
        }

        @Override
        public void run() {
            try {
                container.take();
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static void main(String[] args) {
        Container<Integer> container = new Container<>();
        // 1. 定时线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        // 2. 定时生产
        scheduledExecutorService.scheduleAtFixedRate(new Producer(container), 0, 1, TimeUnit.SECONDS);
        // 3. 定时消费
        scheduledExecutorService.scheduleAtFixedRate(new Consumer(container), 0, 2, TimeUnit.SECONDS);

    }

}
