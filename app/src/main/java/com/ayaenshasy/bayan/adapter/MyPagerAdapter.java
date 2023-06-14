package com.ayaenshasy.bayan.adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] fragments;
    private final String[] fragmentTitles;

    public MyPagerAdapter(@NonNull FragmentManager fm, Fragment[] fragments, String[] fragmentTitles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
        this.fragmentTitles = fragmentTitles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles[position];
    }
}
