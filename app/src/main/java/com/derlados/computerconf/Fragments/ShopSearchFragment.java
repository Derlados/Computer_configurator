package com.derlados.computerconf.Fragments;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Good.Good;
import com.derlados.computerconf.Managers.RequestHelper;
import com.derlados.computerconf.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class ShopSearchFragment extends Fragment implements View.OnClickListener {

    LinearLayout goodsContainer;
    int currentPage = 1;

    enum Direction {
        NEXT,
        BACK,
        START,
        CURRENT
    }
    ArrayList<Good> goodsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_shop_search, container, false);
        goodsContainer = fragment.findViewById(R.id.fragment_shop_search_goods_container);
        DownloadPage(getArguments().getString("typeGood"), Direction.CURRENT);
        return fragment;
    }

    //TODO
    // Вынести в сервис
    private void DownloadPage(String typeGood, Direction dir) {
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
        }

        // Загрузка страницы
        String apiUrl = RequestHelper.MAIN_URL + String.format("goods?typeGood=%s&page=%d", typeGood, currentPage);
        RequestHelper.getRequest(getContext(), apiUrl, RequestHelper.TypeRequest.STRING, new RequestHelper.CallBack<String>() {

            @Override
            public void call(String response) {
                // Парсинг всех товаров на страницы
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); ++i)
                    {
                        JSONObject jsonGood = new JSONObject(jsonArray.get(i).toString());
                        Good good = gson.fromJson(jsonGood.toString(), Good.class);
                        goodsList.add(good);
                        createGoodUI(good);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void fail(String message) {
                Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    public void createGoodUI(Good good) {
        RelativeLayout blank = (RelativeLayout) getLayoutInflater().inflate(R.layout.inflate_good_blank, goodsContainer, false);
        blank.setOnClickListener(this);
        //Взятие основной таблицы информации об комплектующем
        TableLayout tableData = (TableLayout)blank.getChildAt(1);

        // Установка имени
        TextView nameText = (TextView) ((TableRow)tableData.getChildAt(0)).getChildAt(0);
        nameText.setText(good.getName());

        // Установка самих характеристик
        // Preview - таблица 5x2 (1 строка - название, 2 и 4 - характеристики, 3 и 5 - значения)
        HashMap<String, String> previewStats = good.getStats();
        TableRow row1 = ((TableRow)tableData.getChildAt(1));
        TableRow row2 = ((TableRow)tableData.getChildAt(2));
        TableRow row3 = ((TableRow)tableData.getChildAt(3));
        TableRow row4 = ((TableRow)tableData.getChildAt(4));

        // key - характеристика, value - значение
        int count = 0;
        for (HashMap.Entry<String, String> entry: previewStats.entrySet()) {

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

    //TODO
    // Загрузку нужно будет вынести в сервис
    public void loadImage(final ImageView imageView, Good good) {
        RequestHelper.getRequest(getContext(), good.getImageUrl(), RequestHelper.TypeRequest.IMAGE, new RequestHelper.CallBack<Bitmap>() {

            @Override
            public void call(Bitmap response) {
                imageView.setImageBitmap(response);
            }

            @Override
            public void fail(String message) {
                Toast.makeText(getContext(), "Проблемы с сервером", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO
    // Здесь должен быть переход для просмотра подробной информации о комплектующем
    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(), "Clicable", Toast.LENGTH_SHORT).show();
    }

}
