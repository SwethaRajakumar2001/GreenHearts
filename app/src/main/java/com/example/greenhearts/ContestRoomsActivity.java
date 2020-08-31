package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContestRoomsActivity extends AppCompatActivity implements RoomDetailsAdapter.OnRoomClicked {

    Button btn_Create,btn_Join;
    RecyclerView rv;
    RoomDetailsAdapter a;
    ArrayList<RoomDetails> details=new ArrayList<RoomDetails>();
    ArrayList<String> contest_ids=new ArrayList<String>();
    int reload=6;
    ChildEventListener listener;

    DatabaseReference ref;
    String user_id= FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_rooms);
        btn_Create=findViewById(R.id.btn_Create);
        btn_Join=findViewById(R.id.btn_Join);
        a=new RoomDetailsAdapter(ContestRoomsActivity.this, details, contest_ids);
        readDetails();
        ref= FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("contests");
        rv=(RecyclerView)findViewById(R.id.rvContestRooms);
        rv.setLayoutManager(new LinearLayoutManager(ContestRoomsActivity.this));
        rv.setAdapter(a);

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContestRoomsActivity.this, CreateRoomActivity.class);
                startActivityForResult(i, reload);
            }
        });

        btn_Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContestRoomsActivity.this, JoinRoomActivity.class);
                startActivityForResult(i,reload);
            }
        });
    }

    private void readDetails() {
        if(listener==null){
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==reload){
            a.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(listener!=null){
            ref.removeEventListener(listener);
            listener=null;
        }
    }


    @Override
    public void RoomClicked(int index) {
        Intent i = new Intent(this, IndContestRoom.class);
        i.putExtra("contest_id", contest_ids.get(index));
        startActivity(i);
    }

}