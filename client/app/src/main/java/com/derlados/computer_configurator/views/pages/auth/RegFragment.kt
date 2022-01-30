package com.derlados.computer_configurator.views.pages.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.presenters.AuthPresenter
import com.derlados.computer_configurator.view_interfaces.AuthView
import com.derlados.computer_configurator.views.pages.OnFragmentInteractionListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_registration.view.*

class RegFragment: Fragment(), AuthView, MainActivity.OnBackPressedListener {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View

    private lateinit var etUsername: TextInputLayout
    private lateinit var etPassword: TextInputLayout
    private lateinit var etConfirmPassword: TextInputLayout
    private lateinit var etSecret: TextInputLayout

    private lateinit var presenter: AuthPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.title = getString(R.string.authorization)
        currentFragment = inflater.inflate(R.layout.fragment_registration, container, false)
        presenter = AuthPresenter(this, App.app.resourceProvider)

        etUsername = currentFragment.fragment_registration_et_username
        etPassword = currentFragment.fragment_registration_et_password
        etConfirmPassword = currentFragment.fragment_registration_et_confirm_password
        etSecret = currentFragment.fragment_registration_et_secret

        etUsername.editText?.doOnTextChanged { _, _, _, _ -> clearError(etUsername) }
        etPassword.editText?.doOnTextChanged { _, _, _, _ -> clearError(etPassword) }
        etConfirmPassword.editText?.doOnTextChanged { _, _, _, _ -> clearError(etConfirmPassword) }
        etSecret.editText?.doOnTextChanged { _, _, _, _ -> clearError(etSecret) }

        currentFragment.fragment_registration_bt_reg.setOnClickListener { register() }
        currentFragment.fragment_registration_tv_to_login.setOnClickListener { changeToLogin() }

        return currentFragment
    }

    override fun onBackPressed(): Boolean {
        activity?.title = arguments?.getString("title")
        return true
    }

    override fun onDestroy() {
        presenter.finish()
        super.onDestroy()
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun returnBack() {
        fragmentListener.popBackStack(BackStackTag.MAIN)
    }

    override fun setInvalid(field: AuthView.Field, message: String) {
        when (field) {
            AuthView.Field.USERNAME -> {
                etUsername.error = message
                etUsername.requestFocus()
            }
            AuthView.Field.PASSWORD -> {
                etPassword.error = message
                etPassword.requestFocus()
            }
            AuthView.Field.CONFIRM_PASSWORD -> {
                etConfirmPassword.error = message
                etConfirmPassword.requestFocus()
            }
            AuthView.Field.SECRET -> {
                etSecret.error = message
                etSecret.requestFocus()
            }
        }
    }

    private fun register() {
        presenter.tryReg(etUsername.editText?.text.toString(), etPassword.editText?.text.toString(), etConfirmPassword.editText?.text.toString(), etSecret.editText?.text.toString())
    }

    private fun changeToLogin() {
        fragmentListener.popBackStack(BackStackTag.AUTH)
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.isErrorEnabled = false
        textInputLayout.error = null
    }
}