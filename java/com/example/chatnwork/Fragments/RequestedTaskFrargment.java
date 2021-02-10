package com.example.chatnwork.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chatnwork.Adapters.TaskAdapter;
import com.example.chatnwork.Model.Task;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;

import java.util.ArrayList;
import java.util.List;


public class RequestedTaskFrargment extends Fragment {
    private UserAccount current;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> requestedtasks;

    public RequestedTaskFrargment() {
        // Required empty public constructor
    }

    public RequestedTaskFrargment( UserAccount current) {
        this.current = current;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_requested_task, container, false);
        recyclerView= v.findViewById(R.id.requestedrecycler);
        try{
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }catch (Exception e){
            Log.d("oppai",e.getMessage());
        }

        recyclerView.setHasFixedSize(true);

        requestedtasks= new ArrayList<>();

        getRequestedTask();
        return v;
    }

    private void getRequestedTask() {
        DatabaseReference dref= FirebaseDatabase.getInstance().getReference("Tasks");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestedtasks.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Task task=snap.getValue(Task.class);
                    assert task != null;
                    task.setKey(snap.getKey());
                    if(task.getRequester().getEmail().equals(current.getEmail())){
                        requestedtasks.add(task);
                    }
                }
                adapter= new TaskAdapter(getContext(),requestedtasks);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}