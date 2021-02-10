package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatnwork.Adapters.AsigneeListAdapter;
import com.example.chatnwork.Fragments.ChatFriendsFragment;
import com.example.chatnwork.Model.Group;
import com.example.chatnwork.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.chatnwork.Adapters.UserAdapter;
import com.example.chatnwork.Fragments.AccountFragment;
import com.example.chatnwork.Fragments.ChatFragment;
import com.example.chatnwork.Fragments.ContactsFragment;
import com.example.chatnwork.Fragments.TasksFragment;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.Notification.Token;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AsigneeListAdapter.OnItemClickListener{
    //android:background="?android:attr/windowBackground"
    private final int result_image= 1000;
    private Toolbar chatToolbar, taskToolbar, contactToolbar, profileToolbar;
    private BottomNavigationView bottomNavigationView;
    private ImageView cimage, timage, coimage, grpimage;

    private String email, curprofileurl, curUsername, currentUserKey;
    private UserAccount current;
    private Button searchnew, logout, addchat, addtask;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference,grpStorageReference;

    private Dialog dialog;
    private RecyclerView recyclerView;
    private EditText grpname;
    private Button okay,cancel;
    private List<UserAccount> friends,members;
    private AsigneeListAdapter adapter;
    private Uri grpimageurl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        try{

            firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null) {
                email = firebaseAuth.getCurrentUser().getEmail();
//                try{
//                    String topic= email.replace("@","");
//                    FirebaseMessaging.getInstance().subscribeToTopic("nishan")
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(!task.isSuccessful()){
//                                        Log.d("now","Failed");
//                                    }
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("now",e.getMessage()+" : ");
//                                }
//                            });
//                }catch (Exception e){
//                    Log.d("now",e.getMessage());
//                }
            }
            friends= new ArrayList<>();
            members= new ArrayList<>();

            //Receive Firebase notification
            String senderkey= email.replace("@","").replace(".","");
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+senderkey);



            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            storageReference = FirebaseStorage.getInstance().getReference("ProfilePic");
            storageReference= FirebaseStorage.getInstance().getReference("GroupPic");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot selected : snapshot.getChildren()) {
                        UserAccount user = selected.getValue(UserAccount.class);
                        user.setKey(selected.getKey());


                        if (user.getEmail().equals(email)) {
                            current = user;

                            current.setKey(selected.getKey());
                            curUsername = user.getUsername();
                            curprofileurl = user.getImageurl();
                            Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(cimage);
                            Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(timage);
                            Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(coimage);

                            status("true");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment(current)).commit();
                                    getFriendsList();
                                    createdialog();
                                }
                            },2000);

                            break;
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            chatToolbar = findViewById(R.id.chatToolBar);
            taskToolbar = findViewById(R.id.taskToolBar);
            contactToolbar = findViewById(R.id.contactsToolBar);
            profileToolbar = findViewById(R.id.profileToolBar);
            setSupportActionBar(chatToolbar);
            taskToolbar.setVisibility(View.INVISIBLE);
            contactToolbar.setVisibility(View.INVISIBLE);
            profileToolbar.setVisibility(View.INVISIBLE);

            cimage = chatToolbar.findViewById(R.id.cimage);
            timage = taskToolbar.findViewById(R.id.timage);
            coimage = contactToolbar.findViewById(R.id.coimage);

            searchnew = contactToolbar.findViewById(R.id.search_newpeople);
            logout = profileToolbar.findViewById(R.id.logout);
            addtask = taskToolbar.findViewById(R.id.addtask);
            addchat = chatToolbar.findViewById(R.id.addchat);
            searchnew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddFriendActivity.class);
                    i.putExtra("curUsername", curUsername);
                    startActivity(i);
                }
            });


            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        status("false");
                        firebaseAuth.signOut();
                    }catch (Exception e){
                        Log.d("oppai",e.getMessage());
                    }

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });

            bottomNavigationView = findViewById(R.id.bottomnavbar);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected_frag = null;
                    switch (item.getItemId()) {
                        case R.id.chat:
                            chatToolbar.setVisibility(View.VISIBLE);
                            taskToolbar.setVisibility(View.INVISIBLE);
                            contactToolbar.setVisibility(View.INVISIBLE);
                            profileToolbar.setVisibility(View.INVISIBLE);
                            setSupportActionBar(chatToolbar);
                            selected_frag = new ChatFragment(current);
                            break;
                        case R.id.task:
                            taskToolbar.setVisibility(View.VISIBLE);
                            chatToolbar.setVisibility(View.INVISIBLE);
                            contactToolbar.setVisibility(View.INVISIBLE);
                            profileToolbar.setVisibility(View.INVISIBLE);
                            setSupportActionBar(taskToolbar);
                            selected_frag = new TasksFragment(current);
                            break;
                        case R.id.contact:
                            contactToolbar.setVisibility(View.VISIBLE);
                            taskToolbar.setVisibility(View.INVISIBLE);
                            chatToolbar.setVisibility(View.INVISIBLE);
                            profileToolbar.setVisibility(View.INVISIBLE);
                            setSupportActionBar(contactToolbar);
                            selected_frag = new ContactsFragment(curUsername);
                            break;
                        case R.id.account:
                            profileToolbar.setVisibility(View.VISIBLE);
                            chatToolbar.setVisibility(View.INVISIBLE);
                            taskToolbar.setVisibility(View.INVISIBLE);
                            contactToolbar.setVisibility(View.INVISIBLE);
                            setSupportActionBar(profileToolbar);
                            selected_frag = new AccountFragment(current);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected_frag).commit();
                    return true;
                }
            });

            addtask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,AddTaskActivity.class));
                }
            });

            addchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });

        }catch (Exception e){
            Log.d("oppai",e.getMessage());
        }




    }

    private void createdialog() {
        dialog= new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.create_group_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        recyclerView= dialog.findViewById(R.id.memberlistrecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        okay=dialog.findViewById(R.id.okay);
        cancel= dialog.findViewById(R.id.cancel);
        grpname= dialog.findViewById(R.id.grpname);
        grpimage= dialog.findViewById(R.id.grpimage);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grpname.setText("");
                members.clear();
                adapter= new AsigneeListAdapter(MainActivity.this,friends);
                recyclerView.setAdapter(adapter);
                Picasso.with(MainActivity.this).load(R.drawable.profiledefault).placeholder(R.drawable.profiledefault).fit().centerCrop().into(grpimage);
                dialog.dismiss();
            }
        });
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grp= grpname.getText().toString();
                if(validate(grp)){
                    databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                    members.add(current);

                    StorageReference fileref= storageReference.child(System.currentTimeMillis()+"."+getFileExtension(grpimageurl));
                    fileref.putFile(grpimageurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata()!=null && taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result= taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imgurl= uri.toString();
                                        Group gp= new Group(grp,members,imgurl);
                                        databaseReference.push().setValue(gp);
                                        Toast.makeText(getApplicationContext(),"Group Created Successfully!",Toast.LENGTH_SHORT).show();
                                        members.clear();
                                        grpname.setText("");
                                        adapter= new AsigneeListAdapter(MainActivity.this,friends);
                                        recyclerView.setAdapter(adapter);
                                        Picasso.with(MainActivity.this).load(R.drawable.profiledefault).placeholder(R.drawable.profiledefault).fit().centerCrop().into(grpimage);
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Failed to locate picture!",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        grpimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,result_image);
            }
        });

    }


    public void status(String status) {
        try{
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(current.getKey());
            HashMap<String, Object> map = new HashMap<>();
            map.put("status", status);
            databaseReference.updateChildren(map);
        }catch (Exception e){
            Log.d("oppai","status "+e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try{
                if(current!=null && current.getKey()!=null){
                    status("true");
                }

            }catch (Exception e){
                Log.d("oppai",e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            if(current!=null && current.getKey()!=null){
               status("false");
            }

        }catch (Exception e){
            Log.d("oppai",e.getMessage());
        }
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
                adapter= new AsigneeListAdapter(dialog.getContext(),friends);
                adapter.setOnItemClickListener(MainActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("oppai", error.getMessage() + "activity main");
            }
        });

    }

    private boolean validate(String grp) {
        boolean flag=true;
        if(TextUtils.isEmpty(grp)){
            flag=false;
            grpname.setError("is Required!");
        }
        if(members.size()==0){
            flag=false;
            Toast.makeText(getApplicationContext(),"Please Select Members first!",Toast.LENGTH_SHORT).show();
        }
        if(grpimageurl==null){
            flag= false;
            Toast.makeText(getApplicationContext(),"No Image Selected!",Toast.LENGTH_SHORT).show();
        }
        return flag;
    }
    @Override
    public void onItemClick(int position) {
        members.add(friends.get(position));
    }

    @Override
    public void onItemRemove(int position) {
        members.remove(friends.get(position));
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==result_image && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            grpimageurl=data.getData();
            Picasso.with(this).load(grpimageurl).fit().centerCrop().into(grpimage);
        }
    }
}