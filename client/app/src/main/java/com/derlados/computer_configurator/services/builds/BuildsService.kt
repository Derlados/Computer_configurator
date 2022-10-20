package com.derlados.computer_configurator.services.builds

import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.models.entities.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.services.Service
import com.derlados.computer_configurator.services.users.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BuildsService: Service() {
    private val api: BuildsApi

    init {
        api = Retrofit.Builder()
            .baseUrl(UserApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BuildsApi::class.java)
    }

    suspend fun getPublicBuilds(): ArrayList<Build> {
        val res = api.getPublicBuilds()
        val builds = res.body()

        if (res.isSuccessful && builds != null) {
            return builds
        } else {
            throw NetworkErrorException(ResourceProvider.ResString.INTERNAL_SERVER_ERROR.name)
        }
    }
}