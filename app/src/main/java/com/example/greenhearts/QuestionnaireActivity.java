package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class QuestionnaireActivity extends AppCompatActivity {

    Button btnQSubmit;
    RadioGroup rg1,rg2,rg3;
    DatabaseReference ref,ref1;
    String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
    Integer score=0,prev;
    ArrayList<String> contest_ids=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        final int plants=getIntent().getIntExtra("plants", 0);

        btnQSubmit=findViewById(R.id.btnQSubmit);
        rg1=findViewById(R.id.rg1);
        rg2=findViewById(R.id.rg2);
        rg3=findViewById(R.id.rg3);
        ref= FirebaseDatabase.getInstance().getReference();


        btnQSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c=Calendar.getInstance();
                SimpleDateFormat simple=new SimpleDateFormat("dd-MM-yyyy");
                final String date = simple.format(c.getTime());

                score=0;

                int selected1 = rg1.getCheckedRadioButtonId();
                int selected2 = rg2.getCheckedRadioButtonId();
                int selected3 = rg3.getCheckedRadioButtonId();


                if(selected1 == -1 || selected2==-1 || selected3==-1){
                    Toast.makeText(QuestionnaireActivity.this, "Answer all the questions", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(selected1==R.id.rbYes1) score++;
                    if(selected2==R.id.rbYes2) score++;
                    if(selected3==R.id.rbYes3) score++;



                    ref.child("user").child(user_id).child("contests").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap:snapshot.getChildren()){
                                contest_ids.add(snap.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    ref.child("contest").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            for(int i=0;i<contest_ids.size();i++){
                                prev= Integer.parseInt(currentData.child(contest_ids.get(i)).child("participants").child(user_id).child("score").getValue().toString());
                               currentData.child(contest_ids.get(i)).child("participants").child(user_id).child("score").setValue(prev+score*plants);
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        }
                    });

                    ref.child("contest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

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
                    Toast.makeText(QuestionnaireActivity.this, "Submission Successful!", Toast.LENGTH_SHORT).show();
                    ref.child("user").child(user_id).child("last_response").setValue(date);
                    QuestionnaireActivity.this.finish();
                }
            }
        });

    }
}