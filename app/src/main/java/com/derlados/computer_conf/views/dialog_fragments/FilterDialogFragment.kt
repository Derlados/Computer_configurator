package com.derlados.computer_conf.views.dialog_fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
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
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.SortType
import com.derlados.computer_conf.data_classes.FilterAttribute
import com.derlados.computer_conf.presenters.FiltersPresenter
import com.derlados.computer_conf.view_interfaces.FiltersDialogView
import com.google.android.material.slider.RangeSlider
import kotlinx.android.synthetic.main.dialog_fragment_filters.view.*
import kotlinx.android.synthetic.main.inflate_filter_block.view.*
import kotlinx.android.synthetic.main.inflate_filter_range.view.*
import kotlin.math.round
import kotlin.math.roundToInt


class FilterDialogFragment(private val resultListener: () -> Unit) : DialogFragment(), TextWatcher, FiltersDialogView {
    private val sortsList = listOf(
        Pair(R.string.price_low_to_high, SortType.PRICE_LOW_TO_HIGH),
        Pair(R.string.price_high_to_low, SortType.PRICE_HIGH_TO_LOW),
        Pair(R.string.not_chosen, SortType.DEFAULT)
    )
    private var chosenPosInSpinner: Int = -1

    private var rangeSliders: ArrayList<Pair<RangeSlider, List<Float>>> = ArrayList() // Массив пар слайдера и диапазона
    private var checkBoxes: ArrayList<CheckBox> = ArrayList() // Все чекбоксы
    private lateinit var sortSpinner: Spinner
    private lateinit var etMinPrice: EditText
    private lateinit var etMaxPrice: EditText

    private lateinit var thisDialog: Dialog
    private lateinit var thisView: View
    private lateinit var presenter: FiltersPresenter

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        presenter = FiltersPresenter(this)

        val builder = AlertDialog.Builder(requireActivity())
        thisView = layoutInflater.inflate(R.layout.dialog_fragment_filters, null)
        builder.setView(thisView)

        // Анимация не корректно работает, если диалоговое окно меняет размер
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            thisView.minimumHeight =((windowMetrics.bounds.height() - insets.top - insets.bottom) * 0.92).roundToInt()
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            thisView.minimumHeight = (displayMetrics.heightPixels * 0.92).roundToInt()
        }

        thisDialog = builder.create()
        thisDialog.setCanceledOnTouchOutside(false);
        thisDialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialogInterface: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    thisDialog.hide()
                    return true
                }
                return false
            }
        })

        thisView.dialog_fragment_filters_bt_reset.setOnClickListener {
            presenter.resetFilters()
            thisDialog.hide()
            resultListener()
        }
        thisView.dialog_fragment_filters_bt_apply.setOnClickListener {
            thisDialog.hide()
            resultListener()
        }

        return thisDialog
    }

    override fun initFilters(filters: HashMap<Int, FilterAttribute>, maxPrice: Int) {
        val container = thisView.dialog_fragment_filters_ll_main_container

        initSortSpinner(thisView) // Инициализация сортировок
        initPriceFilter(thisView, maxPrice) // Инициализация фильтрации по цене

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
                    btAttribute.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_arrow_down_36,
                            activity?.theme
                        ), null
                    )
                } else {
                    btAttribute.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_arrow_up_36,
                            activity?.theme
                        ), null
                    )
                }
            }

            if (filterAttribute.isRange) {
                val range = Pair(
                    filterAttribute.values[0].toFloat(),
                    filterAttribute.values[1].toFloat()
                )
                createRangeSlider(ellValues, key, range, filterAttribute.step)
            } else {
                createCheckBoxes(ellValues, key, filterAttribute.values)
            }

            container.addView(dialogBlock)
        }
    }

    override fun setPricesInvalidWarning() {
        etMinPrice.setBackgroundResource(R.drawable.border_red)
        etMaxPrice.setBackgroundResource(R.drawable.border_red)
    }

    override fun resetPricesInvalidWarning() {
        etMinPrice.setBackgroundResource(R.drawable.border_blue)
        etMaxPrice.setBackgroundResource(R.drawable.border_blue)
    }

    override fun resetAll(maxPrice: Int) {
        for (i in 0 until checkBoxes.size) {
            checkBoxes[i].isChecked = false
        }

        for (i in 0 until rangeSliders.size) {
            rangeSliders[i].first.values = rangeSliders[i].second
        }

        sortSpinner.setSelection(sortsList.size - 1)

        etMinPrice.setText("0")
        etMaxPrice.setText(maxPrice.toString())
    }

    override fun closeProgressBar() {
        thisView.dialog_fragment_filters_ll_progress_bar.visibility = View.GONE
        thisView.dialog_fragment_filters_sv_filters.visibility = View.VISIBLE
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
                    presenter.addFilterValue(key, values[i])
                } else {
                    presenter.removeFilterValue(key, values[i])
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
               presenter.setRangeFilter(key, Pair(min, max))
               llRangeFilterBlock.inflate_filter_range_tv_min_value.text = String.format("%s", round(min * 100) / 100)
               llRangeFilterBlock.inflate_filter_range_tv_max_value.text = String.format("%s", round(max * 100) / 100)
           }
       }
        rangeSliders.add(Pair(rangeSlider, values.toList()))

        parent.addView(llRangeFilterBlock)
    }

    /**
     * Инициализация двух EditText для ввода диапазона цены. Выставление начальных значений и добавление оброботчика ввода
     * @param parent - родительский элемент к которому будет прикрекплен слайдер
     * @param maxPrice - максимальная цена, нужна для инициализации правой границы
     */
    private fun initPriceFilter(parent: View, maxPrice: Int) {
        etMinPrice = parent.inflate_filter_range_et_min_price
        etMaxPrice = parent.inflate_filter_range_et_max_price
        etMinPrice.setText("0")
        etMaxPrice.setText(maxPrice.toString())
        etMinPrice.addTextChangedListener(this)
        etMaxPrice.addTextChangedListener(this)
    }

    /**
     * Инициализация выпадающего списка сортировок.
     * @param parent - родительский элемент к которому будет прикрекплен слайдер
     */
    private fun initSortSpinner(parent: View) {
        sortSpinner = parent.dialog_fragment_filters_sp_sort
        val listItems = ArrayList<String>()
        for ((id, _) in sortsList) {
            listItems.add(resources.getString(id))
        }

        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_item, listItems) {
            /**
             * Последний элемент - подсказка, потому количество реальных элементов на 1 меньше
             */
            override fun getCount(): Int {
                return sortsList.size - 1
            }

            /**
             * Изменение цвета у последнего элменета, так как он является подсказкой
             */
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv: TextView = super.getView(position, convertView, parent) as TextView
                if (position == sortsList.size - 1) {
                    tv.setTextColor(Color.GRAY)
                }
                return tv
            }

            /**
             * Выделение цветом выбранного элемента
             */
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if (position == chosenPosInSpinner)
                    tv.setTextColor(resources.getColor(R.color.light_blue, App.app.theme))
                return tv
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item_list)

        sortSpinner.adapter = adapter
        sortSpinner.setSelection(sortsList.size - 1)
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                chosenPosInSpinner = position
                presenter.setSortType(sortsList[position].second)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        presenter.setPriceRange(etMinPrice.text.toString(), etMaxPrice.text.toString())
    }
}