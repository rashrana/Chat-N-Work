package com.example.chatnwork.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.chatnwork.MessegeActivity;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {
    private Context context;
    private List<UserAccount> userlist, friendlist, sentlist, incominglist;
    private Activity con;
    private DatabaseReference databaseReference;
    private Dialog dialog;
    private UserAccount current;

    @SuppressLint("UseCompatLoadingForDrawables")
    public AddFriendAdapter(Context context, List<UserAccount> userlist, List<UserAccount> friendlist, List<UserAccount> sentlist,
                            List<UserAccount> incominglist, UserAccount current, Activity con) {
        this.context = context;
        this.userlist = userlist;
        this.friendlist = friendlist;
        this.sentlist = sentlist;
        this.incominglist = incominglist;
        this.current = current;
        this.con = con;


        dialog = new Dialog(con);
        dialog.setContentView(R.layout.respond_request);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(con.getDrawable(R.drawable.roundbuttonsent));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);


    }

    @NonNull
    @Override
    public AddFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.addfrnd_item, parent, false);

        return new AddFriendAdapter.ViewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddFriendAdapter.ViewHolder holder, int position) {
        UserAccount user = userlist.get(position);
        try {
            holder.username.setText(user.getUsername());
        } catch (Exception e) {
            Log.d("oppai", e.getMessage() + "adapter1");
        }

        if (user.getImageurl().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context).load(user.getImageurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
        }
        boolean flag = false;
        for (UserAccount u : friendlist) {
            if (u.getEmail().equals(user.getEmail())) {
                holder.addbutton.setText("Friends");
                holder.addbutton.setBackgroundResource(R.drawable.roundbuttonsent);
                flag = true;
                break;
            }
        }
        if (!flag) {
            for (UserAccount u : sentlist) {
                if (u.getEmail().equals(user.getEmail())) {
                    holder.addbutton.setText("Cancel Req.");
                    holder.addbutton.setBackgroundResource(R.drawable.roundedittext);
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            for (UserAccount u : incominglist) {
                if (u.getEmail().equals(user.getEmail())) {
                    holder.addbutton.setText("Respond Req.");
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            holder.addbutton.setText("Add Friend");
        }
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView username, addbutton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.aimage);
            username = itemView.findViewById(R.id.aname);
            addbutton = itemView.findViewById(R.id.abutton);
            addbutton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    int p = getAdapterPosition();
                    UserAccount user = userlist.get(p);
                    if (addbutton.getText().equals("Add Friend")) {
                        //adding the persons name to request sent list

                        databaseReference = FirebaseDatabase.getInstance().getReference(current.getUsername()).child("Request Sent");
                        databaseReference.child(user.getUsername()).setValue(user);

                        //adding the users name to incoming request list of the receiver
                        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUsername()).child("Incoming Request");
                        databaseReference.child(current.getUsername()).setValue(current);
                        addbutton.setText("Request Sent");
                        addbutton.setBackgroundResource(R.drawable.roundbuttonsent);//*****************************************************************************************//

                    } else if (addbutton.getText().equals("Respond Req.")) {
                        createDialog(user);
                        dialog.show();


                    } else if (addbutton.getText().equals("Cancel Req.")) {
                        //removing the persons name to request sent list
                        databaseReference = FirebaseDatabase.getInstance().getReference(current.getUsername()).child("Request Sent");
                        databaseReference.child(user.getUsername()).removeValue();

                        //removing the users name from incoming request list of the receiver
                        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUsername()).child("Incoming Request");
                        databaseReference.child(current.getUsername()).removeValue();
                        addbutton.setText("Add Friend");
                        addbutton.setBackgroundResource(R.drawable.roundbutton);
                    }

                }
            });

        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void createDialog(UserAccount user) {
            Button accept = dialog.findViewById(R.id.accept);
            Button reject = dialog.findViewById(R.id.reject);
            TextView cancel = dialog.findViewById(R.id.cancel);

            CircleImageView image = dialog.findViewById(R.id.resimage);
            TextView username = dialog.findViewById(R.id.resname);
            TextView email = dialog.findViewById(R.id.resemail);
            TextView dob = dialog.findViewById(R.id.resdob);

            Picasso.with(context).load(user.getImageurl()).placeholder(R.drawable.profiledefault).into(image);
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            dob.setText(user.getDob());

            accept.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    //adding the persons name to friends list
                    databaseReference = FirebaseDatabase.getInstance().getReference(current.getUsername()).child("Friends");
                    databaseReference.child(user.getUsername()).setValue(user);

                    //adding the users name to persons' friends list
                    databaseReference = FirebaseDatabase.getInstance().getReference(user.getUsername()).child("Friends");
                    databaseReference.child(current.getUsername()).setValue(current);

                    //removing the persons name from incoming list
                    databaseReference = FirebaseDatabase.getInstance().getReference(current.getUsername()).child("Incoming Request");
                    databaseReference.child(user.getUsername()).removeValue();

                    //removing the users name from persons' sent list
                    databaseReference = FirebaseDatabase.getInstance().getReference(user.getUsername()).child("Request Sent");
                    databaseReference.child(current.getUsername()).removeValue();

                    addbutton.setText("Friends");
                    addbutton.setBackgroundResource(R.drawable.roundbuttonsent);
                    dialog.dismiss();
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    //removing the persons name from incoming list
                    databaseReference = FirebaseDatabase.getInstance().getReference(current.getUsername()).child("Incoming Request");
                    databaseReference.child(user.getUsername()).removeValue();

                    //removing the users name from persons' sent list
                    databaseReference = FirebaseDatabase.getInstance().getReference(user.getUsername()).child("Request Sent");
                    databaseReference.child(current.getUsername()).removeValue();

                    addbutton.setText("Add Friend");
                    addbutton.setBackgroundResource(R.drawable.roundbutton);
                    dialog.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }


}

