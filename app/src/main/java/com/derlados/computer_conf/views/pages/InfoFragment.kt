package com.derlados.computer_conf.views.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R

class InfoFragment: Fragment(), MainActivity.OnBackPressedListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.title = getString(R.string.info)
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onBackPressed(): Boolean {
        activity?.title = arguments?.getString("title")
        return true
    }
}