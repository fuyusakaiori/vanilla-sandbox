package com.vanillaware.sandbox.java.design.single;

/**
 * 懒汉式
 */
public class LazySingleton {

    private static LazySingleton instance;

    private LazySingleton() {

    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            return instance = new LazySingleton();
        }
        return instance;
    }

}
