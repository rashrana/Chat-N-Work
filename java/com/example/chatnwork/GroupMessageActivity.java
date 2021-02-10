package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatnwork.Adapters.GroupMessegeAdapter;
import com.example.chatnwork.Adapters.MessegeAdapter;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.Group;
import com.example.chatnwork.Model.GroupChat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.Notification.APIService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.cache.DiskLruCache;

public class GroupMessageActivity extends AppCompatActivity {

    private final static int image_selected_code=1000;
    APIService apiService;
    String  groupname, groupimage,senderemail;
    TextView goback, rtitle;
    CircleImageView rimage;
    private Toolbar toolbar;
    private EditText messegetext;
    private ImageButton sendbtn,sendimg;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private GroupMessegeAdapter adapter;
    private RecyclerView recyclerView;
    private List<GroupChat> chatList;
    private Group curgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        storageReference= FirebaseStorage.getInstance().getReference("Messages");
        //  apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        chatList = new ArrayList<>();
        recyclerView = findViewById(R.id.messrecylerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        Bundle b = getIntent().getExtras();
        groupname = b.getString("groupname");
        groupimage = b.getString("groupimage");
        senderemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase.getInstance().getReference("Groups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap: snapshot.getChildren()){
                            Group grp= snap.getValue(Group.class);
                            if(grp.getGroupname().equals(groupname)){
                                curgroup=grp;
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("now", error.getMessage());
                    }
                });


        toolbar = findViewById(R.id.messegeToolBar);
        setSupportActionBar(toolbar);
//        try{
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }catch (NullPointerException e){
//            Log.d("oppai",e.getMessage());        }

        rtitle = toolbar.findViewById(R.id.messname);
        rimage = toolbar.findViewById(R.id.messimage);
        goback = toolbar.findViewById(R.id.messgoback);

        rtitle.setText(groupname);
        Picasso.with(getApplicationContext()).load(groupimage).placeholder(R.drawable.profiledefault).into(rimage);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        messegetext = findViewById(R.id.messedittext);
        sendbtn = findViewById(R.id.messsend);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messegetext.getText().toString();
                if (!msg.equals("")) {
                    sendMessege(senderemail, msg);
                    messegetext.setText("");
                }
            }
        });

        sendimg= findViewById(R.id.sendimg);
        sendimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,image_selected_code);
            }
        });
        readMessege(senderemail);

    }

    private void readMessege(String senderemail) {
        databaseReference = FirebaseDatabase.getInstance().getReference("GroupChats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot current : snapshot.getChildren()) {
                    GroupChat chat = current.getValue(GroupChat.class);
                    if (chat.getGroupname().equals(groupname)) {
                        chatList.add(chat);
                    }
                }


                adapter = new GroupMessegeAdapter(getApplicationContext(), chatList, curgroup);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //  updateToken();
    }



    private void sendMessege(String senderemail, String msg) {
        final Calendar cldr= Calendar.getInstance();
        Date times= cldr.getTime();
        GroupChat c = new GroupChat(senderemail,groupname, msg,times);
        databaseReference.push().setValue(c);

        //notification(msg);
    }

//    private void notification(String msg) {
//        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String usertoken=snapshot.getValue(String.class);
//                sendNotifications(usertoken,senderemail,msg);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messege_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.chatInfo) {

        } else if (item.getItemId() == R.id.messFiles) {

        } else if (item.getItemId() == R.id.messTask) {
            startActivity(new Intent(getApplicationContext(),AddTaskActivity.class));
        }
        return true;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==image_selected_code && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            StorageReference fref= storageReference.child(System.currentTimeMillis()+"."+getFileExtension(data.getData()));
            fref.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(taskSnapshot.getMetadata()!=null && taskSnapshot.getMetadata().getReference()!=null){
                        Task<Uri> result= taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageurl= uri.toString();
                                sendMessege(senderemail,imageurl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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

//    public void sendNotifications(String userToken,String title,String message){
//        Data data= new Data(title,message);
//        NotificationSender sender= new NotificationSender(userToken,data);
//        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
//            @Override
//            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                if(response.code()==200){
//                    if(response.body().success!=1){
//                        Toast.makeText(MessegeActivity.this,"Failed",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MyResponse> call, Throwable t) {
//
//            }
//        });
//    }

//    private void updateToken() {
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            String refreshToken= FirebaseInstanceId.getInstance().getToken();
//            Token token= new Token(refreshToken);
//            FirebaseDatabase.getInstance().getReference("Tokens")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .setValue(token);
//        }
//
//
//    }

}