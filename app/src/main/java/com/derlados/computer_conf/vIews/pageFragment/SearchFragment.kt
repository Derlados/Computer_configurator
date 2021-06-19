package com.derlados.computer_conf.VIews.PageFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.derlados.computer_conf.R
import com.derlados.computer_conf.Constants.ComponentCategory
import com.derlados.computer_conf.VIews.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_shop.view.*

class SearchFragment : PageFragment(), View.OnClickListener {
    private var frListener: OnFragmentInteractionListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_shop, container, false)

        fragment.fragment_shop_cpu.setOnClickListener(this)
        fragment.fragment_shop_gpu.setOnClickListener(this)
        fragment.fragment_shop_motherboard.setOnClickListener(this)
        fragment.fragment_shop_hdd.setOnClickListener(this)
        fragment.fragment_shop_ssd.setOnClickListener(this)
        fragment.fragment_shop_ram.setOnClickListener(this)
        fragment.fragment_shop_power_supply.setOnClickListener(this)
        fragment.fragment_shop_case.setOnClickListener(this)

        return fragment
    }

    /**
     * Слушатель нажатия кнопки. Переход к поиску компонентов
     */
    override fun onClick(view: View) {
        val data = Bundle() // Данные которые будут переданы другому фрагменту
        var componentCategory: ComponentCategory? = null
        componentCategory = when (view.id) {
            R.id.fragment_shop_cpu -> ComponentCategory.CPU
            R.id.fragment_shop_gpu -> ComponentCategory.GPU
            R.id.fragment_shop_motherboard -> ComponentCategory.MOTHERBOARD
            R.id.fragment_shop_hdd -> ComponentCategory.HDD
            R.id.fragment_shop_ssd -> ComponentCategory.SSD
            R.id.fragment_shop_ram -> ComponentCategory.RAM
            R.id.fragment_shop_power_supply -> ComponentCategory.POWER_SUPPLY
            R.id.fragment_shop_case -> ComponentCategory.CASE
            else -> return
        }
        data.putSerializable("typeGood", componentCategory)
        //TODO(Переход к фрагменту с компонентами)
        //frListener!!.nextFragment(this, ShopSearchFragment(), data, null)
    }
}