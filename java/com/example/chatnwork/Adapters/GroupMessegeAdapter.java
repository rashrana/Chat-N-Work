package com.example.chatnwork.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatnwork.Model.Group;
import com.example.chatnwork.Model.GroupChat;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.List;

public class GroupMessegeAdapter extends RecyclerView.Adapter<GroupMessegeAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<GroupChat> chatList;
    private Group group;
    private FirebaseUser fuser;

    public GroupMessegeAdapter(Context context, List<GroupChat> chatList, Group group) {
        this.context = context;
        this.chatList = chatList;
        this.group = group;
    }

    @NonNull
    @Override
    public GroupMessegeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        Log.d("hohoho","y1");
        if (viewType == MSG_TYPE_RIGHT) {
            Log.d("hohoho","y2");
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);

        } else {
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        Log.d("hohoho","y3");
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t=v.findViewById(R.id.date);
                int vis= t.getVisibility();
                if(vis== View.GONE){
                    vis= View.VISIBLE;

                }else{
                    vis=View.GONE;
                }
                t.setVisibility(vis);

                TextView s= v.findViewById(R.id.time);
                int visi= s.getVisibility();
                if(visi== View.GONE){
                    visi= View.VISIBLE;

                }else{
                    visi=View.GONE;
                }
                s.setVisibility(visi);

            }
        });
        Log.d("hohoho","y4");
        return new GroupMessegeAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupMessegeAdapter.ViewHolder holder, int position) {
        GroupChat chat = chatList.get(position);

        //Check if given msg is url or simple messege
        URLConnection connection = null;
        try {
            if(URLUtil.isValidUrl(chat.getMessege())){
                connection = new URL(chat.getMessege()).openConnection();
                //String contentType="";
                //contentType = connection.getHeaderField("Content-Type");

                //boolean image = contentType.startsWith("image/");
                //if(image){
                final Drawable[] drawable = new Drawable[1];
                Picasso.with(context)
                        .load(chat.getMessege())
                        .centerCrop()
                        .resize(700,900)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                drawable[0] = new BitmapDrawable(context.getResources(), bitmap);
                                holder.show_messege.setText("");
                                holder.show_messege.setCompoundDrawablesWithIntrinsicBounds(null,drawable[0],null,null);
                                holder.show_messege.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessege())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }catch (Exception e){
                                            Log.d("oppai",e.getMessage());
                                        }

                                    }
                                });
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
//                }
//                else{
//                    Log.d("oppai","im8");
//                    holder.show_messege.setText(chat.getMessege());
//                }
            }else{
                holder.show_messege.setText(chat.getMessege());
            }
        } catch (IOException e) {
            Log.d("oppai",e.getMessage());
            holder.show_messege.setText(chat.getMessege());
        }
        Log.d("hohoho","y10");

        for(UserAccount user: group.getMembers()){
            if(chat.getSender().equals(user.getEmail())){
                Picasso.with(context).load(user.getImageurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
            }
        }

        @SuppressLint("SimpleDateFormat") String ti= new SimpleDateFormat("hh:mm").format(chat.getTimes());
        @SuppressLint("SimpleDateFormat") String day= new SimpleDateFormat("dd MMM, yyyy").format(chat.getTimes());
        @SuppressLint("SimpleDateFormat") String hr1= new SimpleDateFormat("HH").format(chat.getTimes());
        if(Integer.parseInt(hr1)>11){
            ti+=" pm";
        }else{
            ti+=" am";
        }
        holder.date.setText(day);
        holder.time.setText(ti);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView show_messege,time,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_messege = itemView.findViewById(R.id.messege);
            time= itemView.findViewById(R.id.time);
            date= itemView.findViewById(R.id.date);
            date.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fuser.getEmail())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}