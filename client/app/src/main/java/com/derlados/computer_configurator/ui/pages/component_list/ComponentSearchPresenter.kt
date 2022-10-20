package com.derlados.computer_configurator.ui.pages.component_list

import kotlinx.coroutines.*
import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.models.ComponentModel
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.consts.SortType
import com.derlados.computer_configurator.types.UserFilterChoice
import com.derlados.computer_configurator.models.LocalAccBuildModel
import com.derlados.computer_configurator.models.entities.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.models.entities.Component
import java.util.*
import kotlin.collections.ArrayList

class ComponentSearchPresenter(private val view: ComponentSearchView, private val resourceProvider: ResourceProvider): Observer {
    private var downloadJob: Job? = null
    private var currentComponentList: List<Component> = listOf()
    private var searchText: String = ""

    fun init() {
        view.setTitleByCategory(ComponentModel.chosenCategory)
        view.setDefaultImageByCategory(resourceProvider.getDefaultImageByCategory(ComponentModel.chosenCategory))

        view.setComponents(currentComponentList, ComponentModel.favouriteComponents)
        downloadComponents()

        if (ComponentModel.chosenCategory == ComponentCategory.FAVOURITE) {
            view.closeFilters()
        }

        ComponentModel.addObserver(this)
    }

    fun finish() {
        ComponentModel.deleteObserver(this)
        downloadJob?.cancel()
        ComponentModel.saveDataInCache()

        if (ComponentModel.chosenCategory != ComponentCategory.FAVOURITE) {
            ComponentModel.clearComponents()
        }

        view.updateComponentList()
    }

    /**
     * Поиск по тексту, утснавливает значение поисковому тексту и производите фильтрацию всех комплектующих
     * @param text - поисковый текст
     */
    fun searchComponentByText(text: String) {
        searchText = text
        filterComponents()
    }

    /**
     * Фильтрация списка комплектующих. Для фильтрации извлекается текущий список всех комплектующих,
     * после чего происходит фильтрация по совместимости если ключена, по всем выбранным фильтрам пользователя
     * и по тексту который введен в поисковой строке
     */
    fun filterComponents() {
        currentComponentList = ComponentModel.components

        LocalAccBuildModel.editableBuild?.let {
            if (ComponentModel.isCheckCompatibility) {
                currentComponentList = currentComponentList.filter {
                    component ->  it.checkCompatibility(ComponentModel.chosenCategory, component) == Build.Companion.CompatibilityError.OK
                }
                // Отсеивание тех комплектующих, которые повторяются
                it.components[ComponentModel.chosenCategory]?.let { buildComponents ->
                    currentComponentList = currentComponentList.filter { component -> buildComponents.find { bc -> bc.component.id == component.id} == null}
                }
            }
        }

        val userChoice = ComponentModel.userFilterChoice
        // Фильтрация по атрибутам
        currentComponentList = currentComponentList.filter { component -> isFilterValid(component, userChoice) }

        // Сортировка комплектующих
        when (userChoice.chosenSortType) {
            SortType.PRICE_HIGH_TO_LOW -> currentComponentList = currentComponentList.sortedByDescending { component -> component.price  }
            SortType.PRICE_LOW_TO_HIGH -> currentComponentList = currentComponentList.sortedBy { component -> component.price  }
            else -> {}
        }

        currentComponentList = currentComponentList.filter { component -> component.name.contains(searchText) }

        view.setComponents(currentComponentList, ComponentModel.favouriteComponents)
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
     * @param id - id комплектующего
     */
    fun toggleFavoriteStatus(id: Int) {
        val indexInFavoriteList: Int = ComponentModel.favouriteComponents.indexOfFirst { component -> component.id == id }

        if (indexInFavoriteList != -1) {
            ComponentModel.deleteFromFavorite(id)
        } else {
            ComponentModel.addToFavorite(id)
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

    private fun downloadComponents() {
         downloadJob = CoroutineScope(Dispatchers.Main).launch {
             try {
                 if (isActive) {
                     currentComponentList = ComponentModel.getComponents()
                     filterComponents() // Применение фильтрации, если есть например проверка совместимости сборки
                 }
             } catch (e: NetworkErrorException) {
                 if (isActive) {
                     currentComponentList = ComponentModel.getLocalComponents()
                     view.showError(e.toString())
                 }
                 //TODO добавить класс ErrorHandler
             }

             view.setComponents(currentComponentList, ComponentModel.favouriteComponents)
             view.updateComponentList()
             view.closeProgressBar()
         }
     }

    /**
     * Визуальное изменение статуса избранного
     * @param o - обсервер
     * @param arg - pair<Int, Int>, первый элемент в паре сообщение об изменении, второй id компонента
     */
    override fun update(o: Observable?, arg: Any?) {
        arg?.let {
            val message: Int = (arg as Pair<*, *>).first as Int

            if (message == ComponentModel.CHANGED_FAVOURITE_STATUS) {
                val id: Int = arg.second  as Int
                val indexInCurrentList = currentComponentList.indexOfFirst { component -> component.id == id }
                val indexInFavoriteList: Int = ComponentModel.favouriteComponents.indexOfFirst { component -> component.id == id }

                if (indexInFavoriteList == -1 && ComponentModel.chosenCategory == ComponentCategory.FAVOURITE) {
                    (currentComponentList as ArrayList<Component>).removeAt(indexInCurrentList)
                    view.removeSingleComponent(indexInCurrentList)
                } else {
                    view.updateSingleComponent(indexInCurrentList)
                }
            }
        }
    }
}