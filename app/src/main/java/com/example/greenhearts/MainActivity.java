package com.example.greenhearts;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mfirebasedatabse;
    private DatabaseReference messageRefernce;
    private FirebaseAuth.AuthStateListener listener;
    public static final int RC_SIGN_IN = 1;
    final int topostrequestcode = 4;
    List<AuthUI.IdpConfig> providers;
    ImageView navprofile;
    ImageView navfeed;
    ImageView navcontests;
    ImageView navquest;
    Button btnfeedpost;

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
        setContentView(R.layout.activity_main);
        mfirebasedatabse = FirebaseDatabase.getInstance();

        init();
        btnfeedpost= findViewById(R.id.btnfeedpost);
        btnfeedpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, com.example.greenhearts.PostActivity.class);
                startActivity(intent);

            }
        });

        navquest= findViewById(R.id.navquest);
        navquest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Toast.makeText(MainActivity.this,"clicked",Toast.LENGTH_SHORT).show();
            }
        });
        /*navfeed= findViewById(R.id.navfeed);
        navfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager= getSupportFragmentManager();
                manager.beginTransaction()
                        .show(manager.findFragmentById(R.id.thefeedfrag))
                        .commit();


//  .addToBackStack(null) if it should return to prev state when <-
            }
        });
        navprofile= findViewById(R.id.navprofile);
        navprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        navcontests= findViewById(R.id.navcontests);
        navcontests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         */


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