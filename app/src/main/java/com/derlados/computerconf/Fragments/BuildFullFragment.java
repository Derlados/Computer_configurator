package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BuildFullFragment extends Fragment {

    private final String DATA_TYPE_GOOD_KEY = "typeGood";
    private OnFragmentInteractionListener fragmentListener;
    private View fragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_build_full, container, false);
        setBuildContent();
        return fragment;
    }

    // Установка всего контента который находится в сборке, установка всех обработчиков нажатий
    private void setBuildContent() {

        // Установка обработчиков для кнопок скрытия/раскрытия блоков (невозможно прямо прописать в xml, так как это всё реализовано на фрагменте)

        // Структура блока описания (0 - текст "Описание", 1 - поле для ввода описания. Далее четные кнопки хедеры блоков, нечетные - содержание каждого блока (Linear layout))
        LinearLayout fullDesc = fragment.findViewById(R.id.fragment_build_full_ll_full_desc);
        for (int i = 2; i < fullDesc.getChildCount(); i += 2)
            fullDesc.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBlockList(view);
                }
            });

        // Загрузка всех данных сборки в каждый блок комплектующих
        for (int i = 3; i < fullDesc.getChildCount(); i += 2) {
            LinearLayout block = (LinearLayout) fullDesc.getChildAt(i);
            TypeGood type = getTypeGood(block.getId());
            ArrayList<Good> goodList = UserData.getUserData().addBuild().getGoodList(type);

            for (int j = 0; j < goodList.size(); ++j)
                createGoodUI(goodList.get(j), block);

            block.getChildAt(block.getChildCount() - 1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickGood(view);
                }
            });
        }
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private void createGoodUI(Good good, LinearLayout goodsContainer) {
        RelativeLayout blank = (RelativeLayout) getLayoutInflater().inflate(R.layout.inflate_good_blank, goodsContainer, false);

        //TODO
        //blank.setOnClickListener(this);

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

        // Установка изображения
        ((ImageView) blank.findViewById(R.id.inflate_good_blank_img)).setImageBitmap(good.getImage());
        goodsContainer.addView(blank);
    }

    // Обработчик кнопок на визуальное отображения блока комплектующих (раскрыть/скрыть)
    public void openBlockList(View view) {
        // Получение необходимого списка, он находится сразу под кнопкой, потому сначала находится индекс кнопки и потом контейне как <индекс> + 1
        LinearLayout mainList = (LinearLayout) view.getParent();
        int index = ((LinearLayout) view.getParent()).indexOfChild(view);
        LinearLayout blockList = (LinearLayout) mainList.getChildAt(index + 1);

        // Если блок скрыт - он открывается, иначе скрывается
        if (blockList.getVisibility() == View.GONE) {
            blockList.setVisibility(View.VISIBLE);
            ((Button)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0);
        }
        else {
            blockList.setVisibility(View.GONE);
            ((Button)view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_36, 0);
        }
    }

    // Обработчик кнопок на добавление комплектующих - переходит на страницу поиска товаров. Тип комплектующего выбирается по id отцовского контейнера
    public void pickGood(View view) {
        LinearLayout block = (LinearLayout) view.getParent();
        Bundle data = new Bundle();
        data.putString(DATA_TYPE_GOOD_KEY, getTypeGood(block.getId()).toString());

        fragmentListener.onFragmentInteraction(this, new ShopSearchFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data);
    }

    // Получение типа комплектующего с которым происходит взаимодействие
    private TypeGood getTypeGood(int parentId) {
        TypeGood typeGood = null;

        switch (parentId) {
            case R.id.fragment_build_full_ll_cpu:
                typeGood = TypeGood.CPU;
                break;
            case R.id.fragment_build_full_ll_gpu:
                typeGood = TypeGood.GPU;
                break;
            case R.id.fragment_build_full_ll_mb:
                typeGood = TypeGood.MOTHERBOARD;
                break;
            case R.id.fragment_build_full_ll_ram:
                typeGood = TypeGood.RAM;
                break;
            case R.id.fragment_build_full_ll_hdd:
                typeGood = TypeGood.HDD;
                break;
            case R.id.fragment_build_full_ll_ssd:
                typeGood = TypeGood.SSD;
                break;
            case R.id.fragment_build_full_ll_power_supply:
                typeGood = TypeGood.POWER_SUPPLY;
                break;
        }

        return typeGood;
    }
}
