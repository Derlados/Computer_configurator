package com.derlados.computer_conf

import android.app.Application
import com.derlados.computer_conf.providers.android_providers_interfaces.NetworkProvider
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.providers.AndroidNetworkProvider
import com.derlados.computer_conf.providers.AndroidResourceProvider

class App : Application() {
    lateinit var resourceProvider: ResourceProvider
        private set
    lateinit var networkProvider: NetworkProvider
        private set

    override fun onCreate() {
        super.onCreate()
        app = this
        resourceProvider = AndroidResourceProvider(this)
        networkProvider = AndroidNetworkProvider(this)
    }

    companion object {
        lateinit var app: App
            private set
    }
}