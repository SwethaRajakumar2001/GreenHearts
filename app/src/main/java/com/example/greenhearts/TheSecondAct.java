package com.example.greenhearts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TheSecondAct extends AppCompatActivity {

    ImageButton btnProfile, btnContest, btnFeed, btnQuestion;
    Button btnDummy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_second);

        btnContest=findViewById(R.id.btnContest);
        btnProfile=findViewById(R.id.btnProfile);
        btnFeed= findViewById(R.id.btnFeed);
        btnQuestion=findViewById(R.id.btnQuestion);
        btnDummy=findViewById(R.id.btnDummy);

        btnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}