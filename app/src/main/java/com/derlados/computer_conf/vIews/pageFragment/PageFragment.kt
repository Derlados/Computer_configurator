package com.derlados.computer_conf.VIews.PageFragment

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

    // Создание самого фрагмента.
    // Реализация должна быть в наследниках где каждый наследний - отдельный фрагмент
    abstract override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    companion object {
        /** Создание новой страницы в viewPage
        * @param numPage - номер страницы
        * */
        fun newInstanse(numPage: Int): PageFragment {
            // Взятие нужной константы (для читабельности кода и легкости изменений меню)
            val page = PageMenu.values()[numPage]
            val pageFragment = when (page) {
                PageMenu.BUILDS -> BuildsFragment()
                PageMenu.SHOP -> SearchFragment()
                PageMenu.INFO -> InfoFragment()
            }
            return pageFragment
        }
    }
}