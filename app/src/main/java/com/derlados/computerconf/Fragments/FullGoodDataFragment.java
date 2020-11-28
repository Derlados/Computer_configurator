package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Internet.RequestAPI;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FullGoodDataFragment extends Fragment implements View.OnClickListener {

    private Good currentGood; // Текущий товар который отображается
    private LinearLayout dataContainer; // Контейнер в который помещается все характеристики товара
    private TypeGood typeGood; // Тип товара
    private OnFragmentInteractionListener fragmentListener;
    private UserData userData;
    private View currentFragment;

    private final int ADD_TO_BUILD = 0, NOTHING = 1, ADD_TO_FAVORITE = 2;
    private int clickAction; // Переменной присваивается значение одной из констант в зависмость от который будет происходить то или иное действие

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_full_data, container, false);
        userData =  UserData.getUserData();

        if (getArguments() != null) {
            String jsonGood = getArguments().getString("good");
            currentGood = (new Gson()).fromJson(jsonGood, Good.class);
            dataContainer = currentFragment.findViewById(R.id.fragment_full_data_main_container);
            typeGood = (TypeGood) getArguments().get("typeGood");
        }

        setPreviewData(); // Установка превью характеристик

        // Если данных полных нету - они загружаются, иначе можно сразу же их отрисовывать
        if (currentGood.getFullData() == null)
            downloadFullData();
        else
            setFullData();

        return currentFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void downloadFullData() {
        (new GoodFullDataDownloader()).execute(currentGood);
    }

    private void setPreviewData() {
        // Установка изображения (Picasso кеширует запросы, так что если ранее было загружено изображение - оно возьмется из кеша)
        final ImageView imageView = currentFragment.findViewById(R.id.fragment_full_data_img);
        Picasso.get().load(currentGood.getImageUrl()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                currentGood.setImage(((BitmapDrawable)imageView.getDrawable()).getBitmap());
            }

            @Override
            public void onError(Exception e) {

            }
        });

        ((TextView)currentFragment.findViewById(R.id.fragment_full_data_name)).setText(currentGood.getName());
        ((TextView)currentFragment.findViewById(R.id.fragment_full_data_price)).setText(String.format(Locale.getDefault(),"%.2f ГРН", currentGood.getPrice()));
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

        // Скрытие прогресс баров
        currentFragment.findViewById(R.id.fragment_full_data_pb_data).setVisibility(View.GONE);
        currentFragment.findViewById(R.id.fragment_full_data_pb_button).setVisibility(View.GONE);

        Button btAction = currentFragment.findViewById(R.id.fragment_full_data_bt_add_to_build);
        // Если сборки текущей нету, значит пользователь просто смотрит каталог
        // Если сборка есть, но текущего комплектующего нету, значит идет выбор товара для сборки
        // В другом случае пользователь просто смотрит описание того комплектующего, что он выбрал
        if (userData.getCurrentBuild() == null) {
            btAction.setText(R.string.add_favorite);
            clickAction = ADD_TO_FAVORITE;
        }
        else if (userData.getCurrentBuild().getGood(typeGood) == null) {
            btAction.setText(R.string.add_to_build);
            clickAction = ADD_TO_BUILD;
        }
        else {
            btAction.setText(R.string.in_build);
            clickAction = NOTHING;
        }
        btAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (clickAction) {
            case ADD_TO_BUILD:
                userData.getCurrentBuild().addToBuild(typeGood, currentGood);
                Toast.makeText(App.getApp().getApplicationContext(), "Добавлено в сборку", Toast.LENGTH_SHORT).show();
                fragmentListener.onFragmentInteraction(this, null, OnFragmentInteractionListener.Action.RETURN_FRAGMENT_BY_TAG, new Bundle(), "Build");
                break;
            case ADD_TO_FAVORITE: //TODO
                break;
            case NOTHING:
                break;
        }
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
                    .baseUrl("http://buildpc.netxisp.host")
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
