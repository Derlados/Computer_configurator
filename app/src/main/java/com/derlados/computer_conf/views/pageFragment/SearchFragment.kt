package com.derlados.computer_conf.views.pageFragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.models.ComponentModel
import com.derlados.computer_conf.views.ComponentSearchFragment
import com.derlados.computer_conf.views.OnFragmentInteractionListener
import com.derlados.computer_conf.views.decorators.AnimOnTouchListener

class SearchFragment : PageFragment(), View.OnTouchListener {

    private val btCategories: HashMap<Int, ComponentCategory> = hashMapOf(
            R.id.fragment_search_component_cpu to ComponentCategory.CPU,
            R.id.fragment_search_component_gpu to ComponentCategory.GPU,
            R.id.fragment_search_component_motherboard to ComponentCategory.MOTHERBOARD,
            R.id.fragment_search_component_hdd to ComponentCategory.HDD,
            R.id.fragment_search_component_ssd to ComponentCategory.SSD,
            R.id.fragment_search_component_ram to ComponentCategory.RAM,
            R.id.fragment_search_component_power_supply to ComponentCategory.POWER_SUPPLY,
            R.id.fragment_search_component_case to ComponentCategory.CASE
    )

    private lateinit var frListener: OnFragmentInteractionListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_component_catalogue, container, false)

        for ((btId, _) in btCategories) {
            fragment.findViewById<Button>(btId).setOnTouchListener(AnimOnTouchListener(this))
        }

        return fragment
    }


    /**
     * Слушатель нажатия кнопки. Переход к поиску компонентов
     */
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        btCategories[view?.id]?.let {
            //TODO Вызов модели из View, однако это единственная функция этого экрана
            ComponentModel.chooseCategory(it)

            frListener.nextFragment(this, ComponentSearchFragment(), BackStackTag.COMPONENT_SEARCH)
        }

        return true
    }
}