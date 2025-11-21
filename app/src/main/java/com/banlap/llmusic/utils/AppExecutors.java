package com.banlap.llmusic.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * 线程池管理类
 * */
public class AppExecutors {
    private final Executor diskIO;
    private static AppExecutors INSTANCE;

    public static AppExecutors getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AppExecutors(Executors.newSingleThreadExecutor());
        }
        return INSTANCE;
    }

    public AppExecutors(Executor diskIO) {
        this.diskIO = diskIO;
    }


    public Executor diskIO() {
        return diskIO;

    }
}
