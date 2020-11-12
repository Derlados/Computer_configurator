package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Constants.LogsKeys;
import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Internet.GsonSerializers.HashMapDeserializer;
import com.derlados.computerconf.Internet.RequestAPI;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopSearchFragment extends Fragment implements View.OnClickListener {

    // Направление движения по страницам
    enum Direction {
        NEXT,
        BACK,
        START,
        CURRENT,
        CHOSEN_PAGE
    }
    int currentPage = 1, maxPages = 0;

    LinearLayout goodsContainer; // XML контейнер (лаяут) в который ложаться все товары
    TypeGood typeGood; // Тип комплектующего на текущей странице
    ArrayList<Good> goodsList = new ArrayList<>(); // Список с комплектующими
    ArrayList<RelativeLayout> blanks = new ArrayList<>();

    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_shop_search, container, false);
        goodsContainer = fragment.findViewById(R.id.fragment_shop_search_goods_container);
        typeGood = (TypeGood) getArguments().get("typeGood");
        downloadPage(typeGood, Direction.CURRENT, null);
        return fragment;
    }

    // Загрузка страницы (загрузка всех превью данных для отображения на странице)
    private void downloadPage(TypeGood typeGood, Direction dir, Integer page) {
        goodsContainer.removeAllViews();// Очистка фрагмента фрагмента

        // Очистка данных
        goodsList.clear();
        blanks.clear();

        // Выбор страницы которую необходимо загрузить
        switch (dir) {
            case BACK:
                --currentPage;
                break;
            case NEXT:
                ++currentPage;
                break;
            case START:
                currentPage = 1;
                break;
            case CHOSEN_PAGE:
                currentPage = page;
                break;

        }

        GoodsDownloader goodsDownloader = new GoodsDownloader();
        goodsDownloader.execute(typeGood.toString(), Integer.toString(currentPage));
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private void createGoodUI(Good good) {
        RelativeLayout blank = (RelativeLayout) getLayoutInflater().inflate(R.layout.inflate_good_blank, goodsContainer, false);
        blank.setOnClickListener(this);
        //Взятие основной таблицы информации об комплектующем
        TableLayout tableData = (TableLayout)blank.findViewById(R.id.inflate_good_blank_tr_data);

        // Установка имени
        TextView nameText = (TextView) ((TableRow)tableData.getChildAt(0)).getChildAt(0);
        nameText.setText(good.getName());

        // Установка самих характеристик
        // Preview - таблица 5x2 (1 строка - название, 2 и 4 - характеристики, 3 и 5 - значения)
        HashMap<String, String> previewData = good.getPreviewData();
        TableRow row1 = ((TableRow)tableData.getChildAt(1));
        TableRow row2 = ((TableRow)tableData.getChildAt(2));
        TableRow row3 = ((TableRow)tableData.getChildAt(3));
        TableRow row4 = ((TableRow)tableData.getChildAt(4));

        // key - характеристика, value - значение
        int count = 0;
        for (HashMap.Entry<String, String> entry: previewData.entrySet()) {
            if (count < 2) {
                ((TextView) row1.getChildAt(count)).setText(entry.getKey());
                ((TextView) row2.getChildAt(count)).setText(entry.getValue());
            }
            else {
                ((TextView) row3.getChildAt(count - 2)).setText(entry.getKey());
                ((TextView) row4.getChildAt(count - 2)).setText(entry.getValue());
            }

            ++count;
        }

        // Установка рейтинга и цены
        ((TextView) blank.findViewById(R.id.inflate_good_blank_price)).setText(String.format(Locale.getDefault(), "%.2f ГРН", good.getPrice()));

        blanks.add(blank);

        goodsContainer.addView(blank);
    }

    /* Загрузка панели для выбора страниц и её настройка
    * Вид панели для выбора страницы:
    * 1 и последний элемент - кнопки '<', '>'
    * 2 и предпоследний жлемент - текстовые поля, начальная страница и последняя соответственно
    * остальные - промежуточные страницы
    * */
    private void createGoodsFlipPager() {
        LinearLayout flipPager = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_flip_page_navigator, goodsContainer, false);
        final int COUNT_ELEMENTS = 7;
        String[] texts = new String[COUNT_ELEMENTS];

        texts[0] = "1";
        texts[COUNT_ELEMENTS - 1] = Integer.toString(maxPages);

        // Если текущая страница меньше 5, нужно показать страницы без ".." с левой части
        if (currentPage < 5) {
            for (int i = 2; i <= 5; ++i)
                texts[i - 1] = Integer.toString(i);
            texts[COUNT_ELEMENTS - 2] = "..";
        }
        else if (maxPages - currentPage < 4) { // Если текущая страница отличается менее чем на 4 от максимальной, нужно показать страницы без ".." с правой части
            texts[1] = "..";
            for (int i = 1; i < 5; ++i)
                texts[COUNT_ELEMENTS - i - 1] = Integer.toString(maxPages - i);
        }
        else { // если страница в середине всего количества, показывается в стандартном виде с двумя пропусками ".." на обоих сторонах
            texts[1] = "..";
            texts[2] = Integer.toString(currentPage - 1);
            texts[3] = Integer.toString(currentPage);
            texts[4] = Integer.toString(currentPage + 1);
            texts[COUNT_ELEMENTS - 2] = "..";
        }

        // Установка всего текста в соответствующие поля и обработчиков нажатия
        for (int i = 1; i < flipPager.getChildCount() - 1; ++i) {
            TextView tv = ((TextView) flipPager.getChildAt(i));
            tv.setText(texts[i - 1]);
            tv.setOnClickListener(this);

            // Выделение страницы которая была выделена соответствующим цветом
            if (texts[i - 1].equals(Integer.toString(currentPage)))
                tv.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.main_theme_1));
        }
        flipPager.findViewById(R.id.inflate_flip_page_navigator_ibt_back).setOnClickListener(this);
        flipPager.findViewById(R.id.inflate_flip_page_navigator_ibt_next).setOnClickListener(this);

        // Если текущая страница находится на краю списка (слева или справа), в одноий из кнопок для движения впереж/назад нету необходимости
        if (currentPage == 1)
            flipPager.removeViewAt(0);
        else if (currentPage == maxPages)
            flipPager.removeViewAt(flipPager.getChildCount() - 1);

        goodsContainer.addView(flipPager);
    }

    // Загрузка изображений для комплектующих
    public void loadImages() {
        for (int i = 0; i < goodsList.size(); ++i) {
            final RelativeLayout blank = blanks.get(i);
            ImageView imageView = blank.findViewById(R.id.inflate_good_blank_img);
            Picasso.get().load(goodsList.get(i).getImageUrl()).into(imageView,  new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    blank.findViewById(R.id.inflate_good_blank_pb).setVisibility(View.GONE); // Скрытие прогресс бара
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }
    }

    /* Обработчик нажатий. Есть два места для обработки - один из бланков комплектующего и панель прокрутки страниц
    * Нажатие на бланк - вызов подробной информации о комплектующем в новом фрагменте
    * Нажатие на панель страниц - загрузка страницы в соответствии с выбором пользователя
    * */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.inflate_good_blank_rl_blank:
                ImageView iv = view.findViewById(R.id.inflate_good_blank_img);
                Bitmap image = ((BitmapDrawable)iv.getDrawable()).getBitmap();

                // Отправка объекта в следующий фрагмет для отображения полной информации о нем
                Bundle data = new Bundle();
                Gson gson = new Gson();
                Good sendGood = goodsList.get(goodsContainer.indexOfChild(view)); // Объект получается по индексу вьюшки бланка в списке
                sendGood.setImage(image);
                data.putString("good", gson.toJson(sendGood)); // Объект передается в виде json строки, сам берется относительно его положения в контейнере
                data.putSerializable("typeGood", typeGood);
                fragmentListener.onFragmentInteraction(this, new FullGoodDataFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
                break;
            case R.id.inflate_flip_page_navigator_ibt_next:
                downloadPage(typeGood, Direction.NEXT, null);
                break;
            case R.id.inflate_flip_page_navigator_ibt_back:
                downloadPage(typeGood, Direction.BACK, null);
                break;
            default:
                String numPage = ((TextView)view).getText().toString();
                // Если нажатое поле является "..", это событие никак не должно обрабатываться
                if (!numPage.equals(".."))
                    downloadPage(typeGood, Direction.CHOSEN_PAGE, Integer.parseInt(numPage));
                break;
            //TODO
            // Возможно можно будет добавить прямой выбор страницы
        }
    }

    // Класс для загрузки превью информации о комплектующих
    public class GoodsDownloader extends AsyncTask<String, Integer, String> {
        Retrofit retrofit;
        RequestAPI requestAPI;
        final Integer SET_GOODS = 0, SET_FLIP_PAGER = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Создание gson и регистрация собственного сериализатора для HashMap
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(HashMap.class, new HashMapDeserializer())
                    .create();

            // Для работы с сетью
            retrofit =  new Retrofit.Builder()
                    .baseUrl("http://192.168.1.3/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
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

            Integer action = values[0];

            if (action.equals(SET_GOODS)) {
                for (int i = 0; i < goodsList.size(); ++i)
                    createGoodUI(goodsList.get(i));
                loadImages();
            }
            else if (action.equals(SET_FLIP_PAGER))
                createGoodsFlipPager();

        }

        // Получение всего списка товаров. Параметры: 0 - тип комплектующего, 1 - страница которую необходимо загрузить
        // Так же если необходимо загрузить количество страниц - загружает количество страниц
        @Override
        protected String doInBackground(String... values) {
            String typeGood = values[0];
            int page = Integer.parseInt(values[1]);

            // Загрузка списка комплектующих выбранной категории
            Call<ArrayList<Good>> callGoods = requestAPI.getGoodsPage(typeGood, page);
            Response<ArrayList<Good>> response1;
            try {
                response1 = callGoods.execute();
                if (response1.isSuccessful()) {
                    goodsList = response1.body();
                    publishProgress(SET_GOODS);
                }
            }
            catch (Exception e) {
                Log.e(LogsKeys.ERROR_LOG.toString(), e.toString());
            }

            // Если неизвестно максимальное количество страниц в поиске
            if (maxPages == 0) {
                Call<Integer> callMaxPages = requestAPI.getMaxPages(typeGood);
                Response<Integer> response2;
                try {
                    response2 = callMaxPages.execute();
                    if (response2.isSuccessful()) {
                        maxPages = response2.body();
                        publishProgress(SET_FLIP_PAGER);
                    }
                }
                catch (Exception e) {
                    Log.e(LogsKeys.ERROR_LOG.toString(), e.toString());
                }
            }
            else
                publishProgress(SET_FLIP_PAGER);

            return null;
        }

        // Отрисовка всех комплектующих
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
