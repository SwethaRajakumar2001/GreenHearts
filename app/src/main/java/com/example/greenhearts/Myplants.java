package com.example.greenhearts;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Myplants extends AppCompatActivity {
    DatabaseReference dbrf;
    DatabaseReference plantshow;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private PlantAdapter madapter;
    private String current_User_Id;
    private ChildEventListener mChildEventListener;
    private ProgressBar mProgressBar;
    private ListView gallery;
    ArrayList<Plants> plantlist;
    ArrayList<String> listkey;
    private int selectedpos=0;
    private Boolean itemSelected = false;
    Plants p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplants);
        gallery = (ListView)findViewById(R.id.gallery);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        plantshow = FirebaseDatabase.getInstance().getReference().child("plant");
        current_User_Id = mAuth.getUid();
        dbrf = FirebaseDatabase.getInstance().getReference().child("user").child(current_User_Id);
        plantlist = new ArrayList<>();
        listkey = new ArrayList<>();
        madapter = new PlantAdapter(Myplants.this,R.layout.mytrees_style,plantlist);
        gallery.setAdapter(madapter);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedpos = i;
                itemSelected = true;
              // madapter.remove(i);
            }
        });
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
                        listkey.add((String)dataSnapshot.getKey());
                       // Toast.makeText(Myplants.this,dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    int index = listkey.indexOf(key);

                    if (index != -1) {
                        plantlist.remove(index);
                        listkey.remove(index);
                        madapter.notifyDataSetChanged();
                    }

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
    public void Removetree(View view)
    {
        gallery.setItemChecked(selectedpos,false);
       // plantlist.remove(selectedpos);
        //madapter.notifyDataSetChanged();
       plantshow.child(listkey.get(selectedpos)).removeValue();
       dbrf.child("plant").child(listkey.get(selectedpos)).removeValue();
       dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               Integer n = snapshot.child("no_plant").getValue(Integer.class);
               n = n-1;
               dbrf.child("no_plant").setValue(n);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
}