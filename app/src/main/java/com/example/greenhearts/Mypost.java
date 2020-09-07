package com.example.greenhearts;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Mypost extends AppCompatActivity implements PostAdapter.OnPostClicked {
private RecyclerView myfeed;
DatabaseReference dbrf;
private PostAdapter madapter;
ChildEventListener childEventListener;
private ArrayList<String> feedid= new ArrayList<String>();
private FirebaseAuth mauth;
private int count =0;
String currenet_user;
    final int backtofeed=6;
    final ArrayList<PostStructure> posts= new ArrayList<PostStructure>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);
        myfeed = (RecyclerView)findViewById(R.id.myfeed);
        mauth = FirebaseAuth.getInstance();
        currenet_user = mauth.getUid();

        madapter = new PostAdapter(Mypost.this,posts,feedid);
        readPosts();
        myfeed.setHasFixedSize(true);
       myfeed.setLayoutManager(new LinearLayoutManager(Mypost.this));
        myfeed.setAdapter(madapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==backtofeed)
        {
            madapter.notifyDataSetChanged();
        }
    }
    private void readPosts() {
        dbrf = FirebaseDatabase.getInstance().getReference().child("post");
        if(childEventListener==null)
        {

            childEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //Toast.makeText(getContext(), "here!", Toast.LENGTH_SHORT).show();
                    PostStructure apost= snapshot.getValue(PostStructure.class);
                        if (currenet_user.equals(apost.getUser_id())) {
                            posts.add(0, apost);
                            feedid.add(snapshot.getKey().toString());
                            madapter.notifyDataSetChanged();
                            count++;
                        }


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
            dbrf.addChildEventListener(childEventListener);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(childEventListener!=null) {
            dbrf.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
    @Override
    public void PostClicked(int i) {
        int k = feedid.size();
        Intent intent= new Intent(this, com.example.greenhearts.CommentActivity.class );
        intent.putExtra("PostID", feedid.get(k-i-1));
        startActivityForResult(intent, backtofeed);
    }
}