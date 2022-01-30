package com.derlados.computer_configurator.views.pages.pager_fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class MenuPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val COUNT_PAGES_MENU: Int = 3
    private val pageFragments: ArrayList<Fragment> = ArrayList<Fragment>()

    init {
        for (i in 0 until COUNT_PAGES_MENU) {
            pageFragments.add(PageFragment.newInstance(i))
        }
    }

    override fun createFragment(position: Int): Fragment {
        return pageFragments[position]
    }

    override fun getItemCount(): Int = COUNT_PAGES_MENU

    fun getPageFragment(index: Int): Fragment {
        return pageFragments[index]
    }
}