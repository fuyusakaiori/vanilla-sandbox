package com.vanillaware.sandbox.java.juc;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal
 */
public class ThreadLocalDemo {

    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    private static ThreadLocal<String> threadLocalWithValue = ThreadLocal.withInitial(() -> "Hello World");

    private static InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    private static TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();

    /**
     * 数据库连接池
     */
    private static class ConnectionPool {

        private static ThreadLocal<Connection> connections = ThreadLocal.withInitial(() -> {
            try {
                return DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "root", "");
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });

        public static Connection getConnection() {
            return connections.get();
        }

        public static void removeConnection() {
            connections.remove();
        }

    }

    /**
     * 用户信息
     */
    private static class UserSession {

        private static ThreadLocal<String> userSessions = ThreadLocal.withInitial(() -> StringUtils.EMPTY);

        public static String getUserSession() {
            return userSessions.get();
        }

        public static void setUserSession(String sessionId) {
            userSessions.set(sessionId);
        }

    }

    @Aspect
    private static class LoginAspect {

        @Pointcut()
        public void login() {

        }

        @Before(value = "login()")
        public void checkLogin() {
            // 1. 设置用户信息
            UserSession.setUserSession(RandomStringUtils.random(6));
            // 2. 可以在其他任何位置调用获取用户信息
            UserSession.getUserSession();
        }

    }

    private static void runThreadLocal() {
        // 1. 初始化任务
        Runnable task = () -> {
            // 2. 获取 ThreadLocal 保存的变量
            Integer value = threadLocal.get();
            // 3. 打印当前线程变量的值
            System.out.println(Thread.currentThread().getName() + " before: " + value);
            // 4. 设置 ThreadLocal 的变量
            threadLocal.set(value + 1);
            // 5. 打印当前线程变量的值
            System.out.println(Thread.currentThread().getName() + " after: " + threadLocal.get());
        };
        // 6. 线程池启动
        Executors.newScheduledThreadPool(3)
                .scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private static void runInheritableThreadLocal() throws InterruptedException {
        // 1. 主线程设置 ThreadLocal
        inheritableThreadLocal.set(Thread.currentThread().getName());
        // 2. 子线程获取 ThreadLocal
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + inheritableThreadLocal.get());
            // inheritableThreadLocal.set(Thread.currentThread().getName());
        }, 0, 1, TimeUnit.SECONDS);
        // 3. 子线程获取 ThreadLocal
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + inheritableThreadLocal.get());
            // inheritableThreadLocal.set(Thread.currentThread().getName());
        }, 0, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(2);
        // 4. 主线程重新设置 ThreadLocal
        inheritableThreadLocal.set("Hello World");

        System.out.println(Thread.currentThread().getName() + " -> " + inheritableThreadLocal.get());
    }

    private static void runTransmittableThreadLocal() throws InterruptedException {
        // 1. 初始化两个线程池
        ExecutorService firstExecutorService = Executors.newSingleThreadExecutor();
        ExecutorService secondExecutorService = Executors.newSingleThreadExecutor();
        // 2. 主线程设置 ThreadLocal
        transmittableThreadLocal.set(Thread.currentThread().getName());
        // 3. 子线程获取 ThreadLocal: 不要使用定时线程池, 没办法每次提交任务都重新包装
        firstExecutorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        }));
        // 4. 子线程获取 ThreadLocal
        secondExecutorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        }));

        TimeUnit.SECONDS.sleep(2);

        // 4. 主线程重新设置 ThreadLocal
        transmittableThreadLocal.set("Hello World");

        System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());

        // 5. 子线程获取 ThreadLocal: 不要使用定时线程池, 没办法每次提交任务都重新包装
        firstExecutorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        }));
        // 6. 子线程获取 ThreadLocal
        secondExecutorService.execute(TtlRunnable.get(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        }));
    }

    private static void runTransmittableThreadLocalSimple() throws InterruptedException {
        // 1. 初始化两个线程池
        ExecutorService firstExecutorService = TtlExecutors.getTtlExecutorService(Executors.newScheduledThreadPool(1));
        ExecutorService secondExecutorService = TtlExecutors.getTtlExecutorService(Executors.newScheduledThreadPool(1));
        // 2. 主线程设置 ThreadLocal
        transmittableThreadLocal.set(Thread.currentThread().getName());
        // 3. 子线程获取 ThreadLocal
        firstExecutorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        });
        // 4. 子线程获取 ThreadLocal
        secondExecutorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        });
        TimeUnit.SECONDS.sleep(2);

        // 5. 主线程重新设置 ThreadLocal
        transmittableThreadLocal.set("Hello World");

        System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());

        // 6. 子线程获取 ThreadLocal
        firstExecutorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        });
        // 7. 子线程获取 ThreadLocal
        secondExecutorService.execute(() -> {
            System.out.println(Thread.currentThread().getName() + " -> " + transmittableThreadLocal.get());
        });
    }

    public static void main(String[] args) throws InterruptedException {
        runTransmittableThreadLocalSimple();

    }
}
