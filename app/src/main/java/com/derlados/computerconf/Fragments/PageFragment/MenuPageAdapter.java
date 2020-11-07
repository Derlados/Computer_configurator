package com.derlados.computerconf.Fragments.PageFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MenuPageAdapter extends FragmentPagerAdapter {

    final int COUNT_PAGES_MENU = 3;
    Fragment[] pageFragments = new Fragment[COUNT_PAGES_MENU];

    public MenuPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        pageFragments[position] = PageFragment.newInstanse(position);
        return pageFragments[position];
    }

    @Override
    public int getCount() {
        return COUNT_PAGES_MENU;
    }

    public Fragment getPageFragment(int index) {
        return pageFragments[index];
    }
}
