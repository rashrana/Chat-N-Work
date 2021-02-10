package com.example.chatnwork.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.chatnwork.MessegeActivity;
import com.example.chatnwork.Model.Task;
import com.example.chatnwork.Model.UserAccount;
import com.example.chatnwork.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private List<Task> tasks;
    private Dialog dialog;
    private TextView cancel,ttitle,tdue,treq,tstat;
    private ListView tlistview;
    private Button tokay;
    private List<String> tlist;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;

        dialog= new Dialog(context);
        dialog.setContentView(R.layout.taskdetails_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        cancel=dialog.findViewById(R.id.cancel);
        ttitle= dialog.findViewById(R.id.title);
        tdue= dialog.findViewById(R.id.duedate);
        treq= dialog.findViewById(R.id.requester);
        tstat= dialog.findViewById(R.id.status);
        tlistview= dialog.findViewById(R.id.listview);
        tokay= dialog.findViewById(R.id.okay);

        tlist= new ArrayList<>();
        tokay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task task= tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.due.setText(task.getDue());

        if(task.getStatus().equalsIgnoreCase("open")){
            holder.done.setText("done");
            holder.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference dref= FirebaseDatabase.getInstance().getReference("Tasks").child(task.getKey());
                    HashMap<String,Object> map= new HashMap<>();
                    map.put("status","completed");
                    dref.updateChildren(map);
                }
            });
        }else{
            holder.done.setText("open");
            holder.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference dref= FirebaseDatabase.getInstance().getReference("Tasks").child(task.getKey());
                    HashMap<String,Object> map= new HashMap<>();
                    map.put("status","open");
                    dref.updateChildren(map);
                }
            });
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dref= FirebaseDatabase.getInstance().getReference("Tasks").child(task.getKey());
                dref.removeValue();
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                ttitle.setText(task.getTitle());
                tdue.setText("Due Date: "+task.getDue());
                treq.setText("Requester: "+task.getRequester().getUsername());
                tstat.setText("Status: "+task.getStatus());

                tlist.clear();
                for(UserAccount user:task.getAssignees()){
                    tlist.add(user.getUsername());
                }

                ArrayAdapter adapter= new ArrayAdapter(dialog.getContext(), android.R.layout.simple_list_item_1,tlist);
                tlistview.setAdapter(adapter);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, due,details,edit, delete;
        Button  done;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.tasktitle);
            due= itemView.findViewById(R.id.taskdue);
            details= itemView.findViewById(R.id.taskdetail);
            edit= itemView.findViewById(R.id.taskedit);
            delete= itemView.findViewById(R.id.taskdelete);
            done= itemView.findViewById(R.id.taskbtn);



        }
    }
}

