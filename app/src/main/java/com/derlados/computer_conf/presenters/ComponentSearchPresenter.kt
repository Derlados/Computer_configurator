package com.derlados.computer_conf.presenters

import kotlinx.coroutines.*
import android.accounts.NetworkErrorException
import com.derlados.computer_conf.interfaces.ComponentSearchView
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.Component

class ComponentSearchPresenter(private val view: ComponentSearchView, private val category: ComponentCategory) {
    private var downloadJob: Job? = null

    fun init() {
        view.setComponents(ComponentModel.components)

        if (!ComponentModel.restoreFromCache(category)) {
            download()
        } else {
            view.updateComponents()
            view.closeProgressBar()
        }
    }

    fun finish() {
        downloadJob?.cancel()
        ComponentModel.saveComponents(category)
        ComponentModel.clearComponents()
    }

    fun searchComponent(searchText: String) {
        val filteredComponents = ComponentModel.components.filter { component -> component.name.contains(searchText) }
        view.setComponents(filteredComponents)
        view.updateComponents()
    }

    /**
     * Сохранение выбранного комплектующего для дальнейшего отображения
     */
    fun saveChosenComponent(component: Component) {
        ComponentModel.chosenComponentToView = component
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
                         view.updateComponents()
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
}