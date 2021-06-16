package com.derlados.computerconf.VIews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.derlados.computerconf.App
import com.derlados.computerconf.Constants.TypeComp
import com.derlados.computerconf.MainActivity.OnBackPressedListener
import com.derlados.computerconf.Objects.Build
import com.derlados.computerconf.Objects.Component
import com.derlados.computerconf.Objects.UserData
import com.derlados.computerconf.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.util.*

class BuildFullFragment : Fragment(), TextWatcher, BottomNavigationView.OnNavigationItemSelectedListener, OnBackPressedListener {
    private val DATA_TYPE_GOOD_KEY = "typeGood"
    private var fragmentListener: OnFragmentInteractionListener? = null
    private var currentFragment: View? = null
    private var currentBuild: Build? = null
    private var isSaved = false

    // Поля для модифицакции после возврата с меню выбора комплектующего
    private var typeCompToModify: TypeComp? = null
    private var containerToModify: LinearLayout? = null
    private var tvPrice // Текстовое поле с ценой
            : TextView? = null
    private var tvComplete: TextView? = null
    private var tvCompatibility: TextView? = null
    private var imageBuild: ImageView? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentFragment = inflater.inflate(R.layout.fragment_build_full, container, false)
        (currentFragment.findViewById<View>(R.id.fragment_build_full_menu_bottom_navigator) as BottomNavigationView).setOnNavigationItemSelectedListener(this)
        currentBuild = UserData.userData.currentBuild
        setBuildContent()
        return currentFragment
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        // Изменения должны происходить если окно было показано и это не первый заход в данное окно
        if (!hidden && containerToModify != null && currentBuild!!.getGood(typeCompToModify) != null) {
            setHeaderData() // В заголовке данные могли изменится, потому их надо обновить
            val goodToModify = currentBuild!!.getGood(typeCompToModify)
            createGoodUI(goodToModify, containerToModify!!)
            containerToModify = null
        }
    }

    // Для отклика на кнопку назад
    override fun onBackPressed(): Boolean {
        return if (this.isVisible) {
            if (isSaved) true else {
                showSaveDialog()
                false
            }
        } else true
    }

    // Для обработки диалога который появляется, если пользователь захотел выйти из фрагмента сборки не сохранившись
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SAVE_DIALOG_FRAGMENT && resultCode == Activity.RESULT_OK) {
            UserData.userData.saveCurrentBuild()
            isSaved = true
            Toast.makeText(App.app.applicationContext, "Сохранено", Toast.LENGTH_SHORT).show()
        }
        fragmentListener!!.popBackStack()
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Отрыктие диалога с предложением сохранить изменения
    fun showSaveDialog() {
        val dialogFragment: DialogFragment = SaveDialogFragment()
        dialogFragment.setTargetFragment(this, SAVE_DIALOG_FRAGMENT)
        dialogFragment.show(activity!!.supportFragmentManager, "hello")
    }

    // Установка всего контента который находится в сборке, установка всех обработчиков нажатий
    private fun setBuildContent() {

        // Установка значений в текстовые поля (имя сборки, цена, описание, статус завершенности)
        val tvName = currentFragment!!.findViewById<View>(R.id.fragment_build_full_et_name_build) as EditText
        tvName.setText(currentBuild!!.name)
        tvName.addTextChangedListener(this)
        val tvDesc = currentFragment!!.findViewById<View>(R.id.fragment_build_full_et_desc) as EditText
        tvDesc.setText(currentBuild!!.description)
        tvDesc.addTextChangedListener(this)

        // Установка данных заголовка (нахождение полей и запись в них значения)
        tvComplete = currentFragment!!.findViewById(R.id.fragment_build_full_tv_complete_status)
        tvPrice = currentFragment!!.findViewById(R.id.fragment_build_full_tv_price)
        tvCompatibility = currentFragment!!.findViewById(R.id.fragment_build_full_tv_compatibility)
        imageBuild = currentFragment!!.findViewById(R.id.fragment_build_full_img)
        setHeaderData()

        // Установка обработчиков для кнопок скрытия/раскрытия блоков (невозможно прямо прописать в xml, так как это всё реализовано на фрагменте), следовательно обработчики вызывают функцию

        // Структура блока описания (0 - текст "Описание", 1 - поле с описанием совместимости, 2 - поле для ввода описания.
        // Далее нечетные кнопки хедеры блоков, четные - содержание каждого блока (Linear layout))
        val fullDesc = currentFragment!!.findViewById<LinearLayout>(R.id.fragment_build_full_ll_full_desc)
        run {
            var i = 3
            while (i < fullDesc.childCount) {
                fullDesc.getChildAt(i).setOnClickListener { view -> openBlockList(view) }
                i += 2
            }
        }

        // Загрузка всех данных сборки в каждый блок комплектующих
        var i = 4
        while (i < fullDesc.childCount) {
            val block = fullDesc.getChildAt(i) as LinearLayout
            val typeGood = getTypeGood(block.id)

            // Добавление комплектующих
            val good = UserData.userData.currentBuild.getGood(typeGood)
            good?.let { createGoodUI(it, block) }

            // Кнопка на добавление нового комплектующего (ереводит на магазин)
            block.getChildAt(0).setOnClickListener { view -> pickGood(view) }
            i += 2
        }
        isSaved = true // После того как все данные загружены, сборка считается сохраненной (Потому что изменений не было)
    }

    // Создание бланка предмета, бланк состоит из 3 частей (изображение, таблица информации, цена)
    private fun createGoodUI(component: Component, goodsContainer: LinearLayout) {
        val typeGoodBlank = getTypeGood(goodsContainer.id)
        val blank = layoutInflater.inflate(R.layout.inflate_good_blank, goodsContainer, false) as RelativeLayout

        // Отображение полной статистики при нажатии на бланк комплектующего
        blank.setOnClickListener { view -> openFullGoodData(view) }

        //Взятие основной таблицы информации об комплектующем
        val tableData = blank.findViewById<View>(R.id.inflate_good_blank_tr_data) as TableLayout

        // Установка имени
        val nameText = (tableData.getChildAt(0) as TableRow).getChildAt(0) as TextView
        nameText.text = component.name

        // Установка самих характеристик
        // Preview - таблица 5x2 (1 строка - название, 2 и 4 - характеристики, 3 и 5 - значения)
        val previewData = component.previewData
        val row1 = tableData.getChildAt(1) as TableRow
        val row2 = tableData.getChildAt(2) as TableRow
        val row3 = tableData.getChildAt(3) as TableRow
        val row4 = tableData.getChildAt(4) as TableRow

        // key - характеристика, value - значение
        var count = 0
        for ((key, value) in previewData) {
            if (count < 2) {
                (row1.getChildAt(count) as TextView).text = key
                (row2.getChildAt(count) as TextView).text = value
            } else {
                (row3.getChildAt(count - 2) as TextView).text = key
                (row4.getChildAt(count - 2) as TextView).text = value
            }
            ++count
        }

        // Установка рейтинга и цены
        (blank.findViewById<View>(R.id.inflate_good_blank_tv_price) as TextView).setText(String.format(Locale.getDefault(), "%.2f ГРН", component.price))

        // Установка изображения
        (blank.findViewById<View>(R.id.inflate_good_blank_img) as ImageView).setImageBitmap(component.image)
        blank.findViewById<View>(R.id.inflate_good_blank_pb).visibility = View.GONE

        // Кнопка удалить комплектующее
        val ibtDelete = blank.findViewById<ImageButton>(R.id.inflate_good_blank_ibt_corner)
        ibtDelete.setOnClickListener {
            currentBuild!!.deleteGood(typeGoodBlank) // Удаление идет относительно текущего положения в списке
            goodsContainer.removeView(blank) // Удаление комплектующего
            goodsContainer.getChildAt(0).visibility = View.VISIBLE
            setHeaderData() // Данные заголовка необходимо обновить
        }
        ibtDelete.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_trash, App.app.theme)) // Отрисовка значка

        // Если это ОЗУ или внешняя память - открывается блок для изменения количества в сборке
        if (currentBuild!!.isMultipleGood(typeGoodBlank)) {
            blank.findViewById<View>(R.id.inflate_good_blank_ll_count).visibility = View.VISIBLE
            val tvCount = blank.findViewById<TextView>(R.id.inflate_good_blank_tv_count)
            tvCount.text = Integer.toString(currentBuild!!.getCountGoods(typeGoodBlank))
            blank.findViewById<View>(R.id.inflate_good_blank_bt_increase_count).setOnClickListener { view -> countChange(view, typeGoodBlank, tvCount) }
            blank.findViewById<View>(R.id.inflate_good_blank_bt_reduce_count).setOnClickListener { view -> countChange(view, typeGoodBlank, tvCount) }
        }
        goodsContainer.getChildAt(0).visibility = View.GONE // Скрытие кнопки "+"
        goodsContainer.addView(blank)
    }

    // Установка всех данных в заголовке (цена сборки, статус завершенности, изображение, совместимость)
    private fun setHeaderData() {
        isSaved = false // Любое изменений будет считать сборку измененной и не сохранненой
        tvPrice.setText(String.format(Locale.getDefault(), "%.2f ГРН", currentBuild!!.price))
        if (currentBuild!!.isComplete) {
            tvComplete!!.setText(R.string.complete)
            tvComplete!!.setTextColor(resources.getColor(R.color.green, App.app.theme))
        } else {
            tvComplete!!.setText(R.string.not_complete)
            tvComplete!!.setTextColor(resources.getColor(R.color.red, App.app.theme))
        }

        // Проверка совместимости
        val compatibilityTest = currentBuild!!.compatibility
        if (compatibilityTest == resources.getString(R.string.true_compatibility)) {
            tvCompatibility!!.text = compatibilityTest
            tvCompatibility!!.setTextColor(resources.getColor(R.color.green, App.app.theme))
        } else {
            tvCompatibility!!.text = compatibilityTest
            tvCompatibility!!.setTextColor(resources.getColor(R.color.red, App.app.theme))
        }

        // Изображение сборки берется из корпуса
        if (currentBuild!!.getGood(TypeComp.CASE) != null) {
            val caseImg = currentBuild!!.getGood(TypeComp.CASE).image
            imageBuild!!.setImageBitmap(caseImg)
        } else imageBuild!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_case_24, App.app.theme))
    }

    // Получение типа комплектующего с которым происходит взаимодействие
    private fun getTypeGood(parentId: Int): TypeComp? {
        var typeComp: TypeComp? = null
        when (parentId) {
            R.id.fragment_build_full_ll_cpu -> typeComp = TypeComp.CPU
            R.id.fragment_build_full_ll_gpu -> typeComp = TypeComp.GPU
            R.id.fragment_build_full_ll_mb -> typeComp = TypeComp.MOTHERBOARD
            R.id.fragment_build_full_ll_ram -> typeComp = TypeComp.RAM
            R.id.fragment_build_full_ll_hdd -> typeComp = TypeComp.HDD
            R.id.fragment_build_full_ll_ssd -> typeComp = TypeComp.SSD
            R.id.fragment_build_full_ll_power_supply -> typeComp = TypeComp.POWER_SUPPLY
            R.id.fragment_build_full_ll_case -> typeComp = TypeComp.CASE
        }
        return typeComp
    }

    ///////////////////////////////// Обработчики кнопок ///////////////////////////////////////////
    // Обработчик кнопок на визуальное отображения блока комплектующих (раскрыть/скрыть)
    fun openBlockList(view: View) {
        // Получение необходимого списка, он находится сразу под кнопкой, потому сначала находится индекс кнопки и потом контейне как <индекс> + 1
        val mainList = view.parent as LinearLayout
        val index = (view.parent as LinearLayout).indexOfChild(view)
        val blockList = mainList.getChildAt(index + 1) as LinearLayout

        // Если блок скрыт - он открывается, иначе скрывается
        if (blockList.visibility == View.GONE) {
            blockList.visibility = View.VISIBLE
            (view as Button).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_36, 0)
        } else {
            blockList.visibility = View.GONE
            (view as Button).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_36, 0)
        }
    }

    // Обработчик кнопок на добавление комплектующих - переходит на страницу поиска товаров.
    // Тип комплектующего выбирается по id отцовского контейнера
    fun pickGood(view: View) {
        // Данные для модификации
        containerToModify = view.parent as LinearLayout
        typeCompToModify = getTypeGood(containerToModify!!.id)

        // Подготовка данных
        val data = Bundle()
        data.putSerializable(DATA_TYPE_GOOD_KEY, typeCompToModify)
        fragmentListener!!.nextFragment(this, ShopSearchFragment(), data, null)
    }

    fun openFullGoodData(view: View) {
        // Отправка объекта в следующий фрагмет для отображения полной информации о нем
        val data = Bundle()
        val gson = Gson()

        // Получение контейнера списка и определение типа объекта
        val block = view.parent as LinearLayout
        val typeGood = getTypeGood(block.id)

        // Подгтовка данных, сериализация
        val sendGood = currentBuild!!.getGood(typeGood) // Объект получается по индексу вьюшки бланка в списке
        data.putString("good", gson.toJson(sendGood)) // Объект передается в виде json строки, сам берется относительно его положения в контейнере
        data.putSerializable("typeGood", typeGood)

        // Отображение полной информации о комплектующем
        fragmentListener!!.nextFragment(this, FullGoodDataFragment(), data, null)
    }

    // Уменьшение или увелечение количества комплектующих одного типа в сборке (Доступно только для SSD, HDD, RAM)
    fun countChange(view: View, typeComp: TypeComp?, tvCount: TextView) {
        when (view.id) {
            R.id.inflate_good_blank_bt_increase_count -> currentBuild!!.increaseCountGoods(typeComp)
            R.id.inflate_good_blank_bt_reduce_count -> currentBuild!!.reduceCountGoods(typeComp)
        }
        tvCount.text = Integer.toString(currentBuild!!.getCountGoods(typeComp))
        setHeaderData()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.build_full_menu_bottom_navigator_action_brain_com -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://brain.com.ua/"))
                startActivity(browserIntent)
            }
            R.id.build_full_menu_bottom_navigator_action_save -> {
                isSaved = true
                UserData.userData.saveCurrentBuild() // Сохранение сборки перед выходом
                Toast.makeText(App.app.applicationContext, "Сохранено", Toast.LENGTH_SHORT).show()
                currentBuild = UserData.userData.currentBuild
            }
        }
        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //TODO
    // Может необходимо производить контроль и ограничить количество символов для имени сборки
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        isSaved = false

        // Нахождение нужного EditText
        val view = activity!!.currentFocus
        val text = (view as EditText?)!!.text.toString()

        // Определение того, какое поле меняет юзер
        if (view!!.id == R.id.fragment_build_full_et_desc) currentBuild!!.description = text else if (view.id == R.id.fragment_build_full_et_name_build) {
            currentBuild!!.name = text
        }
    }

    companion object {
        const val SAVE_DIALOG_FRAGMENT = 1
    }
}