package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class Fewdetails {
    private String user_name, profile_pic;
    private int no_plant;

    public Fewdetails() {

    }

    public Fewdetails(String user_name, String profile_pic, int no_plant) {
        this.user_name = user_name;
        this.profile_pic = profile_pic;
        this.no_plant = no_plant;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getProfile_pic() {
        return profile_pic;
    }
    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
    public int getNo_plant() {
        return no_plant;
    }
    public void setNo_plant(int no_plant) {
        this.no_plant = no_plant;
    }
}

public class IndContestRoom extends AppCompatActivity {

    TextView tvScore, tvRank, tvCreatedby;
    Button btnChat, btnLeave, btnCopy;
    String contest_id, current_user;
    String creator, contest_name;
    int no_contestants, c;

    ArrayList<LeaderCard> list;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LeaderCardAdapter myAdapter;
    //LeaderCard card;


    FirebaseDatabase db;
    DatabaseReference dbref, user_ref, scoreRef, rankRef, conNameRef;
    ValueEventListener listen, scoreListen, rankListen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ind_contest_room);

        contest_id=getIntent().getStringExtra("contest_id");
        current_user= FirebaseAuth.getInstance().getCurrentUser().getUid();

        db=FirebaseDatabase.getInstance();
        dbref=db.getReference().child("contest").child(contest_id).child("participants");
        user_ref=db.getReference().child("user").child(current_user);
        conNameRef=db.getReference().child("contest").child(contest_id).child("contest_name");

        conNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                    setTitle(snapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerView=findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(IndContestRoom.this);
        recyclerView.setLayoutManager(layoutManager);

        list=new ArrayList<LeaderCard>();
        myAdapter=new LeaderCardAdapter(IndContestRoom.this, list);
        myAdapter.setHasStableIds(true);
        recyclerView.setAdapter(myAdapter);

        tvCreatedby=findViewById(R.id.tvCreatedBy);
        tvRank=findViewById(R.id.tvUserRank);
        tvScore=findViewById(R.id.tvUserScore);
        btnChat=findViewById(R.id.btnChat);
        btnLeave=findViewById(R.id.btnLeave);
        btnCopy=findViewById(R.id.btnCopy);

        DatabaseReference ref2=db.getReference().child("contest").child(contest_id).child("creator_username");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                creator=snapshot.getValue(String.class);
                tvCreatedby.setText("Created by " +creator);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IndContestRoom.this, "creator name cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("contest_id", contest_id);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(IndContestRoom.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(IndContestRoom.this);
                builder.setMessage("Sure you want to quit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(IndContestRoom.this, "Press back to refresh!", Toast.LENGTH_SHORT).show();
                                dbref.child(current_user).removeValue();
                                user_ref.child("contests").child(contest_id).setValue(null);
                                IndContestRoom.this.finish();
                            }
                        }).setNegativeButton("Cancel", null);
                AlertDialog alert=builder.create();
                alert.show();
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

    }

    public void updateRank() {

        list.get(0).setRank(1);
        if(list.get(0).getUser_id().equals(current_user))
            tvRank.setText("1");
        for(int j=1; j<list.size();j++) {
            if(list.get(j).getScore()==list.get(j-1).getScore())
                list.get(j).setRank(list.get(j-1).getRank());
            else list.get(j).setRank(list.get(j-1).getRank()+1);
            if(list.get(j).getUser_id().equals(current_user))
                tvRank.setText(Integer.toString(list.get(j).getRank()));
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if(listen!=null) {
            dbref.removeEventListener(listen);
            listen=null;
        }
        if(scoreListen!=null) {
            scoreRef.removeEventListener(scoreListen);
            scoreListen=null;
        }
      /*  if(rankListen!=null) {
            rankRef.removeEventListener(rankListen);
            rankListen=null;
        }*/
        /*
        for(int j=0; j<list.size(); j++) {
            dbref.child(list.get(j).getUser_id()).child("rank").setValue(list.get(j).getRank());
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference leaveConRef=db.getReference().child("contest").child(contest_id).child("participants");
        leaveConRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren()) {
                    DatabaseReference conRef=db.getReference().child("contest").child(contest_id);
                    conRef.setValue(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //Application.leftcontest=true;
    }

    public boolean exists(String user_id) {
        for(int i=0;i<list.size();i++) {
            if (list.get(i).getUser_id().equals(user_id))
                return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        
        if(listen==null) {
            //Toast.makeText(IndContestRoom.this, "hello listen null", Toast.LENGTH_SHORT).show();
            listen = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snap) {
                    list.clear();
                    c = 0;
                    no_contestants=(int)snap.getChildrenCount();
                    //Log.d("error", "on data chaanges");
                    //Toast.makeText(IndContestRoom.this, "on data change", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot snapshot : snap.getChildren()) {
                        String user_id = snapshot.getKey().toString();

                        Log.d("size", Integer.toString(list.size()));
                        Log.d("error", user_id);

                        int score=0;
                        /*if(snapshot.child("rank").getValue()!=null)
                            rank=snapshot.child("rank").getValue(Integer.class);*/
                        if(snapshot.child("score").getValue()!=null)
                            score=snapshot.child("score").getValue(Integer.class);

                        final LeaderCard card=new LeaderCard(score);
                        //card = snapshot.getValue(LeaderCard.class);

                        card.setUser_id(user_id);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(user_id);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                Fewdetails details=snapshot1.getValue(Fewdetails.class);
                                //Log.d("user", details.getUser_name());
                                card.setUser_name(details.getUser_name());
                                card.setNo_plant(details.getNo_plant());
                                card.setProfile_pic(details.getProfile_pic());
                                if(!exists(card.getUser_id())) {
                                    list.add(card);
                                    c=c+1;
                                }
                                Log.d("c:", Integer.toString(c));
                                if(c==no_contestants) {
                                    Log.d("SIZE", Integer.toString(list.size()));
                                    //Log.d("member", list.get(0).getUser_id());
                                    //Log.d("member", list.get(1).getUser_id());
                                    Collections.sort(list);
                                    //Log.d("member", list.get(0).getUser_name());
                                    //Log.d("member", list.get(1).getUser_name());
                                    updateRank();
                                    myAdapter=new LeaderCardAdapter(IndContestRoom.this, list);
                                    recyclerView.setAdapter(myAdapter);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    //myAdapter=new LeaderCardAdapter(IndContestRoom.this, list);
                    //recyclerView.setAdapter(myAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(IndContestRoom.this, "leaderboard cancelled", Toast.LENGTH_SHORT).show();
                }
            };
            dbref.addValueEventListener(listen);
        }

        scoreRef=dbref.child(current_user).child("score");
        if(scoreListen==null) {
            scoreListen = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!=null) {
                        int score = snapshot.getValue(Integer.class);
                        tvScore.setText(Integer.toString(score));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(IndContestRoom.this, "score cancelled", Toast.LENGTH_SHORT).show();
                }
            };
            scoreRef.addValueEventListener(scoreListen);
        }

       /* rankRef=dbref.child(current_user).child("rank");
        if(rankListen==null) {
            rankListen = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!=null) {
                        int rank = snapshot.getValue(Integer.class);
                        tvRank.setText(Integer.toString(rank));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(IndContestRoom.this, "rank cancelled", Toast.LENGTH_SHORT).show();
                }
            };
            rankRef.addValueEventListener(rankListen);
        }*/
    }
}

/*


 */