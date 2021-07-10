package com.derlados.computer_conf

import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.views.MainMenuFragment
import com.derlados.computer_conf.views.OnFragmentInteractionListener


class MainActivity : AppCompatActivity(), OnFragmentInteractionListener, PopupMenu.OnMenuItemClickListener {
    var fragmentManager = supportFragmentManager
    private lateinit var mainMenuFragment: MainMenuFragment;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация менеджера смены фрагментов
        mainMenuFragment = MainMenuFragment()

        // Открытие фрагмента главного меню
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_ll_container, mainMenuFragment)
                .commit()

    }

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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show()
        return true
    }

    override fun nextFragment(
        fragmentSource: Fragment,
        fragmentReceiver: Fragment,
        backStackTag: BackStackTag
    ) {
        val fTrans = fragmentManager.beginTransaction().setCustomAnimations(
            R.anim.flip_fragment_in,
            R.anim.flip_fragment_out, R.anim.flip_fragment_in, R.anim.flip_fragment_out
        )

        if (mainMenuFragment.isVisible) fTrans.hide(mainMenuFragment) else fTrans.hide(
            fragmentSource
        )
        fTrans.add(R.id.activity_main_ll_container, fragmentReceiver)
        fTrans.addToBackStack(backStackTag.toString()) // Добавление изменнений в стек
        fTrans.commit()
    }

    override fun popBackStack() {
        fragmentManager.popBackStack()
    }

    override fun popBackStack(backStackTag: BackStackTag) {
        fragmentManager.popBackStack(backStackTag.toString(), 0)
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