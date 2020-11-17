package com.derlados.computerconf.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.MainActivity;
import com.derlados.computerconf.Objects.Build;
import com.derlados.computerconf.Objects.Good;
import com.derlados.computerconf.Objects.UserData;
import com.derlados.computerconf.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BuildFullFragment extends Fragment implements TextWatcher, BottomNavigationView.OnNavigationItemSelectedListener, MainActivity.onBackPressedListener {

    private final String DATA_TYPE_GOOD_KEY = "typeGood";
    public static final int SAVE_DIALOG_FRAGMENT = 1;

    private OnFragmentInteractionListener fragmentListener;
    private View currentFragment;
    private Build currentBuild;

    private boolean isSaved;

    // Поля для модифицакции после возврата с меню выбора комплектующего
    private TypeGood typeGoodToModify;
    private LinearLayout containerToModify;
    private TextView tvPrice; // Текстовое поле с ценой
    private TextView tvComplete;
    private TextView tvCompatibility;
    private ImageView imageBuild;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_build_full, container, false);
        ((BottomNavigationView)currentFragment.findViewById(R.id.fragment_build_full_menu_bottom_navigator)).setOnNavigationItemSelectedListener(this);
        currentBuild = UserData.getUserData().getCurrentBuild();
        setBuildContent();
        return currentFragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        // Изменения должны происходить если окно было показано и это не первый заход в данное окно
        if (!hidden && containerToModify != null && currentBuild.getGood(typeGoodToModify) != null) {
            setHeaderData(); // В заголовке данные могли изменится, потому их надо обновить
            Good goodToModify =  currentBuild.getGood(typeGoodToModify);
            createGoodUI(goodToModify, containerToModify);
            containerToModify = null;
        }
    }

    // Для отклика на кнопку назад
    @Override
    public boolean onBackPressed() {
        if (this.isVisible()) {
            if (isSaved)
                return true;
            else {
                showSaveDialog();
                return false;
            }
        }
        return true;
    }

    // Для обработки диалога который появляется, если пользователь захотел выйти из фрагмента сборки не сохранившись
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SAVE_DIALOG_FRAGMENT && resultCode == Activity.RESULT_OK) {
            UserData.getUserData().saveCurrentBuild();
            isSaved = true;
            Toast.makeText(App.getApp().getApplicationContext(), "Сохранено",Toast.LENGTH_SHORT).show();
        }
        fragmentListener.onFragmentInteraction(null, null, OnFragmentInteractionListener.Action.POP_BACK_STACK, null, null);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Отрыктие диалога с предложением сохранить изменения
    void showSaveDialog() {
        DialogFragment dialogFragment = new SaveDialogFragment();
        dialogFragment.setTargetFragment(this, SAVE_DIALOG_FRAGMENT);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "hello");
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
        tvPrice = currentFragment.findViewById(R.id.fragment_build_full_tv_price);
        tvCompatibility = currentFragment.findViewById(R.id.fragment_build_full_tv_compatibility);
        imageBuild = currentFragment.findViewById(R.id.fragment_build_full_img);
        setHeaderData();

        // Установка обработчиков для кнопок скрытия/раскрытия блоков (невозможно прямо прописать в xml, так как это всё реализовано на фрагменте), следовательно обработчики вызывают функцию

        // Структура блока описания (0 - текст "Описание", 1 - поле с описанием совместимости, 2 - поле для ввода описания.
        // Далее нечетные кнопки хедеры блоков, четные - содержание каждого блока (Linear layout))
        LinearLayout fullDesc = currentFragment.findViewById(R.id.fragment_build_full_ll_full_desc);
        for (int i = 3; i < fullDesc.getChildCount(); i += 2)
            fullDesc.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBlockList(view);
                }
            });

        // Загрузка всех данных сборки в каждый блок комплектующих
        for (int i = 4; i < fullDesc.getChildCount(); i += 2) {
            LinearLayout block = (LinearLayout) fullDesc.getChildAt(i);
            TypeGood typeGood = getTypeGood(block.getId());

            // Добавление комплектующих
            Good good = UserData.getUserData().getCurrentBuild().getGood(typeGood);
            if (good != null)
                createGoodUI(good, block);

            // Кнопка на добавление нового комплектующего (ереводит на магазин)
            block.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickGood(view);
                }
            });
        }

        isSaved = true; // После того как все данные загружены, сборка считается сохраненной (Потому что изменений не было)
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private void createGoodUI(Good good, final LinearLayout goodsContainer) {
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
                currentBuild.deleteGood(getTypeGood(goodsContainer.getId())); // Удаление идет относительно текущего положения в списке
                goodsContainer.removeView(blank); // Удаление комплектующего
                goodsContainer.getChildAt(0).setVisibility(View.VISIBLE);
                setHeaderData(); // Данные заголовка необходимо обновить
            }
        });
        ibtDelete.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_trash, App.getApp().getTheme())); // Отрисовка значка

        goodsContainer.getChildAt(0).setVisibility(View.GONE); // Скрытие кнопки "+"
        goodsContainer.addView(blank);
    }

    // Установка всех данных в заголовке (цена сборки, статус завершенности, изображение, совместимость)
    private void setHeaderData() {
        isSaved = false; // Любое изменений будет считать сборку измененной и не сохранненой

        tvPrice.setText(String.format(Locale.getDefault(), "%.2f ГРН", currentBuild.getPrice()));
        if (currentBuild.isComplete()) {
            tvComplete.setText(R.string.complete);
            tvComplete.setTextColor(getResources().getColor(R.color.green, App.getApp().getTheme()));
        }
        else {
            tvComplete.setText(R.string.not_complete);
            tvComplete.setTextColor(getResources().getColor(R.color.red, App.getApp().getTheme()));
        }

        // Проверка совместимости
        String compatibilityTest = currentBuild.getCompatibility();
        if (compatibilityTest.equals(getResources().getString(R.string.true_compatibility))) {
            tvCompatibility.setText(compatibilityTest);
            tvCompatibility.setTextColor(getResources().getColor(R.color.green, App.getApp().getTheme()));
        }
        else {
            tvCompatibility.setText(compatibilityTest);
            tvCompatibility.setTextColor(getResources().getColor(R.color.red, App.getApp().getTheme()));
        }

        // Изображение сборки берется из корпуса
        if (currentBuild.getGood(TypeGood.CASE) != null) {
            Bitmap caseImg = currentBuild.getGood(TypeGood.CASE).getImage();
            imageBuild.setImageBitmap(caseImg);
        }
        else
            imageBuild.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_case_24, App.getApp().getTheme()));
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
            case R.id.fragment_build_full_ll_case:
                typeGood = TypeGood.CASE;
                break;
        }

        return typeGood;
    }

    ///////////////////////////////// Обработчики кнопок ///////////////////////////////////////////

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

    // Обработчик кнопок на добавление комплектующих - переходит на страницу поиска товаров.
    // Тип комплектующего выбирается по id отцовского контейнера
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
        Good sendGood = currentBuild.getGood(typeGood); // Объект получается по индексу вьюшки бланка в списке
        data.putString("good", gson.toJson(sendGood)); // Объект передается в виде json строки, сам берется относительно его положения в контейнере
        data.putSerializable("typeGood", typeGood);

        // Отображение полной информации о комплектующем
        fragmentListener.onFragmentInteraction(this, new FullGoodDataFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data, null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.build_full_menu_bottom_navigator_action_brain_com:
                showSaveDialog();
                break;
            case R.id.build_full_menu_bottom_navigator_action_save:
                isSaved = true;
                UserData.getUserData().saveCurrentBuild(); // Сохранение сборки перед выходом
                Toast.makeText(App.getApp().getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
        isSaved = false;

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
