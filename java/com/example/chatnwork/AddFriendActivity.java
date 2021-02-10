package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.chatnwork.Adapters.AddFriendAdapter;
import com.example.chatnwork.Adapters.ChatlistAdapter;
import com.example.chatnwork.Adapters.UserAdapter;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText search;
    private RecyclerView recyclerView;
    private AddFriendAdapter adapter, searchadapter;
    private List<UserAccount> users, searchUsers;
    private List<UserAccount> friends, incoming, sent;
    private UserAccount current;
    private DatabaseReference databaseReference;
    private String curUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        curUsername = getIntent().getStringExtra("curUsername");
        toolbar = findViewById(R.id.addfToolBar);
        setSupportActionBar(toolbar);

        TextView goback=toolbar.findViewById(R.id.addfgoback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        users = new ArrayList<>();
        friends = new ArrayList<>();
        incoming = new ArrayList<>();
        sent = new ArrayList<>();
        searchUsers = new ArrayList<>();

        search = findViewById(R.id.addfsearch);
        recyclerView = findViewById(R.id.addfrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        search = findViewById(R.id.addfsearch);

        readUsers();
        getFriendsList();
        getIncomingList();
        getSentList();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().equals("")) {
                    searchUsers(s.toString());
                } else {
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchUsers(String toString) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(toString).endAt(toString + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchUsers.clear();
                if (snapshot.getChildrenCount()>0) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        UserAccount user = snap.getValue(UserAccount.class);
                        assert user != null;
                        try {
                            if (!user.getEmail().equals(current.getEmail())) {
                                searchUsers.add(user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                searchadapter = new AddFriendAdapter(getApplicationContext(), searchUsers, friends, sent, incoming, current, AddFriendActivity.this);
                recyclerView.setAdapter(searchadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSentList() {
        databaseReference = FirebaseDatabase.getInstance().getReference(curUsername).child("Request Sent");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sent.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                    sent.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai", error.getMessage() + "activity4");
            }
        });
    }

    private void getIncomingList() {
        databaseReference = FirebaseDatabase.getInstance().getReference(curUsername).child("Incoming Request");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incoming.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                    incoming.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai", error.getMessage() + "activity3");
            }
        });
    }

    private void getFriendsList() {
        databaseReference = FirebaseDatabase.getInstance().getReference(curUsername).child("Friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                    friends.add(user);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai", error.getMessage() + "activity2");
            }
        });

    }

    private void readUsers() {
        Log.d("oval", "1");
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                    user.setKey(selected.getKey());
                    if (!user.getEmail().equals(currentuser)) {
                        users.add(user);
                    } else {
                        current = user;
                    }
                }
                adapter = new AddFriendAdapter(getApplicationContext(), users, friends, sent, incoming, current, AddFriendActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai", error.getMessage() + "activity1");
            }
        });
    }
}