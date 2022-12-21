package com.derlados.computer_configurator.ui.pages.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.ui.decorators.AnimOnTouchListener
import com.derlados.computer_configurator.ui.OnFragmentInteractionListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_forget_pass.view.*

class ForgetPassFragment: Fragment(), AuthView {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View
    private lateinit var presenter: AuthPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_forget_pass, container, false)
        presenter = AuthPresenter(this, App.app.resourceProvider)
        currentFragment.fragment_forget_pass_bt_accept.setOnTouchListener(AnimOnTouchListener { _, _ ->
            acceptChanges()
            true
        })
        currentFragment.fragment_forget_pass_et_username.editText?.doOnTextChanged  { _, _, _, _ -> clearError(currentFragment.fragment_forget_pass_et_username) }
        currentFragment.fragment_forget_pass_et_new_pass.editText?.doOnTextChanged  { _, _, _, _ -> clearError(currentFragment.fragment_forget_pass_et_new_pass) }
        currentFragment.fragment_forget_pass_et_secret.editText?.doOnTextChanged  { _, _, _, _ -> clearError(currentFragment.fragment_forget_pass_et_secret) }

        return currentFragment
    }

    override fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun returnBack() {
        fragmentListener.popBackStack(BackStackTag.AUTH)
    }

    override fun setInvalid(field: AuthView.Field, message: String) {
        when (field) {
            AuthView.Field.USERNAME -> {
                currentFragment.fragment_forget_pass_et_username.error = message
                currentFragment.fragment_forget_pass_et_username.requestFocus()
            }
            AuthView.Field.PASSWORD -> {
                currentFragment.fragment_forget_pass_et_new_pass.error = message
                currentFragment.fragment_forget_pass_et_new_pass.requestFocus()
            }
            AuthView.Field.SECRET -> {
                currentFragment.fragment_forget_pass_et_secret.error = message
                currentFragment.fragment_forget_pass_et_secret.requestFocus()
            }
        }
    }


    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.isErrorEnabled = false
        textInputLayout.error = null
    }

    private fun acceptChanges() {
        val username: String = currentFragment.fragment_forget_pass_et_username.editText?.text.toString()
        val secret: String = currentFragment.fragment_forget_pass_et_secret.editText?.text.toString()
        val newPass: String = currentFragment.fragment_forget_pass_et_new_pass.editText?.text.toString()
        presenter.tryRestorePassword(username, secret, newPass)
    }
}