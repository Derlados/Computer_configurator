package com.derlados.computerconf.VIews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.derlados.computer_conf.R
import com.derlados.computerconf.VIews.PageFragment.MenuPageAdapter
import com.derlados.computerconf.VIews.PageFragment.PageFragment.PageMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main_menu.view.*

class MainMenuFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var pager: ViewPager? = null
    private lateinit var fragment: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragment = inflater.inflate(R.layout.fragment_main_menu, container, false)

        return fragment
    }

    //TODO (По хорошему нужно делать через контроллер, однако функционал полностью под ЖЦ UI)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        // По скольку дочерние фрагменты не реагируют на изменения видимости родителя - необходимо чтобы родителя сам их оповещал
        for (i in 0 until (pager?.adapter?.count ?: 0)) {
            val fragment = (pager?.adapter as MenuPageAdapter?)?.getPageFragment(i)
            // Необходимо проверять создался ли объект, ибо создаются они лишь заранее на 1 влево и вправо в viewPager
            fragment?.onHiddenChanged(hidden)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Настройка ViewPager-а для просмотра
        pager = fragment.fragment_main_menu_pager
        pager?.adapter = MenuPageAdapter(fragmentManager)
        pager?.offscreenPageLimit = pager?.adapter!!.count
        (fragment.fragment_main_menu_bottom_navigator as BottomNavigationView).setOnNavigationItemSelectedListener(this)
    }

    // Переход между страницами при помощи меню в нижней части экрана
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_bottom_navigator_action_builds -> pager!!.setCurrentItem(PageMenu.BUILDS.ordinal, false)
            R.id.main_menu_bottom_navigator_action_shop -> pager!!.setCurrentItem(PageMenu.SHOP.ordinal, false)
            R.id.main_menu_bottom_navigator_action_info -> pager!!.setCurrentItem(PageMenu.INFO.ordinal, false)
        }
        return false
    }
}