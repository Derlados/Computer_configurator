package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import android.os.AsyncTask
import com.derlados.computer_conf.interfaces.ComponentSearchView
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.Constants.ComponentCategory

class SearchPresenter(val componentSearchView: ComponentSearchView?) {

    init {
        val downloader = ComponentsDownloader()
        downloader.execute()
    }

    /**
     * Класс для загрузки компонентов
     */
    class ComponentsDownloader : AsyncTask<ComponentCategory, Void, Void>() {

        /**
         * Загрузка данных с сервера
         * @param params - [0 - категория комплектующего]
         */
        override fun doInBackground(vararg params: ComponentCategory): Void? {
            val category: ComponentCategory = params[0]

            try {
                val maxBlocks = ComponentModel.getMaxBlocks(category)
                for (i in 1..maxBlocks) {
                    ComponentModel.getComponents(category, i)
                }
            } catch (e: NetworkErrorException) {
                //TODO добавить класс ErrorHandler
            }

            return null
        }

        override fun onPostExecute(result: Void) {

            super.onPostExecute(result)
        }

        override fun onProgressUpdate(vararg values: Void) {
            super.onProgressUpdate(*values)
        }

        override fun onCancelled(result: Void) {
            super.onCancelled(result)
        }
    }
}