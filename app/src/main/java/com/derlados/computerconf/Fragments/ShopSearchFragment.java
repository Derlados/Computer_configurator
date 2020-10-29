package com.derlados.computerconf.Fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Good.Good;
import com.derlados.computerconf.Managers.RequestHelper;
import com.derlados.computerconf.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

public class ShopSearchFragment extends Fragment implements View.OnClickListener {

    // Направление движения по страницам
    enum Direction {
        NEXT,
        BACK,
        START,
        CURRENT,
        CHOSEN_PAGE
    }
    int currentPage = 1, maxPages;

    LinearLayout goodsContainer; // XML контейнер (лаяут) в который ложаться все товары
    String typeGood; // Тип комплектующего на текущей странице
    ArrayList<Good> goodsList = new ArrayList<>(); // Список с комплектующими

    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_shop_search, container, false);
        goodsContainer = fragment.findViewById(R.id.fragment_shop_search_goods_container);
        typeGood = getArguments().getString("typeGood");
        downloadPage(typeGood, Direction.CURRENT, null);
        return fragment;
    }

    // Загрузка страницы (загрузка всех превью данных для отображения на странице)
    private void downloadPage(String typeGood, Direction dir, Integer page) {
         goodsContainer.removeAllViews();// Очистка фрагмента фрагмента

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

        //TODO
        // Загрузка страницы, вынести в отдельный класс с потоками
        String apiUrl = RequestHelper.MAIN_URL + String.format("goods?typeGood=%s&page=%d", typeGood, currentPage);
        RequestHelper.getRequest(getContext(), apiUrl, RequestHelper.TypeRequest.STRING, new RequestHelper.CallBack<String>() {

            @Override
            public void call(String response) {
                // Парсинг всех товаров на страницы
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    maxPages = jsonObject.getInt("maxPages");
                    JSONArray jsonArray = jsonObject.getJSONArray("goods");

                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); ++i)
                    {
                        JSONObject jsonGood = new JSONObject(jsonArray.get(i).toString());
                        Good good = gson.fromJson(jsonGood.toString(), Good.class);
                        goodsList.add(good);
                        createGoodUI(good);
                    }
                    createGoodsFlipPager();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception ex) {
                        Log.e("error", ex.toString());
                    }
                }

            }

            @Override
            public void fail(String message) {
                try {
                    Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Log.e("error", e.toString());
                }
            }
        });
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private void createGoodUI(Good good) {
        RelativeLayout blank = (RelativeLayout) getLayoutInflater().inflate(R.layout.inflate_good_blank, goodsContainer, false);
        blank.setOnClickListener(this);
        //Взятие основной таблицы информации об комплектующем
        TableLayout tableData = (TableLayout)blank.getChildAt(1);

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
        ((TextView) blank.getChildAt(2)).setText(String.format(Locale.getDefault(), "%.2f ГРН", good.getPrice()));

        loadImage((ImageView) blank.getChildAt(0), good);

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
        else if (maxPages - currentPage < 4) { // Если текущая страница отличается менее чем на 4, нужно показать страницы без ".." с правой части
            texts[1] = "..";
            for (int i = 1; i < 5; ++i)
                texts[COUNT_ELEMENTS - i - 1] = Integer.toString(maxPages - i);
        }
        else { // если страница в середине всего количеста, показывается в стандартном виде с двумя пропусками ".." на обоих сторонах
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
    //TODO
    // Загрузку нужно будет вынести в отдельный класс с потоками
    public void loadImage(final ImageView imageView, final Good good) {
        RequestHelper.getRequest(getContext(), good.getImageUrl(), RequestHelper.TypeRequest.IMAGE, new RequestHelper.CallBack<Bitmap>() {

            @Override
            public void call(Bitmap response) {
                good.setImage(response);
                imageView.setImageBitmap(response);
            }

            @Override
            public void fail(String message) {
                Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
            }
        });
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
                Bundle data = new Bundle();
                //TODO
                // Доделать корректный выбор товара
                data.putString("good", (new Gson()).toJson(goodsList.get(0)));
                fragmentListener.onFragmentInteraction(this, new FullDataFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data);
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
}
