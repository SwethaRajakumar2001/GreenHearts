package com.example.greenhearts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<PostStructure> feedposts;
    private ArrayList<String> feedpostIDs;
    private FirebaseUser firebaseUser;
    private OnPostClicked activity;


    public PostAdapter(Context context, ArrayList<PostStructure> data , ArrayList<String> dataID)
    {
        this.feedposts=data;
        activity= (OnPostClicked) context;
        feedpostIDs= dataID;
    }

    public interface OnPostClicked
    {
        public void PostClicked(int i);
    }




    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView ivpostpic;
        TextView tvpost;
        TextView tvpostdate;
        TextView tvpostusername;
        TextView tvpostnumlikes;
        TextView tvnumcomments;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ivpostpic= itemView.findViewById(R.id.ivfeedpostpic);
            tvpost= itemView.findViewById(R.id.tvpost);
            tvpostdate= itemView.findViewById(R.id.tvpostdate);
            tvpostusername= itemView.findViewById(R.id.tvpostusername);
            tvpostnumlikes= itemView.findViewById(R.id.tvpostnumlikes);
            tvnumcomments= itemView.findViewById(R.id.tvnumcomments);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.PostClicked(feedposts.indexOf(view.getTag()));
                }
            });
        }
    }
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutpost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(feedposts.get(position));
        holder.ivpostpic.setVisibility(View.GONE);
        holder.tvpost.setVisibility(View.GONE);
        if(!feedposts.get(position).getMessage().isEmpty())
        {
            holder.tvpost.setVisibility(View.VISIBLE);
            holder.tvpost.setText(feedposts.get(position).getMessage());
        }
        if(!feedposts.get(position).getImage().isEmpty())
        {
            holder.ivpostpic.setVisibility(View.VISIBLE);
            Glide.with(holder.ivpostpic.getContext())
                    .load(feedposts.get(position).getImage())
                    .into(holder.ivpostpic);
        }
        holder.tvpostdate.setText(feedposts.get(position).getTimestamp());
        holder.tvpostusername.setText(feedposts.get(position).getUsername());
        holder.tvpostnumlikes.setText(Integer.toString(feedposts.get(position).getNlikes()));
        holder.tvnumcomments.setText(Integer.toString(feedposts.get(position).getNcomment()));
    }

    @Override
    public int getItemCount() {
        return feedposts.size();
    }
}
