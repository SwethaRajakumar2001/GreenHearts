package com.example.greenhearts;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragFeed extends Fragment  {
    private RecyclerView recyclerView;
    private ArrayList<PostStructure> posts;
    private PostAdapter adapter;
    private ChildEventListener mchildEventListener;
    private DatabaseReference postref;
    View view;



    public FragFeed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_frag_feed, container, false);

        //Toast.makeText(getContext(), num , Toast.LENGTH_LONG).show();

        return view;
        //return inflater.inflate(R.layout.fragment_frag_feed, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        posts= new ArrayList<PostStructure>();
        adapter= new PostAdapter(this.getActivity(),posts,true);
        readPosts();
        recyclerView= view.findViewById(R.id.posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(adapter);
        String num= "adapter set" + Integer.toString(posts.size());
    }

    private void readPosts() {
        postref= FirebaseDatabase.getInstance().getReference().child("post");
        if(mchildEventListener==null)
        {
            mchildEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    PostStructure apost= snapshot.getValue(PostStructure.class);
                    posts.add(apost);
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
        postref.removeEventListener(mchildEventListener);
        mchildEventListener=null;
    }
}