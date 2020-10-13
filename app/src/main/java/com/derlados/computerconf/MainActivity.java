package com.derlados.computerconf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;


import com.derlados.computerconf.PageFragment.MenuPageAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager=(ViewPager)findViewById(R.id.activity_main_pager);
        pager.setAdapter(new MenuPageAdapter(getSupportFragmentManager()));

    }
}