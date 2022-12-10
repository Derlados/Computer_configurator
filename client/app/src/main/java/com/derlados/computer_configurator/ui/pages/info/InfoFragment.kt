package com.derlados.computer_configurator.ui.pages.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R

class InfoFragment: Fragment(), MainActivity.OnBackPressedListener {
    private lateinit var fragment: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragment = inflater.inflate(R.layout.fragment_info, container, false)
        activity?.title = getString(R.string.updates)
        return fragment
    }

    override fun onBackPressed(): Boolean {
        activity?.title = arguments?.getString("title")
        return true
    }
}