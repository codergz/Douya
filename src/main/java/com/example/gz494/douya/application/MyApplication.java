package com.example.gz494.douya.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by gz494 on 2018/3/26.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            //This process is dedicated to LeakCanary for heap analysis.
            //we should not init our app in this process
            return;
        }

        LeakCanary.install(this);
    }
}
