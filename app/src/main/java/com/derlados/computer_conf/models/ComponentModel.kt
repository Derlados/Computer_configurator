package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.Managers.FileManager
import com.derlados.computer_conf.internet.ComponentAPI
import com.derlados.computer_conf.consts.ComponentCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


object ComponentModel {

    var maxBlocks: Int = -1
    var components: ArrayList<Component>
    private set
    lateinit var chosenComponent: Component
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

    /**
     * Сохранение комплектующих на устройство
     * @param category -  категория комплектуюших
     */
    fun saveComponents(category: ComponentCategory) {
        if (!FileManager.isExist(FileManager.Entity.COMPONENT, category.toString()))
            FileManager.saveJsonData(FileManager.Entity.COMPONENT, category.toString(), Gson().toJson(components))
    }

    /**
     * Чтение информации о комплектующих с устройства
     * @param category - категория комплектующих
     * @return - true - успешное чтение (данные есть), false - даннных нету
     */
    fun restoreFromCache(category: ComponentCategory): Boolean {
        return if (FileManager.isExist(FileManager.Entity.COMPONENT, category.toString())) {
            val data: String = FileManager.restoreJsonData(FileManager.Entity.COMPONENT, category.toString())
            val type: Type = object: TypeToken<ArrayList<Component>>() {}.type
            val cacheComponents: ArrayList<Component> =  Gson().fromJson(data, type)
            components.addAll(cacheComponents)
            true
        } else {
            false
        }
    }

    fun tempSaveComponent(component: Component) {
        chosenComponent = component
    }
}