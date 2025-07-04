package com.vanillaware.sandbox.java.design.single;

/**
 * 静态类
 */
public class StaticSingleton {

    private static class SingletonHolder {
        private static StaticSingleton instance = new StaticSingleton();
    }

    private StaticSingleton() {

    }

    public static StaticSingleton getInstance() {
        return SingletonHolder.instance;
    }

}
