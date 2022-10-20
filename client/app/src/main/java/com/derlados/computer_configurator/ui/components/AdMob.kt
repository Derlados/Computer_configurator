package com.derlados.computer_configurator.ui.components

import com.google.android.gms.ads.AdRequest

object AdMob {
    var adCount = 0
    lateinit var adRequest: AdRequest
        private set

    fun init() {
        adRequest = AdRequest.Builder().build()
    }
}