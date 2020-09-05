package com.example.greenhearts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContestRoomsActivity extends AppCompatActivity implements RoomDetailsAdapter.OnRoomClicked {

    Button btn_Create,btn_Join;
    RecyclerView rv;
    RoomDetailsAdapter a;
    ArrayList<RoomDetails> details;
    ArrayList<String> contest_ids;
    final int reload=1;
    ChildEventListener listener;

    DatabaseReference ref;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_rooms);

        details=new ArrayList<RoomDetails>();
        contest_ids=new ArrayList<String>();
        user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();

        btn_Create=findViewById(R.id.btn_Create);
        btn_Join=findViewById(R.id.btn_Join);

        a=new RoomDetailsAdapter(ContestRoomsActivity.this, details, contest_ids);


        rv=(RecyclerView)findViewById(R.id.rvContestRooms);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(ContestRoomsActivity.this));
        rv.setAdapter(a);

        readDetails();

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContestRoomsActivity.this, CreateRoomActivity.class);
                startActivity(i);
            }
        });

        btn_Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContestRoomsActivity.this, JoinRoomActivity.class);
                startActivity(i);
            }
        });
    }


    private void readDetails() {

        if(listener==null){
            ref= FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("contests");
            details.clear();
            contest_ids.clear();
            listener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    RoomDetails room = snapshot.getValue(RoomDetails.class);

                    details.add(a.getItemCount(), room);
                    contest_ids.add(snapshot.getKey().toString());
                    a.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            ref.addChildEventListener(listener);
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        if(listener!=null){
            ref.removeEventListener(listener);
            listener=null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        readDetails();
    }

    @Override
    public void RoomClicked(int index) {
        Intent i = new Intent(this, IndContestRoom.class);
        i.putExtra("contest_id", contest_ids.get(index));
        startActivity(i);
    }

}