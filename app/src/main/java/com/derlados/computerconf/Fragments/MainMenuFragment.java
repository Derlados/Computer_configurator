package com.derlados.computerconf.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.derlados.computerconf.Fragments.PageFragment.MenuPageAdapter;
import com.derlados.computerconf.Fragments.PageFragment.PageFragment;
import com.derlados.computerconf.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenuFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        // По скольку дочерние фрагменты не реагируют на изменения видимости родителя - необходимо чтобы родителя сам их оповещал
        for (int i = 0; i < pager.getAdapter().getCount(); ++i) {
            Fragment fragment = ((MenuPageAdapter) pager.getAdapter()).getPageFragment(i);
            // Необходимо проверять создался ли объект, ибо создаются они лишь заранее на 1 влево и вправо в viewPager
            if (fragment != null)
                fragment.onHiddenChanged(hidden);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Настройка ViewPager-а для просмотра
        pager = this.getView().findViewById(R.id.fragment_main_menu_pager);
        pager.setAdapter(new MenuPageAdapter(getFragmentManager()));
        pager.setOffscreenPageLimit(pager.getAdapter().getCount());
        ((BottomNavigationView)getView().findViewById(R.id.fragment_main_menu_bottom_navigator)).setOnNavigationItemSelectedListener(this);
    }

    // Переход между страницами при помощи меню в нижней части экрана
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_bottom_navigator_action_builds:
                pager.setCurrentItem(PageFragment.PageMenu.BUILDS.ordinal(), false);
                break;
            case R.id.main_menu_bottom_navigator_action_shop:
                pager.setCurrentItem(PageFragment.PageMenu.SHOP.ordinal(), false);
                break;
            case R.id.main_menu_bottom_navigator_action_info:
                pager.setCurrentItem(PageFragment.PageMenu.INFO.ordinal(), false);
                break;
        }
        return false;
    }
}
