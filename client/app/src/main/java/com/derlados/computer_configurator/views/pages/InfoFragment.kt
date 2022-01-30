package com.derlados.computer_configurator.views.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import kotlinx.android.synthetic.main.fragment_info.view.*

class InfoFragment: Fragment(), MainActivity.OnBackPressedListener {
    private lateinit var fragment: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragment = inflater.inflate(R.layout.fragment_info, container, false)
        activity?.title = getString(R.string.info)
        return fragment
    }

    override fun onBackPressed(): Boolean {
        activity?.title = arguments?.getString("title")
        return true
    }
}