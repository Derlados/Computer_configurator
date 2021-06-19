package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import android.util.Log
import com.derlados.computer_conf.internet.ComponentAPI
import com.derlados.computer_conf.Constants.ComponentCategory
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


object ComponentModel {

    private var components: ArrayList<Component>
    private val retrofit: Retrofit
    private val api: ComponentAPI

    init {
        this.components = ArrayList()
        this.retrofit = Retrofit.Builder()
                .baseUrl(ComponentAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        this.api = retrofit.create(ComponentAPI::class.java)
    }

    fun getComponents(category: ComponentCategory, block: Int): ArrayList<Component> {
        if (components.isEmpty()) {
            val call: Call<ArrayList<Component>> = api.getGoodsBlock(category.toString(), block)
            val response: Response<ArrayList<Component>> = call.execute()
            if (response.code() == 200) {
                components = response.body()!!
            } else {
                throw NetworkErrorException(response.code().toString())
            }
        }

        return components
    }

    fun getMaxBlocks(category: ComponentCategory): Int {

        val call: Call<Int> = api.getMaxBlocks(category.toString());
        val response: Response<Int> = call.execute()

        if (response.code() == 200 && response.body() != null) {
            return response.body()!!
        } else {
            throw NetworkErrorException(response.code().toString())
        }
    }

    fun clearComponents() {
        components.clear()
    }

    fun saveComponentsInCache() {
       // TODO(Сделать кеширование)
    }

    fun restoreFromCache() {
        // TODO(Сделать кеширование)
    }
}