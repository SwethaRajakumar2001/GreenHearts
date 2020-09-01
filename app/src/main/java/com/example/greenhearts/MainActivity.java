package com.example.greenhearts;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mfirebasedatabse;
    private DatabaseReference messageRefernce;
    private FirebaseAuth.AuthStateListener listener;
    public static final int RC_SIGN_IN = 1;
    final int topostrequestcode = 4;
    List<AuthUI.IdpConfig> providers;
    ImageButton btnProfile, btnContest, btnFeed, btnQuestion;
    Button btnDummy;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener!=null)
        {
            mFirebaseAuth.removeAuthStateListener(listener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_second);
        mfirebasedatabse = FirebaseDatabase.getInstance();

        init();
        btnContest=findViewById(R.id.btnContest);
        btnProfile=findViewById(R.id.btnProfile);
        btnFeed= findViewById(R.id.btnFeed);
        btnQuestion=findViewById(R.id.btnQuestion);
        btnDummy=findViewById(R.id.btnDummy);

        btnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MainActivity.this, com.example.greenhearts.QuestionnaireActivity.class);
                startActivity(i);
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, com.example.greenhearts.ProfileFragment.class);
                startActivity(intent);
            }
        });
        btnContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MainActivity.this, com.example.greenhearts.ContestRoomsActivity.class);
                startActivity(i);
            }
        });
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MainActivity.this, com.example.greenhearts.FeedActivity.class);
                startActivity(i);

            }
        });
        btnDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

    private void init() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        mFirebaseAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    Toast.makeText(MainActivity.this, "Your are signed in", Toast.LENGTH_SHORT).show();

                }else
                {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(this, "Sign!!", Toast.LENGTH_SHORT).show();
            }else
            if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Sign cancel!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
}