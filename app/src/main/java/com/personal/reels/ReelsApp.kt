package com.personal.reels

import android.app.Application
import com.personal.reels.di.AppContainer

class ReelsApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
