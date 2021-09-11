package com.derlados.computer_conf.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.presenters.AuthPresenter
import com.derlados.computer_conf.view_interfaces.AuthView
import com.derlados.computer_conf.views.components.GoogleSign
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment: Fragment(), AuthView {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var googleSign: GoogleSign
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var presenter: AuthPresenter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_login, container, false)
        presenter = AuthPresenter(this, App.app.resourceProvider)

        etUsername = currentFragment.fragment_login_et_username
        etPassword = currentFragment.fragment_login_et_password

        currentFragment.fragment_login_bt_login.setOnClickListener { login() }
        currentFragment.fragment_login_tv_to_reg.setOnClickListener { changeToReg() }

        // Инициализация GoogleSignIn
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::signInGoogle)
        googleSign = GoogleSign(context, resultLauncher)
        currentFragment.fragment_login_google_sign_in.setOnClickListener { googleSign.openGoogleSignIn() }

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

    private fun login() {
        presenter.tryLogin(etUsername.text.toString(), etPassword.text.toString())
    }

    /**
     * Получение данных пользователя после успешного входа
     */
    private fun signInGoogle(result: ActivityResult) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = googleSign.getAccount(task)

        val id = account.id
        val username = account.displayName
        val email = account.email

        if (id != null && username != null && email != null) {
            presenter.tryGoogleSingIn(id, username, email, account.photoUrl?.toString())
        }
    }

    private fun changeToReg() {
        fragmentListener.nextFragment(this, RegFragment(), BackStackTag.REG)
    }
}