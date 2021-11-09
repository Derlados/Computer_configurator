package com.derlados.computer_conf

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.presenters.MainAppPresenter
import com.derlados.computer_conf.view_interfaces.MainView
import com.derlados.computer_conf.views.pages.auth.LoginFragment
import com.derlados.computer_conf.views.components.GoogleSign
import com.derlados.computer_conf.views.pages.InfoFragment
import com.derlados.computer_conf.views.pages.MainMenuFragment
import com.derlados.computer_conf.views.pages.OnFragmentInteractionListener
import com.derlados.computer_conf.views.pages.SettingsFragment
import java.lang.Exception


class MainActivity : AppCompatActivity(), OnFragmentInteractionListener, PopupMenu.OnMenuItemClickListener, MainView {
    var fragmentManager = supportFragmentManager
    private lateinit var mainMenuFragment: MainMenuFragment;
    private lateinit var presenter: MainAppPresenter
    private lateinit var menu: Menu
    override lateinit var googleSign: GoogleSign

    override var isMenuCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initServices()

        presenter = MainAppPresenter(this, App.app.resourceProvider)
        presenter.init()

        // Инициализация менеджера смены фрагментов
        mainMenuFragment = MainMenuFragment()

        // Открытие фрагмента главного меню
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_ll_container, mainMenuFragment)
                .commit()
    }

    private fun initServices() {
        googleSign = GoogleSign(this)
    }

    /**
     * Обработчик нажатий на кнопку, так как его получает только Activity, то событие
     * необходимо передать во фрагменты которые реализуют OnBackPressedListener
     * @see OnBackPressedListener
     */
    override fun onBackPressed() {
        var close = true
        // Проход по фрагментам и попытка взятия  onBackPressedListener у фрагмента, если он реализован
        val fragmentList = fragmentManager.fragments
        for (fragment in fragmentList)  {
            if (fragment != null && fragment.isVisible) {
                val bpl = fragment as? OnBackPressedListener
                bpl?.let {
                    close = bpl.onBackPressed()
                }
            }
        }

        if (close) super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            this.menu = menu
            val inflater = menuInflater
            inflater.inflate(R.menu.app_menu, menu)
            presenter.menuCreated()

        }
        isMenuCreated = true
        return true
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun changeAuthItemMenu(isAuth: Boolean) {
        menu.findItem(R.id.app_menu_exit).isVisible = isAuth
        menu.findItem(R.id.app_menu_settings).isVisible = isAuth
        menu.findItem(R.id.app_menu_auth).isVisible = !isAuth
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragmentSource = getCurrentFragment()
        val tag: BackStackTag
        val fragment: Fragment

        when (item.itemId) {
            R.id.app_menu_auth -> {
                tag = BackStackTag.AUTH
                fragment = LoginFragment()
            }
            R.id.app_menu_info -> {
                tag = BackStackTag.INFO
                fragment = InfoFragment()
            }
            R.id.app_menu_settings -> {
                tag = BackStackTag.SETTINGS
                fragment = SettingsFragment()
            }
            R.id.app_menu_exit -> {
                presenter.exitAccount()
                popBackStack(BackStackTag.MAIN)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

        nextFragment(fragmentSource, fragment, tag)
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        return true
    }

    override fun nextFragment(fragmentSource: Fragment, fragmentReceiver: Fragment, backStackTag: BackStackTag) {
        if (getCurrentTag() == backStackTag.name) {
            return
        }

        val fTrans = fragmentManager.beginTransaction().setCustomAnimations(
            R.anim.flip_fragment_in, R.anim.flip_fragment_out,
            R.anim.flip_fragment_in, R.anim.flip_fragment_out
        )

        if (mainMenuFragment.isVisible) {
            fTrans.hide(mainMenuFragment)
        } else {
            fTrans.hide(fragmentSource)
        }

        fTrans.add(R.id.activity_main_ll_container, fragmentReceiver)
        fTrans.addToBackStack(backStackTag.name) // Добавление изменнений в стек
        fTrans.commit()
    }

    override fun popBackStack() {
        fragmentManager.popBackStack()
    }

    override fun popBackStack(backStackTag: BackStackTag) {
        if (backStackTag == BackStackTag.MAIN) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } else {
            fragmentManager.popBackStack(backStackTag.name, 0)
        }
    }

    /**
     * Получение текущего открытого фрагмента
     */
    private fun getCurrentFragment(): Fragment {
        val fragmentList = fragmentManager.fragments
        var fragmentSource: Fragment? = null

        if (mainMenuFragment.isVisible) {
            fragmentSource = mainMenuFragment
        } else {
            for (fragment in fragmentList)  {
                if (fragment != null && fragment.isVisible) {
                    fragmentSource = fragment
                }
            }
        }

        if (fragmentSource == null) {
            throw Exception("current fragment not found")
        }

        return fragmentSource
    }

    private fun getCurrentTag(): String? {
        val index = fragmentManager.backStackEntryCount - 1
        return if (index != -1) {
            val backEntry: FragmentManager.BackStackEntry  = fragmentManager.getBackStackEntryAt(index)
            backEntry.name
        } else {
            null
        }
    }

    /**
     * Отклик на BackPressed во фрагментах.
     */
    interface OnBackPressedListener {
        /**
         * Обработка BackPressed во фрагмента
         * @return : true - фрагмент можно закрыть, false - фрагмент должен жить
         * Если onBackPressed() возвращает false, то фрагмент сам должен позаботится о освобождении backStack-а
         */
        fun onBackPressed(): Boolean
    }
}