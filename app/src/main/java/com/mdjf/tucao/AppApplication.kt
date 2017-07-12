package com.mdjf.tucao

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex

/**
 * Created by Administrator on 2017/6/15.
 */
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}