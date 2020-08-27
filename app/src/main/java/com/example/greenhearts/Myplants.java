package com.example.greenhearts;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class Myplants extends AppCompatActivity {
    DatabaseReference plantshow;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private PlantAdapter madapter;
    private String current_User_Id;
    private ChildEventListener mChildEventListener;
    private ProgressBar mProgressBar;
    private ListView gallery;
    ArrayList<Plants> plantlist;
    Plants p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplants);
        gallery = (ListView)findViewById(R.id.gallery);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        plantshow = FirebaseDatabase.getInstance().getReference().child("plant");
        plantlist = new ArrayList<>();

        madapter = new PlantAdapter(Myplants.this,R.layout.mytrees_style,plantlist);
        gallery.setAdapter(madapter);
//        plantshow.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds : snapshot.getChildren())
//                {
//                    p = ds.getValue(Plants.class);
//                    plantlist.add(p);
//                }
//                gallery.setAdapter(madapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        current_User_Id = mAuth.getCurrentUser().getUid();
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Plants pl = dataSnapshot.getValue(Plants.class);
                   // Toast.makeText(Myplants.this,pl.getid(), Toast.LENGTH_LONG).show();
                    if(pl.getid().equals(current_User_Id)) {

                       madapter.add(pl);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            plantshow.addChildEventListener(mChildEventListener);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mChildEventListener!=null)
        {
            plantshow.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        madapter.clear();
    }
}