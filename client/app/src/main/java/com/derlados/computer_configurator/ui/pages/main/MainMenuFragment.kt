package com.derlados.computer_configurator.ui.pages.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.ui.pages.main.PageFragment.PageMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main_menu.view.*

class MainMenuFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var pager: ViewPager2
    private lateinit var fragment: View
    private lateinit var fragmentActivity: FragmentActivity

    val titles: HashMap<Int, String> = hashMapOf(
            PageMenu.BUILDS.ordinal to "Ваши сборки",
            PageMenu.SEARCH.ordinal to "Категории комплектуюших",
            PageMenu.INFO.ordinal to "Сборки пользователей"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivity = context as FragmentActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragment = inflater.inflate(R.layout.fragment_main_menu, container, false)
        return fragment
    }

    //TODO (По хорошему нужно делать через контроллер, однако функционал полностью под ЖЦ UI)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        // По скольку дочерние фрагменты не реагируют на изменения видимости родителя - необходимо чтобы родителя сам их оповещал
        for (i in 0 until (pager.adapter?.itemCount ?: 0)) {
            val fragment = (pager.adapter as MenuPageAdapter?)?.getPageFragment(i)
            fragment?.onHiddenChanged(hidden)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Настройка ViewPager-а для просмотра
        pager = fragment.fragment_main_menu_pager
        pager.adapter = MenuPageAdapter(fragmentActivity)
        pager.offscreenPageLimit = pager.adapter?.itemCount!!
        pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                activity?.title = titles[position]
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
        (fragment.fragment_main_menu_bottom_navigator as BottomNavigationView).setOnNavigationItemSelectedListener(this)
    }

    // Переход между страницами при помощи меню в нижней части экрана
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_bottom_navigator_action_builds -> {
                pager.setCurrentItem(PageMenu.BUILDS.ordinal, false)
                activity?.title = titles[PageMenu.BUILDS.ordinal]
            }
            R.id.main_menu_bottom_navigator_action_shop -> {
                pager.setCurrentItem(PageMenu.SEARCH.ordinal, false)
                activity?.title = titles[PageMenu.SEARCH.ordinal]
            }
            R.id.main_menu_bottom_navigator_action_info -> {
                pager.setCurrentItem(PageMenu.INFO.ordinal, false)
                activity?.title = titles[PageMenu.INFO.ordinal]
            }
        }
        return false
    }
}