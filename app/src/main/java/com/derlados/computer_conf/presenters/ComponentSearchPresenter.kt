package com.derlados.computer_conf.presenters

import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.os.AsyncTask
import com.derlados.computer_conf.interfaces.ComponentSearchView
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.consts.ComponentCategory

class ComponentSearchPresenter(private val view: ComponentSearchView, private val category: ComponentCategory) {
    enum class UpdateCmd {
        NOT_FOUND,
        DOWNLOADED,
        ERROR
    }

    private val downloader = ComponentsDownloader()

    fun init() {
        view.setComponents(ComponentModel.components)
        downloader.execute(category)
    }

    fun finish() {
        downloader.cancel(false)
        ComponentModel.clearComponents()
    }

    /**
     * Класс для загрузки компонентов
     * Загрузка происходит по блокам, для устранения больших задержек
     */
    inner class  ComponentsDownloader : AsyncTask<ComponentCategory, Any, Void>() {

        /**
         * Загрузка данных с сервера
         * @param params - [0 - категория комплектующего]
         */
        override fun doInBackground(vararg params: ComponentCategory): Void? {
            val category: ComponentCategory = params[0]

            try {
                val maxBlocks = ComponentModel.getMaxBlocks(category)
                if (maxBlocks == 0) {
                    publishProgress(UpdateCmd.NOT_FOUND)
                    return null
                }

               for (i in 1..maxBlocks) {
                   if (!isCancelled) {
                       ComponentModel.downloadComponents(category, i)
                       publishProgress(UpdateCmd.DOWNLOADED)
                   }
               }
            }
            catch (e: NetworkErrorException) {
                publishProgress(UpdateCmd.ERROR, e.toString())
                //TODO добавить класс ErrorHandler
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            ComponentModel.saveComponentsInCache()
        }

        /**
         * @see UpdateCmd
         * @param values - [0] - команда UpdateMsg, [1..] - данные которые необходимы
         */
        override fun onProgressUpdate(vararg values: Any) {
            super.onProgressUpdate(*values)

            when (values[0] as UpdateCmd) {
                UpdateCmd.DOWNLOADED -> view.updateComponents()
                UpdateCmd.NOT_FOUND -> view.showNotFoundMessage()
                UpdateCmd.ERROR-> view.showError(values[1] as String)
            }
        }

        override fun onCancelled() {
            super.onCancelled()
            ComponentModel.saveComponentsInCache()
        }
    }
}