package com.walknxt.app

import android.app.Application
import com.walknxt.app.core.di.AppContainer
import com.walknxt.app.core.di.DefaultAppContainer

class WalkNxtApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
