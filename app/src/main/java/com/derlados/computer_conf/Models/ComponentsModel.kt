package com.derlados.computer_conf.Models

import com.derlados.computerconf.Internet.ComponentAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ComponentsModel {
    private val retrofit: Retrofit

    init {
        this.retrofit = Retrofit.Builder()
                .baseUrl(ComponentAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}