package com.derlados.computer_configurator.ui.pages.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.ui.pages.build_list.BuildsListFragment
import com.derlados.computer_configurator.ui.pages.build_online_list.BuildsOnlineListFragment

abstract class PageFragment : Fragment() {
    // Перечисления
    enum class PageMenu {
        BUILDS, SEARCH, INFO
    }

    // Создание самого фрагмента.
    // Реализация должна быть в наследниках где каждый наследний - отдельный фрагмент
    abstract override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    companion object {
        /** Создание новой страницы в viewPage
        * @param numPage - номер страницы
        * */
        fun newInstance(numPage: Int): PageFragment {
            return when (PageMenu.values()[numPage]) {
                PageMenu.BUILDS -> BuildsListFragment()
                PageMenu.SEARCH -> CatalogueFragment()
                PageMenu.INFO -> BuildsOnlineListFragment()
            }
        }
    }
}