package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class JoinRoomActivity extends AppCompatActivity {

    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    Button btnJoin;
    EditText etContestName,etContestID;
    String user_id,dbName;
    DatabaseReference dbReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        dbReference= FirebaseDatabase.getInstance().getReference();
        etContestName=findViewById(R.id.etContestName);
        etContestID=findViewById(R.id.etContestID);
        btnJoin=findViewById(R.id.btnJoin);
        user_id=mAuth.getCurrentUser().getUid();

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String roomNo = etContestID.getText().toString();
                final String name = etContestName.getText().toString();
                if(roomNo.isEmpty() || name.isEmpty()){
                    Toast.makeText(JoinRoomActivity.this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                }
                else{
                    dbReference.child("contest").child(roomNo).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()==null){
                                Toast.makeText(JoinRoomActivity.this, "Invalid Contest ID", Toast.LENGTH_SHORT).show();
                                etContestID.setText("");
                                etContestName.setText("");
                            }
                            else{
                                dbName =snapshot.child("contest_name").getValue().toString();
                                if(!(dbName.equals(name))){
                                    Toast.makeText(JoinRoomActivity.this, "Invalid Contest Name", Toast.LENGTH_SHORT).show();
                                    etContestName.setText("");
                                }
                                else if(snapshot.child("participants").child(user_id).getValue()!=null){
                                    Toast.makeText(JoinRoomActivity.this, "You are already in the contest", Toast.LENGTH_SHORT).show();
                                    JoinRoomActivity.this.finish();
                                }
                                else{
                                    dbReference.child("contest").child(roomNo).child("participants").child(user_id).child("score").setValue(0);
                                    dbReference.child("contest").child(roomNo).child("participants").child(user_id).child("rank").setValue(1);
                                    dbReference.child("user").child(user_id).child("contests").child(roomNo).child("name").setValue(dbName);
                                    Toast.makeText(JoinRoomActivity.this, "Join Successful!", Toast.LENGTH_SHORT).show();
                                    JoinRoomActivity.this.finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }
}