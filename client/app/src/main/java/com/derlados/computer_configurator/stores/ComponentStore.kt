package com.derlados.computer_configurator.stores

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.consts.SortType
import com.derlados.computer_configurator.types.FilterAttribute
import com.derlados.computer_configurator.types.UserFilterChoice
import com.derlados.computer_configurator.types.clear
import com.derlados.computer_configurator.managers.FileManager
import com.derlados.computer_configurator.entities.Component
import com.derlados.computer_configurator.services.components.ComponentsService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object ComponentStore: Observable() {
    const val CHANGED_FAVOURITE_STATUS: Int = 1
    private const val RELEVANCE_CACHE_DAYS: Long = 1

    private var filters: HashMap<Int, FilterAttribute> = HashMap()
    var components: ArrayList<Component>
        private set

    var favouriteComponents: ArrayList<Component>
    private set

    lateinit var chosenComponent: Component
    lateinit var chosenCategory: ComponentCategory
    val userFilterChoice: UserFilterChoice
    var isCheckCompatibility: Boolean = true

    init {
        components = ArrayList()
        favouriteComponents = ArrayList()
        userFilterChoice = UserFilterChoice(
                HashMap(),
                HashMap(),
                Pair(0, Int.MAX_VALUE),
                SortType.DEFAULT
        )

        restoreFavorites()
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
        restoreComponentsFromCache()

        if ((components.isEmpty() || !isRelevanceCache()) && chosenCategory != ComponentCategory.FAVOURITE) {
            val downloadedComponents = ComponentsService.getComponents(chosenCategory)
            components.addAll(downloadedComponents)
            saveComponentsInCache()
        }

        return components
    }

    /**
     * Получение локальных данных о комплектующих. На случай если данные не получилось скачать
     */
    fun getLocalComponents(): ArrayList<Component> {
        return components
    }

    suspend fun getFilters(): HashMap<Int, FilterAttribute> {
        restoreFiltersFromCache()
        if (filters.isEmpty()) {
            filters = ComponentsService.getFilters(chosenCategory)
            saveFilterInCache()
        }

        return filters
    }

    /**
     * После того как модель использована, данные должны быть очищены и сброшены
     */
    fun clearComponents() {
        components.clear()
        userFilterChoice.clear()
        filters.clear()
    }

    /**
     * Сохранение комплектующих на устройство
     */
    private fun saveComponentsInCache() {
        if (components.isNotEmpty()) {
            FileManager.saveJsonData(FileManager.Entity.COMPONENT, chosenCategory.toString(), Gson().toJson(components))
        }
    }

    /**
     * Сохранение фильтров для категории на устройство
     */
    private fun saveFilterInCache() {
        if (filters.isNotEmpty() && !FileManager.isExist(FileManager.Entity.FILTERS, chosenCategory.toString())) {
            FileManager.saveJsonData(FileManager.Entity.FILTERS, chosenCategory.toString(), Gson().toJson(filters))
        }
    }

    /**
     * Чтение информации о комплектующих с устройства
     */
    private fun restoreComponentsFromCache() {
        if (chosenCategory == ComponentCategory.FAVOURITE) {
            components = favouriteComponents
            return
        }

        if (FileManager.isExist(FileManager.Entity.COMPONENT, chosenCategory.toString())) {
            val data: String = FileManager.readJson(FileManager.Entity.COMPONENT, chosenCategory.toString())
            val type: Type = object : TypeToken<ArrayList<Component>>() {}.type
            val cacheComponents: ArrayList<Component> = Gson().fromJson(data, type)
            components = cacheComponents
        }
    }

    /**
     * Чтение информации о фильтрах с устройства
     */
    private fun restoreFiltersFromCache() {
        if (FileManager.isExist(FileManager.Entity.FILTERS, chosenCategory.toString())) {
            val data: String = FileManager.readJson(FileManager.Entity.FILTERS, chosenCategory.toString())
            val type: Type = object : TypeToken<HashMap<Int, FilterAttribute>>() {}.type
            val cacheFilters: HashMap<Int, FilterAttribute> = Gson().fromJson(data, type)
            filters =  cacheFilters
        }
    }

    /**
     * Проверка актуальности данных. Время опеределяется константой в днях
     * @see RELEVANCE_CACHE_DAYS
     */
    private fun isRelevanceCache(): Boolean {
        val lastSaveTime = FileManager.lastModDate(FileManager.Entity.COMPONENT, chosenCategory.toString()).time
        val expiryTime = lastSaveTime + TimeUnit.DAYS.toMillis(RELEVANCE_CACHE_DAYS)

        return System.currentTimeMillis() < expiryTime
    }

    //////////////////////////////////////////// ФУНКЦИИ ДЛЯ РАБОТЫ С ИЗБРАННЫМИ КОМПЛЕКТУЮЩИМИ /////////////////////////////////////

    /**
     * Добавление в избранное по id. Производится поиск сред текущих комплектующих и найденное добавляется в избранное.
     * Отслеживаемая цена ставится по умолчанию 0
     * @param id - id комплектующего которое пользователь хочет добавить в избранное
     */
    fun addToFavorite(id: Int) {
        components.find { component -> component.id == id }?.let { favouriteComponents.add(it) }
        saveFavorites()
        setChanged()
        notifyObservers(Pair(CHANGED_FAVOURITE_STATUS, id))
    }

    /**
     * Удаление из избранного id. Производится поиск среди избранных и найденное удаляется
     * Так же удаляется отслеживаемая цена
     * @param id - id комплектующего которое пользователь хочет удалить
     */
    fun deleteFromFavorite(id: Int) {
        favouriteComponents.remove(favouriteComponents.find { component -> component.id == id })
        saveFavorites()
        setChanged()
        notifyObservers(Pair(CHANGED_FAVOURITE_STATUS, id))
    }

    /**
     * Чтение информации об избранных комплектующих
     */
    private fun restoreFavorites() {
        if (!FileManager.isExist(FileManager.Entity.COMPONENT, ComponentCategory.FAVOURITE.toString())) {
            FileManager.remove(FileManager.Entity.COMPONENT, ComponentCategory.FAVOURITE.toString())
            return
        }

        val data: String = FileManager.readJson(FileManager.Entity.COMPONENT, ComponentCategory.FAVOURITE.toString())
        val type: Type = object : TypeToken<ArrayList<Component>>() {}.type
        favouriteComponents = Gson().fromJson(data, type)
    }

    /**
     * Сохранение изменений в избранных комплектующих
     */
    private fun saveFavorites() {
        FileManager.saveJsonData(FileManager.Entity.COMPONENT, ComponentCategory.FAVOURITE.toString(), Gson().toJson(favouriteComponents))
    }
}