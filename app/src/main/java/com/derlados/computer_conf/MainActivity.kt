package com.derlados.computer_conf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.presenters.SearchPresenter
import com.derlados.computer_conf.VIews.MainMenuFragment
import com.derlados.computer_conf.VIews.OnFragmentInteractionListener

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {
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

        SearchPresenter(null)
    }


    override fun onBackPressed() {
        var close = true
        // Проход по фрагментам и попытка взятия  onBackPressedListener у фрагмента, если он реализован
        val fragmentList = fragmentManager.fragments
        for (fragment in fragmentList) if (fragment != null) {
            try {
                val bpl = fragment as OnBackPressedListener
                close = bpl.onBackPressed()
            } catch (e: Exception) {
                Log.w(Log.WARN.toString(), "interface onBackPressedListener not found")
            }
        }
        if (close) super.onBackPressed()
    }

    override fun nextFragment(fragmentSource: Fragment, fragmentReceiver: Fragment, data: Bundle?, backStackTag: String?) {
        val fTrans = fragmentManager.beginTransaction().setCustomAnimations(R.anim.flip_fragment_in,
                R.anim.flip_fragment_out, R.anim.flip_fragment_in, R.anim.flip_fragment_out)

        fragmentReceiver.arguments = data
        if (mainMenuFragment.isVisible) fTrans.hide(mainMenuFragment) else fTrans.hide(fragmentSource)
        fTrans.add(R.id.activity_main_ll_container, fragmentReceiver)
        fTrans.addToBackStack(backStackTag) // Добавление изменнений в стек
        fTrans.commit()
    }

    override fun popBackStack() {
        fragmentManager.popBackStack()
    }

    override fun popBackStack(backStackTag: String?) {
        fragmentManager.popBackStack(backStackTag, 0)
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