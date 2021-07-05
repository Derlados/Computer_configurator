package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.Managers.FileManager
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.internet.ComponentAPI
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
    private data class CacheData(val components: ArrayList<Component>, val lastBLock: Int, val maxBlocks: Int)

    private const val TRACK_PRICES_FILENAME = "TRACK_PRICES"

    private val retrofit: Retrofit
    private val api: ComponentAPI

    var maxBlocks: Int = -1
    var filters: HashMap<String, ArrayList<String>> = HashMap()
    var components: ArrayList<Component>
    private set
    var favoriteComponents: ArrayList<Component>
    private set
    var trackPrices: HashMap<Int, Int>
    private set

    lateinit var chosenComponent: Component
    lateinit var chosenCategory: ComponentCategory

    init {
        components = ArrayList()
        trackPrices = HashMap()
        favoriteComponents = ArrayList()

//        val gson = GsonBuilder()
//                .registerTypeAdapter(HashMap::class.java, HashMapDeserializer())
//                .create()

        retrofit = Retrofit.Builder()
                .baseUrl(ComponentAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit.create(ComponentAPI::class.java)

        restoreFavorite()
    }

    /**
     * Скачивание комплектующих по блокам
     * @param category - категория комплектующих
     * @param block - номер блока данных для скачиваняия
     */
    suspend fun downloadComponents(category: ComponentCategory, block: Int) {
       return suspendCoroutine { continuation ->
           val call: Call<ArrayList<Component>> = api.getGoodsBlock(category.toString(), block)

           call.enqueue(object : Callback<ArrayList<Component>> {
               override fun onResponse(call: Call<ArrayList<Component>>, response: Response<ArrayList<Component>>) {
                   val newComponents: ArrayList<Component>? = response.body()

                   if (response.code() == 200 && newComponents != null) {
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
            call.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    val result: Int? = response.body()

                    if (response.code() == 200 && result != null) {
                        maxBlocks = result
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

    suspend fun getFilters(category: ComponentCategory): HashMap<String, ArrayList<String>> {
        return suspendCoroutine { continuation ->
            val call: Call<HashMap<String, ArrayList<String>>> = api.getFilters(category.toString())
            call.enqueue(object : Callback<HashMap<String, ArrayList<String>>> {
                override fun onResponse(call: Call<HashMap<String, ArrayList<String>>>, response: Response<HashMap<String, ArrayList<String>>>) {
                    val result: HashMap<String, ArrayList<String>>? = response.body()

                    if (response.code() == 200 && result != null) {
                        filters = result
                        continuation.resume(filters)
                    } else {
                        continuation.resumeWithException(NetworkErrorException(response.code().toString()))
                    }
                }

                override fun onFailure(call: Call<HashMap<String, ArrayList<String>>>, t: Throwable) {
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
        FileManager.saveJsonData(FileManager.Entity.COMPONENT, ComponentCategory.FAVORITE.toString(), Gson().toJson(favoriteComponents))
        FileManager.saveJsonData(FileManager.Entity.COMPONENT, TRACK_PRICES_FILENAME, Gson().toJson(trackPrices))

        //TODO улучшить кеширование
    }

    /**
     * Чтение информации о комплектующих с устройства
     * @param category - категория комплектующих
     */
    //TODO проверка актуальности
    fun restoreFromCache(category: ComponentCategory) {
        if (category == ComponentCategory.FAVORITE) {
            components.addAll(favoriteComponents)
            return
        }

        if (FileManager.isExist(FileManager.Entity.COMPONENT, category.toString())) {
            val data: String = FileManager.readJson(FileManager.Entity.COMPONENT, category.toString())
            val type: Type = object : TypeToken<ArrayList<Component>>() {}.type
            val cacheComponents: ArrayList<Component> = Gson().fromJson(data, type)
            components.addAll(cacheComponents)
        }
    }

    /**
     * Чтение информации о избранных комплектующих и отслеживаемых ценах с устройства
     * Должны присутствовать два файла - цены и комплектующие. В случае отсутствия одного из них - данные расцениваются как поврежденные
     */
    private fun restoreFavorite() {
        if (!FileManager.isExist(FileManager.Entity.COMPONENT, ComponentCategory.FAVORITE.toString())
                || !FileManager.isExist(FileManager.Entity.COMPONENT, TRACK_PRICES_FILENAME)) {

            FileManager.remove(FileManager.Entity.COMPONENT, ComponentCategory.FAVORITE.toString())
            FileManager.remove(FileManager.Entity.COMPONENT, TRACK_PRICES_FILENAME)
            return
        }

        var data: String = FileManager.readJson(FileManager.Entity.COMPONENT, ComponentCategory.FAVORITE.toString())
        var type: Type = object : TypeToken<ArrayList<Component>>() {}.type
        favoriteComponents.addAll(Gson().fromJson(data, type))

        data= FileManager.readJson(FileManager.Entity.COMPONENT, TRACK_PRICES_FILENAME)
        type = object : TypeToken<HashMap<Int, Int>>() {}.type
        trackPrices.putAll(Gson().fromJson(data, type))
    }

    //////////////////////////////////////////// ФУНКЦИИ ДЛЯ РАБОТЫ С ИЗБРАННЫМИ КОМПЛЕКТУЮЩИМИ /////////////////////////////////////

    fun addToFavorite(id: Int) {
        components.find { component -> component.id == id }?.let { favoriteComponents.add(it) }
        trackPrices[id] = 0
    }

    fun removeFromFavorite(id: Int) {
        favoriteComponents.remove(components.find { component -> component.id == id })
        trackPrices.remove(id)
    }
}