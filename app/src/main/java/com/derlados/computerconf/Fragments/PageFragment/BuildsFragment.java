package com.derlados.computerconf.Fragments.PageFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.HandlerMessages;
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

    private OnFragmentInteractionListener frListener;
    private View fragment;
    private LinearLayout buildsContainer; // Основной контейнер для бланков сборок
    private UserData userData; // Объект данных юзера

    // Данные для модификации после возврата с режима сборки
    private LinearLayout blankToModify;
    private boolean addToParent; // true - новая сборка и её необходимо добавить, false - старая и надо лишь обновить

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

        // Создание списка сохраненных сборок (последняя изменяемая сборка является текущей, потому находится сверху списка)
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                @SuppressWarnings("unchecked")
                ArrayList<Build> builds = (ArrayList<Build>) msg.obj;
                for (int i = 0; i < builds.size(); ++i)
                    setBuildBlank(builds.get(i), (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_build_blank, buildsContainer, false), true);
                // Конец
                if (msg.what == HandlerMessages.FINISH.ordinal())
                    fragment.findViewById(R.id.inflate_fragment_builds_pb).setVisibility(View.GONE);
            }
        };
        userData.getBuilds(handler);

        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        // Если сборка модифицировалась - данные обновляются, если сборка создавалась - добавляется в список
        if (!hidden && blankToModify != null) {

            // Если сборка только создавалась и пользователь её не захотел сохранить - все данные уничтожаются и сборка не добавляется
            if (addToParent && !userData.isCurrentBuildIsSaved()) {
                UserData.getUserData().discardCurrentBuild(true); // Когда пользователь выходит в меню, текущая сборка сбрасывается
                blankToModify = null;
                return;
            }

            if (userData.isCurrentBuildIsSaved())
                setBuildBlank(userData.getCurrentBuild(), blankToModify, addToParent);
            UserData.getUserData().discardCurrentBuild(false); // Когда пользователь выходит в меню, текущая сборка сбрасывается
            blankToModify = null;
        }
    }

    /* Создание бланка
    * Параметры:
    * build - сборка с которой берутся данные
    * buildBlank - бланк (контейнер LinearLayout) в котором будут происходить изменения
    * */
    private void setBuildBlank(final Build build, final LinearLayout buildBlank, boolean addToParent) {

        // Установка превью бланка сборки
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_name)).setText(build.getName());
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_price)).setText(String.format(Locale.getDefault(), "%.2f ГРН", build.getPrice()));

        TextView tvComplete = buildBlank.findViewById(R.id.inflate_build_blank_tv_complete);
        if (build.isComplete()) {
            tvComplete.setText(getResources().getString(R.string.complete));
            tvComplete.setTextColor(getResources().getColor(R.color.green, App.getApp().getTheme()));
        }
        else {
            tvComplete.setText(getResources().getString(R.string.not_complete));
            tvComplete.setTextColor(getResources().getColor(R.color.red, App.getApp().getTheme()));
        }

        // Установка комплектующих в раскрывающемся списке (названия комплектующих)
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_CPU)).setText(getGoodStr(build, TypeGood.CPU));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_GPU)).setText(getGoodStr(build, TypeGood.GPU));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_motherboard)).setText(getGoodStr(build, TypeGood.MOTHERBOARD));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_RAM)).setText(getGoodStr(build, TypeGood.RAM));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_HDD)).setText(getGoodStr(build, TypeGood.HDD));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_SSD)).setText(getGoodStr(build, TypeGood.SSD));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_power_supply)).setText(getGoodStr(build, TypeGood.POWER_SUPPLY));
        ((TextView)buildBlank.findViewById(R.id.inflate_build_blank_tv_chosen_case)).setText(getGoodStr(build, TypeGood.CASE));

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
        buildBlank.findViewById(R.id.inflate_build_blank_ibt_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserData.getUserData().deleteBuildByIndex(buildsContainer.indexOfChild(buildBlank)); // Индекс опеределяется текущим положением бланка в списке
                buildsContainer.removeView(buildBlank); // Удаление бланка
            }
        });

        // Установка изображения сборки
        if (build.getGood(TypeGood.CASE) != null) {
            Bitmap caseImg = build.getGood(TypeGood.CASE).getImage();
            ((ImageView)buildBlank.findViewById(R.id.inflate_build_blank_img)).setImageBitmap(caseImg);
        }
        else
            ((ImageView)buildBlank.findViewById(R.id.inflate_build_blank_img)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_case_24, App.getApp().getTheme()));

        // Добавление бланка
        if (addToParent)
            buildsContainer.addView(buildBlank, buildsContainer.getChildCount() - 1);
    }

    // Формирование строки списка комплектующих
    private String getGoodStr(Build build, TypeGood typeGood) {
        Good good = build.getGood(typeGood);
        if (good != null)
            return good.getName();
        return  "--";
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            // Если нажата кнопка справа внизу - создается новая сборка
            case R.id.fragment_builds_float_bt:
                userData.addNewBuild();

                blankToModify = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_build_blank, buildsContainer, false);
                addToParent = true;
                frListener.nextFragment(this, new BuildFullFragment(), null, "Build");
                break;
            // Нажатие было произведено на саму сборку - открывается нужная сборка, индекс сборки берется относительно положения в списке
            case R.id.inflate_build_blank_ll_preview_info_header:
                LinearLayout listBuildsContainer = fragment.findViewById(R.id.fragment_builds_builds_container);
                int index = listBuildsContainer.indexOfChild((LinearLayout)view.getParent());

                // Получение данных для будущей модификации
                blankToModify = (LinearLayout) view.getParent();
                addToParent = false;

                userData.setCurrentBuild(index); // Выбор идет по индексу положения в списке
                frListener.nextFragment(this, new BuildFullFragment(),  null, "Build");
                break;
        }
    }
}
