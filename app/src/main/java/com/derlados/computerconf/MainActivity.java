package com.derlados.computerconf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.derlados.computerconf.Fragments.MainMenuFragment;
import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;
import com.derlados.computerconf.Fragments.PageFragment.MenuPageAdapter;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment mainMenuFragment;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация менеджера смены фрагментов
        mainMenuFragment = new MainMenuFragment();

        // Открытие фрагмента главного меню
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_ll_container, mainMenuFragment)
                .commit();


    }

    @Override
    public void onFragmentInteraction(Fragment fragmentSource, Fragment fragmentReciver,  Action action, Bundle data) {
        FragmentTransaction fTrans = fragmentManager.beginTransaction();
        fragmentReciver.setArguments(data);

        switch (action)
        {
            case NEXT_FRAGMENT_HIDE:
                fTrans.hide(mainMenuFragment);
                fTrans.add(R.id.activity_main_ll_container, fragmentReciver);

                fTrans.addToBackStack(null);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case NEXT_FRAGMENT_REPLACE:
                fTrans.replace(R.id.activity_main_ll_container, fragmentReciver);
                fTrans.addToBackStack(null);   // Добавление изменнений в стек
                fTrans.commit();
                break;
        }
    }

    @Override
    public void onActivityInteraction(Fragment fragmentSource, Action action, Bundle data) {
        switch (action)
        {
            case SET_PAGER:
                pager = fragmentSource.getView().findViewById(R.id.fragment_main_menu_pager);
                pager.setAdapter(new MenuPageAdapter(getSupportFragmentManager()));
                break;
            case OPEN_SELECTED_PAGE:
                int page = data.getInt("page");
                if (Math.abs(page - pager.getCurrentItem()) > 1)
                    pager.setCurrentItem(data.getInt("page"), false);
                else
                    pager.setCurrentItem(data.getInt("page"), true);
                break;
        }
    }
}