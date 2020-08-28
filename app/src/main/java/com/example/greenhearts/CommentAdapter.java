package com.example.greenhearts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private ArrayList<CommentStructure> postcomments;
    private FirebaseUser firebaseUser;
    private Context activity;

    public CommentAdapter(Context context, ArrayList<CommentStructure> data)
    {
        postcomments= data;
        activity = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvcommentusername;
        TextView tvcomment;
        TextView tvcommenttime;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvcommentusername= itemView.findViewById(R.id.tvcommentusername);
            tvcomment= itemView.findViewById(R.id.tvcomment);
            tvcommenttime= itemView.findViewById(R.id.tvcommenttime);

        }
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutcomment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(postcomments.get(position));
        holder.tvcommentusername.setText(postcomments.get(position).getUser_name());
        holder.tvcomment.setText(postcomments.get(position).getContent());
        holder.tvcommenttime.setText(postcomments.get(position).getTimestamp());

    }


    @Override
    public int getItemCount() {
        return postcomments.size();
    }


}
