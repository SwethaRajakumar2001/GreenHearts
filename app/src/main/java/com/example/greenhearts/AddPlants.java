package com.example.greenhearts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AddPlants extends AppCompatActivity {
    private TextView thedate;
    private PlantAdapter madapter;
    private ImageView btngocalendar;
    private EditText ename;
    private ImageView img;
    private Button button;
    private ListView gallery;
    private int count =-1;
    public static final int RC_PHOTO_PICKER =2;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 20;
    String url;
   private String current_User_Id;
   DatabaseReference cnodes;
    FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filepath;
    DatabaseReference db;
    android.text.format.DateFormat df;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        df = new android.text.format.DateFormat();
        url = "";
        setContentView(R.layout.activity_add_plants);
        thedate = (TextView)findViewById(R.id.date);
        thedate.setText(date_n);
       // Toast.makeText(AddPlants.this,(df.format("dd MMM yyyy", new java.util.Date())).getClass().getSimpleName(),Toast.LENGTH_LONG).show();
    //    btngocalendar = (ImageView)findViewById(R.id.cal);
        ename = (EditText)findViewById(R.id.etreeename);
        img = (ImageView)findViewById(R.id.addtree);
        button = (Button)findViewById(R.id.addbut);
        gallery = (ListView)findViewById(R.id.gallery);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
       current_User_Id = mAuth.getCurrentUser().getUid();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        cnodes = FirebaseDatabase.getInstance().getReference().child("user");
       // current_username ="random";
        db = FirebaseDatabase.getInstance().getReference();
        final List<Plants> plantList = new ArrayList<>();
  //     madapter = new PlantAdapter(AddPlants.this,R.layout.mytrees_style,plantList);
//        gallery.setAdapter(madapter);
//        Intent incoming = getIntent();
//        String date = incoming.getStringExtra("date");
//        thedate.setText(date);
//        btngocalendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(AddPlants.this, CalenderActivity.class);
//                startActivity(i);
//            }
//        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_PHOTO_PICKER);
            }
        });
        ename.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        cnodes.child(current_User_Id).child("plant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    count = (int)snapshot.getChildrenCount();
                }else
                {
                    count =-1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Plants p = new Plants(thedate.toString(),ename.getText().toString(),null);

                if(url.isEmpty())
                {
                    Toast.makeText(AddPlants.this, "Empty image!!", Toast.LENGTH_SHORT).show();
                }else
                    if(ename.getText().toString().isEmpty())
                    {
                        Toast.makeText(AddPlants.this, "Empty Name!!", Toast.LENGTH_SHORT).show();
                    }
                else
                {

                    count++;
                    HashMap<String, Object> map= new HashMap<>();
                    Toast.makeText(AddPlants.this,ename.getText().toString(),Toast.LENGTH_LONG).show();
                    map.put("plantname",ename.getText().toString());
                    map.put("timestamp", df.format("dd MMM yyyy", new java.util.Date()));
                    map.put("image",url);
                    map.put("user_id",current_User_Id);
                    String tempkey = db.child("plant").push().getKey();
                    db.child("plant").child(tempkey).updateChildren(map);

                    HashMap<String, Object> map2= new HashMap<>();
                    map2.put("user_name",firebaseUser.getDisplayName());
                    map2.put("no_plant",count);
                    db.child("user").child(current_User_Id).updateChildren(map2);
                    db.child("user").child(current_User_Id).child("plant").child(tempkey).setValue(0);
                    AddPlants.this.finish();
                }

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
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            upload();

        }
    }
    private void upload()
    {
        final ProgressDialog progressDialog = new ProgressDialog(AddPlants.this);
        progressDialog.setTitle("Uploading.....");
        progressDialog.show();
        if(filepath!=null)
        {
            StorageReference s = storageReference.child("Trees/"+ UUID.randomUUID().toString());
            s.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPlants.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            url = task.getResult().toString();
                        }
                    });


        }
    }
}