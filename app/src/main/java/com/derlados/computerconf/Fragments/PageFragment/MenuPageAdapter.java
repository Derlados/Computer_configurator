package com.derlados.computerconf.Fragments.PageFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MenuPageAdapter extends FragmentPagerAdapter {

    final int COUNT_PAGES_MENU = 3;

    public MenuPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstanse(position);
    }

    @Override
    public int getCount() {
        return COUNT_PAGES_MENU;
    }
}
