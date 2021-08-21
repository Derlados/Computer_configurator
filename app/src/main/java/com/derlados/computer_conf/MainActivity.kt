package com.derlados.computer_conf

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.models.BuildModel
import com.derlados.computer_conf.presenters.MainAppPresenter
import com.derlados.computer_conf.view_interfaces.MainView
import com.derlados.computer_conf.views.LoginFragment
import com.derlados.computer_conf.views.MainMenuFragment
import com.derlados.computer_conf.views.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnFragmentInteractionListener, PopupMenu.OnMenuItemClickListener, MainView {
    var fragmentManager = supportFragmentManager
    private lateinit var mainMenuFragment: MainMenuFragment;
    private lateinit var presenter: MainAppPresenter
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainAppPresenter(this, App.app.resourceProvider)
        presenter.init()

        // Инициализация менеджера смены фрагментов
        mainMenuFragment = MainMenuFragment()

        // Открытие фрагмента главного меню
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_ll_container, mainMenuFragment)
                .addToBackStack(BackStackTag.MAIN.name)
                .commit()
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
        return true
    }

    override fun changeAuthItemMenu(isAuth: Boolean) {
        menu.findItem(R.id.app_menu_exit).isVisible = isAuth
        menu.findItem(R.id.app_menu_auth).isVisible = !isAuth
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Операции для выбранного пункта меню
        return when (item.itemId) {
            R.id.app_menu_auth -> {
                val fragmentList = fragmentManager.fragments
                if (mainMenuFragment.isVisible) {
                    nextFragment(mainMenuFragment, LoginFragment(), BackStackTag.AUTH)
                } else {
                    for (fragment in fragmentList)  {
                        if (fragment != null && fragment.isVisible) {
                            nextFragment(fragment, LoginFragment(), BackStackTag.AUTH)
                        }
                    }
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        return true
    }

    override fun nextFragment(fragmentSource: Fragment, fragmentReceiver: Fragment, backStackTag: BackStackTag) {
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
        fragmentManager.popBackStack(backStackTag.name, 0)
    }

    // Отклик на BackPressed во фрагментах.
    interface OnBackPressedListener {
        /**
         * Обработка BackPressed во фрагмента
         * @return : true - фрагмент можно закрыть, false - фрагмент должен жить
         * Если onBackPressed() возвращает false, то фрагмент сам должен позаботится о освобождении backStack-а
         */
        fun onBackPressed(): Boolean
    }


}