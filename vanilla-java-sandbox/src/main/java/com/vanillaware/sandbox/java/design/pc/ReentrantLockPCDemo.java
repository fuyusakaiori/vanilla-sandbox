package com.vanillaware.sandbox.java.design.pc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 实现生产者消费者模式
 */
public class ReentrantLockPCDemo {

    /**
     * 线程安全的容器
     */
    private static class Container<T> {

        private final static int MAX_SIZE = 10;

        private final ReentrantLock lock = new ReentrantLock();

        private final Condition empty = lock.newCondition();

        private final Condition full = lock.newCondition();

        private final Queue<T> elements = new LinkedList<>();

        public void put(T element) throws InterruptedException {
            // 1. 上锁确保线程安全
            lock.lock();
            try {
                // 2. 判断是否可以继续生产: 没有虚假唤醒问题, 但是依然唤醒多个生产线程, 循环是为了避免循环条件变化
                while (elements.size() == MAX_SIZE) {
                    full.await();
                }
                System.out.println(Thread.currentThread().getName() + " put element " + element);
                // 3. 生产数据
                elements.offer(element);
                // 4. 唤醒所有消费线程
                empty.signalAll();
            } finally {
                // 5. 释放锁
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            // 1. 上锁确保线程安全
            lock.lock();
            try {
                // 2. 判断是否可以继续消费
                while (elements.isEmpty()) {
                    empty.await();
                }
                // 3. 消费数据
                T element = elements.poll();
                System.out.println(Thread.currentThread().getName() + " take element " + element);
                // 4. 唤醒所有生产线程
                full.signalAll();
                return element;
            } finally {
                lock.unlock();
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
        Container<Integer> container = new Container<Integer>();
        // 1. 定时线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        // 2. 定时生产
        scheduledExecutorService.scheduleAtFixedRate(new Producer(container), 0, 1, TimeUnit.SECONDS);
        // 3. 定时消费
        scheduledExecutorService.scheduleAtFixedRate(new Consumer(container), 0, 3, TimeUnit.SECONDS);
    }

}
