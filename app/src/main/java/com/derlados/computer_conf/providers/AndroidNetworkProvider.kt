package com.derlados.computer_conf.providers

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.derlados.computer_conf.providers.android_providers_interfaces.NetworkProvider

class AndroidNetworkProvider(private val context: Context): NetworkProvider {
    private var isNetworkConnected = false

    init {
        if (Build.VERSION.SDK_INT >  Build.VERSION_CODES.N) {
            registerNetworkCallback()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun registerNetworkCallback() {
        try {
            val connectivityManager = context.getSystemService(Application.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("INTERNET_CONNECTION", "AVAILABLE")
                    isNetworkConnected = true
                }

                override fun onLost(network: Network) {
                    Log.d("INTERNET_CONNECTION", "NOT AVAILABLE")
                    isNetworkConnected = false
                }
            }
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            isNetworkConnected = false
        }
    }

    override fun isInternetAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >  Build.VERSION_CODES.N) {
            isNetworkConnected
        } else {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }
}