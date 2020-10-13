package com.derlados.computerconf.PageFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.derlados.computerconf.R;

public class BuildsFragment extends PageFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_builds, container, false);
        return fragment;
    }
}
