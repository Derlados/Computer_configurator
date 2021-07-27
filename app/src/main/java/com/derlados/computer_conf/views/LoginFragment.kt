package com.derlados.computer_conf.views

import android.app.Activity
import android.app.TaskInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.R
import com.derlados.computer_conf.presenters.AuthPresenter
import com.derlados.computer_conf.view_interfaces.AuthView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment: Fragment(), AuthView {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var currentFragment: View

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
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
        currentFragment.fragment_login_google_sign_in.setOnClickListener { openGoogleSignIn() }

        currentFragment.fragment_login_tv_to_reg.setOnClickListener { changeToReg() }

        // Результат логина
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            signInGoogle(task)
        }

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
        fragmentListener.popBackStack()
    }

    private fun login() {
        presenter.tryLogin(etUsername.text.toString(), etPassword.text.toString())
    }

    /**
     * Открытие меню для выбора гугл аккаунта и авторизации
     */
    private fun openGoogleSignIn() {
        // Инициализация клиента
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("768086134086-kbt53st09v1402m4kku2ghi5seeohto3.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        account = GoogleSignIn.getLastSignedInAccount(context)



        // Запуска активити с выбором гугл аккаунта
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    /**
     * Получение данных пользователя после успешного входа
     */
    private fun signInGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val id = account.id
            val username = account.displayName
            Toast.makeText(context, username, Toast.LENGTH_SHORT).show()
            if (id != null && username != null) {
                presenter.tryGoogleSingIn(id, username, account.photoUrl?.toString())
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleApiException", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun changeToReg() {
        fragmentListener.nextFragment(this, RegFragment())
    }
}