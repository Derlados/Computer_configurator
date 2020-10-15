package com.derlados.computerconf.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.derlados.computerconf.R;

public class MainMenuFragment extends Fragment {

    OnFragmentInteractionListener fragmentListener;

    @Override
    public void onAttach(@NonNull  Context context)
    {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentListener.onActivityInteraction(this, OnFragmentInteractionListener.Action.SET_PAGER);
    }


}
