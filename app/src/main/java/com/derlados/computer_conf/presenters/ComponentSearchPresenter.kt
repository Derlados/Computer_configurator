package com.derlados.computer_conf.presenters

import kotlinx.coroutines.*
import android.accounts.NetworkErrorException
import com.derlados.computer_conf.view_interfaces.ComponentSearchView
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.view_interfaces.ResourceProvider
import com.derlados.computer_conf.models.Component

class ComponentSearchPresenter(private val view: ComponentSearchView, private val resourceProvider: ResourceProvider) {
    private var category: ComponentCategory = ComponentModel.chosenCategory
    private var downloadJob: Job? = null
    lateinit var filteredComponents: List<Component>

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
        filteredComponents = ComponentModel.components.filter { component -> component.name.contains(searchText) }
        view.setComponents(filteredComponents, ComponentModel.trackPrices)
        view.updateComponentList()
    }

    fun filterComponents(chosenFilters: HashMap<Int, ArrayList<String>>, chosenRangeFilters: HashMap<Int, Pair<Float, Float>>) {
        ComponentModel.components.forEach {
            for ((key, value) in chosenFilters) {
                
            }

            for ((key, value) in chosenRangeFilters) {

            }
        }
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
        val indexInList: Int = ComponentModel.components.indexOfFirst { component -> component.id == id }

        if (ComponentModel.favoriteComponents.contains(ComponentModel.components[indexInList])) {
            ComponentModel.removeFromFavorite(id)

            if (category == ComponentCategory.FAVORITE) {
                view.removeSingleComponent(indexInList)
            } else {
                view.updateSingleComponent(indexInList)
            }
        } else {
            ComponentModel.addToFavorite(id)
            view.updateSingleComponent(indexInList)
        }
    }

    private fun download() {
         downloadJob = CoroutineScope(Dispatchers.Main).launch {
             try {
                 val filterDownloader = async { ComponentModel.downloadFilters(category) }
                 filterDownloader.start()
                 view.setFiltersInDialog(filterDownloader.await())

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
            view.setFiltersInDialog(ComponentModel.downloadFilters(category))
        }
    }
}