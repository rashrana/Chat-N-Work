package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.chatnwork.Adapters.MessegeAdapter;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.Notification.APIService;
import com.example.chatnwork.Notification.Client;
import com.example.chatnwork.Notification.Data;
import com.example.chatnwork.Notification.MyResponse;
import com.example.chatnwork.Notification.NotificationSender;
import com.example.chatnwork.Notification.Token;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class MessegeActivity extends AppCompatActivity {

    private final static int image_selected_code=1000;
    APIService apiService;
    String receiverkey, receiveremail, receiverusername, receiverimage, senderemail;
    TextView goback, rtitle;
    CircleImageView rimage;
    private Toolbar toolbar;
    private EditText messegetext;
    private ImageButton sendbtn,sendimg;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private MessegeAdapter adapter;
    private RecyclerView recyclerView;
    private List<Chat> chatList;

    private RequestQueue requestQueue;
    private String FCM_API= "https://fcm.googleapis.com/fcm/send";
    private String serverKey="key="+"AAAAgGV7B8c:APA91bGfQDciGc57rY5Z_8GO0hGqfryfu-O_yFBP2iqIzo1uejF4a_AY_uyoN6rDuFy2o84ofADrw2sGwaNnPExMCiokRJ87mzQsBZHFo4YDD746Y7MTK0Pq_EMdFUWapH7j4gdefsM-";
    private String contentType= "application/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messege);

        storageReference= FirebaseStorage.getInstance().getReference("Messages");
      //  apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        chatList = new ArrayList<>();
        recyclerView = findViewById(R.id.messrecylerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        Bundle b = getIntent().getExtras();
        receiverkey = b.getString("userkey");
        receiveremail = b.getString("useremail");
        receiverusername = b.getString("username");
        receiverimage = b.getString("userimage");
        senderemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        toolbar = findViewById(R.id.messegeToolBar);
        setSupportActionBar(toolbar);
//        try{
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }catch (NullPointerException e){
//            Log.d("oppai",e.getMessage());        }


        rtitle = toolbar.findViewById(R.id.messname);
        rimage = toolbar.findViewById(R.id.messimage);
        goback = toolbar.findViewById(R.id.messgoback);

        rtitle.setText(receiverusername);
        Picasso.with(getApplicationContext()).load(receiverimage).placeholder(R.drawable.profiledefault).into(rimage);
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
                    sendMessege(senderemail, receiveremail, msg);
                    messegetext.setText("");

                    //sendNotification
                    String s= receiveremail.replace("@","").replace(".","");
                    String topic= "/topic/"+s;
                    JSONObject notification= new JSONObject();
                    JSONObject notificationBody= new JSONObject();

                    try{
                        notificationBody.put("title","Chat N Work");
                        notificationBody.put("message",msg);
                        notification.put("to",topic);
                        notification.put("data",notificationBody);
                    }catch (Exception e){
                        Log.d("oppai","Message Activity: "+e.getMessage());
                    }
                    //sendNotification(notification);

                    requestQueue= Volley.newRequestQueue(MessegeActivity.this);
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
        readMessege(senderemail, receiveremail, receiverimage);


    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("oppai", "success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("oppai", error.getMessage());
            }
        }){
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> params= new HashMap<String,String>();
                params.put("Authorization",serverKey);
                params.put("Content-Type",contentType);
                return params;

            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void readMessege(String senderemail, String receiveremail, String receiverimage) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot current : snapshot.getChildren()) {
                    Chat chat = current.getValue(Chat.class);
                    if ((chat.getSender().equals(senderemail) && chat.getReceiver().equals(receiveremail)) ||
                            chat.getReceiver().equals(senderemail) && chat.getSender().equals(receiveremail)) {
                        chatList.add(chat);
                    }
                }


                adapter = new MessegeAdapter(getApplicationContext(), chatList, receiverimage);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


      //  updateToken();
    }



    private void sendMessege(String senderemail, String receiveremail, String msg) {
        final Calendar cldr= Calendar.getInstance();
        Date times= cldr.getTime();
        Chat c = new Chat(senderemail, receiveremail, msg,times);
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
                                sendMessege(senderemail,receiveremail,imageurl);
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