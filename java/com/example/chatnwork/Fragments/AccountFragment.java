package com.example.chatnwork.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.example.chatnwork.RegisterActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.STORAGE_SERVICE;


public class AccountFragment extends Fragment implements View.OnLongClickListener {
    private final static int IMAGE_SELECTED= 101;
    private TextView username, email, dob, address, phone, sex;
    private Uri imageuri;
    private CircleImageView profileimage;
    private FloatingActionButton change;
    private UserAccount current;
    private StorageReference storageReference;
    private Dialog dialog;
    private TextView title;
    private Button acceptchange,cancel;
    private EditText edit;
    private KeyListener list;
    public AccountFragment() {

    }
    public AccountFragment(UserAccount current){
        this.current= current;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_account, container, false);

        username= v.findViewById(R.id.username);
        email= v.findViewById(R.id.email);
        dob= v.findViewById(R.id.dateofbirth);
        address= v.findViewById(R.id.address);
        phone= v.findViewById(R.id.phoneno);
        sex= v.findViewById(R.id.sex);
        profileimage= v.findViewById(R.id.profileimage);
        change= v.findViewById(R.id.change);

        username.setText(current.getUsername());

        email.setText(current.getEmail());
        dob.setText(current.getDob());

        if(current.getAddress()!=null){
            address.setText(current.getAddress());
        }
        if(current.getSex()!=null){
            sex.setText(current.getSex());
        }
        if(current.getPhoneno()!=null){
            phone.setText(current.getPhoneno());
        }


        Picasso.with(getContext()).load(current.getImageurl()).fit().placeholder(R.drawable.dragonball).centerCrop().into(profileimage);

        dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.edit_profile_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        title= dialog.findViewById(R.id.title);
        edit= dialog.findViewById(R.id.input);
        acceptchange= dialog.findViewById(R.id.change);
        cancel= dialog.findViewById(R.id.cancel);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,IMAGE_SELECTED);
            }
        });

        //username.setOnLongClickListener(this);
        dob.setOnLongClickListener(this);
        phone.setOnLongClickListener(this);
        address.setOnLongClickListener(this);
        sex.setOnLongClickListener(this);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_SELECTED && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            try {
                imageuri= data.getData();
                storageReference= FirebaseStorage.getInstance().getReference("ProfilePic").child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
                storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getMetadata()!=null && taskSnapshot.getMetadata().getReference()!=null){
                            Task<Uri> result= taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    StorageReference tempref= FirebaseStorage.getInstance().getReferenceFromUrl(current.getImageurl());
                                    tempref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            current.setImageurl(uri.toString());
                                            Picasso.with(getContext()).load(uri.toString()).fit().placeholder(R.drawable.dragonball).centerCrop().into(profileimage);
                                            DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("Users").child(current.getKey());
                                            HashMap<String,Object> map= new HashMap<>();
                                            map.put("imageurl",uri.toString());
                                            dbref.updateChildren(map);
                                            Toast.makeText(getContext(),"Profile changed successfully!",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(getView(),e.getMessage(),Snackbar.LENGTH_SHORT).setAction("Okay", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            }).setActionTextColor(getResources().getColor(R.color.purple_500)).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
                Log.d("oppai",e.getMessage());
            }



        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr= getActivity().getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.username:
                title.setText("Edit Username");
                edit.setHint("Enter New Username");
                edit.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case R.id.dateofbirth:
                title.setText("Edit Date of Birth");
                list= edit.getKeyListener();
                edit.setKeyListener(null);
                edit.setHint("Enter Your Date of Birth");
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar cldr= Calendar.getInstance();
                        int day= cldr.get(Calendar.DAY_OF_MONTH);
                        int month= cldr.get(Calendar.MONTH);
                        int year= cldr.get(Calendar.YEAR);

                        DatePickerDialog datePicker= new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edit.setText(dayOfMonth+"/"+month+"/"+year);
                            }
                        },year,month,day);
                        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePicker.show();
                    }
                });
                break;
            case R.id.phoneno:
                title.setText("Edit Phone no.");
                edit.setHint("Enter Your Phone no.");
                edit.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case R.id.address:
                title.setText("Edit Address");
                edit.setHint("Enter Your Address");
                edit.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case R.id.sex:
                title.setText("Edit Sex");
                edit.setHint("Enter Your Sex");
                edit.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }

        dialog.show();
        dialog.getWindow().setWindowAnimations(android.R.style.Animation);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
                edit.setKeyListener(list);
                edit.setOnClickListener(null);
                dialog.dismiss();
            }
        });

        acceptchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input= edit.getText().toString().trim();
                DatabaseReference dataref= FirebaseDatabase.getInstance().getReference("Users").child(current.getKey());
                if(!TextUtils.isEmpty(input)){
                    if(v.getId()==R.id.username){
                        HashMap<String,Object> map= new HashMap<>();
                        map.put("username",input);
                        map.put("search",input.toLowerCase());
                        dataref.updateChildren(map);
                        username.setText(input);
                    }else if(v.getId()==R.id.dateofbirth){
                        HashMap<String,Object> map= new HashMap<>();
                        map.put("dob",input);
                        dataref.updateChildren(map);
                        dob.setText(input);
                        edit.setOnClickListener(null);
                    }else if(v.getId()==R.id.phoneno){
                        HashMap<String,Object> map= new HashMap<>();
                        map.put("phoneno",input);
                        dataref.updateChildren(map);
                        phone.setText(input);
                    }else if(v.getId()==R.id.address){
                        HashMap<String,Object> map= new HashMap<>();
                        map.put("address",input);
                        dataref.updateChildren(map);
                        address.setText(input);
                    }else if(v.getId()==R.id.sex){
                        if(input.equalsIgnoreCase("Male") || input.equalsIgnoreCase("Female") ||
                        input.equalsIgnoreCase("Others")){
                            HashMap<String,Object> map= new HashMap<>();
                            map.put("sex",input);
                            dataref.updateChildren(map);
                            sex.setText(input);
                        }
                    }
                }
                edit.setText("");
                edit.setKeyListener(list);
                edit.setOnClickListener(null);
                dialog.dismiss();

            }
        });
        return true;
    }
}