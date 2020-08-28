package com.example.greenhearts;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView commentsrecycler;
    private EditText etcommenttext;
    private ImageView ivcommentpost;
    private ImageView ivcommentlike;
    private ArrayList<CommentStructure> postcomments;
    private String Post_ID;

    private ChildEventListener mchildEventListener;
    private DatabaseReference postref;
    private DatabaseReference commentref;
    private String current_User_Id;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private CommentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        current_User_Id= mAuth.getCurrentUser().getUid();

        postcomments= new ArrayList<CommentStructure>();
        commentsrecycler= findViewById(R.id.comments);
        etcommenttext= findViewById(R.id.etcommenttext);
        ivcommentpost= findViewById(R.id.ivcommentpost);
        ivcommentlike= findViewById(R.id.ivcommentlike);
        Post_ID= getIntent().getStringExtra("PostID");
        Log.d(" this", Post_ID);
        
        adapter= new CommentAdapter(CommentActivity.this, postcomments);
        commentsrecycler.setHasFixedSize(true);
        commentsrecycler.setAdapter(adapter);
        commentsrecycler.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        postref= FirebaseDatabase.getInstance().getReference().child("post").child(Post_ID);
        postref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("comment")) {
                    commentref=postref.child("comment");
                    readcomments();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        ivcommentlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like();
            }
        });

        ivcommentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etcommenttext.getText().toString().isEmpty())
                    Toast.makeText(CommentActivity.this, "Type comment...", Toast.LENGTH_SHORT).show();
                else {
                    sendcomment(etcommenttext.getText().toString());
                    etcommenttext.setText("");
                    Toast.makeText(CommentActivity.this, "Comment sent", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void like()
    {

        postref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("like")) {
                    postref.child("like").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(current_User_Id)) {
                                Toast.makeText(CommentActivity.this, "Already liked", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                postref.child("like").child(current_User_Id).setValue(0);
                                Toast.makeText(CommentActivity.this, "Liked Post!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
                else
                {
                    postref.child("like").child(current_User_Id).setValue(0);
                    Toast.makeText(CommentActivity.this, "Liked Post!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        postref.child("like").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postref.child("nlikes").setValue(snapshot.getChildrenCount());

                Toast.makeText(CommentActivity.this, "you clicked like", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void sendcomment(String s)
    {
        android.text.format.DateFormat df= new android.text.format.DateFormat();
        HashMap<String, Object> map = new HashMap<>();
        map.put("content", s);
        map.put("user_name", mAuth.getCurrentUser().getDisplayName());
        map.put("timestamp", df.format("dd MMM yyyy", new java.util.Date()));
        postref.child("comment").push().setValue(map);
        postref.child("comment").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postref.child("ncomment").setValue(snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter.notifyDataSetChanged();
        Toast.makeText(CommentActivity.this, "here!", Toast.LENGTH_SHORT).show();

    }

    private void readcomments() {
        if(mchildEventListener==null)
        {

            mchildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //Toast.makeText(getContext(), "here!", Toast.LENGTH_SHORT).show();
                    CommentStructure apost= snapshot.getValue(CommentStructure.class);
                    postcomments.add(adapter.getItemCount(),apost);
                    adapter.notifyDataSetChanged();
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
            commentref.addChildEventListener(mchildEventListener);
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        if(mchildEventListener!=null) {
            commentref.removeEventListener(mchildEventListener);
            mchildEventListener = null;
        }
    }
}