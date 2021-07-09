package com.derlados.computer_conf.presenters

import kotlinx.coroutines.*
import android.accounts.NetworkErrorException
import com.derlados.computer_conf.view_interfaces.ComponentSearchView
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.consts.SortType
import com.derlados.computer_conf.data_classes.FilterUserChoice
import com.derlados.computer_conf.view_interfaces.ResourceProvider
import com.derlados.computer_conf.models.Component

class ComponentSearchPresenter(private val view: ComponentSearchView, private val resourceProvider: ResourceProvider) {
    private companion object {
        const val MAX_DEFAULT_PRICE = 100000
    }

    private var category: ComponentCategory = ComponentModel.chosenCategory
    private var downloadJob: Job? = null
    lateinit var currentComponentList: List<Component>

    fun init() {
        view.setDefaultImageByCategory(resourceProvider.getDefaultImageByCategory(category))

        if (category != ComponentCategory.FAVORITE) {
            ComponentModel.restoreFromCache(category)
            view.setComponents(ComponentModel.components, ComponentModel.trackPrices)

            //TODO изменить в соответствии с изменением кеширования
            downloadFilters()
            if (ComponentModel.components.isEmpty())
                download()
            else
                view.closeProgressBar()
        } else {
            view.setComponents(ComponentModel.favoriteComponents, ComponentModel.trackPrices)
        }
    }

    fun finish() {
        downloadJob?.cancel()
        ComponentModel.saveComponents(category)
        ComponentModel.clearComponents()
    }

    fun searchComponent(searchText: String) {
        currentComponentList = ComponentModel.components.filter { component -> component.name.contains(searchText) }
        view.setComponents(currentComponentList, ComponentModel.trackPrices)
        view.updateComponentList()
    }

    fun filterComponents(userChoice: FilterUserChoice) {
        // Фильтрация по атрибутам
        currentComponentList = ComponentModel.components.filter { component -> isFilterValid(component, userChoice) }

        // Сортировка комплектующих
        when (userChoice.chosenSortType) {
            SortType.PRICE_HIGH_TO_LOW -> currentComponentList = currentComponentList.sortedByDescending { component -> component.price  }
            SortType.PRICE_LOW_TO_HIGH -> currentComponentList = currentComponentList.sortedBy { component -> component.price  }
            else -> {}
        }

        view.setComponents(currentComponentList, ComponentModel.trackPrices)
        view.updateComponentList()
    }

    /**
     * Проверка на валидацию по фильтрам комплектующего
     * @param component - комплектующее
     * @see FilterUserChoice
     * @param userChoice - выбранные фильтры пользователя (цена, необходимые характеристики, тип сортирофки)
     * @return - true - соответствует фильтрам, false - не соответствует филтрам
     */
    private fun isFilterValid(component: Component, userChoice: FilterUserChoice): Boolean {
        // Проверка на наличие необходимого атрибута
        for ((key, values) in userChoice.chosenFilters) {
            val attribute: Component.Attribute? = component.attributes[key]
            if (attribute == null || !values.contains(attribute.value)) {
                return false
            }
        }

        // Проверка на наличие и находится ли значение атрибута в заданом диапазоне
        for ((key, value) in userChoice.chosenRangeFilters) {
            val attribute: Component.Attribute? = component.attributes[key]
            if (attribute == null) {
                return false
            } else {
                val attrValue: Float? = Regex("([0-9]|\\.)+").find(attribute.value)?.value?.toFloat()
                if (attrValue == null || attrValue !in value.first..value.second) {
                    return false
                }
            }
        }

        // Проверка цены
        if (component.price !in userChoice.chosenRangePrice.first..userChoice.chosenRangePrice.second) {
            return false
        }

        return true
    }

    /**
     * Сохранение выбранного комплектующего для дальнейшего отображения
     */
    fun saveChosenComponent(component: Component) {
        ComponentModel.chosenComponent = component
    }

    /**
     * Переключение статуса "избранного" у комплектующего. Если категория комплектующих "Избранное",
     * то в случае удаления - удаляется также и блок во view, иначе - обновляется
     * @param id - id комплектуюшего
     */
    fun toggleFavoriteStatus(id: Int) {
        val indexInCurrentList = currentComponentList.indexOfFirst { component -> component.id == id }
        val indexInFavoriteList: Int = ComponentModel.favoriteComponents.indexOfFirst { component -> component.id == id }

        if (indexInFavoriteList != -1) {
            ComponentModel.removeFromFavorite(id)

            if (category == ComponentCategory.FAVORITE) {
                view.removeSingleComponent(indexInCurrentList)
            } else {
                view.updateSingleComponent(indexInCurrentList)
            }
        } else {
            ComponentModel.addToFavorite(id)
            view.updateSingleComponent(indexInCurrentList)
        }
    }

    private fun download() {
         downloadJob = CoroutineScope(Dispatchers.Main).launch {
             try {
                 val maxBlocks = ComponentModel.getMaxBlocks(category)
                 if (maxBlocks == 0 && isActive) {
                     view.showNotFoundMessage()
                 }

                 for (i in 1..maxBlocks) {
                     if (isActive) {
                         ComponentModel.downloadComponents(category, i)
                         view.updateComponentList()
                         view.closeProgressBar()
                     }
                 }
             } catch (e: NetworkErrorException) {
                 if (isActive) {
                     view.showError(e.toString())
                 }
                 //TODO добавить класс ErrorHandler
             }

         }
     }

    private fun downloadFilters() {
        CoroutineScope(Dispatchers.Main).launch {
            val filters = ComponentModel.downloadFilters(category)
            val maxPrice = ComponentModel.components.maxByOrNull { it.price }?.price

            if (maxPrice == null) {
                view.setFiltersInDialog(filters, MAX_DEFAULT_PRICE)
            } else {
                view.setFiltersInDialog(filters, maxPrice)
            }
        }
    }
}