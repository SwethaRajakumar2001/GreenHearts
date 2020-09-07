package com.example.greenhearts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Myplants extends AppCompatActivity {
    DatabaseReference dbrf;
    DatabaseReference plantshow;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private PlantAdapter madapter;
    private String current_User_Id;
    private ChildEventListener mChildEventListener;
    private ListView gallery;
    ArrayList<Plants> plantlist;
    ArrayList<String> listkey;
    public int selectedpos=0;
    private Boolean itemSelected = false;
    Plants p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplants);
        gallery = (ListView)findViewById(R.id.gallery);
        plantshow = FirebaseDatabase.getInstance().getReference().child("plant");
        current_User_Id = mAuth.getUid();
        dbrf = FirebaseDatabase.getInstance().getReference().child("user").child(current_User_Id);
        plantlist = new ArrayList<>();
        listkey = new ArrayList<>();
        madapter = new PlantAdapter(Myplants.this,R.layout.mytrees_style,plantlist);
        gallery.setAdapter(madapter);
       // Toast.makeText(Myplants.this,gallery.getAdapter().getCount(),Toast.LENGTH_LONG).show();
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final  int item = i;
                selectedpos = item;
               // Toast.makeText(Myplants.this,selectedpos, Toast.LENGTH_LONG).show();
               // itemSelected = true;
            }
        });

        current_User_Id = mAuth.getCurrentUser().getUid();
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Plants pl = dataSnapshot.getValue(Plants.class);
                   // Toast.makeText(Myplants.this,pl.getid(), Toast.LENGTH_LONG).show();

                    if(pl.getid().equals(current_User_Id)) {

                       madapter.add(pl);
                        listkey.add((String)dataSnapshot.getKey());

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

//        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                final int selectp = i;
//                new AlertDialog.Builder(Myplants.this)
//                        .setIcon(android.R.drawable.ic_delete)
//                        .setTitle("Are you sure? ")
//                        .setMessage("Do you want to delete?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                plantlist.remove(selectp);
//                                listkey.remove(selectp);
//                                plantshow.child(listkey.get(selectp)).removeValue();
//                                dbrf.child("plant").child(listkey.get(selectp)).removeValue();
//                                dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        Integer n = snapshot.child("no_plant").getValue(Integer.class);
//                                        n = n-1;
//                                        dbrf.child("no_plant").setValue(n);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                                madapter.notifyDataSetChanged();
//                            }
//                        })
//                        .setNegativeButton("No",null)
//                        .show();
//
//            }
//        });
       // gallery.setItemChecked(selectedpos,true);
                new AlertDialog.Builder(Myplants.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure? ")
                        .setMessage("Do you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                        })
                        .setNegativeButton("No",null)
                        .show();
//       plantshow.child(listkey.get(selectedpos)).removeValue();
//       dbrf.child("plant").child(listkey.get(selectedpos)).removeValue();
//       dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot snapshot) {
//               Integer n = snapshot.child("no_plant").getValue(Integer.class);
//               n = n-1;
//               dbrf.child("no_plant").setValue(n);
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError error) {
//
//           }
//       });
    }
}