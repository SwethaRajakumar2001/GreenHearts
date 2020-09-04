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
    private ArrayList<PostStructure> posts;
    private ArrayList<String> feedpostIDs;
    private PostAdapter adapter;
    private LinearLayoutManager mymanager;
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
//here

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==backtofeed)
        {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        posts= new ArrayList<PostStructure>();
        feedpostIDs= new ArrayList<String>();

        thetestfeedrecycler= (RecyclerView) findViewById(R.id.thetestfeedrecycler);
        adapter= new PostAdapter(FeedActivity.this, posts ,feedpostIDs);
        readPosts();
        thetestfeedrecycler= findViewById(R.id.thetestfeedrecycler);
        thetestfeedrecycler.setHasFixedSize(true);
        mymanager=new LinearLayoutManager(FeedActivity.this);
        mymanager.setReverseLayout(true);
        mymanager.setStackFromEnd(true);
        thetestfeedrecycler.setLayoutManager(mymanager);
        thetestfeedrecycler.setAdapter(adapter);
        Toast.makeText(FeedActivity.this, "Loading....", Toast.LENGTH_LONG).show();
    }

    private void readPosts() {
        postref= FirebaseDatabase.getInstance().getReference().child("post");
        if(mchildEventListener==null)
        {

            mchildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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