package com.example.chatnwork.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chatnwork.Adapters.UserAdapter;
import com.example.chatnwork.AddFriendActivity;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserAccount> contacts;
    private String curUsername;

    public ContactsFragment() {
    }
    public ContactsFragment(String curUsername){
        this.curUsername= curUsername;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView=v.findViewById(R.id.contactsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        contacts= new ArrayList<>();
        friendnames();
        return v;
    }



    private void friendnames() {
        String currentuser= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference databaseRef= FirebaseDatabase.getInstance().getReference(curUsername).child("Friends");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contacts.clear();
                for(DataSnapshot selected: snapshot.getChildren()){
                    UserAccount user = selected.getValue(UserAccount.class);
                    if(!user.getEmail().equals(currentuser)){
                        contacts.add(user);
                    }

                }
                userAdapter= new UserAdapter(getContext(),contacts);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}