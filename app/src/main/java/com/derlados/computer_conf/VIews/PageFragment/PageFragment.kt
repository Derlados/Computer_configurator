package com.derlados.computerconf.VIews.PageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class PageFragment : Fragment() {
    // Перечисления
    enum class PageMenu {
        BUILDS, SHOP, INFO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Создание самого фрагмента.
    // Реализация должна быть в наследниках где каждый наследний - отдельный фрагмент
    abstract override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    companion object {
        /* Создание новой страницы в viewPage
    * Параметры:
    * numPage - номер страницы
    * */
        fun newInstanse(numPage: Int): PageFragment? {
            var pageFragment: PageFragment? = null

            // Взятие нужной константы (для читабельности кода и легкости изменений меню)
            val page = PageMenu.values()[numPage]
            pageFragment = when (page) {
                PageMenu.BUILDS -> BuildsFragment()
                PageMenu.SHOP -> ShopFragment()
                PageMenu.INFO -> InfoFragment()
            }
            return pageFragment
        }
    }
}