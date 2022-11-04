package com.derlados.computer_configurator.ui.pages.component_list

import kotlinx.coroutines.*
import android.accounts.NetworkErrorException
import com.derlados.computer_configurator.stores.ComponentStore
import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.consts.SortType
import com.derlados.computer_configurator.types.UserFilterChoice
import com.derlados.computer_configurator.stores.LocalBuildsStore
import com.derlados.computer_configurator.stores.entities.Build
import com.derlados.computer_configurator.providers.android_providers_interfaces.ResourceProvider
import com.derlados.computer_configurator.stores.entities.Component
import java.util.*
import kotlin.collections.ArrayList

class ComponentSearchPresenter(private val view: ComponentSearchView, private val resourceProvider: ResourceProvider): Observer {
    private var downloadJob: Job? = null
    private var currentComponentList: List<Component> = listOf()
    private var searchText: String = ""

    fun init() {
        view.setTitleByCategory(ComponentStore.chosenCategory)
        view.setDefaultImageByCategory(resourceProvider.getDefaultImageByCategory(ComponentStore.chosenCategory))

        view.setComponents(currentComponentList, ComponentStore.favouriteComponents)
        downloadComponents()

        if (ComponentStore.chosenCategory == ComponentCategory.FAVOURITE) {
            view.closeFilters()
        }

        ComponentStore.addObserver(this)
    }

    fun finish() {
        ComponentStore.deleteObserver(this)
        downloadJob?.cancel()

        if (ComponentStore.chosenCategory != ComponentCategory.FAVOURITE) {
            ComponentStore.clearComponents()
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
        currentComponentList = ComponentStore.components

        LocalBuildsStore.editableBuild?.let {
            if (ComponentStore.isCheckCompatibility) {
                currentComponentList = currentComponentList.filter {
                    component ->  it.checkCompatibility(ComponentStore.chosenCategory, component) == Build.Companion.CompatibilityError.OK
                }
                // Отсеивание тех комплектующих, которые повторяются
                it.components[ComponentStore.chosenCategory]?.let { buildComponents ->
                    currentComponentList = currentComponentList.filter { component -> buildComponents.find { bc -> bc.component.id == component.id} == null}
                }
            }
        }

        val userChoice = ComponentStore.userFilterChoice
        // Фильтрация по атрибутам
        currentComponentList = currentComponentList.filter { component -> isFilterValid(component, userChoice) }

        // Сортировка комплектующих
        when (userChoice.chosenSortType) {
            SortType.PRICE_HIGH_TO_LOW -> currentComponentList = currentComponentList.sortedByDescending { component -> component.price  }
            SortType.PRICE_LOW_TO_HIGH -> currentComponentList = currentComponentList.sortedBy { component -> component.price  }
            else -> {}
        }

        currentComponentList = currentComponentList.filter { component -> component.name.contains(searchText) }

        view.setComponents(currentComponentList, ComponentStore.favouriteComponents)
        view.updateComponentList()
    }

    /**
     * Сохранение выбранного комплектующего для дальнейшего отображения
     */
    fun saveChosenComponent(component: Component) {
        ComponentStore.chosenComponent = component
    }

    /**
     * Переключение статуса "избранного" у комплектующего. Если категория комплектующих "Избранное",
     * то в случае удаления - удаляется также и блок во view, иначе - обновляется
     * @param id - id комплектующего
     */
    fun toggleFavoriteStatus(id: Int) {
        val indexInFavoriteList: Int = ComponentStore.favouriteComponents.indexOfFirst { component -> component.id == id }

        if (indexInFavoriteList != -1) {
            ComponentStore.deleteFromFavorite(id)
        } else {
            ComponentStore.addToFavorite(id)
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
                     currentComponentList = ComponentStore.getComponents()
                     filterComponents() // Применение фильтрации, если есть например проверка совместимости сборки
                 }
             } catch (e: NetworkErrorException) {
                 if (isActive) {
                     currentComponentList = ComponentStore.getLocalComponents()
                     view.showError(e.toString())
                 }
                 //TODO добавить класс ErrorHandler
             }

             view.setComponents(currentComponentList, ComponentStore.favouriteComponents)
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

            if (message == ComponentStore.CHANGED_FAVOURITE_STATUS) {
                val id: Int = arg.second  as Int
                val indexInCurrentList = currentComponentList.indexOfFirst { component -> component.id == id }
                val indexInFavoriteList: Int = ComponentStore.favouriteComponents.indexOfFirst { component -> component.id == id }

                if (indexInFavoriteList == -1 && ComponentStore.chosenCategory == ComponentCategory.FAVOURITE) {
                    (currentComponentList as ArrayList<Component>).removeAt(indexInCurrentList)
                    view.removeSingleComponent(indexInCurrentList)
                } else {
                    view.updateSingleComponent(indexInCurrentList)
                }
            }
        }
    }
}