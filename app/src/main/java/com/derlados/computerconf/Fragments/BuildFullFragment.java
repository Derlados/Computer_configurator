package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.R;

import java.lang.reflect.Type;

public class BuildFullFragment extends Fragment {

    private final String DATA_TYPE_GOOD_KEY = "typeGood";
    private OnFragmentInteractionListener fragmentListener;
    private View fragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_build_full, container, false);

        return fragment;
    }

    // Установка всего контента который находится в сборке, установка всех обработчиков нажатий
    private void setBuildContent() {

        // Установка обработчиков для кнопок скрытия/раскрытия блоков (невозможно прямо прописать в xml, так как это всё реализовано на фрагменте)

        // Структура блока описания (0 - текст "Описание", 1 - поле для ввода описания. Далее четные кнопки хедеры блоков, нечетные - содержание каждого блока (Linear layout))
        LinearLayout fullDesc = fragment.findViewById(R.id.fragment_build_full_ll_full_desc);
        for (int i = 2; i < fullDesc.getChildCount(); ++i)
            fullDesc.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBlockList(view);
                }
            });

        // Загрузка всех данных сборки в каждый блок комплектующих
        
    }

    // Обработчик кнопок на визуальное отображения блока комплектующих (раскрыть/скрыть)
    public void openBlockList(View view) {
        LinearLayout blockList = (LinearLayout) view.getParent();

        // Если блок скрыт - он открывается, иначе скрывается
        if (blockList.getVisibility() == View.GONE) {
            blockList.setVisibility(View.VISIBLE);
            ((Button)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0);
        }
        else {
            blockList.setVisibility(View.GONE);
            ((Button)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_36, 0);
        }
    }

    // Обработчик кнопок на добавление комплектующих - переходит на страницу поиска товаров. Тип комплектующего выбирается по id отцовского контейнера
    public void pickGood(View view) {
        LinearLayout block = (LinearLayout) view.getParent();
        Bundle data = new Bundle();
        data.putString(DATA_TYPE_GOOD_KEY, getTypeGood(block.getId()).toString());

        fragmentListener.onFragmentInteraction(this, new ShopSearchFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data);
    }

    // Получение типа комплектующего с которым происходит взаимодействие
    public TypeGood getTypeGood(int parentId) {
        TypeGood typeGood = null;

        switch (parentId) {
            case R.id.fragment_build_full_ll_cpu:
                typeGood = TypeGood.CPU;
                break;
            case R.id.fragment_build_full_ll_gpu:
                typeGood = TypeGood.GPU;
                break;
            case R.id.fragment_build_full_ll_mb:
                typeGood = TypeGood.MOTHERBOARD;
                break;
            case R.id.fragment_build_full_ll_ram:
                typeGood = TypeGood.RAM;
                break;
            case R.id.fragment_build_full_ll_hdd:
                typeGood = TypeGood.HDD;
                break;
            case R.id.fragment_build_full_ll_ssd:
                typeGood = TypeGood.SSD;
                break;
            case R.id.fragment_build_full_ll_power_supply:
                typeGood = TypeGood.POWER_SUPPLY;
                break;
        }

        return typeGood;
    }
}
