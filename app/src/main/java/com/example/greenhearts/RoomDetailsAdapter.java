package com.example.greenhearts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RoomDetailsAdapter extends FirebaseRecyclerAdapter<RoomDetails, RoomDetailsAdapter.ViewHolder> {
    
    public RoomDetailsAdapter(@NonNull FirebaseRecyclerOptions<RoomDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RoomDetails model) {
        holder.tvContestName.setText(model.name);
        if(model.imageurl!="null") {
            Glide.with(holder.ivContestIcon.getContext()).load(model.imageurl).into(holder.ivContestIcon);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_room_row, parent,false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivContestIcon;
        TextView tvContestName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContestIcon=(ImageView)itemView.findViewById(R.id.ivContestIcon);
            tvContestName=(TextView)itemView.findViewById(R.id.tvContest);

        }
    }
}
