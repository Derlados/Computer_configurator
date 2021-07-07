package com.derlados.computer_conf.views.dialog_fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.derlados.computer_conf.R
import com.derlados.computer_conf.views.BuildConstructorFragment

class SaveDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.DarkAlert)
        builder
                .setTitle("Сохранить изменения ?")
                .setPositiveButton("Да") { _, _ -> parentFragmentManager.setFragmentResult(
                    BuildConstructorFragment.SAVE_BUILD, Bundle())}
                .setNegativeButton("Нет") { _, _ -> parentFragmentManager.setFragmentResult(
                    BuildConstructorFragment.WITHOUT_SAVE, Bundle())}
        return builder.create()
    }
}