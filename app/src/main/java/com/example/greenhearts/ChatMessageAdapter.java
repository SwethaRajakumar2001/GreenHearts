package com.example.greenhearts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private ArrayList<ChatMessage> messages;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> list) {
        messages=list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor, tvMessage;
        ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAuthor=itemView.findViewById(R.id.tvAuthor);
            tvMessage=itemView.findViewById(R.id.tvMessage);
            ivPhoto=itemView.findViewById(R.id.ivPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }


    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_layout,viewGroup,false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(messages.get(i));
        viewHolder.tvAuthor.setText(messages.get(i).getUsername());
        viewHolder.tvMessage.setVisibility(View.GONE);
        viewHolder.ivPhoto.setVisibility(View.GONE);
        if(messages.get(i).getText()!=null) {
            viewHolder.tvMessage.setVisibility(View.VISIBLE);
            viewHolder.tvMessage.setText(messages.get(i).getText());
        }
        if(messages.get(i).getPhoto_url()!=null) {
            viewHolder.ivPhoto.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.ivPhoto.getContext())
                    .load(messages.get(i).getPhoto_url())
                    .into(viewHolder.ivPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
