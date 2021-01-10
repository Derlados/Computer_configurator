package com.derlados.computerconf.Fragments;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;


// Интерфейс для общения фрагментов с активити
public interface OnFragmentInteractionListener {

    //TODO доделать функционал для реплейса
    /**
     * Смена фрагмента (прошлый фрагмент скрывается)
     * @param fragmentSource - экземпляр фрагмента который вызывает метод
     * @param fragmentReceiver - экземпляр фрагмента который должен будет созда
     * @param data - данные которые передаются следующему фрагмету
     * @param backStackTag - тег бекстека для возврата если это необъодимо
     */
    void nextFragment(Fragment fragmentSource, Fragment fragmentReceiver, Bundle data, String backStackTag);

    /**
     * Возврат фрагмент из стека
     */
    void popBackStack();

    /**
     * Возврат фрагмента из стека по его тегу
     * @param backStackTag - тег по которому необходимо сделать возврат
     */
    void popBackStack(String backStackTag);
}
