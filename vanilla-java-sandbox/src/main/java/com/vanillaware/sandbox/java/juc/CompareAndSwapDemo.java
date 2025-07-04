package com.vanillaware.sandbox.java.juc;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * CAS
 */
public class CompareAndSwapDemo {

    private volatile int counter = 0;

    /**
     * 通过构造函数获取 Unsafe 类
     */
    public static Unsafe getUnsafeByConstructor() throws Exception {
        // 1. 利用反射获取到内存中的 Unsafe Class 对象
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        // 2. 获取构造器
        Constructor<?> constructor = unsafe.getDeclaredConstructor();
        // 3. 设置访问权限
        constructor.setAccessible(true);
        // 4. 创建实例
        return (Unsafe) constructor.newInstance();
    }

    /**
     * 通过 theUnsafe 成员变量获取 Unsafe 类
     */
    public static Unsafe getUnsafeByField() throws Exception {
        // 1. 获取 Unsafe Class 对象中的 Field 成员变量
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        // 2 设置访问权限
        theUnsafe.setAccessible(true);
        // 3. 将成员变量从 Field 对象中提取出来, 因为是静态变量所以传入的是 null
        return (Unsafe) theUnsafe.get(null);
    }

    /**
     * 通过 Unsafe 实现比较并交换
     */
    public static void compareAndSwapByUnsafe(int expected, int newValue) throws Exception {
        CompareAndSwapDemo demo = new CompareAndSwapDemo();
        // 1. 获取 Unsafe 类
        Unsafe unsafe = getUnsafeByConstructor();
        // 2. 获取字段的内存偏移量
        long offset = unsafe.objectFieldOffset(CompareAndSwapDemo.class.getDeclaredField("counter"));
        // 2. 执行比较并交换
        System.out.println("before compare and swap, counter = " + demo.counter);
        unsafe.compareAndSwapInt(demo, offset, expected, newValue);
        System.out.println("after compare and swap, counter = " + demo.counter);
    }

    public static void compareAndSwapByHandle(int expected, int newValue) throws Exception {
        CompareAndSwapDemo demo = new CompareAndSwapDemo();
        // 1. 获取句柄
        VarHandle varHandle = MethodHandles.lookup().findVarHandle(CompareAndSwapDemo.class, "counter", int.class);
        // 2. 执行比较并交换
        System.out.println("before compare and swap, counter = " + demo.counter);
        varHandle.compareAndSet(demo, expected, newValue);
        System.out.println("after compare and swap, counter = " + demo.counter);
    }



    public static void main(String[] args) throws Exception {
        // Unsafe 比较并交换
        // compareAndSwapByUnsafe(0, 1);
        // VarHandle 比较并交换
        compareAndSwapByHandle(0, 1);
    }



}
