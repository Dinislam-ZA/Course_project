package com.example.note_application_neko_ru.db;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static AppExecutor instance;
    private final Executor mainFlow;
    private final Executor subFlow;

    public AppExecutor(Executor mainFlow, Executor subFlow) {
        this.mainFlow = mainFlow;
        this.subFlow = subFlow;
    }

    public static AppExecutor getInstance(){

        if (instance == null) instance = new AppExecutor(new MainThreadHandler(),Executors.newSingleThreadExecutor());
        return instance;
    }

    public static class MainThreadHandler implements Executor{

        private static Handler mainhandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {

            mainhandler.post(runnable);
        }

    }

    public Executor getMainFlow() {
        return mainFlow;
    }

    public Executor getSubFlow() {
        return subFlow;
    }
}
