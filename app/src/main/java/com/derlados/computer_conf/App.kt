package com.derlados.computer_conf

import android.app.Application
import com.derlados.computer_conf.views.providers.AndroidResourceProvider

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        resourceProvider = AndroidResourceProvider(this)
    }

    companion object {
        lateinit var app: App
            private set
        lateinit var resourceProvider: AndroidResourceProvider
            private set
    }

}