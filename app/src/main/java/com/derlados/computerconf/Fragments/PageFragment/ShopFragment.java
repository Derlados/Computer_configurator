package com.derlados.computerconf.Fragments.PageFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.Fragments.OnFragmentInteractionListener;
import com.derlados.computerconf.Fragments.ShopSearchFragment;
import com.derlados.computerconf.R;

public class ShopFragment extends PageFragment implements View.OnClickListener {

    OnFragmentInteractionListener frListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        frListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_shop, container, false);

        fragment.findViewById(R.id.fragment_shop_cpu).setOnClickListener(this);
        fragment.findViewById(R.id.fragment_shop_gpu).setOnClickListener(this);
        fragment.findViewById(R.id.fragment_shop_motherboard).setOnClickListener(this);
        fragment.findViewById(R.id.fragment_shop_hdd).setOnClickListener(this);
        fragment.findViewById(R.id.fragment_shop_ssd).setOnClickListener(this);
        fragment.findViewById(R.id.fragment_shop_ram).setOnClickListener(this);
        fragment.findViewById(R.id.fragment_shop_power_supply).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view) {
        Bundle data = new Bundle(); // Данные которые будут переданы другому фрагменту

        // Определение типа товара который хочет найти пользователь
        switch (view.getId())
        {
            case R.id.fragment_shop_cpu:
                data.putString("typeGood", TypeGood.CPU.toString());
                break;
            case R.id.fragment_shop_gpu:
                data.putString("typeGood", TypeGood.GPU.toString());
                break;
            case R.id.fragment_shop_motherboard:
                data.putString("typeGood", TypeGood.MOTHERBOARD.toString());
                break;
            case R.id.fragment_shop_hdd:
                data.putString("typeGood", TypeGood.HDD.toString());
                break;
            case R.id.fragment_shop_ssd:
                data.putString("typeGood", TypeGood.SSD.toString());
                break;
            case R.id.fragment_shop_ram:
                data.putString("typeGood", TypeGood.RAM.toString());
                break;
            case R.id.fragment_shop_power_supply:
                data.putString("typeGood", TypeGood.POWER_SUPPLY.toString());
                break;
            default:
                return;
        }

        frListener.onFragmentInteraction(this, new ShopSearchFragment(), OnFragmentInteractionListener.Action.NEXT_FRAGMENT_HIDE, data);
    }
}
