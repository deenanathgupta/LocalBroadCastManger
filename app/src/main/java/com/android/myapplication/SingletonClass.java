package com.android.myapplication;

public class SingletonClass {

    private static SingletonClass singletonInstance;

    private SingletonClass() {

    }

    public static SingletonClass getInstance() {
        if (singletonInstance == null) {
            synchronized (SingletonClass.class) {
                if (singletonInstance == null) {
                    singletonInstance = new SingletonClass();
                }
            }
        }
        return singletonInstance;
    }
}
