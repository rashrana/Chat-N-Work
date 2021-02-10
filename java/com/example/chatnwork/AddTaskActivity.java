package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.example.chatnwork.Adapters.AsigneeListAdapter;
import com.example.chatnwork.Model.Task;
import com.example.chatnwork.Model.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity implements AsigneeListAdapter.OnItemClickListener {
    private Toolbar toolbar;
    private EditText tasktitle;
    private TextView due, people;
    private Button save,okay;
    private DatePickerDialog datePicker;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private List<UserAccount> friends,assignees;
    private DatabaseReference databaseReference,taskreference;
    private AsigneeListAdapter adapter;
    private UserAccount current;
    private ValueEventListener valueEventListener;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        try{

            friends= new ArrayList<>();
            assignees= new ArrayList<>();

            tasktitle= findViewById(R.id.newtaskt);
            due= findViewById(R.id.newtdue);
            people= findViewById(R.id.newtassignee);
            save= findViewById(R.id.newtasksave);
            toolbar = findViewById(R.id.addtaskToolBar);
            setSupportActionBar(toolbar);

            TextView goback=toolbar.findViewById(R.id.addtgoback);
            goback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assignees.clear();
                    finish();
                }
            });


            getCurrentUser();
            try{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFriendsList();
                    }
                },4000);

            }catch (Exception e){
                Log.d("oppai",e.getMessage()+"100");
            }

            due.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr= Calendar.getInstance();
                    int day= cldr.get(Calendar.DAY_OF_MONTH);
                    int month= cldr.get(Calendar.MONTH);
                    int year= cldr.get(Calendar.YEAR);

                    try{
                        datePicker= new DatePickerDialog(AddTaskActivity.this,AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                due.setText(dayOfMonth+"/"+month+"/"+year);
                            }
                        },year,month,day);
                        datePicker.getDatePicker().setMinDate(System.currentTimeMillis()-120);
                        datePicker.show();
                    }catch (Exception e){
                        Log.d("now",e.getMessage());
                    }

                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskreference= FirebaseDatabase.getInstance().getReference("Tasks");
                    String title= tasktitle.getText().toString();
                    String duedate= due.getText().toString();
                    if(validate(title,duedate)) {
                        //Log.d("oppai",assignees.get(0).getUsername());
                        Task task= new Task(title,duedate,"open",current,assignees);

                        taskreference.push().setValue(task);
                        Toast.makeText(getApplicationContext(),"Task Added Successfully!",Toast.LENGTH_SHORT).show();
                        assignees.clear();

                        finish();
                        getCallingActivity();

                    }


                }
            });
            dialog = new Dialog(AddTaskActivity.this);
            dialog.setContentView(R.layout.asignee_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);

            recyclerView= dialog.findViewById(R.id.assigneelistrecyler);
            recyclerView.setLayoutManager(new LinearLayoutManager(AddTaskActivity.this));
            okay=dialog.findViewById(R.id.okay);
            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            people.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        dialog.show();
                    }catch (Exception e){
                        Log.d("oppai",e.getMessage()+"333");
                    }

                }
            });
        }catch (Exception e){
            Log.d("oppai",e.getMessage());
            e.printStackTrace();
        }



    }

    private boolean validate(String task,String dued) {
        boolean flag=true;
        if(TextUtils.isEmpty(task)){
            flag=false;
            tasktitle.setError("is Required!");
        }if(TextUtils.isEmpty(dued)){
            flag=false;
            due.setError("Set Date");
        }
        if(assignees.size()==0){
            flag=false;
            Toast.makeText(getApplicationContext(),"Please Select Assignee first!",Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    private void getCurrentUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                    user.setKey(selected.getKey());
                    if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                       current= user;
                       break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getFriendsList() {
        databaseReference = FirebaseDatabase.getInstance().getReference(current.getUsername()).child("Friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot selected : snapshot.getChildren()) {
                    UserAccount user = selected.getValue(UserAccount.class);
                    friends.add(user);
                }
                friends.add(current);
                adapter= new AsigneeListAdapter(dialog.getContext(),friends);
                adapter.setOnItemClickListener(AddTaskActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai", error.getMessage() + "activityaddtask");
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        assignees.add(friends.get(position));
    }

    @Override
    public void onItemRemove(int position) {
        assignees.remove(friends.get(position));
    }

}