package com.example.haclicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainScreen extends AppCompatActivity {

    ImageButton settings, createRoom, joinRoom;
    String username, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        settings = findViewById(R.id.settings);
        createRoom = findViewById(R.id.create_room);
        joinRoom = findViewById(R.id.join_room);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            username = signInAccount.getDisplayName();
            userEmail = signInAccount.getEmail();
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsoleScreen.class);
                startActivity(intent);
            }
        });

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: generate classroom;
            }
        });
    }
}