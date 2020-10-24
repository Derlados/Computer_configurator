package com.derlados.computerconf.Fragments;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;


// Интерфейс для общения фрагментов с активити
public interface OnFragmentInteractionListener {

    /* Константы действий активити
    * SET_PAGER - Установить viewPager для фрагмента главного меню
    * OPEN_SELECTED_PAGE - Открыть страницу выбранную в нижней панели
    * NEXT_FRAGMENT_HIDE - Показать следующий фрагмент и спрятать текущий
    * NEXT_FRAGMENT_REPLACE - Показать следующий фрагмент, заменив текущий
    * */
    enum Action {
        SET_PAGER,
        OPEN_SELECTED_PAGE,
        NEXT_FRAGMENT_HIDE,
        NEXT_FRAGMENT_REPLACE
    }

    /* Метод для общения с активити
    * Параметры:
    * fragmentSource - фрагмент который вызвал метод
    * data - данные, если они необходимы
    * action - одна из констант действий
    * */
    void onActivityInteraction(Fragment fragmentSource,Action action, Bundle data);

    /* Метод для общения между фрагментами
     * Параметры:
     * fragmentSource - фрагмент который вызвал метод
     * fragmentReceiver - Фрагмент с которым хотят взаимодействовать
     * data - данные, если они необходимы
     * action - одна из констант действий
     * */
    void onFragmentInteraction(Fragment fragmentSource, Fragment fragmentReceiver, Action action,  Bundle data);
}
