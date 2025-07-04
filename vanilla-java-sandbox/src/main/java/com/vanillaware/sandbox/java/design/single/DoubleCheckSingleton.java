package com.vanillaware.sandbox.java.design.single;

import java.io.*;
import java.lang.reflect.Constructor;

/**
 * 双重检查锁
 */
public class DoubleCheckSingleton implements Serializable {

    private static volatile DoubleCheckSingleton instance;

    private DoubleCheckSingleton() {

    }

    public static DoubleCheckSingleton getInstance() {
        // 1. 第一次判断: 检查是否已经初始化
        if (instance == null) {
            // 2. 尝试获取锁
            synchronized (DoubleCheckSingleton.class) {
                // 3. 第二次判断: 再次检查是否已经初始化
                if (instance == null) {
                    return instance = new DoubleCheckSingleton();
                }
            }
        }
        return instance;
    }

    protected Object readResolve() throws ObjectStreamException {
        return instance;
    }

    /**
     * 反序列化破坏单例
     */
    private static void serializeDestroySingleton() throws Exception {
        // 1. 单例对象
        DoubleCheckSingleton singleton = DoubleCheckSingleton.getInstance();
        // 2. 序列化
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(singleton);
        objectOutputStream.flush();
        // 3. 反序列化
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        if (objectInputStream.readObject() instanceof DoubleCheckSingleton clone) {
            System.out.println("singleton " + singleton.hashCode());
            System.out.println("singleton clone " + clone.hashCode());
        }
    }

    /**
     * 反射破坏单例
     */
    private static void reflectDestroySingleton() throws Exception {
        // 1. 获取类的构造器
        Constructor<DoubleCheckSingleton> constructor = DoubleCheckSingleton.class.getDeclaredConstructor();
        // 2. 设置为可访问的
        constructor.setAccessible(true);
        // 3. 反射创建对象
        DoubleCheckSingleton singleton = constructor.newInstance();

        System.out.println("singleton " + DoubleCheckSingleton.getInstance().hashCode());
        System.out.println("singleton clone " + singleton.hashCode());
    }

    
}
