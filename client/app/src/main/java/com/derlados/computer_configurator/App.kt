package com.derlados.computer_configurator

import android.app.Application
import com.derlados.computer_configurator.providers.android_providers_interfaces.NetworkProvider
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.providers.AndroidNetworkProvider
import com.derlados.computer_configurator.providers.AndroidResourceProvider
import com.derlados.computer_configurator.views.components.AdMob
import com.google.android.gms.ads.AdRequest

class App : Application() {
    lateinit var resourceProvider: ResourceProvider
        private set
    lateinit var networkProvider: NetworkProvider
        private set

    override fun onCreate() {
        super.onCreate()
        AdMob.init()
        app = this
        resourceProvider = AndroidResourceProvider(this)
        networkProvider = AndroidNetworkProvider(this)
    }

    companion object {
        lateinit var app: App
            private set
    }
}