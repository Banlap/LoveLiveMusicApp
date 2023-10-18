package com.banlap.llmusic.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * 线程池管理类 TODO未处理好
 * */
public class AppExecutors {

    private final Executor mNetworkIO;
    private static AppExecutors instance;
    private static Object object = new Object();

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (object) {
                if (instance == null) {
                    instance = new AppExecutors();
                }
            }
        }
        return instance;
    }

    public AppExecutors() {
        this.mNetworkIO = Executors.newFixedThreadPool(3, new MyThreadFactory(""));
    }

    class MyThreadFactory implements ThreadFactory {

        private int count = 0;
        private String name;

        private MyThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            count++;
            return new Thread(r, name + "-" + count + "-Thread");
        }
    }

    public Executor networkIO() {
        return mNetworkIO;
    }
}
