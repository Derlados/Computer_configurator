package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Fragments.PageFragment.PageFragment;
import com.derlados.computerconf.R;
import com.google.android.material.bottomnavigation.BottomNavigationPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainMenuFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull  Context context)
    {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentListener.onActivityInteraction(this, OnFragmentInteractionListener.Action.SET_PAGER, null); // Установка viewPager-а, это может сделать только активити
        ((BottomNavigationView)getView().findViewById(R.id.fragment_main_menu_bottom_navigator)).setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle data = new Bundle(); // Данные о переходе на другую страницу, здесь должен хранится с ключом "page" номер страницы

        switch (item.getItemId())
        {
            case R.id.menu_bottom_navigator_action_builds:
                data.putInt("page", PageFragment.PageMenu.BUILDS.ordinal());
                break;
            case R.id.menu_bottom_navigator_action_shop:
                data.putInt("page", PageFragment.PageMenu.SHOP.ordinal());
                break;
            case R.id.menu_bottom_navigator_action_info:
                data.putInt("page", PageFragment.PageMenu.INFO.ordinal());
                break;
        }

        fragmentListener.onActivityInteraction(this, OnFragmentInteractionListener.Action.OPEN_SELECTED_PAGE, data);
        return false;
    }
}
