package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionnaireActivity extends AppCompatActivity {

    Button btnQSubmit;
    RadioGroup rg1,rg2,rg3;
    DatabaseReference ref,ref1;
    String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid(),contest_id;
    Integer score=0,prev;
    ArrayList<String> contest_ids=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        btnQSubmit=findViewById(R.id.btnQSubmit);
        rg1=findViewById(R.id.rg1);
        rg2=findViewById(R.id.rg2);
        rg3=findViewById(R.id.rg3);

        btnQSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected1 = rg1.getCheckedRadioButtonId();
                int selected2 = rg2.getCheckedRadioButtonId();
                int selected3 = rg3.getCheckedRadioButtonId();
                score=0;
                Log.d("error", "start.....");
                if (selected1 == -1 || selected2==-1 || selected3==-1) {
                    Toast.makeText(QuestionnaireActivity.this, "Answer all the questions", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("error", "start1");

                    if(selected1==R.id.rbYes1) score++;
                    if(selected2==R.id.rbYes2) score++;
                    if(selected3==R.id.rbYes3) score++;

                    ref= FirebaseDatabase.getInstance().getReference();



                    ref.child("user").child(user_id).child("contests").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap:snapshot.getChildren()){
                                contest_ids.add(snap.getKey());
                                System.out.println(snap.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    ref.child("contest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(int i=0;i<contest_ids.size();i++){
                                prev= Integer.parseInt(snapshot.child(contest_ids.get(i)).child("participants").child(user_id).child("score").getValue().toString());
                                ref.child("contest").child(contest_ids.get(i)).child("participants").child(user_id).child("score").setValue(prev+score);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


/*
                    ref.child("user").child(user_id).child("contests").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                            for(DataSnapshot snap:snapshot.getChildren()){
                                contest_id=snap.getKey().toString();
                                ref1=ref.child("contest").child(contest_id).child("participants").child(user_id);
                                prev=0;
                                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        prev= Integer.parseInt(snapshot1.child("score").getValue().toString()) ;
                                        ref1.child("score").setValue(prev+score);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(QuestionnaireActivity.this, "Gone", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QuestionnaireActivity.this, "Gone", Toast.LENGTH_SHORT).show();
                        }
                    });*/

                   QuestionnaireActivity.this.finish();
                }
            }
        });

    }
}