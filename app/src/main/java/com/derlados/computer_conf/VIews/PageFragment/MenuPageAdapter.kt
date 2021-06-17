package com.derlados.computerconf.VIews.PageFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MenuPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    private val COUNT_PAGES_MENU = 3
    private val pageFragments = arrayOfNulls<Fragment>(COUNT_PAGES_MENU)

    override fun getItem(position: Int): Fragment {
        pageFragments[position] = PageFragment.newInstanse(position)
        return pageFragments[position]!!
    }

    override fun getCount(): Int {
        return COUNT_PAGES_MENU
    }

    fun getPageFragment(index: Int): Fragment? {
        return pageFragments[index]
    }
}