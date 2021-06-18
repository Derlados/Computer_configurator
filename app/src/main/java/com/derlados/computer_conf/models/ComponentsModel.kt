package com.derlados.computer_conf.models

import com.derlados.computer_conf.internet.ComponentAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

object ComponentsModel {

    private var components: ArrayList<Component>? = null

    private val retrofit: Retrofit

    init {
        this.retrofit = Retrofit.Builder()
                .baseUrl(ComponentAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}