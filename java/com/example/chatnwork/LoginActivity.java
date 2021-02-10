package com.example.chatnwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chatnwork.Model.UserAccount;

public class LoginActivity extends AppCompatActivity {
    private String eml,pwd, userKey;
    private EditText email,pass;
    private TextView signup;
    private AppCompatButton signin, forgotpwd;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        email= findViewById(R.id.logemail);
        pass= findViewById(R.id.logpassword);
        signin= findViewById(R.id.logsignin);
        forgotpwd= findViewById(R.id.forgotpwd);
        signup= findViewById(R.id.logsignup);
        progressBar= findViewById(R.id.logprogresscircle);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                eml= email.getText().toString().trim();
                pwd= pass.getText().toString().trim();
                if(validate()){
                    firebaseAuth.signInWithEmailAndPassword(eml,pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(),"Login Successfull!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText foremail= new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog= new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Forgot Password");
                passwordResetDialog.setMessage("Enter your email to Reset Your Password:");
                passwordResetDialog.setView(foremail);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newemail= foremail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(newemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Password Reset Link Sent to your mail!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }

    private boolean validate() {
        boolean res= true;
        if(TextUtils.isEmpty(pwd)){
            pass.setError("Required!");
            res=false;
        }
        if(TextUtils.isEmpty(eml)){
            email.setError("Required!");
            res=false;
        }
        return res;
    }
}