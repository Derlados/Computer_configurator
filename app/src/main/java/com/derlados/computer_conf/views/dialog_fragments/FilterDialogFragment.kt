package com.derlados.computer_conf.views.dialog_fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Range
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import com.derlados.computer_conf.R
import com.derlados.computer_conf.data_classes.FilterAttribute
import com.derlados.computer_conf.views.BuildConstructorFragment
import com.derlados.computer_conf.views.ComponentSearchFragment
import com.google.android.material.slider.RangeSlider
import kotlinx.android.synthetic.main.dialog_fragment_filters.view.*
import kotlinx.android.synthetic.main.inflate_filter_block.view.*

class FilterDialogFragment(private var filters: HashMap<Int, FilterAttribute>,
                           private val resultListener: (HashMap<Int, ArrayList<String>>, HashMap<Int, Pair<Float, Float>>) -> Unit)
    : DialogFragment() {

    private val chosenFilters = HashMap<Int, ArrayList<String>>()
    private val chosenRangeFilters = HashMap<Int, Pair<Float, Float>>()

    private var rangeSliders: ArrayList<Pair<RangeSlider, List<Float>>> = ArrayList() // Массив пар слайдера и диапазона
    private var checkBoxes: ArrayList<CheckBox> = ArrayList() // Все чекбоксы

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = createView()
        builder.setView(view)

        val dialog = builder.create()

        view.dialog_fragment_filters_bt_reset.setOnClickListener {
            resetAll()
            resultListener(chosenFilters, chosenRangeFilters)
            dialog.dismiss()
        }

        view.dialog_fragment_filters_bt_apply.setOnClickListener {
            resultListener(chosenFilters, chosenRangeFilters)
            dialog.dismiss()
        }

        return dialog
    }

    private fun createView() : View {
        val dialog = layoutInflater.inflate(R.layout.dialog_fragment_filters, null)
        val container = dialog.dialog_fragment_filters_ll_main_container

        for ((key, filterAttribute) in filters) {
            val dialogBlock = layoutInflater.inflate(R.layout.inflate_filter_block, container, false)
            val tableValues = dialogBlock.inflate_filter_block_tl_attribute_values

            dialogBlock.inflate_filter_block_bt_attribute.text = filterAttribute.name
            dialogBlock.inflate_filter_block_bt_attribute.setOnClickListener {
                toggleListVisibility(tableValues)
            }

            if (filterAttribute.isRange) {
                val range = Pair(filterAttribute.values[0].toFloat(), filterAttribute.values[1].toFloat())
                createRangeSlider(tableValues, key, range, filterAttribute.step)
            } else {
                createCheckBoxes(tableValues, key, filterAttribute.values)
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
    }

    private fun createRangeSlider(parent: ViewGroup, key: Int, values: Pair<Float, Float>, step: Float) {
        val rangeSlider = layoutInflater.inflate(R.layout.inflate_filter_range, parent, false) as RangeSlider

        rangeSlider.valueFrom = values.first
        rangeSlider.valueTo = values.second
        rangeSlider.stepSize = step
        rangeSlider.values = values.toList()

       rangeSlider.addOnChangeListener { _, _, _ ->
           val min = rangeSlider.values.minOrNull()
           val max =  rangeSlider.values.maxOrNull()
           if (min != null && max != null) {
               chosenRangeFilters[key] = Pair(min, max)
           }

       }
    }

    private fun addFilterValue(key: Int, value: String) {
        if (!chosenFilters.containsKey(key)) {
            chosenFilters[key] = ArrayList()
        }
        chosenFilters[key]?.add(value)
    }

    private fun removeFilterValue(key: Int, value: String) {
        chosenFilters[key]?.remove(value)
        if (chosenFilters[key]?.size == 0) {
            chosenFilters.remove(key)
        }
    }

    private fun toggleListVisibility(table: View) {
        if (table.visibility == View.GONE) {
            table.visibility = View.VISIBLE
        } else {
            table.visibility = View.GONE
        }
    }

    private fun resetAll() {
        chosenRangeFilters.clear()
        chosenFilters.clear()

        for (i in 0 until checkBoxes.size) {
            checkBoxes[0].isChecked = false
        }

        for (i in 0 until rangeSliders.size) {
            rangeSliders[i].first.values = rangeSliders[i].second
        }
    }
}