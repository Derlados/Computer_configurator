package com.derlados.computerconf.Fragments.PageFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Fragments.BuildFullFragment;
import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;
import com.derlados.computerconf.Objects.Build;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class BuildsFragment extends PageFragment implements View.OnClickListener {

    OnFragmentInteractionListener frListener;
    View fragment;
    LinearLayout buildsContainer; // Основной контейнер для бланков сборок
    UserData userData; // Объект данных юзера

    // Данные для модификации после возврата с режима сборки
    LinearLayout blankToModify;
    Build buildToModify;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        frListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_builds, container, false);

        // Определение данных и основного контейнера
        userData = UserData.getUserData();
        buildsContainer = fragment.findViewById(R.id.fragment_builds_builds_container);

        // Кнопка в левом нижнем углу экрана для создания новых сборок
        FloatingActionButton addBuildBt = fragment.findViewById(R.id.fragment_builds_float_bt);
        addBuildBt.setOnClickListener(this);

        //TODO
        // Операция тяжелая для главного потока, необходимо будет перенести в другой, к тому же это надо будет синхронизировать с выгрузкой данных с самого устройства

        // Создание списка сохраненных сборок (последняя изменяемая сборка является текущей, потому находится сверху списка)
        ArrayList<Build> buildsList = userData.getBuilds();
        for (int i = 0; i < buildsList.size(); ++i) {
            setBuildBlank(buildsList.get(i), (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_build_blank, buildsContainer, false), true);
        }

        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        // Изменение сборки над которой произошло изменение, текущая сборка и есть той сборкой которую меняли
        if (blankToModify != null && !hidden)
            setBuildBlank(buildToModify, blankToModify, false);
    }

    /* Создание бланка
    * Параметры:
    * build - сборка с которой берутся данные
    * buildBlank - бланк (контейнер LinearLayout) в котором будут происходить изменения
    * */
    private void setBuildBlank(Build build, final LinearLayout buildBlank, boolean addToParent) {

        // Установка превью бланка сборки
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_name)).setText(build.getName());
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_price)).setText(String.format(Locale.getDefault(), "%.2f ГРН", build.getPrice()));
        if (build.isComplete())
            ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_complete)).setText(getResources().getString(R.string.complete));
        else
            ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_complete)).setText(getResources().getString(R.string.not_complete));

        // Установка комплектующих в раскрывающемся списке (названия комплектующих)
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_CPU)).setText(createStrGoodList(build, TypeGood.CPU));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_GPU)).setText(createStrGoodList(build, TypeGood.GPU));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_motherboard)).setText(createStrGoodList(build, TypeGood.MOTHERBOARD));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_RAM)).setText(createStrGoodList(build, TypeGood.RAM));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_HDD)).setText(createStrGoodList(build, TypeGood.HDD));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_SSD)).setText(createStrGoodList(build, TypeGood.SSD));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_power_supply)).setText(createStrGoodList(build, TypeGood.POWER_SUPPLY));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_others)).setText(createStrGoodList(build, TypeGood.OTHERS));

        // Кнопка для сворачивания/раскрытия списка комлектующих в сборке
        (buildBlank.findViewById(R.id.inflate_build_blank_ibt_hide)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout previewInfo = buildBlank.findViewById(R.id.inflate_build_blank_ll_preview_info);
                ImageButton ibt = (ImageButton) view;

                if (previewInfo.getVisibility() == View.GONE) {
                    previewInfo.setVisibility(View.VISIBLE);
                    ibt.setImageResource(R.drawable.ic_arrow_up_36);
                }
                else {
                    previewInfo.setVisibility(View.GONE);
                    ibt.setImageResource(R.drawable.ic_arrow_down_36);
                }
            }
        });

        // Обработка на нажатие по всему бланку
        buildBlank.findViewById(R.id.inflate_build_blank_ll_preview_info_header).setOnClickListener(this);

        // Добавление бланка
        if (addToParent)
            buildsContainer.addView(buildBlank);
    }

    // Формирование строки списка комплектующих
    private String createStrGoodList(Build build, TypeGood typeGood) {
        StringBuilder strGoodsList = new StringBuilder();
        ArrayList<Good> goodsList = build.getGoodList(typeGood);

        if (goodsList.size() != 0) {
            strGoodsList.append(goodsList.get(0).getName());
            for (int i = 1; i < goodsList.size(); ++i) {
                strGoodsList.append("\n");
                strGoodsList.append(goodsList.get(i).getName());
            }
        }
        else
            strGoodsList.append("--");

        return strGoodsList.toString();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            // Если нажата кнопка справа внизу - создается новая сборка
            case R.id.fragment_builds_float_bt:
                userData.addNewBuild();
                frListener.onFragmentInteraction(this, new BuildFullFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, "Build");
                break;
            // Нажатие было произведено на саму сборку - открывается нужная сборка, индекс сборки берется относительно положения в списке
            case R.id.inflate_build_blank_ll_preview_info_header:
                LinearLayout listBuildsContainer = fragment.findViewById(R.id.fragment_builds_builds_container);
                int index = listBuildsContainer.indexOfChild((LinearLayout)view.getParent());

                // Получение данных для будущей модификации
                buildToModify = userData.getBuildByIndex(index);
                blankToModify = (LinearLayout) view.getParent();

                userData.setCurrentBuild(buildToModify); // Выбранная сборка становится текущей (что собственно логично)
                frListener.onFragmentInteraction(this, new BuildFullFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, null, "Build");
                break;
        }
    }
}
