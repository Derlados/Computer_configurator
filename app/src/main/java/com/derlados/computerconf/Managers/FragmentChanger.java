package com.derlados.computerconf.Managers;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Stack;

public class FragmentChanger {
    // Запоминание фрагментов реализовано через стек. Непонятно почему, но Id у всех фрагментов одинаковы,при том что сами фрагменты разные,
    // следовательно не получается их передать в BackStac. Может быть я найду когда нибудь этому объяснение
    private static Stack<Fragment> activeFragments = new Stack<>();
    private static FragmentManager fragmentManager = null;
    private static int containerId; // id основного контейнера для фрагментов (в приложении он всего один)

    // Инициализация, должна быть вызвана перед использованием класса
    public static void init (Fragment activeFr, FragmentManager frManager, int contId) {
        fragmentManager = frManager;
        containerId = contId;
        activeFragments.push(activeFr);
    }

    /* Открытие фрагмент
    * Парматры:
    * fragment - фрагмент который необходимо открыть
    * saveFragment - флаг, true - спрятать предыдущий фрагмент, false - заменить предыдущий новым
    * */
    public static void  nextFragment (Fragment fragment, boolean saveFragment) {
        FragmentTransaction fTrans = fragmentManager.beginTransaction();

        if (saveFragment) {
            fTrans.hide(activeFragments.peek());
            fTrans.add(containerId, fragment);
        }
        else
            fTrans.replace(containerId, fragment);

        activeFragments.push(fragment); // Запоминание нового фрагмент
        fTrans.addToBackStack(null);   // Добавление изменнений в стек
        fTrans.commit();
    }

    // Закрытие фрагмент и возврат к предыдущему и очистка стека
    public static void backFragment () {
        activeFragments.pop();
    }
}
