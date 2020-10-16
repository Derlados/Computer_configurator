package com.derlados.computerconf.Fragments;

import android.app.DownloadManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class ShopSearchFragment extends Fragment {

    LinearLayout goodsContainer;
    int currentPage = 1;
    enum Direction {
        NEXT,
        BACK,
        START,
        CURRENT
    }
    LinkedList<Good> goodsList = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_shop_search, container, false);
        goodsContainer = fragment.findViewById(R.id.fragment_shop_search_goods_container);
        DownloadPage(getArguments().getString("typeGood"), Direction.CURRENT);
        return fragment;
    }

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
        String apiUrl = String.format("goods?typeGood=%s&page=%d", typeGood, currentPage);
        RequestHelper.getRequest(getContext(), apiUrl, RequestHelper.TypeRequest.STRING, new RequestHelper.CallBack<String>() {

            @Override
            public void call(String response) {
                // Парсинг всех товаров на страницы
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); ++i)
                    {
                        JSONObject jsonGood = new JSONObject(jsonArray.get(i).toString());
                        String name = jsonGood.getString("name");
                        double rating = 0;
                        double price = jsonGood.getDouble("price");
                        Good good = new Good(name, rating, price);
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

            }
        });
    }

    public void createGoodUI(Good good) {
        RelativeLayout blank = (RelativeLayout) getLayoutInflater().inflate(R.layout.inflate_good_blank, goodsContainer, false);
        //Взятие основной таблицы информации об комплектующем
        TableLayout tableData = (TableLayout)blank.getChildAt(1);

        // Установка имени
        TextView nameText = (TextView) ((TableRow)tableData.getChildAt(0)).getChildAt(0);
        nameText.setText(good.getName());

        // Установка имен характеристик для превью
        String[] previewStats = good.getPreviewStats();
        TableRow row1 = ((TableRow)tableData.getChildAt(1));
        TableRow row2 = ((TableRow)tableData.getChildAt(3));
        for (int i = 0; i < 3; ++ i)
            ((TextView)row1.getChildAt(i)).setText(previewStats[i]);
        for (int i = 0; i < 3; ++ i)
            ((TextView)row2.getChildAt(i)).setText(previewStats[i + 3]);

        //TODO
        // Установка самих характеристик
        goodsContainer.addView(blank);
    }

    //TODO
    public void setImage(ImageView imageView, Good good) {
       imageView.setImageBitmap(good.getImg());
    }
}
