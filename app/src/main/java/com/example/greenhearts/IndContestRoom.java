package com.example.greenhearts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IndContestRoom extends AppCompatActivity {

    TextView tvScore, tvRank, tvCreatedby;
    Button btnChat, btnLeave;
    String contest_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ind_contest_room);

        contest_id=getIntent().getStringExtra("contest_id");

        tvCreatedby=findViewById(R.id.tvCreatedBy);
        tvRank=findViewById(R.id.tvRank);
        tvScore=findViewById(R.id.tvScore);
        btnChat=findViewById(R.id.btnChat);
        btnLeave=findViewById(R.id.btnLeave);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(IndContestRoom.this, com.example.greenhearts.ChatRoom.class);
                intent.putExtra("contest_id", contest_id);
                startActivity(intent);
            }
        });

        
    }
}