package com.derlados.computerconf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.derlados.computerconf.PageFragment.PageFragment;

public class ShopFragment extends PageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_shop, container, false);
        return fragment;
    }
}
