package com.example.chatnwork.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TaskViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> FragmentList= new ArrayList<>();
    private final List<String> FragmentTitleList= new ArrayList<>();

    public TaskViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentList.size();
    }

    public void addFragment(Fragment frag,String title){
        FragmentList.add(frag);
        FragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        return FragmentTitleList.get(position);
    }
}
