package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.derlados.computerconf.Internet.RequestAPI;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FullGoodDataFragment extends Fragment implements View.OnClickListener {

    Good currentGood; // Текущий товар который отображается
    LinearLayout dataContainer; // Контейнер в который помещается все характеристики товара
    TypeGood typeGood; // Тип товара
    OnFragmentInteractionListener fragmentListener;
    UserData userData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_full_data, container, false);
        userData =  UserData.getUserData();

        if (getArguments() != null) {
            String jsonGood = getArguments().getString("good");
            currentGood = (new Gson()).fromJson(jsonGood, Good.class);
            dataContainer = fragment.findViewById(R.id.fragment_full_data_main_container);
            typeGood = (TypeGood) getArguments().get("typeGood");
        }

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setPreviewData(); // Установка превью характеристик
        // Если данных полных нету - они загружаются, иначе можно сразу же их отрисовывать
        if (currentGood.getFullData() == null)
            downloadFullData();
        else
            setFullData();

        // Обработчик нажатий кнопки
        getView().findViewById(R.id.fragment_full_data_bt_add_to_build).setOnClickListener(this);
    }

    private void downloadFullData() {
        (new GoodFullDataDownloader()).execute(currentGood);
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

    @Override
    public void onClick(View view) {
        userData.getCurrentBuild().addToBuild(typeGood, currentGood);
        Toast.makeText(getActivity().getApplicationContext(), "Добавлено в сборку", Toast.LENGTH_SHORT).show();
        fragmentListener.onFragmentInteraction(this, null, OnFragmentInteractionListener.Action.RETURN_FRAGMENT_BY_TAG, new Bundle(), "Build");
    }

    // Класс для загрузки информации
    public class GoodFullDataDownloader extends AsyncTask<Good, Integer, String> {
        Retrofit retrofit;
        RequestAPI requestAPI;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Для работы с сетью
            retrofit =  new Retrofit.Builder()
                    .baseUrl("http://192.168.1.3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            requestAPI = retrofit.create(RequestAPI.class);
        }

        /* Прорисовка элементов
         * SET_GOODS - прорисовка комплектующих
         * SET_FLIP_PAGER - прорисовка
         * */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        // Получение полной информации о комплектующем, если их нету. Параметры: 0 - тип комплектующего
        @Override
        protected String doInBackground(Good... values) {
            Good good = values[0];

            if (good.getFullData() == null) {
                Call<ArrayList<Good.dataBlock>> call = requestAPI.getGoodFullData(good.getUrlFullData());
                try {
                    Response<ArrayList<Good.dataBlock>> response = call.execute();
                    if (response.isSuccessful())
                        good.setFullData(response.body());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        // Отрисовка полной информации
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setFullData();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
