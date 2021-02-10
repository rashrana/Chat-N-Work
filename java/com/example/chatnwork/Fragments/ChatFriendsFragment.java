package com.example.chatnwork.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatnwork.Adapters.ChatlistAdapter;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFriendsFragment extends Fragment {
    private EditText search;
    private RecyclerView recyclerView;
    private ChatlistAdapter adapter;
    private ChatlistAdapter searchadapter;
    private List<UserAccount> users, searchUsers;
    private List<Chat> searchList;
    private FirebaseUser fuser;
    private DatabaseReference dref;
    private List<String> userlist;
    private List<Chat> lastmsglist;
    private UserAccount current;

    public ChatFriendsFragment() {
    }

    public ChatFriendsFragment(UserAccount current) {
        this.current = current;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        try {
            recyclerView = v.findViewById(R.id.chatrecyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);

            fuser = FirebaseAuth.getInstance().getCurrentUser();

            userlist = new ArrayList<>();
            lastmsglist = new ArrayList<>();
            searchUsers = new ArrayList<>();
            searchList = new ArrayList<>();

            dref = FirebaseDatabase.getInstance().getReference("Chats");
            dref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userlist.clear();
                    if (search.getText().toString().equals("")) {
                        for (DataSnapshot cur : snapshot.getChildren()) {
                            Chat chat = cur.getValue(Chat.class);
                            if (chat.getSender().equals(fuser.getEmail())) {
                                userlist.add(chat.getReceiver());
                            }
                            if (chat.getReceiver().equals(fuser.getEmail())) {
                                userlist.add(chat.getSender());
                            }
                            //adding last messeges of all participants
                            if (chat.getSender().equals(fuser.getEmail()) || chat.getReceiver().equals(fuser.getEmail())) {
                                if (lastmsglist.size() != 0) {
                                    boolean found = false;
                                    for (Chat ch : lastmsglist) {
                                        if ((ch.getSender().equals(chat.getSender()) && ch.getReceiver().equals(chat.getReceiver())) ||
                                                (ch.getSender().equals(chat.getReceiver()) && ch.getReceiver().equals(chat.getSender()))) {
                                            lastmsglist.remove(ch);
                                            lastmsglist.add(chat);
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        lastmsglist.add(chat);
                                    }
                                } else {
                                    lastmsglist.add(chat);
                                }
                            }

                        }
                    }
                    Collections.reverse(lastmsglist);
                    List<String> temp = new ArrayList<>();
                    for (String acc : userlist) {
                        if (!temp.contains(acc)) {
                            temp.add(acc);
                        }
                    }
                    userlist.clear();
                    userlist.addAll(temp);

                    readChats();
                    try{
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }

                    }
                    catch (Exception e){
                        Log.d("oppai",e.getMessage()+"0");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            search = v.findViewById(R.id.chatsearch);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().trim().equals("")) {

                        searchUsers(s.toString().toLowerCase());
                    } else {
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {
            Log.d("oppai", e.getMessage());
            e.printStackTrace();
        }
        return v;
    }

    private void searchUsers(String toString) {
        // toString= toString.toLowerCase();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(toString).endAt(toString + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchUsers.clear();
                searchList.clear();
                if (snapshot.getChildrenCount() > 0) {
                    try {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            UserAccount user = snap.getValue(UserAccount.class);
                            if (user != null && current != null) {
                                if (!user.getEmail().equals(current.getEmail())) {
                                    searchUsers.add(user);
                                }
                            }

                        }
                    } catch (Exception e) {
                        Log.d("oppai", e.getMessage() + " : " + toString);
                    }

                }
                if (searchUsers.size() > 0) {
                    for (UserAccount user : searchUsers) {
                        boolean flag = false;
                        for (Chat msg : lastmsglist) {
                            if (msg.getSender().equals(user.getEmail()) || msg.getReceiver().equals(user.getEmail())) {
                                searchList.add(msg);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            searchUsers.remove(user);
                        }
                    }
                }

                Log.d("oppai", searchUsers.size() + " : " + searchList.size());
                searchadapter = new ChatlistAdapter(getContext(), searchUsers, searchList);
                recyclerView.setAdapter(searchadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {

        users = new ArrayList<>();
        dref = FirebaseDatabase.getInstance().getReference("Users");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                if (search.getText().toString().trim().equals("")) {
                    for (DataSnapshot cur : snapshot.getChildren()) {
                        UserAccount user = cur.getValue(UserAccount.class);
                        if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            current = user;
                        }
                        for (String id : userlist) {
                            if (user.getEmail().equals(id)) {
                                if (users.size() != 0) {
                                    boolean flag = false;
                                    for (UserAccount u : users) {
                                        if (u.getEmail().equals(id)) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (!flag) {
                                        users.add(user);
                                    }
                                } else {
                                    users.add(user);
                                }
                            }

                        }
                    }
                }
                adapter = new ChatlistAdapter(getContext(), users, lastmsglist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
