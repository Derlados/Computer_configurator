package com.derlados.computer_conf.views.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSign(context: Context?, private val resultLauncher: ActivityResultLauncher<Intent>) {
    private val signInIntent: Intent;

    init {
        // Инициализация клиента
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("768086134086-kbt53st09v1402m4kku2ghi5seeohto3.apps.googleusercontent.com")
                .requestEmail()
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

        // Запуска активити с выбором гугл аккаунта
        signInIntent = mGoogleSignInClient.signInIntent
    }

    /**
     * Открытие меню для выбора гугл аккаунта и авторизации
     */
    fun openGoogleSignIn() {
        resultLauncher.launch(signInIntent)
    }

    fun getAccount(completedTask: Task<GoogleSignInAccount>): GoogleSignInAccount {
        try {
            return completedTask.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleApiException", "signInResult:failed code=" + e.statusCode)
            throw e
        }
    }
}