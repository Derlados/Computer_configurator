package com.derlados.computer_conf.views.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.consts.BackStackTag

// Интерфейс для общения фрагментов с активити
interface OnFragmentInteractionListener {
    //TODO доделать функционал для реплейса
    /**
     * Смена фрагмента (прошлый фрагмент скрывается)
     * @param fragmentSource - экземпляр фрагмента который вызывает метод
     * @param fragmentReceiver - экземпляр фрагмента который должен будет созда
     * @param data - данные которые передаются следующему фрагмету
     * @param backStackTag - тег бекстека для возврата если это необъодимо
     */
    fun nextFragment(fragmentSource: Fragment, fragmentReceiver: Fragment, backStackTag: BackStackTag)


    /**
     * Возврат фрагмент из стека
     */
    fun popBackStack()

    /**
     * Возврат фрагмента из стека по его тегу
     * @param backStackTag - тег по которому необходимо сделать возврат
     */
    fun popBackStack(backStackTag: BackStackTag)
}