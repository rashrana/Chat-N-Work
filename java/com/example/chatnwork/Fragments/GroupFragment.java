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
import com.example.chatnwork.Adapters.GroupChatlistAdapter;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.Group;
import com.example.chatnwork.Model.GroupChat;
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

public class GroupFragment extends Fragment {
    private EditText search;
    private RecyclerView recyclerView;
    private GroupChatlistAdapter adapter,searchadapter;
    private List<Group> searchGroups;
    private List<GroupChat> searchList;
    private FirebaseUser fuser;
    private DatabaseReference dref;
    private List<Group> grouplist;
    private List<GroupChat> lastmsglist;
    private UserAccount current;

    public GroupFragment() {
    }

    public GroupFragment(UserAccount current) {
        this.current = current;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group, container, false);
        try {

            recyclerView = v.findViewById(R.id.chatrecyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            fuser = FirebaseAuth.getInstance().getCurrentUser();

            grouplist = new ArrayList<>();
            lastmsglist = new ArrayList<>();
            searchGroups = new ArrayList<>();
            searchList = new ArrayList<>();

            dref = FirebaseDatabase.getInstance().getReference("Groups");
            dref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    grouplist.clear();
                    if (search.getText().toString().equals("")) {
                        for (DataSnapshot cur : snapshot.getChildren()) {
                            Group gp = cur.getValue(Group.class);
                            for(UserAccount u: gp.getMembers()){
                                if(u.getEmail().equals(fuser.getEmail())){
                                    grouplist.add(gp);
                                    break;
                                }
                            }
                        }
                    }
//                    Collections.reverse(lastmsglist);
//                    List<String> temp = new ArrayList<>();
//                    for (String acc : userlist) {
//                        if (!temp.contains(acc)) {
//                            temp.add(acc);
//                        }
//                    }
//                    userlist.clear();
//                    userlist.addAll(temp);
                    readChats();
                    try{
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }

                    }
                    catch (Exception e){
                        Log.d("oppai",e.getMessage());
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
        Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("search").startAt(toString).endAt(toString + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchGroups.clear();
                searchList.clear();
                if (snapshot.getChildrenCount() > 0) {
                    try {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Group gp = snap.getValue(Group.class);
                            if (gp != null && current != null) {
                               for(UserAccount user:gp.getMembers()){
                                   if(user.getEmail().equals(current.getEmail())){
                                       searchGroups.add(gp);
                                       break;
                                   }
                               }
                            }

                        }
                    } catch (Exception e) {
                        Log.d("oppai", e.getMessage() + " : " + toString);
                    }

                }
//                if (searchUsers.size() > 0) {
//                    for (UserAccount user : searchUsers) {
//                        boolean flag = false;
//                        for (Chat msg : lastmsglist) {
//                            if (msg.getSender().equals(user.getEmail()) || msg.getReceiver().equals(user.getEmail())) {
//                                searchList.add(msg);
//                                flag = true;
//                                break;
//                            }
//                        }
//                        if (!flag) {
//                            searchUsers.remove(user);
//                        }
//                    }
//                }

                searchadapter = new GroupChatlistAdapter(getContext(), searchGroups, lastmsglist);
                recyclerView.setAdapter(searchadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        dref = FirebaseDatabase.getInstance().getReference("GroupChats");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lastmsglist.clear();
                if (search.getText().toString().trim().equals("")) {
                    try{
                        for (DataSnapshot cur : snapshot.getChildren()) {
                            GroupChat gp = cur.getValue(GroupChat.class);
                            for(Group g: grouplist){
                                try{
                                    if(g.getGroupname().equals(gp.getGroupname())){
                                        if(lastmsglist.size()!=0){
                                            boolean flag= false;
                                            for(GroupChat chat:lastmsglist){
                                                if(gp.getGroupname().equals(chat.getGroupname())){
                                                    lastmsglist.remove(chat);
                                                    lastmsglist.add(gp);
                                                    flag=true;
                                                    break;
                                                }
                                            }
                                            if(!flag) lastmsglist.add(gp);
                                        }
                                        else{
                                            lastmsglist.add(gp);
                                        }
                                    }
                                }catch (Exception e){
                                    Log.d("hohoho",e.getMessage());
                                }

                            }
                        }
                    }catch (Exception e){
                        Log.d("hohoho",e.getMessage());                    }

                }
                Collections.reverse(lastmsglist);
                Log.d("hohoho",grouplist.size()+" "+lastmsglist.size());
                adapter = new GroupChatlistAdapter(getContext(), grouplist, lastmsglist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

