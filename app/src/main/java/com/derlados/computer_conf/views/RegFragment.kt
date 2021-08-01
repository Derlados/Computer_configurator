package com.derlados.computer_conf.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.presenters.AuthPresenter
import com.derlados.computer_conf.view_interfaces.AuthView
import kotlinx.android.synthetic.main.fragment_registration.view.*

class RegFragment: Fragment(), AuthView {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSecret: EditText

    private lateinit var llEmail: LinearLayout
    private lateinit var llSecret: LinearLayout

    private var isEmailOpened = true

    private lateinit var presenter: AuthPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_registration, container, false)
        presenter = AuthPresenter(this, App.app.resourceProvider)

        etUsername = currentFragment.fragment_registration_et_username
        etPassword = currentFragment.fragment_registration_et_password
        etConfirmPassword = currentFragment.fragment_registration_et_confirm_password
        etEmail = currentFragment.fragment_registration_et_email
        etSecret = currentFragment.fragment_registration_et_secret

        llEmail = currentFragment.fragment_registration_ll_email
        llSecret = currentFragment.fragment_registration_ll_secret

        currentFragment.fragment_registration_bt_reg.setOnClickListener { register() }
        currentFragment.fragment_registration_tv_to_login.setOnClickListener { changeToLogin() }
        currentFragment.fragment_registration_swap_field.setOnClickListener { swapField() }

        return currentFragment
    }

    override fun onDestroy() {
        presenter.finish()
        super.onDestroy()
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun returnBack() {
        fragmentListener.popBackStack(BackStackTag.MAIN)
    }

    /**
     * Изменения поля почты на секретное слово (второй пароль) и наоборот
     */
    private fun swapField() {
        if (isEmailOpened) {
            llEmail.visibility = View.GONE
            llSecret.visibility = View.VISIBLE
            currentFragment.fragment_registration_swap_field.text = context?.getString(R.string.i_have_an_email)
        } else {
            llEmail.visibility = View.VISIBLE
            llSecret.visibility  = View.GONE
            currentFragment.fragment_registration_swap_field.text = context?.getString(R.string.no_email)
        }
    }

    private fun register() {
        presenter.tryReg(etUsername.text.toString(), etPassword.text.toString(), etConfirmPassword.text.toString(),
                            etEmail.text.toString(), etSecret.text.toString())
    }

    private fun changeToLogin() {
        fragmentListener.popBackStack(BackStackTag.AUTH)
    }
}