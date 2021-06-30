package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.Managers.FileManager
import com.derlados.computer_conf.internet.ComponentAPI
import com.derlados.computer_conf.consts.ComponentCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ComponentModel {
    var maxBlocks: Int = -1
    var components: ArrayList<Component>
    private set
    lateinit var chosenComponent: Component
    lateinit var chosenCategory: ComponentCategory

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

    suspend fun downloadComponents(category: ComponentCategory, block: Int) {
       return suspendCoroutine { continuation ->
           val call: Call<ArrayList<Component>> = api.getGoodsBlock(category.toString(), block)

           call.enqueue(object : Callback<ArrayList<Component>> {
               override fun onResponse(call: Call<ArrayList<Component>>, response: Response<ArrayList<Component>>) {
                   if (response.code() == 200) {
                       val newComponents: ArrayList<Component> = response.body()!!
                       components.addAll(newComponents)
                       continuation.resume(Unit)
                   } else {
                       continuation.resumeWithException(NetworkErrorException(response.code().toString()))
                   }
               }

               override fun onFailure(call: Call<ArrayList<Component>>, t: Throwable) {
                   continuation.resumeWithException(NetworkErrorException("No connection"))
               }
           })
       }
    }

    suspend fun getMaxBlocks(category: ComponentCategory): Int {
        return suspendCoroutine { continuation ->
            if (maxBlocks != -1) {
                continuation.resume(maxBlocks)
            }

            val call: Call<Int> = api.getMaxBlocks(category.toString());
            call.enqueue(object: Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if (response.code() == 200 && response.body() != null) {
                        maxBlocks = response.body()!!
                        continuation.resume(maxBlocks)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(response.code().toString()))
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    continuation.resumeWithException(NetworkErrorException("No connection"))
                }
            })
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
            val data: String = FileManager.readJson(FileManager.Entity.COMPONENT, category.toString())
            val type: Type = object: TypeToken<ArrayList<Component>>() {}.type
            val cacheComponents: ArrayList<Component> =  Gson().fromJson(data, type)
            components.addAll(cacheComponents)
            true
        } else {
            false
        }
    }

}