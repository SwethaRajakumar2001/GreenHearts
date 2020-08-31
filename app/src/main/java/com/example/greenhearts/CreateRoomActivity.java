package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class CreateRoomActivity extends AppCompatActivity {

    EditText etRoomName;
    Button btnCreate;
    DatabaseReference ref;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    String current_user,room_name,contest_id,user_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        etRoomName=findViewById(R.id.etRoomName);
        btnCreate=findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                room_name=etRoomName.getText().toString();
                if(room_name.isEmpty()){
                    Toast.makeText(CreateRoomActivity.this, "Enter Contest room name", Toast.LENGTH_SHORT).show();
                }
                else{
                    ref= FirebaseDatabase.getInstance().getReference();
                    current_user=mAuth.getCurrentUser().getUid();

                    contest_id=ref.child("user").child(current_user).child("contests").push().getKey();
                    ref.child("user").child(current_user).child("contests").child(contest_id).child("name").setValue(room_name);
                    DatabaseReference temp=ref.child("contest").child(contest_id);
                    temp.child("contest_name").setValue(room_name);
                    user_name=mAuth.getCurrentUser().getDisplayName();
                    temp.child("creator_username").setValue(user_name);
                    temp.child("participants").child(current_user).child("score").setValue(0);
                    temp.child("participants").child(current_user).child("rank").setValue(1);
                    Toast.makeText(CreateRoomActivity.this, "Create Successful!", Toast.LENGTH_SHORT).show();
                    CreateRoomActivity.this.finish();
                }
            }
        });

    }
}
