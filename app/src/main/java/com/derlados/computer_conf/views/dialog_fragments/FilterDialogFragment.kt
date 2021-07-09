package com.derlados.computer_conf.views.dialog_fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.SortType
import com.derlados.computer_conf.data_classes.FilterAttribute
import com.derlados.computer_conf.data_classes.FilterUserChoice
import com.google.android.material.slider.RangeSlider
import kotlinx.android.synthetic.main.dialog_fragment_filters.view.*
import kotlinx.android.synthetic.main.inflate_filter_block.view.*
import kotlinx.android.synthetic.main.inflate_filter_range.view.*
import java.lang.Exception
import kotlin.math.round
import kotlin.math.roundToInt


class FilterDialogFragment(
    private var filters: HashMap<Int, FilterAttribute>,
    private val maxPrice: Int,
    private val resultListener: (userChoice: FilterUserChoice) -> Unit
) : DialogFragment(), TextWatcher {

    private val sortsList = listOf(
            Pair(R.string.price_low_to_high, SortType.PRICE_LOW_TO_HIGH),
            Pair(R.string.price_high_to_low, SortType.PRICE_HIGH_TO_LOW)
    )

    private val userChoice = FilterUserChoice(HashMap(), HashMap(), Pair(0, maxPrice), SortType.DEFAULT)

    private var rangeSliders: ArrayList<Pair<RangeSlider, List<Float>>> = ArrayList() // Массив пар слайдера и диапазона
    private var checkBoxes: ArrayList<CheckBox> = ArrayList() // Все чекбоксы
    private lateinit var etMinPrice: EditText
    private lateinit var etMaxPrice: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = createView()

        // Анимация не корректно работает, если диалоговое окно меняет размер
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            view.minimumHeight =((windowMetrics.bounds.height() - insets.top - insets.bottom) * 0.92).roundToInt()
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            view.minimumHeight = (displayMetrics.heightPixels * 0.92).roundToInt()
        }
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialogInterface: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.hide()
                    return true
                }
                return false
            }
        })

        view.dialog_fragment_filters_bt_reset.setOnClickListener {
            resetAll()
            resultListener(userChoice)
            dialog.hide()
        }
        view.dialog_fragment_filters_bt_apply.setOnClickListener {
            resultListener(userChoice)
            dialog.hide()
        }

        return dialog
    }

    @SuppressLint("InflateParams")
    private fun createView() : View {
        val dialog = layoutInflater.inflate(R.layout.dialog_fragment_filters, null)
        val container = dialog.dialog_fragment_filters_ll_main_container

        initSortSpinner(dialog) // Инициализация сортировок
        initPriceFilter(dialog) // Инициализация фильтрации по цене

        // Установка фильтров
        for ((key, filterAttribute) in filters) {
            val dialogBlock = layoutInflater.inflate(
                R.layout.inflate_filter_block,
                container,
                false
            )
            val ellValues = dialogBlock.inflate_filter_block_ll_attribute_values
            val btAttribute = dialogBlock.inflate_filter_block_bt_attribute

            btAttribute.text = filterAttribute.name
            btAttribute.setOnClickListener {
                ellValues.toggle()

                // Поворот изображения на 180 градусов стрелки
                if (ellValues.isExpanded) {
                    btAttribute.setCompoundDrawablesWithIntrinsicBounds(null, null, ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down_36, activity?.theme), null)
                } else {
                    btAttribute.setCompoundDrawablesWithIntrinsicBounds(null, null, ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_up_36, activity?.theme), null)
                }
            }

            if (filterAttribute.isRange) {
                val range = Pair(filterAttribute.values[0].toFloat(), filterAttribute.values[1].toFloat())
                createRangeSlider(ellValues, key, range, filterAttribute.step)
            } else {
                createCheckBoxes(ellValues, key, filterAttribute.values)
            }

            container.addView(dialogBlock)
        }

        return dialog
    }

    /**
     * Создание чебоксов. Значения разбиты по две колонки с сортировкой по колонкам сверху-вниз начиная с левой
     * @param parent - родительский контейнер куда будут добавлятся строки с чекбокс полями
     * @param key - атрибут (нужен для передачи в OnCheckedChangeListener)
     * @param values - все возможные значения атрибута
     */
    private fun createCheckBoxes(parent: ViewGroup, key: Int, values: ArrayList<String>) {
        var countRows = values.size / 2
        var isLastInvisible = false
        if (values.size % 2 == 1) {
            ++countRows
            isLastInvisible = true
        }

        val leftCheckBoxes = ArrayList<CheckBox>()
        val rightCheckBoxes = ArrayList<CheckBox>()
        for (i in 0 until countRows) {
            val row = layoutInflater.inflate(R.layout.inflate_filter_row, parent, false) as TableRow
            leftCheckBoxes.add(row.getChildAt(0) as CheckBox)
            rightCheckBoxes.add(row.getChildAt(1) as CheckBox)
            parent.addView(row)
        }

        for (i in 0 until values.size) {
            val checkBox = if (i < leftCheckBoxes.size) leftCheckBoxes[i] else rightCheckBoxes[i - leftCheckBoxes.size]

            checkBox.text = values[i]
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    addFilterValue(key, values[i])
                } else {
                    removeFilterValue(key, values[i])
                }
            }
        }

        if (isLastInvisible) {
            rightCheckBoxes[rightCheckBoxes.size - 1].visibility = View.INVISIBLE
        }

        checkBoxes.addAll(leftCheckBoxes)
        checkBoxes.addAll(rightCheckBoxes)
    }

    /**
     * Создание слайдеров для фильтрации. Каждый слайдер является двухсторонним
     * @param parent - родительский элемент к которому будет прикрекплен слайдер
     * @param key - ключ, id атрибута
     * @param values - пара значений, минимальное и максимальное значения соответственно
     * @param step - шаг слайдера
     */
    private fun createRangeSlider(parent: ViewGroup, key: Int, values: Pair<Float, Float>, step: Float) {
        val llRangeFilterBlock = layoutInflater.inflate(R.layout.inflate_filter_range, parent, false) as LinearLayout
        val rangeSlider = llRangeFilterBlock.inflate_filter_range_slider

        rangeSlider.valueFrom = values.first
        rangeSlider.valueTo = values.second
        rangeSlider.stepSize = step
        rangeSlider.values = values.toList()
        llRangeFilterBlock.inflate_filter_range_tv_min_value.text = values.first.toString()
        llRangeFilterBlock.inflate_filter_range_tv_max_value.text = values.second.toString()

        rangeSlider.addOnChangeListener { _, _, _ ->
           val min = rangeSlider.values.minOrNull()
           val max =  rangeSlider.values.maxOrNull()
           if (min != null && max != null) {
               userChoice.chosenRangeFilters[key] = Pair(min, max)
               llRangeFilterBlock.inflate_filter_range_tv_min_value.text = String.format("%s", round(min * 100) / 100)
               llRangeFilterBlock.inflate_filter_range_tv_max_value.text = String.format("%s", round(max * 100) / 100)
           }
       }
        rangeSliders.add(Pair(rangeSlider, values.toList()))

        parent.addView(llRangeFilterBlock)
    }

    private fun initPriceFilter(dialog: View) {
        etMinPrice = dialog.inflate_filter_range_et_min_price
        etMaxPrice = dialog.inflate_filter_range_et_max_price
        etMinPrice.setText(userChoice.chosenRangePrice.first.toString())
        etMaxPrice.setText(userChoice.chosenRangePrice.second.toString())
        etMinPrice.addTextChangedListener(this)
        etMaxPrice.addTextChangedListener(this)
    }

    private fun initSortSpinner(dialog: View) {
        val spinner = dialog.dialog_fragment_filters_sp_sort
        val listItem = ArrayList<String>()
        for ((id, _) in sortsList) {
            listItem.add(resources.getString(id))
        }

        spinner.item = listItem
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                spinner.hint = ""

                userChoice.chosenSortType = sortsList[position].second
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    private fun addFilterValue(key: Int, value: String) {
        if (!userChoice.chosenFilters.containsKey(key)) {
            userChoice.chosenFilters[key] = ArrayList()
        }
        userChoice. chosenFilters[key]?.add(value)
    }

    private fun removeFilterValue(key: Int, value: String) {
        userChoice.chosenFilters[key]?.remove(value)
        if (userChoice.chosenFilters[key]?.size == 0) {
            userChoice.chosenFilters.remove(key)
        }
    }

    private fun resetAll() {
        userChoice.chosenRangeFilters.clear()
        userChoice.chosenFilters.clear()
        userChoice.chosenRangePrice = Pair(0, maxPrice)
        userChoice.chosenSortType = SortType.DEFAULT

        for (i in 0 until checkBoxes.size) {
            checkBoxes[i].isChecked = false
        }

        for (i in 0 until rangeSliders.size) {
            rangeSliders[i].first.values = rangeSliders[i].second
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        val minValue: Int
        val maxValue: Int
        try {
            minValue = etMinPrice.text.toString().toInt()
            maxValue = etMaxPrice.text.toString().toInt()
        } catch (e: Exception) {
            return
        }

        userChoice.chosenRangePrice = if (minValue > maxValue) {
            etMinPrice.setBackgroundResource(R.drawable.border_red)
            etMaxPrice.setBackgroundResource(R.drawable.border_red)
            Pair(0, maxPrice)
        } else {
            etMinPrice.setBackgroundResource(R.drawable.border_blue)
            etMaxPrice.setBackgroundResource(R.drawable.border_blue)
            Pair(minValue, maxValue)
        }
    }
}