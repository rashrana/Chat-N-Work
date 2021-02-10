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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatnwork.GroupMessageActivity;
import com.example.chatnwork.MessegeActivity;
import com.example.chatnwork.Model.Chat;
import com.example.chatnwork.Model.Group;
import com.example.chatnwork.Model.GroupChat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GroupChatlistAdapter extends RecyclerView.Adapter<GroupChatlistAdapter.ViewHolder> {
    private Context context;
    private List<Group> grouplist;
    private List<GroupChat> lastmsgs;


    public GroupChatlistAdapter(Context context, List<Group> userlist, List<GroupChat> lastmsgs) {
        this.context = context;
        this.grouplist = userlist;
        this.lastmsgs = lastmsgs;

    }

    @NonNull
    @Override
    public GroupChatlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chatlist_item, parent, false);
        return new GroupChatlistAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupChatlistAdapter.ViewHolder holder, int position) {

        Group gp = grouplist.get(position);
        boolean flag=false;
        for (GroupChat chat : lastmsgs) {
            if (gp.getGroupname().equals(chat.getGroupname())) {
                holder.groupname.setText(gp.getGroupname());
                if (gp.getProfileurl().equals("default")) {
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.with(context).load(gp.getProfileurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
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
                flag=true;
                break;
            } else {
                if (lastmsgs.indexOf(chat) == lastmsgs.size() - 1) {
                    holder.groupname.setText(gp.getGroupname());
                    final Calendar cldr = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") String curdate = new SimpleDateFormat("MMM, yyyy").format(cldr.getTime());
                    holder.time.setText(curdate);
                    Picasso.with(context).load(gp.getProfileurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
                }
            }
        }
        try {
            if (lastmsgs.size() == 0) {
                holder.groupname.setText(gp.getGroupname());
                holder.msg.setText("Say hi to your friends!");
                final Calendar cldr = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") String curdate = new SimpleDateFormat("MMM, yyyy").format(cldr.getTime());
                holder.time.setText(curdate);
                Picasso.with(context).load(gp.getProfileurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
            }
            if(!flag){
                holder.msg.setText("Say hi to your friends!");
            }
//            if(holder.msg.getText().equals("")){
//                holder.msg.setText("Say hi to your friends!");
//            }
        } catch (Exception e) {
            Log.d("hohoho", e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return grouplist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView groupname, msg, time;
        FloatingActionButton status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.climage);
            groupname = itemView.findViewById(R.id.clname);
            msg = itemView.findViewById(R.id.clmsg);
            time = itemView.findViewById(R.id.cltime);
            status = itemView.findViewById(R.id.clstatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getAdapterPosition();
                    Group gp = grouplist.get(p);

                    Intent i = new Intent(context, GroupMessageActivity.class);
                     i.putExtra("groupimage", gp.getProfileurl());
                    i.putExtra("groupname", gp.getGroupname());
                    context.startActivity(i);


                }
            });
        }
    }
}

