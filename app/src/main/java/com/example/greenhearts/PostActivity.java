package com.example.greenhearts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    EditText etposttext;
    ImageView ivpostpic;
    ImageView ivpostsend;
    ImageView postleafpic;
    private static final int IMAGE_REQUEST = 2;
    private Uri imageuri;
    private String url= "";
    private String pic_ID = "";
    private String current_User_Id;
    private String current_username;
    private boolean isgettingimage = false;
    private ProgressBar postprogressbar;
    DatabaseReference dbref;
    android.text.format.DateFormat df;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postprogressbar= findViewById(R.id.postprogressbar);
        postprogressbar.setVisibility(View.GONE);
        df = new android.text.format.DateFormat();
        dbref = FirebaseDatabase.getInstance().getReference();
        current_User_Id = mAuth.getCurrentUser().getUid();
        current_username = mAuth.getCurrentUser().getDisplayName();
        etposttext = findViewById(R.id.etposttext);
        postleafpic = findViewById(R.id.ivpostleafpic);
        postleafpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostActivity.this, "SaiShrSwePran!", Toast.LENGTH_SHORT).show();
            }
        });
        ivpostpic = findViewById(R.id.ivpostpic);
        ivpostpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isgettingimage=true;
                openImage();
            }
        });

        ivpostsend = findViewById(R.id.ivpostsend);
        ivpostsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isgettingimage==true)
                {
                    Toast.makeText(PostActivity.this, "Uploading img...Rehit send", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (etposttext.getText().toString().isEmpty())
                    if (url.isEmpty())
                        Toast.makeText(PostActivity.this, "Empty Post", Toast.LENGTH_SHORT).show();
                    else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("user_id", current_User_Id);
                        map.put("username", current_username);
                        map.put("message", "");
                        map.put("image", url);
                        map.put("timestamp", df.format("dd MMM yyyy", new java.util.Date()));
                        map.put("nlikes", 0);
                        map.put("ncomment", 0);
                        String tempkey = dbref.child("post").push().getKey();
                        dbref.child("post").child(tempkey).updateChildren(map);
                        dbref.child("user").child(current_User_Id).child("post").child(tempkey).setValue(0);
                        PostActivity.this.finish();
                    }
                else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("user_id", current_User_Id);
                    map.put("username", current_username);
                    map.put("message", etposttext.getText().toString());
                    map.put("image", url);
                    map.put("timestamp", df.format("dd MMM yyyy", new java.util.Date()));
                    map.put("nlikes", 0);
                    map.put("ncomment", 0);
                    String tempkey = dbref.child("post").push().getKey();
                    dbref.child("post").child(tempkey).updateChildren(map);
                    dbref.child("user").child(current_User_Id).child("post").child(tempkey).setValue(0);
                    Toast.makeText(PostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                    PostActivity.this.finish();
                }

            }
        });


        // Enables Always-on
        //setAmbientEnabled();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageuri = data.getData();
            UploadImage();
        }
    }


    private void UploadImage() {
        if (imageuri != null) {
            //pic_ID = System.currentTimeMillis() + "." + getFileExtension(imageuri);
  ///////////////////
  //this right here is the magic
            postprogressbar.setVisibility(View.VISIBLE);
            postprogressbar.bringToFront();
            pic_ID= imageuri.getLastPathSegment();
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads")
                    .child(pic_ID);
            StorageTask uploadtask= fileRef.putFile(imageuri);
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
                    url= task.getResult().toString();
                    isgettingimage=false;
                    postprogressbar.setVisibility(View.GONE);
//TADAAAA!!! URL!!!
                }
            });

        }
    }
/*
        private String getFileExtension (Uri uri){
            ContentResolver cresolve = getContentResolver();
            MimeTypeMap mmap = MimeTypeMap.getSingleton();
            return mmap.getExtensionFromMimeType(cresolve.getType(uri));

        }

 */
    }

