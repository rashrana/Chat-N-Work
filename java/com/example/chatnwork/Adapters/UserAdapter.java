package com.example.chatnwork.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.chatnwork.MessegeActivity;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<UserAccount> userlist;

    public UserAdapter(Context context, List<UserAccount> userlist) {
        this.context = context;
        this.userlist = userlist;

    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserAccount user = userlist.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageurl().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context).load(user.getImageurl()).placeholder(R.drawable.profiledefault).into(holder.profile_image);
        }
        if(user.getStatus().equals("true")){
            holder.status.setVisibility(View.VISIBLE);
        }else{
            holder.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView username;
        FloatingActionButton status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.pimage);
            username = itemView.findViewById(R.id.usname);
            status= itemView.findViewById(R.id.status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p= getAdapterPosition();
                    UserAccount user= userlist.get(p);
                    Intent i = new Intent(context, MessegeActivity.class);
                    i.putExtra("userkey", user.getKey());
                    i.putExtra("useremail", user.getEmail());
                    i.putExtra("userimage", user.getImageurl());
                    i.putExtra("username", user.getUsername());
                    context.startActivity(i);
                }
            });

        }
    }
}
