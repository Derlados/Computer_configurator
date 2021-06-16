package com.derlados.computerconf.VIews.PageFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.derlados.computerconf.Constants.TypeComp
import com.derlados.computerconf.VIews.OnFragmentInteractionListener
import com.derlados.computerconf.VIews.ShopSearchFragment
import com.derlados.computerconf.R

class ShopFragment : PageFragment(), View.OnClickListener {
    private var frListener: OnFragmentInteractionListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        frListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragment = inflater.inflate(R.layout.fragment_shop, container, false)
        fragment.findViewById<View>(R.id.fragment_shop_cpu).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_gpu).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_motherboard).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_hdd).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_ssd).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_ram).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_power_supply).setOnClickListener(this)
        fragment.findViewById<View>(R.id.fragment_shop_case).setOnClickListener(this)
        return fragment
    }

    override fun onClick(view: View) {
        val data = Bundle() // Данные которые будут переданы другому фрагменту
        var typeComp: TypeComp? = null
        typeComp = when (view.id) {
            R.id.fragment_shop_cpu -> TypeComp.CPU
            R.id.fragment_shop_gpu -> TypeComp.GPU
            R.id.fragment_shop_motherboard -> TypeComp.MOTHERBOARD
            R.id.fragment_shop_hdd -> TypeComp.HDD
            R.id.fragment_shop_ssd -> TypeComp.SSD
            R.id.fragment_shop_ram -> TypeComp.RAM
            R.id.fragment_shop_power_supply -> TypeComp.POWER_SUPPLY
            R.id.fragment_shop_case -> TypeComp.CASE
            else -> return
        }
        data.putSerializable("typeGood", typeComp)
        frListener!!.nextFragment(this, ShopSearchFragment(), data, null)
    }
}