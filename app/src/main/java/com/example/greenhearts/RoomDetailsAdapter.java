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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RoomDetailsAdapter extends RecyclerView.Adapter<RoomDetailsAdapter.ViewHolder> {

    ArrayList<RoomDetails> details;
    ArrayList<String> ids;
    OnRoomClicked activity;
    public RoomDetailsAdapter(Context context, ArrayList<RoomDetails> details, ArrayList<String> contest_ids) {
        this.details=details;
        this.ids=contest_ids;
        activity=(OnRoomClicked) context;
    }

    public interface  OnRoomClicked{
        public void RoomClicked(int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.ivContestIcon);
            tv=itemView.findViewById(R.id.tvContestName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.RoomClicked(details.indexOf(view.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public RoomDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_room_row, parent,false);
        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomDetailsAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(details.get(position));
        if(!(details.get(position).getImageurl().isEmpty())){
            Glide.with(holder.iv.getContext()).load(details.get(position).getImageurl()).into(holder.iv);
        }
        holder.tv.setText(details.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return details.size();
    }
}
