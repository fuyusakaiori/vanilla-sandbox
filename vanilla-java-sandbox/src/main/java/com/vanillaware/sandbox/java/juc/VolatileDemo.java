package com.vanillaware.sandbox.java.juc;

/**
 * volatile
 */
public class VolatileDemo {

    private static class Task implements  Runnable {
        /**
         * 确保状态变化时对其他线程可见
         */
        private volatile boolean stop = true;

        @Override
        public void run() {
            while (!stop) {

            }
        }
    }

    public static void main(String[] args) {

    }

}
