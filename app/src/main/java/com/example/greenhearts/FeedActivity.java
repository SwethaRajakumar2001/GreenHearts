package com.example.greenhearts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity implements PostAdapter.OnPostClicked {

    private RecyclerView thetestfeedrecycler;
    final int backtofeed=6;
    final ArrayList<PostStructure> posts= new ArrayList<PostStructure>();
    private ArrayList<String> feedpostIDs= new ArrayList<String>();
    private PostAdapter adapter;
    private ChildEventListener mchildEventListener;
    private DatabaseReference postref;
    private FloatingActionButton btnfeedaddpost;
    int x=0;
    View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        btnfeedaddpost= findViewById(R.id.btnfeedaddpost);

        btnfeedaddpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(view.getContext() ,com.example.greenhearts.PostActivity.class);
                startActivityForResult(intent , backtofeed);
            }
        });
        thetestfeedrecycler= (RecyclerView) findViewById(R.id.thetestfeedrecycler);
        adapter= new PostAdapter(FeedActivity.this, posts ,feedpostIDs);
        readPosts();
        thetestfeedrecycler= findViewById(R.id.thetestfeedrecycler);
        thetestfeedrecycler.setHasFixedSize(true);
        thetestfeedrecycler.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        thetestfeedrecycler.setAdapter(adapter);
        Toast.makeText(FeedActivity.this, "Loading....", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==backtofeed)
        {
            adapter.notifyDataSetChanged();
        }
    }

    private void readPosts() {
        postref= FirebaseDatabase.getInstance().getReference().child("post");
        if(mchildEventListener==null)
        {

            mchildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //Toast.makeText(getContext(), "here!", Toast.LENGTH_SHORT).show();
                    PostStructure apost= snapshot.getValue(PostStructure.class);
                    posts.add(adapter.getItemCount(),apost);
                    feedpostIDs.add(snapshot.getKey().toString());
                    x++;
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
            postref.addChildEventListener(mchildEventListener);
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        if(mchildEventListener!=null) {
            postref.removeEventListener(mchildEventListener);
            mchildEventListener = null;
        }
    }

    @Override
    public void PostClicked(int i) {
        Intent intent= new Intent(this, com.example.greenhearts.CommentActivity.class );
        intent.putExtra("PostID", feedpostIDs.get(i));
        startActivityForResult(intent, backtofeed);
    }
}