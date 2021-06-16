package com.derlados.computerconf.VIews

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.derlados.computerconf.R

class SaveDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.DarkAlert)
        builder
                .setTitle("Сохранить изменения ?")
                .setPositiveButton("Да") { dialogInterface, i -> targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, activity!!.intent) }
                .setNegativeButton("Нет") { dialogInterface, i -> targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, activity!!.intent) }
        return builder.create()
    }
}