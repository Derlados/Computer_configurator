package com.derlados.computer_conf.models

import android.accounts.NetworkErrorException
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.consts.SortType
import com.derlados.computer_conf.data_classes.FilterAttribute
import com.derlados.computer_conf.data_classes.UserFilterChoice
import com.derlados.computer_conf.data_classes.clear
import com.derlados.computer_conf.internet.ComponentApi
import com.derlados.computer_conf.managers.FileManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ComponentModel {
    private const val RELEVANCE_CACHE_DAYS: Long = 1
    private const val TRACK_PRICES_FILENAME = "TRACK_PRICES"

    private val retrofit: Retrofit
    private val API: ComponentApi

    private var filters: HashMap<Int, FilterAttribute> = HashMap()
    var components: ArrayList<Component>
        private set
    private var isMustSaveComponents = false

    var favoriteComponents: ArrayList<Component>
    private set
    var trackPrices: HashMap<Int, Int>
    private set

    lateinit var chosenComponent: Component
    lateinit var chosenCategory: ComponentCategory
    val userFilterChoice: UserFilterChoice

    init {
        components = ArrayList()
        trackPrices = HashMap()
        favoriteComponents = ArrayList()

        userFilterChoice = UserFilterChoice(
                HashMap(),
                HashMap(),
                Pair(0, 0),
                SortType.DEFAULT
        )

        retrofit = Retrofit.Builder()
                .baseUrl(ComponentApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        API = retrofit.create(ComponentApi::class.java)

        restoreFavorite()
    }

    /**
     * Выбор категории. Когда категория выбрана - из кэша восстанавливаются сохраненные данные
     * относительно этой категории комплектющих
     * @param category - категория комплектующих
     */
    fun chooseCategory(category: ComponentCategory) {
        chosenCategory = category
    }

    /**
     * Скачивание комплектующих. Прежде чем начнется скачивание с интернета, проверяется наличие и актуальность данных в кэше
     * @return массив полученных комплектующих
     */
    suspend fun getComponents(): ArrayList<Component> {
       return suspendCoroutine { continuation ->
           restoreDataFromCache()
           // Есди данные присутствуют и они актуальные или если нету интернет соединения - используются данные с кеша
           if ((components.isNotEmpty() && isRelevanceCache())) {
               continuation.resume(components)
           } else {
               val call: Call<ArrayList<Component>> = API.getGoodsBlock(chosenCategory.toString())
               call.enqueue(object : Callback<ArrayList<Component>> {
                   override fun onResponse(call: Call<ArrayList<Component>>, response: Response<ArrayList<Component>>) {
                       val newComponents: ArrayList<Component>? = response.body()
                       if (response.code() == 200 && newComponents != null) {
                           components.addAll(newComponents)
                           isMustSaveComponents = true
                           continuation.resume(components)
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
    }

    /**
     * Получение локальных данных о комплектующих. На случай если данные не получилось скачать
     */
    fun getLocalComponents(): ArrayList<Component> {
        return components
    }

    suspend fun getFilters(): HashMap<Int, FilterAttribute> {
        return suspendCoroutine { continuation ->
            if (filters.isNotEmpty()) {
                continuation.resume(filters)
            } else {
                val call: Call<HashMap<Int, FilterAttribute>> = API.getFilters(chosenCategory.toString())
                call.enqueue(object : Callback<HashMap<Int, FilterAttribute>> {
                    override fun onResponse(call: Call<HashMap<Int, FilterAttribute>>, response: Response<HashMap<Int, FilterAttribute>>) {
                        val result: HashMap<Int, FilterAttribute>? = response.body()

                        if (response.code() == 200 && result != null) {
                            filters = result
                            continuation.resume(filters)
                        } else {
                            continuation.resumeWithException(NetworkErrorException(response.code().toString()))
                        }
                    }

                    override fun onFailure(call: Call<HashMap<Int, FilterAttribute>>, t: Throwable) {
                        continuation.resumeWithException(NetworkErrorException("No connection"))
                    }
                })
            }
        }
    }

    /**
     * После того как модель использована, данные должны быть очищены и сброшены
     */
    fun resetData() {
        components.clear()
        isMustSaveComponents = false
        userFilterChoice.clear()
        filters.clear()
    }

    /**
     * Сохранение комплектующих на устройство
     */
    fun saveDataInCache() {
        // Перезапись должна быть только в том случае, если данные загружались с сервера
        if (components.isNotEmpty() && isMustSaveComponents)
            FileManager.saveJsonData(FileManager.Entity.COMPONENT, chosenCategory.toString(), Gson().toJson(components))

        // Фильтры нет необходимости сохранять, если они уже присутствуют
        if (filters.isNotEmpty() && !FileManager.isExist(FileManager.Entity.FILTERS, chosenCategory.toString()))
            FileManager.saveJsonData(FileManager.Entity.FILTERS, chosenCategory.toString(), Gson().toJson(filters))

        FileManager.saveJsonData(FileManager.Entity.COMPONENT, ComponentCategory.FAVORITE.toString(), Gson().toJson(favoriteComponents))
        FileManager.saveJsonData(FileManager.Entity.COMPONENT, TRACK_PRICES_FILENAME, Gson().toJson(trackPrices))
    }

    /**
     * Чтение информации о комплектующих с устройства
     */
    private fun restoreDataFromCache() {
        if (chosenCategory == ComponentCategory.FAVORITE) {
            components = favoriteComponents
            return
        }

        if (FileManager.isExist(FileManager.Entity.COMPONENT, chosenCategory.toString())) {
            val data: String = FileManager.readJson(FileManager.Entity.COMPONENT, chosenCategory.toString())
            val type: Type = object : TypeToken<ArrayList<Component>>() {}.type
            val cacheComponents: ArrayList<Component> = Gson().fromJson(data, type)
            components = cacheComponents
        }

        if (FileManager.isExist(FileManager.Entity.FILTERS, chosenCategory.toString())) {
            val data: String = FileManager.readJson(FileManager.Entity.FILTERS, chosenCategory.toString())
            val type: Type = object : TypeToken<HashMap<Int, FilterAttribute>>() {}.type
            val cacheFilters: HashMap<Int, FilterAttribute> = Gson().fromJson(data, type)
            filters =  cacheFilters
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
        favoriteComponents = Gson().fromJson(data, type)

        data= FileManager.readJson(FileManager.Entity.COMPONENT, TRACK_PRICES_FILENAME)
        type = object : TypeToken<HashMap<Int, Int>>() {}.type
        trackPrices = Gson().fromJson(data, type)
    }

    /**
     * Проверка актуальности данных. Время опеределяется константой в днях
     * @see RELEVANCE_CACHE_DAYS
     */
    private fun isRelevanceCache(): Boolean {
        val lastSaveTime = FileManager.lastModDate(FileManager.Entity.COMPONENT, chosenCategory.toString()).time
        val expiryTime = lastSaveTime + TimeUnit.DAYS.toMillis(RELEVANCE_CACHE_DAYS)

        return lastSaveTime < expiryTime
    }

    //////////////////////////////////////////// ФУНКЦИИ ДЛЯ РАБОТЫ С ИЗБРАННЫМИ КОМПЛЕКТУЮЩИМИ /////////////////////////////////////

    /**
     * Добавление в избранное по id. Производится поиск сред текущих комплектующих и найденное добавляется в избранное.
     * Отслеживаемая цена ставится по умолчанию 0
     * @param id - id комплектующего которое пользователь хочет добавить в избранное
     */
    fun addToFavorite(id: Int) {
        components.find { component -> component.id == id }?.let { favoriteComponents.add(it) }
        trackPrices[id] = 0
    }

    /**
     * Удаление из избранного id. Производится поиск среди избранных и найденное удаляется
     * Так же удаляется отслеживаемая цена
     * @param id - id комплектующего которое пользователь хочет удалить
     */
    fun removeFromFavorite(id: Int) {
        favoriteComponents.remove(favoriteComponents.find { component -> component.id == id })
        trackPrices.remove(id)
    }
}