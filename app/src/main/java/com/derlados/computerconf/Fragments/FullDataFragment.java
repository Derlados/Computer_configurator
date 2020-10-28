package com.derlados.computerconf.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Good.Good;
import com.derlados.computerconf.Managers.RequestHelper;
import com.derlados.computerconf.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class FullDataFragment extends Fragment {

    Good currentGood;
    LinearLayout dataContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_full_data, container, false);

        if (getArguments() != null) {
            String jsonGood = getArguments().getString("good");
            currentGood = (new Gson()).fromJson(jsonGood, Good.class);
            dataContainer = fragment.findViewById(R.id.fragment_full_data_main_container);
        }

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        downloadFullData();
        setPreviewData();
    }

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
}
