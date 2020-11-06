package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Objects.Build;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Managers.RequestHelper;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class FullGoodDataFragment extends Fragment {

    Good currentGood; // Текущий товар который отображается
    LinearLayout dataContainer; // Контейнер в который помещается все характеристики товара
    TypeGood typeGood; // Тип товара
    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_full_data, container, false);

        if (getArguments() != null) {
            String jsonGood = getArguments().getString("good");
            String jsonBmp = getArguments().getString("imageGood");
            currentGood = (new Gson()).fromJson(jsonGood, Good.class);
            currentGood.setImage((new Gson()).fromJson(jsonBmp, Bitmap.class));
            dataContainer = fragment.findViewById(R.id.fragment_full_data_main_container);
            typeGood = (TypeGood) getArguments().get("typeGood");
        }

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        downloadFullData(); // Загрузка всех характеристик товара
        setPreviewData(); // Установка превью характеристик

        // Обработчик нажатий кнопки
        getView().findViewById(R.id.fragment_full_data_bt_add_to_build).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserData.getUserData(getActivity().getApplicationContext()).getCurrentBuild().addToBuild(typeGood, currentGood);
                Toast.makeText(getActivity().getApplicationContext(), "Добавлено в сборку", Toast.LENGTH_SHORT).show();
                backToBuild();
            }
        });
    }

    //TODO
    // Вынести в отдельный класс с потоками
    private void downloadFullData() {
        String apiUrl = RequestHelper.MAIN_URL + String.format("goods/fullData?urlFullData=%s", currentGood.getUrlFullData());
        RequestHelper.getRequest(getContext(), apiUrl, RequestHelper.TypeRequest.STRING, new RequestHelper.CallBack<String>() {

            @Override
            public void call(String response) {
                Type type = new TypeToken<ArrayList<Good.dataBlock>>() {}.getType();
                ArrayList<Good.dataBlock> fullData = (new Gson()).fromJson(response, type);
                currentGood.setFullData(fullData);;
                setFullData();
            }

            @Override
            public void fail(String message) {
                Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPreviewData() {
        View fragment = getView();

        ((ImageView)fragment.findViewById(R.id.fragment_full_data_img)).setImageBitmap(currentGood.getImage());
        ((TextView)fragment.findViewById(R.id.fragment_full_data_name)).setText(currentGood.getName());
        ((TextView)fragment.findViewById(R.id.fragment_full_data_price)).setText(String.format(Locale.getDefault(),"%.2f ГРН", currentGood.getPrice()));
    }

    private void setFullData() {
        ArrayList<Good.dataBlock> fullData = currentGood.getFullData();


        for (int i = 0; i < fullData.size(); ++i) {
            TextView header = (TextView) getLayoutInflater().inflate(R.layout.inflate_full_data_block_header, dataContainer, false);
            header.setText(fullData.get(i).header);
            dataContainer.addView(header);

            // Строка характеристики (Имя:значение)
            for (HashMap.Entry<String, String> entryStat : fullData.get(i).data.entrySet()) {
                LinearLayout dataDesc = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_goods_datablock_description_string, dataContainer, false);

                ((TextView)dataDesc.getChildAt(0)).setText(entryStat.getKey()); // Установка имени
                ((TextView)dataDesc.getChildAt(1)).setText(entryStat.getValue()); // Установка значения характеристики
                dataContainer.addView(dataDesc);
            }
        }
    }

    private void backToBuild() {
        Bundle data = new Bundle();
        data.putString("tag", "Build");
        fragmentListener.onActivityInteraction(this, OnFragmentInteractionListener.Action.RETURN_FRAGMENT_BY_TAG, data);
    }
}
