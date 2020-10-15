package com.derlados.computerconf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.derlados.computerconf.Fragments.MainMenuFragment;
import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;
import com.derlados.computerconf.Managers.FragmentChanger;
import com.derlados.computerconf.PageFragment.MenuPageAdapter;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация менеджера смены фрагментов
        Fragment mainMenuFragment = new MainMenuFragment();
        FragmentChanger.init(mainMenuFragment, fragmentManager, R.id.activity_main_ll_container);

        // Открытие фрагмента главного меню
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_ll_container, mainMenuFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(View fragment, Action action) {
        switch (action)
        {
            case SET_PAGER:
                ViewPager pager=(ViewPager)fragment.findViewById(R.id.activity_main_pager);
                pager.setAdapter(new MenuPageAdapter(getSupportFragmentManager()));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0)
            FragmentChanger.backFragment();
        super.onBackPressed();
    }
}