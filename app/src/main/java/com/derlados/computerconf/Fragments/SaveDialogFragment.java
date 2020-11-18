package com.derlados.computerconf.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.R;

public class SaveDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DarkAlert);

        builder
                .setTitle("Сохранить изменения ?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                });


        return  builder.create();
    }
}
