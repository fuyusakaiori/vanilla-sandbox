package com.vanillaware.sandbox.java.design.single;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

/**
 * 枚举实现
 */
public enum EnumSingleton {

    SINGLETON;

    /**
     * 反序列化破坏单例
     */
    private static void serializeDestroySingleton() throws Exception {
        // 1. 单例对象
        EnumSingleton singleton = EnumSingleton.SINGLETON;
        // 2. 序列化
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(singleton);
        objectOutputStream.flush();
        // 3. 反序列化
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        if (objectInputStream.readObject() instanceof EnumSingleton clone) {
            System.out.println("singleton " + singleton.hashCode());
            System.out.println("singleton clone " + clone.hashCode());
        }
    }

    /**
     * 反射破坏单例
     */
    private static void reflectDestroySingleton() throws Exception {
        // 1. 获取枚举类的构造器
        Constructor<EnumSingleton> constructor = EnumSingleton.class.getDeclaredConstructor();
        // 2. 设置为可访问的
        constructor.setAccessible(true);
        // 3. 反射创建对象
        EnumSingleton singleton = constructor.newInstance();

        System.out.println("singleton " + SINGLETON.hashCode());
        System.out.println("singleton clone " + singleton.hashCode());
    }

}
