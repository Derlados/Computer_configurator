package com.derlados.computer_configurator.views.pages.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.derlados.computer_configurator.App
import com.derlados.computer_configurator.MainActivity
import com.derlados.computer_configurator.R
import com.derlados.computer_configurator.consts.BackStackTag
import com.derlados.computer_configurator.presenters.AuthPresenter
import com.derlados.computer_configurator.view_interfaces.AuthView
import com.derlados.computer_configurator.view_interfaces.MainView
import com.derlados.computer_configurator.views.pages.OnFragmentInteractionListener
import com.derlados.computer_configurator.views.decorators.AnimOnTouchListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment: Fragment(), AuthView, MainActivity.OnBackPressedListener {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var mainView: MainView

    private lateinit var currentFragment: View

    private lateinit var etUsername: TextInputLayout
    private lateinit var etPassword: TextInputLayout
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var presenter: AuthPresenter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
        mainView = context as MainView
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        activity?.title = getString(R.string.authorization)
        currentFragment = inflater.inflate(R.layout.fragment_login, container, false)
        presenter = AuthPresenter(this, App.app.resourceProvider)

        etUsername = currentFragment.fragment_login_et_username
        etPassword = currentFragment.fragment_login_et_password
        etUsername.editText?.doOnTextChanged { _, _, _, _ -> clearError(etUsername) }
        etPassword.editText?.doOnTextChanged{ _, _, _, _ -> clearError(etPassword) }

        currentFragment.fragment_login_bt_login.setOnTouchListener(AnimOnTouchListener { _, _ ->
            login()
            true
        })
        currentFragment.fragment_login_tv_to_reg.setOnClickListener { changeToReg() }
        currentFragment.fragment_login_bt_forget_pass.setOnClickListener {
            fragmentListener.nextFragment(this, ForgetPassFragment(), BackStackTag.FORGET_PASS)
        }

        // Инициализация GoogleSignIn
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::signInGoogle)
        currentFragment.fragment_login_google_sign_in.setOnClickListener { mainView.googleSign.openGoogleSignIn(resultLauncher) }

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
        }
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.isErrorEnabled = false
        textInputLayout.error = null
    }

    private fun login() {
        presenter.tryLogin(etUsername.editText?.text.toString(), etPassword.editText?.text.toString())
    }

    /**
     * Получение данных пользователя после успешного входа
     */
    private fun signInGoogle(result: ActivityResult) {
        if (result.resultCode != 0 && result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = mainView.googleSign.getAccount(task)

            val id = account.id
            val username = account.displayName
            val email = account.email

            if (id != null && username != null && email != null) {
                presenter.tryGoogleSingIn(id, username, email, account.photoUrl?.toString())
            }
        } else {
            Toast.makeText(requireContext(),"Login failed: " +  Gson().toJson(result), Toast.LENGTH_SHORT).show();
        }
    }

    private fun changeToReg() {
        fragmentListener.nextFragment(this, RegFragment(), BackStackTag.REG)
    }
}