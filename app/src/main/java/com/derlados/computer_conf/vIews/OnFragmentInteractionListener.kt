package com.derlados.computerconf.VIews

import android.os.Bundle
import androidx.fragment.app.Fragment

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
    fun nextFragment(fragmentSource: Fragment, fragmentReceiver: Fragment, data: Bundle?, backStackTag: String?)


    /**
     * Возврат фрагмент из стека
     */
    fun popBackStack()

    /**
     * Возврат фрагмента из стека по его тегу
     * @param backStackTag - тег по которому необходимо сделать возврат
     */
    fun popBackStack(backStackTag: String?)
}