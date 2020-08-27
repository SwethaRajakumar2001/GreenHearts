package com.example.greenhearts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlantAdapter extends ArrayAdapter<Plants> {

    public PlantAdapter(@NonNull Context context, int resource, List<Plants> objects) {
        super(context, resource,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.mytrees_style, parent, false);
        }
        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.treeimg);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.treenam);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.treedate);
        Button bu = (Button)convertView.findViewById(R.id.removetree);
        Plants plant = getItem(position);
        boolean isphoto = plant.getPhotoUrl()!=null;
        boolean name = plant.getName()!=null;
        if(isphoto && name)
        {

            Glide.with(photoImageView.getContext())
                .load(plant.getPhotoUrl())
                .into(photoImageView);
            nameTextView.setText(plant.getName());
            dateTextView.setText(plant.getText());
        }
        return convertView;
    }
}
