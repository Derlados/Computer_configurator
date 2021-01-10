package com.derlados.computerconf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.derlados.computerconf.Constants.LogsKeys;
import com.derlados.computerconf.Fragments.MainMenuFragment;
import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;

import java.util.List;

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
            if (fragment != null) {
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
    public void nextFragment(Fragment fragmentSource, Fragment fragmentReceiver, Bundle data, String backStackTag) {
        FragmentTransaction fTrans = fragmentManager.beginTransaction().setCustomAnimations(R.anim.flip_fragment_in, R.anim.flip_fragment_out,  R.anim.flip_fragment_in, R.anim.flip_fragment_out);
        fragmentReceiver.setArguments(data);
        if (mainMenuFragment.isVisible())
            fTrans.hide(mainMenuFragment);
        else
            fTrans.hide(fragmentSource);

        fTrans.add(R.id.activity_main_ll_container, fragmentReceiver);
        fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
        fTrans.commit();
    }

    @Override
    public void popBackStack() {
        fragmentManager.popBackStack();
    }

    @Override
    public void popBackStack(String backStackTag) {
        fragmentManager.popBackStack(backStackTag, 0);
    }

    // Отклик на BackPressed во фрагментах.
    public interface onBackPressedListener {
        /**
         * Обработка BackPressed во фрагмента
         * @return : true - фрагмент можно закрыть, false - фрагмент должен жить
         * Если onBackPressed() возвращает false, то фрагмент сам должен позаботится о освобождении backStack-а
         */
        boolean onBackPressed();
    }
}