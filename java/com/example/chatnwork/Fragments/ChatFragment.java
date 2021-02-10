package com.example.chatnwork.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chatnwork.Adapters.TaskViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.chatnwork.Adapters.ChatlistAdapter;
import com.example.chatnwork.Adapters.UserAdapter;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


public class ChatFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TaskViewPagerAdapter viewPagerAdapter;

    private UserAccount current;

    public ChatFragment() {

    }

    public ChatFragment(UserAccount current) {
        this.current = current;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat_frame, container, false);

        tabLayout = v.findViewById(R.id.chattablayout);
        viewPager = v.findViewById(R.id.chatviewpager);

        addFragments();


        return v;
    }

    private void addFragments() {
        viewPagerAdapter = new TaskViewPagerAdapter(getChildFragmentManager());
        Log.d("oppai", "h1");


        viewPagerAdapter.addFragment(new ChatFriendsFragment(current), "Friends Chat");


        try {
            viewPagerAdapter.addFragment(new GroupFragment(current), "Group Chat");
        } catch (Exception e) {
            Log.d("oppai", e.getMessage());
        }
        Log.d("oppai", "h3");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("oppai", "h4");

    }
}