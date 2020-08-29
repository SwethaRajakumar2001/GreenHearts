package com.example.greenhearts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContestRoomsActivity extends AppCompatActivity {

    Button btn_Create,btn_Join;
    RecyclerView rv;
    DatabaseReference ref;
    String user_id= FirebaseAuth.getInstance().getUid();
    RoomDetailsAdapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_rooms);
        btn_Create=findViewById(R.id.btn_Create);
        btn_Join=findViewById(R.id.btn_Join);
        ref= FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("contests");
        rv=(RecyclerView)findViewById(R.id.rvContestRooms);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<RoomDetails>options= new FirebaseRecyclerOptions.Builder<RoomDetails>().setQuery(ref,RoomDetails.class).build();

        a=new RoomDetailsAdapter(options);
        rv.setAdapter(a);

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

    @Override
    protected void onStart() {
        super.onStart();
        a.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        a.stopListening();
    }
}