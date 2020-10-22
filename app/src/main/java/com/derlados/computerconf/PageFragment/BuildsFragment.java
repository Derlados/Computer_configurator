package com.derlados.computerconf.PageFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;
import com.derlados.computerconf.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BuildsFragment extends PageFragment implements View.OnClickListener {

    OnFragmentInteractionListener frListener;
    View fragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        frListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_builds, container, false);

        FloatingActionButton addBuildBt = fragment.findViewById(R.id.fragment_builds_float_bt);
        addBuildBt.setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view) {

    }
}
