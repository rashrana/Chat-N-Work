package com.example.chatnwork.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.chatnwork.MessegeActivity;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AsigneeListAdapter extends RecyclerView.Adapter<AsigneeListAdapter.ViewHolder> {
    private Context context;
    private List<UserAccount> userlist;
    private OnItemClickListener listener;

    public AsigneeListAdapter(Context context, List<UserAccount> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public AsigneeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.asignee_list_item, parent, false);
        return new AsigneeListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AsigneeListAdapter.ViewHolder holder, int position) {
        UserAccount user = userlist.get(position);
        holder.person.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox person;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            person = itemView.findViewById(R.id.asigneeitem);

            person.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if(person.isChecked()){
                        listener.onItemClick(position);
                    }else{
                        listener.onItemRemove(position);
                    }
                    
                }
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onItemRemove(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
