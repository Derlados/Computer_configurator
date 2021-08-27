package com.derlados.computer_conf.presenters

import kotlinx.coroutines.*
import android.accounts.NetworkErrorException
import com.derlados.computer_conf.view_interfaces.ComponentSearchView
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.consts.SortType
import com.derlados.computer_conf.data_classes.UserFilterChoice
import com.derlados.computer_conf.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_conf.models.entities.Component

class ComponentSearchPresenter(private val view: ComponentSearchView, private val resourceProvider: ResourceProvider) {
    private var downloadJob: Job? = null
    private var currentComponentList: List<Component> = listOf()

    fun init() {
        view.setDefaultImageByCategory(resourceProvider.getDefaultImageByCategory(ComponentModel.chosenCategory))

        if (ComponentModel.chosenCategory != ComponentCategory.FAVORITE) {
            view.setComponents(currentComponentList, ComponentModel.trackPrices)
            download()
        } else {
            view.setComponents(ComponentModel.favoriteComponents, ComponentModel.trackPrices)
        }
    }

    fun finish() {
        downloadJob?.cancel()
        ComponentModel.saveDataInCache()
        ComponentModel.resetData()
        view.updateComponentList()
    }

    fun searchComponent(searchText: String) {
        currentComponentList = ComponentModel.components.filter { component -> component.name.contains(searchText) }
        view.setComponents(currentComponentList, ComponentModel.trackPrices)
        view.updateComponentList()
    }

    fun filterComponents() {
        val userChoice = ComponentModel.userFilterChoice
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

            if (ComponentModel.chosenCategory == ComponentCategory.FAVORITE) {
                view.removeSingleComponent(indexInCurrentList)
            } else {
                view.updateSingleComponent(indexInCurrentList)
            }
        } else {
            ComponentModel.addToFavorite(id)
            view.updateSingleComponent(indexInCurrentList)
        }
    }

    /**
     * Проверка на валидацию по фильтрам комплектующего
     * @param component - комплектующее
     * @see UserFilterChoice
     * @param userFilterChoice - выбранные фильтры пользователя (цена, необходимые характеристики, тип сортирофки)
     * @return - true - соответствует фильтрам, false - не соответствует филтрам
     */
    private fun isFilterValid(component: Component, userFilterChoice: UserFilterChoice): Boolean {
        // Проверка на наличие необходимого атрибута
        for ((key, values) in userFilterChoice.chosenFilters) {
            val attribute: Component.Attribute? = component.attributes[key]
            if (attribute == null || !values.contains(attribute.value)) {
                return false
            }
        }

        // Проверка на наличие и находится ли значение атрибута в заданом диапазоне
        for ((key, value) in userFilterChoice.chosenRangeFilters) {
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
        if (component.price !in userFilterChoice.chosenRangePrice.first..userFilterChoice.chosenRangePrice.second) {
            return false
        }

        return true
    }

    private fun download() {
         downloadJob = CoroutineScope(Dispatchers.Main).launch {
             try {
                 if (isActive) {
                     currentComponentList = ComponentModel.getComponents()
                 }
             } catch (e: NetworkErrorException) {
                 if (isActive) {
                     currentComponentList = ComponentModel.getLocalComponents()
                     view.showError(e.toString())
                 }
                 //TODO добавить класс ErrorHandler
             }

             view.setComponents(currentComponentList, ComponentModel.trackPrices)
             view.updateComponentList()
             view.closeProgressBar()
         }
     }
}