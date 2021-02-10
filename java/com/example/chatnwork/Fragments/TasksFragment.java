package com.example.chatnwork.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.chatnwork.Adapters.TaskViewPagerAdapter;
import com.example.chatnwork.LoginActivity;
import com.example.chatnwork.MainActivity;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class TasksFragment extends Fragment {

    
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TaskViewPagerAdapter viewPagerAdapter;

    private UserAccount current;
    public TasksFragment() {

    }
    public TasksFragment(UserAccount current){
        this.current= current;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_tasks, container, false);
        
        tabLayout= v.findViewById(R.id.tasktablayout);
        viewPager= v.findViewById(R.id.taskviewpager);
        addFragments();

        return v;
    }

    private void addFragments() {
        viewPagerAdapter= new TaskViewPagerAdapter(getChildFragmentManager());
        Log.d("oppai","h1");

        viewPagerAdapter.addFragment(new OpenTaskFragment(current),"Open Task");
        Log.d("oppai","h2");

        viewPagerAdapter.addFragment(new CompletedFragmentTask(current),"Completed Task");
        viewPagerAdapter.addFragment(new RequestedTaskFrargment(current),"Requested Task");
        Log.d("oppai","h3");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("oppai","h4");

    }
}