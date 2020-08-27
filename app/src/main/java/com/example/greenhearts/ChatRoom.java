package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatRoom extends AppCompatActivity {

    ImageView btnPickImg, btnSend;
    EditText etMessage;
    FirebaseDatabase db;
    DatabaseReference dbref;
    private static final int CODE_IMAGE=1;
    String finalUrl=null;
    String contest_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        db=FirebaseDatabase.getInstance();
        dbref=db.getReference().child("contest");

        btnPickImg=findViewById(R.id.btnPickImg);
        etMessage=findViewById(R.id.etMessage);
        btnSend=findViewById(R.id.btnSend);

        btnPickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent, CODE_IMAGE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etMessage.getText().toString();
                if(text.isEmpty() && finalUrl==null) {
                    Toast.makeText(ChatRoom.this, "Empty message", Toast.LENGTH_SHORT).show();
                }
                else {
                    String user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String time_stamp=getCurrentTimeStamp();
                    ChatMessage chat=new ChatMessage(user_id, username, text, finalUrl, "0", time_stamp);
                    dbref.child(contest_id).child("message").push().setValue(chat);
                    finalUrl=null;
                    etMessage.setText("");
                }
            }
        });

    }

    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CODE_IMAGE && resultCode==RESULT_OK) {
            Uri localUri=data.getData();
            if (localUri != null) {
                String storeUri= localUri.getLastPathSegment();
                final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("chat_photos")
                        .child(storeUri);
                StorageTask uploadtask= fileRef.putFile(localUri);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful())
                            throw task.getException();
                        return fileRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        finalUrl= task.getResult().toString();
                    }
                });
            }
        }
    }
}