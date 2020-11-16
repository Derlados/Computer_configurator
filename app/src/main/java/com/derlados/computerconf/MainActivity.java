package com.derlados.computerconf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.derlados.computerconf.Constants.LogsKeys;
import com.derlados.computerconf.Fragments.MainMenuFragment;
import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;
import com.derlados.computerconf.Fragments.PageFragment.MenuPageAdapter;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment mainMenuFragment;

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
    public void onBackPressed() {
        boolean close = true;
        // Проход по фрагментам и попытка взятия  onBackPressedListener у фрагмента, если он реализован
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for(Fragment fragment : fragmentList)
            if (fragment != null && fragment.isVisible()) {
                try {
                    onBackPressedListener bpl = (onBackPressedListener) fragment;
                    close = bpl.onBackPressed();
                }
                catch (Exception e) {
                    Log.w(LogsKeys.WARNING_LOG.toString(), "interface onBackPressedListener not found");
                }
            }

        if (close)
            super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Fragment fragmentSource, Fragment fragmentReceiver, Action action, Bundle data, String backStackTag) {
        FragmentTransaction fTrans = fragmentManager.beginTransaction();
        if (fragmentReceiver != null)
            fragmentReceiver.setArguments(data);

        switch (action)
        {
            case NEXT_FRAGMENT_HIDE:
                if (mainMenuFragment.isVisible())
                    fTrans.hide(mainMenuFragment);
                else
                    fTrans.hide(fragmentSource);

                fTrans.add(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case NEXT_FRAGMENT_REPLACE:
                fTrans.replace(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case RETURN_FRAGMENT_BY_TAG:
                for (int i = fragmentManager.getBackStackEntryCount() - 1; i >= 1; --i) {
                    if (fragmentManager.getBackStackEntryAt(i).getName() == null || !fragmentManager.getBackStackEntryAt(i).getName().equalsIgnoreCase(backStackTag))
                        fragmentManager.popBackStack();
                    else
                        break;
                }
                break;
            case POP_BACK_STACK:
                fragmentManager.popBackStack();
                break;
        }
    }

    // Отклик на BackPressed во фрагментах.
    public interface onBackPressedListener {
        // Обработка BackPressed во фрагмента. Возврат : true - фрагмент можно закрыть, false - фрагмент должен жить
        // Если onBackPressed() возвращает false, то фрагмент сам должен позаботится о освобождении backStack-а
        boolean onBackPressed();
    }
}