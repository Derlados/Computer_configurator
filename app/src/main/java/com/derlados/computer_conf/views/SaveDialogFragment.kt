package com.derlados.computer_conf.views

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.derlados.computer_conf.R

class SaveDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.DarkAlert)
        builder
                .setTitle("Сохранить изменения ?")
                .setPositiveButton("Да") { _, _ -> targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, activity?.intent) }
                .setNegativeButton("Нет") { _, _ -> targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, activity?.intent) }
        //parentFragmentManager.setFragmentResult()

        return builder.create()
    }
}