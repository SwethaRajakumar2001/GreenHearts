package com.example.greenhearts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatRoom extends AppCompatActivity implements ChatMessageAdapter.ItemClicked {

    ImageView btnPickImg, btnSend;
    EditText etMessage;

    FirebaseDatabase db;
    DatabaseReference dbref,mref,conNameRef;
    ChildEventListener listen;

    private static final int CODE_IMAGE=1;
    String finalUrl=null;
    String contest_id;
    int nlikes;

    ArrayList<ChatMessage> chatList;
    RecyclerView recyclerView;
    ChatMessageAdapter myAdapter;
    LinearLayoutManager myLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        contest_id=getIntent().getStringExtra("contest_id");

        db=FirebaseDatabase.getInstance();
        dbref=db.getReference().child("contest");
        mref=dbref.child(contest_id).child("message");
        conNameRef=db.getReference().child("contest").child(contest_id).child("contest_name");

        conNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                    setTitle(snapshot.getValue(String.class)+" Chat");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerView=findViewById(R.id.messageList);
        recyclerView.setHasFixedSize(true);
        myLayoutManager=new LinearLayoutManager(ChatRoom.this, LinearLayoutManager.VERTICAL, false);
        myLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(myLayoutManager);

        chatList=new ArrayList<>();
        myAdapter=new ChatMessageAdapter(this, chatList);
        recyclerView.setAdapter(myAdapter);

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
                    //ChatMessage chat=new ChatMessage(user_id, username, text, finalUrl, 0, time_stamp);
                    HashMap<String, Object> map=new HashMap<>();
                    //user_id, username, text, photo_url, nlikes, time_stamp
                    map.put("user_id", user_id);
                    map.put("username", username);
                    map.put("text", text);
                    map.put("photo_url", finalUrl);
                    map.put("nlikes", 0);
                    map.put("time_stamp", time_stamp);
                    dbref.child(contest_id).child("message").push().setValue(map);
                    finalUrl=null;
                    etMessage.setText("");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listen==null) {
            listen = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    String push_id = snapshot.getKey();
                    chatMessage.setPush_id(push_id);
                    chatList.add(chatMessage);
                    myAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
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
            mref.addChildEventListener(listen);
        }
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

    @Override
    public void onItemClicked(final int index) {
        final String push_id=chatList.get(index).getPush_id();
        final String user_id=chatList.get(index).getUser_id();
        String current_user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(user_id.equals(current_user)) {
            Toast.makeText(ChatRoom.this, "Can't like this", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference ref=dbref.child(contest_id).child("message").child(push_id).child("like").child(current_user);
        ref.setValue(0);

        ref=dbref.child(contest_id).child("message").child(push_id).child("like");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nlikes = (int) snapshot.getChildrenCount();
                if(nlikes!=chatList.get(index).getNlikes()) {
                    //updating nlikes in db and arraylist
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("nlikes", nlikes);
                    DatabaseReference REF;
                    REF = dbref.child(contest_id).child("message").child(push_id);
                    REF.updateChildren(map);
                    chatList.get(index).setNlikes(nlikes);
                    myAdapter.notifyDataSetChanged();

                    //updating score for the user who got a like in db
                    DatabaseReference TREF=dbref.child(contest_id).child("participants").child(user_id).child("score");
                    TREF.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if(currentData.getValue()!=null) {
                                int localscore = currentData.getValue(Integer.class);
                                localscore++;
                                currentData.setValue(localscore);
                            }
                            return Transaction.success(currentData);
                        }
                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            Log.d("Trans", "scoreTransaction:onComplete:" + error);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(listen!=null) {
            mref.removeEventListener(listen);
            listen=null;
        }
    }
}