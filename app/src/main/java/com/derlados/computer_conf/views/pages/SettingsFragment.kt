package com.derlados.computer_conf.views.pages

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Bundle
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.derlados.computer_conf.App
import com.derlados.computer_conf.MainActivity
import com.derlados.computer_conf.R
import com.derlados.computer_conf.consts.BackStackTag
import com.derlados.computer_conf.presenters.SettingsPresenter
import com.derlados.computer_conf.view_interfaces.MainView
import com.derlados.computer_conf.view_interfaces.SettingsView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.io.File


class SettingsFragment: Fragment(), SettingsView, MainActivity.OnBackPressedListener {
    private lateinit var fragmentListener: OnFragmentInteractionListener
    private lateinit var mainView: MainView
    private lateinit var currentFragment: View

    private lateinit var originalEtDrawable: Drawable
    private lateinit var originalEtKeyListener: KeyListener

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var cropLauncher: ActivityResultLauncher<Intent>

    private lateinit var presenter: SettingsPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as OnFragmentInteractionListener
        mainView = context as MainView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        currentFragment = inflater.inflate(R.layout.fragment_settings, container, false)

        // Сохранение keyListener и drawable  для их возобнавления
        originalEtKeyListener = currentFragment.fragment_settings_et_username.keyListener
        originalEtDrawable = currentFragment.fragment_settings_et_username.background
        disableEditable(currentFragment.fragment_settings_et_username) // Отключение всех возможностей EditText

        // Инициализация обработчиков нажатий, активити результатов для работы с загрузкой изображения
        currentFragment.fragment_settings_img.setOnClickListener { uploadImage() }
        currentFragment.fragment_settings_tv_add_img.setOnClickListener { uploadImage() }
        imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::imageUpload)
        cropLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::cropResult)

        // Обработчики нажатий на кнопки редактирования имени пользователя
        currentFragment.fragment_settings_img_reset_username.setOnClickListener { closeUsernameEdit() }
        currentFragment.fragment_settings_img_accept_username.setOnClickListener { closeUsernameEdit(); acceptUsernameEdit(); }
        currentFragment.fragment_settings_img_edit_username.setOnClickListener { openUsernameEdit() }

        // Инициализация GoogleSignIn
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::signInGoogle)
        currentFragment.fragment_settings_ll_google_add.setOnClickListener { mainView.googleSign.openGoogleSignIn(resultLauncher) }

        presenter = SettingsPresenter(this, App.app.resourceProvider)
        presenter.init()

        return currentFragment
    }

    override fun updateUserData(username: String, photoUrl: String?, email: String?) {
        currentFragment.fragment_settings_et_username.setText(username)
        photoUrl?.let {
            Picasso.get().load(photoUrl).into(currentFragment.fragment_settings_img)
        }
        email?.let {
            currentFragment.fragment_settings_ll_google_acc.visibility = View.VISIBLE
            currentFragment.fragment_settings_ll_google_add.visibility = View.GONE
            currentFragment.fragment_settings_tv_used_google_acc.text = email
        }
    }

    /**
     * Отключение всего функционала, а так же стилистических особенностей EditText
     * @param editText - элемент EditText
     */
    private fun disableEditable(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null

        if (editText.background is InsetDrawable) {
            val insetDrawable = editText.background as InsetDrawable
            val originalDrawable = insetDrawable.drawable!!
            editText.background = InsetDrawable(originalDrawable, 0, 0, 0, 0)
            editText.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Возвращение всего функционала EditText
     * @param editText - элемент EditText
     */
    private fun openEditable(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.isFocusable = true
        editText.isEnabled = true
        editText.isCursorVisible = true
        editText.keyListener = originalEtKeyListener
        editText.background = originalEtDrawable
    }

    /**
     * Вкл режима редактирования имени пользователя. Показ всех кнопко и восстановление EditText
     */
    private fun openUsernameEdit() {
        currentFragment.fragment_settings_img_accept_username.visibility = View.VISIBLE
        currentFragment.fragment_settings_img_reset_username.visibility = View.VISIBLE
        currentFragment.fragment_settings_img_edit_username.visibility = View.GONE

        openEditable(currentFragment.fragment_settings_et_username)
    }

    /**
     * Выкл режима редактирования имени пользователя
     */
    private fun closeUsernameEdit() {
        currentFragment.fragment_settings_img_accept_username.visibility = View.GONE
        currentFragment.fragment_settings_img_reset_username.visibility = View.GONE
        currentFragment.fragment_settings_img_edit_username.visibility = View.VISIBLE

        disableEditable(currentFragment.fragment_settings_et_username)
    }

    private fun signInGoogle(result: ActivityResult) {
        if (result.resultCode != 0 && result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = mainView.googleSign.getAccount(task)

            val id = account.id
            val email = account.email
            if (id != null && email != null) {
                presenter.addGoogleAcc(id, email, account.photoUrl?.toString())
            }
        }
    }

    override fun signOutGoogle() {
        mainView.googleSign.signOut()
    }

    private fun acceptUsernameEdit() {
        val newUsername = currentFragment.fragment_settings_et_username.text.toString()
        presenter.updateUsername(newUsername)
    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imageLauncher.launch(intent)
    }

    /**
     * Результат после загрузки изображения
     */
    private fun imageUpload(result: ActivityResult) {
        val uri = result.data?.data ?: return
        val mime = MimeTypeMap.getSingleton()
        val extension = mime.getExtensionFromMimeType(requireContext().contentResolver.getType(uri))

        uri.path?.let {
            val intent = UCrop.of(uri, Uri.fromFile(File(requireContext().cacheDir, "IMG_" + System.currentTimeMillis() + ".${extension}")))
                    .withAspectRatio(1F, 1f)
                    .getIntent(requireContext())
            cropLauncher.launch(intent)
        }
    }

    /**
     * Результат после обрезки изображения. Образанное изображение отображается в превью и
     * преображается в файл
     */
    private fun cropResult(result: ActivityResult) {
        val resultUri = result.data?.let { UCrop.getOutput(it) }
        resultUri?.path?.let {
            val file = File(it)
            currentFragment.fragment_settings_img.setImageURI(resultUri)
            presenter.uploadImage(file)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    override fun showImgLoadProgress() {
        currentFragment.fragment_settings_pb_img.visibility = View.VISIBLE
        currentFragment.fragment_settings_img.visibility = View.GONE
        currentFragment.fragment_settings_tv_add_img.visibility = View.GONE
    }

    override fun closeImgLoadProgress() {
        currentFragment.fragment_settings_pb_img.visibility = View.GONE
        currentFragment.fragment_settings_img.visibility = View.VISIBLE
        currentFragment.fragment_settings_tv_add_img.visibility = View.VISIBLE
    }

    override fun showUsernamePB() {
        currentFragment.fragment_settings_img_edit_username.visibility = View.GONE
        currentFragment.fragment_settings_pb_img_edit_username.visibility = View.VISIBLE
    }

    override fun closeUsernamePB() {
        currentFragment.fragment_settings_img_edit_username.visibility = View.VISIBLE
        currentFragment.fragment_settings_pb_img_edit_username.visibility = View.GONE
    }

    override fun onBackPressed(): Boolean {
        fragmentListener.popBackStack(BackStackTag.MAIN)
        return false
    }
}