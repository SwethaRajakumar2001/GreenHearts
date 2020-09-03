package com.example.greenhearts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileFragment extends AppCompatActivity {

// protected void onCreate(Bundle savedInstanceState) {
//     super.onCreate(savedInstanceState);
//     setContentView(R.layout.profile);
// }
TextView seetrees;
    TextView addtrees;
    private String pic_ID = "";
    String url;
    private String current_User_Id;
    DatabaseReference cnodes;
    FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    TextView yourpost;
    ImageView image;
    TextView user_name;
    private Uri filepath;
    ChildEventListener childEventListener;
    DatabaseReference db;
    TextView remind;
    private static final int RC_PHOTO_PICKER =2;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        seetrees = findViewById(R.id.seetrees);
        addtrees = findViewById(R.id.addtrees);
        user_name = findViewById(R.id.user_name);
        image = findViewById(R.id.profileimg);
        yourpost=findViewById(R.id.upost);
        remind = findViewById(R.id.remind);
        url = "";
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        current_User_Id = mAuth.getCurrentUser().getUid();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
      //  cnodes = FirebaseDatabase.getInstance().getReference().child("user");
        db = FirebaseDatabase.getInstance().getReference().child("user");
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_PHOTO_PICKER);
            }
        });
        seetrees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileFragment.this,Myplants.class);
                startActivity(i);
            }
        });
        addtrees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFragment.this,AddPlants.class);
                startActivity(intent);
            }
        });
        yourpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileFragment.this,Mypost.class);
                startActivity(i);
            }
        });
        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(AlarmClock.ACTION_SET_ALARM);
                i2.putExtra(AlarmClock.EXTRA_HOUR,00);
                i2.putExtra(AlarmClock.EXTRA_MINUTES,00);
               // i2.putExtra(AlarmClock.EXTRA_MESSAGE,"Test");
                if(i2.resolveActivity(getPackageManager())!=null)
                startActivity(i2);
                else
                    Toast.makeText(ProfileFragment.this,"No app",Toast.LENGTH_LONG).show();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("user").child(current_User_Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profile_pic = snapshot.child("profile_pic").getValue(String.class);
                String name = snapshot.child("user_name").getValue(String.class);
                if(name!=null)
                user_name.setText(name);
                if(profile_pic!=null) {
                    Glide.with(image.getContext())
                            .load(profile_pic)
                            .into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_PHOTO_PICKER && resultCode==RESULT_OK)
        {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            upload();

        }

    }
    private void upload() {
//        final ProgressDialog progressDialog = new ProgressDialog(AddPlants.this);
//        progressDialog.setTitle("Uploading.....");
//        progressDialog.show();

        if (filepath != null) {
            pic_ID = filepath.getLastPathSegment();
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Users")
                    .child(pic_ID);
            StorageTask uploadtask = fileRef.putFile(filepath);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    url = task.getResult().toString();
                    HashMap<String,Object> m = new HashMap<>();
                    m.put("profile_pic",url);
                    System.out.print(url);
                    db.child(current_User_Id).updateChildren(m);

                }
            });
        }
    }
}
