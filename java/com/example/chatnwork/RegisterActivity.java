package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.chatnwork.Model.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private final static int image_selected_code= 100;


    String uname,pwd,cpwd,eml,db,imgurl;
    Uri imageurl;

    private ProgressBar progressBar;
    private DatePickerDialog datePicker;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ImageView profilepic;
    private EditText username,pass,conpass,email,dob;
    private AppCompatButton signup;
    private TextView signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profilepic= findViewById(R.id.regimage);
        username= findViewById(R.id.regusername);
        pass= findViewById(R.id.regpassword);
        conpass= findViewById(R.id.confirmpassword);
        email= findViewById(R.id.regemail);
        dob= findViewById(R.id.regdob);
        signup= findViewById(R.id.regsignup);
        signin= findViewById(R.id.regsignin);
        progressBar= findViewById(R.id.regprogresscircle);

        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        storageReference= FirebaseStorage.getInstance().getReference("ProfilePic");

        Picasso.with(getApplicationContext()).load(imageurl).placeholder(R.mipmap.ic_launcher_round).into(profilepic);
        progressBar.setVisibility(ProgressBar.INVISIBLE);


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,image_selected_code);
            }
        });


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr= Calendar.getInstance();
                int day= cldr.get(Calendar.DAY_OF_MONTH);
                int month= cldr.get(Calendar.MONTH);
                int year= cldr.get(Calendar.YEAR);

                datePicker= new DatePickerDialog(RegisterActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                },year,month,day);
                datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePicker.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                uname= username.getText().toString().trim();
                pwd= pass.getText().toString().trim();
                cpwd= conpass.getText().toString().trim();
                eml= email.getText().toString().trim();
                db= dob.getText().toString();

                if(validate()){
                    firebaseAuth.createUserWithEmailAndPassword(eml,pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            StorageReference fileref= storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageurl));
                            fileref.putFile(imageurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if(taskSnapshot.getMetadata()!=null && taskSnapshot.getMetadata().getReference()!=null){
                                        Task<Uri> result= taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imgurl= uri.toString();
                                                UserAccount user= new UserAccount(uname,pwd,eml,db,imgurl,"false");
                                                String key=databaseReference.push().getKey();
                                                user.setKey(key);
                                               // String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                assert key != null;
                                                databaseReference.child(key).setValue(user);
                                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                finish();
                                                Toast.makeText(getApplicationContext(),"Registered Successfully!",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                imgurl="default";
                                                UserAccount user= new UserAccount(uname,pwd,eml,db,imgurl,"true");
                                                String key=databaseReference.push().getKey();
                                                user.setKey(key);                                                                           ///*********************************************************************//
                                                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                assert key != null;
                                                databaseReference.child(key).setValue(user);
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                                Toast.makeText(getApplicationContext(),"Failed to locate picture!",Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private boolean validate() {
        boolean res= true;
        if(TextUtils.isEmpty(uname)){
            username.setError("Required!");
            res= false;
        }
        if(TextUtils.isEmpty(pwd)){
            pass.setError("Required!");
            res=false;
        }
        if(TextUtils.isEmpty(cpwd)){
            conpass.setError("Required!");
            res=false;
        }
        if(TextUtils.isEmpty(eml)){
            email.setError("Required!");
            res=false;
        }
        if(TextUtils.isEmpty(db)){
            dob.setError("Required!");
            res=false;
        }
        if(!cpwd.equals(pwd)){
            conpass.setError("Password and confirm password must be same!");
            res=false;
        }
        if(imageurl==null){
            Toast.makeText(getApplicationContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
            res=false;
        }
        return res;
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
            imageurl=data.getData();
            Picasso.with(this).load(imageurl).fit().centerCrop().into(profilepic);
        }
    }
}