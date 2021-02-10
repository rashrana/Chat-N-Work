package com.example.chatnwork.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.chatnwork.MessegeActivity;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ViewHolder> {
    private Context context;
    private List<UserAccount> userlist;
    private List<Chat> lastmsgs;


    public ChatlistAdapter(Context context, List<UserAccount> userlist, List<Chat> lastmsgs) {
        this.context = context;
        this.userlist = userlist;
        this.lastmsgs = lastmsgs;

    }

    @NonNull
    @Override
    public ChatlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chatlist_item, parent, false);
        return new ChatlistAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatlistAdapter.ViewHolder holder, int position) {


        Chat chat = lastmsgs.get(position);
        for (UserAccount user : userlist) {
            if (user.getEmail().equals(chat.getSender()) || user.getEmail().equals(chat.getReceiver())) {
                holder.username.setText(user.getUsername());

                if (user.getImageurl().equals("default")) {
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.with(context).load(user.getImageurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
                }

                if (user.getStatus().equals("true")) {
                    holder.status.setVisibility(View.VISIBLE);
                } else {
                    holder.status.setVisibility(View.GONE);
                }

                if(URLUtil.isValidUrl(chat.getMessege())){
                    holder.msg.setText("Photo");
                }
                else if (chat.getMessege().length() > 55) {
                    String ms = chat.getMessege().substring(0, 55) + "...";
                    holder.msg.setText(ms);
                } else {
                    holder.msg.setText(chat.getMessege());
                }

                final Calendar cldr = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") String curdate = new SimpleDateFormat("dd MMM, yyyy").format(cldr.getTime());
                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd MMM, yyyy").format(chat.getTimes());
                @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("hh:mm").format(chat.getTimes());
                @SuppressLint("SimpleDateFormat") String hr = new SimpleDateFormat("HH").format(chat.getTimes());
                if (Integer.parseInt(hr) > 11) {
                    time += " pm";
                } else {
                    time += " am";
                }

                if (curdate.equals(date)) {
                    holder.time.setText(time);
                } else {
                    holder.time.setText(date);
                }

            }
        }


    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView username, msg, time;
        FloatingActionButton status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.climage);
            username = itemView.findViewById(R.id.clname);
            msg = itemView.findViewById(R.id.clmsg);
            time = itemView.findViewById(R.id.cltime);
            status = itemView.findViewById(R.id.clstatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getAdapterPosition();
                    Chat chat = lastmsgs.get(p);
                    for (UserAccount user : userlist) {
                        if (user.getEmail().equals(chat.getSender()) || user.getEmail().equals(chat.getReceiver())) {
                            Intent i = new Intent(context, MessegeActivity.class);
                            i.putExtra("userkey", user.getKey());
                            i.putExtra("useremail", user.getEmail());
                            i.putExtra("userimage", user.getImageurl());
                            i.putExtra("username", user.getUsername());
                            context.startActivity(i);
                            break;
                        }
                    }

                }
            });
        }
    }
}

