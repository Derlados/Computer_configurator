package com.derlados.computerconf.Fragments;

import android.view.View;


// Интерфейс для общения фрагментов с активити
public interface OnFragmentInteractionListener {

    // Константы действий активити
    enum Action {
        SET_PAGER,
        NEXT_FRAGMENT,
        BACK_FRAGMENT
    }

    /* Метод для общения с активити
    * Параметры:
    * fragment - фрагмент который вызвал метод
    * action - одна из констант действий
    * */
    void onFragmentInteraction(View fragment, Action action);
}
