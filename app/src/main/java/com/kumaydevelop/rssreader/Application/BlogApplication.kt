package com.kumaydevelop.rssreader.Application

import android.app.Application
import io.realm.Realm

class BlogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}