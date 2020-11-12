package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Objects.Build;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BuildFullFragment extends Fragment implements TextWatcher {

    private final String DATA_TYPE_GOOD_KEY = "typeGood";
    private OnFragmentInteractionListener fragmentListener;
    private View currentFragment;
    private Build currentBuild;

    // Поля для модифицакции после возврата с меню выбора комплектующего
    private TypeGood typeGoodToModify;
    private LinearLayout containerToModify;
    private TextView tvPrice; // Текстовое поле с ценой
    private TextView tvComplete;
    private ImageView imageBuild; //TODO

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_build_full, container, false);
        currentBuild = UserData.getUserData().getCurrentBuild();
        setBuildContent();
        return currentFragment;
    }

    @Override
    public void onDestroy() {
        UserData.getUserData().saveCurrentBuild(); // Сохранение сборки перед выходом
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        // Изменения должны происходить если окно было показано и это не первый заход в данное окно
        if (!hidden && containerToModify != null) {
            int countGoodInList = containerToModify.getChildCount() - 1;
            // Если количество товаров в сборке изменилось - пользователь вернулся выбрав товар, иначе ничего не выбрал
            if (countGoodInList !=  currentBuild.getGoodList(typeGoodToModify).size()) {
                setHeaderData(); // В заголовке данные могли изменится, потому их надо обновить
                Good goodToModify =  currentBuild.getGoodByIndex(typeGoodToModify, containerToModify.getChildCount() - 1);
                createGoodUI(goodToModify, containerToModify, containerToModify.getChildCount() - 1);
            }
        }
    }

    // Установка всего контента который находится в сборке, установка всех обработчиков нажатий
    private void setBuildContent() {

        // Установка значений в текстовые поля (имя сборки, цена, описание, статус завершенности)
        EditText tvName = ((EditText) currentFragment.findViewById(R.id.fragment_build_full_et_name_build));
        tvName.setText(currentBuild.getName());
        tvName.addTextChangedListener(this);

        EditText tvDesc = ((EditText) currentFragment.findViewById(R.id.fragment_build_full_et_desc));
        tvDesc.setText(currentBuild.getDescription());
        tvDesc.addTextChangedListener(this);

        // Установка данных заголовка (нахождение полей и запись в них значения)
        tvComplete = currentFragment.findViewById(R.id.fragment_build_full_tv_complete_status);
        tvPrice = ((TextView) currentFragment.findViewById(R.id.fragment_build_full_tv_price));
        setHeaderData();

        // Установка обработчиков для кнопок скрытия/раскрытия блоков (невозможно прямо прописать в xml, так как это всё реализовано на фрагменте), следовательно обработчики вызывают функцию

        // Структура блока описания (0 - текст "Описание", 1 - поле для ввода описания. Далее четные кнопки хедеры блоков, нечетные - содержание каждого блока (Linear layout))
        LinearLayout fullDesc = currentFragment.findViewById(R.id.fragment_build_full_ll_full_desc);
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
            TypeGood typeGood = getTypeGood(block.getId());

            // Добавление комплектующих
            ArrayList<Good> goodList = UserData.getUserData().getCurrentBuild().getGoodList(typeGood);
            for (int j = 0; j < goodList.size(); ++j)
                createGoodUI(goodList.get(j), block, block.getChildCount() - 1);

            // Кнопка на добавление нового комплектующего (ереводит на магазин)
            block.getChildAt(block.getChildCount() - 1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickGood(view);
                }
            });
        }
    }

    // Установка всех данных в заголовке (цена сборки, статус завершенности, изображение)
    private void setHeaderData() {
        tvPrice.setText(String.format(Locale.getDefault(), "%.2f ГРН",  currentBuild.getPrice()));
        if (currentBuild.isComplete())
            tvComplete.setText(getResources().getString(R.string.complete));
        else
            tvComplete.setText(getResources().getString(R.string.not_complete));
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private void createGoodUI(Good good, final LinearLayout goodsContainer, int index) {
        final RelativeLayout blank = (RelativeLayout) getLayoutInflater().inflate(R.layout.inflate_good_blank, goodsContainer, false);

        // Отображение полной статистики при нажатии на бланк комплектующего
        blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullGoodData(view);
            }
        });

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
        blank.findViewById(R.id.inflate_good_blank_pb).setVisibility(View.GONE);

        // Кнопка удалить комплектующее
        ImageButton ibtDelete = blank.findViewById(R.id.inflate_good_blank_ibt_corner);
        ibtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBuild.deleteGoodByIndex(getTypeGood(goodsContainer.getId()), goodsContainer.indexOfChild(blank)); // Удаление идет относительно текущего положения в списке
                goodsContainer.removeView(blank); // Удаление комплектующего
                setHeaderData(); // Данные заголовка необходимо обновить
            }
        });
        ibtDelete.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_trash, App.getApp().getTheme())); // Отрисовка значка



        goodsContainer.addView(blank, index);
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
            case R.id.fragment_build_full_ll_other:
                typeGood = TypeGood.OTHERS;
                break;
        }

        return typeGood;
    }

    ////////////////////////////////////////////////////////////////////////////// Обработчиков кнопок //////////////////////////////////////////////////////////////////////////////

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
        // Данные для модификации
        containerToModify = (LinearLayout) view.getParent();
        typeGoodToModify = getTypeGood(containerToModify.getId());

        // Подготовка данных
        Bundle data = new Bundle();
        data.putSerializable(DATA_TYPE_GOOD_KEY, typeGoodToModify);

        fragmentListener.onFragmentInteraction(this, new ShopSearchFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
    }

    public void openFullGoodData(View view) {
        // Отправка объекта в следующий фрагмет для отображения полной информации о нем
        Bundle data = new Bundle();
        Gson gson = new Gson();

        // Получение контейнера списка и определение типа объекта
        LinearLayout block = (LinearLayout) view.getParent();
        TypeGood typeGood = getTypeGood(block.getId());

        // Подгтовка данных, сериализация
        Good sendGood = currentBuild.getGoodByIndex(typeGood, block.indexOfChild(view)); // Объект получается по индексу вьюшки бланка в списке
        data.putString("good", gson.toJson(sendGood)); // Объект передается в виде json строки, сам берется относительно его положения в контейнере
        data.putSerializable("typeGood", typeGood);

        // Отображение полной информации о комплектующем
        fragmentListener.onFragmentInteraction(this, new FullGoodDataFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //TODO
    // Может необходимо производить контроль и ограничить количество символов для имени сборки
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        // Нахождение нужного EditText
        View view = getActivity().getCurrentFocus();
        String text = ((EditText) view).getText().toString();

        // Определение того, какое поле меняет юзер
        if (view.getId() == R.id.fragment_build_full_et_desc)
            currentBuild.setDescription(text);
        else if (view.getId() == R.id.fragment_build_full_et_name_build) {
            currentBuild.setName(text);
        }
    }
}
