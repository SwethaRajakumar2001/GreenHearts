package com.example.greenhearts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LeaderCardAdapter extends RecyclerView.Adapter<LeaderCardAdapter.ViewHolder> {

    private ArrayList<LeaderCard> leaderList;

    public LeaderCardAdapter(Context context, ArrayList<LeaderCard> list) {
        leaderList=list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPic;
        TextView tvUsername, tvRank, tvScore, tvNplants;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPic=itemView.findViewById(R.id.ivPic);
            tvUsername=itemView.findViewById(R.id.tvUsername);
            tvRank=itemView.findViewById(R.id.tvRank);
            tvScore=itemView.findViewById(R.id.tvScore);
            tvNplants=itemView.findViewById(R.id.tvNplants);
        }
    }
    @NonNull
    @Override
    public LeaderCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewgroup, int i) {
        View v= LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.leader_list_layout, viewgroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderCardAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(leaderList.get(i));
        viewHolder.tvUsername.setText(leaderList.get(i).getUser_name());
        viewHolder.tvNplants.setText(Integer.toString(leaderList.get(i).getNo_plant()));
        viewHolder.tvScore.setText(Integer.toString(leaderList.get(i).getScore()));
        viewHolder.tvRank.setText(Integer.toString(leaderList.get(i).getRank()));

        if(leaderList.get(i).getProfile_pic()!=null) {
            Glide.with(viewHolder.ivPic.getContext())
                    .load(leaderList.get(i).getProfile_pic())
                    .into(viewHolder.ivPic);
        }
    }

    @Override
    public int getItemCount() {
        return leaderList.size();
    }
}
