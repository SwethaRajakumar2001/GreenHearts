package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class IndContestRoom extends AppCompatActivity {

    TextView tvScore, tvRank, tvCreatedby;
    Button btnChat, btnLeave;
    String contest_id, current_user;
    String creator;

    ArrayList<LeaderCard> list;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter myAdapter;
    LeaderCard card;


    FirebaseDatabase db;
    DatabaseReference dbref, user_ref;
    ValueEventListener listen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ind_contest_room);

        contest_id=getIntent().getStringExtra("contest_id");
        current_user= FirebaseAuth.getInstance().getCurrentUser().getUid();

        db=FirebaseDatabase.getInstance();
        dbref=db.getReference().child("contest").child(contest_id).child("participants");
        user_ref=db.getReference().child("user").child(current_user);

        recyclerView=findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        list=new ArrayList<LeaderCard>();
        myAdapter=new LeaderCardAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        tvCreatedby=findViewById(R.id.tvCreatedBy);
        tvRank=findViewById(R.id.tvRank);
        tvScore=findViewById(R.id.tvScore);
        btnChat=findViewById(R.id.btnChat);
        btnLeave=findViewById(R.id.btnLeave);

        DatabaseReference ref2=db.getReference().child("contest").child(contest_id).child("creator_username");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                creator=snapshot.getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        tvCreatedby.setText("Created by "+creator);

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(IndContestRoom.this);
                builder.setMessage("Sure you want to quit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(IndContestRoom.this, "You are outta here", Toast.LENGTH_SHORT).show();
                                dbref.child(current_user).setValue(null);
                                user_ref.child("contests").child(contest_id).setValue(null);
                                IndContestRoom.this.finish();
                            }
                        }).setNegativeButton("Cancel", null);
                AlertDialog alert=builder.create();
                alert.show();
            }
        });

        dbref.child(current_user).child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvScore.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dbref.child(current_user).child("rank").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvRank.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(IndContestRoom.this, com.example.greenhearts.ChatRoom.class);
                intent.putExtra("contest_id", contest_id);
                startActivity(intent);
            }
        });

        listen=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                list.clear();
                for(DataSnapshot snapshot: snap.getChildren()) {
                    card=snapshot.getValue(LeaderCard.class);
                    String user_id=snapshot.getKey();
                    card.setUser_id(user_id);
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("user").child(user_id);
                    ref.child("user_name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            card.setUser_name(snapshot.getValue().toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    ref.child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            card.setProfile_pic(snapshot.getValue().toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    ref.child("no_plant").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            card.setNo_plant((int)snapshot.getValue());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
                Collections.sort(list);
                updateRank();
                myAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        dbref.addValueEventListener(listen);
    }

    public void updateRank() {

        list.get(0).setRank(1);
        for(int j=1; j<list.size();j++) {
            if(list.get(j).getScore()==list.get(j-1).getScore())
                list.get(j).setRank(list.get(j-1).getRank());
            else list.get(j).setRank(list.get(j).getRank()+1);
        }

        for(int j=0; j<list.size(); j++) {
            dbref.child(list.get(j).getUser_id()).child("rank").setValue(list.get(j).getRank());
        }
    }

}

/*


 */