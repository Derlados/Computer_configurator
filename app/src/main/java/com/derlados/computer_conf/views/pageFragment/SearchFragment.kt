package com.derlados.computer_conf.views.pageFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.views.ComponentSearchFragment
import com.derlados.computer_conf.views.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_shop.view.*

class SearchFragment : PageFragment(), View.OnClickListener {
    private lateinit var frListener: OnFragmentInteractionListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_shop, container, false)

        fragment.fragment_search_component_cpu.setOnClickListener(this)
        fragment.fragment_search_component_gpu.setOnClickListener(this)
        fragment.fragment_search_component_motherboard.setOnClickListener(this)
        fragment.fragment_search_component_hdd.setOnClickListener(this)
        fragment.fragment_search_component_ssd.setOnClickListener(this)
        fragment.fragment_search_component_ram.setOnClickListener(this)
        fragment.fragment_search_component_power_supply.setOnClickListener(this)
        fragment.fragment_search_component_case.setOnClickListener(this)

        return fragment
    }

    /**
     * Слушатель нажатия кнопки. Переход к поиску компонентов
     */
    override fun onClick(view: View) {
        val data = Bundle() // Данные которые будут переданы другому фрагменту
        val componentCategory: ComponentCategory = when (view.id) {
            R.id.fragment_search_component_cpu -> ComponentCategory.CPU
            R.id.fragment_search_component_gpu -> ComponentCategory.GPU
            R.id.fragment_search_component_motherboard -> ComponentCategory.MOTHERBOARD
            R.id.fragment_search_component_hdd -> ComponentCategory.HDD
            R.id.fragment_search_component_ssd -> ComponentCategory.SSD
            R.id.fragment_search_component_ram -> ComponentCategory.RAM
            R.id.fragment_search_component_power_supply -> ComponentCategory.POWER_SUPPLY
            R.id.fragment_search_component_case -> ComponentCategory.CASE
            else -> return
        }
        data.putSerializable("category", componentCategory)
        frListener.nextFragment(this, ComponentSearchFragment(), BackStackTag.COMPONENT_SEARCH)
    }
}