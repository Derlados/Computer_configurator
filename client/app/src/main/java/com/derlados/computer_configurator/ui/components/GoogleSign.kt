package com.derlados.computer_configurator.ui.components

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.content.Context
import com.derlados.computer_configurator.R

class GoogleSign(context: Context) {
    private val signInIntent: Intent;
    private val mGoogleSignInClient: GoogleSignInClient

    init {
        // Инициализация клиента
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.server_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

        // Интент для запуска активити с выбором гугл аккаунта
        signInIntent = mGoogleSignInClient.signInIntent
    }

    /**
     * Открытие меню для выбора гугл аккаунта и авторизации
     */
    fun openGoogleSignIn(resultLauncher: ActivityResultLauncher<Intent>) {
        resultLauncher.launch(signInIntent)
    }

    fun signOut() {
        mGoogleSignInClient.signOut()
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