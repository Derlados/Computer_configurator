package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.internet.ComponentAPI
import com.derlados.computer_conf.consts.ComponentCategory
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ComponentModel {

    var maxBlocks: Int = -1
    var components: ArrayList<Component>
    private set

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

    fun downloadComponents(category: ComponentCategory, block: Int) {
        val call: Call<ArrayList<Component>> = api.getGoodsBlock(category.toString(), block)
        val response: Response<ArrayList<Component>> = call.execute()
        if (response.code() == 200) {
            val newComponents: ArrayList<Component> = response.body()!!
            components.addAll(newComponents)
        } else {
            throw NetworkErrorException(response.code().toString())
        }
    }

    fun getMaxBlocks(category: ComponentCategory): Int {
        if (maxBlocks != -1) {
            return maxBlocks
        }

        val call: Call<Int> = api.getMaxBlocks(category.toString());
        val response: Response<Int> = call.execute()

        if (response.code() == 200 && response.body() != null) {
            maxBlocks = response.body()!!
            return maxBlocks
        } else {
            throw NetworkErrorException(response.code().toString())
        }
    }

    fun clearComponents() {
        components.clear()
        maxBlocks = -1
    }

    fun saveComponentsInCache() {
       // TODO(Сделать кеширование)
    }

    fun restoreFromCache() {
        // TODO(Сделать кеширование)
    }
}