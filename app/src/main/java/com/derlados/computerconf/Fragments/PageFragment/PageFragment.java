package com.derlados.computerconf.Fragments.PageFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public abstract class PageFragment extends Fragment {

    // Перечисления
    public enum PageMenu {
        BUILDS,
        SHOP,
        INFO
    }

    /* Создание новой страницы в viewPage
    * Параметры:
    * numPage - номер страницы
    * */
    public static PageFragment newInstanse(int numPage) {
        PageFragment pageFragment = null;

        // Взятие нужной константы (для читабельности кода и легкости изменений меню)
        PageMenu page = PageMenu.values()[numPage];

        // Выбор подходящего фрагмента
        switch (page)
        {
            case BUILDS:
                pageFragment = new BuildsFragment();
                break;
            case SHOP:
                pageFragment = new ShopFragment();
                break;
            case INFO:
                pageFragment = new InfoFragment();
                break;
        }


        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Создание самого фрагмента.
    // Реализация должна быть в наследниках где каждый наследний - отдельный фрагмент
    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
